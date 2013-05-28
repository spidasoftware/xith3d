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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;

/**
 * This is a Shape3D extension, that allows you to use multiple Geometries and
 * Appearances for, of which only one set is active at a time.<br>
 * This is comparable to a Switch node, but is more efficient for this task.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MultiShape3D extends Shape3D
{
    private ArrayList< Geometry > geometries = new ArrayList< Geometry >();
    private ArrayList< Appearance > appearances = new ArrayList< Appearance >();
    
    private HashMap< String, Integer > mapName2Unit = new HashMap< String, Integer >();
    private ArrayList< String > mapUnit2Name = new ArrayList< String >();
    private List< String > unmodMapUnit2Name = Collections.unmodifiableList( mapUnit2Name );
    
    private int activeUnit = -1;
    
    /**
     * @return an unmodifiable List of all available units in this MultiShape3D.
     */
    public final List< String > getUnits()
    {
        return ( unmodMapUnit2Name );
    }
    
    /**
     * @return the number of units in this MultiShape3D.
     */
    public final int getNumberOfUnits()
    {
        return ( mapUnit2Name.size() );
    }
    
    /**
     * @return the active unit's index
     */
    public final int getActiveUnit()
    {
        if ( activeUnit == -1 )
            return ( -1 );
        
        return ( activeUnit );
    }
    
    /**
     * @return the active unit's name
     */
    public final String getActiveUnitName()
    {
        if ( activeUnit == -1 )
            return ( null );
        
        return ( mapUnit2Name.get( activeUnit ) );
    }
    
    /**
     * Sets the active unit.
     * 
     * @param index
     */
    public void setActiveUnit( int index )
    {
        setGeometry( geometries.get( index ) );
        
        setAppearance( appearances.get( index ) );
        
        activeUnit = index;
    }
    
    /**
     * Sets the active unit.
     * 
     * @param name
     */
    public void setActiveUnit( String name )
    {
        setActiveUnit( mapName2Unit.get( name ) );
    }
    
    /**
     * Adds a named Shape-unit to the MultiShape3D.
     * 
     * @param name
     * @param geometry
     * @param appearance
     */
    public void addUnit( String name, Geometry geometry, Appearance appearance )
    {
        mapName2Unit.put( name, new Integer( getNumberOfUnits() ) );
        
        geometries.add( geometry );
        appearances.add( appearance );
        
        geometry.setModListener( getModListener() );
        appearance.setModListener( getModListener() );
        
        if ( ( getGeometry() == null ) || ( getAppearance() == null ) )
            setActiveUnit( name );
    }
    
    /**
     * Adds an unnamed Shape-unit to the MultiShape3D.
     * 
     * @param geometry
     * @param appearance
     */
    public void addUnit( Geometry geometry, Appearance appearance )
    {
        addUnit( Integer.toHexString( geometry.hashCode() ) + "-" + Integer.toHexString( appearance.hashCode() ), geometry, appearance );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setModListener( ScenegraphModificationsListener modListener )
    {
        super.setModListener( modListener );
        
        if ( getAppearance() != null )
            getAppearance().setModListener( this.getModListener() );
        
        if ( getGeometry() != null )
            getGeometry().setModListener( this.getModListener() );
        
        for ( int i = 0; i < geometries.size(); i++ )
        {
            geometries.get( i ).setModListener( getModListener() );
        }
        
        for ( int i = 0; i < appearances.size(); i++ )
        {
            appearances.get( i ).setModListener( getModListener() );
        }
    }
    
    /**
     * Constructs a new Shape3D object with specified geometry and
     * appearance components.
     */
    public MultiShape3D( Geometry geometry, Appearance appearance )
    {
        super();
        
        this.setGeometry( geometry );
        this.setAppearance( appearance );
    }
    
    /**
     * Constructs a new Shape3D object with specified geometry component
     * and a null appearance component.
     */
    public MultiShape3D( Geometry geometry )
    {
        this( geometry, (Appearance)null );
    }
    
    /**
     * Constructs a new Shape3D object with a null geometry component
     * and a null appearance component.
     */
    public MultiShape3D()
    {
        this( (Geometry)null, (Appearance)null );
    }
}
