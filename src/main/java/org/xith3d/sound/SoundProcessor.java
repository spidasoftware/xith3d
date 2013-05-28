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
package org.xith3d.sound;

import java.util.ArrayList;
import java.util.HashMap;

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.scenegraph.BackgroundSound;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.PointSound;
import org.xith3d.scenegraph.Sound;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * A SoundProcessor is responsible for processing Sound Nodes in relation to a View.<br>
 * <br>
 * Most parts are taken from other classes originally coded by David Yazel.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class SoundProcessor
{
    private static final SoundProcessor INSTANCE = new SoundProcessor();
    
    private SoundDriver soundDriver = new org.xith3d.sound.drivers.javasound.SoundDriverImpl();
    
    private final HashMap<BranchGroup, ArrayList<Sound>> soundNodes = new HashMap<BranchGroup, ArrayList<Sound>>();
    
    private long lastFrameId = -1L;
    
    /**
     * Returns the soundProcessor's singleton instance.
     * @return the soundProcessor's singleton instance.
     */
    public static final SoundProcessor getInstance()
    {
        return ( INSTANCE );
    }
    
    /**
     * Sets the SoundDriver to be used by this SoundProcessor.
     * 
     * @param soundDriver
     */
    public void setSoundDriver( SoundDriver soundDriver )
    {
        this.soundDriver = soundDriver;
    }
    
    /**
     * Returns the used SoundDriver (default=javasound).
     * @return the used SoundDriver.
     */
    public final SoundDriver getSoundDriver()
    {
        return ( soundDriver );
    }
    
    /**
     * Adds a Sound to the SoundProcessor.
     * 
     * @param sound the Sound Node to be added
     */
    public void addSound( Sound sound, BranchGroup rootBranch )
    {
        ArrayList<Sound> list = soundNodes.get( rootBranch );
        if ( list == null )
        {
            list = new ArrayList<Sound>();
            soundNodes.put( rootBranch, list );
        }
        
        list.add( sound );
        
        
        X3DLog.debug( "A ", sound.getClass().getSimpleName(), " has been added to the SoundProcesor ", this );
    }
    
    /**
     * Removes a Sound Node from the SoundProcessor.
     * 
     * @param sound the Sound Node to be removed
     * 
     * @return true, if the Sound was presend before removing
     */
    public boolean removeSound( Sound sound )
    {
        for ( ArrayList<Sound> list : soundNodes.values() )
        {
            if ( list.remove( sound ) )
            {
                X3DLog.debug( "A ", sound.getClass().getSimpleName(), " has been removed from the SoundProcesor ", this );
                
                return ( true );
            }
        }
        
        return ( false );
    }
    
    /**
     * Removes all stored Sound Nodes from the list.
     */
    public void clearSoundList()
    {
        X3DLog.debug( "Sound list have been cleared in the SoundProcesor ", this );
        
        soundNodes.clear();
    }
    
    /**
     * Processes a Sound Node to let it play.
     * 
     * @param sound the Sound Node to be processed
     */
    private void process( Sound sound, Point3f soundLoc, View view, Point3f listenerPosition )
    {
        if ( ( soundDriver == null ) || !soundDriver.isOnline() )
            return;
        
        if ( !sound.isEnabled() && !sound.isPlaying() )
            return;
        
        if ( sound.isDisabled() )
            return;
        
        final boolean isBackgroundSound = ( sound instanceof BackgroundSound );
        
        // first determine if the sound should be active
        
        boolean setSoundLoc = false;
        boolean activate;
        if ( !sound.isEnabled() )
        {
            activate = false;
        }
        else if ( isBackgroundSound )
        {
            activate = true;
        }
        else
        {
            setSoundLoc = true;
            sound.getWorldTransform().get( soundLoc );
            
            float squaredDist = soundLoc.distanceSquared( listenerPosition );
            activate = ( squaredDist < ( view.getSoundActivationRadius() * view.getSoundActivationRadius() ) );
        }
        
        try
        {
            SoundSource ss = sound.getSource();
            
            // if the sound is not active then make sure it is deactivated
            if ( !activate )
            {
                if ( ss == null )
                    return;
                
                if ( ss.isPlaying() )
                {
                    ss.pause();
                    sound.setDeactivated( true );
                }
                
                return;
            }
            
            // if the sound should be active then make sure it is
            if ( ss == null )
            {
                SoundContainer sc = sound.getSoundContainer();
                
                // set up the sound source
                try
                {
                    ss = soundDriver.allocateSoundSource();
                    ss.setContainer( sc );
                    ss.setLoop( sound.isContinuousEnabled() );
                    
                    if ( isBackgroundSound )
                    {
                        ss.setRolloffFactor( 0 );
                        ss.setRelative( true );
                        ss.setPosition( 0f, 0f, 0f );
                    }
                    else
                    {
                        PointSound ps = (PointSound)sound;
                        
                        float max = ps.getMaxDistance();
                        
                        if ( max == -1 )
                            max = view.getSoundActivationRadius();
                        
                        float ref = ps.getReferenceDistance();
                        
                        if ( ref == -1 )
                            ref = 0.17f * max;
                        
                        ss.setMaxDistance( max * 0.9f );
                        ss.setReferenceDistance( ref );
                        ss.setMaxVolume( ps.getMaxVolume() );
                        ss.setMinVolume( ps.getMinVolume() );
                        ss.setRolloffFactor( ps.getRolloffFactor() );
                        ss.setRelative( false );
                        
                        X3DLog.debug( "max distance = ", ( max * 0.9f ), ", ", "ref = ", ref, ", ", "max vol = ", ps.getMaxVolume(), ", ", "min vol = ", ps.getMinVolume(), ", ", "vol = ", sound.getInitialGain() );
                    }
                    
                    ss.setVolume( sound.getInitialGain() );
                    
                    sound.setSource( ss );
                }
                catch ( Throwable t )
                {
                    X3DLog.print( t );
                    t.printStackTrace();
                    
                    sound.setDisabled( true );
                    
                    return;
                }
            }
            
            // if the sound node is not a background node then set the source position
            if ( setSoundLoc )
                ss.setPosition( soundLoc );
            
            // ok now we have a legitimate sound source.  We need to make sure it is
            // configured with the current state of the node
            
            if ( sound.isPaused() )
            {
                if ( sound.isPlaying() )
                    ss.pause();
            }
            else if ( sound.isEnabled() && !sound.isPlaying() )
            {
                if ( sound.wasRestarted() || sound.wasDeactivated() )
                {
                    ss.rewind();
                    ss.play();
                    sound.setRestarted( false );
                    sound.setDeactivated( false );
                }
            }
            else if ( !sound.isEnabled() && sound.isPlaying() )
            {
                ss.pause();
            }
        }
        catch ( Throwable t )
        {
            X3DLog.print( t );
            t.printStackTrace();
            
            soundDriver = null;
        }
    }
    
    /**
     * Processes a Sound Node to let it play.
     * 
     * @param sound the Sound Node to be processed
     */
    /**
     * This method is called once per frame to update the
     * positional and rotational data needed to process a Sound Node.
     * 
     * @param viewTransform the Transform3D of the current View
     */
    private void update( View view, Point3f listenerPosition )
    {
        if ( ( soundDriver != null ) && soundDriver.isOnline() )
        {
            Vector3f facingDirection = Vector3f.fromPool();
            Vector3f upDirection = Vector3f.fromPool();
            
            soundDriver.setListenerPosition( listenerPosition );
            
            view.getFacingDirection( facingDirection );//.normalize();
            view.getUpDirection( upDirection );//.normalize();
            soundDriver.setListenerOrientation( facingDirection, upDirection );
            
            Vector3f.toPool( upDirection );
            Vector3f.toPool( facingDirection );
        }
    }
    
    /**
     * Process all Sound Nodes, that have been collected from the scenegraph.
     */
    public void processAll( BranchGroup rootBranch, View view, long frameId, boolean force )
    {
        ArrayList<Sound> soundList = soundNodes.get( rootBranch );
        
        if ( soundList == null )
            return;
        
        Point3f listenerPosition = Point3f.fromPool();
        
        view.getPosition( listenerPosition );
        
        if ( ( frameId > lastFrameId ) || force )
        {
            if ( !force )
                lastFrameId = frameId;
            
            if ( soundNodes.size() > 0)
                update( view, listenerPosition );
        }
        
        X3DLog.debug( "We have ", soundNodes.size(), " Sound Nodes to process in the sound processor ", this );
        
        Point3f soundLoc = Point3f.fromPool();
        
        for ( int i = 0; i < soundNodes.size(); i++ )
        {
            process( soundList.get( i ), soundLoc, view, listenerPosition );
        }
        
        Point3f.toPool( soundLoc );
        Point3f.toPool( listenerPosition );
    }
    
    /**
     * Process all Sound Nodes, that have been collected from the scenegraph.
     */
    public void processAll( BranchGroup rootBranch, View view, long frameId )
    {
        processAll( rootBranch, view, frameId, false );
    }
    
    /**
     * Creates a new SoundProcessor.
     */
    private SoundProcessor()
    {
        // Nothing to do here
    }
}
