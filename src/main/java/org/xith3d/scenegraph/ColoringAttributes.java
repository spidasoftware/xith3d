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

import org.jagatoo.opengl.enums.ShadeModel;
import org.openmali.vecmath2.Colorf;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * ColoringAttributes defines attributes that apply to color mapping.
 * Coloring attributes also may define some of the material colors if used in conjunction
 * with lit objects and materials with color target set to something else than NONE.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class ColoringAttributes extends NodeComponent implements StateTrackable<ColoringAttributes>
{
    /**
     * @see ShadeModel#FLAT
     */
    public static final ShadeModel SHADE_FLAT = ShadeModel.FLAT;
    
    /**
     * @see ShadeModel#GOURAUD
     */
    public static final ShadeModel SHADE_GOURAUD = ShadeModel.GOURAUD;
    
    /**
     * @see ShadeModel#FASTEST
     */
    public static final ShadeModel FASTEST = ShadeModel.FASTEST;
    
    /**
     * @see ShadeModel#NICEST
     */
    public static final ShadeModel NICEST = ShadeModel.NICEST;
    
    /**
     * The desired color.
     */
    private final Colorf color = new Colorf( 1f, 1f, 1f );
    
    /**
     * The desired shade model.
     */
    private ShadeModel shadeModel = ShadeModel.GOURAUD;
    
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * Sets the color.
     */
    public final void setColor( float r, float g, float b )
    {
        this.color.set( r, g, b );
        
        setChanged( true );
    }
    
    /**
     * Sets the color.
     */
    public final void setColor( Colorf color )
    {
        this.color.set( color );
        
        setChanged( true );
    }
    
    /**
     * @return the color.
     */
    public final Colorf getColor( Colorf color )
    {
        color.set( this.color );
        
        return ( color );
    }
    
    /**
     * @return the color.
     */
    public final Colorf getColor()
    {
        return ( color.getReadOnly() );
    }
    
    /**
     * Sets the shade model.
     */
    public final void setShadeModel( ShadeModel model )
    {
        shadeModel = model;
        
        setChanged( true );
    }
    
    /**
     * @return the shade model.
     */
    public final ShadeModel getShadeModel()
    {
        return ( shadeModel );
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
    
    public ColoringAttributes getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof ColoringAttributes ) )
            return ( false );
        ColoringAttributes co = (ColoringAttributes)o;
        if ( ComparatorHelper.compare( color, co.color ) != 0 )
            return ( false );
        if ( shadeModel != co.shadeModel )
            return ( false );
        
        return ( true );
    }
    
    public int compareTo( ColoringAttributes o )
    {
        if ( this == o )
            return ( 0 );
        
        int val = ComparatorHelper.compare( color, o.color );
        if ( val != 0 )
            return val;
        
        if ( shadeModel.ordinal() < o.shadeModel.ordinal() )
            return ( -1 );
        else if ( shadeModel.ordinal() > o.shadeModel.ordinal() )
            return ( 1 );
        else
            return ( 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        ColoringAttributes o = (ColoringAttributes)original;
        setColor( o.getColor() );
        
        setShadeModel( o.getShadeModel() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ColoringAttributes cloneNodeComponent( boolean forceDuplicate )
    {
        ColoringAttributes ca = new ColoringAttributes();
        ca.duplicateNodeComponent( this, forceDuplicate );
        
        return ( ca );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /**
     * Constructs a new ColoringAttributes object.
     */
    public ColoringAttributes()
    {
        super( false );
    }
    
    /**
     * Constructs a new ColoringAttributes object with the specified color.
     */
    public ColoringAttributes( float r, float g, float b, ShadeModel shadeModel )
    {
        this();
        
        this.color.set( r, g, b );
        this.shadeModel = shadeModel;
    }
    
    /**
     * Constructs a new ColoringAttributes object with the specified color.
     */
    public ColoringAttributes( float r, float g, float b )
    {
        this( r, g, b, ShadeModel.GOURAUD );
    }
    
    /**
     * Constructs a new ColoringAttributes object with the specified color.
     */
    public ColoringAttributes( Colorf color, ShadeModel shadeModel )
    {
        this( color.getRed(), color.getGreen(), color.getBlue(), shadeModel );
    }
    
    /**
     * Constructs a new ColoringAttributes object with the specified color.
     */
    public ColoringAttributes( Colorf color )
    {
        this( color.getRed(), color.getGreen(), color.getBlue() );
    }
    
    /**
     * Constructs a new ColoringAttributes object with the specified color.
     */
    public ColoringAttributes( ShadeModel shadeModel )
    {
        this( Colorf.WHITE, shadeModel );
    }
}
