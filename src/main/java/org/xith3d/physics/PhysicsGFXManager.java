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
package org.xith3d.physics;

import java.util.Vector;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.physics.collision.Collideable;
import org.xith3d.physics.simulation.Body;
import org.xith3d.physics.simulation.Joint;
import org.xith3d.physics.simulation.joints.FixedJoint;
import org.xith3d.physics.simulation.joints.Hinge2Joint;
import org.xith3d.physics.simulation.joints.HingeJoint;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.Line;

/**
 * The {@link PhysicsGFXManager} is capable of updating the graphical
 * representations of {@link Collideable}s each frame.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class PhysicsGFXManager implements Updatable
{
    private final Vector<Collideable> colliders = new Vector<Collideable>();
    private final Vector<TransformGroup> colliderNodes = new Vector<TransformGroup>();
    private final Vector<Body> bodies = new Vector<Body>();
    private final Vector<TransformGroup> bodyNodes = new Vector<TransformGroup>();
    
    public final int getNumItems()
    {
        return ( colliders.size() + bodies.size() );
    }
    
    /**
     * Adds a Collideable and its graphical counterpart to the manager.
     * 
     * @param collider
     * @param node
     * @param parentGroup if not null, the node will be directly added to this group
     * 
     * @return the TransformGroup back again
     */
    public final TransformGroup directAdd( Collideable collider, TransformGroup node, GroupNode parentGroup )
    {
        colliders.add( collider );
        colliderNodes.add( node );
        
        update( collider, node );
        
        if ( parentGroup != null )
        {
            parentGroup.addChild( node );
        }
        
        return ( node );
    }
    
    /**
     * Adds a Collideable and its graphical counterpart to the manager.<br>
     * This method uses collider.getBaseGFX() to create the GFX object.
     * 
     * @param collider
     * @param parentGroup if not null, the node will be directly added to this group
     * 
     * @return the TransformGroup, that is used to position the GFX object
     */
    public final TransformGroup directAdd( Collideable collider, GroupNode parentGroup )
    {
        final TransformGroup node = new TransformGroup();
        final Node gfx = collider.getBaseGFX();
        gfx.detach();
        node.addChild( gfx );
        
        return ( directAdd( collider, node, parentGroup ) );
    }
    
    /**
     * Adds a Body and its graphical counterpart to the manager.
     * 
     * @param body
     * @param node
     * @param parentGroup if not null, the node will be directly added to this group
     * 
     * @return the TransformGroup back again
     */
    public final TransformGroup directAdd( Body body, TransformGroup node, GroupNode parentGroup )
    {
        bodies.add( body );
        bodyNodes.add( node );
        
        update( body, node );
        
        if ( parentGroup != null )
        {
            parentGroup.addChild( node );
        }
        
        return ( node );
    }
    
    /**
     * Adds a Body and its graphical counterpart to the manager.<br>
     * This method uses collider.getBaseGFX() to create the GFX objects
     * for all Collideables in the Body.
     * 
     * @param body
     * @param parentGroup if not null, the node will be directly added to this group
     * 
     * @return the TransformGroup, that is used to position the GFX object
     */
    public final TransformGroup directAdd( Body body, GroupNode parentGroup )
    {
        final TransformGroup node = new TransformGroup();
        
        final int n = body.getCollideablesCount();
        
        for ( int i = 0; i < n; i++ )
        {
            final Collideable collider = body.getCollideable( i );
            
            final Node gfx = collider.getBaseGFX();
            gfx.detach();
            node.addChild( gfx );
            
            directAdd( body, node, parentGroup );
        }
        
        return ( node );
    }
    
    /**
     * Adds all Bodies of a Joint and its graphical counterpart and lines for the Joint itself to the manager.
     * 
     * @param joint
     * @param parentGroup
     * @param lineColor
     * @param lineWidth
     */
    public final void directAdd( Joint joint, GroupNode parentGroup, Colorf lineColor, float lineWidth )
    {
        TransformGroup node1 = null;
        //TransformGroup node2 = null;
        
        if ( joint.getBody1() != null )
        {
            node1 = directAdd( joint.getBody1(), parentGroup );
        }
        
        if ( joint.getBody2() != null )
        {
            /*node2 = */directAdd( joint.getBody2(), parentGroup );
        }
        
        /*
        if ( joint instanceof BallJoint )
        {
            BallJoint bJoint = (BallJoint)joint;
            
            add( bJoint.)
        }
        else */if ( joint instanceof FixedJoint )
        {
            //FixedJoint fJoint = (FixedJoint)joint;
            
            Vector3f vec = Vector3f.fromPool();
            
            vec.sub( joint.getBody2().getPosition(), joint.getBody1().getPosition() );
            
            Line line = new Line( vec, lineWidth, lineColor );
            node1.addChild( line );
            
            Vector3f.toPool( vec );
        }
        else if ( joint instanceof HingeJoint )
        {
            HingeJoint hJoint = (HingeJoint)joint;
            
            Vector3f vec = Vector3f.fromPool();
            
            vec.sub( hJoint.getAnchor(), joint.getBody1().getPosition() );
            
            Line line = new Line( vec, lineWidth, lineColor );
            node1.addChild( line );
            
            Vector3f.toPool( vec );
        }
        else if ( joint instanceof Hinge2Joint )
        {
            Hinge2Joint h2Joint = (Hinge2Joint)joint;
            
            Vector3f vec = Vector3f.fromPool();
            
            vec.sub( joint.getBody1().getPosition(), h2Joint.getAnchor() );
            
            Line line = new Line( vec, lineWidth, lineColor );
            node1.addChild( line );
            
            Vector3f.toPool( vec );
        }
    }
    
    /**
     * Adds a Collideable and its graphical counterpart to the manager.
     * 
     * @param collider
     * @param node
     * 
     * @return the TransformGroup back again
     */
    public final TransformGroup add( Collideable collider, TransformGroup node )
    {
        return ( directAdd( collider, node, null ) );
    }
    
    /**
     * Adds a Collideable and its graphical counterpart to the manager.<br>
     * This method uses collider.getBaseGFX() to create the GFX object.
     * 
     * @param collider
     * 
     * @return the TransformGroup, that is used to position the GFX object
     */
    public final TransformGroup add( Collideable collider )
    {
        return ( directAdd( collider, null ) );
    }
    
    /**
     * Removes a Collider and its graphical counterpart from the manager.
     * (optional operation)
     * 
     * @param index
     */
    public final void removeCollider( int index )
    {
        colliders.remove( index );
        colliderNodes.remove( index );
    }
    
    /**
     * Removes a Collider and its graphical counterpart from the manager.
     * (optional operation)
     * 
     * @param collider
     */
    public final void remove( Collideable collider )
    {
        final int index = colliders.indexOf( collider );
        if ( index >= 0 )
        {
            removeCollider( index );
        }
    }
    
    /**
     * Adds a Body and its graphical counterpart to the manager.
     * 
     * @param body
     * @param node
     * 
     * @return the TransformGroup back again
     */
    public final TransformGroup add( Body body, TransformGroup node )
    {
        return ( directAdd( body, node, null ) );
    }
    
    /**
     * Adds a Body and its graphical counterpart to the manager.<br>
     * This method uses collider.getBaseGFX() to create the GFX object.
     * 
     * @param body
     * 
     * @return the TransformGroup, that is used to position the GFX object
     */
    public final TransformGroup add( Body body )
    {
        return ( directAdd( body, null ) );
    }
    
    /**
     * Removes a Body and its graphical counterpart from the manager.
     * (optional operation)
     * 
     * @param index
     */
    public final void removeBody( int index )
    {
        bodies.remove( index );
        bodyNodes.remove( index );
    }
    
    /**
     * Removes a Body and its graphical counterpart from the manager.
     * (optional operation)
     * 
     * @param body
     */
    public final void remove( Body body )
    {
        final int index = bodies.indexOf( body );
        if ( index >= 0 )
        {
            removeBody( index );
        }
    }
    
    /**
     * Removes a Joint's Bodies and their graphical counterparts and the oint-lines from the manager.
     * (optional operation)
     * 
     * @param body
     */
    public final void remove( Joint joint )
    {
        if ( joint.getBody1() != null )
        {
            remove( joint.getBody1() );
        }
        
        if ( joint.getBody2() != null )
        {
            remove( joint.getBody2() );
        }
    }
    
    /**
     * Removes a Collider and its graphical counterpart from the manager.
     * (optional operation)
     * 
     * @param node
     */
    public final void remove( TransformGroup node )
    {
        int index = colliderNodes.indexOf( node );
        if ( index >= 0 )
        {
            removeCollider( index );
        }
        else
        {
            index = bodyNodes.indexOf( node );
            if ( index >= 0 )
            {
                removeBody( index );
            }
        }
    }
    
    /**
     * Removes all items (colliders and GFX objects).
     */
    public final void clear()
    {
        colliders.clear();
        colliderNodes.clear();
        bodies.clear();
        bodyNodes.clear();
    }
    
    public final Collideable getCollider( int index )
    {
        return ( colliders.get( index ) );
    }
    
    public final Body getBody( int index )
    {
        return ( bodies.get( index ) );
    }
    
    public final TransformGroup getColliderGFX( int index )
    {
        return ( colliderNodes.get( index ) );
    }
    
    public final TransformGroup getBodyGFX( int index )
    {
        return ( bodyNodes.get( index ) );
    }
    
    private static final void update( final Collideable collider, final TransformGroup node )
    {
        final Transform3D t3d = node.getTransform();
        
        t3d.setRotation( collider.getWorldRotMat() );
        t3d.setTranslation( collider.getWorldPos() );
        
        node.setTransform( t3d );
    }
    
    private static final void update( final Body body, final TransformGroup node )
    {
        final Transform3D t3d = node.getTransform();
        
        t3d.setRotation( body.getRotationMatrix() );
        t3d.setTranslation( body.getPosition() );
        
        node.setTransform( t3d );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        for ( int i = 0; i < colliders.size(); i++ )
        {
            update( colliders.get( i ), colliderNodes.get( i ) );
        }
        
        for ( int i = 0; i < bodies.size(); i++ )
        {
            update( bodies.get( i ), bodyNodes.get( i ) );
        }
    }
}
