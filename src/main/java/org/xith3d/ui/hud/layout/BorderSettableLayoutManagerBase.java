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

/**
 * An abstract base class for all {@link BorderSettableLayoutManager} {@link LayoutManager}s.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class BorderSettableLayoutManagerBase extends LayoutManagerBase implements BorderSettableLayoutManager
{
    private float borderBottom;
    private float borderRight;
    private float borderTop;
    private float borderLeft;
    
    /**
     * {@inheritDoc}
     */
    public void setBorderBottom( float border )
    {
        this.borderBottom = border;
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getBorderBottom()
    {
        return ( borderBottom );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBorderRight( float border )
    {
        this.borderRight = border;
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getBorderRight()
    {
        return ( borderRight );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBorderTop( float border )
    {
        this.borderTop = border;
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getBorderTop()
    {
        return ( borderTop );
    }
    
    /**
     * {@inheritDoc}
     */
    public void setBorderLeft( float border )
    {
        this.borderLeft = border;
    }
    
    /**
     * {@inheritDoc}
     */
    public final float getBorderLeft()
    {
        return ( borderLeft );
    }
    
    public BorderSettableLayoutManagerBase( float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        super();
        
        if ( ( borderBottom < 0f ) || ( borderRight < 0f ) || ( borderTop < 0f ) || ( borderLeft < 0f ) )
            throw new IllegalArgumentException( "all borders must be >= 0" );
        
        this.borderBottom = borderBottom;
        this.borderRight = borderRight;
        this.borderTop = borderTop;
        this.borderLeft = borderLeft;
    }
    
    public BorderSettableLayoutManagerBase()
    {
        this( 0f, 0f, 0f, 0f );
    }
}
