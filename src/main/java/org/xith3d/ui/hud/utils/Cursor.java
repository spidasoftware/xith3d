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
package org.xith3d.ui.hud.utils;

import org.openmali.vecmath2.Point2i;
import org.openmali.vecmath2.Tuple2i;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.HUD;

/**
 * The {@link Cursor} class encapsulates a {@link Texture} and
 * zero-point-coordinates for a {@link HUD}-Cursor.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Cursor
{
    public static enum Type
    {
        INHERIT,
        POINTER1,
        POINTER2,
        CROSSHAIR,
        TEXT,
        WAIT,
        HELP,
        ;
    }
    
    private Texture2D texture;
    
    private final Point2i zeroPoint = new Point2i( 0, 0 );
    
    /**
     * Sets this Cursor's Texture.
     * 
     * @param texture
     */
    public final void setTexture( Texture2D texture )
    {
        if ( texture == null )
            throw new IllegalArgumentException( "texture must not be null" );
        
        this.texture = texture;
    }
    
    /**
     * Sets this Cursor's Texture.
     * 
     * @param texture
     */
    public final void setTexture( String texture )
    {
        if ( texture == null )
            setTexture( (Texture2D)null );
        
        setTexture( HUDTextureUtils.getTexture( texture, true ) );
    }
    
    /**
     * @return this Cursor's Texture.
     */
    public final Texture2D getTexture()
    {
        return ( texture );
    }
    
    /**
     * Sets this Cursor's zero-point cursor-local pixel coordinates.
     * 
     * @param zeroPointX
     * @param zeroPointY
     */
    public final void setZeroPoint( int zeroPointX, int zeroPointY )
    {
        this.zeroPoint.set( zeroPointX, zeroPointY );
    }
    
    /**
     * Sets this Cursor's zero-point cursor-local pixel coordinates.
     * 
     * @param zeroPoint
     */
    public final void setZeroPoint( Tuple2i zeroPoint )
    {
        this.zeroPoint.set( zeroPoint );
    }
    
    /**
     * @return this Cursor's zero-point cursor-local pixel coordinates.
     */
    public final Point2i getZeroPoint()
    {
        return ( zeroPoint.getReadOnly() );
    }
    
    /**
     * @return this Cursor's zero-point x-coordinate cursor-local pixel coordinates.
     */
    public final int getZeroPointX()
    {
        return ( zeroPoint.getX() );
    }
    
    /**
     * @return this Cursor's zero-point y-coordinate cursor-local pixel coordinates.
     */
    public final int getZeroPointY()
    {
        return ( zeroPoint.getY() );
    }
    
    
    /**
     * Creates a new Cursor.
     * 
     * @param texture
     * @param zeroX
     * @param zeroY
     */
    public Cursor( Texture2D texture, int zeroX, int zeroY )
    {
        if ( texture == null )
            throw new IllegalArgumentException( "texture must not be null" );
        
        this.texture = texture;
        this.zeroPoint.set( zeroX, zeroY );
    }
    
    /**
     * Creates a new Cursor.
     * 
     * @param texture
     * @param zeroPoint
     */
    public Cursor( Texture2D texture, Tuple2i zeroPoint )
    {
        if ( texture == null )
            throw new IllegalArgumentException( "texture must not be null" );
        
        this.texture = texture;
        if ( zeroPoint != null )
            this.zeroPoint.set( zeroPoint );
    }
    
    /**
     * Creates a new Cursor.
     * 
     * @param texture
     */
    public Cursor( Texture2D texture )
    {
        this( texture, null );
    }
    
    /**
     * Creates a new Cursor.
     * 
     * @param texture
     * @param zeroX
     * @param zeroY
     */
    public Cursor( String texture, int zeroX, int zeroY )
    {
        if ( texture == null )
            throw new IllegalArgumentException( "texture must not be null" );
        
        this.setTexture( texture );
        this.zeroPoint.set( zeroX, zeroY );
    }
    
    /**
     * Creates a new Cursor.
     * 
     * @param texture
     * @param zeroPoint
     */
    public Cursor( String texture, Tuple2i zeroPoint )
    {
        if ( texture == null )
            throw new IllegalArgumentException( "texture must not be null" );
        
        this.setTexture( texture );
        if ( zeroPoint != null )
            this.zeroPoint.set( zeroPoint );
    }
    
    /**
     * Creates a new Cursor.
     * 
     * @param texture
     */
    public Cursor( String texture )
    {
        this( texture, null );
    }
}
