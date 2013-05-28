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
package org.xith3d.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jagatoo.logging.LogLevel;
import org.jagatoo.logging.ProfileTimer;
import org.openmali.vecmath2.Point3f;

import org.xith3d.picking.PickPool;
import org.xith3d.picking.PickRequest;
import org.xith3d.picking.PickResult;
import org.xith3d.render.preprocessing.FrustumCuller;
import org.xith3d.render.preprocessing.sorting.FrontToBackRenderBinSorter;
import org.xith3d.render.preprocessing.sorting.OrderedStateRenderBinSorter;
import org.xith3d.render.preprocessing.sorting.RenderBinSorter;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.SceneGraph;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph._SG_PrivilegedAccess;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;
import org.xith3d.sound.SoundProcessor;
import org.xith3d.utility.logging.X3DLog;

/**
 * The Renderer is the main class for managing the transformation from the
 * scene graph to the 3D card. The Renderer is in charge of atom shader
 * sorting, transparency passes, etc.<br>
 * <br>
 * The actual OpenGL calls are made by the rendering peer which is
 * supplied when the renderer is created. The renderer supports several different
 * modes of operation including offscreen, render to texture, render to image and
 * rendering to screen.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky) [code cleaning, documentation]
 */
public class DefaultRenderer extends Renderer
{
    private static final FrontToBackRenderBinSorter frontToBackSorter = new FrontToBackRenderBinSorter();
    
    private RenderBinSorter opaqueRenderBinSorter = new OrderedStateRenderBinSorter();
    private RenderBinSorter transparentRenderBinSorter = new FrontToBackRenderBinSorter();
    
    private OpaqueSortingPolicy opaqueSortingPolicy = OpaqueSortingPolicy.SORT_BY_STATES;
    private TransparentSortingPolicy transparentSortingPolicy = TransparentSortingPolicy.SORT_FRONT_TO_BACK;
    
    private final FrustumCuller frustumCuller;
    
    private final ScenegraphModificationsManager modManager;
    private final ArrayList< ScenegraphModificationsListener > modListeners;
    
    private final ArrayList< Canvas3D > canvasList;
    
    private final ArrayList< RenderTarget > renderTargets;
    private final HashMap< RenderTarget, RenderPass > renderTargetRenderPassMap = new HashMap< RenderTarget, RenderPass >();
    
    private final ArrayList< RenderPass > renderPasses;
    private final ArrayList< RenderPass > tmpRenderPasses;
    private final ArrayList< RenderPass > tmpRenderPasses2;
    private final ArrayList< RenderPass > effectiveRenderPasses;
    private final ArrayList< ArrayList< GroupNode >> tmpGroupsListsLists;
    private final ArrayList< GroupNode > tmpGroupList;
    private boolean layeredMode = false;
    
    private Point3f viewPosition2 = new Point3f();
    
    private List< PickRequest > pickRequests;
    private boolean recullForced = false;
    
    private long frameId = 0L;
    
    private volatile boolean isRendering;
    private long shapesRendered = 0;
    private long trianglesRendered = 0;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final SoundProcessor getSoundProcessor()
    {
        return ( SoundProcessor.getInstance() );
    }
    
    public ScenegraphModificationsManager getScenegraphModificationsManager()
    {
        return ( modManager );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addScenegraphModificationListener( ScenegraphModificationsListener modListener )
    {
        modListeners.add( modListener );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeScenegraphModificationListener( ScenegraphModificationsListener modListener )
    {
        modListeners.remove( modListener );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List< ScenegraphModificationsListener > getScenegraphModificationListeners()
    {
        return ( modListeners );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final void addCanvas3D( Canvas3D canvas )
    {
        canvasList.add( canvas );
        
        canvas.setRenderer( this );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final void removeCanvas3D( Canvas3D canvas )
    {
        canvasList.remove( canvas );
        
        canvas.setRenderer( null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final Canvas3D removeCanvas3D( int i )
    {
        final Canvas3D canvas = canvasList.remove( i );
        
        canvas.setRenderer( null );
        
        return ( canvas );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getNumberOfCanvas3Ds()
    {
        return ( canvasList.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final Canvas3D getCanvas3D( int index )
    {
        return ( canvasList.get( index ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addRenderTarget( RenderTarget renderTarget, RenderPass renderPass )
    {
        renderTargets.add( renderTarget );
        renderPass.setRenderTarget( renderTarget );
        renderTargetRenderPassMap.put( renderTarget, renderPass );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final RenderPass addRenderTarget( RenderTarget renderTarget, RenderPassConfig passConfig )
    {
        RenderPass pass = new RenderPass( passConfig );
        
        addRenderTarget( renderTarget, pass );
        
        return ( pass );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeRenderTarget( RenderTarget renderTarget )
    {
        renderTargets.remove( renderTarget );
        renderTargetRenderPassMap.remove( renderTarget );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List< RenderTarget > getRenderTargets()
    {
        return ( renderTargets );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final RenderPass addRenderPass( RenderPass renderPass )
    {
        if ( renderPass == null )
            throw new IllegalArgumentException( "renderPass must not be null." );
        
        renderPasses.add( renderPass );
        
        return ( renderPass );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final RenderPass addRenderPass( int index, RenderPass renderPass )
    {
        if ( renderPass == null )
            throw new IllegalArgumentException( "renderPass must not be null." );
        
        renderPasses.add( index, renderPass );
        
        return ( renderPass );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean removeRenderPasses( BranchGroup branchGroup )
    {
        final List< RenderPass > passes = getRenderPasses( branchGroup );
        
        int n = 0;
        for ( int i = 0; i < passes.size(); i++ )
        {
            if ( renderPasses.remove( passes.get( i ) ) )
                n++;
        }
        
        return ( n > 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean removeRenderPass( RenderPass renderPass )
    {
        return ( removeRenderPasses( renderPass.getBranchGroup() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected final boolean removeRenderPass( int index )
    {
        final RenderPass removedPass = renderPasses.remove( index );
        
        return ( removedPass != null );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeAllRenderPasses()
    {
        renderPasses.clear(); // TODO: handle map?
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final int getRenderPassesCount()
    {
        return ( renderPasses.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final List< RenderPass > getRenderPasses( BranchGroup branchGroup )
    {
        tmpRenderPasses2.clear();
        
        for ( int i = 0; i < renderPasses.size(); i++ )
        {
            final RenderPass rp = renderPasses.get( i );
            
            if ( rp.getBranchGroup() == branchGroup )
                tmpRenderPasses2.add( rp );
        }
        
        return ( tmpRenderPasses2 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final RenderPass getRenderPass( int index )
    {
        return ( renderPasses.get( index ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final List< RenderPass > getRenderPasses()
    {
        return ( renderPasses );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setLayeredMode( boolean layeredMode )
    {
        this.layeredMode = layeredMode;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isLayeredMode()
    {
        return ( layeredMode );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setOpaqueSorter( RenderBinSorter sorter )
    {
        this.opaqueRenderBinSorter = sorter;
        this.opaqueSortingPolicy = OpaqueSortingPolicy.CUSTOM;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final RenderBinSorter getOpaqueSorter()
    {
        return ( opaqueRenderBinSorter );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTransparentSorter( RenderBinSorter sorter )
    {
        this.transparentRenderBinSorter = sorter;
        this.transparentSortingPolicy = TransparentSortingPolicy.CUSTOM;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final RenderBinSorter getTransparentSorter()
    {
        return ( transparentRenderBinSorter );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setOpaqueSortingPolicy( OpaqueSortingPolicy policy )
    {
        this.opaqueRenderBinSorter = policy.getSorter();
        this.opaqueSortingPolicy = policy;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final OpaqueSortingPolicy getOpaqueSortingPolicy()
    {
        return ( opaqueSortingPolicy );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTransparentSortingPolicy( TransparentSortingPolicy policy )
    {
        this.transparentRenderBinSorter = policy.getSorter();
        this.transparentSortingPolicy = policy;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final TransparentSortingPolicy getTransparentSortingPolicy()
    {
        return ( transparentSortingPolicy );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void addPickRequest( PickRequest pickRequest )
    {
        synchronized ( pickRequests )
        {
            pickRequests.add( pickRequest );
        }
    }
    
    /**
     * Sorts all atoms according to the current policies.
     * 
     * @param pass
     * @param viewTransform
     */
    private final void sortAllAtoms( RenderPass pass, Transform3D viewTransform )
    {
        final RenderPassConfig passConfig = pass.getConfig();
        
        RenderBinSorter opaqueSorter;
        RenderBinSorter transparentSorter;
        
        if ( passConfig != null )
        {
            opaqueSorter = passConfig.getOpaqueSorter();
            transparentSorter = passConfig.getTransparentSorter();
            
            if ( passConfig.getOpaqueSortingPolicy() == null )
                opaqueSorter = this.getOpaqueSorter();
            if ( passConfig.getTransparentSortingPolicy() == null )
                transparentSorter = this.getTransparentSorter();
        }
        else
        {
            opaqueSorter = this.getOpaqueSorter();
            transparentSorter = this.getTransparentSorter();
        }
        
        pass.getRenderBinProvider().sortAllAtoms( opaqueSorter, transparentSorter, viewTransform );
    }
    
    protected Object doRender( List< RenderPass > renderPasses, Canvas3D canvas, long frameId, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        // block until the rendering is finished...
        while ( canvas.getPeer().isRendering() )
        {
            try
            {
                Thread.sleep( 10L );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
        
        return ( canvas.getPeer().initRenderingImpl( canvas.getView(), renderPasses, layeredMode, frameId, nanoTime, nanoStep, pickRequest ) );
    }
    
    /**
     * Renders one frame from a specified universe and on a canvas.
     * 
     * @param universe
     * @param canvas
     */
    private Object renderOnceInternal( List< RenderPass > renderPasses, List< ? extends List< GroupNode > > groupLists, Canvas3D canvas, long nanoTime, long nanoStep, PickRequest pickRequest )
    {
        Object result;
        synchronized ( canvas.getPeer().getRenderLock() )
        {
            frameId++;
            
            canvas.checkForResized();
            
            X3DLog.println("\nSTARTING FRAME\n" );
            
            shapesRendered = 0L;
            trianglesRendered = 0L;
            isRendering = true;
            
            final OpenGLCapabilities glCaps = canvas.getPeer().getOpenGLCapabilities();
            
            ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "Renderer:renderOnce" );
            
            if ( ( groupLists != null ) && modManager.hasAnythingChanged() )
            {
                for ( int i = 0; i < groupLists.size(); i++ )
                {
                    final List< GroupNode > groups = groupLists.get( i );
                    for ( int j = 0; j < groups.size(); j++ )
                    {
                        final GroupNode group = groups.get( j );
                        
                        group.setModListener( modManager );
                        
                        /*
                        if ( ( group instanceof BranchGroup ) && ( (BranchGroup)group ).isRefillForeced() )
                        {
                            // collect
                            atomsCollector.collectAtoms( renderPasses, groupLists, modManager, soundProcessor, glCaps );
                        }
                        */
                    }
                }
            }
            else if ( ( renderPasses != null ) && modManager.hasAnythingChanged() )
            {
                for ( int i = 0; i < renderPasses.size(); i++ )
                {
                    final RenderPass renderPass = renderPasses.get( i );
                    final BranchGroup bg = renderPass.getBranchGroup();
                    bg.setModListener( modManager );
                    
                    /*
                    if ( bg.isRefillForeced() )
                    {
                        // collect
                        atomsCollector.collectAtoms( renderPasses, modManager, soundProcessor, glCaps );
                    }
                    */
                }
            }
            
            final Point3f viewPosition = canvas.getView().getPosition();
            
            List< RenderPass > rps = renderPasses;
            
            if ( pickRequest == null )
            {
                if ( renderTargets.size() > 0 )
                {
                    effectiveRenderPasses.clear();
                    
                    for ( int i = 0; i < renderTargets.size(); i++ )
                    {
                        final RenderTarget renderTarget = renderTargets.get( i );
                        final RenderPass renderPass = renderTargetRenderPassMap.get( renderTarget );
                        
                        // notify the RenderCallbacks, if any
                        renderPass.getRenderCallbackNotifier().notifyBeforeRenderPassIsProcessed( renderPass );
                        
                        final Point3f viewPos;
                        if ( renderPass.getConfig().getViewTransform() == null )
                        {
                            viewPos = viewPosition;
                        }
                        else
                        {
                            renderPass.getConfig().getViewTransform().getTranslation( viewPosition2 );
                            viewPos = viewPosition2;
                        }
                        
                        frustumCuller.cullAtoms( renderPass, renderTarget.getGroup(), canvas, viewPos, glCaps, frameId, nanoTime, nanoStep, null );
                        
                        effectiveRenderPasses.add( renderPass );
                        
                        // notify the RenderCallbacks, if any
                        renderPass.getRenderCallbackNotifier().notifyAfterRenderPassIsProcessed( renderPass );
                    }
                    
                    for ( int j = 0; j < renderPasses.size(); j++ )
                    {
                        effectiveRenderPasses.add( renderPasses.get( j ) );
                    }
                    
                    rps = effectiveRenderPasses;
                }
                
                // cull
                shapesRendered = frustumCuller.cullAtoms( renderPasses, groupLists, canvas, glCaps, frameId, nanoTime, nanoStep, null );
                trianglesRendered = canvas.getPeer().getTriangles();
                
                // sort
                for ( int i = 0; i < rps.size(); i++ )
                {
                    final RenderPass renderPass = rps.get( i );
                    
                    final Transform3D viewTransform;
                    if ( renderPass.getConfig().getViewTransform() == null )
                        viewTransform = canvas.getView().getTransform();
                    else
                        viewTransform = renderPass.getConfig().getViewTransform();
                    
                    sortAllAtoms( renderPass, viewTransform );
                }
                
                modManager.resetAnythingChanged();
                _SG_PrivilegedAccess.setChanged( canvas.getView().getTransform(), false );
                
                recullForced = false;
            }
            else if ( ( modManager.hasAnythingChanged() ) || ( canvas.getView().getTransform().isChanged() ) || ( recullForced ) )
            {
                // cull
                frustumCuller.cullAtoms( (RenderPass)null, (GroupNode)null, canvas, viewPosition, glCaps, frameId, nanoTime, nanoStep, pickRequest );
                
                // sort
                for ( int i = 0; i < pickRequest.getRenderPasses().size(); i++ )
                {
                    final RenderPass renderPass = pickRequest.getRenderPasses().get( i );
                    
                    // notify the RenderCallbacks, if any
                    renderPass.getRenderCallbackNotifier().notifyBeforeRenderPassIsProcessed( renderPass );
                    
                    final Transform3D viewTransform;
                    if ( renderPass.getConfig().getViewTransform() == null )
                        viewTransform = canvas.getView().getTransform();
                    else
                        viewTransform = renderPass.getConfig().getViewTransform();
                    
                    renderPass.getRenderBinProvider().sortAllAtoms( frontToBackSorter, frontToBackSorter, viewTransform );
                    
                    // notify the RenderCallbacks, if any
                    renderPass.getRenderCallbackNotifier().notifyAfterRenderPassIsProcessed( renderPass );
                }
            }
            
            
            if ( pickRequest == null )
                result = doRender( rps, canvas, frameId, nanoTime, nanoStep, pickRequest );
            else
                result = doRender( pickRequest.getRenderPasses(), canvas, frameId, nanoTime, nanoStep, pickRequest );
            
            ProfileTimer.endProfile();
            
            isRendering = false;
        }
        canvas.getPeer().finish();

            return ( result );
        
    }
    
    @SuppressWarnings( "unchecked" )
    private final long performPickings( Canvas3D canvas, long nanoTime, long nanoStep )
    {
        synchronized ( pickRequests )
        {
            for ( int i = 0; i < pickRequests.size(); i++ )
            {
                final long t0 = System.nanoTime();
                
                final PickRequest pickRequest = pickRequests.get( i );
                
                Object resultObj = renderOnceInternal( null, null, canvas, nanoTime, nanoStep, pickRequest );
                
                final long dt = System.nanoTime() - t0;
                final long dtm = dt / 1000000L;
                
                if ( pickRequest.getPickAll() )
                {
                    final List< PickResult > results = (List< PickResult >)resultObj;
                    
                    if ( ( results == null ) || ( results.size() == 0 ) )
                        pickRequest.getAllPickListener().onPickingMissed( pickRequest.getUserObject(), dtm );
                    else
                        pickRequest.getAllPickListener().onObjectsPicked( results, pickRequest.getUserObject(), dtm );
                }
                else
                {
                    final PickResult result = (PickResult)resultObj;
                    
                    if ( result == null )
                        pickRequest.getNearestPickListener().onPickingMissed( pickRequest.getUserObject(), dtm );
                    else
                        pickRequest.getNearestPickListener().onObjectPicked( result, pickRequest.getUserObject(), dtm );
                }
                
                PickPool.deallocatePickRequest( pickRequest );
            }
            
            pickRequests.clear();
            
            recullForced = true;
        }
        
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( List< RenderPass > renderPasses, List< ? extends List< GroupNode >> groupsLists, Canvas3D canvas, long nanoTime, long nanoStep )
    {
        synchronized ( this )
        {
            if ( !pickRequests.isEmpty() )
            {
                performPickings( canvas, nanoTime, nanoStep );
            }
            
            renderOnceInternal( renderPasses, groupsLists, canvas, nanoTime, nanoStep, null );
        }
        
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( RenderPass renderPass, GroupNode group, Canvas3D canvas, long nanoTime, long nanoStep )
    {
        synchronized ( this )
        {
            if ( !pickRequests.isEmpty() )
            {
                performPickings( canvas, nanoTime, nanoStep );
            }
            
            tmpRenderPasses.clear();
            tmpRenderPasses.add( renderPass );
            
            if ( group == null )
            {
                renderOnceInternal( tmpRenderPasses, null, canvas, nanoTime, nanoStep, null );
            }
            else
            {
                tmpGroupsListsLists.clear();
                tmpGroupList.clear();
                tmpGroupList.add( group );
                tmpGroupsListsLists.add( tmpGroupList );
                
                renderOnceInternal( tmpRenderPasses, tmpGroupsListsLists, canvas, nanoTime, nanoStep, null );
            }
        }
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( RenderPass renderPass, Canvas3D canvas, long nanoTime, long nanoStep )
    {
        return ( renderOnce( renderPass, null, canvas, nanoTime, nanoStep ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( Canvas3D canvas, long nanoTime, long nanoStep )
    {
        synchronized ( this )
        {
            if ( !pickRequests.isEmpty() )
            {
                performPickings( canvas, nanoTime, nanoStep );
            }
            
            renderOnceInternal( renderPasses, (List< List< GroupNode >>)null, canvas, nanoTime, nanoStep, null );
        }
        
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( View view, long nanoTime, long nanoStep )
    {
        final int n = view.getCanvas3Ds().size();
        for ( int i = 0; i < n; i++ )
        {
            renderOnce( view.getCanvas3D( i ), nanoTime, nanoStep );
        }
        
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( RenderPass renderPass, GroupNode group, long nanoTime, long nanoStep )
    {
        final SceneGraph sg;
        if ( group == null )
            sg = renderPass.getBranchGroup().getSceneGraph();
        else
            sg = group.getRoot().getSceneGraph();
        final int nv = sg.getNumberOfViews();
        
        for ( int i = 0; i < nv; i++ )
        {
            renderOnce( sg.getView( i ), nanoTime, nanoStep );
        }
        
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( RenderPass renderPass, long nanoTime, long nanoStep )
    {
        final SceneGraph sg = renderPass.getBranchGroup().getSceneGraph();
        final int nv = sg.getNumberOfViews();
        
        for ( int i = 0; i < nv; i++ )
        {
            renderOnce( sg.getView( i ), nanoTime, nanoStep );
        }
        
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long renderOnce( long nanoTime, long nanoStep )
    {
        synchronized ( this )
        {
            final Canvas3D firstCanvas = canvasList.get( 0 );
            
            if ( !pickRequests.isEmpty() )
            {
                performPickings( firstCanvas, nanoTime, nanoStep );
            }
            
            final int nc = canvasList.size();
            for ( int i = 0; i < nc; i++ )
            {
                renderOnceInternal( renderPasses, (List< List< GroupNode >>)null, canvasList.get( i ), nanoTime, nanoStep, null );
            }
        }
        
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public long getLastFrameId()
    {
        return ( frameId );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long getNumRenderedShapes()
    {
        return ( shapesRendered );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final long getNumRenderedTriangles()
    {
        return ( trianglesRendered );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRendering()
    {
        return ( isRendering );
    }
    
    /**
     * Creates a new Renderer.
     */
    public DefaultRenderer()
    {
        this.frustumCuller = new FrustumCuller();
        this.modManager = new ScenegraphModificationsManager( this );
        this.modListeners = new ArrayList< ScenegraphModificationsListener >( 1 );
        
        this.canvasList = new ArrayList< Canvas3D >();
        
        this.renderTargets = new ArrayList< RenderTarget >();
        
        this.renderPasses = new ArrayList< RenderPass >();
        this.tmpRenderPasses = new ArrayList< RenderPass >();
        this.tmpRenderPasses2 = new ArrayList< RenderPass >();
        this.effectiveRenderPasses = new ArrayList< RenderPass >();
        this.tmpGroupsListsLists = new ArrayList< ArrayList< GroupNode > >();
        this.tmpGroupList = new ArrayList< GroupNode >( 1 );
        
        this.pickRequests = new ArrayList< PickRequest >();
    }
}
