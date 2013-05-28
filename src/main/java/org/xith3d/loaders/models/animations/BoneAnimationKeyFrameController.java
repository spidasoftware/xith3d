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
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.util.FloatUtils;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedGeometryArray;
import org.xith3d.scenegraph.Shape3D;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class BoneAnimationKeyFrameController extends KeyFrameController
{
    private int lastFrame = -1;
    
    private final Shape3D shape;
    private final float[] coords;
    private final float[] normals;
    
    private final BoneWeight[][] weights;
    
    public final BoneAnimationKeyFrame getFrame( int index )
    {
        return ( (BoneAnimationKeyFrame)getKeyFrame( index ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Shape3D getTarget()
    {
        return ( shape );
    }
    
    private void computeNormals( Geometry geom )
    {
        int i3;
        
        int a, b, c;
        
        float vecACx;
        float vecACy;
        float vecACz;
        float vecABx;
        float vecABy;
        float vecABz;
        
        Vector3f tmp = new Vector3f();
        
        //geom.getCoordinates( 0, coords );
        int[] index = ( (IndexedGeometryArray)geom ).getIndex();
        int numTriangles = index.length / 3;
        
        // Go though all of the faces of this object
        for ( int i = 0; i < numTriangles; i++ )
        {
            i3 = i * 3;
            
            a = index[i3 + 0] * 3;
            b = index[i3 + 1] * 3;
            c = index[i3 + 2] * 3;
            
            vecACx = coords[c + 0] - coords[a + 0];
            vecACy = coords[c + 1] - coords[a + 1];
            vecACz = coords[c + 2] - coords[a + 2];
            vecABx = coords[b + 0] - coords[a + 0];
            vecABy = coords[b + 1] - coords[a + 1];
            vecABz = coords[b + 2] - coords[a + 2];
            
            FloatUtils.cross( vecACx, vecACy, vecACz, vecABx, vecABy, vecABz, tmp );
            tmp.normalize();
            
            normals[index[i3 + 0] * 3 + 0] = tmp.getX();
            normals[index[i3 + 0] * 3 + 1] = tmp.getY();
            normals[index[i3 + 0] * 3 + 2] = tmp.getZ();
            normals[index[i3 + 1] * 3 + 0] = tmp.getX();
            normals[index[i3 + 1] * 3 + 1] = tmp.getY();
            normals[index[i3 + 1] * 3 + 2] = tmp.getZ();
            normals[index[i3 + 2] * 3 + 0] = tmp.getX();
            normals[index[i3 + 2] * 3 + 1] = tmp.getY();
            normals[index[i3 + 2] * 3 + 2] = tmp.getZ();
        }
        
        geom.setNormals( 0, normals );
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
    protected void updateTarget( float absAnimType, int baseFrame, int nextFrame, float alpha, ModelAnimation animation )
    {
        int frame = ( alpha < 0.5f ) ? baseFrame : nextFrame;
        
        Geometry geom = shape.getGeometry();
        Bone[] skeleton = getFrame( frame ).getSkeleton();
        
        Point3f tmp = Point3f.fromPool();
        
        float x, y, z;
        
        for ( int i = 0; i < geom.getVertexCount(); i++ )
        {
            x = 0f;
            y = 0f;
            z = 0f;
            
            // calculate final vertex to draw with weights
            for ( int j = 0; j < weights[i].length; j++ )
            {
                BoneWeight weight = weights[i][j];
                Bone bone = skeleton[weight.getBoneIndex()];
                
                bone.getRotation().transform( weight.getOffset(), tmp );
                x += ( bone.getTranslation().getX() + tmp.getX() ) * weight.getWeight();
                y += ( bone.getTranslation().getY() + tmp.getY() ) * weight.getWeight();
                z += ( bone.getTranslation().getZ() + tmp.getZ() ) * weight.getWeight();
            }
            
            coords[i * 3 + 0] = x;
            coords[i * 3 + 1] = y;
            coords[i * 3 + 2] = z;
            
            //geom.setCoordinate( i, x, y, z );
        }
        
        Point3f.toPool( tmp );
        
        geom.setCoordinates( 0, coords );
        shape.updateBounds( false );
        
        computeNormals( geom );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BoneAnimationKeyFrameController sharedCopy( Map<String, NamedObject> namedObjects )
    {
        String shapeName = this.shape.getName();
        Shape3D newShape = (Shape3D)namedObjects.get( shapeName );
        if ( newShape == null )
            throw new Error( "Can't clone this AnimationController!" );
        
        return ( new BoneAnimationKeyFrameController( (BoneAnimationKeyFrame[])this.getKeyFrames(), weights, newShape ) );
    }
    
    public BoneAnimationKeyFrameController( BoneAnimationKeyFrame[] frames, BoneWeight[][] weights, Shape3D shape )
    {
        super( AnimationType.SKELETAL, frames );
        
        this.weights = weights;
        
        this.shape = shape;
        this.coords = new float[ shape.getGeometry().getVertexCount() * 3 ];
        this.normals = new float[ shape.getGeometry().getVertexCount() * 3 ];
    }
}
