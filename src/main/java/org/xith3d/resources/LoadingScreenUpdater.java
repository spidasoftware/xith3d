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
package org.xith3d.resources;

import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.widgets.assemblies.LoadingScreen;

/**
 * This interface is implemented by LoadingScreen. It makes LoadingScreen
 * independent of the actual implementation. So just use this interface
 * as parameter type in your game's loading methods.
 * 
 * @see LoadingScreen
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface LoadingScreenUpdater
{
    /**
     * Initializes the progress, caption and background image.
     * The progress value is resetted to zero.
     * 
     * @param maxValue the new maximum progress value
     * @param caption the new caption (or null to keep the old one)
     * @param backgroundTexture the new background Texture (or null to keep the old one)
     */
    public void init( int maxValue, String caption, Texture2D backgroundTexture );
    
    /**
     * Initializes the progress, caption and background image.
     * The progress value is resetted to zero.
     * 
     * @param maxValue the new maximum progress value
     * @param caption the new caption (or null to keep the old one)
     * @param backgroundTexture the new background Texture (or null to keep the old one)
     */
    public void init( int maxValue, String caption, String backgroundTexture );
    
    /**
     * Updates the progress value, caption and background image.
     * 
     * @param incValue the value to add to the current progress value
     * @param caption the new caption (or null to keep the old one)
     * @param backgroundTexture the new background Texture (or null to keep the old one)
     */
    public void update( int incValue, String caption, Texture2D backgroundTexture );
    
    /**
     * Updates the progress value and background image.
     * 
     * @param incValue the value to add to the current progress value
     * @param backgroundTexture the new background Texture (or null to keep the old one)
     */
    public void update( int incValue, Texture2D backgroundTexture );
    
    /**
     * Increases the progress value by one and updates the background image.
     * 
     * @param backgroundTexture the new background Texture (or null to keep the old one)
     */
    public void update( Texture2D backgroundTexture );
    
    /**
     * Updates the background image only.
     * 
     * @param backgroundTexture the new background Texture (or null to keep the old one)
     */
    public void updateOnly( Texture2D backgroundTexture );
    
    /**
     * Updates the progress value, caption and background image.
     * 
     * @param incValue the value to add to the current progress value
     * @param caption the new caption (or null to keep the old one)
     * @param backgroundTexture the new background Texture (or null to keep the old one)
     */
    public void update( int incValue, String caption, String backgroundTexture );
    
    /**
     * Updates the progress value and caption.
     * 
     * @param incValue the value to add to the current progress value
     * @param caption the new caption (or null to keep the old one)
     */
    public void update( int incValue, String caption );
    
    /**
     * Updates the progress value and leaves the caption unchanged.
     * 
     * @param incValue the value to add to the current progress value
     */
    public void update( int incValue );
    
    /**
     * Updates the caption only.
     * 
     * @param caption the new caption (or null to keep the old one)
     */
    public void updateOnly( String caption );
    
    /**
     * Updates the progress value incremented by one and sets the new caption.
     * 
     * @param caption the new caption (or null to keep the old one)
     */
    public void update( String caption );
    
    /**
     * Updates the progress value incremented by one and leaves the caption unchanged.
     */
    public void update();
}
