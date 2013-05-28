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
package org.xith3d.utility.hud.editor;

import org.openmali.types.twodee.Dim2f;
import org.openmali.vecmath2.Point2f;

import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.AbstractButton;
import org.xith3d.ui.hud.listeners.ButtonListener;
import org.xith3d.ui.hud.widgets.Button;
import org.xith3d.ui.hud.widgets.Frame;
import org.xith3d.ui.hud.widgets.Panel;
import org.xith3d.utility.hud.editor.info.HEInfo_Widget;

/**
 * A HUD editor
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class HUDEditor implements ButtonListener {

    private Panel panel;

    /**
     * Creates a new HUD editor on this panel
     * 
     * @param panel 
     */
    public HUDEditor(Panel panel) {

        this.panel = panel;
        
        if(panel.getHUD() == null) {
            throw new Error("You should add your panel to a HUD before" +
                        " initiating a HUDEditor on it !");
        }
        
        HUD hud = panel.getHUD();

        Dim2f frameSize = new Dim2f(hud.getResX() / 5f, hud.getResY() / 1.5f);        
        Frame frame = new Frame(frameSize.getWidth(), frameSize.getHeight(), "Widgets");

        Dim2f size = new Dim2f(frameSize.getWidth() - 4f, frameSize.getHeight() / 7f);
        final float yStep = size.getHeight();
        float yPos = 0;
        ((Button) frame.getContentPane().addWidget(new Button(size.getWidth(), size.getHeight(), "Label"),
                0f, yPos)).addButtonListener(this);
        ((Button) frame.getContentPane().addWidget(
                new Button(size.getWidth(), size.getHeight(), "DynamicLabel"), 0f, yPos += yStep))
                .addButtonListener(this);
        ((Button) frame.getContentPane().addWidget(new Button(size.getWidth(), size.getHeight(), "Image"),
                0f, yPos += yStep)).addButtonListener(this);
        ((Button) frame.getContentPane().addWidget(new Button(size.getWidth(), size.getHeight(), "Button"),
                0f, yPos += yStep)).addButtonListener(this);
        ((Button) frame.getContentPane().addWidget(
                new Button(size.getWidth(), size.getHeight(), "TextField"), 0f, yPos += yStep))
                .addButtonListener(this);

        hud.addWindow(frame, hud.getWidth() - frame.getWidth() - 10f, 10f);
        frame.setCloseButtonVisible(false);
        frame.setVisible(true);

    }

    public void onButtonClicked(AbstractButton button, Object userObject) {

        try {
            HEInfo_Widget widget = HEInfo_Widget.newWidget(((Button)button).getText(),
                    new Point2f(100f, 100f), new Dim2f(100f, 50f));
            panel.addWidget(widget.getWidget());
            new WidgetManipulator(widget.getWidget());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
