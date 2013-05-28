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
import java.net.URL;

import org.xith3d.loaders.shaders.impl.glsl.GLSLShaderLoader;
import org.xith3d.loaders.shaders.impl.glsl.GLSLShaderLoader.InlineVariableMapping;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.GLSLContext;
import org.xith3d.scenegraph.GLSLFragmentShader;
import org.xith3d.scenegraph.GLSLParameters;
import org.xith3d.scenegraph.GLSLShaderProgram;
import org.xith3d.scenegraph.GLSLVertexShader;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TextureUnit;
import org.xith3d.scenegraph.Transform3D;

/**
 * This {@link ShadowFactory} realizes shadow-mapping through GLSL.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GLSLShadowMappingFactory extends ShadowMappingFactory
{
    private static GLSLContext shaderProgram = null;
    
    private final Transform3D cameraModelView = new Transform3D();
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void calculateTextureMatrix( float fovy, float aspect, float near, float far, Transform3D lightTransform, Transform3D viewTransform, Transform3D textureTransform )
    {
        super.calculateTextureMatrix( fovy, aspect, near, far, lightTransform, viewTransform, textureTransform );
        
        cameraModelView.set( viewTransform );
        //cameraModelView.invert();
        textureTransform.mul( cameraModelView );
    }
    
    private static URL getResource( String resName ) throws IOException
    {
        URL url = GLSLShadowMappingFactory.class.getClassLoader().getResource( resName );
        
        if ( url == null )
        {
            throw new IOException( "Could not find resource \"" + resName + "\"." );
        }
        
        return ( url );
    }
    
    private GLSLContext getShaderProgram() throws IOException
    {
        if ( shaderProgram != null )
            return ( shaderProgram );
        
        URL url = getResource( "resources/org/xith3d/shaders/shadowmapping/shader.shadowmapping.glslvert" );
        
        GLSLVertexShader vertexShader = GLSLShaderLoader.getInstance().loadVertexShader( url, new InlineVariableMapping[] { new InlineVariableMapping( "shadowMapUnit", 3 ) } );
        
        url = getResource( "resources/org/xith3d/shaders/shadowmapping/shader.shadowmapping.glslfrag" );
        
        GLSLFragmentShader fragmentShader = GLSLShaderLoader.getInstance().loadFragmentShader( url );
        
        shaderProgram = new GLSLContext( new GLSLShaderProgram( vertexShader, fragmentShader ) );
        
        GLSLParameters params = shaderProgram.getUniformParameters();
        
        params.setUniformVar( "texture0", 0 );
        params.setUniformVar( "shadowMap", getShadowTextureUnit() );
        //params.setUniformVar( "shadowMapUnit", getShadowTextureUnit() );
        params.setUniformVar( "softness", getShadowSoftness() );
        params.setUniformVar( "soft_dist", 0.2f );
        
        return ( shaderProgram );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setShadowSoftness( int softness )
    {
        super.setShadowSoftness( softness );
        
        try
        {
            getShaderProgram().getUniformParameters().setUniformVar( "softness", getShadowSoftness() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
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
            
            try
            {
                app.setShaderProgramContext( getShaderProgram() );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
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
        }
        else
        {
            final Appearance app = shape.getAppearance();
            if ( app != null )
            {
                TextureUnit tu = app.getTextureUnit( getShadowTextureUnit() );
                if ( tu != null )
                {
                    if ( ( tu.getTexture() == getShadowMap() ) && ( tu.getTextureAttributes() == this.getShadowMapAttributes() ) && ( tu.getTexCoordGeneration() == null ) )
                    {
                        app.setTextureUnit( getShadowTextureUnit(), null );
                    }
                    else
                    {
                        if ( tu.getTexture() == getShadowMap() )
                            tu.setTexture( (Texture)null );
                        if ( tu.getTextureAttributes() == getShadowMapAttributes() )
                            tu.setTextureAttributes( null );
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
        
        if ( shaderProgram != null )
            shaderProgram.setEnabled( enabled );
    }
    
    public GLSLShadowMappingFactory()
    {
        setShadowQuality( getShadowQuality() );
    }
}
