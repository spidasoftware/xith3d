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

/**
 * With JointLimitMotor limits can be attached to Joints,
 * but also motor-like behaviour.
 * <br><br>
 * If you want to use the limit functionality of JointLimitMotor,
 * you have to set lostop and histop.
 * <br><br>
 * If you want to use it as a motor, you have to set vel and fmax, where
 * <br> vel is the speed to which the Joint should reach
 * <br> fmax is the force that maximally can be applied to reach this target,
 * by default this is zero, so setting vel alone does not suffice.
 * 
 * Joints, where limits and motor functionality are possible have a field(s)
 * containing the JointLimitMotor, so as a user you won't have to call the
 * constructor yourself.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class JointLimitMotor
{
    private float vel;
    private float fmax;
    
    private float lowStop;
    private float highStop;
    
    // position
    
    private float fudge_factor;
    private float normal_cfm;
    
    private float stop_erp;
    private float stop_cfm;
    
    private float bounce;
    
    /**
     *  variables used between getInfo1() and getInfo2()
     *  0=free, 1=at lo limit, 2=at hi limit
     */
    private int limit;
    
    /** if at limit, amount over limit */
    private float limit_err;
    
    private boolean isDirty = true;
    
    public final boolean isDirty()
    {
        return ( isDirty );
    }
    
    public final void makeClean()
    {
        this.isDirty = false;
    }
    
    /**
     * powered joint : velocity
     * 
     * @param velocity
     */
    public final void setVelocity( float velocity )
    {
        this.vel = velocity;
        this.isDirty = true;
    }
    
    /**
     * @return powered joint : velocity
     */
    public final float getVelocity()
    {
        return ( vel );
    }
    
    /**
     * powered joint : max force
     * 
     * @param maxForce
     */
    public final void setMaxForce( float maxForce )
    {
        this.fmax = maxForce;
        this.isDirty = true;
    }
    
    /**
     * @return powered joint : max force
     */
    public final float getMaxForce()
    {
        return ( fmax );
    }
    
    /**
     * joint limit : lo stop (relative to initial)
     * 
     * @param lowStop
     */
    public final void setLowStop( float lowStop )
    {
        this.lowStop = lowStop;
        this.isDirty = true;
    }
    
    /**
     * @return joint limit : lo stop (relative to initial)
     */
    public final float getLowStop()
    {
        return ( lowStop );
    }
    
    /**
     * joint limit : hi stop (relative to initial)
     * 
     * @param hiStop
     */
    public final void setHighStop( float hiStop )
    {
        this.highStop = hiStop;
        this.isDirty = true;
    }
    
    /**
     * @return joint limit : hi stop (relative to initial)
     */
    public final float getHighStop()
    {
        return ( highStop );
    }
    
    /**
     * when powering away from joint limits
     * 
     * @param fudgeFactor
     */
    public final void setFudgeFactor( float fudgeFactor )
    {
        this.fudge_factor = fudgeFactor;
        this.isDirty = true;
    }
    
    /**
     * @return when powering away from joint limits
     */
    public final float getFudgeFactor()
    {
        return ( fudge_factor );
    }
    
    /**
     * cfm to use when not at a stop
     * 
     * @param normalCFM
     */
    public final void setNormalCFM( float normalCFM )
    {
        this.normal_cfm = normalCFM;
        this.isDirty = true;
    }
    
    /**
     * @return cfm to use when not at a stop
     */
    public final float getNormalCFM()
    {
        return ( normal_cfm );
    }
    
    /**
     * erp for when at joint limit
     * 
     * @param stopERP
     */
    public final void setStopERP( float stopERP )
    {
        this.stop_erp = stopERP;
        this.isDirty = true;
    }
    
    /**
     * @return erp for when at joint limit
     */
    public final float getStopERP()
    {
        return ( stop_erp );
    }
    
    /**
     * cfm for when at joint limit
     * 
     * @param stopCFM
     */
    public final void setStopCFM( float stopCFM )
    {
        this.stop_cfm = stopCFM;
        this.isDirty = true;
    }
    
    /**
     * @return cfm for when at joint limit
     */
    public final float getStopCFM()
    {
        return ( stop_cfm );
    }
    
    /**
     * restitution factor
     * 
     * @param bounce
     */
    public final void setBounce( float bounce )
    {
        this.bounce = bounce;
        this.isDirty = true;
    }
    
    /**
     * @return restitution factor
     */
    public final float getBounce()
    {
        return ( bounce );
    }
    
    /**
     * variables used between getInfo1() and getInfo2()
     * 0=free, 1=at lo limit, 2=at hi limit
     * 
     * @param limit
     */
    public final void setLimit( int limit )
    {
        this.limit = limit;
        this.isDirty = true;
    }
    
    /**
     * variables used between getInfo1() and getInfo2()
     * 0=free, 1=at lo limit, 2=at hi limit
     */
    public final int getLimit()
    {
        return ( limit );
    }
    
    /**
     * if at limit, amount over limit
     * 
     * @param limitErr
     */
    public final void setLimitError( float limitErr )
    {
        this.limit_err = limitErr;
        this.isDirty = true;
    }
    
    /**
     * @return if at limit, amount over limit
     */
    public final float getLimitError()
    {
        return ( limit_err );
    }
    
    /**
     * creates a new JointLimitMotor
     * You normally won't have to call this.
     * A JointLimitMotor-Field is already provided in the Joints able to have limits/motors.
     * 
     * @param defaultCFM
     * @param defaultERP
     */
    protected JointLimitMotor( float defaultCFM, float defaultERP )
    {
        this.vel = 0f;
        this.fmax = 0f;
        this.lowStop = Float.NEGATIVE_INFINITY;
        this.highStop = Float.POSITIVE_INFINITY;
        this.fudge_factor = 1f;
        this.normal_cfm = defaultCFM;
        this.stop_erp = defaultERP;
        this.stop_cfm = defaultCFM;
        this.bounce = 0f;
        this.limit = 0;
        this.limit_err = 0f;
    }
}
