/**
 * Copyright (c) 2003-2009, Xith3D Project Group all rights reserved.
 * 
 * Portions based on the Java3D interface, Copyright by Sun Microsystems.
 * Many thanks to the developers of Java3D and Sun Microsystems for their
 * innovation and design.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * Neither the name of the 'Xith3D Project Group' nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) A
 * RISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE
 */
package org.xith3d.terrain.legacy;

import java.io.IOException;

import org.xith3d.io.InvalidFormat;
import org.xith3d.io.Scribable;
import org.xith3d.io.ScribeInputStream;
import org.xith3d.io.ScribeOutputStream;
import org.xith3d.io.UnscribableNodeEncountered;
import org.xith3d.utility.logging.X3DLog;

/**
 *
 * Terrain data bank holds terrain information for a given portion of the
 * world.  Banks can be read and written with great speed from disk
 * because they are organized in large arrays.<br>
 * <br>
 * Multiple banks can be used for the same portion of the world, stored at different
 * densities.  Then the terrain system can use the lower density bank for areas far away,
 * saving on memory.<br>
 * <br>
 * 
 * @author David Yazel
 */
public class TerrainDataBank implements Scribable
{
    public static final int MUL_VERTEX = 5;
    public static final int MUL_ERROR = 6;
    public static final int MUL_SUB_ENABLED = 2;
    public static final int MUL_CHILD = 4;
    public static final int MUL_STATIC = 1;
    public static final int MUL_DIRTY = 1;
    public static final int MUL_NEXT = 1;
    public static final int MUL_ENABLED = 1;
    public static final int MUL_MINMAX = 1;
    
    //private final int MAX_NODES = 100000;
    private final static int EXTENT_NODES = 10000;
    
    private int[] child; // this is the node within a bank
    private int[] childBank; // this is the bank that the child exists in
    
    protected int[] handles; // indirection pointers into the vertices.
    private float[] y;
    private short[] error;
    private short[] minY;
    private short[] maxY;
    private byte[] enabledFlags;
    private byte[] subEnabledCount;
    private byte[] dataStatic;
    private byte[] dataDirty;
    private int[] next;
    private boolean compressed = false;
    private int bankId;
    
    protected TerrainDataBank newBank = null;
    
    // total number of nodes allocated
    
    private int maxNodes = 0;
    protected int freeList = 0;
    private int freeHandle = 0;
    
    public boolean isCompressed()
    {
        return ( compressed );
    }
    
    private static void copy( TerrainDataBank db, TerrainDataBank d )
    {
        System.arraycopy( db.child, 0, d.child, 0, db.child.length );
        System.arraycopy( db.childBank, 0, d.childBank, 0, db.childBank.length );
        System.arraycopy( db.y, 0, d.y, 0, db.y.length );
        System.arraycopy( db.minY, 0, d.minY, 0, db.minY.length );
        System.arraycopy( db.maxY, 0, d.maxY, 0, db.maxY.length );
        System.arraycopy( db.error, 0, d.error, 0, db.error.length );
        System.arraycopy( db.enabledFlags, 0, d.enabledFlags, 0, db.enabledFlags.length );
        System.arraycopy( db.subEnabledCount, 0, d.subEnabledCount, 0, db.subEnabledCount.length );
        System.arraycopy( db.dataStatic, 0, d.dataStatic, 0, db.dataStatic.length );
        System.arraycopy( db.dataDirty, 0, d.dataDirty, 0, db.dataDirty.length );
        System.arraycopy( db.next, 0, d.next, 0, db.next.length );
    }
    
    private void deleteData()
    {
        child = null;
        childBank = null;
        y = null;
        minY = null;
        maxY = null;
        error = null;
        enabledFlags = null;
        subEnabledCount = null;
        dataStatic = null;
        dataDirty = null;
        handles = null;
        next = null;
    }
    
    public static TerrainDataBank expand( TerrainDataBank db )
    {
        TerrainDataBank d = new TerrainDataBank( db.bankId, db.maxNodes + EXTENT_NODES );
        d.freeList = db.maxNodes;
        
        if ( db.handles.length >= d.maxNodes )
        {
            X3DLog.debug( "keeping bigger handle size" );
            d.handles = new int[ db.handles.length ];
            System.arraycopy( db.handles, 0, d.handles, 0, db.handles.length );
            d.freeHandle = db.freeHandle;
        }
        else
        {
            X3DLog.debug( "extanding handle size for bank ", db.bankId, " from ", db.handles.length, " to ", d.maxNodes );
            System.arraycopy( db.handles, 0, d.handles, 0, db.handles.length );
            
            // ok we need to fix up the free handle list.  all the new handles need
            // to be added to the free list
            
            d.handles[ d.handles.length - 1 ] = -db.freeHandle;
            d.freeHandle = db.handles.length;
        }
        
        copy( db, d );
        db.newBank = d;
        db.deleteData();
        
        return ( d );
    }
    
    public void delete( int node )
    {
        if ( ( bankId == 11 ) && ( node == 13 ) )
        {
            X3DLog.debug( "node 11/13 deleted" );
        }
        
        for ( int i = 0; i < MUL_CHILD; i++ )
        {
            setChild( node, i, -1 );
            setChildBank( node, i, -1 );
        }
        next[ handles[ node ] ] = freeList;
        freeList = handles[ node ];
        
        // fix the list of free handles
        
        handles[ node ] = -freeHandle;
        freeHandle = node;
    }
    
    /**
     * copies the specified node from the source into the dest.  This is used
     * to compress the databank into a new bank
     */
    private static int copyNode( int node, TerrainDataBank source, TerrainDataBank dest )
    {
        int n = dest.freeList;
        if ( n == -1 )
            throw new Error( "Out of quad nodes to allocate" );
        dest.freeList = dest.next[ n ];
        dest.next[ n ] = -1;
        
        dest.handles[ node ] = n;
        
        //Log.print("Copying node "+node+" to location "+n);
        
        try
        {
            System.arraycopy( source.y, source.handles[ node ] * MUL_VERTEX, dest.y, dest.handles[ node ] * MUL_VERTEX, MUL_VERTEX );
            System.arraycopy( source.minY, source.handles[ node ] * MUL_MINMAX, dest.minY, dest.handles[ node ] * MUL_MINMAX, MUL_MINMAX );
            System.arraycopy( source.maxY, source.handles[ node ] * MUL_MINMAX, dest.maxY, dest.handles[ node ] * MUL_MINMAX, MUL_MINMAX );
            System.arraycopy( source.error, source.handles[ node ] * MUL_ERROR, dest.error, dest.handles[ node ] * MUL_ERROR, MUL_ERROR );
            System.arraycopy( source.enabledFlags, source.handles[ node ] * MUL_ENABLED, dest.enabledFlags, dest.handles[ node ] * MUL_ENABLED, MUL_ENABLED );
            System.arraycopy( source.subEnabledCount, source.handles[ node ] * MUL_SUB_ENABLED, dest.subEnabledCount, dest.handles[ node ] * MUL_SUB_ENABLED, MUL_SUB_ENABLED );
            System.arraycopy( source.dataStatic, source.handles[ node ] * MUL_STATIC, dest.dataStatic, dest.handles[ node ] * MUL_STATIC, MUL_STATIC );
            System.arraycopy( source.dataDirty, source.handles[ node ] * MUL_DIRTY, dest.dataDirty, dest.handles[ node ] * MUL_DIRTY, MUL_DIRTY );
            System.arraycopy( source.child, source.handles[ node ] * MUL_CHILD, dest.child, dest.handles[ node ] * MUL_CHILD, MUL_CHILD );
            System.arraycopy( source.childBank, source.handles[ node ] * MUL_CHILD, dest.childBank, dest.handles[ node ] * MUL_CHILD, MUL_CHILD );
        }
        catch ( Throwable e )
        {
            X3DLog.exception( "Error attempting to compress" );
            X3DLog.exception( "  node = ", node );
            X3DLog.exception( "  source location = ", source.handles[ node ] );
            X3DLog.exception( "  dest location = ", dest.handles[ node ] );
            
            throw new Error( e );
        }
        
        return ( n );
    }
    
    /**
     * Compresses the data bank by moving the non-deleted nodes down near the front and then
     * building a new version which is shorter. This should be done after a static cull to
     * save disk space and memory. <b>This wont be super fast.</b>
     * 
     * @param db
     * 
     * @return the new TerrainDataBank
     */
    public static TerrainDataBank compress( TerrainDataBank db )
    {
        //if (db.compressed) return ( db );
        
        /*
        if (db.next[0]!=-1)
        {
            TerrainDataBank d = new TerrainDataBank( 10 );
            db.newBank = d;
            return ( d );
        }
        */

        // count the number of nodes which are *not* deleted
        int num = 0;
        for ( int i = 0; i < db.maxNodes; i++ )
            if ( db.next[ i ] == -1 )
                num++;
        
        X3DLog.debug( "compressing bank ", db.bankId, " to ", num, " nodes from ", db.maxNodes );
        
        // find the bank, and make sure that all the banks are the same
        for ( int i = 0; i < db.maxNodes * MUL_CHILD; i++ )
        {
            if ( db.childBank[ i ] != -1 )
            {
                if ( db.bankId != db.childBank[ i ] )
                {
                    X3DLog.error( "we encountered a node in bank ", db.bankId, " that has a child in bank ", db.childBank[ i ] );
                    X3DLog.error( "index is ", i );
                    throw new Error( "Child exists outside of bank" );
                }
            }
        }
        
        // ok now we have the number of nodes and we need to make a new bank with jsut that many nodes
        
        TerrainDataBank d = new TerrainDataBank( db.bankId, ( num + 10 ) );
        d.handles = new int[ db.handles.length ];
        System.arraycopy( db.handles, 0, d.handles, 0, db.handles.length );
        d.freeHandle = db.freeHandle;
        
        db.newBank = d;
        
        // copy all the nodes
        
        for ( int i = 0; i < db.handles.length; i++ )
        {
            if ( db.handles[ i ] >= 0 )
                copyNode( i, db, d );
        }
        d.compressed = true;
        db.deleteData();
        
        // now do some validation
        
        for ( int i = 0; i < d.handles.length; i++ )
        {
            if ( d.handles[ i ] >= 0 )
            {
                // make sure all children are valid pointers for this node
                
                for ( int j = 0; j < MUL_CHILD; j++ )
                {
                    int child = d.getChild( i, j );
                    if ( child != -1 )
                        if ( d.handles[ child ] < 0 )
                        {
                            throw new Error( "Terrain Data Bank handles are corrupt" );
                        }
                }
            }
        }
        
        // find the bank, and make sure that all the banks are the same
        
        for ( int i = 0; i < d.maxNodes * MUL_CHILD; i++ )
        {
            if ( d.childBank[ i ] != -1 )
            {
                if ( d.bankId != d.childBank[ i ] )
                {
                    X3DLog.error( "POST COMPRESSION ERROR" );
                    X3DLog.error( "we encountered a node in bank ", d.bankId, " that has a child in bank ", d.childBank[ i ] );
                    throw new Error( "Child exists outside of bank" );
                }
            }
        }
        
        return ( d );
    }
    
    // we store all the quad squares broken down into arrays so we can load
    // and unload data extremely quickly
    
    public TerrainDataBank()
    {
    }
    
    public TerrainDataBank( int bankId, int max )
    {
        this.bankId = bankId;
        initialize( max );
    }
    
    /**
     * Takes a node off the free list and returns the index
     * 
     * @return the new handle
     */
    public int allocateNode()
    {
        int n = freeList;
        if ( n == -1 )
            throw new Error( "Out of quad nodes to allocate" );
        
        int h = freeHandle;
        if ( h < 0 )
            throw new Error( "out of handles to allocate" );
        
        freeList = next[ n ];
        next[ n ] = -1;
        
        if ( handles[ freeHandle ] >= 0 )
        {
            X3DLog.error( "we are allocating a handle already allocated!" );
            System.exit( 0 );
        }
        
        freeHandle = -handles[ h ];
        handles[ h ] = n;
        for ( int i = 0; i < MUL_CHILD; i++ )
            child[ n * MUL_CHILD + i ] = -1;
        if ( ( bankId == 11 ) && ( h == 13 ) )
            X3DLog.debug( "node 11/13 allocated" );
        
        //Log.print( "allocated node " + n + " with handle " + h + " in bank " + bankId );
        return ( h );
    }
    
    public float getY( int node, int index )
    {
        return ( y[ handles[ node ] * MUL_VERTEX + index ] );
    }
    
    public void setY( int node, int index, float val )
    {
        y[ handles[ node ] * MUL_VERTEX + index ] = val;
    }
    
    public short getError( int node, int index )
    {
        return ( error[ handles[ node ] * MUL_ERROR + index ] );
    }
    
    public void setError( int node, int index, short val )
    {
        error[ handles[ node ] * MUL_ERROR + index ] = val;
    }
    
    public int getChild( int node, int index )
    {
        if ( ( bankId == 11 ) && ( child[ handles[ node ] * MUL_CHILD + index ] == 13 ) )
            X3DLog.debug( "node 11/13 is child ", index, " of node ", node );
        
        return ( child[ handles[ node ] * MUL_CHILD + index ] );
    }
    
    public int getChildBank( int node, int index )
    {
        return ( childBank[ handles[ node ] * MUL_CHILD + index ] );
    }
    
    public void setChild( int node, int index, int val )
    {
        child[ handles[ node ] * MUL_CHILD + index ] = val;
    }
    
    public void setChildBank( int node, int index, int val )
    {
        //if (val != bankId) throw new Error( "Attempting to set child bank as " + val + " inside bank " + bankId ) );
        childBank[ handles[ node ] * MUL_CHILD + index ] = val;
    }
    
    public byte getSubEnabledCount( int node, int index )
    {
        return ( subEnabledCount[ handles[ node ] * MUL_SUB_ENABLED + index ] );
    }
    
    public void setSubEnabledCount( int node, int index, byte val )
    {
        subEnabledCount[ handles[ node ] * MUL_SUB_ENABLED + index ] = val;
    }
    
    public short getMinY( int node )
    {
        return ( minY[ handles[ node ] * MUL_MINMAX ] );
    }
    
    public void setMinY( int node, short val )
    {
        minY[ handles[ node ] * MUL_MINMAX ] = val;
    }
    
    public byte getEnabledFlags( int node )
    {
        return ( enabledFlags[ handles[ node ] * MUL_ENABLED ] );
    }
    
    public void setEnabledFlags( int node, byte val )
    {
        enabledFlags[ handles[ node ] * MUL_ENABLED ] = val;
    }
    
    public short getMaxY( int node )
    {
        return ( maxY[ handles[ node ] * MUL_MINMAX ] );
    }
    
    public void setMaxY( int node, short val )
    {
        maxY[ handles[ node ] * MUL_MINMAX ] = val;
    }
    
    public boolean getStatic( int node )
    {
        return ( dataStatic[ handles[ node ] * MUL_STATIC ] == 1 );
    }
    
    public void setStatic( int node, boolean val )
    {
        dataStatic[ handles[ node ] * MUL_STATIC ] = val ? (byte)1 : (byte)0;
    }
    
    public boolean getDirty( int node )
    {
        return ( dataDirty[ handles[ node ] * MUL_DIRTY ] == 1 );
    }
    
    public void setDirty( int node, boolean val )
    {
        dataDirty[ handles[ node ] * MUL_DIRTY ] = val ? (byte)1 : (byte)0;
    }
    
    public void setNext( int node, int val )
    {
        next[ handles[ node ] * MUL_NEXT ] = val;
    }
    
    public int getNext( int node )
    {
        return ( next[ handles[ node ] * MUL_NEXT ] );
    }
    
    public void initialize( int max )
    {
        handles = new int[ max ];
        child = new int[ max * MUL_CHILD ];
        childBank = new int[ max * MUL_CHILD ];
        y = new float[ max * MUL_VERTEX ];
        enabledFlags = new byte[ max * MUL_ENABLED ];
        error = new short[ max * MUL_ERROR ];
        minY = new short[ max * MUL_MINMAX ];
        maxY = new short[ max * MUL_MINMAX ];
        subEnabledCount = new byte[ max * MUL_SUB_ENABLED ];
        dataDirty = new byte[ max * MUL_DIRTY ];
        dataStatic = new byte[ max * MUL_STATIC ];
        
        maxNodes = max;
        
        // define the free list
        
        next = new int[ max * MUL_NEXT ];
        for ( int i = 0; i < max; i++ )
        {
            next[ i ] = i + 1;
            handles[ i ] = -i - 1;
        }
        
        for ( int i = 0; i < max * MUL_CHILD; i++ )
        {
            child[ i ] = -1;
            childBank[ i ] = -1;
        }
        next[ max - 1 ] = -1;
    }
    
    public void load( ScribeInputStream in ) throws InvalidFormat, IOException
    {
        bankId = in.readInt();
        maxNodes = in.readInt();
        freeList = in.readInt();
        freeHandle = in.readInt();
        
        handles = in.readIntArray();
        next = in.readIntArray();
        child = in.readIntArray();
        childBank = in.readIntArray();
        
        y = in.readFloatArray();
        subEnabledCount = in.readByteArray();
        enabledFlags = in.readByteArray();
        dataStatic = in.readByteArray();
        dataDirty = in.readByteArray();
        
        error = in.readShortArray();
        minY = in.readShortArray();
        maxY = in.readShortArray();
    }
    
    public void save( ScribeOutputStream out ) throws UnscribableNodeEncountered, IOException
    {
        out.writeInt( bankId );
        out.writeInt( maxNodes );
        out.writeInt( freeList );
        out.writeInt( freeHandle );
        out.writeIntArray( handles );
        out.writeIntArray( next );
        out.writeIntArray( child );
        out.writeIntArray( childBank );
        out.writeFloatArray( y );
        out.writeByteArray( subEnabledCount );
        out.writeByteArray( enabledFlags );
        out.writeByteArray( dataStatic );
        out.writeByteArray( dataDirty );
        out.writeShortArray( error );
        out.writeShortArray( minY );
        out.writeShortArray( maxY );
    }
}
