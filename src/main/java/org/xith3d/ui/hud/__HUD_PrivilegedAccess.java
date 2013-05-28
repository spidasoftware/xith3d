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
package org.xith3d.ui.hud;

import org.xith3d.ui.hud.base.Widget;

/**
 * Since java doesn't correctly implement the protected modifier
 * and also doesn't provide necessary other modifiers, we need this class
 * to avoid exposing some internal methods to the Widgets' public APIs.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class __HUD_PrivilegedAccess
{
    public static final Widget focus( HUD hud, Widget widget )
    {
        return ( hud.focus( widget ) );
    }
    
    public static final Widget getCurrentFocusedWidget( HUD hud, boolean getLeaf )
    {
        return ( hud.getCurrentFocusedWidget( getLeaf ) );
    }
    
    public static final void bindMouseMovement( HUD hud, Widget widget )
    {
        hud.bindMouseMovement( widget );
    }
    
    public static void addVolatilePopup( HUD hud, Widget widget, Widget assembly, float locX, float locY )
    {
        hud.addVolatilePopup( widget, assembly, locX, locY );
    }
    
    public static void removeVolatilePopup( HUD hud )
    {
        hud.removeVolatilePopup();
    }
    
    public static final Widget getCurrentVolatilePopup( HUD hud )
    {
        return ( hud.getCurrentVolatilePopup() );
    }
    
    public static final void updateOperations( HUD hud, long nanoGameTime, long nanoFrameTime )
    {
        hud.updateOperations( nanoGameTime, nanoFrameTime );
    }
}
