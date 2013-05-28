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
package org.xith3d.resources;

import java.util.Collection;

import org.xith3d.loaders.models.Model;
import org.xith3d.scenegraph.Shader;
import org.xith3d.scenegraph.Texture;
import org.xith3d.sound.SoundContainer;

/**
 * This is a "shortcut" for the singleton instance of ResourceBag.
 * 
 * @see ResourceBag
 * @see ResourceBag#setSingletonInstance(ResourceBag)
 * @see ResourceBag#getInstance()
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ResBag
{
    /**
     * Retrieves the given Texture resource from the ResourceBag.
     * 
     * @param name the name to use as key
     * 
     * @return the retrieved Texture resource or <i>null</i>, if the name doesn't exist in the bag.
     */
    public static Texture getTexture( String name )
    {
        return ( ResourceBag.getInstance().getTexture( name ) );
    }
    
    /**
     * @return a Collection of all Textures stored in the ResourceBag
     */
    public static Collection< Texture > getTextures()
    {
        return ( ResourceBag.getInstance().getTextures() );
    }
    
    /**
     * @return the number of Texture resources stored in this ResourceBag
     */
    public static int numTextures()
    {
        return ( ResourceBag.getInstance().numTextures() );
    }
    
    /**
     * Retrieves the given Model resource from the ResourceBag.
     * 
     * @param name the name to use as key
     * 
     * @return the retrieved Model resource or <i>null</i>, if the name doesn't exist in the bag.
     */
    public static Model getModel( String name )
    {
        return ( ResourceBag.getInstance().getModel( name ) );
    }
    
    /**
     * Retrieves a new shared instance of the given Model resource from the ResourceBag.
     * 
     * @param name the name to use as key
     * 
     * @return a new shared instance of the Model resource or <i>null</i>, if the name doesn't exist in the bag.
     */
    public static Model getModelInstance( String name )
    {
        return ( ResourceBag.getInstance().getModelInstance( name ) );
    }
    
    /**
     * @return a Collection of all Models stored in the ResourceBag
     */
    public static Collection< Model > getModels()
    {
        return ( ResourceBag.getInstance().getModels() );
    }
    
    /**
     * @return the number of Model resources stored in this ResourceBag
     */
    public static int numModels()
    {
        return ( ResourceBag.getInstance().numModels() );
    }
    
    /**
     * Retrieves the given Sound resource from the ResourceBag.
     * 
     * @param name the name to use as key
     * 
     * @return the retrieved Sound resource or <i>null</i>, if the name doesn't exist in the bag.
     */
    public static SoundContainer getSound( String name )
    {
        return ( ResourceBag.getInstance().getSound( name ) );
    }
    
    /**
     * @return a Collection of all Sounds stored in the ResourceBag
     */
    public static Collection< SoundContainer > getSounds()
    {
        return ( ResourceBag.getInstance().getSounds() );
    }
    
    /**
     * @return the number of Sound resources stored in this ResourceBag
     */
    public static int numSounds()
    {
        return ( ResourceBag.getInstance().numSounds() );
    }
    
    /**
     * Retrieves the given Shader resource from the ResourceBag.
     * 
     * @param name the name to use as key
     * 
     * @return the retrieved Shader resource or <i>null</i>, if the name doesn't exist in the bag.
     */
    public static Shader getShader( String name )
    {
        return ( ResourceBag.getInstance().getShader( name ) );
    }
    
    /**
     * @return a Collection of all Shaders stored in the ResourceBag
     */
    public static Collection< Shader > getShaders()
    {
        return ( ResourceBag.getInstance().getShaders() );
    }
    
    /**
     * @return the number of Texture resources stored in this ResourceBag
     */
    public static int numShaders()
    {
        return ( ResourceBag.getInstance().numShaders() );
    }
    
    /**
     * @return the number of Texture resources stored in this ResourceBag
     */
    public static int numResources()
    {
        return ( ResourceBag.getInstance().numResources() );
    }
    
    /**
     * Creates a new ResourceBag.
     */
    private ResBag()
    {
    }
}
