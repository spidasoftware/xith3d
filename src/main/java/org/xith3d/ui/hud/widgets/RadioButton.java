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

import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.LabeledStateButton;
import org.xith3d.ui.hud.utils.HUDFont;

/**
 * RadioButton implementation for your HUD.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class RadioButton extends LabeledStateButton
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void cycleState()
    {
        if ( !getState() )
        {
            setState( true );
        }
    }
    
    /**
     * Creates a new RadioButton
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param text the text to be displayed
     * @param desc the Description object for this Widget
     */
    public RadioButton( boolean isHeavyWeight, float width, float height, String text, Description desc )
    {
        super( isHeavyWeight, width, height, text, desc );
    }
    
    private static final LabeledStateButton.Description deriveDesc( HUDFont font, Colorf fontColor )
    {
        LabeledStateButton.Description desc = HUD.getTheme().getRadioButtonDescription();
        
        if ( font != null )
        {
            desc.getLabelDescription().setFont( font, false );
            desc.getLabelDescription().setFont( Label.Description.deriveDisabledFont( font ), true );
        }
        
        if ( fontColor != null )
        {
            desc.getLabelDescription().setFontColor( fontColor, false );
        }
        
        return ( desc );
    }
    
    /**
     * Creates a new RadioButton.
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param text the text to be displayed
     * @param font the font to use for the label
     * @param fontColor the color to use for the label's font
     */
    public RadioButton( boolean isHeavyWeight, float width, float height, String text, HUDFont font, Colorf fontColor )
    {
        this( isHeavyWeight, width, height, text, deriveDesc( font, fontColor ) );
    }
    
    /**
     * Creates a new RadioButton
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param text the text to be displayed
     */
    public RadioButton( boolean isHeavyWeight, float width, float height, String text )
    {
        this( isHeavyWeight, width, height, text, HUD.getTheme().getRadioButtonDescription() );
    }
    
    /**
     * Creates a new RadioButton
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param text the text to be displayed
     * @param desc the Description object for this Widget
     */
    public RadioButton( float width, float height, String text, Description desc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, desc );
    }
    
    /**
     * Creates a new RadioButton.
     * 
     * @param width the desired width
     * @param height the desired height
     * @param text the text to be displayed
     * @param font the font to use for the label
     * @param fontColor the color to use for the label's font
     */
    public RadioButton( float width, float height, String text, HUDFont font, Colorf fontColor )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, deriveDesc( font, fontColor ) );
    }
    
    /**
     * Creates a new RadioButton
     * 
     * @param isHeavyWeight
     * @param width the desired width
     * @param height the desired height
     * @param text the text to be displayed
     */
    public RadioButton( float width, float height, String text )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, HUD.getTheme().getRadioButtonDescription() );
    }
}
