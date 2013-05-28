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
package org.xith3d.loaders.models.conversion;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.loaders.models._util.AnimationFactory;
import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.models.animations.Bone;
import org.xith3d.loaders.models.animations.BoneAnimationKeyFrame;
import org.xith3d.loaders.models.animations.BoneAnimationKeyFrameController;
import org.xith3d.loaders.models.animations.BoneWeight;
import org.xith3d.loaders.models.animations.KeyFrameController;
import org.xith3d.loaders.models.animations.MeshDeformationKeyFrame;
import org.xith3d.loaders.models.animations.MeshDeformationKeyFrameController;
import org.xith3d.loaders.models.animations.MeshTransformKeyFrame;
import org.xith3d.loaders.models.animations.MeshTransformKeyFrameController;
import org.xith3d.loaders.models.animations.ModelAnimation;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class XithAnimationFactory implements AnimationFactory
{
    public final NamedObject createBone( NamedObject parentBone, String name, Vector3f translation, Quaternion4f rotation, Tuple3f scale )
    {
        return ( new Bone( (Bone)parentBone, name, translation, rotation, scale ) );
    }
    
    public final NamedObject createBone( NamedObject parentBone, String name, Matrix4f transformation )
    {
        return ( new Bone( (Bone)parentBone, name, transformation ) );
    }
    
    public final Object createBoneAnimationKeyFrame( NamedObject[] bones )
    {
        return ( new BoneAnimationKeyFrame( (Bone[])bones ) );
    }
    
    public final Object createBoneWeight( int boneIndex, float weight, Vector3f offset )
    {
        return ( new BoneWeight( boneIndex, weight, offset ) );
    }
    
    public final Object createBoneAnimationKeyFrameController( Object[] keyFrames, Object[][] boneWeights, NamedObject target )
    {
        return ( new BoneAnimationKeyFrameController( (BoneAnimationKeyFrame[])keyFrames, (BoneWeight[][])boneWeights, (Shape3D)target ) );
    }
    
    
    
    public final Object createMeshDeformationKeyFrame( float[] coords, float[] normals )
    {
        return ( new MeshDeformationKeyFrame( coords, normals ) );
    }
    
    public final Object createMeshDeformationKeyFrameController( Object[] keyFrames, NamedObject target )
    {
        return ( new MeshDeformationKeyFrameController( (MeshDeformationKeyFrame[])keyFrames, (Shape3D)target ) );
    }
    
    
    
    public final Object createMeshTransformKeyFrame( float time, Vector3f translation, Quaternion4f rotation, Tuple3f scale )
    {
        Matrix4f transform = new Matrix4f();
        Matrix4f tmp = Matrix4f.fromPool();
        
        transform.set( translation );
        
        tmp.set( rotation );
        transform.mul( tmp );
        
        tmp.setIdentity();
        tmp.m00( scale.getX() );
        tmp.m11( scale.getY() );
        tmp.m22( scale.getZ() );
        transform.mul( tmp );
        
        Matrix4f.toPool( tmp );
        
        return ( new MeshTransformKeyFrame( time, transform ) );
    }
    
    public final Object createMeshTransformKeyFrame( float time, Vector3f translation, AxisAngle3f rotation, Tuple3f scale )
    {
        Matrix4f transform = new Matrix4f();
        Matrix4f tmp = Matrix4f.fromPool();
        
        transform.set( translation );
        
        tmp.set( rotation );
        transform.mul( tmp );
        
        tmp.setIdentity();
        tmp.m00( scale.getX() );
        tmp.m11( scale.getY() );
        tmp.m22( scale.getZ() );
        transform.mul( tmp );
        
        Matrix4f.toPool( tmp );
        
        return ( new MeshTransformKeyFrame( time, transform ) );
    }
    
    public final Object createMeshTransformKeyFrame( float time, Vector3f translation, Matrix3f rotation, Tuple3f scale )
    {
        Matrix4f transform = new Matrix4f();
        Matrix4f tmp = Matrix4f.fromPool();
        
        transform.set( translation );
        
        tmp.set( rotation );
        transform.mul( tmp );
        
        tmp.setIdentity();
        tmp.m00( scale.getX() );
        tmp.m11( scale.getY() );
        tmp.m22( scale.getZ() );
        transform.mul( tmp );
        
        Matrix4f.toPool( tmp );
        
        return ( new MeshTransformKeyFrame( time, transform ) );
    }
    
    public final Object createMeshTransformKeyFrame( float time, Matrix4f transform )
    {
        return ( new MeshTransformKeyFrame( time, transform ) );
    }
    
    public void transformMeshTransformKeyFrame( Matrix4f transform, Object frameObj )
    {
        MeshTransformKeyFrame frame = (MeshTransformKeyFrame)frameObj;
        
        frame.getTransform().mul( transform, frame.getTransform() );
    }
    
    public final void transformMeshTransformKeyFrames( Matrix4f transform, Object[] frames )
    {
        for ( int i = 0; i < frames.length; i++ )
        {
            MeshTransformKeyFrame frame = (MeshTransformKeyFrame)frames[i];
            
            frame.getTransform().mul( transform, frame.getTransform() );
        }
    }
    
    public final Object createMeshTransformKeyFrameController( Object[] keyFrames, NamedObject target )
    {
        return ( new MeshTransformKeyFrameController( (MeshTransformKeyFrame[])keyFrames, (TransformGroup)target ) );
    }
    
    public Object createAnimation( String name, int numFrames, float fps, Object[] controllers, Matrix4f[][] mountTransformFrames )
    {
        ModelAnimation animation = new ModelAnimation( name, numFrames, fps, (KeyFrameController[])controllers );
        
        animation.setMountTransformFrames( mountTransformFrames );
        
        return ( animation );
    }
}
