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
package org.xith3d.scenegraph;

import java.util.List;

import org.jagatoo.opengl.enums.GeometryArrayType;
import org.openmali.spatial.VertexContainer;
import org.openmali.spatial.WriteableTriangleContainer;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.spatial.polygons.Triangle;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.TexCoordf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.TupleNf;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.Vertex3f;
import org.xith3d.picking.PickPool;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.SceneGraphOpenGLReferences;

/**
 * A Geometry contains mesh data. Usually a Appearance is associated to it, both
 * composing a Shape3D
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Geometry extends NodeComponent implements VertexContainer
{
    public enum Optimization
    {
        /**
         * Use this, if a Shape is highly dynamic (Appearance and Geometry).
         */
        NONE,
        
        /**
         * The Renderer tries to find the best suitable optimization.
         */
        AUTO,
        
        /**
         * Use this, if a Geometry and texture coordinates are absolutely
         * static.<br>
         * Changes will be expensive.
         */
        USE_DISPLAY_LISTS,
        
        /**
         * Use this, if a Geometry and texture coordinates are absolutely
         * static.<br>
         * Changes will be expensive.
         */
        USE_VBOS,
        
        /**
         * Use this, if the vertices are absolutely static.<br>
         * Changes will be expensive.
         */
        USE_VBO_FOR_VERTEX_DATA,
        
        /**
         * Use this, if a the texture coordinates are absolutely static.<br>
         * Changes will be expensive.
         */
        USE_VBO_FOR_TEXTURE_COORDINATES;
        
        
        public final boolean isNone()
        {
            return ( this == NONE );
        }
        
        public final boolean isAuto()
        {
            return ( this == AUTO );
        }
        
        public final boolean dl()
        {
            return ( this == USE_DISPLAY_LISTS );
        }
        
        public final boolean vboForVertices()
        {
            return ( ( this == USE_VBOS ) || ( this == USE_VBO_FOR_VERTEX_DATA ) );
        }
        
        public final boolean vboForTexCoords()
        {
            return ( ( this == USE_VBOS ) || ( this == USE_VBO_FOR_TEXTURE_COORDINATES ) );
        }
        
        public final boolean vbo()
        {
            return ( ( this == USE_VBOS ) || ( this == USE_VBO_FOR_VERTEX_DATA ) || ( this == USE_VBO_FOR_TEXTURE_COORDINATES ) );
        }
        
        public final boolean optForVertices()
        {
            return ( vboForVertices() );
        }
        
        public final boolean optForTexCoords()
        {
            return ( vboForTexCoords() );
        }
    }
    
    public static final int COORDINATES = GeometryDataContainer.COORDINATES;
    public static final int NORMALS = GeometryDataContainer.NORMALS;
    public static final int COLORS = GeometryDataContainer.COLORS;
    public static final int TEXTURE_COORDINATES = GeometryDataContainer.TEXTURE_COORDINATES;
    public static final int VERTEX_ATTRIBUTES = GeometryDataContainer.VERTEX_ATTRIBUTES;
    public static final int BY_REFERENCE = GeometryDataContainer.BY_REFERENCE;
    public static final int INTERLEAVED = GeometryDataContainer.INTERLEAVED;
    /*
    public static final int USE_NIO_BUFFER = 2048;
    public static final int USE_COORD_INDEX_ONLY = 4096;
    */
    
    protected final GeometryDataContainer dataContainer;
    
    private final SceneGraphOpenGLReferences openGLReferences_texCoords = new SceneGraphOpenGLReferences( 1 );
    private final SceneGraphOpenGLReferences openGLReferences_geomData = new SceneGraphOpenGLReferences( 1 );
    private final SceneGraphOpenGLReferences openGLReferences = new SceneGraphOpenGLReferences( 1 );
    
    private Optimization optimization = Optimization.AUTO;
    private Bounds cachedBounds = null;
    
    /**
     * @return this Geometry's basic type (used by OpenGL).
     */
    public final GeometryArrayType getType()
    {
        return ( dataContainer.getType() );
    }
    
    /**
     * @return <code>true</code>, if this Geometry is a Strip
     */
    public final boolean isStrip()
    {
        return ( dataContainer.isStrip() );
    }
    
    /**
     * @return the number of vertices per face (3 for triangles).
     */
    public final int getFaceSize()
    {
        return ( dataContainer.getFaceSize() );
    }
    
    /**
     * @return <code>true</code>, if this Geometry is built of triangles or can at least be interpreted as triangles.
     */
    public abstract boolean isTriangulatable();
    
    /**
     * @return <code>true</code>, if this Geometry is built of triangles.
     */
    public abstract boolean isTriangulated();
    
    /**
     * @return the maximum number of vertices, this geometry can hold.
     */
    public final int getMaxVertexCount()
    {
        return ( dataContainer.getMaxVertexCount() );
    }
    
    /**
     * {@inheritDoc}
     */
    public int getVertexCount()
    {
        return ( dataContainer.getVertexCount() );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean getVertex( int i, Tuple3f pos )
    {
        return ( dataContainer.getVertex( i, pos ) );
    }
    
    /**
     * @return the format of the vertices in this object.
     */
    public final int getVertexFormat()
    {
        return ( dataContainer.getVertexFormat() );
    }
    
    /**
     * @return <code>true</code>, if this Geometry is constructed by interleaved data
     * (one ByteBuffer for all data except index).
     */
    public final boolean isInterleaved()
    {
        return ( dataContainer.isInterleaved() );
    }
    
    /**
     * @return <code>true</code>, if this Geometry has an Index.
     */
    public final boolean hasIndex()
    {
        return ( dataContainer.hasIndex() );
    }
    
    /**
     * @return <code>true</code>, if this geometry's color component has an alpha channel.
     */
    public final boolean hasColorAlpha()
    {
        return ( dataContainer.hasColorAlpha() );
    }
    
    /**
     * @return <code>true</code>, if this Geometry has the queried feature(s).
     */
    public final boolean hasFeature( int flag )
    {
        return ( dataContainer.hasFeature( flag ) );
    }
    
    /**
     * Sets this Geometry's Optimization to be used.
     * 
     * @param opt
     */
    public void setOptimization( Optimization opt )
    {
        if ( opt == null )
            throw new NullPointerException( "Optimization must not be null" );
        
        this.optimization = opt;
    }
    
    /**
     * @return this Geometry's Optimization level.
     */
    public final Optimization getOptimization()
    {
        return ( optimization );
    }
    
    /**
     * Marks this Geometry's bounds dirty. This will cause a bounds update
     * when the Geometry is next rendered.
     */
    public void setBoundsDirty()
    {
        this.cachedBounds = null;
    }
    
    /**
     * @return <code>true</code>, if a bounds update is requested.
     */
    public final boolean isBoundsDirty()
    {
        return ( cachedBounds == null );
    }
    
    void setCachedBounds( Bounds b )
    {
        this.cachedBounds = b;
    }
    
    final Bounds getCachedBounds()
    {
        return ( cachedBounds );
    }
    
    /**
     * This method calculates face normals (each orthogonal to its face).
     * 
     * @see #calculateFaceNormals()
     * 
     * @param apply if <code>true</code>, the normals are applied back to the Geometry
     * @param faceNormals must be of size getTrianglesCount(), or <code>null</code>.
     *                    It is filled with the face normals, if not <code>null</code>.
     * @param vertexNormals must be of size getVertexCount(), or <code>null</code>.
     *                      It is filled with the new vertex normals, if not <code>null</code>.
     */
    public void calculateFaceNormals( boolean apply, Vector3f[] faceNormals, Vector3f[] vertexNormals )
    {
        if ( !( this instanceof WriteableTriangleContainer ) )
            throw new Error( "Not an instance of WriteableTriangleContainer" );
        
        WriteableTriangleContainer wtc = (WriteableTriangleContainer)this;
        
        Triangle triangle = PickPool.allocateTriangle();
        triangle.setFeatures( Vertex3f.COORDINATES | Vertex3f.NORMALS );
        Vector3f faceNormal = Vector3f.fromPool();
        
        final int numTrangles = wtc.getTriangleCount();
        
        for ( int i = 0; i < numTrangles; i++ )
        {
            wtc.getTriangle( i, triangle );
            
            triangle.getFaceNormal( faceNormal );
            
            if ( apply )
            {
                triangle.setVertexNormalA( faceNormal );
                triangle.setVertexNormalB( faceNormal );
                triangle.setVertexNormalC( faceNormal );
                
                this.setTriangle( triangle );
            }
            
            if ( faceNormals != null )
            {
                if ( faceNormals[ i ] != null )
                    faceNormals[ i ].set( faceNormal );
                else
                    faceNormals[ i ] = new Vector3f( faceNormal );
            }
            
            if ( vertexNormals != null )
            {
                if ( vertexNormals[ triangle.getVertexIndexA() ] != null )
                    vertexNormals[ triangle.getVertexIndexA() ].set( faceNormal );
                else
                    vertexNormals[ triangle.getVertexIndexA() ] = new Vector3f( faceNormal );
                
                if ( vertexNormals[ triangle.getVertexIndexB() ] != null )
                    vertexNormals[ triangle.getVertexIndexB() ].set( faceNormal );
                else
                    vertexNormals[ triangle.getVertexIndexB() ] = new Vector3f( faceNormal );
                
                if ( vertexNormals[ triangle.getVertexIndexC() ] != null )
                    vertexNormals[ triangle.getVertexIndexC() ].set( faceNormal );
                else
                    vertexNormals[ triangle.getVertexIndexC() ] = new Vector3f( faceNormal );
            }
        }
        
        Vector3f.toPool( faceNormal );
    }
    
    /**
     * This method calculates face normals and applies them to the Geometry.
     * 
     * @see #calculateFaceNormals(boolean, Vector3f[], Vector3f[])
     */
    public void calculateFaceNormals()
    {
        calculateFaceNormals( true, null, null );
    }
    
    /**
     * Inverts all the normals in place.
     */
    public void invertNormals()
    {
        if ( !hasNormals() )
            return;
        
        Vector3f normal = Vector3f.fromPool();
        
        for ( int i = 0; i < getVertexCount(); i++ )
        {
            getNormal( i, normal );
            normal.negate();
            setNormal( i, normal );
        }
        
        Vector3f.toPool( normal );
    }
    
    /**
     * Super fast method add a bunch of data right into the data elements.
     */
    public final void addData( float[] coordData, float[] texCoordData, float[] normalData, float[] colorData )
    {
        dataContainer.addData( coordData, texCoordData, normalData, colorData );
    }
    
    /**
     * Super fast method for moving a bunch of data into the data elements. The
     * Positions are all translated according to the tuple passed in.
     */
    public final void addData( Tuple3f translate, int numVertices, float[] coordData, float[] texCoordData, float[] normalData, float[] colorData )
    {
        dataContainer.addData( translate.getX(), translate.getY(), translate.getZ(), numVertices, coordData, texCoordData, normalData, colorData );
    }
    
    /**
     * Super fast method for moving a bunch of data into the data elements. The
     * Positions are all translated according to the tuple passed in. The color
     * for all the vertices are set the specified value
     */
    public void addData( Tuple3f translate, int numVertices, float[] coordData, float[] texCoordData, float[] normalData, float alpha )
    {
        dataContainer.addData( translate.getX(), translate.getY(), translate.getZ(), numVertices, coordData, texCoordData, normalData, alpha );
    }
    
    /**
     * Directly sets the coordinates data buffer.
     * 
     * @param data
     */
    public final void setCoordinateData( GeomNioFloatData data )
    {
        dataContainer.setCoordinateData( data );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * @return the data buffer for coordinate data.
     */
    public final GeomNioFloatData getCoordinatesData()
    {
        return ( (GeomNioFloatData)dataContainer.getCoordinatesData() );
    }
    
    /**
     * @return 3 for 3D-coordinates, etc.
     */
    public final int getCoordinatesSize()
    {
        return ( dataContainer.getCoordinatesSize() );
    }
    
    /**
     * @return the offset in the data buffer, if this is interleaved data.
     */
    public final long getCoordinatesOffset()
    {
        return ( dataContainer.getCoordinatesOffset() );
    }
    
    /**
     * Directly sets the normals data buffer.
     * 
     * @param data
     */
    public void setNormalData( GeomNioFloatData data )
    {
        dataContainer.setNormalData( data );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * @return the data buffer for normal data.
     */
    public final GeomNioFloatData getNormalsData()
    {
        return ( (GeomNioFloatData)dataContainer.getNormalsData() );
    }
    
    /**
     * @return this size of normals (always 3).
     */
    public final int getNormalsSize()
    {
        return ( dataContainer.getNormalsSize() );
    }
    
    /**
     * @return the offset in the data buffer, if this is interleaved data.
     */
    public final long getNormalsOffset()
    {
        return ( dataContainer.getNormalsOffset() );
    }
    
    /**
     * Directly sets the color data buffer.
     * 
     * @param data
     */
    public void setColorData( GeomNioFloatData data )
    {
        dataContainer.setColorData( data );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * @return the data buffer for color data.
     */
    public final GeomNioFloatData getColorData()
    {
        return ( (GeomNioFloatData)dataContainer.getColorData() );
    }
    
    /**
     * @return this size of colors (always 3 or 4, no alpha / alpha).
     */
    public final int getColorsSize()
    {
        return ( dataContainer.getColorsSize() );
    }
    
    /**
     * @return the offset in the data buffer, if this is interleaved data.
     */
    public final long getColorsOffset()
    {
        return ( dataContainer.getColorsOffset() );
    }
    
    /**
     * Directly sets the tex-coords data buffer for the guven texture-unit.
     * 
     * @param unit
     * @param data
     */
    public void setTexCoordData( int unit, GeomNioFloatData data )
    {
        dataContainer.setTexCoordData( unit, data );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * @return the data buffer for tex-coord data for the given texture-unit.
     * 
     * @param unit
     */
    public final GeomNioFloatData getTexCoordsData( int unit )
    {
        return ( (GeomNioFloatData)dataContainer.getTexCoordsData( unit ) );
    }
    
    /**
     * @return the size of the texture coordinates of texture unit 'unit'.
     * This is always (1, 2, 3 or 4).
     */
    public final int getTexCoordSize( int unit )
    {
        return ( dataContainer.getTexCoordSize( unit ) );
    }
    
    /**
     * @return the number of texture units used in this Geometry.
     */
    public final int getNumTextureUnits()
    {
        return ( dataContainer.getNumTextureUnits() );
    }
    
    /**
     * @deprecated replaced by {@link #getNumTextureUnits()}
     */
    @Deprecated
    public final int getTexCoordSetCount()
    {
        return ( getNumTextureUnits() );
    }
    
    /**
     * @return The map for texture coordinates to texture units. (for internal use only!)
     */
    public final int[] getTexCoordSetMap()
    {
        return ( dataContainer.getTexCoordSetMap() );
    }
    
    /**
     * Gets the map for texture coordinates to texture units. (for internal use only!)
     * 
     * @param intArray 
     */
    public final void getTexCoordSetMap( int[] intArray )
    {
        dataContainer.getTexCoordSetMap( intArray );
    }
    
    /**
     * @return the offset in the data buffer, if this is interleaved data.
     */
    public final long getTexCoordsOffset( int unit )
    {
        return ( dataContainer.getTexCoordsOffset( unit ) );
    }
    
    /**
     * @return the data buffer for vertex-attributes data.
     * 
     * @param index
     */
    public final GeomNioFloatData getVertexAttribData( int index )
    {
        return ( (GeomNioFloatData)dataContainer.getVertexAttribData( index ) );
    }
    
    /**
     * @return the size of the queried vertex attributes.
     * 
     * @param index
     */
    public final int getVertexAttribSize( int index )
    {
        return ( dataContainer.getVertexAttribSize( index ) );
    }
    
    /**
     * @return the offset in the data buffer, if this is interleaved data.
     */
    public final long getVertexAttribsOffset( int index )
    {
        return ( dataContainer.getVertexAttribsOffset( index ) );
    }
    
    /**
     * @return the data buffer for interleaved data. If this Geometry is not
     * interleaved, an error is thrown.
     */
    public final GeomNioFloatData getInterleavedData()
    {
        return ( (GeomNioFloatData)dataContainer.getInterleavedData() );
    }
    
    /**
     * Sets the index of the first vertex which will be rendered from this
     * geometry array. The extact vertices which will be rendered is from
     * InitialVertexIndex to InitialVertexIndex + ValidVertexCount-1.
     * 
     * @param initialVertex
     */
    public final void setInitialVertexIndex( int initialVertex )
    {
        dataContainer.setInitialIndex( initialVertex );
    }
    
    /**
     * @return the index of the first vertex which will be rendered from this
     *         geometry array. The extact vertices which will be rendered is
     *         from InitialVertexIndex to InitialVertexIndex +
     *         ValidVertexCount-1.
     */
    public final int getInitialVertexIndex()
    {
        return ( dataContainer.getInitialIndex() );
    }
    
    /**
     * Sets the number of vertices which will be rendered from this geometry
     * array. The extact vertices which will be rendered is from
     * InitialVertexIndex to InitialVertexIndex + ValidVertexCount-1.
     * 
     * @param count
     */
    public void setValidVertexCount( int count )
    {
        dataContainer.setValidVertexCount( count );
        
        setChanged( true );
        setBoundsDirty();
    }
    
    /**
     * @return the number of vertices which will be rendered from this geometry
     *         array. The extact vertices which will be rendered is from
     *         InitialVertexIndex to InitialVertexIndex + ValidVertexCount-1.
     */
    public final int getValidVertexCount()
    {
        return ( dataContainer.getValidVertexCount() );
    }
    
    
    /**
     * Sets the coordinates of the specified vertex. The coordinates should
     * occupy the first three indices of the given array.
     */
    public final void setCoordinate( int vertexIndex, float[] floatArray )
    {
        dataContainer.setCoordinate( vertexIndex, floatArray );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the coordinates of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to be modified.
     * @param floatArray The new coordinates. The size of the array must be a
     *            multiple of 3.
     * @param startIndex The index of the first coordinate in the given array.
     *            The first read item of the array will be startIndex*3.
     * @param length The number of vertices to copy
     */
    public final void setCoordinates( int vertexIndex, float[] floatArray, int startIndex, int length )
    {
        dataContainer.setCoordinates( vertexIndex, floatArray, startIndex, length );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the coordinates of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param floatArray The new coordinates. The size of the array must be a
     *            multiple of 3.
     */
    public final void setCoordinates( int vertexIndex, float[] floatArray )
    {
        dataContainer.setCoordinates( vertexIndex, floatArray );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the coordinates of the vertex at the given index
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param point3f The new coordinates
     */
    public final void setCoordinate( int vertexIndex, Tuple3f point3f )
    {
        dataContainer.setCoordinate( vertexIndex, point3f );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the coordinates of the vertex at the given index
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param x The new coordinates
     * @param y The new coordinates
     * @param z The new coordinates
     */
    public final void setCoordinate( int vertexIndex, float x, float y, float z )
    {
        dataContainer.setCoordinate( vertexIndex, x, y, z );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the coordinates of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param point3fArray The new coordinates.
     */
    public final void setCoordinates( int vertexIndex, Tuple3f[] point3fArray )
    {
        dataContainer.setCoordinates( vertexIndex, point3fArray );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the coordinates of the vertices starting at the specified index
     * 
     * @param vertexIndex The index of the first vertex to be modified.
     * @param point3fArray The new coordinates
     * @param startIndex The index of the first coordinate in the given array
     * @param length The number of coordinates to copy
     */
    public final void setCoordinates( int vertexIndex, Tuple3f[] point3fArray, int startIndex, int length )
    {
        dataContainer.setCoordinates( vertexIndex, point3fArray, startIndex, length );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    public final void setCoordinates( int vertexIndex, List<Tuple3f> point3fList )
    {
        dataContainer.setCoordinates( vertexIndex, point3fList );
        
        setBoundsDirty();
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    public final <T extends Tuple3f> T getCoordinate( int index, T point )
    {
        dataContainer.getCoordinate( index, point );
        
        return ( point );
    }
    
    public final void getCoordinate( int vertexIndex, float[] floatArray )
    {
        dataContainer.getCoordinate( vertexIndex, floatArray );
    }
    
    public final void getCoordinates( int vertexIndex, Tuple3f[] point3fArray )
    {
        dataContainer.getCoordinates( vertexIndex, point3fArray );
    }
    
    public final void getCoordinates( int vertexIndex, float[] floatArray )
    {
        dataContainer.getCoordinates( vertexIndex, floatArray );
    }
    
    /**
     * @return <code>true</code>, if this geometry contains normal data.
     */
    public final boolean hasNormals()
    {
        return ( dataContainer.hasNormals() );
    }
    
    /**
     * Sets the normal of the vertex at the given index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param floatArray The new normal data. Its size must be a multiple of 3.
     */
    public final void setNormal( int vertexIndex, float[] floatArray )
    {
        dataContainer.setNormal( vertexIndex, floatArray );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the normals of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param floatArray The new normals. Its size must be a multiple of 3.
     */
    public final void setNormals( int vertexIndex, float[] floatArray )
    {
        dataContainer.setNormals( vertexIndex, floatArray );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the normals of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param floatArray The new normal data. Its size must be a multiple of 3.
     * @param startIndex The first coordinate to use in the given array. The
     *            first element of the array to be used will be startIndex*3.
     * @param length The number of vertices to modify
     */
    public final void setNormals( int vertexIndex, float[] floatArray, int startIndex, int length )
    {
        dataContainer.setNormals( vertexIndex, floatArray, startIndex, length );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the normal of the vertex at the given index.
     * 
     * @param vertexIndex THe index of the vertex to modify
     * @param vector3f The new normal
     */
    public final void setNormal( int vertexIndex, Vector3f vector3f )
    {
        dataContainer.setNormal( vertexIndex, vector3f );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the normal of the vertex at the given index.
     * 
     * @param vertexIndex THe index of the vertex to modify
     * @param x The new normal
     * @param y The new normal
     * @param z The new normal
     */
    public final void setNormal( int vertexIndex, float x, float y, float z )
    {
        dataContainer.setNormal( vertexIndex, x, y, z );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the normals of the vertices at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param vector3fArray The new normals
     */
    public final void setNormals( int vertexIndex, Vector3f[] vector3fArray )
    {
        dataContainer.setNormals( vertexIndex, vector3fArray );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the normals of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param vector3fArray The new normals
     * @param startIndex The index of the first coordinate to use in the given
     *            array.
     * @param length The number of vertices to modify
     */
    public final void setNormals( int vertexIndex, Vector3f[] vector3fArray, int startIndex, int length )
    {
        dataContainer.setNormals( vertexIndex, vector3fArray, startIndex, length );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    public final Vector3f getNormal( int index, Vector3f normal )
    {
        return ( dataContainer.getNormal( index, normal ) );
    }
    
    public final void getNormal( int vertexIndex, float[] floatArray )
    {
        dataContainer.getNormal( vertexIndex, floatArray );
    }
    
    public final void getNormals( int vertexIndex, float[] floatArray )
    {
        dataContainer.getNormals( vertexIndex, floatArray );
    }
    
    public final void getNormals( int index0, Vector3f[] vector3fArray )
    {
        dataContainer.getNormals( index0, vector3fArray );
    }
    
    /**
     * Flips (inverts, negates) all the normals of this Geometry.
     * 
     * @throws Error, if this Geometry doesn't currently have normals.
     */
    public void flipNormals()
    {
        if ( !this.hasNormals() )
            throw new Error( "This Geometry doesn't have normals!" );
        
        Vector3f normal = Vector3f.fromPool();
        
        for ( int i = 0; i < getVertexCount(); i++ )
        {
            getNormal( i, normal );
            
            normal.negate();
            
            setNormal( i, normal );
        }
        
        Vector3f.toPool( normal );
    }
    
    /**
     * @return <code>true</code>, if this Geometry contains color data.
     */
    public final boolean hasColors()
    {
        return ( dataContainer.hasColors() );
    }
    
    /**
     * Sets the color of the vertex at the specified index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param floatArray The new color data. The first {@link #colorSize}
     *            elements will be used.
     */
    public final void setColor( int vertexIndex, float[] floatArray )
    {
        dataContainer.setColor( vertexIndex, floatArray );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the colors of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param colorSize
     * @param floatArray The new color value. Its size must be a multiple of
     *            {@link #colorSize}.
     */
    public final void setColors( int vertexIndex, int colorSize, float[] floatArray )
    {
        dataContainer.setColors( vertexIndex, colorSize, floatArray );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the colors of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to be modified.
     * @param floatArray The new color data. The size of the array must be a
     *            multiple of {@link #colorSize}.
     * @param colorSize
     * @param startIndex The index of the first color in the given array. The
     *            first read item of the array will be startIndex*colorSize.
     * @param length The number of colors to copy
     */
    public final void setColors( int vertexIndex, int colorSize, float[] floatArray, int startIndex, int length )
    {
        dataContainer.setColors( vertexIndex, colorSize, floatArray, startIndex, length );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the color of the vertex at the specified index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param colorf The new color.
     */
    public final void setColor( int vertexIndex, Colorf colorf )
    {
        dataContainer.setColor( vertexIndex, colorf );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the color of the vertex at the specified index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param r
     * @param g
     * @param b
     */
    public final void setColor( int vertexIndex, float r, float g, float b )
    {
        dataContainer.setColor( vertexIndex, r, g, b );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the color of the vertex at the specified index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param r
     * @param g
     * @param b
     * @param a
     */
    public final void setColor( int vertexIndex, float r, float g, float b, float a )
    {
        dataContainer.setColor( vertexIndex, r, g, b, a );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the colors of the vertices starting at the specified index.
     * 
     * @param vertexIndex The index of the first vertex to modify.
     * @param colorfArray The new color values.
     */
    public final void setColors( int vertexIndex, Colorf[] colorfArray )
    {
        dataContainer.setColors( vertexIndex, colorfArray );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the colors of the vertices starting at the given index.
     * 
     * @param vertexIndex The index of the first vertex to modify
     * @param colorfArray The new color data.
     * @param startIndex The index of the first color in the given array
     * @param length The number of vertices to modify.
     */
    public final void setColors( int vertexIndex, Colorf[] colorfArray, int startIndex, int length )
    {
        dataContainer.setColors( vertexIndex, colorfArray, startIndex, length );
        
        openGLReferences_geomData.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Gets the color of the vertex at the specified index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param colorf The new color.
     */
    public final Colorf getColor( int vertexIndex, Colorf colorf )
    {
        return ( dataContainer.getColor( vertexIndex, colorf ) );
    }
    
    /**
     * Gets the color of the vertex at the specified index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param floatArray
     */
    public final void getColor( int vertexIndex, float[] floatArray )
    {
        dataContainer.getColor( vertexIndex, floatArray );
    }
    
    /**
     * Gets the color of the vertex at the specified index.
     * 
     * @param vertexIndex The index of the vertex to modify
     * @param colorf The new color.
     */
    public final void getColors( int vertexIndex, float[] floatArray )
    {
        dataContainer.getColors( vertexIndex, floatArray );
    }
    
    /**
     * @return <code>true</code>, if this Geometry contains texture-coordinate data (for any texture-unit).
     */
    public final boolean hasTextureCoordinates()
    {
        return ( dataContainer.hasTextureCoordinates() );
    }
    
    /**
     * @return <code>true</code>, if this Geometry contains texture-coordinate data (for the given texture-unit).
     */
    public final boolean hasTextureCoordinates( int unit )
    {
        return ( dataContainer.hasTextureCoordinates( unit ) );
    }
    
    /**
     * Sets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param floatArray The new texture coordinate data. Its size must be 2, 3 or 4.
     */
    public final void setTextureCoordinate( int unit, int vertexIndex, float[] floatArray )
    {
        dataContainer.setTextureCoordinate( unit, vertexIndex, floatArray );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex starting at the specified index
     * for the specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the first vertex to modify
     * @param texCoordSize 1, 2, 3 or 4
     * @param floatArray The new coordinate data. Its size must be a multiple of
     *            2, 3 or 4 depending on texCoordSet format.
     */
    public final void setTextureCoordinates( int unit, int vertexIndex, int texCoordSize, float[] floatArray )
    {
        dataContainer.setTextureCoordinates( unit, vertexIndex, texCoordSize, floatArray );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex starting at the specified index
     * for the specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the first vertex to modify
     * @param texCoordSize
     * @param floatArray The new coordinate data. Its size must be a multiple of 2.
     * @param startIndex
     * @param length
     */
    public final void setTextureCoordinates( int unit, int vertexIndex, int texCoordSize, float[] floatArray, int startIndex, int length )
    {
        dataContainer.setTextureCoordinates( unit, vertexIndex, texCoordSize, floatArray, startIndex, length );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param s
     */
    public final void setTextureCoordinate( int unit, int vertexIndex, float s )
    {
        dataContainer.setTextureCoordinate( unit, vertexIndex, s );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param s
     * @param t
     */
    public final void setTextureCoordinate( int unit, int vertexIndex, float s, float t )
    {
        dataContainer.setTextureCoordinate( unit, vertexIndex, s, t );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param s
     * @param t
     * @param r
     */
    public final void setTextureCoordinate( int unit, int vertexIndex, float s, float t, float r )
    {
        dataContainer.setTextureCoordinate( unit, vertexIndex, s, t, r );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param s
     * @param t
     * @param r
     * @param q
     */
    public final void setTextureCoordinate( int unit, int vertexIndex, float s, float t, float r, float q )
    {
        dataContainer.setTextureCoordinate( unit, vertexIndex, s, t, r, q );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param texCoord
     */
    public final void setTextureCoordinate( int unit, int vertexIndex, TexCoordf<?> texCoord )
    {
        dataContainer.setTextureCoordinate( unit, vertexIndex, texCoord );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex starting at the specified index
     * for the specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the first vertex to modify
     * @param texCoordArray
     */
    public final void setTextureCoordinates( int unit, int vertexIndex, TexCoordf<?>[] texCoordArray )
    {
        dataContainer.setTextureCoordinates( unit, vertexIndex, texCoordArray );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Sets the texture coordinate of the vertex starting at the specified index
     * for the specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the first vertex to modify
     * @param texCoordArray
     * @param startIndex
     * @param length
     */
    public final void setTextureCoordinates( int unit, int vertexIndex, TexCoordf<?>[] texCoordArray, int startIndex, int length )
    {
        dataContainer.setTextureCoordinates( unit, vertexIndex, texCoordArray, startIndex, length );
        
        openGLReferences_texCoords.invalidateNames();
        openGLReferences.invalidateNames();
    }
    
    /**
     * Gets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param floatArray The new texture coordinate data. Its size must be 2, 3 or 4.
     */
    public final void getTextureCoordinate( int unit, int vertexIndex, float[] floatArray )
    {
        dataContainer.getTextureCoordinate( unit, vertexIndex, floatArray );
    }
    
    public final void getTextureCoordinates( int unit, int vertexIndex, float[] floatArray )
    {
        dataContainer.getTextureCoordinates( unit, vertexIndex, floatArray );
    }
    
    /**
     * Gets the texture coordinate of the vertex at the specified index for the
     * specified coordinates set.
     * 
     * @param unit The coordinates set.
     * @param vertexIndex The index of the vertex to modify
     * @param texCoord
     */
    public final <T extends TexCoordf<?>> T getTextureCoordinate( int unit, int vertexIndex, T texCoord )
    {
        return ( dataContainer.getTextureCoordinate( unit, vertexIndex, texCoord ) );
    }
    
    /**
     * @return <code>true</code>, if this Geometry contains vertex-attribute data.
     */
    public final boolean hasVertexAttributes()
    {
        return ( dataContainer.hasVertexAttributes() );
    }
    
    /**
     * @return <code>true</code>, if this Geometry contains vertex-attribute data at the given index.
     * 
     * @param attribIndex
     */
    public final boolean hasVertexAttributes( int attribIndex )
    {
        return ( dataContainer.hasVertexAttributes( attribIndex ) );
    }
    
    /**
     * @return the number of vertex attributes in the Geometry.
     */
    public final int getVertexAttributesCount()
    {
        return ( dataContainer.getVertexAttributesCount() );
    }
    
    /**
     * Sets the vertex attribute of the vertex at the specified index for the
     * specified attribute.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the vertex to modify
     * @param floatArray The new attribute data. Its size must be 1, 2, 3 or 4.
     */
    public final void setVertexAttribute( int attribIndex, int vertexIndex, float[] floatArray )
    {
        dataContainer.setVertexAttribute( attribIndex, vertexIndex, floatArray );
    }
    
    /**
     * Sets the vertex attributes.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the first vertex to modify
     * @param values The new attribute data.
     * @param attribSize the size of each attribute element (1, 2, 3, 4)
     */
    public final void setVertexAttributes( int attribIndex, int vertexIndex, float[] values, int attribSize )
    {
        dataContainer.setVertexAttributes( attribIndex, vertexIndex, values, attribSize );
    }
    
    /**
     * Sets the vertex attributes.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the first vertex to modify
     * @param values The new attribute data.
     * @param attribsSize (1, 2, 3, 4)
     * @param startIndex
     * @param length
     */
    public final void setVertexAttributes( int attribIndex, int vertexIndex, float[] values, int attribsSize, int startIndex, int length )
    {
        dataContainer.setVertexAttributes( attribIndex, vertexIndex, values, attribsSize, startIndex, length );
    }
    
    /**
     * Sets the vertex attribute of the vertex at the specified index for the
     * specified attribute.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the vertex to modify
     * @param value The new attribute data.
     */
    public final void setVertexAttribute( int attribIndex, int vertexIndex, float value )
    {
        dataContainer.setVertexAttribute( attribIndex, vertexIndex, value );
    }
    
    /**
     * Sets the vertex attribute of the vertex at the specified index for the
     * specified attribute.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the vertex to modify
     * @param value The new attribute data.
     */
    public final void setVertexAttribute( int attribIndex, int vertexIndex, TupleNf<?> value )
    {
        dataContainer.setVertexAttribute( attribIndex, vertexIndex, value );
    }
    
    /**
     * Sets the vertex attributes.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the first vertex to modify
     * @param values The new attribute data.
     */
    public final void setVertexAttributes( int attribIndex, int vertexIndex, TupleNf<?>[] values )
    {
        dataContainer.setVertexAttributes( attribIndex, vertexIndex, values );
    }
    
    /**
     * Sets the vertex attributes.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the first vertex to modify
     * @param values The new attribute data.
     * @param startIndex
     * @param length
     */
    public final void setVertexAttributes( int attribIndex, int vertexIndex, TupleNf<?>[] values, int startIndex, int length )
    {
        dataContainer.setVertexAttributes( attribIndex, vertexIndex, values, startIndex, length );
    }
    
    public final void getVertexAttribute( int attribIndex, int vertexIndex, float[] floatArray )
    {
        dataContainer.getVertexAttribute( attribIndex, vertexIndex, floatArray );
    }
    
    public final void getVertexAttributes( int attribIndex, int vertexIndex, float[] floatArray )
    {
        dataContainer.getVertexAttributes( attribIndex, vertexIndex, floatArray );
    }
    
    /**
     * Gets the vertex attribute of the vertex at the specified index for the
     * specified attribute.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the vertex to modify
     */
    public final float getVertexAttribute( int attribIndex, int vertexIndex )
    {
        return ( dataContainer.getVertexAttribute( attribIndex, vertexIndex ) );
    }
    
    /**
     * Gets the vertex attribute of the vertex at the specified index for the
     * specified attribute.
     * 
     * @param attribIndex The attributes set.
     * @param vertexIndex The index of the vertex to modify
     * @param value The buffer for the attribute data.
     */
    public final void getVertexAttribute( int attribIndex, int vertexIndex, TupleNf<?> value )
    {
        dataContainer.getVertexAttribute( attribIndex, vertexIndex, value );
    }
    
    /**
     * Applies the the n-th Triangle to the GeometryArray.
     * This method must be overridden by concrete classes to fix the vertex-index (e.g. for an IndexedTriangleArray)
     * 
     * @param i0 the first triangle's vertex-index
     * @param i1 the second triangle's vertex-index
     * @param i2 the third triangle's vertex-index
     * @param triangle
     * 
     * @return true, if the triangle could be applied
     */
    public final boolean setTriangle( int i0, int i1, int i2, Triangle triangle )
    {
        return ( setTriangle( i0, i1, i2, triangle ) );
    }
    
    /**
     * Applies the the n-th Triangle to the GeometryArray.
     * This method must be overridden by concrete classes to fix the vertex-index (e.g. for an IndexedTriangleArray)
     * 
     * @param triangle
     * 
     * @return true, if the triangle could be applied
     */
    public final boolean setTriangle( Triangle triangle )
    {
        return ( dataContainer.setTriangle( triangle ) );
    }
    
    /**
     * Retrieves the the n-th Triangle from the GeometryArray.
     * This method must be overridden by concrete classes to fix the vertex-index (e.g. for an IndexedTriangleArray)
     * 
     * @param i0 the first triangle's vertex-index
     * @param i1 the second triangle's vertex-index
     * @param i2 the third triangle's vertex-index
     * @param triangle
     * 
     * @return true, if the triangle could be retrieved
     */
    public final boolean getTriangle( int i0, int i1, int i2, Triangle triangle )
    {
        return ( dataContainer.getTriangle( i0, i1, i2, triangle ) );
    }
    
    public final float[] getCoordRefFloat()
    {
        return ( dataContainer.getCoordRefFloat() );
    }
    
    public final float[] getColorRefFloat()
    {
        return ( dataContainer.getColorRefFloat() );
    }
    
    public final float[] getNormalRefFloat()
    {
        return ( dataContainer.getNormalRefFloat() );
    }
    
    public final float[] getTexCoordRefFloat( int unit )
    {
        return ( dataContainer.getTexCoordRefFloat( unit ) );
    }
    
    public final SceneGraphOpenGLReferences getOpenGLReference_DL_GeomData()
    {
        return ( openGLReferences_geomData );
    }
    
    public final SceneGraphOpenGLReferences getOpenGLReference_DL_TexCoords()
    {
        return ( openGLReferences_texCoords );
    }
    
    public final SceneGraphOpenGLReferences getOpenGLReference_DL()
    {
        return ( openGLReferences );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize()
    {
        openGLReferences_geomData.prepareObjectForDestroy();
        openGLReferences_texCoords.prepareObjectForDestroy();
        openGLReferences.prepareObjectForDestroy();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( getCoordinatesData() != null )
            getCoordinatesData().freeOpenGLResources( canvasPeer );
        
        if ( getNormalsData() != null )
            getNormalsData().freeOpenGLResources( canvasPeer );
        
        if ( getColorData() != null )
            getColorData().freeOpenGLResources( canvasPeer );
        
        if ( hasTextureCoordinates() )
        {
            int[] tcsm = getTexCoordSetMap();
            
            for ( int i = 0; i < tcsm.length; i++ )
            {
                getTexCoordsData( tcsm[i] ).freeOpenGLResources( canvasPeer );
            }
        }
        
        if ( hasVertexAttributes() )
        {
            GeomNioFloatData d;
            
            for ( int i = 0; i < 16; i++ )
            {
                if ( ( d = getVertexAttribData( i ) ) != null )
                    d.freeOpenGLResources( canvasPeer );
            }
        }
        
        if ( openGLReferences_geomData.referenceExists( canvasPeer ) )
            openGLReferences_geomData.prepareObjectForDestroy( canvasPeer );
        
        if ( openGLReferences_texCoords.referenceExists( canvasPeer ) )
            openGLReferences_texCoords.prepareObjectForDestroy( canvasPeer );
        
        if ( openGLReferences.referenceExists( canvasPeer ) )
            openGLReferences.prepareObjectForDestroy( canvasPeer );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        Geometry o = (Geometry)original;
        
        this.dataContainer.copyFrom( o.dataContainer, forceDuplicate );
        
        this.optimization = o.optimization;
        
        this.setBoundsDirty();
    }
    
    /**
     * Sets up the Geometry to be stored in a single NIO buffer for interleaved geometry.
     * 
     * @param features
     * @param colorAlpha
     * @param tuSizes the sizes of the texture-units (may be null, if not contained in the features mask)
     * @param vaSizes the sizes of the vertex-arrays (may be null, if not contained in the features mask)
     */
    public final void makeInterleaved( int features, boolean colorAlpha, int[] tuSizes, int[] vaSizes )
    {
        dataContainer.makeInterleaved( features, colorAlpha, tuSizes, vaSizes );
    }
    
    /**
     * Sets up the Geometry to be stored in a single NIO buffer for interleaved geometry.
     */
    public final void makeInterleaved()
    {
        dataContainer.makeInterleaved();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Geometry cloneNodeComponent( boolean forceDuplicate );
    
    public Geometry( GeometryArrayType type, boolean hasIndex, int coordsSize, int vertexCount, int[] stripVertexCounts, int indexCount )
    {
        super( false );
        
        this.dataContainer = new GeometryDataContainer( type, hasIndex, coordsSize, vertexCount, stripVertexCounts, indexCount );
    }
}
