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

import org.jagatoo.input.devices.components.ControllerAxis;
import org.jagatoo.input.devices.components.ControllerButton;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.HUD.FocusMoveDirection;
import org.xith3d.ui.hud.utils.HUDPickResult;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;

/**
 * Since java doesn't correctly implement the protected modifier
 * and also doesn't provide necessary other modifiers, we need this class
 * to avoid exposing some internal methods to the Widgets' public APIs.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class __HUD_base_PrivilegedAccess
{
    public static final Node getSGNode( Widget widget )
    {
        return ( widget.getSGNode() );
    }
    
    public static final GroupNode getSGGroup( WidgetContainer container )
    {
        return ( container.getSGGroup() );
    }
    
    public static final void setHUD( HUD hud, Widget widget )
    {
        widget.setHUD( hud );
    }
    
    public static final Widget getAssembly( Widget widget )
    {
        return ( widget.getAssembly() );
    }
    
    public static final void setTextureDirty( Widget widget )
    {
        widget.setTextureDirty();
    }
    
    public static final void getBorderSizeInHUDSpace( Widget widget, Dim2f leftTop, Dim2f rightBottom )
    {
        widget.getSizePixels2HUD_( widget.getBorder().getLeftWidth(), widget.getBorder().getTopHeight(), leftTop );
        widget.getSizePixels2HUD_( widget.getBorder().getRightWidth(), widget.getBorder().getBottomHeight(), rightBottom );
    }
    
    public static final void getBorderAndPaddingSizeInHUDSpace( Widget widget, Dim2f buffer )
    {
        if ( ( !( widget instanceof PaddingSettable ) ) && ( widget.getBorder() == null ) )
        {
            buffer.set( 0, 0 );
            return;
        }
        
        int w = 0;
        int h = 0;
        
        if ( widget instanceof PaddingSettable )
        {
            w += ( (PaddingSettable)widget ).getPaddingLeft() + ( (PaddingSettable)widget ).getPaddingRight();
            h += ( (PaddingSettable)widget ).getPaddingTop() + ( (PaddingSettable)widget ).getPaddingBottom();
        }
        
        Border border = widget.getBorder();
        
        if ( border != null )
        {
            w += border.getLeftWidth() + border.getRightWidth();
            h += border.getTopHeight() + border.getBottomHeight();
        }
        
        widget.getSizePixels2HUD_( w, h, buffer );
    }
    
    public static final Widget moveFocus( WidgetContainer container, FocusMoveDirection direction )
    {
        return ( container.moveFocus( direction ) );
    }
    
    public static final boolean widgetBlocksFocusMoveDeviceComponent( Widget widget, DeviceComponent dc )
    {
        return ( widget.blocksFocusMoveDeviceComponent( dc ) );
    }
    
    public static final HUDPickResult pick( Widget widget, int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        return ( widget.pick( canvasX, canvasY, pickReason, button, when, meta, flags ) );
    }
    
    public static final void onFocusLost( Widget widget )
    {
        widget.onFocusLost();
    }
    
    public static final void onFocusGained( Widget widget )
    {
        widget.onFocusGained();
    }
    
    public static final void setCachedToolTipWidget( Widget widget, Widget tooltipWidget )
    {
        widget.setCachedToolTipWidget( tooltipWidget );
    }
    
    public static final Widget getCachedToolTipWidget( Widget widget )
    {
        return ( widget.getCachedToolTipWidget() );
    }
    
    public static final void onMouseEntered( Widget widget, boolean isTopMost, boolean hasFocus )
    {
        widget.onMouseEntered( isTopMost, hasFocus );
    }
    
    public static final void onMouseExited( Widget widget, boolean isTopMost, boolean hasFocus )
    {
        widget.onMouseExited( isTopMost, hasFocus );
    }
    
    public static final void onMouseButtonPressed( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        widget.onMouseButtonPressed( button, x, y, when, lastWhen, isTopMost, hasFocus );
    }
    
    public static final void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        widget.onMouseButtonReleased( button, x, y, when, lastWhen, isTopMost, hasFocus );
    }
    
    public static final void onMouseMoved( Widget widget, float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
        widget.onMouseMoved( x, y, buttonsState, when, isTopMost, hasFocus );
    }
    
    public static final void onMouseStopped( Widget widget, float x, float y, long when, boolean isTopMost, boolean hasFocus )
    {
        widget.onMouseStopped( x, y, when, isTopMost, hasFocus );
    }
    
    public static final void onMouseWheelMoved( Widget widget, int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost )
    {
        widget.onMouseWheelMoved( delta, isPageMove, x, y, when, isTopMost );
    }
    
    public static final void onKeyPressed( Widget widget, Key key, int modifierMask, long when )
    {
        widget.onKeyPressed( key, modifierMask, when );
    }
    
    public static final void onKeyReleased( Widget widget, Key key, int modifierMask, long when )
    {
        widget.onKeyReleased( key, modifierMask, when );
    }
    
    public static final void onKeyTyped( Widget widget, char ch, int modifierMask, long when )
    {
        widget.onKeyTyped( ch, modifierMask, when );
    }
    
    public static final void onControllerButtonPressed( Widget widget, ControllerButton button, long when )
    {
        widget.onControllerButtonPressed( button, when );
    }
    
    public static final void onControllerButtonReleased( Widget widget, ControllerButton button, long when )
    {
        widget.onControllerButtonReleased( button, when );
    }
    
    public static final void onControllerAxisChanged( Widget widget, ControllerAxis axis, int axisDelta, long when )
    {
        widget.onControllerAxisChanged( axis, axisDelta, when );
    }
    
    public static final void onInputStateChanged( Widget widget, DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
        widget.onInputStateChanged( comp, delta, state, when, isTopMost, hasFocus );
    }
    
    public static final boolean isMouseOverStateImage( LabeledStateButton sb, float mouseX, float mouseY )
    {
        return ( sb.isMouseOverStateImage( mouseX, mouseY ) );
    }
    
    public static final int getWidthPX( Widget widget )
    {
        return ( widget.getWidthPX() );
    }
    
    public static final int getHeightPX( Widget widget )
    {
        return ( widget.getHeightPX() );
    }
    
    public static final <Dim2f_ extends Dim2f> Dim2f_ getContentOffset( Widget widget, Dim2f_ buffer )
    {
        return ( widget.getContentOffset( buffer ) );
    }
    
    public static final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationPixels2HUD_( Widget widget, int x, int y, Tuple2f_ buffer )
    {
        return ( widget.getLocationPixels2HUD_( x, y, buffer ) );
    }
}
