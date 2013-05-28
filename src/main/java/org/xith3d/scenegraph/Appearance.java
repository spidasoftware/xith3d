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

import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.openmali.vecmath2.Colorf;

import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.textureprojection.TextureProjectionFactory;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;

/**
 * Appearance is a component object of a Shape3D node that defines all rendering
 * state attributes for that shape node.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Florian Hofmann (aka Goliat) [GLSL Shader support]
 * @author Amos Wenger (aka BlueSky) [Convenience methods]
 */
public class Appearance extends NodeComponent
{
    /**
     * The used ShaderProgram, that bypasses the GL fixed function pipeline.
     */
    private ShaderProgramContext< ? > shaderProgramContext = null;
    
    /**
     * The desired transparency attribute properties.
     */
    private TransparencyAttributes transparencyAttrs = null;
    
    /**
     * The desired material properties used for lighting.
     */
    private Material material = null;
    
    /**
     * The desired coloring attribute properties.
     */
    private ColoringAttributes coloringAttrs = null;
    
    /**
     * The desired rendering attribute properties.
     */
    private RenderingAttributes renderingAttrs = null;
    
    /**
     * The desired polygon attribute properties.
     */
    private PolygonAttributes polygonAttrs = null;
    
    /**
     * The desired line attribute properties.
     */
    private LineAttributes lineAttrs = null;
    
    /**
     * The desired point attribute properties.
     */
    private PointAttributes pointAttrs = null;
    
    private TextureUnit[] textureUnits = null;
    
    private static long nextChangeId = 1L;
    
    /**
     * Flag for appearance change
     */
    private long changeID = 0L;
    
    private static boolean defaultStaticApp = false;
    
    private boolean staticApp = defaultStaticApp;
    
    private boolean staticDirty = true;
    
    public static final void setDefaultStatic( boolean b )
    {
        defaultStaticApp = b;
    }
    
    public static final boolean isDefaultStatic()
    {
        return ( defaultStaticApp );
    }
    
    public final void setStatic( boolean b )
    {
        this.staticApp = b;
    }
    
    public final boolean isStatic()
    {
        return ( staticApp );
    }
    
    public void markStaticDirty()
    {
        this.staticDirty = true;
    }
    
    final void markStaticClean()
    {
        this.staticDirty = false;
    }
    
    public final boolean isStaticDirty()
    {
        return ( staticDirty );
    }
    
    @Override
    public void setModListener( ScenegraphModificationsListener modListener )
    {
        super.setModListener( modListener );
        
        if ( shaderProgramContext != null )
            shaderProgramContext.setModListener( modListener );
        if ( transparencyAttrs != null )
            transparencyAttrs.setModListener( modListener );
        if ( material != null )
            material.setModListener( modListener );
        if ( coloringAttrs != null )
            coloringAttrs.setModListener( modListener );
        if ( renderingAttrs != null )
            renderingAttrs.setModListener( modListener );
        if ( polygonAttrs != null )
            polygonAttrs.setModListener( modListener );
        if ( lineAttrs != null )
            lineAttrs.setModListener( modListener );
        if ( pointAttrs != null )
            pointAttrs.setModListener( modListener );
        if ( textureUnits != null )
        {
            for ( int i = 0; i < textureUnits.length; i++ )
            {
                if ( textureUnits[ i ] != null )
                    textureUnits[ i ].setModListener( modListener );
            }
        }
    }
    
    /**
     * Sets the material information.
     */
    public final void setMaterial( Material material )
    {
        this.material = material;
        setChanged( true );
        if ( material != null )
            material.setModListener( getModListener() );
    }
    
    /**
     * @return the material information.
     */
    public final Material getMaterial()
    {
        return ( material );
    }
    
    /**
     * @param forceExistence if this appearance has no material, create one and
     *            return it.
     * @return the material information, or create one if necessary
     */
    public final Material getMaterial( boolean forceExistence )
    {
        if ( forceExistence && ( getMaterial() == null ) )
        {
            setMaterial( new Material() );
        }
        
        return ( getMaterial() );
    }
    
    /**
     * Sets the Texture of the given TextureUnit.
     * 
     * @param unit
     * @param texture
     */
    public final void setTexture( int unit, Texture texture )
    {
        if ( this.textureUnits == null )
        {
            this.textureUnits = new TextureUnit[ unit + 1 ];
            this.textureUnits[ unit ] = new TextureUnit( texture );
        }
        else if ( this.textureUnits.length <= unit )
        {
            TextureUnit[] textureUnits = new TextureUnit[ unit + 1 ];
            
            System.arraycopy( this.textureUnits, 0, textureUnits, 0, this.textureUnits.length );
            
            textureUnits[ unit ] = new TextureUnit( texture );
            
            this.textureUnits = textureUnits;
        }
        else if ( this.textureUnits[ unit ] == null )
        {
            this.textureUnits[ unit ] = new TextureUnit( texture );
        }
        else
        {
            this.textureUnits[ unit ].setTexture( texture );
        }
        
        if ( texture != null )
            texture.setChanged( true );
        
        setChanged( true );
        
        if ( texture != null )
            texture.setModListener( getModListener() );
    }
    
    /**
     * Sets the Texture of the first (#0) TextureUnit.
     * 
     * @param texture
     */
    public final void setTexture( Texture texture )
    {
        setTexture( 0, texture );
    }
    
    /**
     * Sets the Texture of the first (#0) TextureUnit.
     * 
     * @param texture
     */
    public final void setTexture( int unit, String texture )
    {
        if ( texture == null )
            setTexture( unit, (Texture)null );
        else
            setTexture( unit, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Sets the Texture of the first (#0) TextureUnit.
     * 
     * @param texture
     */
    public final void setTexture( String texture )
    {
        if ( texture == null )
            setTexture( 0, (Texture)null );
        else
            setTexture( 0, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * @return the Texture of the requested TextureUnit.
     * 
     * @param unit
     */
    public final Texture getTexture( int unit )
    {
        if ( ( textureUnits == null ) || ( textureUnits.length <= unit ) || ( textureUnits[ unit ] == null ) )
            return ( null );
        
        return ( textureUnits[ unit ].getTexture() );
    }
    
    /**
     * @return the Texture of the first (#0) TextureUnit.
     */
    public final Texture getTexture()
    {
        return ( getTexture( 0 ) );
    }
    
    /**
     * Sets the TextureAttributes of the given TextureUnit.
     * 
     * @param unit
     * @param textureAttribs
     */
    public final void setTextureAttributes( int unit, TextureAttributes textureAttribs )
    {
        if ( this.textureUnits == null )
        {
            this.textureUnits = new TextureUnit[ unit + 1 ];
            this.textureUnits[ unit ] = new TextureUnit( (Texture)null, textureAttribs );
        }
        else if ( this.textureUnits.length <= unit )
        {
            TextureUnit[] textureUnits = new TextureUnit[ unit + 1 ];
            
            System.arraycopy( this.textureUnits, 0, textureUnits, 0, this.textureUnits.length );
            
            textureUnits[ unit ] = new TextureUnit( (Texture)null, textureAttribs );
            
            this.textureUnits = textureUnits;
        }
        else if ( this.textureUnits[ unit ] == null )
        {
            this.textureUnits[ unit ] = new TextureUnit( (Texture)null, textureAttribs );
        }
        else
        {
            this.textureUnits[ unit ].setTextureAttributes( textureAttribs );
        }
        
        if ( textureAttribs != null )
            textureAttribs.setChanged( true );
        
        setChanged( true );
        
        if ( textureAttribs != null )
            textureAttribs.setModListener( getModListener() );
    }
    
    /**
     * Sets the TextureAttributes of the first (#0) TextureUnit.
     * 
     * @param textureAttribs
     */
    public final void setTextureAttributes( TextureAttributes textureAttribs )
    {
        setTextureAttributes( 0, textureAttribs );
    }
    
    /**
     * @return the TextureAttributes of the requested TextureUnit.
     * 
     * @param unit
     */
    public final TextureAttributes getTextureAttributes( int unit )
    {
        if ( ( textureUnits == null ) || ( textureUnits.length <= unit ) || ( textureUnits[ unit ] == null ) )
            return ( null );
        
        return ( textureUnits[ unit ].getTextureAttributes() );
    }
    
    /**
     * @return the TextureAttributes of the requested TextureUnit.
     * 
     * @param unit
     */
    public final TextureAttributes getTextureAttributes( int unit, boolean forceExistence )
    {
        if ( ( getTextureAttributes( unit ) == null ) && ( forceExistence ) )
        {
            setTextureAttributes( unit, new TextureAttributes() );
        }
        
        return ( getTextureAttributes( unit ) );
    }
    
    /**
     * @return the TextureAttributes of the first (#0) TextureUnit.
     */
    public final TextureAttributes getTextureAttributes()
    {
        return ( getTextureAttributes( 0 ) );
    }
    
    /**
     * @return the TextureAttributes of the first (#0) TextureUnit.
     */
    public final TextureAttributes getTextureAttributes( boolean forceExistence )
    {
        if ( ( getTextureAttributes() == null ) && ( forceExistence ) )
        {
            setTextureAttributes( new TextureAttributes() );
        }
        
        return ( getTextureAttributes() );
    }
    
    /**
     * Sets the TexCoordGeneration of the given TextureUnit.
     * 
     * @param unit
     * @param texCoordGen
     */
    public final void setTexCoordGeneration( int unit, TexCoordGeneration texCoordGen )
    {
        if ( this.textureUnits == null )
        {
            this.textureUnits = new TextureUnit[ unit + 1 ];
            this.textureUnits[ unit ] = new TextureUnit( (Texture)null, null, texCoordGen );
        }
        else if ( this.textureUnits.length <= unit )
        {
            TextureUnit[] textureUnits = new TextureUnit[ unit + 1 ];
            
            System.arraycopy( this.textureUnits, 0, textureUnits, 0, this.textureUnits.length );
            
            textureUnits[ unit ] = new TextureUnit( (Texture)null, null, texCoordGen );
            
            this.textureUnits = textureUnits;
        }
        else if ( this.textureUnits[ unit ] == null )
        {
            this.textureUnits[ unit ] = new TextureUnit( (Texture)null, null, texCoordGen );
        }
        else
        {
            this.textureUnits[ unit ].setTexCoordGeneration( texCoordGen );
        }
        
        if ( texCoordGen != null )
            texCoordGen.setChanged( true );
        
        setChanged( true );
        
        if ( texCoordGen != null )
            texCoordGen.setModListener( getModListener() );
    }
    
    /**
     * Sets the TexCoordGeneration of the first (#0) TextureUnit.
     * 
     * @param texCoordGen
     */
    public final void setTexCoordGeneration( TexCoordGeneration texCoordGen )
    {
        setTexCoordGeneration( 0, texCoordGen );
    }
    
    /**
     * @return the TexCoordGeneration of the requested TextureUnit.
     * 
     * @param unit
     */
    public final TexCoordGeneration getTexCoordGeneration( int unit )
    {
        if ( ( textureUnits == null ) || ( textureUnits.length <= unit ) || ( textureUnits[ unit ] == null ) )
            return ( null );
        
        return ( textureUnits[ unit ].getTexCoordGeneration() );
    }
    
    /**
     * @return the TexCoordGeneration of the requested TextureUnit.
     * 
     * @param unit
     */
    public final TexCoordGeneration getTexCoordGeneration( int unit, boolean forceExistence )
    {
        if ( ( getTexCoordGeneration( unit ) == null ) && ( forceExistence ) )
        {
            setTexCoordGeneration( unit, new TexCoordGeneration() );
        }
        
        return ( getTexCoordGeneration( unit ) );
    }
    
    /**
     * @return the TexCoordGeneration of the first (#0) TextureUnit.
     */
    public final TexCoordGeneration getTexCoordGeneration()
    {
        return ( getTexCoordGeneration( 0 ) );
    }
    
    /**
     * @return the TexCoordGeneration of the first (#0) TextureUnit.
     */
    public final TexCoordGeneration getTexCoordGeneration( boolean forceExistence )
    {
        if ( ( getTexCoordGeneration() == null ) && ( forceExistence ) )
        {
            setTexCoordGeneration( new TexCoordGeneration() );
        }
        
        return ( getTexCoordGeneration() );
    }
    
    private static final void applyProjTU( Appearance app, ProjectiveTextureUnit projTU )
    {
        final EffectFactory effFact = EffectFactory.getInstance();
        if ( effFact != null )
        {
            final TextureProjectionFactory texProjFact = effFact.getTextureProjectionFactory();
            if ( texProjFact != null )
                texProjFact.onProjectiveTextureApplied( app, projTU );
        }
    }
    
    public final void setTextureUnits( TextureUnit... textureUnits )
    {
        if ( this.textureUnits != null )
        {
            for ( int i = 0; i < this.textureUnits.length; i++ )
            {
                final TextureUnit tu = this.textureUnits[ i ];
                
                if ( tu instanceof ProjectiveTextureUnit )
                {
                    applyProjTU( null, (ProjectiveTextureUnit)tu );
                }
                
                tu.setModListener( null );
            }
        }
        
        if ( textureUnits == null )
        {
            this.textureUnits = null;
        }
        else
        {
            if ( ( this.textureUnits == null ) || ( this.textureUnits.length != textureUnits.length ) )
                this.textureUnits = new TextureUnit[ textureUnits.length ];
            
            System.arraycopy( textureUnits, 0, this.textureUnits, 0, textureUnits.length );
        }
        
        if ( this.textureUnits != null )
        {
            for ( int i = 0; i < this.textureUnits.length; i++ )
            {
                final TextureUnit tu = this.textureUnits[ i ];
                
                if ( tu instanceof ProjectiveTextureUnit )
                {
                    applyProjTU( this, (ProjectiveTextureUnit)tu );
                }
                
                tu.setModListener( getModListener() );
            }
        }
        
        setChanged( true );
    }
    
    public final void setTextureUnit( int index, TextureUnit tu )
    {
        if ( ( this.textureUnits != null ) && ( this.textureUnits.length > index ) && ( this.textureUnits[ index ] != null ) )
        {            
            final TextureUnit tu_ = this.textureUnits[ index ];
            
            if ( tu_ instanceof ProjectiveTextureUnit )
            {
                applyProjTU( null, (ProjectiveTextureUnit)tu_ );
            }
            
            tu_.setModListener( null );
        }
        
        if ( textureUnits == null )
        {
            textureUnits = new TextureUnit[ index + 1 ];
        }
        else if ( textureUnits.length <= index )
        {
            TextureUnit[] textureUnits2 = new TextureUnit[ index + 1 ];
            System.arraycopy( textureUnits, 0, textureUnits2, 0, textureUnits.length );
            textureUnits = textureUnits2;
        }
        
        textureUnits[ index ] = tu;
        setChanged( true );
        
        if ( this.textureUnits[ index ] != null )
        {
            final TextureUnit tu_ = this.textureUnits[ index ];
            
            if ( tu_ instanceof ProjectiveTextureUnit )
            {
                applyProjTU( this, (ProjectiveTextureUnit)tu_ );
            }
            
            tu_.setModListener( getModListener() );
        }
    }
    
    public final TextureUnit[] getTextureUnits()
    {
        return ( textureUnits );
    }
    
    public TextureUnit getTextureUnit( int index )
    {
        if ( ( textureUnits == null ) || ( textureUnits.length <= index ) )
            return ( null );
        
        return ( textureUnits[ index ] );
    }
    
    public int getTextureUnitsCount()
    {
        if ( textureUnits == null )
            return ( 0 );
        
        return ( textureUnits.length  );
    }
    
    /**
     * Sets the ShaderProgram information.
     */
    @SuppressWarnings("unchecked")
    public final void setShaderProgramContext( ShaderProgramContext shaderProgramContext )
    {
        this.shaderProgramContext = shaderProgramContext;
        setChanged( true );
        
        if ( shaderProgramContext != null )
            shaderProgramContext.setModListener( getModListener() );
    }
    
    /**
     * @return the ShaderProgram information
     */
    @SuppressWarnings("unchecked")
    public final ShaderProgramContext getShaderProgramContext()
    {
        return ( shaderProgramContext );
    }
    
    /**
     * Sets the coloring attributes information.
     */
    public final void setColoringAttributes( ColoringAttributes coloringAttrs )
    {
        this.coloringAttrs = coloringAttrs;
        setChanged( true );
        
        if ( coloringAttrs != null )
            coloringAttrs.setModListener( getModListener() );
    }
    
    /**
     * @return the coloring attributes information.
     */
    public final ColoringAttributes getColoringAttributes()
    {
        return ( coloringAttrs );
    }
    
    /**
     * @return the coloring attributes information.
     */
    public final ColoringAttributes getColoringAttributes( boolean forceExistance )
    {
        if ( ( getColoringAttributes() == null ) && ( forceExistance ) )
        {
            setColoringAttributes( new ColoringAttributes() );
        }
        
        return ( getColoringAttributes() );
    }
    
    /**
     * Sets the transparency attributes information.
     */
    public final void setTransparencyAttributes( TransparencyAttributes transparencyAttrs )
    {
        this.transparencyAttrs = transparencyAttrs;
        setChanged( true );
        
        if ( transparencyAttrs != null )
            transparencyAttrs.setModListener( getModListener() );
    }
    
    /**
     * @return the transparency attributes information.
     */
    public final TransparencyAttributes getTransparencyAttributes()
    {
        return ( transparencyAttrs );
    }
    
    /**
     * @return the transparency attributes information.
     */
    /**
     * Returns this Appearance'es TransparencyAttributes, if they exist. If they
     * don't exist, they are created depending on the <b>forceExistance</b>
     * parameter.
     * 
     * @param forceExistance if true, a new TransparencyAttributes is created
     *            and attached, if it doesn't already exist.
     * 
     * @return the TransparencyAttributes for this object
     */
    public final TransparencyAttributes getTransparencyAttributes( boolean forceExistance )
    {
        if ( ( getTransparencyAttributes() == null ) && ( forceExistance ) )
        {
            setTransparencyAttributes( new TransparencyAttributes() );
        }
        
        return ( getTransparencyAttributes() );
    }
    
    /**
     * Sets the rendering attributes information.
     */
    public final void setRenderingAttributes( RenderingAttributes renderingAttrs )
    {
        this.renderingAttrs = renderingAttrs;
        setChanged( true );
        
        if ( renderingAttrs != null )
            renderingAttrs.setModListener( getModListener() );
    }
    
    /**
     * @return the rendering attributes information.
     */
    public final RenderingAttributes getRenderingAttributes()
    {
        return ( renderingAttrs );
    }
    
    /**
     * @return the rendering attributes information.
     */
    public final RenderingAttributes getRenderingAttributes( boolean forceExistence )
    {
        if ( ( getRenderingAttributes() == null ) && ( forceExistence ) )
        {
            setRenderingAttributes( new RenderingAttributes() );
        }
        
        return ( getRenderingAttributes() );
    }
    
    /**
     * Sets the polygon attributes information.
     */
    public final void setPolygonAttributes( PolygonAttributes polygonAttrs )
    {
        this.polygonAttrs = polygonAttrs;
        setChanged( true );
        
        if ( polygonAttrs != null )
            polygonAttrs.setModListener( getModListener() );
    }
    
    /**
     * @return the polygon attributes information.
     */
    public final PolygonAttributes getPolygonAttributes()
    {
        return ( polygonAttrs );
    }
    
    /**
     * @return the polygon attributes information.
     */
    public final PolygonAttributes getPolygonAttributes( boolean forceExistence )
    {
        if ( ( getPolygonAttributes() == null ) && ( forceExistence ) )
        {
            setPolygonAttributes( new PolygonAttributes() );
        }
        
        return ( getPolygonAttributes() );
    }
    
    /**
     * Sets the line attributes information.
     */
    public final void setLineAttributes( LineAttributes lineAttrs )
    {
        this.lineAttrs = lineAttrs;
        setChanged( true );
        
        if ( lineAttrs != null )
            lineAttrs.setModListener( getModListener() );
    }
    
    /**
     * @return the line attributes information.
     */
    public final LineAttributes getLineAttributes()
    {
        return ( lineAttrs );
    }
    
    /**
     * @return the line attributes information.
     */
    public final LineAttributes getLineAttributes( boolean forceExistence )
    {
        if ( ( getLineAttributes() == null ) && ( forceExistence ) )
        {
            setLineAttributes( new LineAttributes() );
        }
        
        return ( getLineAttributes() );
    }
    
    /**
     * Sets the point attributes information.
     */
    public final void setPointAttributes( PointAttributes pointAttrs )
    {
        this.pointAttrs = pointAttrs;
        setChanged( true );
        
        if ( pointAttrs != null )
            pointAttrs.setModListener( getModListener() );
    }
    
    /**
     * @return the point attributes information.
     */
    public final PointAttributes getPointAttributes()
    {
        return ( pointAttrs );
    }
    
    /**
     * @return the point attributes information.
     */
    public final PointAttributes getPointAttributes( boolean forceExistance )
    {
        if ( ( getPointAttributes() == null ) && ( forceExistance ) )
        {
            setPointAttributes( new PointAttributes() );
        }
        
        return ( getPointAttributes() );
    }
    
    @Override
    public void setChanged( boolean changed )
    {
        super.setChanged( changed );
        
        if ( !changed )
        {
            if ( this.shaderProgramContext != null )
                this.shaderProgramContext.setChanged( false );
            if ( this.coloringAttrs != null )
                this.coloringAttrs.setChanged( false );
            if ( this.material != null )
                this.material.setChanged( false );
            if ( this.transparencyAttrs != null )
                this.transparencyAttrs.setChanged( false );
            if ( this.renderingAttrs != null )
                this.renderingAttrs.setChanged( false );
            if ( this.polygonAttrs != null )
                this.polygonAttrs.setChanged( false );
            if ( this.lineAttrs != null )
                this.lineAttrs.setChanged( false );
            if ( this.pointAttrs != null )
                this.pointAttrs.setChanged( false );
            if ( this.textureUnits != null )
            {
                for ( int i = 0; i < this.textureUnits.length; i++ )
                    if ( this.textureUnits[ i ] != null )
                        this.textureUnits[ i ].setChanged( false );
            }
        }
    }
    
    public void setChangedRecursive( boolean changed )
    {
        super.setChanged( changed );
        
        if ( this.shaderProgramContext != null )
            this.shaderProgramContext.setChanged( changed );
        if ( this.coloringAttrs != null )
            this.coloringAttrs.setChanged( changed );
        if ( this.material != null )
            this.material.setChanged( changed );
        if ( this.transparencyAttrs != null )
            this.transparencyAttrs.setChanged( changed );
        if ( this.renderingAttrs != null )
            this.renderingAttrs.setChanged( changed );
        if ( this.polygonAttrs != null )
            this.polygonAttrs.setChanged( changed );
        if ( this.lineAttrs != null )
            this.lineAttrs.setChanged( changed );
        if ( this.pointAttrs != null )
            this.pointAttrs.setChanged( changed );
        if ( this.textureUnits != null )
        {
            for ( int i = 0; i < this.textureUnits.length; i++ )
                if ( this.textureUnits[ i ] != null )
                    this.textureUnits[ i ].setChangedRecursive( changed );
        }
    }
    
    public final long verifyChange( Shape3D shape, OpenGLCapabilities glCaps )
    {
        if ( isChanged() )
        {
            changeID = nextChangeId++;
            
            if ( !isStatic() || isStaticDirty() )
                shape.getAtom().updateStateUnits( this, glCaps );
            
            setChanged( false );
        }
        
        return ( changeID );
    }
    
    /*
     * !!!!!!!!!!!!! Convenience methods begin here (Amos Wenger) !!!!!!!!!!!!!
     */

    /**
     * Sets the color of this object, creating a ColoringAttributes if needed.
     * 
     * @param color Color value
     */
    public void setColor( Colorf color )
    {
        getColoringAttributes( true ).setColor( color );
    }
    
    /**
     * Sets the color of this object, creating a ColoringAttributes if needed.
     * 
     * @param r Red value
     * @param g Green value
     * @param b Blue value
     */
    public void setColor( float r, float g, float b )
    {
        getColoringAttributes( true ).setColor( r, g, b );
    }
    
    /**
     * Changes the face culling mode.
     * 
     * @param mode The new culling mode. Can be PolygonAttributes.CULL_NONE,
     *            PolygonAttributes.CULL_BACK, or PolygonAttributes.CULL_FRONT
     */
    public void setFaceCullMode( FaceCullMode mode )
    {
        getPolygonAttributes( true ).setFaceCullMode( mode );
    }
    
    /**
     * Changes the draw mode
     * 
     * @param drawMode The new draw mode. Can be PolygonAttributes.POLYGON_FILL,
     *            PolygonAttributes.POLYGON_LINE,
     *            PolygonAttributes.POLYGON_POINT
     */
    public void setDrawMode( DrawMode drawMode )
    {
        getPolygonAttributes( true ).setDrawMode( drawMode );
    }
    
    @Override
    public boolean isChanged()
    {
        if ( super.isChanged() )
            return ( true );
        
        if ( this.shaderProgramContext != null && this.shaderProgramContext.isChanged() )
            return ( true );
        if ( this.coloringAttrs != null && this.coloringAttrs.isChanged() )
            return ( true );
        if ( this.material != null && this.material.isChanged() )
            return ( true );
        if ( this.transparencyAttrs != null && this.transparencyAttrs.isChanged() )
            return ( true );
        if ( this.renderingAttrs != null && this.renderingAttrs.isChanged() )
            return ( true );
        if ( this.polygonAttrs != null && this.polygonAttrs.isChanged() )
            return ( true );
        if ( this.lineAttrs != null && this.lineAttrs.isChanged() )
            return ( true );
        if ( this.pointAttrs != null && this.pointAttrs.isChanged() )
            return ( true );
        if ( this.textureUnits != null )
        {
            for ( int i = 0; i < this.textureUnits.length; i++ )
                if ( this.textureUnits[ i ] != null && this.textureUnits[ i ].isChanged() )
                    return ( true );
        }
        
        return ( false );
    }
    
    @Override
    protected void duplicateNodeComponent( NodeComponent originalNodeComponent, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( originalNodeComponent, forceDuplicate );
        
        final Appearance origApp = (Appearance)originalNodeComponent;
        
        if ( origApp.getTextureUnits() != null )
        {
            final TextureUnit[] origTUs = origApp.getTextureUnits();
            final TextureUnit[] clonedTUs = new TextureUnit[ origTUs.length ];
            for ( int i = 0; i < origTUs.length; i++ )
            {
                clonedTUs[ i ] = origTUs[ i ].cloneNodeComponent( forceDuplicate );
            }
            this.setTextureUnits( clonedTUs );
        }
        
        if ( forceDuplicate )
        {
            if ( origApp.getMaterial() != null )
            {
                setMaterial( origApp.getMaterial().cloneNodeComponent( true ) );
            }
            setShaderProgramContext( origApp.getShaderProgramContext() );
            if ( origApp.getColoringAttributes() != null )
            {
                setColoringAttributes( origApp.getColoringAttributes().cloneNodeComponent( true ) );
            }
            if ( origApp.getTransparencyAttributes() != null )
            {
                setTransparencyAttributes( origApp.getTransparencyAttributes().cloneNodeComponent( true ) );
            }
            if ( origApp.getRenderingAttributes() != null )
            {
                setRenderingAttributes( origApp.getRenderingAttributes().cloneNodeComponent( true ) );
            }
            if ( origApp.getPolygonAttributes() != null )
            {
                setPolygonAttributes( origApp.getPolygonAttributes().cloneNodeComponent( true ) );
            }
            if ( origApp.getLineAttributes() != null )
            {
                setLineAttributes( origApp.getLineAttributes().cloneNodeComponent( true ) );
            }
            if ( origApp.getPointAttributes() != null )
            {
                setPointAttributes( origApp.getPointAttributes().cloneNodeComponent( true ) );
            }
        }
        else
        {
            setMaterial( origApp.getMaterial() );
            setShaderProgramContext( origApp.getShaderProgramContext() );
            setColoringAttributes( origApp.getColoringAttributes() );
            setTransparencyAttributes( origApp.getTransparencyAttributes() );
            setRenderingAttributes( origApp.getRenderingAttributes() );
            setPolygonAttributes( origApp.getPolygonAttributes() );
            setLineAttributes( origApp.getLineAttributes() );
            setPointAttributes( origApp.getPointAttributes() );
        }
    }
    
    @Override
    public Appearance cloneNodeComponent( boolean forceDuplicate )
    {
        Appearance a = new Appearance();
        a.duplicateNodeComponent( this, forceDuplicate );
        
        return ( a );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( shaderProgramContext != null )
            shaderProgramContext.freeOpenGLResources( canvasPeer );
        
        if ( transparencyAttrs != null )
            transparencyAttrs.freeOpenGLResources( canvasPeer );
        
        if ( material != null )
            material.freeOpenGLResources( canvasPeer );
        
        if ( coloringAttrs != null )
            coloringAttrs.freeOpenGLResources( canvasPeer );
        
        if ( renderingAttrs != null )
            renderingAttrs.freeOpenGLResources( canvasPeer );
        
        if ( polygonAttrs != null )
            polygonAttrs.freeOpenGLResources( canvasPeer );
        
        if ( lineAttrs != null )
            lineAttrs.freeOpenGLResources( canvasPeer );
        
        if ( pointAttrs != null )
            pointAttrs.freeOpenGLResources( canvasPeer );
        
        if ( textureUnits != null )
        {
            for ( int i = 0; i < textureUnits.length; i++ )
            {
                if ( textureUnits[ i ] != null )
                    textureUnits[ i ].freeOpenGLResources( canvasPeer );
            }
        }
    }
    
    /**
     * Constructs a new Appearance object.
     */
    public Appearance()
    {
        super( false );
    }
    
    /**
     * Constructs a new Appearance object.
     * 
     * @param texture
     */
    public Appearance( Texture texture )
    {
        this();
        
        setTexture( texture );
    }
    
    /**
     * Constructs a new Appearance object.
     * 
     * @param texture
     */
    public Appearance( String texture )
    {
        this();
        
        setTexture( texture );
    }
}
