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
package org.xith3d.ui.text2d;

import java.awt.Font;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple2f;

import org.xith3d.scenegraph.MultiShape3D;

/**
 * This Character2D can contain multiple Character2Ds at a time through a
 * MultiShape3D.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class MultiCharacter2D extends MultiShape3D
{
    private Colorf color;
    private Character2D[] character2Ds;
    
    /**
     * Sets the active char for this MultiCharacter2D.
     * 
     * @param ch
     */
    public void setActiveChar( char ch )
    {
        super.setActiveUnit( String.valueOf( ch ) );
    }
    
    /**
     * @return the active char of this MultiCharacter2D.
     */
    public char getActiveChar()
    {
        return ( super.getActiveUnitName().charAt( 0 ) );
    }
    
    public Colorf getColor()
    {
        return ( color );
    }
    
    public Tuple2f getSize()
    {
        return ( character2Ds[ getActiveUnit() ].getSize() );
    }
    
    public float getWidth()
    {
        return ( character2Ds[ getActiveUnit() ].getWidth() );
    }
    
    public float getHeight()
    {
        return ( character2Ds[ getActiveUnit() ].getHeight() );
    }
    
    public MultiCharacter2D( char[] characters, Colorf color, Font font )
    {
        super();
        
        this.character2Ds = new Character2D[ characters.length ];
        
        for ( int i = 0; i < characters.length; i++ )
        {
            character2Ds[ i ] = Character2D.loadCharacter( characters[ i ], color, font );
            
            super.addUnit( String.valueOf( characters[ i ] ), character2Ds[ i ].getGeometry(), character2Ds[ i ].getAppearance() );
        }
    }
}
