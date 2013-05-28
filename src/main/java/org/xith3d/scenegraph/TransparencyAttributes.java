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

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.opengl.enums.BlendFunction;
import org.jagatoo.opengl.enums.BlendMode;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * TransparencyAttributes defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class TransparencyAttributes extends NodeComponent implements Enableable, StateTrackable< TransparencyAttributes >
{
    /**
     * @see BlendMode#NONE
     */
    public static final BlendMode NONE = BlendMode.NONE;
    
    /**
     * @see BlendMode#BLENDED
     */
    public static final BlendMode BLENDED = BlendMode.BLENDED;
    
    /**
     * @see BlendMode#NICEST
     */
    public static final BlendMode NICEST = BlendMode.NICEST;
    
    /**
     * @see BlendMode#FASTEST
     */
    public static final BlendMode FASTEST = BlendMode.FASTEST;
    
    /**
     * @see BlendMode#SCREEN_DOOR
     */
    public static final BlendMode SCREEN_DOOR = BlendMode.SCREEN_DOOR;
    
    /**
     * @see BlendFunction#BLEND_ZERO
     */
    public static final BlendFunction BLEND_ZERO = BlendFunction.ZERO;
    
    /**
     * @see BlendFunction#BLEND_ONE
     */
    public static final BlendFunction BLEND_ONE = BlendFunction.ONE;
    
    /**
     * @see BlendFunction#BLEND_SRC_ALPHA
     */
    public static final BlendFunction BLEND_SRC_ALPHA = BlendFunction.SRC_ALPHA;
    
    /**
     * @see BlendFunction#BLEND_ONE_MINUS_SRC_ALPHA
     */
    public static final BlendFunction BLEND_ONE_MINUS_SRC_ALPHA = BlendFunction.ONE_MINUS_SRC_ALPHA;
    
    private BlendFunction srcBlendFunction = BLEND_SRC_ALPHA;
    
    private BlendFunction dstBlendFunction = BLEND_ONE_MINUS_SRC_ALPHA;
    
    /**
     * The desired transparency mode.
     */
    private BlendMode mode = BlendMode.BLENDED;
    
    /**
     * The desired transparency level 0=opaque, 1=fully transparent.
     */
    private float tval = 0f;
    
    private boolean enabled = true;
    
    private boolean sortEnabled = true;
    
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * Enables/disables actual transparency rendering on renderer level. Setting
     * this to false causes the object to be places on transparency rendering
     * pass but actually disables the transparency for this object. This can be
     * used to ensure rendering order for stencil operations and keep opaque
     * object rendering speed high.
     * 
     * @see org.xith3d.scenegraph.OrderedGroup
     * @see org.xith3d.scenegraph.TransparencyAttributes#setSortEnabled(boolean)
     */
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
        setChanged( true );
    }
    
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * Enables or disables transparency sorting for this shape.
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
     * @see org.xith3d.scenegraph.TransparencyAttributes#setEnabled(boolean)
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
     * Sets the tranparency level 0=opaque, 1=fully transparent.
     */
    public final void setTransparency( float tval )
    {
        this.tval = tval;
        setChanged( true );
    }
    
    /**
     * Gets the transparency level.
     */
    public final float getTransparency()
    {
        return ( tval );
    }
    
    public void setMode( BlendMode mode )
    {
        this.mode = mode;
        setChanged( true );
    }
    
    public final BlendMode getMode()
    {
        return ( mode );
    }
    
    public void setSrcBlendFunction( BlendFunction srcBlendFunction )
    {
        this.srcBlendFunction = srcBlendFunction;
        setChanged( true );
    }
    
    public final BlendFunction getSrcBlendFunction()
    {
        return ( srcBlendFunction );
    }
    
    public void setDstBlendFunction( BlendFunction dstBlendFunction )
    {
        this.dstBlendFunction = dstBlendFunction;
        setChanged( true );
    }
    
    public final BlendFunction getDstBlendFunction()
    {
        return ( dstBlendFunction );
    }
    
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
    
    public TransparencyAttributes getCopy()
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
        if ( !( o instanceof TransparencyAttributes ) )
            return ( false );
        TransparencyAttributes ro = (TransparencyAttributes)o;
        if ( this.mode != ro.mode )
            return ( false );
        if ( this.tval != ro.tval )
            return ( false );
        if ( this.srcBlendFunction != ro.srcBlendFunction )
            return ( false );
        if ( this.dstBlendFunction != ro.dstBlendFunction )
            return ( false );
        if ( this.sortEnabled != ro.sortEnabled )
            return ( false );
        if ( this.enabled != ro.enabled )
            return ( false );
        
        return ( true );
    }
    
    public int compareTo( TransparencyAttributes o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.mode.ordinal() < o.mode.ordinal() )
            return ( -1 );
        else if ( this.mode.ordinal() > o.mode.ordinal() )
            return ( 1 );
        
        if ( this.tval < o.tval )
            return ( -1 );
        else if ( this.tval > o.tval )
            return ( 1 );
        
        if ( this.srcBlendFunction.ordinal() < o.srcBlendFunction.ordinal() )
            return ( -1 );
        else if ( this.srcBlendFunction.ordinal() > o.srcBlendFunction.ordinal() )
            return ( 1 );
        
        if ( this.dstBlendFunction.ordinal() < o.dstBlendFunction.ordinal() )
            return ( -1 );
        else if ( this.dstBlendFunction.ordinal() > o.dstBlendFunction.ordinal() )
            return ( 1 );
        
        int val = ComparatorHelper.compareBoolean( this.sortEnabled, o.sortEnabled );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareBoolean( this.enabled, o.enabled );
        if ( val != 0 )
            return ( val );
        
        return ( 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( super.toString() + "[a=" + tval + ";mode=" + mode + ";srcf=" + srcBlendFunction + ";dstf=" + dstBlendFunction + ";sort=" + sortEnabled + ";ena=" + enabled + "]" );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        TransparencyAttributes a = (TransparencyAttributes)original;
        setSortEnabled( a.isSortEnabled() );
        setEnabled( a.isEnabled() );
        setMode( a.getMode() );
        setSrcBlendFunction( a.getSrcBlendFunction() );
        setDstBlendFunction( a.getDstBlendFunction() );
        setTransparency( a.getTransparency() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TransparencyAttributes cloneNodeComponent( boolean forceDuplicate )
    {
        TransparencyAttributes ta = new TransparencyAttributes();
        
        ta.duplicateNodeComponent( this, forceDuplicate );
        
        return ( ta );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    
    /**
     * Constructs a new TransparencyAttributes object.
     */
    public TransparencyAttributes()
    {
        super( false );
    }
    
    /**
     * Constructs a new TransparencyAttributes object.
     */
    public TransparencyAttributes( BlendMode mode, float tval )
    {
        this();
        
        this.mode = mode;
        this.tval = tval;
    }
    
    /**
     * Constructs a new TransparencyAttributes object.
     */
    public TransparencyAttributes( float tval )
    {
        this();
        
        this.tval = tval;
    }
    
    public TransparencyAttributes( BlendMode mode, float tval, BlendFunction srcBlendFunction, BlendFunction dstBlendFunction )
    {
        this();
        
        this.mode = mode;
        this.tval = tval;
        this.srcBlendFunction = srcBlendFunction;
        this.dstBlendFunction = dstBlendFunction;
    }
    
    public TransparencyAttributes( BlendMode mode, float tval, BlendFunction srcBlendFunction, BlendFunction dstBlendFunction, boolean sortEnabled, boolean enabled )
    {
        this();
        
        this.mode = mode;
        this.tval = tval;
        this.srcBlendFunction = srcBlendFunction;
        this.dstBlendFunction = dstBlendFunction;
        this.sortEnabled = sortEnabled;
        this.enabled = enabled;
    }
}
