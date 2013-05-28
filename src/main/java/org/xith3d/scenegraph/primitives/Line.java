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

import org.jagatoo.opengl.enums.LinePattern;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.LineArray;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.Shape3D;

/**
 * This Shape represents a simple 3D-line.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Line extends Shape3D
{
    private float[] coords;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LineArray getGeometry()
    {
        return ( (LineArray)super.getGeometry() );
    }
    
    /**
     * Sets this Line's coordinates.
     * 
     * @param coords a 6 elemental array with start and end point coordinates
     */
    public void setCoordinates( float[] coords )
    {
        this.coords = coords;
        
        getGeometry().setCoordinates( 0, coords );
        setBoundsDirty();
    }
    
    /**
     * Sets this Line's coordinates.
     * 
     * @param x0 start and end point coordinates
     * @param y0 start and end point coordinates
     * @param z0 start and end point coordinates
     * @param x1 start and end point coordinates
     * @param y1 start and end point coordinates
     * @param z1 start and end point coordinates
     */
    public void setCoordinates( float x0, float y0, float z0, float x1, float y1, float z1 )
    {
        setCoordinates( new float[]
        {
            x0, y0, z0,
            x1, y1, z1
        } );
    }
    
    /**
     * Sets this Line's coordinates.<br>
     * This sets the start-position to (0, 0, 0).
     * 
     * @param x1 start and end point coordinates
     * @param y1 start and end point coordinates
     * @param z1 start and end point coordinates
     */
    public void setCoordinates( float x1, float y1, float z1 )
    {
        setCoordinates( 0f, 0f, 0f, x1, y1, z1 );
    }
    
    /**
     * Sets this Line's coordinates.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     */
    public void setCoordinates( Tuple3f start, Tuple3f end )
    {
        setCoordinates( new float[]
        {
            start.getX(), start.getY(), start.getZ(),
            end.getX(), end.getY(), end.getZ()
        } );
    }
    
    /**
     * Sets this Line's coordinates.<br>
     * This sets the start-point to (0, 0, 0).
     * 
     * @param end end point coordinates
     */
    public void setCoordinates( Tuple3f end )
    {
        setCoordinates( Point3f.ZERO, end );
    }
    
    /**
     * @return this Line's coordinates.
     */
    public float[] getCoordinates()
    {
        return ( coords );
    }
    
    /**
     * Sets the Line's color.
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
     * @return the Line's color.
     */
    public Colorf getColor()
    {
        return ( getAppearance().getColoringAttributes().getColor() );
    }
    
    /**
     * Sets the Line's LinePattern.
     * 
     * @param pattern the new LinePattern
     */
    public void setPattern( LinePattern pattern )
    {
        getAppearance().getLineAttributes().setLinePattern( pattern );
    }
    
    /**
     * @return the Line's LinePattern.
     */
    public LinePattern getPattern()
    {
        return ( getAppearance().getLineAttributes().getLinePattern() );
    }
    
    /**
     * Sets the Line's width in pixels.
     * 
     * @param width the new line-width in pixels
     */
    public void setWidth( float width )
    {
        getAppearance().getLineAttributes().setLineWidth( width );
    }
    
    /**
     * @return the Line's width in pixels.
     */
    public float getWidth()
    {
        return ( getAppearance().getLineAttributes().getLineWidth() );
    }
    
    /**
     * Sets the Line's antialiasing flag to enabled.
     * 
     * @param enabled
     */
    public void setAntialiasingEnabled( boolean enabled )
    {
        getAppearance().getLineAttributes().setLineAntialiasingEnabled( enabled );
    }
    
    /**
     * @return the value of the Line's antialiasing flag.
     */
    public boolean isAntialiasingEnabled()
    {
        return ( getAppearance().getLineAttributes().isLineAntialiasingEnabled() );
    }
    
    /**
     * Creates a new Line.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     * @param width the Line's width in pixels
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     * @param linePattern the pattern for how this line is to be rendered
     * 
     * @see LineAttributes
     */
    public Line( Tuple3f start, Tuple3f end, float width, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        super();
        
        final boolean ib = isIgnoreBounds();
        setIgnoreBounds( true );
        setGeometry( new LineArray( 2 ) );
        setIgnoreBounds( ib );
        setCoordinates( start, end );
        updateBounds( true );
        
        setAppearance( new Appearance() );
        
        ColoringAttributes ca = new ColoringAttributes();
        ca.setShadeModel( ColoringAttributes.SHADE_FLAT );
        getAppearance().setColoringAttributes( ca );
        
        setColor( color );
        
        getAppearance().setLineAttributes( new LineAttributes() );
        setWidth( width );
        setPattern( linePattern );
        setAntialiasingEnabled( antiAliasing );
    }
    
    /**
     * Creates a new solid Line.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     * @param width the Line's width in pixels
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     */
    public Line( Tuple3f start, Tuple3f end, float width, boolean antiAliasing, Colorf color )
    {
        this( start, end, width, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid Line.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     * @param width the Line's width in pixels
     * @param color the Line's color
     */
    public Line( Tuple3f start, Tuple3f end, float width, Colorf color )
    {
        this( start, end, width, false, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     * @param linePattern the pattern for how this line is to be rendered
     */
    public Line( Tuple3f start, Tuple3f end, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        this( start, end, 1.0f, antiAliasing, color, linePattern );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     * @param color the Line's color
     * @param linePattern the pattern for how this line is to be rendered
     */
    public Line( Tuple3f start, Tuple3f end, Colorf color, LinePattern linePattern )
    {
        this( start, end, 1.0f, false, color, linePattern );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     */
    public Line( Tuple3f start, Tuple3f end, boolean antiAliasing, Colorf color )
    {
        this( start, end, 1.0f, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width.
     * 
     * @param start start point coordinates
     * @param end end point coordinates
     * @param color the Line's color
     */
    public Line( Tuple3f start, Tuple3f end, Colorf color )
    {
        this( start, end, 1.0f, false, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid Line starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param width the Line's width in pixels
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     * @param linePattern the pattern for how this line is to be rendered
     */
    public Line( Tuple3f end, float width, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, antiAliasing, color, linePattern );
    }
    
    /**
     * Creates a new solid Line starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param width the Line's width in pixels
     * @param color the Line's color
     * @param linePattern the pattern for how this line is to be rendered
     */
    public Line( Tuple3f end, float width, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, false, color, linePattern );
    }
    
    /**
     * Creates a new solid Line starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param width the Line's width in pixels
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     */
    public Line( Tuple3f end, float width, boolean antiAliasing, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid Line starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param width the Line's width in pixels
     * @param color the Line's color
     */
    public Line( Tuple3f end, float width, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, width, false, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     * @param linePattern the pattern for how this line is to be rendered
     */
    public Line( Tuple3f end, boolean antiAliasing, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, antiAliasing, color, linePattern );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param color the Line's color
     * @param linePattern the pattern for how this line is to be rendered
     */
    public Line( Tuple3f end, Colorf color, LinePattern linePattern )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, false, color, linePattern );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param antiAliasing the value for the Line's antialiasing flag
     * @param color the Line's color
     */
    public Line( Tuple3f end, boolean antiAliasing, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, antiAliasing, color, LinePattern.SOLID );
    }
    
    /**
     * Creates a new solid Line of 1 pixel width starting at (0, 0, 0).
     * 
     * @param end end point coordinates
     * @param color the Line's color
     */
    public Line( Tuple3f end, Colorf color )
    {
        this( new Point3f( 0.0f, 0.0f, 0.0f ), end, 1.0f, false, color, LinePattern.SOLID );
    }
}
