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
package org.xith3d.physics.util;

import org.jagatoo.datatypes.RepositionListener3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.physics.simulation.Body;
import org.xith3d.physics.simulation.Joint;
import org.xith3d.physics.simulation.joints.BallJoint;
import org.xith3d.physics.simulation.joints.Hinge2Joint;
import org.xith3d.physics.simulation.joints.HingeJoint;

/**
 * The {@link BodyJointAnchorLink} listens for {@link Body}-position-changes
 * and adjusts a {@link Joint}'s anchor accordingly.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class BodyJointAnchorLink implements RepositionListener3f
{
    private final Body body;
    private final Vector3f offset;
    
    public final Body getBody()
    {
        return ( body );
    }
    
    public final Vector3f getOffset()
    {
        return ( offset );
    }
    
    public abstract Joint getJoint();
    
    protected BodyJointAnchorLink( Body body, boolean useOffset, float offsetX, float offsetY, float offsetZ )
    {
        if ( body == null )
            throw new IllegalArgumentException( "body must not be null." );
        
        this.body = body;
        
        if ( useOffset )
            this.offset = new Vector3f( offsetX, offsetY, offsetZ );
        else
            this.offset = null;
    }
    
    
    private static final class BodyJointAnchorLink_Ball extends BodyJointAnchorLink
    {
        private final BallJoint joint;
        private final int anchorIndex;
        
        @Override
        public final BallJoint getJoint()
        {
            return ( joint );
        }
        
        public void onPositionChanged( float posX, float posY, float posZ )
        {
            switch ( anchorIndex )
            {
                case 1:
                    joint.setAnchor1( posX, posY, posZ );
                    break;
                case 2:
                    joint.setAnchor2( posX, posY, posZ );
                    break;
            }
        }
        
        public BodyJointAnchorLink_Ball( Body body, BallJoint joint, int anchorIndex, boolean useOffset, float offsetX, float offsetY, float offsetZ )
        {
            super( body, useOffset, offsetX, offsetY, offsetZ );
            
            if ( joint == null )
                throw new IllegalArgumentException( "joint must not be null." );
            
            if ( ( anchorIndex < 1 ) || ( anchorIndex > 2 ) )
                throw new IllegalArgumentException( "anchorIndex must be in range [1, 2]." );
            
            this.joint = joint;
            this.anchorIndex = anchorIndex;
        }
    }
    
    private static final class BodyJointAnchorLink_Hinge extends BodyJointAnchorLink
    {
        private final HingeJoint joint;
        
        @Override
        public final HingeJoint getJoint()
        {
            return ( joint );
        }
        
        public void onPositionChanged( float posX, float posY, float posZ )
        {
            joint.setAnchor( posX, posY, posZ );
        }
        
        public BodyJointAnchorLink_Hinge( Body body, HingeJoint joint, boolean useOffset, float offsetX, float offsetY, float offsetZ )
        {
            super( body, useOffset, offsetX, offsetY, offsetZ );
            
            if ( joint == null )
                throw new IllegalArgumentException( "joint must not be null." );
            
            this.joint = joint;
        }
    }
    
    private static final class BodyJointAnchorLink_Hinge2 extends BodyJointAnchorLink
    {
        private final Hinge2Joint joint;
        
        @Override
        public final Hinge2Joint getJoint()
        {
            return ( joint );
        }
        
        public void onPositionChanged( float posX, float posY, float posZ )
        {
            joint.setAnchor( posX, posY, posZ );
        }
        
        public BodyJointAnchorLink_Hinge2( Body body, Hinge2Joint joint, boolean useOffset, float offsetX, float offsetY, float offsetZ )
        {
            super( body, useOffset, offsetX, offsetY, offsetZ );
            
            if ( joint == null )
                throw new IllegalArgumentException( "joint must not be null." );
            
            this.joint = joint;
        }
    }
    
    
    /**
     * Creates a new {@link BodyJointAnchorLink} for a {@link BallJoint}.<br>
     * The link is directly added to the Body as a {@link RepositionListener3f}.<br>
     * You must remove it on your own in case.
     * 
     * @param body
     * @param joint
     * @param anchorIndex 1 for anchor 1, 2 for anchor 2
     * 
     * @return the new {@link BodyJointAnchorLink}.
     */
    public static BodyJointAnchorLink make( Body body, BallJoint joint, int anchorIndex )
    {
        BodyJointAnchorLink_Ball bjal = new BodyJointAnchorLink_Ball( body, joint, anchorIndex, false, 0f, 0f, 0f );
        
        body.addRepositionListener( bjal );
        
        return ( bjal );
    }
    
    /**
     * Creates a new {@link BodyJointAnchorLink} for a {@link BallJoint}.<br>
     * The link is directly added to the Body as a {@link RepositionListener3f}.<br>
     * You must remove it on your own in case.
     * 
     * @param body
     * @param joint
     * @param anchorIndex 1 for anchor 1, 2 for anchor 2
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * 
     * @return the new {@link BodyJointAnchorLink}.
     */
    public static BodyJointAnchorLink make( Body body, BallJoint joint, int anchorIndex, float offsetX, float offsetY, float offsetZ )
    {
        BodyJointAnchorLink_Ball bjal = new BodyJointAnchorLink_Ball( body, joint, anchorIndex, true, offsetX, offsetY, offsetZ );
        
        body.addRepositionListener( bjal );
        
        return ( bjal );
    }
    
    /**
     * Creates a new {@link BodyJointAnchorLink} for a {@link BallJoint}.<br>
     * The link is directly added to the Body as a {@link RepositionListener3f}.<br>
     * You must remove it on your own in case.
     * 
     * @param body
     * @param joint
     * 
     * @return the new {@link BodyJointAnchorLink}.
     */
    public static BodyJointAnchorLink make( Body body, HingeJoint joint )
    {
        BodyJointAnchorLink_Hinge bjal = new BodyJointAnchorLink_Hinge( body, joint, false, 0f, 0f, 0f );
        
        body.addRepositionListener( bjal );
        
        return ( bjal );
    }
    
    /**
     * Creates a new {@link BodyJointAnchorLink} for a {@link BallJoint}.<br>
     * The link is directly added to the Body as a {@link RepositionListener3f}.<br>
     * You must remove it on your own in case.
     * 
     * @param body
     * @param joint
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * 
     * @return the new {@link BodyJointAnchorLink}.
     */
    public static BodyJointAnchorLink make( Body body, HingeJoint joint, float offsetX, float offsetY, float offsetZ )
    {
        BodyJointAnchorLink_Hinge bjal = new BodyJointAnchorLink_Hinge( body, joint, true, offsetX, offsetY, offsetZ );
        
        body.addRepositionListener( bjal );
        
        return ( bjal );
    }
    
    /**
     * Creates a new {@link BodyJointAnchorLink} for a {@link BallJoint}.<br>
     * The link is directly added to the Body as a {@link RepositionListener3f}.<br>
     * You must remove it on your own in case.
     * 
     * @param body
     * @param joint
     * 
     * @return the new {@link BodyJointAnchorLink}.
     */
    public static BodyJointAnchorLink make( Body body, Hinge2Joint joint )
    {
        BodyJointAnchorLink_Hinge2 bjal = new BodyJointAnchorLink_Hinge2( body, joint, false, 0f, 0f, 0f );
        
        body.addRepositionListener( bjal );
        
        return ( bjal );
    }
    
    /**
     * Creates a new {@link BodyJointAnchorLink} for a {@link BallJoint}.<br>
     * The link is directly added to the Body as a {@link RepositionListener3f}.<br>
     * You must remove it on your own in case.
     * 
     * @param body
     * @param joint
     * @param offsetX
     * @param offsetY
     * @param offsetZ
     * 
     * @return the new {@link BodyJointAnchorLink}.
     */
    public static BodyJointAnchorLink make( Body body, Hinge2Joint joint, float offsetX, float offsetY, float offsetZ )
    {
        BodyJointAnchorLink_Hinge2 bjal = new BodyJointAnchorLink_Hinge2( body, joint, true, offsetX, offsetY, offsetZ );
        
        body.addRepositionListener( bjal );
        
        return ( bjal );
    }
}
