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
package org.xith3d.input.modules.fpih;

import org.jagatoo.opengl.enums.DrawMode;
import org.openmali.FastMath;
import org.openmali.spatial.AxisIndicator;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.input.FirstPersonInputHandler;
import org.xith3d.input.modules.ColliderCheckCallback;
import org.xith3d.input.modules.SlidingColliderCheckCallback;
import org.xith3d.physics.PhysicsEngine;
import org.xith3d.physics.collision.Collideable;
import org.xith3d.physics.collision.CollideableGroup;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.physics.simulation.Body;
import org.xith3d.physics.simulation.SimulationEngine;
import org.xith3d.physics.simulation.SimulationWorld;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.Transformable;

/**
 * This is the physics code for the {@link FirstPersonInputHandler}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPIHPhysics
{
    private final SimulationWorld simWorld;
    
    private final float width;
    private final float height;
    private final float halfWidthEpsilon;
    private final float halfHeightEpsilon;
    private final float maxHalfEpsilon;
    
    private final Collideable collider;
    private final Body body;
    private final Node colliderDebugger;
    private final TransformGroup colliderDebuggerTG;
    
    private boolean collisionsEnabled = true;
    private boolean simulationEnabled = true;
    
    private Vector3f colliderOffset = null;
    
    private ColliderCheckCallback colliderCallback = null;
    
    private long lastGameMicros = 0L;
    private long lastCollisionTime = -999999999L;
    
    private boolean isJumping = false;
    
    private final Tuple3f lastBodyPos = new Tuple3f();
    
    private final Vector3f lastCollisionDirection = new Vector3f();
    
    public void setCollisionsEnabled( boolean enabled )
    {
        this.collisionsEnabled = enabled;
        
        if ( colliderDebuggerTG != null )
        {
            colliderDebuggerTG.setRenderable( enabled );
        }
    }
    
    public final boolean areCollisionsEnabled()
    {
        return ( collisionsEnabled );
    }
    
    public void setSimulationEnabled( boolean enabled )
    {
        this.simulationEnabled = enabled;
    }
    
    public final boolean isSimulationEnabled()
    {
        return ( simulationEnabled );
    }
    
    public final void updateGameTime( long gameMicros )
    {
        this.lastGameMicros = gameMicros;
    }
    
    public final long getLastGameTime()
    {
        return ( lastGameMicros );
    }
    
    public final Collideable getCollider()
    {
        return ( collider );
    }
    
    public final Body getBody()
    {
        return ( body );
    }
    
    public final Node getDebugNode()
    {
        return ( colliderDebuggerTG );
    }
    
    /**
     * Sets the collision-avatar-offset in world coordinates.
     * 
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     */
    public void setColliderOffset( float offsetX, float offsetY, float offsetZ )
    {
        if ( (offsetX == 0f) && (offsetY == 0f) && (offsetZ == 0f) )
        {
            this.colliderOffset = null;
            return;
        }
        
        if ( this.colliderOffset == null )
            this.colliderOffset = new Vector3f( offsetX, offsetY, offsetZ );
        else
            this.colliderOffset.set( offsetX, offsetY, offsetZ );
    }
    
    /**
     * Sets the collision-avatar-offset in world coordinates.
     * 
     * @param offset
     */
    public void setColliderOffset( Tuple3f offset )
    {
        if ( offset == null )
        {
            this.colliderOffset = null;
            //updateViewInverse();
            return;
        }
        
        setColliderOffset( offset.getX(), offset.getY(), offset.getZ() );
    }
    
    /**
     * @return the collision-avatar-offset in world coordinates.
     */
    public Vector3f getColliderOffset()
    {
        return ( colliderOffset );
    }
    
    /**
     * Called, when the player jumped.
     * This method will never contain any code and can easily been
     * overridden.
     * 
     * @return if the jump was accepted
     */
    public boolean startJump( AxisIndicator upAxis )
    {
        if ( ( lastGameMicros <= lastCollisionTime + 100000L ) && ( lastCollisionDirection.getY() < 0f ) )
        {
            if ( simulationEnabled && ( body != null ) )
            {
                final Vector3f vel = body.getLinearVelocity();
                
                switch ( upAxis )
                {
                    case POSITIVE_X_AXIS:
                        body.setLinearVelocity( +5, vel.getY(), vel.getZ() );
                        break;
                    case NEGATIVE_X_AXIS:
                        body.setLinearVelocity( -5, vel.getY(), vel.getZ() );
                        break;
                    case POSITIVE_Y_AXIS:
                        body.setLinearVelocity( vel.getX(), +5f, vel.getZ() );
                        break;
                    case NEGATIVE_Y_AXIS:
                        body.setLinearVelocity( vel.getX(), -5f, vel.getZ() );
                        break;
                    case POSITIVE_Z_AXIS:
                        body.setLinearVelocity( vel.getX(), vel.getY(), +5 );
                        break;
                    case NEGATIVE_Z_AXIS:
                        body.setLinearVelocity( vel.getX(), vel.getY(), -5 );
                        break;
                }
            }
            
            isJumping = true;
            
            return ( true );
        }
        
        return ( false );
    }
    
    public void updateFromView( Tuple3f viewPosition )
    {
        if ( body != null )
        {
            Tuple3f tmp = Tuple3f.fromPool( viewPosition );
            
            if ( colliderOffset != null )
            {
                tmp.sub( colliderOffset );
            }
            
            body.setPosition( tmp );
            collider.setPosition( tmp );
            lastBodyPos.set( tmp );
            
            body.resetLinearVelocity();
            body.resetAngularVelocity();
            
            Tuple3f.toPool( tmp );
        }
    }
    
    /**
     * Updates the view position from the collider.
     * 
     * @param view the {@link Transformable} to update
     * @param tpOffset2 the third-person-offset in view-space
     */
    private void updateFromCollider( Transformable view, Vector3f tpOffset2 )
    {
        Tuple3f pos = Tuple3f.fromPool();
        
        collider.getPosition( pos );
        
        if ( simulationEnabled && ( body != null ) )
        {
            body.setPosition( pos );
            lastBodyPos.set( pos );
        }
        
        if ( tpOffset2 != null )
        {
            pos.add( tpOffset2 );
        }
        
        if ( colliderOffset != null )
        {
            pos.sub( colliderOffset );
        }
        
        view.getTransform().setTranslation( pos );
        
        if ( colliderDebuggerTG != null )
        {
            colliderDebuggerTG.getTransform().setTranslation( pos );
            colliderDebuggerTG.updateTransform();
        }
        
        Tuple3f.toPool( pos );
    }
    
    private boolean updateAndCheckCollider( Vector3f colliderPosition )
    {
        collider.setPosition( colliderPosition );
        
        if ( colliderCallback != null )
        {
            if ( colliderCallback.checkCollision( collider ) )
            {
                lastCollisionDirection.sub( colliderPosition, collider.getPosition() );
                
                collider.getPosition( colliderPosition );
                
                return ( true );
            }
        }
        
        return ( false );
    }
    
    /**
     * 
     * @param gameMicros
     * @param frameMicros
     * @param view
     * @param deltaMovement
     * @param rotX
     * @param rotY
     * @param thirdPersonOffset
     */
    public void update( long gameMicros, long frameMicros, Transformable view, Vector3f deltaMovement, float rotX, float rotY, Vector3f thirdPersonOffset )
    {
        if ( !collisionsEnabled )
        {
            return;
        }
        
        Vector3f colliderPosition = Vector3f.fromPool();
        Vector3f bodyDelta = Vector3f.fromPool( 0f, 0f, 0f );
        Vector3f totalDelta = Vector3f.fromPool();
        
        
        // get collider position...
        {
            view.getTransform().getTranslation( colliderPosition );
            
            if ( colliderOffset != null )
            {
                colliderPosition.add( colliderOffset );
            }
            colliderPosition.sub( thirdPersonOffset );
        }
        
        // get body-position and delta...
        if ( simulationEnabled && ( body != null ) )
        {
            bodyDelta.sub( body.getPosition(), lastBodyPos );
            colliderPosition.add( bodyDelta );
        }
        
        totalDelta.add( deltaMovement, bodyDelta );
        
        final float totalDeltaLength = totalDelta.length();
        
        boolean hasCollision = false;
        if ( ( totalDeltaLength <= maxHalfEpsilon ) && ( !simulationEnabled || ( body == null ) || ( frameMicros <= simWorld.getMaxStepSize() ) ) )
        {
            hasCollision = updateAndCheckCollider( colliderPosition );
        }
        else
        {
            final float distanceStepFraction0 = totalDeltaLength / maxHalfEpsilon;
            final float timeStepFraction0 = (float)frameMicros / ( ( simulationEnabled && ( body != null ) ) ? (float)simWorld.getMaxStepSize() : 1f );
            final float stepFraction0 = ( distanceStepFraction0 > timeStepFraction0 ) ? distanceStepFraction0 : timeStepFraction0;
            
            Vector3f oldColliderPosition = Vector3f.fromPool();
            oldColliderPosition.sub( colliderPosition, deltaMovement );
            oldColliderPosition.sub( bodyDelta );
            
            float stepFraction = stepFraction0;
            int step = 0;
            while ( ( stepFraction > 0f ) && !hasCollision )
            {
                colliderPosition.set( totalDelta );
                colliderPosition.mul( (float)(++step) / stepFraction0 );
                colliderPosition.add( oldColliderPosition );
                
                hasCollision = updateAndCheckCollider( colliderPosition );
                
                stepFraction -= 1f;
            }
            
            Vector3f.toPool( oldColliderPosition );
        }
        
        if ( hasCollision )
        {
            if ( simulationEnabled && ( body != null ) )
            {
                if ( !isJumping || ( gameMicros > lastCollisionTime + 100000L ) )
                {
                    body.resetLinearVelocity();
                    isJumping = false;
                }
            }
            
            updateFromCollider( view, thirdPersonOffset );
            collider.getPosition( colliderPosition );
            lastCollisionTime = gameMicros;
        }
        else if ( simulationEnabled && ( body != null ) )
        {
            updateFromCollider( view, thirdPersonOffset );
        }
        
        
        if ( colliderDebuggerTG != null )
        {
            colliderDebuggerTG.getTransform().setEuler( 0f, rotY, 0f );
            colliderDebuggerTG.getTransform().setTranslation( colliderPosition );
            colliderDebuggerTG.updateTransform();
        }
        
        
        Vector3f.toPool( totalDelta );
        Vector3f.toPool( bodyDelta );
        Vector3f.toPool( colliderPosition );
    }
    
    /**
     * Use this to check for avatar-collisions from.
     * 
     * @param ccc
     */
    public void setColliderCheckCallback( ColliderCheckCallback ccc )
    {
        this.colliderCallback = ccc;
    }
    
    public SlidingColliderCheckCallback setSlidingColliderCheckCallback( CollisionEngine collEngine, CollideableGroup collGroup )
    {
        SlidingColliderCheckCallback ccc = new SlidingColliderCheckCallback( collEngine, collGroup );
        
        setColliderCheckCallback( ccc );
        
        return ( ccc );
    }
    
    public void init( Transformable view, Vector3f tpOffset )
    {
        Vector3f viewPosition = Vector3f.fromPool();
        
        view.getPosition( viewPosition );
        
        if ( tpOffset != null )
        {
            Vector3f tpOffset2 = Vector3f.fromPool();
            
            view.getTransform().getMatrix4f().transform( tpOffset, tpOffset2 );
            viewPosition.add( tpOffset2 );
            
            Vector3f.toPool( tpOffset2 );
        }
        
        if ( colliderOffset != null )
        {
            viewPosition.add( colliderOffset );
        }
        
        if ( body == null )
            collider.setPosition( viewPosition );
        else
            body.setPosition( viewPosition );
        
        if ( colliderDebuggerTG != null )
        {
            colliderDebuggerTG.getTransform().setTranslation( viewPosition );
            colliderDebuggerTG.setTransform( colliderDebuggerTG.getTransform() );
        }
        
        Vector3f.toPool( viewPosition );
    }
    
    /**
     * Creates the {@link Collideable}, that is used to check for FPIH
     * collisions with the environment.
     * 
     * @param collEngine
     * @param width
     * @param height
     * 
     * @return the Collideable
     */
    protected Collideable createCollideable( CollisionEngine collEngine, float width, float height )
    {
        //return ( collEngine.newCapsule( width / 2.0f, height - width ) );
        
        /*
         * As long as JOODE doesn't support trimesh-capsule collision-detection
         * we need to simulate it with two spheres and a cylinder.
         */
        
        CollideableGroup group = collEngine.newGroup( "Simple" );
        
        //Collideable cylinder = collEngine.newCylinder( width / 2f, height - width );
        //group.addCollideable( cylinder );
        
        Collideable sphereTop = collEngine.newSphere( width / 2f );
        sphereTop.setPositionZ( ( height - width ) / 2f );
        group.addCollideable( sphereTop );
        
        Collideable sphereBottom = collEngine.newSphere( width / 2f );
        sphereBottom.setPositionZ( -( height - width ) / 2f );
        group.addCollideable( sphereBottom );
        
        return ( group );
    }
    
    /**
     * Creates the Node (probably a Shape3D), that visualizes the Collideable.
     * 
     * @param width
     * @param height
     * 
     * @return the Collideable-debugger Node
     */
    protected Node createCollideableDebugger( AxisIndicator upAxis, float width, float height )
    {
        Shape3D colliderDebugger = new org.xith3d.scenegraph.primitives.Capsule( width / 2.0f, height - width, 8, 4, Colorf.BLUE );
        
        switch ( upAxis )
        {
            case POSITIVE_X_AXIS:
                StaticTransform.rotateZ( colliderDebugger, FastMath.PI_HALF );
                break;
            case NEGATIVE_X_AXIS:
                StaticTransform.rotateZ( colliderDebugger, FastMath.PI_HALF );
                break;
            case POSITIVE_Y_AXIS:
            case NEGATIVE_Y_AXIS:
                break;
            case POSITIVE_Z_AXIS:
                StaticTransform.rotateX( colliderDebugger, FastMath.PI_HALF );
                break;
            case NEGATIVE_Z_AXIS:
                StaticTransform.rotateX( colliderDebugger, FastMath.PI_HALF );
                break;
        }
        
        colliderDebugger.getAppearance( true ).getPolygonAttributes( true ).setDrawMode( DrawMode.LINE );
        
        return ( colliderDebugger );
    }
    
    /**
     * 
     * @param upAxis
     * @param width
     * @param height
     * @param offset
     * @param collEngine
     * @param simEngine
     * @param simWorld
     * @param integrateWithSimulation
     * @param addDebugShape
     */
    public FPIHPhysics( AxisIndicator upAxis, float width, float height, Vector3f offset, CollisionEngine collEngine, SimulationEngine simEngine, SimulationWorld simWorld, boolean integrateWithSimulation, boolean addDebugShape )
    {
        this.simWorld = simWorld;
        
        this.width = width;
        this.height = height;
        this.halfWidthEpsilon = ( this.width / 2f ) * 0.9f;
        this.halfHeightEpsilon = ( this.height / 2f ) * 0.9f;
        this.maxHalfEpsilon = Math.max( this.halfWidthEpsilon, this.halfHeightEpsilon );
        
        this.collider = createCollideable( collEngine, width, height );
        
        if ( integrateWithSimulation )
        {
            this.body = simWorld.newBody();
            body.setMass( 85f );
            switch ( upAxis )
            {
                case POSITIVE_X_AXIS:
                    body.setRotation( 0f, FastMath.PI_HALF, 0f );
                    break;
                case NEGATIVE_X_AXIS:
                    body.setRotation( 0f, FastMath.PI_HALF, 0f );
                    break;
                case POSITIVE_Y_AXIS:
                    body.setRotation( FastMath.PI_HALF, 0f, 0f );
                    break;
                case NEGATIVE_Y_AXIS:
                    body.setRotation( -FastMath.PI_HALF, 0f, 0f );
                    break;
                case POSITIVE_Z_AXIS:
                    break;
                case NEGATIVE_Z_AXIS:
                    break;
            }
            
            body.addCollideable( collider );
            
            if ( simWorld != null )
                simWorld.addBody( body );
        }
        else
        {
            switch ( upAxis )
            {
                case POSITIVE_X_AXIS:
                    collider.setRotation( 0f, FastMath.PI_HALF, 0f );
                    break;
                case NEGATIVE_X_AXIS:
                    collider.setRotation( 0f, FastMath.PI_HALF, 0f );
                    break;
                case POSITIVE_Y_AXIS:
                    collider.setRotation( FastMath.PI_HALF, 0f, 0f );
                    break;
                case NEGATIVE_Y_AXIS:
                    collider.setRotation( -FastMath.PI_HALF, 0f, 0f );
                    break;
                case POSITIVE_Z_AXIS:
                    break;
                case NEGATIVE_Z_AXIS:
                    break;
            }
            this.body = null;
        }
        
        this.colliderOffset = offset;
        
        if ( addDebugShape )
        {
            this.colliderDebugger = createCollideableDebugger( upAxis, width, height );
            this.colliderDebuggerTG = new TransformGroup();
            colliderDebuggerTG.addChild( colliderDebugger );
            
            colliderDebuggerTG.setPickableRecursive( false );
        }
        else
        {
            this.colliderDebugger = null;
            this.colliderDebuggerTG = null;
        }
    }
    
    public FPIHPhysics( float width, float height, Vector3f offset, CollisionEngine collEngine, SimulationEngine simEngine, SimulationWorld simWorld, boolean integrateWithSimulation, boolean addDebugShape )
    {
        this( AxisIndicator.POSITIVE_Y_AXIS, width, height, offset, collEngine, simEngine, simWorld, integrateWithSimulation, addDebugShape );
    }
    
    public FPIHPhysics( float width, float height, Vector3f offset, CollisionEngine collEngine, SimulationEngine simEngine, boolean integrateWithSimulation, boolean addDebugShape )
    {
        this( width, height, offset, collEngine, simEngine, null, integrateWithSimulation, addDebugShape );
    }
    
    public FPIHPhysics( float width, float height, Vector3f offset, PhysicsEngine physEngine, SimulationWorld simWorld, boolean integrateWithSimulation, boolean addDebugShape )
    {
        this( width, height, offset, physEngine.getCollisionEngine(), physEngine.getSimulationEngine(), simWorld, integrateWithSimulation, addDebugShape );
    }
    
    public FPIHPhysics( float width, float height, Vector3f offset, PhysicsEngine physEngine, boolean integrateWithSimulation, boolean addDebugShape )
    {
        this( width, height, offset, physEngine.getCollisionEngine(), physEngine.getSimulationEngine(), integrateWithSimulation, addDebugShape );
    }
    
    public FPIHPhysics( float width, float height, Vector3f offset, CollisionEngine collEngine, boolean addDebugShape )
    {
        this( width, height, offset, collEngine, null, false, addDebugShape );
    }
    
    public FPIHPhysics( float width, float height, Vector3f offset, CollisionEngine collEngine )
    {
        this( width, height, offset, collEngine, false );
    }
}
