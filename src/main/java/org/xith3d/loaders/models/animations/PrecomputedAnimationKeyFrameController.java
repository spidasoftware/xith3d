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
import org.xith3d.scenegraph.Shape3D;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class PrecomputedAnimationKeyFrameController extends KeyFrameController
{
    private int lastFrame = -1;
    
    private final Shape3D shape;
    
    public final PrecomputedAnimationKeyFrame getFrame( int index )
    {
        return ( (PrecomputedAnimationKeyFrame)getKeyFrame( index ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Shape3D getTarget()
    {
        return ( shape );
    }
    
    
    /**
     * TODO: This won't be necessary anymore, when we're finally using skeleton interpolation!
     * 
     * {@inheritDoc}
     */
    @Override
    public void reset()
    {
        lastFrame = -1;
    }
    
    /**
     * TODO: This won't be necessary anymore, when we're finally using skeleton interpolation!
     * 
     * {@inheritDoc}
     */
    @Override
    protected boolean checkUpdateConditions( boolean forced, float absAnimTime, int frame, float animDuration )
    {
        if ( !forced )
        {
            if ( frame == lastFrame )
            {
                return ( false );
            }
        }
        
        lastFrame = frame;
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTarget( float absAnimTime, int baseFrame, int nextFrame, float alpha, ModelAnimation animation )
    {
        int frame = ( alpha < 0.5f ) ? baseFrame : nextFrame;
        
        shape.setGeometry( getFrame( frame ).getGeometry() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PrecomputedAnimationKeyFrameController sharedCopy( Map<String, NamedObject> namedObjects )
    {
        /*
        Shape3D[] newShapes = new Shape3D[ shapes.length ];
        
        for ( int i = 0; i < shapes.length; i++ )
        {
            String shapeName = this.shapes[i].getName();
            Shape3D newShape = (Shape3D)namedObjects.get( shapeName );
            if ( newShape == null )
                throw new Error( "Can't clone this AnimationController!" );
            newShapes[i] = newShape;
        }
        
        return ( new PrecomputedAnimationKeyFrameController( (PrecomputedAnimationKeyFrame[])this.getKeyFrames(), newShapes ) );
        */
        String shapeName = this.shape.getName();
        Shape3D newShape = (Shape3D)namedObjects.get( shapeName );
        if ( newShape == null )
            throw new Error( "Can't clone this AnimationController!" );
        
        return ( new PrecomputedAnimationKeyFrameController( (PrecomputedAnimationKeyFrame[])this.getKeyFrames(), newShape ) );
    }
    
    public PrecomputedAnimationKeyFrameController( PrecomputedAnimationKeyFrame[] frames, Shape3D shape )
    {
        super( AnimationType.PRECOMPUTED, frames );
        
        this.shape = shape;
    }
}
