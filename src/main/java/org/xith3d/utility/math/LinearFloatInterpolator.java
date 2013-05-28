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
package org.xith3d.utility.math;

import org.xith3d.utility.logging.X3DLog;

/**
 * LinearFloatInterpolator interpolates a float linearly
 * between two values.
 * Use it whenever you need non-instantaneous (="progressive")
 * actions, e.g. opening a door, moving blocks.. With interpolators,
 * your game can be much smoother.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class LinearFloatInterpolator implements Interpolator {
    
    private float value;
    private float value1;
    private float value2;
    
    /**
     * Create a new LinearFloatInterpolator.
     * Note : alpha is set at 0f when this constructor is called,
     * which means that if you don't call setAlpha()
     * @param value1 The value which will be set when alpha is at 0
     * @param value2 The value which will be set when alpha is at 1
     */
    public LinearFloatInterpolator(float value1, float value2) {
        
        this.value1 = value1;
        this.value2 = value2;
        setAlpha(0f);
        
    }
    
    /**
     * Set the alpha of this interpolator
     * Changing alpha progressively from 0 to 1 changes the value from
     * value1 to value2
     * If alpha = 0f, the value of this interpolator will be value1
     * If alpha = 1f, it will be value2
     * If it's between 0f and 1f (inclusive) it will be between value1 and value2
     */
    public void setAlpha(float alpha) {
        
        if(alpha < 0 || alpha > 1) {
            Error er = new Error("Alpha = " + alpha + " !! outside [0f-1f] range !!");
            X3DLog.print(er);
            throw er;
        }
        
        final float beta = 1 - alpha;
        this.value = beta * value1 + alpha * value2;
        
    }

    /**
     * @return the value
     */
    public float getValue() {
        return value;
    }


    /**
     * @return the value1
     */
    public float getValue1() {
        return value1;
    }

    /**
     * @param value1 the value1 to set
     */
    public void setValue1(float value1) {
        this.value1 = value1;
    }

    /**
     * @return the value2
     */
    public float getValue2() {
        return value2;
    }

    /**
     * @param value2 the value2 to set
     */
    public void setValue2(float value2) {
        this.value2 = value2;
    }
    
}
