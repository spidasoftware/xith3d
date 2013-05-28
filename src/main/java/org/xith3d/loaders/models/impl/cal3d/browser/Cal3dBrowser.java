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
package org.xith3d.loaders.models.impl.cal3d.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;

import org.openmali.vecmath2.Colorf;

import org.xith3d.loaders.models.impl.cal3d.Cal3dLoader;
import org.xith3d.loaders.models.impl.cal3d.Cal3dModel;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.OpenGLLayer;

import org.xith3d.scenegraph.AmbientLight;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.PointLight;

/**
 *
 * @author kman
 */
public class Cal3dBrowser {
    
    int width = 800, height = 600;
    
    // Swing frame
    private SwingFrame frame;
    
    public Cal3dBrowser() {
        frame = new SwingFrame();
        // center the frame
        frame.setLocationRelativeTo(null);
        // show frame
        frame.setVisible(true);
    }
    
    /**
     * Main Entry point...
     *
     * @param args
     *            String[]
     */
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Cal3dBrowser();
    }
    
    // **************** SWING FRAME ****************
    
    // Our custom Swing frame... Nothing really special here.
    class SwingFrame extends JFrame {
        private static final long serialVersionUID = 1L;
        
        JPanel contentPane;
        JPanel mainPanel = new JPanel();
        Component comp = null;
        JButton coolButton = new JButton();
        JButton uncoolButton = new JButton();
        JPanel spPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JTree jTree1 = new JTree();
        JCheckBox scaleBox = new JCheckBox("Scale GL Image");
        JPanel colorPanel = new JPanel();
        JLabel colorLabel = new JLabel("BG Color:");
        MyImplementor impl;
        
        // Construct the frame
        public SwingFrame() {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });
            
            init();
            //pack();
            setSize(800,600);
            doJob();
            
            // MAKE SURE YOU REPAINT SOMEHOW OR YOU WON'T SEE THE UPDATES...
            new Thread("MyImplementor update") {
                { setDaemon(true); }
                @Override
                public void run() {
                    while (true) {
                        impl.update();
                    }
                }
            }.start();
            
            
        }
        
        // Component initialization
        private void init() {
            contentPane = (JPanel) this.getContentPane();
            contentPane.setLayout(new BorderLayout());
            mainPanel.setLayout(new GridBagLayout());
            setTitle("CAL3D Animation Browser");
            
            JPanel panel = new JPanel();
            impl = new MyImplementor(width, height, this, panel);
            
            contentPane.add(panel, BorderLayout.CENTER);
            
            impl.update();
        }
        
        public void doJob(){
        	
            AnimationBrowser br = new AnimationBrowser((impl).getModel());
            
            getContentPane().add(br,BorderLayout.WEST);
            repaint();
            validate();
            
        }
        
        // Overridden so we can exit when window is closed
        @Override
        protected void processWindowEvent(WindowEvent e) {
            super.processWindowEvent(e);
            if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                System.exit(0);
            }
        }
    }
    
    
    // IMPLEMENTING THE SCENE:
    
    class MyImplementor extends Xith3DEnvironment {
        Cal3dModel amodel;

        long startTime = 0;
        long fps = 0;
        SwingFrame frame;

        public Cal3dModel getModel(){
            return amodel;
        }
        public MyImplementor(int width, int height,SwingFrame fr, JPanel panel) {
            super();
            addCanvas(Canvas3DFactory.create(OpenGLLayer.JOGL_AWT, width, height, DisplayMode.WINDOWED, panel));
            frame = fr;
            simpleSetup();
        }
        
        public void simpleSetup() {
            
            BranchGroup scene = addPerspectiveBranch().getBranchGroup();
            
        	AmbientLight aLight = new AmbientLight(new Colorf(0.3f, 0.3f, 0.3f));
            scene.addChild(aLight);
        	
        	PointLight light = new PointLight();
        	light.setColor(1f, 1f, 1f);
            light.setLocation(50f, 50f, 50f);
            light.setAttenuation(0.0005f, 0.0005f, 0.0005f);
            scene.addChild(light);

            try {
                amodel = (Cal3dModel)new Cal3dLoader().loadModel("/home/bluesky/workspace/stratagem/data/models/units/fantassin/cal3d/Fantassin.cfg");
            } catch (Exception e) {
                e.printStackTrace();
            }
            scene.addChild(amodel);
            
            startTime = System.currentTimeMillis() + 5000L;
            
            amodel.getInternalModel().setLodLevel(1f);
            
            getView().lookAt(-5f, -5f, 5f,
                             0f, 0f, 0f,
                             0f, 0f, 1f
                            );
        };
        
        public void update() {
           
        	amodel.updateController(0.02f);
        	
        	render();
        }
    }
}