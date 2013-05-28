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
package org.xith3d.loop.opscheduler.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.xith3d.loop.opscheduler.ScheduledScreenshot;
import org.xith3d.picking.ScheduledPicker;
import org.xith3d.render.Canvas3D;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.utility.general.CircularArray;

/**
 * A Heap to store ScheduledOperation instances.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class SchedOpsPool
{
    private static CircularArray< ScheduledScreenshot > schededShots = new CircularArray< ScheduledScreenshot >( 16 );
    private static CircularArray< ScheduledPicker > schededPickers = new CircularArray< ScheduledPicker >( 128 );
    private static CircularArray< List< GroupNode >> groupLists = new CircularArray< List< GroupNode >>( 128 );
    
    /**
     * Allocates a new ScheduledScreenshot instance.
     */
    public static ScheduledScreenshot allocateSchededScreenshot( Canvas3D canvas, File file, boolean alpha )
    {
        if ( schededShots.isEmpty() )
            return ( new ScheduledScreenshot( canvas, file, alpha ) );
        
        return ( schededShots.pop() );
    }
    
    /**
     * Deallocates a ScheduledScreenshot instance and stores it in a LinkedList.
     */
    public static void deallocateSchededScreenshot( ScheduledScreenshot schededShot )
    {
        schededShots.push( schededShot );
    }
    
    /**
     * Allocates a new ScheduledPicker instance.
     */
    public static ScheduledPicker allocateScheduledPicker()
    {
        if ( schededPickers.isEmpty() )
            return ( new ScheduledPicker() );
        
        return ( schededPickers.pop() );
    }
    
    /**
     * Deallocates a ScheduledPicker instance and stores it in a LinkedList.
     */
    public static void deallocateScheduledPicker( ScheduledPicker schededPicker )
    {
        schededPickers.push( schededPicker );
    }
    
    /**
     * Allocates a new List of GroupNodes.
     */
    public static List< GroupNode > allocateGroupList()
    {
        if ( groupLists.isEmpty() )
        {
            return ( new ArrayList< GroupNode >() );
        }
        
        final List< GroupNode > groupList = groupLists.pop();
        
        groupList.clear();
        
        return ( groupList );
    }
    
    /**
     * Deallocates a List of GroupNodes and stores it in a LinkedList.
     */
    public static void deallocateGroupList( List< GroupNode > groupList )
    {
        groupLists.push( groupList );
    }
}
