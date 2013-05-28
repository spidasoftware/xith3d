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
 * Generates a heightmap using fractal faulting.  The Algorithm was adapted from Aurel Balmosan's
 * 'faultmap' program posted to Usenet by Carl Burke.  Carl has an example and explanation here:
 * <a href="http://www.geocities.com/Area51/6902/t_fl_app.html">
 * http://www.geocities.com/Area51/6902/t_fl_app.html</a>.  Converson to the HeightMap class
 * by William Denniss.
 *  
 * @author William Denniss
 * @version 1.0 - 17 March 2004
 */
public class Faulting extends HeightMap implements Terrain
{
    private static final long serialVersionUID = -4450585657315846596L;
    
    private int width = 256;
    private int height = 256;
    private Random rgen;
    
    /**
     * Initialises the miller fractal generator.
     * 
     * @param width
     * @param height
     * @param scalefactor
     * @param rgen Random number generator to use
     */
    public Faulting( int width, int height, double scalefactor, Random rgen )
    {
        this.width = width;
        this.height = height;
        //this.scalefactor = scalefactor;
        this.rgen = rgen;
        
        // generates the terrain
        generateTerrain();
    }
    
    /************************************************************
     * Faulting implementations
     ************************************************************/
    
    private int rand0( int range )
    {
        double d = rgen.nextDouble();
        int i = (int)( d * range );
        if ( i < 0 )
            i = -i;
        
        return ( i );
    }
    
    synchronized void doFaulting( int iterations )
    {
        double pfi, cp, sp;
        int i, h, j, x, y;
        int px, py, wmod = width, hmod = width;
        
        /* Get the memory (widthxwidth bytes) */
        heightmap = new float[ width ][ width ];
        /* base value = 0 */
        for ( i = 0; i < width; i++ )
            for ( j = 0; j < width; j++ )
                heightmap[ i ][ j ] = 0;
        
        while ( iterations-- > 0 )
        {
            px = rand0( wmod );
            py = rand0( hmod );
            pfi = rgen.nextDouble() * 2.0 * Math.PI;
            cp = Math.cos( pfi );
            sp = Math.sin( pfi );
            h = ( ( pfi > Math.PI / 2 ) && ( pfi <= Math.PI + Math.PI / 2 ) ) ? -1 : 1;
            // I'll ignore any horizontal or vertical cuts -- CDB
            if ( ( sp > -0.01 ) && ( sp < 0.01 ) ) // line essentially horizontal
            {
                iterations++;
            }
            else if ( ( cp > -0.01 ) && ( cp < 0.01 ) ) // line essentially vertical
            {
                iterations++;
            }
            else
            // walk from selected point, setting faultline
            {
                double dx;
                double mx = cp / sp; // dx for unit y
                for ( y = py + 1, dx = px + mx, x = (int)dx; ( ( y < height ) && ( x >= 0 ) && ( x < width ) ); y++, dx += mx, x = (int)( dx + 0.5 ) )
                {
                    heightmap[ x ][ y ] += h;
                }
                if ( ( x < 0 ) && ( y < height ) )
                    for ( ; y < height; y++ )
                    {
                        heightmap[ 0 ][ y ] += h;
                    }
                for ( y = py, dx = px, x = px; ( ( y >= 0 ) && ( x >= 0 ) && ( x < width ) ); y--, dx -= mx, x = (int)( dx + 0.5 ) )
                {
                    heightmap[ x ][ y ] += h;
                }
                if ( ( x < 0 ) && ( y >= 0 ) )
                    for ( ; y >= 0; y-- )
                    {
                        heightmap[ 0 ][ y ] += h;
                    }
            }
        }
        
        for ( y = 0; y < height; y++ )
        {
            h = 0;
            for ( x = 0; x < width; x++ )
            {
                h += heightmap[ x ][ y ];
                heightmap[ x ][ y ] = h;
            }
        }
        //BuildImage();
    }
    
    public void generateTerrain()
    {
        doFaulting( 10000 );
    }
    
    public Geometry generateGeometry( float startX, float startY, float stepX, float stepY )
    {
        return ( generate3D( startX, startY, stepX, stepY, 0 ) );
    }
    
    /*
    public static final void main( String[] args ) throws IOException
    {	
    	Faulting m = new Faulting( 256, 256, 1.0, new Random() );
    	//System.out.println( m.generateUTF() ); //m.outputAscii();
    	System.out.println( "done1" );
    	ImageIO.write( m.generate2D(), "png", new File( "output.png" ) );
    }
    */
}
