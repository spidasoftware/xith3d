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
package org.xith3d.loaders.models.conversion;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.loaders.models.bsp.BSPVisibilityUpdater;
import org.jagatoo.loaders.textures.AbstractTexture;
import org.openmali.spatial.PlaneIndicator;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.BoundsType;
import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.util.MatrixUtils;
import org.xith3d.scenegraph.AmbientLight;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.BSPTreeGroup;
import org.xith3d.scenegraph.BoundsTypeHint;
import org.xith3d.scenegraph.DummyLeaf;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.OcTreeGroup;
import org.xith3d.scenegraph.PointLight;
import org.xith3d.scenegraph.QuadTreeGroup;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.SpotLight;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.SkyBox;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class XithNodeFactory implements org.jagatoo.loaders.models._util.NodeFactory
{
    private static final BoundsTypeHint toBTH( BoundsType boundsType )
    {
        if ( boundsType == null )
            return ( BoundsTypeHint.NONE );
        
        if ( boundsType == BoundsType.SPHERE )
            return ( BoundsTypeHint.SPHERE );
        
        if ( boundsType == BoundsType.AABB )
            return ( BoundsTypeHint.AABB );
        
        //if ( boundsType == BoundsType.POLYTOPE )
        //    return ( BoundsTypeHint.POLYTOPE );
        
        return ( BoundsTypeHint.SPHERE );
    }
    
    public final DummyLeaf createDummyNode()
    {
        return ( new DummyLeaf() );
    }
    
    public final Group createSimpleGroup( String name, BoundsType boundsType )
    {
        BoundsTypeHint oldBTH = Group.getBoundsTypeHint();
        Group.setBoundsTypeHint( toBTH( boundsType ) );
        
        Group group = new Group();
        group.setName( name );
        
        Group.setBoundsTypeHint( oldBTH );
        
        return ( group );
    }
    
    public final QuadTreeGroup createQuadTreeGroup( String name, float centerX, float centerY, float centerZ, PlaneIndicator plane, float width, float depth, float height )
    {
        QuadTreeGroup group = new QuadTreeGroup( centerX, centerY, centerZ, plane, width, depth, height, true );
        group.setName( name );
        
        return ( group );
    }
    
    public final OcTreeGroup createOcTreeGroup( String name, float centerX, float centerY, float centerZ, float sizeX, float sizeY, float sizeZ )
    {
        OcTreeGroup group = new OcTreeGroup( centerX, centerY, centerZ, sizeX, sizeY, sizeZ, true );
        group.setName( name );
        
        return ( group );
    }
    
    public final BSPTreeGroup createBSPTreeGroup( String name, BSPVisibilityUpdater visUpdater, BoundsType boundsType )
    {
        BoundsTypeHint oldBTH = Group.getBoundsTypeHint();
        BSPTreeGroup.setBoundsTypeHint( toBTH( boundsType ) );
        
        BSPTreeGroup group = new BSPTreeGroup( visUpdater );
        group.setName( name );
        
        BSPTreeGroup.setBoundsTypeHint( oldBTH );
        
        return ( group );
    }
    
    public final void setBSPGroupVisibilityUpdater( NamedObject bspGroupObj, BSPVisibilityUpdater visUpdater )
    {
        ( (BSPTreeGroup)bspGroupObj ).setBSPVisibilityUpdater( visUpdater );
    }
    
    public final TransformGroup createTransformGroup( String name, BoundsType boundsType )
    {
        BoundsTypeHint oldBTH = Group.getBoundsTypeHint();
        TransformGroup.setBoundsTypeHint( toBTH( boundsType ) );
        
        TransformGroup group = new TransformGroup();
        group.setName( name );
        
        TransformGroup.setBoundsTypeHint( oldBTH );
        
        return ( group );
    }
    
    public final TransformGroup createTransformGroup( String name, Matrix4f transform, BoundsType boundsType )
    {
        BoundsTypeHint oldBTH = Group.getBoundsTypeHint();
        TransformGroup.setBoundsTypeHint( toBTH( boundsType ) );
        
        TransformGroup group = new TransformGroup( transform );
        group.setName( name );
        
        TransformGroup.setBoundsTypeHint( oldBTH );
        
        return ( group );
    }
    
    public final void setTransformGroupTransform( NamedObject tgObj, Matrix4f transform )
    {
        TransformGroup tg = (TransformGroup)tgObj;
        
        tg.getTransform().set( transform );
        tg.updateTransform();
    }
    
    public final void setTransformGroupRotation( NamedObject tgObj, Matrix3f rotation )
    {
        TransformGroup tg = (TransformGroup)tgObj;
        
        Matrix4f m4 = tg.getTransform().getMatrix4f();
        
        m4.m00( rotation.m00() );
        m4.m01( rotation.m01() );
        m4.m02( rotation.m02() );
        m4.m10( rotation.m10() );
        m4.m11( rotation.m11() );
        m4.m12( rotation.m12() );
        m4.m20( rotation.m20() );
        m4.m21( rotation.m21() );
        m4.m22( rotation.m22() );
        
        tg.updateTransform();
    }
    
    public final void setTransformGroupRotation( NamedObject tgObj, float quatA, float quatB, float quatC, float quatD )
    {
        Quaternion4f quat = Quaternion4f.fromPool();
        quat.set( quatA, quatB, quatC, quatD );
        
        Matrix3f mat = Matrix3f.fromPool();
        mat.set( quat );
        
        setTransformGroupRotation( tgObj, mat );
        
        Matrix3f.toPool( mat );
        Quaternion4f.toPool( quat );
    }
    
    public final void setTransformGroupRotation( NamedObject tgObj, float rx, float ry, float rz )
    {
        Matrix3f mat = Matrix3f.fromPool();
        MatrixUtils.eulerToMatrix3f( rx, ry, rz, mat );
        
        setTransformGroupRotation( tgObj, mat );
        
        Matrix3f.toPool( mat );
    }
    
    public final void setTransformGroupTranslation( NamedObject tgObj, float tx, float ty, float tz )
    {
        TransformGroup tg = (TransformGroup)tgObj;
        
        tg.getTransform().setTranslation( tx, ty, tz );
        
        tg.updateTransform();
        
    }
    
    public final void setTransformGroupScale( NamedObject tgObj, float sx, float sy, float sz )
    {
        TransformGroup tg = (TransformGroup)tgObj;
        
        Matrix4f scaleMat = Matrix4f.fromPool();
        
        scaleMat.m00( sx );
        scaleMat.m11( sy );
        scaleMat.m22( sz );
        
        tg.getTransform().getMatrix4f().mul( scaleMat );
        
        Matrix4f.toPool( scaleMat );
        
        tg.updateTransform();
    }
    
    
    
    public final Shape3D createShape( String name, NamedObject geometry, NamedObject appearance, BoundsType boundsType )
    {
        BoundsTypeHint oldBTH = Group.getBoundsTypeHint();
        Shape3D.setBoundsTypeHint( toBTH( boundsType ) );
        
        Shape3D shape = new Shape3D( (Geometry)geometry, (Appearance)appearance );
        shape.setName( name );
        
        Shape3D.setBoundsTypeHint( oldBTH );
        
        shape.updateBounds( false );
        
        return ( shape );
    }
    
    
    
    public final void applyGeometryToShape( NamedObject geometry, NamedObject shape )
    {
        ( (Shape3D)shape ).setGeometry( (Geometry)geometry );
    }
    
    public final void applyAppearanceToShape( NamedObject appearance, NamedObject shape )
    {
        ( (Shape3D)shape ).setAppearance( (Appearance)appearance );
    }
    
    
    
    public final Geometry getGeometryFromShape( NamedObject shape )
    {
        return ( ( (Shape3D)shape ).getGeometry() );
    }
    
    public final Appearance getAppearanceFromShape( NamedObject shape )
    {
        return ( ( (Shape3D)shape ).getAppearance() );
    }
    
    
    
    public final AmbientLight createAmbientLightNode( String name )
    {
        AmbientLight light = new AmbientLight();
        light.setName( name );
        
        return ( light );
    }
    
    public final void setAmbientLightColor( NamedObject ambientLight, float r, float g, float b )
    {
        ( (AmbientLight)ambientLight ).setColor( r, g, b );
    }
    
    
    
    public final PointLight createPointLightNode( String name )
    {
        PointLight light = new PointLight();
        light.setName( name );
        
        return ( light );
    }
    
    public final void setPointLightLocation( NamedObject pointLight, float x, float y, float z )
    {
        ( (PointLight)pointLight ).setLocation( x, y, z );
    }
    
    public final void setPointLightColor( NamedObject pointLight, float r, float g, float b )
    {
        ( (PointLight)pointLight ).setColor( r, g, b );
    }
    
    public final void setPointLightAttenuation( NamedObject pointLight, float attConstant, float attLinear, float attQuadratic )
    {
        ( (PointLight)pointLight ).setAttenuation( attConstant, attLinear, attQuadratic );
    }
    
    
    
    public final SpotLight createSpotLightNode( String name )
    {
        SpotLight light = new SpotLight();
        light.setName( name );
        
        return ( light );
    }
    
    public final void setSpotLightLocation( NamedObject spotLight, float x, float y, float z )
    {
        ( (SpotLight)spotLight ).setLocation( x, y, z );
    }
    
    public final void setSpotLightColor( NamedObject spotLight, float r, float g, float b )
    {
        ( (SpotLight)spotLight ).setColor( r, g, b );
    }
    
    public final void setSpotLightAngle( NamedObject spotLight, float angle )
    {
        ( (SpotLight)spotLight ).setSpreadAngle( angle );
    }
    
    public final void setSpotLightAttenuation( NamedObject spotLight, float attConstant, float attLinear, float attQuadratic )
    {
        ( (SpotLight)spotLight ).setAttenuation( attConstant, attLinear, attQuadratic );
    }
    
    
    
    public final void setLightRadius( NamedObject light, float radius )
    {
        ( (Light)light ).setInfluencingBounds( new BoundingSphere( 0f, 0f, 0f, radius ) );
    }
    
    public final void setLightEnbaled( NamedObject light, boolean enabled )
    {
        ( (Light)light ).setEnabled( enabled );
    }
    
    
    
    public final void addNodeToGroup( NamedObject node, NamedObject group )
    {
        if ( !( node instanceof Light ) )
        {
            ( (GroupNode)group ).addChild( (Node)node );
        }
    }
    
    
    
    public final Object createSkyBox( AbstractTexture texFront, AbstractTexture texRight, AbstractTexture texBack, AbstractTexture texLeft, AbstractTexture texTop, AbstractTexture texBottom )
    {
        return ( new SkyBox( (Texture2D)texFront, (Texture2D)texRight, (Texture2D)texBack, (Texture2D)texLeft, (Texture2D)texTop, (Texture2D)texBottom ) );
        //return ( new SkyBox( "stone.jpg", "stone.jpg", "stone.jpg", "stone.jpg", "stone.jpg", "stone.jpg" ) );
    }
    
    
    
    public final NamedObject transformShapeOrGeometry( NamedObject shapeOrGeom, Matrix4f transform )
    {
        if ( shapeOrGeom instanceof Shape3D )
        {
            Shape3D shape = (Shape3D)shapeOrGeom;
            StaticTransform.transform( shape, transform );
        }
        else if ( shapeOrGeom instanceof Geometry )
        {
            Geometry geom = (Geometry)shapeOrGeom;
            StaticTransform.transform( geom, transform );
        }
        
        return ( shapeOrGeom );
    }
    
    public final NamedObject transformShapeOrGeometry( NamedObject shapeOrGeom, Vector3f translation, Matrix3f rotation, Tuple3f scale )
    {
        Matrix4f transform = Matrix4f.fromPool();
        Matrix4f tmp = Matrix4f.fromPool();
        
        transform.set( translation );
        
        tmp.set( rotation );
        transform.mul( tmp );
        
        tmp.setIdentity();
        tmp.m00( scale.getX() );
        tmp.m11( scale.getY() );
        tmp.m22( scale.getZ() );
        transform.mul( tmp );
        
        Matrix4f.toPool( tmp );
        
        NamedObject result = transformShapeOrGeometry( shapeOrGeom, transform );
        
        Matrix4f.toPool( transform );
        
        return ( result );
    }
    
    public final NamedObject transformShapeOrGeometry( NamedObject shapeOrGeom, Vector3f translation, Quaternion4f rotation, Tuple3f scale )
    {
        Matrix4f transform = Matrix4f.fromPool();
        Matrix4f tmp = Matrix4f.fromPool();
        
        transform.set( translation );
        
        tmp.set( rotation );
        transform.mul( tmp );
        
        tmp.setIdentity();
        tmp.m00( scale.getX() );
        tmp.m11( scale.getY() );
        tmp.m22( scale.getZ() );
        transform.mul( tmp );
        
        Matrix4f.toPool( tmp );
        
        NamedObject result = transformShapeOrGeometry( shapeOrGeom, transform );
        
        Matrix4f.toPool( transform );
        
        return ( result );
    }
    
    public final NamedObject transformShapeOrGeometry( NamedObject shapeOrGeom, Vector3f translation, AxisAngle3f rotation, Tuple3f scale )
    {
        Matrix4f transform = Matrix4f.fromPool();
        Matrix4f tmp = Matrix4f.fromPool();
        
        transform.set( translation );
        
        tmp.set( rotation );
        transform.mul( tmp );
        
        tmp.setIdentity();
        tmp.m00( scale.getX() );
        tmp.m11( scale.getY() );
        tmp.m22( scale.getZ() );
        transform.mul( tmp );
        
        Matrix4f.toPool( tmp );
        
        NamedObject result = transformShapeOrGeometry( shapeOrGeom, transform );
        
        Matrix4f.toPool( transform );
        
        return ( result );
    }
    
    public final NamedObject translateShapeOrGeometry( NamedObject shapeOrGeom, Vector3f translation )
    {
        if ( shapeOrGeom instanceof Shape3D )
        {
            Shape3D shape = (Shape3D)shapeOrGeom;
            StaticTransform.translate( shape, translation );
        }
        else if ( shapeOrGeom instanceof Geometry )
        {
            Geometry geom = (Geometry)shapeOrGeom;
            StaticTransform.translate( geom, translation );
        }
        
        return ( shapeOrGeom );
    }
    
    public final NamedObject translateShapeOrGeometry( NamedObject shapeOrGeom, float translationX, float translationY, float translationZ )
    {
        if ( shapeOrGeom instanceof Shape3D )
        {
            Shape3D shape = (Shape3D)shapeOrGeom;
            StaticTransform.translate( shape, translationX, translationY, translationZ );
        }
        else if ( shapeOrGeom instanceof Geometry )
        {
            Geometry geom = (Geometry)shapeOrGeom;
            StaticTransform.translate( geom, translationX, translationY, translationZ );
        }
        
        return ( shapeOrGeom );
    }
    
    public final NamedObject rotateShapeOrGeometry( NamedObject shapeOrGeom, Matrix3f rotation )
    {
        if ( shapeOrGeom instanceof Shape3D )
        {
            Shape3D shape = (Shape3D)shapeOrGeom;
            StaticTransform.transform( shape, rotation );
        }
        else if ( shapeOrGeom instanceof Geometry )
        {
            Geometry geom = (Geometry)shapeOrGeom;
            StaticTransform.transform( geom, rotation );
        }
        
        return ( shapeOrGeom );
    }
    
    public final NamedObject scaleShapeOrGeometry( NamedObject shapeOrGeom, Tuple3f scale )
    {
        if ( shapeOrGeom instanceof Shape3D )
        {
            Shape3D shape = (Shape3D)shapeOrGeom;
            StaticTransform.scale( shape, scale );
        }
        else if ( shapeOrGeom instanceof Geometry )
        {
            Geometry geom = (Geometry)shapeOrGeom;
            StaticTransform.scale( geom, scale );
        }
        
        return ( shapeOrGeom );
    }
}
