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
package org.xith3d.physics.simulation.joints;

import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.physics.simulation.Body;
import org.xith3d.physics.simulation.Joint;
import org.xith3d.physics.simulation.JointLimitMotor;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SliderJoint extends Joint
{
    protected JointLimitMotor limot = null;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final String getType()
    {
        return ( "Slider" );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getInfo()
    {
        return ( null );
    }
    
    /**
     * Sets the point relative to body2.
     * 
     * @param x
     * @param y
     * @param z
     */
    public abstract void setOffset( float x, float y, float z );
    
    /**
     * Sets the point relative to body2.
     * 
     * @param offset
     */
    public final void setOffset( Tuple3f offset )
    {
        setAxis( offset.getX(), offset.getY(), offset.getZ() );
    }
    
    /**
     * @return the point relative to body2.
     */
    public abstract Vector3f getOffset();
    
    public abstract void setAxis( float x, float y, float z );
    
    public final void setAxis( Vector3f axis )
    {
        setAxis( axis.getX(), axis.getY(), axis.getZ() );
    }
    
    /**
     * {@inheritDoc}
     */
    public abstract Vector3f getAxis();
    
    /**
     * Sets the initial relative rotation.
     * 
     * @param rot
     */
    public abstract void setInitialRotation( Matrix3f initialRotation );
    
    /**
     * Sets the initial relative rotation.
     * 
     * @param rotX
     * @param rotY
     * @param rotZ
     */
    public abstract void setInitialRotation( float x, float y, float z );
    
    /**
     * @return the initial relative rotation.
     */
    public abstract Matrix3f getInitialRotation();
    
    protected abstract void setLimitMotorImpl( JointLimitMotor limot );
    
    public final void setLimitMotor( JointLimitMotor limot )
    {
        this.limot = limot;
        
        setLimitMotorImpl( limot );
    }
    
    public final JointLimitMotor getLimitMotor()
    {
        return ( limot );
    }
    
    public SliderJoint( Body body1, Body body2 )
    {
        super( body1, body2 );
    }
}
