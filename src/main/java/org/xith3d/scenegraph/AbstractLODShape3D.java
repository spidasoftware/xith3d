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

/**
 * The LODShape3D is a Shape3D Node extension, that handles discrete LOD.
 * Discrete LOD (level of detail) is a technique, that
 * selects an item depending on its distance to the camera.
 * 
 * @see LODSwitch
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class AbstractLODShape3D extends Shape3D
{
    private String[] lodNames = new String[ 16 ];
    
    private float[] minDistances = new float[ 16 ];
    private float[] maxDistances = new float[ 16 ];
    
    private int numLODs;
    private int currentLOD;
    private boolean isInitialized = false;
    
    /**
     * @param index the index of the LOD item of interest
     * 
     * @return the minimal distance of the LOD item
     */
    public final float getMinDist( int index )
    {
        if ( index >= numLODs )
            throw new ArrayIndexOutOfBoundsException( "There's no item with the index " + index );
        
        return ( minDistances[ index ] );
    }
    
    /**
     * @param index the index of the LOD item of interest
     * 
     * @return the maximal distance of the LOD item
     */
    public final float getMaxDist( int index )
    {
        if ( index >= numLODs )
            throw new ArrayIndexOutOfBoundsException( "There's no item with the index " + index );
        
        return ( maxDistances[ index ] );
    }
    
    /**
     * Returns the name of the LOD with the given index.
     * 
     * @param lod the LOD, for which the name is requested
     * 
     * @return the name of the given LOD
     */
    public final String getLODName( int lod )
    {
        return ( lodNames[ currentLOD ] );
    }
    
    /**
     * @return the number of LODs
     */
    public final int getNumLODs()
    {
        return ( numLODs );
    }
    
    private final int getCurrentLOD()
    {
        return ( currentLOD );
    }
    
    /**
     * This eveent is fired when the LOD has changed.<br>
     * This is always done by the redner thread.
     * 
     * @param oldLOD the old LOD
     * @param newLOD the new LOD
     * @param name the name of the new LOD
     */
    protected abstract void onLODChanged( int oldLOD, int newLOD, String name );
    
    /**
     * Called by the Renderer to make the LODSwitch select the right item for
     * the given view position.
     */
    public void updateLOD( Point3f viewPosition )
    {
        //super.setWhichChild( Switch.CHILD_ALL );
        //if (true) return;
        
        if ( getNumLODs() == 0 )
        {
            currentLOD = -1;
            return;
        }
        
        if ( getCurrentLOD() < 0 )
        {
            this.currentLOD = 0;
        }
        
        final int oldLOD = getCurrentLOD();
        
        Point3f translation = Point3f.fromPool();
        this.getWorldTransform().getTranslation( translation );
        final float dist = translation.distance( viewPosition );
        Point3f.toPool( translation );
        
        if ( isInitialized )
        {
            if ( dist < minDistances[ oldLOD ] )
            {
                for ( int i = oldLOD; i >= 0; i-- )
                {
                    if ( ( dist <= maxDistances[ i ] ) && ( dist >= minDistances[ i ] ) )
                    {
                        currentLOD = i;
                        onLODChanged( oldLOD, currentLOD, lodNames[ currentLOD ] );
                        return;
                    }
                }
                
                currentLOD = -1;
            }
            else if ( dist > maxDistances[ oldLOD ] )
            {
                for ( int i = oldLOD; i < getNumLODs(); i++ )
                {
                    if ( ( dist <= maxDistances[ i ] ) && ( dist >= minDistances[ i ] ) )
                    {
                        currentLOD = i;
                        onLODChanged( oldLOD, currentLOD, lodNames[ currentLOD ] );
                        return;
                    }
                }
                
                currentLOD = -1;
            }
        }
        else
        {
            currentLOD = -1;
            
            for ( int i = 0; i < getNumLODs(); i++ )
            {
                if ( ( dist <= maxDistances[ i ] ) && ( dist >= minDistances[ i ] ) )
                {
                    currentLOD = i;
                    break;
                }
            }
            
            if ( currentLOD != -1 )
                onLODChanged( -1, currentLOD, lodNames[ currentLOD ] );
            
            isInitialized = true;
        }
    }
    
    /**
     * Adds a new LOD (level of detail).
     * 
     * @param name the name of the new LOD
     * @param minDist the minimum distance
     * @param maxDist the maximum distance
     */
    protected int addLOD( String name, float minDist, float maxDist )
    {
        if ( minDist > maxDist )
            throw new IllegalArgumentException( "minDist (" + minDist + ") is greater than maxDist (" + maxDist + ")" );
        
        int level = -1;
        
        if ( getNumLODs() == 0 )
        {
            level = 0;
            
            minDistances[ level ] = minDist;
            maxDistances[ level ] = maxDist;
            
            currentLOD = level;
            
            lodNames = ArrayUtils.ensureCapacity( lodNames, String.class, 1 );
            lodNames[ level ] = name;
        }
        else
        {
            for ( int i = 0; i < getNumLODs(); i++ )
            {
                if ( minDistances[ i ] > minDist )
                {
                    level = i;
                }
                else if ( maxDistances[ i ] < maxDist )
                {
                    level = i + 1;
                }
            }
            
            if ( level < 0 )
                throw new IllegalArgumentException( "Don't know where to insert this item." );
            
            if ( ( level > 0 ) && ( minDistances[ level - 1 ] > minDist ) )
                throw new IllegalArgumentException( "minDist MUST NOT overlap two items." );
            if ( ( level < getNumLODs() - 1 ) && ( maxDistances[ level + 1 ] < maxDist ) )
                throw new IllegalArgumentException( "maxDist MUST NOT overlap two items." );
            
            minDistances = ArrayUtils.ensureCapacity( minDistances, getNumLODs() + 1 );
            maxDistances = ArrayUtils.ensureCapacity( maxDistances, getNumLODs() + 1 );
            lodNames = ArrayUtils.ensureCapacity( lodNames, String.class, getNumLODs() + 1 );
            
            if ( level < getNumLODs() )
            {
                System.arraycopy( minDistances, level, minDistances, level + 1, getNumLODs() - level );
                System.arraycopy( maxDistances, level, maxDistances, level + 1, getNumLODs() - level );
                System.arraycopy( lodNames, level, lodNames, level + 1, getNumLODs() - level );
            }
            
            minDistances[ level ] = minDist;
            maxDistances[ level ] = maxDist;
            lodNames[ level ] = name;
        }
        
        numLODs++;
        
        return ( level );
    }
    
    /**
     * Adds a new (unnamed) LOD (level of detail).
     * 
     * @param minDist the minimum distance
     * @param maxDist the maximum distance
     */
    protected final int addLOD( float minDist, float maxDist )
    {
        return ( addLOD( (String)null, minDist, maxDist ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract AbstractLODShape3D newInstance();
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void copy( Shape3D dst )
    {
        super.copy( dst );
        
        AbstractLODShape3D newShape = (AbstractLODShape3D)dst;
        
        if ( newShape.lodNames.length != this.lodNames.length )
            newShape.lodNames = new String[ this.lodNames.length ];
        System.arraycopy( this.lodNames, 0, newShape.lodNames, 0, this.lodNames.length );
        
        if ( newShape.minDistances.length != this.minDistances.length )
            newShape.minDistances = new float[ this.minDistances.length ];
        System.arraycopy( this.minDistances, 0, newShape.minDistances, 0, this.minDistances.length );
        
        if ( newShape.maxDistances.length != this.maxDistances.length )
            newShape.maxDistances = new float[ this.maxDistances.length ];
        System.arraycopy( this.maxDistances, 0, newShape.maxDistances, 0, this.maxDistances.length );
        
        newShape.numLODs = this.numLODs;
        newShape.currentLOD = this.currentLOD;
        newShape.isInitialized = this.isInitialized;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractLODShape3D sharedCopy( CopyListener listener )
    {
        return ( (AbstractLODShape3D)super.sharedCopy( listener ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractLODShape3D sharedCopy()
    {
        return ( (AbstractLODShape3D)super.sharedCopy() );
    }
    
    /**
     * Constructs a new Shape3D object with specified geometry and
     * appearance components.
     */
    public AbstractLODShape3D( Geometry geometry, Appearance appearance )
    {
        super( geometry, appearance );
        
        this.numLODs = 0;
        this.currentLOD = -1;
    }
    
    /**
     * Constructs a new LODShape3D object with specified geometry component
     * and a null appearance component.
     */
    public AbstractLODShape3D( Geometry geometry )
    {
        this( geometry, (Appearance)null );
    }
    
    /**
     * Constructs a new LODShape3D object with a null geometry component
     * and a null appearance component.
     */
    public AbstractLODShape3D()
    {
        this( (Geometry)null, (Appearance)null );
    }
}
