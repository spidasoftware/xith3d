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
package org.xith3d.utility.noise;

import java.util.Random;

/**
 * PerlinSolidNoiseGenerator.java  1.0  98/06/16  Carl Burke
 *
 * Encapsulates Perlin's method for solid noise generation.
 *
 * Copyright (c) 1998 Carl Burke.
 *
 * Adapted from copyrighted source code by Ken Perlin
 * and F. Kenton Musgrave to accompany:
 * Texturing and Modeling: A Procedural Approach
 * Ebert, D., Musgrave, K., Peachey, P., Perlin, K., and Worley, S.
 * AP Professional, September, 1994. ISBN 0-12-228760-6
 * Web site: http://www.cs.umbc.edu/~ebert/book/book.html
 * 
 * @author Carl Burke
 */
public class Noise
{
    /************************************************************
     * Noise generation (interpolation) over 1,2, and 3 dimensions
     ************************************************************/
    
    /* noise functions over 1, 2, and 3 dimensions */
    public static final int B = 0x100;
    public static final int BM = 0xff;
    public static final int N = 0x1000;
    public static final int NP = 12; /* 2^N */
    public static final int NM = 0xfff;
    private Random rgen;
    private double rseed;
    private double[] vec;
    private int[] p;
    private double[][] g3;
    private double[][] g2;
    private double[] g1;
    
    public Noise()
    {
        rgen = new Random();
        init_noise();
    }
    
    public Noise( double seed )
    {
        rgen = new Random();
        setSeed( seed );
        init_noise();
    }
    
    public void setSeed( double s )
    {
        rseed = s;
        rgen.setSeed( Double.doubleToLongBits( rseed ) );
        init_noise();
    }
    
    public double getSeed()
    {
        return rseed;
    }
    
    /************************************************************
     * Methods specific to noise synthetic terrain generation
     ************************************************************/
    /************************************************************
     * Supporting/filtering methods
     ************************************************************/
    public double bias( double a, double b )
    {
        return Math.pow( a, Math.log( b ) / Math.log( 0.5 ) );
    }
    
    public double gain( double a, double b )
    {
        double p = Math.log( 1. - b ) / Math.log( 0.5 );
        
        if ( a < 0.001 )
        {
            return ( 0.0 );
        }
        
        if ( a > 0.999 )
        {
            return 1.0;
        }
        
        if ( a < 0.5 )
        {
            return Math.pow( 2 * a, p ) / 2;
        }
        
        return 1.0 - ( Math.pow( 2 * ( 1.0 - a ), p ) / 2 );
    }
    
    public double turbulence( double[] v, double freq )
    {
        double t;
        
        if ( vec == null )
        {
            vec = new double[ 3 ];
        }
        
        for ( t = 0.; freq >= 1.; freq /= 2 )
        {
            vec[ 0 ] = freq * v[ 0 ];
            vec[ 1 ] = freq * v[ 1 ];
            vec[ 2 ] = freq * v[ 2 ];
            t += ( Math.abs( noise3( vec ) ) / freq );
        }
        
        return t;
    }
    
    /************************************************************
     * Initialization
     ************************************************************/
    private void normalize2( double[] v ) // v.length == 2
    {
        double s;
        
        s = Math.sqrt( ( v[ 0 ] * v[ 0 ] ) + ( v[ 1 ] * v[ 1 ] ) );
        v[ 0 ] = v[ 0 ] / s;
        v[ 1 ] = v[ 1 ] / s;
    }
    
    private void normalize3( double[] v ) // v.length == 3
    {
        double s;
        
        s = Math.sqrt( ( v[ 0 ] * v[ 0 ] ) + ( v[ 1 ] * v[ 1 ] ) + ( v[ 2 ] * v[ 2 ] ) );
        v[ 0 ] = v[ 0 ] / s;
        v[ 1 ] = v[ 1 ] / s;
        v[ 2 ] = v[ 2 ] / s;
    }
    
    private void init_noise()
    {
        int i;
        int j;
        int k;
        
        p = new int[ B + B + 2 ];
        g3 = new double[ B + B + 2 ][ 3 ];
        g2 = new double[ B + B + 2 ][ 2 ];
        g1 = new double[ B + B + 2 ];
        
        for ( i = 0; i < B; i++ )
        {
            p[ i ] = i;
            
            g1[ i ] = ( rgen.nextDouble() * 2.0 ) - 1.0; // -1.0 to 1.0
            
            for ( j = 0; j < 2; j++ )
                g2[ i ][ j ] = ( rgen.nextDouble() * 2.0 ) - 1.0; // -1.0 to 1.0
            
            normalize2( g2[ i ] );
            
            for ( j = 0; j < 3; j++ )
                g3[ i ][ j ] = ( rgen.nextDouble() * 2.0 ) - 1.0; // -1.0 to 1.0
            
            normalize3( g3[ i ] );
        }
        
        while ( ( --i ) > 0 )
        {
            j = (int)( rgen.nextDouble() * B );
            k = p[ i ];
            p[ i ] = p[ j ];
            p[ j ] = k;
        }
        
        for ( i = 0; i < ( B + 2 ); i++ )
        {
            p[ B + i ] = p[ i ];
            g1[ B + i ] = g1[ i ];
            
            for ( j = 0; j < 2; j++ )
                g2[ B + i ][ j ] = g2[ i ][ j ];
            
            for ( j = 0; j < 3; j++ )
                g3[ B + i ][ j ] = g3[ i ][ j ];
        }
    }
    
    public double s_curve( double t )
    {
        return t * t * ( 3. - ( 2. * t ) );
    }
    
    public double lerp( double t, double a, double b )
    {
        return a + ( t * ( b - a ) );
    }
    
    /*
       #define setup(i,b0,b1,r0,r1)\
       t = vec[i] + N;\
       b0 = ((int)t) & BM;\
       b1 = (b0+1) & BM;\
       r0 = t - (int)t;\
       r1 = r0 - 1.;\
     */
    public double noise1( double arg )
    {
        int bx0;
        int bx1;
        double rx0;
        double rx1;
        double sx;
        double t;
        double u;
        double v;
        
        /* setup(0, bx0,bx1, rx0,rx1) */
        t = arg + N;
        bx0 = ( (int)t ) & BM;
        bx1 = ( bx0 + 1 ) & BM;
        rx0 = t - (int)t;
        rx1 = rx0 - 1.;
        
        sx = s_curve( rx0 );
        
        u = rx0 * g1[ p[ bx0 ] ];
        v = rx1 * g1[ p[ bx1 ] ];
        
        return lerp( sx, u, v );
    }
    
    public double noise2( double[] vec ) // vec.length == 2
    {
        int bx0;
        int bx1;
        int by0;
        int by1;
        int b00;
        int b10;
        int b01;
        int b11;
        double rx0;
        double rx1;
        double ry0;
        double ry1;
        double[] q;
        double sx;
        double sy;
        double a;
        double b;
        double t;
        double u;
        double v;
        int i;
        int j;
        
        /* setup(0, bx0,bx1, rx0,rx1) */
        t = vec[ 0 ] + N;
        bx0 = ( (int)t ) & BM;
        bx1 = ( bx0 + 1 ) & BM;
        rx0 = t - (int)t;
        rx1 = rx0 - 1.;
        
        /* setup(1, by0,by1, ry0,ry1) */
        t = vec[ 1 ] + N;
        by0 = ( (int)t ) & BM;
        by1 = ( by0 + 1 ) & BM;
        ry0 = t - (int)t;
        ry1 = ry0 - 1.;
        
        i = p[ bx0 ];
        j = p[ bx1 ];
        
        b00 = p[ i + by0 ];
        b10 = p[ j + by0 ];
        b01 = p[ i + by1 ];
        b11 = p[ j + by1 ];
        
        sx = s_curve( rx0 );
        sy = s_curve( ry0 );
        
        q = g2[ b00 ];
        u = ( ( rx0 * q[ 0 ] ) + ( ry0 * q[ 1 ] ) );
        q = g2[ b10 ];
        v = ( ( rx1 * q[ 0 ] ) + ( ry0 * q[ 1 ] ) );
        a = lerp( sx, u, v );
        
        q = g2[ b01 ];
        u = ( ( rx0 * q[ 0 ] ) + ( ry1 * q[ 1 ] ) );
        q = g2[ b11 ];
        v = ( ( rx1 * q[ 0 ] ) + ( ry1 * q[ 1 ] ) );
        b = lerp( sx, u, v );
        
        return lerp( sy, a, b );
    }
    
    public double noise3( double[] vec ) // vec.length == 3
    {
        int bx0, bx1, by0, by1, bz0, bz1, b00, b10, b01, b11;
        double rx0, rx1, ry0, ry1, rz0, rz1;
        double[] q;
        double sy, sz, a, b, c, d, t, u, v;
        int i, j;
        
        /* setup(0, bx0,bx1, rx0,rx1) */
        t = vec[ 0 ] + N;
        bx0 = ( (int)t ) & BM;
        bx1 = ( bx0 + 1 ) & BM;
        rx0 = t - (int)t;
        rx1 = rx0 - 1.;
        
        /* setup(1, by0,by1, ry0,ry1) */
        t = vec[ 1 ] + N;
        by0 = ( (int)t ) & BM;
        by1 = ( by0 + 1 ) & BM;
        ry0 = t - (int)t;
        ry1 = ry0 - 1.;
        
        /* setup(2, bz0,bz1, rz0,rz1) */
        t = vec[ 2 ] + N;
        bz0 = ( (int)t ) & BM;
        bz1 = ( bz0 + 1 ) & BM;
        rz0 = t - (int)t;
        rz1 = rz0 - 1.;
        
        i = p[ bx0 ];
        j = p[ bx1 ];
        
        b00 = p[ i + by0 ];
        b10 = p[ j + by0 ];
        b01 = p[ i + by1 ];
        b11 = p[ j + by1 ];
        
        t = s_curve( rx0 );
        sy = s_curve( ry0 );
        sz = s_curve( rz0 );
        
        q = g3[ b00 + bz0 ];
        u = ( ( rx0 * q[ 0 ] ) + ( ry0 * q[ 1 ] ) + ( rz0 * q[ 2 ] ) );
        q = g3[ b10 + bz0 ];
        v = ( ( rx1 * q[ 0 ] ) + ( ry0 * q[ 1 ] ) + ( rz0 * q[ 2 ] ) );
        a = lerp( t, u, v );
        
        q = g3[ b01 + bz0 ];
        u = ( ( rx0 * q[ 0 ] ) + ( ry1 * q[ 1 ] ) + ( rz0 * q[ 2 ] ) );
        q = g3[ b11 + bz0 ];
        v = ( ( rx1 * q[ 0 ] ) + ( ry1 * q[ 1 ] ) + ( rz0 * q[ 2 ] ) );
        b = lerp( t, u, v );
        
        c = lerp( sy, a, b );
        
        q = g3[ b00 + bz1 ];
        u = ( ( rx0 * q[ 0 ] ) + ( ry0 * q[ 1 ] ) + ( rz1 * q[ 2 ] ) );
        q = g3[ b10 + bz1 ];
        v = ( ( rx1 * q[ 0 ] ) + ( ry0 * q[ 1 ] ) + ( rz1 * q[ 2 ] ) );
        a = lerp( t, u, v );
        
        q = g3[ b01 + bz1 ];
        u = ( ( rx0 * q[ 0 ] ) + ( ry1 * q[ 1 ] ) + ( rz1 * q[ 2 ] ) );
        q = g3[ b11 + bz1 ];
        v = ( ( rx1 * q[ 0 ] ) + ( ry1 * q[ 1 ] ) + ( rz1 * q[ 2 ] ) );
        b = lerp( t, u, v );
        
        d = lerp( sy, a, b );
        
        return lerp( sz, c, d );
    }
    
    public double noise( double[] vec, int len )
    {
        switch ( len )
        {
            case 0:
                return 0.0;
                
            case 1:
                return noise1( vec[ 0 ] );
                
            case 2:
                return noise2( vec );
                
            default:
                return noise3( vec );
        }
    }
}
