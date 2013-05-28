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
package org.xith3d.render.jsr231;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import org.jagatoo.logging.ProfileTimer;
import org.openmali.vecmath2.Colorf;

import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.OpenGLStatesCache;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPeer;
import org.xith3d.render.RenderPeer.RenderMode;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.preprocessing.ShapeAtom;
import org.xith3d.render.states.StateUnit;
import org.xith3d.render.states.units.MaterialStateUnit;
import org.xith3d.render.states.units.StateUnitPeer;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

import com.sun.opengl.util.BufferUtil;

/**
 * Handles the shading for material attributes. Materials can be tricky because
 * they can be used in different ways. We are using materials in conjunction
 * with lights, and we are multiplying it with the geometry vertex colors for
 * ambient and diffuse lighting.
 * 
 * @author David Yazel
 * @author Lilian Chamontin [jsr231 port]
 * @author Marvin Froehlich (aka Qudus)
 */
public class MaterialStateUnitPeer implements StateUnitPeer
{
    private FloatBuffer tempBuffer = BufferUtil.newFloatBuffer( 4 );
    
    public void apply( RenderAtom< ? > atom, StateUnit stateUnit, Object glObj, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, OpenGLStatesCache statesCache, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        if ( renderMode != RenderMode.NORMAL )
            return;
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "MaterialStateUnitPeer::apply()" );
        
        final GL gl = (GL)glObj;
        
        final Material m = ( (MaterialStateUnit)stateUnit ).getMaterial();
        if ( m != null )
        {
            if ( m.isLightingEnabled() && options.isLightingEnabled() )
            {
                X3DLog.debug( "Lighting enabled" );
                
                // enable lighting
                
                if ( !statesCache.enabled || !statesCache.lightingEnabled )
                {
                    gl.glEnable( GL.GL_LIGHTING );
                    statesCache.lightingEnabled = true;
                }
                
                /*
                 * Set the coloring material to be used.
                 * Should be set before setting the other material colors,
                 * because of otherwise appropriate part may be ignored due to
                 * old GL_COLOR_MATERIAL settings
                 */
                if ( m.getColorTarget() != Material.NONE )
                {
                    gl.glColorMaterial( GL.GL_FRONT_AND_BACK, m.getColorTarget().toOpenGL() );
                    if ( !statesCache.enabled || !statesCache.colorMaterialEnabled )
                    {
                        gl.glEnable( GL.GL_COLOR_MATERIAL );
                        statesCache.colorMaterialEnabled = true;
                    }
                }
                else
                {
                    if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
                    {
                        gl.glDisable( GL.GL_COLOR_MATERIAL );
                        statesCache.colorMaterialEnabled = false;
                    }
                }
                
                // set the various material colors

                float opaque = 1.0f;
                if ( atom instanceof ShapeAtom )
                {
                    final ShapeAtom shapeAtom = (ShapeAtom)atom;
                    final Appearance app = ( (Shape3D)shapeAtom.getNode() ).getAppearance();

                    if ( ( app != null ) && ( app.getTransparencyAttributes() != null ) )
                    {
                        opaque = 1.0f - app.getTransparencyAttributes().getTransparency();
                    }
                }
                // float buffer[] = new float[4];
                // buffer[3] = 1f;
                Colorf color = m.getAmbientColor();
                tempBuffer.put( color.getRed() );
                tempBuffer.put( color.getGreen() );
                tempBuffer.put( color.getBlue() );
                tempBuffer.put( opaque );
                tempBuffer.rewind();
                
                // m.getAmbientColor().get(buffer);
                gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, tempBuffer );
                color = m.getDiffuseColor();
                tempBuffer.put( color.getRed() );
                tempBuffer.put( color.getGreen() );
                tempBuffer.put( color.getBlue() );
                tempBuffer.put( opaque );
                tempBuffer.rewind();

                
                // m.getDiffuseColor().get(buffer);
                gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, tempBuffer );
                color = m.getEmissiveColor();
                tempBuffer.put( color.getRed() );
                tempBuffer.put( color.getGreen() );
                tempBuffer.put( color.getBlue() );
                tempBuffer.put( opaque );
                tempBuffer.rewind();
                // m.getEmissiveColor().get(buffer);
                gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_EMISSION, tempBuffer );
                
                // set the specular color
                color = m.getSpecularColor();
                tempBuffer.put( color.getRed() );
                tempBuffer.put( color.getGreen() );
                tempBuffer.put( color.getBlue() );
                tempBuffer.put( opaque );
                tempBuffer.rewind();
                // m.getSpecularColor().get(buffer);
                gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, tempBuffer );
                
                gl.glMaterialf( GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, m.getShininess() );
                
                if ( m.getNormalizeNormals() && ( !statesCache.enabled || !statesCache.normalizeEnabled ) )
                    gl.glEnable( GL.GL_NORMALIZE );
                else if ( !m.getNormalizeNormals() && ( !statesCache.enabled || statesCache.normalizeEnabled ) )
                    gl.glDisable( GL.GL_NORMALIZE );
                
                statesCache.normalizeEnabled = m.getNormalizeNormals();
            }
            else
            {
                if ( !statesCache.enabled || statesCache.lightingEnabled )
                {
                    gl.glDisable( GL.GL_LIGHTING );
                    statesCache.lightingEnabled = false;
                }
                
                if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
                {
                    gl.glDisable( GL.GL_COLOR_MATERIAL );
                    statesCache.colorMaterialEnabled = false;
                }
                
                X3DLog.debug( "Lighting disabled" );
            }
        }
        else
        {
            if ( !statesCache.enabled || statesCache.lightingEnabled )
            {
                gl.glDisable( GL.GL_LIGHTING );
                statesCache.lightingEnabled = false;
            }
            
            if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
            {
                gl.glDisable( GL.GL_COLOR_MATERIAL );
                statesCache.colorMaterialEnabled = false;
            }
            
            X3DLog.debug( "Lighting disabled" );
        }
        
        ProfileTimer.endProfile();
    }
}
