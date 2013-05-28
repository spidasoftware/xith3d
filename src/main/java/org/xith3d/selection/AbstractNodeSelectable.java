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
package org.xith3d.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.xith3d.scenegraph.Node;

/**
 * @author Mathias Henze (aka cylab)
 */
public abstract class AbstractNodeSelectable<NodeType extends Node>  implements Selectable
{
    private NodeType node;
    private HashMap< Class< ? >, ArrayList< Object > > lookup= new HashMap< Class< ? >, ArrayList< Object > >();
    private boolean selected;
    private boolean selectedContext;
    
    @SuppressWarnings("unchecked")
    public < T > T get( Class< T > type )
    {
        ArrayList< T > list = (ArrayList<T>) lookup.get( type );
        if ( ( list == null ) || ( list.size() == 0 ) )
            return ( null );
        
        return ( list.get( 0 ) );
    }
    
        @SuppressWarnings( "unchecked" )
    public < T > List<T> lookup( Class< T > type )
    {
        ArrayList< T > list = (ArrayList<T>) lookup.get( type );
        if ( ( list == null ) || ( list.size() == 0 ) )
            return ( Collections.EMPTY_LIST );
        
        return ( list );
    }
    
    // This is maybe a bit too wastefull just for a fast lookup access...
    public void register( Object object )
    {
        ArrayList<Class<?>> types = new ArrayList<Class<?>>( 20 );
        Class<?> currentClass = object.getClass();
        do
        {
            types.add( currentClass );
            Class<?>[] interfaces = currentClass.getInterfaces();
            for ( int i = 0; i < interfaces.length; types.add( interfaces[ i++ ] ) );
        }
        while( (currentClass = currentClass.getSuperclass()) != Object.class );
        
        for( int i = 0; i < types.size(); i++ )
        {
            Class<?> type = types.get( i );
            ArrayList< Object > list = lookup.get( type );
            
            if ( list == null )
            {
                list = new ArrayList< Object >( 4 );
                lookup.put( type, list );
            }
            
            list.add( object );
        }
    }
    
    public void deregister( Object object )
    {
        Class< ? >[] interfaces = object.getClass().getInterfaces();
        Class< ? >[] classes = object.getClass().getClasses();
        
        for ( int i = 0; i < interfaces.length; i++ )
        {
            Class< ? > type = interfaces[ i ];
            ArrayList< Object > list = lookup.get( type );
            if ( list != null )
            {
                list.remove( object );
                if ( list.size() == 0 )
                    lookup.remove( type );
            }
        }
        
        for ( int i = 0; i < classes.length; i++ )
        {
            Class< ? > type = classes[ i ];
            ArrayList< Object > list = lookup.get( type );
            if ( list != null )
            {
                list.remove( object );
                if ( list.size() == 0 )
                    lookup.remove( type );
            }
        }
    }
    
    public NodeType getNode()
    {
        return ( node );
    }
    
    public boolean isSelectionBound()
    {
        return ( true );
    }

    public boolean isSelected()
    {
        return selected;
    }

    public boolean isSelectedContext()
    {
        return selectedContext;
    }

    public void setSelected( SelectionManager selectionManager, boolean selected )
    {
        this.selected = selected;
    }

    public void setSelectedContext( SelectionManager selectionManager, boolean selected )
    {
        selectedContext = selected;
    }
    
    public AbstractNodeSelectable( NodeType node )
    {
        this.node = node;
    }
}
