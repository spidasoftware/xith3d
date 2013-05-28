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
package org.xith3d.terrain.legacy.heightmap;

import java.util.Random;

import org.xith3d.scenegraph.Geometry;

/**
 * Generates a heightmap using the "Miller" algorithm based on random fractals.  
 * It is an implementation of a varient of that alogirthm which is described by
 * Paul Martz here:
 * <a href="http://www.gameprogrammer.com/fractal.html">
 * http://www.gameprogrammer.com/fractal.html</a>.
 * 
 * @author William Denniss
 * @version 1.0 - 22 December 2003
 */
public class MillerFractal extends HeightMap implements Terrain
{
    private static final long serialVersionUID = -7931928839066644505L;
    
    private float roughness;
    private int stride;
    private int size;
    private Random rg;
    
    /**
     * Initialises the miller fractal generator.
     * 
     * @param powerOfTwo number which will be raised to the power of to too calculate the width and height of the terrain
     * @param startingHeight base height
     * @param roughness how random, or "rough" the terrain will be
     * @param rg Random number generator to use
     */
    public MillerFractal( int powerOfTwo, float startingHeight, float roughness, Random rg )
    {
        // size must be a power of two plus 1
        size = (int)Math.pow( 2, powerOfTwo ) + 1;
        
        // sets up hightmap 2d array
        heightmap = new float[ size ][ size ];
        
        // seeds the initial values
        heightmap[ 0 ][ 0 ] = startingHeight;
        heightmap[ 0 ][ size - 1 ] = startingHeight;
        heightmap[ size - 1 ][ 0 ] = startingHeight;
        heightmap[ size - 1 ][ size - 1 ] = startingHeight;
        
        // setup initial stride
        stride = ( size - 1 ) / 2;
        
        // stores values
        this.rg = rg;
        this.roughness = roughness;
        
        // generates the terrain
        generateTerrain();
    }
    
    // possibility - mod the calcualtes so that it randomly increases/decreases the original 4 points as well
    
    /**
     * generates terrain using the Miller algorithm
     */
    public void generateTerrain()
    {
        while ( stride >= 1 )
        {
            for ( int i = stride; i < size; i = i + stride * 2 )
            {
                for ( int j = stride; j < size; j = j + stride * 2 )
                {
                    calculateDiamondValue( i, j );
                    
                    calculateSquareValue( i, j - stride );
                    calculateSquareValue( i, j + stride );
                    calculateSquareValue( i - stride, j );
                    calculateSquareValue( i + stride, j );
                    //System.out.println( "! x: " + heightmap[ i ][ j ] );
                }
            }
            
            stride /= 2;
        }
    }
    
    /**
     * @return the next random number
     */
    private float randomNumber()
    {
        return ( ( rg.nextFloat() - 0.5f ) * roughness );
    }
    
    /**
     * Calculates a single diamond value by using it's four surrounding square points
     * 
     * @param x
     * @param y
     */
    private void calculateDiamondValue( int x, int y )
    {
        heightmap[ x ][ y ] = ( heightmap[ x - stride ][ y - stride ] + heightmap[ x + stride ][ y - stride ] + heightmap[ x - stride ][ y + stride ] + heightmap[ x + stride ][ y + stride ] ) / 4;
        
        heightmap[ x ][ y ] = heightmap[ x ][ y ] + randomNumber();
    }
    
    // Calculates a single square value by using it's four surrounding dimond points
    private void calculateSquareValue( int x, int y )
    {
        int yMinus = y - stride;
        int yPlus = y + stride;
        int xMinus = x - stride;
        int xPlus = x + stride;
        
        if ( yMinus < 0 )
            yMinus = yMinus + ( size - 1 );
        if ( yPlus >= size )
            yPlus = ( yPlus - ( size - 1 ) );
        if ( xMinus < 0 )
            xMinus = xMinus + ( size - 1 );
        if ( xPlus >= size )
            xPlus = ( xPlus - ( size - 1 ) );
        
        heightmap[ x ][ y ] = ( heightmap[ x ][ yPlus ] + heightmap[ x ][ yMinus ] + heightmap[ xMinus ][ y ] + heightmap[ xPlus ][ y ] ) / 4;
        
        heightmap[ x ][ y ] = heightmap[ x ][ y ] + randomNumber();
    }
    
    public Geometry generateGeometry( float startX, float startY, float stepX, float stepY )
    {
        return ( generate3D( startX, startY, stepX, stepY, 0 ) );
    }
    
    /*
    public static final void main( String[] args ) throws IOException
    {	
    	MillerFractal m = new MillerFractal( 6, 1f, 50f, new Random() );
    	System.out.println( m.generateUTF() ); //m.outputAscii();
    	System.out.println( "done1" );
    	ImageIO.write( m.generate2D(), "png", new File( "output.png" ) );
    }
    */
}
