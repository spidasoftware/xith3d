/**
 * Copyright (c) 2007-2008, JAGaToo Project Group all rights reserved.
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
package org.xith3d.physics.util;

import java.util.ArrayList;

import org.jagatoo.datatypes.RepositionListener3f;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.util.MatrixUtils;

/**
 * A small, GC-friendly implementation of the Placeable
 * interface. Extend it when you need Placeable without
 * much hassle and you don't already have a superclass.
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class PlaceableImpl implements ListeningPlaceable
{
    protected boolean valueCheckedBeforeChanged = false;
    
    protected final Point3f position;
    protected final Matrix3f rotation;
    private Tuple3f rotEuler = null;
    
    private final ArrayList<RepositionListener3f> repositionListeners = new ArrayList<RepositionListener3f>();
    
    /**
     * {@inheritDoc}
     */
    public void addRepositionListener( RepositionListener3f l )
    {
        repositionListeners.add( l );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeRepositionListener( RepositionListener3f l )
    {
        repositionListeners.remove( l );
    }
    
    protected void onPositionChanged()
    {
        for ( int i = 0; i < repositionListeners.size(); i++ )
        {
            repositionListeners.get( i ).onPositionChanged( position.getX(), position.getY(), position.getZ() );
        }
    }
    
    public void setPosition( float posX, float posY, float posZ )
    {
        if ( valueCheckedBeforeChanged && ( position.getX() == posX ) && ( position.getY() == posY ) && ( position.getZ() == posZ ) )
            return;
        
        this.position.set( posX, posY, posZ );
        
        onPositionChanged();
    }
    
    public final void setPosition( Tuple3f pos )
    {
        setPosition( pos.getX(), pos.getY(), pos.getZ() );
    }
    
    public final Point3f getPosition()
    {
        return ( this.position.getReadOnly() );
    }
    
    public final void getPosition( Tuple3f pos )
    {
        pos.set( this.position );
    }
    
    protected void onRotationChanged()
    {
    }
    
    public void setRotation( float rotX, float rotY, float rotZ )
    {
        MatrixUtils.eulerToMatrix3f( rotX, rotY, rotZ, this.rotation );
        
        onRotationChanged();
    }
    
    public final void setRotation( Tuple3f rot )
    {
        setRotation( rot.getX(), rot.getY(), rot.getZ() );
    }
    
    public final Tuple3f getRotation()
    {
        if ( rotEuler == null )
            rotEuler = new Tuple3f();
        
        MatrixUtils.matrixToEuler( this.rotation, rotEuler );
        
        return ( rotEuler.getReadOnly() );
    }
    
    public final void getRotation( Tuple3f rot )
    {
        setRotation( rot.getX(), rot.getY(), rot.getZ() );
    }
    
    public void setRotationMatrix( Matrix3f rot )
    {
        this.rotation.set( rot );
        
        onRotationChanged();
    }
    
    public final Matrix3f getRotationMatrix()
    {
        return ( this.rotation.getReadOnly() );
    }
    
    public final void getRotationMatrix( Matrix3f rot )
    {
        rot.set( this.rotation );
    }
    
    /*
     * Additionnal methods (e.g. for those wanting to connect
     * Interpolators to only one dimension
     */
    
    public final void setPositionX( float v ) { this.position.setX( v ); onPositionChanged(); }
    public final void setPositionY( float v ) { this.position.setY( v ); onPositionChanged(); }
    public final void setPositionZ( float v ) { this.position.setZ( v ); onPositionChanged(); }
    
    public final float getPositionX() { return this.position.getX(); }
    public final float getPositionY() { return this.position.getY(); }
    public final float getPositionZ() { return this.position.getZ(); }
    
    public final void setRotationX( float v)  { Tuple3f rot = getRotation(); MatrixUtils.eulerToMatrix3f( v, rot.getY(), rot.getZ(), this.rotation ); onRotationChanged(); }
    public final void setRotationY( float v ) { Tuple3f rot = getRotation(); MatrixUtils.eulerToMatrix3f( rot.getX(), v, rot.getZ(), this.rotation ); onRotationChanged(); }
    public final void setRotationZ( float v ) { Tuple3f rot = getRotation(); MatrixUtils.eulerToMatrix3f( rot.getX(), rot.getY(), v, this.rotation ); onRotationChanged(); }
    
    public final float getRotationX() { return getRotation().getX(); }
    public final float getRotationY() { return getRotation().getY(); }
    public final float getRotationZ() { return getRotation().getZ(); }
    
    /**
     * Creation a new PlaceableImpl with pos (0, 0, 0)
     * and no rotation.
     */
    public PlaceableImpl()
    {
        this.position = new Point3f( 0f, 0f, 0f );
        this.rotation = new Matrix3f();
        this.rotation.setIdentity();
    }
}
