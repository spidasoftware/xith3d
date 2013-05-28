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
package org.xith3d.utility.launching;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.ArrayList;

import org.xith3d.utility.logging.X3DLog;

/**
 * Launches Xith3D apps and fixes the classpath through a ClassLoader hack.
 * 
 * @author Mathias Henze (aka cylab)
 */
public class Xith3DLauncher
{
    private static ClassLoader cl;
    
    public static void main( String[] args )
    {
        try
        {
            String classpathspec = System.getProperty( "xith3d.cp", System.getProperty( "xith3d.classpath", null ) );
            String mainclassspec = System.getProperty( "xith3d.mc", System.getProperty( "xith3d.mainclass", null ) );
            if ( classpathspec == null )
                throw new IllegalArgumentException( "Missing system property 'xith3d.classpath'!" );
            if ( mainclassspec == null )
                throw new IllegalArgumentException( "Missing system property 'xith3d.mainclass'!" );
            
            ArrayList< URL > classpath = new ArrayList< URL >( 8 );
            String last = null;
            String current = "";
            for ( StringTokenizer tokenizer = new StringTokenizer( classpathspec, ";:", true ); tokenizer.hasMoreTokens(); )
            {
                String token = tokenizer.nextToken();
                if ( !( token.equals( ":" ) || token.equals( ";" ) ) )
                {
                    current += token;
                    if ( token.length() < 3 )
                    {
                        last = token;
                    }
                    else
                    {
                        File location = new File( current );
                        if ( !location.exists() )
                        {
                            System.err.println( "Warning: classpathspec location '" + current + "' does not exist!" );
                            continue;
                        }
                        try
                        {
                            classpath.add( location.toURI().toURL() );
                        }
                        catch ( MalformedURLException e )
                        {
                            System.err.println( "Warning: could not convert location '" + current + "' to an URL!" );
                            continue;
                        }
                        current = "";
                        last = null;
                    }
                }
                else if ( last != null )
                {
                    current += token;
                }
            }
            
            cl = new IsolationClassLoader( classpath.toArray( new URL[ classpath.size() ] ), Xith3DLauncher.class.getClassLoader() );
            Class< ? > mainClass = cl.loadClass( mainclassspec );
            mainClass.getMethod( "main", new Class[]
            {
                String[].class
            } ).invoke( null, new Object[]
            {
                args
            } );
        }
        // TODO cylab 2007-MAR-08: catch some specific exceptions and give concrete error messages?
        /*
        catch (MalformedURLException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */
        catch ( Exception e )
        {
            X3DLog.error( "Error starting application: ", e.getMessage() );
            X3DLog.error( "Stacktrace: " );
            X3DLog.print( e );
        }
    }
    
    private static class IsolationClassLoader extends URLClassLoader
    {
        private ClassLoader parent;
        
        public IsolationClassLoader( URL[] urls, ClassLoader parent )
        {
            // don't pass in the parent, so that the superclass has no code path to delegate to
            super( urls, null );
            
            this.parent = parent;
        }
        
        @Override
        public Class< ? > loadClass( String name ) throws ClassNotFoundException
        {
            try
            {
                // first search the class in the local realm of the URLClassLoader
                return ( super.loadClass( name ) );
            }
            catch ( ClassNotFoundException e )
            {
                // only delegate to the parent, if no class was found, effectively reversing the class search order
                return ( parent.loadClass( name ) );
            }
        }
    }
}
