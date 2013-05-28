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
package org.xith3d.loaders.models.animations;

import org.jagatoo.datatypes.NamedObject;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Quaternion4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

/**
 * A Bone can either consist of a translation, rotation and scale,
 * where the scale can be null or a full transformation matrix.
 * In the first case the transformation matrix is null and
 * in the latter case the translation, rotaion and scale are all null.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Bone implements NamedObject
{
    private final Bone parent;
    
    private final String name;
    
    private final Vector3f translation;
    private final Quaternion4f rotation;
    private final Tuple3f scale;
    
    private final Matrix4f transformation;
    
    public final Bone getParent()
    {
        return ( parent );
    }
    
    public final String getName()
    {
        return ( name );
    }
    
    public final Vector3f getTranslation()
    {
        return ( translation );
    }
    
    public final Quaternion4f getRotation()
    {
        return ( rotation );
    }
    
    public final Tuple3f getScale()
    {
        return ( scale );
    }
    
    public final Matrix4f getTransformation()
    {
        return ( transformation );
    }
    
    public Bone( Bone parent, String name, Vector3f translation, Quaternion4f rotation, Tuple3f scale )
    {
        this.parent = parent;
        
        this.name = name;
        
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
        
        this.transformation = null;
    }
    
    public Bone( Bone parent, String name, Matrix4f transformation )
    {
        this.parent = parent;
        
        this.name = name;
        
        this.translation = null;
        this.rotation = null;
        this.scale = null;
        
        this.transformation = transformation;
    }
}
