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

import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.PointArray;
import org.xith3d.scenegraph.PointAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph._SG_PrivilegedAccess;

/**
 * This Shape represents a set of points in 3D-space.
 * Points are always drawn as real points on the screen.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Points extends Shape3D
{
    public float minBoundsRadius = 0.1f;
    
    private float[] coords;
    private Tuple3f[] points;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PointArray getGeometry()
    {
        return ( (PointArray)super.getGeometry() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateBoundsCheap( boolean onlyDirty, boolean childrenToo, boolean parentToo, boolean onlyWorld )
    {
        // if we already have the bounds then return
        if ( ( isIgnoreBounds() ) || ( !boundsDirty && onlyDirty ) )
        {
            return;
        }
        
        if ( boundsAutoCompute && !onlyWorld )
        {
            final Geometry geom = this.getGeometry();
            if ( geom != null )
            {
                if ( ( geom.isBoundsDirty() ) || ( !onlyDirty ) )
                {
                    final Bounds b = _SG_PrivilegedAccess.getCachedBounds( geom );
                    final BoundingSphere bsph;
                    if ( b == null )
                        bsph = new BoundingSphere();
                    else
                        bsph = (BoundingSphere)b;
                    
                    bsph.compute( geom );
                    if ( bsph.getRadius() < minBoundsRadius )
                        bsph.setRadius( minBoundsRadius );
                    _SG_PrivilegedAccess.setCachedBounds( bsph, geom );
                }
                
                untransformedBounds.set( _SG_PrivilegedAccess.getCachedBounds( geom ) );
                bounds.set( _SG_PrivilegedAccess.getCachedBounds( geom ) );
            }
        }
        
        super.updateBoundsCheap( onlyDirty, childrenToo, parentToo, onlyWorld );
    }
    
    /**
     * Sets this Points' coordinates
     * 
     * @param points a Tuple3f-array containing the points' coordinates
     */
    private void setCoordinates()
    {
        if ( ( this.coords == null ) || ( this.coords.length != points.length ) )
        {
            this.coords = new float[ points.length * 3 ];
        }
        
        for ( int i = 0; i < points.length; i++ )
        {
            this.coords[ i * 3 + 0 ] = points[ i ].getX();
            this.coords[ i * 3 + 1 ] = points[ i ].getY();
            this.coords[ i * 3 + 2 ] = points[ i ].getZ();
        }
        
        getGeometry().setCoordinates( 0, coords );
        setBoundsDirty();
    }
    
    /**
     * Sets this Points' coordinates
     * 
     * @param points a Tuple3f-array containing the points' coordinates
     */
    public void setCoordinates( Tuple3f[] points )
    {
        this.points = points;
        
        setCoordinates();
    }
    
    /**
     * Sets this Point's coordinates
     * 
     * @param point a Tuple3f containing the point's coordinates
     */
    public void setCoordinates( Tuple3f point )
    {
        if ( this.points == null )
        {
            this.points = new Tuple3f[]
            {
                new Point3f()
            };
            
        }
        else if ( this.points.length != 1 )
        {
            this.points = new Tuple3f[]
            {
                new Point3f()
            };
        }
        
        this.points[ 0 ].set( point );
        
        setCoordinates();
    }
    
    /**
     * @return this Line's coordinates
     */
    public Tuple3f[] getCoordinates()
    {
        return ( points );
    }
    
    /**
     * @return this Line's coordinates
     */
    public Tuple3f getPoint()
    {
        if ( points == null )
            throw new NullPointerException( "This PointArray doesn't contain any coordinates." );
        
        if ( points.length != 1 )
            throw new UnsupportedOperationException( "This PointArray consists of more than one point." );
        
        return ( points[ 0 ] );
    }
    
    /**
     * Sets the Point's color
     * 
     * @param color the new color
     */
    public void setColor( Colorf color )
    {
        getAppearance().getColoringAttributes().setColor( color );
        
        if ( color.hasAlpha() )
            getAppearance().getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
        else
            getAppearance().setTransparencyAttributes( null );
    }
    
    /**
     * @return the Line's color
     */
    public Colorf getColor()
    {
        return ( getAppearance().getColoringAttributes().getColor() );
    }
    
    /**
     * Sets the Points' size in pixels
     * 
     * @param size the new point-size in pixels
     */
    public void setSize( float size )
    {
        getAppearance().getPointAttributes().setPointSize( size );
    }
    
    /**
     * @return the Points' size in pixels
     */
    public float getSize()
    {
        return ( getAppearance().getPointAttributes().getPointSize() );
    }
    
    /**
     * Sets the Points' antialiasing flag to enabled
     * 
     * @param enabled
     */
    public void setAntialiasingEnabled( boolean enabled )
    {
        getAppearance().getPointAttributes().setPointAntialiasingEnabled( enabled );
    }
    
    /**
     * @return the value of the Points' antialiasing flag
     */
    public boolean isAntialiasingEnabled()
    {
        return ( getAppearance().getPointAttributes().isPointAntialiasingEnabled() );
    }
    
    /**
     * Creates a new Points instance
     * 
     * @param points the points' coordinates
     * @param size the points' size in pixels
     * @param antiAliasing the value of the points' antialiasing flag
     * @param color the points' color
     * 
     * @see PointAttributes
     */
    public Points( Tuple3f[] points, float size, boolean antiAliasing, Colorf color )
    {
        super();
        
        final boolean ib = isIgnoreBounds();
        setIgnoreBounds( true );
        setGeometry( new PointArray( points.length ) );
        setIgnoreBounds( ib );
        setCoordinates( points );
        
        setAppearance( new Appearance() );
        
        ColoringAttributes ca = new ColoringAttributes();
        ca.setShadeModel( ColoringAttributes.SHADE_FLAT );
        getAppearance().setColoringAttributes( ca );
        
        setColor( color );
        
        getAppearance().setPointAttributes( new PointAttributes() );
        setSize( size );
        setAntialiasingEnabled( antiAliasing );
        
        updateBounds( true );
    }
    
    /**
     * Creates a new Points instance
     * 
     * @param points the points' coordinates
     * @param size the points' size in pixels
     * @param color the points' color
     * 
     * @see PointAttributes
     */
    public Points( Tuple3f[] points, float size, Colorf color )
    {
        this( points, size, false, color );
    }
    
    /**
     * Creates a new Points instance
     * 
     * @param point the point's coordinates
     * @param size the points' size in pixels
     * @param antiAliasing the value of the points' antialiasing flag
     * @param color the points' color
     * 
     * @see PointAttributes
     */
    public Points( Tuple3f point, float size, boolean antiAliasing, Colorf color )
    {
        this( new Tuple3f[]
        {
            point
        }, size, antiAliasing, color );
    }
    
    /**
     * Creates a new Points instance
     * 
     * @param point the point's coordinates
     * @param size the points' size in pixels
     * @param color the points' color
     * 
     * @see PointAttributes
     */
    public Points( Tuple3f point, float size, Colorf color )
    {
        this( point, size, false, color );
    }
}
