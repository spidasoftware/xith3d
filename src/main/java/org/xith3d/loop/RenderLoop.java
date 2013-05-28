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
package org.xith3d.loop;

import java.util.Vector;

import org.jagatoo.util.timing.JavaTimer;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.opscheduler.Animator;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.OperationSchedulerImpl;
import org.xith3d.scenegraph.utils.LODWorkerThread;

/**
 * This loop renders the scene in a separate thread.
 * 
 * You can schedule operations to be done by the thread in the next loop iteration.
 * @see org.xith3d.loop.opscheduler.ScheduledOperation
 * 
 * You can schedule animations to be done by the thread in the next loop iteration.
 * @see org.xith3d.loop.opscheduler.Animatable
 * 
 * You can get the fps count from this loop and set the maximum FPS.
 * @see org.xith3d.loop.FPSListener
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class RenderLoop extends UpdatingThread implements GameTimeHost, Updater, RenderLoopController
{
    /**
     * You can start the RenderLoop in the same Thread as the app itself or in
     * a separate one.
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public enum RunMode
    {
        /**
         * Let the RenderLoop run in the same Thread.
         */
        RUN_IN_SAME_THREAD,
        
        /**
         * Let the RenderLoop run in a separate Thread.
         */
        RUN_IN_SEPARATE_THREAD,
        
        /**
         * Let the RenderLoop run in a separate Thread and wait for
         * nextIteration() invokation.
         * 
         * @see RenderLoop#nextFrame()
         */
        RUN_IN_SEPARATE_THREAD_AND_WAIT;
    }
    
    /**
     * Use this enum for the setStopOperation() method.<br>
     * It controls the RenderLoop's behavior when it gets stopped.
     * 
     * @see #setStopOperation(StopOperation)
     * 
     * @author Marvin Froehlich (aka Qudus)
     */
    public enum StopOperation
    {
        /**
         * When the RenderLoop stops, it just stops and does nothing more.<br>
         * This is the default.
         */
        DO_NOTHING,
        
        /**
         * When the RenderLoop stops, the exit() method is called.<br>
         * The exit() method calls System.gc() and System.exit( 0 );
         */
        EXIT,
        
        /**
         * When the RenderLoop stops, the destroy() method is called.<br>
         * The destroy() method calls the destroy() method on each registered
         * RenderEngine.
         * 
         * @see RenderLoop#destroy()
         */
        DESTROY,
        
        /**
         * When the RenderLoop stops, the destroy() method is called.<br>
         * The destroy() method calls the destroy() method on each registered
         * RenderEngine.
         * Then the exit() method is called, which calls System.gc() and System.exit( 0 );
         * 
         * @see RenderLoop#destroy()
         * @see RenderLoop#exit()
         */
        DESTROY_AND_EXIT;
    }
    
    public static final int PAUSE_RENDERING = 2;
    
    private RunMode runMode = null;
    private boolean nextFrameAllowed = false;
    private float maxFPS;
    private float fps = 0.0f;
    private long fpsCalcInterval;
    private final Vector< FPSListener > fpsListeners;
    private final Vector< RenderLoopListener > rlListeners;
    private OperationScheduler opScheder;
    private Updater updater;
    
    private Xith3DEnvironment x3dEnv = null;
    
    private StopOperation stopOperation = null;
    private Thread thread = null;
    
    /**
     * @return the RunMode this RenderLoop is running in.
     *         Returns <i>null</i>, if the RenderLoop hasn't been started.
     * 
     * @see RunMode
     */
    public RunMode getRunMode()
    {
        return ( runMode );
    }
    
    /**
     * Sets the StopOperation to invoke when the RenderLoop stops.
     * 
     * @see StopOperation
     * 
     * @param operation
     */
    public void setStopOperation( StopOperation operation )
    {
        if ( operation == null )
            throw new IllegalArgumentException( "StopOperation must not be null" );
        
        this.stopOperation = operation;
    }
    
    private StopOperation getStopOperation( RunMode runMode )
    {
        if ( stopOperation == null )
        {
            if ( runMode == null )
                return ( null );
            
            if ( runMode == RunMode.RUN_IN_SEPARATE_THREAD )
                return ( StopOperation.DESTROY_AND_EXIT  );
            
            if ( runMode == RunMode.RUN_IN_SEPARATE_THREAD_AND_WAIT )
                return ( StopOperation.DESTROY  );
            
            /* if (runMode == RunMode.RUN_IN_SAME_THREAD)*/
            return ( StopOperation.DESTROY_AND_EXIT  );
        }
        
        return ( stopOperation );
    }
    
    /**
     * @see StopOperation
     * 
     * @return the StopOperation to invoke when the RenderLoop stops.
     */
    public StopOperation getStopOperation()
    {
        return ( getStopOperation( getRunMode() ) );
    }
    
    /**
     * Sets the OperationScheduler to be used by this RenderLoop (can be null).
     * 
     * @param opScheder
     */
    public void setOperationScheduler( OperationScheduler opScheder )
    {
        if ( this.opScheder == opScheder )
            return;
        
        this.opScheder = opScheder;
    }
    
    /**
     * @return the OperationScheduler used by this RenderLoop.
     */
    public final OperationScheduler getOperationScheduler()
    {
        return ( opScheder );
    }
    
    /**
     * @return the Animator used by this RenderLoop
     */
    public Animator getAnimator()
    {
        return ( opScheder );
        /*
        if ( opScheder instanceof Animator )
            return ( (Animator)opScheder );
        else
            return ( null );
        */
    }
    
    /**
     * Adds a new FPSListener to this loop to be notified periodically.
     * 
     * @param l the new FPSListener
     */
    public void addFPSListener( FPSListener l )
    {
        //synchronized (fpsListeners)
        {
            fpsListeners.add( l );
        }
        
        if ( l instanceof ConsciousFPSListener )
        {
            ( (ConsciousFPSListener)l ).setRenderLoop( this );
        }
    }
    
    /**
     * Removes an FPSListener from this loop.
     * 
     * @param l the FPSListener to be removed
     */
    public void removeFPSListener( FPSListener l )
    {
        //synchronized (fpsListeners)
        {
            fpsListeners.remove( l );
        }
        
        if ( l instanceof ConsciousFPSListener )
        {
            ( (ConsciousFPSListener)l ).setRenderLoop( null );
        }
    }
    
    /**
     * Sets the {@link Updater}, that manages the Updatables.
     */
    public void setUpdater( Updater updater )
    {
        this.updater = updater;
    }
    
    /**
     * @return the {@link Updater}, that manages the Updatables.
     */
    public final Updater getUpdater()
    {
        return ( updater );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void addUpdatable( Updatable updatable )
    {
        if ( updater == null )
        {
            throw new NullPointerException( "No Updater present!" );
        }
        
        updater.addUpdatable( updatable );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void removeUpdatable( Updatable updatable )
    {
        if ( updater == null )
        {
            throw new NullPointerException( "No Updater present!" );
        }
        
        updater.removeUpdatable( updatable );
    }
    
    /**
     * Adds a new RenderLoopListener to this loop.
     * 
     * @param l the new RenderLoopListener
     */
    public void addRenderLoopListener( RenderLoopListener l )
    {
        synchronized ( rlListeners )
        {
            rlListeners.add( l );
        }
    }
    
    /**
     * Removes a RenderLoopListener from this loop.
     * 
     * @param l the RenderLoopListener to be removed
     */
    public void removeRenderLoopListener( RenderLoopListener l )
    {
        synchronized ( rlListeners )
        {
            rlListeners.remove( l );
        }
    }
    
    /**
     * Sets the {@link Xith3DEnvironment} to this RenderLoop to be updated
     * frame-by-frame.
     * 
     * @param env the {@link Xith3DEnvironment} to be updated by this loop
     */
    public void setXith3DEnvironment( Xith3DEnvironment env )
    {
        this.x3dEnv = env;
    }
    
    /**
     * @return the {@link Xith3DEnvironment}, which is linked to this RenderLoop.
     */
    public final Xith3DEnvironment getXith3DEnvironment()
    {
        return ( x3dEnv );
    }
    
    /**
     * This method is executed by the RenderLoop when the thread has been started.
     */
    protected void onRenderLoopStarted()
    {
        synchronized ( rlListeners )
        {
            for ( int i = 0; i < rlListeners.size(); i++ )
            {
                rlListeners.get( i ).onRenderLoopStarted( this );
            }
        }
    }
    
    /**
     * This method is executed by the RenderLoop when the thread has been stopped.
     * 
     * @param gameTime the current gameTime
     * @param timingMode
     * @param averageFPS the average FPS over the time the loop was running
     */
    protected void onRenderLoopStopped( long gameTime, TimingMode timingMode, float averageFPS )
    {
        synchronized ( rlListeners )
        {
            for ( int i = 0; i < rlListeners.size(); i++ )
            {
                rlListeners.get( i ).onRenderLoopStopped( this, getGameTime(), timingMode, averageFPS );
            }
        }
    }
    
    /**
     * This method is invoked, when the RenderLoop stopped and the
     * StopOperation is set to DESTROY or DESTROY_AND_EXIT.<br>
     * <br>
     * It calls destroy() on any registered RenderEngine and
     * sets any MouseDevice registered to the InputManager to non-exclusive.<br>
     * <br>
     * <b>Don't invoke this method directly except in a super call.
     * Use end() instead.</b>
     * 
     * @see StopOperation
     * @see #setStopOperation(StopOperation)
     * @see #end()
     */
    protected void destroy()
    {
        LODWorkerThread.getInstance().shutDown();
        
        if ( x3dEnv != null )
        {
            x3dEnv.destroy();
        }
    }
    
    /**
     * This method is invoked, when the RenderLoop stopped and the
     * StopOperation is set to DESTROY_AND_EXIT.<br>
     * <br>
     * <b>Don't invoke this method directly except in a super call.
     * Use end() instead.</b>
     * 
     * @see #setStopOperation(StopOperation)
     * @see #end()
     */
    protected void exit()
    {
        System.gc();

        //SPIDA - for our version of xith we do not want a system exit ever called
//        System.exit( 0 );
    }
    
    /**
     * This method is invoked, when the RenderLoop stopped and the
     * StopOperation is set to DESTROY_AND_EXIT.<br>
     * <br>
     * <b>Don't invoke this method directly except in a super call.
     * Use end() instead.</b>
     * 
     * @see #setStopOperation(StopOperation)
     * @see #end()
     */
    protected void destroyAndExit()
    {
        destroy();
        exit();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void end()
    {
        if ( getThread() == null )
        {
            if ( getStopOperation() == null )
            {
                destroyAndExit();
            }
            else
            {
                switch ( getStopOperation() )
                {
                    case DO_NOTHING:
                        break;
                    
                    case DESTROY:
                        destroy();
                        break;
                    
                    case EXIT:
                        exit();
                        break;
                    
                    case DESTROY_AND_EXIT:
                        destroyAndExit();
                        break;
                }
            }
        }
        else
        {
            super.end();
        }
    }
    
    /**
     * This event is fired by the RenderLoop when the pauseMode has been increased.
     * 
     * @param gameTime the current gameTime
     * @param timingMode
     * @param pauseMode the current pause-mode
     */
    protected void onRenderLoopPaused( long gameTime, TimingMode timingMode, int pauseMode )
    {
        synchronized ( rlListeners )
        {
            for ( int i = 0; i < rlListeners.size(); i++ )
            {
                rlListeners.get( i ).onRenderLoopPaused( this, getGameTime(), timingMode, pauseMode );
            }
        }
    }
    
    /**
     * This event is fired by the RenderLoop when the pauseMode has been released.
     * 
     * @param gameTime the current gameTime
     * @param timingMode
     * @param pauseMode the current pause-mode
     */
    protected void onRenderLoopResumed( long gameTime, TimingMode timingMode, int pauseMode )
    {
        synchronized ( rlListeners )
        {
            for ( int i = 0; i < rlListeners.size(); i++ )
            {
                rlListeners.get( i ).onRenderLoopResumed( this, getGameTime(), timingMode, pauseMode );
            }
        }
    }
    
    /**
     * Sets the maximum frames per second.
     */
    public void setMaxFPS( float maxFPS )
    {
        this.maxFPS = maxFPS;
        setMinIterationTime( (long)( 1000000000.0f / maxFPS ) );
    }
    
    /**
     * @return the maximum frames per second.
     */
    public float getMaxFPS()
    {
        return ( maxFPS );
    }
    
    /**
     * @return the current average frames per second
     */
    public float getFPS()
    {
        return ( fps );
    }
    
    /**
     * @return the average frames per second over the total game-time.
     */
    public float getTotalAverageFPS()
    {
        if ( getGameNanoTime() == 0L )
            return ( -1f );
        
        return ( (float)getIterationsCount() / ( (float)getGameNanoTime() / 1000000000L ) );
    }
    
    /**
     * Sets the interval in which the FPS are recalculated
     * 
     * @param micros the new calculation interval
     */
    public void setFPSCalcInterval( long micros )
    {
        this.fpsCalcInterval = micros;
    }
    
    /**
     * @return the interval in which the FPS are recalculated
     */
    public long getFPSCalcInterval()
    {
        return ( fpsCalcInterval );
    }
    
    /**
     * This method is called each loop iteration before the renderNextFrame()
     * methiod.
     * 
     * @param gameTime the current game time
     * @param frameTime time needed to render the last frame
     * @param timingMode
     */
    protected void prepareNextFrame( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( x3dEnv != null )
        {
            x3dEnv.updatePhysicsEngine( gameTime, frameTime, timingMode );
            
            x3dEnv.updateInputSystem( gameTime, timingMode );
        }
        
        final OperationScheduler opScheder = getOperationScheduler();
        if ( opScheder != null )
        {
            opScheder.update( gameTime, frameTime, timingMode );
        }
        
        if ( ( updater != null ) && ( updater != opScheder ) )
        {
            updater.update( gameTime, frameTime, timingMode );
        }
    }
    
    /**
     * Just calls the render() method on the linked Xith3DEnvironment.
     * 
     * @param gameTime the current game time
     * @param frameTime time needed to render one frame
     * @param timingMode
     */
    protected void renderNextFrame( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( x3dEnv != null )
        {
            x3dEnv.render( getGameNanoTime(), getLastNanoFrameTime() );
        }
        else
        {
            System.err.println( "No Xith3DEnvironment registered!" );
        }
    }
    
    /**
     * This method is called each loop iteration.
     * It just updates all registered Keyboard- and MouseDevices and calls invokeRendering(long).
     * Override this method if you want something more to be done each iteration.
     * 
     * @param gameTime the current game time
     * @param frameTime time needed to render one frame
     * @param timingMode
     */
    protected void loopIteration( long gameTime, long frameTime, TimingMode timingMode )
    {
        prepareNextFrame( gameTime, frameTime, timingMode );
        
        if ( ( getPauseMode() & PAUSE_RENDERING ) == 0 )
        {
            renderNextFrame( gameTime, frameTime, timingMode );
        }
    }
    
    /**
     * This method is called by the render loop each counting interval
     * 
     * @param fps the average frames count during the last interval
     */
    protected void onFPSCountIntervalHit( float fps )
    {
        // notify FPSListeners
        if ( !fpsListeners.isEmpty() )
        {
            //synchronized (fpsListeners)
            {
                for ( int i = 0; i < fpsListeners.size(); i++ )
                {
                    fpsListeners.get( i ).onFPSCountIntervalHit( getFPS() );
                }
            }
        }
    }
    
    private long framesCountStartTime = 0L;
    private long framesCountTimeDelta;
    private int frames;
    
    /**
     * Calculates FPS.
     * 
     * @param gameTime the current game time
     * @param timingMode
     * 
     * @return true, if the count interval has been hit
     */
    private void calcFPS( long gameTime, TimingMode timingMode )
    {
        frames++;
        framesCountTimeDelta = timingMode.getMicroSeconds( gameTime - framesCountStartTime );
        if ( framesCountTimeDelta >= fpsCalcInterval )
        {
            fps = ( (float)frames / ( framesCountTimeDelta / 1000000f ) );
            framesCountStartTime = gameTime;
            frames = 0;
            
            onFPSCountIntervalHit( fps );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        loopIteration( gameTime, frameTime, timingMode );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected long nextIteration( boolean force )
    {
        calcFPS( getGameTime(), getTimingMode() );
        
        return ( super.nextIteration( force ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public long nextFrame()
    {
        if ( runMode == null )
        {
            return ( nextIteration( true ) );
        }
        else if ( runMode == RunMode.RUN_IN_SEPARATE_THREAD_AND_WAIT )
        {
            nextFrameAllowed = true;
            return ( -1L );
        }
        
        throw new IllegalStateException( "The RenderLoop is not in interactive mode." );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loop()
    {
        if ( runMode == RunMode.RUN_IN_SEPARATE_THREAD_AND_WAIT )
        {
            while ( !isStopping() )
            {
                if ( nextFrameAllowed )
                {
                    nextFrameAllowed = false;
                    
                    nextIteration( false );
                }
                else
                {
                    try
                    {
                        Thread.sleep( getMinIterationTime() / 1000000L );
                    }
                    catch ( InterruptedException e )
                    {
                    }
                }
            }
        }
        else
        {
            while ( !isStopping() )
            {
                nextIteration( false );
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Thread getThread()
    {
        return ( thread );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void run()
    {
        onRenderLoopStarted();
        setPauseMode( PAUSE_NONE );
        
        super.run();
        
        RunMode tmpRunMode = this.runMode;
        this.runMode = null;
        
        onRenderLoopStopped( getGameTime(), getTimingMode(), getTotalAverageFPS() );
        
        switch ( getStopOperation( tmpRunMode ) )
        {
            case DO_NOTHING:
                break;
            
            case DESTROY:
                destroy();
                break;
            
            case EXIT:
                exit();
                break;
            
            case DESTROY_AND_EXIT:
                destroyAndExit();
                break;
        }
    }
    
    /**
     * Starts this RenderLoop.
     * 
     * @param runMode the RunMode this RenderLoop will run in
     * @param timingMode the TimingMode to use
     */
    public void begin( RunMode runMode, TimingMode timingMode )
    {
        if ( !isRunning() )
        {
            if ( x3dEnv != null )
            {
                x3dEnv.checkRenderPreferences();
            }
            
            setTimingMode( timingMode );
            this.runMode = runMode;
            this.nextFrameAllowed = false;
            
            System.gc();
            
            if ( ( runMode == RunMode.RUN_IN_SEPARATE_THREAD ) || ( runMode == RunMode.RUN_IN_SEPARATE_THREAD_AND_WAIT ) )
            {
                this.thread = new Thread( this );
                thread.setName( "Xith3D render thread" );
                this.thread.start();
            }
            else
            {
                this.thread = Thread.currentThread();
                run();
            }
        }
    }
    
    /**
     * Starts this RenderLoop.
     * 
     * @param runMode the RunMode this RenderLoop will run in
     */
    public final void begin( RunMode runMode )
    {
        begin( runMode, TimingMode.MICROSECONDS );
    }
    
    /**
     * Starts this rendering-loop in a separate Thread and with TimingMode.MILLISECONDS.
     * 
     * @param timingMode the TimingMode to use
     */
    public final void begin( TimingMode timingMode )
    {
        begin( RunMode.RUN_IN_SAME_THREAD, timingMode );
    }
    
    /**
     * Starts this rendering-loop in a separate Thread and with TimingMode.MILLISECONDS.
     */
    public final void begin()
    {
        begin( RunMode.RUN_IN_SAME_THREAD, TimingMode.MICROSECONDS );
    }
    
    /**
     * pauses rendering in this RenderLoop
     */
    public void pauseRendering()
    {
        setPauseMode( getPauseMode() + RenderLoop.PAUSE_RENDERING );
        
        onRenderLoopPaused( getGameTime(), getTimingMode(), getPauseMode() );
    }
    
    /**
     * resumes rendering in this RenderLoop
     */
    public void resumeRendering()
    {
        setPauseMode( getPauseMode() - RenderLoop.PAUSE_RENDERING );
        
        onRenderLoopResumed( getGameTime(), getTimingMode(), getPauseMode() );
    }
    
    /**
     * Creates a new instance
     * 
     * @param x3dEnv the {@link Xith3DEnvironment} to be linked with this RenderLoop.
     * @param maxFPS the maximum FPS to render at
     */
    public RenderLoop( Xith3DEnvironment x3dEnv, float maxFPS )
    {
        this.setTimer( new JavaTimer() );
        
        this.fpsListeners = new Vector< FPSListener >();
        this.rlListeners = new Vector< RenderLoopListener >();
        
        this.setXith3DEnvironment( x3dEnv );
        
        this.setFPSCalcInterval( 500000L );
        
        this.setMaxFPS( maxFPS );
        
        OperationSchedulerImpl opScheder = new OperationSchedulerImpl( this );
        setOperationScheduler( opScheder );
        setUpdater( opScheder );
    }
    
    /**
     * Creates a new instance
     * 
     * @param maxFPS the maximum FPS to render at
     */
    public RenderLoop( float maxFPS )
    {
        this( null, maxFPS );
    }
    
    /**
     * Creates a new instance
     * 
     * @param x3dEnv the {@link Xith3DEnvironment} to be linked with this RenderLoop.
     */
    public RenderLoop( Xith3DEnvironment x3dEnv )
    {
        this( x3dEnv, Float.MAX_VALUE );
    }
    
    /**
     * Creates a new instance
     */
    public RenderLoop()
    {
        this( Float.MAX_VALUE );
    }
}
