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
package org.xith3d.scenegraph.primitives;

import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleStripArray;

/**
 * An enumeration of Geometry types, that can be created by shape implementations.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public enum GeometryType
{
    TRIANGLE_ARRAY( TriangleArray.class ),
    TRIANGLE_STRIP_ARRAY( TriangleStripArray.class ),
    INDEXED_TRIANGLE_ARRAY( IndexedTriangleArray.class ),
    INDEXED_TRIANGLE_STRIP_ARRAY( IndexedTriangleStripArray.class );
    
    /**
     * An alias for TRIANGLE_ARRAY.
     */
    public static final GeometryType TA = TRIANGLE_ARRAY;
    
    /**
     * An alias for TRIANGLE_STRIP_ARRAY.
     */
    public static final GeometryType TSA = TRIANGLE_STRIP_ARRAY;
    
    /**
     * An alias for INDEXED_TRIANGLE_ARRAY.
     */
    public static final GeometryType ITA = INDEXED_TRIANGLE_ARRAY;
    
    /**
     * An alias for INDEXED_TRIANGLE_STRIP_ARRAY.
     */
    public static final GeometryType ITSA = INDEXED_TRIANGLE_STRIP_ARRAY;
    
    private Class< ? extends Geometry > clazz;
    
    public Class< ? extends Geometry > getCorrespondingClass()
    {
        return ( clazz );
    }
    
    private GeometryType( Class< ? extends Geometry > clazz )
    {
        this.clazz = clazz;
    }
}
