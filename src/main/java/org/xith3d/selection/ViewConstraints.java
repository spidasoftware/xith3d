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

import org.openmali.spatial.bodies.Frustum;
import org.openmali.spatial.bodies.Plane;
import org.openmali.vecmath2.Point3f;
import org.xith3d.picking.PickPool;
import org.xith3d.picking.PickRay;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.scenegraph._SG_PrivilegedAccess;

/**
 * @author Mathias Henze (aka cylab)
 */
public class ViewConstraints implements MovementConstraints
{
    public void computeNewPosition( RenderPassConfig rpc, Canvas3D canvas, int x, int y, Point3f p0, Point3f newPosition )
    {
        Plane movementPlane = Plane.fromPool();
        PickRay pickRay = PickPool.allocatePickRay();
        
        try
        {
            if ( rpc != null )
            {
                _SG_PrivilegedAccess.set( canvas.getView(), true, rpc );
            }
            
            Frustum frustum;
            if ( ( rpc != null ) && ( rpc.getViewport() != null ) )
                frustum = canvas.getView().getFrustum( rpc.getViewport() );
            else
                frustum = canvas.getView().getFrustum( canvas );
            frustum.getPlaneNear( movementPlane );
            float d = -( movementPlane.getNX() * p0.getX() + movementPlane.getNY() * p0.getY() + movementPlane.getNZ() * p0.getZ() );
            
            movementPlane.setD( d );
            pickRay.recalculate( rpc, canvas, x, y );
            movementPlane.intersects( pickRay, newPosition );
        }
        finally
        {
            if ( rpc != null )
            {
                _SG_PrivilegedAccess.set( canvas.getView(), false, rpc );
            }
            
            PickPool.deallocatePickRay( pickRay );
            Plane.toPool( movementPlane );
        }
    }
    
    public ViewConstraints()
    {
    }
}
