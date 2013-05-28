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

import org.jagatoo.opengl.enums.LinePattern;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * LineAttributes defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class LineAttributes extends NodeComponent implements StateTrackable< LineAttributes >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * @see LineAttributes.Pattern#SOLID
     */
    public static final LinePattern PATTERN_SOLID = LinePattern.SOLID;
    
    /**
     * @see LineAttributes.LinePattern#DASHED
     */
    public static final LinePattern PATTERN_DASH = LinePattern.DASHED;
    
    /**
     * @see LineAttributes.LinePattern#DOTTED
     */
    public static final LinePattern PATTERN_DOT = LinePattern.DOTTED;
    
    /**
     * @see LineAttributes.LinePattern#DASHED_DOTTED
     */
    public static final LinePattern PATTERN_DASH_DOT = LinePattern.DASHED_DOTTED;
    
    /**
     * @see LineAttributes.LinePattern#USER_DEFINED
     */
    public static final LinePattern PATTERN_USER_DEFINED = LinePattern.USER_DEFINED;
    
    /**
     * The desired line width.
     */
    private float width = 1f;
    
    /**
     * The desired line pattern.
     */
    private LinePattern pattern = LinePattern.SOLID;
    
    /**
     * The desired line pattern mask.
     */
    private int patternMask = 0xFFFF;
    
    /**
     * The desired line pattern repeat factor.
     */
    private int patternScaleFactor = 1;
    
    /**
     * Antialiasing enable/disable.
     */
    private boolean antialiasing = false;
    
    /**
     * Set the line width.
     */
    public final void setLineWidth( float lineWidth )
    {
        this.width = lineWidth;
        setChanged( true );
    }
    
    /**
     * Get the line width.
     */
    public final float getLineWidth()
    {
        return ( width );
    }
    
    /**
     * Set antialiasing .
     */
    public final void setLineAntialiasingEnabled( boolean state )
    {
        this.antialiasing = state;
        setChanged( true );
    }
    
    /**
     * Get antialiasing.
     */
    public final boolean isLineAntialiasingEnabled()
    {
        return ( antialiasing );
    }
    
    /**
     * Set the line pattern.
     */
    public final void setLinePattern( LinePattern linePattern )
    {
        this.pattern = linePattern;
        
        setChanged( true );
    }
    
    /**
     * Get the line pattern.
     */
    public final LinePattern getLinePattern()
    {
        return ( pattern );
    }
    
    /**
     * Set the line pattern mask.
     */
    public final void setPatternMask( int patternMask )
    {
        this.patternMask = patternMask;
        setChanged( true );
    }
    
    /**
     * Get the line pattern mask.
     */
    public final int getPatternMask()
    {
        return ( patternMask );
    }
    
    /**
     * Set the line pattern repeat (scale) factor.
     * <p>
     * Repeat (scale) factor is a multiplier for each bit in the line stipple
     * pattern. If factor is 3, for example, each bit in the pattern will be
     * used three times before the next bit in the pattern is used. The factor
     * parameter is clamped to the range [1, 256] and defaults to one.
     */
    public final void setPatternScaleFactor( int factor )
    {
        this.patternScaleFactor = factor;
        setChanged( true );
    }
    
    /**
     * Get the line pattern repeat factor.
     */
    public final int getPatternScaleFactor()
    {
        return ( patternScaleFactor );
    }
    
    private boolean sortEnabled = true;
    
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
    
    public boolean isSortEnabled()
    {
        return ( sortEnabled );
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
    
    public LineAttributes getCopy()
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
        if ( !( o instanceof LineAttributes ) )
            return ( false );
        LineAttributes lo = (LineAttributes)o;
        if ( this.width != lo.width )
            return ( false );
        if ( this.pattern != lo.pattern )
            return ( false );
        if ( this.patternMask != lo.patternMask )
            return ( false );
        if ( this.patternScaleFactor != lo.patternScaleFactor )
            return ( false );
        if ( this.antialiasing != lo.antialiasing )
            return ( false );
        if ( this.sortEnabled != lo.sortEnabled )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( LineAttributes o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.width < o.width )
            return ( -1 );
        else if ( this.width > o.width )
            return ( 1 );
        
        if ( this.pattern.ordinal() < o.pattern.ordinal() )
            return ( -1 );
        else if ( this.pattern.ordinal() > o.pattern.ordinal() )
            return ( 1 );
        
        if ( this.patternMask < o.patternMask )
            return ( -1 );
        else if ( this.patternMask > o.patternMask )
            return ( 1 );
        
        if ( this.patternScaleFactor < o.patternScaleFactor )
            return ( -1 );
        else if ( this.patternScaleFactor > o.patternScaleFactor )
            return ( 1 );
        
        int val = ComparatorHelper.compareBoolean( this.antialiasing, o.antialiasing );
        if ( val != 0 )
            return ( val );
        
        return ( ComparatorHelper.compareBoolean( this.sortEnabled, o.sortEnabled ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        LineAttributes o = (LineAttributes)original;
        
        this.width = o.getLineWidth();
        this.pattern = o.getLinePattern();
        this.patternMask = o.getPatternMask();
        this.patternScaleFactor = o.getPatternScaleFactor();
        this.antialiasing = o.isLineAntialiasingEnabled();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LineAttributes cloneNodeComponent( boolean forceDuplicate )
    {
        LineAttributes la = new LineAttributes();
        
        la.duplicateNodeComponent( this, forceDuplicate );
        
        return ( la );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /**
     * Constructs a new LineAttributes object.
     */
    public LineAttributes()
    {
        super( false );
    }
    
    /**
     * Constructs a new LineAttributes object with the specified attributes.
     */
    public LineAttributes( float lineWidth, LinePattern linePattern, boolean antialiasing )
    {
        this();
        
        this.width = lineWidth;
        this.pattern = linePattern;
        this.antialiasing = antialiasing;
    }
    
    /**
     * Constructs a new LineAttributes object with the specified attributes.
     */
    public LineAttributes( float lineWidth, LinePattern linePattern, int patternScaleFactor, int patternMask, boolean antialiasing )
    {
        this( lineWidth, linePattern, antialiasing );
        
        this.patternScaleFactor = patternScaleFactor;
        this.patternMask = patternMask;
    }
}
