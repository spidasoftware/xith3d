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

import org.jagatoo.opengl.enums.PerspectiveCorrectionMode;
import org.jagatoo.opengl.enums.TextureCombineFunction;
import org.jagatoo.opengl.enums.TextureCombineMode;
import org.jagatoo.opengl.enums.TextureCombineSource;
import org.jagatoo.opengl.enums.CompareFunction;
import org.jagatoo.opengl.enums.TextureCompareMode;
import org.jagatoo.opengl.enums.TextureMode;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * TextureAttributes defines attributes that apply to .
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class TextureAttributes extends NodeComponent implements StateTrackable< TextureAttributes >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * @see TextureMode#MODULATE
     */
    public static final TextureMode MODULATE = TextureMode.MODULATE;
    
    /**
     * @see TextureMode#REPLACE
     */
    public static final TextureMode REPLACE = TextureMode.REPLACE;
    
    /*
     * @see TextureMode#COMBINE
     */
    //public static final TextureMode COMBINE = TextureMode.COMBINE;
    
    /**
     * @see TextureMode#BLEND
     */
    public static final TextureMode BLEND = TextureMode.BLEND;
    
    /**
     * @see TextureMode#DECAL
     */
    public static final TextureMode DECAL = TextureMode.DECAL;
    
    /**
     * @see PerspectiveCorrectionMode#NICEST
     */
    public static final PerspectiveCorrectionMode NICEST = PerspectiveCorrectionMode.NICEST;
    
    /**
     * @see PerspectiveCorrectionMode#FASTEST
     */
    public static final PerspectiveCorrectionMode FASTEST = PerspectiveCorrectionMode.FASTEST;
    
    public static final TextureCombineMode COMBINE_REPLACE = TextureCombineMode.REPLACE;
    public static final TextureCombineMode COMBINE_MODULATE = TextureCombineMode.MODULATE;
    public static final TextureCombineMode COMBINE_ADD = TextureCombineMode.ADD;
    public static final TextureCombineMode COMBINE_ADD_SIGNED = TextureCombineMode.ADD_SIGNED;
    public static final TextureCombineMode COMBINE_SUBTRACT = TextureCombineMode.SUBTRACT;
    public static final TextureCombineMode COMBINE_INTERPOLATE = TextureCombineMode.INTERPOLATE;
    public static final TextureCombineMode COMBINE_DOT3 = TextureCombineMode.DOT3;
    public static final TextureCombineMode COMBINE = TextureCombineMode.DOT3;
    
    public static final TextureCombineSource COMBINE_OBJECT_COLOR = TextureCombineSource.OBJECT_COLOR;
    public static final TextureCombineSource COMBINE_TEXTURE_COLOR = TextureCombineSource.TEXTURE_COLOR;
    public static final TextureCombineSource COMBINE_CONSTANT_COLOR = TextureCombineSource.CONSTANT_COLOR;
    public static final TextureCombineSource COMBINE_PREVIOUS_TEXTURE_UNIT = TextureCombineSource.PREVIOUS_TEXTURE_UNIT;
    public static final TextureCombineSource COMBINE_TEXTURE0 = TextureCombineSource.TEXTURE0;
    public static final TextureCombineSource COMBINE_TEXTURE1 = TextureCombineSource.TEXTURE1;
    
    public static final TextureCombineFunction COMBINE_SRC_COLOR = TextureCombineFunction.SRC_COLOR;
    public static final TextureCombineFunction COMBINE_ONE_MINUS_SRC_COLOR = TextureCombineFunction.ONE_MINUS_SRC_COLOR;
    public static final TextureCombineFunction COMBINE_SRC_ALPHA = TextureCombineFunction.SRC_ALPHA;
    public static final TextureCombineFunction COMBINE_ONE_MINUS_SRC_ALPHA = TextureCombineFunction.ONE_MINUS_SRC_ALPHA;
    
    /**
     * The desired texture mode.
     */
    private TextureMode textureMode = TextureMode.MODULATE;
    
    /**
     * The desired texture blend color.
     */
    private Colorf texBlendColor = null;
    
    /**
     * The desired perspective correction mode.
     */
    private PerspectiveCorrectionMode perspCorrectionMode = PerspectiveCorrectionMode.NICEST;
    
    /**
     * The desired transform.
     */
    private Transform3D transform = null;
    
    private TextureCombineMode combineRGBMode = TextureCombineMode.MODULATE;
    
    private TextureCombineMode combineAlphaMode = TextureCombineMode.MODULATE;
    
    private int[] combineRGBSource = new int[]
    {
        TextureCombineSource.PREVIOUS_TEXTURE_UNIT.ordinal(),
        TextureCombineSource.TEXTURE_COLOR.ordinal(),
        COMBINE_CONSTANT_COLOR.ordinal()
    };
    
    private int[] combineAlphaSource = new int[]
    {
        TextureCombineSource.TEXTURE_COLOR.ordinal(),
        TextureCombineSource.PREVIOUS_TEXTURE_UNIT.ordinal(),
        TextureCombineSource.CONSTANT_COLOR.ordinal()
    };
    
    private int[] combineRGBFunction = new int[]
    {
        TextureCombineFunction.SRC_COLOR.ordinal(),
        TextureCombineFunction.SRC_COLOR.ordinal(),
        TextureCombineFunction.SRC_COLOR.ordinal()
    };
    
    private int[] combineAlphaFunction = new int[]
    {
        TextureCombineFunction.SRC_ALPHA.ordinal(),
        TextureCombineFunction.SRC_ALPHA.ordinal(),
        TextureCombineFunction.SRC_ALPHA.ordinal()
    };
    
    private int combineRGBScale = 1;
    
    private int combineAlphaScale = 1;
    
    private TextureCompareMode compareMode = TextureCompareMode.NONE;
    
    private CompareFunction compareFunc = CompareFunction.LOWER_OR_EQUAL;
    
    public void setCombineAlphaSource( int index, TextureCombineSource value )
    {
        this.combineAlphaSource[ index ] = value.ordinal();
        setChanged( true );
    }
    
    public TextureCombineSource getCombineAlphaSource( int index )
    {
        return ( TextureCombineSource.values()[ combineAlphaSource[ index ] ] );
    }
    
    public void setCombineRGBSource( int index, TextureCombineSource value )
    {
        this.combineRGBSource[ index ] = value.ordinal();
        setChanged( true );
    }
    
    public TextureCombineSource getCombineRGBSource( int index )
    {
        return ( TextureCombineSource.values()[ combineRGBSource[ index ] ] );
    }
    
    /**
     * sets the texture mode. MODULATE, DECAL, BLEND or REPLACE. Default is
     * MODULATE.
     */
    public final void setTextureMode( TextureMode mode )
    {
        textureMode = mode;
        setChanged( true );
    }
    
    /**
     * gets the texture mode.
     */
    public TextureMode getTextureMode()
    {
        return ( textureMode );
    }
    
    /**
     * sets the texture blend color.
     */
    public final void setTextureBlendColor( Colorf color )
    {
        if ( ( texBlendColor == null ) && ( color != null ) )
        {
            this.texBlendColor = new Colorf( color );
            setChanged( true );
        }
        else if ( ( texBlendColor != null ) && ( color == null ) )
        {
            this.texBlendColor = null;
            setChanged( true );
        }
        else if ( ( texBlendColor != null ) && ( color != null ) )
        {
            this.texBlendColor.set( color );
            setChanged( true );
        }
    }
    
    public final void getTextureBlendColor( Colorf color )
    {
        color.set( this.texBlendColor );
    }
    
    /**
     * gets the texture blend color.
     */
    public final Colorf getTextureBlendColor()
    {
        return ( texBlendColor );
    }
    
    /**
     * sets the texture transform.
     */
    public final void setTextureTransform( Transform3D transform )
    {
        this.transform = transform;
        setChanged( true );
    }
    
    /**
     * sets the texture transform.
     */
    public final void setTextureTransform( Matrix4f matrix )
    {
        if ( matrix == null )
            this.transform = null;
        else if ( this.transform == null )
            this.transform = new Transform3D( matrix );
        else
            this.transform.set( matrix );
        
        setChanged( true );
    }
    
    /**
     * gets the texture transform.
     */
    public final Transform3D getTextureTransform()
    {
        return ( transform );
    }
    
    /**
     * Sets the perspective correction mode.<br>
     * The default is NICEST.
     */
    public final void setPerspectiveCorrectionMode( PerspectiveCorrectionMode mode )
    {
        perspCorrectionMode = mode;
        setChanged( true );
    }
    
    /**
     * @return the perspective correction mode.
     */
    public final PerspectiveCorrectionMode getPerspectiveCorrectionMode()
    {
        return ( perspCorrectionMode );
    }
    
    public void setCombineRGBMode( TextureCombineMode combineRgbMode )
    {
        this.combineRGBMode = combineRgbMode;
        setChanged( true );
    }
    
    public TextureCombineMode getCombineRGBMode()
    {
        return ( combineRGBMode );
    }
    
    public void setCombineAlphaMode( TextureCombineMode combineAlphaMode )
    {
        this.combineAlphaMode = combineAlphaMode;
        setChanged( true );
    }
    
    public TextureCombineMode getCombineAlphaMode()
    {
        return ( combineAlphaMode );
    }
    
    public void setCombineRGBFunction( int index, TextureCombineFunction combineRgbFunction )
    {
        this.combineRGBFunction[ index ] = combineRgbFunction.ordinal();
        setChanged( true );
    }
    
    public TextureCombineFunction getCombineRGBFunction( int index )
    {
        return ( TextureCombineFunction.values()[ combineRGBFunction[ index ] ] );
    }
    
    public void setCombineAlphaFunction( int index, TextureCombineFunction combineAlphaFunction )
    {
        this.combineAlphaFunction[ index ] = combineAlphaFunction.ordinal();
        setChanged( true );
    }
    
    public TextureCombineFunction getCombineAlphaFunction( int index )
    {
        return ( TextureCombineFunction.values()[ combineAlphaFunction[ index ] ] );
    }
    
    public int getCombineRGBScale()
    {
        return ( combineRGBScale );
    }
    
    public void setCombineRGBScale( int combineRgbScale )
    {
        this.combineRGBScale = combineRgbScale;
        setChanged( true );
    }
    
    public int getCombineAlphaScale()
    {
        return ( combineAlphaScale );
    }
    
    public void setCombineAlphaScale( int combineAlphaScale )
    {
        this.combineAlphaScale = combineAlphaScale;
        setChanged( true );
    }
    
    public void setCompareMode( TextureCompareMode compareMode )
    {
        if ( compareMode == null )
            throw new IllegalArgumentException( "compareMode must not be null" );
        
        this.compareMode = compareMode;
    }
    
    public final TextureCompareMode getCompareMode()
    {
        return ( compareMode );
    }
    
    public void setCompareFunction( CompareFunction compareFunc )
    {
        if ( compareFunc == null )
            throw new IllegalArgumentException( "compareFunc must not be null" );
        
        this.compareFunc = compareFunc;
    }
    
    public final CompareFunction getCompareFunction()
    {
        return ( compareFunc );
    }
    
    private static final int[] copy( int[] a )
    {
        if ( a == null )
            return ( null );
        int[] b = new int[ a.length ];
        System.arraycopy( a, 0, b, 0, a.length );
        
        return ( b );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent nodeOriginal, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( nodeOriginal, forceDuplicate );
        
        TextureAttributes original = (TextureAttributes)nodeOriginal;
        
        this.perspCorrectionMode = original.perspCorrectionMode;
        this.textureMode = original.textureMode;
        if ( forceDuplicate )
        {
            setTextureBlendColor( original.texBlendColor );
            
            if ( this.transform == null )
            {
                if ( original.transform != null )
                {
                    this.transform = new Transform3D( original.transform );
                }
            }
            else if ( original.transform == null )
            {
                this.transform = null;
            }
            else
            {
                this.transform.set( original.transform );
            }
        }
        else
        {
            this.texBlendColor = original.texBlendColor;
            this.transform = original.transform;
        }
        this.combineRGBMode = original.combineRGBMode;
        this.combineRGBScale = original.combineRGBScale;
        this.combineAlphaMode = original.combineAlphaMode;
        this.combineAlphaScale = original.combineAlphaScale;
        this.combineRGBFunction = copy( original.combineRGBFunction );
        this.combineRGBSource = copy( original.combineRGBSource );
        this.combineAlphaFunction = copy( original.combineAlphaFunction );
        this.combineAlphaSource = copy( original.combineAlphaSource );
        this.compareMode = original.compareMode;
        this.compareFunc = original.compareFunc;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TextureAttributes cloneNodeComponent( boolean forceDuplicate )
    {
        TextureAttributes ta = new TextureAttributes();
        
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
    
    // ////////////////////////////////////////////////////////////////
    // ///////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    // ////////////////////////////////////////////////////////////////
    
    /**
     * {@inheritDoc}
     */
    public final void setStateNode( StateNode node )
    {
        this.stateNode = node;
        this.stateId = node.getId();
    }
    
    /**
     * {@inheritDoc}
     */
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
    public TextureAttributes getCopy()
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
        if ( !( o instanceof TextureAttributes ) )
            return ( false );
        TextureAttributes ro = (TextureAttributes)o;
        if ( this.textureMode != ro.textureMode )
            return ( false );
        if ( this.perspCorrectionMode != ro.perspCorrectionMode )
            return ( false );
        if ( ComparatorHelper.compare( this.texBlendColor, ro.texBlendColor ) != 0 )
            return ( false );
        if ( ComparatorHelper.compare( this.transform, ro.transform ) != 0 )
            return ( false );
        
        if ( this.combineRGBMode != ro.combineRGBMode )
            return ( false );
        if ( this.combineAlphaMode != ro.combineAlphaMode )
            return ( false );
        if ( this.combineRGBScale != ro.combineRGBScale )
            return ( false );
        if ( this.combineAlphaScale != ro.combineAlphaScale )
            return ( false );
        if ( ComparatorHelper.compare( this.combineRGBFunction, ro.combineRGBFunction ) != 0 )
            return ( false );
        if ( ComparatorHelper.compare( this.combineAlphaFunction, ro.combineAlphaFunction ) != 0 )
            return ( false );
        if ( ComparatorHelper.compare( this.combineRGBSource, ro.combineRGBSource ) != 0 )
            return ( false );
        if ( ComparatorHelper.compare( this.combineAlphaSource, ro.combineAlphaSource ) != 0 )
            return ( false );
        if ( this.compareMode != ro.compareMode )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( TextureAttributes o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.textureMode.ordinal() < o.textureMode.ordinal() )
            return ( -1 );
        else if ( this.textureMode.ordinal() > o.textureMode.ordinal() )
            return ( 1 );
        
        if ( this.perspCorrectionMode.ordinal() < o.perspCorrectionMode.ordinal() )
            return ( -1 );
        else if ( this.perspCorrectionMode.ordinal() > o.perspCorrectionMode.ordinal() )
            return ( 1 );
        
        int val = ComparatorHelper.compare( this.texBlendColor, o.texBlendColor );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( this.transform, o.transform );
        if ( val != 0 )
            return ( val );
        
        if ( this.combineRGBMode.ordinal() < o.combineRGBMode.ordinal() )
            return ( -1 );
        else if ( this.combineRGBMode.ordinal() > o.combineRGBMode.ordinal() )
            return ( 1 );
        
        if ( this.combineAlphaMode.ordinal() < o.combineAlphaMode.ordinal() )
            return ( -1 );
        else if ( this.combineAlphaMode.ordinal() > o.combineAlphaMode.ordinal() )
            return ( 1 );
        
        if ( this.combineRGBScale < o.combineRGBScale )
            return ( -1 );
        else if ( this.combineRGBScale > o.combineRGBScale )
            return ( 1 );
        
        if ( this.combineAlphaScale < o.combineAlphaScale )
            return ( -1 );
        else if ( this.combineAlphaScale > o.combineAlphaScale )
            return ( 1 );
        
        val = ComparatorHelper.compare( this.combineRGBFunction, o.combineRGBFunction );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( this.combineAlphaFunction, o.combineAlphaFunction );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( this.combineRGBSource, o.combineRGBSource );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( this.combineAlphaSource, o.combineAlphaSource );
        if ( val != 0 )
            return ( val );
        
        val = this.compareMode.ordinal() - o.compareMode.ordinal();
        if ( val != 0 )
            return ( val );
        
        val = this.compareFunc.ordinal() - o.compareFunc.ordinal();
        if ( val != 0 )
            return ( val );
        
        return ( 0 );
    }
    
    /**
     * Constructs a new TextureAttributes object.
     */
    public TextureAttributes()
    {
        super( false );
    }
    
    /**
     * Constructs a new TextureAttributes object.
     */
    public TextureAttributes( TextureMode textureMode, Transform3D transform, Colorf textureBlendColor, PerspectiveCorrectionMode perspCorrectionMode )
    {
        this();
        
        this.perspCorrectionMode = perspCorrectionMode;
        this.textureMode = textureMode;
        this.setTextureBlendColor( textureBlendColor );
        this.transform = transform;
    }
    
    @SuppressWarnings( "unused" )
    private TextureAttributes( TextureMode textureMode, Transform3D transform, Colorf textureBlendColor, Colorf blendColor, PerspectiveCorrectionMode perspCorrectionMode, TextureCombineMode combineRgbMode, int combineRgbScale, TextureCombineMode combineAlphaMode, int combineAlphaScale, int[] combineRgbFunction, int[] combineRgbSource, int[] combineAlphaFunction, int[] combineAlphaSource )
    {
        this();
        
        this.textureMode = textureMode;
        this.transform = transform;
        this.setTextureBlendColor( textureBlendColor );
        this.perspCorrectionMode = perspCorrectionMode;
        this.combineRGBMode = combineRgbMode;
        this.combineRGBScale = combineRgbScale;
        this.combineAlphaMode = combineAlphaMode;
        this.combineAlphaScale = combineAlphaScale;
        this.combineRGBFunction = copy( combineRgbFunction );
        this.combineRGBSource = copy( combineRgbSource );
        this.combineAlphaFunction = copy( combineAlphaFunction );
        this.combineAlphaSource = copy( combineAlphaSource );
    }
}
