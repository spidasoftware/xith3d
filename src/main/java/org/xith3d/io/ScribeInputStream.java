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
import org.openmali.vecmath2.*;
import org.xith3d.utility.logging.X3DLog;
import org.xith3d.utility.memory.NioMemoryBuffer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * Stream for reading cosm objects.
 * 
 * :Id: ScribeInputStream.java,v 1.10 2003/02/24 00:13:44 wurp Exp $
 * 
 * :Log: ScribeInputStream.java,v $ Revision 1.10 2003/02/24 00:13:44 wurp
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
public class ScribeInputStream extends DataInputStream
{
    private static final boolean USE_NEW = false;
    
    private ArrayList< String > map = new ArrayList< String >();
    
    // used for super fast reads of float arrays
    private byte[] byteBuffer = null;
    private FloatBuffer floatBuffer = null;
    private ByteBuffer bBuffer = null;
    private IntBuffer intBuffer = null;
    private ShortBuffer shortBuffer = null;
    private boolean readHeaderOnly = false;
    
    public ScribeInputStream( InputStream in )
    {
        super( in );
    }
    
    /**
     * Setting this to true will allow objects being loaded to only load their
     * header information.
     * 
     * @param header
     */
    public void setReadHeaderOnly( boolean header )
    {
        this.readHeaderOnly = header;
    }
    
    public boolean shouldReadHeaderOnly()
    {
        return ( this.readHeaderOnly );
    }
    
    private boolean clutchByteBuffer( int size )
    {
        if ( size < 1000 )
        {
            size = 1000;
        }
        
        if ( bBuffer != null )
        {
            if ( bBuffer.capacity() >= size )
            {
                bBuffer.rewind();
                return false;
            }
        }
        
        byteBuffer = new byte[ size ];
        bBuffer = ByteBuffer.wrap( byteBuffer );
        floatBuffer = bBuffer.asFloatBuffer();
        intBuffer = bBuffer.asIntBuffer();
        
        return ( true );
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
        // Log.print( "Clutching " + size + " floats" );
        clutchByteBuffer( size * 4 );
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
    }
    
    private void clutchShortBuffer( int size )
    {
        clutchByteBuffer( size * 2 );
        shortBuffer = bBuffer.asShortBuffer();
    }
    
    /**
     * reads in a scribable node by getting the class name from the stream,
     * building the object and calling its scribable method to load it.
     */
    public Scribable readScribable() throws java.io.IOException, InvalidFormat
    {
        int c = readInt();
        String className = null;
        
        if ( c == ScribeOutputStream.CLASS_DEF )
        {
            className = readUTF();
            map.add( className );
            
            X3DLog.debug( "Reading class ",  className );
        }
        else
        {
            int ref = readInt();
            
            X3DLog.debug( "Reading class ref ",  ref );
            
            className = map.get( ref );
            
            X3DLog.debug( "  Class is ",  className );
        }
        
        // now instantiate the object using its default constructor
        Scribable s = null;
        
        try
        {
            s = (Scribable)Class.forName( className ).newInstance();
            s.load( this );
        }
        catch ( java.lang.ClassNotFoundException e1 )
        {
            X3DLog.print( e1 );
            throw new InvalidFormat( e1.getMessage() );
        }
        catch ( java.lang.IllegalAccessException e2 )
        {
            X3DLog.print( e2 );
            throw new InvalidFormat( e2.getMessage() );
        }
        catch ( java.lang.InstantiationException e3 )
        {
            X3DLog.print( e3 );
            throw new InvalidFormat( e3.getMessage() );
        }
        
        return ( s );
    }
    
    /**
     * Reads in the data into the array. An exception is raised if The data
     * array is not big enough.
     */
    private void readFloatArray( float[] data, int size ) throws java.io.IOException
    {
        if ( data.length < size )
        {
            IOException e = new IOException( "Array not big enough to read " + size + " data elements" );
            
            X3DLog.print( e );
            throw e;
        }
        
        // attempt to do this with NIO for speed
        if ( Scribe.useNIO )
        {
            if ( !USE_NEW )
            {
                clutchFloatBuffer( size );
                
                readFully( byteBuffer, 0, size * 4 );
                floatBuffer.rewind();
                floatBuffer.get( data, 0, size );
            }
            else
            {
                NioMemoryBuffer< ? > m = NioMemoryBuffer.getInstance( size * 4 );
                readFully( m.getByteArray(), 0, size * 4 );
                m.getFloatBuffer().rewind();
                m.getFloatBuffer().get( data, 0, size );
                m.returnInstance();
            }
        }
        else
        {
            for ( int i = 0; i < size; i++ )
                data[ i ] = readFloat();
        }
    }
    
    /**
     * Reads in an array of float and return the number of floats read. The data
     * array must be big enough to read in all the floats in the array
     */
    public int readFloatArray( float[] data ) throws java.io.IOException
    {
        int size = readInt();
        readFloatArray( data, size );
        
        return ( size );
    }
    
    /**
     * Allocates a float array and loads it from the stream
     */
    public float[] readFloatArray() throws java.io.IOException
    {
        int size = readInt();
        float[] data = new float[ size ];
        readFloatArray( data, size );
        
        return ( data );
    }
    
    /**
     * Reads in the data into the array. An exception is raised if The data
     * array is not big enough.
     */
    private void readByteArray( byte[] data, int size ) throws java.io.IOException
    {
        if ( data.length < size )
        {
            throw new java.io.IOException( "Array not big enough to read " + size + " data elements" );
        }
        
        // attempt to do this with NIO for speed
        if ( Scribe.useNIO )
        {
            readFully( data, 0, size );
        }
        else
        {
            for ( int i = 0; i < size; i++ )
                data[ i ] = readByte();
        }
    }
    
    /**
     * Reads in an array of byte and return the number of bytes read. The data
     * array must be big enough to read in all the bytes in the array
     */
    public int readByteArray( byte[] data ) throws java.io.IOException
    {
        int size = readInt();
        readByteArray( data, size );
        
        return ( size );
    }
    
    /**
     * Allocates a byte array and loads it from the stream
     */
    public byte[] readByteArray() throws java.io.IOException
    {
        int size = readInt();
        byte[] data = new byte[ size ];
        readByteArray( data, size );
        
        return ( data );
    }
    
    private void readVectorArray( Vector3f[] data, int size ) throws java.io.IOException
    {
        if ( data.length < size )
        {
            throw new java.io.IOException( "Array not big enough to read " + size + " data elements" );
        }
        
        for ( int i = 0; i < size; i++ )
            data[ i ] = readVector();
    }
    
    /**
     * Reads in an array of vectors and return the number of vectors read. The
     * data array must be big enough to read in all the vectors in the array
     */
    public int readVectorArray( Vector3f[] data ) throws java.io.IOException
    {
        int size = readInt();
        readVectorArray( data, size );
        
        return ( size );
    }
    
    /**
     * Allocates a float array and loads it from the stream
     */
    public Vector3f[] readVectorArray() throws java.io.IOException
    {
        int size = readInt();
        Vector3f[] data = new Vector3f[ size ];
        readVectorArray( data, size );
        
        return ( data );
    }
    
    /**
     * Reads in the data into the array. An exception is raised if The data
     * array is not big enough.
     */
    private void readIntArray( int[] data, int size ) throws java.io.IOException
    {
        if ( data.length < size )
        {
            throw new java.io.IOException( "Array not big enough to read " + size + " data elements" );
        }
        
        if ( Scribe.useNIO )
        {
            if ( !USE_NEW )
            {
                clutchIntBuffer( size );
                
                readFully( byteBuffer, 0, size * 4 );
                intBuffer.rewind();
                intBuffer.get( data, 0, size );
            }
            else
            {
                NioMemoryBuffer< ? > m = NioMemoryBuffer.getInstance( size * 4 );
                byte[] a = m.getByteArray();
                
                for ( int i = 0; i < a.length; i++ )
                    a[ i ] = 0;
                
                readFully( m.getByteArray(), 0, size * 4 );
                m.getIntBuffer().rewind();
                m.getIntBuffer().get( data, 0, size );
                m.returnInstance();
            }
        }
        else
        {
            for ( int i = 0; i < size; i++ )
                data[ i ] = readInt();
        }
    }
    
    private void readShortArray( short[] data, int size ) throws java.io.IOException
    {
        if ( data.length < size )
        {
            throw new java.io.IOException( "Array not big enough to read " + size + " data elements" );
        }
        
        if ( Scribe.useNIO )
        {
            clutchShortBuffer( size );
            
            readFully( byteBuffer, 0, size * 2 );
            shortBuffer.get( data, 0, size );
        }
        else
        {
            for ( int i = 0; i < size; i++ )
                data[ i ] = readShort();
        }
    }
    
    /**
     * Reads in an array of float and return the number of floats read. The data
     * array must be big enough to read in all the floats in the array
     */
    public int readIntArray( int[] data ) throws java.io.IOException
    {
        int size = readInt();
        
        // Log.print("Reading and int array of size "+size);
        readIntArray( data, size );
        
        return ( size );
    }
    
    /**
     * Reads in an array of float and return the number of floats read. The data
     * array must be big enough to read in all the floats in the array
     */
    public int readShortArray( short[] data ) throws java.io.IOException
    {
        int size = readInt();
        
        // Log.print( "Reading and int array of size " + size );
        readShortArray( data, size );
        
        return ( size );
    }
    
    /**
     * Allocates a float array and loads it from the stream
     */
    public int[] readIntArray() throws java.io.IOException
    {
        int size = readInt();
        
        int[] data = new int[ size ];
        readIntArray( data, size );
        
        return ( data );
    }
    
    public short[] readShortArray() throws java.io.IOException
    {
        int size = readInt();
        
        short[] data = new short[ size ];
        readShortArray( data, size );
        
        return ( data );
    }
    
    public void readPoint( Point3f p ) throws java.io.IOException
    {
        p.setX( readFloat() );
        p.setY( readFloat() );
        p.setZ( readFloat() );
    }
    
    public void readVector( Vector3f v ) throws java.io.IOException
    {
        v.setX( readFloat() );
        v.setY( readFloat() );
        v.setZ( readFloat() );
    }
    
    public Vector3f readVector() throws java.io.IOException
    {
        Vector3f v = new Vector3f();
        readVector( v );
        
        return ( v );
    }
    
    public void readColor3f( Colorf v ) throws java.io.IOException
    {
        v.setRed( readFloat() );
        v.setGreen( readFloat() );
        v.setBlue( readFloat() );
    }
    
    public Colorf readColor3f() throws java.io.IOException
    {
        Colorf v = new Colorf();
        readColor3f( v );
        
        return ( v );
    }
    
    public void readQuat( Quaternion4f v ) throws java.io.IOException
    {
        v.setA( readFloat() );
        v.setB( readFloat() );
        v.setC( readFloat() );
        v.setD( readFloat() );
    }
    
    public Quaternion4f readQuat4f() throws java.io.IOException
    {
        Quaternion4f v = new Quaternion4f();
        readQuat( v );
        
        return ( v );
    }
    
    public void readMatrix( Matrix3f v ) throws java.io.IOException
    {
        v.m00( readFloat() );
        v.m01( readFloat() );
        v.m02( readFloat() );
        
        v.m10( readFloat() );
        v.m11( readFloat() );
        v.m12( readFloat() );
        
        v.m20( readFloat() );
        v.m21( readFloat() );
        v.m22( readFloat() );
    }
    
    public void readMatrix( Matrix4f v ) throws java.io.IOException
    {
        v.m00( readFloat() );
        v.m01( readFloat() );
        v.m02( readFloat() );
        v.m03( readFloat() );
        
        v.m10( readFloat() );
        v.m11( readFloat() );
        v.m12( readFloat() );
        v.m13( readFloat() );
        
        v.m20( readFloat() );
        v.m21( readFloat() );
        v.m22( readFloat() );
        v.m23( readFloat() );
        
        v.m30( readFloat() );
        v.m31( readFloat() );
        v.m32( readFloat() );
        v.m33( readFloat() );
    }
    
    public Matrix3f readMatrix3f() throws java.io.IOException
    {
        Matrix3f m = new Matrix3f();
        readMatrix( m );
        
        return ( m );
    }
    
    public Matrix4f readMatrix4f() throws java.io.IOException
    {
        Matrix4f m = new Matrix4f();
        readMatrix( m );
        
        return ( m );
    }
    
    public void readTexCoord( TexCoord2f v ) throws java.io.IOException
    {
        v.setS( readFloat() );
        v.setT( readFloat() );
    }
    
    public TexCoord2f readTexCoord() throws java.io.IOException
    {
        TexCoord2f c = new TexCoord2f();
        readTexCoord( c );
        
        return ( c );
    }
    
    public BufferedImage readImage() throws java.io.IOException
    {
        BufferedImage b = null;
        int w = readInt();
        
        if ( w != 0 )
        {
            int h = readInt();
            int ttype = readInt();
            int[] pixels = readIntArray();
            b = new BufferedImage( w, h, ttype );
            b.setRGB( 0, 0, w, h, pixels, 0, w );
        }
        
        return ( b );
    }
    
    public DirectBufferedImage readDirectImage() throws java.io.IOException
    {
        DirectBufferedImage dbi = null;
        int w = readInt();
        
        if ( w != 0 )
        {
            int h = readInt();
            int ttype = readInt();
            
            switch ( ttype )
            {
                case 0: //DirectBufferedImage.Type.DIRECT_RGB
                    dbi = DirectBufferedImage.makeDirectImageRGB( w, h );
                    
                    break;
                
                case 1: //DirectBufferedImage.Type.DIRECT_RGBA
                    dbi = DirectBufferedImage.makeDirectImageRGBA( w, h );
                    
                    break;
                
                case 2: //DirectBufferedImage.Type.DIRECT_GRAY
                    dbi = DirectBufferedImage.makeDirectImageOneByte( w, h );
                    
                    break;
            }
            
            //readFully( b.getBackingStore(), 0, b.getBackingStore().length );
            
            final int numBytes = dbi.getNumBytes();
            final ByteBuffer bb = dbi.getByteBuffer();
            for ( int i = 0; i < numBytes; i++ )
            {
                byte b = readByte();
                
                bb.put( i, b );
            }
        }
        
        return ( dbi );
    }
    
    /**
     * Reads in the image which has been stored in a lossy compression format
     * 
     * @return BufferedImage
     * @throws IOException
     */
    public BufferedImage readLossyImage() throws java.io.IOException
    {
        BufferedImage b = null;
        int w = readInt();
        
        if ( w != 0 )
        {
            int size = readInt();
            
            byte[] buf = new byte[ size ];
            readFully( buf );
            
            ByteArrayInputStream in = new ByteArrayInputStream( buf, 0, size );

			b = ImageIO.read(in);
//			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder( in );
//            b = decoder.decodeAsBufferedImage();
        }
        
        return ( b );
    }
}
