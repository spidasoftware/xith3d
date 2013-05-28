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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.openmali.vecmath2.Colorf;
import org.xith3d.ui.hud.utils.HUDFont;

/**
 * Reads the Theme-properties from the properties file
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ThemeProperties
{
    private static final String DEFAULT_FONT_NAME = "Monospace";
    private static final String DEFAULT_FONT_STYLE = "";
    private static final String DEFAULT_FONT_SIZE = "12";
    private static final String DEFAULT_FONT_COLOR = "#000000";
    
    private static final String DEFAULT_FONT_DISABLED_NAME = DEFAULT_FONT_NAME;
    private static final String DEFAULT_FONT_DISABLED_STYLE = "ITALIC";
    private static final String DEFAULT_FONT_DISABLED_SIZE = DEFAULT_FONT_SIZE;
    private static final String DEFAULT_FONT_DISABLED_COLOR = "#7F7F7F";
    
    protected String name = "UNNAMED";
    
    protected HUDFont font = HUDFont.getFont( DEFAULT_FONT_NAME, HUDFont.FontStyle.PLAIN, 12 );
    protected Colorf font_color = new Colorf( 0f, 0f, 0f );
    
    protected HUDFont font_disabled = HUDFont.getFont( DEFAULT_FONT_NAME, HUDFont.FontStyle.ITALIC, 12 );
    protected Colorf font_disabled_color = new Colorf( 0.5f, 0.5f, 0.5f );
    
    protected HUDFont label_font = font;
    protected Colorf label_font_color = font_color;
    
    protected HUDFont label_font_disabled = font_disabled;
    protected Colorf label_font_disabled_color = font_disabled_color;
    
    protected HUDFont button_font = font;
    protected Colorf button_font_color = font_color;
    
    protected HUDFont button_font_disabled = font_disabled;
    protected Colorf button_font_disabled_color = font_disabled_color;
    
    protected boolean scrollbar_smoothscrolling = true;
    
    protected int slider_size_height = 23;
    protected int slider_handle_yoffset = 0;
    protected boolean slider_smoothsliding = true;
    
    protected int border_rounded_corners_size_bottom = 4;
    protected int border_rounded_corners_size_right = 4;
    protected int border_rounded_corners_size_top = 4;
    protected int border_rounded_corners_size_left = 4;
    protected int border_rounded_corners_size_ll_upper = 4;
    protected int border_rounded_corners_size_ll_right = 4;
    protected int border_rounded_corners_size_lr_left = 4;
    protected int border_rounded_corners_size_lr_upper = 4;
    protected int border_rounded_corners_size_ur_lower = 4;
    protected int border_rounded_corners_size_ur_left = 4;
    protected int border_rounded_corners_size_ul_right = 4;
    protected int border_rounded_corners_size_ul_lower = 4;
    
    protected int border_frame_size_bottom = 3;
    protected int border_frame_size_right = 3;
    protected int border_frame_size_top = 0;
    protected int border_frame_size_left = 3;
    protected int border_frame_size_ll_upper = 0;
    protected int border_frame_size_ll_right = 0;
    protected int border_frame_size_lr_left = 0;
    protected int border_frame_size_lr_upper = 0;
    protected int border_frame_size_ur_lower = 0;
    protected int border_frame_size_ur_left = 0;
    protected int border_frame_size_ul_right = 0;
    protected int border_frame_size_ul_lower = 0;
    
    protected int border_bevel_lowered_size_bottom = 2;
    protected int border_bevel_lowered_size_right = 2;
    protected int border_bevel_lowered_size_top = 2;
    protected int border_bevel_lowered_size_left = 2;
    protected int border_bevel_lowered_size_ll_upper = 0;
    protected int border_bevel_lowered_size_ll_right = 0;
    protected int border_bevel_lowered_size_lr_left = 0;
    protected int border_bevel_lowered_size_lr_upper = 0;
    protected int border_bevel_lowered_size_ur_lower = 0;
    protected int border_bevel_lowered_size_ur_left = 0;
    protected int border_bevel_lowered_size_ul_right = 0;
    protected int border_bevel_lowered_size_ul_lower = 0;
    
    protected int border_bevel_raised_size_bottom = 2;
    protected int border_bevel_raised_size_right = 2;
    protected int border_bevel_raised_size_top = 2;
    protected int border_bevel_raised_size_left = 2;
    protected int border_bevel_raised_size_ll_upper = 0;
    protected int border_bevel_raised_size_ll_right = 0;
    protected int border_bevel_raised_size_lr_left = 0;
    protected int border_bevel_raised_size_lr_upper = 0;
    protected int border_bevel_raised_size_ur_lower = 0;
    protected int border_bevel_raised_size_ur_left = 0;
    protected int border_bevel_raised_size_ul_right = 0;
    protected int border_bevel_raised_size_ul_lower = 0;
    
    protected int button_size_bottom = 10;
    protected int button_size_right = 10;
    protected int button_size_top = 10;
    protected int button_size_left = 10;
    
    protected int radiobutton_space_size = 5;
    
    protected int checkbox_space_size = 5;
    
    protected String progressbar_border_name = "bevel/lowered";
    protected HUDFont progressbar_label_font = HUDFont.getFont( DEFAULT_FONT_NAME, HUDFont.FontStyle.BOLD, 12 );
    protected Colorf progressbar_label_font_color = Colorf.BLACK;
    
    protected int progressbar_bar_bottom_height = 0;
    protected int progressbar_bar_right_width = 0;
    protected int progressbar_bar_top_height = 0;
    protected int progressbar_bar_left_width = 0;
    
    protected String list_border_name = "bevel/lowered";
    protected Colorf list_background_color = null;
    protected Colorf list_background_color_disabled = null;
    protected boolean use_texture_for_list_background = false;
    protected int list_padding_bottom = 0;
    protected int list_padding_right = 0;
    protected int list_padding_top = 0;
    protected int list_padding_left = 3;
    protected Colorf list_selection_background = new Colorf( 0.29f, 0.58f, 0.84f, 0.0f );
    protected Colorf list_selection_foreground = new Colorf( 1.0f, 1.0f, 1.0f );
    
    protected String combobox_list_border_name = null;
    protected Colorf combobox_list_hover_background = new Colorf( 0.29f, 0.58f, 0.84f, 0.0f );
    protected Colorf combobox_list_hover_foreground = new Colorf( 1.0f, 1.0f, 1.0f );
    
    protected Colorf textfield_background_color = new Colorf( 1.0f, 1.0f, 1.0f, 0.0f );
    protected int textfield_border_size_bottom = 2;
    protected int textfield_border_size_right = 2;
    protected int textfield_border_size_top = 2;
    protected int textfield_border_size_left = 2;
    
    protected String textfield_caret_texture = "black";
    
    protected int frame_title_height = 22;
    protected int frame_title_closebutton_width = 20;
    protected int frame_title_closebutton_height = 20;
    protected HUDFont frame_title_font = HUDFont.getFont( DEFAULT_FONT_NAME, HUDFont.FontStyle.BOLD, 12 );
    protected Colorf frame_title_font_color = new Colorf( 1f, 1f, 1f );
    protected Colorf frame_contentpane_background_color = null;
    protected String frame_contentpane_background_texture = null;
    
    protected String cursor_pointer1_texture = null;
    protected int cursor_pointer1_zero_x = 0;
    protected int cursor_pointer1_zero_y = 0;
    protected String cursor_pointer2_texture = null;
    protected int cursor_pointer2_zero_x = 0;
    protected int cursor_pointer2_zero_y = 0;
    protected String cursor_crosshair_texture = null;
    protected int cursor_crosshair_zero_x = 0;
    protected int cursor_crosshair_zero_y = 0;
    protected String cursor_text_texture = null;
    protected int cursor_text_zero_x = 0;
    protected int cursor_text_zero_y = 0;
    protected String cursor_help_texture = null;
    protected int cursor_help_zero_x = 0;
    protected int cursor_help_zero_y = 0;
    protected String cursor_wait_texture = null;
    protected int cursor_wait_zero_x = 0;
    protected int cursor_wait_zero_y = 0;
    
    /**
     * Parses the String and extrancts an int (font-style-contant)
     * 
     * @param fontStyleString the font-style-contant as String
     */
    private static HUDFont.FontStyle string2FontStyle( String fontStyleString )
    {
        fontStyleString = fontStyleString.toUpperCase();
        
        HUDFont.FontStyle result = HUDFont.FontStyle.PLAIN;
        
        if ( fontStyleString.indexOf( "BOLD" ) >= 0 )
        {
            if ( fontStyleString.indexOf( "ITALIC" ) >= 0 )
                result = HUDFont.FontStyle.BOLD_ITALIC;
            else
                result = HUDFont.FontStyle.BOLD;
        }
        else if ( fontStyleString.indexOf( "ITALIC" ) >= 0 )
        {
            result = HUDFont.FontStyle.ITALIC;
        }
        
        return ( result );
    }
    
    /**
     * Reads from a properties file of version 0.1.1
     * 
     * @param themeProps the properties
     */
    private void read_0_1_1( Properties themeProps )
    {
        this.name = themeProps.getProperty( "theme.name", "UNNAMED" );
        
        String fontName = themeProps.getProperty( "font.name", DEFAULT_FONT_NAME );
        String fontStyle = themeProps.getProperty( "font.style", DEFAULT_FONT_STYLE );
        String fontSize = themeProps.getProperty( "font.size", DEFAULT_FONT_SIZE );
        String font_color = themeProps.getProperty( "font.color", DEFAULT_FONT_COLOR );
        
        this.font = HUDFont.getFont( fontName, string2FontStyle( fontStyle ), Integer.parseInt( fontSize ) );
        this.font_color = Colorf.parseColor( font_color );
        
        this.button_size_bottom = Integer.parseInt( themeProps.getProperty( "button.size.bottom", "10" ) );
        this.button_size_right = Integer.parseInt( themeProps.getProperty( "button.size.right", "10" ) );
        this.button_size_top = Integer.parseInt( themeProps.getProperty( "button.size.top", "10" ) );
        this.button_size_left = Integer.parseInt( themeProps.getProperty( "button.size.left", "10" ) );
    }
    
    /**
     * Reads from a properties file of version 0.1.2
     * 
     * @param themeProps the properties
     */
    private void read_0_1_2( Properties themeProps )
    {
        read_0_1_1( themeProps );
        
        String scrollbar_smoothscrolling = themeProps.getProperty( "scrollbar.smoothscrolling", "true" );
        
        this.scrollbar_smoothscrolling = Boolean.parseBoolean( scrollbar_smoothscrolling );
        
        String radiobutton_space_size = themeProps.getProperty( "radiobutton.space.size", "5" );
        
        this.radiobutton_space_size = Integer.parseInt( radiobutton_space_size );
        
        String checkbox_space_size = themeProps.getProperty( "checkbox.space.size", "5" );
        
        this.checkbox_space_size = Integer.parseInt( checkbox_space_size );
        
        String frame_title_height = themeProps.getProperty( "frame.title.height", "22" );
        String frame_title_closebutton_width = themeProps.getProperty( "frame.title.closebutton.width", "20" );
        String frame_title_closebutton_height = themeProps.getProperty( "frame.title.closebutton.height", "20" );
        
        this.frame_title_height = Integer.parseInt( frame_title_height );
        this.frame_title_closebutton_width = Integer.parseInt( frame_title_closebutton_width );
        this.frame_title_closebutton_height = Integer.parseInt( frame_title_closebutton_height );
        
        String frame_title_font_name = themeProps.getProperty( "frame.title.font.name", "Verdana" );
        String frame_title_font_style = themeProps.getProperty( "frame.title.font.style", "BOLD" );
        String frame_title_font_size = themeProps.getProperty( "frame.title.font.size", "12" );
        String frame_title_font_color = themeProps.getProperty( "frame.title.font.color", "#FFFFFF" );
        
        this.frame_title_font = HUDFont.getFont( frame_title_font_name, string2FontStyle( frame_title_font_style ), Integer.parseInt( frame_title_font_size ) );
        this.frame_title_font_color = Colorf.parseColor( frame_title_font_color );
        
        String frame_contentpane_background_color = themeProps.getProperty( "frame.contentpane.background.color", null );
        this.frame_contentpane_background_texture = themeProps.getProperty( "frame.contentpane.background.texture", null );
        
        if ( ( frame_contentpane_background_color == null ) && ( frame_contentpane_background_texture == null ) )
        {
            this.frame_contentpane_background_color = Colorf.parseColor( "#EFEFEF" );
        }
        else if ( frame_contentpane_background_color != null )
        {
            this.frame_contentpane_background_color = Colorf.parseColor( frame_contentpane_background_color );
        }
    }
    
    /**
     * Reads from a properties file of version 0.1.3
     * 
     * @param themeProps the properties
     */
    private void read_0_1_3( Properties themeProps )
    {
        read_0_1_2( themeProps );
        
        this.progressbar_border_name = themeProps.getProperty( "progressbar.border.name", "bevel/lowered" );
        
        String progressbar_label_font_name = themeProps.getProperty( "progressbar.label.font.name", "Monospace" );
        String progressbar_label_font_style = themeProps.getProperty( "progressbar.label.font.style", "" );
        String progressbar_label_font_size = themeProps.getProperty( "progressbar.label.font.size", "12" );
        String progressbar_label_font_color = themeProps.getProperty( "progressbar.label.font.color", "#000000" );
        
        this.progressbar_label_font = HUDFont.getFont( progressbar_label_font_name, string2FontStyle( progressbar_label_font_style ), Integer.parseInt( progressbar_label_font_size ) );
        this.font_color = Colorf.parseColor( progressbar_label_font_color );
        
        this.progressbar_bar_bottom_height = Integer.parseInt( themeProps.getProperty( "progressbar.bar.bottom.height", "0" ) );
        this.progressbar_bar_right_width = Integer.parseInt( themeProps.getProperty( "progressbar.bar.right.width", "0" ) );
        this.progressbar_bar_top_height = Integer.parseInt( themeProps.getProperty( "progressbar.bar.top.height", "0" ) );
        this.progressbar_bar_left_width = Integer.parseInt( themeProps.getProperty( "progressbar.bar.left.width", "0" ) );
    }
    
    /**
     * Reads from a properties file of version 0.1.4
     * 
     * @param themeProps the properties
     */
    private void read_0_1_4( Properties themeProps )
    {
        read_0_1_3( themeProps );
        
        this.border_rounded_corners_size_bottom = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.bottom", "4" ) );
        this.border_rounded_corners_size_right = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.right", "4" ) );
        this.border_rounded_corners_size_top = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.top", "4" ) );
        this.border_rounded_corners_size_left = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.left", "4" ) );
        this.border_rounded_corners_size_ll_upper = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.ll.upper", "4" ) );
        this.border_rounded_corners_size_ll_right = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.ll.right", "4" ) );
        this.border_rounded_corners_size_lr_left = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.lr.left", "4" ) );
        this.border_rounded_corners_size_lr_upper = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.lr.upper", "4" ) );
        this.border_rounded_corners_size_ur_lower = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.ur.lower", "4" ) );
        this.border_rounded_corners_size_ur_left = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.ur.left", "4" ) );
        this.border_rounded_corners_size_ul_right = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.ul.right", "4" ) );
        this.border_rounded_corners_size_ul_lower = Integer.parseInt( themeProps.getProperty( "border.rounded_corners.size.ul.lower", "4" ) );
        
        this.border_bevel_lowered_size_bottom = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.bottom", "2" ) );
        this.border_bevel_lowered_size_right = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.right", "2" ) );
        this.border_bevel_lowered_size_top = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.top", "2" ) );
        this.border_bevel_lowered_size_left = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.left", "2" ) );
        this.border_bevel_lowered_size_ll_upper = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.ll.upper", "0" ) );
        this.border_bevel_lowered_size_ll_right = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.ll.right", "0" ) );
        this.border_bevel_lowered_size_lr_left = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.lr.left", "0" ) );
        this.border_bevel_lowered_size_lr_upper = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.lr.upper", "0" ) );
        this.border_bevel_lowered_size_ur_lower = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.ur.lower", "0" ) );
        this.border_bevel_lowered_size_ur_left = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.ur.left", "0" ) );
        this.border_bevel_lowered_size_ul_right = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.ul.right", "0" ) );
        this.border_bevel_lowered_size_ul_lower = Integer.parseInt( themeProps.getProperty( "border.bevel.lowered.size.ul.lower", "0" ) );
        
        this.border_bevel_raised_size_bottom = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.bottom", "2" ) );
        this.border_bevel_raised_size_right = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.right", "2" ) );
        this.border_bevel_raised_size_top = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.top", "2" ) );
        this.border_bevel_raised_size_left = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.left", "2" ) );
        this.border_bevel_raised_size_ll_upper = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.ll.upper", "0" ) );
        this.border_bevel_raised_size_ll_right = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.ll.right", "00" ) );
        this.border_bevel_raised_size_lr_left = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.lr.left", "0" ) );
        this.border_bevel_raised_size_lr_upper = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.lr.upper", "0" ) );
        this.border_bevel_raised_size_ur_lower = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.ur.lower", "0" ) );
        this.border_bevel_raised_size_ur_left = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.ur.left", "0" ) );
        this.border_bevel_raised_size_ul_right = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.ul.right", "0" ) );
        this.border_bevel_raised_size_ul_lower = Integer.parseInt( themeProps.getProperty( "border.bevel.raised.size.ul.lower", "0" ) );
        
        this.border_frame_size_bottom = Integer.parseInt( themeProps.getProperty( "border.frame.size.bottom", "3" ) );
        this.border_frame_size_right = Integer.parseInt( themeProps.getProperty( "border.frame.size.right", "3" ) );
        this.border_frame_size_top = Integer.parseInt( themeProps.getProperty( "border.frame.size.top", "0" ) );
        this.border_frame_size_left = Integer.parseInt( themeProps.getProperty( "border.frame.size.left", "3" ) );
        this.border_frame_size_ll_upper = Integer.parseInt( themeProps.getProperty( "border.frame.size.ll.upper", "0" ) );
        this.border_frame_size_ll_right = Integer.parseInt( themeProps.getProperty( "border.frame.size.ll.right", "0" ) );
        this.border_frame_size_lr_left = Integer.parseInt( themeProps.getProperty( "border.frame.size.lr.left", "0" ) );
        this.border_frame_size_lr_upper = Integer.parseInt( themeProps.getProperty( "border.frame.size.lr.upper", "0" ) );
        this.border_frame_size_ur_lower = Integer.parseInt( themeProps.getProperty( "border.frame.size.ur.lower", "0" ) );
        this.border_frame_size_ur_left = Integer.parseInt( themeProps.getProperty( "border.frame.size.ur.left", "0" ) );
        this.border_frame_size_ul_right = Integer.parseInt( themeProps.getProperty( "border.frame.size.ul.right", "0" ) );
        this.border_frame_size_ul_lower = Integer.parseInt( themeProps.getProperty( "border.frame.size.ul.lower", "0" ) );
        
        this.list_border_name = themeProps.getProperty( "list.border.name", "bevel/lowered" );
        String list_background = themeProps.getProperty( "list.background", null );
        if ( list_background == null )
        {
            this.list_background_color = null;
            this.use_texture_for_list_background = false;
        }
        else if ( list_background.equalsIgnoreCase( "texture" ) )
        {
            this.list_background_color = null;
            this.use_texture_for_list_background = true;
        }
        else
        {
            this.list_background_color = Colorf.parseColor( list_background );
            this.use_texture_for_list_background = false;
        }
        this.list_padding_bottom = Integer.parseInt( themeProps.getProperty( "list.padding.bottom", "0" ) );
        this.list_padding_right = Integer.parseInt( themeProps.getProperty( "list.padding.right", "0" ) );
        this.list_padding_top = Integer.parseInt( themeProps.getProperty( "list.padding.top", "0" ) );
        this.list_padding_left = Integer.parseInt( themeProps.getProperty( "list.padding.left", "3" ) );
        String list_selection_background = themeProps.getProperty( "list.selection.background", null );
        String list_selection_foreground = themeProps.getProperty( "list.selection.foreground", null );
        this.list_selection_background = ( list_selection_background != null ) ? Colorf.parseColor( list_selection_background ) : null;
        this.list_selection_foreground = ( list_selection_foreground != null ) ? Colorf.parseColor( list_selection_foreground ) : null;
        
        this.combobox_list_border_name = themeProps.getProperty( "combobox.list.border.name", "bevel/raised" );
        String combobox_list_hover_background = themeProps.getProperty( "combobox.list.hover.background", null );
        String combobox_list_hover_foreground = themeProps.getProperty( "combobox.list.hover.foreground", null );
        this.combobox_list_hover_background = ( combobox_list_hover_background != null ) ? Colorf.parseColor( combobox_list_hover_background ) : null;
        this.combobox_list_hover_foreground = ( combobox_list_hover_foreground != null ) ? Colorf.parseColor( combobox_list_hover_foreground ) : null;
        
        String textfield_background_color = themeProps.getProperty( "textfield.background", null );
        this.textfield_background_color = ( textfield_background_color != null ) ? Colorf.parseColor( textfield_background_color ) : null;
        this.textfield_border_size_bottom = Integer.parseInt( themeProps.getProperty( "textfield.border.size.bottom", "2" ) );
        this.textfield_border_size_right = Integer.parseInt( themeProps.getProperty( "textfield.border.size.right", "2" ) );
        this.textfield_border_size_top = Integer.parseInt( themeProps.getProperty( "textfield.border.size.top", "2" ) );
        this.textfield_border_size_left = Integer.parseInt( themeProps.getProperty( "textfield.border.size.left", "2" ) );
        
        this.textfield_caret_texture = themeProps.getProperty( "textfield.caret.texture", "black" );
        
        String slider_size_height = themeProps.getProperty( "slider.size.height", "23" );
        String slider_handle_yoffset = themeProps.getProperty( "slider.handle.yoffset", "0" );
        String slider_smoothsliding = themeProps.getProperty( "slider.smoothsliding", "true" );
        
        this.slider_size_height = Integer.parseInt( slider_size_height );
        this.slider_handle_yoffset = Integer.parseInt( slider_handle_yoffset );
        this.slider_smoothsliding = Boolean.parseBoolean( slider_smoothsliding );
    }
    
    /**
     * Reads from a properties file of version 0.1.5
     * 
     * @param themeProps the properties
     */
    private void read_0_1_5( Properties themeProps )
    {
        read_0_1_4( themeProps );
        
        String fontName = themeProps.getProperty( "label.font.name", this.font.getName() );
        String fontStyle = themeProps.getProperty( "label.font.style", String.valueOf( this.font.getStyle() ) );
        String fontSize = themeProps.getProperty( "label.font.size", String.valueOf( this.font.getSize() ) );
        
        this.label_font = HUDFont.getFont( fontName, string2FontStyle( fontStyle ), Integer.parseInt( fontSize ) );
        
        String font_color = themeProps.getProperty( "label.font.color", null );
        
        if ( font_color == null )
            this.label_font_color = this.font_color;
        else
            this.label_font_color = Colorf.parseColor( font_color );
    }
    
    /**
     * Reads from a properties file of version 0.1.6
     * 
     * @param themeProps the properties
     */
    private void read_0_1_6( Properties themeProps )
    {
        read_0_1_5( themeProps );
        
        String fontName = themeProps.getProperty( "button.font.name", this.font.getName() );
        String fontStyle = themeProps.getProperty( "button.font.style", String.valueOf( this.font.getStyle() ) );
        String fontSize = themeProps.getProperty( "button.font.size", String.valueOf( this.font.getSize() ) );
        String font_color = themeProps.getProperty( "button.font.color", null );
        
        this.button_font = HUDFont.getFont( fontName, string2FontStyle( fontStyle ), Integer.parseInt( fontSize ) );
        
        if ( font_color == null )
            this.button_font_color = this.font_color;
        else
            this.button_font_color = Colorf.parseColor( font_color );
    }
    
    /**
     * Reads from a properties file of version 0.1.7
     * 
     * @param themeProps the properties
     */
    private void read_0_1_7( Properties themeProps )
    {
        read_0_1_6( themeProps );
        
        this.cursor_pointer1_texture = themeProps.getProperty( "cursor.pointer1.texture", null );
        this.cursor_pointer1_zero_x = Integer.parseInt( themeProps.getProperty( "cursor.pointer1.zero.x", "0" ) );
        this.cursor_pointer1_zero_y = Integer.parseInt( themeProps.getProperty( "cursor.pointer1.zero.y", "0" ) );
        this.cursor_pointer2_texture = themeProps.getProperty( "cursor.pointer2.texture", null );
        this.cursor_pointer2_zero_x = Integer.parseInt( themeProps.getProperty( "cursor.pointer2.zero.x", "0" ) );
        this.cursor_pointer2_zero_y = Integer.parseInt( themeProps.getProperty( "cursor.pointer2.zero.y", "0" ) );
        this.cursor_crosshair_texture = themeProps.getProperty( "cursor.crosshair.texture", null );
        this.cursor_crosshair_zero_x = Integer.parseInt( themeProps.getProperty( "cursor.crosshair.zero.x", "0" ) );
        this.cursor_crosshair_zero_y = Integer.parseInt( themeProps.getProperty( "cursor.crosshair.zero.y", "0" ) );
        this.cursor_text_texture = themeProps.getProperty( "cursor.text.texture", null );
        this.cursor_text_zero_x = Integer.parseInt( themeProps.getProperty( "cursor.text.zero.x", "0" ) );
        this.cursor_text_zero_y = Integer.parseInt( themeProps.getProperty( "cursor.text.zero.y", "0" ) );
        this.cursor_help_texture = themeProps.getProperty( "cursor.help.texture", null );
        this.cursor_help_zero_x = Integer.parseInt( themeProps.getProperty( "cursor.help.zero.x", "0" ) );
        this.cursor_help_zero_y = Integer.parseInt( themeProps.getProperty( "cursor.help.zero.y", "0" ) );
        this.cursor_wait_texture = themeProps.getProperty( "cursor.wait.texture", null );
        this.cursor_wait_zero_x = Integer.parseInt( themeProps.getProperty( "cursor.wait.zero.x", "0" ) );
        this.cursor_wait_zero_y = Integer.parseInt( themeProps.getProperty( "cursor.wait.zero.y", "0" ) );
    }
    
    /**
     * Reads from a properties file of version 0.1.8
     * 
     * @param themeProps the properties
     */
    private void read_0_1_8( Properties themeProps )
    {
        read_0_1_7( themeProps );
        
        String fontName = themeProps.getProperty( "font.disabled.name", DEFAULT_FONT_DISABLED_NAME );
        String fontStyle = themeProps.getProperty( "font.disabled.style", DEFAULT_FONT_DISABLED_STYLE );
        String fontSize = themeProps.getProperty( "font.disabled.size", DEFAULT_FONT_DISABLED_SIZE );
        String font_color = themeProps.getProperty( "font.disabled.color", DEFAULT_FONT_DISABLED_COLOR );
        
        this.font_disabled = HUDFont.getFont( fontName, string2FontStyle( fontStyle ), Integer.parseInt( fontSize ) );
        this.font_disabled_color = Colorf.parseColor( font_color );
        
        
        String fontName2 = themeProps.getProperty( "label.font.disabled.name", this.font_disabled.getName() );
        String fontStyle2 = themeProps.getProperty( "label.font.disabled.style", String.valueOf( this.font_disabled.getStyle() ) );
        String fontSize2 = themeProps.getProperty( "label.font.disabled.size", String.valueOf( this.font_disabled.getSize() ) );
        
        this.label_font_disabled = HUDFont.getFont( fontName2, string2FontStyle( fontStyle2 ), Integer.parseInt( fontSize2 ) );
        
        String font_color2 = themeProps.getProperty( "label.font.disabled.color", null );
        
        if ( font_color2 == null )
            this.label_font_disabled_color = this.font_disabled_color;
        else
            this.label_font_disabled_color = Colorf.parseColor( font_color2 );
        
        
        fontName2 = themeProps.getProperty( "button.font.disabled.name", this.font_disabled.getName() );
        fontStyle2 = themeProps.getProperty( "button.font.disabled.style", String.valueOf( this.font_disabled.getStyle() ) );
        fontSize2 = themeProps.getProperty( "button.font.disabled.size", String.valueOf( this.font_disabled.getSize() ) );
        
        this.button_font_disabled = HUDFont.getFont( fontName2, string2FontStyle( fontStyle2 ), Integer.parseInt( fontSize2 ) );
        
        font_color2 = themeProps.getProperty( "button.font.disabled.color", null );
        
        if ( font_color2 == null )
            this.button_font_disabled_color = this.font_disabled_color;
        else
            this.button_font_disabled_color = Colorf.parseColor( font_color2 );
    }
    
    private void read_2_0_0( Properties themeProps )
    {
        read_0_1_8( themeProps );
    }
    
    private static final long getVersionLong( int major, int minor, int revision )
    {
        return ( major * 1000000000L + minor * 1000000L + revision * 1000L );
    }
    
    protected ThemeProperties()
    {
    }
    
    /**
     * Creates a new ThemeProperties instance
     * and reads the properties from a properties-file.
     * 
     * @param in InputStream to the properties-file
     */
    public ThemeProperties( InputStream in ) throws IOException
    {
        this();
        
        Properties themeProps = new Properties();
        
        themeProps.load( in );
        
        String[] versionString = themeProps.getProperty( "propsfile.version", "0.0.1" ).split( "\\." );
        long version = Long.parseLong( versionString[ 0 ] ) * 1000000000L + Long.parseLong( versionString[ 1 ] ) * 1000000L + Long.parseLong( versionString[ 2 ] ) * 1000L;
        
        if ( version < 0L * 1000000000L + 1L * 1000000L + 1L * 1000L )
            throw new IllegalArgumentException( "unsupported properties file" );
        else if ( version <= getVersionLong( 0, 1, 1 ) )
            read_0_1_1( themeProps );
        else if ( version <= getVersionLong( 0, 1, 2 ) )
            read_0_1_2( themeProps );
        else if ( version <= getVersionLong( 0, 1, 3 ) )
            read_0_1_3( themeProps );
        else if ( version <= getVersionLong( 0, 1, 4 ) )
            read_0_1_4( themeProps );
        else if ( version <= getVersionLong( 0, 1, 5 ) )
            read_0_1_5( themeProps );
        else if ( version <= getVersionLong( 0, 1, 6 ) )
            read_0_1_6( themeProps );
        else if ( version <= getVersionLong( 0, 1, 7 ) )
            read_0_1_7( themeProps );
        else if ( version <= getVersionLong( 0, 1, 8 ) )
            read_0_1_8( themeProps );
        else if ( version <= getVersionLong( 2, 0, 0 ) )
            read_2_0_0( themeProps );
        else
            throw new IllegalArgumentException( "unsupported properties file" );
    }
}
