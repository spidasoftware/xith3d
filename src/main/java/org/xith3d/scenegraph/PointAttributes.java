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
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * PointAttributes defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class PointAttributes extends NodeComponent implements StateTrackable< PointAttributes >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * The desired point size.
     */
    private float size = 1;
    
    /**
     * Antialiasing .
     */
    private boolean antialiasing = false;
    
    /**
     * Set the point size.
     */
    public final void setPointSize( float pointSize )
    {
        this.size = pointSize;
        setChanged( true );
    }
    
    /**
     * Get the point size.
     */
    public final float getPointSize()
    {
        return ( size );
    }
    
    /**
     * Set antialiasing .
     */
    public final void setPointAntialiasingEnabled( boolean state )
    {
        this.antialiasing = state;
        setChanged( true );
    }
    
    /**
     * Get antialiasing.
     */
    public final boolean isPointAntialiasingEnabled()
    {
        return ( antialiasing );
    }
    
    private boolean sortEnabled = true;
    
    /**
     * Enables or disables transaprency sorting for this shape. Point can be classified as transparent if
     * it has antialiasing enabled.
     * <p>
     * Transparency attributes can be marked to disable sorting transparent shapes by calling of
     * setSortEnabled(false).  When this is done, transparent shape it will not be 
     * drawn during the transparent rendering pass, but will be drawn with the solids (in the opaque rendering pass),
     * i.e. this transparent shape will be treated just like regular opaque shape.
     * <p>
     * By default, sorting is enabled.
     *
     * @see org.xith3d.scenegraph.OrderedGroup
     * @see org.xith3d.scenegraph.TransparencyAttributes#setSortEnabled(boolean)
     */
    public void setSortEnabled( boolean sortEnabled )
    {
        this.sortEnabled = sortEnabled;
        setChanged( true );
    }
    
    public final boolean isSortEnabled()
    {
        return ( sortEnabled );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        PointAttributes o = (PointAttributes)original;
        setPointSize( o.getPointSize() );
        setPointAntialiasingEnabled( o.isPointAntialiasingEnabled() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PointAttributes cloneNodeComponent( boolean forceDuplicate )
    {
        PointAttributes pa = new PointAttributes();
        
        pa.duplicateNodeComponent( this, forceDuplicate );
        
        return ( pa );
    }
    
    // ////////////////////////////////////////////////////////////////
    // ///////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    // ////////////////////////////////////////////////////////////////
    
    public final void setStateNode( StateNode node )
    {
        this.stateNode = node;
        this.stateId = node.getId();
    }
    
    public final StateNode getStateNode()
    {
        return ( stateNode );
    }
    
    public final long getStateId()
    {
        return ( stateId );
    }
    
    public PointAttributes getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof PointAttributes ) )
            return ( false );
        PointAttributes po = (PointAttributes)o;
        if ( this.size != po.size )
            return ( false );
        if ( this.antialiasing != po.antialiasing )
            return ( false );
        if ( this.sortEnabled != po.sortEnabled )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( PointAttributes o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.size < o.size )
            return ( -1 );
        
        if ( this.size > o.size )
            return ( 1 );
        
        int val = ComparatorHelper.compareBoolean( this.antialiasing, o.antialiasing );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareBoolean( this.sortEnabled, o.sortEnabled );
        
        return ( val );
    }
    
    /**
     * Constructs a new PointAttributes object.
     */
    public PointAttributes()
    {
        super( false );
    }
    
    /**
     * Constructs a new PointAttributes object with the specified color.
     */
    public PointAttributes( float pointSize, boolean antialiasing )
    {
        this();
        
        this.size = pointSize;
        this.antialiasing = antialiasing;
    }
}
