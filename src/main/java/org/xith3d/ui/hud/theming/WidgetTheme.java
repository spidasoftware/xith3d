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
package org.xith3d.ui.hud.theming;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jagatoo.datatypes.NamedObject;
import org.openmali.vecmath2.Colorf;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loaders.texture.TextureStreamLocatorZip;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.ui.hud.base.Border;
import org.xith3d.ui.hud.base.LabeledStateButton;
import org.xith3d.ui.hud.base.WindowHeaderWidget;
import org.xith3d.ui.hud.utils.Cursor;
import org.xith3d.ui.hud.utils.CursorSet;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.widgets.Button;
import org.xith3d.ui.hud.widgets.ComboBox;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.hud.widgets.List;
import org.xith3d.ui.hud.widgets.ProgressBar;
import org.xith3d.ui.hud.widgets.Scrollbar;
import org.xith3d.ui.hud.widgets.Slider;
import org.xith3d.ui.hud.widgets.TextField;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * Extend this class when you create a Widget-theme for your HUD.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class WidgetTheme implements NamedObject
{
    protected ThemeProperties themeProps;
    
    private Label.Description labelDesc = null;
    private TextField.Description textFieldDesc = null;
    
    private Button.Description buttonDesc = null;
    
    private Border.Description roundedCornersBorderDesc = null;
    private Border.Description loweredBevelBorderDesc = null;
    private Border.Description raisedBevelBorderDesc = null;
    
    private Scrollbar.Description scrollbarDescHoriz = null;
    private Scrollbar.Description scrollbarDescVert = null;
    
    private Slider.Description sliderDesc = null;
    
    private LabeledStateButton.Description radioButtonDesc = null;
    private LabeledStateButton.Description checkboxDesc = null;
    
    private WindowHeaderWidget.Description windowHeaderDesc = null;
    private Border.Description windowBorderDesc = null;
    
    private List.Description listDesc = null;
    private ComboBox.Description comboDesc = null;
    
    private ProgressBar.Description progressbarDesc = null;
    
    private Texture2D frame_contentpane_background_texture = null;
    
    private CursorSet cursorSet = null;
    
    /**
     * @return the name of this WidgetTheme
     */
    public String getName()
    {
        return ( themeProps.name  );
    }
    
    /**
     * Sets the default Font of this theme.
     */
    public void setFont( HUDFont font, boolean disabled )
    {
        if ( disabled )
            themeProps.font_disabled = font;
        else
            themeProps.font = font;
    }
    
    /**
     * @return the default Font of this theme
     */
    public HUDFont getFont( boolean disabled )
    {
        if ( disabled )
            return ( themeProps.font_disabled );
        
        return ( themeProps.font  );
    }
    
    /**
     * Sets the default font-color of this theme.
     */
    public void setFontColor( Colorf color, boolean disabled )
    {
        if ( disabled )
            themeProps.font_disabled_color = color;
        else
            themeProps.font_color = color;
    }
    
    /**
     * @return the default font-color of this theme
     */
    public Colorf getFontColor( boolean disabled )
    {
        if ( disabled )
            return ( themeProps.font_disabled_color  );
        
        return ( themeProps.font_color  );
    }
    
    /**
     * Sets the default Label.Description.
     */
    public void setLabelDescription( Label.Description desc )
    {
        this.labelDesc = desc;
    }
    
    private final Label.Description initLabelDescription()
    {
        if ( this.labelDesc == null )
        {
            Label.Description labelDesc =
                new Label.Description(
                    null, (Texture2D)null,
                    themeProps.label_font, themeProps.label_font_disabled,
                    themeProps.label_font_color, themeProps.label_font_disabled_color,
                    TextAlignment.TOP_LEFT
                );
            
            this.labelDesc = labelDesc;
        }
        
        return ( this.labelDesc );
    }
    
    /**
     * @return the default Label.Description
     */
    public Label.Description getLabelDescription()
    {
        return ( this.initLabelDescription().clone() );
    }
    
    /**
     * @param desc the default Label.Description
     */
    public void getLabelDescription( Label.Description desc )
    {
        desc.set( this.initLabelDescription() );
    }
    
    public Texture2D getTextCaretTexture( String suffix )
    {
        return ( HUDTextureUtils.getTexture( getName() + "/carets/text-caret-" + suffix + ".png", false ) );
    }
    
    /**
     * Sets the default TextField.Description.
     */
    public void setTextFieldDescription( TextField.Description desc )
    {
        this.textFieldDesc = desc;
    }
    
    private final TextField.Description initTextFieldDescription()
    {
        if ( this.textFieldDesc == null )
        {
            TextField.Description textFieldDesc =
                new TextField.Description(
                    this.initLabelDescription(),
                    new Border.Description(
                        themeProps.textfield_border_size_bottom,
                        themeProps.textfield_border_size_right,
                        themeProps.textfield_border_size_top,
                        themeProps.textfield_border_size_left,
                        HUDTextureUtils.getTexture( getName() + "/borders/textfield-normal.png", false )
                    ),
                    HUDTextureUtils.getTexture( getName() + "/carets/text-caret-" + themeProps.textfield_caret_texture + ".png", false )
                );
            
            textFieldDesc.setAlignment( TextAlignment.CENTER_LEFT );
            textFieldDesc.setBackgroundColor( themeProps.textfield_background_color );
            
            this.textFieldDesc = textFieldDesc;
        }
        
        return ( this.textFieldDesc );
    }
    
    /**
     * @return the default TextField.Description
     */
    public TextField.Description getTextFieldDescription()
    {
        return ( this.initTextFieldDescription().clone() );
    }
    
    /**
     * @param desc the default TextField.Description
     */
    public void getTextFieldDescription( TextField.Description desc )
    {
        desc.set( this.initTextFieldDescription() );
    }
    
    /**
     * Sets the default Scrollbar.Description for a HORIZONTAL Scrollbar.
     */
    public void setScrollbarDescriptionHorizontal( Scrollbar.Description desc )
    {
        this.scrollbarDescHoriz = desc;
    }
    
    private final Scrollbar.Description initScrollbarDescriptionHorizontal()
    {
        if ( this.scrollbarDescHoriz == null )
        {
            Scrollbar.Description sbDesc =
                new Scrollbar.Description(
                    Scrollbar.Direction.HORIZONTAL,
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/horizontal/background.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/horizontal/handle_left.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/horizontal/handle_right.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/horizontal/handle_body.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/horizontal/handle_decoration.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/horizontal/decrementor.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/horizontal/incrementor.png", false ),
                    themeProps.scrollbar_smoothscrolling
                );
            
            this.scrollbarDescHoriz = sbDesc;
        }
        
        return ( this.scrollbarDescHoriz );
    }
    
    /**
     * @return the default Scrollbar.Description for a HORIZONTAL Scrollbar
     */
    public Scrollbar.Description getScrollbarDescriptionHorizontal()
    {
        return ( this.initScrollbarDescriptionHorizontal().clone() );
    }
    
    /**
     * @param desc the default Scrollbar.Description for a HORIZONTAL Scrollbar
     */
    public void getScrollbarDescriptionHorizontal( Scrollbar.Description desc )
    {
        desc.set( this.initScrollbarDescriptionHorizontal() );
    }
    
    /**
     * Sets the default Scrollbar.Description for a VERTICAL Scrollbar.
     */
    public void setScrollbarDescriptionVertical( Scrollbar.Description desc )
    {
        this.scrollbarDescVert = desc;
    }
    
    private final Scrollbar.Description initScrollbarDescriptionVertical()
    {
        if ( this.scrollbarDescVert == null )
        {
            Scrollbar.Description sbDesc =
                new Scrollbar.Description(
                    Scrollbar.Direction.VERTICAL,
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/vertical/background.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/vertical/handle_top.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/vertical/handle_bottom.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/vertical/handle_body.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/vertical/handle_decoration.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/vertical/decrementor.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/scrollbar/vertical/incrementor.png", false ),
                    themeProps.scrollbar_smoothscrolling
                );
            
            this.scrollbarDescVert = sbDesc;
        }
        
        return ( this.scrollbarDescVert );
    }
    
    /**
     * @return the default Scrollbar.Description for a VERTICAL Scrollbar
     */
    public Scrollbar.Description getScrollbarDescriptionVertical()
    {
        return ( this.initScrollbarDescriptionVertical().clone() );
    }
    
    /**
     * @param desc the default Scrollbar.Description for a VERTICAL Scrollbar
     */
    public void getScrollbarDescriptionVertical( Scrollbar.Description desc )
    {
        desc.set( this.initScrollbarDescriptionVertical() );
    }
    
    /**
     * @return the texture to use for the space in the lower-right corner of a ScrollPane
     */
    public Texture2D getScrollPanelSpaceTexture()
    {
        return ( HUDTextureUtils.getTexture( getName() + "/scrollpane/spacer.png", false ) );
    }
    
    /**
     * Sets the default Slider.Description.
     */
    public void setSliderDescription( Slider.Description desc )
    {
        this.sliderDesc = desc;
    }
    
    private final Slider.Description initSliderDescription()
    {
        if ( this.sliderDesc == null )
        {
            Slider.Description desc =
                new Slider.Description(
                    themeProps.slider_size_height,
                    HUDTextureUtils.getTexture( getName() + "/slider/left.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/slider/right.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/slider/body.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/slider/value_mark.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/slider/handle.png", false ),
                    themeProps.slider_handle_yoffset,
                    themeProps.slider_smoothsliding
                );
            
            this.sliderDesc = desc;
        }
        
        return ( this.sliderDesc );
    }
    
    /**
     * @return the default Slider.Description
     */
    public Slider.Description getSliderDescription()
    {
        return ( this.initSliderDescription().clone() );
    }
    
    /**
     * @param desc the default Slider.Description
     */
    public void getSliderDescription( Slider.Description desc )
    {
        desc.set( this.initSliderDescription() );
    }
    
    /**
     * Sets the default Button.Description.
     */
    public void setButtonDescription( Button.Description desc )
    {
        this.buttonDesc = desc;
    }
    
    private final Button.Description initButtonDescription()
    {
        if ( this.buttonDesc == null )
        {
            Button.Description buttonDesc = new Button.Description( themeProps.button_size_bottom, themeProps.button_size_right, themeProps.button_size_top, themeProps.button_size_left, getName() + "/button/normal.png", getName() + "/button/hovered.png", getName() + "/button/pressed.png", themeProps.button_font, themeProps.button_font_color );
            
            this.buttonDesc = buttonDesc;
        }
        
        return ( this.buttonDesc );
    }
    
    /**
     * @return the default Button.Description
     */
    public Button.Description getButtonDescription()
    {
        return ( this.initButtonDescription().clone() );
    }
    
    /**
     * @param desc the default Button.Description
     */
    public void getButtonDescription( Button.Description desc )
    {
        desc.set( this.initButtonDescription() );
    }
    
    /**
     * Sets the default RadioButton.Description.
     */
    public void setRadioButtonDescription( LabeledStateButton.Description desc )
    {
        this.radioButtonDesc = desc;
    }
    
    private final LabeledStateButton.Description initRadioButtonDescription()
    {
        if ( this.radioButtonDesc == null )
        {
            LabeledStateButton.Description radioButtonDesc =
                new LabeledStateButton.Description(
                    HUDTextureUtils.getTexture( getName() + "/radiobutton/deactivated-normal.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/radiobutton/deactivated-hovered.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/radiobutton/activated-normal.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/radiobutton/activated-hovered.png", false ),
                    themeProps.radiobutton_space_size,
                    getLabelDescription()
                );
            
            radioButtonDesc.getLabelDescription().setAlignment( TextAlignment.CENTER_LEFT );
            
            this.radioButtonDesc = radioButtonDesc;
        }
        
        return ( this.radioButtonDesc );
    }
    
    /**
     * @return the default RadioButton.Description
     */
    public LabeledStateButton.Description getRadioButtonDescription()
    {
        return ( this.initRadioButtonDescription().clone() );
    }
    
    /**
     * @param desc the default RadioButton.Description
     */
    public void getRadioButtonDescription( LabeledStateButton.Description desc )
    {
        desc.set( this.initRadioButtonDescription() );
    }
    
    /**
     * Sets the default CheckBox.Description.
     */
    public void setCheckBoxDescription( LabeledStateButton.Description desc )
    {
        this.checkboxDesc = desc;
    }
    
    private final LabeledStateButton.Description initCheckBoxDescription()
    {
        if ( this.checkboxDesc == null )
        {
            LabeledStateButton.Description checkboxDesc =
                new LabeledStateButton.Description(
                    HUDTextureUtils.getTexture( getName() + "/checkbox/unchecked-normal.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/checkbox/unchecked-hovered.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/checkbox/checked-normal.png", false ),
                    HUDTextureUtils.getTexture( getName() + "/checkbox/checked-hovered.png", false ),
                    themeProps.checkbox_space_size,
                    getLabelDescription()
                );
            
            checkboxDesc.getLabelDescription().setAlignment( TextAlignment.CENTER_LEFT );
            
            this.checkboxDesc = checkboxDesc;
        }
        
        return ( this.checkboxDesc );
    }
    
    /**
     * @return the default CheckBox.Description
     */
    public LabeledStateButton.Description getCheckBoxDescription()
    {
        return ( this.initCheckBoxDescription().clone() );
    }
    
    /**
     * @param desc the default CheckBox.Description
     */
    public void getCheckBoxDescription( LabeledStateButton.Description desc )
    {
        desc.set( this.initCheckBoxDescription() );
    }
    
    /**
     * Sets the RoundedCorners Border.Description.
     */
    public void setRoundedCornersBorderDescription( Border.Description desc )
    {
        this.roundedCornersBorderDesc = desc;
    }
    
    private final Border.Description initRoundedCornersBorderDescription()
    {
        if ( this.roundedCornersBorderDesc == null )
        {
            Border.Description borderDesc = new Border.Description( themeProps.border_rounded_corners_size_bottom, themeProps.border_rounded_corners_size_right, themeProps.border_rounded_corners_size_top, themeProps.border_rounded_corners_size_left );
            
            borderDesc.setLLupperHeight( themeProps.border_rounded_corners_size_ll_upper );
            borderDesc.setLLrightWidth( themeProps.border_rounded_corners_size_ll_right );
            borderDesc.setLRleftWidth( themeProps.border_rounded_corners_size_lr_left );
            borderDesc.setLRupperHeight( themeProps.border_rounded_corners_size_lr_upper );
            borderDesc.setURlowerHeight( themeProps.border_rounded_corners_size_ur_lower );
            borderDesc.setURleftWidth( themeProps.border_rounded_corners_size_ur_left );
            borderDesc.setULrightWidth( themeProps.border_rounded_corners_size_ul_right );
            borderDesc.setULlowerHeight( themeProps.border_rounded_corners_size_ul_lower );
            
            borderDesc.setTexture( HUDTextureUtils.getTexture( getName() + "/borders/rounded_corners.png", false ) );
            
            borderDesc.setName( "rounded_corners" );
            
            this.roundedCornersBorderDesc = borderDesc;
        }
        
        return ( this.roundedCornersBorderDesc );
    }
    
    /**
     * @return the RoundedCorners Border.Description
     */
    public Border.Description getRoundedCornersBorderDescription()
    {
        return ( this.initRoundedCornersBorderDescription().clone() );
    }
    
    /**
     * @param desc the RoundedCorners Border.Description
     */
    public void getRoundedCornersBorderDescription( Border.Description desc )
    {
        desc.set( this.initRoundedCornersBorderDescription() );
    }
    
    /**
     * @return the standard Border.Description
     * By default this is "rounded courners".
     */
    public Border.Description getStandardBorderDescription()
    {
        return ( getRoundedCornersBorderDescription() );
    }
    
    /**
     * @param desc the standard Border.Description
     * By default this is "rounded courners".
     */
    public void getStandardBorderDescription( Border.Description desc )
    {
        getRoundedCornersBorderDescription( desc );
    }
    
    /**
     * Sets the default Border.Description.
     */
    public void setLoweredBevelBorderDescription( Border.Description desc )
    {
        this.loweredBevelBorderDesc = desc;
    }
    
    private final Border.Description initLoweredBevelBorderDescription()
    {
        if ( this.loweredBevelBorderDesc == null )
        {
            Border.Description borderDesc = new Border.Description( themeProps.border_bevel_lowered_size_bottom, themeProps.border_bevel_lowered_size_right, themeProps.border_bevel_lowered_size_top, themeProps.border_bevel_lowered_size_left );
            
            borderDesc.setLLupperHeight( themeProps.border_bevel_lowered_size_ll_upper );
            borderDesc.setLLrightWidth( themeProps.border_bevel_lowered_size_ll_right );
            borderDesc.setLRleftWidth( themeProps.border_bevel_lowered_size_lr_left );
            borderDesc.setLRupperHeight( themeProps.border_bevel_lowered_size_lr_upper );
            borderDesc.setURlowerHeight( themeProps.border_bevel_lowered_size_ur_lower );
            borderDesc.setURleftWidth( themeProps.border_bevel_lowered_size_ur_left );
            borderDesc.setULrightWidth( themeProps.border_bevel_lowered_size_ul_right );
            borderDesc.setULlowerHeight( themeProps.border_bevel_lowered_size_ul_lower );
            
            borderDesc.setTexture( HUDTextureUtils.getTexture( getName() + "/borders/bevel-lowered.png", false ) );
            
            borderDesc.setName( "bevel/lowered" );
            
            this.loweredBevelBorderDesc = borderDesc;
        }
        
        return ( this.loweredBevelBorderDesc );
    }
    
    /**
     * @return the default Border.Description
     */
    public Border.Description getLoweredBevelBorderDescription()
    {
        return ( this.initLoweredBevelBorderDescription().clone() );
    }
    
    /**
     * @param desc the default Border.Description
     */
    public void getLoweredBevelBorderDescription( Border.Description desc )
    {
        desc.set( this.initLoweredBevelBorderDescription() );
    }
    
    /**
     * Sets the default Border.Description.
     */
    public void setRaisedBevelBorderDescription( Border.Description desc )
    {
        this.raisedBevelBorderDesc = desc;
    }
    
    private final Border.Description initRaisedBevelBorderDescription()
    {
        if ( this.raisedBevelBorderDesc == null )
        {
            Border.Description borderDesc = new Border.Description( themeProps.border_bevel_raised_size_bottom, themeProps.border_bevel_raised_size_right, themeProps.border_bevel_raised_size_top, themeProps.border_bevel_raised_size_left );
            
            borderDesc.setLLupperHeight( themeProps.border_bevel_raised_size_ll_upper );
            borderDesc.setLLrightWidth( themeProps.border_bevel_raised_size_ll_right );
            borderDesc.setLRleftWidth( themeProps.border_bevel_raised_size_lr_left );
            borderDesc.setLRupperHeight( themeProps.border_bevel_raised_size_lr_upper );
            borderDesc.setURlowerHeight( themeProps.border_bevel_raised_size_ur_lower );
            borderDesc.setURleftWidth( themeProps.border_bevel_raised_size_ur_left );
            borderDesc.setULrightWidth( themeProps.border_bevel_raised_size_ul_right );
            borderDesc.setULlowerHeight( themeProps.border_bevel_raised_size_ul_lower );
            
            borderDesc.setTexture( HUDTextureUtils.getTexture( getName() + "/borders/bevel-raised.png", false ) );
            
            borderDesc.setName( "bevel/raised" );
            
            this.raisedBevelBorderDesc = borderDesc;
        }
        
        return ( this.raisedBevelBorderDesc );
    }
    
    /**
     * @return the default Border.Description
     */
    public Border.Description getRaisedBevelBorderDescription()
    {
        return ( this.initRaisedBevelBorderDescription().clone() );
    }
    
    /**
     * @param desc the default Border.Description
     */
    public void getRaisedBevelBorderDescription( Border.Description desc )
    {
        desc.set( this.initRaisedBevelBorderDescription() );
    }
    
    private Border.Description getBorderDescriptionByName( String name )
    {
        if ( name == null )
            return ( null );
        else if ( name.equals( "rounded_corners" ) )
            return ( getRoundedCornersBorderDescription() );
        else if ( name.equals( "bevel/lowered" ) )
            return ( getLoweredBevelBorderDescription() );
        else if ( name.equals( "bevel/raised" ) )
            return ( getRaisedBevelBorderDescription() );
        else if ( name.equals( "frame" ) )
            return ( getFrameBorderDescription() );
        
        throw new IllegalArgumentException( "Unknown named Border.Description \"" + name + "\"" );
    }
    
    /**
     * Sets the default WindowHeaderWidget.Description.
     */
    public void setWindowHeaderDescription( WindowHeaderWidget.Description desc )
    {
        this.windowHeaderDesc = desc;
    }
    
    private final WindowHeaderWidget.Description initWindowHeaderDescription()
    {
        if ( this.windowHeaderDesc == null )
        {
            WindowHeaderWidget.Description headerDesc =
                new WindowHeaderWidget.Description(
                    HUDTextureUtils.getTexture( getName() + "/frame/title-background.png", false ),
                    themeProps.frame_title_font,
                    themeProps.frame_title_font_color,
                    TextAlignment.CENTER_CENTER,
                    new Button.Description( HUDTextureUtils.getTexture( getName() + "/frame/close-normal.png", false ),
                                            HUDTextureUtils.getTexture( getName() + "/frame/close-hovered.png", false ),
                                            HUDTextureUtils.getTexture( getName() + "/frame/close-pressed.png", false ) )
                );
            
            this.windowHeaderDesc = headerDesc;
        }
        
        return ( this.windowHeaderDesc );
    }
    
    /**
     * @return the default WindowHeaderWidget.Description
     */
    public WindowHeaderWidget.Description getWindowHeaderDescription()
    {
        return ( this.initWindowHeaderDescription().clone() );
    }
    
    /**
     * @param desc the default WindowHeaderWidget.Description
     */
    public void getWindowHeaderDescription( WindowHeaderWidget.Description desc )
    {
        desc.set( this.initWindowHeaderDescription() );
    }
    
    /**
     * Sets the default Border.Description for a Frame.
     */
    public void setFrameBorderDescription( Border.Description desc )
    {
        this.windowBorderDesc = desc;
    }
    
    private final Border.Description initFrameBorderDescription()
    {
        if ( this.windowBorderDesc == null )
        {
            Border.Description borderDesc = new Border.Description( themeProps.border_frame_size_bottom, themeProps.border_frame_size_right, themeProps.border_frame_size_top, themeProps.border_frame_size_left );
            
            borderDesc.setLLupperHeight( themeProps.border_frame_size_ll_upper );
            borderDesc.setLLrightWidth( themeProps.border_frame_size_ll_right );
            borderDesc.setLRleftWidth( themeProps.border_frame_size_lr_left );
            borderDesc.setLRupperHeight( themeProps.border_frame_size_lr_upper );
            borderDesc.setURlowerHeight( themeProps.border_frame_size_ur_lower );
            borderDesc.setURleftWidth( themeProps.border_frame_size_ur_left );
            borderDesc.setULrightWidth( themeProps.border_frame_size_ul_right );
            borderDesc.setULlowerHeight( themeProps.border_frame_size_ul_lower );
            
            borderDesc.setTexture( HUDTextureUtils.getTexture( getName() + "/borders/frame.png", false ) );
            
            borderDesc.setName( "frame" );
            
            this.windowBorderDesc = borderDesc;
        }
        
        return ( this.windowBorderDesc );
    }
    
    /**
     * @return the default Border.Description for a Frame
     */
    public Border.Description getFrameBorderDescription()
    {
        return ( this.initFrameBorderDescription().clone() );
    }
    
    /**
     * @param desc the default Border.Description for a Frame
     */
    public void getFrameBorderDescription( Border.Description desc )
    {
        desc.set( this.initFrameBorderDescription() );
    }
    
    public Colorf getContentPaneBackgroundColor()
    {
        return ( themeProps.frame_contentpane_background_color );
    }
    
    public Texture2D getContentPaneBackgroundTexture()
    {
        if ( this.frame_contentpane_background_texture == null )
        {
            if ( themeProps.frame_contentpane_background_texture != null )
                this.frame_contentpane_background_texture = HUDTextureUtils.getTexture( themeProps.frame_contentpane_background_texture, false );
        }
        
        if ( TextureLoader.isFallbackTexture( frame_contentpane_background_texture ) )
            return ( null );
        
        return ( frame_contentpane_background_texture );
    }
    
    /**
     * Sets the default Font of this theme.
     */
    public void setProgressBarLabelFont( HUDFont font )
    {
        themeProps.progressbar_label_font = font;
    }
    
    /**
     * @return the default Font of this theme
     */
    public HUDFont getProgressBarLabelFont()
    {
        return ( themeProps.progressbar_label_font  );
    }
    
    /**
     * Sets the default font-color of this theme.
     */
    public void setProgressBarLabelFontColor( Colorf color )
    {
        themeProps.progressbar_label_font_color = color;
    }
    
    /**
     * @return the default font-color of this theme
     */
    public Colorf getProgressBarLabelFontColor()
    {
        return ( themeProps.progressbar_label_font_color  );
    }
    
    /**
     * @return the default border description for a progressbar of this theme.
     */
    public Border.Description getProgressBarBorderDesc()
    {
        return ( getBorderDescriptionByName( themeProps.progressbar_border_name  ) );
    }
    
    public Label.Description getProgressbarLabelDescription()
    {
        Label.Description desc = getLabelDescription();
        desc.setFont( getProgressBarLabelFont(), false );
        desc.setFontColor( getProgressBarLabelFontColor(), false );
        desc.setAlignment( TextAlignment.CENTER_CENTER );
        
        return ( desc );
    }
    
    /**
     * Sets the default List.Description.
     */
    public void setListDescription( List.Description desc )
    {
        this.listDesc = desc;
    }
    
    private final List.Description initListDescription()
    {
        if ( this.listDesc == null )
        {
            List.Description listDesc = new List.Description(
                themeProps.list_padding_bottom, themeProps.list_padding_right, themeProps.list_padding_top, themeProps.list_padding_left,
                getBorderDescriptionByName( themeProps.list_border_name ),
                ( themeProps.use_texture_for_list_background ? null : themeProps.list_background_color ),
                ( themeProps.use_texture_for_list_background ? HUDTextureUtils.getTexture( getName() + "/list/background.png", false ) : null ),
                null, null,
                themeProps.list_selection_background, themeProps.list_selection_foreground
            );
            
            this.listDesc = listDesc;
        }
        
        return ( this.listDesc );
    }
    
    /**
     * @return the default List.Description
     */
    public List.Description getListDescription()
    {
        return ( this.initListDescription().clone() );
    }
    
    /**
     * @param desc the default List.Description
     */
    public void getListDescription( List.Description desc )
    {
        desc.set( this.initListDescription() );
    }
    
    /**
     * Sets the default ComboBox.Description.
     */
    public void setComboBoxDescription( ComboBox.Description desc )
    {
        this.comboDesc = desc;
    }
    
    private final ComboBox.Description initComboBoxDescription()
    {
        if ( this.comboDesc == null )
        {
            List.Description listDesc = getListDescription();
            listDesc.setBorderDescription( getBorderDescriptionByName( themeProps.combobox_list_border_name ) );
            listDesc.setHoverBackgroundColor( themeProps.combobox_list_hover_background );
            listDesc.setHoverFontColor( themeProps.combobox_list_hover_foreground );
            
            ComboBox.Description comboDesc = new ComboBox.Description( getTextFieldDescription(), listDesc, HUDTextureUtils.getTexture( getName() + "/combobox/button_symbol.png", false ) );
            
            this.comboDesc = comboDesc;
        }
        
        return ( this.comboDesc );
    }
    
    /**
     * @return the default ComboBox.Description
     */
    public ComboBox.Description getComboBoxDescription()
    {
        return ( this.initComboBoxDescription().clone() );
    }
    
    /**
     * @param desc the default ComboBox.Description
     */
    public void getComboBoxDescription( ComboBox.Description desc )
    {
        desc.set( this.initComboBoxDescription() );
    }
    
    /**
     * Sets the default ProgressBar.Description.
     */
    public void setProgressBarDescription( ProgressBar.Description desc )
    {
        this.progressbarDesc = desc;
    }
    
    private final ProgressBar.Description initProgressBarDescription()
    {
        if ( this.progressbarDesc == null )
        {
            ProgressBar.Description pbDesc =
                new ProgressBar.Description(
                    themeProps.progressbar_bar_bottom_height, themeProps.progressbar_bar_right_width, themeProps.progressbar_bar_top_height, themeProps.progressbar_bar_left_width,
                    null, null,
                    HUDTextureUtils.getTexture( getName() + "/progressbar/body.png", false ),
                    getBorderDescriptionByName( themeProps.progressbar_border_name ),
                    getProgressbarLabelDescription()
                );
            
            this.progressbarDesc = pbDesc;
        }
        
        return ( this.progressbarDesc );
    }
    
    /**
     * @return the default ProgressBar.Description
     */
    public ProgressBar.Description getProgressBarDescription()
    {
        return ( this.initProgressBarDescription().clone() );
    }
    
    /**
     * @param desc the default ProgressBar.Description
     */
    public void getProgressBarDescription( ProgressBar.Description desc )
    {
        desc.set( this.initProgressBarDescription() );
    }
    
    
    private final Cursor newCursor( String textureName, int zeroX, int zeroY )
    {
        if ( ( textureName == null ) || ( textureName.length() == 0 ) )
        {
            return ( null );
        }
        
        Texture2D texture = HUDTextureUtils.getTexture( getName() + "/cursors/" + textureName, false );
        
        return ( new Cursor( texture, zeroX, zeroY ) );
    }
    
    private final CursorSet initCursorSet()
    {
        if ( this.cursorSet == null )
        {
            CursorSet cursorSet = new CursorSet();
            
            cursorSet.setPointer1( newCursor( themeProps.cursor_pointer1_texture, themeProps.cursor_pointer1_zero_x, themeProps.cursor_pointer1_zero_y ) );
            cursorSet.setPointer2( newCursor( themeProps.cursor_pointer2_texture, themeProps.cursor_pointer2_zero_x, themeProps.cursor_pointer2_zero_y ) );
            cursorSet.setCrosshair( newCursor( themeProps.cursor_crosshair_texture, themeProps.cursor_crosshair_zero_x, themeProps.cursor_crosshair_zero_y ) );
            cursorSet.setTextCursor( newCursor( themeProps.cursor_text_texture, themeProps.cursor_text_zero_x, themeProps.cursor_text_zero_y ) );
            cursorSet.setHelpCursor( newCursor( themeProps.cursor_help_texture, themeProps.cursor_help_zero_x, themeProps.cursor_help_zero_y ) );
            cursorSet.setWaitCursor( newCursor( themeProps.cursor_wait_texture, themeProps.cursor_wait_zero_x, themeProps.cursor_wait_zero_y ) );
            
            this.cursorSet = cursorSet;
        }
        
        return ( this.cursorSet );
    }
    
    /**
     * @return the default {@link CursorSet}.
     */
    public CursorSet getCursorSet()
    {
        return ( this.initCursorSet().clone() );
    }
    
    /**
     * @param cs the default {@link CursorSet}
     */
    public void getCursorSet( CursorSet cs )
    {
        cs.set( this.initCursorSet() );
    }
    
    
    protected WidgetTheme( ThemeProperties themeProps )
    {
        this.themeProps = themeProps;
    }
    
    protected static ThemeProperties loadThemeProps( InputStream in ) throws IOException
    {
        ZipInputStream zipIn = new ZipInputStream( in );
        ZipEntry en;
        while ( ( en = zipIn.getNextEntry() ) != null )
        {
            if ( en.getName().equals( "theme.properties" ) )
            {
                return ( new ThemeProperties( zipIn ) );
            }
        }
        
        throw new IOException( "No \"theme.properties\" entry found in the theme archive." );
    }
    
    /**
     * Creates the desired WidgetTheme.
     * 
     * @param url a URL pointing to the theme-zip-archive
     */
    public WidgetTheme( URL url ) throws IOException
    {
        this( loadThemeProps( url.openStream() ) );
        
        TextureLoader.getInstance().addTextureStreamLocator( new TextureStreamLocatorZip( url, "textures/" ) );
    }
    
    private static URL getThemeResource( String name ) throws IOException
    {
        URL resource = WidgetTheme.class.getClassLoader().getResource( "resources/org/xith3d/hud/themes/" + name + ".xwt" );
        
        if ( resource == null )
        {
            throw new IOException( "The Theme resource with the name \"" + name + "\" was not found in the classpath." );
        }
        
        return ( resource );
    }
    
    /**
     * Creates the desired <b>built-in</b> WidgetTheme.<br>
     * <br>
     * The theme is loaded from classpath as a resource from path
     * "resources/org/xith3d/hud/themes/[THEME_NAME].xwt".
     * 
     * @param name the name of the WidgetTheme. <b>Default-Theme</b>: <i>"GTK"</i>
     */
    public WidgetTheme( String name ) throws IOException
    {
        this( getThemeResource( name ) );
    }
    
    private static URL file2url( File file ) throws IOException
    {
        try
        {
            return ( file.toURI().toURL() );
        }
        catch ( MalformedURLException e )
        {
            IOException ioe = new IOException( e.getMessage() );
            ioe.initCause( e );
            
            throw ioe;
        }
    }
    
    /**
     * Creates the desired WidgetTheme.
     * 
     * @param zipFile A File representation of the zip-archive of the theme
     */
    public WidgetTheme( File zipFile ) throws IOException
    {
        this( file2url( zipFile ) );
    }
}
