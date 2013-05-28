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
package org.xith3d.scenegraph.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph._SG_PrivilegedAccess;
import org.xith3d.utility.geometry.GeometryUtils;

/**
 * Provides static Shape3D utility methods.
 * 
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public final class ShapeUtils
{
    /**
     * Sets a Shape3D's transparency.<br>
     * 
     * @param shape the Shape3D to manipulate
     * @param transparency <= 0 for not transparent, > 0, <= 1 for transparent
     * @param allowNullTA if <code>true</code>, then the TransparencyAttributes are set to <code>null</code>, if transparency is <= 0
     */
    public static final void setShapesTransparency( Shape3D shape, float transparency, boolean allowNullTA, boolean allowUnblended )
    {
        if ( transparency > 0.0f )
        {
            TransparencyAttributes ta = shape.getAppearance( true ).getTransparencyAttributes( true );
            
            ta.setMode( TransparencyAttributes.BLENDED );
            ta.setTransparency( transparency );
            
            shape.getAppearance().setTransparencyAttributes( ta );
            
            // I have no idea, why this is necessary, but the new transparency value is not visible without it.
            if ( shape.getAppearance().getColoringAttributes() != null )
                _SG_PrivilegedAccess.setChanged( shape.getAppearance().getColoringAttributes(), true );
        }
        else
        {
            Appearance app = shape.getAppearance();
            
            if ( app == null )
                return;
            
            TransparencyAttributes ta = app.getTransparencyAttributes();
            
            if ( ta == null )
                return;
            
            if ( allowNullTA )
            {
                app.setTransparencyAttributes( null );
                return;
            }
            
            if ( allowUnblended )
                ta.setMode( TransparencyAttributes.NONE );
            ta.setTransparency( 0.0f );
        }
    }
    
    /**
     * @return a Shape3D's transparency.
     * 
     * @param shape the Shape3D, which's transparency is to be read
     */
    public static final float getShapesTransparency( Shape3D shape )
    {
        Appearance app = shape.getAppearance();
        
        if ( app == null )
            return ( 0.0f );
        
        TransparencyAttributes ta = app.getTransparencyAttributes();
        
        if ( ta == null )
            return ( 0.0f );
        
        if ( ta.getMode() == TransparencyAttributes.NONE )
            return ( 0.0f );
        
        return ( ta.getTransparency() );
    }
    
    /**
     * Sets the transparency of all Shape3Ds in the given Group, recursively
     * or of this node if it's a Shape3D. If you call it on a Node, which
     * isn't either a Shape3D or a NodeGroup, it will just do nothing.
     * 
     * @param node The Shape3D/Group to set the Transparency to
     * @param transparency A value in the range [0f-1f], 0f being completely
     *                     opaque, and 1f completely transparent 
     */
    public static void setTransparency( Node node, float transparency )
    {
        if ( node instanceof Shape3D )
        {
            if ( ( (Shape3D)node ).getAppearance( true ).getTransparencyAttributes() == null )
            {
                ( (Shape3D)node ).getAppearance( true ).setTransparencyAttributes( new TransparencyAttributes( TransparencyAttributes.BLENDED, transparency ) );
            }
            else
            {
                ( (Shape3D)node ).getAppearance( true ).getTransparencyAttributes().setTransparency( transparency );
            }
        }
        else if ( node instanceof GroupNode )
        {
            GroupNode group = (GroupNode)node;
            final int numChildren = group.numChildren();
            for ( int i = 0; i < numChildren; i++ )
            {
                Node child = group.getChild( i );
                
                if ( ( child instanceof Shape3D ) || ( child instanceof GroupNode ) )
                {
                    setTransparency( child, transparency );
                }
            }
        }
    }
    
    private static Group mergeShapes( boolean useIndex, Shape3D... shapes )
    {
        HashMap< Appearance, ArrayList< Shape3D > > appShapeMap = new HashMap< Appearance, ArrayList< Shape3D > >();
        
        for ( Shape3D shape: shapes )
        {
            final Appearance app = shape.getAppearance();
            
            ArrayList< Shape3D > mappedShapes = appShapeMap.get( app );
            if ( mappedShapes == null )
            {
                mappedShapes = new ArrayList< Shape3D >();
                appShapeMap.put( app, mappedShapes );
            }
            
            mappedShapes.add( shape );
        }
        
        Group group = new Group();
        
        for ( ArrayList< Shape3D > shapesList: appShapeMap.values() )
        {
            Geometry[] geoms = new Geometry[shapesList.size()];
            for ( int i = 0; i < shapesList.size(); i++ )
            {
                geoms[i] = (Geometry)shapesList.get( i ).getGeometry();
            }
            
            Geometry geom;
            if ( useIndex )
                geom = GeometryUtils.mergeGeometriesITA( geoms );
            else
                geom = GeometryUtils.mergeGeometriesTA( geoms );
            //System.out.println( geom.getVertexCount()/* + ", " + geom.getIndexCount()*/ );
            
            group.addChild( new Shape3D( geom, shapesList.get( 0 ).getAppearance() ) );
        }
        
        return ( group );
    }
    
    public static Group mergeShapesTA( Shape3D... shapes )
    {
        return ( mergeShapes( false, shapes ) );
    }
    
    public static Group mergeShapesITA( Shape3D... shapes )
    {
        return ( mergeShapes( true, shapes ) );
    }
}
