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
package org.xith3d.utility.physics.pseudo.joints;

import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * A HingeJoint permits you to constraints some objects to
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class HingeJoint extends Joint {
    
    // What we need to do our job.. and to do it well
    private Transform rotationTrans;
    private Vector3f rotationAxis;
    private Transform translationTrans;
    private Vector3f translationAxis;
    private float angle;
    private float length;
    
    /**
     * Creates a new HingeJoint
     * 
     * @param rotationAxis
     *                Axis about which the attached object will rotate
     * @param translationAxis
     *                Axis about which the object is tied to the joint
     * @param length
     *                Length of the joint (where/how far should the next joint
     *                begin?)
     */
    public HingeJoint(Vector3f rotationAxis, Vector3f translationAxis,
        float length) {
        
        this(rotationAxis, translationAxis, length, 0f);
        
    }
    
    /**
     * Creates a new HingeJoint
     * 
     * @param rotationAxis
     *                Axis about which the attached object will rotate
     * @param translationAxis
     *                Axis about which the object is tied to the joint
     * @param length
     *                Length of the joint (where/how far should the next joint
     *                begin?)
     * @param angle
     *                Initial angle of the HingeJoint
     */
    public HingeJoint(Vector3f rotationAxis, Vector3f translationAxis,
        float length, float angle) {
        
        super();
        
        this.rotationAxis = rotationAxis;
        this.translationAxis = translationAxis;
        this.length = length;
        this.angle = angle;
        
        rotationTrans = new Transform();
        translationTrans = new Transform();
        this.addChild(rotationTrans);
        rotationTrans.addChild(translationTrans);
        
        update();
        
    }
    
    private void update() {
        
        translationTrans.setAxisTranslation(translationAxis, length);
        rotationTrans.setAxisRotation(rotationAxis, angle);
        
    }
    
    /**
     * @return the display group, to which you can add some Shapes for your
     *         joint to be visible.
     */
    public Group getStartGroup() {
        
        return rotationTrans;
        
    }
    
    /**
     * @return the display group, to which you can add some Shapes for your
     *         joint to be visible.
     */
    public Group getEndGroup() {
        
        return translationTrans;
        
    }
    
    /**
     * Add a child joint to this one
     * @param j The new joint to be added
     */
    @Override
    public void addChild(Joint j) {
        
        translationTrans.addChild(j);
        
    }
    
    /**
     * Remove a child joint from this one
     * @param j The joint to be removed from
     */
    @Override
    public void removeChild(Joint j) {
        
        translationTrans.removeChild(j);
        
    }
    
    public Vector3f getRotationAxis() {
        return rotationAxis;
    }
    
    public void setRotationAxis(Vector3f rotationAxis) {
        this.rotationAxis = rotationAxis;
        update();
    }
    
    public Vector3f getTranslationAxis() {
        return translationAxis;
    }
    
    public void setTranslationAxis(Vector3f translationAxis) {
        this.translationAxis = translationAxis;
        update();
    }
    
    public float getAngle() {
        return angle;
    }
    
    public void setAngle(float angle) {
        this.angle = angle;
        update();
    }
    
    public float getLength() {
        return length;
    }
    
    public void setLength(float length) {
        this.length = length;
        update();
    }
    
    public void addAngle(float angle) {
        setAngle(this.angle + angle);
    }
    
    public void subAngle(float angle) {
        setAngle(this.angle - angle);
    }
    
    public void addLength(float length) {
        setLength(this.length + length);
    }
    
    public void subLength(float length) {
        setLength(this.length - length);
    }
    
    /**
     * @see org.xith3d.scenegraph.Node#traverse(org.xith3d.scenegraph.traversal.DetailedTraversalCallback)
     */
    @Override
    public boolean traverse(DetailedTraversalCallback callback) {
        
        return rotationTrans.traverse(callback);
        
    }
    
}
