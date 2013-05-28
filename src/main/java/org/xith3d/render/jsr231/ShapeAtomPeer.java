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
package org.xith3d.render.jsr231;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import org.jagatoo.geometry.GeomNioData;
import org.jagatoo.logging.ProfileTimer;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLInfo;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.OpenGlExtensions;
import org.xith3d.render.RenderAtomPeer;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.SceneGraphOpenGLReference;
import org.xith3d.render.SceneGraphOpenGLReferences;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.preprocessing.ShapeAtom;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Billboard;
import org.xith3d.scenegraph.GeomNioFloatData;
import org.xith3d.scenegraph.GeomNioIntData;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.GeometryStripArray;
import org.xith3d.scenegraph.IndexedGeometryArray;
import org.xith3d.scenegraph.IndexedGeometryStripArray;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph._SG_PrivilegedAccess;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.utility.logging.X3DLog;

import com.sun.opengl.util.BufferUtil;

/**
 * Renders a single Shape3D.
 * 
 * @author David Yazel
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 */
public class ShapeAtomPeer extends RenderAtomPeer
{
    private static enum VBOMode
    {
        ALWAYS,
        AUTO,
        NEVER
    }
    
    private static final int OPT_AUTO_MAX_FRAMES = 10;
    
    private static final int CARE_MAP_COORDINATES = 1;
    private static final int CARE_MAP_NORMALS = 2;
    private static final int CARE_MAP_COLORS = 4;
    private static final int CARE_MAP_TEXTURE_COORDS = 8;
    private static final int CARE_MAP_VERTEX_ATTRIBS = 16;
    private static final int CARE_MAP_INDICES = 32;
    protected static final int CARE_MAP_ALL = CARE_MAP_COORDINATES | CARE_MAP_NORMALS | CARE_MAP_COLORS | CARE_MAP_TEXTURE_COORDS | CARE_MAP_VERTEX_ATTRIBS | CARE_MAP_INDICES;
    
    private static final IntBuffer tmpIntBuffer = BufferUtil.newIntBuffer( 1 );
    
    private static FloatBuffer matrixBuffer = BufferUtil.newFloatBuffer( 16 );
    
    private static Transform3D lastTransform = null;
    
    private static SceneGraphOpenGLReferences.Provider vboNameProvider = new SceneGraphOpenGLReferences.Provider()
    {
        public SceneGraphOpenGLReference newReference( CanvasPeer canvasPeer, SceneGraphOpenGLReferences references, int numNamesPerContext )
        {
            return ( new SceneGraphOpenGLReference( canvasPeer, references, numNamesPerContext )
            {
                @Override
                public void prepareObjectForDestroy()
                {
                    SceneGraphOpenGLReference ref = getReferences().removeReference( getContext().getCanvasID() );
                    
                    ( (CanvasPeerImplBase)getContext() ).addDestroyableObject( ref );
                }
                
                @Override
                public void destroyObject( int index, int name )
                {
                    final GL gl = ( (CanvasPeerImplBase)getContext() ).getGL();
                    
                    tmpIntBuffer.clear();
                    tmpIntBuffer.put( name ).rewind();
                    
                    if ( getContext().getOpenGLCapabilities().isMinVersion15() )
                        gl.glDeleteBuffers( 1, tmpIntBuffer );
                    else
                        gl.glDeleteBuffersARB( 1, tmpIntBuffer );
                }
            } );
        }
    };
    
    /**
     * Prepares this instance to render the next frame.
     */
    public static final void reset()
    {
        lastTransform = null;
    }
    
    protected static final void setMatrix( GL gl, View view, Transform3D transform, boolean ignoreRotation, boolean forced )
    {
        if ( ( lastTransform == transform ) && !forced )
            return;
        
        lastTransform = transform;
        
        // Combine WorldTransform and View (camera) transforms
        gl.glLoadMatrixf( _SG_PrivilegedAccess.getFloatBuffer( view.getModelViewTransform( false ), true ) );
        
        if ( !ignoreRotation )
        {
            gl.glMultMatrixf( _SG_PrivilegedAccess.getFloatBuffer( transform, true ) );
        }
        else
        {
            final Matrix4f mat = transform.getMatrix4f();
            
            matrixBuffer.clear();
            
            matrixBuffer.put( 1.0f );
            matrixBuffer.put( 0.0f );
            matrixBuffer.put( 0.0f );
            matrixBuffer.put( mat.m30() );
            
            matrixBuffer.put( 0.0f );
            matrixBuffer.put( 1.0f );
            matrixBuffer.put( 0.0f );
            matrixBuffer.put( mat.m31() );
            
            matrixBuffer.put( 0.0f );
            matrixBuffer.put( 0.0f );
            matrixBuffer.put( 1.0f );
            matrixBuffer.put( mat.m32() );
            
            matrixBuffer.put( mat.m03() );
            matrixBuffer.put( mat.m13() );
            matrixBuffer.put( mat.m23() );
            matrixBuffer.put( mat.m33() );
            
            matrixBuffer.flip();
            
            gl.glMultMatrixf( matrixBuffer );
        }
    }
    
    private static final VBOMode getTexCoordVBOMode( Optimization optimization )
    {
        switch ( optimization )
        {
            case USE_VBOS:
            case USE_VBO_FOR_TEXTURE_COORDINATES:
                return ( VBOMode.ALWAYS );
            case NONE:
            case USE_DISPLAY_LISTS:
            case USE_VBO_FOR_VERTEX_DATA:
                return ( VBOMode.NEVER );
            case AUTO:
            default:
                return ( VBOMode.AUTO );
        }
    }
    
    private static final VBOMode getGeomVBOMode( Optimization optimization )
    {
        switch ( optimization )
        {
            case USE_VBOS:
            case USE_VBO_FOR_VERTEX_DATA:
                return ( VBOMode.ALWAYS );
            case NONE:
            case USE_DISPLAY_LISTS:
            case USE_VBO_FOR_TEXTURE_COORDINATES:
                return ( VBOMode.NEVER );
            case AUTO:
            default:
                return ( VBOMode.AUTO );
        }
    }
    
    private static final Boolean vboModeToBool( VBOMode vboMode, GeomNioData data )
    {
        switch ( vboMode )
        {
            case ALWAYS:
                return ( Boolean.TRUE );
            case NEVER:
                return ( Boolean.FALSE );
            case AUTO:
            default:
                return ( _SG_PrivilegedAccess.getFramesSinceDirty( data ) >= OPT_AUTO_MAX_FRAMES );
        }
    }
    
    private static final void bindArrayVBO( GL gl, int vbo, OpenGLStatesCache statesCache, boolean glVBOsSupported, boolean arbVBOsSupported )
    {
        if ( ( statesCache.enabled && statesCache.currentBoundArrayVBO == vbo ) || ( !glVBOsSupported && !arbVBOsSupported ) )
            return;
        
        if ( glVBOsSupported )
            gl.glBindBuffer( GL.GL_ARRAY_BUFFER, vbo );
        else
            gl.glBindBufferARB( GL.GL_ARRAY_BUFFER_ARB, vbo );
        
        statesCache.currentBoundArrayVBO = vbo;
    }
    
    private static final void bindIndexVBO( GL gl, int vbo, OpenGLStatesCache statesCache, boolean glVBOsSupported, boolean arbVBOsSupported )
    {
        if ( ( statesCache.enabled && statesCache.currentBoundElementVBO == vbo ) || ( !glVBOsSupported && !arbVBOsSupported ) )
            return;
        
        if ( glVBOsSupported )
            gl.glBindBuffer( GL.GL_ELEMENT_ARRAY_BUFFER, vbo );
        else
            gl.glBindBufferARB( GL.GL_ELEMENT_ARRAY_BUFFER_ARB, vbo );
        
        statesCache.currentBoundElementVBO = vbo;
    }
    
    private static final void createAndBindVBO( GL gl, CanvasPeer canvasPeer, GeomNioData data, int arrayType, OpenGLStatesCache statesCache, boolean glVBOsSupported, boolean arbVBOsSupported )
    {
        final SceneGraphOpenGLReference openGLRef = ( data instanceof GeomNioFloatData ) ? ( (GeomNioFloatData)data ).getOpenGLReferences().getReference( canvasPeer, vboNameProvider ) : ( (GeomNioIntData)data ).getOpenGLReferences().getReference( canvasPeer, vboNameProvider );
        
        // if the data is not currently cached then get a cache handle
        int cacheHandle = openGLRef.getName();
        
        boolean newHandle = false;
        if ( cacheHandle == -1 )
        {
            tmpIntBuffer.clear();
            if ( glVBOsSupported )
                gl.glGenBuffers( 1, tmpIntBuffer );
            else if ( arbVBOsSupported )
                gl.glGenBuffersARB( 1, tmpIntBuffer );
            cacheHandle = tmpIntBuffer.get( 0 );
            openGLRef.setName( cacheHandle );
            newHandle = true;
        }
        
        // bind to the buffer
        if ( arrayType == 0 )
            bindIndexVBO( gl, cacheHandle, statesCache, glVBOsSupported, arbVBOsSupported );
        else
            bindArrayVBO( gl, cacheHandle, statesCache, glVBOsSupported, arbVBOsSupported );
        
        // if the data is dirty update the buffer
        
        if ( _SG_PrivilegedAccess.isDirty( data ) || newHandle )
        {
            if ( arrayType == 0 ) // index
            {
                final IntBuffer buffer = ((GeomNioIntData)data).getBuffer();
                buffer.rewind();
                
                if ( glVBOsSupported )
                    gl.glBufferData( GL.GL_ELEMENT_ARRAY_BUFFER, buffer.capacity() * 4, buffer, GL.GL_STATIC_DRAW );
                else if ( arbVBOsSupported )
                    gl.glBufferDataARB( GL.GL_ELEMENT_ARRAY_BUFFER_ARB, buffer.capacity() * 4, buffer, GL.GL_STATIC_DRAW_ARB );
            }
            else
            {
                final FloatBuffer buffer = ((GeomNioFloatData)data).getBuffer();
                buffer.rewind();
                
                if ( glVBOsSupported )
                    gl.glBufferData( GL.GL_ARRAY_BUFFER, buffer.capacity() * 4, buffer, GL.GL_STATIC_DRAW );
                else if ( glVBOsSupported )
                    gl.glBufferDataARB( GL.GL_ARRAY_BUFFER_ARB, buffer.capacity() * 4, buffer, GL.GL_STATIC_DRAW_ARB );
            }
            _SG_PrivilegedAccess.setDirty( data, false );
            _SG_PrivilegedAccess.incrementFramesSinceDirty( data );
        }
    }
    
    private static final void bindVertexArray( GL gl, CanvasPeer canvasPeer, GeomNioData data, int elemSize, int stride, long offset, int index, int arrayType, OpenGLStatesCache statesCache, boolean glVBOsSupported, boolean arbVBOsSupported )
    {
        final SceneGraphOpenGLReference openGLRef = ( data instanceof GeomNioFloatData ) ? ( (GeomNioFloatData)data ).getOpenGLReferences().getReference( canvasPeer, vboNameProvider ) : ( (GeomNioIntData)data ).getOpenGLReferences().getReference( canvasPeer, vboNameProvider );
        
        int cacheHandle = openGLRef.getName();
        if ( cacheHandle != -1 )
        {
            tmpIntBuffer.clear();
            tmpIntBuffer.put( cacheHandle ).flip();
            if ( glVBOsSupported )
                gl.glDeleteBuffers( 1, tmpIntBuffer );
            else if ( arbVBOsSupported )
                gl.glDeleteBuffersARB( 1, tmpIntBuffer );
            cacheHandle = openGLRef.deleteName();
        }
        
        bindArrayVBO( gl, 0, statesCache, glVBOsSupported, arbVBOsSupported );
        bindIndexVBO( gl, 0, statesCache, glVBOsSupported, arbVBOsSupported );
        
        final FloatBuffer dataBuffer = ((GeomNioFloatData)data).getBuffer();
        dataBuffer.position( (int)( offset / 4L ) );
        
        switch ( arrayType )
        {
            case GL.GL_VERTEX_ARRAY:
                gl.glVertexPointer( elemSize, GL.GL_FLOAT, stride, dataBuffer );
                break;
            case GL.GL_NORMAL_ARRAY:
                gl.glNormalPointer( GL.GL_FLOAT, stride, dataBuffer );
                break;
            case GL.GL_COLOR_ARRAY:
                gl.glColorPointer( elemSize, GL.GL_FLOAT, stride, dataBuffer );
                break;
            case GL.GL_TEXTURE_COORD_ARRAY:
                gl.glTexCoordPointer( elemSize, GL.GL_FLOAT, stride, dataBuffer );
                break;
            case GL.GL_VERTEX_ATTRIB_ARRAY_POINTER:
                gl.glVertexAttribPointer( index, elemSize, GL.GL_FLOAT, false, stride, dataBuffer );
                break;
        }
        
        _SG_PrivilegedAccess.setDirty( data, false );
        _SG_PrivilegedAccess.incrementFramesSinceDirty( data );
    }
    
    /**
     * binds the geometry component to the proper vertex array and uses caching
     * if enabled and if the caching policy of the data deems it appropriate
     * 
     * @return true, if a VBO is used
     */
    private static final boolean bindGeometryComponent( GL gl, CanvasPeer canvasPeer, OpenGLStatesCache statesCache, GeomNioData data, int elemSize, int stride, long offset, int index, int arrayType, VBOMode vboMode, Boolean useVBO, boolean glVBOsSupported, boolean arbVBOsSupported )
    {
        if ( useVBO == null )
        {
            useVBO = vboModeToBool( vboMode, data );
        }
        
        useVBO = useVBO.booleanValue() && ( glVBOsSupported || arbVBOsSupported );
        
        if ( useVBO.booleanValue() )
        {
            if ( data != null )
            {
                createAndBindVBO( gl, canvasPeer, data, arrayType, statesCache, glVBOsSupported, arbVBOsSupported );
            }
            
            // set the data pointer
            switch ( arrayType )
            {
                case GL.GL_VERTEX_ARRAY:
                    gl.glVertexPointer( elemSize, GL.GL_FLOAT, stride, offset );
                    break;
                case GL.GL_NORMAL_ARRAY:
                    gl.glNormalPointer( GL.GL_FLOAT, stride, offset );
                    break;
                case GL.GL_COLOR_ARRAY:
                    gl.glColorPointer( elemSize, GL.GL_FLOAT, stride, offset );
                    break;
                case GL.GL_TEXTURE_COORD_ARRAY:
                    gl.glTexCoordPointer( elemSize, GL.GL_FLOAT, stride, offset );
                    break;
                case GL.GL_VERTEX_ATTRIB_ARRAY_POINTER:
                    gl.glVertexAttribPointer( index, elemSize, GL.GL_FLOAT, false, stride, offset );
                    break;
            }
        }
        else
        {
            if ( data instanceof GeomNioFloatData )
                bindVertexArray( gl, canvasPeer, data, elemSize, stride, offset, index, arrayType, statesCache, glVBOsSupported, arbVBOsSupported );
        }
        
        return ( useVBO.booleanValue() );
    }
    
    private static final int setupBuffers( GL gl, CanvasPeer canvasPeer, OpenGLStatesCache statesCache, OpenGLCapabilities glCaps, Geometry geoArray, Optimization optimization, int texturesUseMap, boolean glVBOsSupported, boolean arbVBOsSupported )
    {
        final VBOMode geomVBOMode = getGeomVBOMode( optimization );
        final Boolean useVBO;
        
        final int stride;
        if ( geoArray.isInterleaved() )
        {
            useVBO = vboModeToBool( geomVBOMode, geoArray.getInterleavedData() ) && ( glVBOsSupported || arbVBOsSupported );
            stride = geoArray.getInterleavedData().getStride();
            if ( useVBO )
                createAndBindVBO( gl, canvasPeer, geoArray.getInterleavedData(), -1, statesCache, glVBOsSupported, arbVBOsSupported );
        }
        else
        {
            useVBO = null; // undefined at this point
            stride = 0;
        }
        
        int vboMap = 0;
        
        // Get the normal data.
        // If the data is cached, but dirty then then disable cache.
        if ( geoArray.hasNormals() )
        {
            if ( bindGeometryComponent( gl, canvasPeer, statesCache, geoArray.getNormalsData(), geoArray.getNormalsSize(), stride, geoArray.getNormalsOffset(), -1, GL.GL_NORMAL_ARRAY, geomVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
                vboMap |= CARE_MAP_NORMALS;
        }
        
        // Get the color data.
        // If the coordinate data is cached, but dirty then then disable cache.
        if ( geoArray.hasColors() )
        {
            if ( bindGeometryComponent( gl, canvasPeer, statesCache, geoArray.getColorData(), geoArray.getColorsSize(), stride, geoArray.getColorsOffset(), -1, GL.GL_COLOR_ARRAY, geomVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
                vboMap |= CARE_MAP_COLORS;
        }
        
        if ( texturesUseMap != 0 )
        {
            final VBOMode texCoordVBOMode = geoArray.isInterleaved() ? geomVBOMode : getTexCoordVBOMode( optimization );
            
            final int maxTUs = glCaps.getMaxTextureUnits();
            
            int maskValue = 1;
            for ( int unit = 0; unit < maxTUs; unit++ )
            {
                if ( ( texturesUseMap & maskValue ) != 0 )
                {
                    selectClientTextureUnit( gl, unit, statesCache, false );
                    
                    final GeomNioFloatData texCoords = geoArray.getTexCoordsData( unit );
                    
                    if ( geoArray.isInterleaved() )
                    {
                        final int texCoordSize = geoArray.getTexCoordSize( unit );
                        final long offset = geoArray.getTexCoordsOffset( unit );
                        
                        if ( bindGeometryComponent( gl, canvasPeer, statesCache, texCoords, texCoordSize, stride, offset, unit, GL.GL_TEXTURE_COORD_ARRAY, texCoordVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
                            vboMap |= CARE_MAP_TEXTURE_COORDS;
                    }
                    else
                    {
                        if ( bindGeometryComponent( gl, canvasPeer, statesCache, texCoords, texCoords.getElemSize(), 0, 0L, unit, GL.GL_TEXTURE_COORD_ARRAY, texCoordVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
                            vboMap |= CARE_MAP_TEXTURE_COORDS;
                    }
                }
                
                maskValue *= 2;
            }
        }
        
        // Get the vertex attributes.
        if ( OpenGlExtensions.GL_CUSTOM_VERTEX_ATTRIBUTES && geoArray.hasVertexAttributes() )
        {
            final int n = Math.min( geoArray.getVertexAttributesCount(), glCaps.getMaxVertexAttributes() );
            for ( int i = 0; i < n; i++ )
            {
                if ( geoArray.hasVertexAttributes( i ) )
                {
                    final GeomNioFloatData attribData = geoArray.getVertexAttribData( i );
                    
                    if ( geoArray.isInterleaved() )
                    {
                        final int attribSize = geoArray.getVertexAttribSize( i );
                        final long offset = geoArray.getVertexAttribsOffset( i );
                        
                        if ( bindGeometryComponent( gl, canvasPeer, statesCache, attribData, attribSize, stride, offset, i, GL.GL_VERTEX_ATTRIB_ARRAY_POINTER, geomVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
                            vboMap |= CARE_MAP_VERTEX_ATTRIBS;
                    }
                    else
                    {
                        if ( bindGeometryComponent( gl, canvasPeer, statesCache, attribData, attribData.getElemSize(), 0, 0L, i, GL.GL_VERTEX_ATTRIB_ARRAY_POINTER, geomVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
                            vboMap |= CARE_MAP_VERTEX_ATTRIBS;
                    }
                }
            }
        }
        
        // Get the coordinate data.
        // If the coordinate data is cached, but dirty then then disable cache.
        if ( bindGeometryComponent( gl, canvasPeer, statesCache, geoArray.getCoordinatesData(), geoArray.getCoordinatesSize(), stride, geoArray.getCoordinatesOffset(), -1, GL.GL_VERTEX_ARRAY, geomVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
            vboMap |= CARE_MAP_COORDINATES;
        
        // Get the coordinate data.
        // If the coordinate data is cached, but dirty then then disable cache.
        if ( geoArray.hasIndex() && !geoArray.isInterleaved() )
        {
            if ( bindGeometryComponent( gl, canvasPeer, statesCache, ((IndexedGeometryArray)geoArray).getIndexData(), 1, 0, 0L, -1, 0, geomVBOMode, useVBO, glVBOsSupported, arbVBOsSupported ) )
                vboMap |= CARE_MAP_INDICES;
        }
        
        return ( vboMap );
    }
    
    public static final int getNumTriangles( Geometry geoArray )
    {
        if ( geoArray.hasIndex() )
            return ( (IndexedGeometryArray)geoArray ).getValidIndexCount() / geoArray.getFaceSize();
        
        return ( geoArray.getValidVertexCount() / geoArray.getFaceSize() );
    }
    
    /**
     * Draws the geometry. At this point the colors and textures and texture
     * coordinates should be set prior to rendering.
     * 
     * @param gl
     * @param geoArray
     * @param useIndexVBO
     * @param isInDisplayList
     * 
     * @return the number of rendered triangles
     */
    protected static final int drawBuffers( GL gl, Geometry geoArray, boolean useIndexVBO, boolean isInDisplayList, boolean isMinVersion13 )
    {
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "ShapeAtomPeer::drawGeometry()" );
        
        final int numVertices = geoArray.getValidVertexCount();
        if ( numVertices == 0 )
        {
            X3DLog.exception( "Skipping because there are no valid vertices " );
            ProfileTimer.endProfile();
            
            return ( 0 );
        }
        
        final int mode = geoArray.getType().toOpenGL();
        if ( mode == -1 )
        {
            X3DLog.exception( "Skipping unknown geometry : ", geoArray.getClass().getName() );
            ProfileTimer.endProfile();
            
            return ( 0 ); // don't know how to draw this
        }
        
        final int triangles;
        
        // do the actual drawing genericly
        
        if ( geoArray.isStrip() )
        {
            if ( geoArray.hasIndex() )
            {
                final IndexedGeometryStripArray igsa = (IndexedGeometryStripArray)geoArray;
                final int[] stripVertexCounts = igsa.getStripVertexCounts();
                
                if ( isInDisplayList )
                {
                    DisplayListRenderPeer.drawIndexedGeometryStripArray( gl, igsa, mode, igsa.getNumStrips(), stripVertexCounts, isMinVersion13 );
                }
                else
                {
                    //final IntBuffer indexBuffer = igsa.getIndexData().getIntBuffer();
                    
                    final int startIndex = igsa.getInitialIndexIndex();
                    final int numIndices = igsa.getValidIndexCount();
                    
                    int i0 = 0;
                    for ( int s = 0; s < stripVertexCounts.length; s++ )
                    {
                        if ( useIndexVBO )
                        {
                            final int start = ( i0 >= startIndex ) ? i0 : startIndex;
                            
                            int end = start + stripVertexCounts[ s ] - 1;
                            if ( end + 1 > numIndices )
                                end = numIndices - 1;
                            
                            if ( end < start )
                                break;
                            
                            gl.glDrawRangeElements( mode, start, end, end - start + 1, GL.GL_UNSIGNED_INT, 0L );
                        }
                        else
                        {
                            IntBuffer buffer = igsa.getIndexData().getBuffer();
                            int position = buffer.position();
                            int limit = buffer.limit();
                            buffer.position( i0 );
                            buffer.limit( i0 + stripVertexCounts[ s ] );
                            gl.glDrawElements( mode, stripVertexCounts[ s ], GL.GL_UNSIGNED_INT, buffer );
                            buffer.limit( limit );
                            buffer.position( position );
                        }
                        
                        i0 += stripVertexCounts[ s ];
                    }
                }
                
                triangles = igsa.getValidIndexCount() / geoArray.getFaceSize();
            }
            else
            {
                final GeometryStripArray geoStripArr = (GeometryStripArray)geoArray;
                
                final int stripCount = geoStripArr.getNumStrips();
                final int[] stripVertexCounts = geoStripArr.getStripVertexCounts();
                
                if ( isInDisplayList )
                {
                    DisplayListRenderPeer.drawGeometryStripArray( gl, geoStripArr, mode, stripCount, stripVertexCounts, isMinVersion13 );
                }
                else
                {
                    final int startIndex = geoStripArr.getInitialVertexIndex();
                    final int numIndices = geoStripArr.getValidVertexCount();
                    
                    // loop through the strips
                    int i0 = 0;
                    for ( int strip = 0; strip < stripCount; strip++ )
                    {
                        final int start = ( i0 >= startIndex ) ? i0 : startIndex;
                        
                        int end = start + stripVertexCounts[ strip ] - 1;
                        if ( end + 1 > numIndices )
                            end = numIndices - 1;
                        
                        if ( end < start )
                            break;
                        
                        gl.glDrawArrays( mode, start, end - start + 1 );
                        
                        i0 += stripVertexCounts[ strip ];
                    }
                }
                
                triangles = geoArray.getValidVertexCount() / geoArray.getFaceSize();
            }
        }
        else if ( geoArray.hasIndex() )
        {
            final IndexedGeometryArray igeoArray = (IndexedGeometryArray)geoArray;
            
            if ( isInDisplayList )
            {
                DisplayListRenderPeer.drawIndexedGeometryArray( gl, igeoArray, mode, isMinVersion13 );
            }
            else
            {
                final IntBuffer buffer = igeoArray.getIndexData().getBuffer();
                final int startIndex = igeoArray.getInitialIndexIndex();
                final int numIndices = igeoArray.getValidIndexCount();
                
                if ( useIndexVBO )
                    gl.glDrawRangeElements( mode, startIndex, startIndex + numIndices - 1, numIndices, GL.GL_UNSIGNED_INT, 0L );
                else
                    gl.glDrawElements( mode, numIndices, GL.GL_UNSIGNED_INT, buffer );
            }
            
            triangles = igeoArray.getValidIndexCount() / geoArray.getFaceSize();
        }
        else
        {
            if ( isInDisplayList )
            {
                DisplayListRenderPeer.drawRegularGeometryArray( gl, geoArray, mode, isMinVersion13 );
            }
            else
            {
                gl.glDrawArrays( mode, geoArray.getInitialVertexIndex(), numVertices );
            }
            
            triangles = geoArray.getValidVertexCount() / geoArray.getFaceSize();
        }
        
        ProfileTimer.endProfile();
        
        return ( triangles );
    }
    
    public static final void selectClientTextureUnit( GL gl, int unit, OpenGLStatesCache statesCache, boolean force )
    {
        if ( ( statesCache.enabled && statesCache.currentClientTextureUnit == unit ) && !force )
            return;
        
        X3DLog.debug( "Activating (client) texture unit ", unit );
        
        final int glUnit = GL.GL_TEXTURE0 + unit;
        
        gl.glClientActiveTexture( glUnit );
        
        statesCache.currentClientTextureUnit = unit;
    }
    
    /**
     * Sets the states, if the desired ones differ from the cached ones.
     * 
     * @param gl
     * @param geoArray
     * @param glCaps
     * @param statesCache
     * @param useTextures
     * @param careMap
     * 
     * @return a a bist-maks indicating, which texture units are being used
     */
    protected static final int setStates( GL gl, Geometry geoArray, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache, boolean useTextures, int careMap )
    {
        if ( ( careMap & CARE_MAP_COORDINATES ) != 0 )
        {
            if ( !statesCache.enabled || !statesCache.coordsArrayEnabled )
            {
                gl.glEnableClientState( GL.GL_VERTEX_ARRAY );
                statesCache.coordsArrayEnabled = true;
            }
        }
        
        
        if ( ( careMap & CARE_MAP_NORMALS ) != 0 )
        {
            if ( geoArray.hasNormals() )
            {
                if ( !statesCache.enabled || !statesCache.normalsArrayEnabled )
                {
                    gl.glEnableClientState( GL.GL_NORMAL_ARRAY );
                    statesCache.normalsArrayEnabled = true;
                }
            }
            else if ( !statesCache.enabled || statesCache.normalsArrayEnabled )
            {
                gl.glDisableClientState( GL.GL_NORMAL_ARRAY );
                statesCache.normalsArrayEnabled = false;
            }
        }
        
        
        if ( ( careMap & CARE_MAP_COLORS ) != 0 )
        {
            if ( geoArray.hasColors() )
            {
                if ( !statesCache.enabled || !statesCache.colorsArrayEnabled )
                {
                    gl.glEnableClientState( GL.GL_COLOR_ARRAY );
                    statesCache.colorsArrayEnabled = true;
                }
            }
            else if ( !statesCache.enabled || statesCache.colorsArrayEnabled )
            {
                gl.glDisableClientState( GL.GL_COLOR_ARRAY );
                statesCache.colorsArrayEnabled = false;
            }
        }
        
        
        int result_texturesUseMap = 0;
        
        if ( ( careMap & CARE_MAP_TEXTURE_COORDS ) != 0 )
        {
            final int maxTUs = glCaps.getMaxTextureUnits();
            
            int maskValue = 1;
            for ( int i = 0; i < maxTUs; i++ )
            {
                final boolean hasTextureUnit = ( geoArray.getTexCoordSize( i ) > 0 );
                
                if ( useTextures && hasTextureUnit )
                {
                    if ( !statesCache.enabled || ( statesCache.texCoordArraysEnableMask & maskValue ) == 0 )
                    {
                        selectClientTextureUnit( gl, i, statesCache, false );
                        
                        gl.glEnableClientState( GL.GL_TEXTURE_COORD_ARRAY );
                        statesCache.texCoordArraysEnableMask |= maskValue;
                    }
                    
                    result_texturesUseMap |= maskValue;
                }
                else
                {
                    if ( !statesCache.enabled || ( statesCache.texCoordArraysEnableMask & maskValue ) != 0 )
                    {
                        selectClientTextureUnit( gl, i, statesCache, false );
                        
                        gl.glDisableClientState( GL.GL_TEXTURE_COORD_ARRAY );
                        statesCache.texCoordArraysEnableMask &= ~maskValue;
                    }
                }
                
                maskValue *= 2;
            }
        }
        
        
        if ( ( ( careMap & CARE_MAP_VERTEX_ATTRIBS ) != 0 ) && OpenGlExtensions.GL_CUSTOM_VERTEX_ATTRIBUTES && ( geoArray.hasVertexAttributes() || ( !statesCache.enabled || statesCache.maxUsedVertexAttrib > 0 ) ) )
        {
            int maskValue = 1;
            final int maxVertexAttribs = glCaps.getMaxVertexAttributes();
            for ( int i = 0; i < maxVertexAttribs; i++ )
            {
                if ( geoArray.hasVertexAttributes( i ) )
                {
                    if ( !statesCache.enabled || ( statesCache.vertexAttribsEnableMask & maskValue ) == 0 )
                    {
                        gl.glEnableVertexAttribArray( i );
                        statesCache.vertexAttribsEnableMask |= maskValue;
                    }
                    
                    if ( !statesCache.enabled || i > statesCache.maxUsedVertexAttrib )
                        statesCache.maxUsedVertexAttrib = i;
                }
                else if ( !statesCache.enabled || ( statesCache.vertexAttribsEnableMask & maskValue ) != 0 )
                {
                    gl.glDisableVertexAttribArray( i );
                    statesCache.vertexAttribsEnableMask &= ~maskValue;
                }
                
                maskValue *= 2;
            }
        }
        
        
        return ( result_texturesUseMap );
    }
    
    private static final int renderNoDisplayLists( GL gl, ShapeAtom shapeAtom, int texturesUseMap, Geometry geoArray, Optimization optimization, CanvasPeer canvasPeer, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache, boolean glVBOsSupported, boolean arbVBOsSupported )
    {
        final int vboMap = setupBuffers( gl, canvasPeer, statesCache, glCaps, geoArray, optimization, texturesUseMap, glVBOsSupported, arbVBOsSupported );
        
        final boolean vboForIndex = ( ( vboMap & CARE_MAP_INDICES ) != 0 );
        shapeAtom.lastComputedPolysCount = drawBuffers( gl, geoArray, vboForIndex, false, glCaps.isMinVersion13() );
        
        return ( shapeAtom.lastComputedPolysCount );
    }
    
    protected static final int renderWithForcedVertexArrays( GL gl, ShapeAtom shapeAtom, int texturesUseMap, Geometry geoArray, CanvasPeer canvasPeer, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache )
    {
        //final Geometry geometry = ( (Shape3D)shapeAtom.getNode() ).getGeometry();
        //final Appearance app = ( (Shape3D)shapeAtom.getNode() ).getAppearance();
        final Optimization optimization = Optimization.NONE;
        
        return ( renderNoDisplayLists( gl, shapeAtom, texturesUseMap, geoArray, optimization, canvasPeer, glCaps, statesCache, false, false ) );
    }
    
    private static final int render( GL gl, ShapeAtom shapeAtom, Geometry geometry, Geometry geoArray, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLInfo glInfo, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache, RenderOptions options, boolean isNormalRenderMode )
    {
        final boolean glVBOsSupported = glCaps.isMinVersion15() && glCaps.supportsVBOs() && options.areVBOsEnabled();
        final boolean arbVBOsSupported = OpenGlExtensions.ARB_vertex_buffer_object && glCaps.supportsVBOs() && options.areVBOsEnabled();
        
        Optimization optimization = geometry.getOptimization();
        
        if ( optimization == Geometry.Optimization.USE_DISPLAY_LISTS )
        {
            if ( options.areDisplayListsEnabled() )
            {
                bindArrayVBO( gl, 0, statesCache, glVBOsSupported, arbVBOsSupported );
                bindIndexVBO( gl, 0, statesCache, glVBOsSupported, arbVBOsSupported );
                
                // completely bypass states management for DisplayLists!
                DisplayListRenderPeer.renderDisplayList( gl, shapeAtom, geoArray, canvasPeer, glCaps, statesCache, isNormalRenderMode );
                
                return ( shapeAtom.lastComputedPolysCount );
            }
            
            optimization = Optimization.USE_VBOS;
        }
        
        final int texturesUseMap = setStates( gl, geoArray, glCaps, statesCache, isNormalRenderMode, CARE_MAP_ALL );
        
        return ( renderNoDisplayLists( gl, shapeAtom, texturesUseMap, geoArray, optimization, canvasPeer, glCaps, statesCache, glVBOsSupported, arbVBOsSupported ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int renderAtom( RenderAtom< ? > atom, Object glObj, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        final GL gl = (GL)glObj;
        final CanvasPeer canvasPeer = renderPeer.getCanvasPeer();
        //final int canvasID = renderPeer.getCanvasPeer().getCanvasID();
        final OpenGLStatesCache statesCache = renderPeer.getStatesCache();
        final OpenGLInfo glInfo = canvasPeer.getOpenGLInfo();
        
        final ShapeAtom shapeAtom = (ShapeAtom)atom;
        
        final Shape3D shape = (Shape3D)shapeAtom.getNode();
        final String shapeName = shape.getName();
        final boolean hasName = ( ( shapeName != null ) && ( shapeName.length() > 0 ) );
        if ( hasName && ProfileTimer.isProfilingEnabled() )
        {
            ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, shapeName );
        }
        
        final Geometry geometry = shape.getGeometry();
        if ( geometry == null )
        {
            X3DLog.exception( "Shape has no geometry, so skipping : ", shape, " : ", shapeName );
            return ( 0 );
        }
        
        final Appearance app = shape.getAppearance();
        final boolean isBillboard = ( shape instanceof Billboard );
        
        setMatrix( gl, view, _SG_PrivilegedAccess.getLeafWorldTransform( shape ), isBillboard, false );
        
        if ( shape.getShowBounds() && ( renderMode == RenderMode.NORMAL ) )
            BoundsAtomPeer.drawBounds( gl, shape.getBounds(), null, statesCache );
        
        int triangles = 0;
        
        if ( ( app != null ) && ( app.getPolygonAttributes() != null ) && ( app.getPolygonAttributes().getFaceCullMode() == FaceCullMode.SWITCH ) )
        {
            tmpIntBuffer.clear();
            gl.glGetIntegerv( GL.GL_CULL_FACE_MODE, tmpIntBuffer );
            final int polyMode = tmpIntBuffer.get();
            final boolean wasPolyCullEnabled = PolygonAttribsStateUnitPeer.setCullMode( gl, statesCache, PolygonAttributes.CULL_BACK, true, renderMode == RenderMode.PICKING );
            triangles = render( gl, shapeAtom, geometry, geometry, canvasPeer, renderPeer, glInfo, glCaps, statesCache, options, renderMode == RenderMode.NORMAL );
            PolygonAttribsStateUnitPeer.setCullMode( gl, statesCache, PolygonAttributes.CULL_FRONT, false, renderMode == RenderMode.PICKING );
            triangles = render( gl, shapeAtom, geometry, geometry, canvasPeer, renderPeer, glInfo, glCaps, statesCache, options, renderMode == RenderMode.NORMAL );
            
            PolygonAttribsStateUnitPeer.setCullMode( gl, statesCache, polyMode, wasPolyCullEnabled, renderMode == RenderMode.PICKING );
        }
        else
        {
            triangles = render( gl, shapeAtom, geometry, geometry, canvasPeer, renderPeer, glInfo, glCaps, statesCache, options, renderMode == RenderMode.NORMAL );
        }
        
        // since vertex colors can change the current color and
        // destroy the shader's known state we must restore them.
        if ( ( renderMode == RenderMode.NORMAL ) && geometry.hasColors() )
        {
            gl.glColor4f( statesCache.color.getRed(), statesCache.color.getGreen(), statesCache.color.getBlue(), 1f - statesCache.color.getAlpha() );
        }
        
        if ( hasName && ProfileTimer.isProfilingEnabled() )
        {
            ProfileTimer.endProfile();
        }
        
        return ( triangles );
    }
}
