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
package org.xith3d.effects.celshading;

import org.openmali.spatial.bodies.Frustum;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.UpdatableNode;
import org.xith3d.scenegraph.View;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class SimpleCelShadingFactory extends CelShadingFactory
{
    private static final Vector3f lightDirection = new Vector3f( 0f, 0f, 1f );
    
    private static class CelShadingShape extends Shape3D implements UpdatableNode
    {
        private long nextUpdateTime = -1L;
        
        private static final void transposeTransform( Matrix4f m, Vector3f v )
        {
            v.set( m.m00() * v.getX() + m.m10() * v.getY() + m.m20() * v.getZ(),
                   m.m01() * v.getX() + m.m11() * v.getY() + m.m21() * v.getZ(),
                   m.m02() * v.getX() + m.m12() * v.getY() + m.m22() * v.getZ()
                 );
        }
        
        private static final void updateTextureCoordinates( Geometry geometry, Matrix4f matrix )
        {
            Vector3f tmpVector = Vector3f.fromPool();
            float tmpShade;
            
            int numVertices = geometry.getVertexCount();
            for ( int i = 0; i < numVertices; i++ )
            {
                geometry.getNormal( i, tmpVector );
                transposeTransform( matrix, tmpVector );
                tmpVector.normalize();
                tmpShade = tmpVector.dot( lightDirection );
                
                if ( tmpShade < 0.0f )
                    tmpShade = 0.0f;
                
                geometry.setTextureCoordinate( 0, i, tmpShade, 0f );
            }
            
            Vector3f.toPool( tmpVector );
        }
        
        public boolean update( View view, Frustum frustum, long nanoTime, long nanoStep )
        {
            if ( nanoTime >= nextUpdateTime )
            {
                Matrix4f m = Matrix4f.fromPool();
                
                this.getWorldTransform().get( m );
                m.invert();
                
                updateTextureCoordinates( getGeometry(), m );
                
                Matrix4f.toPool( m );
                
                nextUpdateTime = nanoTime + 10000000L;
            }
            
            return ( true );
        }
        
        public CelShadingShape( Geometry geometry, Appearance appearance )
        {
            super( geometry, appearance );
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Shape3D createMainShape( Geometry geometry )
    {
        Appearance app = getBaseAppearance();
        
        return ( new CelShadingShape( geometry, app ) );
    }
}
