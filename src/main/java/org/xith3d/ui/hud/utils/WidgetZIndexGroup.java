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
package org.xith3d.ui.hud.utils;

import java.util.HashSet;

import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.devices.components.MouseButton;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.WidgetMouseListener;

/**
 * This class can be used to logically group some Widgets by their
 * z-index. When one Widget is clicked, it's z-index will be set to
 * the maximum z-index of the group and the ones lying over it are
 * being set a lower z-index.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class WidgetZIndexGroup implements WidgetMouseListener
{
    private final HashSet<Widget> widgets = new HashSet<Widget>();
    private int maxZIndex = 0;
    private int quantum;
    
    /**
     * Sets the maximum z-index, which will be set to the last clicked Widget in the group.
     * Call this method, when you change the z-index of a Widget after adding it to this group.
     * 
     * @param mzi the new maximum z-index
     */
    public void setMaxZIndex( int mzi )
    {
        this.maxZIndex = mzi;
    }
    
    /**
     * @return the maximum z-index, which will be set to the last clicked Widget in the group.
     */
    public int getMaxZIndex()
    {
        return ( this.maxZIndex );
    }
    
    /**
     * Sets the step the zIndices are altered with.
     */
    public void setQuantum( int quantum )
    {
        this.quantum = quantum;
    }
    
    /**
     * @return the step the zIndices are altered with.
     */
    public int getQuantum()
    {
        return ( quantum );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonPressed( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
        if ( ( isTopMost ) && ( widgets.contains( widget ) ) )
        {
            // bring the clicked Widget to front
            
            final int zIndex = widget.getZIndex();
            
            for ( Widget w: widgets )
            {
                if ( ( w.getZIndex() > zIndex ) && ( w != widget ) )
                    w.setZIndex( w.getZIndex() - quantum );
            }
            
            widget.setZIndex( maxZIndex );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseButtonReleased( Widget widget, MouseButton button, float x, float y, long when, long lastWhen, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseMoved( Widget widget, float x, float y, int buttonsState, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseStopped( Widget widget, float x, float y, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseWheelMoved( Widget widget, int delta, boolean isPageMove, float x, float y, long when, boolean isTopMost )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseEntered( Widget widget, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onMouseExited( Widget widget, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void onInputStateChanged( Widget widget, DeviceComponent comp, int delta, int state, long when, boolean isTopMost, boolean hasFocus )
    {
    }
    
    /**
     * Adds a Widget to this logic group.
     * 
     * @param widget the Widget to add
     */
    public void add( Widget widget )
    {
        widgets.add( widget );
        
        if ( widget.getZIndex() > maxZIndex )
            maxZIndex = widget.getZIndex();
        
        widget.addMouseListener( this );
    }
    
    /**
     * Removes a Widget from this logic group.
     * 
     * @param widget the Widget to remove
     */
    public boolean remove( Widget widget )
    {
        if ( widgets.contains( widget ) )
            widget.removeMouseListener( this );
        
        return ( widgets.remove( widget ) );
    }
    
    /**
     * Creates a new WidgetZIndexGroup.
     * 
     * @param quantum the step the zIndices are altered with (default: 1)
     */
    public WidgetZIndexGroup( int quantum )
    {
        this.quantum = quantum;
    }
    
    /**
     * Creates a new WidgetZIndexGroup.
     */
    public WidgetZIndexGroup()
    {
        this( 1 );
    }
}
