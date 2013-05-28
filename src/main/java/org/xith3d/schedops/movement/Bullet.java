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
package org.xith3d.schedops.movement;

import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loop.opscheduler.Animatable;
import org.xith3d.loop.UpdatingThread.TimingMode;

import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * This class can be used as a base for a bullet.
 * Add it to the scheduler of an ExtRenderLoop by calling the method addScheduledOperation(bullet).
 * It is automatically removed from its parent BranchGroup and the scheduler when maxLifetime has been reached.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Bullet extends TransformGroup implements Animatable
{
    private Vector3f velocity = null;
    private Tuple3f location = null;
    private long birthTime = -1L;
    private long lifeTime = -1L;
    private long maxLifeTime;
    private boolean alive;
    
    /**
     * A Bullet's animation is always running until it is dead.
     * 
     * @return is this object's animation running
     */
    public boolean isAnimating()
    {
        return ( false );
    }
    
    /**
     * Starts the animation of this Bullet.
     */
    public void startAnimation( long gameTime, TimingMode timingMode )
    {
        this.birthTime = gameTime;
    }
    
    /**
     * A Bullet's animation cannot be stopped.
     * Use setAlive() instead.
     * 
     * @see #setAlive(boolean)
     */
    public void stopAnimation()
    {
        throw new UnsupportedOperationException( "A Bullet's animation cannot be stopped. Use setAlive() instead." );
    }
    
    /**
     * A Bullet is always persistent until it is dead.
     */
    public boolean isPersistent()
    {
        return ( true );
    }
    
    /**
     * @return the milliseconds this Bullet is alive.
     */
    public long getLifeTime()
    {
        return ( lifeTime );
    }
    
    /**
     * Sets this bullet's velocity vector.
     * The length of this vector is the speed.
     * 
     * @param velocity the new velocity vector
     */
    public void setVelocity( Vector3f velocity )
    {
        this.velocity = velocity;
    }
    
    /**
     * @return this Bullet's velocity vector.
     * The length of this vector is the speed.
     */
    public Vector3f getVelocity()
    {
        return ( velocity );
    }
    
    /**
     * Sets this Bullet's current location.
     * 
     * @param location the new location
     */
    public void setLocation( Tuple3f location )
    {
        this.location = location;
        
        Transform3D t3d = this.getTransform();
        t3d.setTranslation( location );
        this.setTransform( t3d );
    }
    
    /**
     * @return this Bullet's current location.
     */
    public Tuple3f getLocation()
    {
        return ( location );
    }
    
    /**
     * @return this Bullet's speed along its velocity vector.
     */
    public float getSpeed()
    {
        return ( velocity.length() );
    }
    
    /**
     * Sets this Bullet's speed along its velocity vector.
     * 
     * @param speed the new speed for this Bullet
     */
    public void setSpeed( float speed )
    {
        velocity.normalize();
        velocity.scale( speed );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        lifeTime = ( gameTime - birthTime );
        
        if ( lifeTime < maxLifeTime )
        {
            Tuple3f loc = getLocation();
            final float factor = timingMode.getSecondsAsFloat( frameTime ) * 2f;
            loc.set( loc.getX() + velocity.getX() * factor, loc.getY() + velocity.getY() * factor, loc.getZ() + velocity.getZ() * factor );
            setLocation( loc );
        }
        else
        {
            setAlive( false );
        }
    }
    
    /**
     * Sets the maximum time, this Bullet will be alive.
     * 
     * @param maxLifeTime maximum life time in milliseconds
     */
    public void setMaxLifeTime( long maxLifeTime )
    {
        this.maxLifeTime = maxLifeTime;
    }
    
    /**
     * @return the maximum time, this Bullet will be alive
     */
    public long getMaxLifeTime()
    {
        return ( maxLifeTime );
    }
    
    /**
     * Sets this Object alive or dead.
     * If it is killed, is is also removed from its parent group.
     * 
     * @param alive if false, the object will be removed from the scheduler next loop iteration
     */
    public void setAlive( boolean alive )
    {
        this.alive = alive;
        
        if ( ( !alive ) && ( this.getParent() != null ) )
            detach();
    }
    
    /**
     * @return false -> the object will be removed from the scheduler next loop iteration, true otherwise
     */
    public boolean isAlive()
    {
        return ( alive );
    }
    
    /**
     * Creates a new Bullet.
     * This calls setVelocity, setLocation, setAlive, setMaxLifeTime.
     * 
     * @param velocity the velocity vector this bullet will move along. Its length is the speed.
     * @param maxLifeTime the maximum amount of milliseconds this bullet will remain in the scheduler.
     */
    public Bullet( Vector3f velocity, long maxLifeTime )
    {
        super();
        
        setVelocity( velocity );
        setLocation( new Vector3f( 0f, 0f, 0f ) );
        setAlive( true );
        setMaxLifeTime( maxLifeTime );
    }
}
