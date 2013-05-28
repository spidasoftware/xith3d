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
package org.xith3d.loaders.texture;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.jagatoo.loaders.textures.locators.TextureStreamLocator;

/**
 * Locates a Texture from a ZipInputStream.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureStreamLocatorZip implements TextureStreamLocator
{
    private URL in;
    private String basePath;
    private Set< String > nameCache;
    
    public String getBaseDirName()
    {
        return ( "zip-file" );
    }
    
    public InputStream openTextureStream( String name )
    {
        if ( !nameCache.contains( name ) )
        {
            return ( null );
        }
        
        try
        {
            ZipInputStream zipIn = new ZipInputStream( in.openStream() );
            
            ZipEntry en;
            while ( ( en = zipIn.getNextEntry() ) != null )
            {
                if ( en.getName().equals( basePath + name ) )
                {
                    //new InputStream().
                    return ( zipIn );
                }
            }
            
            return ( null );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
            return ( null );
        }
    }
    
    private void cacheNames()
    {
        try
        {
            nameCache.clear();
            
            ZipInputStream zipIn = new ZipInputStream( in.openStream() );
            
            ZipEntry en;
            while ( ( en = zipIn.getNextEntry() ) != null )
            {
                nameCache.add( en.getName().substring( basePath.length() ) );
            }
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
    
    public TextureStreamLocatorZip( URL in, String basePath )
    {
        this.in = in;
        this.basePath = basePath;
        if ( ( basePath.length() > 0 ) && ( !basePath.endsWith( "/" ) ) )
            this.basePath += "/";
        
        nameCache = new HashSet< String >();
        cacheNames();
    }
}
