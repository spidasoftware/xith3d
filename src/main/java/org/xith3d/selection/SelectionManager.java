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
package org.xith3d.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.devices.components.MouseButtons;
import org.jagatoo.input.events.MouseButtonClickedEvent;
import org.jagatoo.input.events.MouseButtonEvent;
import org.jagatoo.input.events.MouseButtonPressedEvent;
import org.jagatoo.input.events.MouseButtonReleasedEvent;
import org.jagatoo.input.events.MouseMovedEvent;
import org.jagatoo.input.events.MouseWheelEvent;
import org.jagatoo.input.listeners.MouseListener;
import org.openmali.vecmath2.Point2i;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.picking.AllPickListener;
import org.xith3d.picking.PickResult;
import org.xith3d.picking.PickingLibrary;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderPass;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.SceneGraph;
import org.xith3d.ui.hud.listeners.HUDPickMissedListener;
import org.xith3d.ui.hud.utils.HUDPickResult.HUDPickReason;

/**
 * @author Mathias Henze (aka cylab)
 */
public class SelectionManager implements MouseListener, AllPickListener, HUDPickMissedListener
{
    public static final int HUD_PICK_MISSED_MASK = HUDPickReason.BUTTON_PRESSED_MASK | HUDPickReason.BUTTON_RELEASED_MASK;
    
    private BranchGroup selectionLayer = new BranchGroup();
    private MovementConstraints movementContraints = new ViewConstraints();
    private HashSet<Selectable> selection = new HashSet<Selectable>();
    private HashSet<Selectable> selectedContext = new HashSet<Selectable>();
    private BranchGroup pickableBranch;
    private RenderPassConfig pickablePassConfig;
    private ArrayList<GroupNode> pickableGroups= new ArrayList<GroupNode>();
    private Canvas3D canvas;
    private ArrayList<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
    private ContextMenuProvider contextMenuProvider;
    private Point2i pickLocation = new Point2i();
    private Point2i lastMouseCoords = new Point2i();
    private Point3f lastPosition = new Point3f();
    private MouseButton contextMenuTrigger = MouseButtons.RIGHT_BUTTON;
    private MouseButton movementTrigger = MouseButtons.LEFT_BUTTON;
    private MouseButton selectionTrigger = MouseButtons.LEFT_BUTTON;
    private boolean movementTriggerDown;

    public Point2i getLastMouseCoords()
    {
        return lastMouseCoords;
    }

    public Point3f getLastPosition()
    {
        return lastPosition;
    }

    public MouseButton getContextMenuTrigger()
    {
        return contextMenuTrigger;
    }

    public void setContextMenuTrigger( MouseButton contextMenuTrigger )
    {
        this.contextMenuTrigger = contextMenuTrigger;
    }

    public MouseButton getSelectionTrigger()
    {
        return selectionTrigger;
    }

    public void setSelectionTrigger( MouseButton selectionTrigger )
    {
        this.selectionTrigger = selectionTrigger;
    }

    public BranchGroup getSelectionLayer()
    {
        return ( selectionLayer );
    }

    public MouseButton getMovementTrigger()
    {
        return movementTrigger;
    }

    public void setMovementTrigger( MouseButton movementTrigger )
    {
        this.movementTrigger = movementTrigger;
    }

    public void addSelectionListener( SelectionListener listener )
    {
        selectionListeners.add( listener );
    }

    public boolean removeSelectionListener( SelectionListener listener )
    {
        return ( selectionListeners.remove( listener ) );
    }

    public void setContextMenuProvider( ContextMenuProvider provider )
    {
        contextMenuProvider = provider;
    }

    public ContextMenuProvider getContextMenuProvider()
    {
        return contextMenuProvider;
    }

    public MovementConstraints getMovementConstraints()
    {
        return ( movementContraints );
    }

    public void setMovementConstraints( MovementConstraints movementConstraints )
    {
        this.movementContraints = movementConstraints;
    }

    public void bind( BranchGroup pickableBranch, RenderPassConfig rpc, Canvas3D canvas )
    {
        SceneGraph sceneGraph = pickableBranch.getSceneGraph();
        if ( sceneGraph == null )
        {
            throw new IllegalStateException( "You can only bind a SelectionManager to a branch contained in a SceneGraph!" );
        }
        this.pickableBranch = pickableBranch;
        this.pickablePassConfig = rpc;
        this.pickableGroups.clear();
        this.pickableGroups.add( pickableBranch );
        this.canvas = canvas;
        
        // just to be sure...
        sceneGraph.removeBranchGraph( selectionLayer );
        selectionLayer.removeAllChildren();
        
        // TODO: do I have to remove the selection pass before?
        RenderPass selectionPass = sceneGraph.addPerspectiveBranch( selectionLayer );
        selectionPass.setLayeredModeForced( true );
    }

    public void bind( BranchGroup pickableBranch, Canvas3D canvas )
    {
        bind( pickableBranch, null, canvas );
    }

    public void unbind()
    {
        // TODO: do I have to remove the selection pass too?
        SceneGraph sceneGraph = pickableBranch.getSceneGraph();
        if ( sceneGraph != null )
        {
            sceneGraph.removeBranchGraph( selectionLayer );
            selectionLayer.removeAllChildren();

            this.pickableBranch = null;
            this.canvas = null;
            this.pickablePassConfig = null;
        }
    }

    @SuppressWarnings( "unchecked" )
    protected void changeSelection( Selectable selectable, boolean replace )
    {
        if ( selection.contains( selectable ) )
        {
            return;
        }

        if ( replace )
        {
            if ( selection.size() > 0 )
            {
                // unselect the current selected nodes
                // TODO: allow for programmatic selection!
                for ( Iterator<Selectable> it = selection.iterator(); it.hasNext();)
                {
                    Selectable sel = it.next();
                    sel.setSelected( this, false );
                }
                selection.clear();
            }

            if ( selectedContext.size() > 0 )
            {
                for ( Iterator<Selectable> it = selectedContext.iterator(); it.hasNext();)
                {
                    Selectable sel = it.next();
                    sel.setSelectedContext( this, false );
                }
                selectedContext.clear();
            }

            if ( ( selection.size() > 0 ) || ( selectedContext.size() > 0 ) )
            {
                // notify the listeners
                for ( int i = 0; i < selectionListeners.size(); i++ )
                {
                    SelectionListener listener = selectionListeners.get( i );
                    listener.selectionChanged( (List<Selectable>) Collections.EMPTY_LIST, (List<Selectable>) Collections.EMPTY_LIST );
                }
            }
        }
        
        // return with a cleared selection
        if ( selectable == null )
            return;
        
        // make new selection
        selection.add( selectable );
        @SuppressWarnings("unused")
		Selectable selectionBound = null;
        Node current = selectable.getNode();
        while ( ( current = current.getParent() ) != null )
        {
            if ( current instanceof Selectable )
            {
                Selectable currentSelectable = (Selectable) current;
                if ( !selection.contains( currentSelectable ) )
                    selectedContext.add( currentSelectable );
                if ( currentSelectable.isSelectionBound() )
                {
                    selectionBound = currentSelectable;
                    break;
                }
                break;
            }

            Selectable currentSelectable = (Selectable) current.getUserData( Selectable.class );

            if ( ( currentSelectable != null ) )
            {
                if ( !selection.contains( currentSelectable ) )
                    selectedContext.add( currentSelectable );
                if ( currentSelectable.isSelectionBound() )
                {
                    selectionBound = currentSelectable;
                    break;
                }
                break;
            }
        }

        if ( selectable.getNode() instanceof Group )
        {
            findSelectableChilden( (Group) selectable.getNode() );
        }
        
        ArrayList<Selectable> tmpSelection = new ArrayList<Selectable>( selection.size() );
        ArrayList<Selectable> tmpSelectedContext = new ArrayList<Selectable>( selection.size() );
        tmpSelection.addAll( selection );
        tmpSelectedContext.addAll( selectedContext );
        
        // TODO: allow for programmatic selection!
        for ( int i = 0; i < tmpSelection.size(); i++ )
        {
            Selectable sel = tmpSelection.get( i );
            sel.setSelected( this, true );
        }
        
        for ( int i = 0; i < tmpSelectedContext.size(); i++ )
        {
            Selectable sel = tmpSelectedContext.get( i );
            sel.setSelectedContext( this, true );
        }

        for ( int i = 0; i < selectionListeners.size(); i++ )
        {
            SelectionListener listener = selectionListeners.get( i );
            listener.selectionChanged( tmpSelection, tmpSelectedContext );
        }
    }

    public boolean isBound()
    {
        return ( ( pickableBranch != null ) && ( canvas != null ) );
    }

    private Selectable findSelectable( PickResult pickResult )
    {
        Node current = pickResult.getPickHostOrNode();
        do
        {
            if ( current instanceof Selectable )
                return ( (Selectable) current );

            Selectable currentSelectable = (Selectable) current.getUserData( Selectable.class );
            if ( currentSelectable != null )
                return ( currentSelectable );
        } while ( ( current = current.getParent() ) != null );

        return ( null );
    }

    private void findSelectableChilden( Group group )
    {
        int l = group.numChildren();

        for ( int i = 0; i < l; i++ )
        {
            boolean skipChildren= false;
            Node current = group.getChild( i );
            if ( ( current instanceof Selectable ) )
            {
                skipChildren = true;
                if ( !selection.contains( current ) )
                    selectedContext.add( (Selectable) current );
            }

            Selectable currentSelectable = (Selectable) current.getUserData( Selectable.class );

            if ( ( currentSelectable != null ) )
            {
                skipChildren = true;
                if ( !selection.contains( currentSelectable ) )
                    selectedContext.add( currentSelectable );
            }

            if ( !skipChildren && current instanceof Group )
            {
                findSelectableChilden( (Group) current );
            }
        }
    }

    public void onMouseButtonPressed( MouseButtonPressedEvent e, MouseButton button )
    {
        lastMouseCoords.set( e.getX(), e.getY() );
    }

    public void onHUDPickMissed( MouseButton button, int x, int y, HUDPickReason pickReason, long when, long meta )
    {
        if ( isBound() )
        {
            if ( ( button == selectionTrigger ) || ( button == contextMenuTrigger ) )
            {
                final int mouseX = lastMouseCoords.getX();
                final int mouseY = lastMouseCoords.getY();
                pickLocation.set( mouseX, mouseY );

                if ( pickablePassConfig != null )
                    PickingLibrary.pickAll( pickablePassConfig, pickableGroups, canvas, button, mouseX, mouseY, this, null );
                else
                    PickingLibrary.pickAll( pickableBranch, canvas, button, mouseX, mouseY, this );
            }
        }
    }

    public void onMouseButtonReleased( MouseButtonReleasedEvent e, MouseButton button )
    {
    }

    public void onMouseButtonClicked( MouseButtonClickedEvent e, MouseButton button, int clickCount )
    {
    }

    public void onMouseButtonStateChanged( MouseButtonEvent e, MouseButton button, boolean state )
    {
        if ( button == movementTrigger )
            movementTriggerDown = state;
    }

    public void onMouseMoved( MouseMovedEvent e, int x, int y, int dx, int dy )
    {
        if ( movementTriggerDown && ( selection.size() > 0 ) )
        {
            ArrayList<Selectable> tmpSelection = new ArrayList<Selectable>( selection.size() );
            ArrayList<Selectable> tmpSelectedContext = new ArrayList<Selectable>( selection.size() );
            tmpSelection.addAll( selection );
            tmpSelectedContext.addAll( selectedContext );
            Point3f newPosition = Point3f.fromPool();
            Vector3f delta = Vector3f.fromPool();
            if ( movementContraints != null )
            {
                movementContraints.computeNewPosition( pickablePassConfig, canvas, x, y, lastPosition, newPosition );
                delta.set( newPosition );
                delta.sub( lastPosition );
                lastPosition.set( newPosition );
            }
            else
            {
                delta.set( x - lastMouseCoords.getX(), y - lastMouseCoords.getY(), 0f );
            }

            for ( Iterator<Selectable> it = selection.iterator(); it.hasNext();)
            {
                Selectable sel = it.next();
                sel.onMoved( this, delta );
            }

            for ( int i = 0; i < selectionListeners.size(); i++ )
            {
                SelectionListener listener = selectionListeners.get( i );
                listener.selectionMoved( tmpSelection, tmpSelectedContext, delta );
            }
            Point3f.toPool( newPosition );
            Vector3f.toPool( delta );
        }

        lastMouseCoords.set( x, y );
    }

    public void onMouseWheelMoved( MouseWheelEvent e, int wheelDelta )
    {
    }

    public void onPickingMissed( Object userObject, long pickTime )
    {
        changeSelection( null, true );
    }

    public void onObjectsPicked( List<PickResult> pickResults, Object userObject, long pickTime )
    {
        PickResult nearest = null;
        Selectable nearestSelectable = null;

        for ( int i = 0; i < pickResults.size(); i++ )
        {
            PickResult pickResult = pickResults.get( i );
            Selectable pickedSelectable = findSelectable( pickResult );

            if ( ( pickedSelectable != null ) && ( ( nearest == null ) || ( pickResult.getMinimumDistance() < nearest.getMinimumDistance() ) ) )
            {
                nearest = pickResult;
                nearestSelectable = pickedSelectable;
            }
        }

        if ( nearest != null )
        {
            lastPosition.set( nearest.getPos() );
            changeSelection( nearestSelectable, true );
            if ( nearest.getButton() == contextMenuTrigger && contextMenuProvider != null )
            {
                ArrayList<Selectable> tmpSelection = new ArrayList<Selectable>( selection.size() );
                ArrayList<Selectable> tmpSelectedContext = new ArrayList<Selectable>( selection.size() );
                tmpSelection.addAll( selection );
                tmpSelectedContext.addAll( selectedContext );
                contextMenuProvider.showContextMenu( pickLocation, nearest.getPos(), tmpSelection, tmpSelectedContext );
            }
        }
        else
        {
            changeSelection( null, true );
        }
    }
}
