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
package org.xith3d.scenegraph.avatar;

import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;

/**
 * The DefaultAvatarTransform is a standard {@link AvatarTransform},
 * that translates the avatar to an offset of the View and copies
 * the y-rotation from the View.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DefaultAvatarTransform implements AvatarTransform
{
    private final Node node;
    private final Vector3f offset;
    private final TransformGroup tg;
    
    private final Tuple3f tmpTuple = new Vector3f();
    
    /**
     * @return the avatar's scenegraph Node.
     */
    public final Node getNode()
    {
        return ( node );
    }
    
    /**
     * @return the translational offset of the Node to the View.
     */
    public final Vector3f getOffset()
    {
        return ( offset );
    }
    
    /**
     * [{@inheritDoc}
     */
    public final TransformGroup getTransformGroup()
    {
        return ( tg );
    }
    
    /**
     * [{@inheritDoc}
     */
    public void transform( Transform3D viewTransform, float rotX, float rotY, Vector3f thirdPersonOffset )
    {
        final Transform3D t3d = getTransformGroup().getTransform();
        
        t3d.setEuler( 0f, rotY, 0f );
        viewTransform.getTranslation( tmpTuple );
        tmpTuple.add( getOffset() );
        tmpTuple.sub( thirdPersonOffset );
        t3d.setTranslation( tmpTuple );
        
        getTransformGroup().setTransform( t3d );
    }
    
    public DefaultAvatarTransform( Node node, float offsetX, float offsetY, float offsetZ, BranchGroup sceneRoot )
    {
        this.node = node;
        this.offset = new Vector3f( offsetX, offsetY, offsetZ );
        this.tg = new TransformGroup();
        tg.addChild( node );
        
        if ( sceneRoot != null )
            sceneRoot.addChild( tg );
    }
    
    public DefaultAvatarTransform( Node node, float offsetX, float offsetY, float offsetZ )
    {
        this( node, offsetX, offsetY, offsetZ, null );
    }
    
    public DefaultAvatarTransform( Node node, float offsetY, BranchGroup sceneRoot )
    {
        this( node, 0f, offsetY, 0f, sceneRoot );
    }
    
    public DefaultAvatarTransform( Node node, float offsetY )
    {
        this( node, offsetY, null );
    }
    
    public DefaultAvatarTransform( Node node, Tuple3f offset, BranchGroup sceneRoot )
    {
        this( node, offset.getX(), offset.getY(), offset.getZ(), sceneRoot );
    }
    
    public DefaultAvatarTransform( Node node, Tuple3f offset )
    {
        this( node, offset, null );
    }
}
