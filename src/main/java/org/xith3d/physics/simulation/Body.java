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
package org.xith3d.physics.simulation;

import java.util.ArrayList;

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.datatypes.NamableObject;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.physics.collision.Collideable;
import org.xith3d.physics.collision.CollideableBase;
import org.xith3d.physics.collision.CollideableGroup;
import org.xith3d.physics.util.PlaceableImpl;
import org.xith3d.utility.classes.beans.BeanUtil;

/**
 * A body has a position, an orientation, a linear and
 * angular velocity, and some forces applied to it.
 * It's part of a SimulationWorld and can be stepped.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Body extends PlaceableImpl implements NamableObject, Enableable
{
    private String name = null;
    
    private Object userObject = null;
    
    /** The simulation engine we belong to */
    protected final SimulationWorld world;
    
    /** Our collideables : used for collision detection */
    protected final ArrayList<Collideable> collideables = new ArrayList<Collideable>();
    
    /**
     * @return the {@link SimulationWorld}, this {@link Body} belongs to.
     */
    public final SimulationWorld getWorld()
    {
        return ( world );
    }
    
    /**
     * Sets this Bodie's name.
     * 
     * @param name
     */
    public final void setName( String name )
    {
        this.name = name;
    }
    
    /**
     * @return this Body's name.
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * Sets this Bodie's user-object.
     * 
     * @param userObject
     */
    public final void setUserObject( Object userObject )
    {
        this.userObject = userObject;
    }
    
    /**
     * @return this Bodie's user-object.
     */
    public final Object getUserObject()
    {
        return ( userObject );
    }
    
    /**
     * Enables or disables this {@link Body}.
     * 
     * @param enabled
     */
    public abstract void setEnabled( boolean enabled );
    
    /**
     * @return true, if this {@link Body} is enabled.
     */
    public abstract boolean isEnabled();
    
    /**
     * Enables/Disables gravity specifically for this {@link Body}.
     * 
     * @param enabled
     */
    public abstract void setGravityEnabled( boolean enabled );
    
    /**
     * @return if gravity is enabled specifically for this {@link Body}.
     */
    public abstract boolean isGravityEnabled();
    
    /**
     * @return the linear velocity of this body
     */
    public abstract Vector3f getLinearVelocity();
    
    /**
     * Sets the linear velocity of this object
     * 
     * @param velX
     * @param velY
     * @param velZ
     */
    public abstract void setLinearVelocity( float velX, float velY, float velZ );
    
    /**
     * Sets the linear velocity of this object
     * 
     * @param linearVel
     */
    public final void setLinearVelocity( Vector3f linearVel )
    {
        setLinearVelocity( linearVel.getX(), linearVel.getY(), linearVel.getZ() );
    }
    
    /**
     * Sets the linear velocity of this object to zero.
     */
    public final void resetLinearVelocity()
    {
        setLinearVelocity( 0f, 0f, 0f );
    }
    
    /**
     * @return the angular velocity, as "euler angles"
     * (but for speed), about the X, Y, and Z axis
     */
    public abstract Vector3f getAngularVelocity();
    
    /**
     * Sets the angular velocity, as "euler angles"
     * (but for speed), about the X, Y, and Z axis
     * 
     * @param velX
     * @param velY
     * @param velZ
     */
    public abstract void setAngularVelocity( float velX, float velY, float velZ );
    
    /**
     * Sets the angular velocity, as "euler angles"
     * (but for speed), about the X, Y, and Z axis
     * 
     * @param angularVel
     */
    public final void setAngularVelocity( Tuple3f angularVel )
    {
        setAngularVelocity( angularVel.getX(), angularVel.getY(), angularVel.getZ() );
    }
    
    /**
     * Sets the angular velocity, as "euler angles"
     * (but for speed), about the X, Y, and Z axis to zero.
     */
    public final void resetAngularVelocity()
    {
        setAngularVelocity( 0f, 0f, 0f );
    }
    
    /**
     * Adds a torque to this body.
     * 
     * @param tx X component
     * @param ty Y component
     * @param tz Z component
     */
    public abstract void addTorque( float tx, float ty, float tz );
    
    /**
     * Adds a torque to this body.
     * 
     * @param torque
     */
    public final void addTorque( Tuple3f torque )
    {
        addTorque( torque.getX(), torque.getY(), torque.getZ() );
    }
    
    /**
     * Adds a force to this body.
     * 
     * @param fx X component
     * @param fy Y component
     * @param fz Z component
     */
    public abstract void addForce( float fx, float fy, float fz );
    
    /**
     * Adds a force to this body.
     * 
     * @param force
     */
    public final void addForce( Vector3f force )
    {
        addForce( force.getX(), force.getY(), force.getZ() );
    }
    
    /**
     * Gets a member, by name.
     * 
     * @param member the member to get
     * @return the parameter, by name
     */
    public Object getParameter( String member )
    {
        
        return ( BeanUtil.get( this, member ) );
        
    }
    
    /**
     * Sets a parameter, by name.
     * 
     * @param member
     *            The name of the parameter
     * @param value
     *            The "data" : could be Float, Vector3f, whatever..
     */
    public void setParameter( String member, Object value )
    {
        BeanUtil.set( this, member, value );
    }
    
    /**
     * Sets this Body's mass.
     * 
     * @param mass
     */
    public abstract void setMass( float mass );
    
    /**
     * @return this Body's mass.
     */
    public abstract float getMass();
    
    /**
     * Adds a collideable to this Body.
     * Collideables which are added to a Body are used for
     * collision detection. A Body without any Collideable
     * will just fall (if there's gravity) through anything.
     * Note that the collideables are moved with the Body.
     * 
     * @param collideable
     */
    public void addCollideable( Collideable collideable )
    {
        this.collideables.add( collideable );
        
        collideable.setBody( this );
        
        collideable.setPosition( this.position );
        collideable.setRotationMatrix( this.rotation );
    }
    
    /**
     * Removes a collideable for this Body.
     * 
     * @param collideable
     */
    public void removeCollideable( Collideable collideable )
    {
        this.collideables.remove( collideable );
        collideable.setBody( null );
    }
    
    /*
     * @return the collideables.
     */
    /*
    public ArrayList<Collideable> getCollideables()
    {
        return ( collideables );
    }
    */
    
    /**
     * @return the number of {@link Collideable}s in this {@link Body}.
     */
    public final int getCollideablesCount()
    {
        return ( collideables.size() );
    }
    
    /**
     * @param i
     * 
     * @return the i-th {@link Collideable} in this {@link Body}.
     */
    public final Collideable getCollideable( int i )
    {
        return ( collideables.get( i ) );
    }
    
    /**
     * Refreshes this {@link Body}'s position and rotation from the
     * implementation as well as all the child-{@link Collideable}'s ones.
     */
    protected void refresh()
    {
        for ( int i = 0; i < collideables.size(); i++ )
        {
            final Collideable collideable = collideables.get( i );
            
            //collideable.getPosition().set( this.position );
            ((CollideableBase)collideable).setPosition( this.position.getX(), this.position.getY(), this.position.getZ(), false );
            //collideable.getRotationMatrix().set( this.rotation );
            ((CollideableBase)collideable).setRotationMatrix( this.rotation, false );
            collideable.recomputeWorldCoords( false, false );
            
            if ( collideable instanceof CollideableGroup )
            {
                ( (CollideableGroup)collideable ).recomputeChildrenWorldCoords( true );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( this.getName() == null )
            return ( super.toString() );
        
        return ( super.toString() + " \"" + this.getName() + "\"" );
    }
    
    /**
     * Creates a new Body.
     * At creation time, the Body has no collideable, which means
     * it isn't "solid". It's just a "simulated position and rotation".
     * If you want it to react to collisions (which you probably want),
     * you have to add some collideables to it.
     * 
     * @param world the simulation world we belong to
     * 
     * @see #addCollideable(Collideable)
     */
    public Body( SimulationWorld world )
    {
        super();
        
        this.world = world;
    }
}
