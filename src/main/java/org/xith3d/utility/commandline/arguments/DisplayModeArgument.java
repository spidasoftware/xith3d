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
package org.xith3d.utility.commandline.arguments;

import org.jagatoo.commandline.Argument;
import org.jagatoo.commandline.CommandlineParsingException;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class DisplayModeArgument extends Argument
{
    public static final DisplayModeArgument INSTANCE = new DisplayModeArgument();
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected int[] parseValueImpl( String rawValue ) throws CommandlineParsingException
    {
        String[] parts = rawValue.split( "x" );
        
        if ( parts.length < 2 )
            throw new CommandlineParsingException( "Invalid value for " + this + ": " + rawValue );
        
        int[] result = new int[ Math.min( parts.length, 4 ) ];
        
        result[0] = Integer.parseInt( parts[0] );
        result[1] = Integer.parseInt( parts[1] );
        
        if ( ( result[0] > 4096 ) || ( result[1] > 4096 ) )
            throw new CommandlineParsingException( "Invalid value for " + this + ": " + rawValue );
        
        if ( parts.length >= 3 )
        {
            result[2] = Integer.parseInt( parts[2] );
            
            if ( ( result[2] != 8 ) && ( result[2] != 16 ) && ( result[2] != 24 ) && ( result[2] != 32 ) )
                throw new CommandlineParsingException( "Invalid value for " + this + ": " + rawValue );
        }
        
        if ( parts.length >= 4 )
            result[3] = Integer.parseInt( parts[3] );
        
        return ( result );
    }
    
    private DisplayModeArgument()
    {
        super( 'm', "display-mode", "Selects the display mode (resolution and color depth) in the following form:\n\nExample: 1024x768x32x60\n\nThe first two fields must be provided. The color depth can be omitted as well as the frequency.", true );
    }
}
