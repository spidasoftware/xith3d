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
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MeshDeformationKeyFrameController extends KeyFrameController
{
    private final Shape3D shape;
    
    public final MeshDeformationKeyFrame getFrame( int index )
    {
        return ( (MeshDeformationKeyFrame)getKeyFrame( index ) );
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
     * {@inheritDoc}
     */
    @Override
    protected void updateTarget( float absAnimTime, int baseFrameIndex, int nextFrameIndex, float alpha, ModelAnimation animation )
    {
        MeshDeformationKeyFrame baseFrame = getFrame( baseFrameIndex );
        MeshDeformationKeyFrame nextFrame = getFrame( nextFrameIndex );
        
        float[] coords0 = baseFrame.getCoordinates();
        float[] coords1 = nextFrame.getCoordinates();
        
        Geometry geom = shape.getGeometry();
        //int numVertices = geom.getVertexCount();
        
        int j;
        float[] buffer = new float[ 3 ];
        
        if ( baseFrame == nextFrame )
        {
            geom.setCoordinates( 0, coords0 );
        }
        else
        {
            j = 0;
            for ( int i = 0; i < coords0.length; i += 3 )
            {
                buffer[0] = coords0[i + 0] + ( ( coords1[i + 0] - coords0[i + 0] ) * alpha );
                buffer[1] = coords0[i + 1] + ( ( coords1[i + 1] - coords0[i + 1] ) * alpha );
                buffer[2] = coords0[i + 2] + ( ( coords1[i + 2] - coords0[i + 2] ) * alpha );
                
                geom.setCoordinate( j++, buffer );
            }
        }
        
        if ( baseFrame.getNormals() != null )
        {
            float[] normals0 = baseFrame.getNormals();
            float[] normals1 = nextFrame.getNormals();
            
            if ( baseFrame == nextFrame )
            {
                geom.setNormals( 0, normals0 );
            }
            else
            {
                j = 0;
                for ( int i = 0; i < normals0.length; i += 3 )
                {
                    buffer[0] = normals0[i + 0] + ( ( normals1[i + 0] - normals0[i + 0] ) * alpha );
                    buffer[1] = normals0[i + 1] + ( ( normals1[i + 1] - normals0[i + 1] ) * alpha );
                    buffer[2] = normals0[i + 2] + ( ( normals1[i + 2] - normals0[i + 2] ) * alpha );
                    
                    geom.setNormal( j++, buffer );
                }
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public MeshDeformationKeyFrameController sharedCopy( Map<String, NamedObject> namedObjects )
    {
        String shapeName = this.shape.getName();
        Shape3D newShape = (Shape3D)namedObjects.get( shapeName );
        if ( newShape == null )
            throw new Error( "Can't clone this AnimationController!" );
        
        return ( new MeshDeformationKeyFrameController( (MeshDeformationKeyFrame[])this.getKeyFrames(), newShape ) );
    }
    
    public MeshDeformationKeyFrameController( MeshDeformationKeyFrame[] frames, Shape3D shape )
    {
        super( AnimationType.MESH_DEFORMATION, frames );
        
        this.shape = shape;
    }
}
