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

import java.util.List;

import org.jagatoo.input.devices.components.MouseButton;
import org.openmali.FastMath;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.spatial.polygons.Triangle;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Ray3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Leaf;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.View.CameraMode;
import org.xith3d.utility.general.SortableList;
import org.xith3d.utility.logging.X3DLog;

/**
 * This is used to convert Mouse coordinates to World coordinates
 * 
 * @author Arne Mueller
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class PickingLibrary
{
    private static GeometryPickTester geomPickTester = new DefaultGeometryPickTester();
    private static boolean isGeometryIgnored = false;
    
    /**
     * Sets the GeometryPickTester to use for Geometry-Ray intersection tests.
     * 
     * @param geomPickTester
     */
    public static void setGeometryPickTester( GeometryPickTester geomPickTester )
    {
        PickingLibrary.geomPickTester = geomPickTester;
    }
    
    /**
     * @return the GeometryPickTester to use for Geometry-Ray intersection tests.
     */
    public static GeometryPickTester getGeometryPickTester()
    {
        return ( geomPickTester );
    }
    
    /**
     * Sets whether bounds-wise detected pick-result-candidates are checked
     * more prcisely for geometry-ray intersection.
     * 
     * @param geomIgnored
     */
    public static void setGeometryIgnored( boolean geomIgnored )
    {
        PickingLibrary.isGeometryIgnored = geomIgnored;
    }
    
    /**
     * @return whether bounds-wise detected pick-result-candidates are checked
     * more prcisely for geometry-ray intersection.
     */
    public static boolean isGeometryIgnored()
    {
        return ( PickingLibrary.isGeometryIgnored  );
    }
    
    /**
     * Finds all intersections of a ray with the bounds of all Nodes in a
     * given Vector of Groups.
     * In the next step there will be a closer look at these candidates
     * to check if the ray really intersects the geometries.
     * The PickResults will be in ascending order so that the nearest
     * one will be at index 0.
     * 
     * Don't forget to set the static property Node.setDefaultPickable() to true.
     * If you are using (Ext)Xith3DEnvironment, if is already done.
     * 
     * @param groups the groups that should be tested
     * @param pickRay the pick ray
     * 
     * @return the List of intersections
     */
    private static void getPickCandidates( GroupNode group, Ray3f pickRay, MouseButton button, List< PickResult > candidates, Point3f pos )
    {
        // DFS (Depth First Search)
        for ( int i = 0; i < group.numChildren(); i++ )
        {
            final Node node = group.getChild( i );
            
            if ( node.isPickable() && node.isRenderable() )
            {
                // test, if there are intersections of the ray with the bounds
                // if there is an intersection, record intersection point if possible (for quick exits)
                // only if there are intersections, keep searching
                final Bounds bounds = node.getWorldBounds();
                
                if ( bounds == null )
                {
                    final String msg = "null bounds detected... skipping full (sub-) group.\n" + "  One reson for null bounds is, that you try to pick on a HUD's graph." + "  You should consider using one of the pick*() methods, that take a GroupNode instance. This is anyway faster.";
                    X3DLog.error( msg );
                    continue;
                    //throw new Error( msg ) );
                }
                
                if ( bounds.intersects( pickRay, pos ) )
                {
                    if ( node instanceof GroupNode )
                    {
                        // this is a group => look at the children
                        getPickCandidates( (GroupNode)node, pickRay, button, candidates, pos );
                    }
                    else if ( node instanceof Leaf )
                    {
                        // record dist, because we're only interested in the closest intersection
                        final float dist = pos.distanceSquared( pickRay.getOrigin() );
                        
                        final PickResult pr = PickPool.allocatePickResult();
                        pr.set( (Leaf)node, dist, button );
                        candidates.add( pr );
                    }
                }
            }
        }
    }
    
    /**
     * Finds all intersections of a ray with the bounds of all Nodes in a
     * given Vector of Groups.
     * In the next step there will be a closer look at these candidates
     * to check if the ray really intersects the geometries.
     * The PickResults will be in ascending order so that the nearest
     * one will be at index 0.
     * 
     * Don't forget to set the static property Node.setDefaultPickable() to true.
     * If you are using (Ext)Xith3DEnvironment, if is already done.
     * 
     * @param groups the groups that should be tested
     * @param canvas the canvas to which the coordinates refer
     * @param pickRay the pick ray
     * @param button the mouse button, that was clicked
     * 
     * @return the List of intersections
     */
    private static SortableList< PickResult > getPickCandidates( List< ? extends GroupNode > groups, Ray3f pickRay, MouseButton button )
    {
        final SortableList< PickResult > candidates = PickPool.allocatePickResultList();
        final Point3f pos = Point3f.fromPool();
        
        for ( int g = 0; g < groups.size(); g++ )
        {
            final GroupNode group = (GroupNode)groups.get( g );
            group.updateBounds( true );
            
            getPickCandidates( group, pickRay, button, candidates, pos );
        }
        
        Point3f.toPool( pos );
        
        return ( candidates );
    }
    
    /**
     * Does a closer look at the candidates. If the geometries intersect the ray,
     * the PickResults are left in the List, otherwise they are removed.
     * 
     * @param pickCandidates result candidates, which's geometries are checked for intersection with the ray
     * @param pickRay the pick ray
     * @param onlyNearest if true, the method returns immediately after the first hit
     */
    private static SortableList< PickResult > checkGeomIntersections( SortableList< PickResult > pickCandidates, Ray3f pickRay, boolean onlyNearest )
    {
        final Triangle triang = PickPool.allocateTriangle();
        final Ray3f transRay = PickPool.allocateRay3f();
        float closestIntersect = Float.POSITIVE_INFINITY;
        
        final SortableList< PickResult > pickResults = PickPool.allocatePickResultList();
        
        PickResult pr;
        boolean intersects;
        for ( int i = 0; i < pickCandidates.size(); i++ )
        {
            pr = pickCandidates.get( i );
            
            transRay.set( pickRay );
            pr.transform( transRay );
            
            final float intersectionDistance = geomPickTester.testGeometryIntersection( pr, transRay, closestIntersect, triang );
            intersects = ( intersectionDistance >= 0.0f );
            
            if ( intersects && onlyNearest )
            {
                closestIntersect = intersectionDistance;
            }
            
            if ( intersects )
            {
                // write the distance into the PickResult
                Tuple3f p = pr.tmpPos;
                p.scaleAdd( FastMath.sqrt( pr.getMinimumDistance() ), pickRay.getDirection(), pickRay.getOrigin() );
                pr.setPos( p );
                
                pickResults.add( pr );
                
                // (MF) Commented out, since this can't work. The optimization must be done by the GeomPickTester.
                /*
                if (onlyNearest)
                {
                    break; // don't use return here, since the objects need to be freed!
                }
                */
            }
            else
            {
                PickPool.deallocatePickResult( pr );
            }
        }
        
        PickPool.deallocateRay3f( transRay );
        PickPool.deallocateTriangle( triang );
        PickPool.deallocatePickResultList( pickCandidates );
        
        return ( pickResults );
    }
    
    /**
     * Finds all intersections of a given List of GroupNodes with a
     * given ray.
     * The PickResults will be in ascending order so that the nearest
     * one will be at index 0.
     * 
     * Don't forget to set the static property Node.setDefaultPickable() to true.
     * If you are using Xith3DEnvironment, it is already done.
     * 
     * @param groups the groups that should be tested
     * @param pickRay the pick ray
     * @param button the mouse button, that was clicked
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickAll( List< ? extends GroupNode > groups, Ray3f pickRay, MouseButton button, AllPickListener l, Object userObject )
    {
        final long t0 = System.currentTimeMillis();
        
        SortableList< PickResult > intersecting = getPickCandidates( groups, pickRay, button );
        
        if ( intersecting.size() > 0 )
        {
            if ( !isGeometryIgnored() )
            {
                intersecting = checkGeomIntersections( intersecting, pickRay, false );
            }
            intersecting.sort();
            
            if ( intersecting.size() > 0 )
                l.onObjectsPicked( intersecting, userObject, System.currentTimeMillis() - t0 );
            else
                l.onPickingMissed( userObject, System.currentTimeMillis() - t0 );
        }
        else
            l.onPickingMissed( userObject, System.currentTimeMillis() - t0 );
        
        for ( int i = 0; i < intersecting.size(); i++ )
        {
            PickPool.deallocatePickResult( intersecting.get( i ) );
        }
        PickPool.deallocatePickResultList( intersecting );
    }
    
    /**
     * Finds the closest intersection of a given List of NodeGroups with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * Don't forget to set the static property Node.setDefaultPickable() to true.
     * If you are using (Ext)Xith3DEnvironment, it is already done.
     * 
     * @param groups the groups that should be tested
     * @param pickRay the pick ray
     * @param button the mouse button, that was clicked
     * @param l the listener to be notified of the PickResult
     */
    public static void pickAll( List< ? extends GroupNode > groups, Ray3f pickRay, MouseButton button, AllPickListener l )
    {
        pickAll( groups, pickRay, button, l, (Object)null );
    }
    
    private static void calcPickRay( RenderPassConfig rpConfig, List< ? extends GroupNode > groups, PickRay pickRay, Canvas3D canvas, int x, int y )
    {
        if ( groups.size() > 0 )
        {
            pickRay.recalculate( rpConfig, canvas, x, y );
        }
        else
        {
            pickRay.recalculate( CameraMode.VIEW_NORMAL, canvas, x, y );
        }
    }
    
    /**
     * Finds the closest intersection of a given List of NodeGroups with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param rpConfig the RenderPassConfig to use
     * @param groups the groups that should be tested
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickAll( RenderPassConfig rpConfig, List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int x, int y, AllPickListener l, Object userObject )
    {
        if ( groups.size() == 0 )
            return;
        
        final PickRay pickRay = PickPool.allocatePickRay();
        calcPickRay( rpConfig, groups, pickRay, canvas, x, y );
        
        pickAll( groups, pickRay, button, l, userObject );
        
        PickPool.deallocatePickRay( pickRay );
    }
    
    /**
     * Finds the closest intersection of a given List of NodeGroups with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param groups the groups that should be tested
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickAll( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int x, int y, AllPickListener l, Object userObject )
    {
        if ( groups.size() == 0 )
            return;
        
        final PickRay pickRay = PickPool.allocatePickRay();
        calcPickRay( null, groups, pickRay, canvas, x, y );
        
        pickAll( groups, pickRay, button, l, userObject );
        
        PickPool.deallocatePickRay( pickRay );
    }
    
    /**
     * Finds the closest intersection of a given List of NodeGroups with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param groups the groups that should be tested
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     */
    public static void pickAll( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int x, int y, AllPickListener l )
    {
        pickAll( groups, canvas, button, x, y, l, (Object)null );
    }
    
    /**
     * Finds the closest intersection of a ray and a the nodes in a Group.
     * The ray is specified by screen coordinates.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param group the Branchgroup to intersect with the ray
     * @param canvas the canvas in which the pick is performed
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickAll( GroupNode group, Canvas3D canvas, MouseButton button, int x, int y, AllPickListener l, Object userObject )
    {
        final List< GroupNode > groups = PickPool.allocateGroupList();
        groups.add( group );
        
        pickAll( groups, canvas, button, x, y, l, userObject );
        
        PickPool.deallocateGroupList( groups );
    }
    
    /**
     * Finds the closest intersection of a ray and a the nodes in a Group.
     * The ray is specified by screen coordinates.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param group the Branchgroup to intersect with the ray
     * @param canvas the canvas in which the pick is performed
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     */
    public static void pickAll( GroupNode group, Canvas3D canvas, MouseButton button, int x, int y, AllPickListener l )
    {
        pickAll( group, canvas, button, x, y, l, (Object)null );
    }
    
    /**
     * Finds the closest intersection of a given List of GroupNodes with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * Don't forget to set the static property Node.setDefaultPickable() to true.
     * If you are using Xith3DEnvironment, it is already done.
     * 
     * @param groups the groups that should be tested
     * @param pickRay the pick ray
     * @param button the mouse button, that was clicked
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickNearest( List< ? extends GroupNode > groups, Ray3f pickRay, MouseButton button, NearestPickListener l, Object userObject )
    {
        final long t0 = System.currentTimeMillis();
        
        SortableList< PickResult > intersecting = getPickCandidates( groups, pickRay, button );
        
        if ( intersecting.size() > 0 )
        {
            if ( !isGeometryIgnored() )
            {
                intersecting = checkGeomIntersections( intersecting, pickRay, true );
            }
            
            intersecting.sort();
            
            if ( intersecting.size() > 0 )
                l.onObjectPicked( intersecting.get( 0 ), userObject, System.currentTimeMillis() - t0 );
            else
                l.onPickingMissed( userObject, System.currentTimeMillis() - t0 );
        }
        else
            l.onPickingMissed( userObject, System.currentTimeMillis() - t0 );
        
        for ( int i = 0; i < intersecting.size(); i++ )
        {
            PickPool.deallocatePickResult( intersecting.get( i ) );
        }
        PickPool.deallocatePickResultList( intersecting );
    }
    
    /**
     * Finds the closest intersection of a given List of GroupNodes with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * Don't forget to set the static property Node.setDefaultPickable() to true.
     * If you are using (Ext)Xith3DEnvironment, it is already done.
     * 
     * @param groups the groups that should be tested
     * @param pickRay the pick ray
     * @param button the mouse button, that was clicked
     * @param l the listener to be notified of the PickResult
     */
    public static void pickNearest( List< ? extends GroupNode > groups, Ray3f pickRay, MouseButton button, NearestPickListener l )
    {
        pickNearest( groups, pickRay, button, l, (Object)null );
    }
    
    /**
     * Finds the closest intersection of a given List of GroupNodes with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param rpConfig the RenderPassConfig to use
     * @param groups the groups that should be tested
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickNearest( RenderPassConfig rpConfig, List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int x, int y, NearestPickListener l, Object userObject )
    {
        final PickRay pickRay = PickPool.allocatePickRay();
        calcPickRay( rpConfig, groups, pickRay, canvas, x, y );
        
        pickNearest( groups, pickRay, button, l, userObject );
        
        PickPool.deallocatePickRay( pickRay );
    }
    
    /**
     * Finds the closest intersection of a given List of GroupNodes with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param rpConfig the RenderPassConfig to use
     * @param group the Branchgroup to intersect with the ray
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickNearest( RenderPassConfig rpConfig, GroupNode group, Canvas3D canvas, MouseButton button, int x, int y, NearestPickListener l, Object userObject )
    {
        final List< GroupNode > groups = PickPool.allocateGroupList();
        groups.add( group );
        
        pickNearest( rpConfig, groups, canvas, button, x, y, l, userObject );
        
        PickPool.deallocateGroupList( groups );
    }
    
    /**
     * Finds the closest intersection of a given List of GroupNodes with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param rpConfig the RenderPassConfig to use
     * @param group the Branchgroup to intersect with the ray
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     */
    public static void pickNearest( RenderPassConfig rpConfig, GroupNode group, Canvas3D canvas, MouseButton button, int x, int y, NearestPickListener l )
    {
        pickNearest( rpConfig, group, canvas, button, x, y, l, null );
    }
    
    /**
     * Finds the closest intersection of a given List of GroupNodes with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param groups the groups that should be tested
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickNearest( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int x, int y, NearestPickListener l, Object userObject )
    {
        pickNearest( null, groups, canvas, button, x, y, l, userObject );
    }
    
    /**
     * Finds the closest intersection of a given List of GroupNodes with a
     * given ray.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param groups the groups that should be tested
     * @param canvas the canvas to which the coordinates refer
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     */
    public static void pickNearest( List< ? extends GroupNode > groups, Canvas3D canvas, MouseButton button, int x, int y, NearestPickListener l )
    {
        pickNearest( groups, canvas, button, x, y, l, (Object)null );
    }
    
    /**
     * Finds the closest intersection of a ray and a the nodes in a Group.
     * The ray is specified by screen coordinates.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param group the Branchgroup to intersect with the ray
     * @param canvas the canvas in which the pick is performed
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     * @param userObject the userObject to pass to the listener
     */
    public static void pickNearest( GroupNode group, Canvas3D canvas, MouseButton button, int x, int y, NearestPickListener l, Object userObject )
    {
        final List< GroupNode > groups = PickPool.allocateGroupList();
        groups.add( group );
        
        pickNearest( groups, canvas, button, x, y, l, userObject );
        
        PickPool.deallocateGroupList( groups );
    }
    
    /**
     * Finds the closest intersection of a ray and a the nodes in a Group.
     * The ray is specified by screen coordinates.
     * You can use it, if you want to know where your mouse is pointing at.
     * 
     * @param group the Branchgroup to intersect with the ray
     * @param canvas the canvas in which the pick is performed
     * @param button the mouse button, that was clicked
     * @param x the x-(mouse)coordinate (in pixels)
     * @param y the y-(mouse)coordinate (in pixels)
     * @param l the listener to be notified of the PickResult
     */
    public static void pickNearest( GroupNode group, Canvas3D canvas, MouseButton button, int x, int y, NearestPickListener l )
    {
        pickNearest( group, canvas, button, x, y, l, (Object)null );
    }
}
