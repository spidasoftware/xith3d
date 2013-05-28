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
package org.xith3d.render;

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Texture;

/**
 * This type of RenderTarget is used for render-to-texture.
 * Apply it to a RenderPass to bring it into effect.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureRenderTarget implements RenderTarget
{
    private GroupNode group;
    private Texture texture;
    private Colorf backgroundColor = null;
    private boolean backgroundRenderingEnabled = false;
    
    private final SceneGraphOpenGLReferences openGLReferences = new SceneGraphOpenGLReferences( 2 );
    
    public final SceneGraphOpenGLReferences getOpenGLReferences()
    {
        return ( openGLReferences );
    }
    
    /**
     * Sets the Group to be rendered to this RenderTarget.
     */
    public final void setGroup( GroupNode group )
    {
        if ( group == null )
            throw new NullPointerException( "group must not be null" );
        
        this.group = group;
    }
    
    /**
     * {@inheritDoc}
     */
    public final GroupNode getGroup()
    {
        return ( group );
    }
    
    /**
     * Sets the assotiated Texture instance.
     */
    public final void setTexture( Texture texture )
    {
        if ( texture == null )
            throw new NullPointerException( "texture must not be null" );
        
        this.texture = texture;
    }
    
    /**
     * @return the assotiated Texture instance.
     */
    public final Texture getTexture()
    {
        return ( texture );
    }
    
    /**
     * Sets the color, the texture is to be cleared before the Renderer renders to it.
     * Set this to <code>null</code> to do no clearing.
     */
    public final void setBackgroundColor( Colorf color )
    {
        this.backgroundColor = color;
    }
    
    /**
     * @return the color, the texture is to be cleared before the Renderer renders to it.
     * This is <code>null</code> to do no clearing.
     */
    public final Colorf getBackgroundColor()
    {
        return ( backgroundColor );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBackgroundRenderingEnabled( boolean enabled )
    {
        this.backgroundRenderingEnabled = enabled;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean isBackgroundRenderingEnabled()
    {
        return ( backgroundRenderingEnabled );
    }
    
    /**
     * {@inheritDoc}
     */
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( getTexture() != null )
            getTexture().freeOpenGLResources( canvasPeer );
        
        if ( getGroup() != null )
            getGroup().freeOpenGLResources( canvasPeer );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void freeOpenGLResources( Canvas3D canvas )
    {
        if ( canvas.getPeer() == null )
            throw new Error( "The given Canvas3D is not linked to a CanvasPeer." );
        
        freeOpenGLResources( canvas.getPeer() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize()
    {
        openGLReferences.prepareObjectForDestroy();
    }
    
    public TextureRenderTarget( GroupNode group, Texture texture, Colorf backgroundColor )
    {
        if ( group == null )
            throw new NullPointerException( "group must not be null" );
        
        if ( texture == null )
            throw new NullPointerException( "texture must not be null" );
        
        this.group = group;
        this.texture = texture;
        this.backgroundColor = backgroundColor;
    }
    
    public TextureRenderTarget( GroupNode group, Texture texture )
    {
        this( group, texture, null );
    }
}
