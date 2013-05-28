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
import org.jagatoo.loaders.models.md2.MD2TagFile;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.ModelLoader;
import org.xith3d.loaders.models.animations.ModelAnimation;
import org.xith3d.scenegraph.TransformGroup;

/**
 * <p>
 * These tools will help you to load MD2-tag files or
 * multipart-MD2-models.
 * </p>
 * <p>
 * The methods in this class are just tool and nothing more.
 * Everything, that is done by these methods can easily be
 * done manually, too.
 * </p>
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MD2Tools
{
    /**
     * Loads an MD2 tag file. A file like that contains all the information
     * to build a mount transform with key frames.
     * 
     * @param tagFileURL
     * @param convertZup2Yup
     * @param scale
     * @param targetModel
     * 
     * @throws IOException
     * @throws IncorrectFormatException
     * @throws ParsingException
     */
    public static void readMD2Tags( URL tagFileURL, boolean convertZup2Yup, float scale, Model targetModel ) throws IOException, IncorrectFormatException, ParsingException
    {
        MD2TagFile tagFile = new MD2TagFile( tagFileURL.openStream(), convertZup2Yup, scale );
        
        String[] tagNames = tagFile.getTagNames();
        Matrix4f[][] matrices = tagFile.getMatrices();
        
        TransformGroup[] mountTransforms = new TransformGroup[ tagFile.getNumTags() ];
        
        for ( int i = 0; i < tagNames.length; i++ )
        {
            String tagName = tagNames[i];
            
            mountTransforms[i] = new TransformGroup( matrices[0][i] );
            mountTransforms[i].setName( tagName );
        }
        
        targetModel.setMountTransforms( mountTransforms );
        
        int offset = 0;
        for ( int i = 0; i < targetModel.getAnimationsCount(); i++ )
        {
            ModelAnimation anim = targetModel.getAnimation( i );
            
            if ( ( offset == 0 ) && ( matrices.length == anim.getNumFrames() ) )
            {
                anim.setMountTransformFrames( matrices );
            }
            else
            {
                Matrix4f[][] matrices2 = new Matrix4f[ anim.getNumFrames() ][];
                System.arraycopy( matrices, offset, matrices2, 0, anim.getNumFrames() );
            }
            
            offset += anim.getNumFrames();
        }
    }
    
    /**
     * Loads an MD2 tag file. A file like that contains all the information
     * to build a mount transform with key frames.
     * 
     * @param tagFileURL
     * @param targetModel
     * 
     * @throws IOException
     * @throws IncorrectFormatException
     * @throws ParsingException
     */
    public static void readMD2Tags( URL tagFile, Model targetModel ) throws IOException, IncorrectFormatException, ParsingException
    {
        readMD2Tags( tagFile, true, 1.0f, targetModel );
    }
    
    /**
     * <p>
     * This method loads a model from <b>up to</b> three parts: body, head and weapon.
     * </p>
     * <p>
     * Head and weapon URLs can be null, which makes them be ignored.
     * </p>
     * 
     * @param bodyURL
     * @param bodySkin
     * @param bodyTagFile
     * @param headMount
     * @param headURL
     * @param headSkin
     * @param weaponMount
     * @param weaponURL
     * @param weaponSkin
     * @param scale
     * 
     * @return a Model composed of up to three parts.
     * 
     * @throws IOException
     * @throws IncorrectFormatException
     * @throws ParsingException
     */
    public static Model loadMultipartMD2Model( URL bodyURL, String bodySkin, URL bodyTagFile, String headMount, URL headURL, String headSkin, String weaponMount, URL weaponURL, String weaponSkin, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        ModelLoader loader = ModelLoader.getInstance();
        boolean convertZup2Yup = loader.getFlag( ModelLoader.ALWAYS_CONVERT_Z_UP_TO_Y_UP ) || loader.getFlag( ModelLoader.CONVERT_Z_UP_TO_Y_UP_IF_EXPECTED );
        
        Model body = loader.loadModel( bodyURL, bodySkin, scale );
        
        readMD2Tags( bodyTagFile, convertZup2Yup, scale, body );
        
        if ( headURL != null )
        {
            Model head = loader.loadModel( headURL, headSkin, scale );
            
            if ( head.hasAnimations() )
            {
                body.getAnimations()[0].attachAnimation( head.getAnimations()[0] );
            }
            
            TransformGroup mt = body.getMountTransform( headMount );
            if ( mt != null )
            {
                mt.addChild( head );
            }
        }
        
        if ( weaponURL != null )
        {
            Model weapon = loader.loadModel( weaponURL, weaponSkin, scale );
            
            if ( weapon.hasAnimations() )
            {
                body.getAnimations()[0].attachAnimation( weapon.getAnimations()[0] );
            }
            
            TransformGroup mt2 = body.getMountTransform( weaponMount );
            if ( mt2 != null )
            {
                mt2.addChild( weapon );
            }
        }
        
        return ( body );
    }
    
    /**
     * <p>
     * This method loads a model from <b>up to</b> three parts: body, head and weapon.
     * </p>
     * <p>
     * Head and weapon URLs can be null, which makes them be ignored.
     * </p>
     * 
     * @param bodyURL
     * @param bodySkin
     * @param bodyTagFile
     * @param headURL
     * @param headSkin
     * @param weaponURL
     * @param weaponSkin
     * @param scale
     * 
     * @return a Model composed of up to three parts.
     * 
     * @throws IOException
     * @throws IncorrectFormatException
     * @throws ParsingException
     */
    public static Model loadMultipartMD2Model( URL bodyURL, String bodySkin, URL bodyTagFile, URL headURL, String headSkin, URL weaponURL, String weaponSkin, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        /*
         * tag_head
         * tag_lweapon
         * tag_rweapon
         */
        
        return ( loadMultipartMD2Model( bodyURL, bodySkin, bodyTagFile, "tag_head", headURL, headSkin, "tag_rweapon", weaponURL, weaponSkin, scale ) );
    }
    
    /**
     * <p>
     * This method loads a model from <b>up to</b> three parts: body, head and weapon.
     * </p>
     * <p>
     * Head and weapon URLs can be null, which makes them be ignored.
     * </p>
     * 
     * @param bodyURL
     * @param bodySkin
     * @param bodyTagFile
     * @param headURL
     * @param headSkin
     * @param weaponURL
     * @param weaponSkin
     * 
     * @return a Model composed of up to three parts.
     * 
     * @throws IOException
     * @throws IncorrectFormatException
     * @throws ParsingException
     */
    public static Model loadMultipartMD2Model( URL bodyURL, String bodySkin, URL bodyTagFile, URL headURL, String headSkin, URL weaponURL, String weaponSkin ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadMultipartMD2Model( bodyURL, bodySkin, bodyTagFile, headURL, headSkin, weaponURL, weaponSkin, 1.0f ) );
    }
}
