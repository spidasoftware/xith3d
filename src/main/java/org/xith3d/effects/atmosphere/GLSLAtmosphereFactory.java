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
package org.xith3d.effects.atmosphere;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.jagatoo.opengl.enums.BlendFunction;
import org.jagatoo.opengl.enums.BlendMode;
import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.openmali.FastMath;
import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.openmali.vecmath2.Vector4f;
import org.xith3d.loaders.shaders.impl.glsl.GLSLShaderLoader;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.GLSLFragmentShader;
import org.xith3d.scenegraph.GLSLContext;
import org.xith3d.scenegraph.GLSLParameters;
import org.xith3d.scenegraph.GLSLShaderProgram;
import org.xith3d.scenegraph.GLSLVertexShader;
import org.xith3d.scenegraph.PointLight;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.primitives.Sphere;

/**
 * The AtmosphereFactory is a visual effect for spheres. 
 * It computes in real-time the atmosphere of a planet see from space.<br>
 * <br>
 * All computations are done by the GPU using shaders.<br>
 * <br>
 * This code is a Xith adaptation of Sean O'Neil code given here: http://sponeil.net/
 * (Code under BSD license)<br>
 * <br>
 * 
 * @author Yoann Meste (aka Mancer)
 */
public class GLSLAtmosphereFactory extends AtmosphereFactory
{
    private static GLSLShaderProgram groundFromSpace = null;
    private static GLSLShaderProgram skyFromSpace = null;
    
    private static ArrayList<GLSLParameters> parametersGround = new ArrayList<GLSLParameters>();
    private static ArrayList<GLSLParameters> parametersSky = new ArrayList<GLSLParameters>();
    
    private int nbSamples = 2;
    private float kr = 0.0025f;
    private float km = 0.0015f;
    private float eSun = 15f;
    private float g = -0.95f;
    private float innerRadius;
    private float outerRadius;
    private float rayleighScaleDepth = 0.25f;
    //private float mieScaleDepth = 0.1f;
    
    /*
    public void setNbSamples( int nbSamples )
    {
        this.nbSamples = nbSamples;
    }
    
    public int getNbSamples()
    {
        return ( nbSamples );
    }
    
    public void setKr( float kr )
    {
        this.kr = kr;
    }
    
    public float getKr()
    {
        return ( kr );
    }
    
    public void setKm( float km )
    {
        this.km = km;
    }
    
    public float getKm()
    {
        return ( km );
    }
    
    public void setESun( float sun )
    {
        eSun = sun;
    }
    
    public float getESun()
    {
        return ( eSun );
    }
    
    public void setG( float g )
    {
        this.g = g;
    }
    
    public float getG()
    {
        return ( g );
    }
    
    public void setRayleighScaleDepth( float rayleighScaleDepth )
    {
        this.rayleighScaleDepth = rayleighScaleDepth;
    }
    
    public float getRayleighScaleDepth()
    {
        return ( rayleighScaleDepth );
    }
    
    public void setMieScaleDepth( float mieScaleDepth )
    {
        this.mieScaleDepth = mieScaleDepth;
    }
    
    public float getMieScaleDepth()
    {
        return ( mieScaleDepth );
    }
    */
    
    private final float getScale()
    {
        return ( 1f / ( outerRadius - innerRadius ) );
    }
    
    private final float getKm4Pi()
    {
        return ( km * 4f * FastMath.PI );
    }
    
    private final float getKr4Pi()
    {
        return ( kr * 4f * FastMath.PI );
    }
    
    private void updateShaderWavelength( Tuple3f wavelength3 )
    {
        if ( wavelength3 == null )
            wavelength3 = getWavelength3();
        
        Vector4f wavelength4 = Vector4f.fromPool();
        wavelength4.set( FastMath.pow( wavelength3.getX(), 4f ), FastMath.pow( wavelength3.getY(), 4f ), FastMath.pow( wavelength3.getZ(), 4f ), 0f );
        
        Tuple3f invWaveLength = Tuple3f.fromPool();
        invWaveLength.set( 1f / wavelength4.getX(), 1f / wavelength4.getY(), 1f / wavelength4.getZ() );
        
        for ( int i = 0; i < parametersGround.size(); i++ )
        {
            parametersGround.get( i ).setUniformVar( "v3InvWavelength", invWaveLength );
            parametersSky.get( i ).setUniformVar( "v3InvWavelength", invWaveLength );
        }
        
        Tuple3f.toPool( invWaveLength );
        Vector4f.toPool( wavelength4 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setWavelength3( Tuple3f wavelength3 )
    {
        super.setWavelength3( wavelength3 );
        
        updateShaderWavelength( wavelength3 );
    }
    
    public final void transformVector( Quaternion4f q, Tuple3f v, Vector3f out )
    {
        Matrix3f m = Matrix3f.fromPool();
        m.set( q );
        
        m.mul( v, out );
        
        Matrix3f.toPool( m );
    }
    
    private void setShaderCameraPos( Point3f cameraPos, Vector3f translation, AxisAngle3f angle )
    {
        Vector3f cam = new Vector3f();
        cam.set( cameraPos.getX(), cameraPos.getY(), cameraPos.getZ() );
        cam.sub( translation );
        
        Quaternion4f rotate = Quaternion4f.fromPool();
        
        if ( angle.getAngle() != 0f )
        {
            rotate.set( angle );
        }
        else
        {
            rotate.setFromAxisAngle( 0f, 1f, 0f, 0f );
        }
        
        rotate.invert();
        
        Vector3f computedCam = Vector3f.fromPool();
        transformVector( rotate, cam, computedCam );
        float camHeight = computedCam.length();
        
        Quaternion4f.toPool( rotate );
        
        for ( int i = 0; i < parametersGround.size(); i++ )
        {
            GLSLParameters paramsGround = parametersGround.get( i );
            GLSLParameters paramsSky = parametersSky.get( i );
            
            paramsGround.setUniformVar( "v3CameraPos", computedCam );
            paramsGround.setUniformVar( "fCameraHeight", camHeight );
            paramsGround.setUniformVar( "fCameraHeight2", camHeight * camHeight );
            
            paramsSky.setUniformVar( "v3CameraPos", computedCam );
            paramsSky.setUniformVar( "fCameraHeight", camHeight );
            paramsSky.setUniformVar( "fCameraHeight2", camHeight * camHeight );
        }
        
        Vector3f.toPool( computedCam );
    }
    
    private void setShaderLightPos( Tuple3f lightPos, Vector3f translation, AxisAngle3f angle )
    {
        Quaternion4f rotate = Quaternion4f.fromPool();
        
        if ( angle.getAngle() != 0f )
        {
            rotate.set( angle );
        }
        else
        {
            rotate.setFromAxisAngle( 0f, 1f, 0f, 0f );
        }
        
        rotate.invert();
        
        lightPos.sub( translation );
        
        Vector3f computedLight = Vector3f.fromPool();
        transformVector( rotate, lightPos, computedLight );
        computedLight.normalize();
        
        for ( int i = 0; i < parametersGround.size(); i++ )
        {
            parametersGround.get( i ).setUniformVar( "v3LightPos", computedLight );
            parametersSky.get( i ).setUniformVar( "v3LightPos", computedLight );
        }
        
        Vector3f.toPool( computedLight );
        Quaternion4f.toPool( rotate );
    }
    
    private void initShaderParameters( GLSLParameters paramsGround, GLSLParameters paramsSky )
    {
        paramsGround.setUniformVar( "fInnerRadius", innerRadius );
        paramsGround.setUniformVar( "fInnerRadius2", innerRadius * innerRadius );
        paramsGround.setUniformVar( "fOuterRadius", outerRadius );
        paramsGround.setUniformVar( "fOuterRadius2", outerRadius * outerRadius );
        paramsGround.setUniformVar( "fKrESun", kr * eSun );
        paramsGround.setUniformVar( "fKmESun", km * eSun );
        paramsGround.setUniformVar( "fKr4PI", getKr4Pi() );
        paramsGround.setUniformVar( "fKm4PI", getKm4Pi() );
        paramsGround.setUniformVar( "fScale", getScale() );
        paramsGround.setUniformVar( "fScaleDepth", rayleighScaleDepth );
        paramsGround.setUniformVar( "fScaleOverScaleDepth", ( 1f / ( outerRadius - innerRadius ) ) / rayleighScaleDepth );
        paramsGround.setUniformVar( "g", g );
        paramsGround.setUniformVar( "g2", g * g );
        paramsGround.setUniformVar( "nSamples", nbSamples );
        paramsGround.setUniformVar( "fSamples", (float)nbSamples );
        paramsGround.setUniformVar( "s2Tex2", 0 );
        
        paramsSky.setUniformVar( "fInnerRadius", innerRadius );
        paramsSky.setUniformVar( "fInnerRadius2", innerRadius * innerRadius );
        paramsSky.setUniformVar( "fOuterRadius", outerRadius );
        paramsSky.setUniformVar( "fOuterRadius2", outerRadius * outerRadius );
        paramsSky.setUniformVar( "fKrESun", kr * eSun );
        paramsSky.setUniformVar( "fKmESun", km * eSun );
        paramsSky.setUniformVar( "fKr4PI", getKr4Pi() );
        paramsSky.setUniformVar( "fKm4PI", getKm4Pi() );
        paramsSky.setUniformVar( "fScale", getScale() );
        paramsSky.setUniformVar( "fScaleDepth", rayleighScaleDepth );
        paramsSky.setUniformVar( "fScaleOverScaleDepth", ( 1f / ( outerRadius - innerRadius ) ) / rayleighScaleDepth );
        paramsSky.setUniformVar( "g", g );
        paramsSky.setUniformVar( "g2", g * g );
        paramsSky.setUniformVar( "nSamples", nbSamples );
        paramsSky.setUniformVar( "fSamples", (float)nbSamples );
    }
    
    private static URL getResource( String resName ) throws IOException
    {
        URL url = GLSLAtmosphereFactory.class.getClassLoader().getResource( resName );
        
        if ( url == null )
        {
            throw new IOException( "Could not find resource \"" + resName + "\"." );
        }
        
        return ( url );
    }
    
    private void loadShaderPrograms() throws IOException
    {
        if ( groundFromSpace == null )
        {
            GLSLVertexShader vertShader = GLSLShaderLoader.getInstance().loadVertexShader( getResource( "resources/org/xith3d/shaders/atmosphere/ground_from_space.glslvert" ) );
            
            groundFromSpace = new GLSLShaderProgram();
            groundFromSpace.addShader( vertShader );
            
            GLSLFragmentShader fragShader = GLSLShaderLoader.getInstance().loadFragmentShader( getResource( "resources/org/xith3d/shaders/atmosphere/ground_from_space.glslfrag" ) );
            groundFromSpace.addShader( fragShader );
        }
        
        if ( skyFromSpace == null )
        {
            GLSLVertexShader vertShader = GLSLShaderLoader.getInstance().loadVertexShader( getResource( "resources/org/xith3d/shaders/atmosphere/sky_from_space.glslvert" ) );
            
            skyFromSpace = new GLSLShaderProgram();
            skyFromSpace.addShader( vertShader );
            
            GLSLFragmentShader fragShader = GLSLShaderLoader.getInstance().loadFragmentShader( getResource( "resources/org/xith3d/shaders/atmosphere/sky_from_space.glslfrag" ) );
            skyFromSpace.addShader( fragShader );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void prepareAtmosphere( Sphere sphere, float atmosphereRadius, PointLight light )
    {
        try
        {
            loadShaderPrograms();
        }
        catch ( IOException e )
        {
            throw new Error( e );
        }
        
        innerRadius = sphere.getRadius();
        outerRadius = atmosphereRadius;
        
        // Applying the shader to the first shape
        Appearance appGround = sphere.getAppearance( true );
        
        GLSLContext prgGround = new GLSLContext( groundFromSpace );
        parametersGround.add( prgGround.getUniformParameters() );
        
        appGround.setShaderProgramContext( prgGround );
        
        // the atmospheric sphere is added
        Sphere atmoSphere = new Sphere( outerRadius, 100, 100, Colorf.BLUE );
        
        GLSLContext prgSky = new GLSLContext( skyFromSpace );
        parametersSky.add( prgSky.getUniformParameters() );
        
        Appearance appSky = atmoSphere.getAppearance( true );
        
        TransparencyAttributes attributes = new TransparencyAttributes();
        attributes.setMode( BlendMode.BLENDED );
        attributes.setSrcBlendFunction( BlendFunction.ONE );
        attributes.setDstBlendFunction( BlendFunction.ONE );
        
        appSky.setTransparencyAttributes( attributes );
        
        appSky.getPolygonAttributes( true ).setFaceCullMode( FaceCullMode.BACK );
        appSky.getPolygonAttributes( true ).setDrawMode( DrawMode.FILL );
        
        appSky.setShaderProgramContext( prgSky );
        
        initShaderParameters( prgGround.getUniformParameters(), prgSky.getUniformParameters() );
        
        sphere.getParent().addChild( atmoSphere );
        
        updateShaderWavelength( null );
    }
    
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        // We do not update the shader if the shape is attached in the scenegraph
        if ( getSphere().getRoot() == null )
        {
            return;
        }
        
        // using quaternions to avoid grimbal lock !
        Transform3D world = getSphere().getWorldTransform();
        
        Quaternion4f quat = Quaternion4f.fromPool();
        AxisAngle3f angle = AxisAngle3f.fromPool();
        
        world.get( quat );
        angle.set( quat );
        
        Vector3f translation = world.getTranslation();
        
        Quaternion4f.toPool( quat );
        Point3f cameraPos = getSphere().getRoot().getSceneGraph().getView().getPosition();
        setShaderCameraPos( cameraPos, translation, angle );
        
        Point3f lightPos = Point3f.fromPool();
        getLightPos( lightPos );
        
        setShaderLightPos( lightPos, translation, angle );
        
        Point3f.toPool( lightPos );
        AxisAngle3f.toPool( angle );
    }
    
    public GLSLAtmosphereFactory()
    {
    }
}
