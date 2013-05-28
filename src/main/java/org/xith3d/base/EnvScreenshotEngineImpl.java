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
package org.xith3d.base;

import java.io.File;

import org.xith3d.loop.opscheduler.ScheduledScreenshot;
import org.xith3d.loop.opscheduler.util.SchedOpsPool;
import org.xith3d.render.Canvas3D;

/**
 * Simple implementation of the EnvScreenshotEngine interface.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
class EnvScreenshotEngineImpl implements EnvScreenshotEngine
{
    private final Xith3DEnvironment env;
    
    /**
     * {@inheritDoc}
     */
    public void takeScreenshot( Canvas3D canvas, File file, boolean alpha )
    {
        if ( env.getOperationScheduler() == null )
        {
            System.out.print( "taking screenshot..." );
            
            canvas.takeScreenshot( file, true );
            
            System.out.println( "screenshot: " + file.getAbsolutePath() + " saved." );
        }
        else
        {
            final ScheduledScreenshot schededShot = SchedOpsPool.allocateSchededScreenshot( canvas, file, alpha );
            
            env.getOperationScheduler().scheduleOperation( schededShot );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void takeScreenshot( File file, boolean alpha )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the Environment. Cannot take screenshot." );
        
        env.getCanvas().takeScreenshot( file, alpha );
    }
    
    /**
     * {@inheritDoc}
     */
    public File takeScreenshot( Canvas3D canvas, String filenameBase, boolean alpha )
    {
        return ( canvas.takeScreenshot( filenameBase, alpha ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public File takeScreenshot( Canvas3D canvas, boolean alpha )
    {
        return ( canvas.takeScreenshot( "screenshot", alpha ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public File takeScreenshot( String filenameBase, boolean alpha )
    {
        if ( env.getCanvas() == null )
            throw new NullPointerException( "No Canvas3D added to the Environment. Cannot take screenshot." );
        
        return ( env.getCanvas().takeScreenshot( filenameBase, alpha ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public File takeScreenshot( boolean alpha )
    {
        return ( takeScreenshot( "screenshot", alpha ) );
    }
    
    EnvScreenshotEngineImpl( Xith3DEnvironment env )
    {
        this.env = env;
    }
}
