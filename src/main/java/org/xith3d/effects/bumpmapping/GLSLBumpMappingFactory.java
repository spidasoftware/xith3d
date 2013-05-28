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
package org.xith3d.effects.bumpmapping;

import java.io.IOException;
import java.net.URL;

import org.xith3d.loaders.shaders.impl.glsl.GLSLShaderLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.GLSLFragmentShader;
import org.xith3d.scenegraph.GLSLContext;
import org.xith3d.scenegraph.GLSLShaderProgram;
import org.xith3d.scenegraph.GLSLVertexShader;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.utility.geometry.TangentsFactory.TangentsStoreMode;

/**
 * The BumpmappingFactory is capable of preparing a Shape3D for Bumpmapping.
 * the underlying methods to calculate tangets/bitangets and to load the
 * appropriate shaders are also public to use them for different perposes.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GLSLBumpMappingFactory extends BumpMappingFactory
{
    private static GLSLShaderProgram shaderProgram = null;
    
    private static URL getResource( String resName ) throws IOException
    {
        URL url = GLSLBumpMappingFactory.class.getClassLoader().getResource( resName );
        
        if ( url == null )
        {
            throw new IOException( "Could not find resource \"" + resName + "\"." );
        }
        
        return ( url );
    }
    
    private GLSLShaderProgram getShaderProgram() throws IOException
    {
        if ( shaderProgram != null )
            return ( shaderProgram );
        
        URL url = getResource( "resources/org/xith3d/shaders/bumpmapping/shader.bumpmapping.glslvert" );
        
        GLSLVertexShader vertexShader = GLSLShaderLoader.getInstance().loadVertexShader( url );
        
        if ( getTangentsStoreMode() == TangentsStoreMode.VERTEX_ATTRIBUTES )
        {
            vertexShader.setVertexAttributeMapping( getTangentsVertexAttribute(), "tangent" );
            vertexShader.setVertexAttributeMapping( getBiTangentsVertexAttribute(), "bitangent" );
        }
        
        url = getResource( "resources/org/xith3d/shaders/bumpmapping/shader.bumpmapping.glslfrag" );
        
        GLSLFragmentShader fragmentShader = GLSLShaderLoader.getInstance().loadFragmentShader( url );
        
        shaderProgram = new GLSLShaderProgram( vertexShader, fragmentShader );
        
        return ( shaderProgram );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForBumpMapping( Shape3D shape, Texture normalMapTex ) throws IOException
    {
        //((GeometryArray)shape.getGeometry()).calculateFaceNormals();
        
        getTangentsFactory().calculateTangents( shape.getGeometry(), getTangentsStoreMode(), getStoreIndex1(), getStoreIndex2() );
        
        Appearance app = shape.getAppearance( true );
        
        app.setTexture( getNormalMapTextureUnit(), normalMapTex );
        
        GLSLContext shaderProgram = new GLSLContext( getShaderProgram() );
        shaderProgram.getUniformParameters().setUniformVar( "mainTex", 0 );
        shaderProgram.getUniformParameters().setUniformVar( "normalTex", getNormalMapTextureUnit() );
        if ( getTangentsStoreMode() == TangentsStoreMode.VERTEX_ATTRIBUTES )
        {
            if ( shape.getGeometry().getOptimization() == Optimization.USE_DISPLAY_LISTS )
            {
                // DisplayLists don't currently support vertex attributes!
                shape.getGeometry().setOptimization( Optimization.USE_VBOS );
            }
        }
        
        app.setShaderProgramContext( shaderProgram );
    }
    
    public GLSLBumpMappingFactory()
    {
        setTangentsStoreMode( TangentsStoreMode.VERTEX_ATTRIBUTES );
    }
}
