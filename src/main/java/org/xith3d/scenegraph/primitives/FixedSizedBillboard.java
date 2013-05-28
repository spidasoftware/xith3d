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

import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.FastMath;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Billboard;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * A simple implementation for the Billboard interface. It does not inherit from
 * Rectangle or TextRectangle as RectBillboard and TextBillboard do to avoid
 * geometry duplication.
 * 
 * Moreover, features like creating a text Billboard should go into a factory
 * class and not clutter this class with numerous constructors.
 * 
 * @see FixedSizedBillboardFactory
 * 
 * @author Herve
 */
public class FixedSizedBillboard extends Shape3D implements Billboard
{
    private long lastFrameId = -Long.MAX_VALUE;
    private final float width;
    private final float height;
    private final Point3f center;
    private final Point3f[] vertices;
    private Dim2i sizeOnScreen;
    private final float defaultBoundingRadius;
    private final Vector3f radiusPoint = new Vector3f();
    
    /**
     * Sets the texture used by this billboard.
     * 
     * @param texture the texture to use, or {@code null} to remove the texture.
     * @param texLowerLeft the texture lower left coordinate. It can be
     *        {@code null} in which case the (0f;0f) value is assumed.
     * @param texUpperRight the texture upper right coordinate. It can be
     *        {@code null} in which case the (1f;1f) value is assumed.
     */
    public void setTexture( Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        if ( texture != null )
        {
            final Appearance appearance = getAppearance( true );
            appearance.setTexture( texture );
            if ( texture.getFormat() == TextureFormat.RGBA )
            {
                final TransparencyAttributes transparency = appearance.getTransparencyAttributes( true );
                transparency.setMode( TransparencyAttributes.BLENDED );
                transparency.setTransparency( 0f );
                appearance.setTransparencyAttributes( transparency );
            }
            setTexturePosition( texLowerLeft, texUpperRight );
        }
        else
        {
            Appearance appearance = getAppearance();
            if ( appearance != null )
            {
                appearance.setTexture( (Texture)null );
            }
        }
    }
    
    /**
     * Sets the new texture-coordinates for this Rectangle.
     * 
     * @param texLowerLeft the texture lower left coordinate. It can be
     *        {@code null} in which case the (0f;0f) value is assumed.
     * @param texUpperRight the texture upper right coordinate. It can be
     *        {@code null} in which case the (1f;1f) value is assumed.
     */
    public void setTexturePosition( Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        TexCoord2f textureCoordinates[] =
        {
            new TexCoord2f( 0f, 0f ), new TexCoord2f( 1f, 0f ), new TexCoord2f( 0f, 1f ), new TexCoord2f( 1f, 1f )
        };
        if ( texLowerLeft != null )
        {
            textureCoordinates[ 0 ].setS( texLowerLeft.getX() );
            textureCoordinates[ 0 ].setT( texLowerLeft.getY() );
            textureCoordinates[ 1 ].setT( texLowerLeft.getY() );
            textureCoordinates[ 2 ].setS( texLowerLeft.getX() );
        }
        
        if ( texUpperRight != null )
        {
            textureCoordinates[ 1 ].setS( texUpperRight.getX() );
            textureCoordinates[ 2 ].setT( texUpperRight.getY() );
            textureCoordinates[ 3 ].setS( texUpperRight.getX() );
            textureCoordinates[ 3 ].setT( texUpperRight.getY() );
        }
        
        final TriangleStripArray geometry = this.getGeometry();
        geometry.setTextureCoordinates( 0, 0, textureCoordinates );
    }
    
    /**
     * Returns the desired size on screen for this billboard, in pixels.
     * @return the desired size on screen, or {@code null} for a classical
     *         billboard.
     */
    public Sized2iRO getSizeOnScreen()
    {
        if ( sizeOnScreen == null )
        {
            return ( null );
        }
        
        if ( ( sizeOnScreen.getWidth() <= 0 ) || ( sizeOnScreen.getHeight() <= 0 ) )
        {
            return ( null );
        }
        
        return ( sizeOnScreen );
    }
    
    /**
     * Sets the desired size on screen in pixels. If any of the given values are
     * negative or zero, the {@link #getSizeOnScreen()} method will return
     * {@code null}.
     * @param width the desired width in pixels.
     * @param height the desired height in pixels.
     */
    public void setSizeOnScreen( int width, int height )
    {
        if ( sizeOnScreen == null )
        {
            this.sizeOnScreen = new Dim2i();
        }
        
        this.sizeOnScreen.set( width, height );
    }
    
    /**
     * Interface implementation.
     */
    public void updateFaceToCamera( final Matrix3f viewRotation, final long frameId, long nanoTime, long nanoStep )
    {
        // Check frame number
        if ( frameId <= this.lastFrameId )
        {
            return;
        }
        this.lastFrameId = frameId;
        
        // Update vertices values
        setDefaultVerticesValues();
        radiusPoint.set( vertices[ 0 ].getX(), 0f, 0f );
        
        for ( int i = 0; i < vertices.length; i++ )
        {
            viewRotation.transform( vertices[ i ] );
        }
        
        // Update bounds (needed for pickable constant-size-on-screen billboards)
        viewRotation.transform( radiusPoint );
        ( (BoundingSphere)getBounds() ).setRadius( radiusPoint.length() );
        
        setBoundsDirty();
        
        // Update geometry
        this.getGeometry().setCoordinates( 0, this.vertices );
    }
    
    @Override
    public TriangleStripArray getGeometry()
    {
        return ( (TriangleStripArray)super.getGeometry() );
    }
    
    private static TriangleStripArray createGeometry( final Tuple3f[] coordinates )
    {
        final TriangleStripArray geometry = new TriangleStripArray( 4 );
        geometry.setCoordinates( 0, coordinates );
        // IMPORTANT: if optimization is set to another value than NONE, then
        // the geometry is not updated to face the camera.
        geometry.setOptimization( Optimization.NONE );
        
        return ( geometry );
    }
    
    /**
     * Sets the Billboard vertices values to their default (non-rotated) values.
     * 
     */
    private void setDefaultVerticesValues()
    {
        final float x0 = center.getX();
        final float y0 = center.getY();
        final float z0 = center.getZ();
        final float w0 = sizeOnScreen != null ? 0.5f : width / 2f;
        final float h0 = sizeOnScreen != null ? 0.5f : height / 2f;
        
        vertices[ 0 ].set( x0 - w0, y0 - h0, z0 );
        vertices[ 1 ].set( x0 + w0, y0 - h0, z0 );
        vertices[ 2 ].set( x0 - w0, y0 + h0, z0 );
        vertices[ 3 ].set( x0 + w0, y0 + h0, z0 );
    }
    
    private static Point3f[] createVertices()
    {
        return ( new Point3f[]
        {
            new Point3f(), new Point3f(), new Point3f(), new Point3f()
        } );
    }
    
    /**
     * Constructor.
     * @param center the billboard center. Can be {@code null}, and will
     *        usually be.
     * @param width the billboard width.
     * @param height the billboard height.
     */
    public FixedSizedBillboard( Tuple3f center, float width, float height )
    {
        this.center = ( center == null ? new Point3f() : new Point3f( center ) );
        this.width = width;
        this.height = height;
        this.vertices = createVertices();
        this.sizeOnScreen = null;
        this.defaultBoundingRadius = FastMath.sqrt( width * width + height * height ) / 2f;
        setDefaultVerticesValues();
        setGeometry( createGeometry( this.vertices ) );
        setBounds( new BoundingSphere( this.center, defaultBoundingRadius ) );
    }
}
