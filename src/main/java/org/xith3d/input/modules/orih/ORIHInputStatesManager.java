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
package org.xith3d.input.modules.orih;

import org.jagatoo.input.managers.InputStatesManager;
import org.xith3d.input.ObjectRotationInputHandler;

/**
 * This is a special key-states manager for the {@link ObjectRotationInputHandler}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class ORIHInputStatesManager extends InputStatesManager
{
    @SuppressWarnings("unused")
    private final ObjectRotationInputHandler orih;
    
    public final boolean isRotatingLeft()
    {
        return ( getSimpleInputState( ORIHInputAction.ROTATE_LEFT ) > 0 );
    }
    
    public final boolean isRotatingRight()
    {
        return ( getSimpleInputState( ORIHInputAction.ROTATE_RIGHT ) > 0 );
    }
    
    public final boolean isRotatingUp()
    {
        return ( getSimpleInputState( ORIHInputAction.ROTATE_UP ) > 0 );
    }
    
    public final boolean isRotatingDown()
    {
        return ( getSimpleInputState( ORIHInputAction.ROTATE_DOWN ) > 0 );
    }
    
    /**
     * @return true, if the player is currently moving into any direction
     */
    public final boolean isRotating()
    {
        return ( isRotatingLeft() || isRotatingRight() || isRotatingUp() || isRotatingDown() );
    }
    
    public final boolean isZoomingIn()
    {
        return ( getSimpleInputState( ORIHInputAction.ZOOM_IN ) > 0 );
    }
    
    public final boolean isZoomingOut()
    {
        return ( getSimpleInputState( ORIHInputAction.ZOOM_OUT ) > 0 );
    }
    
    /**
     * @return true, if view is currently zooming
     */
    public final boolean isZooming()
    {
        return ( isZoomingIn() || isZoomingOut() );
    }
    
    @Override
    public void update( long nanoTime )
    {
        super.update( nanoTime );
    }
    
    public ORIHInputStatesManager( ObjectRotationInputHandler orih )
    {
        super( orih.getBindingsManager() );
        
        this.orih = orih;
    }
}
