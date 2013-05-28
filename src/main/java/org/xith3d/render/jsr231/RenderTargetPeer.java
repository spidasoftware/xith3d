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

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.types.twodee.Rect2i;
import org.openmali.vecmath2.Colorf;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.ImageRenderTarget;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderTarget;
import org.xith3d.render.SceneGraphOpenGLReference;
import org.xith3d.render.SceneGraphOpenGLReferences;
import org.xith3d.render.TextureRenderTarget;
import org.xith3d.scenegraph.Texture;

import com.sun.opengl.util.BufferUtil;

/**
 * This class handles the rendering of {@link RenderTarget}s.
 * 
 * @author Marvin Froehlich (aka Qudus)
 * @author Mathias Henze (aka cylab)
 */
public class RenderTargetPeer
{
    private final IntBuffer intBuffer = BufferUtil.newIntBuffer( 1 );
    
    private final RenderPeerImpl renderPeer;
    
    private final SceneGraphOpenGLReferences.Provider frameBufferNameProvider = new SceneGraphOpenGLReferences.Provider()
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
                    GL gl = ( (CanvasPeerImplBase)getContext() ).getGL();
                    
                    intBuffer.clear();
                    intBuffer.put( name ).rewind();
                    
                    if ( index == 0 )
                    {
                        gl.glDeleteFramebuffersEXT( 1, intBuffer );
                    }
                    else if ( index == 1 )
                    {
                        gl.glDeleteRenderbuffersEXT( 1, intBuffer );
                    }
                }
            } );
        }
    };
    
    private final void setupRenderTarget( GL gl, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache, CanvasPeer canvasPeer, TextureRenderTarget renderTarget )
    {
        final SceneGraphOpenGLReference openGLRef = renderTarget.getOpenGLReferences().getReference( canvasPeer, frameBufferNameProvider );
        
        final Texture texture = renderTarget.getTexture();
        final int texWidth = texture.getWidth();
        final int texHeight = texture.getHeight();
        
        if ( ( texWidth <= 0 ) || ( texHeight <= 0 ) )
        {
            throw new Error( "The Texture \"" + texture.getName() + "\" of TextureRenderTarget \"" + renderTarget + "\" doesn't seem to have an InputComponent." );
        }
        
        if ( !openGLRef.nameExists( 0 ) )
        {
            intBuffer.clear();
            gl.glGenFramebuffersEXT( 1, intBuffer );
            openGLRef.setName( 0, intBuffer.get( 0 ) );
        }
        
        final int fb = openGLRef.getName( 0 );
        
        gl.glBindFramebufferEXT( GL.GL_FRAMEBUFFER_EXT, fb );
        
        final int tex = TextureUnitStateUnitPeer.setTextureState2( gl, glCaps, statesCache, texture, statesCache.currentServerTextureUnit, texture.isChanged2(), canvasPeer, renderPeer.getCanvasPeer().getDepthBufferSize() );
        
        if ( texture.getFormat() == TextureFormat.DEPTH )
        {
            /*
            gl.glRenderbufferStorageEXT( GL.GL_RENDERBUFFER_EXT,
                                         TextureUnitStateUnitPeer.translateInternalFormat( texture.getFormat(), texture.getImage( 0 ).getInternalFormatHint(), renderPeer.getPeer().getDepthBufferSize() ),
                                         texWidth, texHeight
                                       );
            */
            gl.glFramebufferTexture2DEXT( GL.GL_FRAMEBUFFER_EXT,
                                          GL.GL_DEPTH_ATTACHMENT_EXT,
                                          GL.GL_TEXTURE_2D, tex, 0
                                        );
            // When no color attachment is used, the color buffer has to be disabled, to still render the depth texture!
            gl.glDrawBuffer( GL.GL_NONE );
            // cylab: dunno why this is used in some online resources, seems to have no effect...
            gl.glReadBuffer( GL.GL_NONE );
        }
        else
        {
            if ( !openGLRef.nameExists( 1 ) )
            {
                intBuffer.clear();
                gl.glGenRenderbuffersEXT( 1, intBuffer );
                openGLRef.setName( 1, intBuffer.get( 0 ) );
            }
            final int rb = openGLRef.getName( 1 );

            gl.glBindRenderbufferEXT( GL.GL_RENDERBUFFER_EXT, rb );

            gl.glRenderbufferStorageEXT( GL.GL_RENDERBUFFER_EXT, GL.GL_DEPTH_COMPONENT24,
                                         texWidth, texHeight
                                       );
            gl.glFramebufferRenderbufferEXT( GL.GL_FRAMEBUFFER_EXT,
                                             GL.GL_DEPTH_ATTACHMENT_EXT,
                                             GL.GL_RENDERBUFFER_EXT,
                                             rb
                                           );

            gl.glFramebufferTexture2DEXT( GL.GL_FRAMEBUFFER_EXT,
                                          GL.GL_COLOR_ATTACHMENT0_EXT,
                                          GL.GL_TEXTURE_2D,
                                          tex, 0
                                        );
        }
        
        gl.glPushAttrib( GL.GL_VIEWPORT_BIT );
        gl.glViewport( 0, 0, texWidth, texHeight );
        
        final Colorf bgCol = renderTarget.getBackgroundColor();
        if ( bgCol != null )
        {
            gl.glClearColor( bgCol.getRed(), bgCol.getGreen(), bgCol.getBlue(), 1f - bgCol.getAlpha() );
            gl.glClear( GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT | GL.GL_STENCIL_BUFFER_BIT );
            
            final float[] clearColor = renderPeer.getClearColor();
            gl.glClearColor( clearColor[ 0 ], clearColor[ 1 ], clearColor[ 2 ], clearColor[ 3 ] );
        }
    }
    
    /**
     * 
     * @param gl
     * @param renderTarget
     */
    private final void finishRenderTarget( GL gl, TextureRenderTarget renderTarget )
    {
        gl.glPopAttrib();
        // TODO: (cylab 07-11-18) should only be called to return to the normal rendering, not between framebuffers
        gl.glBindFramebufferEXT( GL.GL_FRAMEBUFFER_EXT, 0 );
    }
    
    private final void setupRenderTarget( ImageRenderTarget renderTarget )
    {
        final Rect2i viewport = ( (CanvasPeerImplBase)renderPeer.getCanvasPeer() ).getCurrentViewport();
        BufferedImage image = renderTarget.getImage();
        
        if ( ( image == null ) || ( image.getWidth() != viewport.getWidth() ) || ( image.getHeight() != viewport.getHeight() ) )
        {
            renderTarget.setImage( new BufferedImage( viewport.getWidth(), viewport.getHeight(), BufferedImage.TYPE_INT_ARGB ) );
        }
    }
    
    private final void finishRenderTarget( GL gl, ImageRenderTarget renderTarget )
    {
        final BufferedImage image = renderTarget.getImage();
        
        gl.glReadPixels( 0, 0, image.getWidth(), image.getHeight(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, renderTarget.getByteBuffer() );
        
        renderTarget.copyBufferToImage();
    }
    
    public final void setupRenderTarget( GL gl, OpenGLCapabilities glCaps, OpenGLStatesCache statesCache, CanvasPeer canvasPeer, RenderTarget renderTarget )
    {
        if ( renderTarget instanceof TextureRenderTarget )
        {
            setupRenderTarget( gl, glCaps, statesCache, canvasPeer, (TextureRenderTarget)renderTarget );
        }
        else if ( renderTarget instanceof ImageRenderTarget )
        {
            setupRenderTarget( (ImageRenderTarget)renderTarget );
        }
    }
    
    public final void finishRenderTarget( GL gl, RenderTarget renderTarget )
    {
        if ( renderTarget instanceof TextureRenderTarget )
        {
            finishRenderTarget( gl, (TextureRenderTarget)renderTarget );
        }
        else if ( renderTarget instanceof ImageRenderTarget )
        {
            finishRenderTarget( gl, (ImageRenderTarget)renderTarget );
        }
    }
    
    public RenderTargetPeer( RenderPeerImpl renderPeer )
    {
        this.renderPeer = renderPeer;
    }
}
