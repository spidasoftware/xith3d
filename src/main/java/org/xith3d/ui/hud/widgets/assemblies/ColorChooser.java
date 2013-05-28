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
package org.xith3d.ui.hud.widgets.assemblies;

import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture2DCanvas;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.widgets.Image;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ColorChooser extends Widget
{
    private final Image image;
    private final Texture2D texture;
    private final Texture2DCanvas texCanvas;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void drawWidget( Texture2DCanvas texCanvas, int offsetX, int offsetY, int width, int height, boolean drawsSelf )
    {
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void init()
    {
        
    }
    
    private static void createColorTriangle( Texture2DCanvas texCanvas )
    {
        texCanvas.beginUpdateRegionComplete();
        
        texCanvas.getImage().clear( Colorf.BLACK_TRANSPARENT );
        
        texCanvas.finishUpdateRegion();
    }
    
    public ColorChooser( boolean isHeavyWeight, float width, float height, boolean withAlpha )
    {
        super( isHeavyWeight, true, width, height );
        
        if ( withAlpha )
            this.texture = Texture2D.createDrawTexture( TextureFormat.RGBA, (int)width, (int)height, true );
        else
            this.texture = Texture2D.createDrawTexture( TextureFormat.RGB, (int)width, (int)height, true );
        this.texCanvas = texture.getTextureCanvas();
        
        createColorTriangle( texCanvas );
        
        this.image = new Image( width, height, texture );
        
        getWidgetAssembler().addWidget( image );
    }
}
