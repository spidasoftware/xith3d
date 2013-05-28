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
package org.xith3d.utility.timing;

import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;

/**
 * Displays each second on the command line the number of
 * milliseconds spent for a frame and the number of frames per second.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class PerformanceStats implements Updatable {
    
    // In milli-seconds
    private long accumulatedTime;
    
    private int frameCount = 0;
    
    private float fps = 0f;
    
    private long mpf = 0;
    
    private boolean printingEnabled;
    
    // Counted in milli-seconds
    private static long RESOLUTION = 1000;
    
    /**
     * Create a new PerformanceStats with printing enabled by default
     */
    public PerformanceStats() {
        this(true);
    }
    
    /**
     * Create a new PerformanceStats with printing enabled/disabled
     * 
     * @param printingEnabled
     *                If true, prints some stats on the command line each second
     */
    public PerformanceStats(boolean printingEnabled) {
        this.printingEnabled = printingEnabled;
    }
    
    public void update(long gameTime, long frameTime, TimingMode timingMode) {
        
        accumulatedTime += timingMode.getMilliSeconds(frameTime);
        frameCount++;
        
        if ((accumulatedTime) > RESOLUTION) {
            mpf = accumulatedTime / frameCount;
            fps = 1f / (accumulatedTime / frameCount) * RESOLUTION;
            if (printingEnabled) {
                print();
            }
            accumulatedTime = 0;
            frameCount = 0;
        }
        
    }
    
    private String round(float f) {
        
        return Float.toString(((int) (f * 1000)) / 1000);
        
    }
    
    private String format(long l) {
        
        Long object = new Long(l);
        String base = object.toString();
        String formatted = "";
        int count = 0;
        
        for (int i = base.length() - 1; i >= 0; i--) {
            formatted = base.substring(i, i + 1) + formatted;
            if (++count == 3 && base.length() >= 4) {
                count = 0;
                formatted = "'" + formatted;
            }
        }
        
        return formatted;
        
    }
    
    /**
     * @return the count of Frames per second (value updated each second)
     */
    public float getFPS() {
        return fps;
    }
    
    /**
     * @return the count of Millis per frame (value updated each second)
     */
    public long getMPF() {
        return mpf;
    }
    
    /**
     * @return if automatic stat printing is enabled
     */
    public boolean isPrintingEnabled() {
        return printingEnabled;
    }
    
    /**
     * @param printingEnabled set on/off automatic stat printing
     */
    public void setPrintingEnabled(boolean printingEnabled) {
        this.printingEnabled = printingEnabled;
    }
    
    /**
     * Print stats
     */
    public void print() {
        System.out.print("Millis per frame  = " + format(mpf));
        System.out.println("\t\tFrames per second = " + round(fps));
    }
    
}
