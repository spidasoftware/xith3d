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
package org.xith3d.ui.hud.base;

import org.openmali.types.twodee.Sized2fRO;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture;

/**
 * Any Widget class implementing this interface is able to receive and display
 * background Textures.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface BackgroundSettable
{
    /**
     * Sets the background color of the Widget.
     * 
     * @param color the color to use
     */
    public void setBackground( Colorf color );
    
    /**
     * Sets the background texture of the Widget.
     * 
     * @param texture the texture resource to use
     */
    public void setBackground( Texture texture );
    
    /**
     * Sets the background Texture of the Widget.
     * 
     * @param texture the texture resource to use
     */
    public void setBackground( String texture );
    
    /**
     * @return the background Texture of the WidgetContainer.
     */
    public Texture getBackground();
    
    /**
     * @return the background color of the Widget.
     */
    public Colorf getBackgroundColor();
    
    /**
     * Sets the background tile size.<br>
     * Use any negative value for no tiling.
     * 
     * @param tileWidth the tile width (or negative for no tiling of width)
     * @param tileHeight the tile height (or negative for no tiling of height)
     */
    public void setBackgroundTileSize( float tileWidth, float tileHeight );
    
    /**
     * Sets the background tile size.<br>
     * Use any negative value for no tiling.
     * 
     * @param tileSize the tile size (or <i>null</i> for no tiling)
     */
    public void setBackgroundTileSize( Sized2fRO tileSize );
    
    /**
     * @return the background tile size (or <i>null</i> for no tiling)
     */
    public Sized2fRO getBackgroundTileSize();
    
    /**
     * @return the background tile width (or negative for no tiling)
     */
    public float getBackgroundTileWidth();
    
    /**
     * @return the background tile width (or negative for no tiling)
     */
    public float getBackgroundTileHeight();
}
