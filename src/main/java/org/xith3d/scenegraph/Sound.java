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
package org.xith3d.scenegraph;

import org.jagatoo.datatypes.Enableable;
import org.openmali.spatial.bounds.Bounds;
import org.xith3d.render.CanvasPeer;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;
import org.xith3d.sound.SingletonSoundContainer;
import org.xith3d.sound.SoundContainer;
import org.xith3d.sound.SoundProcessor;
import org.xith3d.sound.SoundSource;

/**
 * Base for all Sound Nodes to be placed into the scenegraph.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class Sound extends Leaf implements Enableable
{
    public static final float NO_FILTER = -1.0f;
    
    public static final int DURATION_UNKNOWN = -1;
    
    public static final int INFINITE_LOOPS = -1;
    
    private SoundContainer soundContainer = null;
    private SingletonSoundContainer singSoundContainer = null;
    private SoundSource source;
    
    private float initialGain;
    private int loopType;
    private boolean releaseEnabled;
    private boolean continuousEnabled;
    private boolean enabled = false;
    private boolean paused = false;
    private Bounds region;
    private float priority;
    private boolean disabled = false; // disabled by SoundProcessor because of error
    
    /**
     * Should be set only by the View
     * 
     * @param source
     */
    public void setSource( SoundSource source )
    {
        this.source = source;
    }
    
    /**
     * @return the current sound source supporting the sound. This is here
     *         mostly to support the View, but it can be used if done carefully
     *         to manipulate the sound source explcitely. This can return null
     *         if the sound is not active.
     */
    public SoundSource getSource()
    {
        return source;
    }
    
    public void setDisabled( boolean disabled )
    {
        this.disabled = disabled;
    }
    
    public final boolean isDisabled()
    {
        return ( disabled );
    }
    
    /**
     * Enables or disables this Sound Node.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled )
    {
        if ( ( !this.enabled ) && ( enabled ) )
            wasRestarted = true;
        this.enabled = enabled;
        if ( !enabled )
        {
            if ( source != null )
            {
                if ( source.isPlaying() )
                    source.stop();
                source.getSoundDriver().delete( source );
                source = null;
            }
            
        }
        
        if ( getSingletonSoundContainer() != null )
        {
            getSingletonSoundContainer().setEnabled( enabled );
        }
    }
    
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    private boolean wasRestarted = false;
    
    /**
     * <b>Never use this method on your own! It's just for internal use.</b>
     */
    public void setRestarted( boolean val )
    {
        wasRestarted = val;
    }
    
    /**
     * <b>Never use this method on your own! It's just for internal use.</b>
     */
    public final boolean wasRestarted()
    {
        return ( wasRestarted );
    }
    
    private boolean wasDeactivated = false;
    
    /**
     * <b>Never use this method on your own! It's just for internal use.</b>
     */
    public void setDeactivated( boolean val )
    {
        wasDeactivated = val;
    }
    
    /**
     * <b>Never use this method on your own! It's just for internal use.</b>
     */
    public final boolean wasDeactivated()
    {
        return ( wasDeactivated );
    }
    
    public final SoundContainer getSoundContainer()
    {
        return ( soundContainer );
    }
    
    public final SingletonSoundContainer getSingletonSoundContainer()
    {
        return ( singSoundContainer );
    }
    
    public void setInitialGain( float initialGain )
    {
        this.initialGain = initialGain;
    }
    
    public final float getInitialGain()
    {
        return ( initialGain );
    }
    
    public void setPriority( float priority )
    {
        this.priority = priority;
    }
    
    public final float getPriority()
    {
        return ( priority );
    }
    
    public void setRegion( Bounds region )
    {
        this.region = region;
    }
    
    public final Bounds getRegion()
    {
        return ( region );
    }
    
    public void play()
    {
        setEnabled( false );
        
        if ( source != null )
        {
            source.getSoundDriver().delete( source );
            source = null;
        }
        
        soundContainer = null;
        setEnabled( true );
    }
    
    public void setLoopType( int loopType )
    {
        this.loopType = loopType;
        
        if ( loopType == INFINITE_LOOPS )
            setContinuousEnabled( true );
    }
    
    public final int getLoopType()
    {
        return ( loopType );
    }
    
    public void setReleaseEnabled( boolean releaseEnable )
    {
        this.releaseEnabled = releaseEnable;
    }
    
    public final boolean isReleaseEnabled()
    {
        return ( releaseEnabled );
    }
    
    public void setContinuousEnabled( boolean continuousEnabled )
    {
        this.continuousEnabled = continuousEnabled;
    }
    
    public final boolean isContinuousEnabled()
    {
        return ( continuousEnabled );
    }
    
    /**
     * 
     * @param bounds
     */
    public void setSchedulingBounds( Bounds bounds )
    {
    }
    
    public final Bounds getSchedulingBounds()
    {
        return ( null );
    }
    
    /**
     * 
     * @param boundingLeaf
     */
    public void setSchedulingBoundingLeaf( BoundingLeaf boundingLeaf )
    {
    }
    
    public final BoundingLeaf getSchedulingBoundingLeaf()
    {
        return ( null );
    }
    
    public final long getDuration()
    {
        if ( getSingletonSoundContainer() == null )
            return ( 0L );
        
        return ( getSingletonSoundContainer().getDuration() );
    }
    
    public final boolean isReady()
    {
        return ( true );
    }
    
    /**
     * 
     * @param view
     * @return
     */
    public final boolean isReady( View view )
    {
        return ( false );
    }
    
    public final boolean isPlaying()
    {
        if ( source == null )
            return ( false );
        
        return ( source.isPlaying() );
    }
    
    /**
     * Pauses or resumes this Sound Node.
     * 
     * @param paused
     */
    public void setPaused( boolean paused )
    {
        this.paused = paused;
        
        if ( getSingletonSoundContainer() != null )
        {
            getSingletonSoundContainer().setPaused( enabled );
        }
    }
    
    /**
     * @return <i>true</i>, if the Sound Node has been paused
     */
    public final boolean isPaused()
    {
        return ( paused );
    }
    
    /**
     * If making node non-live when it was live deallocates sound resources
     * associated with this sound node, and marks this node as non-enabled by
     * call to setEnable(false).
     */
    @Override
    protected boolean setLive( boolean live )
    {
        if ( !super.setLive( live ) )
            return ( false );
        
        if ( live )
        {
            SoundProcessor.getInstance().addSound( this, this.getRoot() );
        }
        else
        {
            setEnabled( false );
            
            if ( source != null )
            {
                if ( source.isPlaying() )
                    source.stop();
                source.getSoundDriver().delete( source );
                source = null;
            }
            
            SoundProcessor.getInstance().removeSound( this );
        }
        
        return ( true );
    }
    
    /**
     * @return the position in the sound, in milli-seconds
     */
    public final long getSoundPosition()
    {
        return ( 0L );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /**
     * Traverses the scenegraph from this node on. If this Node is a Group it
     * will recusively run through each child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    @Override
    public boolean traverse( DetailedTraversalCallback callback )
    {
        return ( callback.traversalOperationCommon( this ) && callback.traversalOperation( this ) && callback.traversalOperationAfter( this ) && callback.traversalOperationCommonAfter( this ) );
    }
    
    // Constructors
    
    public Sound( SoundContainer soundContainer, float gain )
    {
        assert ( soundContainer != null ) : "SoundContainer must not be null.";
        
        this.soundContainer = soundContainer;
        if ( soundContainer instanceof SingletonSoundContainer )
        {
            this.singSoundContainer = (SingletonSoundContainer)soundContainer;
        }
        this.initialGain = gain;
    }
    
    public Sound( SoundContainer soundContainer )
    {
        this( soundContainer, 0.0f );
    }
}
