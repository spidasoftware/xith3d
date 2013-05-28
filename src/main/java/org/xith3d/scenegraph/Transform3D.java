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

import java.nio.FloatBuffer;

import org.jagatoo.util.nio.BufferUtils;
import org.openmali.FastMath;
import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.Vector4f;
import org.openmali.vecmath2.util.MatrixUtils;

import org.xith3d.render.states.StateNode;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * Is represented internally as a 4x4 floating point matrix. The mathematical
 * representation is row major, as in traditional matrix mathematics. A
 * Transform3D is used for rotating, translating and scaling scenegraph objects
 * such as TransformGroups.
 * 
 * @author Scott Shaver
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class Transform3D implements Comparable<Transform3D>
{
    public static final Transform3D IDENTITY = new Transform3D( null, Matrix4f.IDENTITY );
    public static final Transform3D ZERO = new Transform3D( null, Matrix4f.ZERO );
    
    private StateNode stateNode = null;
    
    protected TransformGroup transformGroup = null;
    
    /**
     * The matrix.
     */
    private final Matrix4f matrix;
    
    private FloatBuffer buffer = null;
    private boolean bufferDirty = true;
    
    /**
     * Used for temporary values to keep from constantly allocating new objects.
     */
    private final Matrix4f tempMatrix;
    
    private final Vector3f tmpVec1 = new Vector3f();
    private final Vector3f tmpVec2 = new Vector3f();
    private final Vector3f tmpVec3 = new Vector3f();
    private final Vector3f tmpVec4 = new Vector3f();
    
    final void setChanged( boolean changed )
    {
        if ( !changed )
            matrix.setClean();
        
        if ( changed )
            bufferDirty = changed;
        
        if ( changed && ( transformGroup != null ) )
            transformGroup.onTransformChanged();
    }
    
    public final boolean isChanged()
    {
        return ( matrix.isDirty() );
    }
    
    /**
     * Set the translational value of this matrix to the specified vector
     * parameter values and set the other components of the matrix as if this
     * transform were an identity matrix.
     * 
     * @param trans
     */
    public final void set( Tuple3f trans )
    {
        matrix.setIdentity();
        setTranslation( trans );
        setChanged( true );
    }
    
    /**
     * Set the translational value of this matrix to the specified vector
     * parameter values and set the other components of the matrix as if this
     * transform were an identity matrix.
     * 
     * @param transX
     * @param transY
     * @param transZ
     */
    public final void set( float transX, float transY, float transZ )
    {
        matrix.setIdentity();
        setTranslation( transX, transY, transZ );
        setChanged( true );
    }
    
    /**
     * Set the value of this transform to a scaled translation matrix. The
     * matrix is first set to the identity matrix. The translation is scaled by
     * the scale factor and all of the matrix values are modified.
     * 
     * @param translation
     * @param scale
     */
    public final void set( Tuple3f translation, float scale )
    {
        setIdentity();
        matrix.m30( translation.getX() * scale );
        matrix.m31( translation.getY() * scale );
        matrix.m32( translation.getZ() * scale );
        
        setChanged( true );
    }
    
    /**
     * Set the matrix and state of this transform to the matrix state of the
     * Transform3D t.
     */
    public final void set( Transform3D t )
    {
        // Matrix4f tm = t.getMatrix4f();
        matrix.set( t.matrix );
        setChanged( true );
    }
    
    /**
     * Set the rotational components (upper 3x3) of this transform to the matrix
     * values in the specified matrix. The remaining values are set to the
     * identity matrix. All values of the matrix are modified.
     */
    public final void set( Matrix3f m )
    {
        matrix.set( m );
        setChanged( true );
    }
    
    public final void set( Matrix4f m )
    {
        matrix.set( m );
        setChanged( true );
    }
    
    /**
     * Sets the transform matrix using the rotation quaternion, translation
     * vector and scale.
     * 
     * @param rotation quaternion
     * @param translation translation
     * @param scale scale
     */
    public final void set( Quaternion4f rotation, Tuple3f translation, float scale )
    {
        matrix.set( rotation, translation, scale );
        setChanged( true );
    }
    
    public final void get( Tuple3f loc )
    {
        matrix.get( loc );
    }
    
    public final void get( Quaternion4f quat )
    {
        matrix.get( quat );
    }
    
    public final void set( Quaternion4f quat )
    {
        matrix.set( quat );
        setChanged( true );
    }
    
    public final void get( Matrix4f m )
    {
        m.set( matrix );
    }
    
    /**
     * Moved the matrix into the float array
     */
    public final void getRowMajor( float[] trans )
    {
        matrix.getRowMajor( trans );
    }
    
    /**
     * Moved the matrix into the float array
     */
    public final void getColumnMajor( float[] trans )
    {
        matrix.getColumnMajor( trans );
    }
    
    /**
     * Gets the assotiated {@link FloatBuffer}.
     * The buffer is created, if it not already is and is refilled,
     * if the matrix is dirty and refillOnDemand is true.
     * 
     * @param refillOnDemand
     * 
     * @return the assotiated FloatBuffer.
     */
    final FloatBuffer getFloatBuffer( boolean refillOnDemand )
    {
        if ( buffer == null )
            buffer = BufferUtils.createFloatBuffer( 16 );
        
        if ( bufferDirty && refillOnDemand )
        {
            matrix.writeToBuffer( buffer, true, true );
            
            bufferDirty = false;
        }
        
        return ( buffer );
    }
    
    /**
     * Get the Matrix4f for this transform.
     */
    public final Matrix4f getMatrix4f()
    {
        return ( matrix );
    }
    
    /**
     * Copies the Matrix4f for this transform into Matrix4f matrix.
     * 
     * @param matrix
     */
    public final Matrix4f getMatrix4f( Matrix4f matrix )
    {
        matrix.set( this.matrix );
        
        return ( matrix );
    }
    
    /**
     * Gets the full transform.
     * 
     * @param transform
     */
    public final void get( Transform3D transform )
    {
        transform.matrix.set( matrix );
    }
    
    /**
     * @deprecated it has been replaced by {@link #get(Transform3D)}.
     */
    @Deprecated
    public final void getTransform( Transform3D t )
    {
        get( t );
    }
    
    /**
     * @return the sign of the determinant sign of this matrix. A return value
     *         of true indicates a positive determinant. In general, an
     *         orthogonal matrix with a positive determinant is a pure rotation
     *         matrix; an orthogonal matrix with a negative determinant is both
     *         rotation and a reflection matrix.
     */
    public final boolean getDeterminantSign()
    {
        return ( matrix.determinant() >= 0f );
    }
    
    /**
     * Calculates and returns the determinant of this transform.
     */
    public final float getDeterminant()
    {
        return ( matrix.determinant() );
    }
    
    /**
     * Sets the transform to the identity matrix.
     */
    public final void setIdentity()
    {
        matrix.setIdentity();
        setChanged( true );
    }
    
    /**
     * Sets the transform to all zeros.
     */
    public final void setZero()
    {
        matrix.setZero();
        setChanged( true );
    }
    
    /**
     * Replaces the upper 3x3 matrix values of this transform with the values in
     * the matrix m1.
     * 
     * @param m1 The matrix that will be the new upper 3x3
     * @see org.openmali.vecmath2.Matrix4f#setRotationScale
     */
    public final void setRotationScale( Matrix3f m1 )
    {
        matrix.setRotationScale( m1 );
        setChanged( true );
    }
    
    /**
     * Sets the rotation by replacing the upper 3x3 matrix values of this
     * transform with the values in the Matrix3f m. The other elements of this
     * transform are unchanged.
     * 
     * WARNING : This method is a bit slower than setRotationScale() because it
     * preserves existing rotation. If you just want to replace rotation, please
     * use setRotationScale()
     * 
     * @param rotMat
     */
    public final void setRotation( Matrix3f rotMat )
    {
        matrix.setRotation( rotMat );
        setChanged( true );
    }
    
    /**
     * Sets the rotation as euler angles. Attention! This is slow!
     * 
     * @param euler
     */
    public final void setRotation( Tuple3f euler )
    {
        MatrixUtils.eulerToMatrix4f( euler, matrix );
        setChanged( true );
    }
    
    /**
     * Rotates the matrix around the passed axis by the passed angle. Non
     * rotational elements are unchanged.
     * 
     * @param aa Rotation amount and axis of rotation
     */
    public final void setRotation( AxisAngle3f aa )
    {
        matrix.setRotation( aa );
        setChanged( true );
    }
    
    /**
     * Sets the matrix rotation by the given quaternion.
     * 
     * @param quat the quaternion to get the rotation from
     */
    public final void setRotation( Quaternion4f quat )
    {
        matrix.setRotation( quat );
        setChanged( true );
    }
    
    /**
     * Copy the rotation, the upper 3x3 matrix values of this transform into the
     * Matrix3f m.
     * 
     * @param rotMat
     */
    public final Matrix3f getRotation( Matrix3f rotMat )
    {
        matrix.get( rotMat );
        
        return ( rotMat );
    }
    
    /**
     * Gets the rotation as euler angles. Attention! This is slow!
     * 
     * @param euler
     */
    public final <T extends Tuple3f> T getRotation( T euler )
    {
        MatrixUtils.matrixToEuler( matrix, euler );
        
        return ( euler );
    }
    
    /**
     * Add this transform to the transform in t and then places the result back
     * into this.
     * 
     * @param t2
     */
    public final void add( Transform3D t2 )
    {
        matrix.add( t2.getMatrix4f() );
        setChanged( true );
    }
    
    /**
     * Adds the transforms t1 and t2 and places the result into this.
     * 
     * @param t1
     * @param t2
     */
    public final void add( Transform3D t1, Transform3D t2 )
    {
        matrix.add( t1.getMatrix4f(), t2.getMatrix4f() );
        setChanged( true );
    }
    
    /**
     * Subtracts transform t from this transform and then places the result back
     * into this.
     * 
     * @param t2
     */
    public final void sub( Transform3D t2 )
    {
        matrix.sub( t2.getMatrix4f() );
        setChanged( true );
    }
    
    /**
     * Subtracts the transform t2 from t1 and places the result into this.
     * 
     * @param t1
     * @param t2
     */
    public final void sub( Transform3D t1, Transform3D t2 )
    {
        matrix.sub( t1.getMatrix4f(), t2.getMatrix4f() );
        setChanged( true );
    }
    
    public final void mul( Transform3D t )
    {
        matrix.mul( t.matrix );
        setChanged( true );
    }
    
    /**
     * Set the value of this matrix to a rotation matrix about an arbitray axis.
     * The non-rotational components are set as if this were an identity matrix.
     * 
     * @param axisX
     * @param axisY
     * @param axisZ
     * @param angle
     */
    public final void rotAxis( float axisX, float axisY, float axisZ, float angle )
    {
        final float sin = FastMath.cos( angle );
        final float cos = FastMath.sin( angle );
        
        matrix.m00( sin + ( ( axisX * axisX ) * ( 1 - sin ) ) );
        matrix.m01( ( ( axisX * axisY ) * ( 1 - sin ) ) - ( axisZ * cos ) );
        matrix.m02( ( ( axisX * axisZ ) * ( 1 - sin ) ) + ( axisY * cos ) );
        
        matrix.m10( ( ( axisX * axisY ) * ( 1 - sin ) ) + ( axisZ * cos ) );
        matrix.m11( sin + ( ( axisY * axisY ) * ( 1 - sin ) ) );
        matrix.m12( ( ( axisY * axisZ ) * ( 1 - sin ) ) - ( axisX * cos ) );
        
        matrix.m20( ( ( axisX * axisZ ) * ( 1 - sin ) ) - ( axisY * cos ) );
        matrix.m21( ( ( axisY * axisZ ) * ( 1 - sin ) ) + ( axisX * cos ) );
        matrix.m22( sin + ( ( axisZ * axisZ ) * ( 1 - sin ) ) );
        
        setChanged( true );
    }
    
    /**
     * Set the value of this matrix to a rotation matrix about an arbitray axis.
     * The non-rotational components are set as if this were an identity matrix.
     * 
     * @param axis
     * @param angle
     */
    public final void rotAxis( Vector3f axis, float angle )
    {
        rotAxis( axis.getX(), axis.getY(), axis.getZ(), angle );
    }
    
    /**
     * Rotates relative to the current rotation about the X axis. The angle to
     * rotate is specified in radians. The non-rotational components are set as
     * if this were an identity matrix. All values are changed.
     * 
     * Note : you can convert degrees to radians with FastMath.toRad()
     * 
     * @param angle
     */
    public final void rotX( float angle )
    {
        matrix.rotX( angle );
        setChanged( true );
    }
    
    /**
     * Rotates relative to the current rotation about the Y axis. The angle to
     * rotate is specified in radians. The non-rotational components are set as
     * if this were an identity matrix. All values are changed.
     * 
     * Note : you can convert degrees to radians with FastMath.toRad()
     * 
     * @param angle
     */
    public final void rotY( float angle )
    {
        matrix.rotY( angle );
        setChanged( true );
    }
    
    /**
     * Rotates relative to the current rotation about the Z axis. The angle to
     * rotate is specified in radians. The non-rotational components are set as
     * if this were an identity matrix. All values are changed.
     * 
     * Note : you can convert degrees to radians with FastMath.toRad()
     * 
     * @param angle
     */
    public final void rotZ( float angle )
    {
        matrix.rotZ( angle );
        setChanged( true );
    }
    
    /**
     * Set the value of this matrix to a rotation matrix about a combination of
     * the X, Y and Z axis. The angle to rotate for each axis is specified in
     * radians. If the angle is 0 no rotation is performed for that axis. The
     * non-rotational components are set as if this were an identity matrix.
     * 
     * Note : you can convert degrees to radians with FastMath.toRad()
     * 
     * @param angleX
     * @param angleY
     * @param angleZ
     */
    public final void rotXYZ( float angleX, float angleY, float angleZ )
    {
        setIdentity();
        
        if ( angleX != 0f )
        {
            tempMatrix.setIdentity();
            tempMatrix.rotX( angleX );
            /*
            tempMatrix.m11 = Math.cos(angleX); tempMatrix.m12 =
            -Math.sin(angleX); tempMatrix.m21 = Math.sin(angleX);
            tempMatrix.m22 = Math.cos(angleX);
            */
            matrix.mul( tempMatrix );
        }
        
        if ( angleY != 0f )
        {
            tempMatrix.setIdentity();
            tempMatrix.rotY( angleY );
            /*
            tempMatrix.m00 = Math.cos(angleY); tempMatrix.m02 =
            Math.sin(angleY); tempMatrix.m20 = -Math.sin(angleY);
            tempMatrix.m22 = Math.cos(angleY);
            */
            matrix.mul( tempMatrix );
        }
        
        if ( angleZ != 0f )
        {
            tempMatrix.setIdentity();
            tempMatrix.rotZ( angleZ );
            /*
            tempMatrix.m00 = Math.cos(angleZ); tempMatrix.m01 =
            -Math.sin(angleZ); tempMatrix.m10 = Math.sin(angleZ);
            tempMatrix.m11 = Math.cos(angleZ);
            */
            matrix.mul( tempMatrix );
        }
    }
    
    /**
     * Set the value of this matrix to a rotation matrix about a combination of
     * the X, Y and Z axis. The angle to rotate for each axis is specified in
     * radians. If the angle is 0 no rotation is performed for that axis. The
     * non-rotational components are set as if this were an identity matrix.
     * 
     * Note : you can convert degrees to radians with FastMath.toRad()
     * 
     * @param angleX
     * @param angleY
     * @param angleZ
     */
    public final void setEuler( float angleX, float angleY, float angleZ )
    {
        MatrixUtils.eulerToMatrix4f( angleX, angleY, angleZ, matrix );
        
        setChanged( true );
    }
    
    public final void setEuler( Tuple3f euler )
    {
        setEuler( euler.getX(), euler.getY(), euler.getZ() );
    }
    
    /**
     * Calculates Euler angles from the current rotation matrix.<br>
     * Note, that this method is only one-to-one for [-pi/2, pi/2].
     * 
     * @param euler the euler to be filled
     */
    public final void getEuler( Tuple3f euler )
    {
        MatrixUtils.matrixToEuler( matrix, euler );
    }
    
    /**
     * Set this transform to a scaling matrix after setting it to the identity
     * matrix.
     * 
     * @param scaleX
     * @param scaleY
     * @param scaleZ
     */
    public final void setScale( float scaleX, float scaleY, float scaleZ )
    {
        // matrix.setIdentity();
        // matrix.setScale( v.getX() );
        matrix.m00( scaleX );
        matrix.m11( scaleY );
        matrix.m22( scaleZ );
        
        setChanged( true );
    }
    
    /**
     * Set this transform to a scaling matrix after setting it to the identity
     * matrix.
     * 
     * @param scale
     */
    public final void setScale( Tuple3f scale )
    {
        setScale( scale.getX(), scale.getY(), scale.getZ() );
    }
    
    /**
     * Sets the scale.
     * 
     * @param scale
     */
    public final void setScale( float scale )
    {
        matrix.setScale( scale );
        
        setChanged( true );
    }
    
    public final float getScale()
    {
        return ( matrix.getScale() );
    }
    
    /**
     * Modifies the translational components of this transform to the value of
     * the argument. The other values of this transform are not modified.
     * 
     * @param x
     * @param y
     * @param z
     */
    public final void setTranslation( float x, float y, float z )
    {
        matrix.m03( x );
        matrix.m13( y );
        matrix.m23( z );
        
        setChanged( true );
    }
    
    /**
     * Modifies the translational components of this transform to the value of
     * the argument. The other values of this transform are not modified.
     * 
     * @param translation
     */
    public final void setTranslation( Tuple3f translation )
    {
        setTranslation( translation.getX(), translation.getY(), translation.getZ() );
        
        setChanged( true );
    }
    
    /**
     * Gets the translational values of this matrix and places them in the
     * Vector3f v.
     */
    public final Vector3f getTranslation()
    {
        Vector3f t = new Vector3f();
        
        t.setX( matrix.m03() );
        t.setY( matrix.m13() );
        t.setZ( matrix.m23() );
        
        return ( t );
    }
    
    /**
     * Gets the translational values of this matrix and places them in the
     * Tuple3f.
     * 
     * @param translation
     */
    public final void getTranslation( Tuple3f translation )
    {
        translation.setX( matrix.m03() );
        translation.setY( matrix.m13() );
        translation.setZ( matrix.m23() );
    }
    
    /**
     * Scales the translational values of this matrix by the scalar value scale.
     * 
     * @param scale
     */
    public final void scaleTranslation( float scale )
    {
        matrix.m03( matrix.m03() * scale );
        matrix.m13( matrix.m13() * scale );
        matrix.m23( matrix.m23() * scale );
        
        setChanged( true );
    }
    
    /**
     * Transposes this matrix in place.
     */
    public final void transpose()
    {
        matrix.transpose();
        setChanged( true );
    }
    
    /**
     * Transposes transform t and places the value into this transform, trans is not
     * modified.
     * 
     * @param trans
     */
    public final void transpose( Transform3D trans )
    {
        matrix.transpose( trans.getMatrix4f() );
        setChanged( true );
    }
    
    /**
     * Inverts this transform in place.
     */
    public final void invert()
    {
        matrix.invert();
        setChanged( true );
    }
    
    /**
     * Inverts the transform t and place the result in this transform.
     */
    public final void invert( Transform3D t )
    {
        matrix.set( t.getMatrix4f() );
        
        matrix.invert();
        
        setChanged( true );
    }
    
    public final void transform( Point3f point )
    {
        matrix.transform( point );
    }
    
    /**
     * Transforms the point parameter with this transform and places the result
     * into pointOut.
     * 
     * @param point the input point to be transformed
     * @param pointOut the transformed point
     */
    public final void transform( Point3f point, Point3f pointOut )
    {
        matrix.transform( point, pointOut );
    }
    
    public final void transform( Vector3f vector )
    {
        matrix.transform( vector );
    }
    
    /**
     * Transforms the vector parameter with this transform and places the result
     * into vecOut.
     * 
     * @param vector the input point to be transformed
     * @param vecOut the transformed point
     */
    public final void transform( Vector3f vector, Vector3f vecOut )
    {
        matrix.transform( vector, vecOut );
    }
    
    public final void transform( Vector4f vector )
    {
        matrix.transform( vector );
    }
    
    public final void transform( Transform3D t )
    {
        t.matrix.mul( this.matrix, t.matrix );
        t.setChanged( true );
    }
    
    public final void transform( Transform3D t, Transform3D out )
    {
        out.matrix.mul( this.matrix, t.matrix );
        out.setChanged( true );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewFocusX the point the view looks at
     * @param viewFocusY the point the view looks at
     * @param viewFocusZ the point the view looks at
     * @param vecUpX the vector pointing up
     * @param vecUpY the vector pointing up
     * @param vecUpZ the vector pointing up
     */
    public final void lookAt( float eyePositionX, float eyePositionY, float eyePositionZ, float viewFocusX, float viewFocusY, float viewFocusZ, float vecUpX, float vecUpY, float vecUpZ )
    {
        /* Make rotation matrix */

        /* Z vector */
        tmpVec3.setX( eyePositionX - viewFocusX );
        tmpVec3.setY( eyePositionY - viewFocusY );
        tmpVec3.setZ( eyePositionZ - viewFocusZ );
        
        tmpVec3.normalize();
        
        tmpVec2.set( vecUpX, vecUpY, vecUpZ );
        
        /* X vector = Y cross Z */
        tmpVec1.cross( tmpVec2, tmpVec3 );
        tmpVec2.cross( tmpVec3, tmpVec1 );
        
        tmpVec1.normalize();
        tmpVec2.normalize();
        
        matrix.setIdentity();
        
        matrix.m00( tmpVec1.getX() );
        matrix.m01( tmpVec1.getY() );
        matrix.m02( tmpVec1.getZ() );
        
        matrix.m10( tmpVec2.getX() );
        matrix.m11( tmpVec2.getY() );
        matrix.m12( tmpVec2.getZ() );
        
        matrix.m20( tmpVec3.getX() );
        matrix.m21( tmpVec3.getY() );
        matrix.m22( tmpVec3.getZ() );
        
        // matrix.negate();
        tmpVec4.set( eyePositionX, eyePositionY, eyePositionZ );
        tmpVec4.negate();
        
        tempMatrix.setIdentity();
        tempMatrix.setTranslation( tmpVec4 );
        matrix.mul( matrix, tempMatrix );
        
        matrix.invert();
        // this put it into opengl mode, was screwing things up
        // matrix.transpose();
        
        setChanged( true );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param eyePosition the center of the eye
     * @param viewFocus the point the view looks at
     * @param vecUp the vector pointing up
     */
    public final void lookAt( Tuple3f eyePosition, Tuple3f viewFocus, Tuple3f vecUp )
    {
        lookAt( eyePosition.getX(), eyePosition.getY(), eyePosition.getZ(), viewFocus.getX(), viewFocus.getY(), viewFocus.getZ(), vecUp.getX(), vecUp.getY(), vecUp.getZ() );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewDirectionX the direction the view looks along
     * @param viewDirectionY the direction the view looks along
     * @param viewDirectionZ the direction the view looks along
     * @param vecUpX the vector pointing up
     * @param vecUpY the vector pointing up
     * @param vecUpZ the vector pointing up
     */
    public final void lookAlong( float eyePositionX, float eyePositionY, float eyePositionZ, float viewDirectionX, float viewDirectionY, float viewDirectionZ, float vecUpX, float vecUpY, float vecUpZ )
    {
        lookAt( eyePositionX, eyePositionY, eyePositionZ, eyePositionX + viewDirectionX, eyePositionY + viewDirectionY, eyePositionZ + viewDirectionZ, vecUpX, vecUpY, vecUpZ );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.
     * 
     * @param eyePosition the center of the eye
     * @param viewDirection the direction the view looks along
     * @param vecUp the vector pointing up
     */
    public final void lookAlong( Tuple3f eyePosition, Tuple3f viewDirection, Tuple3f vecUp )
    {
        lookAlong( eyePosition.getX(), eyePosition.getY(), eyePosition.getZ(), viewDirection.getX(), viewDirection.getY(), viewDirection.getZ(), vecUp.getX(), vecUp.getY(), vecUp.getZ() );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.<br>
     * <br>
     * This method assumes Y-up.
     * 
     * @param eyePositionX the center of the eye
     * @param eyePositionY the center of the eye
     * @param eyePositionZ the center of the eye
     * @param viewDirectionX the direction the view looks along
     * @param viewDirectionY the direction the view looks along
     * @param viewDirectionZ the direction the view looks along
     */
    public final void lookAlong( float eyePositionX, float eyePositionY, float eyePositionZ, float viewDirectionX, float viewDirectionY, float viewDirectionZ )
    {
        lookAlong( eyePositionX, eyePositionY, eyePositionZ, viewDirectionX, viewDirectionY, viewDirectionZ, Vector3f.POSITIVE_Y_AXIS.getX(), Vector3f.POSITIVE_Y_AXIS.getY(), Vector3f.POSITIVE_Y_AXIS.getZ() );
    }
    
    /**
     * Helping function that specifies the position and orientation of a view
     * matrix.<br>
     * <br>
     * This method assumes Y-up.
     * 
     * @param eyePosition the center of the eye
     * @param viewDirection the direction the view looks along
     */
    public final void lookAlong( Tuple3f eyePosition, Tuple3f viewDirection )
    {
        lookAlong( eyePosition.getX(), eyePosition.getY(), eyePosition.getZ(), viewDirection.getX(), viewDirection.getY(), viewDirection.getZ(), Vector3f.POSITIVE_Y_AXIS.getX(), Vector3f.POSITIVE_Y_AXIS.getY(), Vector3f.POSITIVE_Y_AXIS.getZ() );
    }
    
    /**
     * Creates a perspective projection transform,
     * that mimics a standard, camera-based, view-model.
     * The frustum function-call establishes a view-model with the eye at the
     * apex of a symmetric view frustum. The arguments define the frustum and
     * its associated perspective projection:
     * (left, bottom, -near) and (right, top, -near) specify the point on the
     * near clipping plane that maps onto the lower-left and upper-right corners
     * of the window respectively, assuming the eye is located at (0, 0, 0).
     * 
     * @param left the vertical line on the left edge of the near clipping plane
     *        mapped to the left edge of the graphics window
     * @param right the vertical line on the right edge of the near clipping
     *        plane mapped to the right edge of the graphics window
     * @param bottom the horizontal line on the bottom edge of the near clipping
     *        plane mapped to the bottom edge of the graphics window
     * @param top the horizontal line on the top edge of the near
     * @param near the distance to the frustum's near clipping plane.
     *        This value must be positive, (the value -near is the location of
     *        the near clip plane).
     * @param far the distance to the frustum's far clipping plane.
     *        This value must be positive, and must be greater than near.
     */
    public final void frustum( float left, float right, float bottom, float top, float near, float far )
    {
        final float near2 = 2f * near;
        final float t1 = right - left;
        final float t2 = top - bottom;
        final float t3 = far - near;
        
        matrix.set( near2 / t1, 0f,         ( right + left ) / t1, 0f,
                    0f,         near2 / t2, ( top + bottom ) / t2, 0f,
                    0f,         0f,         -( far + near ) / t3,   -far * near2 / t3,
                    0f,         0f,         -1f,                   0f
                  );
    }
    
    /**
     * Creates a masa-style perspective projection transform,
     * that mimics a standard, camera-based, view-model.
     * The frustum function-call establishes a view-model with the eye at the
     * apex of a symmetric view frustum. The arguments define the frustum and
     * its associated perspective projection:
     * (left, bottom, -near) and (right, top, -near) specify the point on the
     * near clipping plane that maps onto the lower-left and upper-right corners
     * of the window respectively, assuming the eye is located at (0, 0, 0).
     * 
     * @param left the vertical line on the left edge of the near clipping plane
     *        mapped to the left edge of the graphics window
     * @param right the vertical line on the right edge of the near clipping
     *        plane mapped to the right edge of the graphics window
     * @param bottom the horizontal line on the bottom edge of the near clipping
     *        plane mapped to the bottom edge of the graphics window
     * @param top the horizontal line on the top edge of the near
     * @param zNear the distance to the frustum's near clipping plane.
     *        This value must be positive, (the value -near is the location of
     *        the near clip plane).
     * @param zFar the distance to the frustum's far clipping plane.
     *        This value must be positive, and must be greater than near.
     */
    public final void frustumMesa( float left, float right, float bottom, float top, float zNear, float zFar )
    {
        final float x = ( 2.0f * zNear ) / ( right - left );
        final float y = ( 2.0f * zNear ) / ( top - bottom );
        final float a = ( right + left ) / ( right - left );
        final float b = ( top + bottom ) / ( top - bottom );
        final float c = -( zFar + zNear ) / ( zFar - zNear );
        final float d = -( 2.0f * zFar * zNear ) / ( zFar - zNear );
        
        matrix.set( x,  0f, 0f, 0f,
                    0f, y,  0f, 0f,
                    a,  b,  c,  -1f,
                    0f, 0f, d,  0f
                  );
        
        setChanged( true );
    }
    
    /**
     * Creates a mesa-style perspective projection transform,
     * that mimics a standard, camera-based, view-model.
     * 
     * @param fovy specifies the field of view in the y direction, in radians
     * @param aspect specifies the aspect ratio and thus the field of view
     *        in the x direction. The aspect ratio is the ratio of x to y,
     *        or width to height.
     * @param zNear the distance to the frustum's near clipping plane.
     *        This value must be positive, (the value -zNear is the
     *        location of the near clip plane).
     * @param zFar the distance to the frustum's far clipping plane.
     */
    public final void perspectiveMesa( float fovy, float aspect, float zNear, float zFar )
    {
        final float ymax = zNear * FastMath.tan( fovy );
        final float ymin = -ymax;
        final float xmin = ymin * aspect;
        final float xmax = ymax * aspect;
        
        // don't call glFrustum() because of error semantics (covglu)
        frustumMesa( xmin, xmax, ymin, ymax, zNear, zFar );
    }
    
    /**
     * Creates a perspective projection transform,
     * that mimics a standard, camera-based, view-model.
     * 
     * @param fovy specifies the field of view in the y direction, in radians
     * @param aspect specifies the aspect ratio and thus the field of view
     *        in the x direction. The aspect ratio is the ratio of x to y,
     *        or width to height.
     * @param zNear the distance to the frustum's near clipping plane.
     *        This value must be positive, (the value -zNear is the
     *        location of the near clip plane).
     * @param zFar the distance to the frustum's far clipping plane.
     */
    public final void perspective( float fovy, float aspect, float zNear, float zFar )
    {
        final float f = 1f / FastMath.tan( fovy / 2f );
        
        final float a = ( zFar + zNear ) / ( zNear - zFar );
        final float b = ( 2f * zFar * zNear ) / ( zNear - zFar );
        
        matrix.set( f / aspect, 0f, 0f,  0f,
                    0f,         f,  0f,  0f,
                    0f,         0f, a,   b,
                    0f,         0f, -1f, 0f
                  );
        
        setChanged( true );
    }
    
    /**
     * Creates an orthographic projection transform,
     * that mimics a standard, camera-based, view-model.
     * 
     * @param left the vertical line on the left edge of the near clipping plane
     *        mapped to the left edge of the graphics window
     * @param right the vertical line on the right edge of the near clipping
     *        plane mapped to the right edge of the graphics window
     * @param bottom the horizontal line on the bottom edge of the near clipping
     *        plane mapped to the bottom edge of the graphics window
     * @param top the horizontal line on the top edge of the near clipping plane
     *        mapped to the top edge of the graphics window
     * @param near the distance to the frustum's near clipping plane
     *        (the value -near is the location of the near clip plane)
     * @param far the distance to the frustum's far clipping plane
     */
    public final void ortho( float left, float right, float bottom, float top, float near, float far )
    {
        final float t1 = right - left;
        final float t2 = top - bottom;
        final float t3 = far - near;
        
        matrix.set( 2f / t1, 0f,      0f,       -( right + left ) / t1,
                    0f,      2f / t2, 0f,       -( top + bottom ) / t2,
                    0f,      0f,      -2f / t3, -( far + near ) / t3,
                    0f,      0f,      0f,       1f
                  );
        
        setChanged( true );
    }
    
    // ////////////////////////////////////////////////////////////////
    // ///////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    // ////////////////////////////////////////////////////////////////
    
    public final void setStateNode( StateNode node )
    {
        this.stateNode = node;
    }
    
    public final StateNode getStateNode()
    {
        return ( stateNode );
    }
    
    public final Transform3D getCopy()
    {
        return ( new Transform3D( this ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( !( o instanceof Transform3D ) )
            return ( false );
        Transform3D ro = (Transform3D)o;
        return ( ro.matrix.equals( matrix ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( Transform3D t2 )
    {
        return ( ComparatorHelper.compare( this.matrix, t2.matrix ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return ( matrix.hashCode() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( super.toString() + "\n Matrix:\n " + getMatrix4f() );
    }
    
    /**
     * Constructs a new Transform3D object and sets it to the identity
     * transformation.
     * 
     * @param dummy
     * @param readOnlyMatrix
     */
    private Transform3D( Object dummy, Matrix4f readOnlyMatrix )
    {
        if ( readOnlyMatrix == null )
        {
            this.matrix = new Matrix4f();
            this.matrix.setIdentity();
            this.tempMatrix = new Matrix4f();
            this.tempMatrix.setIdentity();
        }
        else
        {
            if ( readOnlyMatrix.isReadOnly() )
                this.matrix = readOnlyMatrix;
            else
                this.matrix = Matrix4f.newReadOnly( readOnlyMatrix );
            
            this.tempMatrix = Matrix4f.newReadOnly( matrix );
        }
    }
    
    /**
     * Constructs a new Transform3D object and sets it to the identity
     * transformation.
     */
    public Transform3D()
    {
        this( null, null );
    }
    
    /**
     * Constructs a new Transform3D object and initializes it from the specified
     * transform.
     */
    public Transform3D( Transform3D t )
    {
        this();
        
        set( t );
    }
    
    public Transform3D( Matrix4f m )
    {
        this();
        
        set( m );
    }
    
    public Transform3D( Matrix3f m )
    {
        this();
        
        set( m );
    }
    
    public Transform3D( Tuple3f v )
    {
        this();
        
        set( v );
    }
    
    public Transform3D( float transX, float transY, float transZ )
    {
        this();
        
        set( transX, transY, transZ );
    }
}
