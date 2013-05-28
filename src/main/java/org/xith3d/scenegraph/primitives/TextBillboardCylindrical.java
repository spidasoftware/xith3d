/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xith3d.scenegraph.primitives;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Transform3D;

import java.awt.*;

/**
 *
 * @author mford
 */
public class TextBillboardCylindrical extends TextBillboard {



        /**
     * {@inheritDoc}
     */
    @Override
    public void updateFaceToCamera( Matrix3f viewRotation, long frameId, long nanoTime, long nanoStep )
    {
        if ( frameId <= lastFrameId )
            return;

        // test stuff for cylindrical billboard
        Transform3D tempTransform = new Transform3D(viewRotation);
        Tuple3f eulers = new Tuple3f();
        tempTransform.getEuler(eulers);

        float yaw = eulers.getY();


        Matrix3f newViewRotation = new Matrix3f();
        newViewRotation.rotY(yaw);
        for ( int i = 0; i < 4; i++ )
        {
            transformedVertices[ i ].set( zeroVertices[ i ] );

//            viewRotation.transform( transformedVertices[ i ] );
            newViewRotation.transform( transformedVertices[ i ] );
        }

        this.getGeometry().setCoordinates( 0, transformedVertices );

        lastFrameId = frameId;
    }
       /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param position relative Location of the Rectangle
     * @param texture the text-texture to apply
     */
    protected TextBillboardCylindrical( float width, float height, Tuple3f position, Texture texture )
    {
        super( width, height, position, texture );

        this.zeroVertices = createVertexCoords( width, height, position );

        getGeometry().setOptimization( Optimization.NONE );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the text-texture to apply
     */
    protected TextBillboardCylindrical( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture )
    {
        super( width, height, zpl, zOffset, texture );

        this.zeroVertices = createVertexCoords( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ) );

        getGeometry().setOptimization( Optimization.NONE );
    }

   /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedWidth( float width, Tuple3f position, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float height = getHeightFromFixedWidth( width, texture );

        final TextBillboardCylindrical rect = new TextBillboardCylindrical( width, height, position, texture );

        return ( rect );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedWidth( float width, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float height = getHeightFromFixedWidth( width, texture );

        final TextBillboardCylindrical rect = new TextBillboardCylindrical( width, height, new Vector3f( 0f, 0f, zOffset ), texture );

        return ( rect );
    }

    /**
     * Creates a TextRectangle.
     *
     * @param alignment the text horizontal alignment
     * @param width (fixed) width of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedWidth( float width, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float height = getHeightFromFixedWidth( width, texture );

        final TextBillboardCylindrical rect = new TextBillboardCylindrical( width, height, (Tuple3f)null, texture );

        return ( rect );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedWidth( float width, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float height = getHeightFromFixedWidth( width, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, zOffset, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedWidth( float width, ZeroPointLocation zpl, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float height = getHeightFromFixedWidth( width, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, 0.0f, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedWidth( float width, Tuple3f position, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float height = getHeightFromFixedWidth( width, texture );

        final TextBillboardCylindrical rect = new TextBillboardCylindrical( width, height, position, texture );

        return ( rect );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedWidth( float width, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float height = getHeightFromFixedWidth( width, texture );

        final TextBillboardCylindrical rect = new TextBillboardCylindrical( width, height, new Vector3f( 0f, 0f, zOffset ), texture );

        return ( rect );
    }

    /**
     * Creates a TextRectangle.
     *
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param width (fixed) width of the Rectangle
     */
    public static TextBillboardCylindrical createFixedWidth( float width, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float height = getHeightFromFixedWidth( width, texture );

        final TextBillboardCylindrical rect = new TextBillboardCylindrical( width, height, (Tuple3f)null, texture );

        return ( rect );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedWidth( float width, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float height = getHeightFromFixedWidth( width, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, zOffset, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param width (fixed) width of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedWidth( float width, ZeroPointLocation zpl, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float height = getHeightFromFixedWidth( width, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, 0.0f, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedHeight( float height, Tuple3f position, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, position, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedHeight( float height, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedHeight( float height, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, (Tuple3f)null, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedHeight( float height, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, zOffset, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     * @param alignment the text horizontal alignment
     */
    public static TextBillboardCylindrical createFixedHeight( float height, ZeroPointLocation zpl, String text, Colorf color, Font font, int alignment )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, alignment );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, 0.0f, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param position relative Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedHeight( float height, Tuple3f position, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, position, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedHeight( float height, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, new Vector3f( 0f, 0f, zOffset ), texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedHeight( float height, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, (Tuple3f)null, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedHeight( float height, ZeroPointLocation zpl, float zOffset, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, zOffset, texture ) );
    }

    /**
     * Creates a TextBillboardCylindrical.
     *
     * @param height (fixed) height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param text the text to render on the Rectangle
     * @param color the color to use for the text
     * @param font the Font to use for the text
     */
    public static TextBillboardCylindrical createFixedHeight( float height, ZeroPointLocation zpl, String text, Colorf color, Font font )
    {
        final Texture texture = TextureCreator.createTexture( text, color, font, TEXT_ALIGNMENT_HORIZONTAL_LEFT );

        final float width = getWidthFromFixedHeight( height, texture );

        return ( new TextBillboardCylindrical( width, height, zpl, 0.0f, texture ) );
    }
}
