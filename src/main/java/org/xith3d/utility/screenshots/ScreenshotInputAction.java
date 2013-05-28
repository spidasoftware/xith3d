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
package org.xith3d.utility.screenshots;

import org.jagatoo.input.InputSystemException;
import org.jagatoo.input.actions.AbstractLabeledInvokableInputAction;
import org.jagatoo.input.devices.InputDevice;
import org.jagatoo.input.devices.components.DeviceComponent;

/**
 * Handles Screenshot generation by hot-key.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ScreenshotInputAction extends AbstractLabeledInvokableInputAction
{
    private ScreenshotEngine shotEngine;
    
    private String filenameBase = "screenshot";
    private boolean createAlphaChannel = false;
    
    /**
     * {@inheritDoc}
     */
    public String invokeAction( InputDevice device, DeviceComponent comp, int delta, int state, long nanoTime ) throws InputSystemException
    {
        if ( state > 0 )
        {
            shotEngine.takeScreenshot( filenameBase, createAlphaChannel );
            
            return ( "screenshot taken" );
        }
        
        return ( null );
    }
    
    /**
     * Sets the ScreenshotEngine to use for screenshot generation.
     * 
     * @param shotEngine the ScreenshotEngine to use for screenshot generation
     */
    public void setScreenshotEngine( ScreenshotEngine shotEngine )
    {
        this.shotEngine = shotEngine;
    }
    
    /**
     * @return the ScreenshotEngine to use for screenshot generation.
     */
    public final ScreenshotEngine getScreenshotEngine()
    {
        return ( shotEngine );
    }
    
    /**
     * Sets the base filename for the screenshot files.
     * 
     * @param filenameBase
     */
    public void setFilenameBase( String filenameBase )
    {
        this.filenameBase = filenameBase;
    }
    
    /**
     * @return the base filename for the screenshot files.
     */
    public final String getFilenameBase()
    {
        return ( filenameBase );
    }
    
    /**
     * Creates a new ScreenshotInputAction.
     * 
     * @param ordinal
     * @param text
     * @param shotEngine the ScreenshotEngine to use for screenshot generation
     * @param filenameBase the filename-base for the screenshot file names
     */
    public ScreenshotInputAction( int ordinal, String text, ScreenshotEngine shotEngine, String filenameBase )
    {
        super( ordinal, text );
        
        this.shotEngine = shotEngine;
        this.filenameBase = filenameBase;
    }
    
    /**
     * Creates a new SchreenshotManager.<br>
     * 
     * @param shotEngine the ScreenshotEngine to use for screenshot generation
     */
    public ScreenshotInputAction( ScreenshotEngine shotEngine, String filenameBase )
    {
        this( -1, "Take Screenshot", shotEngine, filenameBase );
    }
    
    /**
     * Creates a new SchreenshotManager.<br>
     * (default filename base = "screenshot")<br>
     * 
     * @param ordinal
     * @param text
     * @param shotEngine the ScreenshotEngine to use for screenshot generation
     */
    public ScreenshotInputAction( int ordinal, String text, ScreenshotEngine shotEngine )
    {
        this( ordinal, text, shotEngine, "screenshot" );
    }
    
    /**
     * Creates a new SchreenshotManager.<br>
     * (default filename base = "screenshot")<br>
     * 
     * @param shotEngine the ScreenshotEngine to use for screenshot generation
     */
    public ScreenshotInputAction( ScreenshotEngine shotEngine )
    {
        this( -1, "Take Screenshot", shotEngine, "screenshot" );
    }
}
