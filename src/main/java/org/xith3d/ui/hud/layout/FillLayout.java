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
package org.xith3d.ui.hud.layout;

import java.util.ArrayList;

import org.xith3d.ui.hud.base.Widget;

/**
 * The FillLayout simply streches the first Widget over the whole container
 * and ignores the others.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FillLayout extends BorderSettableLayoutManagerBase
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLayout( final float left0, final float top0, final float containerResX, final float containerResY )
    {
        final ArrayList< Widget > widgets = getWidgets();
        
        // find the first visible Widget and stretch it (and only it).
        for ( int i = 0; i < widgets.size(); i++ )
        {
            final Widget widget = widgets.get( i );
            
            if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                continue;
            
            widget.setLocation( left0, top0 );
            widget.setSize( containerResX, containerResY );
            
            break;
        }
    }
    
    public FillLayout( float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        super( borderBottom, borderRight, borderTop, borderLeft );
    }
    
    public FillLayout( float border )
    {
        this( border, border, border, border );
    }
    
    public FillLayout()
    {
        this( 0f );
    }
}
