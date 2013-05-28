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
package org.xith3d.render.preprocessing.sorting;

import org.openmali.vecmath2.Point3f;
import org.xith3d.render.Renderer;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.RenderBin.DynamicAtomArray;
import org.xith3d.scenegraph.Transform3D;

/**
 * A RenderBinSorter is a sorting instance for all RenderBins of the Renderer.
 * 
 * @see RenderBin
 * @see Renderer
 * @see Renderer#setOpaqueSortingPolicy(org.xith3d.render.Renderer.OpaqueSortingPolicy)
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class RenderBinSorter
{
    private Point3f viewPosition = new Point3f();
    private Point3f tmpPos = new Point3f();
    
    public void updateDistancesToView( RenderBin renderBin, Transform3D viewTransform )
    {
        if ( viewTransform != null )
            viewTransform.getTranslation( viewPosition );
        
        final DynamicAtomArray atoms = renderBin.getAtoms();
        
        for ( int i = 0; i < atoms.size(); i++ )
        {
            atoms.get( i ).getPosition( tmpPos );
            atoms.get( i ).setCompareIndicators( ( viewTransform != null ? tmpPos.distanceSquared( viewPosition ) : 0.0f ), tmpPos.getZ(), null );
        }
    }
    
    /**
     * Sorts a whole RenderBin.
     * 
     * @param renderBin
     * @param viewTransform
     */
    public abstract void sortRenderBin( RenderBin renderBin, Transform3D viewTransform );
}
