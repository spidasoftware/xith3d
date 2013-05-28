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
package org.xith3d.resources;

import org.xith3d.loop.RenderLoop;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;

/**
 * This class helps you to load resources within a separate thread
 * while still controlling a loading screen.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class LoadingPhaseController extends ScheduledOperationImpl
{
    private int stage = 0;
    
    private final RenderLoop renderLoop;
    private final float reducedMaxFPS;
    private float oldMaxFPS = 0f;
    
    private final ResourceLoader resLoader;
    private final DelayedLoadingScreenUpdater loadingScreenUpdater;
    private ResourceBag resBag = null;
    private final ResourceBagReceiver resBagReceiver;
    
    protected float reduceMaxFPS( float reducedMaxFPS, RenderLoop renderLoop )
    {
        float oldMaxFPS = renderLoop.getMaxFPS();
        
        /*
         * To make the RenderLoop take as few system performance as possible
         * while still rendering some frames we reduce max-FPS to 5.
         */
        renderLoop.setMaxFPS( reducedMaxFPS );
        
        return ( oldMaxFPS );
    }
    
    protected void restoreMaxFPS( float oldMaxFPS, RenderLoop renderLoop )
    {
        renderLoop.setMaxFPS( oldMaxFPS );
    }
    
    /**
     * This method is executed within a separate Thread.
     * 
     * @param resLoader
     * @param loadingScreenUpdater
     * 
     * @return the ResourceBag filled with the loaded resources.
     */
    protected abstract ResourceBag load( ResourceLoader resLoader, LoadingScreenUpdater loadingScreenUpdater );
    
    private void initialize()
    {
        oldMaxFPS = reduceMaxFPS( reducedMaxFPS, renderLoop );
        
        new Thread("xith3d Loading Phase Controller")
        {
            @Override
            public void run()
            {
                resBag = null;
                
                resBag = load( resLoader, loadingScreenUpdater );
            }
        }.start();
    }
    
    protected void destroy()
    {
        /*
         * Restore old max-FPS...
         */
        restoreMaxFPS( oldMaxFPS, renderLoop );
        
        /*
         * ...and remove me from the OperationScheduler.
         */
        this.setAlive( false );
    }
    
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        switch ( stage )
        {
            case 0:
                initialize();
                this.stage = 1;
                break;
            case 1:
                if ( resBag != null )
                {
                    destroy();
                    resBagReceiver.setResourceBag( resBag );
                    this.stage = 2;
                }
                break;
        }
        
        loadingScreenUpdater.update( gameTime, frameTime, timingMode );
    }
    
    public LoadingPhaseController( RenderLoop renderLoop, float reducedMaxFPS, ResourceLoader resLoader, LoadingScreenUpdater loadingScreenUpdater, ResourceBagReceiver resBagReceiver )
    {
        super( true );
        
        this.renderLoop = renderLoop;
        this.reducedMaxFPS = reducedMaxFPS;
        this.resLoader = resLoader;
        this.loadingScreenUpdater = new DelayedLoadingScreenUpdater( loadingScreenUpdater );
        this.resBagReceiver = resBagReceiver;
    }
}
