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
package org.xith3d.render.states;

import org.xith3d.scenegraph.NodeComponent;

/**
 * A shader is what is responsible for setting up the graphics state to render
 * an atom. StateUnits can be compared to other StateUnits in order to sort them
 * in an efficient manner.<br>
 * <br>
 * Each StateUnit needs to have an API peer which is capable of issuing the proper
 * commands to the 3d card. Thus the StateUnit really is a container for the
 * information needed to set OpenGL states, but the implementation of the
 * StateUnit is within the hardware interface layer.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class StateUnit extends StateSortable
{
    public static final int MAX_STATE_TYPES = 17;
    public static final int MAX_ATOM_TYPES = 3;
    
    private final boolean isDefault;
    
    public abstract NodeComponent getNodeComponent();
    
    public final boolean isDefault()
    {
        return ( isDefault );
    }
    
    /**
     * Because the rendering engine needs to deal with translucent geometry
     * differently the shader needs to be able to indicate whether it is
     * translucent or not.
     * 
     * @return true, if the Shader is translucent
     */
    public abstract boolean isTranslucent();
    
    public StateUnit( int stateType, boolean isDefault )
    {
        super( stateType );
        
        this.isDefault = isDefault;
    }
}
