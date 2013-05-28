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

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.scenegraph.Texture2D;
import org.xith3d.scenegraph.Texture.MipmapMode;

/**
 * This {@link LoadingScreenUpdater} updates a loading screen within the
 * RenderLoop's thread.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DelayedLoadingScreenUpdater extends ScheduledOperationImpl implements LoadingScreenUpdater
{
    private final LoadingScreenUpdater loadingScreenUpdater;
    
    private int init_maxValue = 0;
    private String init_caption = null;
    private Texture2D init_backgroundTexture = null;
    private boolean initRequested = false;
    
    private int update_incValue = 0;
    private String update_caption = null;
    private Texture2D update_backgroundTexture = null;
    private boolean updateRequested = false;
    
    private final Object mutex = new Object();
    
    /**
     * {@inheritDoc}
     */
    public void init( int maxValue, String caption, Texture2D backgroundTexture )
    {
        synchronized ( mutex )
        {
            init_maxValue = maxValue;
            init_caption = caption;
            init_backgroundTexture = backgroundTexture;
            
            initRequested = true;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void init( int maxValue, String caption, String backgroundTexture )
    {
        init( maxValue, caption, TextureLoader.getInstance().getTextureOrNull( backgroundTexture, MipmapMode.BASE_LEVEL ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( int incValue, String caption, Texture2D backgroundTexture )
    {
        synchronized ( mutex )
        {
            update_incValue += incValue;
            update_caption = caption;
            update_backgroundTexture = backgroundTexture;
            
            updateRequested = true;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue, Texture2D backgroundTexture )
    {
        update( incValue, (String)null, backgroundTexture );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( Texture2D backgroundTexture )
    {
        update( +1, (String)null, backgroundTexture );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void updateOnly( Texture2D backgroundTexture )
    {
        update( 0, (String)null, backgroundTexture );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue, String caption, String backgroundTexture )
    {
        update( incValue, caption, TextureLoader.getInstance().getTexture( backgroundTexture, MipmapMode.BASE_LEVEL ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue, String caption )
    {
        update( incValue, caption, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue )
    {
        update( incValue, (String)null, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void updateOnly( String caption )
    {
        update( 0, caption, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( String caption )
    {
        update( +1, caption, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update()
    {
        update( +1, (String)null, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( long gameTime, long frameTime, TimingMode timingMode )
    {
        if ( initRequested || updateRequested )
        {
            synchronized ( mutex )
            {
                if ( initRequested )
                {
                    loadingScreenUpdater.init( init_maxValue, init_caption, init_backgroundTexture );
                    
                    init_maxValue = 0;
                    init_caption = null;
                    init_backgroundTexture = null;
                    initRequested = false;
                }
                
                if ( updateRequested )
                {
                    loadingScreenUpdater.update( update_incValue, update_caption, update_backgroundTexture );
                    
                    update_incValue = 0;
                    update_caption = null;
                    update_backgroundTexture = null;
                    updateRequested = false;
                }
            }
        }
    }
    
    /**
     * @param loadingScreenUpdater
     */
    public DelayedLoadingScreenUpdater( LoadingScreenUpdater loadingScreenUpdater )
    {
        super( true );
        
        this.loadingScreenUpdater = loadingScreenUpdater;
    }
}
