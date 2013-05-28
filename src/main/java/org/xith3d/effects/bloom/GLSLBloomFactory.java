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
package org.xith3d.effects.bloom;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.jagatoo.opengl.enums.FaceCullMode;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loaders.shaders.impl.glsl.GLSLShaderLoader;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.render.ForegroundRenderPass;
import org.xith3d.render.RenderPass;
import org.xith3d.render.TextureRenderTarget;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.GLSLFragmentShader;
import org.xith3d.scenegraph.GLSLContext;
import org.xith3d.scenegraph.GLSLShaderProgram;
import org.xith3d.scenegraph.GLSLVertexShader;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.primitives.Rectangle;

/**
 * The GLSLBloomFactory is an implementation of Bloom effect using GLSL shaders.<br>
 * <br>
 * I requires more render passes but all pixels computations (brightness, gaussian, and blending)
 * are done by the GPU.<br>
 * <br>
 * 
 * @author Yoann Meste (aka Mancer)
 */
public class GLSLBloomFactory extends BloomFactory
{
    private static final int TEXTURE_SIZE = 128;
    private GLSLContext brightpassFilter = null;
    private GLSLContext verticalGaussianFilter = null;
    private GLSLContext horizontalGaussianFilter = null;
    private GLSLContext blendFilter = null;
    
    private Texture2D inTex, brightTex, gaussianTex, outTex;
    
    private final Colorf bgColor = new Colorf( 0f, 0f, 0f, 1.0f );
    
    private static URL getResource( String resName ) throws IOException
    {
        URL url = GLSLBloomFactory.class.getClassLoader().getResource( resName );
        
        if ( url == null )
        {
            throw new IOException( "Could not find resource \"" + resName + "\"." );
        }
        
        return ( url );
    }
    
    private void loadShaders() throws IOException
    {
        GLSLVertexShader vertexShader;
        GLSLFragmentShader fragmentShader;
        
        vertexShader = GLSLShaderLoader.getInstance().loadVertexShader( getResource( "resources/org/xith3d/shaders/bloom/bloom.glslvert" ) );
        fragmentShader = GLSLShaderLoader.getInstance().loadFragmentShader( getResource( "resources/org/xith3d/shaders/bloom/brightness_filter.glslfrag" ) );
        
        GLSLShaderProgram program = new GLSLShaderProgram();
        program.addShader( vertexShader );
        program.addShader( fragmentShader );
        brightpassFilter = new GLSLContext( program );
        brightpassFilter.getUniformParameters().setUniformVar( "tex", 0 );
        
        fragmentShader = GLSLShaderLoader.getInstance().loadFragmentShader( getResource( "resources/org/xith3d/shaders/bloom/gaussian_v.glslfrag" ) );
        
        program = new GLSLShaderProgram();
        program.addShader( vertexShader );
        program.addShader( fragmentShader );
        verticalGaussianFilter = new GLSLContext( program );
        verticalGaussianFilter.getUniformParameters().setUniformVar( "inTexture", 0 );
        
        fragmentShader = GLSLShaderLoader.getInstance().loadFragmentShader( getResource( "resources/org/xith3d/shaders/bloom/gaussian_h.glslfrag" ) );
        
        program = new GLSLShaderProgram();
        program.addShader( vertexShader );
        program.addShader( fragmentShader );
        
        horizontalGaussianFilter = new GLSLContext( program );
        horizontalGaussianFilter.getUniformParameters().setUniformVar( "inTexture", 0 );
        
        fragmentShader = GLSLShaderLoader.getInstance().loadFragmentShader( getResource( "resources/org/xith3d/shaders/bloom/bloom.glslfrag" ) );
        program = new GLSLShaderProgram();
        program.addShader( vertexShader );
        program.addShader( fragmentShader );
        
        blendFilter = new GLSLContext( program );
    }
    
    private void initTextures( Sized2iRO resolution )
    {
        inTex = TextureCreator.createTexture( TextureFormat.RGBA, resolution.getWidth(), resolution.getHeight(), bgColor );
        inTex.enableAutoFreeLocalData();
        
        brightTex = TextureCreator.createTexture( TextureFormat.RGBA, TEXTURE_SIZE, TEXTURE_SIZE, bgColor );
        brightTex.enableAutoFreeLocalData();
        
        gaussianTex = TextureCreator.createTexture( TextureFormat.RGBA, TEXTURE_SIZE, TEXTURE_SIZE, bgColor );
        gaussianTex.enableAutoFreeLocalData();
        
        outTex = TextureCreator.createTexture( TextureFormat.RGBA, TEXTURE_SIZE, TEXTURE_SIZE, bgColor );
        outTex.enableAutoFreeLocalData();
    }
    
    private void createFilter( Xith3DEnvironment env, Sized2iRO res, GLSLContext program, Texture in, Texture out )
    {
        // TODO: Check, in what way this texture could be cached!
        Texture2D empty = TextureCreator.createTexture( TextureFormat.RGBA, 1024, 1024, new Colorf( 0f, 0f, 0f, 1f ) );
        Rectangle finalTarget = new Rectangle( 2f, 2f / res.getWidth() * res.getHeight(), empty );
        
        Appearance appearance = finalTarget.getAppearance( true );
        appearance.setTexture( 0, in );
        appearance.setShaderProgramContext( program );
        
        RenderPass finalPass = ForegroundRenderPass.createParallel();
        
        finalPass.getConfig().setViewTransform( Transform3D.IDENTITY );
        finalPass.getBranchGroup().addChild( finalTarget );
        
        TextureRenderTarget renderTarget = new TextureRenderTarget( finalPass.getBranchGroup(), out, bgColor );
        finalPass.setRenderTarget( renderTarget );
        
        env.addRenderPass( finalPass );
    }
    
    @Override
    public void prepareForBloom( Xith3DEnvironment env, Sized2iRO resolution, GroupNode group ) throws IOException
    {
        loadShaders();
        initTextures( resolution );
        
        List< RenderPass > passes = env.getRenderer().getRenderPasses( group.getRoot() );
        
        TextureRenderTarget renderTarget1 = new TextureRenderTarget( group, inTex, bgColor );
        
        passes.get( 0 ).setRenderTarget( renderTarget1 );
        
        createFilter( env, resolution, brightpassFilter, inTex, brightTex );
        createFilter( env, resolution, horizontalGaussianFilter, brightTex, gaussianTex );
        createFilter( env, resolution, verticalGaussianFilter, gaussianTex, outTex );
        
        // TODO: Check, in what way this texture could be cached!
        Texture2D empty = TextureCreator.createTexture( TextureFormat.RGBA, 1024, 1024, new Colorf( 0f, 0f, 0f, 1f ) );
        Rectangle finalTarget = new Rectangle( 2f, 2f / resolution.getWidth() * resolution.getHeight(), empty );
        
        Appearance appearance = finalTarget.getAppearance( true );
        appearance.setTexture( 0, inTex );
        appearance.setTexture( 1, outTex );
        blendFilter.getUniformParameters().setUniformVar( "originalWeight", getSceneWeight() );
        blendFilter.getUniformParameters().setUniformVar( "bloomWeight", getBloomWeight() );
        blendFilter.getUniformParameters().setUniformVar( "originalTex", 0 );
        blendFilter.getUniformParameters().setUniformVar( "filteredTex", 1 );
        
        appearance.setShaderProgramContext( blendFilter );
        appearance.setPolygonAttributes( new PolygonAttributes( FaceCullMode.BACK ) );
        
        //RenderPass finalPass = env.addParallelBranch();
        RenderPass finalPass = ForegroundRenderPass.createParallel();
        env.addRenderPass( finalPass );
        
        finalPass.getConfig().setViewTransform( Transform3D.IDENTITY );
        
        StaticTransform.translate( finalTarget, 0, 0, -1f );
        finalPass.getBranchGroup().addChild( finalTarget );
        
        //TODO the skybox is still not visible ... why ???
        
        //TODO When the bloom is applied on BSPLoader testcase, the polygons aren't displayed correctly
    }
    
    @Override
    protected void updateBloomSettings()
    {
        if ( blendFilter != null )
        {
            blendFilter.getUniformParameters().setUniformVar( "originalWeight", getSceneWeight() );
            blendFilter.getUniformParameters().setUniformVar( "bloomWeight", getBloomWeight() );
        }
    }
    
    public GLSLBloomFactory()
    {
    }
}
