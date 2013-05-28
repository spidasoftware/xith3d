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
package org.xith3d.ui.hud.listmodels;

import java.util.ArrayList;

import org.xith3d.ui.hud.base.ListModel;

/**
 * The default implementation of {@link ListModel}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class DefaultAbstractListModel extends ListModel
{
    @SuppressWarnings( "unchecked" )
    private java.util.List items;
    
    private java.util.ArrayList<Object> userObjects = null;
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public void addItemImpl( int index, Object item )
    {
        items.add( index, item );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings( "unchecked" )
    public void setItemImpl( int index, Object item )
    {
        items.set( index, item );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object removeItemImpl( int index )
    {
        Object item = items.get( index );
        items.remove( index );
        
        return ( item );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void clear()
    {
        items.clear();
        
        if ( userObjects != null )
            userObjects.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemsCount()
    {
        return ( items.size() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Object getItemImpl( int index )
    {
        return ( items.get( index ) );
    }
    
    /**
     * Sets an item's user-object.
     * 
     * @param itemIndex
     * @param userObject
     */
    public void setItemUserObject( int itemIndex, Object userObject )
    {
        if ( userObjects == null )
        {
            if ( userObject == null )
                return;
            
            this.userObjects = new ArrayList<Object>();
        }
        
        if ( userObjects.size() < getItemsCount() )
        {
            for ( int i = userObjects.size(); i < getItemsCount(); i++ )
            {
                userObjects.add( null );
            }
        }
        
        userObjects.set( itemIndex, userObject );
    }
    
    /**
     * Gets an item's user-object.
     * 
     * @param itemIndex
     * @return the items user-object.
     */
    public final Object getItemUserObject( int itemIndex )
    {
        if ( ( userObjects == null ) || ( itemIndex >= userObjects.size() ) )
            return ( null );
        
        return ( userObjects.get( itemIndex ) );
    }
    
    @SuppressWarnings( "unchecked" )
    public DefaultAbstractListModel( java.util.List items )
    {
        this.items = items;
        
        if ( this.items == null )
            this.items = new java.util.ArrayList<Object>();
    }
    
    public DefaultAbstractListModel()
    {
        this.items = new java.util.ArrayList<Object>();
    }
}
