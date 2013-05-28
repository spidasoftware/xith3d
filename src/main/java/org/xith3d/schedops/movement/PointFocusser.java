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
package org.xith3d.schedops.movement;

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.scenegraph.View;

/**
 * The PointFocusser moves the camera smoothly to a certain point and facing
 * direction with a given speed.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class PointFocusser extends ScheduledOperationImpl
{
    public static interface FinishListener
    {
        public void onFocusserArrived( long gameTime, TimingMode timingMode );
    }
    
    private Vector3f up = new Vector3f( 0.0f, 1.0f, 0.0f );
    
    private float startGameTime = -1f;
    
    private View view;
    
    private float speed;
    private float duration;
    
    private Point3f point0;
    private Vector3f direction0;
    
    private Point3f point;
    private Vector3f direction;
    
    private Point3f point1;
    private Vector3f direction1;
    
    private float length;
    
    private FinishListener finishListener = null;
    
    public final void setUpVector( Vector3f up )
    {
        if ( up == null )
            throw new NullPointerException( "up vector must not be null." );
        
        this.up = up;
    }
    
    public final Vector3f getUpVector()
    {
        return ( up );
    }
    
    public void setPoint( Point3f point )
    {
        this.point1 = point;
    }
    
    public Point3f getPoint()
    {
        return ( point1 );
    }
    
    public void setDirection( Vector3f direction )
    {
        this.direction1 = direction;
    }
    
    public Vector3f getDirection()
    {
        return ( direction1 );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        final float seconds = timingMode.getSecondsAsFloat( gameTime );
        
        if ( startGameTime < 0f )
        {
            startGameTime = seconds;
            
            this.point0 = new Point3f( view.getPosition() );
            this.direction0 = new Vector3f( view.getFacingDirection() );
            this.point = new Point3f();
            this.direction = new Vector3f();
            
            this.length = point0.distance( point1 );
            this.duration = length / speed;
        }
        else
        {
            final float alpha = ( seconds - startGameTime ) / duration;
            
            this.point.interpolate( point0, point1, alpha );
            this.direction.interpolate( direction0, direction1, alpha );
            
            if ( point.distance( point0 ) >= length )
            {
                this.setAlive( false );
                if ( this.finishListener != null )
                    this.finishListener.onFocusserArrived( gameTime, timingMode );
                return;
            }
            
            direction.add( point );
            view.lookAt( point, direction, up );
        }
    }
    
    public void setFinishListener( FinishListener finishListener )
    {
        this.finishListener = finishListener;
    }
    
    /**
     * Creates a new instance.
     * 
     * @param view the View to manipulate
     * @param point the target position
     * @param direction the target facing direction
     * @param speed the speed (units per second)
     * @param finishLsitener the listener to be notified when the point has been reached
     */
    public PointFocusser( View view, Point3f point, Vector3f direction, float speed, FinishListener finishLsitener )
    {
        super( true );
        
        this.view = view;
        
        this.point1 = point;
        this.direction1 = direction;
        
        this.speed = speed;
        
        this.finishListener = finishLsitener;
    }
    
    /**
     * Creates a new instance.
     * 
     * @param view the View to manipulate
     * @param point the target position
     * @param direction the target facing direction
     * @param speed the speed (units per second)
     */
    public PointFocusser( View view, Point3f point, Vector3f direction, float speed )
    {
        this( view, point, direction, speed, null );
    }
}
