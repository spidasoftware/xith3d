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
package org.xith3d.effects.shadows;

import java.util.LinkedList;
import java.util.List;

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.effects.shadows.occluder.Occluder;
import org.xith3d.effects.shadows.occluder.OccluderSubmission;
import org.xith3d.render.RenderPass;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.ShadowAtom;
import org.xith3d.scenegraph.DirectionalLight;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph._SG_PrivilegedAccess;

/**
 * The {@link VolumeShadowFactory} uses shadow volumes to generate shadows.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class VolumeShadowFactory extends ShadowFactory
{
    public static final ShadowFactoryIdentifier SHADOW_FACTORY_ID = new ShadowFactoryIdentifier();
    
    private Point3f lightSource = new Point3f( -10000f, 10000f, 10000f );
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ShadowFactoryIdentifier getShadowFactoryId()
    {
        return ( SHADOW_FACTORY_ID );
    }
    
    public final void setLightSourcePosition( Tuple3f pos )
    {
        this.lightSource.set( pos );
    }
    
    public final Point3f getLightSourcePosition()
    {
        return ( lightSource );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyLight( Light light )
    {
        if ( ( light != null ) && ( !( light instanceof DirectionalLight ) ) )
        {
            throw new IllegalArgumentException( "This shadowFactory accepts DirectionalLights only." );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needsPerLightCulling()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Sized2iRO getLightViewport()
    {
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RenderPass setupRenderPass( View view, Light light, float viewportAspect, RenderBin shadowBin, long frameId, boolean justForCulling )
    {
        return ( null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ShadowAtom getShadowAtom( Node node )
    {
        final ShadowAtom shadowAtom;
        if ( node instanceof GroupNode )
            shadowAtom = _SG_PrivilegedAccess.getAtom( (GroupNode)node );
        else if ( node instanceof Shape3D )
            shadowAtom = _SG_PrivilegedAccess.getAtom( (Shape3D)node );
        else
            shadowAtom = null;
        
        if ( shadowAtom != null )
        {
            final Occluder occluder = (Occluder)node.getShadowAttachment();
            
            occluder.worldTransform = node.getWorldTransform();
        }
        
        return ( shadowAtom );
    }
    
    /**
     * Calculates a list of the occluder shapes in the tree.
     * 
     * @param list
     */
    protected final void getOccluderSubmission( Node node, List< OccluderSubmission > list )
    {
        if ( node instanceof GroupNode )
        {
            final GroupNode group = (GroupNode)node;
            final int count = group.numChildren();
            for ( int i = 0; i < count; i++ )
            {
                getOccluderSubmission( group.getChild( i ), list );
            }
        }
        
        if ( node instanceof Shape3D )
        {
            list.add( new OccluderSubmission( (Shape3D)node, new Transform3D() ) );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onOccluderStateChanged( Node node, boolean isOccluder )
    {
        if ( isOccluder )
        {
            LinkedList< OccluderSubmission > list = new LinkedList< OccluderSubmission >();
            getOccluderSubmission( node, list );
            Occluder occluder = new Occluder();
            occluder.build( list );
            node.setShadowAttachment( occluder );
        }
        else
        {
            node.setShadowAttachment( null );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onShadowReceiverStateChanged( Shape3D shape, boolean isShadowReceiver )
    {
    }
}
