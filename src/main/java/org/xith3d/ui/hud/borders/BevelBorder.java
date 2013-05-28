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
package org.xith3d.ui.hud.borders;

import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.Border;

/**
 * A BevelBorder is a TexturedBorder extension, that makes explizit use of
 * HUD.getTheme().getLoweredBevelBorderDescription() to retrieve its
 * Border.Description.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class BevelBorder extends TexturedBorder
{
    public static enum Type
    {
        /**
         * This indicates a lowered BevelBorder.<br>
         * This is a 3d effect of a sunken area inside the border.
         */
        LOWERED,
        
        /**
         * This indicates a raised BevelBorder.<br>
         * This is a 3d effect of a raised area inside the border.
         */
        RAISED,
        ;
    }
    
    /**
     * @see Type#LOWERED
     */
    public static final Type LOWERED = Type.LOWERED;
    
    /**
     * @see Type#RAISED
     */
    public static final Type RAISED = Type.RAISED;
    
    private final Type type;
    
    /**
     * @return the BevelBorder.Type (LOWERED or RAISED).
     */
    public final Type getType()
    {
        return ( type );
    }
    
    private static Border.Description getBorderDesc( Type type )
    {
        if ( type == null )
            throw new IllegalArgumentException( "You must set a non-null-type." );
        
        switch ( type )
        {
            case LOWERED:
                return ( HUD.getTheme().getLoweredBevelBorderDescription() );
                
            case RAISED:
                return ( HUD.getTheme().getRaisedBevelBorderDescription() );
        }
        
        throw new Error( "This line should never be executed." );
    }
    
    /**
     * Creates a new BevelBorder Widget.
     * 
     * @param type
     */
    public BevelBorder( Type type )
    {
        super( getBorderDesc( type ) );
        
        this.type = type;
    }
}
