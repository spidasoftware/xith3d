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
package org.xith3d.scenegraph;

import org.openmali.FastMath;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

/**
 * SpotLight defines a point light source located at
 * some point in space and radiating in a specific direction.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class SpotLight extends PointLight
{
    public static final Vector3f DEFAULT_DIRECTION = Vector3f.newReadOnly( 0f, 0f, -1f );
    public static final float DEFAULT_SPREAD_ANGLE = FastMath.PI;
    public static final float DEFAULT_CONCENTRAION = 1f;
    
    /**
     * The direction of the light.
     */
    private Vector3f direction = new Vector3f( DEFAULT_DIRECTION );
    
    /**
     * The spread angle of the light.
     */
    private float spread;
    private float spreadDeg;
    
    /**
     * The concentration of the light.
     */
    private float concentration;
    
    /**
     * Sets the direction for this object.
     * 
     * @param dirX
     * @param dirY
     * @param dirZ
     */
    public void setDirection( float dirX, float dirY, float dirZ )
    {
        this.direction.set( dirX, dirY, dirZ );
    }
    
    /**
     * Sets the direction for this object.
     * 
     * @param direction
     */
    public final void setDirection( Tuple3f direction )
    {
        this.setDirection( direction.getX(), direction.getY(), direction.getZ() );
    }
    
    /**
     * Gets the direction for this object.
     */
    public final Vector3f getDirection()
    {
        return ( direction );
    }
    
    /**
     * Gets the direction for this object.
     */
    public final Vector3f getDirection( Vector3f direction )
    {
        direction.set( this.direction );
        
        return ( direction );
    }
    
    /**
     * If this light has a tracked-node, the light's direction is transformed
     * by the tracked-node's world-transform.
     * If it doesn't have a tracked-node, the plain light's direction is returned.
     * 
     * @param direction
     */
    public final void getComputedDirection( Vector3f direction )
    {
        if ( getTrackedNode() == null )
            getDirection( direction );
        else
            getTrackedNode().getWorldTransform().transform( getDirection(), direction );
    }
    
    /**
     * Sets the spread angle for this object.
     *
     * @param spread Spread angle in radians
     */
    public void setSpreadAngle( float spread )
    {
        this.spread = spread;
        this.spreadDeg = FastMath.toDeg( spread );
    }
    
    /**
     * Sets the spread angle for this object.
     *
     * @param spread Spread angle in degrees
     */
    public void setSpreadAngleDeg( float spread )
    {
        spreadDeg = spread;
        this.spread = FastMath.toRad( spreadDeg );
    }
    
    /**
     * Gets the spread angle for this object.
     *
     * @return Spread angle in radians
     */
    public final float getSpreadAngle()
    {
        return ( spread );
    }
    
    /**
     * Gets the spread angle for this object.
     *
     * @return Spread angle in degrees
     */
    public final float getSpreadAngleDeg()
    {
        return ( spreadDeg );
    }
    
    /**
     * Sets the concentration for this object.
     * 
     * @param concentration
     */
    public void setConcentration( float concentration )
    {
        this.concentration = concentration;
    }
    
    /**
     * Gets the concentration for this object.
     */
    public final float getConcentration()
    {
        return ( concentration );
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param enabled
     * @param color
     * @param location
     * @param direction
     * @param attenuation
     * @param spreadAngle
     * @param concentration
     */
    public SpotLight( boolean enabled, Colorf color, Tuple3f location, Tuple3f direction, Tuple3f attenuation, float spreadAngle, float concentration )
    {
        super( enabled, color, location, attenuation );
        
        if ( direction != null )
            this.direction.set( direction );
        this.setSpreadAngle( spreadAngle );
        this.concentration = concentration;
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param color
     * @param location
     * @param direction
     * @param attenuation
     * @param spreadAngle
     * @param concentration
     */
    public SpotLight( Colorf color, Tuple3f location, Tuple3f direction, Tuple3f attenuation, float spreadAngle, float concentration )
    {
        this( true, color, location, direction, attenuation, spreadAngle, concentration );
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param enabled
     * @param color
     * @param location
     * @param direction
     * @param spreadAngle
     */
    public SpotLight( boolean enabled, Colorf color, Tuple3f location, Tuple3f direction, float spreadAngle )
    {
        this( enabled, color, location, direction, DEFAULT_ATTENUATION, spreadAngle, DEFAULT_CONCENTRAION );
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param color
     * @param location
     * @param direction
     * @param spreadAngle
     */
    public SpotLight( Colorf color, Tuple3f location, Tuple3f direction, float spreadAngle )
    {
        this( true, color, location, direction, spreadAngle );
    }
    
    /**
     * Constructs a new SpotLight object with a default color of white
     * and default location of (0, 0, 0).
     * 
     * @param enabled
     */
    public SpotLight( boolean enabled )
    {
        this( enabled, DEFAULT_COLOR, DEFAULT_LOCATION, DEFAULT_DIRECTION, DEFAULT_ATTENUATION, DEFAULT_SPREAD_ANGLE, DEFAULT_CONCENTRAION );
    }
    
    /**
     * Constructs a new SpotLight object with a default color of white
     * and default location of (0, 0, 0).
     */
    public SpotLight()
    {
        this( true );
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param enabled
     * @param color
     * @param direction
     * @param trackedNode
     * @param attenuation
     * @param spreadAngle
     * @param concentration
     */
    public SpotLight( boolean enabled, Colorf color, Tuple3f direction, Node trackedNode, Tuple3f attenuation, float spreadAngle, float concentration )
    {
        this( enabled, color, DEFAULT_LOCATION, direction, attenuation, spreadAngle, concentration );
        
        this.setTrackedNode( trackedNode );
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param color
     * @param direction
     * @param trackedNode
     * @param attenuation
     * @param spreadAngle
     * @param concentration
     */
    public SpotLight( Colorf color, Tuple3f direction, Node trackedNode, Tuple3f attenuation, float spreadAngle, float concentration )
    {
        this( true, color, direction, trackedNode, attenuation, spreadAngle, concentration );
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param enabled
     * @param color
     * @param direction
     * @param trackedNode
     * @param spreadAngle
     */
    public SpotLight( boolean enabled, Colorf color, Tuple3f direction, Node trackedNode, float spreadAngle )
    {
        this( enabled, color, direction, trackedNode, DEFAULT_ATTENUATION, spreadAngle, DEFAULT_CONCENTRAION );
    }
    
    /**
     * Constructs a new SpotLight object with the specified color,
     * location and attenuation.
     * 
     * @param color
     * @param direction
     * @param trackedNode
     * @param spreadAngle
     */
    public SpotLight( Colorf color, Tuple3f direction, Node trackedNode, float spreadAngle )
    {
        this( true, color, direction, trackedNode, spreadAngle );
    }
    
    /**
     * Constructs a new SpotLight object with a default color of white
     * and default location of (0, 0, 0).
     * 
     * @param enabled
     * @param trackedNode
     */
    public SpotLight( boolean enabled, Node trackedNode )
    {
        this( enabled );
        
        this.setTrackedNode( trackedNode );
    }
    
    /**
     * Constructs a new SpotLight object with a default color of white
     * and default location of (0, 0, 0).
     * 
     * @param trackedNode
     */
    public SpotLight( Node trackedNode )
    {
        this( true, trackedNode );
    }
}
