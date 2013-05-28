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
import java.util.List;

import org.xith3d.physics.simulation.Body;
import org.xith3d.physics.simulation.SimulationWorld;
import org.xith3d.physics.simulation.SurfaceParameters;

/**
 * The {@link CollisionResolver} checks for collisions and forwards them to
 * the {@link SimulationWorld} to resolve them.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class CollisionResolver implements CollisionListener
{
    private final ArrayList<Collideable> list1;
    private final ArrayList<Collideable> list2;
    
    private boolean ignoreStatic = true;
    
    private CollisionResolversManager manager = null;
    
    public final void setIgnoreStaticCollisions( boolean ignoreStatic )
    {
        this.ignoreStatic = ignoreStatic;
    }
    
    public final boolean getIgnoreStatic()
    {
        return ( ignoreStatic );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onCollision( Collision collision )
    {
        boolean consumed = false;
        
        final Body body1 = collision.getCollideable1().getBody();
        final Body body2 = collision.getCollideable2().getBody();
        
        final SimulationWorld simWorld;
        if ( body1 != null )
            simWorld = body1.getWorld();
        else if ( body2 != null )
            simWorld = body2.getWorld();
        else
            simWorld = null;
        
        final ArrayList< CollisionResolveListener > listeners = manager.getCollisionResolveListeners();
        final SurfaceParameters defaultSurfaceParameters = manager.getDefaultSurfaceParameters();
        for ( int l = 0; l < listeners.size(); l++ )
        {
            final CollisionResolveListener listener = listeners.get( l );
            
            if ( listener.onCollisionToResolve( collision, defaultSurfaceParameters, simWorld, consumed ) )
            {
                consumed = true;
            }
        }
        
        if ( !consumed && ( simWorld != null ) )
        {
            simWorld.resolveCollision( collision, body1, body2, defaultSurfaceParameters );
        }
    }
    
    public void update()
    {
        final CollisionEngine collisionEngine = manager.getCollisionEngine();
        
        for ( int i = 0; i < list1.size(); i++ )
        {
            final Collideable collideable1 = list1.get( i );
            
            if ( list2 == null )
            {
                collisionEngine.checkCollisions( (CollideableGroup)collideable1, ignoreStatic, this );
            }
            else
            {
                for ( int j = 0; j < list2.size(); j++ )
                {
                    final Collideable collideable2 = list2.get( j );
                    
                    collisionEngine.checkCollisions( collideable1, collideable2, ignoreStatic, this );
                }
            }
        }
    }
    
    protected void setManager( CollisionResolversManager manager )
    {
        this.manager = manager;
    }
    
    private static final void list2List( Body source, List< Collideable > target )
    {
        final int n = source.getCollideablesCount();
        
        for ( int j = 0; j < n; j++ )
        {
            target.add( source.getCollideable( j ) );
        }
    }
    
    private static final void list2List( Body[] source, List< Collideable > target )
    {
        for ( int i = 0; i < source.length; i++ )
        {
            final int n = source[i].getCollideablesCount();
            
            for ( int j = 0; j < n; j++ )
            {
                target.add( source[i].getCollideable( j ) );
            }
        }
    }
    
    private static final void list2List( List< Body > source, List< Collideable > target )
    {
        for ( int i = 0; i < source.size(); i++ )
        {
            final int n = source.get( i ).getCollideablesCount();
            
            for ( int j = 0; j < n; j++ )
            {
                target.add( source.get( i ).getCollideable( j ) );
            }
        }
    }
    
    /**
     * Creates a new {@link CollisionResolver}.
     * 
     * @param body1 the first Body
     * @param body2 the second Body
     */
    public CollisionResolver( Body body1, Body body2 )
    {
        if ( ( body1 == null ) || ( body2 == null ) )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        if ( ( body1.getCollideablesCount() == 0 ) || ( body2.getCollideablesCount() == 0 ) )
            throw new IllegalArgumentException( "No Collideables attached to one of the Bodies." );
        
        this.list1 = new ArrayList< Collideable >( body1.getCollideablesCount() );
        this.list2 = new ArrayList< Collideable >( body2.getCollideablesCount() );
        
        list2List( body1, list1 );
        list2List( body2, list2 );
    }
    
    /**
     * Creates a new {@link CollisionResolver}.
     * 
     * @param body1 the first Body
     * @param bodies2 the List of Bodies
     */
    public CollisionResolver( Body body1, List< Body > bodies2 )
    {
        if ( ( body1 == null ) || ( bodies2 == null ) )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        if ( ( body1.getCollideablesCount() == 0 ) || ( bodies2.size() == 0 ) )
            throw new IllegalArgumentException( "No Collideables attached to one of the Bodies." );
        
        this.list1 = new ArrayList< Collideable >( body1.getCollideablesCount() );
        int count = 0;
        for ( int i = 0; i < bodies2.size(); i++ )
        {
            count += bodies2.get( i ).getCollideablesCount();
        }
        this.list2 = new ArrayList< Collideable >( count );
        
        list2List( body1, list1 );
        list2List( bodies2, list2 );
    }
    
    /**
     * Creates a new {@link CollisionResolver}.
     * 
     * @param body1 the first Body
     * @param bodies2 the List of Bodies
     */
    public CollisionResolver( Body body1, Body[] bodies2 )
    {
        if ( ( body1 == null ) || ( bodies2 == null ) )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        if ( ( body1.getCollideablesCount() == 0 ) || ( bodies2.length == 0 ) )
            throw new IllegalArgumentException( "No Collideables attached to one of the Bodies." );
        
        this.list1 = new ArrayList< Collideable >( body1.getCollideablesCount() );
        int count = 0;
        for ( int i = 0; i < bodies2.length; i++ )
        {
            count += bodies2[ i ].getCollideablesCount();
        }
        this.list2 = new ArrayList< Collideable >( count );
        
        list2List( body1, list1 );
        list2List( bodies2, list2 );
    }
    
    /**
     * Creates a new {@link CollisionResolver}.
     * 
     * @param bodies1 the first List of Bodies
     * @param bodies2 the second List of Bodies
     */
    public CollisionResolver( List< Body > bodies1, List< Body > bodies2 )
    {
        if ( ( bodies1 == null ) || ( bodies2 == null ) )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        if ( ( bodies1.size() == 0 ) || ( bodies2.size() == 0 ) )
            throw new IllegalArgumentException( "No Collideables attached to one of the Bodies." );
        
        int count = 0;
        for ( int i = 0; i < bodies1.size(); i++ )
        {
            count += bodies1.get( i ).getCollideablesCount();
        }
        this.list1 = new ArrayList< Collideable >( count );
        
        count = 0;
        for ( int i = 0; i < bodies2.size(); i++ )
        {
            count += bodies2.get( i ).getCollideablesCount();
        }
        this.list2 = new ArrayList< Collideable >( count );
        
        list2List( bodies1, list1 );
        list2List( bodies2, list2 );
    }
    
    /**
     * Creates a new {@link CollisionResolver}.
     * 
     * @param bodies1 the first List of Bodies
     * @param bodies2 the second List of Bodies
     */
    public CollisionResolver( Body[] bodies1, Body[] bodies2 )
    {
        if ( ( bodies1 == null ) || ( bodies2 == null ) )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        if ( ( bodies1.length == 0 ) || ( bodies2.length == 0 ) )
            throw new IllegalArgumentException( "No Collideables attached to one of the Bodies." );
        
        int count = 0;
        for ( int i = 0; i < bodies1.length; i++ )
        {
            count += bodies1[ i ].getCollideablesCount();
        }
        this.list1 = new ArrayList< Collideable >( count );
        
        count = 0;
        for ( int i = 0; i < bodies2.length; i++ )
        {
            count += bodies2[ i ].getCollideablesCount();
        }
        this.list2 = new ArrayList< Collideable >( count );
        
        list2List( bodies1, list1 );
        list2List( bodies2, list2 );
    }
    
    /**
     * Creates a new {@link CollisionResolver}.
     * 
     * @param body1 the first Body
     * @param collideable2 the second Collideable
     */
    public CollisionResolver( Body body1, Collideable collideable2 )
    {
        if ( ( body1 == null ) || ( collideable2 == null ) )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        if ( body1.getCollideablesCount() == 0 )
            throw new IllegalArgumentException( "No Collideables attached to the Body." );
        
        this.list1 = new ArrayList< Collideable >( body1.getCollideablesCount() );
        this.list2 = new ArrayList< Collideable >( 1 );
        
        list2List( body1, list1 );
        list2.add( collideable2 );
    }
    
    /**
     * Creates a new {@link CollisionResolver}.
     * 
     * @param collideable1 the first Collideable
     * @param collideable2 the second Collideable
     */
    public CollisionResolver( Collideable collideable1, Collideable collideable2 )
    {
        if ( ( collideable1 == null ) || ( collideable2 == null ) )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        this.list1 = new ArrayList< Collideable >( 1 );
        this.list2 = new ArrayList< Collideable >( 1 );
        
        list1.add( collideable1 );
        list2.add( collideable2 );
    }
    
    /**
     * Creates a new collision match.
     * 
     * @param collideableGroup the {@link CollideableGroup}
     */
    public CollisionResolver( CollideableGroup collideableGroup )
    {
        if ( collideableGroup == null )
            throw new IllegalArgumentException( "None of the parameters must be null" );
        
        this.list1 = new ArrayList< Collideable >( 1 );
        this.list2 = null;
        
        list1.add( collideableGroup );
    }
}
