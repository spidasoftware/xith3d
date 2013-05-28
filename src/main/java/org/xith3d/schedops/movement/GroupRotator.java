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

import org.openmali.FastMath;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.util.MatrixUtils;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.Transformable;
import org.xith3d.utility.interpolate.AngleInterpolater;

/**
 * This class is useful to automatically rotate a branch in your scenegraph.
 * Unlike the RotatableGroup it rotates a foreign group and does not extend
 * TransformGroup itself.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GroupRotator extends GroupAnimator
{
    private AngleInterpolater angleX, angleY, angleZ, angleU;
    private Matrix3f rotMatX = new Matrix3f(),
                     rotMatY = new Matrix3f(),
                     rotMatZ = new Matrix3f(),
                     rotMat  = new Matrix3f();
    
    /**
     * @return a reference to this group's AngleInterpolater object
     * 
     * @param axis the axis to get the AngleInterpolater for
     */
    protected AngleInterpolater getAngleInterpolater( TransformationDirectives.Axes axis )
    {
        if ( axis == null )
        {
            return ( angleU );
        }
        
        switch ( axis )
        {
            case X_AXIS:
                return ( angleX );
            case Y_AXIS:
                return ( angleY );
            case Z_AXIS:
                return ( angleZ );
            default:
                return ( null );
        }
    }
    
    /**
     * @return the current rotation value [0; 2*pi] of the specified axis
     * 
     * @param axis the axis to get the AngleInterpolater for
     * @param gameMicros the time to get the value at
     */
    protected float getRotationValue( TransformationDirectives.Axes axis, long gameMicros )
    {
        return ( getAngleInterpolater( axis ).getValue( gameMicros ) );
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
            angleX.startIncreasing( micros );
            angleY.startIncreasing( micros );
            angleZ.startIncreasing( micros );
        }
        else
        {
            angleU.startIncreasing( micros );
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
            angleX.stop();
            angleY.stop();
            angleZ.stop();
        }
        else
        {
            angleU.stop();
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
            final float x = angleX.getValue( micros );
            final float y = angleY.getValue( micros );
            final float z = angleZ.getValue( micros );
            final float sin_x = FastMath.sin( x );
            final float cos_x = FastMath.cos( x );
            final float sin_y = FastMath.sin( y );
            final float cos_y = FastMath.cos( y );
            final float sin_z = FastMath.sin( z );
            final float cos_z = FastMath.cos( z );
            
            /*
            t3dMain.setRotation( angleX.getValue( micros ),
                                 angleY.getValue( micros ),
                                 angleZ.getValue( micros ) );
            */

            rotMatX.set( 1f, 0f, 0f, 0f, cos_x, -sin_x, 0f, sin_x, cos_x );
            rotMatY.set( cos_y, 0f, sin_y, 0f, 1f, 0f, -sin_y, 0f, cos_y );
            rotMatZ.set( cos_z, -sin_z, 0f, sin_z, cos_z, 0f, 0f, 0f, 1f );
            
            switch ( getTransformationDirectives().getAxisOrder() )
            {
                case XYZ:
                    rotMatX.mul( rotMatY );
                    rotMatX.mul( rotMatZ );
                    rotMat = rotMatX;
                    break;
                case XZY:
                    rotMatX.mul( rotMatZ );
                    rotMatX.mul( rotMatY );
                    rotMat = rotMatX;
                    break;
                case YXZ:
                    rotMatY.mul( rotMatX );
                    rotMatY.mul( rotMatZ );
                    rotMat = rotMatY;
                    break;
                case YZX:
                    rotMatY.mul( rotMatZ );
                    rotMatY.mul( rotMatY );
                    rotMat = rotMatY;
                    break;
                case ZXY:
                    rotMatZ.mul( rotMatX );
                    rotMatZ.mul( rotMatY );
                    rotMat = rotMatZ;
                    break;
                case ZYX:
                    rotMatZ.mul( rotMatY );
                    rotMatZ.mul( rotMatX );
                    rotMat = rotMatZ;
                    break;
            }
        }
        else
        {
            final Vector3f axis = getTransformationDirectives().getUserAxis();
            final float angle = angleU.getValue( micros );
            
            MatrixUtils.getRotationMatrix( axis, angle, rotMat );
        }
        
        t3dMain.setRotation( rotMat );
        
        this.setTransform( t3dMain );
        
        return ( true );
    }
    
    /**
     * Changes the RotationDirectives used by this RotatableGroup.
     * 
     * @param rotDirecs the new RotationDirectives
     */
    @Override
    public void setTransformationDirectives( TransformationDirectives rotDirecs )
    {
        if ( rotDirecs == null )
            return;
        
        if ( rotDirecs.getUserAxis() == null )
        {
            if ( angleX != null )
            {
                angleX.setValue( rotDirecs.getInitValueX() * FastMath.TWO_PI );
                angleX.setSpeed( rotDirecs.getSpeedX() * FastMath.TWO_PI );
            }
            else
                angleX = new AngleInterpolater( rotDirecs.getInitValueX() * FastMath.TWO_PI, rotDirecs.getSpeedX() * FastMath.TWO_PI, 0.0f, FastMath.TWO_PI, true );
            
            if ( angleY != null )
            {
                angleY.setValue( rotDirecs.getInitValueY() * FastMath.TWO_PI );
                angleY.setSpeed( rotDirecs.getSpeedY() * FastMath.TWO_PI );
            }
            else
                angleY = new AngleInterpolater( rotDirecs.getInitValueY() * FastMath.TWO_PI, rotDirecs.getSpeedY() * FastMath.TWO_PI, 0.0f, FastMath.TWO_PI, true );
            
            if ( angleZ != null )
            {
                angleZ.setValue( rotDirecs.getInitValueZ() * FastMath.TWO_PI );
                angleZ.setSpeed( rotDirecs.getSpeedZ() * FastMath.TWO_PI );
            }
            else
                angleZ = new AngleInterpolater( rotDirecs.getInitValueZ() * FastMath.TWO_PI, rotDirecs.getSpeedZ() * FastMath.TWO_PI, 0.0f, FastMath.TWO_PI, true );
            
            this.angleU = null;
        }
        else
        {
            this.angleX = null;
            this.angleY = null;
            this.angleZ = null;
            
            if ( angleU != null )
            {
                angleU.setValue( rotDirecs.getInitValueUser() * FastMath.TWO_PI );
                angleU.setSpeed( rotDirecs.getSpeedUser() * FastMath.TWO_PI );
            }
            else
                angleU = new AngleInterpolater( rotDirecs.getInitValueUser() * FastMath.TWO_PI, rotDirecs.getSpeedUser() * FastMath.TWO_PI, 0.0f, FastMath.TWO_PI, true );
        }
        
        super.setTransformationDirectives( rotDirecs );
        
        rotDirecs.setClean();
    }
    
    /**
     * Creates a new GroupRotater with the given TransformationDirectives in use.
     * 
     * @param tn the TransformNode to rotate
     * @param rotDirecs the new TransformationDirectives
     */
    public GroupRotator( Transformable tn, TransformationDirectives rotDirecs )
    {
        super( tn, rotDirecs );
    }
    
    /**
     * Creates a new GroupRotater with the given TransformationDirectives in use.
     * 
     * @param rotDirecs the new TransformationDirectives
     */
    public GroupRotator( TransformationDirectives rotDirecs )
    {
        super( rotDirecs );
    }
    
    /**
     * Creates a new GroupRotater with default TransformationDirectives in use.
     */
    public GroupRotator( Transformable tn )
    {
        this( tn, new TransformationDirectives() );
    }
}
