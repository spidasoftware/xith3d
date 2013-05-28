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
public class Perlin3
{
    // *** METHODS OF TERRAIN GENERATION CURRENTLY SUPPORTED
    public static final int METHOD_BASIC = 1;
    public static final int METHOD_MULTIFRACTAL = 2;
    public static final int METHOD_HETERO_TERRAIN = 3;
    public static final int METHOD_HYBRID_MULTIFRACTAL = 4;
    public static final int METHOD_RIDGED_MULTIFRACTAL = 5;
    
    ///** COLOR INDEX CONSTANTS ***
    public static final int BLACK = 0;
    public static final int BLUE0 = 1;
    public static final int BLUE1 = 9;
    public static final int LAND0 = 10;
    public static final int LAND1 = 18;
    public static final int WHITE = 19;
    
    static int[] rtable =
    {
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 32, 48, 64, 80, 96, 112, 128, 255
    };
    
    static int[] gtable =
    {
        0, 0, 16, 32, 48, 64, 80, 96, 112, 128, 255, 240, 224, 208, 192, 176, 160, 144, 128, 255
    };
    
    static int[] btable =
    {
        0, 255, 255, 255, 255, 255, 255, 255, 255, 255, 0, 4, 8, 12, 16, 20, 24, 28, 32, 255
    };
    
    // *** PRIVATE DATA TO DRIVE TERRAIN CALCULATIONS
    private int method;
    private double H;
    private double lacunarity;
    private double octaves;
    private double offset;
    private double gain;
    private double[] point;
    private Noise noise;
    private double[] vec;
    
    /************************************************************
     * Methods that use the noise functions to generate height fields
     * Adapted from code written by F. Kenton Musgrave
     ************************************************************/
    
    /**
     * Procedural fBm evaluated at "point"; returns value stored in "value".
     *
     * Copyright 1994 F. Kenton Musgrave
     *
     * Parameters:
     *    ``H''  is the fractal increment parameter
     *    ``lacunarity''  is the gap between successive frequencies
     *    ``octaves''  is the number of frequencies in the fBm
     *
     * 'point' must be a double[3]
     */
    private boolean first_fBm = true;
    private double[] exponent_array;
    public boolean latic; // flag for latitude based colour
    public double land; // percentage of surface covered by land
    public double water; // percentage of surface covered by water
    
    public Perlin3()
    {
        point = new double[ 3 ];
        method = METHOD_BASIC;
        H = 0.5;
        lacunarity = 2.0;
        octaves = 7.0;
    }
    
    public Perlin3( double hIn, double lacIn, double octIn )
    {
        point = new double[ 3 ];
        method = METHOD_BASIC;
        H = hIn;
        lacunarity = lacIn;
        octaves = octIn;
        noise = new Noise();
    }
    
    public Perlin3( int methIn, double hIn, double lacIn, double octIn, double offIn, double gainIn, Noise n )
    {
        point = new double[ 3 ];
        noise = n;
        
        switch ( methIn )
        {
            case METHOD_MULTIFRACTAL:
                method = METHOD_MULTIFRACTAL;
                H = hIn;
                lacunarity = lacIn;
                octaves = octIn;
                offset = offIn;
                
                break;
            
            case METHOD_HETERO_TERRAIN:
                method = METHOD_HETERO_TERRAIN;
                H = hIn;
                lacunarity = lacIn;
                octaves = octIn;
                offset = offIn;
                
                break;
            
            case METHOD_HYBRID_MULTIFRACTAL:
                method = METHOD_HYBRID_MULTIFRACTAL;
                H = hIn;
                lacunarity = lacIn;
                octaves = octIn;
                offset = offIn;
                
                break;
            
            case METHOD_RIDGED_MULTIFRACTAL:
                point = new double[ 3 ];
                method = METHOD_RIDGED_MULTIFRACTAL;
                H = hIn;
                lacunarity = lacIn;
                octaves = octIn;
                offset = offIn;
                gain = gainIn;
                
                break;
            
            default: // don't know which method, so do basic
                method = METHOD_BASIC;
                H = hIn;
                lacunarity = lacIn;
                octaves = octIn;
        }
    }
    
    public double gain( double a, double b )
    {
        double p = Math.log( 1.0 - b ) / Math.log( 0.5 );
        
        if ( a < 0.001 )
            return ( 0.0 );
        
        if ( a > 0.999 )
            return ( 1.0 );
        
        if ( a < 0.5 )
            return ( Math.pow( 2 * a, p ) / 2.0 );
        
        return ( 1.0 - ( Math.pow( 2.0 * ( 1.0 - a ), p ) / 2.0 ) );
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
            t += ( Math.abs( noise.noise3( vec ) ) / freq );
        }
        
        return t;
    }
    
    public double fBm( double[] point, double H, double lacunarity, double octaves )
    {
        double value;
        double frequency;
        double remainder;
        int i;
        
        /* precompute and store spectral weights */
        if ( first_fBm )
        {
            /* seize required memory for exponent_array */
            exponent_array = new double[ (int)octaves + 1 ];
            frequency = 1.0;
            
            for ( i = 0; i <= octaves; i++ )
            {
                /* compute weight for each frequency */
                exponent_array[ i ] = Math.pow( frequency, -H );
                frequency *= lacunarity;
            }
            
            first_fBm = false;
        }
        
        value = 0.0; /* initialize vars to proper values */
        frequency = 1.0;
        
        /* inner loop of spectral construction */
        for ( i = 0; i < octaves; i++ )
        {
            value += ( noise.noise3( point ) * exponent_array[ i ] );
            point[ 0 ] *= lacunarity;
            point[ 1 ] *= lacunarity;
            point[ 2 ] *= lacunarity;
        }
        
        remainder = octaves - (int)octaves;
        
        if ( remainder != 0.0 )
        {
            /* add in ``octaves''  remainder */
            /* ``i''  and spatial freq. are preset in loop above */
            value += ( remainder * noise.noise3( point ) * exponent_array[ i ] );
        }
        
        return ( ( value + 1 ) / 2 );
    }
    
    /**
     * Procedural multifractal evaluated at "point";
     * @return value stored in "value".
     *
     * Copyright 1994 F. Kenton Musgrave
     *
     * Parameters:
     *    ``H''  determines the highest fractal dimension
     *    ``lacunarity''  is gap between successive frequencies
     *    ``octaves''  is the number of frequencies in the fBm
     *    ``offset''  is the zero offset, which determines multifractality
     *
     * Note: this tends to yield very small values, so the results need
     * to be scaled appropriately.
     */
    public double multifractal( double[] point, double H, double lacunarity, double octaves, double offset )
    {
        double value;
        double frequency;
        double remainder;
        int i;
        
        /* precompute and store spectral weights */
        if ( first_fBm )
        {
            /* seize required memory for exponent_array */
            exponent_array = new double[ (int)octaves + 1 ];
            frequency = 1.0;
            
            for ( i = 0; i <= octaves; i++ )
            {
                /* compute weight for each frequency */
                exponent_array[ i ] = Math.pow( frequency, -H );
                frequency *= lacunarity;
            }
            
            first_fBm = false;
        }
        
        value = 1.0; /* initialize vars to proper values */
        frequency = 1.0;
        
        /* inner loop of multifractal construction */
        for ( i = 0; i < octaves; i++ )
        {
            value *= ( offset * frequency * noise.noise3( point ) );
            point[ 0 ] *= lacunarity;
            point[ 1 ] *= lacunarity;
            point[ 2 ] *= lacunarity;
        }
        
        remainder = octaves - (int)octaves;
        
        if ( remainder != 0.0 )
        {
            /* add in ``octaves''  remainder */
            /* ``i''  and spatial freq. are preset in loop above */
            value += ( remainder * noise.noise3( point ) * exponent_array[ i ] );
        }
        
        return ( ( value + 1 ) / 2 );
    }
    
    /**
     * Heterogeneous procedural terrain function: stats by altitude method.
     * Evaluated at "point"; returns value stored in "value".
     *
     * Copyright 1994 F. Kenton Musgrave
     *
     * Parameters:
     *       ``H''  determines the fractal dimension of the roughest areas
     *       ``lacunarity''  is the gap between successive frequencies
     *       ``octaves''  is the number of frequencies in the fBm
     *       ``offset''  raises the terrain from `sea level'
     */
    public double Hetero_Terrain( double[] point, double H, double lacunarity, double octaves, double offset )
    {
        double value;
        double increment;
        double frequency;
        double remainder;
        int i;
        
        /* precompute and store spectral weights */
        if ( first_fBm )
        {
            /* seize required memory for exponent_array */
            exponent_array = new double[ (int)octaves + 1 ];
            frequency = 1.0;
            
            for ( i = 0; i <= octaves; i++ )
            {
                /* compute weight for each frequency */
                exponent_array[ i ] = Math.pow( frequency, -H );
                frequency *= lacunarity;
            }
            
            first_fBm = false;
        }
        
        /* first unscaled octave of function; later octaves are scaled */
        value = offset + noise.noise3( point );
        point[ 0 ] *= lacunarity;
        point[ 1 ] *= lacunarity;
        point[ 2 ] *= lacunarity;
        
        /* spectral construction inner loop, where the fractal is built */
        for ( i = 1; i < octaves; i++ )
        {
            /* obtain displaced noise value */
            increment = noise.noise3( point ) + offset;
            
            /* scale amplitude appropriately for this frequency */
            increment *= exponent_array[ i ];
            
            /* scale increment by current `altitude' of function */
            increment *= value;
            
            /* add increment to ``value''  */
            value += increment;
            
            /* raise spatial frequency */
            point[ 0 ] *= lacunarity;
            point[ 1 ] *= lacunarity;
            point[ 2 ] *= lacunarity;
        }
        
        /* for */
        /* take care of remainder in ``octaves''  */
        remainder = octaves - (int)octaves;
        
        if ( remainder != 0.0 )
        {
            /* ``i''  and spatial freq. are preset in loop above */
            /* note that the main loop code is made shorter here */
            /* you may want to that loop more like this */
            increment = ( noise.noise3( point ) + offset ) * exponent_array[ i ];
            value += ( remainder * increment * value );
        }
        
        return ( ( value + 1 ) / 2 );
    }
    
    /**
     * Hybrid additive/multiplicative multifractal terrain model.
     *
     * Copyright 1994 F. Kenton Musgrave
     *
     * Some good parameter values to start with:
     *
     *      H:           0.25
     *      offset:      0.7
     */
    public double HybridMultifractal( double[] point, double H, double lacunarity, double octaves, double offset )
    {
        double frequency;
        double result;
        double signal;
        double weight;
        double remainder;
        int i;
        
        /* precompute and store spectral weights */
        if ( first_fBm )
        {
            /* seize required memory for exponent_array */
            exponent_array = new double[ (int)octaves + 1 ];
            frequency = 1.0;
            
            for ( i = 0; i <= octaves; i++ )
            {
                /* compute weight for each frequency */
                exponent_array[ i ] = Math.pow( frequency, -H );
                frequency *= lacunarity;
            }
            
            first_fBm = false;
        }
        
        /* get first octave of function */
        result = ( noise.noise3( point ) + offset ) * exponent_array[ 0 ];
        weight = result;
        
        /* increase frequency */
        point[ 0 ] *= lacunarity;
        point[ 1 ] *= lacunarity;
        point[ 2 ] *= lacunarity;
        
        /* spectral construction inner loop, where the fractal is built */
        for ( i = 1; i < octaves; i++ )
        {
            /* prevent divergence */
            if ( weight > 1.0 )
            {
                weight = 1.0;
            }
            
            /* get next higher frequency */
            signal = ( noise.noise3( point ) + offset ) * exponent_array[ i ];
            
            /* add it in, weighted by previous freq's local value */
            result += ( weight * signal );
            
            /* update the (monotonically decreasing) weighting value */
            /* (this is why H must specify a high fractal dimension) */
            weight *= signal;
            
            /* increase frequency */
            point[ 0 ] *= lacunarity;
            point[ 1 ] *= lacunarity;
            point[ 2 ] *= lacunarity;
        }
        
        /* for */
        /* take care of remainder in ``octaves''  */
        remainder = octaves - (int)octaves;
        
        if ( remainder != 0.0 )
        {
            /* ``i''  and spatial freq. are preset in loop above */
            result += ( remainder * noise.noise3( point ) * exponent_array[ i ] );
        }
        
        return ( result + 1 ) / 2;
    }
    
    /**
     *  Ridged multifractal terrain model.
     *
     * Copyright 1994 F. Kenton Musgrave
     *
     * Some good parameter values to start with:
     *
     *      H:           1.0
     *      offset:      1.0
     *      gain:        2.0
     */
    public double RidgedMultifractal( double[] point, double H, double lacunarity, double octaves, double offset, double gain )
    {
        double result;
        double frequency;
        double signal;
        double weight;
        int i;
        
        // precompute and store spectral weights
        if ( first_fBm )
        {
            // seize required memory for exponent_array
            exponent_array = new double[ (int)octaves + 1 ];
            frequency = 1.0;
            
            for ( i = 0; i <= octaves; i++ )
            {
                /* compute weight for each frequency */
                exponent_array[ i ] = Math.pow( frequency, -H );
                frequency *= lacunarity;
            }
            
            first_fBm = false;
        }
        
        /* get first octave */
        signal = noise.noise3( point );
        
        /* get absolute value of signal (this creates the ridges) */
        if ( signal < 0.0 )
        {
            signal = -signal;
        }
        
        /* invert and translate (note that "offset" should be ~= 1.0) */
        signal = offset - signal;
        
        /* square the signal, to increase "sharpness" of ridges */
        signal *= signal;
        
        /* assign initial values */
        result = signal;
        weight = 1.0;
        
        for ( i = 1; i < octaves; i++ )
        {
            /* increase the frequency */
            point[ 0 ] *= lacunarity;
            point[ 1 ] *= lacunarity;
            point[ 2 ] *= lacunarity;
            
            /* weight successive contributions by previous signal */
            weight = signal * gain;
            
            if ( weight > 1.0 )
            {
                weight = 1.0;
            }
            
            if ( weight < 0.0 )
            {
                weight = 0.0;
            }
            
            signal = noise.noise3( point );
            
            if ( signal < 0.0 )
            {
                signal = -signal;
            }
            
            signal = offset - signal;
            signal *= signal;
            
            /* weight the contribution */
            signal *= weight;
            result += ( signal * exponent_array[ i ] );
        }
        
        return ( result / 2 );
    }
    
    /**
     * 
     * @param M
     * @param W
     * @param H
     */
    public void setScaling( double M, double W, double H )
    {
    }
    
    /**
     * Calculates an intensity value in [0.0,1.0] at the specified point.
     */
    public double value( double x, double y, double z )
    {
        point[ 0 ] = x;
        point[ 1 ] = y;
        point[ 2 ] = z;
        
        switch ( method )
        {
            case METHOD_BASIC:
                return fBm( point, H, lacunarity, octaves );
                
            case METHOD_MULTIFRACTAL:
                return multifractal( point, H, lacunarity, octaves, offset );
                
            case METHOD_HETERO_TERRAIN:
                return Hetero_Terrain( point, H, lacunarity, octaves, offset );
                
            case METHOD_HYBRID_MULTIFRACTAL:
                return HybridMultifractal( point, H, lacunarity, octaves, offset );
                
            case METHOD_RIDGED_MULTIFRACTAL:
                return RidgedMultifractal( point, H, lacunarity, octaves, offset, gain );
        }
        
        return 0.0;
    }
    
    /**
     * @return an (alpha, red, green, blue) color value associated with
     * the value() at the specified point.
     */
    public int color( double x, double y, double z )
    {
        double alt = value( x, y, z );
        int colour;
        
        // calculate colour
        if ( alt <= 0. ) // if below sea level then
        {
            water++;
            
            if ( latic && ( ( ( y * y ) + alt ) >= 0.90 ) )
            { // white if close to poles
                colour = WHITE;
            }
            else
            { // blue scale otherwise
                colour = BLUE1 + (int)( ( BLUE1 - BLUE0 + 1 ) * ( 2 * alt ) );
                
                if ( colour < BLUE0 )
                {
                    colour = BLUE0;
                }
            }
        }
        else
        {
            land++;
            
            if ( latic )
            {
                alt += ( 0.10204 * y * y ); // altitude adjusted with latitude
            }
            
            if ( alt >= 0.5 ) // arbitrary, but not too bad
            { // if high then white
                colour = WHITE;
            }
            else
            { // else green to brown scale
                colour = LAND0 + (int)( ( LAND1 - LAND0 + 1 ) * ( 2 * alt ) );
            }
        }
        
        if ( colour < 0 )
        {
            colour = 0;
        }
        
        if ( colour > 19 )
        {
            colour = 19;
        }
        
        return ( ( 255 << 24 ) | ( rtable[ colour ] << 16 ) | ( gtable[ colour ] << 8 ) | btable[ colour ] );
    }
    
    /**
     * @return an (alpha, red, green, blue) color value associated with
     * the background value in lieu of valid noise.
     */
    public int background()
    {
        return 0xFF000000;
    }
}
