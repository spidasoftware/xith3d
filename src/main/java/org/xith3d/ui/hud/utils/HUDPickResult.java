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

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.ui.hud.base.Widget;

/**
 * An instance of this class is returned by an pick method call of a Widget,
 * if the picking was successful.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HUDPickResult implements Comparable<HUDPickResult>
{
    public static final int HUD_PICK_FLAG_IS_INTERNAL = 1;
    public static final int HUD_PICK_FLAG_EVENTS_SUPPRESSED = 2;
    public static final int HUD_PICK_FLAG_JUST_TEST_AND_DO_NOTHING = 4096;
    
    public static enum HUDPickReason
    {
        BUTTON_PRESSED( 1 ),
        BUTTON_RELEASED( 2 ),
        MOUSE_MOVED( 4 ),
        MOUSE_STOPPED( 8 ),
        MOUSE_WHEEL_MOVED_UP( 16 ),
        MOUSE_WHEEL_MOVED_DOWN( 16 ),
        ;
        
        public static final int BUTTON_PRESSED_MASK = BUTTON_PRESSED.getMaskValue();
        public static final int BUTTON_RELEASED_MASK = BUTTON_RELEASED.getMaskValue();
        public static final int MOUSE_MOVED_MASK = MOUSE_MOVED.getMaskValue();
        public static final int MOUSE_STOPPED_MASK = MOUSE_STOPPED.getMaskValue();
        public static final int MOUSE_WHEEL_MOVED_MASK = MOUSE_WHEEL_MOVED_UP.getMaskValue() | MOUSE_WHEEL_MOVED_DOWN.getMaskValue();
        
        private final int maskValue;
        
        public final int getMaskValue()
        {
            return ( maskValue );
        }
        
        private HUDPickReason( int maskValue )
        {
            this.maskValue = maskValue;
        }
    }
    
    private static final HUDPickResultPool POOL = new HUDPickResultPool( 128 );
    
    private HUDPickReason pickReason = null;
    private MouseButton button = null;
    private Widget widget = null;
    private Tuple2f absPos = new Point2f();
    private Tuple2f relPos = new Point2f();
    private HUDPickResult subResult = null;
    private Cursor.Type inheritedCursorType = null;
    
    /**
     * @return the picked Widget instance
     */
    public final Widget getWidget()
    {
        return ( widget );
    }
    
    /**
     * @return the absolute position of the picking
     */
    public final Tuple2f getAbsPos()
    {
        return ( absPos );
    }
    
    /**
     * @return the relative position of the picking
     * (relative to the (0, 0) position of the Widget.
     * For rectangular Widgets this is the upper-left corner.)
     */
    public final Tuple2f getRelPos()
    {
        return ( relPos );
    }
    
    /**
     * @return the action which caused this picking
     */
    public final HUDPickReason getPickReason()
    {
        return ( pickReason );
    }
    
    /**
     * @return the mouse-button, that caused the picking or null, if the
     *         picking wasn't caused by a button event.
     */
    public final MouseButton getButton()
    {
        return ( button );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( HUDPickResult hpr2 )
    {
        return ( widget.compareAbsZIndex( hpr2.widget ) );
    }
    
    /**
     * Sets the HUDPickResult of the picked child Widget, if any and if this
     * is a result of a WidgetContainer picking
     */
    public void setSubResult( HUDPickResult subResult )
    {
        if ( subResult == this )
        {
            throw new Error( "A HUDPickResult cannot be set to itself as a sub-result!" );
        }
        
        this.subResult = subResult;
        
        if ( subResult != null )
            this.inheritedCursorType = subResult.getInheritedCursorType();
        else
            this.inheritedCursorType = null;
    }
    
    /**
     * @return the HUDPickResult of the picked child Widget, if any and if this
     *         is a result of a WidgetContainer picking
     */
    public HUDPickResult getSubResult()
    {
        return ( subResult );
    }
    
    /**
     * @return if this HUDPickResult doesn't have a sub-result, this is returned.
     * If it has a sub-resultm the result of subResult.getLeafResult is returned.
     * This means, that the "deepest" result is returned.
     */
    public HUDPickResult getLeafResult()
    {
        HUDPickResult pr = this;
        
        while ( pr.getSubResult() != null )
        {
            pr = pr.getSubResult();
        }
        
        return ( pr );
    }
    
    /**
     * @return if this HUDPickResult doesn't have a sub-result.
     */
    public boolean isLeafResult()
    {
        return ( subResult == null );
    }
    
    /**
     * @return the cursor type, that is inherited by the topmost sub-result.
     */
    public final Cursor.Type getInheritedCursorType()
    {
        return ( inheritedCursorType );
    }
    
    /**
     * @return a String representation of this object.
     */
    @Override
    public String toString()
    {
        return ( getClass().getName() + ": Widget(" + getWidget() + "), abs(" + absPos.getX() + ", " + absPos.getY() + "), rel(" + relPos.getX() + ", " + relPos.getY() + "), caused by " + pickReason + ", subResult(" + ( getSubResult() != null ? "yes)" : "no)" ) );
    }
    
    /**
     * Fills this WidgetPickResult.
     * 
     * @param widget the picked Widget
     * @param cursor
     * @param absPosX the absolute position of the picking
     * @param absPosY the absolute position of the picking
     * @param relPosX the relative position of the picking (relative to the (0, 0) position of the Widget. For rectangular Widgets this is the upper-left corner.)
     * @param relPosY the relative position of the picking (relative to the (0, 0) position of the Widget. For rectangular Widgets this is the upper-left corner.)
     * @param pickReason the action which caused this pick operation
     * @param button the mouse-button, that caused the picking
     */
    public HUDPickResult set( Widget widget, Cursor.Type cursor, float absPosX, float absPosY, float relPosX, float relPosY, HUDPickReason pickReason, MouseButton button )
    {
        this.widget = widget;
        this.inheritedCursorType = cursor;
        this.absPos.set( absPosX, absPosY );
        this.relPos.set( relPosX, relPosY );
        this.pickReason = pickReason;
        this.button = button;
        
        this.subResult = null;
        
        return ( this );
    }
    
    /**
     * Constructs a new WidgetPickResult.
     * 
     * @param widget the picked Widget
     * @param cursor
     * @param absPos the absolute position of the picking
     * @param relPosX the relative position of the picking (relative to the (0, 0) position of the Widget. For rectangular Widgets this is the upper-left corner.)
     * @param relPosY the relative position of the picking (relative to the (0, 0) position of the Widget. For rectangular Widgets this is the upper-left corner.)
     * @param pickReason the action which caused this pick operation
     * @param button the mouse-button, that caused the picking
     */
    public HUDPickResult( Widget widget, Cursor.Type cursor, Tuple2f absPos, float relPosX, float relPosY, HUDPickReason pickReason, MouseButton button )
    {
        this.widget = widget;
        this.absPos.set( absPos );
        this.relPos.set( relPos );
        this.pickReason = pickReason;
        this.button = button;
        
        this.subResult = null;
    }
    
    /**
     * Constructs a new WidgetPickResult.
     */
    public HUDPickResult()
    {
    }
    
    public static final HUDPickResult fromPool()
    {
        return ( POOL.alloc() );
    }
    
    public static final void toPool( HUDPickResult hpr )
    {
        /*
         * First store the subresult locally and remove it from 'hpr'
         * to avoid possible stack overflows.
         */
        
        HUDPickResult subResult = hpr.getSubResult();
        if ( subResult != null )
        {
            hpr.setSubResult( null );
            
            toPool( subResult );
        }
        
        POOL.free( hpr );
    }
}
