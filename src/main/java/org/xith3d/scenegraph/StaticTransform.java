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

import java.util.List;

import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.util.MatrixUtils;
import org.xith3d.utility.geometry.GeometryUtils;

/**
 * This class staticly transforms Shapes/GeometryArrays/Points.<br>
 * If you want to move, rotate or scale a Shape once or only very rare,
 * you should use this class instead of Transform3D, since the transformation
 * is not done each frame.
 * 
 * @author Daniel Herring
 * @author Marvin Froehlich (aka Qudus)
 */
public final class StaticTransform
{
    /*
    private static final float PI = FastMath.PI;
    private static final float PI_HALF = FastMath.PI_HALF;
    private static final float TWO_PI = FastMath.TWO_PI;
    */

    /**
     * Inplace offsets each of the given vertices by a specified offset.
     * 
     * @param coords vertices array to be modified
     * @param offsetX Shift in x dimension
     * @param offsetY Shift in y dimension
     * @param offsetZ Shift in z dimension
     */
    public static void translate( Tuple3f[] coords, float offsetX, float offsetY, float offsetZ )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ].addX( offsetX );
            coords[ i ].addY( offsetY );
            coords[ i ].addZ( offsetZ );
        }
    }
    
    /**
     * Inplace offsets each of the given vertices by a specified offset.
     * 
     * @param coords vertices array to be modified
     * @param offset Shift in x,y,z dimension
     */
    public static void translate( Tuple3f[] coords, Tuple3f offset )
    {
        translate( coords, offset.getX(), offset.getY(), offset.getZ() );
    }
    
    /**
     * In-place addition of an offset to each point in src.
     * 
     * @param src Object to be changed
     * @param offsetX Shift in x dimension
     * @param offsetY Shift in y dimension
     * @param offsetZ Shift in z dimension
     */
    public static void translate( Geometry src, float offsetX, float offsetY, float offsetZ )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            coord.addX( offsetX );
            coord.addY( offsetY );
            coord.addZ( offsetZ );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
    }
    
    /**
     * In-place addition of an offset to each point in src.
     * 
     * @param src Object to be changed
     * @param offset Shift in each dimension
     */
    public static void translate( Geometry src, Tuple3f offset )
    {
        translate( src, offset.getX(), offset.getY(), offset.getZ() );
    }
    
    /**
     * In-place addition of an offset to each point in src.
     * 
     * @param shape Object to be changed
     * @param offsetX Shift in x dimension
     * @param offsetY Shift in y dimension
     * @param offsetZ Shift in z dimension
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S translate( S shape, float offsetX, float offsetY, float offsetZ )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        translate( src, offsetX, offsetY, offsetZ );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * In-place addition of an offset to each point in src.
     * 
     * @param shape Object to be changed
     * @param offset Shift in each dimension
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S translate( S shape, Tuple3f offset )
    {
        return ( translate( shape, offset.getX(), offset.getY(), offset.getZ() ) );
    }
    
    /**
     * In-place addition of an offset to each point in src.
     * 
     * @param src Object to be changed
     * @param offsetX Shift in x dimension
     * @param offsetY Shift in y dimension
     * @param offsetZ Shift in z dimension
     */
    public static void translate( GroupNode src, float offsetX, float offsetY, float offsetZ )
    {
        List< Shape3D > shapes = src.findAll( Shape3D.class );
        
        for ( int i = 0; i < shapes.size(); i++ )
        {
            translate( shapes.get( i ), offsetX, offsetY, offsetZ );
        }
    }
    
    /**
     * In-place addition of an offset to each point in src.
     * 
     * @param src Object to be changed
     * @param offset Shift in each dimension
     */
    public static void translate( GroupNode src, Tuple3f offset )
    {
        translate( src, offset.getX(), offset.getY(), offset.getZ() );
    }
    
    /**
     * Prismatically offsets each point.
     * i.e.
     * x <- x+a11 + a12*y + a13*z
     * y <- a21*x + y+a22 + a23*z
     * z <- a31*x + a32*y + z+a33
     * 
     * @param coords the Vertices-coordinates to be changed
     * @param offset Shift in each dimension
     */
    public static void translate( Tuple3f[] coords, Matrix3f offset )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ].set( coords[ i ].getX() + offset.m00() + coords[ i ].getY() * offset.m01() + coords[ i ].getZ() * offset.m02(),
                             coords[ i ].getX() * offset.m10() + coords[ i ].getY() + offset.m11() + coords[ i ].getZ() * offset.m12(),
                             coords[ i ].getX() * offset.m20() + coords[ i ].getY() * offset.m21() + coords[ i ].getZ() + offset.m22()
                             );
        }
    }
    
    /**
     * Prismatically offsets each point in src.
     * i.e.
     * x <- x+a11 + a12*y + a13*z
     * y <- a21*x + y+a22 + a23*z
     * z <- a31*x + a32*y + z+a33
     * 
     * @param src Object to be changed
     * @param offset Shift in each dimension
     */
    public static void translate( Geometry src, Matrix3f offset )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            coord.set( coord.getX() + offset.m00() + coord.getY() * offset.m01() + coord.getZ() * offset.m02(),
                       coord.getX() * offset.m10() + coord.getY() + offset.m11() + coord.getZ() * offset.m12(),
                       coord.getX() * offset.m20() + coord.getY() * offset.m21() + coord.getZ() + offset.m22()
                     );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
    }
    
    /**
     * Prismatically offsets each point in the shape.
     * i.e.
     * x <- x+a11 + a12*y + a13*z
     * y <- a21*x + y+a22 + a23*z
     * z <- a31*x + a32*y + z+a33
     * 
     * @param shape Object to be changed
     * @param offset Shift in each dimension
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S translate( S shape, Matrix3f offset )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        translate( src, offset );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * In-place multiplies the vertices by scale.
     * i.e. point.x <- scale.x*point.x for all vertices and dimensions
     * 
     * @param coords the Vertices-coordinates to be changed
     * @param scaleX Scaling in x dimension
     * @param scaleY Scaling in y dimension
     * @param scaleZ Scaling in z dimension
     */
    public static void scale( Tuple3f[] coords, float scaleX, float scaleY, float scaleZ )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ].mulX( scaleX );
            coords[ i ].mulY( scaleY );
            coords[ i ].mulZ( scaleZ );
        }
    }
    
    /**
     * In-place multiplies the vertices in src by scale.
     * i.e. point.x <- scale.x*point.x for all vertices and dimensions
     * 
     * @param src Object to be changed
     * @param scaleX Scaling in x dimension
     * @param scaleY Scaling in y dimension
     * @param scaleZ Scaling in z dimension
     */
    public static void scale( Geometry src, float scaleX, float scaleY, float scaleZ )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            coord.mulX( scaleX );
            coord.mulY( scaleY );
            coord.mulZ( scaleZ );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
    }
    
    /**
     * In-place multiplies the vertices in src by scale.
     * i.e. point.x <- scale.x*point.x for all vertices and dimensions
     * 
     * @param src Object to be changed
     * @param scale Scaling in each dimension
     */
    public static void scale( Geometry src, Tuple3f scale )
    {
        scale( src, scale.getX(), scale.getY(), scale.getZ() );
    }
    
    /**
     * In-place multiplies the vertices in src by scale.
     * i.e. point.x <- scale.x*point.x for all vertices and dimensions
     * 
     * @param shape Shape to be changed
     * @param scaleX Scaling in x dimension
     * @param scaleY Scaling in y dimension
     * @param scaleZ Scaling in z dimension
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S scale( S shape, float scaleX, float scaleY, float scaleZ )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        scale( src, scaleX, scaleY, scaleZ );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * In-place multiplies the vertices in src by scale.
     * i.e. point.x <- scale*point.x for all vertices and dimensions
     * 
     * @param src Object to be changed
     * @param scale Scaling factor
     */
    public static void scale( Geometry src, float scale )
    {
        scale( src, scale, scale, scale );
    }
    
    /**
     * In-place multiplies the vertices in src by scale.
     * i.e. point.x <- scale.x*point.x for all vertices and dimensions
     * 
     * @param shape Shape to be changed
     * @param scale Scaling in each dimension
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S scale( S shape, Tuple3f scale )
    {
        return ( scale( shape, scale.getX(), scale.getY(), scale.getZ() ) );
    }
    
    /**
     * In-place multiplies the vertices in src by scale.
     * i.e. point.x <- scale*point.x for all vertices and dimensions
     * 
     * @param shape Object to be changed
     * @param scale Scaling factor
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S scale( S shape, float scale )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        scale( src, scale );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * In-place multiplies each point by A.
     * i.e. point <- A*point for all vertices
     * This allows for arbitrary scaling, rotation, and reflection.
     * 
     * @param coords the Vertices-coordinates to be changed
     * @param m Scaling matrix
     */
    public static void transform( Tuple3f[] coords, Matrix3f m )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ].set( m.m00() * coords[ i ].getX() + m.m01() * coords[ i ].getY() + m.m02() * coords[ i ].getZ(),
                             m.m10() * coords[ i ].getX() + m.m11() * coords[ i ].getY() + m.m12() * coords[ i ].getZ(),
                             m.m20() * coords[ i ].getX() + m.m21() * coords[ i ].getY() + m.m22() * coords[ i ].getZ()
                           );
        }
    }
    
    /**
     * In-place multiplies each point in src by A.
     * i.e. point <- A*point for all vertices
     * This allows for arbitrary scaling, rotation, and reflection.
     * 
     * @param src Shape to be changed
     * @param m Scaling matrix
     */
    public static void transform( Geometry src, Matrix3f m )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            coord.set( m.m00() * coord.getX() + m.m01() * coord.getY() + m.m02() * coord.getZ(),
                       m.m10() * coord.getX() + m.m11() * coord.getY() + m.m12() * coord.getZ(),
                       m.m20() * coord.getX() + m.m21() * coord.getY() + m.m22() * coord.getZ()
                     );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
    }
    
    /**
     * In-place multiplies each point in src by A.
     * i.e. point <- A*point for all vertices
     * This allows for arbitrary scaling, rotation, and reflection.
     * 
     * @param shape Shape to be changed
     * @param m Scaling matrix
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S transform( S shape, Matrix3f m )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        transform( src, m );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * In-place multiplies each point by A.
     * i.e. point <- A*point for all vertices
     * This allows for arbitrary scaling, rotation, and reflection.
     * 
     * @param coords the Vertices-coordinates to be changed
     * @param m transformation matrix
     */
    public static void transform( Point3f[] coords, Matrix4f m )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            m.transform( coords[ i ] );
        }
    }
    
    /**
     * In-place multiplies each point in src by A.
     * i.e. point <- A*point for all vertices
     * This allows for arbitrary scaling, rotation, and reflection.
     * 
     * @param src Shape to be changed
     * @param m transformation matrix
     */
    public static void transform( Geometry src, Matrix4f m )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            m.transform( coord );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
    }
    
    /**
     * In-place multiplies each point in src by A.
     * i.e. point <- A*point for all vertices
     * This allows for arbitrary scaling, rotation, and reflection.
     * 
     * @param shape Shape to be changed
     * @param m transformation matrix
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S transform( S shape, Matrix4f m )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        transform( src, m );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * Rotates the geometry by angle theta about the given axis.
     *
     * @param coords the vertices-coordinates to change
     * @param axisX Rotation axis x-component
     * @param axisY Rotation axis x-component
     * @param axisZ Rotation axis x-component
     * @param theta Rotation angle
     */
    public static void rotate( Tuple3f[] coords, float axisX, float axisY, float axisZ, float theta )
    {
        transform( coords, MatrixUtils.getRotationMatrix( axisX, axisY, axisZ, theta ) );
    }
    
    /**
     * Rotates the geometry by angle theta about the x-axis.
     *
     * @param coords the vertices-coordinates to change
     * @param theta Rotation angle
     */
    public static void rotateX( Tuple3f[] coords, float theta )
    {
        rotate( coords, 1.0f, 0.0f, 0.0f, theta );
    }
    
    /**
     * Rotates the geometry by angle theta about the y-axis.
     *
     * @param coords the vertices-coordinates to change
     * @param theta Rotation angle
     */
    public static void rotateY( Tuple3f[] coords, float theta )
    {
        rotate( coords, 0.0f, 1.0f, 0.0f, theta );
    }
    
    /**
     * Rotates the geometry by angle theta about the z-axis.
     *
     * @param coords the vertices-coordinates to change
     * @param theta Rotation angle
     */
    public static void rotateZ( Tuple3f[] coords, float theta )
    {
        rotate( coords, 0.0f, 0.0f, 1.0f, theta );
    }
    
    /**
     * Rotates the geometry by angle theta about the given axis.
     *
     * @param src Shape to be changed
     * @param axisX Rotation axis x-component
     * @param axisY Rotation axis x-component
     * @param axisZ Rotation axis x-component
     * @param theta Rotation angle
     */
    public static void rotate( Geometry src, float axisX, float axisY, float axisZ, float theta )
    {
        Matrix3f m = MatrixUtils.getRotationMatrix( axisX, axisY, axisZ, theta );
        
        transform( src, m );
        
        // If there are normals, then they also need to be rotated!
        if ( src.hasNormals() )
        {
            final int n = src.getVertexCount();
            
            Vector3f normal = Vector3f.fromPool();
            
            for ( int i = 0; i < n; i++ )
            {
                src.getNormal( i, normal );
                
                normal.set( m.m00() * normal.getX() + m.m01() * normal.getY() + m.m02() * normal.getZ(),
                            m.m10() * normal.getX() + m.m11() * normal.getY() + m.m12() * normal.getZ(),
                            m.m20() * normal.getX() + m.m21() * normal.getY() + m.m22() * normal.getZ()
                          );
                
                src.setNormal( i, normal );
            }
            
            
            
            final Vector3f[] normals = GeometryUtils.getNormals( src );
            
            transform( normals, m );
            
            src.setNormals( 0, normals );
        }
    }
    
    /**
     * Rotates the geometry by angle theta about the x-axis.
     *
     * @param src Shape to be changed
     * @param theta Rotation angle
     */
    public static void rotateX( Geometry src, float theta )
    {
        rotate( src, 1.0f, 0.0f, 0.0f, theta );
    }
    
    /**
     * Rotates the geometry by angle theta about the y-axis.
     *
     * @param src Shape to be changed
     * @param theta Rotation angle
     */
    public static void rotateY( Geometry src, float theta )
    {
        rotate( src, 0.0f, 1.0f, 0.0f, theta );
    }
    
    /**
     * Rotates the geometry by angle theta about the z-axis.
     *
     * @param src Shape to be changed
     * @param theta Rotation angle
     */
    public static void rotateZ( Geometry src, float theta )
    {
        rotate( src, 0.0f, 0.0f, 1.0f, theta );
    }
    
    /**
     * Rotates the geometry by angle theta about the given axis.
     *
     * @param src Shape to be changed
     * @param axis Rotation axis
     * @param theta Rotation angle
     */
    public static void rotate( Geometry src, Tuple3f axis, float theta )
    {
        rotate( src, axis.getX(), axis.getY(), axis.getZ(), theta );
    }
    
    /**
     * Rotates the geometry by angle theta about the given axis.
     * 
     * @param shape Shape to be changed
     * @param axisX Rotation axis x-component
     * @param axisY Rotation axis x-component
     * @param axisZ Rotation axis x-component
     * @param theta Rotation angle
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S rotate( S shape, float axisX, float axisY, float axisZ, float theta )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        rotate( src, axisX, axisY, axisZ, theta );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * Rotates the geometry by angle theta about the x-axis.
     * 
     * @param shape Shape to be changed
     * @param theta Rotation angle
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S rotateX( S shape, float theta )
    {
        return ( rotate( shape, 1.0f, 0.0f, 0.0f, theta ) );
    }
    
    /**
     * Rotates the geometry by angle theta about the y-axis.
     * 
     * @param shape Shape to be changed
     * @param theta Rotation angle
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S rotateY( S shape, float theta )
    {
        return ( rotate( shape, 0.0f, 1.0f, 0.0f, theta ) );
    }
    
    /**
     * Rotates the geometry by angle theta about the z-axis.
     * 
     * @param shape Shape to be changed
     * @param theta Rotation angle
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S rotateZ( S shape, float theta )
    {
        return ( rotate( shape, 0.0f, 0.0f, 1.0f, theta ) );
    }
    
    /**
     * Rotates the geometry by angle theta about the given axis.
     * 
     * @param shape Shape to be changed
     * @param axis Rotation axis
     * @param theta Rotation angle
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S rotate( S shape, Tuple3f axis, float theta )
    {
        return ( rotate( shape, axis.getX(), axis.getY(), axis.getZ(), theta ) );
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param coords the vertices-coordinates to change
     */
    public static void mirrorXY( Tuple3f[] coords )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ].mulZ( -1f );
        }
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param coords the vertices-coordinates to change
     */
    public static void mirrorYZ( Tuple3f[] coords )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ].mulX( -1f );
        }
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param coords the vertices-coordinates to change
     */
    public static void mirrorZX( Tuple3f[] coords )
    {
        for ( int i = 0; i < coords.length; i++ )
        {
            coords[ i ].mulY( -1f );
        }
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param src Object to be changed
     */
    public static void mirrorXY( Geometry src )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            coord.mulZ( -1f );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
        
        // If there are normals, then they also need to be rotated!
        if ( src.hasNormals() )
        {
            Vector3f normal = Vector3f.fromPool();
            
            for ( int i = 0; i < n; i++ )
            {
                src.getNormal( i, normal );
                
                normal.mulZ( -1f );
                
                src.setNormal( i, normal );
            }
            
            Vector3f.toPool( normal );
        }
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param src Object to be changed
     */
    public static void mirrorYZ( Geometry src )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            coord.mulX( -1f );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
        
        // If there are normals, then they also need to be rotated!
        if ( src.hasNormals() )
        {
            Vector3f normal = Vector3f.fromPool();
            
            for ( int i = 0; i < n; i++ )
            {
                src.getNormal( i, normal );
                
                normal.mulX( -1f );
                
                src.setNormal( i, normal );
            }
            
            Vector3f.toPool( normal );
        }
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param src Object to be changed
     */
    public static void mirrorZX( Geometry src )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            coord.mulY( -1f );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
        
        // If there are normals, then they also need to be rotated!
        if ( src.hasNormals() )
        {
            Vector3f normal = Vector3f.fromPool();
            
            for ( int i = 0; i < n; i++ )
            {
                src.getNormal( i, normal );
                
                normal.mulY( -1f );
                
                src.setNormal( i, normal );
            }
            
            Vector3f.toPool( normal );
        }
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param shape Shape to be changed
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S mirrorXY( S shape )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        mirrorXY( src );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param shape Shape to be changed
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S mirrorYZ( S shape )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        mirrorYZ( src );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * In-place mirrors the vertices by the xy-plane.
     * Therefore the z-coordinate is inverted.
     * 
     * @param shape Shape to be changed
     * 
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S mirrorZX( S shape )
    {
        Geometry src = shape.getGeometry();
        if ( src == null )
            return ( null );
        
        mirrorZX( src );
        
        shape.setGeometry( src );
        
        return ( shape );
    }
    
    /**
     * Applies a general quadratic transform.
     * p <- p + A + B*p + p*C*p
     * 
     * i.e.
     * x <- x + a0 + (b00*x + b01*y + b02*z) + x*(c00*x + c01*y + c02*z)
     * y <- y + a1 + (b10*x + b11*y + b12*z) + y*(c10*x + c11*y + c12*z)
     * z <- z + a2 + (b20*x + b21*y + b22*z) + z*(c20*x + c21*y + c22*z)
     * 
     * @param src Shape to be changed
     * @param A Constant offset
     * @param B Linear terms
     * @param C Quadratic terms
     */
    public static void quadratic( Geometry src, Tuple3f A, Matrix3f B, Matrix3f C )
    {
        final int n = src.getVertexCount();
        
        Point3f coord = Point3f.fromPool();
        
        float x, y, z;
        for ( int i = 0; i < n; i++ )
        {
            src.getCoordinate( i, coord );
            
            x = coord.getX();
            y = coord.getY();
            z = coord.getZ();
            coord.set( x + A.getX() + ( B.m00() * x + B.m01() * y + B.m02() * z ) + x * ( C.m00() * x + C.m01() * y + C.m02() * z ),
                       y + A.getY() + ( B.m10() * x + B.m11() * y + B.m12() * z ) + x * ( C.m10() * x + C.m11() * y + C.m12() * z ),
                       z + A.getZ() + ( B.m20() * x + B.m21() * y + B.m22() * z ) + x * ( C.m20() * x + C.m21() * y + C.m22() * z )
                     );
            
            src.setCoordinate( i, coord );
        }
        
        Point3f.toPool( coord );
        
        src.setBoundsDirty();
    }
    
    /**
     * Applies a general quadratic transform.
     * p <- p + A + B*p + p*C*p
     * 
     * i.e.
     * x <- x + a0 + (b00*x + b01*y + b02*z) + x*(c00*x + c01*y + c02*z)
     * y <- y + a1 + (b10*x + b11*y + b12*z) + y*(c10*x + c11*y + c12*z)
     * z <- z + a2 + (b20*x + b21*y + b22*z) + z*(c20*x + c21*y + c22*z)
     * 
     * @param shape Shape to be changed
     * @param A Constant offset
     * @param B Linear terms
     * @param C Quadratic terms
     * @return modified shape if successful; null if not
     */
    public static <S extends Shape3D> S quadratic( S shape, Tuple3f A, Matrix3f B, Matrix3f C )
    {
        Geometry geom = shape.getGeometry();
        if ( geom == null )
            return ( null );
        
        quadratic( geom, A, B, C );
        
        shape.setGeometry( geom );
        
        return ( shape );
    }
    
    private StaticTransform()
    {
    }
}
