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

import org.jagatoo.opengl.enums.TextureCombineFunction;
import org.jagatoo.opengl.enums.TextureCombineMode;
import org.jagatoo.opengl.enums.TextureCombineSource;
import org.jagatoo.opengl.enums.CompareFunction;
import org.jagatoo.opengl.enums.TextureCompareMode;
import org.jagatoo.opengl.enums.TextureMode;
import org.openmali.vecmath2.Colorf;
import org.xith3d.render.states.StateUnit;
import org.xith3d.scenegraph.TexCoordGeneration;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.Transform3D;

/**
 * The {@link OpenGLStatesCache} is a simple, flat class, that keeps
 * a local copy of all the OpenGL states, that are used by the engine.
 * 
 * By keeping local copies unnecessary state chenges can be avoided.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class OpenGLStatesCache
{
    public boolean enabled = true;
    
    public final long[] lastFrameId = new long[ StateUnit.MAX_STATE_TYPES ];
    
    public boolean coordsArrayEnabled = false;
    
    public boolean normalsArrayEnabled = false;
    
    public boolean colorsArrayEnabled = false;
    
    public int texCoordArraysEnableMask = 0;
    
    public final int[] texGenEnableMask = new int[ 8 ];
    
    public final boolean[] texture1DEnabled = new boolean[ 8 ];
    public final boolean[] texture2DEnabled = new boolean[ 8 ];
    public final boolean[] texture3DEnabled = new boolean[ 8 ];
    public final boolean[] textureCMEnabled = new boolean[ 8 ];
    
    public final Texture[] currentBoundTexture = new Texture[ 8 ];
    public final TextureAttributes[] currentTexAttribs = new TextureAttributes[ 8 ];
    public final TexCoordGeneration[] currentTexCoordGen = new TexCoordGeneration[ 8 ];
    
    public final TextureMode[] currentTextureMode = new TextureMode[ 8 ];
    
    public final Colorf[] currentTextureBlendColor = new Colorf[ 8 ];
    
    public final TextureCombineMode[] currentCombineMode_RGB = new TextureCombineMode[ 8 ];
    public final TextureCombineMode[] currentCombineMode_Alpha = new TextureCombineMode[ 8 ];
    
    public final TextureCombineSource[] currentCombineSource0_RGB = new TextureCombineSource[ 8 ];
    public final TextureCombineSource[] currentCombineSource0_Alpha = new TextureCombineSource[ 8 ];
    
    public final TextureCombineSource[] currentCombineSource1_RGB = new TextureCombineSource[ 8 ];
    public final TextureCombineSource[] currentCombineSource1_Alpha = new TextureCombineSource[ 8 ];
    
    public final TextureCombineSource[] currentCombineSource2_RGB = new TextureCombineSource[ 8 ];
    public final TextureCombineSource[] currentCombineSource2_Alpha = new TextureCombineSource[ 8 ];
    
    public final TextureCombineFunction[] currentCombineFunction0_RGB = new TextureCombineFunction[ 8 ];
    public final TextureCombineFunction[] currentCombineFunction0_Alpha = new TextureCombineFunction[ 8 ];
    
    public final TextureCombineFunction[] currentCombineFunction1_RGB = new TextureCombineFunction[ 8 ];
    public final TextureCombineFunction[] currentCombineFunction1_Alpha = new TextureCombineFunction[ 8 ];
    
    public final TextureCombineFunction[] currentCombineFunction2_RGB = new TextureCombineFunction[ 8 ];
    public final TextureCombineFunction[] currentCombineFunction2_Alpha = new TextureCombineFunction[ 8 ];
    
    public final TextureCompareMode[] currentCompareMode = new TextureCompareMode[ 8 ];
    public final CompareFunction[] currentCompareFunc = new CompareFunction[ 8 ];
    
    public final Transform3D[] currentTextureMatrix = new Transform3D[ 8 ];
    
    public final int[] currentCombineRGBScale = new int[ 8 ];
    
    public long vertexAttribsEnableMask = 0L;
    
    public int currentServerTextureUnit = -1;
    public int currentClientTextureUnit = -1;
    
    public int maxUsedVertexAttrib = 0;
    
    public int colorWriteMask = 15;
    public boolean depthWriteMask = true;
    
    public boolean assemblyVertexShadersEnabled = false;
    public boolean assemblyFragmentShadersEnabled = false;
    
    public int currentGLSLShaderProgram = 0;
    
    public boolean depthTestEnabled = false;
    public boolean alphaTestEnabled = false;
    public boolean stencilTestEnabled = false;
    
    public boolean scissorTestEnabled = false;
    public final boolean[] clipPlaneEnabled = new boolean[ 6 ];
    
    public boolean blendingEnabled = false;
    
    public boolean pointSmoothEnabled = false;
    
    public boolean lineStippleEnabled = false;
    public boolean lineSmoothEnabled = false;
    
    public boolean polygonSmoothEnabled = false;
    
    public boolean polygonOffsetPointEnabled = false;
    public boolean polygonOffsetLineEnabled = false;
    public boolean polygonOffsetFillEnabled = false;
    
    public boolean cullFaceEnabled = false;
    
    public boolean normalizeEnabled = false;
    public boolean colorMaterialEnabled = false;
    public boolean lightingEnabled = false;
    public final boolean[] lightEnabled = new boolean[ 8 ];
    public boolean fogEnabled = false;
    
    public final Colorf color = new Colorf();
    
    public int currentBoundArrayVBO = -1;
    public int currentBoundElementVBO = -1;
    
    public final void update( boolean   _coordsArrayEnabled,
                              boolean   _normalsArrayEnabled,
                              boolean   _colorsArrayEnabled,
                              int       _texCoordArraysEnableMask,
                              int[]     _texGenEnableMask,
                              boolean[] _texture1DEnabled,
                              boolean[] _texture2DEnabled,
                              boolean[] _texture3DEnabled,
                              boolean[] _textureCMEnabled,
                              long      _vertexAttribsEnableMask,
                              int       _currentServerTextureUnit,
                              int       _currentClientTextureUnit,
                              int       _maxUsedVertexAttrib,
                              int       _colorWriteMask,
                              boolean   _depthWriteMask,
                              boolean   _assemblyVertexShadersEnabled,
                              boolean   _assemblyFragmentShadersEnabled,
                              int       _currentGLSLShaderProgram,
                              boolean   _depthTestEnabled,
                              boolean   _alphaTestEnabled,
                              boolean   _stencilTestEnabled,
                              boolean   _scissorTestEnabled,
                              boolean[] _clipPlaneEnabled,
                              boolean   _blendingEnabled,
                              boolean   _pointSmoothEnabled,
                              boolean   _lineStippleEnabled,
                              boolean   _lineSmoothEnabled,
                              boolean   _polygonSmoothEnabled,
                              @SuppressWarnings( "unused" ) boolean   _polygonOffsetPointEnabled,
                              @SuppressWarnings( "unused" ) boolean   _polygonOffsetLineEnabled,
                              @SuppressWarnings( "unused" ) boolean   _polygonOffsetFillEnabled,
                              boolean   _cullFaceEnabled,
                              boolean   _normalizeEnabled,
                              boolean   _colorMaterialEnabled,
                              boolean   _lightingEnabled,
                              boolean[] _lightEnabled,
                              boolean   _fogEnabled,
                              float[]   _color
                            )
    {
        this.coordsArrayEnabled = _coordsArrayEnabled;
        this.normalsArrayEnabled = _normalsArrayEnabled;
        this.colorsArrayEnabled = _colorsArrayEnabled;
        this.texCoordArraysEnableMask = _texCoordArraysEnableMask;
        System.arraycopy( _texGenEnableMask, 0, this.texGenEnableMask, 0, _texGenEnableMask.length );
        System.arraycopy( _texture1DEnabled, 0, this.texture1DEnabled, 0, _texture1DEnabled.length );
        System.arraycopy( _texture2DEnabled, 0, this.texture2DEnabled, 0, _texture2DEnabled.length );
        System.arraycopy( _texture3DEnabled, 0, this.texture3DEnabled, 0, _texture3DEnabled.length );
        System.arraycopy( _textureCMEnabled, 0, this.textureCMEnabled, 0, _textureCMEnabled.length );
        this.vertexAttribsEnableMask = _vertexAttribsEnableMask;
        this.currentServerTextureUnit = _currentServerTextureUnit;
        this.currentClientTextureUnit = _currentClientTextureUnit;
        this.maxUsedVertexAttrib = _maxUsedVertexAttrib;
        this.colorWriteMask = _colorWriteMask;
        this.depthWriteMask = _depthWriteMask;
        this.assemblyVertexShadersEnabled = _assemblyVertexShadersEnabled;
        this.assemblyFragmentShadersEnabled = _assemblyFragmentShadersEnabled;
        this.currentGLSLShaderProgram = _currentGLSLShaderProgram;
        this.depthTestEnabled = _depthTestEnabled;
        this.alphaTestEnabled = _alphaTestEnabled;
        this.stencilTestEnabled = _stencilTestEnabled;
        this.scissorTestEnabled = _scissorTestEnabled;
        System.arraycopy( _clipPlaneEnabled, 0, this.clipPlaneEnabled, 0, _clipPlaneEnabled.length );
        this.blendingEnabled = _blendingEnabled;
        this.pointSmoothEnabled = _pointSmoothEnabled;
        this.lineStippleEnabled = _lineStippleEnabled;
        this.lineSmoothEnabled = _lineSmoothEnabled;
        this.polygonSmoothEnabled = _polygonSmoothEnabled;
        this.cullFaceEnabled = _cullFaceEnabled;
        this.normalizeEnabled = _normalizeEnabled;
        this.colorMaterialEnabled = _colorMaterialEnabled;
        this.lightingEnabled = _lightingEnabled;
        System.arraycopy( _lightEnabled, 0, this.lightEnabled, 0, _lightEnabled.length );
        this.fogEnabled = _fogEnabled;
        this.color.set( _color );
        
        this.currentBoundArrayVBO = -1;
        this.currentBoundElementVBO = -1;
        
        java.util.Arrays.fill( lastFrameId, -1L );
        
        java.util.Arrays.fill( currentBoundTexture, null );
        java.util.Arrays.fill( currentTexAttribs, null );
        java.util.Arrays.fill( currentTexCoordGen, null );
        
        java.util.Arrays.fill( currentTextureMode, null );
        java.util.Arrays.fill( currentTextureBlendColor, null );
        java.util.Arrays.fill( currentCombineMode_RGB, null );
        java.util.Arrays.fill( currentCombineMode_Alpha, null );
        java.util.Arrays.fill( currentCombineSource0_RGB, null );
        java.util.Arrays.fill( currentCombineSource0_Alpha, null );
        java.util.Arrays.fill( currentCombineSource1_RGB, null );
        java.util.Arrays.fill( currentCombineSource1_Alpha, null );
        java.util.Arrays.fill( currentCombineSource2_RGB, null );
        java.util.Arrays.fill( currentCombineSource2_Alpha, null );
        java.util.Arrays.fill( currentCombineFunction0_RGB, null );
        java.util.Arrays.fill( currentCombineFunction0_Alpha, null );
        java.util.Arrays.fill( currentCombineFunction1_RGB, null );
        java.util.Arrays.fill( currentCombineFunction1_Alpha, null );
        java.util.Arrays.fill( currentCombineFunction2_RGB, null );
        java.util.Arrays.fill( currentCombineFunction2_Alpha, null );
        java.util.Arrays.fill( currentCombineRGBScale, -1 );
        java.util.Arrays.fill( currentCompareMode, null );
        java.util.Arrays.fill( currentCompareFunc, null );
        java.util.Arrays.fill( currentTextureMatrix, null );
    }
    
    public abstract void update( Object glObj, OpenGLCapabilities glCaps );
    
    private static final String arrayToString( int[] array )
    {
        String s = "";
        for ( int i = 0; i < array.length; i++ )
        {
            if ( i > 0 )
                s += ", ";
            s += String.valueOf( array[ i ] );
        }
        
        return ( s );
    }
    
    @SuppressWarnings("unused")
    private static final String arrayToString( float[] array )
    {
        String s = "";
        for ( int i = 0; i < array.length; i++ )
        {
            if ( i > 0 )
                s += ", ";
            s += String.valueOf( array[ i ] );
        }
        
        return ( s );
    }
    
    private static final String arrayToString( boolean[] array )
    {
        String s = "";
        for ( int i = 0; i < array.length; i++ )
        {
            if ( i > 0 )
                s += ", ";
            s += String.valueOf( array[ i ] );
        }
        
        return ( s );
    }
    
    private static final String arrayToString( Object[] array )
    {
        String s = "";
        for ( int i = 0; i < array.length; i++ )
        {
            if ( i > 0 )
                s += ", ";
            s += String.valueOf( array[ i ].toString() );
        }
        
        return ( s );
    }
    
    public void dump()
    {
        System.out.println( "Current cached OpenGL states:" );
        System.out.println( "  coordsArrayEnabled: " + coordsArrayEnabled );
        System.out.println( "  normalsArrayEnabled: " + normalizeEnabled );
        System.out.println( "  this.colorsArrayEnabled: " + colorsArrayEnabled );
        System.out.println( "  texCoordArraysEnableMask: " + texCoordArraysEnableMask );
        System.out.println( "  texGenEnableMask: " + arrayToString( texGenEnableMask ) );
        System.out.println( "  texture1DEnabled: " + arrayToString( texture1DEnabled ) );
        System.out.println( "  texture2DEnabled: " + arrayToString( texture2DEnabled ) );
        System.out.println( "  texture3DEnabled: " + arrayToString( texture3DEnabled ) );
        System.out.println( "  textureCMEnabled: " + arrayToString( textureCMEnabled ) );
        System.out.println( "  currentCompineMode_RGB: " + arrayToString( currentCombineMode_RGB ) );
        System.out.println( "  currentCompineMode_Alpha: " + arrayToString( currentCombineMode_Alpha ) );
        System.out.println( "  currentCompineSource0_RGB: " + arrayToString( currentCombineSource0_RGB ) );
        System.out.println( "  currentCompineSource0_Alpha: " + arrayToString( currentCombineSource0_Alpha ) );
        System.out.println( "  currentCompineSource1_RGB: " + arrayToString( currentCombineSource1_RGB ) );
        System.out.println( "  currentCompineSource1_Alpha: " + arrayToString( currentCombineSource1_Alpha ) );
        System.out.println( "  currentCompineSource2_RGB: " + arrayToString( currentCombineSource2_RGB ) );
        System.out.println( "  currentCompineSource2_Alpha: " + arrayToString( currentCombineSource2_Alpha ) );
        System.out.println( "  currentCompineFunction0_RGB: " + arrayToString( currentCombineFunction0_RGB ) );
        System.out.println( "  currentCompineFunction0_Alpha: " + arrayToString( currentCombineFunction0_Alpha ) );
        System.out.println( "  currentCompineFunction1_RGB: " + arrayToString( currentCombineFunction1_RGB ) );
        System.out.println( "  currentCompineFunction1_Alpha: " + arrayToString( currentCombineFunction1_Alpha ) );
        System.out.println( "  currentCompineFunction2_RGB: " + arrayToString( currentCombineFunction2_RGB ) );
        System.out.println( "  currentCompineFunction2_Alpha: " + arrayToString( currentCombineFunction2_Alpha ) );
        System.out.println( "  currentTextureMode: " + arrayToString( currentTextureMode ) );
        System.out.println( "  currentCombineRGBScale: " + arrayToString( currentCombineRGBScale ) );
        System.out.println( "  currentCompareMode: " + arrayToString( currentCompareMode ) );
        System.out.println( "  currentCompareFunc: " + arrayToString( currentCompareFunc ) );
        System.out.println( "  currentTextureMatrix: " + arrayToString( currentTextureMatrix ) );
        System.out.println( "  vertexAttribsEnableMask: " + vertexAttribsEnableMask );
        System.out.println( "  currentServerTextureUnit: " + currentServerTextureUnit );
        System.out.println( "  currentClientTextureUnit: " + currentClientTextureUnit );
        System.out.println( "  maxUsedVertexAttrib: " + maxUsedVertexAttrib );
        System.out.println( "  colorWriteMask: " + colorWriteMask );
        System.out.println( "  depthWriteMask: " + depthWriteMask );
        System.out.println( "  assemblyVertexShadersEnabled: " + assemblyVertexShadersEnabled );
        System.out.println( "  assemblyFragmentShadersEnabled: " + assemblyFragmentShadersEnabled );
        System.out.println( "  currentGLSLShaderProgram: " + currentGLSLShaderProgram );
        System.out.println( "  depthTestEnabled: " + depthTestEnabled );
        System.out.println( "  alphaTestEnabled: " + alphaTestEnabled );
        System.out.println( "  stencilTestEnabled: " + stencilTestEnabled );
        System.out.println( "  scissorTestEnabled: " + scissorTestEnabled );
        System.out.println( "  clipPlaneEnabled: " + arrayToString( clipPlaneEnabled ) );
        System.out.println( "  blendingEnabled: " + blendingEnabled );
        System.out.println( "  pointSmoothEnabled: " + pointSmoothEnabled );
        System.out.println( "  lineStippleEnabled: " + lineStippleEnabled );
        System.out.println( "  lineSmoothEnabled: " + lineSmoothEnabled );
        System.out.println( "  polygonSmoothEnabled: " + polygonSmoothEnabled );
        System.out.println( "  cullFaceEnabled: " + cullFaceEnabled );
        System.out.println( "  normalizeEnabled: " + normalizeEnabled );
        System.out.println( "  colorMaterialEnabled: " + colorMaterialEnabled );
        System.out.println( "  lightingEnabled: " + lightingEnabled );
        System.out.println( "  lightEnabled: " + arrayToString( lightEnabled ) );
        System.out.println( "  fogEnabled: " + fogEnabled );
        System.out.println( "  color: " + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ", " + color.getAlpha() );
        
    }
    
    public OpenGLStatesCache()
    {
        java.util.Arrays.fill( lastFrameId, -1L );
        
        java.util.Arrays.fill( currentBoundTexture, null );
        java.util.Arrays.fill( currentTexAttribs, null );
        java.util.Arrays.fill( currentTexCoordGen, null );
        
        java.util.Arrays.fill( currentTextureMode, null );
        java.util.Arrays.fill( currentTextureBlendColor, null );
        java.util.Arrays.fill( texGenEnableMask, 0 );
        java.util.Arrays.fill( texture1DEnabled, false );
        java.util.Arrays.fill( texture2DEnabled, false );
        java.util.Arrays.fill( texture3DEnabled, false );
        java.util.Arrays.fill( textureCMEnabled, false );
        java.util.Arrays.fill( currentCombineMode_RGB, null );
        java.util.Arrays.fill( currentCombineMode_Alpha, null );
        java.util.Arrays.fill( currentCombineSource0_RGB, null );
        java.util.Arrays.fill( currentCombineSource0_Alpha, null );
        java.util.Arrays.fill( currentCombineSource1_RGB, null );
        java.util.Arrays.fill( currentCombineSource1_Alpha, null );
        java.util.Arrays.fill( currentCombineSource2_RGB, null );
        java.util.Arrays.fill( currentCombineSource2_Alpha, null );
        java.util.Arrays.fill( currentCombineFunction0_RGB, null );
        java.util.Arrays.fill( currentCombineFunction0_Alpha, null );
        java.util.Arrays.fill( currentCombineFunction1_RGB, null );
        java.util.Arrays.fill( currentCombineFunction1_Alpha, null );
        java.util.Arrays.fill( currentCombineFunction2_RGB, null );
        java.util.Arrays.fill( currentCombineFunction2_Alpha, null );
        java.util.Arrays.fill( currentCombineRGBScale, -1 );
        java.util.Arrays.fill( currentCompareMode, null );
        java.util.Arrays.fill( currentCompareFunc, null );
        java.util.Arrays.fill( currentTextureMatrix, null );
        java.util.Arrays.fill( lightEnabled, false );
        java.util.Arrays.fill( clipPlaneEnabled, false );
    }
}
