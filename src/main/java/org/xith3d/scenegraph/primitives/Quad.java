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
package org.xith3d.scenegraph.primitives;

import org.jagatoo.opengl.enums.BlendMode;
import org.jagatoo.opengl.enums.TextureFormat;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.TriangleFanArray;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * This is a simple, single textured quad implementation.
 * A Quad is a four-edged polygon, that doesn't necessarily need to be
 * rectangular.<br>
 * <b>If you just want a rectangular Quad you should consider to use Rectangle
 * instad of Quad.</b>
 * 
 * @author Abdul Bezrati
 * @author William Denniss
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Quad extends Shape3D
{
    private void checkVertexCoords( Tuple3f[] vertices ) throws IllegalArgumentException
    {
        if ( vertices == null )
        {
            throw new IllegalArgumentException( "vertices cannot be null" );
        }
        else if ( vertices.length != 4 )
        {
            throw new IllegalArgumentException( "vertices count must be 4 for a Quad" );
        }
    }
    
    /**
     * Updates the vertex data of this Quad.
     * 
     * @param vertices The new vertices (ll, lr, ul, ur)
     * 
     * @deprecated please use {@link #setVertexCoords(Tuple3f[])}, which abstracts the internal vertex order.
     */
    @Deprecated
    public void update( Tuple3f[] vertices )
    {
        checkVertexCoords( vertices );
        
        getGeometry().setCoordinate( 0, vertices[ 0 ] );
        getGeometry().setCoordinate( 1, vertices[ 1 ] );
        getGeometry().setCoordinate( 2, vertices[ 3 ] );
        getGeometry().setCoordinate( 3, vertices[ 2 ] );
    }
    
    /**
     * Updates the vertex data of this Quad.
     * 
     * @param ll New vertex-coord of the lower-left corner
     * @param lr New vertex-coord of the lower-right corner
     * @param ur New vertex-coord of the upper-right corner
     * @param ul New vertex-coord of the upper-left corner
     * 
     * @deprecated please use {@link #setVertexCoords(Tuple3f, Tuple3f, Tuple3f, Tuple3f)}, which abstracts the internal vertex order.
     */
    @Deprecated
    public void update( Tuple3f ll, Tuple3f lr, Tuple3f ul, Tuple3f ur )
    {
        getGeometry().setCoordinate( 0, ll );
        getGeometry().setCoordinate( 1, lr );
        getGeometry().setCoordinate( 2, ur );
        getGeometry().setCoordinate( 3, ul );
    }
    
    private void setVertexCoords( Tuple3f ll, Tuple3f lr, Tuple3f ur, Tuple3f ul, Geometry geom )
    {
        geom.setCoordinate( 0, ll );
        geom.setCoordinate( 1, lr );
        geom.setCoordinate( 2, ur );
        geom.setCoordinate( 3, ul );
    }
    
    public void setVertexCoords( Tuple3f[] coords, Geometry geom )
    {
        checkVertexCoords( coords );
        
        setVertexCoords( coords[ 0 ], coords[ 1 ], coords[ 2 ], coords[ 3 ], geom );
    }
    
    /**
     * Updates the vertex-coord-data of this Quad.
     * 
     * @param ll New vertex-coord of the lower-left corner
     * @param lr New vertex-coord of the lower-right corner
     * @param ur New vertex-coord of the upper-right corner
     * @param ul New vertex-coord of the upper-left corner
     */
    public void setVertexCoords( Tuple3f ll, Tuple3f lr, Tuple3f ur, Tuple3f ul )
    {
        setVertexCoords( ll, lr, ur, ul, getGeometry() );
    }
    
    /**
     * Updates the vertex-coord-data of this Quad.
     * 
     * @param coords The new vertex-coords (ll, lr, ur, ul)
     */
    public void setVertexCoords( Tuple3f[] coords )
    {
        setVertexCoords( coords, getGeometry() );
    }
    
    /**
     * Sets this Quad's color and alpha value.
     * 
     * @param r the red color component
     * @param g the green color component
     * @param b the blue color component
     * @param a the alpha channel value
     */
    private void setColor( float r, float g, float b, float a )
    {
        Appearance app = super.getAppearance( true );
        
        ColoringAttributes ca = app.getColoringAttributes();
        
        if ( r < 0.0f )
        {
            if ( ca != null )
                app.setColoringAttributes( null );
            
            return;
        }
        
        if ( ca == null )
        {
            ca = new ColoringAttributes();
        }
        ca.setColor( r, g, b );
        
        app.setColoringAttributes( ca );
        
        setAlpha( a );
    }
    
    /**
     * Sets this Quad's color.
     * 
     * @param color the new color
     */
    public void setColor( Colorf color )
    {
        if ( ( color != null ) && color.hasAlpha() )
            setColor( color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
        else if ( color != null )
            setColor( color.getRed(), color.getGreen(), color.getBlue(), getAlpha() );
        else
            setColor( -1f, -1f, -1f, getAlpha() );
    }
    
    /**
     * @return this Quad's color or null, if no ColoringAtributes are set
     */
    public Colorf getColor()
    {
        Appearance appearance = super.getAppearance();
        
        if ( appearance != null )
        {
            ColoringAttributes ca = appearance.getColoringAttributes();
            if ( ca != null )
            {
                return ( ca.getColor() );
            }
        }
        
        return ( null );
    }
    
    /**
     * Sets this Quad's alpha value. (-1f if not TransparencyAttributes are set.)
     */
    public void setAlpha( float alpha )
    {
        if ( alpha > 0.0f )
        {
            Appearance app = super.getAppearance( true );
            
            TransparencyAttributes ta = app.getTransparencyAttributes();
            if ( ta == null )
                ta = new TransparencyAttributes( TransparencyAttributes.BLENDED, alpha );
            else
                ta.setTransparency( alpha );
            
            app.setTransparencyAttributes( ta );
        }
        else if ( super.getAppearance() != null )
        {
            super.getAppearance().setTransparencyAttributes( null );
        }
    }
    
    /**
     * @return this Quad's alpha value. (-1f if not TransparencyAttributes are set.)
     */
    public float getAlpha()
    {
        Appearance appearance = super.getAppearance();
        
        if ( appearance != null )
        {
            TransparencyAttributes ta = appearance.getTransparencyAttributes();
            if ( ta != null )
            {
                return ( ta.getTransparency() );
            }
        }
        
        return ( -1.0f );
    }
    
    /**
     * Sets the new texture-coordinates for this Quad.
     * 
     * @param textureCoordinates the texture coordinate for this Quad (ll, lr, ul, ur)
     * 
     * @deprecated please use {@link #setTextureCoordinates(TexCoord2f[])} instead.
     */
    @Deprecated
    public void setTexturePosition( TexCoord2f[] textureCoordinates )
    {
        Geometry geometry = getGeometry();
        
        if ( textureCoordinates == null )
        {
            geometry.setTextureCoordinate( 0, 0, 0f, 0f );
            geometry.setTextureCoordinate( 0, 1, 1f, 0f );
            geometry.setTextureCoordinate( 0, 2, 1f, 1f );
            geometry.setTextureCoordinate( 0, 3, 0f, 1f );
            
            return;
        }
        
        geometry.setTextureCoordinates( 0, 0, textureCoordinates );
    }
    
    /**
     * Sets the new texture-coordinates for this Quad.
     * 
     * @param textureCoordinates the texture coordinate for this Quad (ll, lr, ur, ul)
     */
    public void setTextureCoordinates( TexCoord2f[] textureCoordinates )
    {
        Geometry geometry = getGeometry();
        
        if ( textureCoordinates == null )
        {
            geometry.setTextureCoordinate( 0, 0, 0f, 0f );
            geometry.setTextureCoordinate( 0, 1, 1f, 0f );
            geometry.setTextureCoordinate( 0, 2, 1f, 1f );
            geometry.setTextureCoordinate( 0, 3, 0f, 1f );
            
            return;
        }
        
        geometry.setTextureCoordinate( 0, 0, textureCoordinates[ 0 ] );
        geometry.setTextureCoordinate( 0, 1, textureCoordinates[ 1 ] );
        geometry.setTextureCoordinate( 0, 2, textureCoordinates[ 2 ] );
        geometry.setTextureCoordinate( 0, 3, textureCoordinates[ 3 ] );
    }
    
    /**
     * Sets the new Texture for this Quad.
     * 
     * @param texture the new Texture
     */
    public void setTexture( Texture texture )
    {
        Appearance app = getAppearance();
        
        if ( ( app == null ) && ( texture != null ) )
        {
            app = new Appearance();
            setAppearance( app );
        }
        
        if ( app != null )
        {
            app.setTexture( texture );
            
            if ( texture.getFormat() == TextureFormat.RGBA )
            {
                TransparencyAttributes ta = app.getTransparencyAttributes();
                
                if ( ta == null )
                {
                    ta = new TransparencyAttributes( TransparencyAttributes.BLENDED, 0f );
                }
                else
                {
                    ta.setMode( TransparencyAttributes.BLENDED );
                    ta.setTransparency( 0f );
                }
                
                app.setTransparencyAttributes( ta );
            }
            else
            {
                app.setTransparencyAttributes( null );
            }
        }
        
        setAppearance( app );
    }
    
    /**
     * Sets the new Texture together with texture-coordinates for this Quad.
     * 
     * @param texture the new Texture
     * @param textureCoordinates the texture coordinate for this Quad (ll, lr, ur, ul)
     */
    public void setTexture( Texture texture, TexCoord2f[] textureCoordinates )
    {
        if ( texture != null )
        {
            setTexture( texture );
            
            setTextureCoordinates( textureCoordinates );
        }
        else
        {
            Appearance app = getAppearance();
            if ( app != null )
            {
                app.setTexture( (Texture)null );
            }
        }
    }
    
    /**
     * Sets the new Texture together with texture-coordinates for this Quad.
     * 
     * @param texture the new Texture resource
     * @param textureCoordinates the texture coordinate for this Quad (ll, lr, ur, ul)
     */
    public void setTexture( String texture, TexCoord2f[] textureCoordinates )
    {
        setTexture( TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ), textureCoordinates );
    }
    
    /**
     * Sets the new Texture for this Quad.
     * 
     * @param texture the new Texture resource
     */
    public void setTexture( String texture )
    {
        setTexture( TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ) );
    }
    
    /**
     * @return this Quad's Texture or null
     */
    public Texture getTexture()
    {
        if ( getAppearance() == null )
            return ( null );
        
        return ( getAppearance().getTexture() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TriangleFanArray getGeometry()
    {
        return ( (TriangleFanArray)super.getGeometry() );
    }
    
    protected TriangleFanArray createGeometry()
    {
        return ( new TriangleFanArray( 4 ) );
    }
    
    protected void generateNormals( Geometry geom )
    {
        final int numVertices = geom.getVertexCount();
        
        Point3f coordA = Point3f.fromPool();
        Point3f coordB = Point3f.fromPool();
        Point3f coordC = Point3f.fromPool();
        
        Vector3f vecBA = Vector3f.fromPool();
        Vector3f vecBC = Vector3f.fromPool();
        Vector3f normal = Vector3f.fromPool();
        
        for ( int i = 0; i < numVertices; i++ )
        {
            final int indexA = ( i - 1 + numVertices ) % numVertices;
            final int indexC = ( i + 1 + numVertices ) % numVertices;
            
            geom.getCoordinate( indexA, coordA );
            geom.getCoordinate( i, coordB );
            geom.getCoordinate( indexC, coordC );
            
            vecBA.sub( coordB, coordA );
            vecBC.sub( coordB, coordC );
            
            normal.cross( vecBC, vecBA );
            
            normal.normalize();
            
            if ( normal.getX() == -0.0f )
                normal.setX( 0.0f );
            if ( normal.getY() == -0.0f )
                normal.setY( 0.0f );
            if ( normal.getZ() == -0.0f )
                normal.setZ( 0.0f );
            
            geom.setNormal( i, normal );
        }
        
        Vector3f.toPool( vecBC );
        Vector3f.toPool( vecBA );
        Vector3f.toPool( normal );
        
        Point3f.toPool( coordC );
        Point3f.toPool( coordB );
        Point3f.toPool( coordA );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the texture
     * @param colorR the color for the ColoringAttributes
     * @param colorG the color for the ColoringAttributes
     * @param colorB the color for the ColoringAttributes
     * @param colorA the value for the alpha channel
     */
    private Quad( Tuple3f[] coords, boolean generateNormals, TexCoord2f[] textureCoords, Texture texture, float colorR, float colorG, float colorB, float colorA )
    {
        super();
        
        checkVertexCoords( coords );
        
        /*
        int vertexFormat = Geometry.COORDINATES | Geometry.NORMALS;
        if ( texture != null )
            vertexFormat |= Geometry.TEXTURE_COORDINATES;
        */
        TriangleFanArray geometry = createGeometry();
        
        setVertexCoords( coords, geometry );
        
        if ( generateNormals )
        {
            generateNormals( geometry );
        }
        
        super.setGeometry( geometry );
        
        boolean alphaBlending = false;
        
        if ( texture != null )
        {
            setTexture( texture, textureCoords );
            
            alphaBlending = alphaBlending || texture.getFormat().hasAlpha();
        }
        
        if ( colorR >= 0f )
        {
            this.setColor( colorR, colorG, colorB, colorA );
            
            alphaBlending = alphaBlending || ( colorA > 0.0f );
        }
        
        if ( alphaBlending )
        {
            getAppearance( true ).getTransparencyAttributes( true ).setMode( BlendMode.BLENDED );
        }
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the texture
     * @param color the color for the ColoringAttributes
     */
    public Quad( Point3f[] coords, boolean generateNormals, TexCoord2f[] textureCoords, Texture texture, Colorf color )
    {
        this( coords, generateNormals, textureCoords, texture, ( color != null ) ? color.getRed() : -1.0f, ( color != null ) ? color.getGreen() : -1.0f, ( color != null ) ? color.getBlue() : -1.0f, ( color != null && color.hasAlpha() ) ? color.getAlpha() : -1.0f );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param color the color for the ColoringAttributes
     */
    public Quad( Point3f[] coords, boolean generateNormals, TexCoord2f[] textureCoords, String texture, Colorf color )
    {
        this( coords, generateNormals, textureCoords, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ), color );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Quad( Point3f[] coords, boolean generateNormals, TexCoord2f[] textureCoords, Texture texture )
    {
        this( coords, generateNormals, textureCoords, texture, null );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     */
    public Quad( Point3f[] coords, boolean generateNormals, TexCoord2f[] textureCoords, String texture )
    {
        this( coords, generateNormals, textureCoords, texture, null );
    }
    
    /**
     * Creates an untextured, but colored Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     * @param color the color to use for coloring this Quad
     */
    public Quad( Point3f[] coords, boolean generateNormals, Colorf color )
    {
        this( coords, generateNormals, (TexCoord2f[])null, (Texture)null, color );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Quad( Point3f[] coords, boolean generateNormals, String texture )
    {
        this( coords, generateNormals, (TexCoord2f[])null, texture, null );
    }
    
    /**
     * Creates an untextured Quad without alpha channel.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param generateNormals
     */
    public Quad( Point3f[] coords, boolean generateNormals )
    {
        this( coords, generateNormals, (TexCoord2f[])null, (Texture)null, null );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the texture
     * @param color the color for the ColoringAttributes
     */
    public Quad( Point3f[] coords, TexCoord2f[] textureCoords, Texture texture, Colorf color )
    {
        this( coords, true, textureCoords, texture, color );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param color the color for the ColoringAttributes
     */
    public Quad( Point3f[] coords, TexCoord2f[] textureCoords, String texture, Colorf color )
    {
        this( coords, true, textureCoords, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ), color );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Quad( Point3f[] coords, TexCoord2f[] textureCoords, Texture texture )
    {
        this( coords, true, textureCoords, texture, null );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ur, ul)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Quad( Point3f[] coords, TexCoord2f[] textureCoords, String texture )
    {
        this( coords, true, textureCoords, texture, null );
    }
    
    /**
     * Creates an untextured, but colored Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param color the color to use for coloring this Quad
     */
    public Quad( Point3f[] coords, Colorf color )
    {
        this( coords, true, (TexCoord2f[])null, (Texture)null, color );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param texture the texture
     */
    public Quad( Point3f[] coords, Texture texture )
    {
        this( coords, true, (TexCoord2f[])null, texture, null );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    public Quad( Point3f[] coords, String texture )
    {
        this( coords, true, (TexCoord2f[])null, texture, null );
    }
    
    /**
     * Creates an untextured Quad without alpha channel.
     * 
     * @param coords the vertex-coords of the Quad (ll, lr, ur, ul)
     */
    public Quad( Point3f[] coords )
    {
        this( coords, true, (TexCoord2f[])null, (Texture)null, null );
    }
    
    
    
    
    
    
    
    private static final Tuple3f[] convertVertices( Tuple3f[] vertices )
    {
        Tuple3f v;
        
        v = vertices[ 2 ];
        vertices[ 2 ] = vertices[ 3 ];
        vertices[ 3 ] = v;
        
        return ( vertices );
    }
    
    private static final TexCoord2f[] convertTexCoords( TexCoord2f[] texCoords )
    {
        if ( texCoords == null )
            return ( null );
        
        TexCoord2f t;
        
        t = texCoords[ 2 ];
        texCoords[ 2 ] = texCoords[ 3 ];
        texCoords[ 3 ] = t;
        
        return ( texCoords );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param vertices the vertices of the Quad
     * @param texture the texture
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ul, ur)
     * @param color the color for the ColoringAttributes
     */
    @Deprecated
    public Quad( Tuple3f[] vertices, Texture texture, TexCoord2f[] textureCoords, Colorf color )
    {
        this( convertVertices( vertices ), true, convertTexCoords( textureCoords ), texture, ( color != null ) ? color.getRed() : -1.0f, ( color != null ) ? color.getGreen() : -1.0f, ( color != null ) ? color.getBlue() : -1.0f, ( color != null && color.hasAlpha() ) ? color.getAlpha() : -1.0f );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param vertices the vertices of the Quad
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ul, ur)
     * @param color the color for the ColoringAttributes
     */
    @Deprecated
    public Quad( Tuple3f[] vertices, String texture, TexCoord2f[] textureCoords, Colorf color )
    {
        this( vertices, TextureLoader.getInstance().getTextureOrNull( texture, MipmapMode.MULTI_LEVEL_MIPMAP ), convertTexCoords( textureCoords ), color );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param vertices the vertices of the Quad
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ul, ur)
     */
    @Deprecated
    public Quad( Tuple3f[] vertices, Texture texture, TexCoord2f[] textureCoords )
    {
        this( vertices, texture, convertTexCoords( textureCoords ), null );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param vertices the vertices of the Quad
     * @param texture the name of the texture (loaded with the TextureLoader)
     * @param textureCoords the texture coordinates for the Quad (ll, lr, ul, ur)
     */
    @Deprecated
    public Quad( String texture, Tuple3f[] vertices, TexCoord2f[] textureCoords )
    {
        this( vertices, texture, convertTexCoords( textureCoords ), null );
    }
    
    /**
     * Creates an untextured, but colored Quad.
     * 
     * @param vertices the vertices of the Quad
     * @param color the color to use for coloring this Quad
     */
    @Deprecated
    public Quad( Tuple3f[] vertices, Colorf color )
    {
        this( vertices, (Texture)null, (TexCoord2f[])null, color );
    }
    
    /**
     * Creates a textured Quad.
     * 
     * @param vertices the vertices of the Quad
     * @param texture the name of the texture (loaded with the TextureLoader)
     */
    @Deprecated
    public Quad( Tuple3f[] vertices, String texture )
    {
        this( vertices, texture, (TexCoord2f[])null, null );
    }
    
    /**
     * Creates an untextured Quad without alpha channel.
     * 
     * @param vertices the vertices of the Quad
     */
    @Deprecated
    public Quad( Tuple3f[] vertices )
    {
        this( vertices, (Texture)null, (TexCoord2f[])null, null );
    }
}
