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
package org.xith3d.utility.math;

import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Vector2f;

/**
 * @author David Yazel
 */
public class BezierSolver
{
    public float smoothness = 0.75f;
    public Point2f p1 = new Point2f();
    public Point2f p2 = new Point2f();
    public Point2f p3 = new Point2f();
    public Point2f p4 = new Point2f();
    public Point2f c1 = new Point2f();
    public Point2f c2 = new Point2f();
    public Vector2f vtemp = new Vector2f();
    public Point2f mid = new Point2f();
    
    public BezierSolver()
    {
    }
    
    public void solve()
    {
        
        mid.setX( ( p3.getX() + p2.getX() ) / 2 );
        mid.setY( ( p3.getY() + p2.getY() ) / 2 );
        
        // solve for the first control point
        vtemp.sub( p2, p1 );
        vtemp.scale( smoothness );
        vtemp.scale( 0.5f );
        vtemp.add( p2 );
        
        c1.setX( ( mid.getX() + vtemp.getX() ) / 2 );
        c1.setY( ( mid.getY() + vtemp.getY() ) / 2 );
        
        // solve for the second control point
        vtemp.sub( p3, p4 );
        vtemp.scale( smoothness );
        vtemp.scale( 0.5f );
        vtemp.add( p3 );
        
        c2.setX( ( mid.getX() + vtemp.getX() ) / 2 );
        c2.setY( ( mid.getY() + vtemp.getY() ) / 2 );
    }
}
