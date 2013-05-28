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
package org.xith3d.terrain;

import org.openmali.vecmath2.Vector3f;

/**
 * A GridSampler is used to retrieve height values from a regular grid.
 *
 * A grid is defined as rectangular area with coordinates [s,t] ranging from
 * [0,0] to [1,1]. The values stored in the grid range from 0 to 1.
 * 
 * The GridSampler is normally used by a triangulator, that generates the triangle
 * representation of the grid. To enable effective data storage for LOD rendering
 * of large scale landscapes, the prepare() method accept a detail argument specifying
 * the needed resolution for the triangulation.
 * 
 * The values of the detail argument have no strict meaning other than a value 
 * of 0 requests the most detailed resolution, the GridSampler implementation
 * has to offer. Higher numbers are for less detailed data resolutions. A detail-level
 * of 0 requests the lowest available resolution. Typically (depends on the overall setup)
 * a detail-level of 2 requests the highest available resolution.
 *
 * @author Mathias 'cylab' Henze
 * @since 1.0
 */
public interface GridSampler extends GridResource
{
    /**
     * Samples a height value from the grid
     * @param s the "longitude" coordinate of the grid
     * @param t the "latitude" coordinate of the grid 
     * @return a height value for the given [s,t] coordinate
     */
    public float sampleHeight( float s, float t );

    /**
     * Samples a normal vector from the grid
     * @param s the "longitude" coordinate of the grid
     * @param t the "latitude" coordinate of the grid 
     * @return a normal vector for the given [s,t] coordinate
     */
    public Vector3f sampleNormal( float s, float t );

    /**
     * Samples a tangent vector from the grid
     * @param s the "longitude" coordinate of the grid
     * @param t the "latitude" coordinate of the grid 
     * @return a tangent vector for the given [s,t] coordinate
     */
    public Vector3f sampleTangent( float s, float t );

    /**
     * Samples a binormal vector from the grid
     * @param s the "longitude" coordinate of the grid
     * @param t the "latitude" coordinate of the grid 
     * @return a binormal vector for the given [s,t] coordinate
     */
    public Vector3f sampleBinormal( float s, float t );

    public void release();
}
