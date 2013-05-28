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
package org.xith3d.scenegraph.traversal.impl;

import org.jagatoo.util.errorhandling.UnsupportedFunction;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

/**
 * Various useful static methods manipulate the scenegraph.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class SGUtils
{
    /**
     * Returns the count of polygons in the (subgroup of the) scenegraph.
     * 
     * @param node
     *            the node to search : could be a link or a group
     * @return the count of polygons
     */
    public static int getPolygonCount( Node node )
    {
        if ( node instanceof Group )
        {
            Group group = (Group)node;
            
            return ( PolygonCounter.getPolygonCount( group ) );
        }
        
        throw new UnsupportedFunction( "Cannot count polygons on type " + node.getClass().getName() );
    }
    
    /**
     * Returns the count of polygons in the (subgroup of the) scenegraph.
     * 
     * @param group
     *            the subgroup to search
     * @return the count of polygons
     */
    public static int getPolygonCount( Group group )
    {
        return ( PolygonCounter.getPolygonCount( group ) );
    }
    
    /**
     * Finds the first Shape3D in the scenegraph
     * 
     * @param group
     *            the group to search
     * @return the found Shape3D
     */
    public static Shape3D findFirstShape( Group group )
    {
        return ( ShapeFinder.findFirstShape( group ) );
    }
    
    /**
     * Traverses the scenegraph and applies an Appearance to all Nodes
     * 
     * @param group
     *            the group to search
     * @param app
     *            the Appearance to apply to all nodes in the group
     */
    public static void setAllAppearances( Group group, Appearance app )
    {
        group.traverse( new AppearanceTraversal( app ) );
    }
    
    /**
     * Traverses the scenegraph and applies a Material to all Nodes
     * 
     * @param group
     *            the group to search
     * @param mat
     *            the Material to apply to all nodes in the group
     */
    public static void setAllMaterials( Group group, Material mat )
    {
        group.traverse( new MaterialTraversal( mat ) );
    }
    
    /**
     * Traverses the scenegraph and sets the pickable flag on all Nodes
     * 
     * @param group
     *            the group to search
     * @param pickable
     *            the value to set the pickable flag to
     */
    public static void setAllPickable( Group group, boolean pickable )
    {
        group.traverse( new PickableTraversal( pickable ) );
    }
}
