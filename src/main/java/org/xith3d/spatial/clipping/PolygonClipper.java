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
 * Insert comment here.
 * 
 * @author David Yazel
 */
public class PolygonClipper
{
    void clipLineToPlane( Tuple3f va, Tuple3f vb, Polygon newPolygon, Plane p )
    {
        float da = va.getX() * p.getNormal().getX() + va.getY() * p.getNormal().getY() + va.getZ() * p.getNormal().getZ() - p.getD();
        float db = vb.getX() * p.getNormal().getX() + vb.getY() * p.getNormal().getY() + vb.getZ() * p.getNormal().getZ() - p.getD();
        
        X3DLog.debug( "distance from ", va, " to ", p, " is ", da );
        X3DLog.debug( "distance from ", vb, " to ", p, " is ", db );
        // if they are both positive then we have two points within the clip
        if ( ( da >= 0 ) && ( db >= 0 ) )
        {
            X3DLog.debug( "both are positive, adding points " );
            
            if ( !newPolygon.contains( va ) )
                newPolygon.add( va );
            if ( !newPolygon.contains( vb ) )
                newPolygon.add( vb );
            
            // if they are both negative then both are on the
            // other side of the plane
        }
        else if ( ( da < 0 ) && ( db < 0 ) )
        {
            X3DLog.debug( "both are negative, ignoring points " );
            
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
            
            if ( !newPolygon.contains( intersect ) )
                newPolygon.add( intersect );
            if ( !newPolygon.contains( vb ) )
                newPolygon.add( vb );
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
            
            if ( !newPolygon.contains( va ) )
                newPolygon.add( va );
            if ( !newPolygon.contains( intersect ) )
                newPolygon.add( intersect );
        }
        
        X3DLog.debug( "" );
    }
    
    Polygon clipToPlane( Polygon polygon, Plane p )
    {
        Polygon newPolygon = new Polygon();
        List< Tuple3f > vertices = polygon.getVertices();
        for ( int i = 0; i < vertices.size(); i++ )
        {
            Tuple3f a = vertices.get( i );
            Tuple3f b = vertices.get( ( i + 1 ) % vertices.size() );
            clipLineToPlane( a, b, newPolygon, p );
        }
        
        return ( newPolygon );
    }
    
    public Polygon clip( Polygon polygon, Plane[] planes )
    {
        for ( int i = 0; i < planes.length; i++ )
        {
            polygon = clipToPlane( polygon, planes[ i ] );
            
            if ( polygon.getVertices().size() == 0 )
                return ( polygon );
        }
        
        return ( polygon );
    }
}
