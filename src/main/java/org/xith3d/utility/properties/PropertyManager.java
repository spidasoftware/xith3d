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
package org.xith3d.utility.properties;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Properties;

import org.xith3d.utility.exception.Check;
import org.xith3d.utility.logging.X3DLog;

/**
 * :Id: PropertyManager.java,v 1.6 2003/02/24 00:13:53 wurp Exp $
 * 
 * :Log: PropertyManager.java,v $
 * Revision 1.6  2003/02/24 00:13:53  wurp
 * Formatted all java code for cvs (strictSunConvention.xml)
 * 
 * Revision 1.5  2002/05/15 02:02:31  dilvish
 * Major updates for inventory and terrain
 * 
 * Revision 1.4  2001/06/20 04:05:42  wurp
 * added log4j.
 * 
 * Revision 1.3  2001/01/02 11:12:26  wurp
 * Added getProperty to PropertyManager; made convertToString
 * slightly more efficient in StringProperty.
 * 
 * Revision 1.2  2000/11/04 12:09:58  wizofid
 * Finishing property manager integeration
 * 
 * Revision 1.1  2000/10/08 21:38:22  entropy
 * Initial checkin
 *
 * Revision 1.1  2000/09/21 11:08:03  dyazel
 * Implemented a generic property manager
 * 
 * 
 * This class allows you to setup project properties, read and write them to a
 * commented property file, and yet still be able to access the values quickly at
 * runtime.
 * 
 * @author David Yazel
 */
public class PropertyManager
{
    private Hashtable< String, PropertyInterface > props = new Hashtable< String, PropertyInterface >( 50 );
    
    /**
     * When adding a property to the list, we specfify all the information regarding that
     * property so we can read and write them
     * @param prop The name of the property
     */
    public void addProperty( PropertyInterface prop )
    {
        props.put( prop.getName(), prop );
    }
    
    private String nameToKey( String name )
    {
        String result = name.replace( ' ', '_' );
        
        return ( result );
    }
    
    /**
     * Retrieves a property from the manager.
     * 
     * @param name The name of the property
     * @return PropertyInterface reference back to the property.
     */
    public PropertyInterface getProperty( String name )
    {
        return ( props.get( name ) );
    }
    
    /**
     * Writes the properties out to a file
     */
    public void save( String filename )
    {
        try
        {
            PrintStream prnout = new PrintStream( new BufferedOutputStream( new FileOutputStream( filename ) ) );
            
            // step through all the properties and write them out
            for ( PropertyInterface node: props.values() )
            {
                prnout.println( "#" + node.getComment() );
                prnout.println( nameToKey( node.getName() ) + "=" + node.convertToString() );
                prnout.println();
            }
            
            prnout.close();
        }
        catch ( java.io.FileNotFoundException e )
        {
            Check.assertion( false, e.getMessage() );
        }
    }
    
    /**
     * This will load the specified file, then match all the defined properties against
     * the properties in the file.  The values in the properties will then be overwritten
     */
    public void load( String filename )
    {
        X3DLog.debug( "Loading property file : ", filename );
        
        try
        {
            BufferedInputStream infile = new BufferedInputStream( new FileInputStream( filename ) );
            Properties p = new Properties();
            p.load( infile );
            
            // step through all the properties and write them out
            for ( PropertyInterface node: props.values() )
            {
                String key = nameToKey( node.getName() );
                String value = p.getProperty( key );
                
                if ( value != null )
                {
                    node.convertFromString( value );
                    
                    X3DLog.debug( "   Property ", key, " = ", node.convertToString() );
                }
                else
                {
                    X3DLog.debug( "   Property ", key, " not found, using defaults" );
                }
            }
            
            infile.close();
        }
        catch ( java.io.FileNotFoundException e )
        {
            X3DLog.exception( "Property file ", filename, " not found, using defaults" );
        }
        catch ( java.io.IOException e )
        {
            Check.assertion( false, e.getMessage() );
        }
    }
    
    public PropertyManager()
    {
    }
}
