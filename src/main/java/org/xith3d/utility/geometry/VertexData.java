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
package org.xith3d.utility.geometry;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

/**
 * @author YVG
 */
class VertexData implements Comparable< VertexData >
{
    public int smoothGroup = -1;
    public Point3f coord = null;
    public Vector3f normal = null;
    public Colorf color = null;
    public TexCoord2f[] texCoords = null;
    
    public VertexData()
    {
        super();
    }
    
    public VertexData( VertexData vd )
    {
        smoothGroup = vd.smoothGroup;
        
        if ( vd.coord != null )
            coord = new Point3f( vd.coord );
        if ( vd.normal != null )
            normal = new Vector3f( vd.normal );
        if ( vd.color != null )
            color = new Colorf( vd.color );
        if ( vd.texCoords != null )
        {
            texCoords = new TexCoord2f[ vd.texCoords.length ];
            for ( int i = 0; i < texCoords.length; i++ )
            {
                texCoords[ i ] = ( vd.texCoords[ i ] == null ) ? null : new TexCoord2f( vd.texCoords[ i ] );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append( "[" );
        if ( coord != null )
            sb.append( "P" ).append( coord );
        if ( normal != null )
            sb.append( "N" ).append( coord );
        if ( color != null )
            sb.append( "C" ).append( coord );
        for ( int i = 0; texCoords != null && i < texCoords.length; i++ )
        {
            sb.append( "T" ).append( texCoords[ i ] );
        }
        if ( smoothGroup >= 0 )
            sb.append( "S" ).append( smoothGroup );
        sb.append( "]" );
        
        return ( sb.toString() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int h = 0;
        if ( coord != null )
            h ^= coord.hashCode();
        if ( normal != null )
            h ^= normal.hashCode();
        if ( color != null )
            h ^= color.hashCode();
        // texcoords and smoothgroup do not go into hashcode, above should be enough
        
        return ( h );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( VertexData o )
    {
        int d;
        d = compare( coord, o.coord );
        if ( d != 0 )
            return ( d );
        d = compare( normal, o.normal );
        if ( d != 0 )
            return ( d );
        d = compare( color, o.color );
        if ( d != 0 )
            return ( d );
        d = compare( texCoords, o.texCoords );
        if ( d != 0 )
            return ( d );
        d = smoothGroup - o.smoothGroup;
        if ( d != 0 )
            return ( d );
        
        return ( 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        VertexData vd = (VertexData)o;
        return isEqual( coord, vd.coord ) && isEqual( normal, vd.normal ) && isEqual( color, vd.color ) && isEqual( texCoords, vd.texCoords ) && smoothGroup == vd.smoothGroup;
    }
    
    private static int fdiff( float diff )
    {
        if ( diff < 0 )
            return ( -1 );
        else if ( diff > 0 )
            return ( 1 );
        else
            return ( 0 );
    }
    
    private int compare( TexCoord2f t1, TexCoord2f t2 )
    {
        float d;
        if ( t1 == null && t2 == null )
            return ( 0 );
        if ( t1 == null )
            return ( -1 );
        if ( t2 == null )
            return ( 1 );
        d = t1.getS() - t2.getS();
        if ( d != 0 )
            return ( fdiff( d ) );
        d = t1.getT() - t2.getT();
        if ( d != 0 )
            return ( fdiff( d ) );
        
        return ( 0 );
    }
    
    private int compare( TexCoord2f[] t1, TexCoord2f[] t2 )
    {
        int d;
        if ( t1 == null && t2 == null )
            return ( 0 );
        if ( t1 == null )
            return ( -1 );
        if ( t2 == null )
            return ( 1 );
        if ( t1.length != t2.length )
            return ( t1.length - t2.length );
        for ( int i = 0; i < t1.length; i++ )
        {
            d = compare( t1[ i ], t2[ i ] );
            if ( d != 0 )
                return ( d );
        }
        
        return ( 0 );
    }
    
    private int compare( Tuple3f t1, Tuple3f t2 )
    {
        float d;
        if ( t1 == null && t2 == null )
            return ( 0 );
        if ( t1 == null )
            return ( -1 );
        if ( t2 == null )
            return ( 1 );
        d = t1.getX() - t2.getX();
        if ( d != 0 )
            return ( fdiff( d ) );
        d = t1.getY() - t2.getY();
        if ( d != 0 )
            return ( fdiff( d ) );
        d = t1.getZ() - t2.getZ();
        if ( d != 0 )
            return ( fdiff( d ) );
        
        return ( 0 );
    }
    
    private int compare( Colorf c1, Colorf c2 )
    {
        float d;
        if ( c1 == null && c2 == null )
            return ( 0 );
        if ( c1 == null )
            return ( -1 );
        if ( c2 == null )
            return ( 1 );
        d = c1.getRed() - c2.getRed();
        if ( d != 0 )
            return ( fdiff( d ) );
        d = c1.getGreen() - c2.getGreen();
        if ( d != 0 )
            return ( fdiff( d ) );
        d = c1.getBlue() - c2.getBlue();
        if ( d != 0 )
            return ( fdiff( d ) );
        if ( c1.hasAlpha() || c2.hasAlpha() )
        {
            d = c1.getAlpha() - c2.getAlpha();
            if ( d != 0 )
                return ( fdiff( d ) );
        }
        
        return ( 0 );
    }
    
    private static boolean isEqual( Object o1, Object o2 )
    {
        if ( o1 == null )
            return ( o2 == null );
        
        return ( o1.equals( o2 ) );
    }
    
    private static boolean isEqual( Object[] o1, Object[] o2 )
    {
        if ( o1 == null )
            return ( o2 == null );
        if ( o1.length != o2.length )
            return ( false );
        for ( int i = 0; i < o1.length; i++ )
        {
            if ( !isEqual( o1[ i ], o2[ i ] ) )
                return ( false );
        }
        
        return ( true );
    }
}
