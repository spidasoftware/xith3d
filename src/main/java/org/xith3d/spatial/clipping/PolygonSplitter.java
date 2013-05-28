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
package org.xith3d.spatial.clipping;

import java.util.List;

import org.openmali.spatial.bodies.Plane;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.utility.logging.X3DLog;

/**
 * Polygon splitter takes a set of points which are coplanar and splits them into two polygons
 * divided by the specified plane.
 * 
 * @author David Yazel
 */
public class PolygonSplitter
{
    void clipLineToPlane( Tuple3f va, Tuple3f vb, Polygon frontPolygon, Polygon backPolygon, Plane p )
    {
        float da = va.getX() * p.getNormal().getX() + va.getY() * p.getNormal().getY() + va.getZ() * p.getNormal().getZ() - p.getD();
        float db = vb.getX() * p.getNormal().getX() + vb.getY() * p.getNormal().getY() + vb.getZ() * p.getNormal().getZ() - p.getD();
        
        X3DLog.debug( "distance from ", va, " to ", p, " is ", da );
        X3DLog.debug( "distance from ", vb, " to ", p, " is ", db );
        // if they are both positive then we have two points within the clip
        if ( ( da >= 0 ) && ( db >= 0 ) )
        {
            X3DLog.debug( "both are positive, adding points " );
            
            if ( !frontPolygon.contains( va ) )
                frontPolygon.add( va );
            if ( !frontPolygon.contains( vb ) )
                frontPolygon.add( vb );
            
            // if they are both negative then both are on the
            // other side of the plane
        }
        else if ( ( da < 0 ) && ( db < 0 ) )
        {
            X3DLog.debug( "both are negative, placeing in back " );
            
            if ( !backPolygon.contains( va ) )
                backPolygon.add( va );
            if ( !backPolygon.contains( vb ) )
                backPolygon.add( vb );
            
            // if da is negative than that implies that db is
            // positive, we need to calculate a new point
        }
        else if ( da < 0 )
        {
            X3DLog.debug( "A is negative, adding point + intersection " );
            
            // move the A point to the plane, this means
            // creating a new index
            
            Vector3f intersect = new Vector3f();
            
            float s = da / ( da - db ); // intersection factor (between 0 and 1)
            
            intersect.setX( va.getX() + s * ( vb.getX() - va.getX() ) );
            intersect.setY( va.getY() + s * ( vb.getY() - va.getY() ) );
            intersect.setZ( va.getZ() + s * ( vb.getZ() - va.getZ() ) );
            
            //System.out.println(" i calc intersection is "+intersect);
            
            if ( !frontPolygon.contains( intersect ) )
                frontPolygon.add( intersect );
            if ( !frontPolygon.contains( vb ) )
                frontPolygon.add( vb );
            
            // now back side
            
            if ( !backPolygon.contains( va ) )
                backPolygon.add( va );
            if ( !backPolygon.contains( intersect ) )
                backPolygon.add( intersect );
        }
        else if ( db < 0 )
        {
            X3DLog.debug( "B is negative, adding point + intersection " );
            
            // move the B point to the plane, this means
            // creating a new index
            
            Vector3f intersect = new Vector3f();
            
            float s = da / ( da - db ); // intersection factor (between 0 and 1)
            
            intersect.setX( va.getX() + s * ( vb.getX() - va.getX() ) );
            intersect.setY( va.getY() + s * ( vb.getY() - va.getY() ) );
            intersect.setZ( va.getZ() + s * ( vb.getZ() - va.getZ() ) );
            
            X3DLog.debug( " i calc intersection is ", intersect );
            
            if ( !frontPolygon.contains( va ) )
                frontPolygon.add( va );
            if ( !frontPolygon.contains( intersect ) )
                frontPolygon.add( intersect );
            
            // now back
            if ( !backPolygon.contains( intersect ) )
                backPolygon.add( intersect );
            if ( !backPolygon.contains( vb ) )
                backPolygon.add( vb );
        }
        
        X3DLog.debug( "" );
    }
    
    void splitToPlane( Polygon polygon, Plane p, Polygon frontPolygon, Polygon backPolygon )
    {
        List< Tuple3f > vertices = polygon.getVertices();
        for ( int i = 0; i < vertices.size(); i++ )
        {
            Tuple3f a = vertices.get( i );
            Tuple3f b = vertices.get( ( i + 1 ) % vertices.size() );
            clipLineToPlane( a, b, frontPolygon, backPolygon, p );
        }
    }
}
