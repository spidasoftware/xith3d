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
package org.xith3d.effects.bloom;

import java.io.IOException;

import org.openmali.types.twodee.Sized2iRO;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.scenegraph.GroupNode;

/**
 * The BloomFactory is a visual effect a GroupNode. 
 * It computes in real-time bloom around objects based on their brightness.
 * 
 * @author Yoann Meste (aka Mancer)
 */
public abstract class BloomFactory
{
    private float bloomWeight = 0.75f;
    private float sceneWeight = 0.8f;
    
    public abstract void prepareForBloom( Xith3DEnvironment env, Sized2iRO resolution, GroupNode group ) throws IOException;
    
    protected abstract void updateBloomSettings();
    
    public void setBloomWeight( float bloomWeight )
    {
        this.bloomWeight = bloomWeight;
        
        updateBloomSettings();
    }
    
    public final float getBloomWeight()
    {
        return ( bloomWeight );
    }
    
    public final float getSceneWeight()
    {
        return sceneWeight;
    }
    
    public void setSceneWeight( float sceneWeight )
    {
        this.sceneWeight = sceneWeight;
        
        updateBloomSettings();
    }
}
