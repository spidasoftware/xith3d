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

import org.openmali.FastMath;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.Cone;
import org.xith3d.scenegraph.primitives.Line;

/**
 * @author Mathias Henze (aka cylab)
 */
public class BoundingBoxSelectable<NodeType extends Node> extends AbstractNodeSelectable<NodeType>
{
    private TransformGroup group;
    
    @Override
    public void setSelected( SelectionManager selectionManager, boolean selected  )
    {
        super.setSelected( selectionManager, selected );
        Node node = getNode();
        if ( selected && ( group == null ) )
        {
            Tuple3f center = Tuple3f.fromPool();
            
            Bounds bounds = node.getBounds();
            bounds.getCenter( center );
            float radius = bounds.getMaxCenterDistance();
                
            // TODO die Gruppe cachen und nicht immer wieder neu erstellen
            group = new TransformGroup( node.getWorldTransform() );
            group.addChild( createBox( radius ) );
            group.addChild( createCoordinateSystem( radius ) );
//            selectionManager.getSelectionLayer().addChild( group );
            node.getRoot().addChild( group );
            
            Tuple3f.toPool( center );
        }
        else if ( group != null )
        {
            node.getRoot().removeChild( group );
//            selectionManager.getSelectionLayer().removeChild( group );
            group = null;
        }
    }
    
    public void onMoved( SelectionManager selectionManager, Vector3f delta )
    {
        TransformGroup tg = getNode().getTransformGroup();
        if ( tg != null )
        {
            Vector3f trans = (Vector3f)tg.getTransform().getTranslation().add( delta );
            tg.getTransform().setTranslation( trans );
            tg.updateTransform();
            
            tg.getTransform().setTranslation( trans );
            tg.updateTransform();
        }
        
        group.setTransform( getNode().getWorldTransform() );
    }
    
    private Group createBox(float r)
    {
        Group box = new Group();
        float ou = r;
        float in = 0.8f * r;
        float lw = 4;
        Colorf col = Colorf.BLUE;
        
        Line[] lines = new Line[] {
            new Line( new Tuple3f( -ou,  ou,  ou ), new Tuple3f( -in,  ou,  ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou,  ou ), new Tuple3f(  in,  ou,  ou ), lw, col ),
            new Line( new Tuple3f( -ou,  ou,  ou ), new Tuple3f( -ou,  in,  ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou,  ou ), new Tuple3f(  ou,  in,  ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou,  ou ), new Tuple3f( -in, -ou,  ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou,  ou ), new Tuple3f(  in, -ou,  ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou,  ou ), new Tuple3f( -ou, -in,  ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou,  ou ), new Tuple3f(  ou, -in,  ou ), lw, col ),
            
            new Line( new Tuple3f( -ou,  ou, -ou ), new Tuple3f( -in,  ou, -ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou, -ou ), new Tuple3f(  in,  ou, -ou ), lw, col ),
            new Line( new Tuple3f( -ou,  ou, -ou ), new Tuple3f( -ou,  in, -ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou, -ou ), new Tuple3f(  ou,  in, -ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou, -ou ), new Tuple3f( -in, -ou, -ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou, -ou ), new Tuple3f(  in, -ou, -ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou, -ou ), new Tuple3f( -ou, -in, -ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou, -ou ), new Tuple3f(  ou, -in, -ou ), lw, col ),
            
            new Line( new Tuple3f(  ou, -ou,  ou ), new Tuple3f(  ou, -in,  ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou,  ou ), new Tuple3f(  ou,  in,  ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou,  ou ), new Tuple3f(  ou, -ou,  in ), lw, col ),
            new Line( new Tuple3f(  ou,  ou,  ou ), new Tuple3f(  ou,  ou,  in ), lw, col ),
            new Line( new Tuple3f(  ou, -ou, -ou ), new Tuple3f(  ou, -in, -ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou, -ou ), new Tuple3f(  ou,  in, -ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou, -ou ), new Tuple3f(  ou, -ou, -in ), lw, col ),
            new Line( new Tuple3f(  ou,  ou, -ou ), new Tuple3f(  ou,  ou, -in ), lw, col ),
            
            new Line( new Tuple3f( -ou, -ou,  ou ), new Tuple3f( -ou, -in,  ou ), lw, col ),
            new Line( new Tuple3f( -ou,  ou,  ou ), new Tuple3f( -ou,  in,  ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou,  ou ), new Tuple3f( -ou, -ou,  in ), lw, col ),
            new Line( new Tuple3f( -ou,  ou,  ou ), new Tuple3f( -ou,  ou,  in ), lw, col ),
            new Line( new Tuple3f( -ou, -ou, -ou ), new Tuple3f( -ou, -in, -ou ), lw, col ),
            new Line( new Tuple3f( -ou,  ou, -ou ), new Tuple3f( -ou,  in, -ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou, -ou ), new Tuple3f( -ou, -ou, -in ), lw, col ),
            new Line( new Tuple3f( -ou,  ou, -ou ), new Tuple3f( -ou,  ou, -in ), lw, col ),
            
            new Line( new Tuple3f( -ou,  ou,  ou ), new Tuple3f( -in,  ou,  ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou,  ou ), new Tuple3f(  in,  ou,  ou ), lw, col ),
            new Line( new Tuple3f( -ou,  ou,  ou ), new Tuple3f( -ou,  ou,  in ), lw, col ),
            new Line( new Tuple3f(  ou,  ou,  ou ), new Tuple3f(  ou,  ou,  in ), lw, col ),
            new Line( new Tuple3f( -ou,  ou, -ou ), new Tuple3f( -in,  ou, -ou ), lw, col ),
            new Line( new Tuple3f(  ou,  ou, -ou ), new Tuple3f(  in,  ou, -ou ), lw, col ),
            new Line( new Tuple3f( -ou,  ou, -ou ), new Tuple3f( -ou,  ou, -in ), lw, col ),
            new Line( new Tuple3f(  ou,  ou, -ou ), new Tuple3f(  ou,  ou, -in ), lw, col ),
            
            new Line( new Tuple3f( -ou, -ou,  ou ), new Tuple3f( -in, -ou,  ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou,  ou ), new Tuple3f(  in, -ou,  ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou,  ou ), new Tuple3f( -ou, -ou,  in ), lw, col ),
            new Line( new Tuple3f(  ou, -ou,  ou ), new Tuple3f(  ou, -ou,  in ), lw, col ),
            new Line( new Tuple3f( -ou, -ou, -ou ), new Tuple3f( -in, -ou, -ou ), lw, col ),
            new Line( new Tuple3f(  ou, -ou, -ou ), new Tuple3f(  in, -ou, -ou ), lw, col ),
            new Line( new Tuple3f( -ou, -ou, -ou ), new Tuple3f( -ou, -ou, -in ), lw, col ),
            new Line( new Tuple3f(  ou, -ou, -ou ), new Tuple3f(  ou, -ou, -in ), lw, col ),
        };
        
        for ( int i = 0; i < lines.length; i++ )
        {
            box.addChild( lines[ i ] );
        }
        
        return ( box );
    }
    
    private Group createCoordinateSystem( float r )
    {
        final Group coords = new Group();
        final Colorf col = Colorf.GREEN;
        final float lw = 2;
        
        Line[] lines = new Line[] {
            new Line( new Tuple3f( r, 0f, 0f ), lw, col ),
            new Line( new Tuple3f( 0f, r, 0f ), lw, col ),
            new Line( new Tuple3f( 0f, 0f, r ), lw, col ),
        };
        
        for ( int i = 0; i < lines.length; i++ )
        {
            coords.addChild( lines[ i ] );
        }
        
        Cone cone = new Cone( r / 10f, r / 3f, 10, col );
        StaticTransform.rotateZ( cone, -FastMath.PI_HALF );
        StaticTransform.translate( cone, r, 0f, 0f );
        coords.addChild( cone );

        cone = new Cone( r / 10f, r / 3f, 10, col );
        StaticTransform.translate( cone, 0f, r, 0f );
        coords.addChild( cone );
        
        cone = new Cone( r / 10f, r / 3f, 10, col );
        StaticTransform.rotateX( cone, FastMath.PI_HALF );
        StaticTransform.translate( cone, 0f, 0f, r );
        coords.addChild( cone );
        
        return ( coords );
    }
    
    public BoundingBoxSelectable( NodeType node )
    {
        super( node );
    }
}
