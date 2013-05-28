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
package org.xith3d.loaders.models.animations;

import java.util.Map;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.loaders.models._util.AnimationType;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class KeyFrameController
{
    private static final float UPDATE_DELAY = 1f / 64f;
    
    private final AnimationType animType;
    
    private final KeyFrame[] frames;
    
    private KeyFrame lastFrame = null;
    private float nextAnimTime = -Float.MAX_VALUE;
    
    public final AnimationType getAnimationType()
    {
        return ( animType );
    }
    
    public final int getNumFrames()
    {
        return ( frames.length );
    }
    
    public final KeyFrame[] getKeyFrames()
    {
        return ( frames );
    }
    
    public final KeyFrame getKeyFrame( int index )
    {
        return ( frames[index] );
    }
    
    public abstract Object getTarget();
    
    public void reset()
    {
        lastFrame = null;
        nextAnimTime = -Float.MAX_VALUE;
    }
    
    protected boolean checkUpdateConditions( boolean forced, float absAnimTime, int frame, float animDuration )
    {
        if ( !forced )
        {
            if ( getNumFrames() <= 1 )
            {
                if ( getKeyFrame( frame ) == lastFrame )
                {
                    return ( false );
                }
            }
            else if ( absAnimTime < nextAnimTime )
            {
                return ( false );
            }
        }
        
        lastFrame = getKeyFrame( frame );
        nextAnimTime = ( absAnimTime + UPDATE_DELAY ) % animDuration;
        
        return ( true );
    }
    
    protected abstract void updateTarget( float absAnimTime, int baseFrame, int nextFrame, float alpha, ModelAnimation animation );
    
    public void update( boolean forced, float absAnimTime, int baseFrame, int nextFrame, float alpha, ModelAnimation animation )
    {
        if ( checkUpdateConditions( forced, absAnimTime, ( alpha > 0.5f ) ? nextFrame : baseFrame, animation.getDuration() ) )
        {
            updateTarget( absAnimTime, baseFrame, nextFrame, alpha, animation );
        }
    }
    
    public int update( boolean forced, float absAnimTime, ModelAnimation animation )
    {
        if ( getNumFrames() == 1 )
        {
            update( forced, absAnimTime, 0, 0, 0.5f, animation );
            
            return ( 0 );
        }
        //else if ( absAnimTime >= nextAnimTime )
        {
            //nextAnimTime = ( absAnimTime + UPDATE_DELAY ) % animation.getDuration();
            
            float time = absAnimTime / animation.getDuration();
            float frameDuration = animation.getDuration() / getNumFrames();
            
            int baseFrame = (int)( getNumFrames() * time );
            int nextFrame = ( baseFrame == getNumFrames() - 1 ) ? 0 : baseFrame + 1;
            
            float frameT = ( absAnimTime % animation.getDuration() ) - ( baseFrame * frameDuration );
            float alpha = frameT / frameDuration;
            
            update( forced, absAnimTime, baseFrame, nextFrame, alpha, animation );
            
            return ( baseFrame );
        }
    }
    
    public abstract KeyFrameController sharedCopy( Map<String, NamedObject> namedObjects );
    
    public KeyFrameController( AnimationType animType, KeyFrame[] frames )
    {
        this.animType = animType;
        this.frames = frames;
    }
}
