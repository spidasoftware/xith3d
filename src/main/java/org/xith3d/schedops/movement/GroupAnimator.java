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

import java.util.ArrayList;
import java.util.Random;

import org.openmali.vecmath2.Vector3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Animatable;

import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.Transformable;

/**
 * This class is useful to automatically animate a branch in your scenegraph.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class GroupAnimator implements Animatable
{
    private TransformationDirectives tfDirecs;
    
    protected final ArrayList< Transformable > transformNodes = new ArrayList< Transformable >();
    protected Transform3D t3dMain;
    private boolean isAnimating;
    private boolean isAlive;
    
    protected static Random rnd = new Random( System.currentTimeMillis() );
    
    /**
     * @return the directives for this Group's rotation
     */
    public TransformationDirectives getTransformationDirectives()
    {
        return ( tfDirecs );
    }
    
    /**
     * Changes the TransformationDirectives used by this AnimatableGroup.
     * 
     * @param tfDirecs the new TransformationDirectives
     */
    public void setTransformationDirectives( TransformationDirectives tfDirecs )
    {
        this.tfDirecs = tfDirecs;
    }
    
    /**
     * @return a boolean saying whether this AnimatableGroup is animating or not
     */
    public boolean isAnimating()
    {
        return ( isAnimating );
    }
    
    /**
     * Starts the animation of this object.
     */
    public void startAnimation( long gameTime, TimingMode timingMode )
    {
        isAnimating = true;
    }
    
    /**
     * Stops the animation of this object.
     */
    public void stopAnimation()
    {
        isAnimating = false;
    }
    
    /**
     * calculates positions and rotation of contained objects to render them at their new placement,
     * if the animation has been started.
     * 
     * @param gameTime the amount of milliseconds since the game started
     * @param frameTime miliseconds needed to render one frame
     * @param timingMode
     * 
     * @return a boolean telling whether the rotation has been done or not 
     */
    public abstract boolean animate( long gameTime, long frameTime, TimingMode timingMode );
    
    public final void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        animate( gameTime, frameTime, timingMode );
    }
    
    /**
     * This operation IS persistent.
     * This operation will remains scheduled after beeing executed once.
     */
    public final boolean isPersistent()
    {
        return ( true );
    }
    
    /**
     * Sets this Object alive or dead.
     * 
     * @param alive if false, the object will be removed from the scheduler next loop iteration
     */
    public final void setAlive( boolean alive )
    {
        this.isAlive = alive;
    }
    
    /**
     * If false, the object will be removed from the scheduler next loop iteration.
     */
    public final boolean isAlive()
    {
        return ( isAlive );
    }
    
    /**
     * This method could be implemented otherwise if the super method
     * wasn't final.
     */
    public final void setTransform( Transform3D transform )
    {
        for ( int i = 0; i < transformNodes.size(); i++ )
        {
            transformNodes.get( i ).setTransform( transform );
        }
        
        t3dMain = transform;
    }
    
    /**
     * Moves this Group by the specified vector-components.
     * 
     * @param transX the x-amount to move by
     * @param transY the y-amount to move by
     * @param transZ the z-amount to move by
     */
    public final void setTranslation( float transX, float transY, float transZ )
    {
        t3dMain.setTranslation( transX, transY, transZ );
        for ( int i = 0; i < transformNodes.size(); i++ )
        {
            transformNodes.get( i ).setTransform( t3dMain );
        }
    }
    
    /**
     * Moves this Group by the specified vector.
     * 
     * @param translation the amount to move by
     */
    public final void setTranslation( Vector3f translation )
    {
        setTranslation( translation.getX(), translation.getY(), translation.getZ() );
    }
    
    /**
     * Sets this group's scale to the specified factor.
     * should be implemented in the TransformGroup class to economize coding!
     * 
     * @param factor the factor to scale to
     */
    public final void setScale( float factor )
    {
        t3dMain.setScale( factor );
        for ( int i = 0; i < transformNodes.size(); i++ )
        {
            transformNodes.get( i ).setTransform( t3dMain );
        }
    }
    
    /**
     * @return the number of {@link Transformable}s, this GroupAnimator influences.
     */
    public final int getNumTransformNodes()
    {
        return ( transformNodes.size() );
    }
    
    /**
     * Sets the TransformGroup to animate.
     * 
     * @param tn the TransformNode to animate
     */
    public final void addTransformNode( Transformable tn )
    {
        this.transformNodes.add( tn );
        this.t3dMain = tn.getTransform();
        this.t3dMain.setIdentity();
        this.setTransform( t3dMain );
    }
    
    /**
     * Sets the TransformGroup to animate.
     * 
     * @param index
     * @param tn the TransformNode to animate
     */
    public final void setTransformNode( int index, Transformable tn )
    {
        if ( transformNodes.size() > index )
            this.transformNodes.set( index, tn );
        else
            this.transformNodes.add( index, tn );
        this.t3dMain = tn.getTransform();
        this.t3dMain.setIdentity();
        this.setTransform( t3dMain );
    }
    
    /**
     * Sets the TransformGroup to animate.
     * 
     * @param tn the TransformNode to animate
     */
    public final void setTransformNode( Transformable tn )
    {
        setTransformNode( 0, tn );
    }
    
    /**
     * @return the TransformNode to animate
     * 
     * @param index
     */
    public final Transformable getTransformNode( int index )
    {
        if ( transformNodes.size() <= index )
            return ( null );
        
        return ( transformNodes.get( index ) );
    }
    
    /**
     * @return the TransformNode to animate
     */
    public final Transformable getTransformNode()
    {
        return ( getTransformNode( 0 ) );
    }
    
    /**
     * Creates a new GroupAnimator.
     * 
     * @param tfDirecs the new TransformationDirectives
     */
    public GroupAnimator( TransformationDirectives tfDirecs )
    {
        super();
        
        this.isAnimating = false;
        this.isAlive = true;
        
        this.setTransformationDirectives( tfDirecs );
    }
    
    /**
     * Creates a new GroupAnimator.
     * 
     * @param tn the TransformNode to animate
     * @param tfDirecs the new TransformationDirectives
     */
    public GroupAnimator( Transformable tn, TransformationDirectives tfDirecs )
    {
        this( tfDirecs );
        
        if ( tn != null )
        {
            this.setTransformNode( tn );
        }
    }
}
