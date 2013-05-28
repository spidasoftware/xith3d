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
package org.xith3d.picking;

import org.openmali.FastMath;
import org.openmali.spatial.LineContainer;
import org.openmali.spatial.TriangleContainer;
import org.openmali.spatial.polygons.Triangle;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Ray3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.render.Canvas3D;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.Leaf;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.PointArray;
import org.xith3d.scenegraph.PointAttributes;
import org.xith3d.scenegraph.SceneGraph;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.View.ProjectionPolicy;

/**
 * Geometry-ray intersection test is accessed by PickingLibrary trough an
 * interface instead of an internal method to make it replaceable.
 * This is the default implementation.
 * 
 * @author Arne Mueller
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 * @author Mathias Henze (aka cylab)
 */
public class DefaultGeometryPickTester implements GeometryPickTester
{
    private static final int[] TMP_ARRAY = new int[ 1 ];
    
    private static final float testTriangle( PickResult pr, int faceIndex, Triangle triang, Ray3f pickRay, float closestIntersection )
    {
        float f = triang.intersects( pickRay, closestIntersection );
        if ( f >= 0.0f )
        { // there is an intersection!
            f = f / pickRay.length();
            if ( f < closestIntersection )
            {
                pr.setMinimumDistance( f );
                pr.setFaceIndex( faceIndex );
                
                return ( f );
            }
            
            return ( -1.0f );
        }
        
        return ( -1.0f );
    }
    
    /**
     * {@inheritDoc}
     */
    public float testGeometryIntersection( PickResult pickCandidate, Ray3f pickRay, float closestIntersection, Triangle bufferTriangle )
    {
        float result = -1.0f;
        
        Geometry geom = pickCandidate.getGeometry();
        
        // now find intersection points with the geometry
        if ( geom instanceof TriangleContainer )
        {
            TriangleContainer triangCnt = (TriangleContainer)geom;
            
            /*
             * If the GeometryArray is a strip, we will do the
             * triangle-loop on our own.
             * Simply using the getTriangle( int, Triangle ) would also work,
             * but is slower, since the vertex indices must be calculated
             * by looping over the strips, which is a little more expensive
             * (but not extremely).
             */

            if ( geom instanceof TriangleStripArray )
            {
                TriangleStripArray tsa = (TriangleStripArray)geom;
                
                final int[] stripLengths;
                if ( tsa.getStripVertexCounts() == null )
                {
                    stripLengths = TMP_ARRAY;
                    stripLengths[ 0 ] = tsa.getVertexCount();
                }
                else
                {
                    stripLengths = tsa.getStripVertexCounts();
                }
                
                int offset = 0;
                for ( int i = 0; i < stripLengths.length; i++ )
                {
                    for ( int j = 2; j < stripLengths[ i ]; j++ )
                    {
                        final int idx0 = offset + j - 2;
                        final int idx1 = offset + j - 1;
                        final int idx2 = offset + j - 0;
                        
                        tsa.getTriangle( idx0, idx1, idx2, bufferTriangle );
                        
                        final float f = testTriangle( pickCandidate, idx0, bufferTriangle, pickRay, closestIntersection );
                        if ( f >= 0.0f )
                        {
                            closestIntersection = f;
                            result = f;
                        }
                    }
                    
                    offset += stripLengths[ i ] - 2;
                }
            }
            else if ( geom instanceof IndexedTriangleStripArray )
            {
                IndexedTriangleStripArray itsa = (IndexedTriangleStripArray)geom;
                
                final int[] stripLengths;
                if ( itsa.getStripVertexCounts() == null )
                {
                    stripLengths = TMP_ARRAY;
                    stripLengths[ 0 ] = itsa.getVertexCount();
                }
                else
                {
                    stripLengths = itsa.getStripVertexCounts();
                }
                final int[] index = itsa.getIndex();
                
                int offset = 0;
                for ( int i = 0; i < stripLengths.length; i++ )
                {
                    for ( int j = 2; j < stripLengths[ i ]; j++ )
                    {
                        final int idx0 = index[ offset + j - 2 ];
                        final int idx1 = index[ offset + j - 1 ];
                        final int idx2 = index[ offset + j - 0 ];
                        
                        itsa.getTriangle( idx0, idx1, idx2, bufferTriangle );
                        
                        final float f = testTriangle( pickCandidate, idx0, bufferTriangle, pickRay, closestIntersection );
                        if ( f >= 0.0f )
                        {
                            closestIntersection = f;
                            result = f;
                        }
                    }
                    
                    offset += stripLengths[ i ] - 2;
                }
            }
            else
            {
                final int nTrian = triangCnt.getTriangleCount();
                
                for ( int i = 0; i < nTrian; i++ )
                {
                    triangCnt.getTriangle( i, bufferTriangle );
                    
                    final float f = testTriangle( pickCandidate, i, bufferTriangle, pickRay, closestIntersection );
                    if ( f >= 0.0f )
                    {
                        closestIntersection = f;
                        result = f;
                    }
                }
            }
        }
        else if ( geom instanceof PointArray )
        {
            final PointArray points = (PointArray)geom;
            
            if ( !( pickCandidate.getNode() instanceof Shape3D ) )
                return ( result );
            
            final Shape3D shape = (Shape3D)pickCandidate.getNode();
            
            if ( shape.getAppearance() == null )
                return ( result );
            
            final PointAttributes pointAttribs = shape.getAppearance().getPointAttributes();
            
            if ( pointAttribs == null )
                return ( result );
            
            
            /*
            float size = pointAttribs.getPointSize();
            
            Vector3f tmpVec = Vector3f.fromPool();
            
            float s = 2.0f - 2.0f * ( 300 + size ) / 600 - 1.0f;
            tmpVec.set( s, s, s );
            viewMatrix.transform( tmpVec );
            size = ( tmpVec.getX() + tmpVec.getY() + tmpVec.getZ() ) / 3f;
            System.out.println( size );
            
            Vector3f.toPool( tmpVec );
            */
            float size = pointAttribs.getPointSize();
            
            // get the view, canvas and projection policy
            final Leaf node = pickCandidate.getNode();
            final BranchGroup root = (node == null) ? null : node.getRoot();
            final SceneGraph sceneGraph = (root == null) ? null : root.getSceneGraph();
            final View view = (sceneGraph == null) ? null : sceneGraph.getView();
            final Point3f viewPosition = (view == null) ? null : view.getPosition();
            final Canvas3D canvas = (view == null) ? null : view.getCanvas3D(0);
            final ProjectionPolicy projectionPolicy = (view == null) ? ProjectionPolicy.PERSPECTIVE_PROJECTION : view.getProjectionPolicy();
            float fovH = size * 0.001167f; // this is estimated for a standard 1024*768 window
            float h = fovH;
            
            // if we can get everything we need, calculate the correction factor
            if ( ( viewPosition != null ) && ( canvas != null ) )
            {
                switch ( projectionPolicy )
                {
                    case PERSPECTIVE_PROJECTION:
                        fovH = ( size * FastMath.tan( view.getFieldOfView() ) / canvas.getHeight() );
                        break;
                        
                    case PARALLEL_PROJECTION:
                        h = ( size / 2f ) * ( 2f / canvas.getWidth() );
                        break;
                }
            }
            
            Point3f point = Point3f.fromPool();
            Vector3f tmp = Vector3f.fromPool();
            
            final int n = points.getVertexCount();
            for ( int i = 0; i < n; i++ )
            {
                points.getVertex( i, point );
                
                if ( projectionPolicy == ProjectionPolicy.PERSPECTIVE_PROJECTION )
                {
                    tmp.sub( point, viewPosition );
                    h = fovH * tmp.length();
                }
                
                final float px = point.getX();
                final float py = point.getY();
                final float pz = point.getZ();
                
                // three crossing quads, each aligned to an axis, with 2 triangles = 6 triangles to test
                for ( int j = 0; j < 6; j++ )
                {
                    float ax = 0f, ay = 0f, az = 0f, bx = 0f, by = 0f, bz = 0f, cx = 0f, cy = 0f, cz = 0f;
                    
                    switch ( j )
                    {
                        // the intersection when looking along the z axis
                        case 0:
                            ax = -h; ay = -h; az = 0f;
                            bx = +h; by = +h; bz = 0f;
                            cx = -h; cy = +h; cz = 0f;
                            break;
                        case 1:
                            ax = -h; ay = -h; az = 0f;
                            bx = +h; by = -h; bz = 0f;
                            cx = +h; cy = +h; cz = 0f;
                            break;
                        // we also need to check the intersection when looking along the y axis
                        case 2:
                            ax = -h; ay = 0f; az = -h;
                            bx = +h; by = 0f; bz = +h;
                            cx = -h; cy = 0f; cz = +h;
                            break;
                        case 3:
                            ax = -h; ay = 0f; az = -h;
                            bx = +h; by = 0f; bz = -h;
                            cx = +h; cy = 0f; cz = +h;
                            break;
                        // we also need to check the intersection when looking along the x axis
                        case 4:
                            ax = 0f; ay = -h; az = -h;
                            bx = 0f; by = +h; bz = +h;
                            cx = 0f; cy = -h; cz = +h;
                            break;
                        case 5:
                            ax = 0f; ay = -h; az = -h;
                            bx = 0f; by = +h; bz = -h;
                            cx = 0f; cy = +h; cz = +h;
                            break;
                    }
                    
                    bufferTriangle.setVertexCoordA( tmp.set( px + ax, py + ay, pz + az ) );
                    bufferTriangle.setVertexCoordB( tmp.set( px + bx, py + by, pz + bz ) );
                    bufferTriangle.setVertexCoordC( tmp.set( px + cx, py + cy, pz + cz ) );
                    
                    float f = testTriangle( pickCandidate, i, bufferTriangle, pickRay, closestIntersection );
                    
                    if ( f >= 0.0f )
                    {
                        if ( f < closestIntersection )
                            closestIntersection = f;
                        result = f;
                    }
                }
            }
            
            Vector3f.toPool( tmp );
            Point3f.toPool( point );
        }
        else if ( geom instanceof LineContainer )
        {
            final LineContainer lines = (LineContainer)geom;
            
            if ( !( pickCandidate.getNode() instanceof Shape3D ) )
                return ( result );
            
            final Shape3D shape = (Shape3D)pickCandidate.getNode();
            
            if ( shape.getAppearance() == null )
                return ( result );
            
            final LineAttributes lineAttribs = shape.getAppearance().getLineAttributes();
            
            if ( lineAttribs == null )
                return ( result );
            
            final float lwidth = lineAttribs.getLineWidth();
            
            // get the view, canvas and projection policy
            final Leaf node = pickCandidate.getNode();
            final BranchGroup root = (node == null) ? null : node.getRoot();
            final SceneGraph sceneGraph = (root == null) ? null : root.getSceneGraph();
            final View view = (sceneGraph == null) ? null : sceneGraph.getView();
            final Point3f viewPosition = (view == null) ? null : view.getPosition();
            final Canvas3D canvas = (view == null) ? null : view.getCanvas3D(0);
            final ProjectionPolicy projectionPolicy = (view == null) ? ProjectionPolicy.PERSPECTIVE_PROJECTION : view.getProjectionPolicy();
            float fovH = lwidth  * 0.001167f; // this is estimated for a standard 1024*768 window
            float hn = fovH;
            float hn1 = fovH;
            
            // if we can get everything we need, calculate the correction factor
            if ( ( viewPosition != null ) && ( canvas != null ) )
            {
                switch ( projectionPolicy )
                {
                    case PERSPECTIVE_PROJECTION:
                        fovH = ( lwidth * FastMath.tan( view.getFieldOfView() ) / canvas.getHeight() );
                        break;
                        
                    case PARALLEL_PROJECTION:
                        hn = ( lwidth / 2f ) * ( 2f / canvas.getWidth() );
                        hn1 = hn;
                        break;
                }
            }
            
            final int n = lines.getLinesCount();
            
            Point3f pn = Point3f.fromPool();
            Point3f pn1 = Point3f.fromPool();
            
            Point3f pfll = Point3f.fromPool();
            Point3f pful = Point3f.fromPool();
            Point3f pflr = Point3f.fromPool();
            Point3f pfur = Point3f.fromPool();
            
            Point3f pbll = Point3f.fromPool();
            Point3f pbul = Point3f.fromPool();
            Point3f pblr = Point3f.fromPool();
            Point3f pbur = Point3f.fromPool();
            
            Vector3f lineVector = Vector3f.fromPool();
            Vector3f right = Vector3f.fromPool();
            Vector3f up = Vector3f.fromPool();
            Vector3f tmp = Vector3f.fromPool();
            
            Matrix4f transform = Matrix4f.fromPool();
            Matrix3f rot = Matrix3f.fromPool();
            
            // create a bounding box around the line segment and test intersection with the triangles forming the sides
            for ( int i = 0; i < n; i++ )
            {
                lines.getLineCoordinates( i, pn, pn1 );
                
                if ( projectionPolicy == ProjectionPolicy.PERSPECTIVE_PROJECTION )
                {
                    tmp.sub( pn, viewPosition );
                    hn = fovH * tmp.length();
                    
                    tmp.sub( pn1, viewPosition );
                    hn1 = fovH * tmp.length();
                }
                
                lineVector.set( pn1 ).sub( pn );
                float l = lineVector.length();
                lineVector.normalize();
                
                // "front" face
                pfll.set( -hn, 0f - hn, +hn );
                pflr.set( +hn, 0f - hn, +hn );
                pful.set( -hn1, l + hn1, +hn1 );
                pfur.set( +hn1, l + hn1, +hn1 );
                
                // "back" face
                pbll.set( -hn, 0f - hn, -hn );
                pblr.set( +hn, 0f - hn, -hn );
                pbul.set( -hn1, l + hn1, -hn1 );
                pbur.set( +hn1, l + hn1, -hn1 );
                
                // create a new coordinate system aligned at the line direction
                float dz = pn1.getZ() - pn.getZ();
                tmp.set( ( (-0.0010f < dz) && (dz < 0.0010f) ) ? Vector3f.NEGATIVE_Z_AXIS : Vector3f.NEGATIVE_Y_AXIS );
                right.cross( lineVector, tmp );
                right.normalize();
                up.cross( lineVector, right );
                up.normalize();
                
                // set the new system to the rotation matrix and shift the box to the line origin
                rot.setColumn( 0, right );
                rot.setColumn( 1, lineVector );
                rot.setColumn( 2, up );
                transform.setIdentity();
                transform.setRotation( rot );
                transform.setTranslation( pn );
                
                transform.transform( pfll );
                transform.transform( pful );
                transform.transform( pflr );
                transform.transform( pfur );
                
                transform.transform( pbll );
                transform.transform( pbul );
                transform.transform( pblr );
                transform.transform( pbur );
                
                // two crossing rectangles and two quads for top and bottom = 8 triangles to test
                for ( int j = 0; j < 8; j++ )
                {
                    switch( j )
                    {
                        // crossing rectangle 1
                        case 0:
                            bufferTriangle.setVertexCoordA( pfll );
                            bufferTriangle.setVertexCoordB( pbur );
                            bufferTriangle.setVertexCoordC( pful );
                            break;
                        case 1:
                            bufferTriangle.setVertexCoordA( pfll );
                            bufferTriangle.setVertexCoordB( pbur );
                            bufferTriangle.setVertexCoordC( pblr );
                            break;
                        // crossing rectangle 2
                        case 2:
                            bufferTriangle.setVertexCoordA( pflr );
                            bufferTriangle.setVertexCoordB( pbul );
                            bufferTriangle.setVertexCoordC( pfur );
                            break;
                        case 3:
                            bufferTriangle.setVertexCoordA( pflr );
                            bufferTriangle.setVertexCoordB( pbul );
                            bufferTriangle.setVertexCoordC( pbll );
                            break;
                        // bottom quad
                        case 4:
                            bufferTriangle.setVertexCoordA( pbll );
                            bufferTriangle.setVertexCoordB( pflr );
                            bufferTriangle.setVertexCoordC( pfll );
                            break;
                        case 5:
                            bufferTriangle.setVertexCoordA( pbll );
                            bufferTriangle.setVertexCoordB( pflr );
                            bufferTriangle.setVertexCoordC( pblr );
                            break;
                        // top quad
                        case 6:
                            bufferTriangle.setVertexCoordA( pbul );
                            bufferTriangle.setVertexCoordB( pfur );
                            bufferTriangle.setVertexCoordC( pful );
                            break;
                        case 7:
                            bufferTriangle.setVertexCoordA( pbul );
                            bufferTriangle.setVertexCoordB( pfur );
                            bufferTriangle.setVertexCoordC( pbur );
                            break;
                    }
                    
                    float f = testTriangle( pickCandidate, i, bufferTriangle, pickRay, closestIntersection );
                    
                    if ( f >= 0.0f )
                    {
                        if ( f < closestIntersection )
                        {
                            closestIntersection = f;
                        }
                        
                        result = f;
                    }
                }
            }
            
            Point3f.toPool( pn );
            Point3f.toPool( pn1 );
            
            Point3f.toPool( pfll );
            Point3f.toPool( pful );
            Point3f.toPool( pflr );
            Point3f.toPool( pfur );
            
            Point3f.toPool( pbll );
            Point3f.toPool( pbul );
            Point3f.toPool( pblr );
            Point3f.toPool( pbur );
            
            Vector3f.toPool( lineVector );
            Vector3f.toPool( tmp  );
            Vector3f.toPool( right );
            Vector3f.toPool( up );
            
            Matrix4f.toPool( transform );
            Matrix3f.toPool( rot );
        }
        
        return ( result );
    }
}
