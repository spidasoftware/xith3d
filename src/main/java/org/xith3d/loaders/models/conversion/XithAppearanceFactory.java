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
package org.xith3d.loaders.models.conversion;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.loaders.models._util.AppearanceFactory;
import org.jagatoo.loaders.textures.AbstractTexture;
import org.jagatoo.loaders.textures.AbstractTextureImage;
import org.jagatoo.loaders.textures.MipmapGenerator;
import org.jagatoo.opengl.enums.BlendFunction;
import org.jagatoo.opengl.enums.BlendMode;
import org.jagatoo.opengl.enums.ColorTarget;
import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.jagatoo.opengl.enums.PerspectiveCorrectionMode;
import org.jagatoo.opengl.enums.ShadeModel;
import org.jagatoo.opengl.enums.TestFunction;
import org.jagatoo.opengl.enums.TexCoordGenMode;
import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureCombineFunction;
import org.jagatoo.opengl.enums.TextureCombineMode;
import org.jagatoo.opengl.enums.TextureCombineSource;
import org.jagatoo.opengl.enums.CompareFunction;
import org.jagatoo.opengl.enums.TextureCompareMode;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.jagatoo.opengl.enums.TextureMagFilter;
import org.jagatoo.opengl.enums.TextureMinFilter;
import org.jagatoo.opengl.enums.TextureMode;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Vector4f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loaders.texture.Xith3DTextureFactory2D;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.TexCoordGeneration;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TextureImage2D;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.TexCoordGeneration.CoordMode;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class XithAppearanceFactory implements AppearanceFactory
{
    public final TransparencyAttributes createTransparencyAttributes( String name )
    {
        TransparencyAttributes ta = new TransparencyAttributes();
        ta.setName( name );
        
        return ( ta );
    }
    
    public final void setTransparencyAttribsSourceBlendFunc( NamedObject transpAttribs, BlendFunction srcBlendFunc )
    {
        ( (TransparencyAttributes)transpAttribs ).setSrcBlendFunction( srcBlendFunc );
    }
    
    public final void setTransparencyAttribsDestBlendFunc( NamedObject transpAttribs, BlendFunction dstBlendFunc )
    {
        ( (TransparencyAttributes)transpAttribs ).setDstBlendFunction( dstBlendFunc );
    }
    
    public final void setTransparencyAttribsBlendMode( NamedObject transpAttribs, BlendMode blendMode )
    {
        ( (TransparencyAttributes)transpAttribs ).setMode( blendMode );
    }
    
    public final void setTransparencyAttribsTransparency( NamedObject transpAttribs, float transparency )
    {
        ( (TransparencyAttributes)transpAttribs ).setTransparency( transparency );
    }
    
    public final void setTransparencyAttribsSortingEnabled( NamedObject transpAttribs, boolean sortingEnabled )
    {
        ( (TransparencyAttributes)transpAttribs ).setSortEnabled( sortingEnabled );
    }
    
    public final void applyTransparancyAttributes( NamedObject transpAttribs, NamedObject appearance )
    {
        ( (Appearance)appearance ).setTransparencyAttributes( (TransparencyAttributes)transpAttribs );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final Material createMaterial( String name )
    {
        Material mat = new Material();
        mat.setName( name );
        
        return ( mat );
    }
    
    public final void setMaterialColorTarget( NamedObject material, ColorTarget colorTarget )
    {
        ( (Material)material ).setColorTarget( colorTarget );
    }
    
    public final void setMaterialAmbientColor( NamedObject material, float r, float g, float b )
    {
        ( (Material)material ).setAmbientColor( r, g, b );
    }
    
    public final void setMaterialEmissiveColor( NamedObject material, float r, float g, float b )
    {
        ( (Material)material ).setEmissiveColor( r, g, b );
    }
    
    public final void setMaterialDiffuseColor( NamedObject material, float r, float g, float b )
    {
        ( (Material)material ).setDiffuseColor( r, g, b );
    }
    
    public final void setMaterialSpecularColor( NamedObject material, float r, float g, float b )
    {
        ( (Material)material ).setSpecularColor( r, g, b );
    }
    
    public final void setMaterialShininess( NamedObject material, float shininess )
    {
        ( (Material)material ).setShininess( shininess );
    }
    
    public final void setMaterialNormalizeNormals( NamedObject material, boolean normalizeNormals )
    {
        ( (Material)material ).setNormalizeNormals( normalizeNormals );
    }
    
    public final void setMaterialLightingEnabled( NamedObject material, boolean lightingEnabled )
    {
        ( (Material)material ).setLightingEnabled( lightingEnabled );
    }
    
    public final void applyMaterial( NamedObject material, NamedObject appearance )
    {
        ( (Appearance)appearance ).setMaterial( (Material)material );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final ColoringAttributes createColoringAttributes( String name )
    {
        ColoringAttributes ca = new ColoringAttributes();
        ca.setName( name );
        
        return ( ca );
    }
    
    public final void setColoringAttribsShadeModel( NamedObject coloringAttribs, ShadeModel shadeModel )
    {
        ( (ColoringAttributes)coloringAttribs ).setShadeModel( shadeModel );
    }
    
    public final void setColoringAttribsColor( NamedObject coloringAttribs, float[] color, int offset, int colorSize )
    {
        // TODO: Check, if a color-size of 4 is possible in ColoringAttributes!
        
        ( (ColoringAttributes)coloringAttribs ).setColor( color[offset + 0], color[offset + 1], color[offset + 2] );
    }
    
    public final void applyColoringAttributes( NamedObject coloringAttribs, NamedObject appearance )
    {
        ( (Appearance)appearance ).setColoringAttributes( (ColoringAttributes)coloringAttribs );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final RenderingAttributes createRenderingAttributesAttributes( String name )
    {
        RenderingAttributes ra = new RenderingAttributes();
        ra.setName( name );
        
        return ( ra );
    }
    
    public final void setRenderingAttribsDepthBufferEnabled( NamedObject renderingAttribs, boolean depthBufferEnabled )
    {
    }
    
    public final void setRenderingAttribsDepthBufferWriteEnabled( NamedObject renderingAttribs, boolean depthBufferWriteEnabled )
    {
    }
    
    public final void setRenderingAttribsAlphaTestValue( NamedObject renderingAttribs, float alphaTestValue )
    {
    }
    
    public final void setRenderingAttribsAlphaTestFunction( NamedObject renderingAttribs, TestFunction alphaTestFunction )
    {
        ( (RenderingAttributes)renderingAttribs ).setAlphaTestFunction( alphaTestFunction );
    }
    
    /*
    public final void setRenderingAttribsStencilFuncSep( NamedObject renderingAttribs, int stencilFuncSep )
    {
    }
    */
    
    /*
    public final void setRenderingAttribsStencilOpSep( NamedObject renderingAttribs, int stencilOpSep )
    {
    }
    */
    
    /*
    public final void setRenderingAttribsStencilMaskSep( NamedObject renderingAttribs, int stencilMaskSep )
    {
    }
    */
    
    public final void setRenderingAttribsDepthTestFunction( NamedObject renderingAttribs, TestFunction depthTestFunction )
    {
        ( (RenderingAttributes)renderingAttribs ).setDepthTestFunction( depthTestFunction );
    }
    
    public final void setRenderingAttribsIgnoreVertexColors( NamedObject renderingAttribs, boolean ignoreVertexColors )
    {
        ( (RenderingAttributes)renderingAttribs ).setIgnoreVertexColors( ignoreVertexColors );
    }
    
    public final void setRenderingAttribsStencilEnabled( NamedObject renderingAttribs, boolean stencilEnabled )
    {
        ( (RenderingAttributes)renderingAttribs ).setStencilEnabled( stencilEnabled );
    }
    
    /*
    public final void setRenderingAttribsStencilOpFail( NamedObject renderingAttribs, int stencilOpFail )
    {
    }
    */
    
    /*
    public final void setRenderingAttribsStencilOpZFail( NamedObject renderingAttribs, int stencilOpZFail )
    {
    }
    */
    
    /*
    public final void setRenderingAttribsStencilOpZPass( NamedObject renderingAttribs, int stencilOpZPass )
    {
    }
    */
    
    public final void setRenderingAttribsStencilTestFunction( NamedObject renderingAttribs, TestFunction stencilTestFunction )
    {
        ( (RenderingAttributes)renderingAttribs ).setStencilTestFunction( stencilTestFunction );
    }
    
    public final void setRenderingAttribsStencilRef( NamedObject renderingAttribs, int stencilRef )
    {
        ( (RenderingAttributes)renderingAttribs ).setStencilRef( stencilRef );
    }
    
    public final void setRenderingAttribsStencilMask( NamedObject renderingAttribs, int stencilMask )
    {
        ( (RenderingAttributes)renderingAttribs ).setStencilMask( stencilMask );
    }
    
    public final void setRenderingAttribsColorWriteMask( NamedObject renderingAttribs, int colorWriteMask )
    {
        ( (RenderingAttributes)renderingAttribs ).setColorWriteMask( colorWriteMask );
    }
    
    public final void applyRenderingAttributes( NamedObject renderingAttribs, NamedObject appearance )
    {
        ( (Appearance)appearance ).setRenderingAttributes( (RenderingAttributes)renderingAttribs );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final PolygonAttributes createPolygonAttributes( String name )
    {
        PolygonAttributes pa = new PolygonAttributes();
        pa.setName( name );
        
        return ( pa );
    }
    
    public final void setPolygonAttribsFaceCullMode( NamedObject polygonAttribs, FaceCullMode faceCullMode )
    {
        ( (PolygonAttributes)polygonAttribs ).setFaceCullMode( faceCullMode );
    }
    
    public final void setPolygonAttribsDrawMode( NamedObject polygonAttribs, DrawMode drawMode )
    {
        ( (PolygonAttributes)polygonAttribs ).setDrawMode( drawMode );
    }
    
    public final void setPolygonAttribsPolygonOffset( NamedObject polygonAttribs, float polygonOffset )
    {
        ( (PolygonAttributes)polygonAttribs ).setPolygonOffset( polygonOffset );
    }
    
    public final void setPolygonAttribsPolygonOffsetFactor( NamedObject polygonAttribs, float polygonOffsetFactor )
    {
        ( (PolygonAttributes)polygonAttribs ).setPolygonOffsetFactor( polygonOffsetFactor );
    }
    
    public final void setPolygonAttribsBackfaceNormalFlip( NamedObject polygonAttribs, boolean backfaceNormalFlip )
    {
        ( (PolygonAttributes)polygonAttribs ).setBackFaceNormalFlip( backfaceNormalFlip );
    }
    
    public final void setPolygonAttribsAntialiasing( NamedObject polygonAttribs, boolean anitaliasing )
    {
        ( (PolygonAttributes)polygonAttribs ).setPolygonAntialiasingEnabled( anitaliasing );
    }
    
    public final void setPolygonAttribsSortingEnabled( NamedObject polygonAttribs, boolean sortingEnabled )
    {
        ( (PolygonAttributes)polygonAttribs ).setSortEnabled( sortingEnabled );
    }
    
    public final void applyPolygonAttributes( NamedObject polygonAttribs, NamedObject appearance )
    {
        ( (Appearance)appearance ).setPolygonAttributes( (PolygonAttributes)polygonAttribs );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final void setTextureBoundaryModeS( AbstractTexture texture, TextureBoundaryMode boundaryModeS )
    {
        ( (Texture)texture ).setBoundaryModeS( boundaryModeS );
    }
    
    public final void setTextureBoundaryModeT( AbstractTexture texture, TextureBoundaryMode boundaryModeT )
    {
        ( (Texture)texture ).setBoundaryModeT( boundaryModeT );
    }
    
    public final void setTextureMagFilter( AbstractTexture texture, TextureMagFilter magFilter )
    {
        // Just ignore! Xith uses one filter for both.
    }
    
    public final void setTextureMinFilter( AbstractTexture texture, TextureMinFilter minFilter )
    {
        /*
         * If a model format even stores a texture filter,
         * it is in 99% of the cases TRILINEAR.
         * Therefore we can ignore it and leave the filter decision
         * to the GlobalOptions, which default to TRILINEAR for TextureFilter.
         */
        
        /*
        switch ( minFilter )
        {
            case POINT:
                ( (Texture)texture ).setFilter( TextureFilter.POINT );
                break;
            case BILINEAR:
                ( (Texture)texture ).setFilter( TextureFilter.BILINEAR );
                break;
            case TRILINEAR:
                ( (Texture)texture ).setFilter( TextureFilter.TRILINEAR );
                break;
            case ANISOTROPIC_2:
                ( (Texture)texture ).setFilter( TextureFilter.ANISOTROPIC_2 );
                break;
            case ANISOTROPIC_4:
                ( (Texture)texture ).setFilter( TextureFilter.ANISOTROPIC_4 );
                break;
            case ANISOTROPIC_8:
                ( (Texture)texture ).setFilter( TextureFilter.ANISOTROPIC_8 );
                break;
            case ANISOTROPIC_16:
                ( (Texture)texture ).setFilter( TextureFilter.ANISOTROPIC_16 );
                break;
        }
        */
    }
    
    public final void applyTexture( AbstractTexture texture, int textureUnit, NamedObject appearance )
    {
        ( (Appearance)appearance ).setTexture( textureUnit, (Texture2D)texture );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final TextureAttributes createTextureAttributes( String name )
    {
        TextureAttributes ta = new TextureAttributes();
        ta.setName( name );
        
        return ( ta );
    }
    
    public final void setTextureAttribsTextureMode( NamedObject textureAttribs, TextureMode textureMode )
    {
        ( (TextureAttributes)textureAttribs ).setTextureMode( textureMode );
    }
    
    public final void setTextureAttribsPerspectiveCorrectionMode( NamedObject textureAttribs, PerspectiveCorrectionMode perspCorrMode )
    {
        ( (TextureAttributes)textureAttribs ).setPerspectiveCorrectionMode( perspCorrMode );
    }
    
    public final void setTextureAttribsTextureBlendColor( NamedObject textureAttribs, float[] texBlendColor, int offset, int colorSize )
    {
        Colorf color = Colorf.fromPool();
        
        color.set( texBlendColor, offset, colorSize > 3 );
        
        ( (TextureAttributes)textureAttribs ).setTextureBlendColor( color );
        
        Colorf.toPool( color );
    }
    
    public final void setTextureAttribsTextureTransfrom( NamedObject textureAttribs, Matrix4f textureTransform )
    {
        ( (TextureAttributes)textureAttribs ).setTextureTransform( textureTransform );
    }
    
    public final void setTextureAttribsCombineRGBMode( NamedObject textureAttribs, TextureCombineMode combineRGBMode )
    {
        ( (TextureAttributes)textureAttribs ).setCombineRGBMode( combineRGBMode );
    }
    
    public final void setTextureAttribsCombineAlphaMode( NamedObject textureAttribs, TextureCombineMode combineAlphaMode )
    {
        ( (TextureAttributes)textureAttribs ).setCombineAlphaMode( combineAlphaMode );
    }
    
    public final void setTextureAttribsCombineRGBSource( NamedObject textureAttribs, int channel, TextureCombineSource combineRGBSource )
    {
        ( (TextureAttributes)textureAttribs ).setCombineRGBSource( channel, combineRGBSource );
    }
    
    public final void setTextureAttribsCombineAlphaSource( NamedObject textureAttribs, int channel, TextureCombineSource combineAlphaSource )
    {
        ( (TextureAttributes)textureAttribs ).setCombineAlphaSource( channel, combineAlphaSource );
    }
    
    public final void setTextureAttribsCombineRGBFunction( NamedObject textureAttribs, int channel, TextureCombineFunction combineRGBFunction )
    {
        ( (TextureAttributes)textureAttribs ).setCombineRGBFunction( channel, combineRGBFunction );
    }
    
    public final void setTextureAttribsCombineAlphaFunction( NamedObject textureAttribs, int channel, TextureCombineFunction combineAlphaFunction )
    {
        ( (TextureAttributes)textureAttribs ).setCombineAlphaFunction( channel, combineAlphaFunction );
    }
    
    public final void setTextureAttribsCombineRGBScale( NamedObject textureAttribs, int combineRGBScale )
    {
        ( (TextureAttributes)textureAttribs ).setCombineRGBScale( combineRGBScale );
    }
    
    public final void setTextureAttribsCombineAlphaScale( NamedObject textureAttribs, int combineAlphaScale )
    {
        ( (TextureAttributes)textureAttribs ).setCombineAlphaScale( combineAlphaScale );
    }
    
    public final void setTextureAttribsCompareMode( NamedObject textureAttribs, TextureCompareMode compareMode )
    {
        ( (TextureAttributes)textureAttribs ).setCompareMode( compareMode );
    }
    
    public final void setTextureAttribsCompareFunc( NamedObject textureAttribs, CompareFunction compareFunc )
    {
        ( (TextureAttributes)textureAttribs ).setCompareFunction( compareFunc );
    }
    
    public final void applyTextureAttributes( NamedObject textureAttribs, int textureUnit, NamedObject appearance )
    {
        ( (Appearance)appearance ).setTextureAttributes( textureUnit, (TextureAttributes)textureAttribs );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final TexCoordGeneration createTextureCoordGeneration( String name )
    {
        TexCoordGeneration tcg = new TexCoordGeneration();
        tcg.setName( name );
        
        return ( tcg );
    }
    
    public final void setTexCoordGenerationGenMode( NamedObject texCoordGen, TexCoordGenMode genMode )
    {
        ( (TexCoordGeneration)texCoordGen ).setGenMode( genMode );
    }
    
    public void setTexCoordGenerationNumTexGenUnits( NamedObject texCoordGen, int numTexGenUnits )
    {
        ( (TexCoordGeneration)texCoordGen ).setFormat( CoordMode.getFromNumber( numTexGenUnits ) );
    }
    
    public final void setTexCoordGenerationPlaneS( NamedObject texCoordGen, float[] planeS, int offset )
    {
        Vector4f vec = Vector4f.fromPool();
        
        vec.set( planeS, offset );
        
        ( (TexCoordGeneration)texCoordGen ).setPlaneS( vec );
        
        Vector4f.toPool( vec );
    }
    
    public final void setTexCoordGenerationPlaneT( NamedObject texCoordGen, float[] planeT, int offset )
    {
        Vector4f vec = Vector4f.fromPool();
        
        vec.set( planeT, offset );
        
        ( (TexCoordGeneration)texCoordGen ).setPlaneT( vec );
        
        Vector4f.toPool( vec );
    }
    
    public final void setTexCoordGenerationPlaneR( NamedObject texCoordGen, float[] planeR, int offset )
    {
        Vector4f vec = Vector4f.fromPool();
        
        vec.set( planeR, offset );
        
        ( (TexCoordGeneration)texCoordGen ).setPlaneR( vec );
        
        Vector4f.toPool( vec );
    }
    
    public final void setTexCoordGenerationPlaneQ( NamedObject texCoordGen, float[] planeQ, int offset )
    {
        Vector4f vec = Vector4f.fromPool();
        
        vec.set( planeQ, offset );
        
        ( (TexCoordGeneration)texCoordGen ).setPlaneQ( vec );
        
        Vector4f.toPool( vec );
    }
    
    public final void applyTextureCoordGeneration( NamedObject texCoordGen, int textureUnit, NamedObject appearance )
    {
        ( (Appearance)appearance ).setTexCoordGeneration( textureUnit, (TexCoordGeneration)texCoordGen );
        ( (Appearance)appearance ).markStaticDirty();
    }
    
    
    
    public final NamedObject createAppearance( String name, int flags )
    {
        Appearance app = new Appearance();
        app.setName( name );
        
        if ( ( flags & APP_FLAG_STATIC ) != 0 )
        {
            app.setStatic( true );
        }
        
        return ( app );
    }
    
    private static NamedObject simplyBlendedTransAttribs = null;
    private static NamedObject simpleMaterial = null;
    
    public final NamedObject createStandardAppearance( String name, AbstractTexture texture0, int flags )
    {
        NamedObject app = createAppearance( name, flags );
        
        applyTexture( texture0, 0, app );
        
        if ( texture0.getFormat().hasAlpha() )
        {
            if ( simplyBlendedTransAttribs == null )
            {
                simplyBlendedTransAttribs = createTransparencyAttributes( "simply blended" );
                setTransparencyAttribsBlendMode( simplyBlendedTransAttribs, BlendMode.BLENDED );
                setTransparencyAttribsTransparency( simplyBlendedTransAttribs, 0f );
            }
            
            applyTransparancyAttributes( simplyBlendedTransAttribs, app );
        }
        
        if ( simpleMaterial == null )
        {
            simpleMaterial = createMaterial( "simple material" );
            setMaterialLightingEnabled( simpleMaterial, true );
        }
        
        applyMaterial( simpleMaterial, app );
        
        return ( app );
    }
    
    public final NamedObject createStandardAppearance( String name, String textureName0, URL baseURL, int flags )
    {
        if ( textureName0.startsWith( ":\\", 1 ) )
        {
            textureName0 = "file://" + textureName0.replace( '\\', '/' );
        }
        else
        {
            textureName0 = textureName0.replace( '\\', '/' );
        }
        
        AbstractTexture texture0;
        if ( baseURL == null )
            texture0 = loadOrGetTexture( textureName0, true, true, true, true, false );
        else
            texture0 = loadOrGetTexture( textureName0, baseURL, true, true, true, true, false );
        
        if ( texture0 == null )
        {
            int lastSlashPos = textureName0.lastIndexOf( '/' );
            String simpleTexName = textureName0;
            if ( lastSlashPos >= 0 )
                simpleTexName = textureName0.substring( lastSlashPos + 1 );
            
            if ( baseURL == null )
                texture0 = loadOrGetTexture( simpleTexName, true, true, true, true, true );
            else
                texture0 = loadOrGetTexture( simpleTexName, baseURL, true, true, true, true, true );
        }
        
        return ( createStandardAppearance( name, texture0, flags ) );
    }
    
    public final void applyAppearance( NamedObject appearance, NamedObject geometry )
    {
    }
    
    
    
    public final Texture2D getFallbackTexture()
    {
        return ( TextureLoader.getFallbackTexture() );
    }
    
    public final boolean isFallbackTexture( AbstractTexture texture )
    {
        return ( texture == TextureLoader.getFallbackTexture() );
    }
    
    public final Texture2D loadTexture( InputStream in, String texName, boolean flipVertically, boolean acceptAlpha, boolean loadMipmaps, boolean allowStreching, boolean acceptFallbackTexture )
    {
        TextureLoader.FlipMode flipMode = flipVertically ? TextureLoader.FlipMode.FLIPPED_VERTICALLY : TextureLoader.FlipMode.NOT_FLIPPED;
        TextureFormat format = acceptAlpha ? TextureFormat.RGBA : TextureFormat.RGB;
        Texture2D.MipmapMode mipmapMode = loadMipmaps ? Texture2D.MipmapMode.MULTI_LEVEL_MIPMAP : Texture2D.MipmapMode.BASE_LEVEL;
        
        Texture2D texture = TextureLoader.getInstance().loadTexture( in, flipMode, format, mipmapMode, allowStreching );
        
        if ( ( texture != TextureLoader.getFallbackTexture() ) && ( ( texture.getName() == null ) || ( texture.getName().equals( "" ) ) ) )
        {
            texture.setName( texName );
        }
        
        if ( ( texture == TextureLoader.getFallbackTexture() ) && !acceptFallbackTexture )
            texture = null;
        
        if ( texture != null )
        {
            texture.enableAutoFreeLocalData();
        }
        
        return ( texture );
    }
    
    public final Texture2D loadTexture( URL url, boolean flipVertically, boolean acceptAlpha, boolean loadMipmaps, boolean allowStreching, boolean acceptFallbackTexture )
    {
        TextureLoader.FlipMode flipMode = flipVertically ? TextureLoader.FlipMode.FLIPPED_VERTICALLY : TextureLoader.FlipMode.NOT_FLIPPED;
        TextureFormat format = acceptAlpha ? TextureFormat.RGBA : TextureFormat.RGB;
        Texture2D.MipmapMode mipmapMode = loadMipmaps ? Texture2D.MipmapMode.MULTI_LEVEL_MIPMAP : Texture2D.MipmapMode.BASE_LEVEL;
        
        Texture2D texture = TextureLoader.getInstance().loadTexture( url, flipMode, format, mipmapMode, allowStreching );
        
        if ( ( texture == TextureLoader.getFallbackTexture() ) && !acceptFallbackTexture )
            texture = null;
        
        if ( texture != null )
        {
            texture.enableAutoFreeLocalData();
        }
        
        return ( texture );
    }
    
    public final AbstractTexture loadOrGetTexture( String texName, URL baseURL, boolean flipVertically, boolean acceptAlpha, boolean loadMipmaps, boolean allowStreching, boolean acceptFallbackTexture )
    {
        AbstractTexture texture = null;
        
        try
        {
            URL url;
            if ( texName.indexOf( ' ' ) > 0 )
                url = new URL( baseURL, texName.replaceAll( " ", "%20" ) );
            else
                url = new URL( baseURL, texName );
            
            texture = loadTexture( url, flipVertically, acceptAlpha, loadMipmaps, allowStreching, false );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
        }
        
        if ( texture != null )
        {
            return ( texture );
        }
        
        texture = loadOrGetTexture( texName, flipVertically, acceptAlpha, loadMipmaps, allowStreching, acceptFallbackTexture );
        
        return ( texture );
    }
    
    public final Texture2D loadOrGetTexture( String texName, boolean flipVertically, boolean acceptAlpha, boolean loadMipmaps, boolean allowStreching, boolean acceptFallbackTexture )
    {
        TextureLoader.FlipMode flipMode = flipVertically ? TextureLoader.FlipMode.FLIPPED_VERTICALLY : TextureLoader.FlipMode.NOT_FLIPPED;
        TextureFormat format = acceptAlpha ? TextureFormat.RGBA : TextureFormat.RGB;
        Texture2D.MipmapMode mipmapMode = loadMipmaps ? Texture2D.MipmapMode.MULTI_LEVEL_MIPMAP : Texture2D.MipmapMode.BASE_LEVEL;
        
        Texture2D texture = TextureLoader.getInstance().getTexture( texName, flipMode, format, mipmapMode, allowStreching );
        
        if ( ( texture == TextureLoader.getFallbackTexture() ) && !acceptFallbackTexture )
            texture = null;
        
        if ( texture != null )
        {
            texture.enableAutoFreeLocalData();
        }
        
        return ( texture );
    }
    
    public final TextureImage2D createTextureImage( TextureImageFormat format, int width, int height )
    {
        TextureImage2D texImg = new TextureImage2D( format, width, height, false );
        texImg.setImageData( null, texImg.getDataSize() );
        
        return ( texImg );
    }
    
    public final TextureImage2D createTextureImage( TextureImageFormat format, int orgWidth, int orgHeight, int width, int height )
    {
        TextureImage2D texImg = new TextureImage2D( format, width, height, orgWidth, orgHeight, false );
        texImg.setImageData( null, texImg.getDataSize() );
        
        return ( texImg );
    }
    
    public final Texture2D createTexture( AbstractTextureImage texImage0, boolean generateMipmaps )
    {
        Texture2D texture = new Texture2D( TextureFormat.getFormat( texImage0.getFormat() ) );
        
        texture.setImage( 0, texImage0 );
        
        if ( generateMipmaps )
        {
            MipmapGenerator.createMipMaps( texImage0, texture, Xith3DTextureFactory2D.getInstance() );
        }
        else
        {
            texture.setImage( 0, texImage0 );
        }
        
        if ( texture != null )
        {
            texture.enableAutoFreeLocalData();
        }
        
        return ( texture );
    }
}
