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
package org.xith3d.loop;

import org.jagatoo.util.timing.TimerInterface;

/**
 * The default implementation of {@link FPSLimiter}.
 * It guarantees, that the CPU load is kept at a minimum,
 * while animation might be juddering.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DefaultFPSLimiter implements FPSLimiter
{
    private static final long ONE_MIO = 1000000L;
    private static final long HALF_MIO = 500000L;
    
    private long accumulator = 0L;
    
    private static DefaultFPSLimiter instance = null;
    
    public static final DefaultFPSLimiter getInstance()
    {
        if ( instance == null )
            instance = new DefaultFPSLimiter();
        
        return ( instance );
    }
    
    /**
     * {@inheritDoc}
     */
    public long limitFPS( long frameIdx, long frameTime, long minFrameTime, TimerInterface timer )
    {
        accumulator += minFrameTime - frameTime;
        
        if ( accumulator <= 0L )
        {
            Thread.yield();
            return ( 0L );
        }
        
        long waitMillis = accumulator / ONE_MIO;
        accumulator -= waitMillis * ONE_MIO;
        
        if ( accumulator >= HALF_MIO )
        {
            accumulator -= ONE_MIO;
            waitMillis += 1L;
        }
        
        long t1 = timer.getNanoseconds();
        
        try
        {
            Thread.sleep( waitMillis );
        }
        catch ( InterruptedException e )
        {
            //e.printStackTrace();
        }
        
        long sleptTime = timer.getNanoseconds() - t1;
        accumulator -= sleptTime - waitMillis * ONE_MIO;
        
        return ( sleptTime );
    }
}
