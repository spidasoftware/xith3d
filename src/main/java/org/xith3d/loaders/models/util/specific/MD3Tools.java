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
package org.xith3d.loaders.models.util.specific;

import java.io.IOException;
import java.net.URL;

import org.jagatoo.loaders.IncorrectFormatException;
import org.jagatoo.loaders.ParsingException;
import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.ModelLoader;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TransformGroup;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MD3Tools
{
    /**
     * 
     * @param legs
     * @param torsoMount
     * @param torso
     * @param headMount
     * @param head
     * @param weapon
     * @param skin
     * @param material
     * @param scale
     * 
     * @return the multipart Model.
     * 
     * @throws IOException
     * @throws IncorrectFormatException
     * @throws ParsingException
     */
    public static Model loadMultipartModel( URL legs, String torsoMount, URL torso, String headMount, URL head, URL weapon, String skin, Material material, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        ModelLoader loader = ModelLoader.getInstance();
        
        Texture skinTex = null;
        if ( skin != null )
            skinTex = TextureLoader.getInstance().getTexture( skin );
        
        Model mdlLegs = loader.loadModel( legs, "", scale );
        
        if ( skinTex != null )
            mdlLegs.getShape( 0 ).getAppearance().setTexture( skinTex );
        mdlLegs.getShape( 0 ).getAppearance().setMaterial( material );
        
        if ( torso != null )
        {
            Model mdlTorso = loader.loadModel( torso, "", scale );
            
            if ( skinTex != null )
                mdlTorso.getShape( 0 ).getAppearance().setTexture( skinTex );
            mdlTorso.getShape( 0 ).getAppearance().setMaterial( material );
            
            if ( mdlTorso.hasAnimations() )
            {
                mdlLegs.getAnimations()[0].attachAnimation( mdlTorso.getAnimations()[0] );
            }
            
            /*
            for ( TransformGroup mt : mdlFeet.getMountTransforms() )
            {
                System.out.println( mt );
                System.out.println( mt.getTransform() );
            }
            */
            
            {
                if ( torsoMount == null )
                    torsoMount = "tag_torso";
                
                TransformGroup mt = mdlLegs.getMountTransform( torsoMount );
                if ( mt != null )
                {
                    mt.addChild( mdlTorso );
                }
            }
            
            if ( head != null )
            {
                Model mdlHead = loader.loadModel( head, "", scale );
                
                if ( skinTex != null )
                    mdlHead.getShape( 0 ).getAppearance().setTexture( skinTex );
                mdlHead.getShape( 0 ).getAppearance().setMaterial( material );
                
                if ( mdlHead.hasAnimations() )
                {
                    mdlLegs.getAnimations()[0].attachAnimation( mdlHead.getAnimations()[0] );
                }
                
                {
                    if ( headMount == null )
                        headMount = "tag_head";
                    
                    TransformGroup mt = mdlTorso.getMountTransform( "tag_head" );
                    if ( mt != null )
                    {
                        mt.addChild( mdlHead );
                    }
                }
            }
        }
        
        return ( mdlLegs );
    }
    
    public static Model loadMultipartModel( URL legs, String torsoMount, URL torso, String headMount, URL head, URL weapon, String skin, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadMultipartModel( legs, torsoMount, torso, headMount, head, weapon, skin, null, scale ) );
    }
}
