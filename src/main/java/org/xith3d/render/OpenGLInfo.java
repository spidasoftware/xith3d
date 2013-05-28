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
package org.xith3d.render;

import java.util.Arrays;

/**
 * Small class to hold the information about the OpenGL layer
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OpenGLInfo
{
    public static final int NORM_VERSION_1_1 = composeNormalizedVersion( 1, 1, 0 );
    public static final int NORM_VERSION_1_2 = composeNormalizedVersion( 1, 2, 0 );
    public static final int NORM_VERSION_1_3 = composeNormalizedVersion( 1, 3, 0 );
    public static final int NORM_VERSION_1_4 = composeNormalizedVersion( 1, 4, 0 );
    public static final int NORM_VERSION_1_5 = composeNormalizedVersion( 1, 5, 0 );
    public static final int NORM_VERSION_2_0 = composeNormalizedVersion( 2, 0, 0 );
    public static final int NORM_VERSION_2_1 = composeNormalizedVersion( 2, 1, 0 );
    
    public static enum KnownVendor
    {
        NVIDIA,
        ATI,
        INTEL,
        MESA,
        ;
        
        public static KnownVendor getKnownVendor( String vendor )
        {
            if ( vendor.toLowerCase().contains( "nvidia" ) )
                return ( NVIDIA );
            
            if ( vendor.toLowerCase().contains( "ati" ) )
                return ( ATI );
            
            if ( vendor.toLowerCase().contains( "intel" ) )
                return ( INTEL );
            
            if ( vendor.toLowerCase().contains( "mesa" ) )
                return ( MESA );
            
            return ( null );
        }
    }
    
    private final String       RENDERER;
    private final String       VERSION;
    private final int          VERSION_MAJOR;
    private final int          VERSION_MINOR;
    private final int          VERSION_REVISION;
    private final int          NORM_VERSION;
    private final String       VENDOR;
    private final KnownVendor  KNOWN_VENDOR;
    private final String[]     EXTENSIONS;
    private final int[]        EXT_HASHES;
    
    public final String getRenderer()
    {
        return ( RENDERER );
    }
    
    public final String getVersion()
    {
        return ( VERSION );
    }
    
    public final int getNormalizedVersion()
    {
        return ( NORM_VERSION );
    }
    
    public final int getVersionMajor()
    {
        return ( VERSION_MAJOR );
    }
    
    public final int getVersionMinor()
    {
        return ( VERSION_MINOR );
    }
    
    public final int getVersionRevision()
    {
        return ( VERSION_REVISION );
    }
    
    public final String getVendor()
    {
        return ( VENDOR );
    }
    
    public final KnownVendor getKnwonVendor()
    {
        return ( KNOWN_VENDOR );
    }
    
    public final String[] getExtensions()
    {
        return ( EXTENSIONS );
    }
    
    public final boolean hasExtension( String extension )
    {
        return ( Arrays.binarySearch( EXT_HASHES, extension.hashCode() ) >= 0 );
    }
    
    @Override
    public String toString()
    {
        StringBuffer buff = new StringBuffer();
        
        buff.append( "OpenGL Renderer = " );
        buff.append( RENDERER );
        buff.append( '\n' );
        buff.append( "OpenGL Version = " );
        buff.append( VERSION );
        buff.append( '\n' );
        buff.append( "OpenGL Vendor = " );
        buff.append( VENDOR );
        buff.append( '\n' );
        buff.append( "OpenGL Extensions =" );
        for ( int i = 0; i < EXTENSIONS.length; i++ )
        {
            buff.append( ' ' );
            buff.append( EXTENSIONS[ i ] );
        }
        
        return ( buff.toString() );
    }
    
    public void dump()
    {
        System.out.println( toString() );
    }
    
    public void dumpExtensions()
    {
        System.out.println( "OpenGL Extensions:" );
        for ( int i = 0; i < EXTENSIONS.length; i++ )
        {
            System.out.println( EXTENSIONS[ i ] );
        }
    }
    
    private static int composeNormalizedVersion( int major, int minor, int revision )
    {
        int normVersion = revision + ( minor * 10000 ) + ( major * 10000 * 100 );
        
        return ( normVersion );
    }
    
    public OpenGLInfo( String renderer, String version, String vendor, String extensions )
    {
        this.RENDERER = renderer;
        this.VERSION = version;
        this.VENDOR = vendor;
        this.KNOWN_VENDOR = KnownVendor.getKnownVendor( vendor );
        this.EXTENSIONS = extensions.split( " " );
        
        int p = VERSION.indexOf( ' ' );
        String[] ver = ( p > -1 ? VERSION.substring( 0, p ) : VERSION ).split( "\\." );
        this.VERSION_MAJOR = Integer.parseInt( ver[ 0 ] );
        this.VERSION_MINOR = ( ver.length > 1 ) ? Integer.parseInt( ver[ 1 ] ) : 0;
        this.VERSION_REVISION = ( ver.length > 2 ) ? Integer.parseInt( ver[ 2 ] ) : 0;
        
        this.NORM_VERSION = composeNormalizedVersion( VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION );
        
        this.EXT_HASHES = new int[ EXTENSIONS.length ];
        for ( int i = 0; i < EXTENSIONS.length; i++ )
        {
            EXT_HASHES[ i ] = EXTENSIONS[ i ].hashCode();
        }
        Arrays.sort( EXT_HASHES );
    }
}
