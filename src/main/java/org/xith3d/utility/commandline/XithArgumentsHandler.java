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

import org.jagatoo.commandline.Argument;
import org.jagatoo.commandline.ArgumentsHandler;
import org.jagatoo.commandline.CommandlineParsingException;
import org.jagatoo.commandline.arguments.HelpArgument;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.utility.commandline.arguments.DisplayModeArgument;
import org.xith3d.utility.commandline.arguments.FSAAArgument;
import org.xith3d.utility.commandline.arguments.FullscreenModeArgument;
import org.xith3d.utility.commandline.arguments.MaxFPSArgument;
import org.xith3d.utility.commandline.arguments.MouseYInvertArgument;
import org.xith3d.utility.commandline.arguments.OpenGLLayerArgument;
import org.xith3d.utility.commandline.arguments.VSyncArgument;

/**
 * This is an {@link ArgumentsHandler}, that handles Xith3D arguments.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class XithArgumentsHandler extends ArgumentsHandler
{
    private BasicApplicationArguments arguments = new BasicApplicationArguments();
    
    private boolean helpRequested = false;
    
    /**
     * Gets the arguments capsule.
     * 
     * @return the arguments capsule.
     */
    public final BasicApplicationArguments getArguments()
    {
        return ( arguments );
    }
    
    public final boolean helpRequested()
    {
        return ( helpRequested );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void handleArgument( Argument arg, Object value )
    {
        if ( arg == OpenGLLayerArgument.INSTANCE )
            arguments.setOpenGLLayer( (OpenGLLayer)value );
        else if ( arg == DisplayModeArgument.INSTANCE )
            arguments.displayMode = (int[])value;
        else if ( arg == FullscreenModeArgument.FULLSCREEN_INSTANCE )
            arguments.setFullscreenMode( FullscreenMode.FULLSCREEN );
        else if ( arg == FullscreenModeArgument.WINDOWED_INSTANCE )
            arguments.setFullscreenMode( FullscreenMode.WINDOWED );
        else if ( arg == FullscreenModeArgument.WINDOWED_UNDECORATED_INSTANCE )
            arguments.setFullscreenMode( FullscreenMode.WINDOWED_UNDECORATED );
        else if ( arg == VSyncArgument.TRUE_INSTANCE )
            arguments.setVSync( true );
        else if ( arg == VSyncArgument.FALSE_INSTANCE )
            arguments.setVSync( false );
        else if ( arg == FSAAArgument.INSTANCE )
            arguments.setFSAA( (FSAA)value );
        else if ( arg == MouseYInvertArgument.TRUE_INSTANCE )
            arguments.setMouseYInverted( true );
        else if ( arg == MouseYInvertArgument.FALSE_INSTANCE )
            arguments.setMouseYInverted( false );
        else if ( arg == MaxFPSArgument.LIIMITED_INSTANCE )
            arguments.setMaxFPS( (Float)value );
        else if ( arg == MaxFPSArgument.UNLIIMITED_INSTANCE )
            arguments.setMaxFPS( null );
        else if ( arg == HelpArgument.INSTANCE )
            this.helpRequested = true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void validate() throws CommandlineParsingException
    {
    }
}
