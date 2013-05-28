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
package org.xith3d.render.config;

/**
 * An enumeration of all posiible full scene anti aliasing modes.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public enum FSAA
{
    OFF( 0 ),
    ON_2X( 2 ),
    ON_4X( 4 ),
    ON_8X( 8 ),
    ON_16X( 16 );
    
    private int intValue;
    
    /**
     * @return the int representation of this FSAA mode
     */
    public int getIntValue()
    {
        return ( intValue );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        if ( this == OFF )
            return ( "OFF" );
        
        return ( getIntValue() + "x" );
    }
    
    private FSAA( int intValue )
    {
        this.intValue = intValue;
    }
    
    public static final FSAA getFromFactor( int factor )
    {
        switch ( factor )
        {
            case 0:
                return ( OFF );
            case 2:
                return ( ON_2X );
            case 4:
                return ( ON_4X );
            case 8:
                return ( ON_8X );
            case 16:
                return ( ON_16X );
        }
        
        throw new IllegalArgumentException( "unsupported FSAA factor" );
    }
}
