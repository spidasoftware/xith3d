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
package org.xith3d.render.lwjgl;

import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord1f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.TexCoord3f;
import org.openmali.vecmath2.TexCoord4f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.SceneGraphOpenGLReference;
import org.xith3d.render.SceneGraphOpenGLReferences;
import org.xith3d.render.preprocessing.ShapeAtom;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.GeometryStripArray;
import org.xith3d.scenegraph.IndexedGeometryArray;
import org.xith3d.scenegraph.IndexedGeometryStripArray;

/**
 * This shaderPeer is capable of recording and playing back a DisplayList.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DisplayListRenderPeer
{
    private static final boolean USE_PRECOMPILED_DISPLAY_LISTS = true;
    
    private static final SceneGraphOpenGLReferences.Provider dlNameProvider = new SceneGraphOpenGLReferences.Provider()
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
                    GL11.glDeleteLists( name, 1 );
                }
            } );
        }
    };
    
    private static Point3f coord = new Point3f();
    private static Vector3f normal = new Vector3f();
    private static Colorf color = new Colorf();
    private static TexCoord1f texCoord1 = new TexCoord1f();
    private static TexCoord2f texCoord2 = new TexCoord2f();
    private static TexCoord3f texCoord3 = new TexCoord3f();
    private static TexCoord4f texCoord4 = new TexCoord4f();
    
    private static final void drawGeometryArray( Geometry geom,
                                                 final int[] index,
                                                 final int startIndex,
                                                 final int endIndex,
                                                 final int mode,
                                                 final boolean isMinVersion13
                                               )
    {
        GL11.glBegin( mode );
        
        for ( int i = startIndex; i <= endIndex; i++ )
        {
            final int i_ = ( index == null ) ? i : index[ i ];
            
            if ( geom.hasNormals() )
            {
                geom.getNormal( i_, normal );
                GL11.glNormal3f( normal.getX(), normal.getY(), normal.getZ() );
            }
            
            if ( geom.hasColors() )
            {
                geom.getColor( i_, color );
                if ( color.hasAlpha() )
                    GL11.glColor4f( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
                else
                    GL11.glColor3f( color.getRed(), color.getGreen(), color.getBlue() );
            }
            
            final int[] tuSetMap = geom.getTexCoordSetMap();
            for ( int t = 0; t < tuSetMap.length; t++ )
            {
                final int unit = tuSetMap[ t ];
                
                switch ( geom.getTexCoordSize( unit ) ) // this will return 0, if the texture unit is not used!
                {
                    case 1:
                        geom.getTextureCoordinate( unit, i_, texCoord1 );
                        if ( isMinVersion13 )
                            GL13.glMultiTexCoord1f( unit, texCoord1.getS() );
                        else
                            ARBMultitexture.glMultiTexCoord1fARB( unit, texCoord1.getS() );
                        break;
                    case 2:
                        geom.getTextureCoordinate( unit, i_, texCoord2 );
                        if ( isMinVersion13 )
                            GL13.glMultiTexCoord2f( unit, texCoord2.getS(), texCoord2.getT() );
                        else
                            ARBMultitexture.glMultiTexCoord2fARB( unit, texCoord2.getS(), texCoord2.getT() );
                        break;
                    case 3:
                        geom.getTextureCoordinate( unit, i_, texCoord3 );
                        if ( isMinVersion13 )
                            GL13.glMultiTexCoord3f( unit, texCoord3.getS(), texCoord3.getT(), texCoord3.getP() );
                        else
                            ARBMultitexture.glMultiTexCoord3fARB( unit, texCoord3.getS(), texCoord3.getT(), texCoord3.getP() );
                        break;
                    case 4:
                        geom.getTextureCoordinate( unit, i_, texCoord4 );
                        if ( isMinVersion13 )
                            GL13.glMultiTexCoord4f( unit, texCoord4.getS(), texCoord4.getT(), texCoord4.getP(), texCoord4.getQ() );
                        else
                            ARBMultitexture.glMultiTexCoord4fARB( unit, texCoord4.getS(), texCoord4.getT(), texCoord4.getP(), texCoord4.getQ() );
                        break;
                }
            }
            
            geom.getCoordinate( i_, coord );
            GL11.glVertex3f( coord.getX(), coord.getY(), coord.getZ() );
        }
        
        GL11.glEnd();
    }
    
    public static final void drawRegularGeometryArray( Geometry geom, final int mode, final boolean isMinVersion13 )
    {
        final int[] index = null;
        final int startIndex = geom.getInitialVertexIndex();
        final int numIndices = geom.getValidVertexCount();
        
        drawGeometryArray( geom, index, startIndex, startIndex + numIndices - 1, mode, isMinVersion13 );
    }
    
    public static final void drawIndexedGeometryArray( IndexedGeometryArray geom, final int mode, final boolean isMinVersion13 )
    {
        final int[] index = geom.getIndex();
        final int startIndex = geom.getInitialIndexIndex();
        final int numIndices = geom.getValidIndexCount();
        
        drawGeometryArray( geom, index, startIndex, startIndex + numIndices - 1, mode, isMinVersion13 );
    }
    
    public static final void drawGeometryStripArray( GeometryStripArray geom, final int mode, final int stripsCount, final int[] stripVertexCounts, final boolean isMinVersion13 )
    {
        final int startIndex = geom.getInitialVertexIndex();
        final int numIndices = geom.getValidVertexCount();
        
        int i0 = 0;
        for ( int strip = 0; strip < stripsCount; strip++ )
        {
            final int start = ( i0 >= startIndex ) ? i0 : startIndex;
            
            int end = start + stripVertexCounts[ strip ] - 1;
            if ( end + 1 > numIndices )
                end = numIndices - 1;
            
            if ( end < start )
                break;
            
            drawGeometryArray( geom, null, start, end, mode, isMinVersion13 );
            i0 += stripVertexCounts[ strip ];
        }
    }
    
    public static final void drawIndexedGeometryStripArray( IndexedGeometryStripArray geom, final int mode, final int stripsCount, final int[] stripVertexCounts, final boolean isMinVersion13 )
    {
        final int[] index = geom.getIndex();
        final int startIndex = geom.getInitialIndexIndex();
        final int numIndices = geom.getValidIndexCount();
        
        int i0 = 0;
        for ( int strip = 0; strip < stripsCount; strip++ )
        {
            final int start = ( i0 >= startIndex ) ? i0 : startIndex;
            
            int end = start + stripVertexCounts[ strip ] - 1;
            if ( end + 1 > numIndices )
                end = numIndices - 1;
            
            if ( end < start )
                break;
            
            drawGeometryArray( geom, index, start, end, mode, isMinVersion13 );
            i0 += stripVertexCounts[ strip ];
        }
    }
    
    private static final int recordDisplayList( ShapeAtom atom, Geometry geom, int dlName,
                                                boolean useVertexArrayWorkaround, int texturesUseMap,
                                                CanvasPeer canvasPeer, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache
                                              )
    {
        if ( dlName == -1 )
        {
            dlName = GL11.glGenLists( 1 );
        }
        
        // Don't use COMPILE_AND_EXECUTE! It is MUCH faster to first compile and then playback afterwards.
        if ( USE_PRECOMPILED_DISPLAY_LISTS )
            GL11.glNewList( dlName, GL11.GL_COMPILE );
        else
            GL11.glNewList( dlName, GL11.GL_COMPILE_AND_EXECUTE );
        
        if ( useVertexArrayWorkaround )
            ShapeAtomPeer.renderWithForcedVertexArrays( atom, texturesUseMap, geom, canvasPeer, glCaps, statesCache );
        else
            atom.lastComputedPolysCount = ShapeAtomPeer.drawBuffers( geom, false, true, glCaps.isMinVersion13() );
        
        GL11.glEndList();
        
        return ( dlName );
    }
    
    private static final void playbackDisplayList( final int dlName )
    {
        GL11.glCallList( dlName );
    }
    
    public static final void renderDisplayList( ShapeAtom atom, Geometry geom, CanvasPeer canvasPeer,
                                                OpenGLCapabilities glCaps, OpenGLStatesCache statesCache, boolean isNormalRenderMode
                                              )
    {
        SceneGraphOpenGLReference openGLRef = geom.getOpenGLReference_DL().getReference( canvasPeer, dlNameProvider );
        
        // ATI- and Intel-cards don't like plain-OpenGL-Multi-Texturing.
        final boolean useVertexArrayWorkaround = !glCaps.supportsPlainMultiTexturing() || geom.hasVertexAttributes();
        
        int texturesUseMap = 0;
        if ( useVertexArrayWorkaround )
        {
            texturesUseMap = ShapeAtomPeer.setStates( geom, glCaps, statesCache, isNormalRenderMode, ShapeAtomPeer.CARE_MAP_ALL );
        }
        
        if ( !openGLRef.nameExists() )
        {
            openGLRef.setName( recordDisplayList( atom, geom, -1, useVertexArrayWorkaround, texturesUseMap, canvasPeer, glCaps, statesCache ) );
        }
        else if ( !openGLRef.isNameValid() )
        {
            final int dlName = openGLRef.getName();
            recordDisplayList( atom, geom, dlName, useVertexArrayWorkaround, texturesUseMap, canvasPeer, glCaps, statesCache );
            openGLRef.setName( dlName );
            if ( USE_PRECOMPILED_DISPLAY_LISTS )
                playbackDisplayList( dlName );
        }
        else
        {
            final int dlName = openGLRef.getName();
            playbackDisplayList( dlName );
        }
    }
}
