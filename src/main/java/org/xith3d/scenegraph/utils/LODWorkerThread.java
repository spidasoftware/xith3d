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
package org.xith3d.scenegraph.utils;

import java.util.concurrent.ConcurrentLinkedQueue;
import org.xith3d.utility.logging.X3DLog;

/**
 * Mathias Henze (aka cylab)
 */
public class LODWorkerThread extends Thread
{
    private static final LODWorkerThread instance = new LODWorkerThread();
    private boolean shutDown = false;
    private volatile boolean waiting = false;
    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue< Runnable >();
    
    /**
     * Tries to cancel an enqued job, if its execution hasn't started yet.
     * 
     * @param job the job to cancel
     * @return true, if the job could be canceled.
     */
    public boolean cancel(Runnable job)
    {
        return ( queue.remove( job ) );
    }
    
    public void enqueue( Runnable job )
    {
        queue.add( job );
        if(waiting)
        {
            synchronized(queue)
            {
                queue.notify();
            }
        }
    }
    
    public void shutDown()
    {
        synchronized(queue)
        {
            shutDown = true;
            queue.notify();
        }
    }

    @Override
    public void run()
    {
        while( true )
        {
            synchronized(queue)
            {
                if ( shutDown )
                    break;

                try
                {
                    if( queue.isEmpty() )
                    {
                        waiting = true;
                        queue.wait();
                    }
                }
                catch ( InterruptedException ignore )
                {
                }
            }

            try
            {
                Runnable runnable = queue.poll();
                runnable.run();
            }
            catch ( Throwable t )
            {
                // Should not happen... just log it.
                X3DLog.print( t );
            }
            try
            {
                Thread.sleep(50);
            }
            catch ( InterruptedException ignore )
            {
            }
        }
    }
    
    public static LODWorkerThread getInstance()
    {
        return ( instance );
    }
    
    protected LODWorkerThread()
    {
        super( "Xith LOD worker thread" );
        
        setPriority( MIN_PRIORITY );
        
        start();
    }
}
