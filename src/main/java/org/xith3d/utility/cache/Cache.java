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
package org.xith3d.utility.cache;

import java.util.ArrayList;
import java.util.HashMap;

import org.jagatoo.datatypes.NamedObject;

/**
 * Class that manages cached items.
 * 
 * @author David Yazel
 */
public class Cache< T > implements NamedObject
{
    public static final int FIND_ANY = 0;
    public static final int FIND_BEST = 1;
    public static final int FIND_EXACT = 2;
    public static final int FIND_FIRST = 3;
    
    private static HashMap< String, Cache< ? >> caches = new HashMap< String, Cache< ? >>();
    
    private final String name;
    private final boolean shareable;
    private final ArrayList< CacheNode< T >> items;
    private int attempts = 0;
    private int hits = 0;
    
    public Cache( String name, boolean shareable )
    {
        this.name = name;
        this.shareable = shareable;
        this.items = new ArrayList< CacheNode< T >>();
        
        caches.put( name, this );
    }
    
    public final String getName()
    {
        return ( name );
    }
    
    @SuppressWarnings( "unchecked" )
    public static Cache< ? > getCache( Class< ? > classType, boolean shareable )
    {
        String key = classType.getName() + shareable;
        Cache< ? > c = caches.get( key );
        
        if ( c == null )
        {
            c = new Cache( key, shareable );
        }
        
        return ( c );
    }
    
    /**
     * @return the object containing the object.
     * 
     * @param o
     */
    private CacheNode< T > findObject( T o )
    {
        for ( CacheNode< T > cn: items )
        {
            if ( cn.o == o )
            {
                return ( cn );
            }
        }
        
        return ( null );
    }
    
    public synchronized void put( T o )
    {
        CacheNode< T > n = findObject( o );
        
        // if we already have an entry for this node then update its
        // statistics.
        if ( n != null )
        {
            if ( shareable )
            {
                int num = n.getNumUsers();
                
                if ( num >= 0 )
                {
                    n.setNumUsers( num - 1 );
                }
                
                n.setTimeLastAccessed( System.currentTimeMillis() );
            }
        }
        else
        {
            n = new CacheNode< T >( o );
            n.setNumUsers( 0 );
            n.setTimeCreated( System.currentTimeMillis() );
            items.add( n );
        }
    }
    
    private CacheNode< T > find( CacheMatchInterface< T > matcher, int findStyle )
    {
        CacheNode< T > best = null;
        float bestVal = 0;
        
        for ( CacheNode< T > cn: items )
        {
            if ( matcher != null )
            {
                float m = matcher.match( cn.o );
                
                if ( m == 1 )
                {
                    return ( cn );
                }
                else if ( m > 0 )
                {
                    if ( findStyle == FIND_FIRST )
                    {
                        return ( cn );
                    }
                    
                    if ( m > bestVal )
                    {
                        bestVal = m;
                        best = cn;
                    }
                }
            }
            else
            {
                return ( cn );
            }
        }
        
        if ( findStyle == FIND_EXACT )
        {
            return ( null );
        }
        
        return ( best );
    }
    
    private T get( CacheMatchInterface< T > matcher, int findStyle )
    {
        attempts++;
        
        CacheNode< T > n = find( matcher, findStyle );
        
        if ( n == null )
        {
            return ( null );
        }
        
        if ( shareable )
        {
            n.setNumRequested( n.getNumRequested() + 1 );
            n.setNumUsers( n.getNumUsers() + 1 );
            n.setTimeLastAccessed( System.currentTimeMillis() );
        }
        else
        {
            items.remove( n );
        }
        
        hits++;
        
        return ( n.o  );
    }
    
    public synchronized T getBest( CacheMatchInterface< T > matcher )
    {
        return ( get( matcher, FIND_BEST ) );
    }
    
    public synchronized T getFirst( CacheMatchInterface< T > matcher )
    {
        return ( get( matcher, FIND_FIRST ) );
    }
    
    public synchronized T getAny()
    {
        return ( get( null, FIND_ANY ) );
    }
    
    /**
     * Prints out the cache information to the log.
     */
    @SuppressWarnings( "unchecked" )
    public synchronized void dumpCacheInfo( boolean printItems )
    {
        System.out.println( "Cache : " + name );
        
        long totalMemory = 0;
        
        for ( CacheNode< T > cn: items )
        {
            if ( cn.o instanceof Cachable )
            {
                totalMemory += ( (Cachable< T >)cn.o ).memoryUsed();
                
                if ( printItems )
                {
                    System.out.println( "   " + ( (Cachable< T >)cn.o ).getName() + " " + ( (Cachable< T >)cn.o ).memoryUsed() + " bytes" );
                }
            }
        }
        
        System.out.println( "   " + items.size() + " items using " + totalMemory + " bytes" );
        System.out.println( "   " + attempts + " attempts with " + hits + " hits (" + ( (float)hits / (float)attempts ) + "%)" );
    }
    
    public static void dumpAllCacheInfo()
    {
        for ( Cache< ? > c: caches.values() )
        {
            c.dumpCacheInfo( true );
        }
    }
}
