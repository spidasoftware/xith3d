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
package org.xith3d.terrain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.openmali.FastMath;
import org.w3c.dom.Document;
import org.xith3d.utility.logging.X3DLog;

/**
 * @author Mathias 'cylab' Henze
 */
@SuppressWarnings("unchecked")
public class L3DTResourceProvider extends DefaultGridResourceProvider
{
    private static XPath xpath = XPathFactory.newInstance().newXPath();
    private float minAlt;
    private float maxAlt;
    private float hScale;
    private long nx;
    private long ny;
    
    public final float getHScale()
    {
        return ( hScale );
    }
    
    public final float getMaxAlt()
    {
        return ( maxAlt );
    }
    
    public final float getMinAlt()
    {
        return ( minAlt );
    }
    
    public final long getNx()
    {
        return ( nx );
    }
    
    public final long getNy()
    {
        return ( ny );
    }
    
    private static int createMosaicSpecs( Properties mosaic, List<GridResourceSpec> specs, long bestWidth, long bestHeight, float min, float max ) throws MalformedURLException
    {
        URL location = new URL( mosaic.getProperty( "Location" ) );
        String baseName = mosaic.getProperty( "BaseName" );
        long width = Long.parseLong( mosaic.getProperty( "nPxlsX" ) );
        long height = Long.parseLong( mosaic.getProperty( "nPxlsY" ) );
        long tilesX = Long.parseLong( mosaic.getProperty( "nMapsX" ) );
        long tilesY = Long.parseLong( mosaic.getProperty( "nMapsY" ) );
        String ext = mosaic.getProperty( "FileExt" );
        float dx = 1.0f / tilesX;
        float dy = 1.0f / tilesY;
        int detail = (int)( (float)bestWidth / width + 0.5 ) + (int)( (float)bestHeight / height + 0.5 ) / 2;
        // log2
        for ( int i = 0; i < 32; i++ )
        {
            if( detail >> i == 0 )
            {
                // this is a quick hack. The value is dependent on the ChunkedTerrain Setup (spatialTreeDepth)
                detail = i / 2;
                break;
            }
        }
        
        for ( int x = 0; x < tilesX; x++ )
        {
            for ( int y = 0; y < tilesY; y++ )
            {
                specs.add( new GridResourceSpec(
                    detail, new URL[] { new URL( location, baseName + "_x" + x + "y" + y + "." + ext ) },
                    dx * x, dy * y, dx * ( x + 1 ), dy * ( y + 1 ),
                    min, max
                ) );
            }
        }
        
        return ( detail );
    }
    
    private static List<Properties> findMipMaps( URL project, String type )
    {
        ArrayList<Properties> result = new ArrayList<Properties>( 8 );
        try
        {
            for ( int i = 1; i <= 8; i++ )
            {
                String mip = "Mip" + ( FastMath.pow( 2, i ) );
                URL manifestLocation = new URL( project, "MipMaps/" + type + "/" + mip + "/" + type + "_" + mip + ".mmf" );
                if ( checkUrl( manifestLocation  ) )
                {
                    Properties manifest = loadManifest( manifestLocation  );
                    if ( manifest == null )
                    {
                        break;
                    }
                    result.add( manifest );
                }
                else
                {
                    break;
                }
            }
        }
        catch ( Exception ex )
        {
            Logger.getLogger( L3DTResourceProvider.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        return ( result );
    }
    
    private static boolean checkUrl( URL location )
    {
        try
        {
            return ( location.openConnection().getContentLength() > 0 );
        }
        catch ( Exception ignore )
        {
            return ( false );
        }
    }
    
    private static Document loadXML( URL location )
    {
        InputStream in = null;
        try
        {
            in = location.openStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware( true );
            factory.setValidating( false );
            Document doc = factory.newDocumentBuilder().parse( in );
            
            return ( doc );
        }
        catch ( Exception ex )
        {
            Logger.getLogger( L3DTResourceProvider.class.getName() ).log( Level.SEVERE, null, ex );
        }
        finally
        {
            if ( in != null )
            {
                try { in.close(); } catch ( Exception ignore ) {}
            }
        }
        
        return ( null );
    }
    
    private static Properties loadManifest( URL location ) throws Exception
    {
        /*
         * This is for L3DT itself, which puts backslashes into the xml file.
         * Since there's no way to keep it from doing so we will (have to)
         * replace a backslash with a slash.
         */
        location = new URL( location.toString().replace( '\\', '/' ) );
        
        BufferedReader in = null;
        try
        {
            Properties props = new Properties();
            in = new BufferedReader( new InputStreamReader( location.openStream() ) );
            String line = "";
            while ( ( line = in.readLine() ) != null )
            {
                if ( line.startsWith( "#" ) && !line.startsWith( "#EOF" ) )
                {
                    String[] values = line.split( "[#:\t ]" );
                    if (values.length == 4 )
                        props.setProperty( values[ 1 ], values[ 3 ] );
                    else if ( values.length == 5 )
                        props.setProperty( values[ 1 ] + "." + values[ 3 ], values[ 4 ] );
                }
            }
            
            String locationString = location.toString();
            String baseName = locationString.substring( locationString.lastIndexOf( "/" ) + 1, locationString.lastIndexOf( "." ) );
            props.setProperty( "Location", locationString );
            props.setProperty( "BaseName", baseName );
            
            return ( props );
        }
        catch ( Exception ex )
        {
            Logger.getLogger( L3DTResourceProvider.class.getName() ).log( Level.SEVERE, null, ex );
        }
        finally
        {
            if ( in != null )
            {
                try { in.close(); } catch ( Exception ignore ) {}
            }
        }
        
        return ( null );
    }
    
    private static String buildXPath( String spec, String type )
    {
        String xpathexp = spec.replaceAll( "([^\\/]+)\\/", "varlist[@name='$1']/" );
        xpathexp = xpathexp.replaceAll( "([^\\/]+)$", type + "[@name='$1']" );
        
        return ( xpathexp );
    }
    
    private static float getFloat( Document doc, String spec ) throws XPathExpressionException
    {
        String value = xpath.evaluate( buildXPath( spec, "float" ), doc );
        
        return ( Float.parseFloat( value ) );
    }
    
    private static long getInt( Document doc, String spec ) throws XPathExpressionException
    {
        String value = xpath.evaluate( buildXPath( spec, "int" ), doc );
        
        return ( Long.parseLong( value ) );
    }
    
    private static String getString( Document doc, String spec ) throws XPathExpressionException
    {
        return ( xpath.evaluate( buildXPath( spec, "string" ), doc ) );
    }
    
    public L3DTResourceProvider( URL l3dtProject )
    {
        ArrayList<GridResourceSpec> samplerSpecs = new ArrayList<GridResourceSpec>( 100 );
        ArrayList<GridResourceSpec> surfaceSpecs = new ArrayList<GridResourceSpec>( 100 );
        Document doc = loadXML( l3dtProject );
        
        try
        {
            nx = getInt( doc, "/ProjectData/MapInfo/Terrain/nx" );
            ny = getInt( doc, "/ProjectData/MapInfo/Terrain/ny" );
            minAlt = getFloat( doc, "/ProjectData/MapInfo/Terrain/MinAlt" );
            maxAlt = getFloat( doc, "/ProjectData/MapInfo/Terrain/MaxAlt" );
            hScale = getFloat( doc, "/ProjectData/MapInfo/Terrain/HorizScale" );
            
            String heightFieldLocation = getString( doc, "/ProjectData/Maps/HF/Filename" );
            
            List<Properties> mipMaps = findMipMaps( l3dtProject, "HF" );
            
            int maxDetail = 0;
            for ( int i = 0; i < mipMaps.size(); i++ )
            {
                int value = createMosaicSpecs( mipMaps.get( i ), samplerSpecs, nx, ny, minAlt, maxAlt );
                if ( value > maxDetail )
                    maxDetail = value;
            }
            
            if ( heightFieldLocation.toString().endsWith( ".mmf" ) )
            {
                createMosaicSpecs( loadManifest( new URL( l3dtProject, heightFieldLocation ) ), samplerSpecs, nx, ny, minAlt, maxAlt );
            }
            else
            {
                samplerSpecs.add( new GridResourceSpec( 0, new URL( l3dtProject, heightFieldLocation ), 0.0f, 0.0f, 1.0f, 1.0f, minAlt, maxAlt ) );
            }
            
            // reverse the detail, since 0 is our lowest level detail
            for ( int i = 0; i < samplerSpecs.size(); i++ )
            {
                GridResourceSpec<GridSampler> spec = samplerSpecs.get( i );
                spec.setDetail( maxDetail - spec.getDetail() );
            }
            
            long textureWidth = getInt( doc, "/ProjectData/MapInfo/Texture/nx" );
            long textureHeight = getInt( doc, "/ProjectData/MapInfo/Texture/ny" );
            String textureLocation = getString( doc, "/ProjectData/Maps/TX/Filename" );
            
            mipMaps = findMipMaps( l3dtProject, "TX" );
            
            maxDetail = 0;
            for ( int i = 0; i < mipMaps.size(); i++ )
            {
                int value = createMosaicSpecs( mipMaps.get( i ), surfaceSpecs, textureWidth, textureHeight, 0f, 0f );
                if ( value > maxDetail )
                    maxDetail = value;
            }
            
            if ( textureLocation.toString().endsWith( ".mmf" ) )
            {
                createMosaicSpecs( loadManifest( new URL( l3dtProject, textureLocation ) ), surfaceSpecs, textureWidth, textureHeight, 0f, 0f );
            }
            else
            {
                surfaceSpecs.add( new GridResourceSpec( 0, new URL( l3dtProject, textureLocation ), 0.0f, 0.0f, 1.0f, 1.0f ) );
            }
            
            // reverse the detail, since 0 is our lowest level detail
            for ( int i = 0; i < surfaceSpecs.size(); i++ )
            {
                GridResourceSpec<GridSurface> spec = surfaceSpecs.get( i );
                spec.setDetail( maxDetail - spec.getDetail() );
            }
        }
        catch ( Exception ex )
        {
            X3DLog.print( ex );
            Logger.getLogger( L3DTResourceProvider.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        setSamplerSpecs( samplerSpecs );
        setSurfaceSpecs( surfaceSpecs );
    }
}
