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

import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.utils.HUDTextureUtils;

/**
 * This is an interface for the most basic methods of a Border.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class Border
{
    /**
     * This class is used to describe a Border Widget.
     * You can pass it to the Border constructor.
     * Modifications on the used instance after creating the Border Widget
     * won't have any effect.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public static class Description extends Widget.DescriptionBase
    {
        private String name = null;
        
        private int leftWidth;
        private int rightWidth;
        private int topHeight;
        private int bottomHeight;
        
        private int llUpperHeight;
        private int llRightWidth;
        private int lrLeftWidth;
        private int lrUpperHeight;
        private int urLowerHeight;
        private int urLeftWidth;
        private int ulRightWidth;
        private int ulLowerHeight;
        
        private Colorf color;
        
        private Texture2D texture;
        
        public void setName( String name )
        {
            this.name = name;
        }
        
        public final String getName()
        {
            return ( name );
        }
        
        public final boolean hasNonZeroSize()
        {
            return ( ( bottomHeight + rightWidth + topHeight + leftWidth ) > 0f );
        }
        
        public void setBottomHeight( int bh )
        {
            this.bottomHeight = bh;
        }
        
        public final int getBottomHeight()
        {
            return ( bottomHeight );
        }
        
        public void setRightWidth( int rw )
        {
            this.rightWidth = rw;
        }
        
        public final int getRightWidth()
        {
            return ( rightWidth );
        }
        
        public void setTopHeight( int th )
        {
            this.topHeight = th;
        }
        
        public final int getTopHeight()
        {
            return ( topHeight );
        }
        
        public void setLeftWidth( int lw )
        {
            this.leftWidth = lw;
        }
        
        public final int getLeftWidth()
        {
            return ( leftWidth );
        }
        
        public void setLLupperHeight( int value )
        {
            this.llUpperHeight = value;
        }
        
        public final int getLLupperHeight()
        {
            return ( llUpperHeight );
        }
        
        public void setLLrightWidth( int value )
        {
            this.llRightWidth = value;
        }
        
        public final int getLLrightWidth()
        {
            return ( llRightWidth );
        }
        
        public void setLRleftWidth( int value )
        {
            this.lrLeftWidth = value;
        }
        
        public final int getLRleftWidth()
        {
            return ( lrLeftWidth );
        }
        
        public void setLRupperHeight( int value )
        {
            this.lrUpperHeight = value;
        }
        
        public final int getLRupperHeight()
        {
            return ( lrUpperHeight );
        }
        
        public void setURlowerHeight( int value )
        {
            this.urLowerHeight = value;
        }
        
        public final int getURlowerHeight()
        {
            return ( urLowerHeight );
        }
        
        public void setURleftWidth( int value )
        {
            this.urLeftWidth = value;
        }
        
        public final int getURleftWidth()
        {
            return ( urLeftWidth );
        }
        
        public void setULrightWidth( int value )
        {
            this.ulRightWidth = value;
        }
        
        public final int getULrightWidth()
        {
            return ( ulRightWidth );
        }
        
        public void setULlowerHeight( int value )
        {
            this.ulLowerHeight = value;
        }
        
        public final int getULlowerHeight()
        {
            return ( ulLowerHeight );
        }
        
        public void setSizes( int bottom, int right, int top, int left )
        {
            this.bottomHeight = bottom;
            this.rightWidth = right;
            this.topHeight = top;
            this.leftWidth = left;
        }
        
        public void setSizes( int bottom, int right, int top, int left, int heightLLupper, int widthLLright, int widthLRleft, int heightLRupper, int heightURlower, int widthURleft, int widthULright, int heightULlower )
        {
            this.bottomHeight = bottom;
            this.rightWidth = right;
            this.topHeight = top;
            this.leftWidth = left;
            
            this.llUpperHeight = heightLLupper;
            this.llRightWidth = widthLLright;
            this.lrLeftWidth = widthLRleft;
            this.lrUpperHeight = heightLRupper;
            this.urLowerHeight = heightURlower;
            this.urLeftWidth = widthURleft;
            this.ulRightWidth = widthULright;
            this.ulLowerHeight = heightULlower;
        }
        
        public void setColor( Colorf color )
        {
            this.color = color;
        }
        
        public final Colorf getColor()
        {
            return ( color );
        }
        
        public void setTexture( Texture2D texture )
        {
            this.texture = texture;
        }
        
        public final void setTexture( String texture )
        {
            setTexture( HUDTextureUtils.getTextureOrNull( texture, true ) );
        }
        
        public final Texture2D getTexture()
        {
            return ( texture );
        }
        
        /**
         * Clone-Constructor
         * 
         * @param bd the original to be duplicated
         */
        public void set( Description bd )
        {
            this.name = bd.name;
            
            this.bottomHeight = bd.bottomHeight;
            this.rightWidth = bd.rightWidth;
            this.topHeight = bd.topHeight;
            this.leftWidth = bd.leftWidth;
            
            this.llUpperHeight = bd.llUpperHeight;
            this.llRightWidth = bd.llRightWidth;
            this.lrLeftWidth = bd.lrLeftWidth;
            this.lrUpperHeight = bd.lrUpperHeight;
            this.urLowerHeight = bd.urLowerHeight;
            this.urLeftWidth = bd.urLeftWidth;
            this.ulRightWidth = bd.ulRightWidth;
            this.ulLowerHeight = bd.ulLowerHeight;
            
            this.color = bd.color;
            
            this.texture = bd.texture;
        }
        
        /**
         * @return a clone of this instance.
         */
        @Override
        public Description clone()
        {
            return ( new Description( this ) );
        }
        
        /**
         * Clone-Contructor
         * 
         * @param bd the original to be duplicated
         */
        private Description( Description bd )
        {
            this.set( bd );
        }
        
        public Description( int bottom, int right, int top, int left, int heightLLupper, int widthLLright, int widthLRleft, int heightLRupper, int heightURlower, int widthURleft, int widthULright, int heightULlower, Texture2D texture )
        {
            this.bottomHeight = bottom;
            this.rightWidth = right;
            this.topHeight = top;
            this.leftWidth = left;
            
            this.llUpperHeight = heightLLupper;
            this.llRightWidth = widthLLright;
            this.lrLeftWidth = widthLRleft;
            this.lrUpperHeight = heightLRupper;
            this.urLowerHeight = heightURlower;
            this.urLeftWidth = widthURleft;
            this.ulRightWidth = widthULright;
            this.ulLowerHeight = heightULlower;
            
            this.texture = texture;
        }
        
        public Description( int bottom, int right, int top, int left, Texture2D texture )
        {
            this( bottom, right, top, left, 0, 0, 0, 0, 0, 0, 0, 0, texture );
        }
        
        public Description( int bottom, int right, int top, int left, int heightLLupper, int widthLLright, int widthLRleft, int heightLRupper, int heightURlower, int widthURleft, int widthULright, int heightULlower, String texture )
        {
            this( bottom, right, top, left, heightLLupper, widthLLright, widthLRleft, heightLRupper, heightURlower, widthURleft, widthULright, heightULlower, HUDTextureUtils.getTexture( texture, true ) );
        }
        
        public Description( int bottom, int right, int top, int left, String texture )
        {
            this( bottom, right, top, left, 0, 0, 0, 0, 0, 0, 0, 0, HUDTextureUtils.getTexture( texture, true ) );
        }
        
        public Description( int bottom, int right, int top, int left )
        {
            this( bottom, right, top, left, (Texture2D)null );
        }
        
        public Description( int bottom, int right, int top, int left, Colorf color )
        {
            this( bottom, right, top, left );
            
            this.setColor( color );
        }
        
        public Description( int width, Colorf color )
        {
            this( width, width, width, width, color );
        }
    }
    
    private int bottomHeight;
    private int rightWidth;
    private int topHeight;
    private int leftwidth;
    
    /**
     * @return the height of the bottom side of this Border
     */
    public final int getBottomHeight()
    {
        return ( bottomHeight );
    }
    
    /**
     * @return the width of the right side of this Border
     */
    public final int getRightWidth()
    {
        return ( rightWidth );
    }
    
    /**
     * @return the height of the top side of this Border
     */
    public final int getTopHeight()
    {
        return ( topHeight );
    }
    
    /**
     * @return the width of the left side of this Border
     */
    public final int getLeftWidth()
    {
        return ( leftwidth );
    }
    
    /*
    public HUDPickResult pick( int canvasX, int canvasY, HUDPickReason pickReason, MouseButton button, long when, long meta, int flags )
    {
        HUDPickResult hpr = super.pick( canvasX, canvasY, pickReason, button, when, meta, flags );
        
        if ( hpr != null )
        {
            // the mouse is over the Border-rectangle.
            // now test, if it actually is over the border.
            
            final Tuple2f locP = Tuple2f.fromPool();
            
            getLocationPixels2HUD__( canvasX, canvasY, locP );
            final float locWx = this.getLeft();
            final float locWy = this.getTop();
            
            if ( ( locWx + getLeftWidth() < locP.getX() ) && ( locWy + getTopHeight() < locP.getY() ) && ( locWx + getWidth() - getRightWidth() > locP.getX() ) && ( locWy + getHeight() - getBottomHeight() > locP.getY() ) )
            {
                Tuple2f.toPool( locP );
                
                return ( hpr );
            }
            else
            {
                Tuple2f.toPool( locP );
            }
        }
        
        return ( null );
    }
    */
    
    public abstract void drawBorder( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, Widget hostWidget );
    
    protected Border( int bottomHeight, int rightWidth, int topHeight, int leftwidth )
    {
        this.bottomHeight = bottomHeight;
        this.rightWidth = rightWidth;
        this.topHeight = topHeight;
        this.leftwidth = leftwidth;
    }
}
