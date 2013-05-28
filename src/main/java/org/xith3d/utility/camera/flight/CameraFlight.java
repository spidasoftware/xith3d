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
package org.xith3d.utility.camera.flight;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.Transformable;

/**
 * This class can be used to replay a pre-recorded camera flight.
 * 
 * @see CameraFlightRecorder
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CameraFlight
{
    public enum Format
    {
        UNCOMPRESSED( (byte)0 ),
        COMPRESSED( (byte)1 );
        
        private byte b;
        
        public byte getByte()
        {
            return ( b );
        }
        
        private Format( byte b )
        {
            this.b = b;
        }
        
        public static Format get( byte b )
        {
            switch ( b )
            {
                case 0:
                    return ( Format.UNCOMPRESSED  );
                case 1:
                    return ( Format.COMPRESSED  );
                default:
                    throw new IllegalArgumentException( "Unknown type " + b );
            }
        }
    }
    
    public class InterpolationPoint
    {
        public Matrix3f rot;
        public Point3f pos;
        public float deltaTime;
    }
    
    private List< InterpolationPoint > interPoints;
    private long startTime;
    private Matrix3f camRot;
    
    private float t0i, t0t;
    private float d;
    private int i1;
    private int frames;
    
    private InterpolationPoint ip1, ip2;
    
    private List< CameraFlightListener > listeners = new ArrayList< CameraFlightListener >( 1 );
    
    /**
     * Adds a new CameraFlightListener to the list
     * 
     * @param l the new listener to be added
     */
    public void addCameraFlightListener( CameraFlightListener l )
    {
        listeners.add( l );
    }
    
    /**
     * Remvoes a CameraFlightListener from the list
     * 
     * @param l the listener to be removed
     */
    public void removeCameraFlightListener( CameraFlightListener l )
    {
        listeners.remove( l );
    }
    
    /**
     * Interpolates View rotation and position.
     * 
     * @param cam the View to be updated
     * @param gameTime the current game time
     * @param timingMode
     */
    public void updateCamera( Transformable cam, long gameTime, TimingMode timingMode )
    {
        t0t = (float)timingMode.getMicroSeconds( gameTime - startTime );
        
        ip1 = interPoints.get( i1 );
        
        while ( t0t > ( t0i + ip1.deltaTime ) )
        {
            t0i += ip1.deltaTime;
            
            if ( i1 + 2 >= interPoints.size() )
            {
                restart( gameTime, timingMode );
            }
            
            ip1 = interPoints.get( ++i1 );
        }
        ip2 = interPoints.get( i1 + 1 );
        
        if ( ip1.deltaTime > 0.0f )
            d = ( t0t - t0i ) / ip1.deltaTime;
        else
            d = 0.0f;
        
        camRot.interpolate( ip1.rot, ip2.rot, d );
        
        cam.getTransform().set( camRot );
        cam.getTransform().setTranslation( ip1.pos.getX() + ( ( ip2.pos.getX() - ip1.pos.getX() ) * d ),
                                           ip1.pos.getY() + ( ( ip2.pos.getY() - ip1.pos.getY() ) * d ),
                                           ip1.pos.getZ() + ( ( ip2.pos.getZ() - ip1.pos.getZ() ) * d )
                                         );
        
        frames++;
    }
    
    /**
     * 
     * @param gameTime
     * @param timingMode
     */
    public void restart( long gameTime, TimingMode timingMode )
    {
        float averageFPS = ( (float)frames / (float)t0t * 1000000.0f );
        
        for ( int i = 0; i < listeners.size(); i++ )
            listeners.get( i ).onCameraFlightEnded( frames, (long)t0t, averageFPS );
        
        // restart at the beginning
        startTime = gameTime;
        t0i = 0.0f;
        t0t = 0.0f;
        i1 = -1;
        frames = 0;
    }
    
    public void start( long startTime )
    {
        this.camRot = new Matrix3f();
        
        this.startTime = startTime;
        this.t0i = 0.0f;
        this.i1 = 0;
        
        this.frames = 0;
    }
    
    /**
     * Adds a camera-transformation-matrix to the list.
     * 
     * @param rot
     * @param pos
     * @param deltaTime
     */
    public void addRotPos( Matrix3f rot, Point3f pos, float deltaTime )
    {
        InterpolationPoint interPoint = new InterpolationPoint();
        interPoint.rot = new Matrix3f( rot );
        interPoint.pos = new Point3f( pos );
        interPoint.deltaTime = deltaTime * 1000f; // convert to micors!
        
        interPoints.add( interPoint );
    }
    
    private String readLine( InputStream in ) throws IOException
    {
        StringBuffer str = new StringBuffer();
        char c;
        final char EOL = '\n';
        
        while ( ( in.available() > 0 ) && ( ( c = (char)in.read() ) != EOL ) )
            str.append( c );
        
        return ( str.toString() );
    }
    
    /**
     * Loads the CameraFlight from the specified InputStream.
     * 
     * @param in the InputStream to load from
     * 
     * @throws IOException
     */
    public void load( InputStream in ) throws IOException
    {
        interPoints = new ArrayList< InterpolationPoint >();
        Matrix3f rot = new Matrix3f();
        Point3f pos = new Point3f();
        float t;
        
        Format format = Format.get( Byte.valueOf( readLine( in ) ) );
        readLine( in ); // skip blank line
        
        if ( format == Format.COMPRESSED )
        {
            if ( in instanceof BufferedInputStream )
                throw new IllegalArgumentException( "The InputStream must not be a BufferedInputStream, if read from a COMPRESSED file." );
            
            in = new InflaterInputStream( in );
        }
        
        if ( !( in instanceof BufferedInputStream ) )
            in = new BufferedInputStream( in );
        
        String line;
        String[] comps;
        while ( true )
        {
            line = readLine( in );
            if ( ( line == null ) || ( line.length() == 0 ) || ( Character.getType( line.charAt( 0 ) ) == 0 ) )
                break;
            comps = line.split( " " );
            rot.m00( Float.parseFloat( comps[ 0 ] ) );
            rot.m01( Float.parseFloat( comps[ 1 ] ) );
            rot.m02( Float.parseFloat( comps[ 2 ] ) );
            
            line = readLine( in );
            comps = line.split( " " );
            rot.m10( Float.parseFloat( comps[ 0 ] ) );
            rot.m11( Float.parseFloat( comps[ 1 ] ) );
            rot.m12( Float.parseFloat( comps[ 2 ] ) );
            
            line = readLine( in );
            comps = line.split( " " );
            rot.m20( Float.parseFloat( comps[ 0 ] ) );
            rot.m21( Float.parseFloat( comps[ 1 ] ) );
            rot.m22( Float.parseFloat( comps[ 2 ] ) );
            
            line = readLine( in );
            comps = line.split( " " );
            pos.setX( Float.parseFloat( comps[ 0 ] ) );
            pos.setY( Float.parseFloat( comps[ 1 ] ) );
            pos.setZ( Float.parseFloat( comps[ 2 ] ) );
            
            line = readLine( in );
            t = (float)( Long.parseLong( line ) );
            
            line = readLine( in );
            
            addRotPos( rot, pos, t );
        }
    }
    
    /**
     * Loads the CameraFlight from the specified URL.<br>
     * If read from a URL, the resource must not be a compressed flight file.
     * 
     * @param url the URL to load from
     * 
     * @throws IOException
     */
    public void load( URL url ) throws IOException
    {
        load( url.openStream() );
    }
    
    /**
     * Loads the CameraFlight from the specified File.
     * 
     * @param file the File to load from
     * 
     * @throws IOException
     */
    public void load( File file ) throws IOException
    {
        load( new FileInputStream( file ) );
    }
    
    /**
     * Loads the CameraFlight from the specified file.
     * 
     * @param filename the file to load from
     * 
     * @throws IOException
     */
    public void load( String filename ) throws IOException
    {
        load( new File( filename ) );
    }
    
    /**
     * Creates a new CameraFlight
     */
    public CameraFlight()
    {
    }
    
    public CameraFlight( List< InterpolationPoint > interPoints )
    {
        this.interPoints = interPoints;
    }
    
    /**
     * Creates a new CameraFlight and loads data from the given InputStream.
     */
    public CameraFlight( InputStream in ) throws IOException
    {
        this();
        
        load( in );
    }
    
    /**
     * Creates a new CameraFlight and loads data from the given URL.
     * If read from a URL, the resource must not be a compressed flight file.
     */
    public CameraFlight( URL url ) throws IOException
    {
        this();
        
        load( url );
    }
    
    /**
     * Creates a new CameraFlight and loads data from the given file.
     */
    public CameraFlight( File file ) throws IOException
    {
        this();
        
        load( file );
    }
    
    /**
     * Creates a new CameraFlight and loads data from the given file.
     */
    public CameraFlight( String filename ) throws IOException
    {
        this();
        
        load( filename );
    }
}
