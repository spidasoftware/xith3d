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

import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.util.errorhandling.UnsupportedFunction;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Ray3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Leaf;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Transform3D;

/**
 * Result of a picking action.
 * 
 * @author Arne Mueller
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class PickResult implements Comparable< PickResult >
{
    private Leaf node; // the Leaf that got picked at (with this PickResult)
    private int faceIndex; // the index of the face that got picked (PickingLibrary only!)
    
    protected Tuple3f tmpPos = new Point3f();
    private Tuple3f pos;
    
    private float minDistance; // minimum distance of the intersection
    private float maxDistance; // maximum distance of the intersection
    private float medDistance; // medial distance of the intersection
    
    private Transform3D tmpTrans = new Transform3D();
    
    private MouseButton button;
    
    /**
     * Creates a new instance of PickResult.
     */
    public PickResult( Leaf node, float minDist, float maxDist, float medDist )
    {
        this( node, -1, minDist, maxDist, medDist );
    }
    
    /**
     * Creates a new instance of PickResult.
     */
    public PickResult( Leaf node, int faceIndex, float minDist, float maxDist, float medDist )
    {
        this.node = node;
        this.faceIndex = faceIndex;
        this.pos = null;
        this.minDistance = minDist;
        this.maxDistance = maxDist;
        this.medDistance = medDist;
        this.button = null;
    }
    
    /**
     * Creates a new instance of PickResult.
     */
    public PickResult( Leaf node, float dist )
    {
        this( node, dist, dist, dist );
    }
    
    /**
     * Creates a new instance of PickResult.
     */
    PickResult()
    {
    }
    
    /**
     * Creates a new instance of PickResult.
     */
    void set( Leaf node, float dist, MouseButton button )
    {
        this.node = node;
        this.faceIndex = -1;
        this.pos = null;
        this.minDistance = dist;
        this.maxDistance = dist;
        this.medDistance = dist;
        this.button = button;
    }
    
    /**
     * @return the picked Node
     */
    public Leaf getNode()
    {
        return ( node );
    }
    
    /**
     * @return the picked Shape3D's pick-host
     */
    public Node getPickHost()
    {
        return ( node.getPickHost() );
    }
    
    /**
     * @return the picked Shape3D's pick-host, if not null. The Node itself, otherwise.
     */
    public Node getPickHostOrNode()
    {
        if ( node.getPickHost() == null )
            return ( node );
        
        return ( node.getPickHost() );
    }
    
    /**
     * @return the Geometry of the picked Shape3D
     * @see #getNode()
     */
    public Geometry getGeometry()
    {
        if ( node instanceof Shape3D )
            return ( ( (Shape3D)node ).getGeometry() );
        
        return ( null );
    }
    
    public void setFaceIndex( int faceIndex )
    {
        this.faceIndex = faceIndex;
    }
    
    /**
     * @return the index of the face which has
     * been picked in the Shape3D
     * @see #getNode()
     * @see #getGeometry()
     */
    public int getFaceIndex()
    {
        if ( faceIndex == -1 )
        {
            throw new UnsupportedFunction( "You tried to get the index of the face " + "which has been picked, but you use OpenGL-style picking. If " + "you want to use this functionality, please use org.xith3d." + "picking.PickingLibrary !!" );
        }
        
        return ( faceIndex );
    }
    
    /**
     * Transforms a vector in global-space to local space of this Shape3D.
     * 
     * @param v the vector to transform
     */
    public void transform( Vector3f v )
    {
        node.getWorldTransform( tmpTrans );
        tmpTrans.invert();
        tmpTrans.transform( v );
    }
    
    /**
     * Transforms a point in global-space to local space of this Shape3D.
     * 
     * @param p the point to transform
     */
    public void transform( Point3f p )
    {
        node.getWorldTransform( tmpTrans );
        tmpTrans.invert();
        tmpTrans.transform( p );
    }
    
    /**
     * Transforms a Ray3f in global-space to local space of this Shape3D.
     * 
     * @param ray the ray to transform
     */
    public void transform( Ray3f ray )
    {
        node.getWorldTransform( tmpTrans );
        tmpTrans.invert();
        tmpTrans.transform( ray.getOrigin() );
        tmpTrans.transform( ray.getDirection() );
    }
    
    public void setPos( Tuple3f pos )
    {
        this.pos = pos;
    }
    
    /**
     * @return The position of the intersection of the PickRay
     * with the Shape3D in global-space
     */
    public Tuple3f getPos()
    {
        return ( pos );
    }
    
    /**
     * Sets the minimum distance to the intersection
     * 
     * @param dist the distance
     */
    public void setMinimumDistance( float dist )
    {
        minDistance = dist;
    }
    
    /**
     * @return the minimum distance to the intersection
     */
    public float getMinimumDistance()
    {
        return ( minDistance );
    }
    
    /**
     * Sets the maximum distance to the intersection
     * 
     * @param dist the distance
     */
    public void setMaximumDistance( float dist )
    {
        maxDistance = dist;
    }
    
    /**
     * @return the maximum distance to the intersection
     */
    public float getMaximumDistance()
    {
        return ( maxDistance );
    }
    
    /**
     * Sets the medial distance to the intersection
     * 
     * @param dist the distance
     */
    public void setMedialDistance( float dist )
    {
        medDistance = dist;
    }
    
    /**
     * @return the medial distance to the intersection
     */
    public float getMedialDistance()
    {
        return ( medDistance );
    }
    
    /**
     * @return the mouse button, that was clicked
     */
    public final MouseButton getButton()
    {
        return ( button );
    }
    
    /**
     * @return a String-representation of this PickResult object.
     */
    @Override
    public String toString()
    {
        return ( "PickResult: Node: " + node + " Pos: " + pos );
    }
    
    /**
     * Compares two PickResults according to their distances.
     * 
     * @param pr the PickResult to compare to
     */
    public int compareTo( PickResult pr )
    {
        if ( this.minDistance > pr.minDistance )
            return ( 1 );
        else if ( this.minDistance < pr.minDistance )
            return ( -1 );
        else
            return ( 0 );
    }
    
    @Override
    public boolean equals( Object o )
    {
        if ( o instanceof PickResult )
        {
            final PickResult r = (PickResult)o;
            if ( this.node == r.node || ( this.pos.equals( r.pos ) ) )
            {
                return ( true );
            }
        }
        
        return ( false );
    }
}
