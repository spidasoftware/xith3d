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

import java.util.Comparator;

import org.openmali.spatial.bodies.Plane;
import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.render.preprocessing.RenderBin;
import org.xith3d.render.preprocessing.RenderBin.DynamicAtomArray;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.utility.comparator.Sorter;

/**
 * This sorter sorts RenderAtoms front-to-back.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class FrontToBackRenderBinSorter2 extends RenderBinSorter implements Comparator< RenderAtom< ? > >
{
    private final Plane viewPlane = new Plane();
    private final Point3f tmpPos = new Point3f();
    private final float[] tmpFloats = new float[ 8 ];
    
    /**
     * {@inheritDoc}
     */
    public int compare( RenderAtom< ? > atom1, RenderAtom< ? > atom2 )
    {
        if ( atom1.getSquaredDistanceToView() < atom2.getSquaredDistanceToView() )
            return ( -1 );
        else if ( atom1.getSquaredDistanceToView() > atom2.getSquaredDistanceToView() )
            return ( 1 );
        else
            return ( 0 );
    }
    
    @Override
    public boolean equals( Object o )
    {
        return ( false );
    }
    
    @Override
    public void updateDistancesToView( RenderBin renderBin, Transform3D viewTransform )
    {
        viewTransform.getMatrix4f().transform( Vector3f.NEGATIVE_Z_AXIS, viewPlane.getNormal() );
        viewTransform.getTranslation( tmpPos );
        viewPlane.setD( tmpPos.distance( 0f, 0f, 0f ) );
        //viewPlane.normalize();
        
        final DynamicAtomArray atoms = renderBin.getAtoms();
        
        float dist = 0f;
        float distSq = 0f;
        float depth = 0f;
        for ( int i = 0; i < atoms.size(); i++ )
        {
            final Bounds bounds = atoms.get( i ).getNode().getWorldBounds();
            
            if ( ( bounds != null ) && ( bounds instanceof BoundingSphere ) )
            {
                final BoundingSphere bs = (BoundingSphere)bounds;
                
                tmpPos.set( bs.getCenterX(), bs.getCenterY(), bs.getCenterZ() );
                dist = viewPlane.distanceTo( tmpPos );
                distSq = dist * dist;
                distSq -= bs.getRadiusSquared();
                
                depth = tmpPos.getZ() + bs.getRadius();
            }
            if ( ( bounds != null ) && ( bounds instanceof BoundingBox ) )
            {
                final BoundingBox bb = (BoundingBox)bounds;
                
                System.out.println( "#############" );
                /*
                System.out.println( new Point3f( bb.getLowerX(), bb.getLowerY(), bb.getLowerZ() ) );
                System.out.println( new Point3f( bb.getUpperX(), bb.getLowerY(), bb.getLowerZ() ) );
                System.out.println( new Point3f( bb.getLowerX(), bb.getUpperY(), bb.getLowerZ() ) );
                System.out.println( new Point3f( bb.getUpperX(), bb.getUpperY(), bb.getLowerZ() ) );
                System.out.println( new Point3f( bb.getLowerX(), bb.getLowerY(), bb.getUpperZ() ) );
                System.out.println( new Point3f( bb.getUpperX(), bb.getLowerY(), bb.getUpperZ() ) );
                System.out.println( new Point3f( bb.getLowerX(), bb.getUpperY(), bb.getUpperZ() ) );
                System.out.println( new Point3f( bb.getUpperX(), bb.getUpperY(), bb.getUpperZ() ) );
                */
                
                tmpFloats[ 0 ] = viewPlane.distanceTo( bb.getLowerX(), bb.getLowerY(), bb.getLowerZ() );
                tmpFloats[ 1 ] = viewPlane.distanceTo( bb.getUpperX(), bb.getLowerY(), bb.getLowerZ() );
                tmpFloats[ 2 ] = viewPlane.distanceTo( bb.getLowerX(), bb.getUpperY(), bb.getLowerZ() );
                tmpFloats[ 3 ] = viewPlane.distanceTo( bb.getUpperX(), bb.getUpperY(), bb.getLowerZ() );
                tmpFloats[ 4 ] = viewPlane.distanceTo( bb.getLowerX(), bb.getLowerY(), bb.getUpperZ() );
                tmpFloats[ 5 ] = viewPlane.distanceTo( bb.getUpperX(), bb.getLowerY(), bb.getUpperZ() );
                tmpFloats[ 6 ] = viewPlane.distanceTo( bb.getLowerX(), bb.getUpperY(), bb.getUpperZ() );
                tmpFloats[ 7 ] = viewPlane.distanceTo( bb.getUpperX(), bb.getUpperY(), bb.getUpperZ() );
                dist = tmpFloats[ 0 ];
                System.out.println( tmpFloats[ 0 ] );
                for ( int j = 1; j < tmpFloats.length; j++ )
                {
                    System.out.println( tmpFloats[ j ] );
                    if ( tmpFloats[ j ] < dist )
                        dist = tmpFloats[ j ];
                }
                
                distSq = dist * dist;
                
                depth = bb.getUpperZ();
            }
            else
            {
                atoms.get( i ).getPosition( tmpPos );
                dist = viewPlane.distanceTo( tmpPos );
                distSq = dist * dist;
                
                depth = tmpPos.getZ();
            }
            
            //System.out.println( FastMath.sqrt( distSq ) );
            
            atoms.get( i ).setCompareIndicators( distSq, depth, null );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void sortRenderBin( RenderBin renderBin, Transform3D viewTransform )
    {
        updateDistancesToView( renderBin, viewTransform );
        
        final DynamicAtomArray atoms = renderBin.getAtoms();
        
        //Arrays.sort( atoms.getRawArray(), 0, atoms.size(), this );
        Sorter.quickSort( atoms.getRawArray(), 0, atoms.size() - 1, this );
        //head = Sorter.mergeSort( head, this );
    }
    
    public FrontToBackRenderBinSorter2()
    {
    }
}
