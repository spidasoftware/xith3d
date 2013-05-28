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
package org.xith3d.loaders.models.conversion;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.loaders.models._util.GeometryFactory;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.TexCoord3f;
import org.openmali.vecmath2.TexCoord4f;
import org.openmali.vecmath2.TexCoordf;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedGeometryArray;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleFanArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleFanArray;
import org.xith3d.scenegraph.TriangleStripArray;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class XithGeometryFactory implements GeometryFactory
{
    private Geometry.Optimization optimization;
    
    public final void setOptimization( Geometry.Optimization optimization )
    {
        this.optimization = optimization;
    }
    
    public final Geometry.Optimization getOptimization()
    {
        return ( optimization );
    }
    
    private final NamedObject createGeometry( String name, GeometryType type, boolean interleaved, int coordSize, int numVertices, int numIndices, int[] numStrips, int features, boolean colorAlpha, int[] tuSizes, int[] vaSizes )
    {
        Geometry geom;
        
        switch ( type )
        {
            case TRIANGLE_ARRAY:
                geom = new TriangleArray( coordSize, numVertices );
                break;
                
            case TRIANGLE_STRIP_ARRAY:
                if ( numStrips == null )
                    numStrips = new int[] { numVertices };
                geom = new TriangleStripArray( coordSize, numVertices, numStrips );
                break;
                
            case INDEXED_TRIANGLE_ARRAY:
                geom = new IndexedTriangleArray( coordSize, numVertices, numIndices );
                break;
                
            case INDEXED_TRIANGLE_STRIP_ARRAY:
                if ( numStrips == null )
                    numStrips = new int[] { numIndices };
                geom = new IndexedTriangleStripArray( coordSize, numVertices, numIndices, numStrips );
                break;
                
            case TRIANGLE_FAN_ARRAY:
                if ( numStrips == null )
                    geom = new TriangleFanArray( coordSize, numVertices );
                else
                    geom = new TriangleFanArray( coordSize, numVertices, numStrips );
                break;
                
            case INDEXED_TRIANGLE_FAN_ARRAY:
                if ( numStrips == null )
                    numStrips = new int[] { numIndices };
                geom = new IndexedTriangleFanArray( coordSize, numVertices, numIndices, numStrips );
                break;
                
            default:
                throw new Error( "Unsupported Geometry type " + type );
        }
        
        geom.setName( name );
        
        if ( interleaved )
        {
            geom.makeInterleaved( features, colorAlpha, tuSizes, vaSizes );
        }
        
        geom.setOptimization( optimization );
        
        return ( geom );
    }
    
    public final NamedObject createGeometry( String name, GeometryType type, int coordSize, int numVertices, int numIndices, int[] numStrips )
    {
        return ( createGeometry( name, type, false, coordSize, numVertices, numIndices, numStrips, 0, false, null, null ) );
    }
    
    public final NamedObject createInterleavedGeometry( String name, GeometryType type, int coordSize, int numVertices, int numIndices, int[] numStrips, int features, boolean colorAlpha, int[] tuSizes, int[] vaSizes )
    {
        return ( createGeometry( name, type, true, coordSize, numVertices, numIndices, numStrips, features, colorAlpha, tuSizes, vaSizes ) );
    }
    
    public final void setCoordinate( NamedObject geometry, GeometryType type, int vertexIndex, float x, float y, float z )
    {
        ( (Geometry)geometry ).setCoordinate( vertexIndex, x, y, z );
    }
    
    public final void setCoordinates( NamedObject geometry, GeometryType type, int vertexIndex, float[] data, int offset, int num )
    {
        ( (Geometry)geometry ).setCoordinates( vertexIndex, data, offset / ( (Geometry)geometry ).getCoordinatesSize(), num );
    }
    
    public final void setCoordinates( NamedObject geometry, GeometryType type, int vertexIndex, Point3f[] data, int offset, int num )
    {
        for ( int i = 0; i < num; i++ )
        {
            ( (Geometry)geometry ).setCoordinate( vertexIndex + i, data[ offset + i ] );
        }
    }
    
    public final void setNormal( NamedObject geometry, GeometryType type, int vertexIndex, float x, float y, float z )
    {
        ( (Geometry)geometry ).setNormal( vertexIndex, x, y, z );
    }
    
    public final void setNormals( NamedObject geometry, GeometryType type, int vertexIndex, float[] data, int offset, int num )
    {
        ( (Geometry)geometry ).setNormals( vertexIndex, data, offset / 3, num );
    }
    
    public final void setNormals( NamedObject geometry, GeometryType type, int vertexIndex, Vector3f[] data, int offset, int num )
    {
        for ( int i = 0; i < num; i++ )
        {
            ( (Geometry)geometry ).setNormal( vertexIndex + i, data[ offset + i ] );
        }
    }
    
    public final void setTexCoord( NamedObject geometry, GeometryType type, int textureUnit, int vertexIndex, float s, float t )
    {
        ( (Geometry)geometry ).setTextureCoordinate( textureUnit, vertexIndex, s, t );
    }
    
    public final void setTexCoords( NamedObject geometry, GeometryType type, int textureUnit, int texCoordSize, int vertexIndex, float[] data, int offset, int num )
    {
        ( (Geometry)geometry ).setTextureCoordinates( textureUnit, vertexIndex, texCoordSize, data, offset / texCoordSize, num );
    }
    
    public final void setTexCoords( NamedObject geometry, GeometryType type, int textureUnit, int texCoordSize, int vertexIndex, TexCoordf<?>[] data, int offset, int num )
    {
        switch ( texCoordSize )
        {
            case 1:
                /*
                for ( int i = 0; i < num; i++ )
                {
                    ( (GeometryArray)geometry ).setTextureCoordinate( textureUnit, vertexIndex + i, (TexCoord1f)data[ offset + i ] );
                }
                */
                break;
            case 2:
                for ( int i = 0; i < num; i++ )
                {
                    ( (Geometry)geometry ).setTextureCoordinate( textureUnit, vertexIndex + i, (TexCoord2f)data[ offset + i ] );
                }
                break;
            case 3:
                for ( int i = 0; i < num; i++ )
                {
                    ( (Geometry)geometry ).setTextureCoordinate( textureUnit, vertexIndex + i, (TexCoord3f)data[ offset + i ] );
                }
                break;
            case 4:
                for ( int i = 0; i < num; i++ )
                {
                    ( (Geometry)geometry ).setTextureCoordinate( textureUnit, vertexIndex + i, (TexCoord4f)data[ offset + i ] );
                }
                break;
                
        }
    }
    
    public final void setColors( NamedObject geometry, GeometryType type, int colorSize, int vertexIndex, float[] data, int offset, int num )
    {
        ( (Geometry)geometry ).setColors( vertexIndex, colorSize, data, offset / colorSize, num );
    }
    
    public final void setColors( NamedObject geometry, GeometryType type, int colorSize, int vertexIndex, Colorf[] data, int offset, int num )
    {
        for ( int i = 0; i < num; i++ )
        {
            ( (Geometry)geometry ).setColor( vertexIndex + i, data[ offset + i ] );
        }
    }
    
    public final void setVertexAttribs( NamedObject geometry, GeometryType type, int attribIndex, int attribSize, int vertexIndex, float[] data, int offset, int num )
    {
        ( (Geometry)geometry ).setVertexAttributes( attribIndex, vertexIndex, data, attribSize, offset / attribSize, num );
    }
    
    public final void setIndex( NamedObject geometry, GeometryType type, int vertexIndex, int[] data, int offset, int num )
    {
        switch ( type )
        {
            case TRIANGLE_ARRAY:
            case TRIANGLE_STRIP_ARRAY:
            case TRIANGLE_FAN_ARRAY:
                throw new Error( "The used GeometryType doesn't have an index!" );
            case INDEXED_TRIANGLE_ARRAY:
            case INDEXED_TRIANGLE_STRIP_ARRAY:
            case INDEXED_TRIANGLE_FAN_ARRAY:
                if ( ( vertexIndex == 0 ) && ( offset == 0 ) && ( num == data.length ) )
                {
                    ( (IndexedGeometryArray)geometry ).setIndex( data );
                }
                else
                {
                    for ( int i = 0; i < num; i++ )
                    {
                        ( (IndexedGeometryArray)geometry ).setIndex( vertexIndex + i, data[ offset + i ] );
                    }
                }
                
                return;
        }
        
        throw new Error( "Unsupported Geometry type " + type );
    }
    
    public final void finalizeGeometry( NamedObject geometry, GeometryType type, int initialVertexIndex, int numValidVertices, int initialIndexIndex, int numValidIndices )
    {
        ( (Geometry)geometry ).setInitialVertexIndex( initialVertexIndex );
        ( (Geometry)geometry ).setValidVertexCount( numValidVertices );
        
        switch ( type )
        {
            case INDEXED_TRIANGLE_ARRAY:
            case INDEXED_TRIANGLE_STRIP_ARRAY:
            case INDEXED_TRIANGLE_FAN_ARRAY:
                ( (IndexedGeometryArray)geometry ).setInitialIndexIndex( initialIndexIndex );
                ( (IndexedGeometryArray)geometry ).setValidIndexCount( numValidIndices );
        }
    }
    
    public XithGeometryFactory( Geometry.Optimization optimization )
    {
        this.optimization = optimization;
    }
}
