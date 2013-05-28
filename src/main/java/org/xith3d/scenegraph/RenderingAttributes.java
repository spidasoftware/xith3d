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

import org.jagatoo.opengl.enums.StencilOperation;
import org.jagatoo.opengl.enums.TestFunction;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * RenderingAttributes defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class RenderingAttributes extends NodeComponent implements StateTrackable< RenderingAttributes >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * Indicates pixels are always drawn irrespective of the alpha value. This
     * effectively disables alpha testing.
     */
    public static final TestFunction ALWAYS = TestFunction.ALWAYS;
    
    /**
     * Indicates pixels are never drawn irrespective of the alpha value.
     */
    public static final TestFunction NEVER = TestFunction.NEVER;
    
    /**
     * @see TestFunction#EQUAL
     */
    public static final TestFunction EQUAL = TestFunction.EQUAL;
    
    /**
     * @see TestFunction#NOT_EQUAL
     */
    public static final TestFunction NOT_EQUAL = TestFunction.NOT_EQUAL;
    
    /**
     * @see TestFunction#LESS
     */
    public static final TestFunction LESS = TestFunction.LESS;
    
    /**
     * @see TestFunction#LESS_OR_EQUAL
     */
    public static final TestFunction LESS_OR_EQUAL = TestFunction.LESS_OR_EQUAL;
    
    /**
     * @see TestFunction#GREATER
     */
    public static final TestFunction GREATER = TestFunction.GREATER;
    
    /**
     * @see TestFunction#GREATER_OR_EQUAL
     */
    public static final TestFunction GREATER_OR_EQUAL = TestFunction.GREATER_OR_EQUAL;
    
    /**
     * If true the depth buffer mode is enabled.
     */
    private boolean depthBufferEnabled = true;
    
    /**
     * If true depth buffer mode is writable.
     */
    private boolean depthBufferWriteEnabled = true;
    
    /**
     * The desired alpha test value.
     */
    private float alphaTestValue = 0f;
    
    /**
     * The desired alpha test mode.
     */
    private TestFunction alphaTestFunction = TestFunction.ALWAYS;
    
    private StencilFuncSeparate stencilFuncSep = null;
    private StencilOpSeparate stencilOpSep = null;
    private StencilMaskSeparate stencilMaskSep = null;
    
    /**
     * Sets the depth function for comparing against the z-buffer. Normal
     * operation is LESS, but for coplanar geometry you need to use
     * LESS_OR_EQUAL
     */
    public void setDepthTestFunction( TestFunction depthTestFunction )
    {
        this.depthTestFunction = depthTestFunction;
        setChanged( true );
    }
    
    public TestFunction getDepthTestFunction()
    {
        return ( depthTestFunction );
    }
    
    private TestFunction depthTestFunction = TestFunction.LESS;
    
    private boolean ignoreVertexColors = false;
    
    /**
     * Enables/disables the depth buffer
     */
    public final void setDepthBufferEnabled( boolean state )
    {
        this.depthBufferEnabled = state;
        setChanged( true );
    }
    
    /**
     * Is the depth buffer enabled/disabled
     */
    public final boolean isDepthBufferEnabled()
    {
        return ( depthBufferEnabled );
    }
    
    /**
     * Enables/Disables writing to the depth buffer
     */
    public final void setDepthBufferWriteEnabled( boolean state )
    {
        this.depthBufferWriteEnabled = state;
        setChanged( true );
    }
    
    /**
     * Is the depth buffer write enabled/disabled
     */
    public final boolean isDepthBufferWriteEnabled()
    {
        return ( depthBufferWriteEnabled );
    }
    
    /**
     * Sets the alpha test value
     */
    public final void setAlphaTestValue( float val )
    {
        alphaTestValue = val;
        setChanged( true );
    }
    
    /**
     * Gets the alpha test value
     */
    public final float getAlphaTestValue()
    {
        return ( alphaTestValue );
    }
    
    /**
     * Sets the alpha test function
     */
    public final void setAlphaTestFunction( TestFunction func )
    {
        alphaTestFunction = func;
        setChanged( true );
    }
    
    /**
     * Gets the alpha test function
     */
    public final TestFunction getAlphaTestFunction()
    {
        return ( alphaTestFunction );
    }
    
    public void setIgnoreVertexColors( boolean ignoreVertexColors )
    {
        this.ignoreVertexColors = ignoreVertexColors;
        setChanged( true );
    }
    
    public final boolean getIgnoreVertexColors()
    {
        return ( ignoreVertexColors );
    }
    
    // Stencil support code
    
    /**
     * If true the stencil operations are enabled.
     */
    private boolean stencilEnabled = false;
    
    /**
     * @see StencilOperation#KEEP
     */
    public static final StencilOperation KEEP = StencilOperation.KEEP;
    
    /**
     * @see StencilOperation#ZERO
     */
    public static final StencilOperation ZERO = StencilOperation.ZERO;
    
    /**
     * @see StencilOperation#REPLACE
     */
    public static final StencilOperation REPLACE = StencilOperation.REPLACE;
    
    /**
     * @see StencilOperation#INCREMENT
     */
    public static final StencilOperation INCREMENT = StencilOperation.INCREMENT;
    
    /**
     * @see StencilOperation#DECREMENT
     */
    public static final StencilOperation DECREMENT = StencilOperation.DECREMENT;
    
    /**
     * @see StencilOperation#INVERT
     */
    public static final StencilOperation INVERT = StencilOperation.INVERT;
    
    private StencilOperation stencilOpFail = StencilOperation.KEEP;
    
    private StencilOperation stencilOpZFail = StencilOperation.KEEP;
    
    private StencilOperation stencilOpZPass = StencilOperation.REPLACE;
    
    private TestFunction stencilTestFunction = TestFunction.ALWAYS;
    
    private int stencilRef = 0;
    
    private int stencilMask = -1; // i.e. 0xFFFFFFFF
    
    public void setStencilEnabled( boolean value )
    {
        stencilEnabled = value;
        setChanged( true );
    }
    
    public final boolean isStencilEnabled()
    {
        return ( stencilEnabled );
    }
    
    public void setStencilOpFail( StencilOperation value )
    {
        stencilOpFail = value;
        setChanged( true );
    }
    
    public final StencilOperation getStencilOpFail()
    {
        return ( stencilOpFail );
    }
    
    public void setStencilOpZFail( StencilOperation value )
    {
        stencilOpZFail = value;
        setChanged( true );
    }
    
    public final StencilOperation getStencilOpZFail()
    {
        return ( stencilOpZFail );
    }
    
    public void setStencilOpZPass( StencilOperation value )
    {
        stencilOpZPass = value;
        setChanged( true );
    }
    
    public final StencilOperation getStencilOpZPass()
    {
        return ( stencilOpZPass );
    }
    
    public void setStencilOp( StencilOperation fail, StencilOperation zfail, StencilOperation zpass )
    {
        stencilOpFail = fail;
        stencilOpZFail = zfail;
        stencilOpZPass = zpass;
        
        setChanged( true );
    }
    
    public void setStencilTestFunction( TestFunction value )
    {
        stencilTestFunction = value;
        setChanged( true );
    }
    
    public final TestFunction getStencilTestFunction()
    {
        return ( stencilTestFunction );
    }
    
    public void setStencilRef( int value )
    {
        stencilRef = value;
        setChanged( true );
    }
    
    public final int getStencilRef()
    {
        return ( stencilRef );
    }
    
    public void setStencilMask( int value )
    {
        stencilMask = value;
        setChanged( true );
    }
    
    public final int getStencilMask()
    {
        return ( stencilMask );
    }
    
    public void setStencilTestFunction( TestFunction func, int ref, int mask )
    {
        stencilTestFunction = func;
        stencilRef = ref;
        stencilMask = mask;
        setChanged( true );
    }
    
    // DrawBuffer support code
    
    private int colorWriteMask = 15;
    private boolean hasCustomColorMask = false;
    
    public void setColorWriteMask( int mask )
    {
        this.colorWriteMask = mask;
        
        hasCustomColorMask = true;
        
        setChanged( true );
    }
    
    public final boolean hasColorWriteMask()
    {
        return ( hasCustomColorMask );
    }
    
    public final int getColorWriteMask()
    {
        return ( colorWriteMask );
    }
    
    public void setRedWriteEnabled( boolean value )
    {
        if ( value )
            colorWriteMask |= 1;
        else
            colorWriteMask &= ~1;
        
        hasCustomColorMask = true;
        
        setChanged( true );
    }
    
    public final boolean isRedWriteEnabled()
    {
        return ( ( colorWriteMask & 1 ) != 0 );
    }
    
    public void setGreenWriteEnabled( boolean value )
    {
        if ( value )
            colorWriteMask |= 2;
        else
            colorWriteMask &= ~2;
        
        hasCustomColorMask = true;
        
        setChanged( true );
    }
    
    public final boolean isGreenWriteEnabled()
    {
        return ( ( colorWriteMask & 2 ) != 0 );
    }
    
    public void setBlueWriteEnabled( boolean value )
    {
        if ( value )
            colorWriteMask |= 4;
        else
            colorWriteMask &= ~4;
        
        hasCustomColorMask = true;
        
        setChanged( true );
    }
    
    public final boolean isBlueWriteEnabled()
    {
        return ( ( colorWriteMask & 4 ) != 0 );
    }
    
    public void setAlphaWriteEnabled( boolean value )
    {
        if ( value )
            colorWriteMask |= 8;
        else
            colorWriteMask &= ~8;
        
        hasCustomColorMask = true;
        
        setChanged( true );
    }
    
    public final boolean isAlphaWriteEnabled()
    {
        return ( ( colorWriteMask & 8 ) != 0 );
    }
    
    public void setColorWriteMask( boolean valueR, boolean valueG, boolean valueB, boolean valueA )
    {
        this.colorWriteMask = 0;
        if ( valueR )
            colorWriteMask |= 1;
        else if ( valueG )
            colorWriteMask |= 2;
        else if ( valueB )
            colorWriteMask |= 4;
        else if ( valueA )
            colorWriteMask |= 8;
        
        hasCustomColorMask = true;
        
        setChanged( true );
    }
    
    public final void setStencilFuncSeparate( StencilFuncSeparate funcSep )
    {
        this.stencilFuncSep = funcSep;
    }
    
    public final StencilFuncSeparate getStencilFuncSeparate()
    {
        return ( stencilFuncSep );
    }
    
    public final void setStencilOpSeparate( StencilOpSeparate funcSep )
    {
        this.stencilOpSep = funcSep;
    }
    
    public final StencilOpSeparate getStencilOpSeparate()
    {
        return ( stencilOpSep );
    }
    
    public final void setStencilMaskSeparate( StencilMaskSeparate funcSep )
    {
        this.stencilMaskSep = funcSep;
    }
    
    public final StencilMaskSeparate getStencilMaskSeparate()
    {
        return ( stencilMaskSep );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
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
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent originalNodeComponent, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( originalNodeComponent, forceDuplicate );
        
        RenderingAttributes a = (RenderingAttributes)originalNodeComponent;
        setDepthBufferEnabled( a.isDepthBufferEnabled() );
        setDepthBufferWriteEnabled( a.isDepthBufferWriteEnabled() );
        setDepthTestFunction( a.getDepthTestFunction() );
        setAlphaTestValue( a.getAlphaTestValue() );
        setAlphaTestFunction( a.getAlphaTestFunction() );
        setIgnoreVertexColors( a.getIgnoreVertexColors() );
        setStencilEnabled( a.isStencilEnabled() );
        setStencilOp( a.getStencilOpFail(), a.getStencilOpZFail(), a.getStencilOpZPass() );
        setStencilTestFunction( a.getStencilTestFunction(), a.getStencilRef(), a.getStencilMask() );
        if ( a.hasColorWriteMask() )
            setColorWriteMask( a.isRedWriteEnabled(), a.isGreenWriteEnabled(), a.isBlueWriteEnabled(), a.isAlphaWriteEnabled() );
        setStencilFuncSeparate( a.getStencilFuncSeparate() );
        setStencilOpSeparate( a.getStencilOpSeparate() );
        setStencilMaskSeparate( a.getStencilMaskSeparate() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public RenderingAttributes cloneNodeComponent( boolean forceDuplicate )
    {
        RenderingAttributes a = new RenderingAttributes();
        
        a.duplicateNodeComponent( this, forceDuplicate );
        
        return ( a );
    }
    
    public RenderingAttributes getCopy()
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
        if ( !( o instanceof RenderingAttributes ) )
            return ( false );
        RenderingAttributes ro = (RenderingAttributes)o;
        if ( this.depthBufferEnabled != ro.depthBufferEnabled )
            return ( false );
        if ( this.depthBufferWriteEnabled != ro.depthBufferWriteEnabled )
            return ( false );
        if ( this.depthTestFunction != ro.depthTestFunction )
            return ( false );
        if ( this.alphaTestValue != ro.alphaTestValue )
            return ( false );
        if ( this.alphaTestFunction != ro.alphaTestFunction )
            return ( false );
        if ( this.ignoreVertexColors != ro.ignoreVertexColors )
            return ( false );
        if ( this.stencilEnabled != ro.stencilEnabled )
            return ( false );
        if ( this.stencilOpFail != ro.stencilOpFail )
            return ( false );
        if ( this.stencilOpZFail != ro.stencilOpZFail )
            return ( false );
        if ( this.stencilOpZPass != ro.stencilOpZPass )
            return ( false );
        if ( this.stencilTestFunction != ro.stencilTestFunction )
            return ( false );
        if ( this.stencilRef != ro.stencilRef )
            return ( false );
        if ( this.stencilMask != ro.stencilMask )
            return ( false );
        if ( this.colorWriteMask != ro.colorWriteMask )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( RenderingAttributes o )
    {
        if ( this == o )
            return ( 0 );
        
        int val = ComparatorHelper.compareBoolean( this.depthBufferEnabled, o.depthBufferEnabled );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareBoolean( this.depthBufferWriteEnabled, o.depthBufferWriteEnabled );
        if ( val != 0 )
            return ( val );
        
        if ( this.depthTestFunction.ordinal() < o.depthTestFunction.ordinal() )
            return ( -1 );
        else if ( this.depthTestFunction.ordinal() > o.depthTestFunction.ordinal() )
            return ( 1 );
        
        if ( this.alphaTestFunction.ordinal() < o.alphaTestFunction.ordinal() )
            return ( -1 );
        else if ( this.alphaTestFunction.ordinal() > o.alphaTestFunction.ordinal() )
            return ( 1 );
        
        if ( this.alphaTestValue < o.alphaTestValue )
            return ( -1 );
        else if ( this.alphaTestValue > o.alphaTestValue )
            return ( 1 );
        
        val = ComparatorHelper.compareBoolean( this.ignoreVertexColors, o.ignoreVertexColors );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareBoolean( this.stencilEnabled, o.stencilEnabled );
        if ( val != 0 )
            return ( val );
        
        if ( this.stencilOpFail.ordinal() < o.stencilOpFail.ordinal() )
            return ( -1 );
        else if ( this.stencilOpFail.ordinal() > o.stencilOpFail.ordinal() )
            return ( 1 );
        
        if ( this.stencilOpZFail.ordinal() < o.stencilOpZFail.ordinal() )
            return ( -1 );
        else if ( this.stencilOpZFail.ordinal() > o.stencilOpZFail.ordinal() )
            return ( 1 );
        
        if ( this.stencilOpZPass.ordinal() < o.stencilOpZPass.ordinal() )
            return ( -1 );
        else if ( this.stencilOpZPass.ordinal() > o.stencilOpZPass.ordinal() )
            return ( 1 );
        
        if ( this.stencilTestFunction.ordinal() < o.stencilTestFunction.ordinal() )
            return ( -1 );
        else if ( this.stencilTestFunction.ordinal() > o.stencilTestFunction.ordinal() )
            return ( 1 );
        
        if ( this.stencilRef < o.stencilRef )
            return ( -1 );
        else if ( this.stencilRef > o.stencilRef )
            return ( 1 );
        
        if ( this.stencilMask < o.stencilMask )
            return ( -1 );
        else if ( this.stencilMask > o.stencilMask )
            return ( 1 );
        
        if ( this.colorWriteMask < o.colorWriteMask )
            return ( -1 );
        else if ( this.colorWriteMask > o.colorWriteMask )
            return ( 1 );
        
        return ( 0 );
    }
    
    /**
     * Constructs a new RenderingAttributes object.
     */
    public RenderingAttributes()
    {
        super( false );
    }
    
    /**
     * Constructs a new RenderingAttributes object.
     */
    public RenderingAttributes( boolean depthBufferEnabled, boolean depthBufferWriteEnabled, float alphaTestValue, TestFunction alphaTestFunction )
    {
        this();
        
        this.depthBufferEnabled = depthBufferEnabled;
        this.depthBufferWriteEnabled = depthBufferWriteEnabled;
        this.alphaTestValue = alphaTestValue;
        this.alphaTestFunction = alphaTestFunction;
    }
    
    public RenderingAttributes( boolean depthBufferEnabled, boolean depthBufferWriteEnabled, TestFunction depthTestFunction, float alphaTestValue, TestFunction alphaTestFunction, boolean ignoreVertexColors, boolean stencilEnabled, StencilOperation stencilOpFail, StencilOperation stencilOpZFail, StencilOperation stencilOpZPass, TestFunction stencilTestFunction, int stencilRef, int stencilMask )
    {
        this();
        
        this.depthBufferEnabled = depthBufferEnabled;
        this.depthBufferWriteEnabled = depthBufferWriteEnabled;
        this.depthTestFunction = depthTestFunction;
        this.alphaTestValue = alphaTestValue;
        this.alphaTestFunction = alphaTestFunction;
        this.ignoreVertexColors = ignoreVertexColors;
        this.stencilEnabled = stencilEnabled;
        this.stencilOpFail = stencilOpFail;
        this.stencilOpZFail = stencilOpZFail;
        this.stencilOpZPass = stencilOpZPass;
        this.stencilTestFunction = stencilTestFunction;
        this.stencilRef = stencilRef;
        this.stencilMask = stencilMask;
    }
    
    public RenderingAttributes( boolean depthBufferEnabled, boolean depthBufferWriteEnabled, TestFunction depthTestFunction, float alphaTestValue, TestFunction alphaTestFunction, boolean ignoreVertexColors, boolean stencilEnabled, StencilOperation stencilOpFail, StencilOperation stencilOpZFail, StencilOperation stencilOpZPass, TestFunction stencilTestFunction, int stencilRef, int stencilMask, boolean enableRedWrite, boolean enableGreenWrite, boolean enableBlueWrite, boolean enableAlphaWrite )
    {
        this();
        
        this.depthBufferEnabled = depthBufferEnabled;
        this.depthBufferWriteEnabled = depthBufferWriteEnabled;
        this.depthTestFunction = depthTestFunction;
        this.alphaTestValue = alphaTestValue;
        this.alphaTestFunction = alphaTestFunction;
        this.ignoreVertexColors = ignoreVertexColors;
        this.stencilEnabled = stencilEnabled;
        this.stencilOpFail = stencilOpFail;
        this.stencilOpZFail = stencilOpZFail;
        this.stencilOpZPass = stencilOpZPass;
        this.stencilTestFunction = stencilTestFunction;
        this.stencilRef = stencilRef;
        this.stencilMask = stencilMask;
        this.setColorWriteMask( enableRedWrite, enableGreenWrite, enableBlueWrite, enableAlphaWrite );
    }
}
