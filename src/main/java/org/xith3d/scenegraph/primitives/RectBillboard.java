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

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Billboard;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.Texture.MipmapMode;
import org.xith3d.scenegraph.utils.CopyListener;

/**
 * This is a reguar Rectangle, which will always face the View.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class RectBillboard extends Rectangle implements Billboard
{
    private long lastFrameId = -Long.MAX_VALUE;
    
    protected Tuple3f[] zeroVertices;
    protected Point3f[] transformedVertices = new Point3f[]
    {
        new Point3f(), new Point3f(), new Point3f(), new Point3f()
    };
    
    /**
     * {@inheritDoc}
     */
    public Sized2iRO getSizeOnScreen()
    {
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateFaceToCamera( Matrix3f viewRotation, long frameId, long nanoTime, long nanoStep )
    {
        if ( frameId <= lastFrameId )
            return;
        
        for ( int i = 0; i < 4; i++ )
        {
            transformedVertices[ i ].set( zeroVertices[ i ] );
            viewRotation.transform( transformedVertices[ i ] );
        }
        
        this.getGeometry().setCoordinates( 0, transformedVertices );
        
        lastFrameId = frameId;
    }
    
    private static void copy( RectBillboard src, RectBillboard dest )
    {
        dest.setAppearance( src.getAppearance() );
        dest.setGeometry( src.getGeometry() );
        dest.setBoundsAutoCompute( false );
        dest.setBounds( src.getBounds() );
        dest.boundsDirty = true;
        dest.updateBounds( false );
        dest.setPickable( src.isPickable() );
        dest.setRenderable( src.isRenderable() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void absorbDetails( Node node )
    {
        copy( (RectBillboard)node, this );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RectBillboard sharedCopy( CopyListener listener )
    {
        final boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        // TODO: create protected constructors, that don't create a "default" geometry"!
        RectBillboard result = new RectBillboard( getWidth(), getHeight(), getTexture() );
        Node.globalIgnoreBounds = gib;
        
        result.setColor( getColor() );
        result.setAlpha( getAlpha() );
        
        copy( this, result );
        
        if ( listener != null )
        {
            listener.onNodeCopied( this, result, true );
        }
        
        return ( result );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RectBillboard sharedCopy()
    {
        return ( (RectBillboard)super.sharedCopy() );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     * @param color the color for the ColoringAttributes
     */
    public RectBillboard( float width, float height, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        super( width, height, offset, texture, texLowerLeft, texUpperRight, color );
        
        getGeometry().setOptimization( Optimization.NONE );
        
        this.zeroVertices = createVertexCoords( width, height, ( offset != null ? offset : new Vector3f( 0.0f, 0.0f, 0.0f ) ) );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public RectBillboard( float width, float height, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight, Colorf color )
    {
        this( width, height, offset, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ), texLowerLeft, texUpperRight, color );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, String texture )
    {
        this( width, height, (Tuple3f)null, texture, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, Tuple3f offset, String texture )
    {
        this( width, height, offset, texture, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, float zOffset, String texture )
    {
        this( width, height, new Vector3f( 0f, 0f, zOffset ), texture, null, null, null );
    }
    
    /**
     * Creates an untextured RectBillboard without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     */
    public RectBillboard( float width, float height, float zOffset )
    {
        this( width, height, new Vector3f( 0f, 0f, zOffset ), (Texture)null, null, null, null );
    }
    
    /**
     * Creates an untextured RectBillboard without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset
     */
    public RectBillboard( float width, float height, Tuple3f offset )
    {
        this( width, height, offset, (Texture)null, null, null, null );
    }
    
    /**
     * Creates an untextured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     */
    public RectBillboard( float width, float height )
    {
        this( width, height, null, (Texture)null, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public RectBillboard( float width, float height, Tuple3f offset, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, offset, texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, Texture texture )
    {
        this( width, height, (Tuple3f)null, texture, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, Tuple3f offset, Texture texture )
    {
        this( width, height, offset, texture, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, float zOffset, Texture texture )
    {
        this( width, height, new Vector3f( 0f, 0f, zOffset ), texture, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public RectBillboard( float width, float height, float zOffset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, new Vector3f( 0f, 0f, zOffset ), texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public RectBillboard( float width, float height, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, (Tuple3f)null, texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param texture the texture
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public RectBillboard( float width, float height, Tuple3f offset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, offset, texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates an untextured, but colored RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param offset relative Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public RectBillboard( float width, float height, Tuple3f offset, Colorf color )
    {
        this( width, height, offset, (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zOffset relative z-Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public RectBillboard( float width, float height, float zOffset, Colorf color )
    {
        this( width, height, new Vector3f( 0f, 0f, zOffset ), (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public RectBillboard( float width, float height, Colorf color )
    {
        this( width, height, null, (Texture)null, null, null, color );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, Texture texture )
    {
        this( width, height, createPosition( width, height, zpl, 0.0f, new Vector3f() ), texture, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( Texture texture, float width, float height, ZeroPointLocation zpl, float zOffset )
    {
        this( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ), texture, null, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, float zOffset, Texture texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ), texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, String texture )
    {
        this( width, height, createPosition( width, height, zpl, 0.0f, new Vector3f() ), texture, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, float zOffset, String texture )
    {
        this( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ), texture, null, null );
    }
    
    /**
     * Creates a textured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param texLowerLeft Lower-left texture coordinate
     * @param texUpperRight Upper-right texture coordinate
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, String texture, Tuple2f texLowerLeft, Tuple2f texUpperRight )
    {
        this( width, height, createPosition( width, height, zpl, 0.0f, new Vector3f() ), texture, texLowerLeft, texUpperRight, null );
    }
    
    /**
     * Creates an untextured RectBillboard without alpha channel.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, float zOffset )
    {
        this( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ), (Texture)null, null, null, null );
    }
    
    /**
     * Creates an untextured RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl )
    {
        this( width, height, createPosition( width, height, zpl, 0.0f, new Vector3f() ), (Texture)null, null, null, null );
    }
    
    /**
     * Creates an untextured, but colored RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param zOffset relative z-Location of the Rectangle
     * @param color the color to use for colorin this Rectangle
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, float zOffset, Colorf color )
    {
        this( width, height, createPosition( width, height, zpl, zOffset, new Vector3f() ), (Texture)null, null, null, color );
    }
    
    /**
     * Creates an untextured, but colored RectBillboard.
     * 
     * @param width width of the Rectangle
     * @param height height of the Rectangle
     * @param zpl the location of the point (0, 0, 0)
     * @param color the color to use for colorin this Rectangle
     */
    public RectBillboard( float width, float height, ZeroPointLocation zpl, Colorf color )
    {
        this( width, height, createPosition( width, height, zpl, 0.0f, new Vector3f() ), (Texture)null, null, null, color );
    }
}
