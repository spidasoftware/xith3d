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

/**
 * A GridProvider is used in conjunction with the LOD enabled terrain subsystem
 * to provide GridSampler and GridSurface instances of specific subareas of the
 * grid.
 *
 * To enable effective LOD rendering of large scale landscapes,a grids surface
 * needs to be divided into tiles with surfaces of different detail, so the
 * provider has a findSampler and findSurface method that returns such tiles
 * and constructs them on the fly if needed.
 *
 * The value of the detail argument has no strict meaning other than a value 
 * of 0 requests the most detailed resolution. Higher numbers are for less
 * detailed data resolutions.
 *
 * @author Mathias Henze (aka cylab)
 * @since 1.0
 */
public interface GridResourceProvider
{
    /**
     * Will be called by the terrain subsystem to retrieve a sampler, that holds the data for the area sampled next.
     *  
     * @param s1 the "longitude" corner coordinate of the grid tile sampled next. 
     * @param t1 the "latitude" corner coordinate of the grid tile  sampled next. 
     * @param s2 the opposite corners "longitude" coordinate of the grid tile sampled next.
     * @param t2 the opposite corners "latitude" coordinate of the grid tile sampled next.
     * @param detail the detail level of the grid tile sampled next. If '-1' the best already initialized sampler is returned.
     * @return a GridSampler implementation to provide the height and local coordinate values
     */
    GridSampler findSampler( float s1, float t1, float s2, float t2, int detail );
    
    void releaseSampler( GridSampler sampler);
    
    /**
     * Will be called by the terrain subsystem to retrieve a surface of the
     * specfied detail level.
     *  
     * @param s1 the "longitude" corner coordinate of the grid tile sampled next. 
     * @param t1 the "latitude" corner coordinate of the grid tile  sampled next. 
     * @param s2 the opposite corners "longitude" coordinate of the grid tile sampled next.
     * @param t2 the opposite corners "latitude" coordinate of the grid tile sampled next.
     * @param detail the detail level of the grid tile textured next. If '-1' the best already initialized surface is returned.
     * @return a GridSurface implementation that provides the appearance texture coordinate infos.
     */
    GridSurface findSurface( float s1, float t1, float s2, float t2, int detail );
    
    void releaseSurface( GridSurface surface);
}
