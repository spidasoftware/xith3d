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

import org.openmali.vecmath2.Vector3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.Transformable;
import org.xith3d.utility.interpolate.DistanceInterpolater;

/**
 * This class is useful to automatically translate a branch in your scenegraph.
 * Unlike the TranslatableGroup it translates a foreign group and does not
 * extend TransformGroup itself.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GroupTranslator extends GroupAnimator
{
    private DistanceInterpolater distX, distY, distZ, distU;
    private Vector3f transVec = new Vector3f();
    
    /**
     * @return a reference to this group's AngleInterpolater object.
     * 
     * @param axis the axis to get the AngleInterpolater for
     */
    protected DistanceInterpolater getDistanceInterpolater( TransformationDirectives.Axes axis )
    {
        if ( axis == null )
        {
            return ( distU );
        }
        
        switch ( axis )
        {
            case X_AXIS:
                return ( distX );
            case Y_AXIS:
                return ( distY );
            case Z_AXIS:
                return ( distZ );
            default:
                return ( null );
        }
    }
    
    /**
     * @return the current translation value [0; 2*pi] of the specified axis
     * 
     * @param axis the axis to get the AngleInterpolater for
     * @param gameMicros the time to get the value at
     */
    protected float getTranslationValue( TransformationDirectives.Axes axis, long gameMicros )
    {
        return ( getDistanceInterpolater( axis ).getValue( gameMicros ) );
    }
    
    /**
     * Starts the animation of this object.
     */
    @Override
    public void startAnimation( long gameTime, TimingMode timingMode )
    {
        final long micros = timingMode.getMicroSeconds( gameTime );
        
        if ( getTransformationDirectives().getUserAxis() == null )
        {
            distX.startIncreasing( micros );
            distY.startIncreasing( micros );
            distZ.startIncreasing( micros );
        }
        else
        {
            distU.startIncreasing( micros );
        }
        
        super.startAnimation( gameTime, timingMode );
    }
    
    /**
     * Stops the animation of this object.
     */
    @Override
    public void stopAnimation()
    {
        if ( getTransformationDirectives().getUserAxis() == null )
        {
            distX.stop();
            distY.stop();
            distZ.stop();
        }
        else
        {
            distU.stop();
        }
        
        super.stopAnimation();
    }
    
    /**
     * {@inheritDoc} 
     */
    @Override
    public boolean animate( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( !isAnimating() || ( getNumTransformNodes() == 0 ) )
            return ( false );
        
        final long micros = timingMode.getMicroSeconds( gameTime );
        
        if ( getTransformationDirectives().isDirty() )
        {
            setTransformationDirectives( getTransformationDirectives() );
        }
        
        if ( getTransformationDirectives().getUserAxis() == null )
        {
            transVec.setX( distX.getValue( micros ) );
            transVec.setY( distY.getValue( micros ) );
            transVec.setZ( distZ.getValue( micros ) );
        }
        else
        {
            transVec.set( getTransformationDirectives().getUserAxis() );
            
            transVec.scale( distU.getValue( micros ) );
        }
        
        t3dMain.setTranslation( transVec );
        
        this.setTransform( t3dMain );
        
        return ( true );
    }
    
    /**
     * Changes the TransformationDirectives used by this TranslatableGroup
     * 
     * @param transDirecs the new TransformationDirectives
     */
    @Override
    public void setTransformationDirectives( TransformationDirectives transDirecs )
    {
        if ( transDirecs == null )
            return;
        
        if ( transDirecs.getUserAxis() == null )
        {
            if ( distX != null )
            {
                distX.setValue( transDirecs.getInitValueX() );
                distX.setSpeed( transDirecs.getSpeedX() );
            }
            else
                distX = new DistanceInterpolater( transDirecs.getInitValueX(), transDirecs.getSpeedX(), 0.0f, Float.MAX_VALUE );
            
            if ( distY != null )
            {
                distY.setValue( transDirecs.getInitValueY() );
                distY.setSpeed( transDirecs.getSpeedY() );
            }
            else
                distY = new DistanceInterpolater( transDirecs.getInitValueY(), transDirecs.getSpeedY(), 0.0f, Float.MAX_VALUE );
            
            if ( distZ != null )
            {
                distZ.setValue( transDirecs.getInitValueZ() );
                distZ.setSpeed( transDirecs.getSpeedZ() );
            }
            else
                distZ = new DistanceInterpolater( transDirecs.getInitValueZ(), transDirecs.getSpeedZ(), 0.0f, Float.MAX_VALUE );
            
            this.distU = null;
        }
        else
        {
            this.distX = null;
            this.distY = null;
            this.distZ = null;
            
            if ( distU != null )
            {
                distU.setValue( transDirecs.getInitValueUser() );
                distU.setSpeed( transDirecs.getSpeedUser() );
            }
            else
                distU = new DistanceInterpolater( transDirecs.getInitValueUser(), transDirecs.getSpeedUser(), 0.0f, Float.MAX_VALUE );
        }
        
        super.setTransformationDirectives( transDirecs );
        
        transDirecs.setClean();
    }
    
    /**
     * Creates a new GroupTranslator with the given TransformationDirectives in use
     * 
     * @param tn the TransformNode to rotate
     * @param transDirecs the new TransformationDirectives
     */
    public GroupTranslator( Transformable tn, TransformationDirectives transDirecs )
    {
        super( tn, transDirecs );
    }
    
    /**
     * Creates a new GroupTranslator with the given TransformationDirectives in use
     * 
     * @param transDirecs the new TransformationDirectives
     */
    public GroupTranslator( TransformationDirectives transDirecs )
    {
        super( transDirecs );
    }
    
    /**
     * Creates a new GroupTranslator with default TransformationDirectives in use
     */
    public GroupTranslator( Transformable tn )
    {
        this( tn, new TransformationDirectives() );
    }
}
