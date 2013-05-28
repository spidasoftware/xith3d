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
package org.xith3d.ui.hud.widgets;

import org.xith3d.loop.FPSListener;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * Simple Widget to display the FPS count of your application.
 * You can easily add it to an instance of ExtRenderLoop.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FPSCounter extends Label implements FPSListener
{
    private char decimalSep = '.';
    private float lastFPS = -1.0f;
    
    private String prefix = null;
    private String postfix = null;
    
    /**
     * Sets the prefix to be set to the FPS value.
     */
    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
        
        setText( prefix, lastFPS, decimalSep, 2, postfix );
    }
    
    /**
     * @return the postfix to be appended to the FPS value
     */
    public final String getPrefix()
    {
        return ( prefix );
    }
    
    /**
     * Sets the postfix to be appended to the FPS value.
     */
    public void setPostfix( String postfix )
    {
        this.postfix = postfix;
        
        setText( prefix, lastFPS, decimalSep, 2, postfix );
    }
    
    /**
     * Sets the postfix to be appended to the FPS value.
     */
    public final String getPostfix()
    {
        return ( postfix );
    }
    
    /**
     * Changes the decimal separator to the given char.<br>
     * Use '\0' to not display any decimal places.
     * 
     * @param decSep
     */
    public void setDecimalSeparator( char decSep )
    {
        this.decimalSep = decSep;
        
        setText( prefix, lastFPS, decimalSep, 2, postfix );
    }
    
    public final char getDecimalSeparator()
    {
        return ( decimalSep );
    }
    
    /**
     * @return the last notified FPS value
     */
    public final float getLastFPS()
    {
        return ( lastFPS );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onFPSCountIntervalHit( float fps )
    {
        this.lastFPS = fps;
        
        setText( prefix, lastFPS, decimalSep, 2, postfix );
    }
    
    /**
     * Creates a new FPSCounter with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTexture
     */
    public FPSCounter( boolean isHeavyWeight, float width, float height, Texture2D backgroundTexture )
    {
        super( isHeavyWeight, width, height, "", null, null, TextAlignment.CENTER_CENTER );
        
        setBackgroundTexture( backgroundTexture );
    }
    
    /**
     * Creates a new FPSCounter with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTexture
     */
    public FPSCounter( boolean isHeavyWeight, float width, float height, String backgroundTexture )
    {
        this( isHeavyWeight, width, height, HUDTextureUtils.getTexture( backgroundTexture, true ) );
    }
    
    /**
     * Creates a new FPSCounter with the given width and height.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     */
    public FPSCounter( boolean isHeavyWeight, float width, float height )
    {
        this( isHeavyWeight, width, height, (Texture2D)null );
    }
    
    /**
     * Creates a new FPSCounter with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTexture
     */
    public FPSCounter( float width, float height, Texture2D backgroundTexture )
    {
        this( false, width, height );
    }
    
    /**
     * Creates a new FPSCounter with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param backgroundTexture
     */
    public FPSCounter( float width, float height, String backgroundTexture )
    {
        this( false, width, height, backgroundTexture );
    }
    
    /**
     * Creates a new FPSCounter with the given width and height.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     */
    public FPSCounter( float width, float height )
    {
        this( false, width, height );
    }
}
