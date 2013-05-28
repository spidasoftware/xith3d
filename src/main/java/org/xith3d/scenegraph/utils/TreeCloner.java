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

import java.util.Stack;

import org.openmali.vecmath2.Matrix4f;
import org.xith3d.scenegraph.*;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * Clones a Subtree of the scenegraph.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TreeCloner implements DetailedTraversalCallback
{
    private final Stack< GroupNode > nodeStack = new Stack< GroupNode >();
    
    private GroupNode root = null;

    private boolean deepCopy = false;

    public boolean isDeepCopy() {
        return deepCopy;
    }

    public void setDeepCopy(boolean deepCopy) {
        this.deepCopy = deepCopy;
    }

    
    protected final void add( Node node )
    {
        if ( !nodeStack.isEmpty() )
        {
            nodeStack.peek().addChild( node );
        }
        
        if ( node instanceof GroupNode )
        {
            nodeStack.push( (GroupNode)node );
            
            if ( root == null )
            {
                this.root = (GroupNode)node;
            }
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationCommon( Node node )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationCommon( GroupNode node )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalCheckGroupCommon( GroupNode group )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalCheckGroup( BranchGroup bg )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalCheckGroup( Group group )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalCheckGroup( Switch sw )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalCheckGroup( TransformGroup tg )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalCheckGroup( OrderedGroup og )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( Node node )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( BranchGroup bg )
    {
        add( new BranchGroup() );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( Group group )
    {
        add( new Group() );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( TransformGroup tg )
    {
        if (tg instanceof Transform)
        {
            Transform t = new Transform();
            t.setMatrix(tg.getTransform().getMatrix4f());
            add (t);
        } else
        {
            add(new TransformGroup(tg.getTransform()));
        }
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( OrderedGroup og )
    {
        add( new OrderedGroup() );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( Switch sw )
    {
        add( new Switch( sw.getWhichChild() ) );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( Shape3D shape )
    {
        Shape3D newShape = null;

        if (deepCopy)
        {
         
            newShape = new Shape3D(shape.getGeometry().cloneNodeComponent(true), shape.getAppearance().cloneNodeComponent(true));
        } else
        {
            newShape = new Shape3D(shape.getGeometry(),shape.getAppearance());
        }
        newShape.setName(shape.getName());
        add( newShape );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( Light light )
    {
        if ( light instanceof DirectionalLight )
        {
            final DirectionalLight dirLight = (DirectionalLight)light;
            add( new DirectionalLight( dirLight.isEnabled(), dirLight.getColor(), dirLight.getDirection() ) );
        }
        else if ( light instanceof SpotLight )
        {
            final SpotLight spotLight = (SpotLight)light;
            add( new SpotLight( spotLight.isEnabled(), spotLight.getColor(), spotLight.getLocation(), spotLight.getAttenuation(), spotLight.getDirection(), spotLight.getSpreadAngle(), spotLight.getConcentration() ) );
        }
        else if ( light instanceof PointLight )
        {
            final PointLight pointLight = (PointLight)light;
            add( new PointLight( pointLight.isEnabled(), pointLight.getColor(), pointLight.getLocation(), pointLight.getAttenuation() ) );
        }
        else
        {
            throw new Error( light.getClass().getName() + " is not yet supported for cloning." );
        }
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( Fog fog )
    {
        if ( fog instanceof LinearFog )
        {
            final LinearFog linFog = (LinearFog)fog;
            add( new LinearFog( linFog.getColor(), linFog.getFrontDistance(), linFog.getBackDistance() ) );
        }
        else if ( fog instanceof ExponentialFog )
        {
            final ExponentialFog expFog = (ExponentialFog)fog;
            add( new ExponentialFog( expFog.getColor(), expFog.getDensity() ) );
        }
        else
        {
            throw new Error( fog.getClass().getName() + " is not yet supported for cloning." );
        }
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperation( Sound sound )
    {
        if ( sound instanceof BackgroundSound )
        {
            final BackgroundSound bgSound = (BackgroundSound)sound;
            add( new BackgroundSound( bgSound.getSoundContainer(), bgSound.getInitialGain() ) );
        }
        else if ( sound instanceof PointSound )
        {
            final PointSound pSound = (PointSound)sound;
            add( new PointSound( pSound.getSoundContainer(), pSound.getInitialGain() ) );
        }
        else
        {
            throw new Error( sound.getClass().getName() + " is not yet supported for cloning." );
        }
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationCommonAfter( Node node )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationCommonAfter( GroupNode group )
    {
        nodeStack.pop();
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( BranchGroup bg )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( Group group )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( TransformGroup tg )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( OrderedGroup og )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( Node node )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( Switch sw )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( Shape3D shape )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( Light light )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( Fog fog )
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean traversalOperationAfter( Sound sound )
    {
        return ( true );
    }
    
    
    @SuppressWarnings("unchecked")
    public < GroupNodeExtension extends GroupNode > GroupNodeExtension cloneTree( GroupNodeExtension root )
    {
        this.root = null;
        this.nodeStack.clear();
        
        root.traverse( this );
        
        final GroupNodeExtension result = (GroupNodeExtension)this.root;
        
        this.root = null;
        this.nodeStack.clear();
        
        return ( result );
    }
    
    public TreeCloner()
    {
    }
}
