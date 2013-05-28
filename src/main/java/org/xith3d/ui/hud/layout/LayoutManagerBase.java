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
package org.xith3d.ui.hud.layout;

import java.util.ArrayList;
import java.util.HashSet;

import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.listeners.WidgetSizeListener;
import org.xith3d.ui.hud.listeners.WidgetVisibilityListener;

/**
 * An abstract base class for all {@link LayoutManager}s.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class LayoutManagerBase implements LayoutManager
{
    final WidgetSizeListener sizeListener = new WidgetSizeListener()
    {
        private final HashSet< Widget > ignoredWidgets = new HashSet< Widget >();
        
        public void onWidgetSizeChanged( Widget widget, float oldWidth, float oldHeight, float newWidth, float newHeight )
        {
            if ( ( !ignoredWidgets.contains( widget ) ) && ( widget.getHUD() != null ) )
            {
                ignoredWidgets.add( widget );
                doLayout( widget.getContainer() );
                ignoredWidgets.remove( widget );
            }
        }
    };
    
    final WidgetVisibilityListener visibilityListener = new WidgetVisibilityListener()
    {
        public void onWidgetVisibilityChanged( Widget widget, boolean isVisible )
        {
            if ( getInvisibleWidgetsHidden() && ( widget.getHUD() != null ) )
            {
                doLayout( widget.getContainer() );
            }
        }
    };
    
    private final BorderSettableLayoutManager borderSource;
    
    private final ArrayList<Widget> widgets = new ArrayList<Widget>();
    
    private boolean inivisibleWidgetsHidden = true;
    
    private boolean isWorking = false;
    
    /**
     * {@inheritDoc}
     */
    public final void setInvisibleWidgetsHidden( boolean hidden )
    {
        this.inivisibleWidgetsHidden = hidden;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean getInvisibleWidgetsHidden()
    {
        return ( inivisibleWidgetsHidden );
    }
    
    /**
     * @return all Widgets added to this {@link LayoutManager}.
     */
    public final ArrayList< Widget > getWidgets()
    {
        return ( widgets );
    }
    
    /**
     * {@inheritDoc}
     */
    public void addWidget( Widget widget, Object constraints )
    {
        if ( widget != null )
        {
            widgets.add( widget );
            
            widget.addSizeListener( sizeListener );
            widget.addVisibilityListener( visibilityListener );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeWidget( Widget widget )
    {
        if ( widget != null )
        {
            widgets.remove( widget );
            
            widget.removeSizeListener( sizeListener );
            widget.removeVisibilityListener( visibilityListener );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void clear()
    {
        widgets.clear();
    }
    
    protected abstract void doLayout( final float left0, final float top0, final float containerResX, final float containerResY );
    
    /**
     * {@inheritDoc}
     */
    public void doLayout( WidgetContainer container )
    {
        if ( isWorking )
            return;
        
        isWorking = true;
        
        final float left0;
        final float top0;
        final float resX;
        final float resY;
        
        if ( borderSource == null )
        {
            left0 = 0f;
            top0 = 0f;
            resX = container.getResX();
            resY = container.getResY();
        }
        else
        {
            left0 = borderSource.getBorderLeft();
            top0 = borderSource.getBorderTop();
            resX = container.getResX() - left0 - borderSource.getBorderRight();
            resY = container.getResY() - top0 - borderSource.getBorderBottom();
        }
        
        doLayout( left0, top0, resX, resY );
        
        isWorking = false;
    }
    
    public LayoutManagerBase()
    {
        if ( this instanceof BorderSettableLayoutManager )
            this.borderSource = (BorderSettableLayoutManager)this;
        else
            this.borderSource = null;
    }
}
