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

import org.openmali.vecmath2.Tuple2f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Interval;
import org.xith3d.loop.opscheduler.IntervalListener;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.WidgetMouseAdapter;

/**
 * A MouseHoverWidgetMover interpolatedly moves a Widget from its current
 * location to a destination location when the mouse hovers in and out.
 * 
 * @author hawkwind
 * @author Marvin Froehlich (aka Qudus)
 */
public class MouseHoverWidgetMover extends WidgetMover
{
    private class InputSlave extends WidgetMouseAdapter implements IntervalListener
    {
        private boolean intervalListenerOnceSet = false;
        
        @Override
        public void onMouseEntered( Widget widget, boolean isTopMost, boolean hasFocus )
        {
            if ( !intervalListenerOnceSet )
            {
                widget.getHUD().getOperationScheduler().addIntervalListener( this );
                intervalListenerOnceSet = true;
            }
            
            MouseHoverWidgetMover.this.onMouseEntered();
        }
        
        @Override
        public void onMouseExited( Widget widget, boolean isTopMost, boolean hasFocus )
        {
            if ( !intervalListenerOnceSet )
            {
                widget.getHUD().getOperationScheduler().addIntervalListener( this );
                intervalListenerOnceSet = true;
            }
            
            MouseHoverWidgetMover.this.onMouseLeft();
        }
        
        public void onIntervalHit( Interval interval, long gameTime, long frameTime, TimingMode timingMode )
        {
            if ( interval == enteredDelayInterval )
            {
                interval.kill();
                startMoving();
            }
            else if ( interval == leftDelayInterval )
            {
                interval.kill();
                startMoving();
            }
        }
    }
    
    private InputSlave inputSlave = new InputSlave();
    private Interval enteredDelayInterval = null;
    private Interval leftDelayInterval = null;
    private boolean isBlocked = false;
    
    /**
     * Sets the MouseHoverWidgetMover blocked or unblocked.
     * If blocked, any mouse input is ignored.
     * 
     * @param blocked
     */
    public void setBlocked( boolean blocked )
    {
        this.isBlocked = blocked;
    }
    
    /**
     * If blocked, any mouse input is ignored.
     * 
     * @return if the MouseHoverWidgetMover is blocked or unblocked
     */
    public boolean isBlocked()
    {
        return ( isBlocked );
    }
    
    /**
     * Sets the delay for the onMouseEntered event.
     * 
     * @param delay
     */
    public void setMouseEnteredDelay( long delay )
    {
        if ( delay <= 0L )
        {
            enteredDelayInterval = null;
            return;
        }
        
        if ( enteredDelayInterval == null )
            enteredDelayInterval = new Interval( delay );
        else
            enteredDelayInterval.setInterval( delay );
        
        enteredDelayInterval.kill();
    }
    
    /**
     * @return the delay for the onMouseEntered event
     */
    public long getMouseEnteredDelay()
    {
        if ( enteredDelayInterval == null )
            return ( 0L );
        
        return ( enteredDelayInterval.getInterval() );
    }
    
    /**
     * Sets the delay for the onMouseLeft event.
     * 
     * @param delay
     */
    public void setMouseLeftDelay( long delay )
    {
        if ( delay <= 0L )
        {
            leftDelayInterval = null;
            return;
        }
        
        if ( leftDelayInterval == null )
            leftDelayInterval = new Interval( delay );
        else
            leftDelayInterval.setInterval( delay );
        
        leftDelayInterval.kill();
    }
    
    /**
     * @return the delay for the onMouseLeft event
     */
    public long getMouseLeftDelay()
    {
        if ( leftDelayInterval == null )
            return ( 0L );
        
        return ( leftDelayInterval.getInterval() );
    }
    
    /**
     * This method is invoked, when the mouse entered the Widget's area.
     */
    protected void onMouseEntered()
    {
        if ( isBlocked() )
            return;
        
        if ( ( leftDelayInterval != null ) && ( leftDelayInterval.isAlive() ) )
        {
            leftDelayInterval.kill();
            return;
        }
        
        if ( isMoving() )
            invert();
        else if ( enteredDelayInterval != null )
            getWidget().getHUD().getOperationScheduler().addInterval( enteredDelayInterval );
        else
            startMoving();
    }
    
    /**
     * This method is invoked, when the mouse left the Widget's area.
     */
    protected void onMouseLeft()
    {
        if ( isBlocked() )
            return;
        
        if ( ( enteredDelayInterval != null ) && ( enteredDelayInterval.isAlive() ) )
        {
            enteredDelayInterval.kill();
            return;
        }
        
        if ( isMoving() )
            invert();
        else if ( leftDelayInterval != null )
            getWidget().getHUD().getOperationScheduler().addInterval( leftDelayInterval );
        else
            startMoving();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMovementStopped()
    {
        super.onMovementStopped();
        
        setDestinationLocation( getStartLocationX(), getStartLocationY() );
    }
    
    /**
     * Creates a new MouseHoverWidgetMover for the given Widget and destination location.
     * 
     * @param widget the Widget to be moved
     * @param destX the destination location x-coordinate
     * @param destY the destination location y-coordinate
     * @param speed the speed for the movement (in units per second)
     */
    public MouseHoverWidgetMover( Widget widget, float destX, float destY, float speed )
    {
        super( widget, destX, destY, speed );
        
        widget.addMouseListener( inputSlave );
    }
    
    /**
     * Creates a new MouseHoverWidgetMover for the given Widget and destination location.
     * 
     * @param widget the Widget to be moved
     * @param destLocation the destination location
     * @param speed the speed for the movement (in units per second)
     */
    public MouseHoverWidgetMover( Widget widget, Tuple2f destLocation, float speed )
    {
        this( widget, destLocation.getX(), destLocation.getY(), speed );
    }
    
    /**
     * Creates a new MouseHoverWidgetMover for the given Widget and destination location.
     * 
     * @param widget the Widget to be moved
     */
    public MouseHoverWidgetMover( Widget widget )
    {
        this( widget, widget.getLeft(), widget.getTop(), 0.0f );
    }
}
