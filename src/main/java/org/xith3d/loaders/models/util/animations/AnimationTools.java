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
package org.xith3d.loaders.models.util.animations;

import org.jagatoo.loaders.models._util.AnimationType;
import org.openmali.FastMath;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.util.MatrixUtils;
import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.animations.KeyFrameController;
import org.xith3d.loaders.models.animations.MeshTransformKeyFrame;
import org.xith3d.loaders.models.animations.MeshTransformKeyFrameController;
import org.xith3d.loaders.models.animations.ModelAnimation;
import org.xith3d.loaders.models.animations.PrecomputedAnimationKeyFrame;
import org.xith3d.loaders.models.animations.PrecomputedAnimationKeyFrameController;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * Utility methods for model animatios.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class AnimationTools
{
    private static Geometry copyGeometry( Geometry sourceGeom )
    {
        Geometry copy = sourceGeom.cloneNodeComponent( true );
        //copy.setOptimization( org.xith3d.scenegraph.Geometry.Optimization.USE_DISPLAY_LISTS );
        
        if ( !sourceGeom.hasVertexAttributes() && !sourceGeom.isInterleaved() ) // TODO: Remove vertex-attribs-condition!
        {
            if ( sourceGeom.hasColors() )
                copy.setColorData( sourceGeom.getColorData() );
            
            if ( sourceGeom.hasTextureCoordinates() )
            {
                for ( int i = 0; i < sourceGeom.getNumTextureUnits(); i++ )
                {
                    copy.setTexCoordData( i, sourceGeom.getTexCoordsData( i ) );
                }
            }
            
            // TODO: Implement vertex-attribute buffer sharing.
            
            /*
            if ( sourceGeom.hasVertexAttributes() )
            {
                for ( int i = 0; i < sourceGeom.getVertexAttributesCount(); i++ )
                {
                    copy.setVertexAttributesData( i, sourceGeom.getVertexAttributesData( i ) );
                }
            }
            */
            
            /*
            if ( sourceGeom.hasIndex() )
            {
                ( (IndexedGeometryArray)copy ).setIn
            }
            */
        }
        
        return ( copy );
    }
    
    public static void precomputeAnimations( Model model, float fps )
    {
        // Check, if all animation controllers are suitable for bein precomputed...
        
        ModelAnimation[] animations = model.getAnimations();
        for ( int i = 0; i < animations.length; i++ )
        {
            for ( int j = 0; j < animations[i].getControllers().length; j++ )
            {
                AnimationType animType = animations[i].getControllers()[j].getAnimationType();
                if ( ( animType == AnimationType.MESH_TRANSFORM ) || ( animType == AnimationType.PRECOMPUTED ) )
                {
                    throw new Error( "Cannot precompute an animation of the type " + animType );
                }
            }
        }
        
        // Conditions are met. Now let's got for precomputation...
        
        ModelAnimation[] newAnimations = new ModelAnimation[ animations.length ];
        
        for ( int i = 0; i < animations.length; i++ )
        {
            ModelAnimation animation = animations[i];
            int numFrames = (int)( fps * animation.getDuration() );
            
            PrecomputedAnimationKeyFrameController[] controllers = new PrecomputedAnimationKeyFrameController[ animation.getControllers().length ];
            
            for ( int j = 0; j < animation.getControllers().length; j++ )
            {
                KeyFrameController sourceController = animation.getControllers()[j];
                Shape3D shape = (Shape3D)sourceController.getTarget();
                
                PrecomputedAnimationKeyFrame[] frames = new PrecomputedAnimationKeyFrame[ numFrames ];
                
                for ( int k = 0; k < numFrames; k++ )
                {
                    float absAnimTime = k * animation.getDuration() / numFrames;
                    sourceController.update( true, absAnimTime, animation );
                    
                    Matrix4f[] mountTransforms = null;
                    if ( model.getMountTransforms() != null )
                    {
                        mountTransforms = new Matrix4f[ model.getMountTransforms().length ];
                        
                        for ( int m = 0; m < mountTransforms.length; m++ )
                        {
                            model.getMountTransform( m ).getTransform().get( mountTransforms[m] );
                        }
                    }
                    
                    frames[k] = new PrecomputedAnimationKeyFrame( copyGeometry( shape.getGeometry() ) );
                }
                
                controllers[j] = new PrecomputedAnimationKeyFrameController( frames, shape );
            }
            
            newAnimations[i] = new ModelAnimation( animation.getName(), numFrames, fps, controllers );
            newAnimations[i].setMountTransformFrames( animation.getMountTransformFrames() );
        }
        
        model.setAnimations( newAnimations );
    }
    
    public static MeshTransformKeyFrameController createRotationalKeyFrameController( Vector3f axis, float startAngle, float stopAngle, boolean backwards, int numFrames, TransformGroup target )
    {
        float totalAngleDelta = stopAngle - startAngle;
        
        Matrix3f rot = Matrix3f.fromPool();
        
        MeshTransformKeyFrame[] frames = new MeshTransformKeyFrame[ numFrames ];
        
        for ( int i = 0; i < numFrames; i++ )
        {
            float time = (float)i / (float)numFrames;
            float angle = ( startAngle + ( totalAngleDelta * time ) ) % FastMath.TWO_PI;
            if ( backwards )
                angle = FastMath.TWO_PI - angle;
            
            MatrixUtils.getRotationMatrix( axis, angle, rot );
            
            Matrix4f transform = new Matrix4f( rot );
            
            frames[i] = new MeshTransformKeyFrame( time, transform );
        }
        
        Matrix3f.toPool( rot );
        
        return ( new MeshTransformKeyFrameController( frames, target ) );
    }
    
    public static MeshTransformKeyFrameController createRotationalKeyFrameController( Vector3f axis, boolean backwards, int numFrames, TransformGroup target )
    {
        return ( createRotationalKeyFrameController( axis, 0f, FastMath.TWO_PI, backwards, numFrames, target ) );
    }
}
