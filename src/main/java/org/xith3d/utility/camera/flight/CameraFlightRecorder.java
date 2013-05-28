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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;

import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;

import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.Interval;
import org.xith3d.loop.opscheduler.IntervalListener;

import org.xith3d.scenegraph.Transformable;

/**
 * Used to record a camera flight. The flight can be replayed with CameraFlight.
 * 
 * @see CameraFlight
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CameraFlightRecorder extends Interval implements IntervalListener
{
    private Transformable view;
    private long t1, t2;
    
    private Matrix3f viewRot = new Matrix3f();
    private Point3f viewPos = new Point3f();
    
    private OutputStream output;
    
    private void writeString( String str )
    {
        try
        {
            for ( int i = 0; i < str.length(); i++ )
                output.write( str.charAt( i ) );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Writes the rotation quaternion and positional vector to the output file
     * 
     * @param quat the View's rotation
     * @param pos the View's position
     * @param deltaTime
     */
    private void writeRotPos( Matrix3f rot, Point3f pos, long deltaTime )
    {
        writeString( String.valueOf( rot.m00() ) + " " + String.valueOf( rot.m01() ) + " " + String.valueOf( rot.m02() ) + "\n" );
        writeString( String.valueOf( rot.m10() ) + " " + String.valueOf( rot.m11() ) + " " + String.valueOf( rot.m12() ) + "\n" );
        writeString( String.valueOf( rot.m20() ) + " " + String.valueOf( rot.m21() ) + " " + String.valueOf( rot.m22() ) + "\n" );
        writeString( String.valueOf( pos.getX() ) + " " + String.valueOf( pos.getY() ) + " " + String.valueOf( pos.getZ() ) + "\n" );
        
        writeString( String.valueOf( deltaTime ) + "\n\n" );
    }
    
    /**
     * Writes the View's rotation and position to the output file
     * 
     * @param transformNode
     * @param deltaTime
     */
    private void writeRotPos( Transformable transformNode, long deltaTime )
    {
        transformNode.getTransform().getRotation( viewRot );
        transformNode.getTransform().get( viewPos );
        
        writeRotPos( viewRot, viewPos, deltaTime );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onIntervalHit( Interval interval, long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( ( t1 >= 0L ) && ( interval == this ) )
        {
            t2 = gameTime;
            
            writeRotPos( view, t2 - t1 );
            
            t1 = t2;
        }
    }
    
    /**
     * Starts the record.
     * 
     * @param startTime the current gameTime
     * @param file the file where the flight is to be saved
     * @param format the file's format
     */
    public void startRecord( long startTime, File file, CameraFlight.Format format )
    {
        try
        {
            output = new FileOutputStream( file );
            
            output.write( String.valueOf( format.getByte() ).codePointAt( 0 ) );
            output.write( 10 );
            output.write( 10 );
            
            if ( format == CameraFlight.Format.COMPRESSED )
                output = new DeflaterOutputStream( output );
            
            output = new BufferedOutputStream( output );
            
            writeRotPos( view, 0L );
            
            t1 = startTime;
            
            System.out.println( "CameraFlight record started." );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Starts the record.
     * 
     * @param startTime the current gameTime
     * @param filename the filename where the flight is to be saved
     * @param format the file's format
     */
    public void startRecord( long startTime, String filename, CameraFlight.Format format )
    {
        startRecord( startTime, new File( filename ), format );
    }
    
    /**
     * Starts the record.
     * 
     * @param startTime the current gameTime
     * @param file the file where the flight is to be saved
     */
    public void startRecord( long startTime, File file )
    {
        startRecord( startTime, file, CameraFlight.Format.UNCOMPRESSED );
    }
    
    /**
     * Starts the record.
     * 
     * @param startTime the current gameTime
     * @param filename the filename where the flight is to be saved
     */
    public void startRecord( long startTime, String filename )
    {
        startRecord( startTime, new File( filename ), CameraFlight.Format.UNCOMPRESSED );
    }
    
    /**
     * Stops the recording and closes the file.
     */
    public void stopRecord()
    {
        try
        {
            output.close();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        
        t1 = -1L;
        
        System.out.println( "CameraFlight record stopped." );
    }
    
    /**
     * Creates a new CameraFlightRecorder
     */
    public CameraFlightRecorder( Transformable view, long resolution )
    {
        super( resolution );
        
        this.view = view;
        t1 = -1L;
    }
}
