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
package org.xith3d.ui.hud.theming;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jagatoo.loaders.textures.locators.TextureStreamLocatorFile;
import org.xith3d.loaders.texture.TextureLoader;

/**
 * The TestTheme class is a simple {@link WidgetTheme} extension, that allows
 * for using Theme data directly from the filesystem instead of an .xwt file.
 * This is simpler at theme development time. But should be avoided for
 * released Themes. Themes should always be released within an -xwt file!
 * 
 * @author Marvin Froehlich (aka Qudus)
 * 
 * @deprecated marked deprecated to remind you to replace this with a real .xwt file.
 */
@Deprecated
public class TestTheme extends WidgetTheme
{
    /**
     * Create a new {@link TestTheme}.
     * 
     * @param folder the folder, where the "theme.properties" file resists in.
     * 
     * @throws IOException
     */
    public TestTheme( String folder ) throws IOException
    {
        super( new ThemeProperties( new FileInputStream( folder + "/theme.properties" ) ) );
        
        TextureLoader.getInstance().addTextureStreamLocator( new TextureStreamLocatorFile( folder + "textures/" ) );
    }
    
    /**
     * Create a new {@link TestTheme}.
     * 
     * @param folder the folder, where the "theme.properties" file resists in.
     * 
     * @throws IOException
     */
    public TestTheme( File folder ) throws IOException
    {
        this( folder.getAbsolutePath() );
    }
}
