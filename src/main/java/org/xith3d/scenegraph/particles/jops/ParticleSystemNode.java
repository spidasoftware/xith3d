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
package org.xith3d.scenegraph.particles.jops;

import java.util.ArrayList;
import java.util.List;

import org.openmali.spatial.bodies.Frustum;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.UpdatableNode;
import org.xith3d.scenegraph.View;

import org.softmed.jops.Generator;
import org.softmed.jops.ParticleSystem;

/**
 * Insert comment here.
 * 
 * @author Guilherme Gomes (aka guilhermegrg)
 */
public abstract class ParticleSystemNode extends Group implements UpdatableNode
{
    protected GroupNode node;
    protected boolean showGeneratorsAndPointMasses = false;
    protected GeneratorAndPointMassVisualizer visualizer;
    protected ArrayList<GeneratorShape3D> generatorShapes = new ArrayList<GeneratorShape3D>();
    protected ParticleSystem particleSystem;
    
    protected abstract void customSetup();
    
    protected abstract void customUpdate( Vector3f up, Vector3f right );
    
    protected void setup()
    {
        if ( node != null )
        {
            clearShapes();
        }
        
        // de
        
        customSetup();
    }
    
    protected void clearShapes()
    {
        for ( int i = 0; i < generatorShapes.size(); i++ )
        {
            final GeneratorShape3D generatorShape = generatorShapes.get( i );
            
            generatorShape.dispose();
            //node.removeChild( generatorShape );
        }
        generatorShapes.clear();
        
        showGeneratorsAndPointMasses = false;
        if ( visualizer != null )
            visualizer.dispose();
        
        //updateVisualizer();
    }
    
    protected void updateGeneratorShapes( Vector3f up, Vector3f right )
    {
        for ( int i = 0; i < generatorShapes.size(); i++ )
        {
            final GeneratorShape3D generatorShape = generatorShapes.get( i );
            
            generatorShape.update( up, right );
            // TODO: for automatic bounds
            generatorShape.updateBounds( false );
            // TODO: for static bounds
            // setBounds( new BoundingSphere( ... ) );
            // setBoundsAutoCompute( false );
            // TODO: for disable bounds
            // generatorShape.setBounds( null );
        }
    }
    
    protected void update( Vector3f up, Vector3f right )
    {
        customUpdate( up, right );
        updateGeneratorShapes( up, right );
        
        if ( !particleSystem.isAlive() && particleSystem.isRemove() )
        {
            setRenderable( false );
            node.setRenderable( false );
            for ( int i = 0; i < generatorShapes.size(); i++ )
            {
                generatorShapes.get( i ).setRenderable( false );
            }
            
            new ShutdownThread( this ).cleanParticleSystemNode();
        }
    }
    
    public void shutdown()
    {
        detach();
        
        for ( int i = 0; i < generatorShapes.size(); i++ )
        {
            generatorShapes.get( i ).detach();
        }
        
        clearShapes();
    }
    
    protected void installGenerators( boolean relativeOrientation, GroupNode node )
    {
        this.node = node;
        List<Generator> gens = particleSystem.getGenerators();
        GeneratorShape3D generatorShape3D = null;
        for ( int i = 0; i < gens.size(); i++ )
        {
            final Generator generator = gens.get( i );
            
            generatorShape3D = new GeneratorShape3D( relativeOrientation, generator );
            generatorShapes.add( generatorShape3D );
            // this is for absolute positioning
            node.addChild( generatorShape3D );
        }
        
        updateVisualizer();
    }
    
    private void updateVisualizer()
    {
        if ( showGeneratorsAndPointMasses )
        {
            visualizer = new GeneratorAndPointMassVisualizer();
            visualizer.showGeneratorsAndPointMasses( particleSystem, node );
        }
        else if ( visualizer != null )
        {
            visualizer.removeGeneratorsAndPointMasses();
            visualizer = null;
        }
    }
    
    public void setParticleSystem( ParticleSystem ps )
    {
        this.particleSystem = ps;
        
        setup();
    }
    
    public ParticleSystem getParticleSystem()
    {
        return ( particleSystem );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean update( View view, Frustum frustrum, long nanoTime, long nanoStep )
    {
        update( view.getUpDirection(), view.getRightDirection() );
        
        return ( true );
    }
    
    public boolean isShowGeneratorsAndPointMasses()
    {
        return ( showGeneratorsAndPointMasses );
    }
    
    public void setShowGeneratorsAndPointMasses( boolean showGeneratorsAndPointMasses )
    {
        this.showGeneratorsAndPointMasses = showGeneratorsAndPointMasses;
        
        updateVisualizer();
    }
    
    public final List<GeneratorShape3D> getGeneratorShapes()
    {
        return ( generatorShapes );
    }
    
    public ParticleSystemNode( ParticleSystem ps )
    {
        setParticleSystem( ps );
    }
    
    public ParticleSystemNode()
    {
    }
}
