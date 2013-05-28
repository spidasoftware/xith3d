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

import java.util.ArrayList;
import java.util.Vector;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Vector3f;

/**
 * This class supports converting per-component index data to common-index data. Some file formats
 * allow to specify separate indices for coordinates, normals and tex coords. With this class, it is possible
 * to convert this data for use with GeometryInfo for further processing.<BR>
 * In case you would like to use it for non-indexed geometry, just assign indexes incrementally, with every
 * vertex component having it's own index.<BR>
 * Vertex component data can be specified at any moment - only requirement is that it needs to be specified
 * before fillGeometryInfo method is called.<BR>
 * Index data needs to be specified with per-face granularity. You can specify 3 coordinates and 3 normals,
 * or 1 coord, 1 normal, 1 coord, 1 normal, 1 coord, 1 normal, as long as 3 used components are specified
 * for a face before nextFace is called.  setFaceSmoothingGroup can be called only once per face, and it
 * can to be called at any moment before nextFace call. Example (single triangle with same normal for each vertex)
 * <pre>
 * GeometryCreator gc = new GeometryCreator();
 * gc.addNormal(0,1,0);
 * gc.addCoordinate(0,0,0);
 * gc.addCoordinate(1,0,1);
 * gc.addCoordinate(0,0,1);
 * gc.addCoordIndex(0); gc.addNormalIndex(0);
 * gc.addCoordIndex(1); gc.addNormalIndex(0);
 * gc.addCoordIndex(2); gc.addNormalIndex(0);
 * gc.nextFace();
 * XithGeometryInfo xgi = new XithGeometryInfo();
 * gc.fillGeometryInfo(xgi);
 * //gc is not longer important
 * </pre>
 * 
 * Important - all methods in this class copy vecmath objects by reference, so please do NOT reuse them
 * for filling data.
 * 
 * @author YVG
 */
public class GeometryCreator
{
    private ArrayList< Point3f > coordinates = new ArrayList< Point3f >();
    private ArrayList< Vector3f > normals = new ArrayList< Vector3f >();
    private ArrayList< Colorf > colors = new ArrayList< Colorf >();
    private ArrayList< TexCoord2f[] > texCoords = new ArrayList< TexCoord2f[] >();
    
    private Vector< Integer > coordIndices = new Vector< Integer >();
    private Vector< Integer > normalIndices = new Vector< Integer >();
    private Vector< Integer > colorIndices = new Vector< Integer >();
    private Vector< Integer > texIndices = new Vector< Integer >();
    
    private Vector< Integer > faceSizes = new Vector< Integer >();
    private Vector< Integer > smoothGroups = new Vector< Integer >();
    
    private int lastFaceEnd = 0;
    private int texSets = 1;
    
    /**
     * Default constructor allows for no or one texture coordinate set. For more sets, please use
     * GeometryCreator(int textureCoordinateSets);
     *
     */
    public GeometryCreator()
    {
    }
    
    public GeometryCreator( int textureCoordinateSets )
    {
        texSets = textureCoordinateSets;
    }
    
    public void addCoordinate( float x, float y, float z )
    {
        addCoordinate( new Point3f( x, y, z ) );
    }
    
    public void addCoordinate( Point3f p )
    {
        coordinates.add( p );
    }
    
    public void addNormal( float x, float y, float z )
    {
        normals.add( new Vector3f( x, y, z ) );
    }
    
    public void addNormal( Vector3f v )
    {
        normals.add( v );
    }
    
    public void addColor( float r, float g, float b )
    {
        addColor( new Colorf( r, g, b ) );
    }
    
    public void addColor( float r, float g, float b, float a )
    {
        addColor( new Colorf( r, g, b, a ) );
    }
    
    public void addTexCoord( float s, float t )
    {
        if ( texSets > 1 )
            throw new IllegalArgumentException( "For more than 1 texture coord set, use addTexCoord(TexCoord2f[])" );
        addTexCoord( new TexCoord2f( s, t ) );
    }
    
    public void addTexCoord( TexCoord2f t )
    {
        if ( texSets > 1 )
            throw new IllegalArgumentException( "For more than 1 texture coord set, use addTexCoord(TexCoord2f[])" );
        addTexCoords( new TexCoord2f[]
        {
            t
        } );
    }
    
    public void addTexCoords( TexCoord2f[] t )
    {
        if ( t.length != texSets )
        {
            throw new IllegalArgumentException( "Number of texture coordinates (" + t.length + ") differs from number of textureSets defined(" + texSets + ")" );
        }
        texCoords.add( t );
    }
    
    public void addColor( Colorf c )
    {
        colors.add( c );
    }
    
    public void addCoordIndex( int i )
    {
        coordIndices.add( i );
    }
    
    public void addNormalIndex( int i )
    {
        normalIndices.add( i );
    }
    
    public void addColorIndex( int i )
    {
        colorIndices.add( i );
    }
    
    public void addTexIndex( int i )
    {
        texIndices.add( i );
    }
    
    public void setFaceSmoothingGroup( int group )
    {
        smoothGroups.add( group );
    }
    
    public void nextFace()
    {
        int count = coordIndices.size();
        
        if ( lastFaceEnd == count )
            return;
        
        if ( lastFaceEnd + 3 != count )
            throw new IllegalStateException( "Currently only triangles are supported (face with 3 indices)" );
        
        if ( normalIndices.size() != 0 && normalIndices.size() != count )
            throw new IllegalStateException( "Different number of coordinate and normal indices" );
        
        if ( colorIndices.size() != 0 && colorIndices.size() != count )
            throw new IllegalStateException( "Different number of coordinate and color indices" );
        
        if ( texIndices.size() != 0 && texIndices.size() != count )
            throw new IllegalStateException( "Different number of coordinate and texture indices" );
        
        faceSizes.add( count - lastFaceEnd );
        
        if ( smoothGroups.size() != 0 && smoothGroups.size() != faceSizes.size() )
            throw new IllegalStateException( "Different number of faces and smoothing group info" );
        
        lastFaceEnd = count;
    }
    
    public void fillGeometryInfo( GeometryInfo gi )
    {
        // only 3-face sizes
        int[] faceData = new int[ coordIndices.size() ];
        VertexData[] vertices = new VertexData[ coordIndices.size() ];
        for ( int i = 0; i < coordIndices.size(); i++ )
        {
            VertexData vd = new VertexData();
            vd.coord = new Point3f( coordinates.get( coordIndices.get( i ) ) );
            if ( normalIndices.size() != 0 )
                vd.normal = new Vector3f( normals.get( normalIndices.get( i ) ) );
            if ( colorIndices.size() != 0 )
            {
                vd.color = colors.get( colorIndices.get( i ) );
            }
            
            if ( texIndices.size() != 0 )
            {
                vd.texCoords = texCoords.get( texIndices.get( i ) );
            }
            faceData[ i ] = i;
            vertices[ i ] = vd;
        }
        
        int[] smooth = new int[ faceSizes.size() ];
        for ( int i = 0; i < smooth.length; i++ )
        {
            if ( smoothGroups.size() > 0 )
                smooth[ i ] = smoothGroups.get( i );
            else
                smooth[ i ] = -1;
        }
        
        gi.vertices = vertices;
        gi.triangles = faceData;
        gi.smoothGroups = smooth;
        gi.state = GeometryInfo.STATE_SPLIT;
    }
}
