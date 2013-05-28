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

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.io.Archive;
import org.xith3d.io.InvalidFormat;
import org.xith3d.io.UnscribableNodeEncountered;
import org.xith3d.utility.logging.X3DLog;

/**
 * A terrain class.
 * 
 * @author David Yazel
 */
public class Terrain implements GroundHeightInterface
{
    static float DetailThreshold = 100;
    static final float VERTICAL_SCALE = 1.0f;
    static LinkedList< TerrainCornerData > corners = new LinkedList< TerrainCornerData >();
    static Vector3f SunVector = new Vector3f( 0.0705f, -0.9875f, -0.1411f ); // For demo lighting.  Pick some unit vector pointing roughly downward.
    static int BlockDeleteCount = 0; //xxxxx
    static int BlockUpdateCount = 0; //xxxxx
    
    TerrainDataBank[] banks;
    
    // banks for high and low quality data.  The low quality data is sampled at a much lower
    // rate.  We can mix banks to achive high performance
    
    TerrainDataBank[] banksHigh;
    TerrainDataBank[] banksLow;
    
    int maxBanks;
    int maxLevel;
    int bankLevel;
    
    int highSsample;
    int lowSample;
    
    TerrainCornerData rootData;
    TerrainSquareHandle root;
    
    Archive pagingFile;
    
    public Terrain( int maxLevel, int bankLevel )
    {
        this.maxLevel = maxLevel;
        this.maxBanks = ( 2 << maxLevel ) / ( 2 << bankLevel );
        System.out.println( "World size is " + ( 2 << maxLevel ) );
        System.out.println( "bank size is " + ( 2 << bankLevel ) );
        System.out.println( "total number of banks is " + maxBanks + " in two directions" );
        this.bankLevel = bankLevel;
        banks = new TerrainDataBank[ maxBanks * maxBanks + 1 ];
        for ( int i = 0; i < maxBanks * maxBanks; i++ )
        {
            banks[ i ] = new TerrainDataBank( i, 10000 );
        }
        banks[ maxBanks * maxBanks ] = new TerrainDataBank( maxBanks * maxBanks, 1000 );
        
        rootData = new TerrainCornerData();
        rootData.level = maxLevel;
        
        root = newSquare( rootData );
        rootData.square = root;
    }
    
    public int getWidth()
    {
        return ( 2 << maxLevel );
    }
    
    public int getDepth()
    {
        return ( 2 << maxLevel );
    }
    
    public void load( String filename ) throws IOException
    {
        pagingFile = new Archive( filename, true );
        for ( int i = 0; i <= maxBanks * maxBanks; i++ )
        {
            try
            {
                TerrainDataBank b = (TerrainDataBank)pagingFile.read( "BANK_HIGH_" + i );
                banks[ i ].newBank = b;
                banks[ i ] = b;
            }
            catch ( IOException e )
            {
                X3DLog.print( e );
                throw new IOException( "cannot read terrain banks" );
            }
            catch ( InvalidFormat invalidFormat )
            {
                X3DLog.print( invalidFormat );
                throw new IOException( "cannot read terrain banks" );
            }
        }
        
        this.resetTree( root );
        this.recomputeError();
    }
    
    public void open( String filename )
    {
    }
    
    /**
     * Rebuilds the banks by getting rid of the deleted nodes and shifting all
     * the nodes towards the front of the bank.  Very useful after a call to CullStatic
     */
    public void compressBanks()
    {
        for ( int i = 0; i < maxBanks * maxBanks; i++ )
            banks[ i ] = TerrainDataBank.compress( banks[ i ] );
    }
    
    public void printBankUsage()
    {
        for ( int i = 0; i < maxBanks; i++ )
            for ( int j = 0; j < maxBanks; j++ )
                System.out.println( "Bank [" + i + "][" + j + "] = " + banks[ i * maxBanks + j ].freeList );
        System.out.println( "Master Bank  = " + banks[ maxBanks * maxBanks ].freeList );
    }
    
    private int getBank( TerrainCornerData pcd )
    {
        // ok now we need to determine what bank this goes in
        
        if ( pcd.level > bankLevel )
        {
            return ( maxBanks * maxBanks );
        }
        
        int x = pcd.xorg / ( 2 << bankLevel );
        int z = pcd.zorg / ( 2 << bankLevel );
        
        if ( ( x >= maxBanks ) || ( z >= maxBanks ) )
            throw new Error( "attempt to get coord outside banks " + pcd.xorg + "," + pcd.zorg );
        
        return ( x * maxBanks + z );
    }
    
    private TerrainSquareHandle newSquare( TerrainCornerData pcd )
    {
        // ok now we need to determine what bank this goes in
        
        TerrainSquareHandle s = new TerrainSquareHandle();
        s.bank = getBank( pcd );
        
        try
        {
            s.node = banks[ s.bank ].allocateNode();
        }
        catch ( Throwable t )
        {
            banks[ s.bank ] = TerrainDataBank.expand( banks[ s.bank ] );
            s.node = banks[ s.bank ].allocateNode();
            //Log.print( "Expanded " + s.bank + "for level " + pcd.level + " to " + banks[ s.bank ].maxNodes );
            
            //System.exit( 0 );
        }
        s.b = banks[ s.bank ];
        pcd.square = s;
        
        /*
         * Set static to true if/when this node contains real data, and
         * not just interpolated values.  When static == false, a node
         * can be deleted by the Update() function if none of its
         * vertices or children are enabled.
         */

        s.setStatic( false );
        
        int i;
        
        for ( i = 0; i < 4; i++ )
        {
            s.setChild( i, -1 );
        }
        
        s.setEnabledFlags( (byte)0 );
        
        for ( i = 0; i < 2; i++ )
        {
            s.setSubEnabledCount( i, (byte)0 );
        }
        
        // Set default vertex positions by interpolating from given corners.
        // Just bilinear interpolation.
        s.setY( 0, 0.25f * ( pcd.y[ 0 ] + pcd.y[ 1 ] + pcd.y[ 2 ] + pcd.y[ 3 ] ) );
        s.setY( 1, 0.5f * ( pcd.y[ 3 ] + pcd.y[ 0 ] ) );
        s.setY( 2, 0.5f * ( pcd.y[ 0 ] + pcd.y[ 1 ] ) );
        s.setY( 3, 0.5f * ( pcd.y[ 1 ] + pcd.y[ 2 ] ) );
        s.setY( 4, 0.5f * ( pcd.y[ 2 ] + pcd.y[ 3 ] ) );
        
        for ( i = 0; i < 2; i++ )
        {
            s.setError( i, (short)0 );
        }
        
        for ( i = 0; i < 4; i++ )
        {
            s.setError( i + 2, (short)( Math.abs( ( s.getY( 0 ) + pcd.y[ i ] ) - ( s.getY( i + 1 ) + s.getY( ( ( i + 1 ) & 3 ) + 1 ) ) ) * 0.25f ) );
        }
        
        // Compute MinY/MaxY based on corner verts.
        s.setMinY( (short)pcd.y[ 0 ] );
        s.setMaxY( (short)pcd.y[ 0 ] );
        
        for ( i = 1; i < 4; i++ )
        {
            float y = pcd.y[ i ];
            
            if ( y < s.getMinY() )
            {
                s.setMinY( (short)y );
            }
            
            if ( y > s.getMaxY() )
            {
                s.setMaxY( (short)y );
            }
        }
        
        return ( s );
    }
    
    private synchronized static TerrainCornerData getTerrainCornerData()
    {
        //      return ( new TerrainCornerData() );
        if ( corners.isEmpty() )
        {
            return ( new TerrainCornerData() );
        }
        
        TerrainCornerData qd = corners.removeFirst();
        qd.init();
        
        return ( qd );
    }
    
    public synchronized static void releaseCorner( TerrainCornerData o )
    {
        corners.add( o );
    }
    
    // Fills the given structure with the appropriate corner values for the
    // specified child block, given our own vertex data and our corner
    // vertex data from cd.
    //
    // ChildIndex mapping:
    // +-+-+
    // |1|0|
    // +-+-+
    // |2|3|
    // +-+-+
    //
    // Verts mapping:
    // 1-0
    // | |
    // 2-3
    //
    // vertex mapping:
    // +-2-+
    // | | |
    // 3-0-1
    // | | |
    // +-4-+
    private void SetupCornerData( TerrainCornerData q, TerrainCornerData cd, int childIndex )
    {
        int half = 1 << cd.level;
        
        q.parent = cd;
        
        q.square = new TerrainSquareHandle();
        q.square.node = cd.square.getChild( childIndex );
        q.square.bank = cd.square.getChildBank( childIndex );
        
        if ( q.square.bank >= 0 )
            q.square.b = banks[ q.square.bank ];
        
        q.level = cd.level - 1;
        q.childIndex = childIndex;
        
        switch ( childIndex )
        {
            default:
            case 0:
                q.xorg = cd.xorg + half;
                q.zorg = cd.zorg;
                q.y[ 0 ] = cd.y[ 0 ];
                q.y[ 1 ] = cd.square.getY( 2 );
                q.y[ 2 ] = cd.square.getY( 0 );
                q.y[ 3 ] = cd.square.getY( 1 );
                
                break;
            
            case 1:
                q.xorg = cd.xorg;
                q.zorg = cd.zorg;
                
                q.y[ 0 ] = cd.square.getY( 2 );
                q.y[ 1 ] = cd.y[ 1 ];
                q.y[ 2 ] = cd.square.getY( 3 );
                q.y[ 3 ] = cd.square.getY( 0 );
                
                break;
            
            case 2:
                q.xorg = cd.xorg;
                q.zorg = cd.zorg + half;
                
                q.y[ 0 ] = cd.square.getY( 0 );
                q.y[ 1 ] = cd.square.getY( 3 );
                q.y[ 2 ] = cd.y[ 2 ];
                q.y[ 3 ] = cd.square.getY( 4 );
                
                break;
            
            case 3:
                q.xorg = cd.xorg + half;
                q.zorg = cd.zorg + half;
                
                q.y[ 0 ] = cd.square.getY( 1 );
                q.y[ 1 ] = cd.square.getY( 0 );
                q.y[ 2 ] = cd.square.getY( 4 );
                q.y[ 3 ] = cd.y[ 3 ];
                
                break;
        }
    }
    
    /**
     * Sets this node's static flag to true.  If static == true, then the
     * node or its children is considered to contain significant height data
     * and shouldn't be deleted.
     * 
     * @param cd
     */
    void SetStatic( final TerrainCornerData cd )
    {
        if ( cd.square.getStatic() == false )
        {
            cd.square.setStatic( true );
            
            // Propagate static status to ancestor nodes.
            if ( ( cd.parent != null ) && ( cd.parent.square != null ) )
            {
                SetStatic( cd.parent );
            }
        }
    }
    
    public int CountNodes()
    {
        return ( CountNodes( root ) );
    }
    
    /**
     * Debugging function.  Counts the number of nodes in this subtree.
     * 
     * @param sq
     * @return the number of nodes
     */
    public int CountNodes( TerrainSquareHandle sq )
    {
        int count = 1; // Count ourself.
        
        // Count descendants.
        for ( int i = 0; i < 4; i++ )
        {
            TerrainSquareHandle c = getChild( sq, i );
            if ( c != null )
            {
                count += CountNodes( c );
            }
        }
        
        return ( count );
    }
    
    public float getY( float x, float z )
    {
        return ( getHeight( rootData, x, z, false ) );
    }
    
    public float getCurY( float x, float z )
    {
        return ( getHeight( rootData, x, z, false ) );
    }
    
    /**
     * @param cd
     * @param x
     * @param z
     * @param enabledOnly
     * @return the height of the heightfield at the specified x,z coordinates.
     */
    float getHeight( final TerrainCornerData cd, float x, float z, boolean enabledOnly )
    {
        final int half = 1 << cd.level;
        
        float lx = ( x - cd.xorg ) / (float)half;
        float lz = ( z - cd.zorg ) / (float)half;
        
        int ix = (int)Math.floor( lx );
        int iz = (int)Math.floor( lz );
        
        // Clamp.
        if ( ix < 0 )
        {
            ix = 0;
        }
        
        if ( ix > 1 )
        {
            ix = 1;
        }
        
        if ( iz < 0 )
        {
            iz = 0;
        }
        
        if ( iz > 1 )
        {
            iz = 1;
        }
        
        int index = ix ^ ( ( iz ^ 1 ) + ( iz << 1 ) );
        
        TerrainSquareHandle c = getChild( cd, index );
        if ( c != null && ( !enabledOnly || ( c.getEnabledFlags() & ( 16 << index ) ) != 0 ) )
            if ( ( c != null ) && c.getStatic() )
            {
                // Pass the query down to the child which contains it.
                TerrainCornerData q = getTerrainCornerData();
                SetupCornerData( q, cd, index );
                
                float height = getHeight( q, x, z, enabledOnly );
                releaseCorner( q );
                
                return ( height );
            }
        
        // Bilinear interpolation.
        lx -= ix;
        
        if ( lx < 0 )
        {
            lx = 0;
        }
        
        if ( lx > 1 )
        {
            lx = 1;
        }
        
        lz -= iz;
        
        if ( lx < 0 )
        {
            lz = 0;
        }
        
        if ( lz > 1 )
        {
            lz = 1;
        }
        
        float s00;
        float s01;
        float s10;
        float s11;
        
        switch ( index )
        {
            default:
            case 0:
                s00 = cd.square.getY( 2 );
                s01 = cd.y[ 0 ];
                s10 = cd.square.getY( 0 );
                s11 = cd.square.getY( 1 );
                
                break;
            
            case 1:
                s00 = cd.y[ 1 ];
                s01 = cd.square.getY( 2 );
                s10 = cd.square.getY( 3 );
                s11 = cd.square.getY( 0 );
                
                break;
            
            case 2:
                s00 = cd.square.getY( 3 );
                s01 = cd.square.getY( 0 );
                s10 = cd.y[ 2 ];
                s11 = cd.square.getY( 4 );
                
                break;
            
            case 3:
                s00 = cd.square.getY( 0 );
                s01 = cd.square.getY( 1 );
                s10 = cd.square.getY( 4 );
                s11 = cd.y[ 3 ];
                
                break;
        }
        
        return ( ( ( ( s00 * ( 1 - lx ) ) + ( s01 * lx ) ) * ( 1 - lz ) ) + ( ( ( s10 * ( 1 - lx ) ) + ( s11 * lx ) ) * lz ) );
    }
    
    // Traverses the tree in search of the quadsquare neighboring this square to the
    // specified direction.  0-3 --> { E, N, W, S }.
    // @return NULL if the neighbor is outside the bounds of the tree.
    TerrainSquareHandle GetNeighbor( TerrainSquareHandle sq, int dir, final TerrainCornerData cd )
    {
        // If we don't have a parent, then we don't have a neighbor.
        // (Actually, we could have inter-tree connectivity at this level
        // for connecting separate trees together.)
        if ( cd.parent == null )
        {
            return ( null );
        }
        
        // Find the parent and the child-index of the square we want to locate or create.
        TerrainSquareHandle p = null;
        
        int index = cd.childIndex ^ 1 ^ ( ( dir & 1 ) << 1 );
        boolean SameParent = ( ( ( dir - cd.childIndex ) & 2 ) != 0 ) ? true : false;
        
        if ( SameParent )
        {
            p = cd.parent.square;
        }
        else
        {
            p = GetNeighbor( cd.parent.square, dir, cd.parent );
            
            if ( p == null )
            {
                return ( null );
            }
        }
        
        TerrainSquareHandle n = getChild( p, index );
        
        return ( n );
    }
    
    public float recomputeError()
    {
        return ( recomputeError( root, rootData ) );
    }
    
    /**
     * Recomputes the error values for this tree.
     * Also updates MinY & MaxY.
     * Also computes quick & dirty vertex lighting for the demo.
     * 
     * @return the max error.
     * 
     * @param sq
     * @param cd
     */
    float recomputeError( TerrainSquareHandle sq, final TerrainCornerData cd )
    {
        int i;
        
        // Measure error of center and edge vertices.
        float maxerror = 0;
        
        // Compute error of center vert.
        float e;
        
        if ( ( cd.childIndex & 1 ) != 0 )
        {
            e = Math.abs( sq.getY( 0 ) - ( ( cd.y[ 1 ] + cd.y[ 3 ] ) * 0.5f ) );
        }
        else
        {
            e = Math.abs( sq.getY( 0 ) - ( ( cd.y[ 0 ] + cd.y[ 2 ] ) * 0.5f ) );
        }
        
        if ( e > maxerror )
        {
            maxerror = e;
        }
        
        // Initial min/max.
        short MaxY = (short)sq.getY( 0 );
        short MinY = (short)sq.getY( 0 );
        
        // Check min/max of corners.
        for ( i = 0; i < 4; i++ )
        {
            float y = cd.y[ i ];
            
            if ( y < MinY )
            {
                MinY = (short)y;
            }
            
            if ( y > MaxY )
            {
                MaxY = (short)y;
            }
        }
        
        // Edge verts.
        e = Math.abs( sq.getY( 1 ) - ( ( cd.y[ 0 ] + cd.y[ 3 ] ) * 0.5f ) );
        
        if ( e > maxerror )
        {
            maxerror = e;
        }
        
        sq.setError( 0, (short)e );
        
        e = Math.abs( sq.getY( 4 ) - ( ( cd.y[ 2 ] + cd.y[ 3 ] ) * 0.5f ) );
        
        if ( e > maxerror )
        {
            maxerror = e;
        }
        
        sq.setError( 1, (short)e );
        
        // Min/max of edge verts.
        for ( i = 0; i < 4; i++ )
        {
            float y = sq.getY( 1 + i );
            
            if ( y < MinY )
            {
                MinY = (short)y;
            }
            
            if ( y > MaxY )
            {
                MaxY = (short)y;
            }
        }
        
        cd.square.setMinY( MinY );
        cd.square.setMaxY( MaxY );
        
        // Check child squares.
        for ( i = 0; i < 4; i++ )
        {
            TerrainCornerData q = new TerrainCornerData();
            
            TerrainSquareHandle c = getChild( cd, i );
            if ( c != null )
            {
                SetupCornerData( q, cd, i );
                
                sq.setError( i + 2, (short)recomputeError( q.square, q ) );
                
                if ( q.square.getMinY() < MinY )
                {
                    MinY = q.square.getMinY();
                }
                
                if ( q.square.getMaxY() > MaxY )
                {
                    MaxY = q.square.getMaxY();
                }
            }
            else
            {
                // Compute difference between bilinear average at child center, and diagonal edge approximation.
                sq.setError( i + 2, (short)( Math.abs( ( sq.getY( 0 ) + cd.y[ i ] ) - ( sq.getY( i + 1 ) + sq.getY( ( ( i + 1 ) & 3 ) + 1 ) ) ) * 0.25f ) );
            }
            
            if ( sq.getError( i + 2 ) > maxerror )
            {
                maxerror = sq.getError( i + 2 );
            }
        }
        
        // The error, MinY/MaxY, and lighting values for this node and descendants are correct now.
        cd.square.setDirty( false );
        cd.square.setMinY( MinY );
        cd.square.setMaxY( MaxY );
        
        return ( maxerror );
    }
    
    /**
     * Clears all enabled flags, and delete all non-static child nodes.
     * 
     * @param sq
     */
    void resetTree( TerrainSquareHandle sq )
    {
        int i;
        
        for ( i = 0; i < 4; i++ )
        {
            TerrainSquareHandle c = getChild( sq, i );
            if ( c != null )
            {
                resetTree( c );
                
                if ( c.getStatic() == false )
                {
                    //delete Child[ i ];
                    c.delete();
                    sq.setChild( i, -1 );
                    sq.setChildBank( i, -1 );
                }
            }
        }
        
        sq.setEnabledFlags( (byte)0 );
        sq.setSubEnabledCount( 0, (byte)0 );
        sq.setSubEnabledCount( 1, (byte)0 );
        sq.setDirty( true );
    }
    
    public void cullStaticData( float threshold, int maxLevelToCull )
    {
        staticCullData( root, rootData, threshold, maxLevelToCull );
    }
    
    /**
     * Examine the tree and remove nodes which don't contain necessary
     * detail.  Necessary detail is defined as vertex data with a
     * edge-length to height ratio less than ThresholdDetail.
     * 
     * @param sq
     * @param cd
     * @param thresholdDetail
     * @param maxLevelToCull
     */
    void staticCullData( TerrainSquareHandle sq, final TerrainCornerData cd, float thresholdDetail, int maxLevelToCull )
    {
        // First, clean non-static nodes out of the tree.
        resetTree( sq );
        
        // Make sure error values are up-to-date.
        if ( sq.getDirty() )
        {
            recomputeError( sq, cd );
        }
        
        // Recursively check all the nodes and do necessary removal.
        // We must start at the bottom of the tree, and do one level of
        // the tree at a time, to ensure the dependencies are accounted
        // for properly.
        int level;
        
        for ( level = 0; level < maxLevelToCull; level++ )
        {
            staticCullAux( sq, cd, thresholdDetail, level );
        }
    }
    
    /**
     * Check this node and its descendents, and remove nodes which don't contain
     * necessary detail.
     * 
     * @param sq
     * @param cd
     * @param TargetLevel
     */
    void deleteStaticData( TerrainSquareHandle sq, final TerrainCornerData cd, int TargetLevel )
    {
        int i;
        int j;
        
        if ( cd.level > TargetLevel )
        {
            TerrainCornerData q = new TerrainCornerData();
            
            // Just recurse to child nodes.
            for ( j = 0; j < 4; j++ )
            {
                if ( j < 2 )
                {
                    i = 1 - j;
                }
                else
                {
                    i = j;
                }
                
                TerrainSquareHandle c = getChild( sq, i );
                if ( c != null )
                {
                    SetupCornerData( q, cd, i );
                    deleteStaticData( c, q, TargetLevel );
                }
            }
            
            return;
        }
        
        // See if we have child nodes.
        boolean StaticChildren = false;
        
        for ( i = 0; i < 4; i++ )
        {
            if ( getChild( sq, i ) != null )
            {
                StaticChildren = true;
                
                if ( getChild( sq, i ).getDirty() )
                {
                    sq.setDirty( true );
                }
            }
        }
        
        /*
        if (StaticChildren)
            Log.print( "we have static children" );
        else
            Log.print( "we do not have static children" );
            Log.print( "after verts are " + sq.getY( 0 ) + "," + sq.getY( 1 ) + "," + sq.getY( 2 ) + "," + sq.getY( 3 ) + "," + sq.getY( 4 ) );
        */

        // If we have no children and no necessary edges, then see if we can delete ourself.
        if ( ( StaticChildren == false ) && ( cd.parent != null ) )
        {
            //delete cd.parent.Square.Child[ cd.ChildIndex ];	// Delete this.
            TerrainSquareHandle c = getChild( cd.parent.square, cd.childIndex );
            c.delete();
            cd.parent.square.setChild( cd.childIndex, -1 );
            cd.parent.square.setChildBank( cd.childIndex, -1 );
        }
    }
    
    // Check this node and its descendents, and remove nodes which don't contain
    // necessary detail.
    void staticCullAux( TerrainSquareHandle sq, final TerrainCornerData cd, float thresholdDetail, int targetLevel )
    {
        int i;
        int j;
        
        if ( cd.level > targetLevel )
        {
            TerrainCornerData q = new TerrainCornerData();
            
            // Just recurse to child nodes.
            for ( j = 0; j < 4; j++ )
            {
                if ( j < 2 )
                {
                    i = 1 - j;
                }
                else
                {
                    i = j;
                }
                
                TerrainSquareHandle c = getChild( sq, i );
                if ( c != null )
                {
                    SetupCornerData( q, cd, i );
                    staticCullAux( c, q, thresholdDetail, targetLevel );
                }
            }
            
            return;
        }
        
        //Log.print( "we are checking node :" + cd.xorg + "," + cd.zorg + " with height " + sq.getY( 3 ) );
        
        // We're at the target level.  Check this node to see if it's OK to delete it.
        // Check edge vertices to see if they're necessary.
        float size = 2 << cd.level; // Edge length.
        
        //Log.print( "after verts are " + sq.getY( 0 ) + "," + sq.getY( 1 ) + "," + sq.getY( 2 ) + "," + sq.getY( 3 ) + "," + sq.getY( 4 ) );
        //Log.print( "error 0 is " + sq.getError( 0 ) );
        if ( ( sq.getChild( 0 ) == -1 ) && ( sq.getChild( 3 ) == -1 ) && ( ( sq.getError( 0 ) * thresholdDetail ) < size ) )
        {
            TerrainSquareHandle s = GetNeighbor( sq, 0, cd );
            
            //if (s != null)
            //if ((s.bank == sq.bank) && (s.node == sq.node)) throw new Error( "neighbor is same as self" ) );
            
            if ( ( s == null ) || ( ( s.getChild( 1 ) == -1 ) && ( s.getChild( 2 ) == -1 ) ) )
            {
                // Force vertex height to the edge value.
                float y = ( cd.y[ 0 ] + cd.y[ 3 ] ) * 0.5f;
                sq.setY( 1, y );
                sq.setError( 0, (short)0 );
                //Log.print( "setting Y[1] to " + y );
                
                // Force alias vertex to match.
                if ( s != null )
                {
                    s.setY( 3, y );
                    //Log.print( "setting neghbor Y[3] to " + y );
                }
                sq.setDirty( true );
            }
        }
        
        //Log.print( "error 1 is " + sq.getError( 1 ) );
        if ( ( sq.getChild( 2 ) == -1 ) && ( sq.getChild( 3 ) == -1 ) && ( ( sq.getError( 1 ) * thresholdDetail ) < size ) )
        {
            TerrainSquareHandle s = GetNeighbor( sq, 3, cd );
            //if (s != null)
            //if ((s.bank == sq.bank) && (s.node == sq.node)) throw new Error( "neighbor is same as self" ) );
            
            if ( ( s == null ) || ( ( s.getChild( 0 ) == -1 ) && ( s.getChild( 1 ) == -1 ) ) )
            {
                float y = ( cd.y[ 2 ] + cd.y[ 3 ] ) * 0.5f;
                //Log.print( "setting Y[4] to " + y );
                sq.setY( 4, y );
                sq.setError( 1, (short)0 );
                
                if ( s != null )
                {
                    //Log.print( "setting neghbor Y[2] to " + y );
                    s.setY( 2, y );
                }
                
                sq.setDirty( true );
            }
        }
        
        // See if we have child nodes.
        boolean staticChildren = false;
        
        for ( i = 0; i < 4; i++ )
        {
            if ( getChild( sq, i ) != null )
            {
                staticChildren = true;
                
                if ( getChild( sq, i ).getDirty() )
                {
                    sq.setDirty( true );
                }
            }
        }
        
        /*
        if (StaticChildren)
            Log.print( "we have static children" );
        else
            Log.print( "we do not have static children" );
            Log.print( "after verts are " + sq.getY( 0 ) + "," + sq.getY( 1 ) + "," + sq.getY( 2 ) + "," + sq.getY( 3 ) + "," + sq.getY( 4 ) );
        */

        // If we have no children and no necessary edges, then see if we can delete ourself.
        if ( ( staticChildren == false ) && ( cd.parent != null ) )
        {
            boolean necessaryEdges = false;
            
            for ( i = 0; i < 4; i++ )
            {
                // See if vertex deviates from edge between corners.
                
                //Log.print( "  sub values are " + sq.getY( i + 1 ) + ", " + cd.y[ i ] + ", " + cd.y[ (i + 3) & 3 ] );
                // See if vertex deviates from edge between corners.
                float diff = Math.abs( sq.getY( i + 1 ) - ( ( cd.y[ i ] + cd.y[ ( i + 3 ) & 3 ] ) * 0.5f ) );
                
                //Log.print("diff = "+diff);
                if ( diff > 0.00001f )
                {
                    necessaryEdges = true;
                }
            }
            
            if ( !necessaryEdges )
            {
                //Log.print( "no necessary edges" );
                size *= 1.414213562f; // sqrt( 2 ), because diagonal is longer than side.
                
                //Log.print( "checking with size " + size );
                //Log.print( "tested against " + cd.parent.square.getError( 2 + cd.childIndex ) * ThresholdDetail );
                
                if ( ( cd.parent.square.getError( 2 + cd.childIndex ) * thresholdDetail ) < size )
                {
                    //delete cd.parent.Square.Child[ cd.ChildIndex ]; // Delete this.
                    TerrainSquareHandle c = getChild( cd.parent.square, cd.childIndex );
                    c.delete();
                    cd.parent.square.setChild( cd.childIndex, -1 );
                    cd.parent.square.setChildBank( cd.childIndex, -1 );
                    //System.out.println( "Deleting node" );
                }
                //throw new Error( "done" ) );
            }
            //else Log.print( "we have necessary edges" );
        }
    }
    
    // Enable the specified edge vertex.  Indices go { e, n, w, s }.
    // Increments the appropriate reference-count if IncrementCount is true.
    void enableEdgeVertex( TerrainSquareHandle sq, int index, boolean incrementCount, final TerrainCornerData cd )
    {
        if ( ( ( sq.getEnabledFlags() & ( 1 << index ) ) != 0 ) && !incrementCount )
        {
            return;
        }
        
        // Turn on flag and deal with reference count.
        sq.setEnabledFlags( (byte)( sq.getEnabledFlags() | ( 1 << index ) ) );
        
        if ( ( incrementCount == true ) && ( ( index == 0 ) || ( index == 3 ) ) )
        {
            sq.incSubEnabledCount( index & 1 );
        }
        
        /*
         * Now we need to enable the opposite edge vertex of the adjacent square (i.e. the alias vertex).
         * This is a little tricky, since the desired neighbor node may not exist, in which
         * case we have to create it, in order to prevent cracks.  Creating it may in turn cause
         * further edge vertices to be enabled, propagating updates through the tree.
         * The sticking point is the TerrainCornerData list, which
         * conceptually is just a linked list of activation structures.
         * In this function, however, we will introduce branching into
         * the "list", making it in actuality a tree.  This is all kind
         * of obscure and hard to explain in words, but basically what
         * it means is that our implementation has to be properly
         * recursive.
         * Travel upwards through the tree, looking for the parent in common with our desired neighbor.
         * Remember the path through the tree, so we can travel down the complementary path to get to the neighbor.
         */
        TerrainSquareHandle p = cd.square;
        TerrainCornerData pcd = cd;
        
        int ct = 0;
        int[] stack = new int[ 32 ];
        
        for ( ;; )
        {
            int ci = pcd.childIndex;
            
            if ( ( pcd.parent == null ) || ( pcd.parent.square == null ) )
            {
                // Neighbor doesn't exist (it's outside the tree), so there's no alias vertex to enable.
                return;
            }
            
            p = pcd.parent.square;
            pcd = pcd.parent;
            
            boolean SameParent = ( ( ( index - ci ) & 2 ) != 0 ) ? true : false;
            
            ci = ci ^ 1 ^ ( ( index & 1 ) << 1 ); // Child index of neighbor node.
            
            stack[ ct ] = ci;
            ct++;
            
            if ( SameParent )
            {
                break;
            }
        }
        
        // Get a pointer to our neighbor (create if necessary), by walking down
        // the quadtree from our shared ancestor.
        p = EnableDescendant( p, ct, stack, pcd );
        if ( p == null )
            throw new Error( "enabled descendant is null!" );
        
        // Finally: enable the vertex on the opposite edge of our neighbor, the alias of the original vertex.
        index ^= 2;
        p.setEnabledFlags( (byte)( p.getEnabledFlags() | ( 1 << index ) ) );
        
        if ( ( incrementCount == true ) && ( ( index == 0 ) || ( index == 3 ) ) )
        {
            p.setSubEnabledCount( index & 1, (byte)( p.getSubEnabledCount( index & 1 ) + 1 ) );
        }
    }
    
    TerrainSquareHandle getChild( TerrainCornerData cd, int index )
    {
        if ( cd.square.getChild( index ) == -1 )
            return ( null );
        TerrainSquareHandle s = new TerrainSquareHandle();
        s.node = cd.square.getChild( index );
        s.bank = cd.square.getChildBank( index );
        s.b = banks[ s.bank ];
        
        return ( s );
    }
    
    TerrainSquareHandle getChild( TerrainSquareHandle s, int index )
    {
        if ( s.getChild( index ) == -1 )
            return ( null );
        TerrainSquareHandle ss = new TerrainSquareHandle();
        ss.node = s.getChild( index );
        ss.bank = s.getChildBank( index );
        ss.b = banks[ ss.bank ];
        return ( ss );
    }
    
    // This function enables the descendant node 'count' generations below
    // us, located by following the list of child indices in path[].
    // Creates the node if necessary, and returns a pointer to it.
    TerrainSquareHandle EnableDescendant( TerrainSquareHandle sq, int count, int[] path, final TerrainCornerData cd )
    {
        count--;
        
        int ChildIndex = path[ count ];
        
        if ( ( cd.square.getEnabledFlags() & ( 16 << ChildIndex ) ) == 0 )
        {
            EnableChild( sq, ChildIndex, cd );
        }
        
        if ( count <= 0 )
            return ( getChild( cd, ChildIndex ) );
        
        TerrainCornerData q = new TerrainCornerData();
        SetupCornerData( q, cd, ChildIndex );
        
        TerrainSquareHandle qs = EnableDescendant( sq, count, path, q );
        
        return ( qs );
    }
    
    /**
     * Creates a child square at the specified index.
     * 
     * @param sq
     * @param index
     * @param cd
     */
    void CreateChild( TerrainSquareHandle sq, int index, final TerrainCornerData cd )
    {
        if ( cd.square.getChild( index ) == -1 )
        {
            TerrainCornerData q = new TerrainCornerData();
            SetupCornerData( q, cd, index );
            
            TerrainSquareHandle h = newSquare( q );
            cd.square.setChild( index, h.node );
            cd.square.setChildBank( index, h.bank );
        }
    }
    
    /**
     * Enables the indexed child node.  { ne, nw, sw, se }
     * Causes dependent edge vertices to be enabled.
     * 
     * @param sq
     * @param index
     * @param cd
     */
    void EnableChild( TerrainSquareHandle sq, int index, final TerrainCornerData cd )
    {
        //if (Enabled[index + 4] == false) {
        if ( ( cd.square.getEnabledFlags() & ( 16 << index ) ) == 0 )
        {
            //Enabled[ index + 4 ] = true;
            
            cd.square.setEnabledFlags( (byte)( cd.square.getEnabledFlags() | ( 16 << index ) ) );
            enableEdgeVertex( sq, index, true, cd );
            enableEdgeVertex( sq, ( index + 1 ) & 3, true, cd );
            
            if ( getChild( cd, index ) == null )
            {
                CreateChild( sq, index, cd );
            }
        }
    }
    
    void NotifyChildDisable( TerrainSquareHandle sq, final TerrainCornerData cd, int index )
    {
        // Clear enabled flag for the child.
        cd.square.setEnabledFlags( (byte)( cd.square.getEnabledFlags() & ~( 16 << index ) ) );
        
        // Update child enabled counts for the affected edge verts.
        TerrainSquareHandle s;
        
        if ( ( index & 2 ) != 0 )
        {
            s = cd.square;
        }
        else
        {
            s = GetNeighbor( sq, 1, cd );
        }
        
        if ( s != null )
        {
            s.setSubEnabledCount( 1, (byte)( s.getSubEnabledCount( 1 ) - 1 ) );
        }
        
        if ( ( index == 1 ) || ( index == 2 ) )
        {
            s = GetNeighbor( sq, 2, cd );
        }
        else
        {
            s = cd.square;
        }
        
        if ( s != null )
        {
            s.setSubEnabledCount( 0, (byte)( s.getSubEnabledCount( 0 ) - 1 ) );
        }
        
        TerrainSquareHandle c = getChild( sq, index );
        if ( c.getStatic() == false )
        {
            //delete Child[ index ];
            
            c.delete();
            sq.setChild( index, -1 );
            sq.setChildBank( index, -1 );
            
            BlockDeleteCount++; //xxxxx
        }
    }
    
    /**
     * @return true if the vertex at (x,z) with the given world-space error between
     * its interpolated location and its true location, should be enabled, given that
     * the viewpoint is located at Viewer[].
     */
    static boolean vertexTest( float x, float y, float z, float error, final float[] Viewer )
    {
        final float dx = Math.abs( x - Viewer[ 0 ] );
        final float dy = Math.abs( y - Viewer[ 1 ] );
        final float dz = Math.abs( z - Viewer[ 2 ] );
        float d = dx;
        
        if ( dy > d )
        {
            d = dy;
        }
        
        if ( dz > d )
        {
            d = dz;
        }
        
        return ( ( error * DetailThreshold ) > d );
    }
    
    /**
     * @return true if any vertex within the specified box (origin at x,z,
     * edges of length size) with the given error value could be enabled
     * based on the given viewer location.
     */
    static boolean BoxTest( float x, float z, float size, float miny, float maxy, float error, final float[] Viewer )
    {
        // Find the minimum distance to the box.
        final float half = size * 0.5f;
        final float dx = Math.abs( ( x + half ) - Viewer[ 0 ] ) - half;
        final float dy = Math.abs( ( ( miny + maxy ) * 0.5f ) - Viewer[ 1 ] ) - ( ( maxy - miny ) * 0.5f );
        final float dz = Math.abs( ( z + half ) - Viewer[ 2 ] ) - half;
        float d = dx;
        
        if ( dy > d )
        {
            d = dy;
        }
        
        if ( dz > d )
        {
            d = dz;
        }
        
        return ( ( error * DetailThreshold ) > d );
    }
    
    public void update( Tuple3f loc, float detail )
    {
        Update( root, rootData, new float[]
        {
            loc.getX(), loc.getY(), loc.getZ()
        }, detail );
    }
    
    /**
     * Refresh the vertex enabled states in the tree, according to the
     * location of the viewer.  May force creation or deletion of qsquares
     * in areas which need to be interpolated.
     */
    public void Update( TerrainSquareHandle sq, final TerrainCornerData cd, final float[] ViewerLocation, float Detail )
    {
        DetailThreshold = Detail * VERTICAL_SCALE;
        UpdateAux( sq, cd, ViewerLocation, 0 );
    }
    
    /**
     * Does the actual work of updating enabled states and tree growing/shrinking.
     * 
     * @param sq
     * @param cd
     * @param ViewerLocation
     * @param CenterError
     */
    void UpdateAux( TerrainSquareHandle sq, final TerrainCornerData cd, final float[] ViewerLocation, float CenterError )
    {
        BlockUpdateCount++; //xxxxx
        
        // Make sure error values are current.
        if ( sq.getDirty() )
        {
            recomputeError( sq, cd );
        }
        
        final int half = 1 << cd.level;
        final int whole = half << 1;
        
        // See about enabling child verts.
        if ( ( ( sq.getEnabledFlags() & 1 ) == 0 ) && ( vertexTest( cd.xorg + whole, sq.getY( 1 ), cd.zorg + half, sq.getError( 0 ), ViewerLocation ) == true ) )
        {
            enableEdgeVertex( sq, 0, false, cd ); // East vert.
        }
        
        if ( ( ( sq.getEnabledFlags() & 8 ) == 0 ) && ( vertexTest( cd.xorg + half, sq.getY( 4 ), cd.zorg + whole, sq.getError( 1 ), ViewerLocation ) == true ) )
        {
            enableEdgeVertex( sq, 3, false, cd ); // South vert.
        }
        
        if ( cd.level > 0 )
        {
            if ( ( sq.getEnabledFlags() & 32 ) == 0 )
            {
                if ( BoxTest( cd.xorg, cd.zorg, half, sq.getMinY(), sq.getMaxY(), sq.getError( 3 ), ViewerLocation ) == true )
                {
                    EnableChild( sq, 1, cd ); // nw child.er
                }
            }
            
            if ( ( sq.getEnabledFlags() & 16 ) == 0 )
            {
                if ( BoxTest( cd.xorg + half, cd.zorg, half, sq.getMinY(), sq.getMaxY(), sq.getError( 2 ), ViewerLocation ) == true )
                {
                    EnableChild( sq, 0, cd ); // ne child.
                }
            }
            
            if ( ( sq.getEnabledFlags() & 64 ) == 0 )
            {
                if ( BoxTest( cd.xorg, cd.zorg + half, half, sq.getMinY(), sq.getMaxY(), sq.getError( 4 ), ViewerLocation ) == true )
                {
                    EnableChild( sq, 2, cd ); // sw child.
                }
            }
            
            if ( ( sq.getEnabledFlags() & 128 ) == 0 )
            {
                if ( BoxTest( cd.xorg + half, cd.zorg + half, half, sq.getMinY(), sq.getMaxY(), sq.getError( 5 ), ViewerLocation ) == true )
                {
                    EnableChild( sq, 3, cd ); // se child.
                }
            }
            
            // Recurse into child quadrants as necessary.
            TerrainCornerData q = getTerrainCornerData();
            
            if ( ( sq.getEnabledFlags() & 32 ) != 0 )
            {
                SetupCornerData( q, cd, 1 );
                UpdateAux( q.square, q, ViewerLocation, sq.getError( 3 ) );
            }
            
            if ( ( sq.getEnabledFlags() & 16 ) != 0 )
            {
                SetupCornerData( q, cd, 0 );
                UpdateAux( q.square, q, ViewerLocation, sq.getError( 2 ) );
            }
            
            if ( ( sq.getEnabledFlags() & 64 ) != 0 )
            {
                SetupCornerData( q, cd, 2 );
                UpdateAux( q.square, q, ViewerLocation, sq.getError( 4 ) );
            }
            
            if ( ( sq.getEnabledFlags() & 128 ) != 0 )
            {
                SetupCornerData( q, cd, 3 );
                UpdateAux( q.square, q, ViewerLocation, sq.getError( 5 ) );
            }
            
            releaseCorner( q );
        }
        
        // Test for disabling.  East, South, and center.
        if ( ( ( sq.getEnabledFlags() & 1 ) != 0 ) && ( sq.getSubEnabledCount( 0 ) == 0 ) && ( vertexTest( cd.xorg + whole, sq.getY( 1 ), cd.zorg + half, sq.getError( 0 ), ViewerLocation ) == false ) )
        {
            sq.andEnabledFlags( (byte)~1 );
            
            TerrainSquareHandle s = GetNeighbor( sq, 0, cd );
            
            if ( s != null )
            {
                s.andEnabledFlags( (byte)~4 );
            }
        }
        
        if ( ( ( sq.getEnabledFlags() & 8 ) != 0 ) && ( sq.getSubEnabledCount( 1 ) == 0 ) && ( vertexTest( cd.xorg + half, sq.getY( 4 ), cd.zorg + whole, sq.getError( 1 ), ViewerLocation ) == false ) )
        {
            sq.andEnabledFlags( (byte)~8 );
            
            TerrainSquareHandle s = GetNeighbor( sq, 3, cd );
            
            if ( s != null )
            {
                s.andEnabledFlags( (byte)~2 );
            }
        }
        
        if ( ( sq.getEnabledFlags() == 0 ) && ( cd.parent != null ) && ( BoxTest( cd.xorg, cd.zorg, whole, sq.getMinY(), sq.getMaxY(), CenterError, ViewerLocation ) == false ) )
        {
            // Disable ourself.
            NotifyChildDisable( cd.parent.square, cd.parent, cd.childIndex ); // nb: possibly deletes 'this'.
        }
    }
    
    void clearHeightData( final TerrainCornerData cd, final TerrainSampleInterface hm )
    {
    }
    
    public void addData( TerrainSampleInterface sample )
    {
        AddHeightMap( root, rootData, sample, 0 );
    }
    
    public void addData( TerrainSampleInterface sample, float minDetail )
    {
        AddHeightMap( root, rootData, sample, minDetail );
    }
    
    /**
     * Sets the height of all samples within the specified rectangular
     * region using the given array of floats.  Extends the tree to the
     * level of detail defined by (1 << hm.Scale) as necessary.
     */
    void AddHeightMap( TerrainSquareHandle sq, final TerrainCornerData cd, final TerrainSampleInterface hm, float minDetail )
    {
        // If block is outside rectangle, then don't bother.
        int BlockSize = 2 << cd.level;
        
        if ( ( cd.xorg > ( hm.getXOrg() + ( ( hm.getXDim() + 2 ) << hm.getScale() ) ) ) || ( ( cd.xorg + BlockSize ) < ( hm.getXOrg() - ( 1 << hm.getScale() ) ) ) || ( cd.zorg > ( hm.getZOrg() + ( ( hm.getZDim() + 2 ) << hm.getScale() ) ) ) || ( ( cd.zorg + BlockSize ) < ( hm.getZOrg() - ( 1 << hm.getScale() ) ) ) )
        {
            // This square does not touch the given height array area; no need to modify this square or descendants.
            return;
        }
        
        if ( ( cd.parent != null ) && ( cd.parent.square != null ) )
        {
            EnableChild( cd.parent.square, cd.childIndex, cd.parent ); // causes parent edge verts to be enabled, possibly causing neighbor blocks to be created.
        }
        
        int i;
        
        int half = 1 << cd.level;
        
        // Create and update child nodes.
        for ( i = 0; i < 4; i++ )
        {
            TerrainCornerData q = new TerrainCornerData();
            SetupCornerData( q, cd, i );
            
            if ( ( sq.getChild( i ) == -1 ) && ( cd.level > hm.getScale() ) )
            {
                // Create child node w/ current (unmodified) values for corner verts.
                
                CreateChild( sq, i, cd );
                SetupCornerData( q, cd, i );
            }
            
            // Recurse.
            if ( sq.getChild( i ) != -1 )
            {
                AddHeightMap( q.square, q, hm, minDetail );
                
                if ( q.level == bankLevel )
                {
                    if ( minDetail > 0 )
                    {
                        int bank = getBank( q );
                        X3DLog.printlnEx( "compressing bank ", bank );
                        staticCullData( getChild( sq, i ), q, minDetail, bankLevel );
                        banks[ bank ] = TerrainDataBank.compress( banks[ bank ] );
                        System.gc();
                    }
                }
            }
        }
        
        // Deviate vertex heights based on data sampled from heightmap.
        float[] s = new float[ 5 ];
        s[ 0 ] = hm.sample( cd.xorg + half, cd.zorg + half );
        s[ 1 ] = hm.sample( cd.xorg + ( half * 2 ), cd.zorg + half );
        s[ 2 ] = hm.sample( cd.xorg + half, cd.zorg );
        s[ 3 ] = hm.sample( cd.xorg, cd.zorg + half );
        s[ 4 ] = hm.sample( cd.xorg + half, cd.zorg + ( half * 2 ) );
        
        // Modify the vertex heights if necessary, and set the dirty
        // flag if any modifications occur, so that we know we need to
        // recompute error data later.
        for ( i = 0; i < 5; i++ )
        {
            //if (s[ i ] != 0) {
            sq.setDirty( true );
            
            //vertex[ i ].Y += s[ i ];
            sq.setY( i, s[ i ] );
            
            //}
        }
        
        if ( !sq.getDirty() )
        {
            // Check to see if any child nodes are dirty, and set the dirty flag if so.
            for ( i = 0; i < 4; i++ )
            {
                if ( ( sq.getChild( i ) != -1 ) && getChild( sq, i ).getDirty() )
                {
                    sq.setDirty( true );
                    break;
                }
            }
        }
        
        if ( sq.getDirty() )
        {
            SetStatic( cd );
        }
    }
    
    public int render( TerrainRenderInterface r )
    {
        return ( render( rootData, r ) );
    }
    
    /**
     * Draws the heightfield represented by this tree.
     * @return the number of triangles rendered.
     */
    int render( TerrainCornerData cd, TerrainRenderInterface r )
    {
        int n = renderAux( cd.square, cd, r );
        
        return ( n );
    }
    
    /**
     * Does the work of rendering this square.  Uses the enabled vertices only.
     * Recurses as necessary.
     */
    int renderAux( final TerrainSquareHandle sq, final TerrainCornerData cd, TerrainRenderInterface r )
    {
        int half = 1 << cd.level;
        int whole = 2 << cd.level;
        
        //int blockSize = 2 << cd.level;
        
        /*
        if ((cd.xorg > r.ux) || ((cd.xorg + whole) < r.lx) || (cd.zorg > r.uz) || ((cd.zorg + whole) < r.lz))
        {
            // This square does not touch the given height array area; no need to modify this square or descendants.
            return ( 0 );
        }
        */

        // If this square is outside the bounds of the render then ignore
        int i;
        int num = 0;
        int flags = 0;
        int mask = 1;
        TerrainCornerData q = getTerrainCornerData();
        
        for ( i = 0; i < 4; i++, mask <<= 1 )
        {
            if ( ( sq.getEnabledFlags() & ( 16 << i ) ) != 0 )
            {
                SetupCornerData( q, cd, i );
                num += renderAux( q.square, q, r );
            }
            else
            {
                flags |= mask;
            }
        }
        
        releaseCorner( q );
        
        if ( flags == 0 )
        {
            return ( num );
        }
        
        r.start();
        //	// xxx debug color.
        //	glColor3f(cd.level * 10 / 255.0, ((cd.level & 3) * 60 + ((cd.zorg >> cd.level) & 255)) / 255.0, ((cd.level & 7) * 30 + ((cd.xorg >> cd.level) & 255)) / 255.0);
        // Init vertex data.
        r.initVert( 0, cd.xorg + half, sq.getY( 0 ), cd.zorg + half );
        r.initVert( 1, cd.xorg + whole, sq.getY( 1 ), cd.zorg + half );
        r.initVert( 2, cd.xorg + whole, cd.y[ 0 ], cd.zorg );
        r.initVert( 3, cd.xorg + half, sq.getY( 2 ), cd.zorg );
        r.initVert( 4, cd.xorg, cd.y[ 1 ], cd.zorg );
        r.initVert( 5, cd.xorg, sq.getY( 3 ), cd.zorg + half );
        r.initVert( 6, cd.xorg, cd.y[ 2 ], cd.zorg + whole );
        r.initVert( 7, cd.xorg + half, sq.getY( 4 ), cd.zorg + whole );
        r.initVert( 8, cd.xorg + whole, cd.y[ 3 ], cd.zorg + whole );
        
        // Local macro to make the triangle logic shorter & hopefully clearer.
        // Make the list of triangles to draw.
        if ( ( sq.getEnabledFlags() & 1 ) == 0 )
        {
            r.tri( 0, 8, 2 );
        }
        else
        {
            if ( ( flags & 8 ) != 0 )
            {
                r.tri( 0, 8, 1 );
            }
            
            if ( ( flags & 1 ) != 0 )
            {
                r.tri( 0, 1, 2 );
            }
        }
        
        if ( ( sq.getEnabledFlags() & 2 ) == 0 )
        {
            r.tri( 0, 2, 4 );
        }
        else
        {
            if ( ( flags & 1 ) != 0 )
            {
                r.tri( 0, 2, 3 );
            }
            
            if ( ( flags & 2 ) != 0 )
            {
                r.tri( 0, 3, 4 );
            }
        }
        
        if ( ( sq.getEnabledFlags() & 4 ) == 0 )
        {
            r.tri( 0, 4, 6 );
        }
        else
        {
            if ( ( flags & 2 ) != 0 )
            {
                r.tri( 0, 4, 5 );
            }
            
            if ( ( flags & 4 ) != 0 )
            {
                r.tri( 0, 5, 6 );
            }
        }
        
        if ( ( sq.getEnabledFlags() & 8 ) == 0 )
        {
            r.tri( 0, 6, 8 );
        }
        else
        {
            if ( ( flags & 4 ) != 0 )
            {
                r.tri( 0, 6, 7 );
            }
            
            if ( ( flags & 8 ) != 0 )
            {
                r.tri( 0, 7, 8 );
            }
        }
        
        r.done();
        
        return ( num );
    }
    
    /**
     * Builds the terrain one bank at a time.  Each bank is compressed before moving on
     * to the next bank.
     * @param hm
     * @param minDetail
     */
    public void buildDatabase( TerrainSampleInterface hm, float minDetail )
    {
        this.compressBanks();
        /*
        float lx = hm.getXOrg();
        float lz = hm.getZOrg();
        
        float ux = lx + (hm.getXDim() << hm.getScale());
        float uz = lz + (hm.getZDim() << hm.getScale());
        */
        float bankWidth = ( 2 << bankLevel );
        //int bankDim = (2 << bankLevel) / (1 << hm.getScale());
        MultiPassSampler mps = new MultiPassSampler( hm );
        
        for ( int i = 0; i < maxBanks; i++ )
        {
            for ( int j = 0; j < maxBanks; j++ )
            {
                if ( mps.setBounds( i * bankWidth, j * bankWidth, bankWidth ) )
                {
                    X3DLog.printlnEx( "processing bank ", i, "-", j, " = ", ( i * maxBanks + j ) );
                    
                    X3DLog.debug( "   range is ", (int)mps.getXOrg(), "x", (int)mps.getZOrg(), "  -  ", ( mps.getXOrg() + ( mps.getXDim() << mps.getScale() ) ) + "x" + ( mps.getZOrg() + ( mps.getZDim() << mps.getScale() ) ) );
                    AddHeightMap( root, rootData, mps, 0 );
                    /*
                    Log.print( "   culling" );
                    this.StaticCullData( root, rootData, minDetail, bankLevel - 1 );
                    Log.print( "   compressing" );
                    this.compressBanks();
                    */
                    //banks[ i * maxBanks + j ] = TerrainDataBank.compress( banks[ i * maxBanks + j ] );
                    System.gc();
                    System.gc();
                    X3DLog.printlnEx( "   done" );
                }
            }
        }
        
        // now compress any banks whihc are basically empty
        X3DLog.printlnEx( "final compression" );
        this.cullStaticData( minDetail, bankLevel - 1 );
        compressBanks();
        X3DLog.printlnEx( "done" );
    }
    
    /**
     * @param filename
     * @param hm
     * @param lm
     */
    public void buildDatabase( String filename, TerrainSampleInterface hm, TerrainSampleInterface lm, float minHighDetail, float minLowDetail ) throws IOException
    {
        File f = new File( filename );
        if ( f.exists() )
            f.delete();
        
        Archive a = new Archive( filename, false );
        buildDatabase( hm, minHighDetail );
        
        // now save the heightmap
        
        X3DLog.printlnEx( "Saving high res data" );
        
        try
        {
            for ( int i = 0; i <= maxBanks * maxBanks; i++ )
            {
                a.write( "BANK_HIGH_" + i, banks[ i ], true );
            }
        }
        catch ( UnscribableNodeEncountered unscribableNodeEncountered )
        {
            X3DLog.print( unscribableNodeEncountered );
            throw new Error( "cannot save data" );
        }
        
        X3DLog.printlnEx( "Done Saving data" );
        
        X3DLog.printlnEx( "Nodes for high detail = ", CountNodes() );
        
        for ( int i = 0; i < lm.getScale() + 1; i++ )
            deleteStaticData( root, rootData, i );
        
        this.cullStaticData( minLowDetail, bankLevel - 1 );
        buildDatabase( lm, minLowDetail );
        X3DLog.printlnEx( "Nodes for low detail = " + CountNodes() );
        
        X3DLog.printlnEx( "Saving low res data" );
        try
        {
            for ( int i = 0; i <= maxBanks * maxBanks; i++ )
            {
                a.write( "BANK_LOW_" + i, banks[ i ], true );
            }
        }
        catch ( UnscribableNodeEncountered unscribableNodeEncountered )
        {
            X3DLog.print( unscribableNodeEncountered );
            throw new Error( "cannot save data" );
        }
        
        a.close();
    }
    
    class MultiPassSampler implements TerrainSampleInterface
    {
        float xStart;
        float zStart;
        int xDim;
        int zDim;
        
        TerrainSampleInterface sampler;
        
        MultiPassSampler( TerrainSampleInterface s )
        {
            this.sampler = s;
        }
        
        public boolean setBounds( float x, float z, float width )
        {
            float lx = x;
            float lz = z;
            float ux = x + width;
            float uz = z + width;
            
            int size = sampler.getXDim() << sampler.getScale();
            
            if ( sampler.getXOrg() > x )
                lx = sampler.getXOrg();
            else
                lx = x;
            
            if ( sampler.getZOrg() > z )
                lz = sampler.getZOrg();
            else
                lz = z;
            
            if ( sampler.getXOrg() + size < ux )
                ux = sampler.getXOrg() + size;
            if ( sampler.getZOrg() + size < uz )
                uz = sampler.getZOrg() + size;
            
            if ( ux - lx <= 0 )
                return ( false );
            if ( uz - lz <= 0 )
                return ( false );
            
            xStart = lx;
            zStart = lz;
            xDim = (int)( ( ux - lx ) / ( 1 << sampler.getScale() ) );
            zDim = (int)( ( uz - lz ) / ( 1 << sampler.getScale() ) );
            
            return ( true );
        }
        
        public int getScale()
        {
            return ( sampler.getScale() );
        }
        
        public float sample( int x, int z )
        {
            return ( sampler.sample( x, z ) );
        }
        
        public float getXOrg()
        {
            return ( xStart );
        }
        
        public float getZOrg()
        {
            return ( zStart );
        }
        
        public int getXDim()
        {
            return ( xDim );
        }
        
        public int getZDim()
        {
            return ( zDim );
        }
    }
}
