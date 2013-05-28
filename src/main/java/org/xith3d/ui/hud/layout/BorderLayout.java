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

import org.xith3d.ui.hud.base.Widget;

/**
 * The BorderLayout potentially has five areas to place Widgets to.
 * the NORTH, EAST, WEST and SOUTH earas and the CENTER.
 * the CENTER always takes the whole remaining space.
 * 
 *   ****************************
 *   *           NORTH          *
 *   ****************************
 *   *        *        *        *
 *   *  WEST  * CENTER *  EAST  *
 *   *        *        *        *
 *   ****************************
 *   *          SOUTH           *
 *   ****************************
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class BorderLayout extends BorderSettableLayoutManagerBase
{
    public static enum Area
    {
        NORTH,
        SOUTH,
        WEST,
        EAST,
        CENTER;
    }
    
    private Widget northWidget = null;
    private Widget southWidget = null;
    private Widget westWidget = null;
    private Widget eastWidget = null;
    private Widget centerWidget = null;
    
    private float hgap;
    private float vgap;
    
    public void setHGap( float hgap )
    {
        this.hgap = hgap;
    }
    
    public final float getHGap()
    {
        return ( hgap );
    }
    
    public void setVGap( float vgap )
    {
        this.vgap = vgap;
    }
    
    public final float getVGap()
    {
        return ( vgap );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addWidget( Widget widget, Object constraints )
    {
        super.addWidget( widget, constraints );
        
        if ( ( constraints != null ) && ( !( constraints instanceof Area ) ) )
        {
            throw new IllegalArgumentException( "constraints must either be null of of type BorderLayout.Area" );
        }
        
        if ( ( constraints == null ) || ( constraints == Area.CENTER ) )
        {
            centerWidget = widget;
        }
        else
        {
            switch ( (Area)constraints )
            {
                case NORTH:
                    northWidget = widget;
                    break;
                case SOUTH:
                    southWidget = widget;
                    break;
                case WEST:
                    westWidget = widget;
                    break;
                case EAST:
                    eastWidget = widget;
                    break;
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeWidget( Widget widget )
    {
        super.removeWidget( widget );
        
        if ( widget == northWidget )
            northWidget = null;
        else if ( widget == southWidget )
            southWidget = null;
        else if ( widget == westWidget )
            westWidget = null;
        else if ( widget == eastWidget )
            eastWidget = null;
        else if ( widget == centerWidget )
            centerWidget = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        super.clear();
        
        northWidget = null;
        southWidget = null;
        westWidget = null;
        eastWidget = null;
        centerWidget = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLayout( final float left0, final float top0, final float containerResX, final float containerResY )
    {
        float centerWidth = containerResX;
        float centerHeight = containerResY;
        
        float centerLeft = left0;
        float centerTop = top0;
        
        if ( ( northWidget != null ) && ( northWidget.isVisible() || !getInvisibleWidgetsHidden() ) )
        {
            northWidget.setSize( containerResX, northWidget.getHeight() );
            northWidget.setLocation( left0, top0 );
            
            centerHeight -= northWidget.getHeight() + getVGap();
            centerTop += northWidget.getHeight() + getVGap();
        }
        
        if ( ( southWidget != null ) && ( southWidget.isVisible() || !getInvisibleWidgetsHidden() ) )
        {
            southWidget.setSize( containerResX, southWidget.getHeight() );
            southWidget.setLocation( left0, top0 + containerResY  - southWidget.getHeight() );
            
            centerHeight -= southWidget.getHeight() + getVGap();
        }
        
        if ( ( westWidget != null ) && ( westWidget.isVisible() || !getInvisibleWidgetsHidden() ) )
        {
            westWidget.setSize( westWidget.getWidth(), centerHeight );
            westWidget.setLocation( left0, centerTop );
            
            centerWidth -= westWidget.getWidth() + getHGap();
            centerLeft += westWidget.getWidth() + getHGap();
        }
        
        if ( ( eastWidget != null ) && ( eastWidget.isVisible() || !getInvisibleWidgetsHidden() ) )
        {
            eastWidget.setSize( eastWidget.getWidth(), centerHeight );
            eastWidget.setLocation( left0 + containerResX - eastWidget.getWidth(), centerTop );
            
            centerWidth -= eastWidget.getWidth() + getHGap();
        }
        
        if ( ( centerWidget != null ) && ( centerWidget.isVisible() || !getInvisibleWidgetsHidden() ) )
        {
            centerWidget.setSize( centerWidth, centerHeight );
            centerWidget.setLocation( centerLeft, centerTop );
        }
    }
    
    public BorderLayout( float hgap, float vgap, float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        super( borderBottom, borderRight, borderTop, borderLeft );
        
        if ( ( hgap < 0f ) || ( vgap < 0f ) )
            throw new IllegalArgumentException( "hgap and vgap must be >= 0" );
        
        this.hgap = hgap;
        this.vgap = vgap;
    }
    
    public BorderLayout( float hgap, float vgap )
    {
        this( hgap, vgap, 0f, 0f, 0f, 0f );
    }
    
    public BorderLayout( float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        this( 0f, 0f, borderBottom, borderRight, borderTop, borderLeft );
    }
    
    public BorderLayout()
    {
        this( 0f, 0f, 0f, 0f, 0f, 0f );
    }
}
