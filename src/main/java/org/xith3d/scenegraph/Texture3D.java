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
package org.xith3d.scenegraph;

import org.jagatoo.opengl.enums.TextureBoundaryMode;
import org.jagatoo.opengl.enums.TextureFormat;
import org.jagatoo.opengl.enums.TextureType;

/**
 * Texture3D defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class Texture3D extends Texture
{
    /**
     * The desired boundary mode R
     */
    private TextureBoundaryMode boundaryModeR = TextureBoundaryMode.WRAP;
    
    /**
     * @return this Texture3D's depth.
     */
    public final int getDepth()
    {
        if ( getImagesCount() == 0 )
            return ( -1 );
        
        TextureImage3D image0 = (TextureImage3D)getImage( 0 );
        
        return ( image0.getDepth() );
    }
    
    /**
     * Sets the boundary mode R.
     */
    public void setBoundaryModeR( TextureBoundaryMode mode )
    {
        boundaryModeR = mode;
    }
    
    /**
     * Gets the boundary mode R.
     */
    public final TextureBoundaryMode getBoundaryModeR()
    {
        return ( boundaryModeR );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void checkImageType( TextureImage image )
    {
        if ( !( image instanceof TextureImage3D ) )
        {
            throw new Error( "Only TextureImage3D instances can be added to a Texture3D." );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        final Texture3D t3dOrig = (Texture3D)original;
        
        this.boundaryModeR = t3dOrig.getBoundaryModeR();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Texture3D cloneNodeComponent( boolean forceDuplicate )
    {
        Texture3D t3d = new Texture3D( this.getFormat() );
        
        t3d.duplicateNodeComponent( this, forceDuplicate );
        
        return ( t3d );
    }
    
    /**
     * Constructs a new Texture3D object.
     * 
     * @param format
     * @param boundaryWidth
     */
    public Texture3D( TextureFormat format, int boundaryWidth )
    {
        super( TextureType.TEXTURE_3D, format, boundaryWidth );
    }
    
    /**
     * Constructs a new Texture3D object.
     * 
     * @param format
     */
    public Texture3D( TextureFormat format )
    {
        super( TextureType.TEXTURE_3D, format );
    }
}
