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
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MeshTransformKeyFrameController extends KeyFrameController
{
    private final TransformGroup tg;
    
    public final MeshTransformKeyFrame getFrame( int index )
    {
        return ( (MeshTransformKeyFrame)getKeyFrame( index ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final TransformGroup getTarget()
    {
        return ( tg );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateTarget( float absAnimTime, int baseFrameIndex, int nextFrameIndex, float alpha, ModelAnimation animation )
    {
        if ( getNumFrames() <= 1 )
        {
            // for now, if we don't have more than one frame, we're going to do nothing
            
            MeshTransformKeyFrame frame = getFrame( 0 );
            
            tg.getTransform().set( frame.getTransform() );
            tg.updateTransform();
            
            return;
        }
        
        float time = ( absAnimTime / animation.getDuration() );
        
        MeshTransformKeyFrame prevFrame = null;
        MeshTransformKeyFrame nextFrame = null;
        
        int i;
        
        for ( i = 0; i < getNumFrames(); i++ )
        {
            prevFrame = getFrame( i );
            
            if ( i < getNumFrames() - 1 )
            {
                nextFrame = getFrame( i + 1 );
                
                if ( ( time >= prevFrame.getTime() ) && ( time < nextFrame.getTime() ) )
                {
                    break;
                }
            }
            else
            {
                nextFrame = getFrame( 0 );
                break;
            }
        }
        
        float deltaTime = nextFrame.getTime() - prevFrame.getTime();
        if ( i == getNumFrames() - 1 )
        {
            deltaTime = 1 - prevFrame.getTime();
        }
        
        float delta = ( time - prevFrame.getTime() ) / deltaTime;
        
        Transform3D form = new Transform3D();
        Matrix4f m = Matrix4f.fromPool();
        m.interpolate( prevFrame.getTransform(), nextFrame.getTransform(), delta, false );
        m.m33( 1f );
        form.set( m );
        
        tg.setTransform( form );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MeshTransformKeyFrameController sharedCopy( Map<String, NamedObject> namedObjects )
    {
        String tgName = this.tg.getName();
        TransformGroup newTG = (TransformGroup)namedObjects.get( tgName );
        if ( newTG == null )
            throw new Error( "Can't clone this AnimationController!" );
        
        return ( new MeshTransformKeyFrameController( (MeshTransformKeyFrame[])this.getKeyFrames(), newTG ) );
    }
    
    public MeshTransformKeyFrameController( MeshTransformKeyFrame[] frames, TransformGroup tg )
    {
        super( AnimationType.MESH_TRANSFORM, frames );
        
        this.tg = tg;
    }
}
