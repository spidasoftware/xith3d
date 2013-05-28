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
package org.xith3d.utility.comparator;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector4f;

import org.xith3d.scenegraph.Transform3D;

/**
 * @author David Yazel
 */
public class ComparatorHelper
{
    public ComparatorHelper()
    {
    }
    
    public static int compare( Colorf ca, Colorf cb )
    {
        if ( ca == cb )
        {
            return ( 0 );
        }
        if ( ca == null )
        {
            return ( -1 );
        }
        if ( cb == null )
        {
            return ( 1 );
        }
        
        if ( ca.getRed() < cb.getRed() )
        {
            return ( -1 );
        }
        else if ( ca.getRed() > cb.getRed() )
        {
            return ( 1 );
        }
        
        else if ( ca.getGreen() < cb.getGreen() )
        {
            return ( -1 );
        } else if ( ca.getGreen() > cb.getGreen() )
        {
            return ( 1 );
        }
        
        if ( ca.getBlue() < cb.getBlue() )
        {
            return ( -1 );
        }
        else if ( ca.getBlue() > cb.getBlue() )
        {
            return ( 1 );
        }
        
        if ( ca.hasAlpha() || cb.hasAlpha() )
        {
            if ( ca.getAlpha() < cb.getAlpha() )
            {
                return ( -1 );
            }
            else if ( ca.getAlpha() > cb.getAlpha() )
            {
                return ( 1 );
            }
        }
        
        return ( 0 );
    }
    
    public static int compareTuple( Tuple3f ta, Tuple3f tb )
    {
        if ( ta == tb )
        {
            return ( 0 );
        }
        if ( ta == null )
        {
            return ( -1 );
        }
        if ( tb == null )
        {
            return ( 1 );
        }
        
        if ( ta.getX() < tb.getX() )
        {
            return ( -1 );
        }
        else if ( ta.getX() > tb.getX() )
        {
            return ( 1 );
        }
        
        else if ( ta.getY() < tb.getY() )
        {
            return ( -1 );
        } else if ( ta.getY() > tb.getY() )
        {
            return ( 1 );
        }
        
        if ( ta.getZ() < tb.getZ() )
        {
            return ( -1 );
        }
        else if ( ta.getZ() > tb.getZ() )
        {
            return ( 1 );
        }
        
        return ( 0 );
    }
    
    public static int compareTuple( Tuple2f ta, Tuple2f tb )
    {
        if ( ta == tb )
        {
            return ( 0 );
        }
        if ( ta == null )
        {
            return ( -1 );
        }
        if ( tb == null )
        {
            return ( 1 );
        }
        
        if ( ta.getX() < tb.getX() )
        {
            return ( -1 );
        }
        else if ( ta.getX() > tb.getX() )
        {
            return ( 1 );
        }
        
        else if ( ta.getY() < tb.getY() )
        {
            return ( -1 );
        }
        else if ( ta.getY() > tb.getY() )
        {
            return ( 1 );
        }
        
        return ( 0 );
    }
    
    public static int compareTuple( Vector4f ta, Vector4f tb )
    {
        if (ta == tb)
        {
            return ( 0 );
        }
        if ( ta == null )
        {
            return ( -1 );
        }
        if ( tb == null )
        {
            return ( 1 );
        }
        
        if ( ta.getX() < tb.getX() )
        {
            return ( -1 );
        }
        else if ( ta.getX() > tb.getX() )
        {
            return ( 1 );
        }
        
        else if ( ta.getY() < tb.getY() )
        {
            return ( -1 );
        }
        else if ( ta.getY() > tb.getY() )
        {
            return ( 1 );
        }
        
        if ( ta.getZ() < tb.getZ() )
        {
            return ( -1 );
        }
        else if ( ta.getZ() > tb.getZ() )
        {
            return ( 1 );
        }
        
        if ( ta.getW() < tb.getW() )
        {
            return ( -1 );
        }
        else if ( ta.getW() > tb.getW() )
        {
            return ( 1 );
        }
        
        return ( 0 );
    }
    
    public static int fdiff( float diff )
    {
        if ( diff < 0 )
            return ( -1 );
        else if ( diff > 0 )
            return ( 1 );
        
        return ( 0 );
    }
    
    public static int compare( Matrix4f ma, Matrix4f mb )
    {
        if ( ma == mb)    return ( 0 );
        if ( ma == null ) return ( -1 );
        if ( mb == null ) return ( 1 );
        
        float r = 0;
        
        r = ma.m00() - mb.m00(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m01() - mb.m01(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m02() - mb.m02(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m03() - mb.m03(); if ( r != 0 ) return ( fdiff( r ) );
        
        r = ma.m10() - mb.m10(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m11() - mb.m11(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m12() - mb.m12(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m13() - mb.m13(); if ( r != 0 ) return ( fdiff( r ) );
        
        r = ma.m20() - mb.m20(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m21() - mb.m21(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m22() - mb.m22(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m23() - mb.m23(); if ( r != 0 ) return ( fdiff( r ) );
        
        r = ma.m30() - mb.m30(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m31() - mb.m31(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m32() - mb.m32(); if ( r != 0 ) return ( fdiff( r ) );
        r = ma.m33() - mb.m33(); if ( r != 0 ) return ( fdiff( r ) );
        
        return ( 0 );
    }
    
    public static int compare( Transform3D ta, Transform3D tb )
    {
        if ( ta == tb )   return ( 0 );
        if ( ta == null ) return ( -1 );
        if ( tb == null ) return ( 1 );
        
        return ( compare( ta.getMatrix4f(), tb.getMatrix4f() ) );
    }
    
    public static int compare( String ta, String tb )
    {
        if ( ta == tb )   return ( 0 );
        if ( ta == null ) return ( -1 );
        if ( tb == null ) return ( 1 );
        
        return ( ta.compareTo( tb ) );
    }
    
    public static int compareBoolean( boolean b1, boolean b2 )
    {
        if ( b1 == b2 ) return ( 0 );
        if ( !b1 ) return ( -1 );
        return ( 1 );
    }
    
    public static int compare( int[] a1, int[] a2 )
    {
        if ( a1 == a2 )   return ( 0 );
        if ( a1 == null ) return ( -1 );
        if ( a2 == null ) return ( 1 );
        if ( a1.length < a2.length ) return ( -1 );
        if ( a1.length > a2.length ) return ( 1 );
        for ( int i = 0; i < a1.length; i++ )
        {
           int diff = a2[ i ] - a1[ i ];
           if ( diff < 0 )
              return ( -1 );
           else if ( diff > 0 )
              return ( 1 );
        }
        
        return ( 0 );
    }
    
    public static int compare( Vector4f[] a1, Vector4f[] a2 )
    {
        if ( a1 == a2 )   return ( 0 );
        if ( a1 == null ) return ( -1 );
        if ( a2 == null ) return ( 1 );
        if ( a1.length < a2.length ) return ( -1 );
        if ( a1.length > a2.length ) return ( 1 );
        for ( int i = 0; i < a1.length; i++ )
        {
           int diff = compareTuple( a1[ i ], a2[ i ] );
           if ( diff != 0 )
              return ( diff );
        }
        
        return ( 0 );
    }
}
