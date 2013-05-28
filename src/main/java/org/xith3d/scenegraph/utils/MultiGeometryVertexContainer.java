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
package org.xith3d.scenegraph.utils;

import java.util.ArrayList;
import java.util.List;

import org.openmali.spatial.VertexContainer;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Shape3D;

/**
 * This class allows for retrieving vertex-information from a set of Geometries
 * or whole Groups from the SceneGraph.
 * 
 * @author Lucas Pires Camargo (aka BrazilianBoy)
 */
public class MultiGeometryVertexContainer implements VertexContainer
{
    private final int vertexCount;
    private final List< VertexContainer > geoms;
    
    /**
     * {@inheritDoc}
     */
    public final int getVertexCount()
    {
        return vertexCount;
    }
    
    /**
     * {@inheritDoc}
     */
    public final boolean getVertex( int index, Tuple3f vertex )
    {
        if ( index >= vertexCount )
            return ( false );
        
        int offset = 0;
        
        for ( int i = 0; i < geoms.size(); i++ )
        {
            final VertexContainer geom = geoms.get( i );
            final int size = geom.getVertexCount();
            
            if ( index < offset + size )
            {
                return ( geom.getVertex( index - offset, vertex ) );
            }
            
            offset += size;
        }
        
        return ( false );
    }
    
    public MultiGeometryVertexContainer( GroupNode group )
    {
        List< Shape3D > shapes = group.findAll( Shape3D.class );
        
        this.geoms = new ArrayList< VertexContainer >();
        
        for ( int i = 0; i < shapes.size(); i++ )
        {
            geoms.add( shapes.get( i ).getGeometry() );
        }
        
        int vertexCount = 0;
        
        for ( int i = 0; i < geoms.size(); i++ )
        {
            vertexCount += geoms.get( i ).getVertexCount();
        }
        
        this.vertexCount = vertexCount;
    }
    
    public MultiGeometryVertexContainer( List< VertexContainer > geometries )
    {
        this.geoms = geometries;
        
        int vertexCount = 0;
        
        for ( int i = 0; i < geoms.size(); i++ )
        {
            vertexCount += geoms.get( i ).getVertexCount();
        }
        
        this.vertexCount = vertexCount;
    }
}
