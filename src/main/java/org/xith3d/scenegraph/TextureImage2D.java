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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.jagatoo.image.BufferedImageFactory;
import org.jagatoo.opengl.enums.TextureImageFormat;
import org.jagatoo.opengl.enums.TextureImageInternalFormat;
import org.jagatoo.util.image.ImageUtility;
import org.jagatoo.util.nio.BufferUtils;
import org.openmali.types.twodee.Rect2i;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple2f;

/**
 * {@link TextureImage2D} defines 2D {@link TextureImage}
 * as part of a {@link Texture2D}.
 *
 * Application may define internal format hint for OpenGL that determines how image data should be stored in texture memory.
 * If both format and hint designate uncompressed formats, image will be transferred and stored in uncompressed form.
 * If both format and hint designate compressed formats, image will be transferred and stored compressed form.
 * If format designates uncompressed format and hint designates compressed format, image will be transferred in uncompressed form, 
 * compressed by OpenGL driver and stored in texture memory in compressed form.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureImage2D extends TextureImage
{
    private ByteBuffer dataBuffer = null;
    private byte[] data = null;
    private byte[] pixelRow1 = null;
    private byte[] pixelRow2 = null;
    private int pixelSize = 0;
    private final ArrayList< Rect2i > updateList = new ArrayList< Rect2i >();
    private final TexCoord2f texCoordUR = new TexCoord2f();
    
    private final Rect2i userClipRect = new Rect2i( 0, 0, 128, 128 );
    private final Rect2i clipRect = new Rect2i( 0, 0, 128, 128 );
    
    private boolean readOnly = true;
    private BufferedImage bufferedImage = null;
    
    private final boolean yUp;
    
    protected final boolean getYUp()
    {
        return ( yUp );
    }
    
    /**
     * @return the texture-coordinate for the upper-right corner
     * according to the size and original-size.
     */
    public final TexCoord2f getTextureCoordinateUR( TexCoord2f tcUR )
    {
        tcUR.set( texCoordUR );
        
        return ( tcUR );
    }
    
    /**
     * @return the texture-coordinate for the upper-right corner
     * according to the size and original-size.
     */
    public final < T extends Tuple2f > T getTextureCoordinateUR( T tcUR )
    {
        tcUR.set( texCoordUR.getS(), texCoordUR.getT() );
        
        return ( tcUR );
    }
    
    public final int getPixelSize()
    {
        return ( pixelSize );
    }
    
    public final int getDataSize()
    {
        if ( ( data == null ) && ( dataBuffer == null ) )
            return ( getWidth() * getHeight() * pixelSize );
        
        if ( data == null )
            return ( dataBuffer.capacity() );
        
        return ( data.length );
    }
    
    public final ByteBuffer getDataBuffer()
    {
        return ( dataBuffer );
    }
    
    public final void getData( ByteBuffer bb )
    {
        bb.position( 0 );
        bb.limit( bb.capacity() );
        
        if ( !hasData() )
        {
            bb.flip();
            return;
        }
        
        if ( getFormat().isCompressed() )
        {
            bb.put( data, 0, data.length );
        }
        else
        {
            switch ( this.pixelSize )
            {
                case 4:
                    for ( int i = 0; i < data.length; i += 4 )
                    {
                        bb.put( data[ i + 3 ] );
                        bb.put( data[ i + 2 ] );
                        bb.put( data[ i + 1 ] );
                        bb.put( data[ i + 0 ] );
                    }
                    break;
                case 3:
                    for ( int i = 0; i < data.length; i += 3 )
                    {
                        bb.put( data[ i + 2 ] );
                        bb.put( data[ i + 1 ] );
                        bb.put( data[ i + 0 ] );
                    }
                    break;
                case 2:
                    for ( int i = 0; i < data.length; i += 2 )
                    {
                        bb.put( data[ i + 1 ] );
                        bb.put( data[ i + 0 ] );
                    }
                    break;
                case 1:
                    for ( int i = 0; i < data.length; i += 1 )
                    {
                        bb.put( data[ i + 0 ] );
                    }
                    break;
            }
        }
        
        bb.flip();
    }
    
    public final int getData( byte[] data )
    {
        final byte[] trg = data;
        
        if ( !hasData() )
        {
            return ( 0 );
        }
        
        if ( getFormat().isCompressed() )
        {
            if ( this.dataBuffer == null )
            {
                System.arraycopy( this.data, 0, trg, 0, this.data.length );
                
                return ( this.data.length );
            }
            
            this.dataBuffer.position( 0 );
            this.dataBuffer.get( trg, 0, this.dataBuffer.limit() );
            this.dataBuffer.position( 0 );
            
            return ( this.dataBuffer.limit() );
        }
        
        int b = 0;
        if ( dataBuffer == null )
        {
            final byte[] src = this.data;
            final int n = src.length;
            
            switch ( this.pixelSize )
            {
                case 4:
                    for ( int i = 0; i < n; i += 4 )
                    {
                        trg[ b++ ] = src[ i + 3 ];
                        trg[ b++ ] = src[ i + 2 ];
                        trg[ b++ ] = src[ i + 1 ];
                        trg[ b++ ] = src[ i + 0 ];
                    }
                    break;
                case 3:
                    for ( int i = 0; i < n; i += 3 )
                    {
                        trg[ b++ ] = src[ i + 2 ];
                        trg[ b++ ] = src[ i + 1 ];
                        trg[ b++ ] = src[ i + 0 ];
                    }
                    break;
                case 2:
                    for ( int i = 0; i < n; i += 2 )
                    {
                        trg[ b++ ] = src[ i + 1 ];
                        trg[ b++ ] = src[ i + 0 ];
                    }
                    break;
                case 1:
                    for ( int i = 0; i < n; i += 1 )
                    {
                        trg[ b++ ] = src[ i + 0 ];
                    }
                    break;
            }
            
            return ( n );
        }
        
        final int n = dataBuffer.limit();
        
        switch ( this.pixelSize )
        {
            case 4:
                for ( int i = 0; i < n; i += 4 )
                {
                    trg[ b++ ] = dataBuffer.get( i + 3 );
                    trg[ b++ ] = dataBuffer.get( i + 2 );
                    trg[ b++ ] = dataBuffer.get( i + 1 );
                    trg[ b++ ] = dataBuffer.get( i + 0 );
                }
                break;
            case 3:
                for ( int i = 0; i < n; i += 3 )
                {
                    trg[ b++ ] = dataBuffer.get( i + 2 );
                    trg[ b++ ] = dataBuffer.get( i + 1 );
                    trg[ b++ ] = dataBuffer.get( i + 0 );
                }
                break;
            case 2:
                for ( int i = 0; i < n; i += 2 )
                {
                    trg[ b++ ] = dataBuffer.get( i + 1 );
                    trg[ b++ ] = dataBuffer.get( i + 0 );
                }
                break;
            case 1:
                for ( int i = 0; i < n; i += 1 )
                {
                    trg[ b++ ] = dataBuffer.get( i + 0 );
                }
                break;
        }
        
        dataBuffer.position( 0 );
        
        return ( n );
    }
    
    public void freeLocalData()
    {
        this.dataBuffer = null;
        this.data = null;
        this.bufferedImage = null;
    }
    
    /**
     * Marks a portion of the image component as dirty.
     * The region will be pushed to the graphics card on the next frame.
     * 
     * {@link Texture2D#setHasUpdateList(boolean)} must be called afterwards.
     * 
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void update( int x, int y, int width, int height )
    {
        if ( ( x < 0 ) || ( y < 0 ) || ( x >= this.getWidth() ) || ( y >= this.getHeight() ) || ( width > this.getWidth() ) || ( height > this.getHeight() ) || ( ( x + width ) > this.getWidth() ) || ( ( y + height ) > this.getHeight() ) )
        {
            throw new IllegalArgumentException( "Rectangle outside of image" );
        }
        
        Rect2i rect = Rect2i.fromPool();
        
        if ( yUp )
        {
            rect.set( x, this.getHeight() - height - y, width, height );
        }
        else
        {
            rect.set( x, y, width, height );
        }
        
        if ( updateList.size() > 0 )
        {
            int mostSimilar = -1;
            int mostSimMatchFact = 0;
            
            for ( int i = 0; i < updateList.size(); i++ )
            {
                Rect2i r = updateList.get( i );
                
                if ( rect.isCoveredBy( r ) )
                {
                    Rect2i.toPool( rect );
                    
                    return;
                }
                else if ( !rect.intersects( r ) )
                {
                    int matchFact = rect.getMatchFactor( r );
                    if ( matchFact < mostSimMatchFact )
                    {
                        mostSimMatchFact = matchFact;
                        mostSimilar = i;
                    }
                }
            }
            
            if ( mostSimilar >= 0 )
            {
                updateList.get( mostSimilar ).combine( rect );
                Rect2i.toPool( rect );
                
                return;
            }
        }
        
        updateList.add( rect );
    }
    
    /**
     * Marks a portion of the image component as dirty.
     * The region will be pushed to the graphics card on the next frame.
     * 
     * {@link Texture2D#setHasUpdateList(boolean)} must be called afterwards.
     * 
     * @param r
     */
    public final void update( Rect2i r )
    {
        update( r.getLeft(), r.getTop(), r.getWidth(), r.getHeight() );
    }
    
    public final ArrayList< Rect2i > getUpdateList()
    {
        return ( updateList );
    }
    
    public final void clearUpdateList()
    {
        for ( int i = updateList.size() - 1; i >= 0; i-- )
        {
            Rect2i.toPool( updateList.get( i ) );
        }
        
        updateList.clear();
    }
    
    void fixUpdateListAfterSizeChange()
    {
        for ( int i = 0; i < updateList.size(); i++ )
        {
            Rect2i r = updateList.get( i );
            
            r.setLocation( Math.min( r.getLeft(), this.getWidth() ), Math.min( r.getTop(), this.getHeight() ) );
            r.setSize( Math.min( r.getLeft() + r.getWidth(), this.getWidth() ) - r.getLeft(), Math.min( r.getTop() + r.getHeight(), this.getHeight() ) - r.getTop() );
        }
    }
    
    void setReadOnly( boolean readOnly )
    {
        this.readOnly = readOnly;
    }
    
    public BufferedImage getBufferedImage()
    {
        if ( getFormat().isCompressed() )
            return ( null );
        
        if ( bufferedImage == null )
        {
            if ( this.data == null )
            {
                if ( readOnly )
                {
                    // DirectBufferedImages cannot currently be scaled. Hence we need this to create scalable images.
                    
                    byte[] dbbData = new byte[ dataBuffer.limit() ];
                    getData( dbbData );
                    bufferedImage = BufferedImageFactory.createSharedBufferedImage( getWidth(), getHeight(), pixelSize, hasAlpha(), null, dbbData );
                }
                else
                {
                    int[] pixelOffsets = getFormat().hasAlpha() ? new int[] { 0, 1, 2, 3 } : new int[] { 0, 1, 2 };
                    bufferedImage = BufferedImageFactory.createDirectBufferedImage( getWidth(), getHeight(), getFormat().hasAlpha(), pixelOffsets, dataBuffer );
                }
            }
            else
            {
                bufferedImage = BufferedImageFactory.createSharedBufferedImage( getWidth(), getHeight(), pixelSize, hasAlpha(), null, data );
            }
        }
        
        return ( bufferedImage );
    }
    
    protected Graphics2D createGraphics2D()
    {
        BufferedImage bi = getBufferedImage();
        
        return ( bi.createGraphics() );
    }
    
    private void clampClipRect( int texWidth, int texHeight )
    {
        clipRect.set( userClipRect );
        clipRect.clamp( 0, 0, texWidth, texHeight );
    }
    
    public void setClipRect( int x, int y, int width, int height )
    {
        userClipRect.set( x, y, width, height );
        
        clampClipRect( getWidth(), getHeight() );
    }
    
    public final void setClipRect( Rect2i clipRect )
    {
        setClipRect( clipRect.getLeft(), clipRect.getTop(), clipRect.getWidth(), clipRect.getHeight() );
    }
    
    public final <Rect2i_ extends Rect2i> Rect2i_ getClipRect( Rect2i_ rect )
    {
        rect.set( userClipRect );
        
        return ( rect );
    }
    
    final Rect2i getEffectiveClipRect()
    {
        return ( clipRect );
    }
    
    /**
     * Sets the data for the image.
     * 
     * @param data
     * @param dataLength
     * @param useBuffer
     */
    protected final void setImageData( byte[] data, int dataLength, boolean useBuffer )
    {
        clampClipRect( getWidth(), getHeight() );
        
        if ( ( data == null ) && ( dataLength <= 0 ) )
        {
            this.dataBuffer = null;
            this.data = null;
            this.bufferedImage = null;
            
            this.setHasData( false );
            
            return;
        }
        
        if ( useBuffer )
        {
            if ( ( this.dataBuffer == null ) || ( this.dataBuffer.capacity() < dataLength ) )
            {
                this.dataBuffer = BufferUtils.createByteBuffer( dataLength );
            }
            
            this.data = null;
        }
        else
        {
            if ( ( this.data == null ) || ( this.data.length < dataLength ) )
            {
                this.data = new byte[ dataLength ];
            }
            
            this.dataBuffer = null;
        }
        
        this.bufferedImage = null;
        
        if ( getFormat().isCompressed() )
        {
            if ( ( data != null ) && ( dataLength > 0 ) )
            {
                //final int imgSize = calculateNeededImageSize();
                final int imgSize = dataLength;
                this.pixelSize = imgSize / ( getWidth() * getHeight() );
                
                if ( useBuffer )
                {
                    dataBuffer.position( 0 );
                    dataBuffer.put( data, 0, dataLength );
                    dataBuffer.flip();
                }
                else
                {
                    System.arraycopy( data, 0, this.data, 0, dataLength );
                }
            }
        }
        else
        {
            if ( data == null )
            {
                this.setHasData( true );
                
                return;
            }
            
            if ( useBuffer )
            {
                dataBuffer.position( 0 );
                dataBuffer.put( data, 0, dataLength );
                dataBuffer.flip();
            }
            else
            {
                switch ( this.pixelSize )
                {
                    case 4:
                        for ( int i = 0; i < dataLength; i += 4 )
                        {
                            this.data[ i + 0 ] = data[ i + 3 ];
                            this.data[ i + 1 ] = data[ i + 2 ];
                            this.data[ i + 2 ] = data[ i + 1 ];
                            this.data[ i + 3 ] = data[ i + 0 ];
                        }
                        break;
                    case 3:
                        for ( int i = 0; i < dataLength; i += 3 )
                        {
                            this.data[ i + 0 ] = data[ i + 2 ];
                            this.data[ i + 1 ] = data[ i + 1 ];
                            this.data[ i + 2 ] = data[ i + 0 ];
                        }
                        break;
                    case 2:
                        for ( int i = 0; i < dataLength; i += 2 )
                        {
                            this.data[ i + 0 ] = data[ i + 1 ];
                            this.data[ i + 1 ] = data[ i + 0 ];
                        }
                        break;
                    case 1:
                        for ( int i = 0; i < dataLength; i += 1 )
                        {
                            this.data[ i + 0 ] = data[ i + 0 ];
                        }
                        break;
                }
            }
        }
        
        setHasData( true );
    }
    
    /**
     * Sets the data for the image.
     * 
     * @param data
     * @param dataLength
     */
    public final void setImageData( byte[] data, int dataLength )
    {
        setImageData( data, dataLength, true );
    }
    
    /**
     * Sets the data for the image.
     * 
     * @param data
     * @param useBuffer
     */
    protected final void setImageData( byte[] data, boolean useBuffer )
    {
        setImageData( data, ( data != null ) ? data.length : 0, useBuffer );
    }
    
    /**
     * Sets the data for the image.
     * 
     * @param data
     */
    public final void setImageData( byte[] data )
    {
        setImageData( data, true );
    }
    
    private final boolean needsDataResize( int width, int height, boolean allowBiggerTexture )
    {
        if ( ( width > this.getWidth() ) || ( height > this.getHeight() ) )
            return ( true );
        
        if ( allowBiggerTexture )
            return ( false );
        
        int lWidth = this.getWidth() >> 1;
        int lHeight = this.getHeight() >> 1;
        
        return ( ( width <= lWidth ) || ( height <= lHeight ) );
    }
    
    protected boolean initImageData( int width, int height, int orgWidth, int orgHeight, boolean allowBiggerTexture, boolean usebuffer )
    {
        int oldDataLength = getOriginalWidth() * getOriginalHeight() * pixelSize;
        int newDataLength = orgWidth * orgHeight * pixelSize;
        
        if ( newDataLength == oldDataLength )
        {
            if ( usebuffer && ( this.dataBuffer != null ) )
                return ( false );
            
            if ( !usebuffer && ( this.data != null ) )
                return ( false );
        }
        
        boolean sizeChanged = ( ( width != this.getWidth() ) || ( height != this.getHeight() ) ) && needsDataResize( width, height, allowBiggerTexture );
        
        if ( sizeChanged )
            this.setSize( width, height );
        if ( ( orgWidth != this.getOriginalWidth() ) || ( orgHeight != this.getOriginalHeight() ) )
            this.setOriginalSize( orgWidth, orgHeight );
        
        texCoordUR.set( (float)orgWidth / (float)getWidth(), (float)orgHeight / (float)getHeight() );
        
        if ( sizeChanged )
            setImageData( null, width * height * pixelSize, usebuffer );
        
        return ( sizeChanged );
    }
    
    public final boolean initImageData( int orgWidth, int orgHeight, boolean allowBiggerTexture, boolean usebuffer )
    {
        int width = ImageUtility.roundUpPower2( orgWidth );
        int height = ImageUtility.roundUpPower2( orgHeight );
        
        return ( initImageData( width, height, orgWidth, orgHeight, allowBiggerTexture, usebuffer ) );
    }
    
    public final boolean initImageData( int orgWidth, int orgHeight, boolean allowBiggerTexture )
    {
        return ( initImageData( orgWidth, orgHeight, allowBiggerTexture, ( dataBuffer != null ) ) );
    }
    
    public final boolean initImageData( boolean allowBiggerTexture, boolean usebuffer )
    {
        return ( initImageData( getWidth(), getHeight(), getOriginalWidth(), getOriginalHeight(), allowBiggerTexture, usebuffer ) );
    }
    
    protected void setImageData( BufferedImage image, boolean useBuffer )
    {
        int orgWidth = image.getWidth();
        int orgHeight = image.getWidth();
        
        int width = ImageUtility.roundUpPower2( orgWidth );
        int height = ImageUtility.roundUpPower2( orgHeight );
        
        this.setSize( width, height );
        this.setOriginalSize( orgWidth, orgHeight );
        texCoordUR.set( (float)orgWidth / (float)width, (float)orgHeight / (float)height );
        
        if ( useBuffer )
            this.data = null;
        else
            this.dataBuffer = null;
        
        this.pixelSize = getFormat().getPixelSize();
        
        if ( useBuffer )
        {
            this.dataBuffer = BufferUtils.createByteBuffer( width * height * pixelSize );
            
            Raster raster = image.getRaster();
            ColorModel cm = image.getColorModel();
            Object o = null;
            
            int i = 0;
            for ( int x = 0; x < width; x++ )
            {
                for ( int y = 0; y < height; y++ )
                {
                    o = raster.getDataElements( x, y, o );
                    
                    switch ( pixelSize )
                    {
                        case 4:
                        {
                            final byte r = (byte)cm.getRed( o );
                            final byte g = (byte)cm.getGreen( o );
                            final byte b = (byte)cm.getBlue( o );
                            final byte a = (byte)cm.getAlpha( o );
                            
                            dataBuffer.put( i++, r );
                            dataBuffer.put( i++, g );
                            dataBuffer.put( i++, b );
                            dataBuffer.put( i++, a );
                            break;
                        }
                        case 3:
                        {
                            final byte r = (byte)cm.getRed( o );
                            final byte g = (byte)cm.getGreen( o );
                            final byte b = (byte)cm.getBlue( o );
                            
                            dataBuffer.put( i++, r );
                            dataBuffer.put( i++, g );
                            dataBuffer.put( i++, b );
                            break;
                        }
                        case 2:
                        {
                            final byte r = (byte)cm.getRed( o );
                            final byte g = (byte)cm.getGreen( o );
                            
                            dataBuffer.put( i++, r );
                            dataBuffer.put( i++, g );
                            break;
                        }
                        case 1:
                        {
                            final byte r = (byte)cm.getRed( o );
                            
                            dataBuffer.put( i++, r );
                            break;
                        }
                    }
                }
            }
            
            //dataBuffer.flip();
            dataBuffer.position( 0 );
            dataBuffer.limit( dataBuffer.capacity() );
        }
        else
        {
            this.data = new byte[ width * height * pixelSize ];
            
            int i = getDataOffset( 0, 0 );
            for ( int x = 0; x < width; x++ )
            {
                for ( int y = 0; y < height; y++ )
                {
                    int argb = image.getRGB( x, y );
                    
                    if ( pixelSize == 4 )
                    {
                        data[ i++ ] = (byte)( ( argb & 0xFF000000 ) >> 24 );
                        data[ i++ ] = (byte)( ( argb & 0x00FF0000 ) >> 16 );
                        data[ i++ ] = (byte)( ( argb & 0x0000FF00 ) >> 8 );
                        data[ i++ ] = (byte)( argb & 0x000000FF );
                    }
                    else if ( pixelSize == 3 )
                    {
                        data[ i++ ] = (byte)( ( argb & 0x00FF0000 ) >> 16 );
                        data[ i++ ] = (byte)( ( argb & 0x0000FF00 ) >> 8 );
                        data[ i++ ] = (byte)( argb & 0x000000FF );
                    }
                }
            }
        }
        
        setHasData( true );
    }
    
    public void setImageData( BufferedImage image )
    {
        setImageData( image, true );
    }
    
    private final int getDataOffset( int x, int y )
    {
        if ( yUp )
            return ( ( y * getWidth() * pixelSize ) + ( x * pixelSize ) );
        
        return ( ( ( getHeight() - y - 1 ) * getWidth() * pixelSize ) + ( x * pixelSize ) );
    }
    
    protected final void setPixel( int offset, byte[] data )
    {
        if ( this.data == null )
        {
            this.dataBuffer.position( offset );
            this.dataBuffer.put( data, 0, pixelSize );
            this.dataBuffer.position( 0 );
        }
        else
        {
            switch ( this.pixelSize )
            {
                case 4:
                    this.data[ offset + 0 ] = data[ 3 ];
                    this.data[ offset + 1 ] = data[ 2 ];
                    this.data[ offset + 2 ] = data[ 1 ];
                    this.data[ offset + 3 ] = data[ 0 ];
                    break;
                case 3:
                    this.data[ offset + 0 ] = data[ 2 ];
                    this.data[ offset + 1 ] = data[ 1 ];
                    this.data[ offset + 2 ] = data[ 0 ];
                    break;
                case 2:
                    this.data[ offset + 0 ] = data[ 1 ];
                    this.data[ offset + 1 ] = data[ 0 ];
                    break;
                case 1:
                    this.data[ offset + 0 ] = data[ 0 ];
                    break;
            }
        }
    }
    
    public final void setPixel( int x, int y, byte[] data )
    {
        //if ( ( x < 0 ) || ( x >= getOriginalWidth() ) || ( y < 0 ) || ( y >= getOriginalHeight() ) )
        if ( ( x < 0 ) || ( x >= getWidth() ) || ( y < 0 ) || ( y >= getHeight() ) )
            return;
        
        setPixel( getDataOffset( x, y ), data );
    }
    
    private final byte[] getPixel( int offset, byte[] data )
    {
        if ( this.data == null )
        {
            this.dataBuffer.position( offset );
            this.dataBuffer.get( data, 0, pixelSize );
            this.dataBuffer.position( 0 );
        }
        else
        {
            switch ( this.pixelSize )
            {
                case 4:
                    data[ 0 ] = this.data[ offset + 3 ];
                    data[ 1 ] = this.data[ offset + 2 ];
                    data[ 2 ] = this.data[ offset + 1 ];
                    data[ 3 ] = this.data[ offset + 0 ];
                    break;
                case 3:
                    data[ 0 ] = this.data[ offset + 2 ];
                    data[ 1 ] = this.data[ offset + 1 ];
                    data[ 2 ] = this.data[ offset + 0 ];
                    break;
                case 2:
                    data[ 0 ] = this.data[ offset + 1 ];
                    data[ 1 ] = this.data[ offset + 0 ];
                    break;
                case 1:
                    data[ 0 ] = this.data[ offset + 0 ];
                    break;
            }
        }
        
        return ( data );
    }
    
    public final byte[] getPixel( int x, int y, byte[] data )
    {
        return ( getPixel( getDataOffset( x, y ), data ) );
    }
    
    public final void setPixelLine( int trgByteOffset, byte[] data, int srcByteOffset, int length )
    {
        final int n = length * pixelSize;
        
        if ( this.data == null )
        {
            this.dataBuffer.position( trgByteOffset );
            this.dataBuffer.put( data, srcByteOffset, n );
            this.dataBuffer.position( 0 );
        }
        else
        {
            switch ( this.pixelSize )
            {
                case 4:
                    for ( int i = 0; i < n; i += 4 )
                    {
                        this.data[ trgByteOffset + i + 0 ] = data[ srcByteOffset + i + 3 ];
                        this.data[ trgByteOffset + i + 1 ] = data[ srcByteOffset + i + 2 ];
                        this.data[ trgByteOffset + i + 2 ] = data[ srcByteOffset + i + 1 ];
                        this.data[ trgByteOffset + i + 3 ] = data[ srcByteOffset + i + 0 ];
                    }
                    break;
                case 3:
                    for ( int i = 0; i < n; i += 3 )
                    {
                        this.data[ trgByteOffset + i + 0 ] = data[ srcByteOffset + i + 2 ];
                        this.data[ trgByteOffset + i + 1 ] = data[ srcByteOffset + i + 1 ];
                        this.data[ trgByteOffset + i + 2 ] = data[ srcByteOffset + i + 0 ];
                    }
                    break;
                case 2:
                    for ( int i = 0; i < n; i += 2 )
                    {
                        this.data[ trgByteOffset + i + 0 ] = data[ srcByteOffset + i + 1 ];
                        this.data[ trgByteOffset + i + 1 ] = data[ srcByteOffset + i + 0 ];
                    }
                    break;
                case 1:
                    for ( int i = 0; i < n; i += 1 )
                    {
                        this.data[ trgByteOffset + i + 0 ] = data[ srcByteOffset + i + 0 ];
                    }
                    break;
            }
        }
    }
    
    public final void setPixelLine( int x, int y, byte[] data, int srcOffset, int length )
    {
        setPixelLine( getDataOffset( x, y ), data, srcOffset, length );
    }
    
    public final byte[] getPixelLine( int offset, int length, byte[] data )
    {
        if ( this.data == null )
        {
            this.dataBuffer.position( offset );
            this.dataBuffer.get( data, 0, length * pixelSize );
            this.dataBuffer.position( 0 );
        }
        else
        {
            final int n = length * pixelSize;
            
            switch ( this.pixelSize )
            {
                case 4:
                    for ( int i = 0; i < n; i += 4 )
                    {
                        data[ i + 0 ] = this.data[ offset + i + 3 ];
                        data[ i + 1 ] = this.data[ offset + i + 2 ];
                        data[ i + 2 ] = this.data[ offset + i + 1 ];
                        data[ i + 3 ] = this.data[ offset + i + 0 ];
                    }
                    break;
                case 3:
                    for ( int i = 0; i < n; i += 3 )
                    {
                        data[ i + 0 ] = this.data[ offset + i + 2 ];
                        data[ i + 1 ] = this.data[ offset + i + 1 ];
                        data[ i + 2 ] = this.data[ offset + i + 0 ];
                    }
                    break;
                case 2:
                    for ( int i = 0; i < n; i += 2 )
                    {
                        data[ i + 0 ] = this.data[ offset + i + 1 ];
                        data[ i + 1 ] = this.data[ offset + i + 0 ];
                    }
                    break;
                case 1:
                    for ( int i = 0; i < n; i += 1 )
                    {
                        data[ i + 0 ] = this.data[ offset + i + 0 ];
                    }
                    break;
            }
        }
        
        return ( data );
    }
    
    public final byte[] getPixelLine( int x, int y, int length, byte[] data )
    {
        return ( getPixelLine( getDataOffset( x, y ), length, data ) );
    }
    
    private static final byte[] combinePixels( final byte[] src,
                                               final int srcByteOffset,
                                               final int srcPixelSize,
                                               final TextureImage2D trgIC, final int trgPixelSize,
                                               final byte[] trg, final int trgByteOffset,
                                               final int numPixels,
                                               final boolean overwrite
                                             )
    {
        if ( srcPixelSize == 3 )
        {
            if ( trgPixelSize == 3 )
            {
                return ( src );
            }
            
            // target has size 4
            
            //trgIC.getPixelLine( trgOffset, numPixels, trg );
            
            int j = srcByteOffset;
            int k = 0;
            for ( int i = 0; i < numPixels; i++ )
            {
                trg[ k + 0 ] = src[ j + 0 ];
                trg[ k + 1 ] = src[ j + 1 ];
                trg[ k + 2 ] = src[ j + 2 ];
                trg[ k + 3 ] = (byte)255;
                
                j += srcPixelSize;
                k += trgPixelSize;
            }
        }
        else if ( srcPixelSize == 4 )
        {
            if ( trgPixelSize == 3 )
            {
                if ( !overwrite )
                    trgIC.getPixelLine( trgByteOffset, numPixels, trg );
                
                int j = srcByteOffset;
                int k = 0;
                for ( int i = 0; i < numPixels; i++ )
                {
                    final int srcR = src[ j + 0 ] & 0x000000FF;
                    final int srcG = src[ j + 1 ] & 0x000000FF;
                    final int srcB = src[ j + 2 ] & 0x000000FF;
                    final int srcA = src[ j + 3 ] & 0x000000FF;
                    
                    if ( overwrite )
                    {
                        trg[ k + 0 ] = (byte)( srcR * srcA / 255 );
                        trg[ k + 1 ] = (byte)( srcG * srcA / 255 );
                        trg[ k + 2 ] = (byte)( srcB * srcA / 255 );
                    }
                    else
                    {
                        final int trgR = trg[ k + 0 ] & 0x000000FF;
                        final int trgG = trg[ k + 1 ] & 0x000000FF;
                        final int trgB = trg[ k + 2 ] & 0x000000FF;
                        
                        trg[ k + 0 ] = (byte)( ( srcR * srcA / 255 ) + ( trgR * ( 255 - srcA ) / 255 ) );
                        trg[ k + 1 ] = (byte)( ( srcG * srcA / 255 ) + ( trgG * ( 255 - srcA ) / 255 ) );
                        trg[ k + 2 ] = (byte)( ( srcB * srcA / 255 ) + ( trgB * ( 255 - srcA ) / 255 ) );
                    }
                    
                    j += srcPixelSize;
                    k += trgPixelSize;
                }
            }
            else if ( trgPixelSize == 4 )
            {
                if ( overwrite )
                    return ( src );
                
                trgIC.getPixelLine( trgByteOffset, numPixels, trg );
                
                int j = srcByteOffset;
                int k = 0;
                for ( int i = 0; i < numPixels; i++ )
                {
                    final int srcR = src[ j + 0 ] & 0x000000FF;
                    final int srcG = src[ j + 1 ] & 0x000000FF;
                    final int srcB = src[ j + 2 ] & 0x000000FF;
                    final int srcA = src[ j + 3 ] & 0x000000FF;
                    
                    final int trgR = trg[ k + 0 ] & 0x000000FF;
                    final int trgG = trg[ k + 1 ] & 0x000000FF;
                    final int trgB = trg[ k + 2 ] & 0x000000FF;
                    final int trgA = trg[ k + 3 ] & 0x000000FF;
                    
                    trg[ k + 0 ] = (byte)( ( srcR * srcA / 255 ) + ( trgR * ( 255 - srcA ) / 255 ) );
                    trg[ k + 1 ] = (byte)( ( srcG * srcA / 255 ) + ( trgG * ( 255 - srcA ) / 255 ) );
                    trg[ k + 2 ] = (byte)( ( srcB * srcA / 255 ) + ( trgB * ( 255 - srcA ) / 255 ) );
                    trg[ k + 3 ] = (byte)( ( srcA * srcA / 255 ) + ( trgA * ( 255 - srcA ) / 255 ) );
                    
                    j += srcPixelSize;
                    k += trgPixelSize;
                }
            }
        }
        
        return ( trg );
    }
    
    private final byte[] getPixelLineBuffer1( int size )
    {
        if ( ( pixelRow1 == null ) || ( pixelRow1.length < size ) )
            pixelRow1 = new byte[ Math.max( size, this.getWidth() * this.getPixelSize() ) ];
        
        return ( pixelRow1 );
    }
    
    private final byte[] getPixelLineBuffer2( int size )
    {
        if ( ( pixelRow2 == null ) || ( pixelRow2.length < size ) )
            pixelRow2 = new byte[ Math.max( size, this.getWidth() * this.getPixelSize() ) ];
        
        return ( pixelRow2 );
    }
    
    /**
     * Copies the image data from the given {@link TextureImage2D}
     * and writes them to this image at the given position.
     * 
     * @param srcTI source image
     * @param srcX the rectangle's left to copy from the source {@link TextureImage2D}.
     * @param srcY the rectangle's top to copy from the source {@link TextureImage2D}.
     * @param srcWidth the rectangle's width to copy from the source {@link TextureImage2D}.
     * @param srcHeight the rectangle's height to copy from the source {@link TextureImage2D}.
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     * @param trgWidth the targetWidth (tiled or clipped if necessary)
     * @param trgHeight the targetHeight (tiled or clipped if necessary)
     * @param overwrite
     */
    private void copyImageDataFrom( TextureImage2D srcTI, int srcX, int srcY, int srcWidth, int srcHeight, int trgX, int trgY, int trgWidth, int trgHeight, boolean overwrite )
    {
        if ( trgX + trgWidth < clipRect.getLeft() )
            return;
        
        if ( trgY + trgHeight < clipRect.getTop() )
            return;
        
        if ( trgX >= clipRect.getLeft() + clipRect.getWidth() )
            return;
        
        if ( trgY >= clipRect.getTop() + clipRect.getHeight() )
            return;
        
        if ( trgX < clipRect.getLeft() )
        {
            int oldTrgX = trgX;
            trgX = clipRect.getLeft() - ( ( clipRect.getLeft() - trgX ) % srcWidth );
            trgWidth -= trgX - oldTrgX;
        }
        
        if ( trgY < clipRect.getTop() )
        {
            int oldTrgY = trgY;
            trgY = clipRect.getTop() - ( ( clipRect.getTop() - trgY ) % srcHeight );
            trgHeight -= trgY - oldTrgY;
        }
        
        if ( trgX + trgWidth > clipRect.getLeft() + clipRect.getWidth() )
        {
            trgWidth = (int)Math.ceil( (double)( clipRect.getLeft() + clipRect.getWidth() - trgX ) / (double)srcWidth ) * srcWidth;
        }
        
        /*
        if ( trgY + trgHeight > clipRect.getTop() + clipRect.getHeight() )
        {
            trgHeight = (int)Math.ceil( (double)( clipRect.getTop() + clipRect.getHeight() - trgY ) / (double)srcHeight ) * srcHeight;
        }
        */
        
        final int srcPixelSize = srcTI.getPixelSize();
        final int trgPixelSize = this.getPixelSize();
        
        byte[] srcBuffer = getPixelLineBuffer1( srcWidth * srcPixelSize );
        byte[] trgBuffer = getPixelLineBuffer2( srcWidth * trgPixelSize );
        
        final int y_ = getHeight() - getOriginalHeight();
        
        final int x0 = Math.max( clipRect.getLeft(), trgX );
        final int x1 = Math.min( clipRect.getLeft() + clipRect.getWidth(), trgX + trgWidth );
        final int y0 = Math.max( clipRect.getTop(), trgY );
        final int y1 = Math.min( clipRect.getTop() + clipRect.getHeight(), trgY + trgHeight );
        
        for ( int j = y0; j < y1; j++ )
        {
            int srcJ = srcY + ( ( j - trgY ) % srcHeight );
            
            srcTI.getPixelLine( srcX, srcJ, srcWidth, srcBuffer );
            
            int trgX_ = trgX;
            int trgWidth_ = trgWidth;
            int trgLength_ = srcWidth;
            while ( trgX_ < trgX + trgWidth )
            {
                if ( trgWidth_ < srcWidth )
                    trgLength_ = trgWidth_;
                
                if ( trgX_ + trgLength_ >= x1 )
                    trgLength_ = x1 - trgX_;
                
                int trgX__ = Math.max( x0, trgX_ );
                int trgByteOffset = this.getDataOffset( trgX__, y_ + j );
                int srcPixelOffset = ( trgX__ - trgX_ );
                
                byte[] pixels = combinePixels( srcBuffer, srcPixelOffset * srcPixelSize, srcPixelSize, this, trgPixelSize, trgBuffer, trgByteOffset, trgLength_ - srcPixelOffset, overwrite );
                
                if ( ( srcPixelSize == trgPixelSize ) && overwrite )
                    this.setPixelLine( trgByteOffset, pixels, srcPixelOffset * srcPixelSize, trgLength_ - srcPixelOffset );
                else
                    this.setPixelLine( trgByteOffset, pixels, 0, trgLength_ - srcPixelOffset );
                
                trgX_ += srcWidth;
                trgWidth_ -= srcWidth;
            }
        }
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and honors the alpha channels (if any).
     * 
     * @param srcTI source image
     * @param srcX the rectangle's left to copy from the source {@link TextureImage2D}.
     * @param srcY the rectangle's top to copy from the source {@link TextureImage2D}.
     * @param srcWidth the rectangle's width to copy from the source {@link TextureImage2D}.
     * @param srcHeight the rectangle's height to copy from the source {@link TextureImage2D}.
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     * @param trgWidth the targetWidth (tiled or clipped if necessary)
     * @param trgHeight the targetHeight (tiled or clipped if necessary)
     */
    public final void drawImage( TextureImage2D srcTI, int srcX, int srcY, int srcWidth, int srcHeight, int trgX, int trgY, int trgWidth, int trgHeight )
    {
        copyImageDataFrom( srcTI, srcX, srcY, srcWidth, srcHeight, trgX, trgY, trgWidth, trgHeight, false );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and honors the alpha channels (if any).
     * 
     * @param srcTI source image
     * @param srcX the rectangle's left to copy from the source {@link TextureImage2D}.
     * @param srcY the rectangle's top to copy from the source {@link TextureImage2D}.
     * @param srcWidth the rectangle's width to copy from the source {@link TextureImage2D}.
     * @param srcHeight the rectangle's height to copy from the source {@link TextureImage2D}.
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     */
    public final void drawImage( TextureImage2D srcTI, int srcX, int srcY, int srcWidth, int srcHeight, int trgX, int trgY )
    {
        drawImage( srcTI, srcX, srcY, srcWidth, srcHeight, trgX, trgY, srcWidth, srcHeight );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and honors the alpha channels (if any).
     * 
     * @param ti source image
     * @param srcRect the rectangle to copy from the source {@link TextureImage2D}.
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     */
    public final void drawImage( TextureImage2D ti, Rect2i srcRect, int trgX, int trgY )
    {
        drawImage( ti, srcRect.getLeft(), srcRect.getTop(), srcRect.getWidth(), srcRect.getHeight(), trgX, trgY );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and honors the alpha channels (if any).
     * 
     * @param ti source image
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     */
    public final void drawImage( TextureImage2D ti, int trgX, int trgY )
    {
        drawImage( ti, 0, 0, ti.getOriginalWidth(), ti.getOriginalHeight(), trgX, trgY );
    }
    
    public void fillRectangle( Colorf color, int offsetX, int offsetY, int width, int height )
    {
        if ( !color.hasAlpha() )
        {
            clear( color, offsetX, offsetY, width, height );
            return;
        }
        
        int srcPixelSize = 4;
        byte[] pixel = getPixelLineBuffer1( srcPixelSize );
        
        pixel[ 0 ] = color.getRedByte();
        pixel[ 1 ] = color.getGreenByte();
        pixel[ 2 ] = color.getBlueByte();
        pixel[ 3 ] = (byte)( (byte)255 - color.getAlphaByte() );
        
        int trgPixelSize = this.getPixelSize();
        byte[] trgBuffer = getPixelLineBuffer2( trgPixelSize );
        
        final int x0 = Math.max( clipRect.getLeft(), offsetX );
        final int x1 = Math.min( clipRect.getLeft() + clipRect.getWidth(), offsetX + width );
        final int y0 = Math.max( clipRect.getTop(), offsetY );
        final int y1 = Math.min( clipRect.getTop() + clipRect.getHeight(), offsetY + height );
        final int y_ = getHeight() - getOriginalHeight();
        
        for ( int j = y0; j < y1; j++ )
        {
            for ( int i = x0; i < x1; i++ )
            {
                int trgOffset = this.getDataOffset( i, y_ + j );
                byte[] newPixel = combinePixels( pixel, 0, pixelSize, this, pixelSize, trgBuffer, trgOffset, 1, false );
                this.setPixel( trgOffset, newPixel );
            }
        }
    }
    
    public final void fillFullRectangle( Colorf color )
    {
        fillRectangle( color, 0, 0, getOriginalWidth(), getOriginalHeight() );
    }
    
    public void drawPixelLine( byte[] pixels, int pixelSize, int startX, int startY, int length )
    {
        if ( pixelSize < 4 )
        {
            clearPixelLine( pixels, pixelSize, startX, startY, length );
            return;
        }
        
        if ( ( clipRect.getLeft() > startX + length - 1 ) || ( clipRect.getLeft() + clipRect.getWidth() - 1 < startX ) )
            return;
        
        if ( ( clipRect.getTop() > startY ) || ( clipRect.getTop() + clipRect.getHeight() - 1 < startY ) )
            return;
        
        int trgPixelSize = this.getPixelSize();
        byte[] trgBuffer = getPixelLineBuffer2( trgPixelSize );
        
        final int x0 = Math.max( clipRect.getLeft(), startX );
        final int x1 = Math.min( clipRect.getLeft() + clipRect.getWidth() - 1, startX + length - 1 );
        length = x1 - x0 + 1;
        final int y_ = getHeight() - getOriginalHeight();
        
        int srcByteOffset = ( x0 - startX ) * pixelSize;
        int trgByteOffset = this.getDataOffset( x0, y_ + startY );
        byte[] newPixels = combinePixels( pixels, srcByteOffset, pixelSize, this, trgPixelSize, trgBuffer, trgByteOffset, length, false );
        this.setPixelLine( trgByteOffset, newPixels, srcByteOffset, length );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and simply overwrites anything.
     * 
     * @param srcTI source image
     * @param srcX the rectangle's left to copy from the source {@link TextureImage2D}.
     * @param srcY the rectangle's top to copy from the source {@link TextureImage2D}.
     * @param srcWidth the rectangle's width to copy from the source {@link TextureImage2D}.
     * @param srcHeight the rectangle's height to copy from the source {@link TextureImage2D}.
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     * @param trgWidth the targetWidth (tiled or clipped if necessary)
     * @param trgHeight the targetHeight (tiled or clipped if necessary)
     */
    public final void clear( TextureImage2D srcTI, int srcX, int srcY, int srcWidth, int srcHeight, int trgX, int trgY, int trgWidth, int trgHeight )
    {
        copyImageDataFrom( srcTI, srcX, srcY, srcWidth, srcHeight, trgX, trgY, trgWidth, trgHeight, true );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and simply overwrites anything.
     * 
     * @param srcTI source image
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     * @param trgWidth the targetWidth (tiled or clipped if necessary)
     * @param trgHeight the targetHeight (tiled or clipped if necessary)
     */
    public final void clear( TextureImage2D srcTI, int trgX, int trgY, int trgWidth, int trgHeight )
    {
        copyImageDataFrom( srcTI, 0, 0, srcTI.getOriginalWidth(), srcTI.getOriginalHeight(), trgX, trgY, trgWidth, trgHeight, true );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and simply overwrites anything.
     * 
     * @param srcTI source image
     * @param srcX the rectangle's left to copy from the source {@link TextureImage2D}.
     * @param srcY the rectangle's top to copy from the source {@link TextureImage2D}.
     * @param srcWidth the rectangle's width to copy from the source {@link TextureImage2D}.
     * @param srcHeight the rectangle's height to copy from the source {@link TextureImage2D}.
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     */
    public final void clear( TextureImage2D srcTI, int srcX, int srcY, int srcWidth, int srcHeight, int trgX, int trgY )
    {
        clear( srcTI, srcX, srcY, srcWidth, srcHeight, trgX, trgY, srcWidth, srcHeight );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and simply overwrites anything.
     * 
     * @param srcTI source image
     * @param srcRect the rectangle to copy from the source {@link TextureImage2D}.
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     */
    public final void clear( TextureImage2D srcTI, Rect2i srcRect, int trgX, int trgY )
    {
        clear( srcTI, srcRect.getLeft(), srcRect.getTop(), srcRect.getWidth(), srcRect.getHeight(), trgX, trgY );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and simply overwrites anything.
     * 
     * @param srcTI source image
     * @param trgX target x-coordinate
     * @param trgY target y-coordinate
     */
    public final void clear( TextureImage2D srcTI, int trgX, int trgY )
    {
        clear( srcTI, 0, 0, srcTI.getOriginalWidth(), srcTI.getOriginalHeight(), trgX, trgY );
    }
    
    /**
     * Draws the given {@link TextureImage2D} onto this one and simply overwrites anything.
     * 
     * @param ti source image
     */
    public final void clear( TextureImage2D srcTI )
    {
        clear( srcTI, 0, 0, srcTI.getOriginalWidth(), srcTI.getOriginalHeight(), 0, 0 );
    }
    
    public void clear( Colorf color, int offsetX, int offsetY, int width, int height )
    {
        //byte[] pixel = getPixelLineBuffer1( width * this.getPixelSize() );
        byte[] pixel = getPixelLineBuffer1( this.getPixelSize() );
        
        switch ( this.getPixelSize() )
        {
            case 4:
                pixel[ 0 ] = color.getRedByte();
                pixel[ 1 ] = color.getGreenByte();
                pixel[ 2 ] = color.getBlueByte();
                pixel[ 3 ] = (byte)( (byte)255 - color.getAlphaByte() );
                break;
            case 3:
                pixel[ 0 ] = color.getRedByte();
                pixel[ 1 ] = color.getGreenByte();
                pixel[ 2 ] = color.getBlueByte();
                break;
            case 2:
                pixel[ 0 ] = color.getRedByte();
                pixel[ 1 ] = (byte)( (byte)255 - color.getAlphaByte() );
                break;
            case 1:
                pixel[ 0 ] = (byte)( (byte)255 - color.getAlphaByte() );
                break;
        }
        
        final int x0 = Math.max( clipRect.getLeft(), offsetX );
        final int x1 = Math.min( clipRect.getLeft() + clipRect.getWidth(), offsetX + width );
        final int y0 = Math.max( clipRect.getTop(), offsetY );
        final int y1 = Math.min( clipRect.getTop() + clipRect.getHeight(), offsetY + height );
        final int y_ = getHeight() - getOriginalHeight();
        
        for ( int j = y0; j < y1; j++ )
        {
            for ( int i = x0; i < x1; i++ )
            {
                this.setPixel( i, y_ + j, pixel );
            }
        }
    }
    
    public final void clear( Colorf color )
    {
        clear( color, 0, 0, getOriginalWidth(), getOriginalHeight() );
    }
    
    public void clearPixelLine( byte[] pixels, int pixelSize, int startX, int startY, int length )
    {
        if ( ( clipRect.getLeft() > startX + length - 1 ) || ( clipRect.getLeft() + clipRect.getWidth() - 1 < startX ) )
            return;
        
        if ( ( clipRect.getTop() > startY ) || ( clipRect.getTop() + clipRect.getHeight() - 1 < startY ) )
            return;
        
        final int x0 = Math.max( clipRect.getLeft(), startX );
        final int x1 = Math.min( clipRect.getLeft() + clipRect.getWidth() - 1, startX + length - 1 );
        length = x1 - x0 + 1;
        final int y_ = getHeight() - getOriginalHeight();
        
        int srcByteOffset = ( x0 - startX ) * pixelSize;
        int trgByteOffset = this.getDataOffset( x0, y_ + startY );
        this.setPixelLine( trgByteOffset, pixels, srcByteOffset, length );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        //super.duplicateNodeComponent( original, forceDuplicate );
        throw new UnsupportedOperationException( "Not implemented yet" );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TextureImage2D cloneNodeComponent( boolean forceDuplicate )
    {
        throw new UnsupportedOperationException( "Not implemented yet" );
    }
    
    
    public TextureImage2D( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, boolean yUp, TextureImageInternalFormat internalFormat )
    {
        super( format, width, height, orgWidth, orgHeight, internalFormat );
        
        setClipRect( 0, 0, width, height );
        
        this.pixelSize = format.getPixelSize();
        
        this.yUp = yUp;
        
        setHasData( false );
        
        texCoordUR.set( (float)orgWidth / (float)width, (float)orgHeight / (float)height );
    }
    
    public TextureImage2D( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, boolean yUp )
    {
        this( format, width, height, orgWidth, orgHeight, yUp, TextureImageInternalFormat.getFallbackInternalFormat( format ) );
    }
    
    protected TextureImage2D( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, byte[] data, int dataLength, boolean useBuffer, TextureImageInternalFormat internalFormat )
    {
        this( format, width, height, orgWidth, orgHeight, false, internalFormat );
        
        if ( format.isCompressed() )
        {
            //final int imgSize = calculateNeededImageSize();
            final int imgSize = dataLength;
            //this.dataBuffer = BufferUtils.createByteBuffer( imgSize );
            //dataBuffer.put( data, 0, dataLength );
            //dataBuffer.flip();
            //this.data = new byte[ imgSize ];
            this.pixelSize = imgSize / ( width * height );
            setHasData( true );
        }
        else
        {
            if ( dataLength <= 0 )
            {
                this.data = null;
                this.dataBuffer = null;
                
                setHasData( false );
            }
        }
        
        setImageData( data, dataLength, useBuffer );
    }
    
    public TextureImage2D( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, byte[] data, int dataLength, TextureImageInternalFormat internalFormat )
    {
        this( format, width, height, orgWidth, orgHeight, data, dataLength, true, internalFormat );
    }
    
    public TextureImage2D( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, byte[] data, TextureImageInternalFormat internalFormat )
    {
        this( format, width, height, orgWidth, orgHeight, data, ( data != null ) ? data.length : 0, true, internalFormat );
    }
    
    /**
     * Constructs a new {@link TextureImage2D} object.
     */
    public TextureImage2D( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, byte[] data, int dataLength )
    {
        this( format, width, height, orgWidth, orgHeight, data, dataLength, true, TextureImageInternalFormat.getFallbackInternalFormat( format ) );
    }
    
    /**
     * Constructs a new {@link TextureImage2D} object.
     */
    public TextureImage2D( TextureImageFormat format, int width, int height, int orgWidth, int orgHeight, byte[] data )
    {
        this( format, width, height, orgWidth, orgHeight, data, ( data != null ) ? data.length : 0 );
    }
    
    public TextureImage2D( TextureImageFormat format, int width, int height, boolean yUp, TextureImageInternalFormat internalFormat )
    {
        this( format, width, height, width, height, yUp, internalFormat );
    }
    
    public TextureImage2D( TextureImageFormat format, int width, int height, boolean yUp )
    {
        this( format, width, height, width, height, yUp );
    }
    
    protected TextureImage2D( TextureImageFormat format, int width, int height, byte[] data, int dataLength, boolean useBuffer, TextureImageInternalFormat internalFormat )
    {
        this( format, width, height, width, height, data, dataLength, useBuffer, internalFormat );
    }
    
    public TextureImage2D( TextureImageFormat format, int width, int height, byte[] data, int dataLength, TextureImageInternalFormat internalFormat )
    {
        this( format, width, height, width, height, data, dataLength, internalFormat );
    }
    
    public TextureImage2D( TextureImageFormat format, int width, int height, byte[] data, TextureImageInternalFormat internalFormat )
    {
        this( format, width, height, width, height, data, internalFormat );
    }
    
    /**
     * Constructs a new {@link TextureImage2D} object.
     */
    public TextureImage2D( TextureImageFormat format, int width, int height, byte[] data, int dataLength )
    {
        this( format, width, height, width, height, data, dataLength );
    }
    
    /**
     * Constructs a new {@link TextureImage2D} object.
     */
    public TextureImage2D( TextureImageFormat format, int width, int height, byte[] data )
    {
        this( format, width, height, width, height, data );
    }
    
    public TextureImage2D( TextureImageFormat format, int orgWidth, int orgHeight, BufferedImage image, boolean yUp )
    {
        this( format, image.getWidth(), image.getHeight(), orgWidth, orgHeight, yUp );
        
        setImageData( image, true );
    }
    
    public TextureImage2D( TextureImageFormat format, BufferedImage image, boolean yUp )
    {
        this( format, image.getWidth(), image.getHeight(), image, yUp );
    }
    
    public TextureImage2D( TextureImageFormat format, BufferedImage image )
    {
        this( format, image.getWidth(), image.getHeight(), image, false );
    }
}
