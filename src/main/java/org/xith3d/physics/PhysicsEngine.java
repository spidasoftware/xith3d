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
package org.xith3d.physics;

import org.jagatoo.datatypes.Enableable;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.physics.collision.CollisionEngine;
import org.xith3d.physics.collision.CollisionResolveListener;
import org.xith3d.physics.collision.CollisionResolver;
import org.xith3d.physics.collision.CollisionResolversManager;
import org.xith3d.physics.simulation.SimulationEngine;
import org.xith3d.physics.simulation.SurfaceParameters;

/**
 * This is a handy utility class, that holds instances of
 * CollisionEngine and SimulationEngine.<br>
 * The update() method directly invokes the update() methods of these two engines.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class PhysicsEngine implements Updatable, Enableable
{
    private final CollisionResolversManager collisionResolversManager;
    private final CollisionEngine   collEngine;
    private final SimulationEngine  simEngine;
    private       PhysicsGFXManager gfxManager = null;
    
    private boolean enabled = true;
    
    public final CollisionResolversManager getCollisionResolversManager()
    {
        return ( collisionResolversManager );
    }
    
    public final void setDefaultSurfaceParameters( SurfaceParameters parameters )
    {
        getCollisionResolversManager().setDefaultSurfaceParameters( parameters );
    }
    
    public final SurfaceParameters getDefaultSurfaceParameters()
    {
        return ( getCollisionResolversManager().getDefaultSurfaceParameters() );
    }
    
    public final void addCollisionResolver( CollisionResolver cr )
    {
        getCollisionResolversManager().addCollisionResolver( cr );
    }
    
    public final void removeCollisionResolver( CollisionResolver cr )
    {
        getCollisionResolversManager().removeCollisionResolver( cr );
    }
    
    public final void addCollisionResolverListener( CollisionResolveListener l )
    {
        getCollisionResolversManager().addCollisionResolveListener( l );
    }
    
    public final void removeCollisionResolverListener( CollisionResolveListener l )
    {
        getCollisionResolversManager().removeCollisionResolveListener( l );
    }
    
    public final CollisionEngine getCollisionEngine()
    {
        return ( collEngine );
    }
    
    public final SimulationEngine getSimulationEngine()
    {
        return ( simEngine );
    }
    
    public final void setGFXManager( PhysicsGFXManager gfxManager )
    {
        this.gfxManager = gfxManager;
    }
    
    public final PhysicsGFXManager getGFXManager()
    {
        return ( gfxManager );
    }
    
    /**
     * Sets this PhysicsEngine enabled/disabled.<br>
     * If not enabled, the update() method will do nothing.
     * 
     * @param enabled
     */
    public final void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
        
        if (collEngine != null)
            collEngine.setEnabled( enabled );
        
        if ( simEngine != null )
            simEngine.setEnabled( enabled );
    }
    
    /**
     * @return if this PhysicsEngine is enabled.<br>
     * If not enabled, the update() method will do nothing.
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( simEngine != null )
            simEngine.update( gameTime, frameTime, timingMode );
        
        if ( collEngine != null )
            collEngine.update( gameTime, frameTime, timingMode );
        
        if ( gfxManager != null )
            gfxManager.update( gameTime, frameTime, timingMode );
    }
    
    protected PhysicsEngine( CollisionEngine collEngine, SimulationEngine simEngine )
    {
        this.collEngine = collEngine;
        this.simEngine = simEngine;
        this.gfxManager = new PhysicsGFXManager();
        
        if (simEngine != null)
        {
            this.collisionResolversManager = new CollisionResolversManager( collEngine, simEngine.newSurfaceParameters() );
            simEngine.setCollisionResolversManager( collisionResolversManager );
        }
        else
        {
            this.collisionResolversManager = null;
        }
    }
}
