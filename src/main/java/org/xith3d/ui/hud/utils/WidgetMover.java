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

import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Vector2f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.ui.hud.base.Widget;

/**
 * A WidgetMover interpolatedly moves a Widget from its current location to a
 * destination location.
 * 
 * @author hawkwind
 * @author Marvin Froehlich (aka Qudus)
 */
public class WidgetMover //implements WidgetWrapper
{
    private class Mover extends ScheduledOperationImpl
    {
        private long startTime;
        private Vector2f pathTotal = new Vector2f();
        private float pathTotalLength;
        private Vector2f pathNorm = new Vector2f();
        private Vector2f path = new Vector2f();
        private boolean isSuspended;
        
        public void update( long gameTime, long frameTime, TimingMode timingMode )
        {
            if ( isSuspended )
                return;
            
            if ( startTime >= 0L )
            {
                final float dt = timingMode.getSecondsAsFloat( gameTime - startTime );
                path.scale( speed * dt, pathNorm );
                if ( path.length() < pathTotalLength )
                {
                    widget.setLocation( startX + path.getX(), startY + path.getY() );
                }
                else
                {
                    this.setAlive( false );
                    widget.setLocation( destX, destY );
                    onMovementStopped();
                }
            }
            else
            {
                startTime = gameTime;
            }
        }
        
        public void startMoving()
        {
            startX = widget.getLeft();
            startY = widget.getTop();
            pathTotal.set( ( destX - startX ), ( destY - startY ) );
            pathTotalLength = pathTotal.length();
            pathNorm.normalize( pathTotal );
            path.set( 0.0f, 0.0f );
            
            startTime = -1L;
            isSuspended = false;
            this.setAlive( true );
            widget.getHUD().getOperationScheduler().scheduleOperation( this );
        }
        
        public void setSuspended( boolean suspended )
        {
            this.isSuspended = suspended;
        }
        
        public Mover()
        {
            super( true );
            this.setAlive( false );
        }
    }
    
    private Widget widget;
    private float startX = -1.0f, startY = -1.0f;
    private float destX, destY;
    private float speed;
    private Mover mover = new Mover();
    
    /**
     * {@inheritDoc}
     */
    public Widget getWidget()
    {
        return ( widget );
    }
    
    /**
     * @return the Widget's start location of the current the movement.
     *         The returned values are invalid, if the Widget isn't currently moving.
     */
    public Tuple2f getStartLocation()
    {
        return ( new Point2f( startX, startY ) );
    }
    
    /**
     * @return the Widget's start x-location of the current the movement.
     *         The returned value is invalid, if the Widget isn't currently moving.
     */
    public float getStartLocationX()
    {
        return ( startX );
    }
    
    /**
     * @return the Widget's start y-location of the current the movement.
     *         The returned value is invalid, if the Widget isn't currently moving.
     */
    public float getStartLocationY()
    {
        return ( startY );
    }
    
    /**
     * Sets the Widget's destination location for the movement.
     * 
     * @param destLocation
     */
    public void setDestinationLocation( Tuple2f destLocation )
    {
        this.destX = destLocation.getX();
        this.destY = destLocation.getY();
    }
    
    /**
     * Sets the Widget's destination location for the movement.
     * 
     * @param destLocationX
     * @param destLocationY
     */
    public void setDestinationLocation( float destLocationX, float destLocationY )
    {
        this.destX = destLocationX;
        this.destY = destLocationY;
    }
    
    /**
     * @return the Widget's destination location for the movement
     */
    public Tuple2f getDestinationLocation()
    {
        return ( new Point2f( destX, destY ) );
    }
    
    /**
     * @return the Widget's destination x-location for the movement
     */
    public float getDestinationLocationX()
    {
        return ( destX );
    }
    
    /**
     * @return the Widget's destination y-location for the movement
     */
    public float getDestinationLocationY()
    {
        return ( destY );
    }
    
    /**
     * Sets the speed for the movement (in units per second).
     * 
     * @param speed
     */
    public void setSpeed( float speed )
    {
        this.speed = speed;
    }
    
    /**
     * @return the speed for the movement (in units per second)
     */
    public float getSpeed()
    {
        return ( speed );
    }
    
    /**
     * Starts the movement from the Widget's current location to the
     * destination location.
     */
    public void startMoving()
    {
        mover.startMoving();
    }
    
    /**
     * @return true, if the WidgetMover is currently active
     */
    public boolean isMoving()
    {
        return ( mover.isAlive() );
    }
    
    /**
     * This method is invoked when the Widget has reached its destination
     * location.
     */
    protected void onMovementStopped()
    {
    }
    
    public void invert()
    {
        if ( !isMoving() )
            return;
        
        mover.setSuspended( true );
        
        // swap start and dest locations
        final float sx = startX;
        final float sy = startY;
        startX = destX;
        startY = destY;
        destX = sx;
        destY = sy;
        
        // invert path vectors and shift startTime to match the current segment length
        final long tt = (long)( ( mover.pathTotalLength / getSpeed() ) * 1000.0f );
        final long t = (long)( ( mover.path.length() / getSpeed() ) * 1000.0f );
        mover.pathTotal.negate();
        mover.pathNorm.negate();
        mover.startTime = mover.startTime + ( 2 * t ) - tt;
        
        mover.setSuspended( false );
    }
    
    /**
     * Creates a new WidgetMover for the given Widget and destination location.
     * 
     * @param widget the Widget to be moved
     * @param destX the destination location x-coordinate
     * @param destY the destination location y-coordinate
     * @param speed the speed for the movement (in units per second)
     */
    public WidgetMover( Widget widget, float destX, float destY, float speed )
    {
        this.widget = widget;
        
        this.destX = destX;
        this.destY = destY;
        this.speed = speed;
    }
    
    /**
     * Creates a new WidgetMover for the given Widget and destination location.
     * 
     * @param widget the Widget to be moved
     * @param destLocation the destination location
     * @param speed the speed for the movement (in units per second)
     */
    public WidgetMover( Widget widget, Tuple2f destLocation, float speed )
    {
        this( widget, destLocation.getX(), destLocation.getY(), speed );
    }
    
    /**
     * Creates a new WidgetMover for the given Widget and destination location.
     * 
     * @param widget the Widget to be moved
     */
    public WidgetMover( Widget widget )
    {
        this( widget, widget.getLeft(), widget.getTop(), 0.0f );
    }
}
