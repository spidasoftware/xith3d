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

import org.jagatoo.util.arrays.ArrayUtils;
import org.openmali.vecmath2.Point3f;

import org.xith3d.scenegraph.utils.CopyListener;
import org.xith3d.scenegraph.utils.LODWorkerThread;
import org.xith3d.utility.logging.X3DLog;

/**
 * The LODSwitch is a Switch Node extension, that handles its children as
 * discrete LOD items. Discrete LOD (level of detail) is a technique, that
 * selects an item depending on its distance to the camera.
 * 
 * <b>Don't use the regular addChild() methods of the NodeGroup interface, that
 * are inherited, but the addLODItem() methods.</b>
 * 
 * @see AbstractLODShape3D
 * 
 * @author Marvin Froehlich (aka Qudus)
 * @author Mathias Henze (aka cylab)
 */
public class LODSwitch extends Switch
{
    private static final long ETERNAL = -1L;
    private static final long INACTIVE = 0L;
    
    private float[] minDistances = new float[ 16 ];
    private float[] maxDistances = new float[ 16 ];
    private long[] lastActive = new long[ 16 ];
    
    private boolean first = true;
    private boolean containsLazyLoadables = false;
    private final Point3f translation = new Point3f();
    private int pendingChange = -1;
    
    // This needs to be volatile to be thread safe without synchronization (Java 1.5+ only)
    private volatile boolean pendingSetUpFinished = false;
    
    /**
     * @param index the index of the LOD item of interest
     * 
     * @return the minimal distance of the LOD item
     */
    public final float getMinDist( int index )
    {
        if ( ( index < 0 ) || ( index >= numChildren() ) )
            throw new ArrayIndexOutOfBoundsException( "There's no item with the index " + index );
        
        return ( minDistances[ index ] );
    }
    
    /**
     * @param item the LOD item of interest
     * 
     * @return the minimal distance of the LOD item
     */
    public final float getMinDist( Node item )
    {
        final int index = indexOf( item );
        
        return ( getMinDist( index ) );
    }
    
    /**
     * @param index the index of the LOD item of interest
     * 
     * @return the maximal distance of the LOD item
     */
    public final float getMaxDist( int index )
    {
        if ( ( index < 0 ) || ( index >= numChildren() ) )
            throw new ArrayIndexOutOfBoundsException( "There's no item with the index " + index );
        
        return ( maxDistances[ index ] );
    }
    
    /**
     * @param item the LOD item of interest
     * 
     * @return the maximal distance of the LOD item
     */
    public final float getMaxDist( Node item )
    {
        final int index = indexOf( item );
        
        return ( getMaxDist( index ) );
    }
    
    /**
     * @return true, if the child is set up or starts a pending change and returns false
     * 
     * @param index
     */
    protected void setLODChild( int index )
    {
        final Node child = getChild( index );
        // set no child if none is found...
        if ( child == null )
            super.setWhichChild( Switch.CHILD_NONE );
        
        // switch to normal and always set up nodes
        if ( !containsLazyLoadables || !( child instanceof LazyLoadable ) || ((LazyLoadable)child).isSetUp() )
        {
            super.setWhichChild( index );
            return;
        }
        
        // otherwise start a pending change...
        pendingChange = index;
        LODWorkerThread.getInstance().enqueue( new Runnable()
        {
            public void run()
            {
                try
                {
                    ((LazyLoadable)child).prepare();
                }
                catch ( Throwable t )
                {
                    // Should not happen... just log it.
                    X3DLog.print( t );
                }
                // Set the pending change to be finished, when the setUp() method returns
                pendingSetUpFinished = true;
            }
        } );
    }
    
    /**
     * <b>Unsupported for LODSwitch</b>
     * 
     * @deprecated just because it is unsupported.
     */
    @Deprecated
    @Override
    public void setWhichChild( int whichChild )
    {
        throw new UnsupportedOperationException( "The child selection is managed by the Switch." );
    }
    
    private void checkAndTearDownChildren()
    {
        final int currentIndex = getWhichChild();
        final int num = numChildren() - 1;
        final long current = System.currentTimeMillis();
        // a deactivated LOD level will be cached for 1min
        final long max = (60 * 1000);
        
        // check all levels except the least detailed one, if it can be discarded
        for ( int i = 0; i < num; i++ )
        {
            int x = ( currentIndex - i );
            if ( ( x != 0 ) && ( lastActive[ i ] > INACTIVE ) && ( ( lastActive[ i ] + max ) < current ) )
            {
                // since only lazy loadable nodes can have an active timestamp, it's safe to cast to LazyLoadable
                final LazyLoadable child = (LazyLoadable)getChild( i );
                if ( child.isSetUp() )
                {
                    lastActive[ i ] = INACTIVE;
                    // tear down the child on the current thread before invoking the clean up...
                    child.tearDown();
                    LODWorkerThread.getInstance().enqueue( new Runnable()
                    {
                        public void run()
                        {
                            child.cleanUp();
                        }
                    } );
                }
                break;
            }
        }
    }
    
    /**
     * Called by the Renderer to make the LODSwitch select the right item for
     * the given view position.
     */
    public void updateWhichChild( Point3f viewPosition )
    {
        //super.setWhichChild( Switch.CHILD_ALL );
        //if (true) return;
        
        // TODO: this belongs to a worker thread or an Intervall
        if ( containsLazyLoadables )
            checkAndTearDownChildren();
        
        // If there are no children available, set the Switch to CHILD_NONE
        if ( numChildren() == 0 )
        {
            super.setWhichChild( Switch.CHILD_NONE );
            
            return;
        }
        
        if ( first )
        {
            setLODChild( numChildren() - 1 );
            first = false;
            return;
        }

        // If there is an open pending change, process it first
        if ( pendingChange != -1 )
        {
            // if the setup is finished, change the child
            if ( pendingSetUpFinished )
            {
                Node child = getChild( pendingChange );
                if ( ( child != null ) && ( child instanceof LazyLoadable ) )
                {
                    ( (LazyLoadable)child ).setUp();
                }
                
                // Reset the pending fields to prepare the next pending change
                pendingChange = -1; 
                pendingSetUpFinished = false;
            }
            
            // With a pending change, don't change LODs
            return;
        }
        
        
        // If there is currently no child set, but there are some available, set the Switch to the first Child
        if ( getWhichChild() < 0 )
        {
            setLODChild( 0 );
            return;
        }
        
        // Get the index of the currently set child
        final int currentIndex = getWhichChild();
        
        // Calculate the distance between this nodes anchor and the viewPosition
        this.getWorldTransform().getTranslation( translation );
        final float dist = translation.distance( viewPosition );
        
        // If the distance is less than the minimum distance of the current LOD child,
        // search the next "better" LOD child
        if ( dist < minDistances[ currentIndex ] )
        {
            if ( lastActive[ currentIndex ] != ETERNAL )
                lastActive[ currentIndex ] = System.currentTimeMillis();
            
            // TODO: isn't just decrementing the currentIndex by one sufficient? "Skipping" a LOD level seems unlikely...
            for ( int i = currentIndex - 1; i >= 0; i-- )
            {
                if ( ( dist <= maxDistances[ i ] ) && ( dist >= minDistances[ i ] ) )
                {
                    setLODChild( i );
                    return;
                }
            }
            
            // If none is found, set the Switch to CHILD_NONE
            super.setWhichChild( Switch.CHILD_NONE );
        }
        // If the distance is greater than the maximum distance of the current LOD child
        // search the next "worse" LOD child
        else if ( dist > maxDistances[ currentIndex ] )
        {
            if ( lastActive[ currentIndex ] != ETERNAL )
                lastActive[ currentIndex ] = System.currentTimeMillis();
            
            // TODO: isn't just incrementing the currentIndex by one sufficient? "Skipping" a LOD level seems unlikely...
            for ( int i = currentIndex+1; i < numChildren(); i++ )
            {
                if ( ( dist <= maxDistances[ i ] ) && ( dist >= minDistances[ i ] ) )
                {
                    setLODChild( i );
                    return;
                }
            }
            
            // If none is found, set the Switch to CHILD_NONE
            super.setWhichChild( Switch.CHILD_NONE );
        }
    }
    
    public int addLODItem( Node item, float minDist, float maxDist )
    {
        if ( minDist > maxDist )
            throw new IllegalArgumentException( "minDist (" + minDist + ") is greater than maxDist (" + maxDist + ")" );
        
        if ( numChildren() == 0 )
        {
            minDistances[ 0 ] = minDist;
            maxDistances[ 0 ] = maxDist;
            
            super.addChild( item, 0 );
            
            return ( 0 );
        }
        
        
        // try to find the index of the new item by comparing the minDistance
        int index = -1;
        for ( int i = 0; i < numChildren(); i++ )
        {
            if ( minDistances[ i ] > minDist )
            {
                index = i;
                break;
            }
        }
        
        // if no item with a greater minDistance is already stored, append the new item
        if ( index < 0 )
        {
            index = numChildren();
        }
        
        if ( ( index > 0 ) && ( minDistances[ index - 1 ] > minDist ) )
            throw new IllegalArgumentException( "minDist MUST NOT overlap two items." );
        if ( ( index < numChildren() - 1 ) && ( maxDistances[ index + 1 ] < maxDist ) )
            throw new IllegalArgumentException( "maxDist MUST NOT overlap two items." );
        
        minDistances = ArrayUtils.ensureCapacity( minDistances, numChildren() + 1 );
        maxDistances = ArrayUtils.ensureCapacity( maxDistances, numChildren() + 1 );
        lastActive = ArrayUtils.ensureCapacity( lastActive, numChildren() + 1 );
            
        if ( index < numChildren() )
        {
            System.arraycopy( minDistances, index, minDistances, index + 1, numChildren() - index );
            System.arraycopy( maxDistances, index, maxDistances, index + 1, numChildren() - index );
            System.arraycopy( lastActive, index, lastActive, index + 1, numChildren() - index );
        }
        
        minDistances[ index ] = minDist;
        maxDistances[ index ] = maxDist;
        //lastActive[ index ] = ( item instanceof LazyLoadable ) ? ETERNAL : INACTIVE;
        lastActive[ index ] = INACTIVE;
        containsLazyLoadables |= item instanceof LazyLoadable;
            
        super.addChild( item, index );
        
        return ( index );
    }
    
    private final boolean recheckContainsLazyLoadables()
    {
        containsLazyLoadables = false;
        int n = numChildren();
        for ( int i = 0; i < n; i++ )
        {
            if ( getChild( i ) instanceof LazyLoadable )
            {
                containsLazyLoadables = true;
                break;
            }
        }
        
        return ( containsLazyLoadables );
    }
    
    protected void setLODItem( int index, Node item, boolean itemChanged, float minDist, float maxDist )
    {
        if ( ( index < 0 ) || ( index >= numChildren() ) )
            throw new ArrayIndexOutOfBoundsException( "There's no item with the index " + index );
        
        if ( ( index > 0 ) && ( minDistances[ index - 1 ] > minDist ) )
            throw new IllegalArgumentException( "minDist MUST NOT overlap two items." );
        if ( ( index < numChildren() - 1 ) && ( maxDistances[ index + 1 ] < maxDist ) )
            throw new IllegalArgumentException( "maxDist MUST NOT overlap two items." );
        
        minDistances[ index ] = minDist;
        maxDistances[ index ] = maxDist;
        
        if ( itemChanged )
        {
            Node oldItem = setChild( item, index );
            
            if ( oldItem instanceof LazyLoadable )
            {
                if ( !( item instanceof LazyLoadable ) )
                {
                    lastActive[ index ] = INACTIVE;
                    
                    recheckContainsLazyLoadables();
                }
            }
            else
            {
                if ( item instanceof LazyLoadable )
                {
                    lastActive[ index ] = INACTIVE;
//                    lastActive[ index ] = ETERNAL;
                    
                    recheckContainsLazyLoadables();
                }
            }
        }
    }
    
    public final void setLODItem( int index, Node item, float minDist, float maxDist )
    {
        boolean itemChanged = ( getChild( index ) != item );
        
        setLODItem( index, item, itemChanged, minDist, maxDist );
    }
    
    public final void setLODItem( int index, float minDist, float maxDist )
    {
        setLODItem( index, null, false, minDist, maxDist );
    }
    
    public void removeLODItem( int index )
    {
        if ( ( index < 0 ) || ( index >= numChildren() ) )
            throw new IllegalArgumentException( "There is no LODItem with that index." );
        
        int n = numChildren();
        
        System.arraycopy( minDistances, 0, minDistances, 0, index );
        System.arraycopy( minDistances, index + 1, minDistances, index, n - index - 1 );
        System.arraycopy( maxDistances, 0, maxDistances, 0, index );
        System.arraycopy( maxDistances, index + 1, maxDistances, index, n - index - 1 );
        System.arraycopy( lastActive, 0, lastActive, 0, index );
        System.arraycopy( lastActive, index + 1, lastActive, index, n - index - 1 );
        
        removeChild( index );
        
        if ( containsLazyLoadables )
        {
            recheckContainsLazyLoadables();
        }
    }
    
    public void removeLODItem( Node node )
    {
        int index = indexOf( node );
        
        if ( index < 0 )
            throw new IllegalArgumentException( "There is no such LODItem." );
        
        removeLODItem( index );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected LODSwitch newInstance()
    {
        boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        LODSwitch s = new LODSwitch();
        Node.globalIgnoreBounds = gib;
        
        return ( s );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LODSwitch sharedCopy( CopyListener listener )
    {
        LODSwitch copy = (LODSwitch)super.sharedCopy( listener );
        
        copy.minDistances = new float[ this.minDistances.length ];
        System.arraycopy( this.minDistances, 0, copy.minDistances, 0, this.minDistances.length );
        copy.maxDistances = new float[ this.maxDistances.length ];
        System.arraycopy( this.maxDistances, 0, copy.maxDistances, 0, this.maxDistances.length );
        
        if ( listener != null )
        {
            listener.onNodeCopied( this, copy, true );
        }
        
        return ( copy );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LODSwitch sharedCopy()
    {
        return ( (LODSwitch)super.sharedCopy() );
    }
    
    public LODSwitch()
    {
        super( Switch.CHILD_NONE );
    }
}
