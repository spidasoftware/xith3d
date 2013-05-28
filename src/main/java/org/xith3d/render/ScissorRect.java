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

import org.openmali.types.twodee.Sized2iRO;

/**
 * This class is used to attach glScissor information to a RenderAtom.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ScissorRect
{
    private int x, y, width, height;
    private boolean isChanged;
    
    /**
     * @return the x coordinate of the lower-left corner
     */
    public int getX()
    {
        return ( x );
    }
    
    /**
     * @return the y coordinate of the lower-left corner
     */
    public int getY()
    {
        return ( y );
    }
    
    /**
     * @return the width of the box
     */
    public int getWidth()
    {
        return ( width );
    }
    
    /**
     * @return the height of the box
     */
    public int getHeight()
    {
        return ( height );
    }
    
    /**
     * Have the values changed since last time?
     */
    public void setChanged( boolean changed )
    {
        this.isChanged = changed;
    }
    
    /**
     * Have the values changed since last time?
     */
    public boolean isChanged()
    {
        return ( isChanged );
    }
    
    /**
     * Checks if the values of the box are valid
     * 
     * @param viewport the Viewport size to check the bounds against
     * 
     * @return true, if the bounds are valid, false otherwise
     */
    public boolean check( Sized2iRO viewport )
    {
        return ( ( x >= 0 ) && ( y >= 0 ) && ( x + width <= viewport.getWidth() ) && ( y + height <= viewport.getHeight() ) );
    }
    
    /**
     * Clamps the ScissorBox'es values to be insode the Canvas.
     * 
     * @param viewport the Viewport size to check the bounds against
     */
    public void clamp( Sized2iRO viewport )
    {
        if ( width > viewport.getWidth() )
            width = viewport.getWidth();
        if ( height > viewport.getHeight() )
            height = viewport.getHeight();
        if ( x + width > viewport.getWidth() )
            x = viewport.getWidth() - width;
        if ( y + height > viewport.getHeight() )
            y = viewport.getHeight() - height;
        if ( x < 0 )
            x = 0;
        if ( y < 0 )
            y = 0;
    }
    
    public boolean equals( ScissorRect box2 )
    {
        return ( ( box2 != null ) && ( ( this == box2 ) || ( ( this.x == box2.x ) && ( this.y == box2.y ) && ( this.width == box2.width ) && ( this.height == box2.height ) ) ) );
    }
    
    @Override
    public String toString()
    {
        return ( this.getClass().getSimpleName() + ": [x = " + x + ", y = " + y + ", width = " + width + ", height = " + height + "]" );
    }
    
    /**
     * Initializes the ScissorBox
     * 
     * @param x the x coordinate of the lower-left corner
     * @param y the y coordinate of the lower-left corner
     * @param width the width of the box
     * @param height the height of the box
     */
    public void init( int x, int y, int width, int height )
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        
        this.isChanged = true;
    }
    
    /**
     * Creates a new ScissorBox
     * 
     * @param x the x coordinate of the lower-left corner
     * @param y the y coordinate of the lower-left corner
     * @param width the width of the box
     * @param height the height of the box
     */
    public ScissorRect( int x, int y, int width, int height )
    {
        this.init( x, y, width, height );
    }
}
