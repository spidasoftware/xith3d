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

import java.io.File;

import org.jagatoo.loaders.models.cal3d.buffer.TexCoord2fBuffer;
import org.jagatoo.loaders.models.cal3d.buffer.Vector3fBuffer;
import org.jagatoo.loaders.models.cal3d.core.CalCoreMaterial;
import org.jagatoo.loaders.models.cal3d.core.CalModel;
import org.jagatoo.loaders.models.cal3d.core.CalSubmesh;
import org.jagatoo.loaders.models.cal3d.core.CalCoreSubmesh.Face;
import org.jagatoo.loaders.models.cal3d.util.BufferToArray;
import org.jagatoo.loaders.textures.locators.TextureStreamLocatorURL;
import org.openmali.vecmath2.Colorf;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.Material;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * @author Dave Lloyd
 * @author kman
 * @author Amos Wenger (aka BlueSky)
 */
public class Cal3dSubmesh extends Shape3D
{
    protected IndexedTriangleArray ta;
    
    protected CalSubmesh           subMesh;
    
    private int                    oldNumVertices;
    
    /** Creates a new instance of Cal3dSubmesh */
    public Cal3dSubmesh()
    {
        this("");
    }
    
    public Cal3dSubmesh(String name)
    {
        super();
        
        setName(name);
        this.setAppearance(new Appearance());
    }
    
    public Cal3dSubmesh(String name, CalSubmesh submesh)
    {
        this(name);
        
        this.subMesh = submesh;
        constructSubMesh();
    }
    
    public CalSubmesh getSubMesh()
    {
        return subMesh;
    }
    
    public void setSubMesh(CalSubmesh subMesh)
    {
        this.subMesh = subMesh;
        constructSubMesh();
    }
    
    private void constructSubMesh()
    {
        
        if (subMesh.hasInternalData())
        {
            Vector3fBuffer vertexBuffer2 = new Vector3fBuffer(subMesh.getVertexCount());
            Vector3fBuffer normalBuffer2 = new Vector3fBuffer(subMesh.getVertexCount());
            
            ta = new IndexedTriangleArray(vertexBuffer2.length, subMesh.getFaceIndices().size());
            ta.setOptimization(Optimization.NONE);
            this.setGeometry(ta);
            ta.setCoordinates(0, BufferToArray.array(vertexBuffer2.getBuffer()));
            ta.setNormals(0, BufferToArray.array(normalBuffer2.getBuffer()));
            ta.setIndex(BufferToArray.array(subMesh.getFaceIndices().getBuffer()));
        }
        else
        {
            @SuppressWarnings("unused")
            int numVertices = subMesh.getCoreSubmesh().getVertexCount();
            Vector3fBuffer vertexBuffer2 = subMesh.getCoreSubmesh().getVertexPositions();
            Vector3fBuffer normalBuffer2 = subMesh.getCoreSubmesh().getVertexNormals();
            TexCoord2fBuffer[] textBuffer = subMesh.getCoreSubmesh().getTextureCoordinates();
            int[] faces = new int[subMesh.getCoreSubmesh().getFaceCount() * 3];
            int cc = 0;
            for (int p = 0; p < subMesh.getCoreSubmesh().getFaceCount(); p++)
            {
                Face aface = subMesh.getCoreSubmesh().getVectorFace()[p];
                faces[cc] = aface.vertexId[0];
                cc++;
                faces[cc] = aface.vertexId[1];
                cc++;
                faces[cc] = aface.vertexId[2];
                cc++;
            }
            if (textBuffer == null || textBuffer.length == 0)
            {
                ta = new IndexedTriangleArray(vertexBuffer2.length, faces.length);
                ta.setOptimization(Optimization.NONE);
                this.setGeometry(ta);
                
                ta.setCoordinates(0, BufferToArray.array(vertexBuffer2.getBuffer()));
                ta.setNormals(0, BufferToArray.array(normalBuffer2.getBuffer()));
                ta.setIndex(faces);
            }
            else
            {
                ta = new IndexedTriangleArray(vertexBuffer2.length, faces.length);
                ta.setOptimization(Optimization.NONE);
                this.setGeometry(ta);
                
                ta.setCoordinates(0, BufferToArray.array(vertexBuffer2.getBuffer()));
                ta.setNormals(0, BufferToArray.array(normalBuffer2.getBuffer()));
                for (int i = 0; i < textBuffer.length; i++)
                {
                    ta.setTextureCoordinates(i, 0, 2, BufferToArray.array(textBuffer[i].getBuffer()));
                }
                ta.setIndex(faces);
            }
        }
        
        CalCoreMaterial mat = subMesh.getCoreMaterial();
        
        if (mat != null)
        {
            Colorf ambient = mat.getAmbientColor();
            Colorf diffuse = mat.getDiffuseColor();
            Colorf specular = mat.getSpecularColor();
            float shine = mat.getShininess();
            
            Material mstate = new Material();
            mstate.setShininess(shine);
            mstate.setAmbientColor(ambient.getRed(), ambient.getGreen(), ambient.getBlue());
            mstate.setDiffuseColor(diffuse.getRed(), diffuse.getGreen(), diffuse.getBlue());
            mstate.setSpecularColor(specular.getRed(), specular.getGreen(), specular.getBlue());
            getAppearance().setMaterial(mstate);
            
            if (mat.getMapCount() > 0)
            {
                String filename = mat.getMapFilename(0);
                while (filename.startsWith("/")) {
                    filename = filename.substring(1);
                }
                System.err.println("[Cal3dSubmesh] Texture filename = "+filename);
                Texture tex = null;
                // Do the file exist on the local filesystem ?
                File f = new File(filename);
                if(f.exists()) {
                	tex = TextureLoader.getInstance().getTexture(filename);
                }
                if(tex == null) {
                	// Try to read from Jar
                	/*String basePath = path.substring(0, path.lastIndexOf(File.separator));
                	String fileName = path.substring(path.lastIndexOf(File.separator));
                	System.out.println("Base path = "+basePath);
                	System.out.println("File name = "+fileName);*/
                	TextureStreamLocatorURL loc = new TextureStreamLocatorURL(/*Thread.currentThread().
                			getContextClassLoader().getResource(basePath)*/mat.getBaseURL());
                	TextureLoader.getInstance().addTextureStreamLocator(loc);
                	tex = TextureLoader.getInstance().getTexture(filename);
                	TextureLoader.getInstance().removeTextureStreamLocator(loc);
                }
                getAppearance().setTexture(tex);
            }
        }
    }
    
    public void doUpdate(CalModel calModel)
    {
        if (subMesh.hasInternalData())
        {
            Vector3fBuffer vertexBuffer2 = subMesh.getVertexPositions();
            Vector3fBuffer normalBuffer2 = subMesh.getVertexNormals();
            ta.setNormals(0, BufferToArray.array(normalBuffer2.getBuffer()));
            ta.setCoordinates(0, BufferToArray.array(vertexBuffer2.getBuffer()));
            setGeometry(ta);
            setBoundsDirty();
        }
        else
        {
            Vector3fBuffer vertexBuffer2 = new Vector3fBuffer(subMesh.getVertexCount());
            Vector3fBuffer normalBuffer2 = new Vector3fBuffer(subMesh.getVertexCount());
            
            int numVertices = calModel.getPhysique().calculateVertices(subMesh, vertexBuffer2);
            @SuppressWarnings("unused")
            int numNormals = calModel.getPhysique().calculateNormals(subMesh, normalBuffer2);
            
            if (numVertices != oldNumVertices)
            {
                ta = new IndexedTriangleArray(numVertices, subMesh.getFaceIndices().size());
                ta.setOptimization(Optimization.NONE);
                
                oldNumVertices = numVertices;
            }
            
            ta.setCoordinates(0, BufferToArray.array(vertexBuffer2.getBuffer()));
            ta.setNormals(0, BufferToArray.array(normalBuffer2.getBuffer()));
            ta.setIndex(BufferToArray.array(subMesh.getFaceIndices().getBuffer()));
            
            for (int i = 0; i < subMesh.getCoreSubmesh().getTextureCoordinates().length; i++)
            {
                ta.setTextureCoordinates(i, 0, 2, BufferToArray.array(subMesh.getCoreSubmesh().getTextureCoordinates()[i].getBuffer()));
            }
            
            setGeometry(ta);
            setBoundsDirty();
        }
        
        ta.setBoundsDirty();
    }
    
    /**
     * @return a Shape3D representing the current state
     */
    public Shape3D getShape3D(int flags)
    {
        IndexedTriangleArray geom = new IndexedTriangleArray(ta.getVertexCount(), ta.getIndex().length);
        ta.setOptimization(Optimization.NONE);
        geom.setCoordinates(0, ta.getCoordRefFloat());
        geom.setNormals(0, ta.getNormalRefFloat());
        for (int i = 0; i < 8; i++)
        {
            if (ta.hasTextureCoordinates(i))
                geom.setTextureCoordinates(i, 0, 2, ta.getTexCoordRefFloat(i));
        }
        geom.setIndex(ta.getIndex());
        
        // TODO (Amos Wenger) : Add a flag for that
        geom.calculateFaceNormals();
        
        Shape3D shape = new Shape3D(geom, getAppearance());
        
        return shape;
    }
}
