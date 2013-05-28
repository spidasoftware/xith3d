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
package org.xith3d.physics.simulation;

import java.util.ArrayList;

import org.jagatoo.datatypes.Enableable;
import org.xith3d.loop.Updatable;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.physics.collision.CollisionResolversManager;

/**
 * A simulation engine
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SimulationEngine implements Updatable, Enableable
{
    protected final ArrayList<SimulationWorld> worlds = new ArrayList<SimulationWorld>(1);
    
    private CollisionResolversManager collisionResolversManager = null;
    
    private long stepMicros = 5000L;
    private long maxStepMicros = 2000000L;
    
    private boolean enabled = true;
    
    /**
     * @return the list of worlds created and managed by this SimulationEngine.
     */
    public final ArrayList<SimulationWorld> getWorlds()
    {
        return ( worlds );
    }
    
    /*
     * CREATION METHODS
     */
    
    /**
     * Creates a new Simulation World.
     * 
     * @return the newly created World.
     */
    public abstract SimulationWorld newWorldImpl();
    
    /**
     * Creates a new Simulation World.
     * 
     * @return the newly created World.
     */
    public final SimulationWorld newWorld()
    {
        final SimulationWorld world = newWorldImpl();
        
        worlds.add( world );
        
        return ( world );
    }
    
    /**
     * Creates a new SurfaceParameters with specified params.
     * 
     * @param paramStrings the param strings to be specified, e.g. "mu" or "bounce"
     * @param paramValues the values of the params to be specified, e.g. ".1f" or "true"
     */
    public abstract SurfaceParameters newSurfaceParameters( String[] paramStrings, Object[] paramValues );
    
    /**
     * @return a new instance of {@link SurfaceParameters} filled with default values.
     */
    public final SurfaceParameters newSurfaceParameters()
    {
        return ( newSurfaceParameters( null, null ) );
    }
    
    /**
     * Destroys the given SimulationWorld.
     * 
     * @param world
     */
    public void destroyWorld( SimulationWorld world )
    {
        worlds.remove( world );
    }
    
    /**
     * Sets the constant internal step time in microseconds.
     * 
     * @param micros
     */
    public final void setStepSize( long micros )
    {
        if ( micros < 1L )
            throw new IllegalArgumentException( "micros must be greater then 0" );
        
        this.stepMicros = micros;
    }
    
    /**
     * @return the constant internal step time in microseconds.
     */
    public final long getStepSize()
    {
        return ( stepMicros );
    }
    
    /**
     * Sets the maximum internal step time in microseconds.
     * 
     * @param micros
     */
    public final void setMaxStepSize( long micros )
    {
        if ( micros < 1L )
            throw new IllegalArgumentException( "micros must be greater then 0" );
        
        this.maxStepMicros = micros;
    }
    
    /**
     * @return the maximum internal step time in microseconds.
     */
    public final long getMaxStepSize()
    {
        return ( maxStepMicros );
    }
    
    /**
     * Step the simulation = advance the time
     * @param stepMicros the size of the step which should
     * be taken. Note that 2 steps of .01f isn't equal to
     * 1 step of .02f : the smaller is your stepSize, the
     * more accurate your simulation (but computations take
     * more time). As always, you should find a tradeoff
     * between speed and accuracy (which is the whole point
     * of physic simulation, anyway).
     * Begin with .01f and adjust it later if you don't know
     * what to put.
     * Note : you could also make the stepSize "adaptative",
     * ie proportional to the (real) time that has passed since
     * the last step, but sometimes adaptive stepSize can
     * cause some simulation problems (jumps, instability).
     * If you have these problems you could also try to
     * advance by the right quantity of time by several
     * fixed size steps.
     */
    public final void step( long stepMicros )
    {
        for ( int i = 0; i < worlds.size(); i++ )
        {
            worlds.get( i ).step( stepMicros, collisionResolversManager );
        }
    }
    
    public final void setCollisionResolversManager( CollisionResolversManager collisionResolversManager )
    {
        this.collisionResolversManager = collisionResolversManager;
    }
    
    /**
     * Sets this SimulationEngine enabled/disabled.<br>
     * If not enabled, the update() method will do nothing.
     * 
     * @param enabled
     */
    public final void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
    
    /**
     * @return if this SimulationEngine is enabled.<br>
     * If not enabled, the update() method will do nothing.
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( isEnabled() )
        {
            step( timingMode.getMicroSeconds( frameTime ) );
        }
    }
    
    protected SimulationEngine()
    {
    }
}
