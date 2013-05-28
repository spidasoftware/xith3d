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
package org.xith3d.render.preprocessing;

import org.openmali.spatial.bodies.Classifier;

import org.xith3d.render.preprocessing.sorting.RenderBinSorter;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.Transform3D;

/**
 * Contains and maintains all RenderBins, which on their part may contain all
 * RenderAtoms of the whole scenegraph.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class RenderBinProvider
{
    protected RenderBin opaqueBin;
    protected RenderBin transparentBin;
    
    protected RenderBin shadowsBin;
    
    public RenderBinProvider( RenderBin opaqueBin, RenderBin transparentBin, RenderBin shadowsBin )
    {
        this.opaqueBin = opaqueBin;
        this.transparentBin = transparentBin;
        
        this.shadowsBin = shadowsBin;
    }
    
    public RenderBinProvider()
    {
        this( new RenderBin( RenderBinType.MAIN_OPAQUE, "Main Opaque RenderBin", 2048 ),
              new RenderBin( RenderBinType.MAIN_TRANSPARENT, "Main Transparent RenderBin", 128 ),
              new RenderBin( RenderBinType.MAIN_OPAQUE, "Shadows RenderBin", 2048 )
            );
    }
    
    public final RenderBin getOpaqueBin()
    {
        return ( opaqueBin );
    }
    
    public final RenderBin getTransparentBin()
    {
        return ( transparentBin );
    }
    
    public final RenderBin getShadowsBin()
    {
        return ( shadowsBin );
    }
    
    public final int getAtomsCount()
    {
        return ( transparentBin.size() + opaqueBin.size() );
    }
    
    /**
     * Adds an atom to be rendered. The atoms are sorted into multiple render
     * bins to facilitate multiple passes where necessary.
     * 
     * @param atom
     */
    public final void addMainAtom( RenderAtom< ? extends Node > atom, Classifier.Classification classify, long frameId )
    {
        if ( !atom.isTranslucent() )
            opaqueBin.addAtom( atom, classify, frameId );
        else
            transparentBin.addAtom( atom, classify, frameId );
    }
    
    public final void addShadowAtom( RenderAtom< ? extends Node > atom, Classifier.Classification classify, long frameId  )
    {
        shadowsBin.addAtom( atom, classify, frameId );
    }
    
    private static final void sortRenderBin( RenderBin renderBin, RenderBinSorter opaqueSorter, RenderBinSorter transparentSorter, Transform3D viewTransform )
    {
        if ( renderBin.getType().isTransparent() )
        {
            if ( ( transparentSorter != null ) && ( renderBin.size() > 0 ) )
                transparentSorter.sortRenderBin( renderBin, viewTransform );
        }
        else
        {
            if ( ( opaqueSorter != null ) && ( renderBin.size() > 0 ) )
                opaqueSorter.sortRenderBin( renderBin, viewTransform );
        }
    }
    
    /**
     * Sorts the RenderAtoms in the diverse RenderBins by policies.
     * 
     * @param opaqueSorter the sorting policy for opaque shapes
     * @param transparentSorter the sorting policy for transparent shapes
     * @param viewTransform the View's transform
     */
    public final void sortAllAtoms( RenderBinSorter opaqueSorter, RenderBinSorter transparentSorter, Transform3D viewTransform )
    {
        sortRenderBin( opaqueBin, opaqueSorter, transparentSorter, viewTransform );
        sortRenderBin( transparentBin, opaqueSorter, transparentSorter, viewTransform );
        sortRenderBin( shadowsBin, opaqueSorter, transparentSorter, viewTransform );
    }
    
    public final void clearAllBins()
    {
        opaqueBin.clear();
        transparentBin.clear();
        shadowsBin.clear();
    }

    public final void shrinkAllBins()
    {
        opaqueBin.shrink();
        transparentBin.shrink();
        shadowsBin.shrink();
    }
}
