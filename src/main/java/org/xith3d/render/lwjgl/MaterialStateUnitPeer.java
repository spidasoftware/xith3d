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

import org.openmali.vecmath2.Colorf;

import org.jagatoo.logging.ProfileTimer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
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

/**
 * Handles the shading for material attributes. Materials can be tricky because
 * they can be used in different ways. We are using materials in conjunction
 * with lights, and we are multiplying it with the geometry vertex colors for
 * ambient and diffuse lighting.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class MaterialStateUnitPeer implements StateUnitPeer
{
    private static final FloatBuffer tmpFloatBuffer = BufferUtils.createFloatBuffer( 4 );
    
    public void apply( RenderAtom< ? > atom, StateUnit stateUnit, Object glObj, CanvasPeer canvasPeer, RenderPeer renderPeer, OpenGLCapabilities glCaps, View view, OpenGLStatesCache statesCache, RenderOptions options, long nanoTime, long nanoStep, RenderMode renderMode, long frameId )
    {
        if ( renderMode != RenderMode.NORMAL )
            return;
        
        ProfileTimer.startProfile( X3DLog.LOG_CHANNEL, "MaterialStateUnitPeer::apply()" );
        
        final Material m = ( (MaterialStateUnit)stateUnit ).getMaterial();
        if ( m != null )
        {
            if ( m.isLightingEnabled() && options.isLightingEnabled() )
            {
                X3DLog.debug( "Lighting enabled" );
                
                // enable lighting
                if ( !statesCache.enabled || !statesCache.lightingEnabled )
                {
                    GL11.glEnable( GL11.GL_LIGHTING );
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
                    GL11.glColorMaterial( GL11.GL_FRONT_AND_BACK, m.getColorTarget().toOpenGL() );
                    
                    if ( !statesCache.enabled || !statesCache.colorMaterialEnabled )
                    {
                        GL11.glEnable( GL11.GL_COLOR_MATERIAL );
                        statesCache.colorMaterialEnabled = true;
                    }
                }
                else
                {
                    if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
                    {
                        GL11.glDisable( GL11.GL_COLOR_MATERIAL );
                        statesCache.colorMaterialEnabled = false;
                    }
                }
                
                // set the various material colors
                
                Colorf ambient = m.getAmbientColor();
                tmpFloatBuffer.clear();
                tmpFloatBuffer.put( ambient.getRed() ).put( ambient.getGreen() ).put( ambient.getBlue() ).put( 1.0f );
                tmpFloatBuffer.rewind();
                GL11.glMaterial( GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT, tmpFloatBuffer );
                
                float opaque = 1.0f;
                if ( atom instanceof ShapeAtom )
                {
                    final ShapeAtom shapeAtom = (ShapeAtom)atom;
                    Appearance app = ( (Shape3D)shapeAtom.getNode() ).getAppearance();
                    
                    if ( ( app != null ) && ( app.getTransparencyAttributes() != null ) )
                    {
                        opaque = 1.0f - app.getTransparencyAttributes().getTransparency();
                    }
                }
                
                Colorf diffuse = m.getDiffuseColor();
                tmpFloatBuffer.clear();
                tmpFloatBuffer.put( diffuse.getRed() ).put( diffuse.getGreen() ).put( diffuse.getBlue() ).put( opaque );
                tmpFloatBuffer.rewind();
                GL11.glMaterial( GL11.GL_FRONT_AND_BACK, GL11.GL_DIFFUSE, tmpFloatBuffer );
                
                Colorf emmissive = m.getEmissiveColor();
                tmpFloatBuffer.clear();
                tmpFloatBuffer.put( emmissive.getRed() ).put( emmissive.getGreen() ).put( emmissive.getBlue() ).put( opaque );
                tmpFloatBuffer.rewind();
                GL11.glMaterial( GL11.GL_FRONT_AND_BACK, GL11.GL_EMISSION, tmpFloatBuffer );
                
                Colorf specular = m.getSpecularColor();
                tmpFloatBuffer.clear();
                tmpFloatBuffer.put( specular.getRed() ).put( specular.getGreen() ).put( specular.getBlue() ).put( opaque);
                tmpFloatBuffer.rewind();
                GL11.glMaterial( GL11.GL_FRONT_AND_BACK, GL11.GL_SPECULAR, tmpFloatBuffer );
                
                GL11.glMaterialf( GL11.GL_FRONT_AND_BACK, GL11.GL_SHININESS, m.getShininess() );
                
                if ( m.getNormalizeNormals() && ( !statesCache.enabled || !statesCache.normalizeEnabled ) )
                    GL11.glEnable( GL11.GL_NORMALIZE );
                else if ( !m.getNormalizeNormals() && ( !statesCache.enabled || statesCache.normalizeEnabled ) )
                    GL11.glDisable( GL11.GL_NORMALIZE );
                
                statesCache.normalizeEnabled = m.getNormalizeNormals();
            }
            else
            {
                if ( !statesCache.enabled || statesCache.lightingEnabled )
                {
                    GL11.glDisable( GL11.GL_LIGHTING );
                    statesCache.lightingEnabled = false;
                }
                
                if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
                {
                    GL11.glDisable( GL11.GL_COLOR_MATERIAL );
                    statesCache.colorMaterialEnabled = false;
                }
                
                X3DLog.debug( "Lighting disabled" );
            }
        }
        else
        {
            if ( !statesCache.enabled || statesCache.lightingEnabled )
            {
                GL11.glDisable( GL11.GL_LIGHTING );
                statesCache.lightingEnabled = false;
            }
            
            if ( !statesCache.enabled || statesCache.colorMaterialEnabled )
            {
                GL11.glDisable( GL11.GL_COLOR_MATERIAL );
                statesCache.colorMaterialEnabled = false;
            }
            
            X3DLog.debug( "Lighting disabled" );
        }
        
        ProfileTimer.endProfile();
    }
}
