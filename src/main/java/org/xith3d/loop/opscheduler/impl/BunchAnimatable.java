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
package org.xith3d.loop.opscheduler.impl;

import java.util.ArrayList;
import java.util.List;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Animatable;

/**
 * This is an {@link Animatable} implementation, that animates a list of
 * {@link Animatable}s at once.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class BunchAnimatable implements Animatable
{
    private final boolean persistent;
    private boolean alive = false;
    private boolean animating = false;
    
    private final ArrayList< Animatable > subAnims;
    
    private long lastGameTime = 0L;
    private TimingMode lastTimingMode = TimingMode.MICROSECONDS; // Just initialized like this to avoid NPEs!
    
    /**
     * {@inheritDoc}
     */
    public boolean isPersistent()
    {
        return ( persistent );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setAlive( boolean alive )
    {
        this.alive = alive;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isAlive()
    {
        return ( alive );
    }
    
    /**
     * {@inheritDoc}
     */
    public void startAnimation( long gameTime, TimingMode timingMode )
    {
        animating = true;
        
        for ( int i = 0; i < subAnims.size(); i++ )
        {
            subAnims.get( i ).startAnimation( gameTime, timingMode );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void stopAnimation()
    {
        this.animating = false;
        
        for ( int i = 0; i < subAnims.size(); i++ )
        {
            subAnims.get( i ).stopAnimation();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isAnimating()
    {
        return ( animating );
    }
    
    /**
     * Adds an {@link Animatable} to the list of managed items.
     * 
     * @param animatable
     */
    public final void addAnimatable( Animatable animatable )
    {
        this.subAnims.add( animatable );
        
        if ( isAnimating() )
        {
            for ( int i = 0; i < subAnims.size(); i++ )
            {
                subAnims.get( i ).startAnimation( lastGameTime, lastTimingMode );
            }
        }
    }
    
    /**
     * Removes an {@link Animatable} from the list of managed items.
     * 
     * @param animatable
     */
    public final boolean removeAnimatable( Animatable animatable )
    {
        return ( this.subAnims.remove( animatable ) );
    }
    
    /**
     * @return the list of managed items.
     */
    public final ArrayList< Animatable > getAnimatables()
    {
        return ( this.subAnims );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        lastGameTime = gameTime;
        lastTimingMode = timingMode;
        
        int i = 0;
        while ( i < subAnims.size() )
        {
            final Animatable anim = subAnims.get( i );
            
            if ( anim.isAlive() )
            {
                anim.update( gameTime, frameTime, timingMode );
                i++;
            }
            else
            {
                subAnims.remove( i );
            }
        }
    }
    
    /**
     * Creates a new BunchAnimatable.
     * 
     * @param persistent see {@link #isPersistent()}
     * @param subAnims the new list of sub-{@link Animatable}s
     */
    public BunchAnimatable( boolean persistent, List< Animatable > subAnims )
    {
        this.persistent = persistent;
        
        if ( ( subAnims != null ) && ( subAnims instanceof ArrayList ) )
        {
            this.subAnims = (ArrayList< Animatable >)subAnims;
        }
        else
        {
            this.subAnims = new ArrayList< Animatable >( subAnims.size() );
            
            for ( int i = 0; i < subAnims.size(); i++ )
            {
                this.subAnims.add( subAnims.get( i ) );
            }
        }
    }
    
    /**
     * Creates a new BunchAnimatable.
     * 
     * @param persistent see {@link #isPersistent()}
     * @param subAnims the new list of sub-{@link Animatable}s
     */
    public BunchAnimatable( boolean persistent, Animatable[] subAnims )
    {
        this.persistent = persistent;
        
        if ( subAnims == null )
        {
            this.subAnims = new ArrayList< Animatable >();
        }
        else
        {
            this.subAnims = new ArrayList< Animatable >( subAnims.length );
            
            for ( int i = 0; i < subAnims.length; i++ )
            {
                this.subAnims.add( subAnims[ i ] );
            }
        }
    }
    
    /**
     * Creates a new BunchAnimatable.
     * 
     * @param persistent see {@link #isPersistent()}
     */
    public BunchAnimatable( boolean persistent )
    {
        this( persistent, (ArrayList< Animatable >)null );
    }
    
    /**
     * Creates a new persistent BunchAnimatable.
     */
    public BunchAnimatable()
    {
        this( true, (ArrayList< Animatable >)null );
    }
}
