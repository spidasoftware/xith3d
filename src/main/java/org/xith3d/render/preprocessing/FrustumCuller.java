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
package org.xith3d.render.preprocessing;

import org.openmali.spatial.bodies.Box;
import org.openmali.spatial.bodies.Classifier;
import org.openmali.spatial.bodies.Classifier.Classification;
import org.openmali.spatial.bodies.Frustum;
import org.openmali.spatial.bodies.Sphere;
import org.openmali.spatial.bounds.BoundsType;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Point3f;
import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.shadows.ShadowFactory;
import org.xith3d.picking.PickPool;
import org.xith3d.picking.PickRay;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.RenderPass;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.scenegraph.*;
import org.xith3d.utility.logging.X3DLog;

import java.util.BitSet;
import java.util.List;

/**
 * The ViewCuller is in charge of traversing the scenegraph to
 * cull any Shape3D with the View's Frustum.<br>
 * <br>
 * This class is based on AtomsCollector, which is originally coded by David Yazel
 * and heavily modified by Marvin Froehlich.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FrustumCuller
{
    private ShadowFactory shadowFactory;
    
    private int unculledShapesCount = 0;
    
    private final Point3f viewPosition2 = new Point3f();
    
    /**
     * Further traverses all enabled subnodes of the Switch.
     */
    private final void cullSwitchAtoms( Switch sw, Classification parentClassify, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass )
    {
        if ( sw instanceof LODSwitch )
        {
            ( (LODSwitch)sw ).updateWhichChild( viewPosition );
        }
        
        final int childIdx = sw.getWhichChild();
        switch ( childIdx )
        {
            case Switch.CHILD_MASK:
            {
                final BitSet bs = sw.getChildMask();
                final int numChildren = sw.numChildren();
                for ( int i = bs.nextSetBit( 0 ); i >= 0 && i < numChildren; i = bs.nextSetBit( i + 1 ) )
                {
                    cullNodeAtoms( sw.getChild( i ), parentClassify, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
                }
                break;
            }
            case Switch.CHILD_ALL:
            {
                final int numChildren = sw.numChildren();
                for ( int i = 0; i < numChildren; i++ )
                {
                    cullNodeAtoms( sw.getChild( i ), parentClassify, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
                }
                break;
            }
            case Switch.CHILD_NONE:
            {
                break;
            }
            default:
            {
                final Node n = sw.getChild( childIdx );
                cullNodeAtoms( n, parentClassify, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
                break;
            }
        }
    }
    
    /**
     * Further traverses this group to find Shape3Ds.
     * Checks for state changes and cares for the state-stack.
     */
    private final void cullGroupAtoms( GroupNode group, Classification parentClassify, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass )
    {
        final int numChildren = group.numChildren();
        for ( int i = 0; i < numChildren; i++ )
        {
            cullNodeAtoms( group.getChild( i ), parentClassify, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
        }
    }
    
    private final void handleGroupShadow( GroupNode group, Classification classify, RenderBinProvider binProvider, long frameId, boolean isShadowPass )
    {
        final boolean b = ( shadowFactory.needsPerLightCulling() && isShadowPass ) || ( !shadowFactory.needsPerLightCulling() && !isShadowPass );
        
        // submit the occluder if there is one. Set the virtual world transform for the occluder
        if ( ( shadowFactory != null ) && b && ( group.isOccluder() ) )
        {
            final ShadowAtom shadowAtom = shadowFactory.getShadowAtom( group );
            if ( shadowAtom != null )
            {
                shadowAtom.updateLightsAndFogs();
                binProvider.addShadowAtom( shadowAtom, classify, frameId );
                
                //unculledShapesCount++;
                
                return;
            }
        }
    }
    
    /**
     * Adds the ShapeAtom the the appropriate list
     * 
     * @param shape
     */
    private void addShapeAtom( Shape3D shape, Classification classify, Point3f viewPosition, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, PickRay pickRay, boolean isShadowPass )
    {
        ShapeAtom atom = _SG_PrivilegedAccess.getAtom( shape );
        if ( atom == null )
        {
            atom = new ShapeAtom( shape, glCaps );
            _SG_PrivilegedAccess.setAtom( shape, atom );
        }
        
        if ( shape instanceof AbstractLODShape3D )
        {
            ( (AbstractLODShape3D)shape ).updateLOD( viewPosition );
        }
        
        binProvider.addMainAtom( atom, classify, frameId );
        
        if ( pickRay == null )
        {
            Appearance app = shape.getAppearance();
            if ( ( app != null ) && ( !app.isStatic() || app.isStaticDirty() ) )
            {
                atom.updateStateUnits( app, glCaps );
            }
            
            if ( ( shadowFactory != null ) && shadowFactory.isEnabled() )
            {
                // submit the occluder if there is one. Set the virtual world transform for the occluder
                if ( shape.isOccluder() && ( ( shadowFactory.needsPerLightCulling() && isShadowPass ) || ( !shadowFactory.needsPerLightCulling() && !isShadowPass ) ) )
                {
                    final ShadowAtom shadowAtom = shadowFactory.getShadowAtom( shape );
                    if ( shadowAtom != null )
                        binProvider.addShadowAtom( shadowAtom, classify, frameId );
                }
            }
        }
        
        unculledShapesCount++;
    }
    
    /**
     * Checks the Node's actual type and invokes the appropriate method to further traverse the scenegraph.
     */
    @SuppressWarnings("unchecked")
    public final void cullNodeAtoms( Node node, Classification parentClassify, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass )
    {
        /*
        if ( node.getName() == null )
            X3DLog.debug( "FrustumCuller.cullNodeAtoms(): Checking node ", node.getClass().getName() );
        else
            X3DLog.debug( "FrustumCuller.cullNodeAtoms(): Checking node ", node.getClass().getName(), " \"", node.getName(), "\"" );
        */
		if (node == null) {
			// this should never happen. this is a bug that I am hiding for now. -MFORD
			X3DLog.error("node is null. Node should never be null");
			return;
		}
        
        // a non-renderable node or in pickmode a non-pickable node must not be processed
        if ( ( ( pickRay == null ) && !node.isRenderable() ) || ( ( pickRay != null ) && !node.isPickable() ) )
        {
            return;
        }
        
        Classification classify;
        
        if ( node instanceof BranchGroup )
        {
            classify = null;
        }
        else if ( pickRay != null )
        {
            // pick-ray intersection test replaces frustum culling here.
            // This results in more accurate preselection for GLSelect picking.
            if ( !node.isIgnoreBounds() )
            {
                if ( node.getWorldBounds().intersects( pickRay ) )
                {
                    classify = null;
                }
                else
                {
                    classify = Classification.OUTSIDE;
                    
                    return;
                }
            }
            else
            {
                classify = null;
            }
        }
        else if ( !cullingSuppressed && !node.isIgnoreBounds() )
        {
            if ( parentClassify != Classification.INSIDE )
            {
                if ( node.getBoundsType() == BoundsType.SPHERE )
                {
                    classify = Classifier.classifyFrustumSphere( frustum, (Sphere)node.getWorldBounds() );
                }
                else if ( node.getBoundsType() == BoundsType.AABB )
                {
                    classify = Classifier.classifyFrustumBox( frustum, (Box)node.getWorldBounds() );
                }
                else
                {
                    classify = null;
                }
                
                // Break traversal here. This is especially useful in case this is a Group.
                if ( classify == Classification.OUTSIDE )
                {
                    //System.out.println( "culled: " + node + ", " + node.getWorldBounds() );
                    return;
                }
            }
            else
            {
                classify = parentClassify;
            }
        }
        else
        {
            classify = null;
        }
        
        
        if ( node.isUpdatableNode() )
        {
            _SG_PrivilegedAccess.update( (UpdatableNode)node, view, frustum, nanoTime, nanoStep );
        }
        
        if ( ( node.getShowBounds() ) && ( !( node instanceof Shape3D ) ) )
        {
            binProvider.addMainAtom( new BoundsAtom( node ), classify, frameId );
        }
        
        
        if ( node instanceof GroupNode )
        {
            if ( pickRay == null )
            {
                handleGroupShadow( (GroupNode)node, classify, binProvider, frameId, isShadowPass );            
            }
            
            if ( ( (GroupNode)node ).getTotalNumShapes() > 0 )
            {
                if ( node instanceof SpecialCullingNode )
                {
                    ( (SpecialCullingNode)node ).cullSpecialNode( node, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, this );
                }
                else if ( node instanceof Switch )
                {
                    cullSwitchAtoms( (Switch)node, classify, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
                }
                else
                {
                    cullGroupAtoms( (GroupNode)node, classify, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
                }
            }
        }
        else if ( node instanceof Shape3D )
        {
            final Shape3D shape = (Shape3D)node;
            
            if ( shape.isVisible() )
            {
                addShapeAtom( shape, classify, viewPosition, binProvider, glCaps, frameId, pickRay, isShadowPass );
            }
        }
    }
    
    /**
     * Collects all Atoms in this Group, if the give Node is a Group, otherwise only this Node's Atom.
     * 
     * @param group the Group to collect Atoms from
     * @param frustum
     * @param cullingSuppressed
     * @param viewPosition
     * @param binProvider
     * @param frameId
     * @param pickRay
     */
    private final void cullAtoms( GroupNode group, Frustum frustum, boolean cullingSuppressed, View view, Point3f viewPosition, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass )
    {
        cullNodeAtoms( group, null, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
    }
    
    /**
     * Traverses the scenegraph and collects all RenderAtoms and spreads them over different RenderBins.
     * This collection is later passed to the OpenGL renderer.
     * 
     * @param renderPass
     * @param initialize
     * @param rootGroup
     * @param canvas
     * @param viewPosition
     * @param glCaps
     * @param frameId
     * @param nanoTime
     * @param nanoStep
     */
    private final int cullAtoms_normal( RenderPass renderPass, boolean initialize, GroupNode rootGroup, Canvas3D canvas, Point3f viewPosition, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep )
    {
        unculledShapesCount = 0;
        
        final View view = canvas.getView();
        
        //if ( initialize )
        //{
            _SG_PrivilegedAccess.set( view, true, renderPass.getConfig() );
            
            final Sized2iRO viewport;
            if ( ( renderPass.getConfig() != null ) && ( renderPass.getConfig().getViewport() != null ) )
            {
                viewport = renderPass.getConfig().getViewport();
            }
            else
            {
                viewport = canvas;
            }
            
            Frustum frustum = view.getFrustum( viewport );
        //}
        
        renderPass.getRenderBinProvider().clearAllBins();
        
        cullAtoms( rootGroup, frustum, !renderPass.isFrustumCullingEnabled(), view, viewPosition, renderPass.getRenderBinProvider(), glCaps, frameId, nanoTime, nanoStep, null, false );

        try
        {
            //if ( initialize )
            {
                _SG_PrivilegedAccess.set( canvas.getView(), false, (RenderPassConfig)null );
            }

            if ( ( shadowFactory != null ) && shadowFactory.isEnabled() && shadowFactory.needsPerLightCulling() && ( renderPass.getShadowCasterLight() != null ) )
            {
                final RenderPass viewPass = shadowFactory.setupRenderPass( view, renderPass.getShadowCasterLight(), 0f, null, frameId, true );
                if ( viewPass != null )
                {
                    _SG_PrivilegedAccess.set( view, true, viewPass.getConfig() );

                    final Sized2iRO viewport2;
                    if ( shadowFactory.getLightViewport() != null )
                    {
                        viewport2 = shadowFactory.getLightViewport();
                    }
                    else if ( ( renderPass.getConfig() != null ) && ( renderPass.getConfig().getViewport() != null ) )
                    {
                        viewport2 = renderPass.getConfig().getViewport();
                    }
                    else
                    {
                        viewport2 = canvas;
                    }

                    frustum = view.getFrustum( viewport2 );

                    cullAtoms( rootGroup, frustum, !renderPass.isFrustumCullingEnabled(), canvas.getView(), viewPosition, renderPass.getRenderBinProvider(), glCaps, frameId, nanoTime, nanoStep, null, true );

                    _SG_PrivilegedAccess.set( view, false, (RenderPassConfig)null );
                }
            }
        }
        finally
        {
            renderPass.getRenderBinProvider().shrinkAllBins();
        }
        return ( unculledShapesCount );
    }
    
    /**
     * Traverses the scenegraph and collects all RenderAtoms and spreads them over different RenderBins.
     * This collection is later passed to the OpenGL renderer.
     * 
     * @param canvas
     * @param viewPosition
     * @param frameId
     * @param pickRequest
     */
    private final int cullAtoms_picking( Canvas3D canvas, Point3f viewPosition, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        unculledShapesCount = 0;
        
        BranchGroup bg0 = null;
        Frustum frustum = null;
        PickRay pickRay = null;
        int j = 0;
        
        for ( int i = 0; i < pickRequest.getGroups().size(); i++ )
        {
            pickRequest.getRenderPasses().get( i ).getRenderBinProvider().clearAllBins();
        }
        
        for ( int i = 0; i < pickRequest.getGroups().size(); i++ )
        {
            final GroupNode group = (GroupNode)pickRequest.getGroups().get( i );
            final RenderPass pass = pickRequest.getRenderPasses().get( i );
            
            final BranchGroup bg = group.getRoot();
            if ( ( i == 0 ) || ( bg != bg0 ) )
            {
                bg0 = bg;
                
                if ( bg == null )
                {
                    throw new NullPointerException( "You cannot cull on non-live Groups." );
                }
                
                final View view = canvas.getView();
                _SG_PrivilegedAccess.set( view, true, pass.getConfig() );
                j++;
                
                final Sized2iRO viewport;
                if ( pass.getConfig() != null )
                {
                    if ( pass.getConfig().getViewport() != null )
                        viewport = pass.getConfig().getViewport();
                    else
                        viewport = canvas;
                }
                else
                {
                    viewport = canvas;
                }
                
                frustum = view.getFrustum( viewport );
            }
            
            //if ( ( i == 0 ) || ( group.getLayeredNode() != layNode ) )
            {
                //layNode = group.getLayeredNode();
                
                if ( pickRay == null )
                {
                    pickRay = PickPool.allocatePickRay();
                }
                
                /*
                final CameraMode cameraMode;
                if ( layNode == null )
                    cameraMode = null;
                else
                    cameraMode = layNode.getCameraMode();
                */
                
                pickRay.recalculate( pass.getConfig(), canvas, pickRequest.getMouseX(), pickRequest.getMouseY() );
            }
            
            cullAtoms( group, frustum, !pass.isFrustumCullingEnabled(), canvas.getView(), viewPosition, pass.getRenderBinProvider(), glCaps, frameId, nanoTime, nanoStep, pickRay, false );
        }
        
        if ( pickRay != null )
        {
            PickPool.deallocatePickRay( pickRay );
        }
        
        while ( j-- > 0 )
        {
            _SG_PrivilegedAccess.set( canvas.getView(), false, (RenderPassConfig)null );
        }
        
        return ( unculledShapesCount );
    }
    
    private final void updateShadowFactory()
    {
        final EffectFactory effFact = EffectFactory.getInstance();
        if ( effFact != null )
        {
            shadowFactory = effFact.getShadowFactory();
        }
        else
        {
            shadowFactory = null;
        }
    }
    
    /**
     * Traverses the scenegraph and collects all RenderAtoms and spreads them over different RenderBins.
     * This collection is later passed to the OpenGL renderer.
     * 
     * @param renderPass
     * @param rootGroup
     * @param canvas
     * @param viewPosition
     * @param frameId
     * @param pickRequest
     */
    public final int cullAtoms( RenderPass renderPass, GroupNode rootGroup, Canvas3D canvas, Point3f viewPosition, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        updateShadowFactory();
        
        if ( pickRequest == null )
            return ( cullAtoms_normal( renderPass, true, rootGroup, canvas, viewPosition, glCaps, frameId, nanoTime, nanoStep ) );
        
        return ( cullAtoms_picking( canvas, viewPosition, glCaps, frameId, nanoTime, nanoStep, pickRequest ) );
    }
    
    /**
     * Traverses the scenegraph and collects all RenderAtoms and spreads them over different RenderBins.
     * This collection is later passed to the OpenGL renderer.
     * 
     * @param renderPasses
     * @param groupsLists
     * @param canvas
     * @param frameId
     * @param nanoTime
     * @param nanoStep
     * @param pickRequest
     */
    public final int cullAtoms( List< RenderPass > renderPasses, List< ? extends List< GroupNode >> groupsLists, Canvas3D canvas, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        updateShadowFactory();
        
        int unculledShapesCount = 0;
        
        if ( pickRequest == null )
        {
            if ( groupsLists == null )
            {
                for ( int i = 0; i < renderPasses.size(); i++ )
                {
                    final RenderPass pass = renderPasses.get( i );
                    
                    // notify the RenderCallbacks, if any
                    pass.getRenderCallbackNotifier().notifyBeforeRenderPassIsProcessed( pass );
                    
                    if ( pass.getConfig().getViewTransform() == null )
                        canvas.getView().getPosition( viewPosition2 );
                    else
                        pass.getConfig().getViewTransform().getTranslation( viewPosition2 );
                    
                    if ( pass.getBranchGroup() != null )
                        unculledShapesCount += cullAtoms_normal( pass, ( i == 0 ), pass.getBranchGroup(), canvas, viewPosition2, glCaps, frameId, nanoTime, nanoStep );
                    
                    // notify the RenderCallbacks, if any
                    pass.getRenderCallbackNotifier().notifyAfterRenderPassIsProcessed( pass );
                }
            }
            else
            {
                for ( int i = 0; i < renderPasses.size(); i++ )
                {
                    final RenderPass pass = renderPasses.get( i );
                    
                    // notify the RenderCallbacks, if any
                    pass.getRenderCallbackNotifier().notifyBeforeRenderPassIsProcessed( pass );
                    
                    if ( pass.getConfig().getViewTransform() == null )
                        canvas.getView().getPosition( viewPosition2 );
                    else
                        pass.getConfig().getViewTransform().getTranslation( viewPosition2 );
                    
                    final List< GroupNode > groups = groupsLists.get( i );
                    for ( int j = 0; j < groups.size(); j++ )
                    {
                        unculledShapesCount += cullAtoms_normal( pass, ( i == 0 && j == 0 ), groups.get( j ), canvas, viewPosition2, glCaps, frameId, nanoTime, nanoStep );
                    }
                    
                    // notify the RenderCallbacks, if any
                    pass.getRenderCallbackNotifier().notifyAfterRenderPassIsProcessed( pass );
                }
            }
        }
        else
        {
            return ( cullAtoms_picking( canvas, canvas.getView().getPosition(), glCaps, frameId, nanoTime, nanoStep, pickRequest ) );
        }
        
        return ( unculledShapesCount );
    }
    
    /**
     * Creates a new renderer that also capable of collecting RenderAtoms.
     */
    public FrustumCuller()
    {
    }
}
