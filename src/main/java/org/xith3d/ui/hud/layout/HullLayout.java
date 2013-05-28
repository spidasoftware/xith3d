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

import org.openmali.types.twodee.Dim2f;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.base.Window;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;

/**
 * The {@link HullLayout} uses the left-most and top-most Widget to define a
 * border and then shrinks the container around the contained Widgets.
 * This LayoutManager doesn't layout the Widgets at all. An inner LayoutManager
 * can be used to layout the Widgets.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HullLayout extends BorderSettableLayoutManagerBase
{
    private final LayoutManager innerLayout;
    
    private Window window = null;
    private WidgetContainer container = null;
    
    private boolean stopper = false;
    
    /**
     * @return the {@link LayoutManager}, that layouts the Widgets.
     */
    public final LayoutManager getInnerLayout()
    {
        return ( innerLayout );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addWidget( Widget widget, Object constraints )
    {
        super.addWidget( widget, constraints );
        
        if ( innerLayout != null )
        {
            innerLayout.addWidget( widget, constraints );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWidget( Widget widget )
    {
        super.removeWidget( widget );
        
        if ( innerLayout != null )
        {
            innerLayout.removeWidget( widget );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        super.clear();
        
        if ( innerLayout != null )
        {
            innerLayout.clear();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLayout( float left0, float top0, float containerResX, float containerResY )
    {
        if ( stopper )
        {
            return;
        }
        
        if ( innerLayout != null )
        {
            innerLayout.doLayout( container );
        }
        
        final ArrayList< Widget > widgets = getWidgets();
        
        float leftMost = Float.MAX_VALUE;
        float topMost = Float.MAX_VALUE;
        float rightMost = -Float.MAX_VALUE;
        float bottomMost = -Float.MAX_VALUE;
        
        int numVisibleWidgets = 0;
        
        for ( int i = 0; i < widgets.size(); i++ )
        {
            Widget widget = widgets.get( i );
            
            if ( widget.isVisible() || !getInvisibleWidgetsHidden() )
            {
                float left = widget.getLeft();
                if ( left < leftMost )
                    leftMost = left;
                
                float top = widget.getTop();
                if ( top < topMost )
                    topMost = top;
                
                float right = widget.getLeft() + widget.getWidth();
                if ( right > rightMost )
                    rightMost = right;
                
                float bottom = widget.getTop() + widget.getHeight();
                if ( bottom > bottomMost )
                    bottomMost = bottom;
                
                numVisibleWidgets++;
            }
        }
        
        stopper = true;
        
        if ( numVisibleWidgets > 0 )
        {
            final float deltaLeft = left0 - leftMost;
            final float deltaTop = top0 - topMost;
            
            if ( ( deltaLeft != 0f ) || ( deltaTop != 0f ) )
            {
                for ( int i = 0; i < widgets.size(); i++ )
                {
                    Widget widget = widgets.get( i );
                    
                    if ( widget.isVisible() || !getInvisibleWidgetsHidden() )
                    {
                        widget.setLocation( widget.getLeft() + deltaLeft, widget.getTop() + deltaTop );
                    }
                }
            }
            
            float borderWidth = left0 + getBorderRight();
            float borderHeight = top0 + getBorderBottom();
            
            Dim2f buffer = Dim2f.fromPool();
            __HUD_base_PrivilegedAccess.getBorderAndPaddingSizeInHUDSpace( container, buffer );
            borderWidth += buffer.getWidth();
            borderHeight += buffer.getHeight();
            Dim2f.toPool( buffer );
            
            if ( window == null )
                container.setSize( borderWidth + rightMost - leftMost, borderHeight + bottomMost - topMost );
            else
                window.setContentSize( borderWidth + rightMost - leftMost, borderHeight + bottomMost - topMost );
            
            if ( innerLayout != null )
            {
                innerLayout.doLayout( container );
            }
        }
        else
        {
            if ( window == null )
                container.setSize( 0f, 0f );
            else
                window.setContentSize( 0f, 0f );
        }
        
        stopper = false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void doLayout( WidgetContainer container )
    {
        this.container = container;
        Widget assemlby = __HUD_base_PrivilegedAccess.getAssembly( container );
        if ( ( assemlby != null ) && ( assemlby instanceof Window ) )
        {
            this.window = (Window)assemlby;
        }
        else
        {
            this.window = null;
        }
        
        super.doLayout( container );
    }
    
    public HullLayout( LayoutManager innerLayout, float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        super( borderBottom, borderRight, borderTop, borderLeft );
        
        this.innerLayout = innerLayout;
    }
    
    public HullLayout( float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        this( null, borderBottom, borderRight, borderTop, borderLeft );
    }
    
    public HullLayout( LayoutManager innerLayout, float border )
    {
        this( innerLayout, border, border, border, border );
    }
    
    public HullLayout( float border )
    {
        this( null, border, border, border, border );
    }
    
    public HullLayout( LayoutManager innerLayout )
    {
        this( innerLayout, 0f, 0f, 0f, 0f );
    }
    
    public HullLayout()
    {
        this( null );
    }
}
