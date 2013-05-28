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
package org.xith3d.loaders.models.util.specific.bsp;

import java.util.BitSet;

import org.jagatoo.loaders.models.bsp.BSPClusterManager;
import org.jagatoo.loaders.models.bsp.lumps.BSPVisData;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.loaders.models.Model;
import org.xith3d.physics.collision.Collideable;
import org.xith3d.physics.collision.CollideableGroup;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.scenegraph.Shape3D;

/**
 * An extension of the Standard BSPClusterManager with additional
 * collision support.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CollisionEnabledClusterManager extends BSPClusterManager
{
    private final CollideableGroup sceneCollGroup;
    
    public final CollideableGroup getCollideableGroup()
    {
        return ( sceneCollGroup );
    }
    
    private static final CollideableGroup createCollideables( Model scene, CollisionEngine collEngine )
    {
        CollideableGroup sceneCollGroup = collEngine.newGroup( "Simple" );
        
        for ( int i = 0; i < scene.getShapesCount(); i++ )
        {
            Shape3D shape = scene.getShape( i );
            
            final Collideable coll = collEngine.newTriMesh( shape );
            //final Collideable coll = collEngine.newBox( shape );
            
            sceneCollGroup.addCollideable( coll );
        }
        
        return ( sceneCollGroup );
    }
    
    @Override
    public boolean updateVisibility( Matrix4f cameraTransform )
    {
        boolean result = super.updateVisibility( cameraTransform );
        
        if ( result )
        {
            for ( int i = 0; i < sceneCollGroup.getChildrenCount(); i++ )
            {
                Collideable coll = sceneCollGroup.getChild( i );
                
                coll.setEnabled( shapeBitset.get( i ) );
            }
        }
        
        return ( result );
    }
    
    public CollisionEnabledClusterManager( BSPVisData bspVisData, int[][][] clusterLeafs, int[] leafToCluster, float[] planes, int[] nodes, Model scene, CollisionEngine collEngine, BitSet faceBitset )
    {
        super( bspVisData, clusterLeafs, leafToCluster, planes, nodes, faceBitset );
        
        this.sceneCollGroup = createCollideables( scene, collEngine );
    }
    
    public CollisionEnabledClusterManager( BSPClusterManager template, Model scene, CollisionEngine collEngine )
    {
        super( template );
        
        this.sceneCollGroup = createCollideables( scene, collEngine );
    }
}
