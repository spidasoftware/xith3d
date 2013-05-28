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
package org.xith3d.ui.hud.menusystem;

/**
 * A {@link Menu} can be added to a {@link MenuGroup} instance.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface Menu
{
    /**
     * @return this {@link Menu}'s unique name.
     */
    public String getName();
    
    /**
     * This method is used by the {@link MenuSystem} to populate itself to the {@link Menu}.
     * 
     * @param menuSystem
     */
    public void setMenuSystem( MenuSystem menuSystem );
    
    /**
     * @return the {@link MenuSystem}, this {@link Menu} belongs to.
     */
    public MenuSystem getMenuSystem();
    
    /**
     * This method is used by the {@link MenuGroup} to populate itself to the {@link Menu}.
     * 
     * @param menuGroup
     */
    public void setMenuGroup( MenuGroup menuGroup );
    
    /**
     * @return the {@link MenuGroup}, this {@link Menu} belongs to.
     */
    public MenuGroup getMenuGroup();
    
    /**
     * This method is called my the MenuGroup prepare an action and
     * to ask the current active Menu, if it accepts the given action.
     * 
     * @param action
     * 
     * @return true, if the action is accepted
     */
    public boolean prepareAction( String action );
    
    /**
     * This event notifies a {@link Menu}, that an action has been consumed
     * in user-space.
     * 
     * @param action
     * 
     * @return true, of the consume was accepted.
     */
    public boolean onActionConsumed( String action );
}
