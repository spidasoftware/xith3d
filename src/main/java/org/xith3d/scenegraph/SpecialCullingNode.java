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
package org.xith3d.scenegraph;

import org.openmali.spatial.bodies.Frustum;
import org.openmali.vecmath2.Point3f;
import org.xith3d.picking.PickRay;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.preprocessing.FrustumCuller;
import org.xith3d.render.preprocessing.RenderBinProvider;

/**
 * This interface must be implemented by {@link Node}s, that need a special
 * culling algorithm. Typically thiese are {@link QuadTreeGroup}s, {@link OcTreeGroup}s
 * or Cells.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface SpecialCullingNode< N extends Node >
{
    /**
     * Thsi method is called by the {@link FrustumCuller}, if an instance of {@link SpecialCullingNode}
     * is detected. The implementation must traverse the individual structure of this node
     * and call the {@link FrustumCuller}'s {@link FrustumCuller#cullNodeAtoms(Node, org.openmali.spatial.bodies.Classifier.Classification, boolean, View, Point3f, Frustum, RenderBinProvider, OpenGLCapabilities, long, long, long, PickRay, boolean)}
     * method.
     * 
     * @param node the node to be culled
     * @param cullingSuppressed
     * @param view
     * @param viewPosition
     * @param frustum
     * @param binProvider
     * @param glCaps
     * @param frameId
     * @param nanoTime
     * @param nanoStep
     * @param pickRay if this is not null, it is a pick-culling
     * @param isShadowPass
     * @param frustumCuller the {@link FrustumCuller} instance to call the {@link FrustumCuller#cullNodeAtoms(Node, org.openmali.spatial.bodies.Classifier.Classification, boolean, View, Point3f, Frustum, RenderBinProvider, OpenGLCapabilities, long, long, long, PickRay, boolean)} method on for each {@link Node} to be further traversed
     */
    public void cullSpecialNode( N node, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller );
}
