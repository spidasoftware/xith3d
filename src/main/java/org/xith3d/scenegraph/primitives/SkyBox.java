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
package org.xith3d.scenegraph.primitives;

import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loaders.texture.TextureLoader.FlipMode;
import org.xith3d.render.BackgroundRenderPass;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.View.CameraMode;
import org.xith3d.utility.texturing.CubeTextureSet;

/**
 * A SkyBox is a special type of Background Node.  It is a six sided cube which is 
 * actually very small (20 x 20 x 20).  It is drawn with the depth buffer disabled,
 * so all objects which are drawn after it will be drawn in front.  As it is a background
 * node, it will always be drawn as if the camera was in its center regardless of the
 * actual position of the camera.  For realism, the six textures need to be carefully constructed
 * with a 90 degree field of view.  Typically this can be done in most 3d terrain generation
 * programs (for example Terragen).
 * 
 * @author William Denniss
 * @author Marvin Froehlich (aka Qudus)
 */
public class SkyBox extends BackgroundRenderPass
{
    private static final boolean DEFAULT_TEXTURE_FLIP = false;
    
    private static final float DEFAULT_SIZE = 20f;
    
    public static <G extends GroupNode> G createSkyBoxGroup( float size, Texture[] textures, G group )
    {
        if ( textures.length != 6 )
        {
            throw new IllegalArgumentException( "The given array of Textures MUST be of length 6." );
        }
        
        final float halfSize = size / 2f;
        
        // vertex-coords of the cube...
        final Point3f leftTopFront     = new Point3f( -halfSize,  halfSize,  halfSize );
        final Point3f rightTopFront    = new Point3f(  halfSize,  halfSize,  halfSize );
        final Point3f rightBottomFront = new Point3f(  halfSize, -halfSize,  halfSize );
        final Point3f leftBottomFront  = new Point3f( -halfSize, -halfSize,  halfSize );
        final Point3f leftTopBack      = new Point3f( -halfSize,  halfSize, -halfSize );
        final Point3f rightTopBack     = new Point3f(  halfSize,  halfSize, -halfSize );
        final Point3f rightBottomBack  = new Point3f(  halfSize, -halfSize, -halfSize );
        final Point3f leftBottomBack  =  new Point3f( -halfSize, -halfSize, -halfSize );
        
        /*
        // Coordinates for 6 Quads
        Point3f[][] vertices = new Point3f[][]
        {
            {
                // back
                leftBottomBack, rightBottomBack, leftTopBack,
                //leftTopBack, rightBottomBack,
                rightTopBack
            },
            {
                // right
                rightBottomBack, rightBottomFront, rightTopBack,
                //rightTopBack, rightBottomFront,
                rightTopFront
            },
            {
                // front
                rightBottomFront, leftBottomFront, rightTopFront,
                //rightTopFront, leftBottomFront,
                leftTopFront
            },
            {
                // left
                leftBottomFront, leftBottomBack, leftTopFront,
                //leftTopFront, leftBottomBack,
                leftTopBack
            },
            {
                // top
                leftTopBack, rightTopBack, leftTopFront,
                //leftTopFront, rightTopBack,
                rightTopFront
            },
            {
                // bottom
                leftBottomFront, rightBottomFront, leftBottomBack,
                //leftBottomBack, rightBottomFront,
                rightBottomBack
            }
        };
        */
        
        /*
        // Coordinates for 6 Quads
        Point3f[][] vertices = new Point3f[][]
        {
            {
                leftTopFront, rightTopFront, rightBottomFront, leftBottomFront
            },
            {
                rightTopFront, rightTopBack, rightBottomBack, rightBottomFront
            },
            {
                rightTopBack, leftTopBack, leftBottomBack, rightBottomBack
            },
            {
                leftTopBack, leftTopFront, leftBottomFront, leftBottomBack
            },
            {
                leftTopBack, rightTopBack, rightTopFront, leftTopFront
            },
            {
                leftBottomFront, rightBottomFront, rightBottomBack, leftBottomBack
            },
        };
        */
        Point3f[][] vertices = new Point3f[][]
        {
            {
                leftTopFront, rightTopFront, leftBottomFront, rightBottomFront
            },
            {
                rightTopFront, rightTopBack, rightBottomFront, rightBottomBack
            },
            {
                rightTopBack, leftTopBack, rightBottomBack, leftBottomBack
            },
            {
                leftTopBack, leftTopFront, leftBottomBack, leftBottomFront
            },
            {
                leftTopBack, rightTopBack, leftTopFront, rightTopFront
            },
            {
                leftBottomFront, rightBottomFront, leftBottomBack, rightBottomBack
            },
        };
        
        // Texture coordinates (same for all sides)
        /*
        TexCoord2f[] texCoords = new TexCoord2f[]
        {
            new TexCoord2f( 0f, 0f ),
            new TexCoord2f( 1f, 0f ),
            new TexCoord2f( 0f, 1f ),
            new TexCoord2f( 1f, 1f ),
        };
        */
        /*
        TexCoord2f[] texCoords = new TexCoord2f[]
        {
            new TexCoord2f( 0f, 1f ),
            new TexCoord2f( 1f, 1f ),
            new TexCoord2f( 1f, 0f ),
            new TexCoord2f( 0f, 0f ),
        };
        */
        TexCoord2f[] texCoords = new TexCoord2f[]
        {
            new TexCoord2f( 0f, 1f ),
            new TexCoord2f( 1f, 1f ),
            new TexCoord2f( 0f, 0f ),
            new TexCoord2f( 1f, 0f ),
        };
        
        Shape3D[] sh = new Shape3D[ 6 ];
        
        RenderingAttributes ra = new RenderingAttributes();
        ra.setDepthBufferWriteEnabled( false );
        
        for ( int i = 0; i < sh.length; i++ )
        {
            // Skips this side if the texture is null (i.e. the side isn't needed).
            if ( textures[ i ] == null )
            {
                continue;
            }
            
            // Creates the side
            //QuadArray quads = new QuadArray( vertices[ i ].length );
            //TriangleArray quads = new TriangleArray( vertices[ i ].length );
            TriangleStripArray quads = new TriangleStripArray( vertices[ i ].length );
            quads.setOptimization( Optimization.USE_VBOS );
            quads.setCoordinates( 0, vertices[ i ] );
            quads.setTextureCoordinates( 0, 0, texCoords );
            //quads.calculateFaceNormals();
            
            // Clamped texture
            Texture texture = textures[ i ];
            texture.setBoundaryModeS( TextureBoundaryMode.CLAMP_TO_EDGE );
            texture.setBoundaryModeT( TextureBoundaryMode.CLAMP_TO_EDGE );
            
            // Sets texture and diables depth buffer (so all nodes drawn after this one will be drawn in front)
            Appearance a = new Appearance();
            a.setRenderingAttributes( ra );
            a.setTexture( texture );
            
            sh[ i ] = new Shape3D( quads, a);
            
            group.addChild( sh[ i ] );
        }
        
        group.setPickableRecursive( false );
        
        return ( group );
    }
    
    public static <G extends GroupNode> G createSkyBoxGroup( Texture[] textures, G group )
    {
        return ( createSkyBoxGroup( DEFAULT_SIZE, textures, group ) );
    }
    
    public static BranchGroup createSkyBoxGroup( float size, Texture[] textures )
    {
        BranchGroup skybox = new BranchGroup();
        
        createSkyBoxGroup( size, textures, skybox );
        
        //skybox.setPickableRecursive( false );
        
        return ( skybox );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture, and those sides which <i>null</i> was passed
     * will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param front The front image (0 degrees rotation, 0 degrees pitch)
     * @param right The right image (90 degrees rotation, 0 degrees pitch)
     * @param back The back image (180 degrees rotation, 0 degrees pitch)
     * @param left The left image (270 degrees rotation, 0 degrees pitch)
     * @param top The top image (0 degrees rotation, -90 degrees pitch)
     * @param bottom The bottom image (0 degrees rotation, 90 degrees pitch)
     */
    public static final BranchGroup createSkyBoxGroup( Texture front, Texture right, Texture back, Texture left, Texture top, Texture bottom )
    {
        return ( createSkyBoxGroup( DEFAULT_SIZE, new Texture[] { front, right, back, left, top, bottom } ) );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was passed
     * will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param textures a six elemental array of Textures of the following form:
     * <blockquote>
     *     [0] The front image (0 degrees rotation, 0 degrees pitch)
     *     [1] The right image (90 degrees rotation, 0 degrees pitch)
     *     [2] The back image (180 degrees rotation, 0 degrees pitch)
     *     [3] The left image (270 degrees rotation, 0 degrees pitch)
     *     [4] The top image (0 degrees rotation, -90 degrees pitch)
     *     [5] The bottom image (0 degrees rotation, 90 degrees pitch)
     * </blockquote>
     * @param flipTextures flip textures vertically? 
     */
    public static final BranchGroup createSkyBoxGroup( String[] textures, boolean flipTextures )
    {
        return ( createSkyBoxGroup( textures[ 0 ], textures[ 1 ], textures[ 2 ], textures[ 3 ], textures[ 4 ], textures[ 5 ], flipTextures ) );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was passed
     * will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param textures a six elemental array of Textures of the following form:
     * <blockquote>
     *     [0] The front image (0 degrees rotation, 0 degrees pitch)
     *     [1] The right image (90 degrees rotation, 0 degrees pitch)
     *     [2] The back image (180 degrees rotation, 0 degrees pitch)
     *     [3] The left image (270 degrees rotation, 0 degrees pitch)
     *     [4] The top image (0 degrees rotation, -90 degrees pitch)
     *     [5] The bottom image (0 degrees rotation, 90 degrees pitch)
     * </blockquote> 
     */
    public static final BranchGroup createSkyBoxGroup( String[] textures )
    {
        return ( createSkyBoxGroup( textures, DEFAULT_TEXTURE_FLIP ) );
    }
    
    private static Texture getTextureOrNull( String texture, boolean flipVertically )
    {
        if ( texture == null )
            return ( null );
        
        TextureLoader tl = TextureLoader.getInstance();
        
        tl.getCache().pushEnabled( false );
        
        /*
         * In OpenGL a flipped texture is the "normal" case.
         * Therefore we use (flipVertically == true) -> NOT_FLIPPED.
         */
        FlipMode flipMode = flipVertically ? FlipMode.NOT_FLIPPED : FlipMode.FLIPPED_VERTICALLY;
        
        Texture tex = tl.getTexture( texture, flipMode, TextureFormat.RGB, Texture.MipmapMode.BASE_LEVEL );
        tl.getCache().popEnabled();
        
        tex.enableAutoFreeLocalData();
        
        return ( tex );
    }
    
    private static Texture[] createTexturesArray( String front, String right, String back, String left, String top, String bottom, boolean flipVertically )
    {
        Texture[] textures = new Texture[ 6 ];
        
        textures[ 0 ] = getTextureOrNull( front, flipVertically );
        textures[ 1 ] = getTextureOrNull( right, flipVertically );
        textures[ 2 ] = getTextureOrNull( back, flipVertically );
        textures[ 3 ] = getTextureOrNull( left, flipVertically );
        textures[ 4 ] = getTextureOrNull( top, flipVertically );
        textures[ 5 ] = getTextureOrNull( bottom, flipVertically );
        
        for ( int i = 0; i < textures.length; i++ )
        {
            textures[ i ].setBoundaryModeS( TextureBoundaryMode.CLAMP_TO_EDGE );
            textures[ i ].setBoundaryModeT( TextureBoundaryMode.CLAMP_TO_EDGE );
        }
        
        return ( textures );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was
     * passed will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param front The front image (0 degrees rotation, 0 degrees pitch)
     * @param right The right image (90 degrees rotation, 0 degrees pitch)
     * @param back The back image (180 degrees rotation, 0 degrees pitch)
     * @param left The left image (270 degrees rotation, 0 degrees pitch)
     * @param top The top image (0 degrees rotation, -90 degrees pitch)
     * @param bottom The bottom image (0 degrees rotation, 90 degrees pitch)
     * @param flipVertically
     */
    public static final BranchGroup createSkyBoxGroup( String front, String right, String back, String left, String top, String bottom, boolean flipVertically )
    {
        return ( createSkyBoxGroup( DEFAULT_SIZE, createTexturesArray( front, right, back, left, top, bottom, flipVertically ) ) );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was
     * passed will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param front The front image (0 degrees rotation, 0 degrees pitch)
     * @param right The right image (90 degrees rotation, 0 degrees pitch)
     * @param back The back image (180 degrees rotation, 0 degrees pitch)
     * @param left The left image (270 degrees rotation, 0 degrees pitch)
     * @param top The top image (0 degrees rotation, -90 degrees pitch)
     * @param bottom The bottom image (0 degrees rotation, 90 degrees pitch)
     */
    public static final BranchGroup createSkyBoxGroup( String front, String right, String back, String left, String top, String bottom )
    {
        return ( createSkyBoxGroup( front, right, back, left, top, bottom, DEFAULT_TEXTURE_FLIP ) );
    }
    
    public static final BranchGroup createSkyBoxGroup( CubeTextureSet textureSet )
    {
        return ( createSkyBoxGroup( textureSet.getFrontTexture(), textureSet.getRightTexture(), textureSet.getBackTexture(), textureSet.getLeftTexture(), textureSet.getTopTexture(), textureSet.getBottomTexture() ) );
    }
    
    
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture, and those sides which <i>null</i> was passed
     * will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param textures a six elemental array of Textures of the following form:
     * <blockquote>
     *     [0] The front image (0 degrees rotation, 0 degrees pitch)
     *     [1] The right image (90 degrees rotation, 0 degrees pitch)
     *     [2] The back image (180 degrees rotation, 0 degrees pitch)
     *     [3] The left image (270 degrees rotation, 0 degrees pitch)
     *     [4] The top image (0 degrees rotation, -90 degrees pitch)
     *     [5] The bottom image (0 degrees rotation, 90 degrees pitch)
     * </blockquote> 
     */
    public SkyBox( Texture[] textures )
    {
        super( createSkyBoxGroup( DEFAULT_SIZE, textures ), new BaseRenderPassConfig( CameraMode.VIEW_FIXED_POSITION ) );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture, and those sides which <i>null</i> was passed
     * will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param front The front image (0 degrees rotation, 0 degrees pitch)
     * @param right The right image (90 degrees rotation, 0 degrees pitch)
     * @param back The back image (180 degrees rotation, 0 degrees pitch)
     * @param left The left image (270 degrees rotation, 0 degrees pitch)
     * @param top The top image (0 degrees rotation, -90 degrees pitch)
     * @param bottom The bottom image (0 degrees rotation, 90 degrees pitch)
     */
    public SkyBox( Texture front, Texture right, Texture back, Texture left, Texture top, Texture bottom )
    {
        this( new Texture[] { front, right, back, left, top, bottom } );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was passed
     * will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param textures a six elemental array of Textures of the following form:
     * <blockquote>
     *     [0] The front image (0 degrees rotation, 0 degrees pitch)
     *     [1] The right image (90 degrees rotation, 0 degrees pitch)
     *     [2] The back image (180 degrees rotation, 0 degrees pitch)
     *     [3] The left image (270 degrees rotation, 0 degrees pitch)
     *     [4] The top image (0 degrees rotation, -90 degrees pitch)
     *     [5] The bottom image (0 degrees rotation, 90 degrees pitch)
     * </blockquote> 
     */
    public SkyBox( String[] textures, boolean flipVertically )
    {
        this( textures[ 0 ], textures[ 1 ], textures[ 2 ], textures[ 3 ], textures[ 4 ], textures[ 5 ], flipVertically );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was passed
     * will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param textures a six elemental array of Textures of the following form:
     * <blockquote>
     *     [0] The front image (0 degrees rotation, 0 degrees pitch)
     *     [1] The right image (90 degrees rotation, 0 degrees pitch)
     *     [2] The back image (180 degrees rotation, 0 degrees pitch)
     *     [3] The left image (270 degrees rotation, 0 degrees pitch)
     *     [4] The top image (0 degrees rotation, -90 degrees pitch)
     *     [5] The bottom image (0 degrees rotation, 90 degrees pitch)
     * </blockquote> 
     */
    public SkyBox( String[] textures )
    {
        this( textures, DEFAULT_TEXTURE_FLIP );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was
     * passed will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param front The front image (0 degrees rotation, 0 degrees pitch)
     * @param right The right image (90 degrees rotation, 0 degrees pitch)
     * @param back The back image (180 degrees rotation, 0 degrees pitch)
     * @param left The left image (270 degrees rotation, 0 degrees pitch)
     * @param top The top image (0 degrees rotation, -90 degrees pitch)
     * @param bottom The bottom image (0 degrees rotation, 90 degrees pitch)
     * @param flipVertically
     */
    public SkyBox( String front, String right, String back, String left, String top, String bottom, boolean flipVertically )
    {
        this( createTexturesArray( front, right, back, left, top, bottom, flipVertically ) );
    }
    
    /**
     * Creates a new SkyBox using the six given Textures. <i>Null</i> may be
     * passed instead of the Texture filename, and those sides which <i>null</i> was
     * passed will not be included in the SkyBox (e.g. if you don't need a bottom or top
     * to the SkyBox, simply pass <i>null</i> for those Textures).
     * 
     * @param front The front image (0 degrees rotation, 0 degrees pitch)
     * @param right The right image (90 degrees rotation, 0 degrees pitch)
     * @param back The back image (180 degrees rotation, 0 degrees pitch)
     * @param left The left image (270 degrees rotation, 0 degrees pitch)
     * @param top The top image (0 degrees rotation, -90 degrees pitch)
     * @param bottom The bottom image (0 degrees rotation, 90 degrees pitch)
     */
    public SkyBox( String front, String right, String back, String left, String top, String bottom )
    {
        this( front, right, back, left, top, bottom, DEFAULT_TEXTURE_FLIP );
    }
    
    public SkyBox( CubeTextureSet textureSet )
    {
        this( textureSet.getFrontTexture(), textureSet.getRightTexture(), textureSet.getBackTexture(), textureSet.getLeftTexture(), textureSet.getTopTexture(), textureSet.getBottomTexture() );
    }
}
