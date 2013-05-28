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
package org.xith3d.render;

import org.xith3d.render.OpenGLInfo.KnownVendor;

/**
 * This is a simple container class, that holds some values
 * describing the capabilities of the current OpenGL context.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OpenGLCapabilities
{
    private final int maxTextureSize;
    private final int maxTextureUnits;
    private final int maxAnisotropicLevel;
    private final int maxVertexAttributes;
    
    private final boolean isMinVersion13;
    private final boolean isMinVersion15;
    
    private final boolean supportsPlainMultiTexturing;
    private final boolean supportsVBOs;
    
    public final int getMaxTextureSize()
    {
        return ( maxTextureSize );
    }
    
    public final int getMaxTextureUnits()
    {
        return ( maxTextureUnits );
    }
    
    public final int getMaxAnisotropicLevel()
    {
        return ( maxAnisotropicLevel );
    }
    
    public final int getMaxVertexAttributes()
    {
        return ( maxVertexAttributes );
    }
    
    public final boolean isMinVersion13()
    {
        return ( isMinVersion13 );
    }
    
    public final boolean isMinVersion15()
    {
        return ( isMinVersion15 );
    }
    
    public final boolean supportsPlainMultiTexturing()
    {
        return ( supportsPlainMultiTexturing );
    }
    
    public final boolean supportsVBOs()
    {
        return ( supportsVBOs );
    }
    
    public OpenGLCapabilities( int maxTextureSize, int maxTextureUnits, int maxAnisotropicLevel, int maxVertexAttributes, OpenGLInfo glInfo )
    {
        this.maxTextureSize = maxTextureSize;
        this.maxTextureUnits = maxTextureUnits;
        this.maxAnisotropicLevel = maxAnisotropicLevel;
        this.maxVertexAttributes = maxVertexAttributes;
        
        this.isMinVersion13 = ( glInfo.getNormalizedVersion() >= OpenGLInfo.NORM_VERSION_1_3 );
        this.isMinVersion15 = ( glInfo.getNormalizedVersion() >= OpenGLInfo.NORM_VERSION_1_5 );
        
        this.supportsPlainMultiTexturing = ( glInfo.getKnwonVendor() != KnownVendor.ATI ) && ( glInfo.getKnwonVendor() != KnownVendor.INTEL ) && ( glInfo.getKnwonVendor() != KnownVendor.MESA );
        this.supportsVBOs = ( glInfo.getKnwonVendor() != KnownVendor.INTEL );
    }
}
