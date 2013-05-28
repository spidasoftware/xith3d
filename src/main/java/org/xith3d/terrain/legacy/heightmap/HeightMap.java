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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

import org.openmali.vecmath2.Point3f;

import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedTriangleArray;

/**
 * Storage and conversion for height maps (two dimentional arrays of height data).
 * The height maps can be created in many ways such as a random generator, or reading
 * an image.  This class allows the 2D height map to be exported to ASCII (for debuggin),
 * 2D images (for terrain detailing and cloud textures) and 3D geometry (for intereactive
 * terrain).
 * 
 * @author William Denniss
 * @version 1.0 - 22 December 2003
 */
public class HeightMap implements Serializable
{
    private static final long serialVersionUID = -3199715473452121495L;
    
    /**
     * Alternate rows when exporting 3D triangular geometry (default)
     */
    public final static int ZIGZAG_ROWS = 1;
    
    /**
     * Alternate collumns when exporting 3D triangular geometry.
     */
    public final static int ZIGZAG_COLS = 2;
    
    /**
     * Changes starting
     *
     */
    public final static int FLIP_STARTING = 4;
    
    // The heightmap data
    protected float[][] heightmap;
    
    /**
     * Empty Constructor
     */
    protected HeightMap()
    {
    }
    
    /**
     * Initialises using the given two dimentional array as the hight map.
     * 
     * @param heightmap the heightmap to be used
     */
    public HeightMap( float[][] heightmap )
    {
        this.heightmap = heightmap;
    }
    
    /**
     * Initialises using the given one dimentional array, unfolded using
     * the given node count per side.
     * 
     * @param heights the table of heights to be used
     * @param nodesPerSide width of table in number of cells.
     */
    public HeightMap( float[] heights, int nodesPerSide )
    {
        heightmap = new float[ nodesPerSide ][ heights.length / nodesPerSide ];
        
        for ( int i = 0; i < heights.length; i++ )
        {
            heightmap[ i % nodesPerSide ][ i / nodesPerSide ] = heights[ i ];
        }
    }
    
    /**
     * @return string representation of this heightmap.
     */
    public String generateUTF()
    {
        StringWriter sw = new StringWriter();
        
        try
        {
            BufferedWriter bw = new BufferedWriter( sw );
            
            for ( int i = 0; i < heightmap.length; i++ )
            {
                for ( int j = 0; j < heightmap[ i ].length; j++ )
                {
                    bw.write( (int)Math.abs( heightmap[ i ][ j ] ) );
                }
                bw.newLine();
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
        
        return ( sw.toString() );
    }
    
    /**
     * @return graphical grayscale image representation of this heightmap
     */
    public BufferedImage generate2D()
    {
        BufferedImage terrainmap = new BufferedImage( heightmap.length, heightmap[ 0 ].length, BufferedImage.TYPE_INT_RGB );
        
        Graphics2D g = terrainmap.createGraphics();
        
        for ( int i = 0; i < heightmap.length; i++ )
        {
            for ( int j = 0; j < heightmap[ i ].length; j++ )
            {
                int rgb = (int)heightmap[ i ][ j ] + 128;
                
                if ( rgb < 0 )
                    rgb = 0;
                if ( rgb > 255 )
                    rgb = 255;
                g.setColor( new Color( rgb, rgb, rgb ) );
                g.fillRect( i, j, 1, 1 );
            }
        }
        
        return ( terrainmap );
    }
    
    /**
     * Builds 3D geometrical representation of this height map using the given offsets and spacing.
     * 
     * @param startX starting X value (used to offset the geometry)
     * @param startY starting Y value (used to offset the geometry)
     * @param stepX distance along the X-axis between vertexes
     * @param stepY distance along the Y-axis between vertexes
     */
    public Geometry generate3D( float startX, float startY, float stepX, float stepY )
    {
        return ( generate3D( startX, startY, stepX, stepY, ZIGZAG_ROWS ) );
    }
    
    /**
     * Builds 3D geometrical representation of this height map using the given offsets and spacing.
     * internally calls the relevent tesselate for the geometry you have specified.
     * 
     * @param startX starting X value (used to offset the geometry)
     * @param startY starting Y value (used to offset the geometry)
     * @param stepX distance along the X-axis between vertexes
     * @param stepY distance along the Y-axis between vertexes
     * @param flags modifiers including using Triangles instead of Quads to generate the geom.
     */
    public IndexedTriangleArray generate3D( float startX, float startY, float stepX, float stepY, int flags )
    {
        // Generates the vertexes from the heightmap
        Point3f[][] coords = calculateCoords( startX, startY, stepX, stepY );
        Point3f[] verticies = flatten2DArray( coords );
        
        int[] indices = calculateIndicies( coords.length, coords[ 0 ].length, flags );
        
        // Calculates the coordinate array from the vertex indicies
        IndexedTriangleArray ita = new IndexedTriangleArray( verticies.length, indices.length );
        
        ita.setCoordinates( 0, verticies );
        ita.setIndex( indices );
        ita.setInitialIndexIndex( 0 );
        
        ita.calculateFaceNormals();
        ita.setIndex( indices );
        
        ita.setValidIndexCount( indices.length );
        
        ita.calculateFaceNormals();
        
        return ( ita );
    }
    
    /**
     * Creates Odejava GeomTriMesh object
     */
    /*
    public GeomTriMesh generateOde (String name, float startX, float startY, float stepX, float stepY, int flags)
    {
    	Point3f[][] coords = calculateCoords( startX, startY, stepX, stepY );
    	float[] vertices = point2float( flatten2DArray( coords ) );
    	int[] indices = calculateIndicies( coords.length, coords[ 0 ].length, flags );
    	
    	return ( new GeomTriMesh( name, vertices, indices ) );
    }
    
    public static GeomTriMesh generateOde (IndexedTriangleArray ita)
    {
    	return ( Xith3DToOdejava.createTriMesh( ita ) );
    }
    */
    
    /**
     * Calculates vertex points using given offsets and spacing.
     * 
     * @param startX starting X value (used to offset the geometry)
     * @param startY starting Y value (used to offset the geometry)
     * @param stepX distance along the X-axis between vertexes
     * @param stepY distance along the Y-axis between vertexes
     */
    public Point3f[][] calculateCoords( float startX, float startY, float stepX, float stepY )
    {
        // Generates the vertexes from the heightmap
        Point3f[][] pts = new Point3f[ heightmap.length ][ heightmap[ 0 ].length ];
        
        for ( int i = 0; i < heightmap.length; i++ )
        {
            for ( int j = 0; j < heightmap[ i ].length; j++ )
            {
                pts[ i ][ j ] = new Point3f( startX + stepX * i, startY + stepY * j, heightmap[ i ][ j ] );
            }
        }
        
        return ( pts );
    }
    
    public static Point3f[] unIndex( Point3f[] vertices, int[] indices )
    {
        Point3f[] points = new Point3f[ indices.length ];
        
        for ( int i = 0; i < indices.length; i++ )
        {
            points[ i ] = vertices[ indices[ i ] ];
        }
        
        return ( points );
    }
    
    /**
     * Converts the two dimentional table of points into 3D geometry by calculating the connecting edges
     * between the verticies by using Triangular primitives.
     * 
     * @param width
     * @param height
     * @param flags modifiers
     * 
     * @return built geometry
     * 
     * @see #ZIGZAG_ROWS
     * @see #ZIGZAG_COLS
     * @see #generate3D (float startX, float startY, float stepX, float stepY, int flags)
     */
    public static int[] calculateIndicies( int width, int height, int flags )
    {
        int[] indices = new int[ ( width - 1 ) * ( height - 1 ) * 6 ];
        
        int alternate = 0;
        
        if ( ( flags & FLIP_STARTING ) == FLIP_STARTING )
        {
            alternate = 1;
        }
        
        int indicies = 0;
        
        for ( int i = 0; i < width - 1; i++ )
        {
            for ( int j = 0; j < height - 1; j++ )
            {
                if ( alternate == 0 )
                {
                    indices[ indicies++ ] = flattenCoordinate( i + 1, j, width );
                    indices[ indicies++ ] = flattenCoordinate( i, j, width );
                    indices[ indicies++ ] = flattenCoordinate( i, j + 1, width );
                    
                    indices[ indicies++ ] = flattenCoordinate( i + 1, j + 1, width );
                    indices[ indicies++ ] = flattenCoordinate( i + 1, j, width );
                    indices[ indicies++ ] = flattenCoordinate( i, j + 1, width );
                }
                else
                {
                    indices[ indicies++ ] = flattenCoordinate( i + 1, j + 1, width );
                    indices[ indicies++ ] = flattenCoordinate( i, j, width );
                    indices[ indicies++ ] = flattenCoordinate( i, j + 1, width );
                    
                    indices[ indicies++ ] = flattenCoordinate( i + 1, j + 1, width );
                    indices[ indicies++ ] = flattenCoordinate( i + 1, j, width );
                    indices[ indicies++ ] = flattenCoordinate( i, j, width );
                }
                
                if ( ( flags & ZIGZAG_COLS ) == ZIGZAG_COLS )
                {
                    alternate++;
                    alternate = alternate % 2;
                }
            }
            
            if ( ( flags & ZIGZAG_ROWS ) == ZIGZAG_ROWS )
            {
                alternate++;
                alternate = alternate % 2;
            }
        }
        
        return ( indices );
    }
    
    private static int flattenCoordinate( int x, int y, int width )
    {
        return ( y * width + x );
    }
    
    public static Point3f[] flatten2DArray( Point3f[][] vertices )
    {
        Point3f[] vert2 = new Point3f[ vertices.length * vertices[ 0 ].length ];
        
        int vertCount = 0;
        for ( int i = 0; i < vertices.length; i++ )
        {
            for ( int j = 0; j < vertices[ i ].length; j++ )
            {
                vert2[ vertCount++ ] = vertices[ i ][ j ];
            }
        }
        
        return ( vert2 );
    }
    
    public static float[] point2float( Point3f[] coords )
    {
        float[] vertices = new float[ coords.length * 3 ];
        
        int vertex = 0;
        for ( int i = 0; i < coords.length; i++ )
        {
            vertices[ vertex++ ] = coords[ i ].getX();
            vertices[ vertex++ ] = coords[ i ].getY();
            vertices[ vertex++ ] = coords[ i ].getZ();
        }
        
        return ( vertices );
    }
}
