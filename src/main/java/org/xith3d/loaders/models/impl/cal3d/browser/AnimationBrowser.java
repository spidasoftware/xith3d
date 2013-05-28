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

import java.util.Collection;
import javax.swing.Box;

import org.jagatoo.loaders.models.cal3d.core.CalCoreAnimation;
import org.jagatoo.loaders.models.cal3d.core.CalCoreModel;
import org.xith3d.loaders.models.impl.cal3d.Cal3dModel;

/**
 *
 * @author  Dave
 */
public class AnimationBrowser extends javax.swing.JPanel {
    
	private static final long serialVersionUID = 5018525814303707429L;
    
	private Cal3dModel character;
    private javax.swing.JPanel animsPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel sceneViewPanel;
    private javax.swing.JCheckBox skeletonVisibleCheckBox;
    private javax.swing.JCheckBox skinVisibleCheckBox;
    
    /** Creates new form AnimationBrowser */
    public AnimationBrowser (Cal3dModel character) {
        this.character = character;
        initComponents ();
        createAnimControls ();
    }
    
    void createAnimControls () {
        CalCoreModel model = character.getInternalModel().getCoreModel();
        Collection<String> animIds = model.getCoreAnimationIds();
        
        for (String id: animIds) {
            CalCoreAnimation anim = model.getCoreAnimation(id);
            
            AnimControlPanel animControl = new AnimControlPanel(character, id, anim);
            
            animsPanel.add (animControl);
            
            // sceneView.canvas.addKeyListener (animControl.keyBinding);
        }
        
        animsPanel.add (Box.createVerticalGlue ());
    }
    
    private void initComponents () {
        jSplitPane1 = new javax.swing.JSplitPane ();
        sceneViewPanel = new javax.swing.JPanel ();
        jScrollPane1 = new javax.swing.JScrollPane ();
        animsPanel = new javax.swing.JPanel ();
        jToolBar1 = new javax.swing.JToolBar ();
        skeletonVisibleCheckBox = new javax.swing.JCheckBox ();
        skinVisibleCheckBox = new javax.swing.JCheckBox ();
        
        setLayout (new java.awt.BorderLayout ());
        
        sceneViewPanel.setLayout (new java.awt.BorderLayout ());
        
        jSplitPane1.setRightComponent (sceneViewPanel);
        
        animsPanel.setLayout (new javax.swing.BoxLayout (animsPanel, javax.swing.BoxLayout.Y_AXIS));
        
        jScrollPane1.setViewportView (animsPanel);
        
        jSplitPane1.setLeftComponent (jScrollPane1);
        
        add (jSplitPane1, java.awt.BorderLayout.CENTER);
        
        skeletonVisibleCheckBox.setSelected (true);
        skeletonVisibleCheckBox.setText ("Show Skeleton");
        skeletonVisibleCheckBox.addActionListener (new java.awt.event.ActionListener () {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
            skeletonVisibleCheckBoxActionPerformed (evt);
            }
        });
        
        jToolBar1.add (skeletonVisibleCheckBox);
        
        skinVisibleCheckBox.setSelected (true);
        skinVisibleCheckBox.setText ("Show Skin");
        skinVisibleCheckBox.addActionListener (new java.awt.event.ActionListener () {
            public void actionPerformed (java.awt.event.ActionEvent evt) {
            skinVisibleCheckBoxActionPerformed (evt);
            }
        });
        
        jToolBar1.add (skinVisibleCheckBox);
        
        //add(jToolBar1, java.awt.BorderLayout.NORTH);
        
    }
    
    private void skinVisibleCheckBoxActionPerformed (java.awt.event.ActionEvent evt) {
        //character.setSkinVisible (skinVisibleCheckBox.isSelected ());
    }
    
    private void skeletonVisibleCheckBoxActionPerformed (java.awt.event.ActionEvent evt) {
        //character.setSkeletonVisible (skeletonVisibleCheckBox.isSelected ());
    }
}
