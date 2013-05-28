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
package org.xith3d.scenegraph;

import java.util.ArrayList;
import java.util.List;

import org.jagatoo.input.InputSystem;
import org.xith3d.render.BackgroundRenderPass;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.ForegroundRenderPass;
import org.xith3d.render.RenderPass;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.render.Renderer;
import org.xith3d.render._RNDR_PrivilegedAccess;
import org.xith3d.scenegraph.View.ProjectionPolicy;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;
import org.xith3d.ui.hud.HUD;

/**
 * A SceneGraph provides all methods to control adding and removing Nodes of
 * all kinds and adding and removing of RenderPasses.
 * It also provides getters for the Root objects of a Scenegraph.
 * 
 * @see org.xith3d.base.Xith3DEnvironment
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class SceneGraph
{
    /**
     * If true, the renderer checks for illegal nodes modification
     * 
     * @see #checkForIllegalModification(Node)
     */
    protected static final boolean CHECK_FOR_ILLEGAL_MODIFICATION = false;
    
    private final Renderer renderer;
    
    private final ArrayList< View > views = new ArrayList< View >();
    
    protected final ArrayList< HUD > huds = new ArrayList< HUD >();
    
    /**
     * The list of BranchGroups contained in this SceneGraph.
     */
    private final ArrayList< BranchGroup > branchGroups = new ArrayList< BranchGroup >();
    
    public ScenegraphModificationsListener modListener = null;
    
    /**
     * can be used by nodes to make sure that a change is not being attempted at runtime.
     */
    protected static final void checkForIllegalModification( final Node node )
    {
        if ( node.isLive() && _RNDR_PrivilegedAccess.getRenderersWorking() > 0 )
        {
            new Error( "Illegal Scenegraph Modification " ).printStackTrace();
            System.exit( 100 );
        }
    }
    
    /**
     * @return this SceneGraph's Renderer.
     */
    public final Renderer getRenderer()
    {
        return ( renderer );
    }
    
    /**
     * Adds a new ScenegraphModificationListener to the List.
     * It will be notified of any scenegraph change at runtime.
     * 
     * @param modListener the new ScenegraphModificationsListener to add
     */
    public final void addScenegraphModificationListener( ScenegraphModificationsListener modListener )
    {
        getRenderer().addScenegraphModificationListener( modListener );
    }
    
    /**
     * Removes a ScenegraphModificationListener from the List.
     * 
     * @param modListener the ScenegraphModificationsListener to be removed
     */
    public final void removeScenegraphModificationListener( ScenegraphModificationsListener modListener )
    {
        getRenderer().removeScenegraphModificationListener( modListener );
    }
    
    /**
     * Adds a new View to the SceneGraph.
     * 
     * @param view the View to be added
     */
    public final void addView( View view )
    {
        views.add( view );
    }
    
    /**
     * @return this SceneGraph's Views count.
     */
    public final int getNumberOfViews()
    {
        return ( views.size() );
    }
    
    /**
     * @return this SceneGraph's (first) View or null, if no View is present.
     */
    public final View getView()
    {
        if ( views.size() == 0 )
            return ( null );
        
        return ( views.get( 0 ) );
    }
    
    /**
     * @return this SceneGraph's View with the specified index.
     * 
     * @param index the desired View's index in the SceneGraph
     */
    public View getView( int index )
    {
        return ( views.get( index ) );
    }
    
    private final void addBranchGroup( BranchGroup bg )
    {
        branchGroups.add( bg );
        
        bg.setSceneGraph( this );
        
        if ( modListener != null )
            modListener.onBranchGraphAdded( bg );
    }
    
    /**
     * Adds a new BranchGroup to the SceneGraph's Locale.<br>
     * The also created and added RenderPass is automatically linked with the BranchGroup.<br>
     * <br>
     * There're convenience methods, with which you don't need to pass a
     * RenderPassConfigProvider.<br>
     * <br>
     * @see org.xith3d.scenegraph.BranchGroup
     * @see RenderPassConfig
     * @see BaseRenderPassConfig
     * @see #addParallelBranch( BranchGroup )
     * @see #addPerspectiveBranch( BranchGroup )
     * 
     * @param branchGraph the new branch graph to add
     * @param renderPassConfig the configuration for the new RenderPass to add
     * 
     * @return the created RenderPass
     */
    public final RenderPass addBranchGraph( BranchGroup branchGraph, RenderPassConfig renderPassConfig )
    {
        addBranchGroup( branchGraph );
        
        ProjectionPolicy projPoli = renderPassConfig.getProjectionPolicy();
        
        if ( ( projPoli == null ) && ( getView() != null ) )
            projPoli = getView().getProjectionPolicy();
        
        RenderPass renderPass = new RenderPass( branchGraph, renderPassConfig );
        addRenderPass( renderPass );
        
        return ( renderPass );
    }
    
    /**
     * Adds a perspective (projected) RenderPass and links it to the given BranchGroup.<br>
     * <br>
     * This is a convenience method and is functionally equal to:<br>
     * <blockquote>
     *     addBranchGraph( branchGraph, new RenderPassConfig( RenderPassConfigProvider.PERSPECTIVE_PROJECTION ) );<br>
     * </blockquote>
     * 
     * @see org.xith3d.scenegraph.BranchGroup
     * @see #addParallelBranch( BranchGroup )
     * 
     * @param branchGraph the BranchGroup used for the new RenderPass
     * @return the created RenderPass
     */
    public final RenderPass addPerspectiveBranch( BranchGroup branchGraph )
    {
        RenderPassConfig persPassConfig = new BaseRenderPassConfig( ProjectionPolicy.PERSPECTIVE_PROJECTION );
        
        return ( addBranchGraph( branchGraph, persPassConfig ) );
    }
    
    /**
     * Adds a perspective (projected) RenderPass and links it to a new BranchGroup,
     * which also been added to the Locale.<br>
     * <br>
     * This is a convenience method and is functionally equal to:<br>
     * <blockquote>
     *     addBranchGraph( new BranchGroup(), new RenderPassConfig( RenderPassConfigProvider.PERSPECTIVE_PROJECTION ) );<br>
     *     or<br>
     *     addPerspectiveBranch( new BranchGroup() );<br>
     * </blockquote>
     * 
     * @see #addPerspectiveBranch( BranchGroup )
     * @see #addParallelBranch( BranchGroup )
     * 
     * @return the created RenderPass
     */
    public final RenderPass addPerspectiveBranch()
    {
        return ( addPerspectiveBranch( new BranchGroup() ) );
    }
    
    /**
     * Adds a parallel (projected) RenderPass and links it to the given BranchGroup.<br>
     * <br>
     * This is a convenience method and is functionally equal to:<br>
     * <blockquote>
     *     addBranchGraph( branchGraph, new RenderPassConfig( RenderPassConfigProvider.PARALLEL_PROJECTION ) );<br>
     * </blockquote>
     * 
     * @see org.xith3d.scenegraph.BranchGroup
     * @see #addPerspectiveBranch( BranchGroup )
     * 
     * @param branchGraph the BranchGroup used for the new RenderPass
     * @return the created RenderPass
     */
    public final RenderPass addParallelBranch( BranchGroup branchGraph )
    {
        RenderPassConfig paraPassConfig = new BaseRenderPassConfig( ProjectionPolicy.PARALLEL_PROJECTION );
        
        return ( addBranchGraph( branchGraph, paraPassConfig ) );
    }
    
    /**
     * Adds a parallel (projected) RenderPass and links it to a new BranchGroup,
     * which is also been added to the Locale.<br>
     * <br>
     * This is a convenience method and is functionally equal to:<br>
     * <blockquote>
     *     addBranchGraph( branchGraph, new RenderPassConfig( RenderPassConfigProvider.PARALLEL_PROJECTION ) );<br>
     *     or<br>
     *     addParallelBranch( new BranchGroup() );<br>
     * </blockquote>
     * 
     * @see #addParallelBranch( BranchGroup )
     * @see #addPerspectiveBranch( BranchGroup )
     * 
     * @return the created RenderPass
     */
    public final RenderPass addParallelBranch()
    {
        return ( addParallelBranch( new BranchGroup() ) );
    }
    
    /**
     * Removes the given BranchGroup from the SceneGraph.<br>
     * The assotiated RenderPass is also removed from the Renderer.
     * 
     * @param branchGraph the BranchGroup to remove
     */
    public final void removeBranchGraph( BranchGroup branchGraph )
    {
        final List< RenderPass > renderPasses = getRenderer().getRenderPasses( branchGraph );
        
        branchGraph.setSceneGraph( null );
        
        if ( modListener != null )
            modListener.onBranchGraphRemoved( branchGraph );
        
        for ( int i = 0; i < renderPasses.size(); i++ )
            _RNDR_PrivilegedAccess.removeRenderPass( renderPasses.get( i ), getRenderer() );
        
        branchGroups.remove( branchGraph );
    }
    
    /**
     * @return the number of BranchGroups in this SceneGraph.
     */
    public final int getNumberOfBranchGroups()
    {
        return ( branchGroups.size() );
    }
    
    /**
     * @return the total number of children in this group and its subgroups.
     */
    public final long getTotalNumChildren()
    {
        long totalNumChildren = 0L;
        
        for ( int i = 0; i < branchGroups.size(); i++ )
        {
            totalNumChildren += 1 + branchGroups.get( i ).getTotalNumChildren();
        }
        
        return ( totalNumChildren );
    }
    
    /**
     * @return the total number of shapes in this group and its subgroups.
     */
    public final long getTotalNumShapes()
    {
        long totalNumShapes = 0L;
        
        for ( int i = 0; i < branchGroups.size(); i++ )
        {
            totalNumShapes += branchGroups.get( i ).getTotalNumShapes();
        }
        
        return ( totalNumShapes );
    }
    
    /**
     * @return the n-th BranchGroup in this SceneGraph.
     */
    public final BranchGroup getBranchGroup( int index )
    {
        return ( branchGroups.get( index ) );
    }
    
    /**
     * @return the <b>first</b> BranchGroup in this SceneGraph or <i>null</i>.
     */
    public final BranchGroup getBranchGroup()
    {
        if ( branchGroups.size() == 0 )
            return ( null );
        
        return ( getBranchGroup( 0 ) );
    }
    
    private final int findLastBackgroundPass()
    {
        final List< RenderPass > renderPasses = getRenderer().getRenderPasses();
        
        for ( int i = 0; i < renderPasses.size(); i++ )
        {
            final RenderPass rp = renderPasses.get( i );
            
            if ( !( rp instanceof BackgroundRenderPass ) )
                return ( i - 1 );
        }
        
        return ( renderPasses.size() - 1 );
    }
    
    private final int findLastNormalPass()
    {
        final int lastBackgroundPass = findLastBackgroundPass();
        
        final List< RenderPass > renderPasses = getRenderer().getRenderPasses();
        
        for ( int i = lastBackgroundPass + 1; i < renderPasses.size(); i++ )
        {
            final RenderPass rp = renderPasses.get( i );
            
            if ( rp instanceof ForegroundRenderPass )
                return ( i - 1 );
        }
        
        //if ( renderPasses.size() > lastBackgroundPass + 1 )
            return ( renderPasses.size() - 1 );
        //else
        //    return ( -1 );
    }
    
    /**
     * Removes the given RenderPass from the SceneGraph's Renderer.<br>
     * The assotiated BranchGroup is also removed.
     * 
     * @param renderPass the RenderPass to remove
     */
    public final void removeRenderPass( RenderPass renderPass )
    {
        removeBranchGraph( renderPass.getBranchGroup() );
    }
    
    /**
     * Adds a RenderPass to the SceneGraph's Renderer.<br>
     * The BranchGroup assotiated to the RenderPass is also added to the
     * SceneGraph.
     * 
     * @param renderPass the new RenderPass to add
     * 
     * @return the RenderPass'es BranchGroup to add further children to
     */
    public final BranchGroup addRenderPass( RenderPass renderPass )
    {
        if ( !branchGroups.contains( renderPass.getBranchGroup() ) )
        {
            addBranchGroup( renderPass.getBranchGroup() );
        }
        
        if ( getRenderer().getRenderPasses().contains( renderPass ) )
            return ( renderPass.getBranchGroup() );
        
        //System.out.println( renderPass + ", " + findLastBackgroundPass() );
        
        if ( renderPass instanceof BackgroundRenderPass )
        {
            _RNDR_PrivilegedAccess.addRenderPass( findLastBackgroundPass() + 1, renderPass, getRenderer() );
        }
        else if ( renderPass instanceof ForegroundRenderPass )
        {
            _RNDR_PrivilegedAccess.addRenderPass( renderPass, getRenderer() );
        }
        else
        {
            _RNDR_PrivilegedAccess.addRenderPass( findLastNormalPass() + 1, renderPass, getRenderer() );
        }
        
        /*
        int i = 0;
        for ( RenderPass rp: getRenderer().getRenderPasses() )
        {
            System.out.println( i++ + ", " + rp );
        }
        */
        
        return ( renderPass.getBranchGroup() );
    }
    
    /**
     * Adds a RenderPass to the SceneGraph at first position.<br>
     * The BranchGroup assotiated to the RenderPass is also added to the
     * SceneGraph.
     * 
     * @param renderPass the new RenderPass to add
     * 
     * @return the RenderPass'es BranchGroup to add further children to
     */
    public final BranchGroup addRenderPassFirst( RenderPass renderPass )
    {
        if ( !branchGroups.contains( renderPass.getBranchGroup() ) )
        {
            addBranchGroup( renderPass.getBranchGroup() );
        }
        
        _RNDR_PrivilegedAccess.addRenderPass( 0, renderPass, getRenderer() );
        
        return ( renderPass.getBranchGroup() );
    }
    
    /**
     * Removes all children from the SceneGraph.
     */
    public final void removeAllBranchGraphs()
    {
        if ( modListener != null )
        {
            for ( int i = 0; i < branchGroups.size(); i++ )
            {
                modListener.onBranchGraphRemoved( branchGroups.get( i ) );
            }
        }
        
        branchGroups.clear();
        _RNDR_PrivilegedAccess.removeAllRenderPasses( getRenderer() );
    }
    
    private final Canvas3D findFirstCanvas()
    {
        final int nv = getNumberOfViews();
        for ( int i = 0; i < nv; i++ )
        {
            final View view = getView( i );
            
            if ( view.numCanvas3Ds() > 0 )
                return ( view.getCanvas3D( 0 ) );
        }
        
        return ( null );
    }
    
    /**
     * Adds a HUD to the SceneGraph and sets all necessary properties.
     * 
     * @param hud the HUD to be added to the SceneGraph
     */
    public final RenderPass addHUD( HUD hud )
    {
        final RenderPass renderPass = hud.getRenderPass();
        
        addRenderPass( renderPass );
        
        huds.add( hud );
        
        final Canvas3D canvas = findFirstCanvas();
        if ( canvas != null )
        {
            hud.connect( canvas );
        }
        
        if ( InputSystem.hasInstance() )
        {
            hud.connect( InputSystem.getInstance() );
        }
        
        return ( renderPass );
    }
    
    /**
     * Returns the HUD (first) attached to the SceneGraph id any.
     * 
     * @return the (first) hud or null
     */
    public final HUD getHUD()
    {
        return ( huds.isEmpty() ? null : huds.get( 0 ) );
    }
    
    /**
     * Removes a HUD from the SceneGraph.
     * 
     * @param hud the HUD to be removed from the SceneGraph
     * @param inputMgr the InputManager to get input events from
     */
    public final void removeHUD( HUD hud )
    {
        removeRenderPass( hud.getRenderPass() );
        
        huds.remove( hud );
        
        final Canvas3D canvas = findFirstCanvas();
        if ( canvas != null )
        {
            hud.disconnect( canvas );
        }
        
        if ( InputSystem.hasInstance() )
        {
            hud.disconnect( InputSystem.getInstance() );
        }
    }
    
    public SceneGraph( Renderer renderer )
    {
        this.renderer = renderer;
    }
}
