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
package org.xith3d.effects.shadows;

import java.io.IOException;

import org.jagatoo.opengl.enums.TexCoordGenMode;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TexCoordGeneration;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TextureUnit;

/**
 * This {@link ShadowFactory} realizes shadow-mapping through the
 * fixed-function pipeline of OpenGL.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FixedFuncShadowMappingFactory extends ShadowMappingFactory
{
    private static TexCoordGeneration texGen = null;
    
    /**
     * @return the (cached) {@link TexCoordGeneration} that calculates the
     * texture-coordinates for shadow-mapping.
     * 
     * @throws IOException
     */
    public static TexCoordGeneration getTexCoordGeneration()
    {
        if ( texGen != null )
            return ( texGen );
        
        texGen = new TexCoordGeneration( TexCoordGenMode.EYE_LINEAR,
                                         TexCoordGeneration.CoordMode.TEXTURE_COORDINATES_4
                                       );
        
        texGen.setPlaneR( 0f, 0f, 1f, 0f );
        texGen.setPlaneQ( 0f, 0f, 0f, 1f );
        
        return ( texGen );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setShadowSoftness( int softness )
    {
        super.setShadowSoftness( softness );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onShadowReceiverStateChanged( Shape3D shape, boolean isShadowReceiver )
    {
        if ( isShadowReceiver )
        {
            final Appearance app = shape.getAppearance( true );
            
            app.setTexture( getShadowTextureUnit(), getShadowMap() );
            
            TextureAttributes currTA = app.getTextureAttributes( getShadowTextureUnit() );
            if ( currTA == null )
            {
                app.setTextureAttributes( getShadowTextureUnit(), getShadowMapAttributes() );
            }
            else if ( currTA != getShadowMapAttributes() )
            {
                currTA.setCompareMode( getShadowMapAttributes().getCompareMode() );
                currTA.setCompareFunction( getShadowMapAttributes().getCompareFunction() );
            }
            
            app.setTexCoordGeneration( getShadowTextureUnit(), getTexCoordGeneration() );
        }
        else
        {
            final Appearance app = shape.getAppearance();
            if ( app != null )
            {
                TextureUnit tu = app.getTextureUnit( getShadowTextureUnit() );
                if ( tu != null )
                {
                    if ( ( tu.getTexture() == getShadowMap() ) && ( tu.getTextureAttributes() == this.getShadowMapAttributes() ) && ( tu.getTexCoordGeneration() == getTexCoordGeneration() ) )
                    {
                        app.setTextureUnit( getShadowTextureUnit(), null );
                    }
                    else
                    {
                        if ( tu.getTexture() == getShadowMap() )
                            tu.setTexture( (Texture)null );
                        if ( tu.getTextureAttributes() == getShadowMapAttributes() )
                            tu.setTextureAttributes( null );
                        if ( tu.getTexCoordGeneration() == getTexCoordGeneration() )
                            tu.setTexCoordGeneration( null );
                    }
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled( boolean enabled )
    {
        super.setEnabled( enabled );
        
        if ( texGen != null )
            texGen.setEnabled( enabled );
    }
    
    public FixedFuncShadowMappingFactory()
    {
        setShadowQuality( getShadowQuality() );
    }
}
