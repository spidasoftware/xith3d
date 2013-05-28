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

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.jagatoo.datatypes.Enableable;
import org.openmali.spatial.IndexContainer;
import org.openmali.spatial.TriangleContainer;
import org.openmali.spatial.VertexContainer;
import org.openmali.spatial.polygons.Triangle;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.physics.collision.collideable.BoxCollideable;
import org.xith3d.physics.collision.collideable.CapsuleCollideable;
import org.xith3d.physics.collision.collideable.CylinderCollideable;
import org.xith3d.physics.collision.collideable.PlaneCollideable;
import org.xith3d.physics.collision.collideable.RayCollideable;
import org.xith3d.physics.collision.collideable.SphereCollideable;
import org.xith3d.physics.collision.collideable.TriMeshCollideable;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;

/**
 * A Collision Engine. The base implementation is the JOODE one, but any engine
 * can be implemented... even your own one ! Put your imagination at work !<br>
 * <br>
 * A Collision Engine is in charge of : creating every CollideableGroup or
 * Collideable,<br>
 * <br>
 * Many thanks to the ODE Team for design ideas..
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class CollisionEngine implements Updatable, Enableable
{
    private final Vector<CollisionCheck> collisionCheckList = new Vector<CollisionCheck>();
    private CollisionListener defaultCollisionListener = null;
    
    private boolean enabled = true;
    
    /**
     * Error thrown when the user tries to create a Collideable which
     * type hasn't been implemented in the engine he uses.
     * 
     * @author Amos Wenger (aka BlueSky)
     */
    public class NotImplementedYetError extends Error
    {
        private static final long serialVersionUID = -5061974919339663639L;
        
        /**
         * Creates a new {@link NotImplementedYetError} error.
         * 
         * @param message
         */
        public NotImplementedYetError( String message )
        {
            super( message );
        }
    }
    
    /*
     * CREATION METHODS : GROUPS
     */
    
    /**
     * @return a list of all group types
     */
    public abstract List<CollideableGroupType> getGroupTypes();
    
    /**
     * Creates a new group of the "best" available type (by best we mean the best
     * compromise between speed and memory, the one which would be fine for most
     * situations).
     * 
     * To create a group of a specific type :
     * 
     * @see #newGroup(String) To know all implemented group types
     * @see #getGroupTypes()
     * 
     * @return The newly created Group
     */
    public abstract CollideableGroup newGroup();
    
    /**
     * Creates a new group of a specified type.
     * 
     * @param type
     * 
     * @see #getGroupTypes()
     * 
     * @return The newly created Group
     */
    public abstract CollideableGroup newGroup( String type );
    
    /*
     * CREATION METHODS : COLLIDEABLES
     * Notes on the implementation :
     * all the method aren't abstract, which means the Collision engine
     * has no obligation to implement anything at all. If the user call
     * a creation method and the engine hasn't implemented it, an Exception
     * will be thrown, which is the "implementation" of the creation methods
     * by default in the CollisionEngine class.
     * Here, and implementation is equal to an override.
     * If an engine implements a primitive which isn't supported by XPAL
     * yet, a new method + an abstract class in org.xith3d.physics.collision.collideable
     * should be added.
     */
    
    /**
     * Creates a new ray collideable
     * 
     * @param originX
     * @param originY
     * @param originZ
     * @param directionX
     * @param directionY
     * @param directionZ
     * 
     * @return a ray collideable
     */
    public RayCollideable newRay( float originX, float originY, float originZ, float directionX, float directionY, float directionZ )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Ray collision detection yet !" );
    }
    
    /**
     * Creates a new ray collideable
     * 
     * @param origin
     * @param direction
     * 
     * @return a ray collideable
     */
    public final RayCollideable newRay( Tuple3f origin, Tuple3f direction )
    {
        return ( newRay( origin.getX(), origin.getY(), origin.getZ(), direction.getX(), direction.getY(), direction.getZ() ) );
    }
    
    /**
     * Creates a new ray collideable
     * 
     * @param ray the template ray to create the RayCollideable from
     * 
     * @return a ray collideable
     */
    public final RayCollideable newRay( org.openmali.vecmath2.Ray3f ray )
    {
        final RayCollideable c = newRay( ray.getOrigin(), ray.getDirection() );
        
        return ( c );
    }
    
    /**
     * Creates a new ray collideable
     * 
     * @param vertexContainer the VertexContainer to take the vertex coordinates from
     * 
     * @return a ray collideable
     */
    public final RayCollideable newRay( VertexContainer vertexContainer )
    {
        org.openmali.vecmath2.Point3f coord = org.openmali.vecmath2.Point3f.fromPool();
        
        vertexContainer.getVertex( 0, coord );
        float x0 = coord.getX();
        float y0 = coord.getY();
        float z0 = coord.getZ();
        vertexContainer.getVertex( 1, coord );
        float x1 = coord.getX();
        float y1 = coord.getY();
        float z1 = coord.getZ();
        
        return ( newRay( x0, y0, z0, x1, y1, z1 ) );
    }
    
    /**
     * Creates a new ray collideable
     * 
     * @param shape the tempalte shape
     * 
     * @return a ray collideable
     */
    public final RayCollideable newRay( Shape3D shape )
    {
        return ( newRay( shape.getGeometry() ) );
    }
    
    /**
     * Creates a new sphere collideable
     * 
     * @param radius The radius of the sphere
     * 
     * @return a sphere collideable
     */
    public SphereCollideable newSphere( float radius )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Sphere collision detection yet !" );
    }
    
    /**
     * Creates a new sphere collideable
     * 
     * @param sphere the template sphere to create the SphereCollideable from
     * 
     * @return a sphere collideable
     */
    public final SphereCollideable newSphere( org.openmali.spatial.bodies.Sphere sphere )
    {
        final SphereCollideable c = newSphere( sphere.getRadius() );
        c.setPosition( sphere.getCenterX(), sphere.getCenterY(), sphere.getCenterZ() );
        
        return ( c );
    }
    
    /**
     * Creates a new sphere collideable
     * 
     * @param vertexContainer the VertexContainer to take the vertex coordinates from
     * 
     * @return a sphere collideable
     */
    public final SphereCollideable newSphere( VertexContainer vertexContainer )
    {
        org.openmali.spatial.bounds.BoundingSphere bs = org.openmali.spatial.bounds.BoundingSphere.newBoundingSphere( vertexContainer );
        
        return ( newSphere( bs ) );
    }
    
    /**
     * Creates a new sphere collideable
     * 
     * @param shape the tempalte shape
     * 
     * @return a sphere collideable
     */
    public final SphereCollideable newSphere( Shape3D shape )
    {
        return ( newSphere( shape.getGeometry() ) );
    }
    
    /**
     * Creates a new box collideable
     * 
     * @param size The size of the box
     * 
     * @return a box collideable
     */
    public final BoxCollideable newBox( Tuple3f size )
    {
        return ( newBox( size.getX(), size.getY(), size.getZ() ) );
    }
    
    /**
     * Creates a new box collideable
     * 
     * @param xSize The size of the box along the X axis
     * @param ySize The size of the box along the Y axis
     * @param zSize The size of the box along the Z axis
     * 
     * @return a box collideable
     */
    public BoxCollideable newBox( float xSize, float ySize, float zSize )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Box collision detection yet !" );
    }
    
    /**
     * Creates a new box collideable
     * 
     * @param box the template box to create the BoxCollideable from
     * 
     * @return a box collideable
     */
    public final BoxCollideable newBox( org.openmali.spatial.bodies.Box box )
    {
        final BoxCollideable c = newBox( box.getXSpan(), box.getYSpan(), box.getZSpan() );
        c.setPosition( box.getCenterX(), box.getCenterY(), box.getCenterZ() );
        
        return ( c );
    }
    
    /**
     * Creates a new box collideable
     * 
     * @param vertexContainer the VertexContainer to take the vertex coordinates from
     * 
     * @return a box collideable
     */
    public final BoxCollideable newBox( VertexContainer vertexContainer )
    {
        org.openmali.spatial.bounds.BoundingBox bb = org.openmali.spatial.bounds.BoundingBox.newAABB( vertexContainer );
        
        return ( newBox( bb ) );
    }
    
    /**
     * Creates a new box collideable
     * 
     * @param shape the template shape
     * 
     * @return a box collideable
     */
    public final BoxCollideable newBox( Shape3D shape )
    {
        return ( newBox( shape.getGeometry() ) );
    }
    
    /**
     * Creates a new box collideable
     * 
     * @param box the template box to create the BoxCollideable from
     * 
     * @return a box collideable
     */
    public final CollideableGroup newBoxOutline( org.openmali.spatial.bodies.Box box )
    {
        CollideableGroup boxOutline = newGroup( "Simple" );
        
        final float inverted = -1f;
        
        PlaneCollideable top = newPlane( new Vector3f( 0f, 1f * inverted, 0f ), box.getUpper() );
        boxOutline.addCollideable( top );
        
        PlaneCollideable bottom = newPlane( new Vector3f( 0f, -1f * inverted, 0f ), box.getLower() );
        boxOutline.addCollideable( bottom );
        
        PlaneCollideable front = newPlane( new Vector3f( 0f, 0f, -1f * inverted ), box.getLower() );
        boxOutline.addCollideable( front );
        
        PlaneCollideable back = newPlane( new Vector3f( 0f, 0f, +1f * inverted ), box.getUpper() );
        boxOutline.addCollideable( back );
        
        PlaneCollideable left = newPlane( new Vector3f( -1f * inverted, 0f, 0f ), box.getLower() );
        boxOutline.addCollideable( left );
        
        PlaneCollideable right = newPlane( new Vector3f( 1f * inverted, 0f, 0f ), box.getUpper() );
        boxOutline.addCollideable( right );
        
        return ( boxOutline );
    }
    
    /**
     * Creates a new "box outline" collideable
     * @param vertexContainer the VertexContainer to take the vertex coordinates from
     * @return the created CollideableGroup
     */
    public final CollideableGroup newBoxOutline( VertexContainer vertexContainer )
    {
        org.openmali.spatial.bounds.BoundingBox bb = org.openmali.spatial.bounds.BoundingBox.newAABB( vertexContainer );
        
        return ( newBoxOutline( bb ) );
    }
    
    /**
     * Creates a new "box outline" collideable
     * @param shape the template shape
     * @return the created CollideableGroup
     */
    public final CollideableGroup newBoxOutline( Shape3D shape )
    {
        return ( newBoxOutline( shape.getGeometry() ) );
    }
    
    /**
     * Creates a new capsule collideable
     * 
     * @param radius The radius of the capsule
     * @param length The length of the capsule
     * 
     * @return a capsule collideable
     */
    public CapsuleCollideable newCapsule( float radius, float length )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Capsule collision detection yet !" );
    }
    
    /**
     * Creates a new cylinder collideable
     * 
     * @param radius The radius of the capsule
     * @param length The length of the capsule
     * 
     * @return a cylinder collideable
     */
    public CylinderCollideable newCylinder( float radius, float length )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Cylinder collision detection yet !");
    }
    
    /**
     * Creates a new plane collideable
     * 
     * @param normal The normal of the plane to be created
     * @param point A point by which this plane is defined
     * 
     * @return a plane collideable
     */
    public PlaneCollideable newPlane( Vector3f normal, Point3f point )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Plane collision detection yet !" );
    }
    
    /**
     * Creates a new plane collideable
     * 
     * @param normal The normal of the plane to be created
     * @param d The d-parameter by which this plane is defined
     * 
     * @return a plane collideable
     */
    public PlaneCollideable newPlane( Vector3f normal, float d )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Plane collision detection yet !" );
    }
    
    /**
     * Creates a new plane collideable
     * 
     * @param a the x-component of the plane's normal
     * @param b the y-component of the plane's normal
     * @param c the z-component of the plane's normal
     * @param d The d-parameter by which this plane is defined
     * 
     * @return a plane collideable
     */
    public PlaneCollideable newPlane( float a, float b, float c, float d )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented Plane collision detection yet !" );
    }
    
    /**
     * Creates a new plane collideable
     * 
     * @param plane the Plane, the new PlaneCollideable is defained off.
     * 
     * @return a plane collideable
     */
    public final PlaneCollideable newPlane( org.openmali.spatial.bodies.Plane plane )
    {
        return ( newPlane( plane.getA(), plane.getB(), plane.getC(), plane.getD() ) );
    }
    
    /**
     * Creates a new triangle mesh collideable
     * 
     * @param vertexContainer A vertex container of a Triangle-based mesh (All Geometry types implements that interface)
     * @param indexContainer An index container of a Triangle-based mesh (Indexed Geometry types implements that interface)
     * 
     * If your geometry is not indexed, see the newTriMesh(VertexContainer) method.
     * 
     * @return a triangle mesh collideable
     */
    public TriMeshCollideable newTriMesh( VertexContainer vertexContainer, IndexContainer indexContainer )
    {
        final Point3f[] vertices = new Point3f[ vertexContainer.getVertexCount() ];
        final Point3f coord = new Point3f();
        for ( int i = 0; i < vertices.length; i++ )
        {
            vertexContainer.getVertex( i, coord );
            vertices[i] = new Point3f( coord );
        }
        
        final int[] indices;
        if ( indexContainer == null )
        {
            indices = new int[ vertexContainer.getVertexCount() ];
            for ( int i = 0; i < indices.length; i++ )
            {
                indices[i] = i;
            }
        }
        else
        {
            indices = new int[ indexContainer.getIndexCount() ];
            for ( int i = 0; i < indices.length; i++ )
            {
                indices[i] = indexContainer.getIndex( i );
            }
        }
        
        return ( newTriMesh( vertices, indices ) );
    }
    
    /**
     * Creates a new triangle mesh collideable
     * 
     * @param vertexContainer A vertex container of a Triangle-based mesh (All Geometry types implements that interface)
     * 
     * @return a triangle mesh collideable
     */
    public final TriMeshCollideable newTriMesh( VertexContainer vertexContainer )
    {
        return ( newTriMesh( vertexContainer, null ) );
    }
    
    /**
     * Creates a new triangle mesh collideable
     * 
     * @param triangleContainer A vertex container of a Triangle-based mesh (All Geometry types implements that interface)
     * 
     * @return a triangle mesh collideable
     */
    public TriMeshCollideable newTriMesh( TriangleContainer triangleContainer )
    {
        final VertexContainer vertexContainer = (VertexContainer)triangleContainer;
        
        final int nt = triangleContainer.getTriangleCount();
        final int nv = vertexContainer.getVertexCount();
        
        final Point3f[] coords = new Point3f[ nv ];
        
        final Point3f coord = new Point3f();
        for ( int i = 0; i < coords.length; i++ )
        {
            vertexContainer.getVertex( i, coord );
            coords[i] = new Point3f( coord );
        }
        
        final Triangle trian = new Triangle();
        final Vector3f edgeAC = new Vector3f();
        final Vector3f edgeAB = new Vector3f();
        final Vector3f faceNormal = new Vector3f();
        final Vector3f crossNormal = new Vector3f();
        
        final int[] indices = new int[ nt * 3 ];
        int i = 0;
        for ( int t = 0; t < nt; t++ )
        {
            triangleContainer.getTriangle( t, trian );
            
            // TODO: Check this!
            if ( false && trian.hasFeature( Geometry.NORMALS ) )
            {
                edgeAC.sub( trian.getVertexCoordA(), trian.getVertexCoordB() );
                edgeAB.sub( trian.getVertexCoordA(), trian.getVertexCoordC() );
                crossNormal.cross( edgeAC, edgeAB );
                crossNormal.normalize();
                trian.getFaceNormal( faceNormal );
                indices[i++] = trian.getVertexIndexA();
                if ( crossNormal.epsilonEquals( faceNormal, 0.001f ) )
                {
                    indices[i++] = trian.getVertexIndexB();
                    indices[i++] = trian.getVertexIndexC();
                }
                else
                {
                    indices[i++] = trian.getVertexIndexC();
                    indices[i++] = trian.getVertexIndexB();
                }
            }
            else
            {
                indices[i++] = trian.getVertexIndexC();
                indices[i++] = trian.getVertexIndexB();
                indices[i++] = trian.getVertexIndexA();
            }
        }
        
        return ( newTriMesh( coords, indices ) );
    }
    
    /**
     * Creates a new triangle mesh collideable
     * 
     * @param geometry A Xith3D geometry
     * 
     * @return a triangle mesh collideable
     */
    public final TriMeshCollideable newTriMesh( Geometry geometry )
    {
        if ( geometry instanceof TriangleContainer )
            return ( newTriMesh( (TriangleContainer)geometry ) );
        
        return ( newTriMesh( (VertexContainer)geometry ) );
    }
    
    /**
     * Creates a new triangle mesh collideable
     * 
     * @param shape A Xith3D Shape3D
     * 
     * @return a triangle mesh collideable
     */
    public final TriMeshCollideable newTriMesh( Shape3D shape )
    {
        return ( newTriMesh( shape.getGeometry() ) );
    }
    
    /**
     * Creates a new triangle mesh collideable
     * 
     * @param vertices The vertex data which is indexed
     * @param indices The vertices to chose from the vertex data
     * 
     * @return a triangle mesh collideable
     */
    public TriMeshCollideable newTriMesh( Tuple3f[] vertices, int[] indices )
    {
        throw new NotImplementedYetError( "The engine " + getVendorInformation().getName() +
                                          " hasn't implemented TriMesh collision detection yet !" );
    }
    
    /**
     * Creates a new triangle mesh collideable
     * 
     * @param vertices The vertices of the triangle mesh (three vertices = one triangle)
     * 
     * @return a triangle mesh collideable
     */
    public final TriMeshCollideable newTriMesh( Tuple3f[] vertices )
    {
        return ( newTriMesh( vertices, null ) );
    }
    
    /**
     * Creates a new CollideableGroup filled with TriMeshes derived from the
     * contained Shape3Ds.
     * 
     * @param group A Xith3D GroupNode
     * 
     * @return a collideable group
     */
    public final CollideableGroup newTriMeshGroup( GroupNode group )
    {
        CollideableGroup collGroup = newGroup( "Simple" );
        
        for ( int i = 0; i < group.numChildren(); i++ )
        {
            final Node child = group.getChild( i );
            
            if ( child instanceof GroupNode )
            {
                collGroup.addCollideable( newTriMeshGroup( (GroupNode)child ) );
            }
            else if ( child instanceof Shape3D )
            {
                collGroup.addCollideable( newTriMesh( (Shape3D)child ) );
            }
        }
        
        return ( collGroup );
    }
    
    /**
     * Creates a new CollideableGroup filled with Spheres derived from the
     * contained Shape3Ds.
     * 
     * @param group A Xith3D GroupNode
     * 
     * @return a collideable group
     */
    public final CollideableGroup newSphereGroup( GroupNode group )
    {
        CollideableGroup collGroup = newGroup( "Simple" );
        
        for ( int i = 0; i < group.numChildren(); i++ )
        {
            final Node child = group.getChild( i );
            
            if ( child instanceof GroupNode )
            {
                collGroup.addCollideable( newSphereGroup( (GroupNode)child ) );
            }
            else if ( child instanceof Shape3D )
            {
                collGroup.addCollideable( newSphere( (Shape3D)child ) );
            }
        }
        
        return ( collGroup );
    }
    
    /**
     * Creates a new CollideableGroup filled with Boxes derived from the
     * contained Shape3Ds.
     * 
     * @param group A Xith3D GroupNode
     * 
     * @return a collideable group
     */
    public final CollideableGroup newBoxGroup( GroupNode group )
    {
        CollideableGroup collGroup = newGroup( "Simple" );
        
        for ( int i = 0; i < group.numChildren(); i++ )
        {
            final Node child = group.getChild( i );
            
            if ( child instanceof GroupNode )
            {
                collGroup.addCollideable( newBoxGroup( (GroupNode)child ) );
            }
            else if ( child instanceof Shape3D )
            {
                collGroup.addCollideable( newSphere( (Shape3D)child ) );
            }
        }
        
        return ( collGroup );
    }
    
    
    /*
     * COLLISION DETECTION METHODS
     */
    
    /*
     * n x m, Collideable(Group) vs Collideable(Group)
     */
    
    /**
     * Checks two Collideables (or collideable groups) for collision (n x m collision check)
     * 
     * @param c1 A Collideable or CollideableGroup
     * @param c2 A Collideable or CollideableGroup
     * @param ignoreStatic
     * @param collisions the list to write collisions to
     * 
     * @return the number of detected collisions
     */
    public abstract int checkCollisions( Collideable c1, Collideable c2, boolean ignoreStatic, ArrayList<Collision> collisions );
    
    /**
     * Checks two Collideables (or collideable groups) for collision (n x m collision check)
     * 
     * @param c1 A Collideable or CollideableGroup
     * @param c2 A Collideable or CollideableGroup
     * @param ignoreStatic
     * 
     * @return An ArrayList containing all the collision, or an empty list if there's none.
     */
    public final ArrayList<Collision> checkCollisions( Collideable c1, Collideable c2, boolean ignoreStatic )
    {
        ArrayList<Collision> collisions = new ArrayList<Collision>();
        checkCollisions( c1, c2, ignoreStatic, collisions );
        
        return ( collisions );
    }
    
    /**
     * Checks two Collideables (or collideable groups) for collision (n x m collision check)
     * The listener is notified of any collision.
     * 
     * @param c1 A Collideable or CollideableGroup
     * @param c2 A Collideable or CollideableGroup
     * @param ignoreStatic
     * @param listener The CollisionListener to be notified, when a collision is detected.
     * 
     * @return the number of detected collisions
     */
    public abstract int checkCollisions( Collideable c1, Collideable c2, boolean ignoreStatic,
                                         CollisionListener listener );
    
    /*
     * n x n, ColllideableGroup vs itself
     */
    
    /**
     * Does an n x n collision check for all Collideables in the Group.
     * If there's any collision it's put in the returned array list
     * 
     * @param group
     * @param ignoreStatic
     * @param collisions the list to write collisions to
     * 
     * @return the number of detected collisions
     */
    public abstract int checkCollisions( CollideableGroup group, boolean ignoreStatic, ArrayList<Collision> collisions );
    
    /**
     * Does an n x n collision check for all Collideables in the Group.
     * If there's any collision it's put in the returned array list
     * 
     * @param group
     * @param ignoreStatic
     * @return a list of collisions
     */
    public final ArrayList<Collision> checkCollisions( CollideableGroup group, boolean ignoreStatic )
    {
        ArrayList<Collision> collisions = new ArrayList<Collision>();
        checkCollisions( group, ignoreStatic, collisions );
        
        return ( collisions );
    }
    
    /**
     * Does an n x n collision check for all Collideables in the Group. The
     * listener is notified of any collision.
     * 
     * @param group
     * @param ignoreStatic
     * @param listener
     * 
     * @return the number of detected collisions
     */
    public abstract int checkCollisions( CollideableGroup group, boolean ignoreStatic,
                                         CollisionListener listener );
    
    
    /*
     * INFOS METHODS
     */
    
    /**
     * @return the CollisionEngine's vendor information
     */
    public abstract CollisionEngineVendorInformation getVendorInformation();
    
    /*
     * Updatable implementation
     */
    
    /**
     * Adds a {@link CollisionCheck} to the list, that is handled each frame.
     * 
     * @param cc
     */
    public final void addCollisionCheck( CollisionCheck cc )
    {
        collisionCheckList.add( cc );
    }
    
    /**
     * Removes a {@link CollisionCheck} from the list, that is handled each frame.
     * 
     * @param cc
     */
    public final void removeCollisionCheck( CollisionCheck cc )
    {
        collisionCheckList.remove( cc );
    }
    
    /**
     * Sets the {@link CollisionListener}, that is being used, if a CollisionCheck
     * doesn't define an own {@link CollisionListener}.
     * 
     * @param cl
     */
    public final void setDefaultCollisionListener( CollisionListener cl )
    {
        this.defaultCollisionListener = cl;
    }
    
    /**
     * @return the {@link CollisionListener}, that is being used, if a CollisionCheck
     * doesn't define an own {@link CollisionListener}.
     */
    public final CollisionListener getDefaultCollisionListener()
    {
        return ( defaultCollisionListener );
    }
    
    /**
     * Sets this CollisionEngine enabled/disabled.<br>
     * If not enabled, the update() method will do nothing.
     * 
     * @param enabled
     */
    public final void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
    
    /**
     * @return if this CollisionEngine is enabled.<br>
     * If not enabled, the update() method will do nothing.
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( isEnabled() )
        {
            for ( int i = 0; i < collisionCheckList.size(); i++ )
            {
                final CollisionCheck cc = collisionCheckList.get( i );
                final CollisionListener cl = ( cc.getCollisionListener() != null ) ? cc.getCollisionListener() : defaultCollisionListener;
                
                if ( cl != null )
                {
                    this.checkCollisions( cc.getCollideable1(), cc.getCollideable2(), cc.getIgnoreStatic(), cl );
                }
                else
                {
                    System.err.println( "Warning: Neither the CollisionCheck defines a CollisionListener, nor the CollisionEngine has a default one." );
                }
            }
        }
    }
}
