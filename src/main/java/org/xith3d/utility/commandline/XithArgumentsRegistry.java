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
package org.xith3d.utility.commandline;

import org.jagatoo.commandline.ArgumentsRegistry;
import org.jagatoo.commandline.arguments.HelpArgument;
import org.xith3d.utility.commandline.arguments.DisplayModeArgument;
import org.xith3d.utility.commandline.arguments.FSAAArgument;
import org.xith3d.utility.commandline.arguments.FullscreenModeArgument;
import org.xith3d.utility.commandline.arguments.MaxFPSArgument;
import org.xith3d.utility.commandline.arguments.MouseYInvertArgument;
import org.xith3d.utility.commandline.arguments.OpenGLLayerArgument;
import org.xith3d.utility.commandline.arguments.VSyncArgument;

/**
 * Provides a static method, that fills all the standard arguments into an {@link ArgumentsRegistry}.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class XithArgumentsRegistry
{
    /**
     * Fills all the standard arguments into an {@link ArgumentsRegistry}.
     * 
     * @param argReg
     */
    public static void addStandardArguments( ArgumentsRegistry argReg )
    {
        argReg.addArgument( OpenGLLayerArgument.INSTANCE );
        argReg.addArgument( DisplayModeArgument.INSTANCE );
        argReg.addArgument( FullscreenModeArgument.FULLSCREEN_INSTANCE );
        argReg.addArgument( FullscreenModeArgument.WINDOWED_INSTANCE );
        argReg.addArgument( FullscreenModeArgument.WINDOWED_UNDECORATED_INSTANCE );
        argReg.addArgument( VSyncArgument.TRUE_INSTANCE );
        argReg.addArgument( VSyncArgument.FALSE_INSTANCE );
        argReg.addArgument( FSAAArgument.INSTANCE );
        argReg.addArgument( MouseYInvertArgument.TRUE_INSTANCE );
        argReg.addArgument( MouseYInvertArgument.FALSE_INSTANCE );
        argReg.addArgument( MaxFPSArgument.LIIMITED_INSTANCE );
        argReg.addArgument( MaxFPSArgument.UNLIIMITED_INSTANCE );
        
        argReg.addArgument( HelpArgument.INSTANCE );
    }
    
    /**
     * Creates a new {@link ArgumentsRegistry} and fills all the standard arguments into it.
     * 
     * @return the filled {@link ArgumentsRegistry}.
     */
    public static ArgumentsRegistry createStandardArgumentsRegistry()
    {
        ArgumentsRegistry argReg = new ArgumentsRegistry( "Xith3D Standard Application" );
        
        addStandardArguments( argReg );
        
        return ( argReg );
    }
    
    /**
     * Dumps the valid arguments with descriptions.
     * 
     * @see ArgumentsRegistry#dump()
     */
    public static void dumpHelpForStandardArguments()
    {
        createStandardArgumentsRegistry().dump();
    }
    
    private XithArgumentsRegistry()
    {
    }
}
