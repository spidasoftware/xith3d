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
package org.xith3d.picking;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.spatial.polygons.Triangle;
import org.openmali.vecmath2.Ray3f;
import org.openmali.vecmath2.Vertex3f;
import org.xith3d.render.Canvas3D;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.utility.general.CircularArray;
import org.xith3d.utility.general.SortableList;

/**
 * Simply stores instances of picking related objects.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public final class PickPool
{
    private static CircularArray< PickRequest > pickRequests = new CircularArray< PickRequest >( 32 );
    private static CircularArray< List< GroupNode >> groupLists = new CircularArray< List< GroupNode > >( 128 );
    private static CircularArray< PickRay > pickRays = new CircularArray< PickRay >( 128 );
    private static CircularArray< Ray3f > rays = new CircularArray< Ray3f >( 128 );
    private static CircularArray< Triangle > triangles = new CircularArray< Triangle >( 128 );
    private static CircularArray< SortableList< PickResult >> prLists = new CircularArray< SortableList< PickResult > >( 128 );
    private static CircularArray< Stack< Node >> nodeStacks = new CircularArray< Stack< Node > >( 128 );
    private static CircularArray< PickResult > pickResults = new CircularArray< PickResult >( 512 );
    
    public static PickRequest allocatePickRequest( org.xith3d.render.RenderPass renderPass, List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, Object pl, Object userObject, boolean pickAll )
    {
        synchronized ( pickRequests )
        {
            final PickRequest preq;
            
            if ( pickRequests.isEmpty() )
            {
                preq = new PickRequest();
            }
            else
            {
                preq = pickRequests.pop();
            }
            
            if ( groups.size() == 1 )
            {
                preq.init( renderPass, (GroupNode)groups.get( 0 ), canvas, button, mouseX, mouseY, pl, userObject, pickAll );
            }
            else
            {
                new Error( "Picking on more than one group is not yet supported" ).printStackTrace();
            }
            
            return ( preq );
        }
    }
    
    public static PickRequest allocatePickRequest( org.xith3d.render.RenderPass renderPass, GroupNode group, Canvas3D canvas, MouseButton button, int mouseX, int mouseY, Object pl, Object userObject, boolean pickAll )
    {
        synchronized ( pickRequests )
        {
            final PickRequest preq;
            
            if ( pickRequests.isEmpty() )
            {
                preq = new PickRequest();
            }
            else
            {
                preq = pickRequests.pop();
            }
            
            preq.init( renderPass, group, canvas, button, mouseX, mouseY, pl, userObject, pickAll );
            
            return ( preq );
        }
    }
    
    public static void deallocatePickRequest( PickRequest preq )
    {
        synchronized ( pickRequests )
        {
            pickRequests.push( preq );
        }
    }
    
    /**
     * Allocates a new Group-List instance.
     */
    public static List< GroupNode > allocateGroupList()
    {
        if ( groupLists.isEmpty() )
        {
            return ( new ArrayList< GroupNode >() );
        }
        
        final List< GroupNode > list = groupLists.pop();
        list.clear();
        
        return ( list );
    }
    
    /**
     * Deallocates a Group-List instance and stores it in a LinkedList.
     */
    public static void deallocateGroupList( List< GroupNode > groupList )
    {
        groupLists.push( groupList );
    }
    
    /**
     * Allocates a new PickRay instance.
     */
    public static PickRay allocatePickRay()
    {
        if ( pickRays.isEmpty() )
            return ( new PickRay() );
        
        return ( pickRays.pop() );
    }
    
    /**
     * Deallocates a PickRay instance and stores it in a LinkedList.
     */
    public static void deallocatePickRay( PickRay pickRay )
    {
        pickRays.push( pickRay );
    }
    
    /**
     * Allocates a new Ray3f instance.
     */
    public static Ray3f allocateRay3f()
    {
        if ( rays.isEmpty() )
            return ( new Ray3f() );
        
        return ( rays.pop() );
    }
    
    /**
     * Deallocates a Ray3f instance and stores it in a LinkedList.
     */
    public static void deallocateRay3f( Ray3f ray )
    {
        rays.push( ray );
    }
    
    /**
     * Allocates a new Triangle instance.
     */
    public static Triangle allocateTriangle()
    {
        Triangle result;
        
        if ( triangles.isEmpty() )
            result = new Triangle();
        else
            result = triangles.pop();
        
        result.setFeatures( Vertex3f.COORDINATES );
        
        return ( result );
    }
    
    /**
     * Deallocates a Triangle instance and stores it in a LinkedList.
     */
    public static void deallocateTriangle( Triangle triangle )
    {
        triangles.push( triangle );
    }
    
    /**
     * Allocates a new PickResult-List instance.
     */
    public static SortableList< PickResult > allocatePickResultList()
    {
        if ( prLists.isEmpty() )
        {
            return ( new SortableList< PickResult >() );
        }
        
        final SortableList< PickResult > list = prLists.pop();
        list.clear();
        
        return ( list );
    }
    
    /**
     * Deallocates a PickResult-List instance and stores it in a LinkedList.
     */
    public static void deallocatePickResultList( SortableList< PickResult > prList )
    {
        prLists.push( prList );
    }
    
    /**
     * Allocates a new Node-Stack instance.
     */
    public static Stack< Node > allocateNodeStack()
    {
        if ( nodeStacks.isEmpty() )
            return ( new Stack< Node >() );
        
        return ( nodeStacks.pop() );
    }
    
    /**
     * Deallocates a Node-Stack instance and stores it in a LinkedList.
     */
    public static void deallocateNodeStack( Stack< Node > nodeStack )
    {
        nodeStacks.push( nodeStack );
    }
    
    /**
     * Allocates a new PickResult instance.
     */
    public static PickResult allocatePickResult()
    {
        if ( pickResults.isEmpty() )
            return ( new PickResult() );
        
        return ( pickResults.pop() );
    }
    
    /**
     * Deallocates a PickResult instance and stores it in a LinkedList.
     */
    public static void deallocatePickResult( PickResult pickResult )
    {
        pickResults.push( pickResult );
    }
    
    private PickPool()
    {
    }
}
