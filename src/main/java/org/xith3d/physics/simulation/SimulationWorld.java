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
import java.util.Set;

import org.jagatoo.datatypes.Enableable;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.physics.collision.CollisionResolversManager;
import org.xith3d.physics.collision.Collision;
import org.xith3d.physics.simulation.joints.*;

/**
 * A simulation world
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SimulationWorld implements Updatable, Enableable
{
    protected SimulationEngine engine;
    
    /** All our bodies */
    private final ArrayList<Body> bodies = new ArrayList<Body>();
    
    /** All our joints */
    private final ArrayList<Joint> joints = new ArrayList<Joint>();
    
    private final Vector3f gravity;
    private boolean gravityEnabled = true;
    
    private long stepMicros = -1L;
    private long maxStepMicros = -1L;
    
    private boolean enabled = true;
    
    /**
     * @return the {@link SimulationEngine}.
     */
    public SimulationEngine getEngine()
    {
        return ( engine );
    }
    
    /** Put impl-specific stuff here. */
    protected abstract void setGravityImpl( float x, float y, float z );
    
    /**
     * @param x the gravity to set
     * @param y the gravity to set
     * @param z the gravity to set
     */
    public final void setGravity( float x, float y, float z )
    {
        this.gravity.set( x, y, z );
        
        setGravityImpl( x, y, z );
    }
    
    /**
     * @param gravity the gravity to set
     */
    public final void setGravity( Vector3f gravity )
    {
        setGravity( gravity.getX(), gravity.getY(), gravity.getZ() );
    }
    
    /**
     * Sets whether gravity is (world-)globally enabled or not.
     * 
     * @param enabled
     */
    public void setGravityEnabled( boolean enabled )
    {
        if ( enabled == this.gravityEnabled )
            return;
        
        this.gravityEnabled = enabled;
        
        if ( enabled )
            setGravityImpl( gravity.getX(), gravity.getY(), gravity.getZ() );
        else
            setGravityImpl( 0f, 0f, 0f );
    }
    
    /**
     * @return whether gravity is (world-)globally enabled or not.
     */
    public final boolean isGravityEnabled()
    {
        return ( gravityEnabled );
    }
    
    /**
     * @return the gravity
     */
    public final Vector3f getGravity()
    {
        return ( gravity.getReadOnly() );
    }
    
    /**
     * Creates a new Body.
     * 
     * @return the newly created Body.
     */
    protected abstract Body newBodyImpl();
    
    /**
     * Creates a new Body.
     * 
     * @return the newly created Body.
     */
    public final Body newBody()
    {
        Body body = newBodyImpl();
        
        //addBody(body);
        
        return ( body );
    }
    
    /** Put impl-specific stuff here. */
    protected abstract void addBodyImpl( Body body );
    
    /**
     * Adds a body to the world.
     * 
     * @param body the body to be added
     */
    public final void addBody( Body body )
    {
        if ( !this.bodies.contains( body ) )
        {
            this.bodies.add( body );
            addBodyImpl( body );
        }
    }
    
    /** Put impl-specific stuff here. */
    protected abstract void removeBodyImpl( Body body );
    
    /**
     * Removes a body from the world.
     * 
     * @param body the body to be removed
     */
    public final void removeBody( Body body )
    {
        if (this.bodies.remove( body ) )
        {
            removeBodyImpl( body );
        }
    }
    
    /**
     * @return the number of bodies in the world.
     */
    public final int numBodies()
    {
        return ( this.bodies.size() );
    }
    
    /**
     * Gets a body from its index.
     * 
     * @param i the index of the body
     * 
     * @return the body
     */
    public final Body getBody( int i )
    {
        return ( this.bodies.get( i ) );
    }
    
    protected JointLimitMotor newJointLimitMotor( float defaultCFM, float defaultERP )
    {
        return ( new JointLimitMotor( defaultCFM, defaultERP ) );
    }
    
    public final JointLimitMotor newJointLimitMotor()
    {
        return ( newJointLimitMotor( 0.2f, 1e-6f ) );
    }
    
    /**
     * Creates a new BallJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created BallJoint.
     */
    protected abstract BallJoint newBallJointImpl( Body body1, Body body2 );
    
    /**
     * Creates a new BallJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created BallJoint.
     */
    public final BallJoint newBallJoint( Body body1, Body body2 )
    {
        BallJoint joint = newBallJointImpl( body1, body2 );
        
        //addJoint( joint );
        
        return ( joint );
    }
    
    /**
     * Creates a new FixedJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created FixedJoint.
     */
    protected abstract FixedJoint newFixedJointImpl( Body body1, Body body2 );
    
    /**
     * Creates a new FixedJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created Hinge2Joint.
     */
    public final FixedJoint newFixedJoint( Body body1, Body body2 )
    {
        FixedJoint joint = newFixedJointImpl( body1, body2 );
        
        //addJoint( joint );
        
        return ( joint );
    }
    
    /**
     * Creates a new HingeJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created HingeJoint.
     */
    protected abstract HingeJoint newHingeJointImpl( Body body1, Body body2 );
    
    /**
     * Creates a new HingeJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created HingeJoint.
     */
    public final HingeJoint newHingeJoint( Body body1, Body body2 )
    {
        HingeJoint joint = newHingeJointImpl( body1, body2 );
        
        //addJoint( joint );
        
        return ( joint );
    }
    
    /**
     * Creates a new Hinge2Joint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created Hinge2Joint.
     */
    protected abstract Hinge2Joint newHinge2JointImpl( Body body1, Body body2 );
    
    /**
     * Creates a new Hinge2Joint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created Hinge2Joint.
     */
    public final Hinge2Joint newHinge2Joint( Body body1, Body body2 )
    {
        Hinge2Joint joint = newHinge2JointImpl( body1, body2 );
        
        //addJoint( joint );
        
        return ( joint );
    }
    
    /**
     * Creates a new SliderJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created SliderJoint.
     */
    protected abstract SliderJoint newSliderJointImpl( Body body1, Body body2 );
    
    /**
     * Creates a new SliderJoint.
     * 
     * @param body1
     * @param body2
     * 
     * @return the newly created SliderJoint.
     */
    public final SliderJoint newSliderJoint( Body body1, Body body2 )
    {
        SliderJoint joint = newSliderJointImpl( body1, body2 );
        
        //addJoint( joint );
        
        return ( joint );
    }
    
    /** Put impl-specific stuff here. */
    protected abstract void addJointImpl( Joint joint );
    
    /**
     * Adds a joint to the world.
     * 
     * @param joint the joint to be added
     */
    public final void addJoint( Joint joint )
    {
        if ( !this.joints.contains( joint ) )
        {
            this.joints.add( joint );
            addJointImpl( joint );
        }
    }
    
    /** Put impl-specific stuff here */
    protected abstract void removeJointImpl( Joint joint );
    
    /**
     * Removes a joint from the world.
     * 
     * @param joint the joint to be removed
     */
    public final void removeJoint( Joint joint )
    {
        if ( this.joints.remove( joint ) )
        {
            removeJointImpl( joint );
        }
    }
    
    /**
     * @return the number of joints in the world
     */
    public final int numJoints()
    {
        return ( this.joints.size() );
    }
    
    /**
     * Gets a joint from its index.
     * 
     * @param i the index of the joint
     * 
     * @return the joint
     */
    public final Joint getJoint( int i )
    {
        return ( this.joints.get( i ) );
    }
    
    /**
     * @return a list of all functions available for stepping this world
     */
    public abstract Set<String> getStepperFunctions();
    
    /**
     * Sets the stepping function to be used.
     * 
     * @param stepperFunction the stepper function, it should be
     * one of the stepper functions proposed by {@link #getStepperFunctions()}
     * If stepperFunction is not a valid stepping function, the stepping
     * function will not be changed.
     * If stepperFunction is null, the default stepping function will be set.
     */
    public abstract void setStepperFunction( String stepperFunction );
    
    /**
     * @return The current stepping function used
     */
    public abstract String getStepperFunction();
    
    /**
     * Sets the constant internal step time in microseconds.
     * 
     * @param micros
     */
    public final void setStepSize( long micros )
    {
        if ( ( micros < 1L ) && ( micros != -1L ) )
            throw new IllegalArgumentException( "micros must be -1 or greater then 0" );
        
        this.stepMicros = micros;
    }
    
    /**
     * @return the constant internal step time in microseconds.
     */
    public final long getStepSize()
    {
        if ( stepMicros == -1L )
            return ( engine.getStepSize() );
        
        return ( stepMicros );
    }
    
    /**
     * Sets the maximum internal step time in microseconds.
     * 
     * @param micros
     */
    public final void setMaxStepSize( long micros )
    {
        if ( ( micros < 1L ) && ( micros != -1L ) )
            throw new IllegalArgumentException( "micros must be -1 or greater then 0" );
        
        this.maxStepMicros = micros;
    }
    
    /**
     * @return the maximum internal step time in microseconds.
     */
    public final long getMaxStepSize()
    {
        if ( maxStepMicros == -1L )
            return ( engine.getMaxStepSize() );
        
        return ( maxStepMicros );
    }
    
    /**
     * This method is called before the {@link #stepImpl(long)} method is
     * called.
     */
    protected abstract void beforeStep();
    
    /**
     * This method is called after the {@link #stepImpl(long)} method is
     * called.
     */
    protected abstract void afterStep();
    
    /**
     * Step the simulation = advance the time
     * @param stepMicros the size of the step which should
     * be taken. Note that 2 steps of .01f isn't equal to
     * 1 step of .02f : the smaller is your stepSize, the
     * more accurate your simulation (but computations take
     * more time). As always, you should find a tradeoff
     * between speed and accuracy (which is the whole point
     * of physic simulation, anyway).
     * Begin with .01f and adjust it later if you don't know
     * what to put.
     * Note : you could also make the stepSize "adaptative",
     * ie proportional to the (real) time that has passed since
     * the last step, but sometimes adaptive stepSize can
     * cause some simulation problems (jumps, instability).
     * If you have these problems you could also try to
     * advance by the right quantity of time by several
     * fixed size steps.
     * 
     * @see #setStepperFunction(String)
     */
    protected abstract void stepImpl( long stepMicros );
    
    private long stepAccumulator = 0L;
    
    /**
     * Step the simulation = advance the time
     * @param stepMicros the size of the step which should
     * be taken. Note that 2 steps of .01f isn't equal to
     * 1 step of .02f : the smaller is your stepSize, the
     * more accurate your simulation (but computations take
     * more time). As always, you should find a tradeoff
     * between speed and accuracy (which is the whole point
     * of physic simulation, anyway).
     * Begin with .01f and adjust it later if you don't know
     * what to put.
     * Note : you could also make the stepSize "adaptative",
     * ie proportional to the (real) time that has passed since
     * the last step, but sometimes adaptive stepSize can
     * cause some simulation problems (jumps, instability).
     * If you have these problems you could also try to
     * advance by the right quantity of time by several
     * fixed size steps.
     * 
     * @see #setStepperFunction(String)
     */
    public final void step( long stepMicros, CollisionResolversManager collisionResolversManager )
    {
        final long step = getStepSize();
        final long maxStep = getMaxStepSize();
        
        if ( stepMicros > maxStep )
            stepMicros = step;
        
        beforeStep();
        
        stepAccumulator += stepMicros;
        
        while ( stepAccumulator >= step )
        {
            if ( collisionResolversManager != null )
            {
                collisionResolversManager.update();
            }
            
            stepImpl( step );
            
            stepAccumulator -= step;
        }
        
        for ( int i = 0; i < bodies.size(); i++ )
        {
            bodies.get( i ).refresh();
        }
        
        for ( int i = 0; i < joints.size(); i++ )
        {
            joints.get( i ).refresh();
        }
        
        afterStep();
    }
    
    /**
     * This method should be called by yourself
     * (yeah, SimulationWorld is listening to you !!)
     * when you want a collision to be resolved by the
     * {@link SimulationWorld}, which normally means, that
     * the two bodies won't interpenetrate (well, if the
     * constraint is solvable).
     * You can set one Body to null, it means just one Body
     * needs to be moved (e.g. when a basket ball hits the
     * ground, you want the basket ball to react, not the
     * ground :) ).
     * 
     * @param collision
     * @param body1
     * @param body2
     * @param surfParams
     */
    public abstract void resolveCollision( Collision collision, Body body1, Body body2, SurfaceParameters surfParams );
    
    /**
     * This method should be called by yourself
     * (yeah, SimulationWorld is listening to you !!)
     * when you want a collision to be resolved by the
     * {@link SimulationWorld}, which normally means, that
     * the two bodies won't interpenetrate (well, if the
     * constraint is solvable).
     * One of the Collideables can have no body, it means just one Body
     * needs to be moved (e.g. when a basket ball hit the
     * ground, you want the basket ball to react, not the
     * ground :) ).
     * 
     * @param collision
     * @param surfParams
     */
    public final void resolveCollision( Collision collision, SurfaceParameters surfParams )
    {
        resolveCollision( collision, collision.getCollideable1().getBody(), collision.getCollideable2().getBody(), surfParams );
    }
    
    /**
     * Sets this SimulationWorld enabled/disabled.<br>
     * If not enabled, the update() method will do nothing.
     * 
     * @param enabled
     */
    public final void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
    
    /**
     * @return if this SimulationWorld is enabled.<br>
     * If not enabled, the update() method will do nothing.
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( isEnabled() )
        {
            step( timingMode.getMicroSeconds( frameTime ), null );
        }
    }
    
    /**
     * Creates a new simulation world.
     * 
     * @param engine the simulation engine we belong to
     */
    public SimulationWorld( SimulationEngine engine )
    {
        this.engine = engine;
        this.gravity = new Vector3f( 0f, -9.81f, 0f );
    }
}
