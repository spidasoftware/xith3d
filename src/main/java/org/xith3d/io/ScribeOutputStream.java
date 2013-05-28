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
package org.xith3d.io;

import org.jagatoo.image.DirectBufferedImage;
import org.jagatoo.util.image.ImageUtility;
import org.openmali.vecmath2.*;
import org.xith3d.utility.logging.X3DLog;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * An output stream derived from DataOutputStream for writing out cosm objects
 * to streams. ExtScribable objects which are written to the stream have their
 * class type stored so they can be rebuilt when they are read. Each class name
 * will be only stored once per file to reduce storage.
 * 
 * :Id: ScribeOutputStream.java,v 1.10 2003/02/24 00:13:44 wurp Exp $
 * 
 * :Log: ScribeOutputStream.java,v $ Revision 1.10 2003/02/24 00:13:44 wurp
 * Formatted all java code for cvs (strictSunConvention.xml)
 * 
 * Revision 1.9 2002/09/11 00:53:53 dilvish Big commit of new changes
 * 
 * Revision 1.8 2002/01/23 04:38:33 wurp Integrating i18n code
 * 
 * Revision 1.7 2002/01/20 05:26:02 dilvish JDK 1.4 port
 * 
 * Revision 1.6 2002/01/15 03:36:55 dilvish Improvements to the animation system
 * 
 * Revision 1.5 2002/01/12 03:16:27 dilvish Added read/write of buffered images
 * 
 * Revision 1.4 2001/11/17 03:13:02 dilvish Added the ability to read/write
 * arrays of vectors
 * 
 * Revision 1.3 2001/07/14 14:48:10 wurp New animation, sky and avatar movement
 * 
 * Revision 1.2 2001/06/20 04:05:41 wurp added log4j.
 * 
 * Revision 1.1 2001/06/06 22:42:37 wizofid Massive commit / reintegration
 * 
 * @author David Yazel
 */
public class ScribeOutputStream extends DataOutputStream
{
    protected static final int CLASS_REF = 10000;
    protected static final int CLASS_DEF = 10001;
    
    private Map< String, Integer > map = new HashMap< String, Integer >();
    
    // used for super fast reads of float arrays
    private byte[] byteBuffer = null;
    private FloatBuffer floatBuffer = null;
    private ByteBuffer bBuffer = null;
    private IntBuffer intBuffer = null;
    private ShortBuffer shortBuffer = null;
    
    public ScribeOutputStream( OutputStream stream )
    {
        super( stream );
    }
    
    private void clutchByteBuffer( int size )
    {
        if ( bBuffer != null )
        {
            if ( bBuffer.capacity() >= size )
            {
                return;
            }
        }
        
        byteBuffer = new byte[ size ];
        bBuffer = ByteBuffer.wrap( byteBuffer );
        floatBuffer = null;
        intBuffer = null;
        shortBuffer = null;
    }
    
    /**
     * Internal routine that creates a float buffer big enough to hold the
     * number of floats specified. If the buffer is already big enough then it
     * rewinds the buffer and returns
     * 
     * @param size Number of floats to make room for
     */
    private void clutchFloatBuffer( int size )
    {
        clutchByteBuffer( size * 4 );
        
        if ( floatBuffer != null )
        {
            if ( floatBuffer.capacity() >= size )
            {
                floatBuffer.rewind();
                
                return;
            }
        }
        
        floatBuffer = bBuffer.asFloatBuffer();
    }
    
    /**
     * Internal routine that creates an int buffer big enough to hold the number
     * of ints specified. If the buffer is already big enough then it rewinds
     * the buffer and returns
     * 
     * @param size Number of floats to make room for
     */
    private void clutchIntBuffer( int size )
    {
        clutchByteBuffer( size * 4 );
        
        if ( intBuffer != null )
        {
            if ( intBuffer.capacity() >= size )
            {
                intBuffer.rewind();
                
                return;
            }
        }
        
        intBuffer = bBuffer.asIntBuffer();
    }
    
    private void clutchShortBuffer( int size )
    {
        clutchByteBuffer( size * 2 );
        
        if ( shortBuffer != null )
        {
            if ( shortBuffer.capacity() >= size )
            {
                shortBuffer.rewind();
                
                return;
            }
        }
        
        shortBuffer = bBuffer.asShortBuffer();
    }
    
    /**
     * Writes a scribable node to the output stream. Either the class or a class
     * reference is written to the output file, followed by the scribed object.
     */
    public void writeScribable( Scribable o ) throws java.io.IOException, UnscribableNodeEncountered
    {
        String key = o.getClass().getName();
        
        X3DLog.debug( "Saving class ", key );
        
        Integer ref = map.get( key );
        
        if ( ref == null )
        {
            ref = new Integer( map.size() );
            
            X3DLog.debug( "   Not found, assigning to ref ",  ref );
            
            map.put( key, ref );
            writeInt( CLASS_DEF );
            writeUTF( key );
        }
        else
        {
            X3DLog.debug( "   Found, assigning to ref ",  ref );
            
            writeInt( CLASS_REF );
            writeInt( ref.intValue() );
        }
        
        o.save( this );
    }
    
    /**
     * Writes out an array of float, using the number of elements specified
     */
    public void writeFloatArray( float[] data, int len ) throws java.io.IOException
    {
        writeInt( len );
        
        if ( Scribe.useNIO )
        {
            clutchFloatBuffer( len );
            floatBuffer.put( data, 0, len );
            this.write( byteBuffer, 0, len * 4 );
        }
        else
        {
            for (int i = 0; i < len; i++)
                writeFloat( data[i] );
        }
    }
    
    /**
     * Writes out a complete array of float
     */
    public void writeFloatArray( float[] data ) throws java.io.IOException
    {
        writeFloatArray( data, data.length );
    }
    
    /**
     * Writes out an array of byte, using the number of elements specified
     */
    public void writeByteArray( byte[] data, int len ) throws java.io.IOException
    {
        writeInt( len );
        
        if ( Scribe.useNIO )
        {
            this.write( data, 0, len );
        }
        else
        {
            for (int i = 0; i < len; i++)
                writeByte( data[i] );
        }
    }
    
    /**
     * Writes out a complete array of byte
     */
    public void writeByteArray( byte[] data ) throws java.io.IOException
    {
        writeByteArray( data, data.length );
    }
    
    /**
     * Writes out an array of float, using the number of elements specified
     */
    public void writeVectorArray( Vector3f[] data, int len ) throws java.io.IOException
    {
        writeInt( len );
        
        for ( int i = 0; i < len; i++ )
            writeVector( data[i] );
    }
    
    /**
     * Writes out a complete array of float
     */
    public void writeVectorArray( Vector3f[] data ) throws java.io.IOException
    {
        writeVectorArray( data, data.length );
    }
    
    /**
     * Writes out an array of int, using the number of elements specified
     */
    public void writeIntArray( int[] data, int len ) throws java.io.IOException
    {
        writeInt( len );
        
        if ( Scribe.useNIO )
        {
            clutchIntBuffer( len );
            intBuffer.put( data, 0, len );
            this.write( byteBuffer, 0, len * 4 );
        }
        else
        {
            for ( int i = 0; i < len; i++ )
                writeInt( data[i] );
        }
    }
    
    public void writeShortArray( short[] data, int len ) throws java.io.IOException
    {
        writeInt( len );
        
        if ( Scribe.useNIO )
        {
            clutchShortBuffer( len );
            shortBuffer.put( data, 0, len );
            this.write( byteBuffer, 0, len * 2 );
        }
        else
        {
            for (int i = 0; i < len; i++)
                writeFloat( data[i] );
        }
    }
    
    /**
     * Writes out a complete array of int
     */
    public void writeIntArray( int[] data ) throws java.io.IOException
    {
        writeIntArray( data, data.length );
    }
    
    public void writeShortArray( short[] data ) throws java.io.IOException
    {
        writeShortArray( data, data.length );
    }
    
    /**
     * Writes out a string
     */
    public void writeString( String s ) throws java.io.IOException
    {
        writeInt( s.length() );
        writeBytes( s );
    }
    
    public void writePoint( Point3f p ) throws java.io.IOException
    {
        writeFloat( p.getX() );
        writeFloat( p.getY() );
        writeFloat( p.getZ() );
    }
    
    public void writeVector( Vector3f v ) throws java.io.IOException
    {
        writeFloat( v.getX() );
        writeFloat( v.getY() );
        writeFloat( v.getZ() );
    }
    
    public void writeColor3f( Colorf v ) throws java.io.IOException
    {
        writeFloat( v.getRed() );
        writeFloat( v.getGreen() );
        writeFloat( v.getBlue() );
    }
    
    public void writeQuat( Quaternion4f v ) throws java.io.IOException
    {
        writeFloat( v.getA() );
        writeFloat( v.getB() );
        writeFloat( v.getC() );
        writeFloat( v.getD() );
    }
    
    public void writeTexCoord( TexCoord2f v ) throws java.io.IOException
    {
        writeFloat( v.getS() );
        writeFloat( v.getT() );
    }
    
    public void writeMatrix( Matrix3f v ) throws java.io.IOException
    {
        writeFloat( v.m00() );
        writeFloat( v.m01() );
        writeFloat( v.m02() );
        
        writeFloat( v.m10() );
        writeFloat( v.m11() );
        writeFloat( v.m12() );
        
        writeFloat( v.m20() );
        writeFloat( v.m21() );
        writeFloat( v.m22() );
    }
    
    public void writeMatrix( Matrix4f v ) throws java.io.IOException
    {
        writeFloat( v.m00() );
        writeFloat( v.m01() );
        writeFloat( v.m02() );
        writeFloat( v.m03() );
        
        writeFloat( v.m10() );
        writeFloat( v.m11() );
        writeFloat( v.m12() );
        writeFloat( v.m13() );
        
        writeFloat( v.m20() );
        writeFloat( v.m21() );
        writeFloat( v.m22() );
        writeFloat( v.m23() );
        
        writeFloat( v.m30() );
        writeFloat( v.m31() );
        writeFloat( v.m32() );
        writeFloat( v.m33() );
    }
    
    public void writeDirectImage( DirectBufferedImage dbi ) throws IOException
    {
        if ( dbi != null )
        {
            int w = dbi.getWidth();
            int h = dbi.getHeight();
            writeInt( w );
            writeInt( h );
            writeInt( dbi.getDirectType().ordinal() );
            
            byte[] bytes = ImageUtility.toByteArray( dbi );
            
            write( bytes, 0, bytes.length );
        }
        else
        {
            writeInt( 0 );
        }
    }
    
    /**
     * writes out a buffered image. It is acceptable to pass in null since a
     * byte is written to determine if there is an image or not. This writes out
     * the raw image data, which can be a lot of disk space since there is no
     * special compression being done for images.
     */
    public void writeImage( BufferedImage b ) throws java.io.IOException
    {
        if ( b != null )
        {
            int w = b.getWidth();
            int h = b.getHeight();
            int[] pixels = new int[ w * h ];
            b.getRGB( 0, 0, w, h, pixels, 0, w );
            writeInt( w );
            writeInt( h );
            writeInt( b.getType() );
            writeIntArray( pixels );
        }
        else
        {
            writeInt( 0 );
        }
    }
    
    /**
     * Writes the buffered image to the file using lossy JPG compression.
     * 
     * @param b The image to save
     * @param quality The quality of the output
     * @throws IOException
     */
    public void writeLossyImage( BufferedImage b, float quality ) throws java.io.IOException
    {
        if ( b != null )
        {
            writeInt( 1 ); // flag as non-null
            
            int w = b.getWidth();
            int h = b.getHeight();
            
            ByteArrayOutputStream out = new ByteArrayOutputStream( w * h * 4 );
			ImageIO.write(b, "JPG", out);

			byte[] buf = out.toByteArray();
            int size = out.size();
            
            writeInt( size );
            write( buf, 0, size );
            out.close();
        }
        else
        {
            writeInt( 0 );
        }
    }
}
