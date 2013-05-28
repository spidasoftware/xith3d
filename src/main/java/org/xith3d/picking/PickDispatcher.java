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
package org.xith3d.picking;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.xith3d.utility.logging.X3DLog;

/**
 * This class acts as a proxy for pick events
 * and can dispatch the pick events to multiple listeners.
 * 
 * @author Mathias Henze (aka cylab)
 * 
 * @since 1.0
 */
public class PickDispatcher implements PickListener
{
    private final LinkedList< PickListener > listeners = new LinkedList< PickListener >();
    private final ArrayList< PickListener > copy = new ArrayList< PickListener >( 8 );
    
    public void addPickListener( PickListener listener )
    {
        listeners.add( listener );
    }
    
    public void removePickListener( PickListener listener )
    {
        listeners.remove( listener );
    }
    
    /**
     * {@inheritDoc}
     */
    public void onObjectPicked( PickResult nearest, Object userObject, long pickTime )
    {
        copy.addAll( listeners );
        
        for ( int i = 0; i < copy.size(); i++ )
        {
            PickListener pickListener = copy.get( i );
            
            try
            {
                pickListener.onObjectPicked( nearest, userObject, pickTime );
            }
            catch ( Throwable exception )
            {
                X3DLog.println( exception );
            }
        }
        
        copy.clear();
    }

    /**
     * {@inheritDoc}
     */
    public void onObjectsPicked( List< PickResult > pickResults, Object userObject, long pickTime )
    {
        copy.addAll( listeners );
        
        for ( int i = 0; i < copy.size(); i++ )
        {
            PickListener pickListener = copy.get( i );
            
            try
            {
                pickListener.onObjectsPicked( pickResults, userObject, pickTime );
            }
            catch( Throwable exception )
            {
                X3DLog.println( exception );
            }
        }
        
        copy.clear();
    }
    
    /**
     * {@inheritDoc}
     */
    public void onPickingMissed( Object userObject, long pickTime )
    {
        copy.addAll( listeners );
        
        for ( int i = 0; i < copy.size(); i++ )
        {
            PickListener pickListener = copy.get( i );
            
            try
            {
                pickListener.onPickingMissed( userObject, pickTime );
            }
            catch ( Throwable exception )
            {
                X3DLog.println( exception );
            }
        }
        
        copy.clear();
    }
}
