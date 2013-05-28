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
package org.xith3d.render.lwjgl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.openmali.FastMath;
import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.spatial.bounds.BoundsType;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Tuple3f;

import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderAtomPeer;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.View;

/**
 * Insert package comments here
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class BoundsAtomPeer extends RenderAtomPeer
{
    private static final Matrix4f TEMP_MAT = new Matrix4f();
    private static final Colorf boundsColor_Shape = new Colorf( 1.0f, 0.0f, 0.0f, 0.0f );
    private static BoundingSphere boundingSphere = new BoundingSphere();
    private static BoundingBox boundingBox = new BoundingBox();
    
    private static FloatBuffer tmpPlaneBuffer = BufferUtils.createFloatBuffer( 4 );
    private static final IntBuffer tmpIntBuffer = BufferUtils.createIntBuffer( 16 );
    private static FloatBuffer currentColor = BufferUtils.createFloatBuffer( 16 );
    
    private static final Colorf boundsColor_Group = new Colorf( 0.0f, 1.0f, 0.0f, 0.0f );
    
    private static final int DRAW_STYLE_POINT = 1;
    private static final int DRAW_STYLE_LINE = 2;
    private static final int DRAW_STYLE_FILL = 3;
    private static final int DRAW_STYLE_SILHOUETTE = 4;
    
    /**
     * draws a sphere of the given  radius centered around the origin.
     * The sphere is subdivided around the x/y axis into slices and along the z axis
     * into stacks (similar to lines of longitude and latitude).
     * 
     * This code is ported from LWJGL's GLU implementation to avoid the GLU requirement. (GLU is not included in LWJGL 2.0's jar!)
     */
    private static void drawSphere( float radius, int slices, int stacks, boolean withNormals, boolean normalsToOutside, boolean withTextureCoords )
    {
        float rho, theta;
        float x, y, z;
        float s, t, ds, dt;
        int i, j, imin, imax;
        
        float nsign = normalsToOutside ? +1.0f : -1.0f;
        
        float drho = FastMath.PI / stacks;
        float dtheta = FastMath.TWO_PI / slices;
        
        int drawStyle = DRAW_STYLE_LINE;
        
        if ( drawStyle == DRAW_STYLE_FILL )
        {
            if ( !withTextureCoords )
            {
                // draw +Z end as a triangle fan
                GL11.glBegin( GL11.GL_TRIANGLE_FAN );
                GL11.glNormal3f( 0.0f, 0.0f, 1.0f );
                GL11.glVertex3f( 0.0f, 0.0f, nsign * radius );
                for ( j = 0; j <= slices; j++ )
                {
                    theta = ( j == slices ) ? 0.0f : j * dtheta;
                    x = -FastMath.sin( theta ) * FastMath.sin( drho );
                    y = FastMath.cos( theta ) * FastMath.sin( drho );
                    z = nsign * FastMath.cos( drho );
                    if ( withNormals )
                    {
                        GL11.glNormal3f( x * nsign, y * nsign, z * nsign );
                    }
                    GL11.glVertex3f( x * radius, y * radius, z * radius );
                }
                GL11.glEnd();
            }
            
            ds = 1.0f / slices;
            dt = 1.0f / stacks;
            t = 1.0f; // because loop now runs from 0
            if ( withTextureCoords )
            {
                imin = 0;
                imax = stacks;
            }
            else
            {
                imin = 1;
                imax = stacks - 1;
            }
            
            // draw intermediate stacks as quad strips
            for ( i = imin; i < imax; i++ )
            {
                rho = i * drho;
                GL11.glBegin( GL11.GL_QUAD_STRIP );
                s = 0.0f;
                for ( j = 0; j <= slices; j++ )
                {
                    theta = ( j == slices ) ? 0.0f : j * dtheta;
                    x = -FastMath.sin( theta ) * FastMath.sin( rho );
                    y = FastMath.cos( theta ) * FastMath.sin( rho );
                    z = nsign * FastMath.cos( rho );
                    if ( withNormals )
                    {
                        GL11.glNormal3f( x * nsign, y * nsign, z * nsign );
                    }
                    if ( withTextureCoords )
                    {
                        GL11.glTexCoord2f( s, t );
                    }
                    GL11.glVertex3f( x * radius, y * radius, z * radius );
                    x = -FastMath.sin( theta ) * FastMath.sin( rho + drho );
                    y = FastMath.cos( theta ) * FastMath.sin( rho + drho );
                    z = nsign * FastMath.cos( rho + drho );
                    if ( withNormals )
                    {
                        GL11.glNormal3f( x * nsign, y * nsign, z * nsign );
                    }
                    if ( withTextureCoords )
                    {
                        GL11.glTexCoord2f( s, t - dt );
                    }
                    s += ds;
                    GL11.glVertex3f( x * radius, y * radius, z * radius );
                }
                GL11.glEnd();
                t -= dt;
            }
            
            if ( !withTextureCoords )
            {
                // draw -Z end as a triangle fan
                GL11.glBegin( GL11.GL_TRIANGLE_FAN );
                GL11.glNormal3f( 0.0f, 0.0f, -1.0f );
                GL11.glVertex3f( 0.0f, 0.0f, -radius * nsign );
                rho = FastMath.PI - drho;
                s = 1.0f;
                for ( j = slices; j >= 0; j-- )
                {
                    theta = ( j == slices ) ? 0.0f : j * dtheta;
                    x = -FastMath.sin( theta ) * FastMath.sin( rho );
                    y = FastMath.cos( theta ) * FastMath.sin( rho );
                    z = nsign * FastMath.cos( rho );
                    if ( withNormals )
                        GL11.glNormal3f( x * nsign, y * nsign, z * nsign );
                    s -= ds;
                    GL11.glVertex3f( x * radius, y * radius, z * radius );
                }
                GL11.glEnd();
            }
        }
        else if ( ( drawStyle == DRAW_STYLE_LINE ) || ( drawStyle == DRAW_STYLE_SILHOUETTE ) )
        {
            // draw stack lines
            for ( i = 1; i < stacks; i++ )
            { // stack line at i==stacks-1 was missing here
                rho = i * drho;
                GL11.glBegin( GL11.GL_LINE_LOOP );
                for ( j = 0; j < slices; j++ )
                {
                    theta = j * dtheta;
                    x = FastMath.cos( theta ) * FastMath.sin( rho );
                    y = FastMath.sin( theta ) * FastMath.sin( rho );
                    z = FastMath.cos( rho );
                    if ( withNormals )
                        GL11.glNormal3f( x * nsign, y * nsign, z * nsign );
                    GL11.glVertex3f( x * radius, y * radius, z * radius );
                }
                GL11.glEnd();
            }
            // draw slice lines
            for ( j = 0; j < slices; j++ )
            {
                theta = j * dtheta;
                GL11.glBegin( GL11.GL_LINE_STRIP );
                for ( i = 0; i <= stacks; i++ )
                {
                    rho = i * drho;
                    x = FastMath.cos( theta ) * FastMath.sin( rho );
                    y = FastMath.sin( theta ) * FastMath.sin( rho );
                    z = FastMath.cos( rho );
                    if ( withNormals )
                        GL11.glNormal3f( x * nsign, y * nsign, z * nsign );
                    GL11.glVertex3f( x * radius, y * radius, z * radius );
                }
                GL11.glEnd();
            }
        }
        else if ( drawStyle == DRAW_STYLE_POINT )
        {
            // top and bottom-most points
            GL11.glBegin( GL11.GL_POINTS );
            if ( withNormals )
                GL11.glNormal3f( 0.0f, 0.0f, nsign );
            GL11.glVertex3f( 0.0f, 0.0f, radius );
            if ( withNormals )
                GL11.glNormal3f( 0.0f, 0.0f, -nsign );
            GL11.glVertex3f( 0.0f, 0.0f, -radius );
            
            // loop over stacks
            for ( i = 1; i < stacks - 1; i++ )
            {
                rho = i * drho;
                for ( j = 0; j < slices; j++ )
                {
                    theta = j * dtheta;
                    x = FastMath.cos( theta ) * FastMath.sin( rho );
                    y = FastMath.sin( theta ) * FastMath.sin( rho );
                    z = FastMath.cos( rho );
                    if ( withNormals )
                        GL11.glNormal3f( x * nsign, y * nsign, z * nsign );
                    GL11.glVertex3f( x * radius, y * radius, z * radius );
                }
            }
            GL11.glEnd();
        }
    }
    
    private static final void drawCube( float sizeX, float sizeY, float sizeZ )
    {
        float hsx = sizeX / 2f;
        float hsy = sizeY / 2f;
        float hsz = sizeZ / 2f;
        
        GL11.glBegin( GL11.GL_QUADS ); // Draw A Quad
        
        GL11.glVertex3f( hsx, hsy, -hsz );  // Top Right Of The Quad (Top)
        GL11.glVertex3f( -hsx, hsy, -hsz ); // Top Left Of The Quad (Top)
        GL11.glVertex3f( -hsx, hsy, hsz );  // Bottom Left Of The Quad (Top)
        GL11.glVertex3f( hsx, hsy, hsz );   // Bottom Right Of The Quad (Top)
        
        GL11.glVertex3f( hsx, -hsy, hsz );   // Top Right Of The Quad (Bottom)
        GL11.glVertex3f( -hsx, -hsy, hsz );  // Top Left Of The Quad (Bottom)
        GL11.glVertex3f( -hsx, -hsy, -hsz ); // Bottom Left Of The Quad (Bottom)
        GL11.glVertex3f( hsx, -hsy, -hsz );  // Bottom Right Of The Quad (Bottom)
        
        GL11.glVertex3f( hsx, hsy, hsz );   // Top Right Of The Quad (Front)
        GL11.glVertex3f( -hsx, hsy, hsz );  // Top Left Of The Quad (Front)
        GL11.glVertex3f( -hsx, -hsy, hsz ); // Bottom Left Of The Quad (Front)
        GL11.glVertex3f( hsx, -hsy, hsz );  // Bottom Right Of The Quad (Front)
        
        GL11.glVertex3f( hsx, -hsy, -hsz );  // Bottom Left Of The Quad (Back)
        GL11.glVertex3f( -hsx, -hsy, -hsz ); // Bottom Right Of The Quad (Back)
        GL11.glVertex3f( -hsx, hsy, -hsz );  // Top Right Of The Quad (Back)
        GL11.glVertex3f( hsx, hsy, -hsz );   // Top Left Of The Quad (Back)
        
        GL11.glVertex3f( -hsx, hsy, hsz );   // Top Right Of The Quad (Left)
        GL11.glVertex3f( -hsx, hsy, -hsz );  // Top Left Of The Quad (Left)
        GL11.glVertex3f( -hsx, -hsy, -hsz ); // Bottom Left Of The Quad (Left)
        GL11.glVertex3f( -hsx, -hsy, hsz );  // Bottom Right Of The Quad (Left)
        
        GL11.glVertex3f( hsx, hsy, -hsz );  // Top Right Of The Quad (Right)
        GL11.glVertex3f( hsx, hsy, hsz );   // Top Left Of The Quad (Right)
        GL11.glVertex3f( hsx, -hsy, hsz );  // Bottom Left Of The Quad (Right)
        GL11.glVertex3f( hsx, -hsy, -hsz ); // Bottom Right Of The Quad (Right)
        
        GL11.glEnd(); // Done Drawing The Quad
    }
    
    /**
     * Draws bounds around shapes as a debugging aid
     * 
     * @param bounds
     */
    protected static final int drawBounds( Bounds bounds, Colorf color, OpenGLStatesCache statesCache )
    {
        tmpIntBuffer.clear();
        GL11.glGetTexEnv( GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, tmpIntBuffer );
        final int oldTexEnvMode = tmpIntBuffer.get();
        
        GL11.glTexEnvi( GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE );
        
        tmpPlaneBuffer.clear();
        GL11.glGetTexEnv( GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, tmpPlaneBuffer );
        final float[] oldTexEnvColor = new float[]
        {
            tmpPlaneBuffer.get(), tmpPlaneBuffer.get(), tmpPlaneBuffer.get(), tmpPlaneBuffer.get()
        };
        
        tmpPlaneBuffer.clear();
        tmpPlaneBuffer.put( 0, 0f );
        tmpPlaneBuffer.put( 1, 0f );
        tmpPlaneBuffer.put( 2, 0f );
        tmpPlaneBuffer.put( 3, 1f );
        GL11.glTexEnv( GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, tmpPlaneBuffer );
        
        final boolean oldTex1D = GL11.glIsEnabled( GL11.GL_TEXTURE_1D );
        final boolean oldTex2D = GL11.glIsEnabled( GL11.GL_TEXTURE_2D );
        final boolean oldTex3D = GL11.glIsEnabled( GL12.GL_TEXTURE_3D );
        final boolean oldTexCM = GL11.glIsEnabled( GL13.GL_TEXTURE_CUBE_MAP );
        
        GL11.glDisable( GL11.GL_TEXTURE_1D );
        GL11.glDisable( GL11.GL_TEXTURE_2D );
        GL11.glDisable( GL12.GL_TEXTURE_3D );
        GL11.glDisable( GL13.GL_TEXTURE_CUBE_MAP );
        
        
        switch ( bounds.getType() )
        {
            case SPHERE:
            {
                //GL11.glPushMatrix();
                //GL11.glLoadIdentity();
                boolean isBlended = statesCache.blendingEnabled;
                boolean isAlphaTest = statesCache.alphaTestEnabled;
                if ( isBlended )
                    GL11.glDisable( GL11.GL_BLEND );
                if ( isAlphaTest )
                    GL11.glDisable( GL11.GL_ALPHA_TEST );
                currentColor.clear();
                GL11.glGetFloat( GL11.GL_CURRENT_COLOR, currentColor );
                if ( color == null )
                    GL11.glColor4f( boundsColor_Group.getRed(), boundsColor_Group.getGreen(), boundsColor_Group.getBlue(), boundsColor_Group.getAlpha() );
                else
                    GL11.glColor4f( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
                BoundingSphere s = (BoundingSphere)bounds;
                GL11.glTranslatef( s.getCenterX(), s.getCenterY(), s.getCenterZ() );
                drawSphere( s.getRadius(), 12, 12, false, true, false );
                GL11.glTranslatef( -s.getCenterX(), -s.getCenterY(), -s.getCenterZ() );
                GL11.glColor4f( currentColor.get(), currentColor.get(), currentColor.get(), currentColor.get() );
                if ( isBlended )
                    GL11.glEnable( GL11.GL_BLEND );
                if ( isAlphaTest )
                    GL11.glEnable( GL11.GL_ALPHA_TEST );
                //GL11.glPopMatrix();
                
                break;
            }
            case AABB:
            {
                final boolean isBlended = GL11.glIsEnabled( GL11.GL_BLEND );
                final boolean isAlphaTest = GL11.glIsEnabled( GL11.GL_ALPHA_TEST );
                if ( isAlphaTest )
                    GL11.glDisable( GL11.GL_ALPHA_TEST );
                currentColor.clear();
                GL11.glGetFloat( GL11.GL_CURRENT_COLOR, currentColor );
                GL11.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );
                if ( color == null )
                    GL11.glColor4f( boundsColor_Group.getRed(), boundsColor_Group.getGreen(), boundsColor_Group.getBlue(), 0.05f );
                else
                    GL11.glColor4f( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
                
                BoundingBox box = (BoundingBox)bounds;
                Tuple3f boxSize = box.getSize();
                GL11.glTranslatef( box.getCenterX(), box.getCenterY(), box.getCenterZ() );
                
                GL11.glDepthMask( false );
                
                GL11.glColor4f( boundsColor_Group.getRed(), boundsColor_Group.getGreen(), boundsColor_Group.getBlue(), 0.1f );
                GL11.glEnable( GL11.GL_BLEND );
                drawCube( boxSize.getX(), boxSize.getY(), boxSize.getZ() );
                
                GL11.glDepthMask( true );
                
                tmpIntBuffer.clear();
                GL11.glGetInteger( GL11.GL_LINE_WIDTH, tmpIntBuffer );
                final int LINE_WIDTH = tmpIntBuffer.get();
                //tmpIntBuffer.clear();
                //GL11.glGetInteger( GL11.GL_POLYGON_MODE, tmpIntBuffer );
                //final int POLYGON_MODE = tmpIntBuffer.get();
                
                GL11.glColor4f( 1.0f, 0.0f, 0.0f, 0.0f );
                GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
                GL11.glLineWidth( 3f );
                GL11.glDisable( GL11.GL_BLEND );
                drawCube( boxSize.getX(), boxSize.getY(), boxSize.getZ() );
                GL11.glLineWidth( LINE_WIDTH );
                //GL11.glPolygonMode( POLYGON_MODE, GL11.GL_FILL );
                GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL );
                
                GL11.glTranslatef( -box.getCenterX(), -box.getCenterY(), -box.getCenterZ() );
                GL11.glColor4f( currentColor.get(), currentColor.get(), currentColor.get(), currentColor.get() );
                if ( isBlended )
                    GL11.glEnable( GL11.GL_BLEND );
                else
                    GL11.glDisable( GL11.GL_BLEND );
                if ( isAlphaTest )
                    GL11.glEnable( GL11.GL_ALPHA_TEST );
                else
                    GL11.glDisable( GL11.GL_ALPHA_TEST );
                
                statesCache.blendingEnabled = isBlended;
                statesCache.alphaTestEnabled = isAlphaTest;
                
                GL11.glDepthMask( statesCache.depthWriteMask );
                
                break;
            }
        }
        
        
        if ( oldTex1D )
            GL11.glEnable( GL11.GL_TEXTURE_1D );
        if ( oldTex2D )
            GL11.glEnable( GL11.GL_TEXTURE_2D );
        if ( oldTex3D )
            GL11.glEnable( GL12.GL_TEXTURE_3D );
        if ( oldTexCM )
            GL11.glEnable( GL13.GL_TEXTURE_CUBE_MAP );
        
        statesCache.texture1DEnabled[ statesCache.currentServerTextureUnit ] = oldTex1D;
        statesCache.texture2DEnabled[ statesCache.currentServerTextureUnit ] = oldTex2D;
        statesCache.texture3DEnabled[ statesCache.currentServerTextureUnit ] = oldTex3D;
        statesCache.textureCMEnabled[ statesCache.currentServerTextureUnit ] = oldTexCM;
        
        tmpPlaneBuffer.clear();
        tmpPlaneBuffer.put( 0, oldTexEnvColor[ 0 ] );
        tmpPlaneBuffer.put( 1, oldTexEnvColor[ 1 ] );
        tmpPlaneBuffer.put( 2, oldTexEnvColor[ 2 ] );
        tmpPlaneBuffer.put( 3, oldTexEnvColor[ 3 ] );
        GL11.glTexEnv( GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, tmpPlaneBuffer );
        
        GL11.glTexEnvi( GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, oldTexEnvMode );
        
        return ( 0 ); // (actually this is not 0, but it's not important.)
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int renderAtom( RenderAtom< ? > atom, Object glObj, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        if ( renderMode != RenderMode.NORMAL )
            return ( 0 );
        
        final OpenGLStatesCache statesCache = renderPeer.getStatesCache();
        
        // disable color-material
        if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
        {
            GL11.glDisable( GL11.GL_COLOR_MATERIAL );
            statesCache.colorMaterialEnabled = false;
        }
        
        // disable lighting
        if ( !statesCache.enabled || statesCache.lightingEnabled )
        {
            GL11.glDisable( GL11.GL_LIGHTING );
            statesCache.lightingEnabled = false;
        }
        
        ShapeAtomPeer.setMatrix( view, Transform3D.IDENTITY,//atom.getTransform().getMatrix4f(),
                                 false, true );
        
        Bounds bounds;
        if ( atom.getNode().getBounds().getType() == BoundsType.AABB )
            bounds = boundingBox;
        else
            bounds = boundingSphere;
        
        bounds.set( atom.getNode().getBounds() );
        bounds.transform( atom.getNode().getWorldTransform().getMatrix4f() );
        if ( atom.getNode() instanceof TransformGroup )
        {
            TEMP_MAT.set( ( (TransformGroup)atom.getNode() ).getTransform().getMatrix4f() );
            TEMP_MAT.invert();
            bounds.transform( TEMP_MAT );
        }
        
        return ( drawBounds( bounds, boundsColor_Shape, renderPeer.getStatesCache() ) );
    }
}
