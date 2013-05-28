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

import java.util.List;

import org.xith3d.picking.PickRequest;
import org.xith3d.render.preprocessing.OrderedState;
import org.xith3d.render.preprocessing.sorting.BackToFrontByBoundingSphereAndEyeRayIntersectionRenderBinSorter;
import org.xith3d.render.preprocessing.sorting.FrontToBackRenderBinSorter;
import org.xith3d.render.preprocessing.sorting.OrderedStateRenderBinSorter;
import org.xith3d.render.preprocessing.sorting.RenderBinSorter;
import org.xith3d.render.preprocessing.sorting.StateRenderBinSorter;
import org.xith3d.render.preprocessing.sorting.ZValueRenderBinSorter;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;
import org.xith3d.sound.SoundProcessor;

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
public abstract class Renderer
{
    public static enum OpaqueSortingPolicy
    {
        /**
         * No sorting is done
         */
        SORT_NONE,
        
        /**
         * Opaque shapes are sorted by states (minimize render states
         * changes for maximum performance)
         */
        SORT_BY_STATES,
        
        /**
         * Opaque shapes are sorted by states (minimize render states
         * changes for maximum performance) with respect to the
         * Node's {@link OrderedState}.
         */
        SORT_BY_STATES_ORDERED,
        
        /**
         * Opaque shapes are sorted from front to back
         */
        SORT_FRONT_TO_BACK,
        
        /**
         * Opaque shapes are sorted by z-value
         */
        SORT_BY_Z_VALUE,
        
        /**
         * Indicates, that a custom RenderBinSorter is being used
         */
        CUSTOM,
        ;
        
        public RenderBinSorter getSorter()
        {
            switch ( this )
            {
                case SORT_NONE:
                    return ( null );
                    
                case SORT_BY_STATES:
                    return ( new StateRenderBinSorter() );
                    
                case SORT_BY_STATES_ORDERED:
                    return ( new OrderedStateRenderBinSorter() );
                    
                case SORT_FRONT_TO_BACK:
                    return ( new FrontToBackRenderBinSorter() );
                    
                case SORT_BY_Z_VALUE:
                    return ( new ZValueRenderBinSorter() );
            }
            
            throw new IllegalArgumentException( "No RenderBinSorter for " + this );
        }
    }
    
    public static enum TransparentSortingPolicy
    {
        /**
         * No sorting is done
         */
        SORT_NONE,
        
        /**
         * Transparent shapes are sorted front-to-back
         */
        SORT_FRONT_TO_BACK,
        
        /**
         * Transparent shapes are sorted by bounding spheres and eye
         * ray intersection
         */
        SORT_BOUNDING_SPHERE_AND_EYE_RAY_INTERSECTION,
        
        /**
         * Opaque shapes are sorted by z-value
         */
        SORT_BY_Z_VALUE,
        
        /**
         * Indicates, that a custom REnderBinSorter is being used
         */
        CUSTOM,
        ;
        
        public RenderBinSorter getSorter()
        {
            switch ( this )
            {
                case SORT_NONE:
                    return ( null );
                    
                case SORT_FRONT_TO_BACK:
                    return ( new FrontToBackRenderBinSorter() );
                    
                case SORT_BOUNDING_SPHERE_AND_EYE_RAY_INTERSECTION:
                    return ( new BackToFrontByBoundingSphereAndEyeRayIntersectionRenderBinSorter() );
                    
                case SORT_BY_Z_VALUE:
                    return ( new ZValueRenderBinSorter() );
                    
            }
            
            throw new IllegalArgumentException( "No RenderBinSorter for " + this );
        }
    }
    
    
    protected static int renderersWorking = 0;
    
    /**
     * @deprecated use {@link SoundProcessor#getInstance()} instead to set the sound driver.
     */
    @Deprecated
    public abstract SoundProcessor getSoundProcessor();
    
    /**
     * Adds a new ScenegraphModificationListener to the List.
     * It will be notified of any scenegraph change at runtime.
     * 
     * @param modListener the new ScenegraphModificationsListener to add
     */
    public abstract void addScenegraphModificationListener( ScenegraphModificationsListener modListener );
    
    /**
     * Removes a ScenegraphModificationListener from the List.
     * 
     * @param modListener the ScenegraphModificationsListener to be removed
     */
    public abstract void removeScenegraphModificationListener( ScenegraphModificationsListener modListener );
    
    /**
     * @return a List of all registered ScenegraphModificationListeners
     */
    public abstract List< ScenegraphModificationsListener > getScenegraphModificationListeners();
    
    /**
     * Adds a Canvas3D to the Renderer.
     * 
     * @param canvas
     */
    protected abstract void addCanvas3D( Canvas3D canvas );
    
    /**
     * Removes a Canvas3D from the Renderer.
     * 
     * @param canvas
     */
    protected abstract void removeCanvas3D( Canvas3D canvas );
    
    /**
     * Removes a Canvas3D from the Renderer.
     * 
     * @param i
     * 
     * @return the removed Canvas3D
     */
    protected abstract Canvas3D removeCanvas3D( int i );
    
    /**
     * @return the number of Canvas3Ds in this Renderer.
     */
    public abstract int getNumberOfCanvas3Ds();
    
    /**
     * @return the n-th Canvas3D from the Renderer.
     */
    public abstract Canvas3D getCanvas3D( int index );
    
    /**
     * Adds a RenderTarget to the Renderer.
     * 
     * @param renderTarget
     * @param pass
     */
    public abstract void addRenderTarget( RenderTarget renderTarget, RenderPass pass );
    
    /**
     * Adds a RenderTarget to the Renderer.
     * 
     * @param renderTarget
     * @param passConfig
     */
    public abstract RenderPass addRenderTarget( RenderTarget renderTarget, RenderPassConfig passConfig );
    
    /**
     * Removes a RenderTarget from the Renderer.
     * 
     * @param renderTarget
     */
    public abstract void removeRenderTarget( RenderTarget renderTarget );
    
    /**
     * @return a list of all registered RenderTargets.
     */
    public abstract List< RenderTarget > getRenderTargets();
    
    /**
     * Adds a new RenderPass to this Renderer at the end of the list.
     * In layered rendering mode it is important, that the first added
     * pass will be rendered first and the last added rendered last.
     * 
     * @param renderPass the RenderPass to add
     * @return the given RenderPass object
     */
    protected abstract RenderPass addRenderPass( RenderPass renderPass );
    
    /**
     * Adds a new RenderPass to this Renderer at the given position.
     * In layered rendering mode it is important, that the first pass
     * in the list will be rendered first and the last rendered last.
     * 
     * @param index the position the RenderPass is to be placed at in the list
     * @param renderPass the RenderPass to add
     * @return the given RenderPass object
     */
    protected abstract RenderPass addRenderPass( int index, RenderPass renderPass );
    
    /**
     * Removes the RenderPass from this Renderer, which is assotiated to the given BranchGroup.
     * 
     * @param branchGroup the BranchGroup, which's RenderPass is to be removed
     * @return true, if the RenderPass existed in the Renderer
     */
    protected abstract boolean removeRenderPasses( BranchGroup branchGroup );
    
    /**
     * Removes the RenderPass from this Renderer.
     * 
     * @param renderPass the RenderPass to be removed
     * @return true, if the RenderPass existed in the Renderer
     */
    protected abstract boolean removeRenderPass( RenderPass renderPass );
    
    /**
     * Removes the RenderPass from this Renderer.
     * 
     * @param index the index of the RenderPass to be removed
     * @return true, if the RenderPass existed in the Renderer
     */
    protected abstract boolean removeRenderPass( int index );
    
    /**
     * Removes all RenderPasses from this Renderer.
     */
    protected abstract void removeAllRenderPasses();
    
    /**
     * @return the number of RenderPasses registered in this Renderer.
     */
    public abstract int getRenderPassesCount();
    
    /**
     * @return the List of RenderPasses registered here with this BranchGroup.
     * 
     * @param branchGroup the BranchGroup the RenderPasses are linked with
     */
    public abstract List< RenderPass > getRenderPasses( BranchGroup branchGroup );
    
    /**
     * @return the RenderPass registered here with this index.
     * 
     * @param index the index of the desired RenderPass
     */
    public abstract RenderPass getRenderPass( int index );
    
    /**
     * @return the list of RenderPasses registered with this Renderer.
     */
    public abstract List< RenderPass > getRenderPasses();
    
    /**
     * Sets if the rendering is done in layered mode or not.
     * 
     * @param layeredMode if true, rendering will be done in layered mode
     */
    public abstract void setLayeredMode( boolean layeredMode );
    
    /**
     * @return if the rendering is done in layered mode.
     */
    public abstract boolean isLayeredMode();
    
    /**
     * Sets the opaque RenderBinSorter for this Renderer.<br>
     * This automatically sets the sorting policy to CUSTOM.
     * 
     * @see #setOpaqueSortingPolicy(org.xith3d.render.Renderer.OpaqueSortingPolicy)
     * 
     * @param sorter the RenderBinSorter to use for opaque shapes
     */
    public abstract void setOpaqueSorter( RenderBinSorter sorter );
    
    /**
     * @return sorter the RenderBinSorter to use for opaque shapes
     * 
     * @see #getOpaqueSortingPolicy()
     */
    public abstract RenderBinSorter getOpaqueSorter();
    
    /**
     * Sets the transparent RenderBinSorter for this Renderer.<br>
     * This automatically sets the sorting policy to CUSTOM.
     * 
     * @param sorter the RenderBinSorter to use for transparent shapes
     */
    public abstract void setTransparentSorter( RenderBinSorter sorter );
    
    /**
     * @return sorter the RenderBinSorter to use for transparent shapes
     * 
     * @see #getTransparentSortingPolicy()
     */
    public abstract RenderBinSorter getTransparentSorter();
    
    /**
     * Sets the opaque sorting policy for this Renderer.
     * 
     * @param policy the new policy
     */
    public abstract void setOpaqueSortingPolicy( OpaqueSortingPolicy policy );
    
    /**
     * @return the current opaque sorting policy for this Renderer
     */
    public abstract OpaqueSortingPolicy getOpaqueSortingPolicy();
    
    /**
     * Sets the transparency sorting policy for this Renderer.
     * 
     * @param policy the new policy
     */
    public abstract void setTransparentSortingPolicy( TransparentSortingPolicy policy );
    
    /**
     * @return the current transparency sorting policy for this Renderer.
     */
    public abstract TransparentSortingPolicy getTransparentSortingPolicy();
    
    /**
     * @return the last used frame-id
     */
    public abstract long getLastFrameId();
    
    /**
     * Adds a PickRequest to the Renderer.<br>
     * It is handled with the next rendered frame.
     * 
     * @param pickRequest
     */
    protected abstract void addPickRequest( PickRequest pickRequest );
    
    /**
     * Renders one frame on a specified universe and canvas.
     * 
     * @param renderPasses
     * @param groupsLists
     * @param canvas
     * @param nanoTime
     * @param nanoStep
     */
    public abstract long renderOnce( List< RenderPass > renderPasses, List< ? extends List< GroupNode >> groupsLists, Canvas3D canvas, long nanoTime, long nanoStep );
    
    /**
     * Renders one frame on a specified universe and canvas.
     * 
     * @param renderPass
     * @param group
     * @param canvas
     * @param nanoTime
     * @param nanoStep
     */
    public abstract long renderOnce( RenderPass renderPass, GroupNode group, Canvas3D canvas, long nanoTime, long nanoStep );
    
    /**
     * Renders one frame on a specified universe and canvas.
     * 
     * @param renderPass
     * @param canvas
     */
    public abstract long renderOnce( RenderPass renderPass, Canvas3D canvas, long nanoTime, long nanoStep );
    
    /**
     * Renders the next frame to the given Canvas3D.
     * 
     * @param canvas the canvas to be rendered
     */
    public abstract long renderOnce( Canvas3D canvas, long nanoTime, long nanoStep );
    
    /**
     * Renders a single frame to all Canvas3Ds.
     * This will collect all the renderable atoms and invoke the renderer.
     */
    public abstract long renderOnce( View view, long nanoTime, long nanoStep );
    
    /**
     * Renders a single frame to all Canvas3Ds.
     * This will collect all the renderable atoms and invoke the renderer.
     */
    public abstract long renderOnce( RenderPass renderPass, GroupNode group, long nanoTime, long nanoStep );
    
    /**
     * Renders a single frame to all Canvas3Ds.
     * This will collect all the renderable atoms and invoke the renderer.
     */
    public abstract long renderOnce( RenderPass renderPass, long nanoTime, long nanoStep );
    
    /**
     * Renders a single frame to all Canvas3Ds.
     * This will collect all the renderable atoms and invoke the renderer.
     */
    public abstract long renderOnce( long nanoTime, long nanoStep );
    
    /**
     * @return the number of shapes actually being rendered
     */
    public abstract long getNumRenderedShapes();
    
    /**
     * @return the number of rendered triangles
     */
    public abstract long getNumRenderedTriangles();
    
    /**
     * @return true, if the view is currently rendering a frame
     */
    public abstract boolean isRendering();
}
