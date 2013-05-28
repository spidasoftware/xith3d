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
import org.jagatoo.util.arrays.ArrayUtils;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.loaders.models.Model;
import org.xith3d.scenegraph.TransformGroup;

/**
 * This is an abstraction for animations of loaded Models.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ModelAnimation
{
    private Model model = null;
    
    private final Object animPrototype;
    
    private KeyFrameController[] controllers = null;
    
    private Matrix4f[][] mountTransformFrames = null;
    
    private final String name;
    private final int numFrames;
    private float fps;
    private float frameDuration;
    private float duration;
    
    private float lastAnimTime = 0f;
    
    private ModelAnimation[] attachedAnimations = null;
    
    void setModel( Model model )
    {
        this.model = model;
    }
    
    public final Model getModel()
    {
        return ( model );
    }
    
    public final KeyFrameController[] getControllers()
    {
        return ( controllers );
    }
    
    public void setMountTransformFrames( Matrix4f[][] mountTransformFrames )
    {
        this.mountTransformFrames = mountTransformFrames;
    }
    
    public final Matrix4f[][] getMountTransformFrames()
    {
        return ( mountTransformFrames );
    }
    
    public final Matrix4f[] getMountTransformFrame( int index )
    {
        if ( mountTransformFrames == null )
            return ( null );
        
        return ( mountTransformFrames[index] );
    }
    
    @Deprecated
    public Object getPrototype()
    {
        return ( animPrototype );
    }
    
    public final String getName()
    {
        return ( name );
    }
    
    public final int getNumFrames()
    {
        return ( numFrames );
    }
    
    public void setFPS( float fps )
    {
        this.fps = fps;
        this.frameDuration = 1f / fps;
        this.duration = (float)numFrames / fps;
    }
    
    public final float getFPS()
    {
        return ( fps );
    }
    
    public final float getFrameDuration()
    {
        return ( frameDuration );
    }
    
    public final float getDuration()
    {
        return ( duration );
    }
    
    public void attachAnimation( ModelAnimation anim )
    {
        if ( attachedAnimations == null )
        {
            attachedAnimations = new ModelAnimation[] { anim };
        }
        else
        {
            ModelAnimation[] attachedAnimations2 = new ModelAnimation[attachedAnimations.length + 1];
            System.arraycopy( attachedAnimations, 0, attachedAnimations2, 0, attachedAnimations.length );
            
            attachedAnimations2[attachedAnimations2.length - 1] = anim;
            
            attachedAnimations = attachedAnimations2;
        }
    }
    
    public void detachAnimation( ModelAnimation anim )
    {
        if ( attachedAnimations == null )
            return;
        
        int index = ArrayUtils.indexOf( attachedAnimations, anim, true );
        if ( index < 0 )
            return;
        
        if ( attachedAnimations.length == 1 )
        {
            attachedAnimations = null;
            return;
        }
        
        ModelAnimation[] attachedAnimations2 = new ModelAnimation[attachedAnimations.length - 1];
        System.arraycopy( attachedAnimations, 0, attachedAnimations2, 0, index );
        System.arraycopy( attachedAnimations, index + 1, attachedAnimations2, index, attachedAnimations.length - index - 1 );
        
        attachedAnimations = attachedAnimations2;
    }
    
    protected ModelAnimation[] getAttachedAnimations()
    {
        return ( attachedAnimations );
    }
    
    public void reset()
    {
        lastAnimTime = 0f;
        
        if ( controllers != null )
        {
            for ( int i = 0; i < controllers.length; i++ )
            {
                controllers[i].reset();
            }
        }
    }
    
    protected void updateMountTransforms( int baseFrame, int nextFrame, float alpha, TransformGroup[] mountTransforms )
    {
        if ( mountTransforms != null )
        {
            Matrix4f m = Matrix4f.fromPool();
            
            for ( int i = 0; i < mountTransforms.length; i++ )
            {
                if ( mountTransforms[i].numChildren() > 0 )
                {
                    Matrix4f m0 = getMountTransformFrame( baseFrame )[i];
                    Matrix4f m1 = getMountTransformFrame( nextFrame )[i];
                    
                    if ( ( m0 != null ) && ( m1 != null ) )
                    {
                        m.interpolate( m0, m1, alpha );
                        
                        mountTransforms[i].getTransform().set( m );
                        mountTransforms[i].updateTransform();
                    }
                }
            }
            
            Matrix4f.toPool( m );
        }
    }
    
    /**
     * 
     * @param forced
     * @param absAnimTime
     * @param baseFrame
     * @param nextFrame
     * @param alpha
     */
    protected void updateAttachedAnimations( boolean forced, float absAnimTime, int baseFrame, int nextFrame, float alpha )
    {
        if ( attachedAnimations != null )
        {
            for ( int i = 0; i < attachedAnimations.length; i++ )
            {
                ModelAnimation attachedAnimation = attachedAnimations[i];
                
                float absAnimTime2 = absAnimTime * attachedAnimation.getDuration() / getDuration();
                
                /*
                int baseFrame2 = baseFrame;
                
                if ( attachedAnimation.getControllers() != null )
                {
                    KeyFrameController[] attachedAnimControllers = attachedAnimation.getControllers();
                    for ( int j = 0; j < attachedAnimControllers.length; j++ )
                    {
                        KeyFrameController attachedAnimController = attachedAnimControllers[j];
                        
                        if ( attachedAnimController.getNumFrames() == 1 )
                        {
                            attachedAnimController.update( forced, absAnimTime2, 0, 0, 0.5f, this );
                            
                            if ( j == 0 )
                                baseFrame2 = 0;
                        }
                        if ( attachedAnimController.getNumFrames() == this.getNumFrames() )
                        {
                            attachedAnimController.update( forced, absAnimTime, baseFrame, nextFrame, alpha, attachedAnimation );
                        }
                        else
                        {
                            if ( j == 0 )
                                baseFrame2 = attachedAnimController.update( forced, absAnimTime2, attachedAnimation );
                            else
                                attachedAnimController.update( forced, absAnimTime2, attachedAnimation );
                        }
                    }
                }
                
                int nextFrame2 = ( baseFrame2 + 1 ) % attachedAnimation.getNumFrames();
                
                attachedAnimation.updateMountTransforms( baseFrame2, nextFrame2, alpha, attachedAnimation.getModel().getMountTransforms() );
                
                attachedAnimation.updateAttachedAnimations( forced, absAnimTime, baseFrame2, nextFrame2, alpha );
                */
                
                attachedAnimation.update( forced, absAnimTime2, attachedAnimation.getModel().getMountTransforms() );
            }
        }
    }
    
    public boolean update( boolean forced, float absAnimTime, TransformGroup[] mountTransforms )
    {
        float normedAbsAnimTime = absAnimTime % getDuration();
        
        float time = normedAbsAnimTime / getDuration();
        float frameDuration = getDuration() / getNumFrames();
        
        int baseFrame = (int)( getNumFrames() * time );
        int nextFrame = ( baseFrame + 1 ) % getNumFrames();
        
        float frameT = ( normedAbsAnimTime % getDuration() ) - ( baseFrame * frameDuration );
        float alpha = frameT / frameDuration;
        
        if ( controllers != null )
        {
            for ( int i = 0; i < controllers.length; i++ )
            {
                KeyFrameController controller = controllers[i];
                
                if ( controller.getNumFrames() == 1 )
                {
                    controller.update( forced, normedAbsAnimTime, 0, 0, 0.5f, this );
                }
                else
                {
                    if ( controller.getNumFrames() == this.getNumFrames() )
                        controller.update( forced, normedAbsAnimTime, baseFrame, nextFrame, alpha, this );
                    else
                        controllers[i].update( forced, normedAbsAnimTime, this );
                }
            }
        }
        
        updateMountTransforms( baseFrame, nextFrame, alpha, mountTransforms );
        
        updateAttachedAnimations( forced, normedAbsAnimTime, baseFrame, nextFrame, alpha );
        
        lastAnimTime = absAnimTime;
        
        if ( (int)( lastAnimTime / getDuration() ) < (int)( absAnimTime / getDuration() ) )
        {
            return ( getNumFrames() != 1 );
        }
        
        return ( false );
    }
    
    public ModelAnimation getSharedCopy( Map<String, NamedObject> namedObjects )
    {
        KeyFrameController[] newAnimControllers = null;
        
        if ( controllers != null )
        {
            newAnimControllers = new KeyFrameController[ controllers.length ];
            
            for ( int i = 0; i < controllers.length; i++ )
            {
                newAnimControllers[i] = controllers[i].sharedCopy( namedObjects );
            }
        }
        
        return ( new ModelAnimation( getName(), getNumFrames(), getFPS(), newAnimControllers, getPrototype() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( "ModelAnimation{ name = \"" + getName() + "\", " +
                "numFrames = " + getNumFrames() + ", " +
                "fps = " + getFPS() + ", " +
                "duration = " + getDuration() +
                " }"
              );
    }
    
    public ModelAnimation( String name, int numFrames, float fps, KeyFrameController[] controllers )
    {
        this.name = name;
        this.numFrames = numFrames;
        
        this.controllers = controllers;
        
        this.animPrototype = null;
        
        setFPS( fps );
    }
    
    @Deprecated
    public ModelAnimation( String name, int numFrames, float fps, KeyFrameController[] controllers, Object animPrototype )
    {
        this.name = name;
        this.numFrames = numFrames;
        
        this.controllers = controllers;
        
        this.animPrototype = animPrototype;
        
        setFPS( fps );
    }
}
