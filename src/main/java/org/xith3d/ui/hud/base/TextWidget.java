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
package org.xith3d.ui.hud.base;

import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * A TextWidget is a Widget type, that can hold a text.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface TextWidget
{
    /**
     * Sets the new text to be displayed
     * 
     * @param text the new Text
     */
    public abstract void setText( String text );
    
    /**
     * @return the text that is displayed
     */
    public abstract String getText();
    
    /**
     * Sets the new color to be used
     * 
     * @param color the new color
     */
    public abstract void setFontColor( Colorf color );
    
    /**
     * @return the used color
     */
    public abstract Colorf getFontColor();
    
    /**
     * Sets the new Font to be used
     * 
     * @param font the new Font
     */
    public abstract void setFont( HUDFont font );
    
    /**
     * @return the used Font
     */
    public abstract HUDFont getFont();
    
    /**
     * Sets the horizontal and vertical alignment of the text
     */
    public abstract void setAlignment( TextAlignment alignment );
    
    /**
     * @return the horizontal and vertical alignment of the text
     */
    public abstract TextAlignment getAlignment();
    
    /*
    protected TextWidget( float width, float height, int zIndex, boolean canHaveContent )
    {
        super( width, height, zIndex, canHaveContent );
    }
    */
}
