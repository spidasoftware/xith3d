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
package org.xith3d.loop.opscheduler;

import java.io.File;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.loop.opscheduler.util.SchedOpsPool;
import org.xith3d.render.Canvas3D;
import org.xith3d.utility.logging.X3DLog;

/**
 * An instance of this class can be added to any implementation of OperationScheduler.
 * ExtXith3DEnvironment implements OperationScheduler.
 * 
 * It allows to call a takeScreenshot-method from any thread, and the shot is taken
 * at the next RenderLoop iteration.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ScheduledScreenshot extends ScheduledOperationImpl
{
    private Canvas3D canvas;
    private File file;
    private boolean alpha;
    
    /**
     * Sets the Canvas3D to take the screenshot from.
     */
    public void setCanvas3D( Canvas3D canvas )
    {
        this.canvas = canvas;
    }
    
    /**
     * @return the Canvas3D to take the screenshot from.
     */
    public Canvas3D getCanvas3D()
    {
        return ( canvas );
    }
    
    /**
     * Sets the file to write the screenshot to.
     */
    public void setFile( File file )
    {
        this.file = file;
    }
    
    /**
     * @return the file to write the screenshot to.
     */
    public File getFile()
    {
        return ( file );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        System.out.print( "taking screenshot..." );
        
        getCanvas3D().takeScreenshot( file, alpha );
        
        X3DLog.printlnEx( "screenshot saved at \"", file.getAbsolutePath(), "\"." );
        
        SchedOpsPool.deallocateSchededScreenshot( this );
    }
    
    /**
     * Creates a new instance.
     * 
     * @param canvas the Canvas3D to take the screenshot from
     * @param file the File to store the screenshot at
     * @param alpha with alpha channel?
     */
    public ScheduledScreenshot( Canvas3D canvas, File file, boolean alpha )
    {
        super( false );
        
        this.canvas = canvas;
        this.file = file;
        this.alpha = alpha;
    }
}
