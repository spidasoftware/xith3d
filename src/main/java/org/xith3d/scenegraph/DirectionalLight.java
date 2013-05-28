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

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector3f;

/**
 * DirectionalLight defines an oriented light source with an
 * origin at infinity.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class DirectionalLight extends Light
{
    public static final Vector3f DEFAULT_DIRECTION = Vector3f.newReadOnly( 0f, 0f, -1f );
    
    private Node trackedNode = null;
    
    /**
     * The direction of the light.
     */
    private final Vector3f direction = new Vector3f( DEFAULT_DIRECTION );
    
    /**
     * Sets the Node to be tracked<br>
     * If this is not null, the PointLight's location will not be an absolute
     * one anymore, but relative to the tracked Node's world-transform.<br>
     * 
     * @param node
     */
    public void setTrackedNode( Node node )
    {
        this.trackedNode = node;
    }
    
    /**
     * @return the Node to be tracked<br>
     * If this is not null, the PointLight's location will not be an absolute
     * one anymore, but relative to the tracked Node's world-transform.<br>
     * <br>
     */
    public final Node getTrackedNode()
    {
        return ( trackedNode );
    }
    
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
    public final void setDirection( Vector3f direction )
    {
        setDirection( direction.getX(), direction.getY(), direction.getZ() );
    }
    
    /**
     * Gets the direction for this object.
     */
    public final Vector3f getDirection()
    {
        return ( direction.getReadOnly() );
    }
    
    /**
     * Gets the direction for this object.
     * 
     * @param direction
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
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param enabled
     * @param colorR
     * @param colorG
     * @param colorB
     * @param dirX
     * @param dirY
     * @param dirZ
     */
    public DirectionalLight( boolean enabled, float colorR, float colorG, float colorB, float dirX, float dirY, float dirZ )
    {
        super( enabled, colorR, colorG, colorB );
        
        this.direction.set( dirX, dirY, dirZ );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param colorR
     * @param colorG
     * @param colorB
     * @param dirX
     * @param dirY
     * @param dirZ
     */
    public DirectionalLight( float colorR, float colorG, float colorB, float dirX, float dirY, float dirZ )
    {
        this( true, colorR, colorG, colorB, dirX, dirY, dirZ );
    }
    
    protected static final Vector3f getDir( Vector3f dir )
    {
        return ( ( dir == null ) ?  DEFAULT_DIRECTION :  dir );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param enabled
     * @param color
     * @param direction
     */
    public DirectionalLight( boolean enabled, Colorf color, Vector3f direction )
    {
        this( enabled,
              getCol( color ).getRed(), getCol( color ).getGreen(), getCol( color ).getBlue(),
              getDir( direction ).getX(), getDir( direction ).getY(), getDir( direction ).getZ()
            );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param color
     * @param direction
     */
    public DirectionalLight( Colorf color, Vector3f direction )
    {
        this( true, color, direction );
    }
    
    /**
     * Constructs a new DirectionalLight object with a default color of white
     * and default direction of toward the screen along the negative z axis.
     * 
     * @param enabled
     */
    public DirectionalLight( boolean enabled )
    {
        this( enabled, DEFAULT_COLOR, DEFAULT_DIRECTION );
    }
    
    /**
     * Constructs a new DirectionalLight object with a default color of white
     * and default direction of toward the screen along the negative z axis.
     */
    public DirectionalLight()
    {
        this( true );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param enabled
     * @param colorR
     * @param colorG
     * @param colorB
     * @param trackedNode
     */
    public DirectionalLight( boolean enabled, float colorR, float colorG, float colorB, Node trackedNode, float dirX, float dirY, float dirZ )
    {
        this( enabled, colorR, colorG, colorB, dirX, dirY, dirZ );
        
        this.setTrackedNode( trackedNode );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param enabled
     * @param colorR
     * @param colorG
     * @param colorB
     * @param trackedNode
     */
    public DirectionalLight( boolean enabled, float colorR, float colorG, float colorB, Node trackedNode )
    {
        this( enabled, colorR, colorG, colorB, trackedNode, DEFAULT_DIRECTION.getX(), DEFAULT_DIRECTION.getY(), DEFAULT_DIRECTION.getZ() );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param colorR
     * @param colorG
     * @param colorB
     * @param trackedNode
     */
    public DirectionalLight( float colorR, float colorG, float colorB, Node trackedNode )
    {
        this( true, colorR, colorG, colorB, trackedNode );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param enabled
     * @param color
     * @param trackedNode
     */
    public DirectionalLight( boolean enabled, Colorf color, Node trackedNode )
    {
        this( enabled,
              getCol( color ).getRed(), getCol( color ).getGreen(), getCol( color ).getBlue(),
              trackedNode
            );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param enabled
     * @param color
     * @param trackedNode
     * @param direction
     */
    public DirectionalLight( boolean enabled, Colorf color, Node trackedNode, Vector3f direction )
    {
        this( enabled,
              getCol( color ).getRed(), getCol( color ).getGreen(), getCol( color ).getBlue(),
              trackedNode,
              direction.getX(), direction.getY(), direction.getZ()
            );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param color
     * @param trackedNode
     */
    public DirectionalLight( Colorf color, Node trackedNode )
    {
        this( true, color, trackedNode );
    }
    
    /**
     * Constructs a new DirectionalLight object with the specified color
     * and specified direction.
     * 
     * @param color
     * @param trackedNode
     * @param direction
     */
    public DirectionalLight( Colorf color, Node trackedNode, Vector3f direction )
    {
        this( true, color, trackedNode, direction );
    }
    
    /**
     * Constructs a new DirectionalLight object with a default color of white
     * and default direction of toward the screen along the negative z axis.
     * 
     * @param enabled
     * @param trackedNode
     */
    public DirectionalLight( boolean enabled, Node trackedNode )
    {
        this( enabled );
        
        setTrackedNode( trackedNode );
    }
    
    /**
     * Constructs a new DirectionalLight object with a default color of white
     * and default direction of toward the screen along the negative z axis.
     * 
     * @param trackedNode
     */
    public DirectionalLight( Node trackedNode )
    {
        this( true, trackedNode );
    }
}
