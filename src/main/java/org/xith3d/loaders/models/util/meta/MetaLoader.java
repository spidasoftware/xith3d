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
package org.xith3d.loaders.models.util.meta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.jagatoo.loaders.IncorrectFormatException;
import org.jagatoo.loaders.ParsingException;
import org.openmali.vecmath2.AxisAngle3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.ModelLoader;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.utility.logging.X3DLog;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * A ModelLoader wrapper. Enables a model meta data file (in xml) to be
 * loaded. This file contains information about scaling and rotating
 * the model.
 * 
 * Model Meta Data Format:
 * TODO Create Format Documentation
 * 
 * Look at demo/models/meta/galleon.xml for an example
 * 
 * @author Andrew Hanson (aka Patheros)
 * @author Marvin Froehlich (aka Qudus) [converted to a SAX-Parser based implementation]
 */
public class MetaLoader< D extends ModelMetaData > extends ModelLoader
{
    /**
     * Parse-handler for meta data XML.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    private static class MetaParser extends org.xml.sax.helpers.DefaultHandler
    {
        private int level = -1;
        private String[] path = new String[ 16 ];
        
        private ModelMetaData meta = new ModelMetaData();
        
        private final String getCurrentPathAsString()
        {
            String p = "";
            
            for ( int i = 0; i < level; i++ )
            {
                p += '/' + path[i];
            }
            
            return ( p );
        }
        
        /*
        @Override
        public void startDocument() throws SAXException
        {
            //System.out.println( "startDocument" );
        }
        */
        
        @Override
        public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException
        {
            //System.out.println( "startElement( " + uri + ", " + localName + ", " + qName + ", " + attributes + " )" );
            
            path[ ++level ] = qName;
            
            if ( qName.equals( "resource" ) )
            {
                String type = attributes.getValue( "type" );
                if ( type.equals( "base" ) )
                {
                    meta.setResourceRefrenceBase();
                }
                else// if ( type.equals( "relative" ) )
                {
                    meta.setResourceRefrenceRelative();
                }
            }
            else if ( qName.equals( "rotation" ) )
            {
                String type = attributes.getValue( "type" );
                if ( type.equals( "AXIS_ANGLE" ) )
                {
                    float x = Float.parseFloat( attributes.getValue( "x" ) );
                    float y = Float.parseFloat( attributes.getValue( "y" ) );
                    float z = Float.parseFloat( attributes.getValue( "z" ) );
                    float angle = Float.parseFloat( attributes.getValue( "angle" ) );
                    
                    meta.setRotation( new AxisAngle3f( x, y, z, angle ) );
                }
            }
            else if ( qName.equals( "loadingFlag" ) )
            {
                String name = attributes.getValue( "name" );
                boolean value = Boolean.parseBoolean( attributes.getValue( "value" ) );
                
                if ( name.equals( "LIGHT_NODES" ) )
                {
                    meta.getLoadingFlags().lightNodes = value;
                }
                else if ( name.equals( "FOG_NODES" ) )
                {
                    meta.getLoadingFlags().fogNodes = value;
                }
                else if ( name.equals( "BACKGROUND_NODES" ) )
                {
                    meta.getLoadingFlags().backgroundNodes = value;
                }
                else if ( name.equals( "BEHAVIOR_NODES" ) )
                {
                    meta.getLoadingFlags().behaviorNodes = value;
                }
                else if ( name.equals( "VIEW_GROUPS" ) )
                {
                    meta.getLoadingFlags().viewGroups = value;
                }
                else if ( name.equals( "SOUND_NODES" ) )
                {
                    meta.getLoadingFlags().soundNodes = value;
                }
                else if ( name.equals( "USE_DISPLAY_LISTS" ) )
                {
                    meta.getLoadingFlags().useDisplayLists = value;
                }
            }
        }
        
        @Override
        public void characters( char[] data, int start, int length ) throws SAXException
        {
            //System.out.println( "data: " + new String( data, start, length ) );
            if ( path[ level ].equals( "resource" ) )
            {
                meta.setResourceName( new String( data, start, length ).trim() );
            }
            else if ( path[ level ].equals( "scaling" ) )
            {
                meta.setScaling( Float.parseFloat( new String( data, start, length ).trim() ) );
            }
        }
        
        @Override
        public void endElement( String uri, String localName, String qName ) throws SAXException
        {
            //System.out.println( "endElement( " + uri + ", " + localName + ", " + qName + " )" );
            
            path[ level-- ] = null;
        }
        
        @Override
        public void warning( SAXParseException e ) throws SAXException
        {
            System.err.println( "Warning at: " + getCurrentPathAsString() );
            
            e.printStackTrace();
        }
        
        @Override
        public void error( SAXParseException e ) throws SAXException
        {
            System.err.println( "Error at: " + getCurrentPathAsString() );
            
            e.printStackTrace();
        }
        
        @Override
        public void fatalError( SAXParseException e ) throws SAXException
        {
            System.err.println( "Warning at: " + getCurrentPathAsString() );
            
            //e.printStackTrace();
            throw e;
        }
        
        /*
        @Override
        public void endDocument() throws SAXException
        {
            //System.out.println( "endDocument" );
        }
        */
        
        public ModelMetaData getMetaData()
        {
            return ( meta );
        }
    }
    
    private static final SAXParserFactory SAX_PARSER_FACTORY = SAXParserFactory.newInstance();
    
    private URL metaBaseURL;
    
    /**
     * Peforms any processing on the model defined by the metaData.
     * Subclasses can extend this functionality if they have their own
     * desired actions.
     * @param model the model to act on
     * @param meta the metaData defining the actions
     */
    private void process( Model model, D meta )
    {
        model.setMetaData( meta );
        
        //Start Scaling and Rotation
        float scale = meta.getScaling();
        AxisAngle3f rotation = meta.getRotation();
        Vector3f rotationAngle;
        if ( rotation != null )
        {
            rotationAngle = new Vector3f( rotation.getX(), rotation.getY(), rotation.getZ() );
            if ( rotationAngle.lengthSquared() == 0 )
            {
                ParsingException e = new ParsingException( "Rotation Axis can not be length 0" );
                X3DLog.print( e );
                throw e;
            }
        }
        else
        {
            rotationAngle = null;
        }
        
        for ( int i = 0; i < model.getShapesCount(); i++ )
        {
            final Shape3D shape = model.getShape( i );
            StaticTransform.scale( shape, scale );
            if ( rotation != null )
            {
                StaticTransform.rotate( shape, rotationAngle, rotation.getAngle() );
            }
        }
        
        final List< TransformGroup > transformGroups = model.findAll( TransformGroup.class );
        if ( transformGroups != null )
        {
            for ( int i = 0; i < transformGroups.size(); i++ )
            {
                //final TransformGroup transformGroup = transformGroups.get( i );
                //StaticTransform.scale( transformGroup, scale );
                if ( rotation != null )
                {
                    // TODO: Rotate TransformGroup
                    //GeometricMath.rotate(transformGroup, rotationAngle, rotation.angle);
                }
            }
        }
        
        // End Scaling and Rotation
    }
    
    public void setMetaBaseURL( URL metaBaseURL )
    {
        this.metaBaseURL = metaBaseURL;
    }
    
    public URL getMetaBaseURL()
    {
        return ( metaBaseURL );
    }
    
    /**
     * Creates the URL from a given meta.
     * Subclasses can overide this if they define
     * their own resource locations.
     * 
     * @param baseURL
     * @param meta
     * 
     * @throws MalformedURLException
     */
    private URL createMetaURL( URL baseURL, D meta ) throws MalformedURLException
    {
        URL retVal = null;
        
        if ( meta.isResourceRefrenceBase() )
        {
            retVal = new URL( getMetaBaseURL(), meta.getResourceName() );
        }
        else if ( meta.isResourceRefrenceRelative() )
        {
            retVal = new URL( baseURL, meta.getResourceName() );
        }
        
        return ( retVal );
    }
    
    @SuppressWarnings( "unchecked" )
    private final D loadMeta( URL url ) throws IOException
    {
        SAXParser saxParser;
        try
        {
            saxParser = SAX_PARSER_FACTORY.newSAXParser();
        }
        catch ( ParserConfigurationException e )
        {
            throw new Error ( e );
        }
        catch ( SAXException e )
        {
            throw new Error ( e );
        }
        
        try
        {
            saxParser.setProperty( "http://xml.org/sax/features/validation", false );
        }
        catch ( SAXNotRecognizedException e )
        {
            e.printStackTrace();
        }
        catch ( SAXNotSupportedException e )
        {
            e.printStackTrace();
        }
        
        MetaParser handler = new MetaParser();
        
        try
        {
            saxParser.parse( url.openStream(), handler );
        }
        catch ( SAXException e )
        {
            throw new ParsingException( e );
        }
        
        return ( (D)handler.getMetaData() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Model loadModel( URL url, String filenameBase, URL baseURL, String skin, float scale, int flags ) throws IOException, IncorrectFormatException, ParsingException
    {
        D meta = loadMeta( url );
        Model model = ModelLoader.getInstance().loadModel( createMetaURL( baseURL, meta ), skin );
        process( model, meta );
        
        return ( model );
    }
    
    /**
     * Constucts MetaLoader.
     */
    public MetaLoader()
    {
        super();
    }
}
