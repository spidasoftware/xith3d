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

import org.xith3d.ui.hud.base.AutoSizable;
import org.xith3d.ui.hud.base.Widget;

/**
 * The {@link ListLayout} arranges the Widgets in their order vertically
 * or horizontally.<br>
 * You can set the other (perpendicular) span to be calculated or unmanipulated.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ListLayout extends BorderSettableLayoutManagerBase
{
    public static enum Orientation
    {
        VERTICAL,
        HORIZONTAL
    }
    
    public static enum Alignment
    {
        LEFT_TOP,
        LEFT_CENTER,
        LEFT_BOTTOM,
        CENTER_TOP,
        CENTER_CENTER,
        CENTER_BOTTOM,
        RIGHT_TOP,
        RIGHT_CENTER,
        RIGHT_BOTTOM;
        
        public final boolean isLeft()
        {
            return ( ( this == LEFT_TOP ) || ( this == LEFT_CENTER ) || ( this == LEFT_BOTTOM ) );
        }
        
        public final boolean isHCenter()
        {
            return ( ( this == CENTER_TOP ) || ( this == CENTER_CENTER ) || ( this == CENTER_BOTTOM ) );
        }
        
        public final boolean isRight()
        {
            return ( ( this == RIGHT_TOP ) || ( this == RIGHT_CENTER ) || ( this == RIGHT_BOTTOM ) );
        }
        
        public final boolean isTop()
        {
            return ( ( this == LEFT_TOP ) || ( this == CENTER_TOP ) || ( this == RIGHT_TOP ) );
        }
        
        public final boolean isVCenter()
        {
            return ( ( this == LEFT_CENTER ) || ( this == CENTER_CENTER ) || ( this == RIGHT_CENTER ) );
        }
        
        public final boolean isBottom()
        {
            return ( ( this == LEFT_BOTTOM ) || ( this == CENTER_BOTTOM ) || ( this == RIGHT_BOTTOM ) );
        }
    }
    
    private Orientation orientation;
    private Alignment alignment = Alignment.CENTER_TOP;
    
    private boolean otherSpanCalculated = true;
    
    private float gap;
    
    public void setOrientation( Orientation orientation )
    {
        if ( orientation == null )
            throw new IllegalArgumentException( "orientation must not be null" );
        
        this.orientation = orientation;
    }
    
    public final Orientation getOrientation()
    {
        return ( orientation );
    }
    
    public void setAlignment( Alignment alignment )
    {
        this.alignment = alignment;
    }
    
    public final Alignment getAlignment()
    {
        return ( alignment );
    }
    
    public void setOtherSpanCalculated( boolean b )
    {
        this.otherSpanCalculated = b;
    }
    
    public final boolean isOtherSpanCalculated()
    {
        return ( otherSpanCalculated );
    }
    
    public void setGap( float gap )
    {
        this.gap = gap;
    }
    
    public final float getGap()
    {
        return ( gap );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLayout( final float left0, final float top0, final float containerResX, final float containerResY )
    {
        final ArrayList< Widget > widgets = getWidgets();
        
        if ( orientation == Orientation.HORIZONTAL )
        {
            float left = left0;
            
            if ( ( getAlignment() != null ) && ( getAlignment().isHCenter() || getAlignment().isRight() ) )
            {
                float totalWidth = 0f;
                for ( int i = 0; i < widgets.size(); i++ )
                {
                    final Widget widget = widgets.get( i );
                    
                    if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                        continue;
                    
                    if ( i > 0 )
                        totalWidth += getGap();
                    
                    if ( widget instanceof AutoSizable )
                        ( (AutoSizable)widget ).setMinimalSize();
                    
                    totalWidth += widget.getWidth();
                }
                
                if ( getAlignment().isHCenter() )
                    left += ( containerResX / 2f ) - ( totalWidth / 2f );
                else if ( getAlignment().isRight() )
                    left += containerResX - totalWidth;
            }
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                final Widget widget = widgets.get( i );
                
                if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                    continue;
                
                final float height;
                if ( isOtherSpanCalculated() )
                    height = containerResY;
                else
                    height = widget.getHeight();
                
                widget.setSize( widget.getWidth(), height );
                
                if ( getAlignment() == null )
                {
                    widget.setLocation( left, widget.getTop() );
                }
                else
                {
                    switch ( getAlignment() )
                    {
                        case LEFT_TOP:
                        case CENTER_TOP:
                        case RIGHT_TOP:
                            widget.setLocation( left, top0 );
                            break;
                        case LEFT_CENTER:
                        case CENTER_CENTER:
                        case RIGHT_CENTER:
                            widget.setLocation( left, top0 + ( containerResY / 2f ) - ( height / 2f ) );
                            break;
                        case LEFT_BOTTOM:
                        case CENTER_BOTTOM:
                        case RIGHT_BOTTOM:
                            widget.setLocation( left, top0 + containerResY - height );
                            break;
                    }
                }
                
                left += widget.getWidth() + getGap();
            }
        }
        else if ( orientation == Orientation.VERTICAL )
        {
            float top = top0;
            
            if ( ( getAlignment() != null ) && ( getAlignment().isVCenter() || getAlignment().isBottom() ) )
            {
                float totaHeight = 0f;
                for ( int i = 0; i < widgets.size(); i++ )
                {
                    final Widget widget = widgets.get( i );
                    
                    if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                        continue;
                    
                    if ( i > 0 )
                        totaHeight += getGap();
                    
                    if ( widget instanceof AutoSizable )
                        ( (AutoSizable)widget ).setMinimalSize();
                    
                    totaHeight += widget.getHeight();
                }
                
                if ( getAlignment().isVCenter() )
                    top += ( containerResY / 2f ) - ( totaHeight / 2f );
                else if ( getAlignment().isBottom() )
                    top += containerResY - totaHeight;
            }
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                final Widget widget = widgets.get( i );
                
                if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                    continue;
                
                final float width;
                if ( isOtherSpanCalculated() )
                    width = containerResX;
                else
                    width = widget.getWidth();
                
                widget.setSize( width, widget.getHeight() );
                
                if ( getAlignment() == null )
                {
                    widget.setLocation( widget.getLeft(), top );
                }
                else
                {
                    switch ( getAlignment() )
                    {
                        case LEFT_TOP:
                        case LEFT_CENTER:
                        case LEFT_BOTTOM:
                            widget.setLocation( left0, top );
                            break;
                        case CENTER_TOP:
                        case CENTER_CENTER:
                        case CENTER_BOTTOM:
                            widget.setLocation( left0 + ( containerResX / 2f ) - ( width / 2f ), top );
                            break;
                        case RIGHT_TOP:
                        case RIGHT_CENTER:
                        case RIGHT_BOTTOM:
                            widget.setLocation( left0 + containerResX - width, top );
                            break;
                    }
                }
                
                top += widget.getHeight() + getGap();
            }
        }
    }
    
    public ListLayout( Orientation orientation, float gap, float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        super( borderBottom, borderRight, borderTop, borderLeft );
        
        if ( orientation == null )
            throw new IllegalArgumentException( "orientation must not be null" );
        
        if ( gap < 0f )
            throw new IllegalArgumentException( "gap must be >= 0" );
        
        this.orientation = orientation;
        
        if ( orientation == Orientation.HORIZONTAL )
            this.alignment = Alignment.LEFT_CENTER;
        else
            this.alignment = Alignment.CENTER_TOP;
        
        this.gap = gap;
    }
    
    public ListLayout( Orientation orientation, float gap )
    {
        this( orientation, gap, 0f, 0f, 0f, 0f );
    }
    
    public ListLayout( Orientation orientation, float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        this( orientation, 0f, borderBottom, borderRight, borderTop, borderLeft );
    }
    
    public ListLayout( Orientation orientation )
    {
        this( orientation, 0f, 0f, 0f, 0f, 0f );
    }
}
