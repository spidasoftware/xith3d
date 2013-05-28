/**
 * Copyright (c) 2003-2009, Xith3D Project Group all rights reserved.
 * 
 * Portions based on the Java3D interface, Copyright by Sun Microsystems. Many
 * thanks to the developers of Java3D and Sun Microsystems for their innovation
 * and design.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
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
package org.xith3d.scenegraph.primitives;

import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.FastMath;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loaders.texture.TextureLoader.FlipMode;
import org.xith3d.render.BackgroundRenderPass;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.View.CameraMode;

/**
 * A sky box is background node intended to display the sky with a far away
 * appearance but without consuming massive computational resources. It is drawn
 * with the depth buffer disabled, so all objects which are drawn after it will
 * be drawn in front. Even though it respects View's angle, it will always be
 * drawn as if the View (camera) were in its center regardless of the actual
 * position.
 * <p>
 * This implementation creates a sphere to provide a more realistic sky than
 * SkyBox. The downside to using a sphere is that you need a panoramic picture
 * to use as the Texture. This implementation also uses GeoSphere geometry so
 * that it doesn't create a ridiculous number of triangles.
 * <p>
 * This class ignores the default setting for pickable and sets itself to NOT
 * pickable.  You must explicitly call setPickable( true ) on SkyGeoSphere
 * nodes regardless of the default setting.
 * <p>
 * Originally inspired by William Denniss's SkyBox.
 * 
 * @author Kevin Finley (aka horati)
 */
public class SkyGeoSphere extends BackgroundRenderPass
{
    public static final int DEFAULT_SKY_SPLITS = 5;
    public static final float X_ROTATION = FastMath.PI * 1.5f;
    
    private static Texture getTextureOrNull( String texture )
    {
        if ( texture == null )
            return ( null );
        
        Texture tex = TextureLoader.getInstance().getTexture( texture, (FlipMode)null, TextureFormat.RGB, Texture.MipmapMode.BASE_LEVEL, true, false, false );
        tex.enableAutoFreeLocalData();
        
        return ( tex );
    }
    
    public static <G extends GroupNode> G createSkyGeoSphereGroup( int frequency, Texture texture, G group )
    {
        GeoSphere sphere = new GeoSphere( 5f, frequency, Geometry.COORDINATES | Geometry.TEXTURE_COORDINATES, false, 2 );
        sphere.getGeometry().setOptimization( Geometry.Optimization.USE_DISPLAY_LISTS );
        StaticTransform.rotateX( sphere, FastMath.PI_HALF );
        StaticTransform.rotateZ( sphere, FastMath.PI );
        
        Appearance appearance = sphere.getAppearance( true );
        
        appearance.setTexture( texture );
        
        PolygonAttributes polygonAttributes = new PolygonAttributes( PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_FRONT );
        appearance.setPolygonAttributes( polygonAttributes );
        
        sphere.setAppearance( appearance );
        
        group.addChild( sphere );
        group.setPickableRecursive( false );
        
        return ( group );
    }
    
    public static BranchGroup createSkyGeoSphereGroup( int frequency, Texture texture )
    {
        return ( createSkyGeoSphereGroup( frequency, texture, new BranchGroup() ) );
    }
    
    public static final BranchGroup createSkyGeoSphereGroup( int frequency, String textureName )
    {
        return ( createSkyGeoSphereGroup( frequency, getTextureOrNull( textureName ) ) );
    }
    
    public static final BranchGroup createSkyGeoSphereGroup( Texture texture )
    {
        return ( createSkyGeoSphereGroup( SkyGeoSphere.DEFAULT_SKY_SPLITS, texture ) );
    }
    
    public static final BranchGroup createSkyGeoSphereGroup( String textureName )
    {
        return ( createSkyGeoSphereGroup( SkyGeoSphere.DEFAULT_SKY_SPLITS, textureName ) );
    }
    
    
    
    public SkyGeoSphere( int frequency, Texture texture )
    {
        super( createSkyGeoSphereGroup( frequency, texture ), new BaseRenderPassConfig( CameraMode.VIEW_FIXED_POSITION ) );
    }
    
    public SkyGeoSphere( int frequency, String textureName )
    {
        this( frequency, getTextureOrNull( textureName ) );
    }
    
    public SkyGeoSphere( Texture texture )
    {
        this( SkyGeoSphere.DEFAULT_SKY_SPLITS, texture );
    }
    
    public SkyGeoSphere( String textureName )
    {
        this( SkyGeoSphere.DEFAULT_SKY_SPLITS, textureName );
    }
}
