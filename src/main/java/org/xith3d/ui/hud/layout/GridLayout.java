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
 * The GridLayout arranges the contained Widgets in a Grid.<br>
 * You can define borders and gaps and even column- and row-weights.<br>
 * <br>
 * the widgets' original size is overwritten by the calculated size.
 * If the rows-count is 0, then the widget's original height is respected
 * and only the width is calculated. The same applies for columns-count.<br>
 * <br>
 * If columns-count is greater than 0, one row is filled after the other in the
 * order, the widgets are added.
 * If columns-count is 0, the fields are filled by column-first.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GridLayout extends BorderSettableLayoutManagerBase
{
    private int rows;
    private int cols;
    
    private float hgap;
    private float vgap;
    
    private float[] colWeights = null;
    private float[] rowWeights = null;
    
    private float[] recColWeights = null;
    private float[] recRowWeights = null;
    
    public void setRows( int rows )
    {
        this.rows = rows;
    }
    
    public final int getRows()
    {
        return ( rows );
    }
    
    public void setCols( int cols )
    {
        this.cols = cols;
    }
    
    public final int getCols()
    {
        return ( cols );
    }
    
    public void setHGap( float hgap )
    {
        this.hgap = hgap;
    }
    
    public final float getHGap()
    {
        return ( hgap );
    }
    
    public void setVGap( float vgap )
    {
        this.vgap = vgap;
    }
    
    public final float getVGap()
    {
        return ( vgap );
    }
    
    public void setColWeights( float... weights )
    {
        this.colWeights = weights;
        
        if ( colWeights != null )
        {
            if ( ( recColWeights == null ) || ( recColWeights.length != colWeights.length ) )
                recColWeights = new float[ colWeights.length ];
            
            float sum = 0f;
            for ( int i = 0; i < colWeights.length; i++ )
                sum += colWeights[ i ];
            for ( int i = 0; i < colWeights.length; i++ )
                recColWeights[ i ] = colWeights[ i ] / sum;
        }
    }
    
    public final float[] getColWeights()
    {
        return ( colWeights );
    }
    
    public void setRowWeights( float... weights )
    {
        this.rowWeights = weights;
        
        if ( rowWeights != null )
        {
            if ( ( recRowWeights == null ) || ( recRowWeights.length != rowWeights.length ) )
                recRowWeights = new float[ rowWeights.length ];
            
            float sum = 0f;
            for ( int i = 0; i < rowWeights.length; i++ )
                sum += rowWeights[ i ];
            for ( int i = 0; i < rowWeights.length; i++ )
                recRowWeights[ i ] = rowWeights[ i ] / sum;
        }
    }
    
    public final float[] getRowWeights()
    {
        return ( rowWeights );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLayout( final float left0, final float top0, final float containerResX, final float containerResY )
    {
        final ArrayList< Widget > widgets = getWidgets();
        
        if ( ( rows > 0 ) && ( cols > 0 ) )
        {
            final float totalWidth = ( containerResX - ( ( getCols() - 1 ) * getHGap() ) );
            final float totalHeight = ( containerResY - ( ( getRows() - 1 ) * getVGap() ) );
            
            float width = 0f;
            float height = 0f;
            
            if ( getColWeights() == null )
                width = totalWidth / getCols();
            
            if ( getRowWeights() == null )
                height = totalHeight / getRows();
            
            float left = left0;
            float top = top0;
            
            int row = 0;
            int col = 0;
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                final Widget widget = widgets.get( i );
                
                if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                    continue;
                
                if ( getColWeights() != null )
                    width = totalWidth * recColWeights[ col ];
                
                if ( getRowWeights() != null )
                    height = totalHeight * recRowWeights[ row ];
                
                widget.setSize( width, height );
                widget.setLocation( left, top );
                
                col++;
                left += width + getHGap();
                
                if ( col >= getCols() )
                {
                    col = 0;
                    row++;
                    
                    left = left0;
                    top += height + getVGap();
                }
            }
        }
        else if ( ( rows == 0 ) && ( cols > 0 ) )
        {
            final float totalWidth = ( containerResX - ( ( getCols() - 1 ) * getHGap() ) );
            
            float width = 0f;
            float height = 0f;
            
            if ( getColWeights() == null )
                width = totalWidth / getCols();
            
            float left = left0;
            float top = top0;
            
            int row = 0;
            int col = 0;
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                final Widget widget = widgets.get( i );
                
                if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                    continue;
                
                if ( getColWeights() != null )
                    width = totalWidth * recColWeights[ col ];
                
                if ( widget.getHeight() > height )
                    height = widget.getHeight();
                
                widget.setSize( width, widget.getHeight() );
                widget.setLocation( left, top );
                
                col++;
                left += width + getHGap();
                
                if ( col >= getCols() )
                {
                    col = 0;
                    row++;
                    
                    left = left0;
                    top += height + getVGap();
                    height = 0f;
                }
            }
        }
        else if ( ( rows > 0 ) && ( cols == 0 ) )
        {
            final float totalHeight = ( containerResY - ( ( getRows() - 1 ) * getVGap() ) );
            
            float width = 0f;
            float height = 0f;
            
            if ( getRowWeights() == null )
                height = totalHeight / getRows();
            
            float left = left0;
            float top = top0;
            
            int row = 0;
            int col = 0;
            
            for ( int i = 0; i < widgets.size(); i++ )
            {
                final Widget widget = widgets.get( i );
                
                if ( !widget.isVisible() && getInvisibleWidgetsHidden() )
                    continue;
                
                if ( widget.getWidth() > width )
                    width = widget.getWidth();
                
                if ( getRowWeights() != null )
                    height = totalHeight * recRowWeights[ row ];
                
                widget.setSize( widget.getWidth(), height );
                widget.setLocation( left, top );
                
                row++;
                top += height + getVGap();
                
                if ( row >= getRows() )
                {
                    row = 0;
                    col++;
                    
                    top = top0;
                    left += width + getHGap();
                    width = 0f;
                }
            }
        }
    }
    
    public GridLayout( int rows, int cols, float hgap, float vgap, float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        super( borderBottom, borderRight, borderTop, borderLeft );
        
        if ( ( rows < 0 ) || ( cols < 0 ) )
            throw new IllegalArgumentException( "rows and cols must be >= 0" );
        
        if ( ( rows == 0 ) && ( cols == 0 ) )
            throw new IllegalArgumentException( "rows and cols cannot be 0 at the same time" );
        
        if ( ( hgap < 0f ) || ( vgap < 0f ) )
            throw new IllegalArgumentException( "hgap and vgap must be >= 0" );
        
        this.rows = rows;
        this.cols = cols;
        
        this.hgap = hgap;
        this.vgap = vgap;
    }
    
    public GridLayout( int rows, int cols, float hgap, float vgap )
    {
        this( rows, cols, hgap, vgap, 0f, 0f, 0f, 0f );
    }
    
    public GridLayout( int rows, int cols, float borderBottom, float borderRight, float borderTop, float borderLeft )
    {
        this( rows, cols, 0f, 0f, borderBottom, borderRight, borderTop, borderLeft );
    }
    
    public GridLayout( int rows, int cols )
    {
        this( rows, cols, 0f, 0f );
    }
}
