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
package org.xith3d.ui.hud.layout;

import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetContainer;

/**
 * Similar to the AWT LayoutManager this interface is a base for
 * all Layout managers usable in a Xith3D HUD.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface LayoutManager
{
    /**
     * This is a constraint, that can be passed to the {@link WidgetContainer#addWidget(Widget)} methods
     * to tell it, that the Widget is not to be added to the LayoutManager.
     */
    public static final Object IGNORED_BY_LAYOUT = new Object();
    
    /**
     * If set to true, the {@link LayoutManager} hides invisible Widgets
     * and doesn't use them to calculate the other Widgets' layouts.<br>
     * <br>
     * Default: false
     * 
     * @param hidden
     */
    public void setInvisibleWidgetsHidden( boolean hidden );
    
    /**
     * If set to true, the {@link LayoutManager} hides invisible Widgets
     * and doesn't use them to calculate the other Widgets' layouts.<br>
     * <br>
     * Default: false
     */
    public boolean getInvisibleWidgetsHidden();
    
    /**
     * Adds a new Widget to this LayoutManager with the given constraints.
     * 
     * @param widget the Widget to add
     * @param constraints the constraints to use for this Widget in this LayoutManager
     */
    public void addWidget( Widget widget, Object constraints );
    
    /**
     * Removed a Widget from this LayoutManager
     * 
     * @param widget the Widget to be removed
     */
    public void removeWidget( Widget widget );
    
    /**
     * Clears the Widget List.
     */
    public void clear();
    
    /**
     * This method is invoked by the container, when it needs to be relayouted.
     * 
     * @param container the container this LayoutManager is attached to.
     */
    public void doLayout( WidgetContainer container );
}
