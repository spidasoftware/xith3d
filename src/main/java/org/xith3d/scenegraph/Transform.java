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
package org.xith3d.scenegraph;

import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

/**
 * The Transform class can be used to quickly transform any object, reducing the
 * number of lines needed (Transform3D construction, etc..) and improving
 * readability of your code.<br>
 * <br>
 * However, you can live without ^^
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class Transform extends TransformGroup
{
    /** The identity transform (no transformation at all) */
    public static final Transform IDENTITY_TRANSFORM = new Transform();
    
    public static final int ROTATION = 0;
    public static final int TRANSLATION = 1;
    public static final int SCALE = 2;
    
    // GC-friendly hacks
    private final Transform3D tempTransform = new Transform3D();
    private final AxisAngle3f tempAxisAngle = new AxisAngle3f();
    private final Vector3f tempVector = new Vector3f();
    
    /**
     * Sets a transformation.
     * 
     * @param mode
     *            Transform.ROTATION, Transform.TRANSLATION, or Transform.SCALE
     * @param x
     *            X value
     * @param y
     *            Y value
     * @param z
     *            Z value
     * @return This transform, for cascade method calls
     */
    public Transform setTransform( int mode, float x, float y, float z )
    {
        Transform3D t3d = getTransform();
        
        switch ( mode )
        {
            
            case ROTATION:
                t3d.rotXYZ( x, y, z );
                break;
                
            case TRANSLATION:
                t3d.setTranslation( x, y, z );
                break;
                
            case SCALE:
                t3d.setScale( x, y, z );
                break;
        }
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Sets a transformation.
     * 
     * @param mode
     *            Transform.ROTATION, Transform.TRANSLATION, or Transform.SCALE
     * @param transform
     *            If rotation, euler angles (in radians), if translation or
     *            scale relevant value
     * @return This transform, for cascade method calls
     */
    public Transform setTransform( int mode, Tuple3f transform )
    {
        Transform3D t3d = getTransform();
        
        switch ( mode )
        {
            case ROTATION:
                t3d.rotXYZ( transform.getX(), transform.getY(), transform.getZ() );
                break;
                
            case TRANSLATION:
                t3d.setTranslation( transform.getX(), transform.getY(), transform.getZ() );
                break;
                
            case SCALE:
                t3d.setScale( transform.getX(), transform.getY(), transform.getZ() );
                break;
        }
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Sets a translation.
     * 
     * @param value
     *            Translation
     * @return This transform, for cascade method calls
     */
    public Transform setTranslationX( float value )
    {
        setTranslation( value, 0f, 0f );
        
        return ( this );
    }
    
    /**
     * Sets a translation.
     * 
     * @param value
     *            Translation
     * @return This transform, for cascade method calls
     */
    public Transform setTranslationY( float value )
    {
        setTranslation( 0f, value, 0f );
        
        return ( this );
    }
    
    /**
     * Sets a translation.
     * 
     * @param value
     * 
     * @return This transform, for cascade method calls
     */
    public Transform setTranslationZ( float value )
    {
        Transform3D t3d = getTransform();
        
        t3d.setTranslation( 0f, 0f, value );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds a translation to the current transformation.
     * 
     * @param x
     *            X value
     * @param y
     *            Y value
     * @param z
     *            Z value
     * @return This transform, for cascade method calls
     */
    public Transform addTranslation( float x, float y, float z )
    {
        tempTransform.setIdentity();
        tempTransform.setTranslation( x, y, z );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Applies a translation.
     * 
     * @param x
     *            X value
     * @param y
     *            Y value
     * @param z
     *            Z value
     * @return This transform, for cascade method calls
     */
    public Transform setTranslation( float x, float y, float z )
    {
        Transform3D t3d = getTransform();
        
        t3d.setTranslation( x, y, z );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds a translation to the current transformation.
     * 
     * @param translate
     *            Translation
     * @return This transform, for cascade method calls
     */
    public Transform addTranslation( Tuple3f translate )
    {
        tempTransform.setIdentity();
        tempTransform.setTranslation( translate.getX(), translate.getY(), translate.getZ() );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Applies a translation.
     * 
     * @param translate
     *            Translation
     * @return This transform, for cascade method calls
     */
    public Transform setTranslation( Tuple3f translate )
    {
        Transform3D t3d = getTransform();
        
        t3d.setTranslation( translate.getX(), translate.getY(), translate.getZ() );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Applies a rotation.
     * 
     * @param rotate
     *            Euler angles, in radians
     * @return This transform, for cascade method calls
     */
    public Transform setRotation( Tuple3f rotate )
    {
        Transform3D t3d = getTransform();
        
        t3d.rotXYZ( rotate.getX(), rotate.getY(), rotate.getZ() );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Applies a rotation.
     * 
     * @param x
     *            X angle, in radians
     * @param y
     *            Y angle, in radians
     * @param z
     *            Z angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform setRotation( float x, float y, float z )
    {
        Transform3D t3d = getTransform();
        
        t3d.rotXYZ( x, y, z );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds a rotation to the current transformation.
     * 
     * @param rotate
     *            Euler angles, in radians
     * @return This transform, for cascade method calls
     */
    public Transform addRotation( Tuple3f rotate )
    {
        tempTransform.setIdentity();
        
        tempTransform.rotXYZ( rotate.getX(), rotate.getY(), rotate.getZ() );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Sets a rotation to the current transformation.
     * 
     * @param rotationMatrix
     */
    public Transform setRotation( Matrix3f rotationMatrix )
    {
        getTransform().setRotation( rotationMatrix );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Adds a rotation to the current transformation.
     * 
     * @param rotationMatrix
     */
    public Transform addRotation( Matrix3f rotationMatrix )
    {
        tempTransform.setIdentity();
        
        tempTransform.setRotation( rotationMatrix );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Adds a rotation to the current transformation.
     * 
     * @param x
     *            X angle, in radians
     * @param y
     *            Y angle, in radians
     * @param z
     *            Z angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform addRotation( float x, float y, float z )
    {
        tempTransform.setIdentity();
        
        tempTransform.rotXYZ( x, y, z );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Applies a rotation.
     * 
     * @param angle
     *            Euler angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform setRotationX( float angle )
    {
        Transform3D t3d = getTransform();
        
        t3d.rotXYZ( angle, 0f, 0f );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds a rotation to the current transformation.
     * 
     * @param angle
     *            Euler angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform addRotationX( float angle )
    {
        tempTransform.setIdentity();
        
        tempTransform.rotXYZ( angle, 0f, 0f );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Applies a rotation.
     * 
     * @param angle
     *            Euler angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform setRotationY( float angle )
    {
        Transform3D t3d = getTransform();
        
        t3d.rotXYZ( 0f, angle, 0f );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds a rotation to the current transformation.
     * 
     * @param angle
     *            Euler angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform addRotationY( float angle )
    {
        tempTransform.setIdentity();
        
        tempTransform.rotXYZ( 0f, angle, 0f );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Applies a rotation.
     * 
     * @param angle
     *            Euler angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform setRotationZ( float angle )
    {
        Transform3D t3d = getTransform();
        
        t3d.rotXYZ( 0f, 0f, angle );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds a rotation to the current transformation.
     * 
     * @param angle
     *            Euler angle, in radians
     * @return This transform, for cascade method calls
     */
    public Transform addRotationZ( float angle )
    {
        tempTransform.setIdentity();
        
        tempTransform.rotXYZ( 0f, 0f, angle );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Applies a scale.
     * 
     * @param scale
     *            X, Y, Z scale
     * @return This transform, for cascade method calls
     */
    public Transform setScale( Tuple3f scale )
    {
        Transform3D t3d = getTransform();
        
        t3d.setScale( scale );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Applies a scale.
     * 
     * @param x
     *            X scale
     * @param y
     *            Y scale
     * @param z
     *            Z scale
     * @return This transform, for cascade method calls
     */
    public Transform setScale( float x, float y, float z )
    {
        Transform3D t3d = getTransform();
        
        t3d.setScale( x, y, z );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Applies a scale.
     * 
     * @param scale
     *            X scale
     * @return This transform, for cascade method calls
     */
    public Transform setScaleX( float scale )
    {
        Transform3D t3d = getTransform();
        
        t3d.setScale( scale, 0f, 0f );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Applies a scale.
     * 
     * @param scale
     *            Y scale
     * @return This transform, for cascade method calls
     */
    public Transform setScaleY( float scale )
    {
        Transform3D t3d = getTransform();
        
        t3d.setScale( 0f, scale, 0f );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Applies a scale.
     * 
     * @param scale
     *            Z scale
     * @return This transform, for cascade method calls
     */
    public Transform setScaleZ( float scale )
    {
        Transform3D t3d = getTransform();
        
        t3d.setScale( 0f, 0f, scale );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Applies a scale.
     * 
     * @param scale
     *            X, Y and Z scale
     * @return This transform, for cascade method calls
     */
    public Transform setScale( float scale )
    {
        Transform3D t3d = getTransform();
        
        t3d.setScale( scale );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds a scale transformation to the current transform.
     * 
     * @param scale
     *            Scale factor
     * 
     * @return This transform, for cascade method calls
     */
    public Transform addScale( float scale )
    {
        Transform3D t3d = getTransform();
        tempTransform.setIdentity();
        
        tempTransform.setScale( scale );
        t3d.mul( tempTransform );
        
        setTransform( t3d );
        
        return ( this );
    }
    
    /**
     * Adds node n to this transform and returns n.
     * 
     * @param n
     *            The node to add
     * @return This transform, for cascade method calls
     */
    public Transform add( Node n )
    {
        this.addChild( n );
        
        return ( this );
    }
    
    /**
     * Adds transform t to this transform and returns t.
     * 
     * @param t
     *            The transform to add
     * @return The transform added
     */
    public Transform add( Transform t )
    {
        this.addChild( t );
        
        return ( t );
    }
    
    /**
     * Clears all transformations (Set the identity matrix).
     * 
     * @return itself
     */
    public Transform clear()
    {
        getTransform().setIdentity();
        updateTransform();
        
        return ( this );
    }
    
    /**
     * Sets rotation about an axis.
     * 
     * @param rotationAxis
     *            The axis to rotate about
     * @param angle
     *            The angle to rotate of, in radians
     * @return itself
     */
    public Transform setAxisRotation( Tuple3f rotationAxis, float angle )
    {
        tempAxisAngle.set( rotationAxis, angle );
        getTransform().setRotation( tempAxisAngle );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Adds rotation about an axis.
     * 
     * @param rotationAxis
     *            The axis to rotate about
     * @param angle
     *            The angle to rotate of, in radians
     * @return itself
     */
    public Transform addAxisRotation( Tuple3f rotationAxis, float angle )
    {
        tempTransform.setIdentity();
        tempAxisAngle.set( rotationAxis, angle );
        tempTransform.setRotation( tempAxisAngle );
        
        getTransform().mul( tempTransform );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * Sets translation about an axis with a specified length.
     * 
     * @param translationAxis
     * @param length
     * 
     * @return itself
     */
    public Transform setAxisTranslation( Tuple3f translationAxis, float length )
    {
        tempVector.set( translationAxis );
        tempVector.normalize();
        tempVector.scale( length );
        setTranslation( tempVector );
        
        return ( this );
    }
    
    /**
     * Sets translation about an axis with a specified length.
     * 
     * @param translationAxis
     * @param length
     * 
     * @return itself
     */
    public Transform addAxisTranslation( Tuple3f translationAxis, float length )
    {
        tempVector.set( translationAxis );
        tempVector.normalize();
        tempVector.scale( length );
        
        addTranslation( tempVector );
        
        return ( this );
    }
    
    /**
     * Sets this transform to a transformation corresponding to the
     * matrix argument.
     * 
     * @param matrix The transformation matrix to set this transform to
     * 
     * @return itself
     */
    public Transform setMatrix( Matrix4f matrix )
    {
        getTransform().set( matrix );
        setTransform( getTransform() );
        
        return ( this );
    }
    
    /**
     * @param node
     * 
     * @return The Transform which is parent to the given node (if it's the case)
     */
    public static Transform get( Node node )
    {
        if ( node.getParent() != null )
        {
            GroupNode parent = node.getParent();
            if ( parent instanceof Transform )
            {
                Transform t = (Transform) parent;
                return t;
            }
        }
        
        return ( null );
        
    }
    
    /**
     * Applies this transform to a Point3f.
     * Mathematically, the matrix of this transform is multiplied
     * with the input Tuple3f and the result is stored in it.
     * 
     * @param input the Tuple3f to be applied the Transform
     */
    public void transform( Point3f input )
    {
        this.getTransform().getMatrix4f().transform( input );
    }
    
    /**
     * Applies this transform to a Vector3f.
     * Mathematically, the matrix of this transform is multiplied
     * with the input Tuple3f and the result is stored in it.
     * 
     * @param input the Tuple3f to be applied the Transform
     */
    public void transform( Vector3f input )
    {
        this.getTransform().getMatrix4f().transform( input );
    }
    
    /**
     * Sets this transform to be equal to another one.
     * 
     * @param trans
     */
    public void set( Transform trans )
    {
        this.getTransform().set( trans.getTransform() );
        this.setTransform( this.getTransform() );
    }
    
    /**
     * @param node
     *            The node to transform
     */
    public Transform( Node node )
    {
        addChild( node );
    }
    
    /**
     * @param mode
     *            Transform.ROTATION, Transform.TRANSLATION, or Transform.SCALE
     * @param transform
     *            If rotation, euler angles (in radians), if translation or
     *            scale relevant value
     * @param node
     *            The node to transform
     */
    public Transform( int mode, Tuple3f transform, Node node )
    {
        addChild(node);
        
        setTransform(mode, transform);
    }
    
    /**
     * Creates an empty identity transform.
     */
    public Transform()
    {
        // Nothing to do here
    }
}
