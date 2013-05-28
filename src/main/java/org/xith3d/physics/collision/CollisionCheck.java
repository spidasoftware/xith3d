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

/**
 * The CollisionCheck class is a simple wrapper for two {@link Collideable}s,
 * that are to be tested against each others by the {@link CollisionEngine}.
 * 
 * @see CollisionEngine#addCollisionCheck
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CollisionCheck
{
    private Collideable collideable1;
    private Collideable collideable2;
    
    private boolean ignoreStatic;
    
    private CollisionListener listener;
    
    public final void setCollideable1( Collideable c )
    {
        this.collideable1 = c;
    }
    
    public final Collideable getCollideable1()
    {
        return ( collideable1 );
    }
    
    public final void setCollideable2( Collideable c )
    {
        this.collideable2 = c;
    }
    
    public final Collideable getCollideable2()
    {
        return ( collideable2 );
    }
    
    public final void setIgnoreStaticCollisions( boolean ignoreStatic )
    {
        this.ignoreStatic = ignoreStatic;
    }
    
    public final boolean getIgnoreStatic()
    {
        return ( ignoreStatic );
    }
    
    /**
     * Sets the {@link CollisionListener}, that is being used instead of the CollisionEngine's
     * default {@link CollisionListener}.
     * 
     * @param cl
     */
    public final void setCollisionListener( CollisionListener cl )
    {
        this.listener = cl;
    }
    
    /**
     * @return the {@link CollisionListener}, that is being used instead of the CollisionEngine's
     * default {@link CollisionListener}.
     */
    public final CollisionListener getCollisionListener()
    {
        return ( listener );
    }
    
    public CollisionCheck( Collideable collideable1, Collideable collideable2, boolean ignoreStatic, CollisionListener listener )
    {
        this.collideable1 = collideable1;
        this.collideable2 = collideable2;
        
        this.ignoreStatic = ignoreStatic;
        
        this.listener = listener;
    }
    
    public CollisionCheck( Collideable collideable1, Collideable collideable2, boolean ignoreStatic )
    {
        this( collideable1, collideable2, ignoreStatic, null );
    }
    
    public CollisionCheck( Collideable collideable1, Collideable collideable2, CollisionListener listener )
    {
        this( collideable1, collideable2, false, listener );
    }
    
    public CollisionCheck( Collideable collideable1, Collideable collideable2 )
    {
        this( collideable1, collideable2, false, null );
    }
}
