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
package org.xith3d.effects.shadows;

import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFilter;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.FastMath;
import org.openmali.spatial.bodies.Body;
import org.openmali.types.twodee.Dim2i;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.util.TupleUtils;
import org.xith3d.effects.EffectFactory;
import org.xith3d.loaders.texture.TextureCreator;
import org.xith3d.render.BaseRenderPassConfig;
import org.xith3d.render.RenderPass;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.render.TextureRenderTarget;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.RenderBinProvider;
import org.xith3d.render.preprocessing.RenderBinType;
import org.xith3d.render.preprocessing.ShadowAtom;
import org.xith3d.scenegraph.DirectionalLight;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.SpotLight;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph._SG_PrivilegedAccess;
import org.xith3d.scenegraph.View.ProjectionPolicy;

/**
 * This {@link ShadowFactory} is a base for any factory
 * realizing shadow-mapping.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class ShadowMappingFactory extends ShadowFactory
{
    public static final ShadowFactoryIdentifier SHADOW_FACTORY_ID = new ShadowFactoryIdentifier();
    
    private static final float DIRECTIONAL_LIGHT_SOURCE_DISTANCE = 1000f;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ShadowFactoryIdentifier getShadowFactoryId()
    {
        return ( SHADOW_FACTORY_ID );
    }
    
    public static class HackedRenderBinProvider extends RenderBinProvider
    {
        public void setOpaqueBin( RenderBin opaqueBin )
        {
            this.opaqueBin = opaqueBin;
        }
        
        public HackedRenderBinProvider()
        {
            super( null,
                   new RenderBin( RenderBinType.MAIN_TRANSPARENT, "Shadow Transparent RenderBin", 0 ),
                   new RenderBin( RenderBinType.MAIN_OPAQUE, "Shadow Opaque RenderBin", 0 )
                 );
        }
    }
    
    private static final Transform3D createScaleAndBias()
    {
        Transform3D scaleAndBias = new Transform3D();
        
        scaleAndBias.getMatrix4f().set( 0.5f, 0.0f, 0.0f, 0.5f,
                                        0.0f, 0.5f, 0.0f, 0.5f,
                                        0.0f, 0.0f, 0.5f, 0.5f,
                                        0.0f, 0.0f, 0.0f, 1.0f
                                      );
        
        return ( scaleAndBias );
    }
    
    private final Point3f lightPos = new Point3f();
    private final Vector3f lightDir = new Vector3f();
    private final Transform3D lightProj = new Transform3D();
    private final Transform3D lightModelView = new Transform3D();
    private final Transform3D scaleAndBias = createScaleAndBias();
    
    private final Dim2i lightViewport = new Dim2i();
    private TextureRenderTarget depthTarget = null;
    private HackedRenderBinProvider renderBinProvider = null;
    private RenderPass generationPass = null;
    
    private Texture2D shadowMap = null;
    private TextureAttributes shadowMapAttribs = null;
    
    public TextureRenderTarget getDepthRenderTarget()
    {
        if ( depthTarget != null )
            return ( depthTarget );
        
        final ShadowMappingFactory shadowFactory = (ShadowMappingFactory)EffectFactory.getInstance().getShadowFactory();
        
        Colorf back = new Colorf( 0f, 0f, 0f, 1.0f );
        
        depthTarget = new TextureRenderTarget( new Group(), shadowFactory.getShadowMap(), back );
        depthTarget.setBackgroundRenderingEnabled( false );
        
        return ( depthTarget );
    }
    
    public HackedRenderBinProvider getRenderBinProvider()
    {
        if ( renderBinProvider != null )
            return ( renderBinProvider );
        
        renderBinProvider = new HackedRenderBinProvider();
        
        return ( renderBinProvider );
    }
    
    public RenderPass getGenerationPass()
    {
        if ( generationPass != null )
            return ( generationPass );
        
        generationPass = new RenderPass( new BaseRenderPassConfig() )
        {
            @Override
            protected RenderBinProvider createRenderBinProvider()
            {
                return ( ShadowMappingFactory.this.getRenderBinProvider() );
            }
        };
        generationPass.setRenderTarget( getDepthRenderTarget() );
        generationPass.getConfig().setViewTransform( new Transform3D() );
        
        return ( generationPass );
    }
    
    /*
    private static final SpotLight findLight( RenderBin bin, String shadowLightName )
    {
        for ( int a = 0; a < bin.size(); a++ )
        {
            final InheritedNodes info = bin.getAtoms().get( a ).getNode().getInheritedNodes();
            for ( int i = 0; i < info.getLightsCount(); i++ )
            {
                final Light l = info.getLight( i );
                if ( l instanceof SpotLight )
                {
                    if ( shadowLightName == null )
                    {
                        return ( (SpotLight)l );
                    }
                    else if ( shadowLightName.equals( l.getName() ) )
                    {
                        return ( (SpotLight)l );
                    }
                }
            }
        }
        
        return ( null );
    }
    */
    
    private final void findNearAndFarPlanes( Point3f lightPos, RenderBin shadowBin, RenderPassConfig passConfig )
    {
        float near = +Float.MAX_VALUE;
        float far = -Float.MAX_VALUE;
        
        for ( int i = 0; i < shadowBin.size(); i++ )
        {
            final Body bounds = (Body)shadowBin.getAtom( i ).getNode().getWorldBounds();
            final float distCenter = lightPos.distance( bounds.getCenterX(), bounds.getCenterY(), bounds.getCenterZ() );
            final float radius = bounds.getMaxCenterDistance();
            final float tmpNear = distCenter - radius;
            final float tmpFar = distCenter + radius;
            
            if ( tmpNear < near )
                near = tmpNear;
            
            if ( tmpFar > far )
                far = tmpFar;
        }
        
        // FIXME: I have no idea, why this is necessary!
        near -= ( far - near ) * 0.1f;
        
        passConfig.setFrontClipDistance( near );
        passConfig.setBackClipDistance( far );
    }
    
    protected float calculateScreenScale()
    {
        // TODO: This value needs to be calculateed somehow. Or does it need to be set by the user???
        
        return ( 10.0f );
    }
    
    /**
     * 
     * @param fovy
     * @param aspect
     * @param near
     * @param far
     * @param lightTransform
     * @param viewTransform
     * @param textureTransform
     */
    protected void calculateTextureMatrix( float fovy, float aspect, float near, float far, Transform3D lightTransform, Transform3D viewTransform, Transform3D textureTransform )
    {
        // Extract light's projection matrix.
        if ( fovy < 0f ) // DirectionalLight
        {
            final float screenScale = calculateScreenScale(); 
            lightProj.ortho( -screenScale, screenScale, -screenScale / aspect, screenScale / aspect, near, far );
        }
        else
        {
            lightProj.perspective( fovy * 2f, aspect, near, far );
        }
        
        // Extract light's model-view-matrix.
        lightModelView.set( lightTransform );
        lightModelView.invert();
        
        // Compose the Texture-Matrix...
        textureTransform.setIdentity();
        textureTransform.mul( scaleAndBias );
        textureTransform.mul( lightProj );
        textureTransform.mul( lightModelView );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RenderPass setupRenderPass( View view, Light light, float viewportAspect, RenderBin shadowBin, long frameId, boolean justForCulling )
    {
        if ( !justForCulling && ( shadowBin.size() == 0 ) )
        {
            return ( null );
        }
        
        final RenderPass generationPass = getGenerationPass();
        final RenderPassConfig passConfig = generationPass.getConfig();
        
        if ( light instanceof DirectionalLight )
        {
            ( (DirectionalLight)light ).getComputedDirection( lightDir );
            
            // Set light-position to a galaxy far far away along the negated direction
            lightPos.set( lightDir );
            lightPos.negate();
            TupleUtils.normalizeVector( lightPos ).mul( DIRECTIONAL_LIGHT_SOURCE_DISTANCE );
            
            passConfig.setProjectionPolicy( ProjectionPolicy.PARALLEL_PROJECTION );
            passConfig.setScreenScale( calculateScreenScale() );
        }
        else //if ( light instanceof SpotLight )
        {
            ( (SpotLight)light ).getComputedDirection( lightDir );
            ( (SpotLight)light ).getComputedLocation( lightPos );
            
            passConfig.setProjectionPolicy( ProjectionPolicy.PERSPECTIVE_PROJECTION );
        }
        
        /*
         * Setup the Texture-Matrix.
         */
        
        final float near;
        final float far;
        if ( justForCulling )
        {
            if ( light instanceof DirectionalLight )
            {
                near = 0.01f;
                far = DIRECTIONAL_LIGHT_SOURCE_DISTANCE;
            }
            else
            {
                near = 0.01f;
                far = 2000f;
            }
            
            passConfig.setFrontClipDistance( near );
            passConfig.setBackClipDistance( far );
        }
        else
        {
            // Set near and far clip planes for depth-texture-rendering.
            findNearAndFarPlanes( lightPos, shadowBin, passConfig );
            near = passConfig.getFrontClipDistance();
            far = passConfig.getBackClipDistance();
        }
        passConfig.getViewTransform().lookAlong( lightPos, lightDir );
        float fovy = -1f;
        if ( light instanceof SpotLight )
        {
            fovy = ( (SpotLight)light ).getSpreadAngle() * 2f;
            passConfig.setFieldOfView( fovy );
        }
        
        if ( !justForCulling )
        {
            calculateTextureMatrix( fovy, viewportAspect, near, far, passConfig.getViewTransform(), view.getTransform(), getShadowMapAttributes().getTextureTransform() );
            
            getRenderBinProvider().setOpaqueBin( shadowBin );
        }
        
        return ( generationPass );
    }
    
    public Texture2D getShadowMap()
    {
        if ( shadowMap != null )
            return ( shadowMap );
        
        final int texSize = FastMath.pow( 2, getShadowQuality() );
        
        shadowMap = TextureCreator.createTexture( TextureFormat.DEPTH, texSize, texSize );
        shadowMap.setName( "ShadowMap" );
        shadowMap.setBoundaryModeS( TextureBoundaryMode.CLAMP_TO_EDGE );
        shadowMap.setBoundaryModeT( TextureBoundaryMode.CLAMP_TO_EDGE );
        shadowMap.setFilter( TextureFilter.TRILINEAR );
        
        return ( shadowMap );
    }
    
    public TextureAttributes getShadowMapAttributes()
    {
        if ( shadowMapAttribs != null )
            return ( shadowMapAttribs );
        
        shadowMapAttribs = new TextureAttributes();
        shadowMapAttribs.setTextureTransform( new Transform3D() );
        //shadowMapAttribs.setCompareMode( org.jagatoo.opengl.enums.TextureCompareMode.COMPARE_R_TO_TEXTURE );
        shadowMapAttribs.setCompareFunction( org.jagatoo.opengl.enums.CompareFunction.LOWER_OR_EQUAL );
        
        return ( shadowMapAttribs );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ShadowAtom getShadowAtom( Node node )
    {
        final ShadowAtom shadowAtom;
        if ( node instanceof Shape3D )
            shadowAtom = _SG_PrivilegedAccess.getAtom( (Shape3D)node );
        else
            shadowAtom = null;
        
        /*
        if ( shadowAtom != null )
        {
        }
        */
        
        return ( shadowAtom );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void verifyLight( Light light )
    {
        if ( ( light != null ) && ( !( light instanceof SpotLight ) && !( light instanceof DirectionalLight ) ) )
        {
            throw new IllegalArgumentException( "This shadowFactory accepts SpotLights and DirectionLights only." );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needsPerLightCulling()
    {
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setShadowQuality( int quality )
    {
        super.setShadowQuality( quality );
        
        final int texSize = FastMath.pow( 2, getShadowQuality() );
        
        lightViewport.set( texSize, texSize );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Sized2iRO getLightViewport()
    {
        return ( lightViewport );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onOccluderStateChanged( Node node, boolean isOccluder )
    {
        if ( node instanceof GroupNode )
        {
            final GroupNode group = (GroupNode)node;
            final int n = group.numChildren();
            for ( int i = 0; i < n; i++ )
                group.getChild( i ).setIsOccluder( isOccluder );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled( boolean enabled )
    {
        super.setEnabled( enabled );
        
        if ( shadowMap != null )
            shadowMap.setEnabled( enabled );
    }
    
    public ShadowMappingFactory()
    {
        setShadowQuality( getShadowQuality() );
    }
}
