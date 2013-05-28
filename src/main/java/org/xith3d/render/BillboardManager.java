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

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.render.preprocessing.RenderAtom;
import org.xith3d.scenegraph.Billboard;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.View;

/**
 * Manages the rearrangement of Billboards.
 * 
 * @author Herve
 */
final class BillboardManager
{
    private static Matrix3f viewRotation = null;
    private static Matrix3f billboardScale = null;
    private static Transform3D viewTransform = null;
    private static Vector3f tmpVector = new Vector3f();
    
    /**
     * @return the rotation matrix for the View.
     * 
     * @param view
     */
    private static Matrix3f getViewRotation( View view )
    {
        if ( viewRotation == null )
        {
            viewRotation = new Matrix3f();
        }
        
        if ( viewTransform == null )
        {
            viewTransform = new Transform3D();
        }
        
        view.getTransform( viewTransform );
        viewTransform.getRotation( viewRotation );
        
        return ( viewRotation );
    }
    
    /**
     * Computes and returns the distance along the Z-axis between the RenderAtom
     * and the View. This is the dot product of the view-to-atom vector and the
     * view facing direction vector.
     * 
     * @param atom
     * @param view
     * 
     * @return the z-distance between the Shape and the View.
     */
    private static float computeZDistance( RenderAtom< ? > atom, View view )
    {
        final Point3f viewPosition = view.getPosition();
        final Point3f atomPosition = new Point3f();
        atom.getPosition( atomPosition );
        tmpVector.sub( atomPosition, viewPosition );
        
        return ( view.getFacingDirection().dot( tmpVector ) );
    }
    
    /**
     * @return a Billboard scale matrix (scales the X and the Y dimensions).
     * 
     * @param scaleX
     * @param scaleY
     */
    private static Matrix3f getBillboardXYScaleMatrix( double scaleX, double scaleY )
    {
        if ( billboardScale == null )
        {
            billboardScale = new Matrix3f();
        }
        
        billboardScale.setIdentity();
        billboardScale.m00( (float)scaleX );
        billboardScale.m11( (float)scaleY );
        
        return ( billboardScale );
    }
    
    /**
     * Updates the given Billboard geometry, so that it propertly faces the View.
     * 
     * @param billboardAtom the RenderAtom for the Billboard
     * @param view the View
     * @param frameId the frame-ID
     */
    static void updateBillboardGeometry( final RenderAtom< ? > billboardAtom, final View view, final float canvasWidth, final float canvasHeight, final long nanoTime, final long nanoStep, final long frameId )
    {
        final Matrix3f viewRotation = getViewRotation( view );
        final Billboard billboard = (Billboard)billboardAtom.getNode();
        final Sized2iRO sizeOnScreen = billboard.getSizeOnScreen();

        if ( sizeOnScreen != null )
        {
            /*
             * Let's resize the Billboard appropriately to match the value of
             * sizeOnScreen. The method depends on the projection policy.
             */
            switch ( view.getProjectionPolicy() )
            {
                case PERSPECTIVE_PROJECTION:
                    /*
                     * In case of a perspective projection, the Billboard scale
                     * depends on its distance to the View. The distance we need
                     * is the projection of the cartesian distance onto the
                     * Z-axis to avoid distortion when the Billboard is not at
                     * the center of the screen.
                     */
                    final float zDistance = computeZDistance( billboardAtom, view );
                    final float fovRatio = zDistance * 2f * (float)Math.tan( view.getFieldOfView() ) / canvasHeight;
                    viewRotation.mul( getBillboardXYScaleMatrix( fovRatio * sizeOnScreen.getWidth(), fovRatio * sizeOnScreen.getHeight() ) );
                    break;
                
                case PARALLEL_PROJECTION:
                    /*
                     * TODO: This case handles the "Foreground" Billboards.
                     * Maybe we need some other processing for other cases of
                     * PARALLEL_PROJECTION...
                     * In case of a parallel projection, the scale factor is
                     * constant, as the screen has always a width of 2f in world
                     * coordinates.
                     */
                    final float factor = 2f / canvasWidth;
                    viewRotation.mul( getBillboardXYScaleMatrix( factor * sizeOnScreen.getWidth(), factor * sizeOnScreen.getHeight() ) );
                    break;
            }
        }
        
        billboard.updateFaceToCamera( viewRotation, frameId, nanoTime, nanoStep );
    }
}
