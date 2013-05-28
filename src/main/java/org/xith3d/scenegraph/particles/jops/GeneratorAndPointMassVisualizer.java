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

import org.xith3d.scenegraph.GroupNode;

import org.softmed.jops.Generator;
import org.softmed.jops.ParticleSystem;
import org.softmed.jops.modifiers.Modifier;
import org.softmed.jops.modifiers.PointMass;

/**
 * Insert comment here.
 * 
 * @author Guilherme Gomes (aka guilhermegrg)
 */
public class GeneratorAndPointMassVisualizer
{
    private List< PointMassNode > pointMasses = new ArrayList< PointMassNode >();
    private List< GeneratorNode > generatorNodes = new ArrayList< GeneratorNode >();
    private GroupNode node;
    
    public void dispose()
    {
        if ( node == null )
            return;
        
        removeGeneratorsAndPointMasses();
        node = null;
    }
    
    protected void showGeneratorsAndPointMasses( ParticleSystem particleSystem, GroupNode node )
    {
        this.node = node;
        if ( particleSystem == null || node == null )
            return;
        
        List< Generator > gens2 = particleSystem.getGenerators();
        for ( int i = 0; i < gens2.size(); i++ )
        {
            final Generator generator = gens2.get( i );
            GeneratorNode gnode = new GeneratorNode();
            gnode.setGenerator( generator );
            generatorNodes.add( gnode );
            node.addChild( gnode );
        }
        
        List< Modifier > modifiers = particleSystem.getModifiers();
        for ( int i = 0; i < modifiers.size(); i++ )
        {
            final Modifier modifier = modifiers.get( i );
            
            if ( modifier instanceof PointMass )
            {
                PointMassNode pmnode = new PointMassNode();
                pmnode.setPointMass( (PointMass)modifier );
                pointMasses.add( pmnode );
                node.addChild( pmnode );
            }
        }
    }
    
    protected void removeGeneratorsAndPointMasses()
    {
        for ( int i = 0; i < generatorNodes.size(); i++ )
        {
            final GeneratorNode generator = generatorNodes.get( i );
            
            node.removeChild( generator );
            generator.setGenerator( null );
        }
        generatorNodes.clear();
        
        for ( int i = 0; i < pointMasses.size(); i++ )
        {
            final PointMassNode pmnode = pointMasses.get( i );
            
            node.removeChild( pmnode );
            pmnode.setPointMass( null );
        }
        
        pointMasses.clear();
    }
}
