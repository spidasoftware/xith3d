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
package org.xith3d.scenegraph.particles.jops;

import java.util.List;

import org.jagatoo.opengl.OGL;
import org.jagatoo.opengl.enums.BlendFunction;
import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.PerspectiveCorrectionMode;
import org.jagatoo.opengl.enums.TestFunction;
import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureCombineMode;
import org.jagatoo.opengl.enums.TextureMode;
import org.openmali.FastMath;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Vector3f;
import org.softmed.jops.Generator;
import org.softmed.jops.GeneratorBehaviour;
import org.softmed.jops.Particle;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Billboard;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * Insert comment here.
 * 
 * @author Guilherme Gomes (aka guilhermegrg)
 */
public class GeneratorShape3D extends Shape3D implements Billboard
{
    private static boolean WIRE_FRAME = false;
    private Generator generator;
    private int count;
    private Vector3f viewUp = new Vector3f();
    private Vector3f viewRight = new Vector3f();
    
    private TexCoord2f bl = new TexCoord2f();
    
    private TexCoord2f br = new TexCoord2f();
    
    private TexCoord2f tl = new TexCoord2f();
    
    //private Vector3f side = new Vector3f();
    //private Vector3f up = new Vector3f();
    private Vector3f particleRight = new Vector3f();
    
    private float correctedVAngle;
    private int[] index;
    private final float[] coordsBuffer = new float[ 9 ];
    private final float[] colorBuffer = new float[ 4 ];
    private Vector3f particleUp = new Vector3f();
    private boolean relativeOrientation = false;
    
    private static final TextureAttributes texAttribs;
    private static final RenderingAttributes renderingAttribs;
    static
    {
        //texAttribs = new TextureAttributes( TextureMode.COMBINE_REPLACE, null, null, PerspectiveCorrectionMode.NICEST );
        texAttribs = new TextureAttributes( TextureMode.MODULATE, null, null, PerspectiveCorrectionMode.NICEST );
        texAttribs.setCombineAlphaMode( TextureCombineMode.REPLACE );
        renderingAttribs = new RenderingAttributes( true, false, 0.001f, TestFunction.GREATER );
    }
    
    public static final void setWireFrameEnabled( boolean mode )
    {
        WIRE_FRAME = mode;
    }
    
    public static boolean isWireFrameEnabled()
    {
        return ( WIRE_FRAME );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final IndexedTriangleArray getGeometry()
    {
        return ( (IndexedTriangleArray)super.getGeometry() );
    }
    
    public void setGenerator( Generator generator )
    {
        this.generator = generator;
        
        setup();
    }
    
    public final Generator getGenerator()
    {
        return ( generator );
    }
    
    protected void rotate2f( TexCoord2f v, float angle )
    {
        v.set( v.getS() * FastMath.cos( angle ) - v.getT() * FastMath.sin( angle ),
               v.getT() * FastMath.cos( angle ) + v.getS() * FastMath.sin( angle )
             );
    }
    
    private void setTexture( int index, Particle particle )
    {
        final float texWidth = 1f / particle.texWidth;
        final float texHeight = 1f / particle.texHeight;
        
        bl.set( -0.5f, -0.5f );
        br.set( +1.5f, -0.5f );
        tl.set( -0.5f, +1.5f );
        
        rotate2f( bl, particle.angle );
        rotate2f( br, particle.angle );
        rotate2f( tl, particle.angle );
        
        final float bias = 0.5f;
        bl.set( bl.getS() * texWidth + bias, bl.getT() * texHeight + bias );
        br.set( br.getS() * texWidth + bias, br.getT() * texHeight + bias );
        tl.set( tl.getS() * texWidth + bias, tl.getT() * texHeight + bias );
        
        final IndexedTriangleArray geom = getGeometry();
        geom.setTextureCoordinate( 0, index + 0, bl );
        geom.setTextureCoordinate( 0, index + 1, br );
        geom.setTextureCoordinate( 0, index + 2, tl );
    }
    
    private void setColor( int index, Colorf color, float alpha )
    {
        colorBuffer[0] = color.getRed();
        colorBuffer[1] = color.getGreen();
        colorBuffer[2] = color.getBlue();
        
        colorBuffer[3] = alpha;
        
        final IndexedTriangleArray geom = getGeometry();
        geom.setColor( index, colorBuffer );
        geom.setColor( index + 1, colorBuffer );
        geom.setColor( index + 2, colorBuffer );
    }
    
    protected void buildFacingCameraTriangles( float[] carray, Particle particle )
    {
        Vector3f position = new Vector3f( particle.position );
        
        // provides rotation
        getWorldTransform().transform( position );
        
        float tsize = particle.size;
        
        float width = particle.width * 0.25f;
        float height = particle.height * 0.25f;
        
        Point3f tup = new Point3f();
        tup.set( viewUp.getX(), viewUp.getY(), viewUp.getZ() );
        tup.scale( height );
        
        Point3f tdir = new Point3f();
        tdir.set( viewRight.getX(), viewRight.getY(), viewRight.getZ() );
        tdir.scale( width );
        
        Point3f mup = new Point3f( tup );
        mup.scale( -0.5f );
        Point3f mdir = new Point3f( tdir );
        mdir.scale( -0.5f );
        
        mup.add( mdir );
        
        Point3f topleftT = new Point3f();
        Point3f bottomleftT = new Point3f();
        Point3f bottomrightT = new Point3f();
        
        topleftT.setX( tup.getX() - tdir.getX() - mup.getX() );
        topleftT.setY( tup.getY() - tdir.getY() - mup.getY() );
        topleftT.setZ( tup.getZ() - tdir.getZ() - mup.getZ() );
        
        bottomleftT.setX( -tup.getX() - tdir.getX() - mup.getX() );
        bottomleftT.setY( -tup.getY() - tdir.getY() - mup.getY() );
        bottomleftT.setZ( -tup.getZ() - tdir.getZ() - mup.getZ() );
        
        bottomrightT.setX( -tup.getX() + tdir.getX() - mup.getX() );
        bottomrightT.setY( -tup.getY() + tdir.getY() - mup.getY() );
        bottomrightT.setZ( -tup.getZ() + tdir.getZ() - mup.getZ() );
        
        bottomrightT.scale( tsize );
        bottomleftT.scale( tsize );
        topleftT.scale( tsize );
        
        carray[0] = bottomleftT.getX() + position.getX();
        carray[1] = bottomleftT.getY() + position.getY();
        carray[2] = bottomleftT.getZ() + position.getZ();
        
        carray[3] = bottomrightT.getX() + position.getX();
        carray[4] = bottomrightT.getY() + position.getY();
        carray[5] = bottomrightT.getZ() + position.getZ();
        
        carray[6] = topleftT.getX() + position.getX();
        carray[7] = topleftT.getY() + position.getY();
        carray[8] = topleftT.getZ() + position.getZ();
    }
    
    protected void buildAbsoluteOrientedTriangles( float[] carray, Particle particle/*, Vector3f up, Vector3f right*/ )
    {
        correctedVAngle = particle.angleV - FastMath.PI_HALF;
        
        particleUp.setX( FastMath.cos( particle.angleH ) * FastMath.sin( correctedVAngle ) );
        particleUp.setZ( FastMath.sin( particle.angleH ) * FastMath.sin( correctedVAngle ) );
        particleUp.setY( FastMath.cos( correctedVAngle ) );
        
        particleRight.setX( FastMath.cos( particle.angleH ) * FastMath.sin( particle.angleV ) );
        particleRight.setZ( FastMath.sin( particle.angleH ) * FastMath.sin( particle.angleV ) );
        particleRight.setY( FastMath.cos( particle.angleV ) );
        
        Vector3f side = new Vector3f();
        side.cross( particleRight, particleUp );
        
        Vector3f position = new Vector3f( particle.position );
        
        // provides rotation
        getWorldTransform().getMatrix4f().transform( position );
        
        final float tsize = particle.size * 2f;
        
        final float width = particle.width * 0.25f;
        final float height = particle.height * 0.25f;
        
        Point3f tup = new Point3f();
        tup.set( particleUp );
        tup.scale( height );
        
        Point3f tdir = new Point3f();
        tdir.set( side );
        tdir.scale( width );
        
        Point3f mup = new Point3f( tup );
        mup.scale( -0.5f );
        
        Point3f mdir = new Point3f( tdir );
        mdir.scale( -0.5f );
        
        mup.add( mdir );
        
        Point3f topLeftT = new Point3f();
        Point3f bottomLeftT = new Point3f();
        Point3f bottomRightT = new Point3f();
        
        topLeftT.setX( tup.getX() - tdir.getX() - mup.getX() );
        topLeftT.setY( tup.getY() - tdir.getY() - mup.getY() );
        topLeftT.setZ( tup.getZ() - tdir.getZ() - mup.getZ() );
        
        bottomLeftT.setX( -tup.getX() - tdir.getX() - mup.getX() );
        bottomLeftT.setY( -tup.getY() - tdir.getY() - mup.getY() );
        bottomLeftT.setZ( -tup.getZ() - tdir.getZ() - mup.getZ() );
        
        bottomRightT.setX( -tup.getX() + tdir.getX() - mup.getX() );
        bottomRightT.setY( -tup.getY() + tdir.getY() - mup.getY() );
        bottomRightT.setZ( -tup.getZ() + tdir.getZ() - mup.getZ() );
        
        bottomRightT.scale( tsize );
        bottomLeftT.scale( tsize );
        topLeftT.scale( tsize );
        
        if ( particle.rotation != null )
        {
            particle.rotation.transform( bottomRightT );
            particle.rotation.transform( bottomLeftT );
            particle.rotation.transform( topLeftT );
        }
        
        if ( relativeOrientation )
        {
            getWorldTransform().getMatrix4f().transform( bottomRightT );
            getWorldTransform().getMatrix4f().transform( bottomLeftT );
            getWorldTransform().getMatrix4f().transform( topLeftT );
        }
        
        carray[0] = bottomLeftT.getX() + position.getX();
        carray[1] = bottomLeftT.getY() + position.getY();
        carray[2] = bottomLeftT.getZ() + position.getZ();
        
        carray[3] = bottomRightT.getX() + position.getX();
        carray[4] = bottomRightT.getY() + position.getY();
        carray[5] = bottomRightT.getZ() + position.getZ();
        
        carray[6] = topLeftT.getX() + position.getX();
        carray[7] = topLeftT.getY() + position.getY();
        carray[8] = topLeftT.getZ() + position.getZ();
    }
    
    private void buildTriangles( float[] carray, Particle particle/*, Vector3f up, Vector3f right*/ )
    {
        if ( generator.isAbsoluteParticleAngle() )
        {
            buildAbsoluteOrientedTriangles( carray, particle/*, up, right*/ );
        }
        else
        {
            buildFacingCameraTriangles( carray, particle );
        }
    }
    
    public void update( Vector3f up, Vector3f right )
    {
        this.viewUp.set( up );
        this.viewRight.set( right );
    }
    
    protected void update()
    {
        count++;
        
        List<Particle> particles = generator.getParticles();
        Particle particle = null;
        int aliveParticles = 0;
        int tempVertexCount = 0;
        if ( GeneratorShape3D.isWireFrameEnabled() )
        {
            for ( int i = 0; i < particles.size(); i++ )
            {
                particle = particles.get( i );
                if ( particle.life <= 0f )
                {
                    continue;
                }
                
                tempVertexCount = aliveParticles * 3;
                
                index[tempVertexCount + 0] = tempVertexCount + 0;
                index[tempVertexCount + 1] = tempVertexCount + 1;
                index[tempVertexCount + 2] = tempVertexCount + 2;
                
                buildTriangles( coordsBuffer, particle/*, viewUp, viewRight*/ );
                setColor( i * 3, particle.color, particle.alpha );
                getGeometry().setCoordinates( i * 3, coordsBuffer );
                
                aliveParticles++;
            }
        }
        else
        {
            for ( int i = 0; i < particles.size(); i++ )
            {
                particle = particles.get( i );
                if ( particle.life <= 0f )
                {
                    continue;
                }
                
                tempVertexCount = aliveParticles * 3;
                
                index[tempVertexCount + 0] = tempVertexCount + 0;
                index[tempVertexCount + 1] = tempVertexCount + 1;
                index[tempVertexCount + 2] = tempVertexCount + 2;
                
                buildTriangles( coordsBuffer, particle/*, viewUp, viewRight*/ );
                setColor( i * 3, particle.color, particle.alpha );
                getGeometry().setCoordinates( i * 3, coordsBuffer );
                
                setTexture( i * 3, particle );
                
                aliveParticles++;
            }
        }
        
        getGeometry().setIndex( index );
        getGeometry().setInitialIndexIndex( 0 );
        getGeometry().setValidIndexCount( tempVertexCount );
    }
    
    public void updateFaceToCamera( Matrix3f viewRotation, long frameId, long nanoTime, long nanoStep )
    {
        update();
    }
    
    protected BlendFunction getBlendFactor( int glBlendFactorInteger )
    {
        //return ( BlendFunction.ONE );
        
        switch ( glBlendFactorInteger )
        {
            case OGL.GL_ZERO:
                return ( BlendFunction.ZERO );
                
            case OGL.GL_ONE:
                return ( BlendFunction.ONE );
                
            case OGL.GL_DST_ALPHA:
                return ( BlendFunction.DST_ALPHA );
                
            case OGL.GL_DST_COLOR:
                return ( BlendFunction.DST_COLOR );
                
            case OGL.GL_SRC_ALPHA:
                return ( BlendFunction.SRC_ALPHA );
                
            case OGL.GL_SRC_COLOR:
                return ( BlendFunction.SRC_COLOR );
                
            case OGL.GL_ONE_MINUS_DST_ALPHA:
                return ( BlendFunction.ONE_MINUS_DST_ALPHA );
                
            case OGL.GL_ONE_MINUS_DST_COLOR:
                return ( BlendFunction.ONE_MINUS_DST_COLOR );
                
            case OGL.GL_ONE_MINUS_SRC_ALPHA:
                return ( BlendFunction.ONE_MINUS_SRC_ALPHA );
                
            case OGL.GL_ONE_MINUS_SRC_COLOR:
                return ( BlendFunction.ONE_MINUS_SRC_COLOR );
                
            case OGL.GL_SRC_ALPHA_SATURATE:
                return ( BlendFunction.SRC_ALPHA_SATURATE );
                
            default:
                throw new RuntimeException( "GL blend mode not recognized : " + glBlendFactorInteger );
        }
    }
    
    private void setup()
    {
        if ( generator.getGb() == null || generator.getRender() == null )
            return;
        
        GeneratorBehaviour gb = generator.getGb();
        
        //final int vertexFormat = GeometryArray.COORDINATES | GeometryArray.COLOR_4 | GeometryArray.TEXTURE_COORDINATE_2;
        
        final int maxParticles = gb.getNumber();
        IndexedTriangleArray geom = new IndexedTriangleArray( maxParticles * 3 + 0, maxParticles * 3 + 0 );
        geom.setOptimization( Optimization.NONE );
        
        setGeometry( geom );
        index = new int[ gb.getNumber() * 3 ];
        geom.setIndex( index );
        
        Texture texture = TextureLoader.getInstance().getTexture( generator.getRender().getTextureName(), Texture.MipmapMode.MULTI_LEVEL_MIPMAP );
        texture.setBoundaryModeS( TextureBoundaryMode.CLAMP_TO_BORDER );
        texture.setBoundaryModeT( TextureBoundaryMode.CLAMP_TO_BORDER );
        
        TransparencyAttributes transparencyAttribs = new TransparencyAttributes( TransparencyAttributes.BLENDED, 0.1f, getBlendFactor( generator.getRender().getSourceFactor() ), getBlendFactor( generator.getRender().getDestinationFactor() ) );
        transparencyAttribs.setEnabled( true );
        transparencyAttribs.setSortEnabled( true );
        
        Appearance app = new Appearance();
        
        if ( WIRE_FRAME )
        {
            PolygonAttributes pa = new PolygonAttributes( DrawMode.LINE );
            app.setPolygonAttributes( pa );
        }
        else
        {
            app.setTexture( texture );
            app.setTextureAttributes( texAttribs );
            app.setRenderingAttributes( renderingAttribs );
            app.setTransparencyAttributes( transparencyAttribs );
        }
        
        //app.setLineAttributes(new LineAttributes( 1f, LineAttributes.PATTERN_SOLID, false ) );
        //app.setDrawMode( DrawMode.LINE );
        
        setAppearance( app );
    }
    
    /**
     * {@inheritDoc}
     */
    public Sized2iRO getSizeOnScreen()
    {
        return ( null );
    }
    
    public void dispose()
    {
        generator = null;
    }
    
    public boolean isAbsoluteOrientation()
    {
        return ( relativeOrientation );
    }
    
    public void setAbsoluteOrientation( boolean absoluteOrientation )
    {
        this.relativeOrientation = absoluteOrientation;
    }
    
    public GeneratorShape3D( boolean relativeOrientation )
    {
        this.relativeOrientation = relativeOrientation;
    }
    
    public GeneratorShape3D( boolean relativeOrientation, Generator generator )
    {
        setGenerator( generator );
        
        this.relativeOrientation = relativeOrientation;
    }
}
