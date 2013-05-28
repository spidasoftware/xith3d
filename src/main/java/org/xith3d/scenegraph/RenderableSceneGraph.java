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

import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.CanvasBag;
import org.xith3d.render.RenderPass;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.render.Renderer;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;
import org.xith3d.ui.hud.HUD;

/**
 * The RenderableSceneGraph interface simply unites the SceneGraph and
 * CanvasBag interfaces.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface RenderableSceneGraph extends CanvasBag
{
    /**
     * @return this SceneGraph's Renderer.
     */
    public Renderer getRenderer();
    
    /**
     * Adds a new ScenegraphModificationListener to the List.
     * It will be notified of any scenegraph change at runtime.
     * 
     * @param modListener the new ScenegraphModificationsListener to add
     */
    public void addScenegraphModificationListener( ScenegraphModificationsListener modListener );
    
    /**
     * Removes a ScenegraphModificationListener from the List.
     * 
     * @param modListener the ScenegraphModificationsListener to be removed
     */
    public void removeScenegraphModificationListener( ScenegraphModificationsListener modListener );
    
    /**
     * @return the <b>first</b> BranchGroup in this environemnt or <i>null</i>.
     */
    public BranchGroup getBranchGroup();
    
    /**
     * @return this SceneGraph's (first) View or null, if no View is present.
     */
    public View getView();
    
    /**
     * @return this SceneGraph's View with the specified index.
     * 
     * @param index the desired View's index in the SceneGraph
     */
    public View getView( int index );
    
    /**
     * Adds a new View to the SceneGraph.
     * 
     * @param view the View to be added
     */
    public void addView( View view );
    
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
    public RenderPass addBranchGraph( BranchGroup branchGraph, RenderPassConfig renderPassConfig );
    
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
    public RenderPass addPerspectiveBranch( BranchGroup branchGraph );
    
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
    public RenderPass addPerspectiveBranch();
    
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
    public RenderPass addParallelBranch( BranchGroup branchGraph );
    
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
    public RenderPass addParallelBranch();
    
    /**
     * Removes the given BranchGroup from the SceneGraph.<br>
     * The assotiated RenderPass is also removed from the Renderer.
     * 
     * @param branchGraph the BranchGroup to remove
     */
    public void removeBranchGraph( BranchGroup branchGraph );
    
    /**
     * Removes the given RenderPass from the SceneGraph's Renderer.<br>
     * The assotiated BranchGroup is also removed.
     * 
     * @param renderPass the RenderPass to remove
     */
    public void removeRenderPass( RenderPass renderPass );
    
    /**
     * Adds a RenderPass to the SceneGraph's Renderer.<br>
     * The BranchGroup assotiated to the RenderPass is also added to the
     * SceneGraph.
     * 
     * @param renderPass the new RenderPass to add
     * 
     * @return the RenderPass'es BranchGroup to add further children to
     */
    public BranchGroup addRenderPass( RenderPass renderPass );
    
    /**
     * Adds a RenderPass to the SceneGraph at first position.<br>
     * The BranchGroup assotiated to the RenderPass is also added to the
     * SceneGraph.
     * 
     * @param renderPass the new RenderPass to add
     * 
     * @return the RenderPass'es BranchGroup to add further children to
     */
    public BranchGroup addRenderPassFirst( RenderPass renderPass );
    
    /**
     * Removes all children from the SceneGraph.
     */
    public void removeAllBranchGraphs();
    
    /**
     * Adds a HUD to the SceneGraph and sets all necessary properties.
     * 
     * @param hud the HUD to be added to the SceneGraph
     * @param inputMgr the InputManager to get input events from
     */
    public RenderPass addHUD( HUD hud );
    
    /**
     * Removes a HUD from the SceneGraph.
     * 
     * @param hud the HUD to be removed from the SceneGraph
     */
    public void removeHUD( HUD hud );
}
