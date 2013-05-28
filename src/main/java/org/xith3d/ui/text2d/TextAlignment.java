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

/**
 * Constants for text-alignment
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public enum TextAlignment
{
    //NO_ALIGN,
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER_CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT,
    ;
    
    /**
     * Checks wheather this alignment is a horizontaly left alignment
     */
    public boolean isLeftAligned()
    {
        return ( ( this == TOP_LEFT ) || ( this == CENTER_LEFT ) || ( this == BOTTOM_LEFT ) );
    }
    
    /**
     * Checks wheather this alignment is a horizontaly centered alignment
     */
    public boolean isHCenterAligned()
    {
        return ( ( this == TOP_CENTER ) || ( this == CENTER_CENTER ) || ( this == BOTTOM_CENTER ) );
    }
    
    /**
     * Checks wheather this alignment is a horizontaly right alignment
     */
    public boolean isRightAligned()
    {
        return ( ( this == TOP_RIGHT ) || ( this == CENTER_RIGHT ) || ( this == BOTTOM_RIGHT ) );
    }
    
    /**
     * Checks wheather this alignment is a vertically top alignment
     */
    public boolean isTopAligned()
    {
        return ( ( this == TOP_LEFT ) || ( this == TOP_CENTER ) || ( this == TOP_RIGHT ) );
    }
    
    /**
     * Checks wheather this alignment is a vertically centered alignment
     */
    public boolean isVCenterAligned()
    {
        return ( ( this == CENTER_LEFT ) || ( this == CENTER_CENTER ) || ( this == CENTER_RIGHT ) );
    }
    
    /**
     * Checks wheather this alignment is a vertically bottom alignment
     */
    public boolean isBottomAligned()
    {
        return ( ( this == BOTTOM_LEFT ) || ( this == BOTTOM_CENTER ) || ( this == BOTTOM_RIGHT ) );
    }
}
