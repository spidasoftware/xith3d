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

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.render.CanvasPeer;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;

/**
 * @author David J. Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureUnit extends NodeComponent
{
    private Texture texture;
    private TextureAttributes textureAttributes;
    private TexCoordGeneration texCoordGeneration;
    
    public void setTexture( Texture texture )
    {
        this.texture = texture;
        
        this.setChanged( true );
    }
    
    public final void setTexture( String texture )
    {
        setTexture( TextureLoader.getInstance().getTexture( texture ) );
    }
    
    public final Texture getTexture()
    {
        return ( texture );
    }
    
    public void setTextureAttributes( TextureAttributes textureAttributes )
    {
        this.textureAttributes = textureAttributes;
        
        this.setChanged( true );
    }
    
    public final TextureAttributes getTextureAttributes()
    {
        return ( textureAttributes );
    }
    
    public void setTexCoordGeneration( TexCoordGeneration texCoordGeneration )
    {
        this.texCoordGeneration = texCoordGeneration;
        
        this.setChanged( true );
    }
    
    public final TexCoordGeneration getTexCoordGeneration()
    {
        return ( texCoordGeneration );
    }
    
    public void setChangedRecursive( boolean changed )
    {
        setChanged( changed );
        
        if ( this.texture != null )
            this.texture.setChanged( changed );
        if ( this.textureAttributes != null )
            this.textureAttributes.setChanged( changed );
        if ( this.texCoordGeneration != null )
            this.texCoordGeneration.setChanged( changed );
    }
    
    @Override
    public void setModListener( ScenegraphModificationsListener modListener )
    {
        super.setModListener( modListener );
        
        if ( texture != null )
            texture.setModListener( modListener );
        
        if ( textureAttributes != null )
            textureAttributes.setModListener( modListener );
        
        if ( texCoordGeneration != null )
            texCoordGeneration.setModListener( modListener );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        final TextureUnit origTU = (TextureUnit)original;
        
        this.setTexture( origTU.getTexture() );
        
        if ( forceDuplicate )
        {
            if ( origTU.getTextureAttributes() != null )
            {
                this.setTextureAttributes( origTU.getTextureAttributes().cloneNodeComponent( true ) );
            }
            
            if ( origTU.getTexCoordGeneration() != null )
            {
                this.setTexCoordGeneration( origTU.getTexCoordGeneration().cloneNodeComponent( true ) );
            }
        }
        else
        {
            this.setTextureAttributes( origTU.getTextureAttributes() );
            this.setTexCoordGeneration( origTU.getTexCoordGeneration() );
        }
    }
    
    protected TextureUnit newInstance()
    {
        return ( new TextureUnit() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TextureUnit cloneNodeComponent( boolean forceDuplicate )
    {
        TextureUnit tu = newInstance();
        tu.duplicateNodeComponent( this, forceDuplicate );
        
        return ( tu );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( texture != null )
            texture.freeOpenGLResources( canvasPeer );
        
        if ( textureAttributes != null )
            textureAttributes.freeOpenGLResources( canvasPeer );
        
        if ( texCoordGeneration != null )
            texCoordGeneration.freeOpenGLResources( canvasPeer );
    }
    
    public TextureUnit( Texture texture, TextureAttributes textureAttributes, TexCoordGeneration texCoordGeneration )
    {
        super( false );
        
        this.setTexture( texture );
        this.setTextureAttributes( textureAttributes );
        this.setTexCoordGeneration( texCoordGeneration );
    }
    
    public TextureUnit( String texture, TextureAttributes textureAttributes, TexCoordGeneration texCoordGeneration )
    {
        this( TextureLoader.getInstance().getTexture( texture ), textureAttributes, texCoordGeneration );
    }
    
    public TextureUnit( Texture texture, TextureAttributes textureAttributes )
    {
        this( texture, textureAttributes, null );
    }
    
    public TextureUnit( String texture, TextureAttributes textureAttributes )
    {
        this( texture, textureAttributes, null );
    }
    
    public TextureUnit( Texture texture )
    {
        this( texture, null, null );
    }
    
    public TextureUnit( String texture )
    {
        this( texture, null, null );
    }
    
    public TextureUnit()
    {
        this( (Texture)null, null, null );
    }
}
