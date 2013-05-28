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
package org.xith3d.ui.swingui;

import java.awt.Rectangle;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.RepaintManager;

/**
 * Insert package comments here
 * <p>
 * Originally Coded by David Yazel on Oct 4, 2003 at 2:35:40 PM.
 */
public class UIRepaintManager extends RepaintManager
{
    private HashMap< JComponent, UIDirtyRegion > map = new HashMap< JComponent, UIDirtyRegion >();
    
    @Override
    public void markCompletelyDirty( JComponent aComponent )
    {
        super.markCompletelyDirty( aComponent );
        UIDirtyRegion dr = map.get( aComponent );
        if ( dr == null )
        {
            dr = new UIDirtyRegion( aComponent, aComponent.getBounds(), null );
            map.put( aComponent, dr );
        }
        else
        {
            dr.setCompDirty( aComponent.getBounds() );
        }
    }
    
    @Override
    public void paintDirtyRegions()
    {
        super.paintDirtyRegions();
    }
    
    public void removeUIDirtyRegion( JComponent c )
    {
        map.remove( c );
        super.markCompletelyClean( c );
    }
    
    public boolean isDirty()
    {
        return ( map.size() > 0 );
    }
    
    public void clear()
    {
        map.clear();
    }
    
    public UIDirtyRegion getUIDirtyRegion( JComponent c )
    {
        UIDirtyRegion dr = map.get( c );
        return ( dr );
    }
    
    @Override
    public void addDirtyRegion( JComponent aComponent, int x, int y, int w, int h )
    {
        super.addDirtyRegion( aComponent, x, y, w, h );
        
        UIDirtyRegion dr = map.get( aComponent );
        if ( dr == null )
        {
            dr = new UIDirtyRegion( aComponent, new Rectangle( x, y, w, h ), null );
            map.put( aComponent, dr );
        }
        else
        {
            dr.setCompDirty( dr.getCompDirty().union( new Rectangle( x, y, w, h ) ) );
        }
        //System.out.println( "New dirty " + aComponent.getClass().getName() + " = " + dr.getCompDirty() );
    }
}
