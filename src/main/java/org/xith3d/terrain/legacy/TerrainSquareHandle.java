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

import org.xith3d.utility.logging.X3DLog;

/**
 * Just stores the information to get the a square stored in a terrain data
 * bank.
 * 
 * @author David Yazel
 */
public class TerrainSquareHandle
{
    int bank;
    int node;
    boolean deleted = false;
    TerrainDataBank b;
    
    public void delete()
    {
        checkBank();
        b.delete( node );
        deleted = true;
    }
    
    private void checkBank()
    {
        while ( b.newBank != null )
            b = b.newBank;
        
        if ( deleted )
            throw new Error( "attempt to access a deleted node" );
        if ( b.handles[ node ] < 0 )
        {
            X3DLog.error( "terrain square handle for bank ", bank, " node ", node, " is invalid" );
            throw new Error( "attempt to access a deleted node (2)" );
        }
    }
    
    public float getY( int index )
    {
        checkBank();
        return ( b.getY( node, index ) );
    }
    
    public void setY( int index, float val )
    {
        checkBank();
        b.setY( node, index, val );
    }
    
    public short getError( int index )
    {
        checkBank();
        return ( b.getError( node, index ) );
    }
    
    public void setError( int index, short val )
    {
        checkBank();
        b.setError( node, index, val );
    }
    
    public int getChild( int index )
    {
        checkBank();
        return ( b.getChild( node, index ) );
    }
    
    public int getChildBank( int index )
    {
        checkBank();
        return ( b.getChildBank( node, index ) );
    }
    
    public void setChild( int index, int val )
    {
        checkBank();
        b.setChild( node, index, val );
    }
    
    public void setChildBank( int index, int val )
    {
        checkBank();
        b.setChildBank( node, index, val );
    }
    
    public void incSubEnabledCount( int index )
    {
        checkBank();
        setSubEnabledCount( index, (byte)( getSubEnabledCount( index ) + 1 ) );
    }
    
    public byte getSubEnabledCount( int index )
    {
        checkBank();
        return ( b.getSubEnabledCount( node, index ) );
    }
    
    public void setSubEnabledCount( int index, byte val )
    {
        checkBank();
        b.setSubEnabledCount( node, index, val );
    }
    
    public short getMinY()
    {
        checkBank();
        return ( b.getMinY( node ) );
    }
    
    public void setMinY( short val )
    {
        checkBank();
        b.setMinY( node, val );
    }
    
    public void andEnabledFlags( byte val )
    {
        checkBank();
        setEnabledFlags( (byte)( getEnabledFlags() & val ) );
    }
    
    public byte getEnabledFlags()
    {
        checkBank();
        return ( b.getEnabledFlags( node ) );
    }
    
    public void setEnabledFlags( byte val )
    {
        checkBank();
        b.setEnabledFlags( node, val );
    }
    
    public short getMaxY()
    {
        checkBank();
        return ( b.getMaxY( node ) );
    }
    
    public void setMaxY( short val )
    {
        checkBank();
        b.setMaxY( node, val );
    }
    
    public boolean getStatic()
    {
        checkBank();
        return ( b.getStatic( node ) );
    }
    
    public void setStatic( boolean val )
    {
        checkBank();
        b.setStatic( node, val );
    }
    
    public boolean getDirty()
    {
        checkBank();
        return ( b.getDirty( node ) );
    }
    
    public void setDirty( boolean val )
    {
        checkBank();
        b.setDirty( node, val );
    }
}
