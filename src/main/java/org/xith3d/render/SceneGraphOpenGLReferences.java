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
import java.util.Vector;

/**
 * This class manages {@link SceneGraphOpenGLReference}es per OpenGL context.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class SceneGraphOpenGLReferences
{
    public static interface Provider
    {
        public SceneGraphOpenGLReference newReference( CanvasPeer canvasPeer, SceneGraphOpenGLReferences references, int numNamesPerContext );
    }
    
    private static final Vector<SceneGraphOpenGLReferences> instances = new Vector<SceneGraphOpenGLReferences>();
    
    //private CanvasPeer[] contexts = new CanvasPeer[ 0 ];
    private SceneGraphOpenGLReference[] references = new SceneGraphOpenGLReference[ 0 ];
    
    private final int numNamesPerContext;
    
    private final boolean ensureCanvasID( int canvasID )
    {
        if ( references.length >= canvasID )
            return ( true );
        
        final SceneGraphOpenGLReference[] newReferences = new SceneGraphOpenGLReference[ canvasID ];
        System.arraycopy( references, 0, newReferences, 0, references.length );
        Arrays.fill( newReferences, references.length, newReferences.length, null );
        references = newReferences;
        
        /*
        final CanvasPeer[] newContexts = new CanvasPeer[ canvasID ];
        System.arraycopy( contexts, 0, newContexts, 0, contexts.length );
        Arrays.fill( newContexts, contexts.length, newContexts.length, null );
        contexts = newContexts;
        */
        
        return ( false );
    }
    
    /*
    public final SceneGraphOpenGLReference getReference( int canvasID, Provider provider )
    {
        final int canvasIDm1 = canvasID - 1;
        
        if ( ( !ensureCanvasID( canvasID ) ) || ( references[ canvasIDm1 ] == null ) )
        {
            references[ canvasIDm1 ] = provider.newReference( this, numNamesPerContext );
        }
        
        return ( references[ canvasIDm1 ] );
    }
    */
    
    public final SceneGraphOpenGLReference getReference( CanvasPeer canvasPeer, Provider provider )
    {
        int canvasID = canvasPeer.getCanvasID();
        final int canvasIDm1 = canvasID - 1;
        
        if ( references.length < canvasID )
        {
            ensureCanvasID( canvasID );
            
            references[ canvasIDm1 ] = provider.newReference( canvasPeer, this, numNamesPerContext );
            //contexts[ canvasIDm1 ] = canvasPeer;
        }
        else if ( references[ canvasIDm1 ] == null )
        {
            references[ canvasIDm1 ] = provider.newReference( canvasPeer, this, numNamesPerContext );
            //contexts[ canvasIDm1 ] = canvasPeer;
        }
        /*
        else if ( contexts[ canvasIDm1 ] != canvasPeer )
        {
            references[ canvasIDm1 ].destroyObject();
            
            references[ canvasIDm1 ] = provider.newReference( canvasPeer, this, numNamesPerContext );
            //contexts[ canvasIDm1 ] = canvasPeer;
        }
        */
        
        return ( references[ canvasIDm1 ] );
    }
    
    public SceneGraphOpenGLReference removeReference( int canvasID )
    {
        if ( references.length < canvasID )
            //throw new Error( "No reference available" ) );
            return ( null );
        
        final int canvasIDm1 = canvasID - 1;
        
        final SceneGraphOpenGLReference result = references[ canvasIDm1 ];
        
        //references[ canvasIDm1 ].destroy();
        references[ canvasIDm1 ] = null;
        //contexts[ canvasIDm1 ] = null;
        
        return ( result );
    }
    
    public final boolean referenceExists( CanvasPeer canvasPeer )
    {
        final int canvasIDm1 = canvasPeer.getCanvasID() - 1;
        
        if ( canvasIDm1 >= references.length )
            return ( false );
        
        if ( references[ canvasIDm1 ] != null )
        {
            /*
            if ( contexts[ canvasIDm1 ] != canvasPeer )
            {
                //references[ canvasIDm1 ].prepareObjectForDestroy();
                references[ canvasIDm1 ] = null;
                contexts[ canvasIDm1 ] = null;
                
                return ( false );
            }
            */
            
            return ( true );
        }
        
        return ( false );
    }
    
    public final int getNumReferences()
    {
        return ( references.length );
    }
    
    public final int getNumExistingNames()
    {
        int num = 0;
        for ( int i = 0; i < references.length; i++ )
        {
            if ( references[ i ] != null )
                num += references[ i ].getNumNames( true );
        }
        
        return ( num );
    }
    
    public void set( SceneGraphOpenGLReferences other )
    {
        this.ensureCanvasID( other.references.length );
        Arrays.fill( this.references, other.references.length + 1, this.references.length, null );
        //Arrays.fill( this.contexts, other.contexts.length + 1, this.contexts.length, null );
        
        System.arraycopy( other.references, 0, this.references, 0, other.references.length );
        //System.arraycopy( other.contexts, 0, this.contexts, 0, other.contexts.length );
    }
    
    public final void prepareObjectForDestroy( CanvasPeer canvasPeer )
    {
        if ( references.length == 0 )
            return;
        
        if ( canvasPeer == null )
            return;
        
        final SceneGraphOpenGLReference ref = references[ canvasPeer.getCanvasID() - 1 ];
        
        if ( ( ref != null )/* && ( contexts[ canvasPeer.getCanvasID() - 1 ] == canvasPeer )*/ )
            ref.prepareObjectForDestroy();
    }
    
    public final void prepareObjectForDestroy()
    {
        for ( int i = 0; i < references.length; i++ )
        {
            if ( references[ i ] != null )
            {
                references[ i ].prepareObjectForDestroy();
            }
        }

        // we can remove the references container now, since this references object does not contain any gl references anymore
        synchronized ( instances )
        {
            instances.remove( this );
        }
    }
    
    public final void invalidateNames()
    {
        for ( int i = 0; i < references.length; i++ )
        {
            if ( references[ i ] != null )
            {
                final int numNames = references[ i ].getNumNames( false );
                for ( int j = 0; j < numNames; j++ )
                {
                    references[ i ].invalidateName( j );
                }
            }
        }
    }
    
    public static void destroyObjects( CanvasPeer context )
    {
        synchronized ( instances )
        {
            for ( int i = instances.size() - 1; i >= 0; i-- )
            {
                SceneGraphOpenGLReferences object = instances.get( i );
                
                for ( int j = 0; j < object.references.length; j++ )
                {
                    if ( ( object.references[ j ] != null ) && ( object.references[ j ].getContext() == context ) )
                    {
                        object.references[ j ].destroyObject();
                        object.references[ j ] = null;
                        //object.contexts[ j ] = null;
                    }
                }
            }
        }
    }
    
    public SceneGraphOpenGLReferences( int numNamesPerContext )
    {
        this.numNamesPerContext = numNamesPerContext;
        
        synchronized ( instances )
        {
            instances.add( this );
        }
    }
}
