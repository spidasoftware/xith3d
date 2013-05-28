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
package org.xith3d.physics.collision;

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.datatypes.NamableObject;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.physics.simulation.Body;
import org.xith3d.physics.util.Placeable;
import org.xith3d.scenegraph.Node;

/**
 * A Collideable is an object, which can collide with others objects.
 * 
 * @see CollisionEngine
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public interface Collideable extends Placeable, NamableObject, Enableable
{
    /**
     * @return the name of the Geom, that is, the one which is used to refer to
     *         it in CollisionEngine. All camel-case, please ! (e.g. : "Sphere")
     */
    public String getType();
    
    /**
     * @return a small description of how is this geom, its exact
     * shape, a brief human-readable description of what can be adjusted
     */
    public String getInfo();
    
    /**
     * @return the collision engine of this Collideable
     */
    public CollisionEngine getEngine();
    
    /**
     * @return a "base" GFX (can be used as a start, then the users adjusts
     *         himself e.g. the Material or even apply a deformation to the
     *         Shape)
     *         
     *         The only *rule* for this object is that it should extend
     *         org.xith3d.scenegraph.Node so that it can be added to the
     *         scenegraph. So it can be e.g. a Shape3D, a Group
     *
     * @see #getDebugGFX()
     */
    public Node getBaseGFX();
    
    /**
     * @return a "debug" GFX (probably wireframe, and maybe bounds displayed,
     *         and so on)
     *         
     *         The only *rule* for this object is that it should extend
     *         org.xith3d.scenegraph.Node so that it can be added to the
     *         scenegraph. So it can be e.g. a Shape3D, a Group
     *
     * @see #getBaseGFX()
     */
    public Node getDebugGFX();
    
    /**
     * Gets the parent of this Collideable, or <code>null</code> if it has no parent.
     * 
     * @return The parent, or <code>null</code>
     */
    public CollideableGroup getParent();
    
    /**
     * Sets the Body, this Collideable is attached to.
     * 
     * @param body
     */
    public void setBody( Body body );
    
    /**
     * @return the Body, this Collideable is attached to.
     */
    public Body getBody();
    
    /**
     * Sets this Collideable's user-object.
     * 
     * @param userObject
     */
    public void setUserObject( Object userObject );
    
    /**
     * @return this Collideable's user-object.
     */
    public Object getUserObject();
    
    /**
     * Enables or disables this {@link Collideable}.
     * 
     * @param enabled
     */
    public abstract void setEnabled( boolean enabled );
    
    /**
     * @return true, if this {@link Collideable} is enabled.
     */
    public abstract boolean isEnabled();
    
    /*
     * METHODS FROM THE org.jagatoo.datatypes.Placeable INTERFACE :
     * Javadoc changed for clarity.
     */
    
    /**
     * @return The position of this Collideable, in Local coordinates, (e.g.
     *         without pos/rot of parents taken into account). To get World coordinates,
     * 
     * @see #getWorldPos()
     */
    public Point3f getPosition();
    
    /**
     * Writes the position of this Placeable object to the
     * given Tuple3f, in Local coordinates (e.g. without pos/rot of
     * parents taken into account). To get World coordinates, {@link #getWorldPos()}.
     * 
     * @param pos The Tuple3f to put the pos into
     */
    public void getPosition( Tuple3f pos );
    
    /**
     * Sets the position, in local coordinates of this Placeable object
     * To get world coordinates, {@link #getWorldPos()}.
     * 
     * @param pos
     */
    public void setPosition( Tuple3f pos );
    
    /**
     * Sets the position, in local coordinates of this Placeable object.
     * to get world coordinates {@link #getWorldPos()}.
     */
    public void setPosition( float posX, float posY, float posZ );
    
    /**
     * @return The rotation (local coordinates), in Euler angles (degrees) of
     * this Placeable object
     * To get rotation in World coordinates, {@link #getWorldRot()}.
     */
    public Tuple3f getRotation();
    
    /**
     * Writes the rotation (local coordinates), in Euler angles (degrees) of
     * this Placeable object in the given Tuple3f.
     * To get rotation in World coordinates, {@link #getWorldRot()}
     */
    public void getRotation( Tuple3f rot );
    
    /**
     * @return The rotation (local coordinates), as a 3x3 rotation Matrix,
     * of this Placeable object.
     * To get rotation matrix in World coordinates, {@link #getWorldRotMat()}.
     */
    public Matrix3f getRotationMatrix();
    
    /**
     * Writes the rotation, as a 3x3 rotation Matrix,
     * of this Placeable object in the given Matrix3f.
     * To get rotation matrix in World coordinates, {@link #getWorldRotMat()}.
     */
    public void getRotationMatrix( Matrix3f rot );
    
    /**
     * Sets the rotation of this object, in Euler angles
     * (degrees), local coordinates.
     * 
     * @param rot The rotation, in Euler angles (degrees) of
     * this Placeable object
     */
    public void setRotation( Tuple3f rot );
    
    /**
     * Sets the rotation Matrix of this object,
     * local coordinates.
     * 
     * @param rot The rotation, as a 3x3 rotation Matrix,
     * of this Placeable object
     */
    public void setRotationMatrix( Matrix3f rot );
    
    /**
     * Sets the rotation (in local coordinates) of this Collideable,
     * local coordinates.
     * 
     * @param rotX The x-rotation of the Collideable
     * @param rotY The y-rotation of the Collideable
     * @param rotZ The z-rotation of the Collideable
     */
    public void setRotation( float rotX, float rotY, float rotZ );
    
    /**
     * Sets the X position (in local coordinates) of this Collideable
     * 
     * @param x
     */
    public void setPositionX( float x );
    
    /**
     * Sets the Y position (in local coordinates) of this Collideable
     * 
     * @param y
     */
    public void setPositionY( float y );
    
    /**
     * Sets the Z position (in local coordinates) of this Collideable
     * 
     * @param z
     */
    public void setPositionZ( float z );
    
    /**
     * Sets the X rotation (in local coordinates) of this Collideable
     * 
     * @param x
     */
    public void setRotationX( float x );
    
    /**
     * Sets the Y rotation (in local coordinates) of this Collideable
     * 
     * @param y
     */
    public void setRotationY( float y );
    
    /**
     * Sets the Z rotation (in local coordinates) of this Collideable
     * 
     * @param z
     */
    public void setRotationZ( float z );
    
    /**
     * Gets position, in world coordinates, which means, taking
     * into account all transformations from parents, and all.
     * Correct implementation of this method is up to the Physic
     * Engine's developers, but it should be pretty easy anyway.
     * Order of transformation is Translate, then Rotate. There
     * is no scale.
     * 
     * @return The world coordinates position.
     */
    public Point3f getWorldPos();
    
    /**
     * Writes position to pos, in world coordinates, which means, taking
     * into account all transformations from parents, and all.
     * Correct implementation of this method is up to the Physic
     * Engine's developers, but it should be pretty easy anyway.
     * Order of transformation is Translate, then Rotate. There
     * is no scale.
     * 
     * @param pos The Tuple3f to write in.
     */
    public void getWorldPos( Tuple3f pos );
    
    /**
     * Gets rotation, in world coordinates, which means, taking
     * into account all transformations from parents, and all.
     * Correct implementation of this method is up to the Physic
     * Engine's developers, but it should be pretty easy anyway.
     * Order of transformation is Translate, then Rotate. There
     * is no scale.
     * 
     * @return The world coordinates position.
     */
    public Tuple3f getWorldRot();
    
    /**
     * Writes rotation to rot, in world coordinates, which means, taking
     * into account all transformations from parents, and all.
     * Correct implementation of this method is up to the Physic
     * Engine's developers, but it should be pretty easy anyway.
     * Order of transformation is Translate, then Rotate. There
     * is no scale.
     * 
     * @param rot
     */
    public void getWorldRot( Tuple3f rot );
    
    /**
     * Gets rotation, in world coordinates, which means, taking
     * into account all transformations from parents, and all.
     * Correct implementation of this method is up to the Physic
     * Engine's developers, but it should be pretty easy anyway.
     * Order of transformation is Translate, then Rotate. There
     * is no scale.
     * 
     * @return The world coordinates position.
     */
    public Matrix3f getWorldRotMat();
    
    /**
     * Writes rotation to rot, in world coordinates, which means, taking
     * into account all transformations from parents, and all.
     * Correct implementation of this method is up to the Physic
     * Engine's developers, but it should be pretty easy anyway.
     * Order of transformation is Translate, then Rotate. There
     * is no scale.
     * 
     * @param rot
     */
    public void getWorldRotMat( Matrix3f rot );
    
    /**
     * Recompute the world coordinates of all children, if and only if
     * this JoodeCollideable is a space.
     * 
     * @param applyToImplementation
     */
    public void recomputeChildrenWorldCoords( boolean applyToImplementation );
    
    /**
     * Recompute world coordinates, e.g. world position and rotation.
     * This method should be called whenever a parent changes its
     * local position/rotation on all its children.
     * 
     * @param childrenToo
     * @param applyToImplementation
     */
    public void recomputeWorldCoords( boolean childrenToo, boolean applyToImplementation );
}
