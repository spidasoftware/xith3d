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
package org.xith3d.scenegraph.primitives;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * Add this to your scene to know where is the origin.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Origin extends Group
{
    public Origin( Tuple3f origin )
    {
        //final float INF = 80000000f; // Too large numbers will cause problems!
        final float INF = 65536.0f;
        
        Line line = new Line( new Point3f( -INF, origin.getY(), origin.getZ() ), new Point3f( INF, origin.getY(), origin.getZ() ), Colorf.RED );
        line.getGeometry().makeInterleaved();
        line.getGeometry().setOptimization( Geometry.Optimization.USE_DISPLAY_LISTS );
        this.addChild( line );
        
        line = new Line( new Point3f( origin.getX(), -INF, origin.getZ() ), new Point3f( origin.getX(), INF, origin.getZ() ), Colorf.GREEN );
        line.getGeometry().makeInterleaved();
        line.getGeometry().setOptimization( Geometry.Optimization.USE_DISPLAY_LISTS );
        this.addChild( line );
        
        line = new Line( new Point3f( origin.getX(), origin.getY(), -INF ), new Point3f( origin.getX(), origin.getY(), INF ), Colorf.BLUE );
        line.getGeometry().makeInterleaved();
        line.getGeometry().setOptimization( Geometry.Optimization.USE_DISPLAY_LISTS );
        this.addChild( line );
    }
    
    public Origin()
    {
        this( new Point3f( 0f, 0f, 0f ) );
    }
    
    public Origin( Transform3D transform )
    {
        this( transform.getTranslation() );
    }
    
    public Origin( TransformGroup tg )
    {
        this( tg.getWorldTransform().getTranslation() );
    }
}
