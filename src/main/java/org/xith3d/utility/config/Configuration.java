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
package org.xith3d.utility.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Properties;
import javax.swing.filechooser.FileSystemView;

import org.xith3d.Xith3D;
import org.xith3d.utility.logging.X3DLog;

/**
 * @author Mathias Henze (aka cylab)
 */
public final class Configuration
{
    private static class TargetSpec
    {
        final Class< ? > targetClass;
        final String targetProperty;
        final String camelCaseTargetProperty;
        final String description;
        
        TargetSpec( Class< ? > targetClass, String targetProperty, String description )
        {
            this.targetClass = targetClass;
            this.targetProperty = targetProperty;
            this.description = description;
            
            String camelCaseName = targetProperty;
            camelCaseName = camelCaseName.substring( 0, 1 ).toUpperCase() + camelCaseName.substring( 1 );
            this.camelCaseTargetProperty = camelCaseName;
        }
    }

    private final HashMap< String, TargetSpec > mapping = new HashMap< String, TargetSpec >();
    private Properties currentConfig = new Properties();
    
    public final Properties getCurrentConfig()
    {
        return ( currentConfig );
    }
    
    public void register( String propertyKey, Class< ? > targetClass, String targetProperty, String description )
    {
        mapping.put( propertyKey, new TargetSpec( targetClass, targetProperty, description ) );
    }
    
    public void register( String propertyKey, Class< ? > targetClass, String targetProperty )
    {
        register( propertyKey, targetClass, targetProperty, null );
    }
    
    public void register( Class< ? > targetClass, String targetProperty, String description )
    {
        final String propertyKey = targetClass.getSimpleName() + "." + targetProperty;
        
        register( propertyKey, targetClass, targetProperty, description );
    }

    public void register( Class< ? > targetClass, String targetProperty )
    {
        final String propertyKey = targetClass.getSimpleName() + "." + targetProperty;
        
        register( propertyKey, targetClass, targetProperty, null );
    }
    
    public void register( Class< ? > targetClass )
    {
        Method[] methods = targetClass.getDeclaredMethods();
        for ( int i = 0; i < methods.length; i++ )
        {
            Method method = methods[ i ];
            Configurable c = method.getAnnotation( Configurable.class );
            if ( c != null )
            {
                String targetProperty = method.getName();
                int skip = ( targetProperty.indexOf( "is" ) == 0 ) ? 2 : 3;
                targetProperty = targetProperty.substring( skip, skip + 1 ).toLowerCase() + targetProperty.substring( skip + 1 );
                String[] vs = c.value();
                if ( ( vs != null ) & ( vs.length > 0 ) )
                {
                    register( vs[ 0 ], targetClass, targetProperty, ( ( vs.length > 1 ) ? vs[ 1 ] : null ) );
                }
                else
                {
                    register( targetClass, targetProperty );
                }
            }
        }
        
        Field[] fields = targetClass.getDeclaredFields();
        for ( int i = 0; i < methods.length; i++ )
        {
            Field field = fields[ i ];
            Configurable c = field.getAnnotation( Configurable.class );
            if ( c != null )
            {
                String targetProperty = field.getName();
                String[] vs = c.value();
                if ( ( vs != null ) & ( vs.length > 0 ) )
                {
                    register( vs[ 0 ], targetClass, targetProperty, ( ( vs.length > 1 ) ? vs[ 1 ] : null ) );
                }
                else
                {
                    register( targetClass, targetProperty );
                }
            }
        }
    }
    
    /**
     * Searches <filename> in a subdir under "/org/xith3d/settings/" in the current classpath.
     * 
     * @param subdir
     * @param filename
     */
    private void loadFromClassPath( String subdir, String filename )
    {
        InputStream stream = Xith3D.class.getResourceAsStream( "settings/" + subdir + "/" + filename );
        
        if ( stream != null )
        {
            try
            {
                currentConfig.load( stream );
            }
            catch ( IOException ex )
            {
                // ignore!
            }
            finally
            {
                if ( stream != null )
                    try { stream.close(); } catch ( Exception ignore ) {}
            }
        }
    }
    
    /**
     * Searches <filename> in a subdir under the current user's home.
     * 
     * @param subdir
     * @param filename
     */
    private void loadFromHomeDirectory( String subdir, String filename )
    {
        String home = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
        File file = new File( home + "/" + subdir, filename );
        
        InputStream stream = null;
        
        if ( file.exists() )
        {
            try
            {
                stream = new FileInputStream( file );
                currentConfig = new Properties( currentConfig );
                currentConfig.load( stream );
            }
            catch ( IOException ex )
            {
                // ignore!
            }
            finally
            {
                if ( stream != null )
                    try { stream.close(); } catch ( Exception ignore ) {}
            }
        }
    }
    
    /**
     * Searches <filename> in a subdir under "My Documents" (Windows)...
     * 
     * @param subdir
     * @param filename
     */
    private void loadFromMyDocuments( String subdir, String filename )
    {
        final FileSystemView filesystem = FileSystemView.getFileSystemView();
        
        if ( !filesystem.getHomeDirectory().equals( filesystem.getDefaultDirectory() ) )
        {
            File file = new File( filesystem.getDefaultDirectory() + "/" + subdir, filename );
            
            InputStream stream = null;
            
            if ( file.exists() )
            {
                try
                {
                    stream = new FileInputStream( file );
                    currentConfig = new Properties( currentConfig );
                    currentConfig.load( stream );
                }
                catch ( IOException ex )
                {
                    // ignore!
                }
                finally
                {
                    if ( stream != null ) try { stream.close(); } catch ( Exception ignore ) {}
                }
            }
        }
    }
    
    /**
     * Searches <filename> in the current working directory.
     * 
     * @param subdir
     * @param filename
     */
    @SuppressWarnings("unused")
    private void loadFromCurrentWorkingDirectory( String subdir, String filename )
    {
        File file = new File( System.getProperty( "user.dir" ), filename );
        
        InputStream stream = null;
        
        if ( file.exists() )
        {
            try
            {
                stream = new FileInputStream( file );
                currentConfig = new Properties( currentConfig );
                currentConfig.load( stream );
            }
            catch ( IOException ex )
            {
                // ignore!
            }
            finally
            {
                if ( stream != null )
                    try { stream.close(); } catch ( Exception ignore ) {}
            }
        }
    }
    
    private Method findSetter( TargetSpec spec )
    {
        Method[] methods = spec.targetClass.getDeclaredMethods();
        for ( int i = 0; i < methods.length; i++ )
        {
            Method method = methods[ i ];
            Class< ? >[] params = method.getParameterTypes();
            if ( ( params != null ) && ( params.length == 1 ) && method.getName().equals( "set" + spec.camelCaseTargetProperty ) )
            {
                return ( method );
            }
        }
        
        return ( null );
    }
    
    /**
     * Iterates over the loaded configuration and tries to set static properties
     * or fields on the registered classes.
     * 
     * @param config
     */
    private void loadSettings( Properties config )
    {
        for ( Object key: config.keySet() )
        {
            final String name = (String)key;
            final String value = config.getProperty( name );
            final TargetSpec spec = mapping.get( name );
            
            if ( spec == null )
                continue;
            
            final Method setter = findSetter( spec );
            try
            {
                // use a setter, if available
                if ( setter != null )
                {
                    final Class< ? > type = setter.getParameterTypes()[ 0 ];
                    
                    if ( type.isAssignableFrom( byte.class ) )
                    {
                        setter.invoke( null, Byte.parseByte( value ) );
                    }
                    else if ( type.isAssignableFrom( short.class ) )
                    {
                        setter.invoke( null, Short.parseShort( value ) );
                    }
                    else if ( type.isAssignableFrom( int.class ) )
                    {
                        setter.invoke( null, Integer.parseInt( value ) );
                    }
                    else if ( type.isAssignableFrom( long.class ) )
                    {
                        setter.invoke( null, Long.parseLong( value ) );
                    }
                    else if ( type.isAssignableFrom( float.class ) )
                    {
                        setter.invoke( null, Float.parseFloat( value ) );
                    }
                    else if ( type.isAssignableFrom( double.class ) )
                    {
                        setter.invoke( null, Double.parseDouble( value ) );
                    }
                    else if ( type.isAssignableFrom( boolean.class ) )
                    {
                        setter.invoke( null, Boolean.parseBoolean( value ) );
                    }
                    else if ( type.isAssignableFrom( String.class ) )
                    {
                        setter.invoke( null, value );
                    }
                }
                // else try a field
                else
                {
                    final Field field = spec.targetClass.getField( spec.targetProperty );
                    final Class< ? > type = field.getType();
                    
                    if ( type.isAssignableFrom( byte.class ) )
                    {
                        field.set( null, Byte.parseByte( value ) );
                    }
                    else if ( type.isAssignableFrom( short.class ) )
                    {
                        field.set( null, Short.parseShort( value ) );
                    }
                    else if ( type.isAssignableFrom( int.class ) )
                    {
                        field.set( null, Integer.parseInt( value ) );
                    }
                    else if ( type.isAssignableFrom( long.class ) )
                    {
                        field.set( null, Long.parseLong( value ) );
                    }
                    else if ( type.isAssignableFrom( float.class ) )
                    {
                        field.set( null, Float.parseFloat( value ) );
                    }
                    else if ( type.isAssignableFrom( double.class ) )
                    {
                        field.set( null, Double.parseDouble( value ) );
                    }
                    else if ( type.isAssignableFrom( boolean.class ) )
                    {
                        field.set( null, Boolean.parseBoolean( value ) );
                    }
                    else if ( type.isAssignableFrom( String.class ) )
                    {
                        field.set( null, value );
                    }
                }
            }
            catch ( Throwable t )
            {
                X3DLog.print( t );
            }
        }
    }
    
    /**
     * Inspects different points in the claspath and the filesystem for the
     * requested file resource and loads/merges the config settings in case.
     * 
     * @param subdir
     * @param filename
     */
    public void load( String subdir, String filename )
    {
        currentConfig.clear();
        
        loadFromClassPath( subdir, filename );
        loadFromHomeDirectory( subdir, filename );
        loadFromMyDocuments( subdir, filename );
        //loadFromCurrentWorkingDirectory( subdir, filename );
        
        loadSettings( currentConfig );
    }
}
