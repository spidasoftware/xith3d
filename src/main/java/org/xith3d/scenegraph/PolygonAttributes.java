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

import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * PolygonAttributes defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class PolygonAttributes extends NodeComponent implements StateTrackable< PolygonAttributes >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * @see FaceCullMode#NONE
     */
    public static final FaceCullMode CULL_NONE = FaceCullMode.NONE;
    
    /**
     * @see FaceCullMode#FRONT
     */
    public static final FaceCullMode CULL_FRONT = FaceCullMode.FRONT;
    
    /**
     * @see FaceCullMode#BACK
     */
    public static final FaceCullMode CULL_BACK = FaceCullMode.BACK;
    
    /**
     * @see FaceCullMode#SWITCH
     */
    public static final FaceCullMode CULL_SWITCH = FaceCullMode.SWITCH;
    
    /**
     * @see DrawMode#POINT
     */
    public static final DrawMode POLYGON_POINT = DrawMode.POINT;
    
    /**
     * @see DrawMode#LINE
     */
    public static final DrawMode POLYGON_LINE = DrawMode.LINE;
    
    /**
     * @see DrawMode#FILL
     */
    public static final DrawMode POLYGON_FILL = DrawMode.FILL;
    
    /**
     * The desired face culling mode.
     */
    private FaceCullMode cullFace = FaceCullMode.NONE;
    
    /**
     * The desired polygon mode.
     */
    private DrawMode drawMode = DrawMode.FILL;
    
    /**
     * The desired polygon offset.
     */
    private float polygonOffset = 0f;
    
    private float polygonOffsetFactor = 0f;
    
    private boolean backFaceNormalFlip = false;
    
    /**
     * Antialiasing enable/disable.
     */
    private boolean antialiasing = false;
    
    private boolean sortEnabled = true;
    
    /**
     * Sets the polygon draw mode.
     * 
     * @param drawMode
     */
    public void setDrawMode( DrawMode drawMode )
    {
        this.drawMode = drawMode;
        setChanged( true );
    }
    
    /**
     * @return the polygon draw mode.
     */
    public final DrawMode getDrawMode()
    {
        return ( drawMode );
    }
    
    /**
     * Sets the face culling mode. The default mode is CULL_NONE.
     */
    public final void setFaceCullMode( FaceCullMode mode )
    {
        this.cullFace = mode;
        setChanged( true );
    }
    
    /**
     * Gets the face culling mode.
     */
    public final FaceCullMode getFaceCullMode()
    {
        return ( cullFace );
    }
    
    /**
     * Sets the polygon offset.
     */
    public final void setPolygonOffset( float polygonOffset )
    {
        this.polygonOffset = polygonOffset;
        setChanged( true );
    }
    
    /**
     * Gets the polygon offset.
     */
    public final float getPolygonOffset()
    {
        return ( polygonOffset );
    }
    
    /**
     * Set antialiasing .
     */
    public final void setPolygonAntialiasingEnabled( boolean state )
    {
        this.antialiasing = state;
        setChanged( true );
    }
    
    /**
     * Get antialiasing.
     */
    public final boolean isPolygonAntialiasingEnabled()
    {
        return ( antialiasing );
    }
    
    /**
     * Enables or disables transaprency sorting for this shape. Line can be
     * classified as transparent if it has antialiasing enabled.
     * <p>
     * Transparency attributes can be marked to disable sorting transparent
     * shapes by calling of setSortEnabled(false). When this is done,
     * transparent shape it will not be drawn during the transparent rendering
     * pass, but will be drawn with the solids (in the opaque rendering pass),
     * i.e. this transparent shape will be treated just like regular opaque
     * shape.
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
    
    public void setBackFaceNormalFlip( boolean backFaceNormalFlip )
    {
        this.backFaceNormalFlip = backFaceNormalFlip;
        setChanged( true );
    }
    
    public final boolean getBackFaceNormalFlip()
    {
        return ( this.backFaceNormalFlip );
    }
    
    public void setPolygonOffsetFactor( float polygonOffsetFactor )
    {
        this.polygonOffsetFactor = polygonOffsetFactor;
        setChanged( true );
    }
    
    public final float getPolygonOffsetFactor()
    {
        return ( polygonOffsetFactor );
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
    
    public PolygonAttributes getCopy()
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
        if ( !( o instanceof PolygonAttributes ) )
            return ( false );
        PolygonAttributes ro = (PolygonAttributes)o;
        if ( this.drawMode != ro.drawMode )
            return ( false );
        if ( this.cullFace != ro.cullFace )
            return ( false );
        if ( this.polygonOffset != ro.polygonOffset )
            return ( false );
        if ( this.polygonOffsetFactor != ro.polygonOffsetFactor )
            return ( false );
        if ( this.backFaceNormalFlip != ro.backFaceNormalFlip )
            return ( false );
        if ( this.antialiasing != ro.antialiasing )
            return ( false );
        if ( this.sortEnabled != ro.sortEnabled )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( PolygonAttributes o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.drawMode.ordinal() < o.drawMode.ordinal() )
            return ( -1 );
        else if ( this.drawMode.ordinal() > o.drawMode.ordinal() )
            return ( 1 );
        
        if ( this.polygonOffset < o.polygonOffset )
            return ( -1 );
        else if ( this.polygonOffset > o.polygonOffset )
            return ( 1 );
        
        if ( this.polygonOffsetFactor < o.polygonOffsetFactor )
            return ( -1 );
        else if ( this.polygonOffsetFactor > o.polygonOffsetFactor )
            return ( 1 );
        
        if ( this.cullFace.ordinal() < o.cullFace.ordinal() )
            return ( -1 );
        else if ( this.cullFace.ordinal() > o.cullFace.ordinal() )
            return ( 1 );
        
        int val = ComparatorHelper.compareBoolean( this.antialiasing, o.antialiasing );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareBoolean( this.backFaceNormalFlip, o.backFaceNormalFlip );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareBoolean( this.sortEnabled, o.sortEnabled );
        if ( val != 0 )
            return ( val );
        
        return ( 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        PolygonAttributes o = (PolygonAttributes)original;
        setFaceCullMode( o.getFaceCullMode() );
        setDrawMode( o.getDrawMode() );
        setPolygonOffset( o.getPolygonOffset() );
        setPolygonOffsetFactor( o.getPolygonOffsetFactor() );
        setBackFaceNormalFlip( o.getBackFaceNormalFlip() );
        setPolygonAntialiasingEnabled( o.isPolygonAntialiasingEnabled() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PolygonAttributes cloneNodeComponent( boolean forceDuplicate )
    {
        PolygonAttributes pa = new PolygonAttributes();
        
        pa.duplicateNodeComponent( this, forceDuplicate );
        
        return ( pa );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /**
     * Constructs a new PolygonAttributes object.
     */
    public PolygonAttributes()
    {
        super( false );
    }
    
    /**
     * Constructs a new PolygonAttributes object.
     */
    public PolygonAttributes( DrawMode drawMode, FaceCullMode cullFace, float polygonOffset )
    {
        this();
        
        this.cullFace = cullFace;
        this.drawMode = drawMode;
        this.polygonOffset = polygonOffset;
    }
    
    /**
     * Constructs a new PolygonAttributes object.
     */
    public PolygonAttributes( DrawMode drawMode, FaceCullMode cullFace )
    {
        this( drawMode, cullFace, 0f );
    }
    
    /**
     * Constructs a new PolygonAttributes object.
     */
    public PolygonAttributes( DrawMode drawMode )
    {
        this( drawMode, FaceCullMode.NONE, 0f );
    }
    
    /**
     * Constructs a new PolygonAttributes object.
     */
    public PolygonAttributes( FaceCullMode cullFace )
    {
        this( DrawMode.FILL, cullFace, 0f );
    }
    
    /**
     * Constructs a new PolygonAttributes object.
     */
    public PolygonAttributes( DrawMode drawMode, FaceCullMode cullFace, float polygonOffset, float polygonOffsetFactor )
    {
        this();
        
        this.polygonOffset = polygonOffset;
        this.polygonOffsetFactor = polygonOffsetFactor;
        this.cullFace = cullFace;
        this.drawMode = drawMode;
    }
    
    /**
     * Constructs a new PolygonAttributes object.
     */
    public PolygonAttributes( DrawMode drawMode, FaceCullMode cullFace, float polygonOffset, float polygonOffsetFactor, boolean backFaceNormalFlip, boolean antialiasing, boolean sortEnabled )
    {
        this();
        
        this.polygonOffset = polygonOffset;
        this.polygonOffsetFactor = polygonOffsetFactor;
        this.cullFace = cullFace;
        this.drawMode = drawMode;
        this.backFaceNormalFlip = backFaceNormalFlip;
        this.antialiasing = antialiasing;
        this.sortEnabled = sortEnabled;
    }
}
