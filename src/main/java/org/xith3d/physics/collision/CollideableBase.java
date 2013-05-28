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

import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.util.MatrixUtils;
import org.xith3d.physics.simulation.Body;

/**
 * Common parent (abstract class) for all Collideable Objects.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class CollideableBase implements Collideable
{
    private final CollisionEngine engine;
    
    private String name = null;
    
    private Object userObject = null;
    
    private Body body = null;
    
    /** The parent node to this one */
    private CollideableGroup parent;
    
    /** The position (in local coordinates) */
    private final Point3f position;
    
    /** The position (in world coordinates) */
    private final Point3f worldPos;
    
    /** The rotation (in local coordinates) */
    private final Matrix3f rotation;
    
    /** The rotation (in world coordinates) */
    private final Matrix3f worldRot;
    
    /** Here is a lazily-allocated Point3f to avoid garbage creation */
    private Point3f posPoint3f = null;
    /** Here is a lazily-allocated Tuple3f to avoid garbage creation */
    private Tuple3f rotTuple3f = null;
    /** Here is a lazily-allocated Matrix3f to avoid garbage creation */
    private Matrix3f rotMatrix3f = null;
    
    /**
     * {@inheritDoc}
     */
    public CollisionEngine getEngine()
    {
        return ( engine );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setName( String name )
    {
        this.name = name;
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getName()
    {
        return ( name );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setUserObject( Object userObject )
    {
        this.userObject = userObject;
    }
    
    /**
     * {@inheritDoc}
     */
    public final Object getUserObject()
    {
        return ( userObject );
    }
    
    /**
     * Sets this Collideable's parent group.
     * 
     * @param parent
     */
    protected void setParent( CollideableGroup parent )
    {
        this.parent = parent;
    }
    
    /**
     * {@inheritDoc}
     */
    public final CollideableGroup getParent()
    {
        return ( parent );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBody( Body body )
    {
        this.body = body;
    }
    
    /**
     * {@inheritDoc}
     */
    public final Body getBody()
    {
        if ( body != null )
            return ( body );
        
        if ( getParent() != null )
            return ( getParent().getBody() );
        
        return ( null );
    }
    
    public void setRotation( float x, float y, float z, boolean recomputeWorldCoords )
    {
        MatrixUtils.eulerToMatrix3f( x, y, z, rotation );
        
        if ( recomputeWorldCoords )
            recomputeWorldCoords( true, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setRotation( float x, float y, float z )
    {
        setRotation( x, y, z, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setRotation( Tuple3f rot )
    {
        setRotation( rot.getX(), rot.getY(), rot.getZ() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setRotationX( float x )
    {
        Tuple3f rotation = getRotation();
        rotation.setX( x );
        setRotation(rotation);
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setRotationY( float y )
    {
        Tuple3f rotation = getRotation();
        rotation.setY( y );
        setRotation( rotation );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setRotationZ( float z )
    {
        Tuple3f rotation = getRotation();
        rotation.setZ( z );
        setRotation( rotation );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Tuple3f getRotation()
    {
        if ( rotTuple3f == null )
            rotTuple3f = new Tuple3f();
        
        MatrixUtils.matrixToEuler( rotation, rotTuple3f );
        
        return ( rotTuple3f );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getRotation( Tuple3f rot )
    {
        MatrixUtils.matrixToEuler( rotation, rot );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setRotationMatrix( Matrix3f rot, boolean recomputeWorldCoords )
    {
        rotation.set( rot );
        
        if ( recomputeWorldCoords )
            recomputeWorldCoords( true, true );
    }
    
    public final void setRotationMatrix( Matrix3f rot )
    {
        setRotationMatrix( rot, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Matrix3f getRotationMatrix()
    {
        return ( rotation.getReadOnly() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getRotationMatrix( Matrix3f mat )
    {
        mat.set( rotation );
    }
    
    public void setPosition( float x, float y, float z, boolean recomputeWorldCoords )
    {
        position.set( x, y, z );
        
        if ( recomputeWorldCoords )
            recomputeWorldCoords( true, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setPosition( float x, float y, float z )
    {
        setPosition( x, y, z, true );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setPosition( Tuple3f pos )
    {
        setPosition( pos.getX(), pos.getY(), pos.getZ() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setPositionX( float x )
    {
        Tuple3f position = getPosition();
        setPosition( x, position.getY(), position.getZ() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setPositionY( float y )
    {
        Tuple3f position = getPosition();
        setPosition( position.getX(), y, position.getZ() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void setPositionZ( float z )
    {
        Tuple3f position = getPosition();
        setPosition( position.getX(), position.getY(), z );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Point3f getPosition()
    {
        return ( position.getReadOnly() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getPosition( Tuple3f pos )
    {
        pos.set( this.position );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Point3f getWorldPos()
    {
        if ( posPoint3f == null )
            posPoint3f = new Point3f();
        
        getWorldPos( posPoint3f );
        
        return ( posPoint3f );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getWorldPos( Tuple3f pos )
    {
        pos.set( worldPos );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Tuple3f getWorldRot()
    {
        if ( rotTuple3f == null )
            rotTuple3f = new Tuple3f();
        
        getWorldRot( rotTuple3f );
        
        return ( rotTuple3f );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getWorldRot( Tuple3f rot )
    {
        MatrixUtils.matrixToEuler( getWorldRotMat(), rot );
    }
    
    /**
     * {@inheritDoc}
     */
    public final Matrix3f getWorldRotMat()
    {
        if ( rotMatrix3f == null )
            rotMatrix3f = new Matrix3f();
        
        getWorldRotMat( rotMatrix3f );
        
        return ( rotMatrix3f );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void getWorldRotMat( Matrix3f rot )
    {
        rot.set( worldRot );
    }
    
    
    /**
     * Applies the world-rotation to the implementation.
     * 
     * @param worldRot
     */
    protected abstract void applyWorldRotation( Matrix3f worldRot );
    
    /**
     * Applies the world-position to the implementation.
     * 
     * @param worldPos
     */
    protected abstract void applyWorldPosition( Tuple3f worldPos );
    
    /**
     * Recomputes the World coordinates rotation matrix.
     * 
     * @param worldRot
     * @param applyToImplementation
     */
    protected void recomputeWorldRotMat( Matrix3f worldRot, boolean applyToImplementation )
    {
        if ( getParent() == null )
        {
            worldRot.setIdentity();
        }
        else
        {
            getParent().getWorldRotMat( worldRot );
        }
        
        worldRot.mul( getRotationMatrix() );
        
        if ( applyToImplementation )
        {
            applyWorldRotation( worldRot );
        }
    }
    
    /**
     * Recomputes the World coordinates position.
     * 
     * @param worldPos
     * @param applyToImplementation
     */
    protected void recomputeWorldPos( Point3f worldPos, boolean applyToImplementation )
    {
        if ( getParent() == null )
        {
            worldPos.set( getPosition() );
        }
        else
        {
            if ( posPoint3f == null )
                posPoint3f = new Point3f();
            if ( rotMatrix3f == null )
                rotMatrix3f = new Matrix3f();
            
            getParent().getWorldPos( posPoint3f );
            getParent().getWorldRotMat( rotMatrix3f );
            
            worldPos.set( getPosition() );
            rotMatrix3f.transform( worldPos );
            worldPos.add( posPoint3f, worldPos );
        }
        
        if ( applyToImplementation )
        {
            applyWorldPosition( worldPos );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void recomputeChildrenWorldCoords( boolean applyToImplementation )
    {
        if ( this instanceof CollideableGroup )
        {
            final int n = ( (CollideableGroup)this ).getChildrenCount();
            for ( int i = 0; i < n; i++ )
            {
                ( (CollideableGroup)this ).getChild( i ).recomputeWorldCoords( true, applyToImplementation );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void recomputeWorldCoords( boolean childrenToo, boolean applyToImplementation )
    {
        recomputeWorldPos( worldPos, applyToImplementation );
        recomputeWorldRotMat( worldRot, applyToImplementation );
        
        if ( childrenToo )
        {
            recomputeChildrenWorldCoords( applyToImplementation );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( this.getName() == null )
            return ( super.toString() );
        
        return ( super.toString() + " \"" + this.getName() + "\"" );
    }
    
    /**
     * Creates a new {@link CollideableBase}.
     */
    public CollideableBase( CollisionEngine engine )
    {
        this.engine = engine;
        
        this.position = new Point3f();
        this.worldPos = new Point3f();
        this.rotation = new Matrix3f();
        this.rotation.setIdentity();
        this.worldRot = new Matrix3f();
        this.worldRot.setIdentity();
    }
}
