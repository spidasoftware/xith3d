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
package org.xith3d.render;

import java.util.Arrays;

/**
 * This class manages the reference of SceneGraph objects to OpenGL names.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SceneGraphOpenGLReference
{
    public static final int UNSET_NAME = -1;
    
    private final CanvasPeer context;
    
    private final SceneGraphOpenGLReferences references;
    private final int[] names;
    
    private final boolean[] namesValid;
    
    public final CanvasPeer getContext()
    {
        return ( context );
    }
    
    public final SceneGraphOpenGLReferences getReferences()
    {
        return ( references );
    }
    
    /*
    private final void ensureIndex( int index )
    {
        if ( names == null )
        {
            names = new int[ index + 1 ];
            Arrays.fill( names, UNSET_NAME );
            return;
        }
        
        if ( names.length > index )
            return;
        
        final int[] newNames = new int[ index + 1 ];
        System.arraycopy( names, 0, newNames, 0, names.length );
        Arrays.fill( newNames, names.length, newNames.length, UNSET_NAME );
        names = newNames;
    }
    */
    
    /**
     * Sets the OpenGL name with the specified index.
     * 
     * @param index
     * @param name
     */
    public final void setName( int index, int name )
    {
        //ensureIndex( index );
        
        names[ index ] = name;
        namesValid[ index ] = ( name != UNSET_NAME );
    }
    
    /**
     * Sets the OpenGL name with the specified index.
     * 
     * @param index
     */
    public final int deleteName( int index )
    {
        //ensureIndex( index );
        
        names[ index ] = UNSET_NAME;
        namesValid[ index ] = false;
        
        return ( names[ index ] );
    }
    
    /**
     * Sets the (first) OpenGL name.
     * 
     * @param name
     */
    public final void setName( int name )
    {
        //ensureIndex( 0 );
        
        names[ 0 ] = name;
        namesValid[ 0 ] = ( name != UNSET_NAME );
    }
    
    /**
     * Sets the (first) OpenGL name.
     */
    public final int deleteName()
    {
        //ensureIndex( 0 );
        
        names[ 0 ] = UNSET_NAME;
        namesValid[ 0 ] = false;
        
        return ( names[ 0 ] );
    }
    
    /**
     * @return the OpenGL name with the specified index.
     * 
     * @param index
     */
    public final int getName( int index )
    {
        //if ( ( names == null ) || ( names.length <= index ) )
        //    return ( UNSET_NAME );
        
        return ( names[ index ] );
    }
    
    /**
     * @return true, if the (first) OpenGL name exists.
     * 
     * @param index
     */
    public final boolean nameExists( int index )
    {
        //if ( ( names == null ) || ( names.length <= index ) )
        //    return ( false );
        
        return ( ( names.length > index ) && ( names[ index ] != UNSET_NAME ) );
    }
    
    /**
     * @return the (first) OpenGL name.
     */
    public final int getName()
    {
        //if ( ( names == null ) || ( names.length <= 0 ) )
        //if ( names == null )
        //    return ( UNSET_NAME );
        
        return ( names[ 0 ] );
    }
    
    /**
     * @return true, if the (first) OpenGL name exists.
     */
    public final boolean nameExists()
    {
        //if ( ( names == null ) || ( names.length <= 0 ) )
        //if ( names == null )
        //    return ( false );
        
        return ( names[ 0 ] != UNSET_NAME );
    }
    
    /**
     * Sets the OpenGL name with the specified index.
     * 
     * @param index
     * @param valid
     */
    public final void setNameValid( int index, boolean valid )
    {
        //ensureIndex( index );
        
        namesValid[ index ] = valid;
    }
    
    /**
     * Sets the OpenGL name with the specified index.
     * 
     * @param index
     */
    public final void invalidateName( int index )
    {
        //ensureIndex( index );
        
        namesValid[ index ] = false;
    }
    
    /**
     * Sets the (first) OpenGL name.
     * 
     * @param valid
     */
    public final void setNameValid( boolean valid )
    {
        //ensureIndex( 0 );
        
        namesValid[ 0 ] = valid;
    }
    
    /**
     * Sets the (first) OpenGL name.
     */
    public final void invalidateName()
    {
        //ensureIndex( 0 );
        
        namesValid[ 0 ] = false;
    }
    
    /**
     * @return the OpenGL name with the specified index.
     * 
     * @param index
     */
    public final boolean isNameValid( int index )
    {
        //if ( ( names == null ) || ( names.length <= index ) )
        //    return ( false );
        
        return ( namesValid[ index ] );
    }
    
    /**
     * @return the (first) OpenGL name.
     */
    public final boolean isNameValid()
    {
        //if ( ( names == null ) || ( names.length <= 0 ) )
        //if ( names == null )
        //    return ( false );
        
        return ( namesValid[ 0 ] );
    }
    
    public final int getNumNames( boolean onlyExisting )
    {
        if ( names == null )
            return ( 0 );
        
        if ( onlyExisting )
        {
            int num = 0;
            for ( int i = 0; i < names.length; i++ )
            {
                if ( names[ i ] != UNSET_NAME )
                    num++;
            }
            
            return ( num );
        }
        
        return ( names.length );
    }
    
    public abstract void prepareObjectForDestroy();
    
    /**
     * Destroys this Object in OpenGL.
     * 
     * @param canvasPeer
     * @param name
     */
    public abstract void destroyObject( int index, int name );
    
    /**
     * Destroys this Object in OpenGL.
     * 
     * @param canvasID
     */
    public final void destroyObject()
    {
        final int n = getNumNames( false );
        for ( int i = 0; i < n; i++ )
        {
            final int name = getName( i );
            
            if ( name != UNSET_NAME )
            {
                if ( !context.isDestroyed() )
                {
                    destroyObject( i, name );
                }
                
                deleteName( i );
            }
        }
    }
    
    /**
     * Creates a new SceneGraphOpenGLReference.
     * 
     * @param references
     * @param numNames
     */
    public SceneGraphOpenGLReference( CanvasPeer context, SceneGraphOpenGLReferences references, int numNames )
    {
        this.context = context;
        
        this.references = references;
        
        this.names = new int[ numNames ];
        Arrays.fill( names, UNSET_NAME );
        
        this.namesValid = new boolean[ numNames ];
        Arrays.fill( namesValid, false );
    }
}
