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

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.utils.HUDFont;

/**
 * A ToggleButton is a Button, that stays PRESSED until it gets clicked again.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ToggleButton extends Button
{
    private boolean isToggled;
    
    /**
     * Sets if the ToggleButton is currently toggled.
     */
    public void setToggled( boolean toggled )
    {
        this.isToggled = toggled;
        
        if ( isToggled )
            buttonState = ButtonState.PRESSED;
        else
            buttonState = ButtonState.NORMAL;
        
        update();
    }
    
    /**
     * @return true, if the ToggleButton is currently toggled
     */
    public final boolean isToggled()
    {
        return ( isToggled );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseEntered( boolean isTopMost, boolean hasFocus )
    {
        isStateChangable = false;
        super.onMouseEntered( isTopMost, hasFocus );
        isStateChangable = true;
        
        if ( isTopMost && !isToggled && isEnabled() )
        {
            buttonState = ButtonState.HOVERED;
            update();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseExited( boolean isTopMost, boolean hasFocus )
    {
        isStateChangable = false;
        super.onMouseExited( isTopMost, hasFocus );
        isStateChangable = true;
        
        if ( isTopMost && !isToggled && isEnabled() )
        {
            setButtonState( ButtonState.NORMAL );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonPressed( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        isStateChangable = false;
        super.onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
        isStateChangable = true;
        
        if ( isTopMost && isEnabled() )
        {
            setButtonState( ButtonState.PRESSED );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMouseButtonReleased( MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        isStateChangable = false;
        super.onMouseButtonReleased( button, x, y, when, lastWhen, isTopMost, hasFocus );
        isStateChangable = true;
        
        if ( isTopMost && isEnabled() )
        {
            if ( isToggled )
            {
                isToggled = false;
                setButtonState( ButtonState.HOVERED );
            }
            else
            {
                isToggled = true;
                setButtonState( ButtonState.PRESSED );
            }
            update();
            
            fireButtonClickedEvent();
        }
    }
    
    /**
     * Create a new ToggleButton.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param zIndex the new z-index of this Widget
     * @param text the text to display on the Button
     * @param desc Button.Description
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, String text, Description desc )
    {
        super( isHeavyWeight, width, height, text, desc );
        
        this.isToggled = false;
    }
    
    /**
     * Create a new ToggleButton.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param zIndex the new z-index of this Widget
     * @param text the text to display on the Button
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, String text )
    {
        this( isHeavyWeight, width, height, text, HUD.getTheme().getButtonDescription() );
    }
    
    /**
     * Creates a new ToggleButton.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, String text, HUDFont font, Colorf color )
    {
        this( isHeavyWeight, width, height, text, deriveDesc( font, color ) );
    }
    
    /**
     * Creates a new ToggleButton.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, String text, HUDFont font )
    {
        this( isHeavyWeight, width, height, text, deriveDesc( font, null ) );
    }
    
    /**
     * Creates a new ToggleButton.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param color
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, String text, Colorf color )
    {
        this( isHeavyWeight, width, height, text, deriveDesc( null, color ) );
    }
    
    /**
     * Create a new ToggleButton.
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param zIndex the new z-index of this Widget
     * @param text the text to display on the Button
     * @param desc Button.Description
     */
    public ToggleButton( float width, float height, String text, Description desc )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, desc );
    }
    
    /**
     * Create a new ToggleButton.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param zIndex the new z-index of this Widget
     * @param text the text to display on the Button
     */
    public ToggleButton( float width, float height, String text )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, HUD.getTheme().getButtonDescription() );
    }
    
    /**
     * Creates a new ToggleButton.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public ToggleButton( float width, float height, String text, HUDFont font, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, deriveDesc( font, color ) );
    }
    
    /**
     * Creates a new ToggleButton.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param font
     */
    public ToggleButton( float width, float height, String text, HUDFont font )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, deriveDesc( font, null ) );
    }
    
    /**
     * Creates a new ToggleButton.
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param text the text to display on the Button
     * @param color
     */
    public ToggleButton( float width, float height, String text, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, deriveDesc( null, color ) );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( isHeavyWeight, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, String normalTexture, String hoveredTexture, String pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( isHeavyWeight, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture )
    {
        this( isHeavyWeight, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param isHeavyWeight
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public ToggleButton( boolean isHeavyWeight, float width, float height, String normalTexture, String hoveredTexture, String pressedTexture )
    {
        this( isHeavyWeight, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public ToggleButton( float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     * @param text the text to display on the Button
     * @param font
     * @param color
     */
    public ToggleButton( float width, float height, String normalTexture, String hoveredTexture, String pressedTexture, String text, HUDFont font, Colorf color )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, text, createImageButtonDesc( normalTexture, hoveredTexture, pressedTexture, font, color ) );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public ToggleButton( float width, float height, Texture2D normalTexture, Texture2D hoveredTexture, Texture2D pressedTexture )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
    
    /**
     * Creates a new Image ToggleButton (textures will simply be streched over the whole Button area).
     * 
     * @param width the new width of this Widget
     * @param height the new height of this Widget
     * @param texNormal the texture for the normal button state
     * @param texHovered the texture for the normal button state
     * @param texPressed the texture for the normal button state
     */
    public ToggleButton( float width, float height, String normalTexture, String hoveredTexture, String pressedTexture )
    {
        this( DEFAULT_HEAVYWEIGHT, width, height, normalTexture, hoveredTexture, pressedTexture, null, null, null );
    }
}
