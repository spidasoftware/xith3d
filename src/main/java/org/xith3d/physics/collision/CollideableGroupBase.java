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
package org.xith3d.physics.collision;

import java.util.ArrayList;

import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Node;

/**
 * Common parent (abstract class) for all CollideableGroup Objects.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class CollideableGroupBase extends CollideableBase implements CollideableGroup
{
    private final CollideableGroupType type;
    private final ArrayList<Collideable> collideables;
    
    /**
     * {@inheritDoc}
     */
    public Group getBaseGFX()
    {
        Group group = new Group();
        
        for ( int i = 0; i < collideables.size(); i++ )
        {
            Node node = collideables.get( i ).getBaseGFX();
            
            if ( node != null )
            {
                group.addChild( node );
            }
        }
        
        return ( group );
    }
    
    /**
     * {@inheritDoc}
     */
    public Group getDebugGFX()
    {
        Group group = new Group();
        
        for ( int i = 0; i < collideables.size(); i++ )
        {
            Node node = collideables.get( i ).getDebugGFX();
            
            if ( node != null )
            {
                group.addChild( node );
            }
        }
        
        return ( group );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getInfo()
    {
        return ( type.getInfo() );
    }
    
    /**
     * {@inheritDoc}
     */
    public final CollideableGroupType getGroupType()
    {
        return ( type );
    }
    
    /**
     * {@inheritDoc}
     */
    public final String getType()
    {
        return ( type.getName() );
    }
    
    /**
     * {@inheritDoc}
     */
    public void addCollideable( Collideable c )
    {
        collideables.add( c );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeCollideable( Collideable c )
    {
        collideables.remove( c );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void removeAllCollideables()
    {
        for ( int i = collideables.size() - 1; i >= 0; i-- )
        {
            removeCollideable( collideables.get( i ) );
        }
    }
    
    /*
     * {@inheritDoc}
     */
    /*
    public final ArrayList<Collideable> getChildren()
    {
        return ( collideables );
    }
    */
    
    /**
     * {@inheritDoc}
     */
    public final Collideable getChild( int i )
    {
        return ( collideables.get( i ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final int getChildrenCount()
    {
        return ( collideables.size() );
    }
    
    public CollideableGroupBase( CollisionEngine engine, CollideableGroupType type )
    {
        super( engine );
        
        this.type = type;
        this.collideables = new ArrayList< Collideable >();
    }
}
