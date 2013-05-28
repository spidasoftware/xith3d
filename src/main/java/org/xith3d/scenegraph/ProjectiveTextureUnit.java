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

import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View;

/**
 * The {@link ProjectiveTextureUnit} class is a container class,
 * that holds everything needed for texture-projection.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ProjectiveTextureUnit extends TextureUnit
{
    private static final Transform3D createScaleAndBias()
    {
        Transform3D scaleAndBias = new Transform3D();
        
        scaleAndBias.getMatrix4f().set( 0.5f, 0.0f, 0.0f, 0.5f,
                                        0.0f, 0.5f, 0.0f, 0.5f,
                                        0.0f, 0.0f, 0.5f, 0.5f,
                                        0.0f, 0.0f, 0.0f, 1.0f
                                      );
        
        return ( scaleAndBias );
    }
    
    private final View projector;
    
    private static final Transform3D scaleAndBias = createScaleAndBias();
    private final Transform3D projectorProj = new Transform3D();
    
    private long lastFrameId = -1L;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTexture( Texture texture )
    {
        super.setTexture( texture );
        
        if ( texture != null )
        {
            texture.setBoundaryModeS( TextureBoundaryMode.CLAMP_TO_EDGE );
            texture.setBoundaryModeT( TextureBoundaryMode.CLAMP_TO_EDGE );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setTextureAttributes( TextureAttributes textureAttributes )
    {
        if ( textureAttributes == null )
        {
            throw new IllegalArgumentException( "A ProjectiveTextureUnit must not have null TextureAttributes" );
        }
        
        super.setTextureAttributes( textureAttributes );
        
        if ( textureAttributes.getTextureTransform() == null )
        {
            textureAttributes.setTextureTransform( new Transform3D() );
        }
    }
    
    public final View getProjector()
    {
        return ( projector );
    }
    
    public void update( float viewportAspect, long frameId )
    {
        if ( frameId <= lastFrameId )
            return;
        
        lastFrameId = frameId;
        
        // Extract projector's projection matrix.
        projectorProj.perspective( projector.getFieldOfView(),
                                   viewportAspect,
                                   projector.getFrontClipDistance(),
                                   projector.getBackClipDistance()
                                 );
        
        Transform3D textureTransform = getTextureAttributes().getTextureTransform();
        
        // Compose the Texture-Matrix...
        textureTransform.setIdentity();
        textureTransform.mul( scaleAndBias );
        textureTransform.mul( projectorProj );
        textureTransform.mul( projector.getModelViewTransform( true ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        /*
        final ProjectiveTextureUnit orgPTU = (ProjectiveTextureUnit)original;
        
        if ( forceDuplicate )
        {
            this.setProjector( new View() );
        }
        else
        {
            this.setProjector( orgPTU.getProjector() );
        }
        */
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected ProjectiveTextureUnit newInstance()
    {
        return ( new ProjectiveTextureUnit( this.getTexture() ) );
    }
    
    public ProjectiveTextureUnit( Texture texture )
    {
        super( texture, new TextureAttributes(), null );
        
        this.projector = new View();
    }
    
    public ProjectiveTextureUnit( String texture )
    {
        this( TextureLoader.getInstance().getTexture( texture ) );
    }
}
