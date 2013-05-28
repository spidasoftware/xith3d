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

import org.xith3d.render.CanvasPeer;
import org.xith3d.render.SceneGraphOpenGLReferences;

/**
 * An implementation of the float data holder which uses a direct
 * NIO buffer.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GeomNioIntData extends org.jagatoo.geometry.GeomNioIntData
{
    private boolean dirty = true;
    private int framesSinceDirty = 0;
    
    private final SceneGraphOpenGLReferences openGLReferences = new SceneGraphOpenGLReferences( 1 );
    
    public final SceneGraphOpenGLReferences getOpenGLReferences()
    {
        return ( openGLReferences );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setDirty( boolean dirty )
    {
        final boolean wasDirty = this.dirty;
        
        this.dirty = dirty;
        
        if ( dirty && !wasDirty )
            framesSinceDirty = 0;
    }
    
    /**
     * Used by the renderer to know if cached geometry is now invalid. This is
     * used even within a single run when binding vertex arrays
     */
    final boolean isDirty()
    {
        return ( dirty );
    }
    
    final void incrementFramesSinceDirty()
    {
        framesSinceDirty++;
    }
    
    final int getFramesSinceDirty()
    {
        return ( framesSinceDirty );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void finalize()
    {
        openGLReferences.prepareObjectForDestroy();
    }
    
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( openGLReferences.referenceExists( canvasPeer ) )
            openGLReferences.prepareObjectForDestroy( canvasPeer );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected GeomNioIntData newInstance( int maxElems, int elemSize, int stride, boolean reversed )
    {
        return ( new GeomNioIntData( maxElems, elemSize, stride, reversed ) );
    }
    
    public GeomNioIntData( int maxElems, int elemSize, int stride, boolean reversed )
    {
        super( maxElems, elemSize, stride, reversed );
    }
    
    public GeomNioIntData( int maxElems, int elemSize, boolean reversed )
    {
        this( maxElems, elemSize, 0, reversed );
    }
}
