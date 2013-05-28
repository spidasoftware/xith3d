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

import java.util.Random;

import org.openmali.vecmath2.Vector3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Animatable;

import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * This class is useful to automatically animate a branch in your scenegraph.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class AnimatableGroup extends TransformGroup implements Animatable
{
    protected static final Random RND = new Random( System.nanoTime() );
    
    protected final GroupAnimator groupAnimator;
    
    public GroupAnimator getGroupAnimator()
    {
        return ( groupAnimator );
    }
    
    /**
     * @return the directives for this Group's rotation
     */
    public TransformationDirectives getTransformationDirectives()
    {
        return ( groupAnimator.getTransformationDirectives() );
    }
    
    /**
     * Changes the TransformationDirectives used by this AnimatableGroup.
     * 
     * @param tfDirecs the new TransformationDirectives
     */
    public void setTransformationDirectives( TransformationDirectives tfDirecs )
    {
        this.groupAnimator.setTransformationDirectives( tfDirecs );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isAnimating()
    {
        return ( groupAnimator.isAnimating() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void startAnimation( long gameTime, TimingMode timingMode )
    {
        groupAnimator.startAnimation( gameTime, timingMode );
    }
    
    /**
     * {@inheritDoc}
     */
    public void stopAnimation()
    {
        groupAnimator.stopAnimation();
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isPersistent()
    {
        return ( groupAnimator.isPersistent() );
    }
    
    /**
     * Calculates positions and rotation of contained objects to render them at
     * their new placement, if the animation has been started.
     * 
     * @param gameTime the amount of milliseconds since the game started
     * @param frameTime miliseconds needed to render one frame
     * 
     * @return a boolean telling whether the rotation has been done or not 
     */
    public boolean animate( long gameTime, long frameTime, TimingMode timingMode )
    {
        return ( groupAnimator.animate( gameTime, frameTime, timingMode ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        animate( gameTime, frameTime, timingMode );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setAlive( boolean alive )
    {
        groupAnimator.setAlive( alive );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isAlive()
    {
        return ( groupAnimator.isAlive() );
    }
    
    /**
     * Moves this Group by the specified vector-components.
     * 
     * @param transX the x-amount to move by
     * @param transY the y-amount to move by
     * @param transZ the z-amount to move by
     */
    public void setTranslation( float transX, float transY, float transZ )
    {
        super.getTransform().setTranslation( transX, transY, transZ );
        //super.setTransform( super.getTransform() );
        updateTransform();
    }
    
    /**
     * Moves this Group by the specified vector.
     * 
     * @param translation the amount to move by
     */
    public void setTranslation( Vector3f translation )
    {
        setTranslation( translation.getX(), translation.getY(), translation.getZ() );
    }
    
    /**
     * Sets this group's scale to the specified factor.
     * should be implemented in the TransformGroup class to economize coding!
     * 
     * @param factor the factor to scale to
     */
    public void setScale( float factor )
    {
        super.getTransform().setScale( factor );
        //super.setTransform( super.getTransform() );
        updateTransform();
    }
    
    /**
     * Creates a new AnimatableGroup.
     * 
     * @param groupAnimator the GroupAnimator used to animate this group
     */
    public AnimatableGroup( GroupAnimator groupAnimator )
    {
        super();
        
        Transform3D t3d = new Transform3D();
        t3d.setIdentity();
        this.setTransform( t3d );
        
        this.groupAnimator = groupAnimator;
        
        if ( groupAnimator != null )
        {
            groupAnimator.setTransformNode( this );
        }
    }
}
