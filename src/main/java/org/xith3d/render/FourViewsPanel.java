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
package org.xith3d.render;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.xith3d.render.config.OpenGLLayer;

/**
 * A JPanel extension holding four Canvas3DPanels for a view
 * known from 3D-modeling programs.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class FourViewsPanel extends JPanel
{
    private static final long serialVersionUID = 1041045431655033959L;
    
    private JSplitPane northSP, southSP, northSouthSP;
    private Canvas3DPanel nw, ne, sw, se;
    
    private class DividerSyncer implements PropertyChangeListener
    {
        public void propertyChange( PropertyChangeEvent evt )
        {
            if ( evt.getPropertyName().equals( "lastDividerLocation" ) )
            {
                if ( evt.getSource() == northSP )
                {
                    southSP.setDividerLocation( northSP.getDividerLocation() );
                }
                else if ( evt.getSource() == southSP )
                {
                    northSP.setDividerLocation( southSP.getDividerLocation() );
                }
            }
        }
    }
    
    /**
     * @return the currently used Canvas3DPanel for north-west
     */
    public Canvas3DPanel getNWPanel()
    {
        return ( nw );
    }
    
    /**
     * Sets the new Canvas3DPanel for north-west
     * 
     * @param c3dp new new Canvas3DPanel for north-west
     */
    public void setNWPanel( Canvas3DPanel c3dp )
    {
        nw = c3dp;
    }
    
    /**
     * @return the currently used Canvas3DPanel for north-east
     */
    public Canvas3DPanel getNEPanel()
    {
        return ( ne );
    }
    
    /**
     * Sets the new Canvas3DPanel for north-east
     * 
     * @param c3dp new new Canvas3DPanel for north-east
     */
    public void setNEPanel( Canvas3DPanel c3dp )
    {
        ne = c3dp;
    }
    
    /**
     * @return the currently used Canvas3DPanel for south-west
     */
    public Canvas3DPanel getSWPanel()
    {
        return ( sw );
    }
    
    /**
     * Sets the new Canvas3DPanel for south-west
     * 
     * @param c3dp new new Canvas3DPanel for south-west
     */
    public void setSWPanel( Canvas3DPanel c3dp )
    {
        sw = c3dp;
    }
    
    /**
     * @return the currently used Canvas3DPanel for south-east
     */
    public Canvas3DPanel getSEPanel()
    {
        return ( se );
    }
    
    /**
     * Sets the new Canvas3DPanel for south-east
     * 
     * @param c3dp new new Canvas3DPanel for south-east
     */
    public void setSEPanel( Canvas3DPanel c3dp )
    {
        se = c3dp;
    }
    
    /**
     * Sets the divider position between nw|ne and sw|se
     * 
     * @param pos the new divider position
     */
    public void setHorizontalDividerPos( int pos )
    {
        northSP.setDividerLocation( pos );
        southSP.setDividerLocation( pos );
    }
    
    /**
     * @return the divider position between nw|ne and sw|se
     */
    public int getHorizontalDividerPos()
    {
        return ( northSP.getDividerLocation() );
    }
    
    /**
     * Sets the divider position between north and south
     * 
     * @param pos the new divider position
     */
    public void setVerticalDividerPos( int pos )
    {
        northSouthSP.setDividerLocation( pos );
    }
    
    /**
     * @return the divider position between north and south
     */
    public int getVerticalDividerPos()
    {
        return ( northSouthSP.getDividerLocation() );
    }
    
    /**
     * Sets the weight of space between nw|ne and sw|se
     * 
     * @param value the new weight
     */
    public void setHorizontalResizeWeight( double value )
    {
        northSP.setResizeWeight( value );
        southSP.setResizeWeight( value );
    }
    
    /**
     * @return the weight of space between nw|ne and sw|se
     */
    public double getHorizontalResizeWeight()
    {
        return ( northSP.getResizeWeight() );
    }
    
    /**
     * Sets the weight of space between north and south
     * 
     * @param value the new weight
     */
    public void setVerticalResizeWeight( double value )
    {
        northSouthSP.setResizeWeight( value );
    }
    
    /**
     * @return the weight of space between north and south
     */
    public double getVerticalResizeWeight()
    {
        return ( northSouthSP.getResizeWeight() );
    }
    
    /**
     * Sets the size of the dividers
     * 
     * @param newSize the new size of the dividers
     */
    public void setDividerSize( int newSize )
    {
        northSP.setDividerSize( newSize );
        southSP.setDividerSize( newSize );
        northSouthSP.setDividerSize( newSize );
    }
    
    /**
     * @return the size of the dividers
     */
    public int getDividerSize()
    {
        return ( northSouthSP.getDividerSize() );
    }
    
    /**
     * Creates a new FourViewsPanel
     * 
     * @param nw the Canvas3DPanel to display at north-west
     * @param ne the Canvas3DPanel to display at north-east
     * @param sw the Canvas3DPanel to display at south-west
     * @param se the Canvas3DPanel to display at south-east
     */
    public FourViewsPanel( Canvas3DPanel nw, Canvas3DPanel ne, Canvas3DPanel sw, Canvas3DPanel se )
    {
        super( new GridLayout( 1, 1 ) );
        
        setNWPanel( nw );
        setNEPanel( ne );
        setSWPanel( sw );
        setSEPanel( se );
        
        DividerSyncer ds = new DividerSyncer();
        
        northSP = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, getNWPanel(), getNEPanel() );
        northSP.setContinuousLayout( true );
        northSP.setOneTouchExpandable( true );
        northSP.addPropertyChangeListener( ds );
        southSP = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, getSWPanel(), getSEPanel() );
        southSP.setContinuousLayout( true );
        southSP.addPropertyChangeListener( ds );
        northSouthSP = new JSplitPane( JSplitPane.VERTICAL_SPLIT, northSP, southSP );
        northSouthSP.setContinuousLayout( true );
        northSouthSP.setOneTouchExpandable( true );
        
        if ( !getNWPanel().isMinimumSizeSet() )
            getNWPanel().setMinimumSize( new Dimension( 0, 0 ) );
        if ( !getNEPanel().isMinimumSizeSet() )
            getNEPanel().setMinimumSize( new Dimension( 0, 0 ) );
        if ( !getSWPanel().isMinimumSizeSet() )
            getSWPanel().setMinimumSize( new Dimension( 0, 0 ) );
        if ( !getSEPanel().isMinimumSizeSet() )
            getSEPanel().setMinimumSize( new Dimension( 0, 0 ) );
        
        setHorizontalResizeWeight( 0.5 );
        setVerticalResizeWeight( 0.5 );
        
        this.add( northSouthSP, null );
    }
    
    /**
     * Creates a new FourViewsPanel
     * 
     * The four needed Canvas3DPanels are created automatically with its empty constructor
     * and can be retrieved by getNWPanel() and the like.
     */
    public FourViewsPanel( OpenGLLayer layer )
    {
        this( new Canvas3DPanel( layer ), new Canvas3DPanel( layer ), new Canvas3DPanel( layer ), new Canvas3DPanel( layer ) );
        
        getNWPanel().setWireframeMode( true );
        getNEPanel().setWireframeMode( true );
        getSWPanel().setWireframeMode( true );
        getSEPanel().setWireframeMode( false );
    }
    
    /**
     * Creates a new FourViewsPanel
     * 
     * The four needed Canvas3DPanels are created automatically with its empty constructor
     * and can be retrieved by getNWPanel() and the like.
     */
    public FourViewsPanel()
    {
        this( OpenGLLayer.getDefault().isJOGL() ? OpenGLLayer.JOGL_AWT : OpenGLLayer.LWJGL_AWT );
    }
}
