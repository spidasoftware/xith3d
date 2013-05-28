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
package org.xith3d.loaders.models.impl.cal3d;

import org.jagatoo.loaders.models.cal3d.Cal3dController;
import org.jagatoo.loaders.models.cal3d.core.CalCoreAnimation;
import org.jagatoo.loaders.models.cal3d.core.CalCoreMaterial;
import org.jagatoo.loaders.models.cal3d.core.CalMesh;
import org.jagatoo.loaders.models.cal3d.core.CalModel;
import org.jagatoo.loaders.models.cal3d.core.CalSubmesh;
import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.vecmath2.Colorf;

import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.animations.ModelAnimation;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Node;

/**
 * This class represents a Model loaded from some model file formats. This class
 * is responsible for both the storage and retrieval of data from the Model. The
 * storage methods (used only by Loader writers) are all of the add*() routines.
 * The retrieval methods (used primarily by Loader users) are all of the get*()
 * routines.
 * 
 * @author Dave Lloyd
 * @author kman
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Cal3dModel extends Model {
    private CalModel calModel;
    private Cal3dController controller;
    private float fps = 25f;
    private Appearance appearance;
    
    public CalModel getInternalModel() {
        return (calModel);
    }
    
    public Cal3dController getCalController() {
        return (controller);
    }
    
    /**
     * @return The number of frames per second. It is used for time
     *         computations.
     */
    public float getFps() {
        return fps;
    }
    
    /**
     * Set the number of frames per second. It is used for time computations.
     * 
     * @param fps
     */
    public void setFps(float fps) {
        this.fps = fps;
    }
    
    /**
     * time = frameIndex / fps
     * @param frameIndex
     * @return
     */
    @SuppressWarnings( "unused" )
    private float frameIndexToTime(int frameIndex) {
        return ((float) frameIndex) / fps;
    }

    /**
     * frameIndex = time * fps
     * @param time
     * @return
     */
    @SuppressWarnings( "unused" )
    private int timeToFrameIndex(float time) {
        return (int) (time * fps);
    }
    
    public void updateController(float time) {
        
        calModel.update(time);
        
        final int numChildren = this.numChildren();
        for (int i = 0; i < numChildren; i++)
        {
            final Node spatial = this.getChild( i );
            
            if (spatial instanceof Cal3dSubmesh)
            {
                Cal3dSubmesh tmp = (Cal3dSubmesh)spatial;
                tmp.doUpdate( calModel );
                tmp.updateBounds( false );
            }
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void interpolateAnimation(float animStartTime, float absAnimTime) {
        
        if ( getCurrentAnimation() != null ) {
            calModel.getMixer().clearAllAnims();
            calModel.getMixer().scrubToTime(getCurrentAnimation().getName(), absAnimTime);
            updateController(1000f);
        }

    }

    /**
     * @return the appearance of this model, or the last one
     * if it has several meshes
     */
    public Appearance getAppearance() {
        
        return appearance;
        
    }
    
    private void initializeNode() {
        int myCounter = 0;
        for (CalMesh mesh : calModel.getMeshes()) {
            for (CalSubmesh submesh : mesh.getSubmeshes()) {
                Cal3dSubmesh xithMesh = new Cal3dSubmesh("mesh : " + myCounter, submesh);
                myCounter++;
                if ((mesh.getCoreMesh().skin != null) && !mesh.getCoreMesh().skin.equals("")) {
                    xithMesh.getAppearance().setTexture(0, mesh.getCoreMesh().skin);
                }
                if ((mesh.getCoreMesh().material != null) && !mesh.getCoreMesh().material.equals("")) {
                    CalCoreMaterial mat = calModel.getCoreModel().getCoreMaterial(mesh.getCoreMesh().material.toExternalForm());
                    Colorf ambient = mat.getAmbientColor();
                    Colorf diffuse = mat.getDiffuseColor();
                    Colorf specular = mat.getSpecularColor();
                    float shine = mat.getShininess();
                    Material mstate = new Material();
                    mstate.setShininess(shine);
                    mstate.setAmbientColor(ambient.getRed(), ambient.getGreen(), ambient.getBlue());
                    mstate.setDiffuseColor(diffuse.getRed(), diffuse.getGreen(), diffuse.getBlue());
                    mstate.setSpecularColor(specular.getRed(), specular.getGreen(), specular.getBlue());
                    mstate.setEmissiveColor(0f, 0f, 0f);
                    xithMesh.getAppearance().setMaterial(mstate);
                }
                xithMesh.setBounds(new BoundingBox());
                xithMesh.updateBounds(false);
                addChild(xithMesh);
                appearance = xithMesh.getAppearance();
            }
        }
    }
    
    protected void init(CalModel calModel) {
        this.calModel = calModel;
        
        initializeNode();
        
        ModelAnimation[] anims = new ModelAnimation[calModel.getCoreModel().getCoreAnimations().size()];
        int i = 0;
        for (CalCoreAnimation calAnim : calModel.getCoreModel().getCoreAnimations().values()) {
            anims[i++] = new ModelAnimation(calAnim.getName(), (int)(calAnim.getDuration() * fps), fps, null, calAnim);
        }
        setAnimations(anims);
        
        this.controller = new Cal3dController(calModel);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Cal3dModel getSharedInstance() {
        Cal3dModel copy = new Cal3dModel();
        copy.setName( this.getName() );
        copy.init(calModel);
        
        return ( copy );
    }
    
    protected Cal3dModel() {
        super();
    }
}
