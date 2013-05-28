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
package org.xith3d.utility.geometry.nvtristrip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Vector;

/**
 * @author YVG
 */
class Stripifier
{
    public static int CACHE_INEFFICIENCY = 6;
    
    private Vector< Integer > indices = new Vector< Integer >();
    private int cacheSize;
    private int minStripLength;
    private float meshJump;
    private boolean bFirstTimeResetPoint;
    
    Stripifier()
    {
        super();
    }
    
    /**
     * Finds the edge info for these two indices.
     * 
     * @param edgeInfos
     * @param v0
     * @param v1
     * 
     * @return the edge info for these two indices
     */
    static EdgeInfo findEdgeInfo( ArrayList< EdgeInfo > edgeInfos, int v0, int v1 )
    {
        
        // we can get to it through either array
        // because the edge infos have a v0 and v1
        // and there is no order except how it was
        // first created.
        EdgeInfo infoIter = edgeInfos.get( v0 );
        while ( infoIter != null )
        {
            if ( infoIter.m_v0 == v0 )
            {
                if ( infoIter.m_v1 == v1 )
                    return ( infoIter );
                
                infoIter = infoIter.m_nextV0;
            }
            else
            {
                assert ( infoIter.m_v1 == v0 );
                if ( infoIter.m_v0 == v1 )
                    return ( infoIter );
                
                infoIter = infoIter.m_nextV1;
            }
        }
        
        return ( null );
    }
    
    /**
     * Finds the other face sharing these vertices exactly like the edge info above.
     * 
     * @param edgeInfos
     * @param v0
     * @param v1
     * @param faceInfo
     * 
     * @return the other face sharing these vertices
     */
    static FaceInfo findOtherFace( ArrayList< EdgeInfo > edgeInfos, int v0, int v1, FaceInfo faceInfo )
    {
        EdgeInfo edgeInfo = findEdgeInfo( edgeInfos, v0, v1 );
        
        if ( ( edgeInfo == null ) && ( v0 == v1 ) )
        {
            //we've hit a degenerate
            return null;
        }
        
        assert ( edgeInfo != null );
        return ( edgeInfo.m_face0 == faceInfo ? edgeInfo.m_face1 : edgeInfo.m_face0 );
    }
    
    static boolean alreadyExists( FaceInfo faceInfo, ArrayList< FaceInfo > faceInfos )
    {
        for ( int i = 0; i < faceInfos.size(); ++i )
        {
            FaceInfo o = faceInfos.get( i );
            if ( ( o.m_v0 == faceInfo.m_v0 ) && ( o.m_v1 == faceInfo.m_v1 ) && ( o.m_v2 == faceInfo.m_v2 ) )
                return true;
        }
        return false;
    }
    
    /**
     * Builds the list of all face and edge infos.
     * 
     * @param faceInfos
     * @param edgeInfos
     * @param maxIndex
     */
    void buildStripifyInfo( ArrayList< FaceInfo > faceInfos, ArrayList< EdgeInfo > edgeInfos, int maxIndex )
    {
        // reserve space for the face infos, but do not resize them.
        int numIndices = indices.size();
        faceInfos.ensureCapacity( numIndices / 3 );
        
        // we actually resize the edge infos, so we must initialize to null
        for ( int i = 0; i < maxIndex + 1; i++ )
            edgeInfos.add( null );
        
        // iterate through the triangles of the triangle list
        int numTriangles = numIndices / 3;
        int index = 0;
        boolean[] bFaceUpdated = new boolean[ 3 ];
        
        for ( int i = 0; i < numTriangles; i++ )
        {
            boolean bMightAlreadyExist = true;
            bFaceUpdated[ 0 ] = false;
            bFaceUpdated[ 1 ] = false;
            bFaceUpdated[ 2 ] = false;
            
            // grab the indices
            int v0 = indices.get( index++ );
            int v1 = indices.get( index++ );
            int v2 = indices.get( index++ );
            
            //we disregard degenerates
            if ( isDegenerate( v0, v1, v2 ) )
                continue;
            
            // create the face info and add it to the list of faces, but only
            // if this exact face doesn't already
            //  exist in the list
            FaceInfo faceInfo = new FaceInfo( v0, v1, v2 );
            
            // grab the edge infos, creating them if they do not already exist
            EdgeInfo edgeInfo01 = findEdgeInfo( edgeInfos, v0, v1 );
            if ( edgeInfo01 == null )
            {
                //since one of it's edges isn't in the edge data structure, it
                // can't already exist in the face structure
                bMightAlreadyExist = false;
                
                // create the info
                edgeInfo01 = new EdgeInfo( v0, v1 );
                
                // update the linked list on both
                edgeInfo01.m_nextV0 = edgeInfos.get( v0 );
                edgeInfo01.m_nextV1 = edgeInfos.get( v1 );
                edgeInfos.set( v0, edgeInfo01 );
                edgeInfos.set( v1, edgeInfo01 );
                
                // set face 0
                edgeInfo01.m_face0 = faceInfo;
            }
            else
            {
                if ( edgeInfo01.m_face1 != null )
                {
                    System.out.println( "BuildStripifyInfo: > 2 triangles on an edge" + v0 + "," + v1 + "... uncertain consequences\n" );
                }
                else
                {
                    edgeInfo01.m_face1 = faceInfo;
                    bFaceUpdated[ 0 ] = true;
                }
            }
            
            // grab the edge infos, creating them if they do not already exist
            EdgeInfo edgeInfo12 = findEdgeInfo( edgeInfos, v1, v2 );
            if ( edgeInfo12 == null )
            {
                bMightAlreadyExist = false;
                
                // create the info
                edgeInfo12 = new EdgeInfo( v1, v2 );
                
                // update the linked list on both
                edgeInfo12.m_nextV0 = edgeInfos.get( v1 );
                edgeInfo12.m_nextV1 = edgeInfos.get( v2 );
                edgeInfos.set( v1, edgeInfo12 );
                edgeInfos.set( v2, edgeInfo12 );
                
                // set face 0
                edgeInfo12.m_face0 = faceInfo;
            }
            else
            {
                if ( edgeInfo12.m_face1 != null )
                {
                    System.out.println( "BuildStripifyInfo: > 2 triangles on an edge" + v1 + "," + v2 + "... uncertain consequences\n" );
                }
                else
                {
                    edgeInfo12.m_face1 = faceInfo;
                    bFaceUpdated[ 1 ] = true;
                }
            }
            
            // grab the edge infos, creating them if they do not already exist
            EdgeInfo edgeInfo20 = findEdgeInfo( edgeInfos, v2, v0 );
            if ( edgeInfo20 == null )
            {
                bMightAlreadyExist = false;
                
                // create the info
                edgeInfo20 = new EdgeInfo( v2, v0 );
                
                // update the linked list on both
                edgeInfo20.m_nextV0 = edgeInfos.get( v2 );
                edgeInfo20.m_nextV1 = edgeInfos.get( v0 );
                edgeInfos.set( v2, edgeInfo20 );
                edgeInfos.set( v0, edgeInfo20 );
                
                // set face 0
                edgeInfo20.m_face0 = faceInfo;
            }
            else
            {
                if ( edgeInfo20.m_face1 != null )
                {
                    System.out.println( "BuildStripifyInfo: > 2 triangles on an edge" + v2 + "," + v0 + "... uncertain consequences\n" );
                }
                else
                {
                    edgeInfo20.m_face1 = faceInfo;
                    bFaceUpdated[ 2 ] = true;
                }
            }
            
            if ( bMightAlreadyExist )
            {
                if ( !alreadyExists( faceInfo, faceInfos ) )
                    faceInfos.add( faceInfo );
                else
                {
                    
                    //cleanup pointers that point to this deleted face
                    if ( bFaceUpdated[ 0 ] )
                        edgeInfo01.m_face1 = null;
                    if ( bFaceUpdated[ 1 ] )
                        edgeInfo12.m_face1 = null;
                    if ( bFaceUpdated[ 2 ] )
                        edgeInfo20.m_face1 = null;
                }
            }
            else
            {
                faceInfos.add( faceInfo );
            }
        }
    }
    
    static boolean isDegenerate( FaceInfo face )
    {
        if ( face.m_v0 == face.m_v1 )
            return true;
        else if ( face.m_v0 == face.m_v2 )
            return true;
        else if ( face.m_v1 == face.m_v2 )
            return true;
        else
            return false;
    }
    
    static boolean isDegenerate( int v0, int v1, int v2 )
    {
        if ( v0 == v1 )
            return true;
        else if ( v0 == v2 )
            return true;
        else if ( v1 == v2 )
            return true;
        else
            return false;
    }
    
    /**
     * @param indices
     * @param face
     * @return vertex of the input face which is "next" in the input index list
     */
    static int getNextIndex( Vector< Integer > indices, FaceInfo face )
    {
        
        int numIndices = indices.size();
        assert ( numIndices >= 2 );
        
        int v0 = indices.get( numIndices - 2 );
        int v1 = indices.get( numIndices - 1 );
        
        int fv0 = face.m_v0;
        int fv1 = face.m_v1;
        int fv2 = face.m_v2;
        
        if ( fv0 != v0 && fv0 != v1 )
        {
            if ( ( fv1 != v0 && fv1 != v1 ) || ( fv2 != v0 && fv2 != v1 ) )
            {
                System.out.println( "GetNextIndex: Triangle doesn't have all of its vertices\n" );
                System.out.println( "GetNextIndex: Duplicate triangle probably got us derailed\n" );
            }
            return fv0;
        }
        if ( fv1 != v0 && fv1 != v1 )
        {
            if ( ( fv0 != v0 && fv0 != v1 ) || ( fv2 != v0 && fv2 != v1 ) )
            {
                System.out.println( "GetNextIndex: Triangle doesn't have all of its vertices\n" );
                System.out.println( "GetNextIndex: Duplicate triangle probably got us derailed\n" );
            }
            return fv1;
        }
        if ( fv2 != v0 && fv2 != v1 )
        {
            if ( ( fv0 != v0 && fv0 != v1 ) || ( fv1 != v0 && fv1 != v1 ) )
            {
                System.out.println( "GetNextIndex: Triangle doesn't have all of its vertices\n" );
                System.out.println( "GetNextIndex: Duplicate triangle probably got us derailed\n" );
            }
            return fv2;
        }
        
        // shouldn't get here, but let's try and fail gracefully
        if ( ( fv0 == fv1 ) || ( fv0 == fv2 ) )
            return fv0;
        else if ( ( fv1 == fv0 ) || ( fv1 == fv2 ) )
            return fv1;
        else if ( ( fv2 == fv0 ) || ( fv2 == fv1 ) )
            return fv2;
        else
            return -1;
    }
    
    /**
     * Finds a good starting point, namely one which has only one neighbor.
     * 
     * @param faceInfos
     * @param edgeInfos
     * 
     * @return a good starting point
     */
    static int findStartPoint( ArrayList< FaceInfo > faceInfos, ArrayList< EdgeInfo > edgeInfos )
    {
        int bestCtr = -1;
        int bestIndex = -1;
        
        for ( int i = 0; i < faceInfos.size(); i++ )
        {
            int ctr = 0;
            
            if ( findOtherFace( edgeInfos, faceInfos.get( i ).m_v0, faceInfos.get( i ).m_v1, faceInfos.get( i ) ) == null )
                ctr++;
            if ( findOtherFace( edgeInfos, faceInfos.get( i ).m_v1, faceInfos.get( i ).m_v2, faceInfos.get( i ) ) == null )
                ctr++;
            if ( findOtherFace( edgeInfos, faceInfos.get( i ).m_v2, faceInfos.get( i ).m_v0, faceInfos.get( i ) ) == null )
                ctr++;
            if ( ctr > bestCtr )
            {
                bestCtr = ctr;
                bestIndex = i;
                //return i;
            }
        }
        //return -1;
        
        if ( bestCtr == 0 )
            return ( -1 );
        
        return bestIndex;
    }
    
    /**
     * A good reset point is one near other commited areas so that
     * we know that when we've made the longest strips its because
     * we're stripifying in the same general orientation.
     * 
     * @param faceInfos
     * @param edgeInfos
     */
    FaceInfo findGoodResetPoint( ArrayList< FaceInfo > faceInfos, ArrayList< EdgeInfo > edgeInfos )
    {
        // we hop into different areas of the mesh to try to get
        // other large open spans done. Areas of small strips can
        // just be left to triangle lists added at the end.
        FaceInfo result = null;
        
        if ( result == null )
        {
            int numFaces = faceInfos.size();
            int startPoint;
            if ( bFirstTimeResetPoint )
            {
                //first time, find a face with few neighbors (look for an edge
                // of the mesh)
                startPoint = findStartPoint( faceInfos, edgeInfos );
                bFirstTimeResetPoint = false;
            }
            else
                startPoint = (int)( ( (float)numFaces - 1 ) * meshJump );
            
            if ( startPoint == -1 )
            {
                startPoint = (int)( ( (float)numFaces - 1 ) * meshJump );
                
                //meshJump += 0.1f;
                //if (meshJump > 1.0f)
                //  meshJump = .05f;
            }
            
            int i = startPoint;
            do
            {
                // if this guy isn't visited, try him
                if ( faceInfos.get( i ).m_stripId < 0 )
                {
                    result = faceInfos.get( i );
                    break;
                }
                
                // update the index and clamp to 0-(numFaces-1)
                if ( ++i >= numFaces )
                    i = 0;
                
            }
            while ( i != startPoint );
            
            // update the meshJump
            meshJump += 0.1f;
            if ( meshJump > 1.0f )
                meshJump = .05f;
        }
        
        // return the best face we found
        return result;
    }
    
    /**
     * @param faceA
     * @param faceB
     * 
     * @return the vertex unique to faceB
     */
    static int getUniqueVertexInB( FaceInfo faceA, FaceInfo faceB )
    {
        
        int facev0 = faceB.m_v0;
        if ( facev0 != faceA.m_v0 && facev0 != faceA.m_v1 && facev0 != faceA.m_v2 )
            return facev0;
        
        int facev1 = faceB.m_v1;
        if ( facev1 != faceA.m_v0 && facev1 != faceA.m_v1 && facev1 != faceA.m_v2 )
            return facev1;
        
        int facev2 = faceB.m_v2;
        if ( facev2 != faceA.m_v0 && facev2 != faceA.m_v1 && facev2 != faceA.m_v2 )
            return facev2;
        
        // nothing is different
        return -1;
    }
    
    /**
     * Retrieves (at most) the two vertices shared between the two faces and
     * writes them into <b>vertex</b>.
     * 
     * @param faceA
     * @param faceB
     * @param vertex
     */
    static void getSharedVertices( FaceInfo faceA, FaceInfo faceB, int[] vertex )
    {
        vertex[ 0 ] = -1;
        vertex[ 1 ] = -1;
        
        int facev0 = faceB.m_v0;
        if ( facev0 == faceA.m_v0 || facev0 == faceA.m_v1 || facev0 == faceA.m_v2 )
        {
            if ( vertex[ 0 ] == -1 )
                vertex[ 0 ] = facev0;
            else
            {
                vertex[ 1 ] = facev0;
                return;
            }
        }
        
        int facev1 = faceB.m_v1;
        if ( facev1 == faceA.m_v0 || facev1 == faceA.m_v1 || facev1 == faceA.m_v2 )
        {
            if ( vertex[ 0 ] == -1 )
                vertex[ 0 ] = facev1;
            else
            {
                vertex[ 1 ] = facev1;
                return;
            }
        }
        
        int facev2 = faceB.m_v2;
        if ( facev2 == faceA.m_v0 || facev2 == faceA.m_v1 || facev2 == faceA.m_v2 )
        {
            if ( vertex[ 0 ] == -1 )
                vertex[ 0 ] = facev2;
            else
            {
                vertex[ 1 ] = facev2;
                return;
            }
        }
    }
    
    /**
     * "Commits" the input strips by setting their m_experimentId to -1 and
     * adding to the allStrips vector.
     * 
     * @param allStrips
     * @param strips
     */
    static void commitStrips( ArrayList< StripInfo > allStrips, ArrayList< StripInfo > strips )
    {
        // Iterate through strips
        int numStrips = strips.size();
        for ( int i = 0; i < numStrips; i++ )
        {
            
            // Tell the strip that it is now real
            StripInfo strip = strips.get( i );
            strip.m_experimentId = -1;
            
            // add to the list of real strips
            allStrips.add( strip );
            
            // Iterate through the faces of the strip
            // Tell the faces of the strip that they belong to a real strip now
            ArrayList< FaceInfo > faces = strips.get( i ).m_faces;
            int numFaces = faces.size();
            
            for ( int j = 0; j < numFaces; j++ )
            {
                strip.markTriangle( faces.get( j ) );
            }
        }
    }
    
    /**
     * @param numIndices
     * 
     * @return true if the next face should be ordered in CW fashion
     */
    static boolean nextIsCW( int numIndices )
    {
        return ( ( numIndices % 2 ) == 0 );
    }
    
    /**
     * Updates the input vertex cache with this face's vertices.
     * 
     * @param vcache
     * @param face
     */
    static void updateCacheFace( VertexCache vcache, FaceInfo face )
    {
        if ( !vcache.inCache( face.m_v0 ) )
            vcache.addEntry( face.m_v0 );
        
        if ( !vcache.inCache( face.m_v1 ) )
            vcache.addEntry( face.m_v1 );
        
        if ( !vcache.inCache( face.m_v2 ) )
            vcache.addEntry( face.m_v2 );
    }
    
    /**
     * Updates the input vertex cache with this strip's vertices.
     * 
     * @param vcache
     * @param strip
     */
    static void updateCacheStrip( VertexCache vcache, StripInfo strip )
    {
        for ( int i = 0; i < strip.m_faces.size(); ++i )
        {
            if ( !vcache.inCache( strip.m_faces.get( i ).m_v0 ) )
                vcache.addEntry( strip.m_faces.get( i ).m_v0 );
            
            if ( !vcache.inCache( strip.m_faces.get( i ).m_v1 ) )
                vcache.addEntry( strip.m_faces.get( i ).m_v1 );
            
            if ( !vcache.inCache( strip.m_faces.get( i ).m_v2 ) )
                vcache.addEntry( strip.m_faces.get( i ).m_v2 );
        }
    }
    
    /**
     * @param vcache
     * @param strip
     * 
     * @return the number of cache hits per face in the strip
     */
    static float calcNumHitsStrip( VertexCache vcache, StripInfo strip )
    {
        int numHits = 0;
        int numFaces = 0;
        
        for ( int i = 0; i < strip.m_faces.size(); i++ )
        {
            if ( vcache.inCache( strip.m_faces.get( i ).m_v0 ) )
                ++numHits;
            
            if ( vcache.inCache( strip.m_faces.get( i ).m_v1 ) )
                ++numHits;
            
            if ( vcache.inCache( strip.m_faces.get( i ).m_v2 ) )
                ++numHits;
            
            numFaces++;
        }
        
        return ( (float)numHits / (float)numFaces );
    }
    
    /**
     * Finds the average strip size of the input vector of strips.
     * @param strips
     * 
     * @return the average strip size of the input vector of strips
     */
    static float avgStripSize( ArrayList< StripInfo > strips )
    {
        int sizeAccum = 0;
        int numStrips = strips.size();
        for ( int i = 0; i < numStrips; i++ )
        {
            StripInfo strip = strips.get( i );
            sizeAccum += strip.m_faces.size();
            sizeAccum -= strip.m_numDegenerates;
        }
        
        return ( (float)sizeAccum ) / ( (float)numStrips );
    }
    
    /**
     * @param vcache
     * @param face
     * @return the number of cache hits in the face
     */
    static int calcNumHitsFace( VertexCache vcache, FaceInfo face )
    {
        int numHits = 0;
        
        if ( vcache.inCache( face.m_v0 ) )
            numHits++;
        
        if ( vcache.inCache( face.m_v1 ) )
            numHits++;
        
        if ( vcache.inCache( face.m_v2 ) )
            numHits++;
        
        return numHits;
    }
    
    /**
     * @param face
     * @param edgeInfoVec
     * @return the number of neighbors that this face has
     */
    static int numNeighbors( FaceInfo face, ArrayList< EdgeInfo > edgeInfoVec )
    {
        int numNeighbors = 0;
        
        if ( findOtherFace( edgeInfoVec, face.m_v0, face.m_v1, face ) != null )
        {
            numNeighbors++;
        }
        
        if ( findOtherFace( edgeInfoVec, face.m_v1, face.m_v2, face ) != null )
        {
            numNeighbors++;
        }
        
        if ( findOtherFace( edgeInfoVec, face.m_v2, face.m_v0, face ) != null )
        {
            numNeighbors++;
        }
        
        return numNeighbors;
    }
    
    /**
     * @param faceInfo
     * @param v0
     * @param v1
     * 
     * @return true if the face is ordered in CW fashion
     */
    static boolean isCW( FaceInfo faceInfo, int v0, int v1 )
    {
        if ( faceInfo.m_v0 == v0 )
            return ( faceInfo.m_v1 == v1 );
        else if ( faceInfo.m_v1 == v0 )
            return ( faceInfo.m_v2 == v1 );
        else
            return ( faceInfo.m_v0 == v1 );
    }
    
    static boolean faceContainsIndex( FaceInfo face, int index )
    {
        return ( ( face.m_v0 == index ) || ( face.m_v1 == index ) || ( face.m_v2 == index ) );
    }
    
    /**
     * Finds the next face to start the next strip on.
     * 
     * @param faceInfos
     * @param edgeInfos
     * @param strip
     * @param startInfo
     * 
     * @return the next face to start the next strip on
     */
    static boolean findTraversal( ArrayList< FaceInfo > faceInfos, ArrayList< EdgeInfo > edgeInfos, StripInfo strip, StripStartInfo startInfo )
    {
        // if the strip was v0.v1 on the edge, then v1 will be a vertex in the
        // next edge.
        int v = ( strip.m_startInfo.m_toV1 ? strip.m_startInfo.m_startEdge.m_v1 : strip.m_startInfo.m_startEdge.m_v0 );
        
        FaceInfo untouchedFace = null;
        EdgeInfo edgeIter = edgeInfos.get( v );
        while ( edgeIter != null )
        {
            FaceInfo face0 = edgeIter.m_face0;
            FaceInfo face1 = edgeIter.m_face1;
            if ( ( face0 != null && !strip.isInStrip( face0 ) ) && face1 != null && !strip.isMarked( face1 ) )
            {
                untouchedFace = face1;
                break;
            }
            if ( ( face1 != null && !strip.isInStrip( face1 ) ) && face0 != null && !strip.isMarked( face0 ) )
            {
                untouchedFace = face0;
                break;
            }
            
            // find the next edgeIter
            edgeIter = ( edgeIter.m_v0 == v ? edgeIter.m_nextV0 : edgeIter.m_nextV1 );
        }
        
        startInfo.m_startFace = untouchedFace;
        startInfo.m_startEdge = edgeIter;
        if ( edgeIter != null )
        {
            if ( strip.sharesEdge( startInfo.m_startFace, edgeInfos ) )
                startInfo.m_toV1 = ( edgeIter.m_v0 == v ); //note! used to be
            // m_v1
            else
                startInfo.m_toV1 = ( edgeIter.m_v1 == v );
        }
        
        return ( startInfo.m_startFace != null );
    }
    
    /**
     * @param allStrips the whole strip vector...all small strips will be deleted
     *                  from this list, to avoid leaking mem
     * @param allBigStrips an out parameter which will contain all strips above minStripLength
     * @param faceList an out parameter which will contain all faces which were faceList is
     *                 removed from the striplist
     */
    void removeSmallStrips( ArrayList< StripInfo > allStrips, ArrayList< StripInfo > allBigStrips, ArrayList< FaceInfo > faceList )
    {
        faceList.clear();
        allBigStrips.clear(); //make sure these are empty
        ArrayList< FaceInfo > tempFaceList = new ArrayList< FaceInfo >();
        
        for ( int i = 0; i < allStrips.size(); i++ )
        {
            if ( allStrips.get( i ).m_faces.size() < minStripLength )
            {
                //strip is too small, add faces to faceList
                for ( int j = 0; j < allStrips.get( i ).m_faces.size(); j++ )
                    tempFaceList.add( allStrips.get( i ).m_faces.get( j ) );
            }
            else
            {
                allBigStrips.add( allStrips.get( i ) );
            }
        }
        
        boolean[] bVisitedList = new boolean[ tempFaceList.size() ];
        
        VertexCache vcache = new VertexCache( cacheSize );
        
        int bestNumHits = -1;
        int numHits;
        int bestIndex = -9999;
        
        while ( true )
        {
            bestNumHits = -1;
            
            //find best face to add next, given the current cache
            for ( int i = 0; i < tempFaceList.size(); i++ )
            {
                if ( bVisitedList[ i ] )
                    continue;
                
                numHits = calcNumHitsFace( vcache, tempFaceList.get( i ) );
                if ( numHits > bestNumHits )
                {
                    bestNumHits = numHits;
                    bestIndex = i;
                }
            }
            
            if ( bestNumHits == -1.0f )
                break;
            bVisitedList[ bestIndex ] = true;
            updateCacheFace( vcache, tempFaceList.get( bestIndex ) );
            faceList.add( tempFaceList.get( bestIndex ) );
        }
    }
    
    /**
     * Generates actual strips from the list-in-strip-order.
     * 
     * @param allStrips
     * @param stripIndices
     * @param bStitchStrips
     */
    int createStrips( ArrayList< StripInfo > allStrips, Vector< Integer > stripIndices, boolean bStitchStrips )
    {
        int numSeparateStrips = 0;
        
        FaceInfo tLastFace = new FaceInfo( 0, 0, 0 );
        int nStripCount = allStrips.size();
        assert ( nStripCount > 0 );
        
        //we infer the cw/ccw ordering depending on the number of indices
        //this is screwed up by the fact that we insert -1s to denote changing
        // strips
        //this is to account for that
        int accountForNegatives = 0;
        
        for ( int i = 0; i < nStripCount; i++ )
        {
            StripInfo strip = allStrips.get( i );
            int nStripFaceCount = strip.m_faces.size();
            assert ( nStripFaceCount > 0 );
            
            // Handle the first face in the strip
            {
                FaceInfo tFirstFace = new FaceInfo( strip.m_faces.get( 0 ).m_v0, strip.m_faces.get( 0 ).m_v1, strip.m_faces.get( 0 ).m_v2 );
                
                // If there is a second face, reorder vertices such that the
                // unique vertex is first
                if ( nStripFaceCount > 1 )
                {
                    int nUnique = getUniqueVertexInB( strip.m_faces.get( 1 ), tFirstFace );
                    if ( nUnique == tFirstFace.m_v1 )
                    {
                        int tmp = tFirstFace.m_v0;
                        tFirstFace.m_v0 = tFirstFace.m_v1;
                        tFirstFace.m_v1 = tmp;
                    }
                    else if ( nUnique == tFirstFace.m_v2 )
                    {
                        int tmp = tFirstFace.m_v0;
                        tFirstFace.m_v0 = tFirstFace.m_v2;
                        tFirstFace.m_v2 = tmp;
                    }
                    
                    // If there is a third face, reorder vertices such that the
                    // shared vertex is last
                    if ( nStripFaceCount > 2 )
                    {
                        if ( isDegenerate( strip.m_faces.get( 1 ) ) )
                        {
                            int pivot = strip.m_faces.get( 1 ).m_v1;
                            if ( tFirstFace.m_v1 == pivot )
                            {
                                int tmp = tFirstFace.m_v1;
                                tFirstFace.m_v1 = tFirstFace.m_v2;
                                tFirstFace.m_v2 = tmp;
                            }
                        }
                        else
                        {
                            int[] nShared = new int[ 2 ];
                            getSharedVertices( strip.m_faces.get( 2 ), tFirstFace, nShared );
                            if ( ( nShared[ 0 ] == tFirstFace.m_v1 ) && ( nShared[ 1 ] == -1 ) )
                            {
                                int tmp = tFirstFace.m_v1;
                                tFirstFace.m_v1 = tFirstFace.m_v2;
                                tFirstFace.m_v2 = tmp;
                            }
                        }
                    }
                }
                
                if ( ( i == 0 ) || !bStitchStrips )
                {
                    if ( !isCW( strip.m_faces.get( 0 ), tFirstFace.m_v0, tFirstFace.m_v1 ) )
                        stripIndices.add( tFirstFace.m_v0 );
                }
                else
                {
                    // Double tap the first in the new strip
                    stripIndices.add( tFirstFace.m_v0 );
                    
                    // Check CW/CCW ordering
                    if ( nextIsCW( stripIndices.size() - accountForNegatives ) != isCW( strip.m_faces.get( 0 ), tFirstFace.m_v0, tFirstFace.m_v1 ) )
                    {
                        stripIndices.add( tFirstFace.m_v0 );
                    }
                }
                
                stripIndices.add( tFirstFace.m_v0 );
                stripIndices.add( tFirstFace.m_v1 );
                stripIndices.add( tFirstFace.m_v2 );
                
                // Update last face info
                tLastFace.set( tFirstFace );
            }
            
            for ( int j = 1; j < nStripFaceCount; j++ )
            {
                int nUnique = getUniqueVertexInB( tLastFace, strip.m_faces.get( j ) );
                if ( nUnique != -1 )
                {
                    stripIndices.add( nUnique );
                    
                    // Update last face info
                    tLastFace.m_v0 = tLastFace.m_v1;
                    tLastFace.m_v1 = tLastFace.m_v2;
                    tLastFace.m_v2 = nUnique;
                }
                else
                {
                    //we've hit a degenerate
                    stripIndices.add( strip.m_faces.get( j ).m_v2 );
                    tLastFace.m_v0 = strip.m_faces.get( j ).m_v0; //tLastFace.m_v1;
                    tLastFace.m_v1 = strip.m_faces.get( j ).m_v1; //tLastFace.m_v2;
                    tLastFace.m_v2 = strip.m_faces.get( j ).m_v2; //tLastFace.m_v1;
                    
                }
            }
            
            // Double tap between strips.
            if ( bStitchStrips )
            {
                if ( i != nStripCount - 1 )
                    stripIndices.add( tLastFace.m_v2 );
            }
            else
            {
                //-1 index indicates next strip
                stripIndices.add( -1 );
                accountForNegatives++;
                numSeparateStrips++;
            }
            
            // Update last face info
            tLastFace.m_v0 = tLastFace.m_v1;
            tLastFace.m_v1 = tLastFace.m_v2;
            tLastFace.m_v2 = tLastFace.m_v2;
        }
        
        if ( bStitchStrips )
            numSeparateStrips = 1;
        return numSeparateStrips;
    }
    
    /**
     * Does the stripification, puts output strips into vector allStrips
     * 
     * Works by setting runnning a number of experiments in different areas of
     * the mesh, and accepting the one which results in the longest strips. It then accepts
     * this, and moves on to a different area of the mesh. We try to jump around the mesh some,
     * to ensure that large open spans of strips get generated.
     * 
     * @param allStrips
     * @param allFaceInfos
     * @param allEdgeInfos
     * @param numSamples
     */
    void findAllStrips( ArrayList< StripInfo > allStrips, ArrayList< FaceInfo > allFaceInfos, ArrayList< EdgeInfo > allEdgeInfos, int numSamples )
    {
        // the experiments
        int experimentId = 0;
        int stripId = 0;
        boolean done = false;
        
        int loopCtr = 0;
        
        while ( !done )
        {
            loopCtr++;
            
            //
            // PHASE 1: Set up numSamples * numEdges experiments
            //
            ArrayList< ArrayList< StripInfo >> experiments = new ArrayList< ArrayList< StripInfo >>( numSamples * 6 );
            for ( int i = 0; i < numSamples * 6; i++ )
                experiments.add( new ArrayList< StripInfo >() );
            
            int experimentIndex = 0;
            HashSet< FaceInfo > resetPoints = new HashSet< FaceInfo >();
            for ( int i = 0; i < numSamples; i++ )
            {
                // Try to find another good reset point.
                // If there are none to be found, we are done
                FaceInfo nextFace = findGoodResetPoint( allFaceInfos, allEdgeInfos );
                if ( nextFace == null )
                {
                    done = true;
                    break;
                }
                // If we have already evaluated starting at this face in this
                // slew
                // of experiments, then skip going any further
                else if ( resetPoints.contains( nextFace ) )
                {
                    continue;
                }
                
                // trying it now...
                resetPoints.add( nextFace );
                
                // otherwise, we shall now try experiments for starting on the
                // 01,12, and 20 edges
                assert ( nextFace.m_stripId < 0 );
                
                // build the strip off of this face's 0-1 edge
                EdgeInfo edge01 = findEdgeInfo( allEdgeInfos, nextFace.m_v0, nextFace.m_v1 );
                StripInfo strip01 = new StripInfo( new StripStartInfo( nextFace, edge01, true ), stripId++, experimentId++ );
                experiments.get( experimentIndex++ ).add( strip01 );
                
                // build the strip off of this face's 1-0 edge
                EdgeInfo edge10 = findEdgeInfo( allEdgeInfos, nextFace.m_v0, nextFace.m_v1 );
                StripInfo strip10 = new StripInfo( new StripStartInfo( nextFace, edge10, false ), stripId++, experimentId++ );
                experiments.get( experimentIndex++ ).add( strip10 );
                
                // build the strip off of this face's 1-2 edge
                EdgeInfo edge12 = findEdgeInfo( allEdgeInfos, nextFace.m_v1, nextFace.m_v2 );
                StripInfo strip12 = new StripInfo( new StripStartInfo( nextFace, edge12, true ), stripId++, experimentId++ );
                experiments.get( experimentIndex++ ).add( strip12 );
                
                // build the strip off of this face's 2-1 edge
                EdgeInfo edge21 = findEdgeInfo( allEdgeInfos, nextFace.m_v1, nextFace.m_v2 );
                StripInfo strip21 = new StripInfo( new StripStartInfo( nextFace, edge21, false ), stripId++, experimentId++ );
                experiments.get( experimentIndex++ ).add( strip21 );
                
                // build the strip off of this face's 2-0 edge
                EdgeInfo edge20 = findEdgeInfo( allEdgeInfos, nextFace.m_v2, nextFace.m_v0 );
                StripInfo strip20 = new StripInfo( new StripStartInfo( nextFace, edge20, true ), stripId++, experimentId++ );
                experiments.get( experimentIndex++ ).add( strip20 );
                
                // build the strip off of this face's 0-2 edge
                EdgeInfo edge02 = findEdgeInfo( allEdgeInfos, nextFace.m_v2, nextFace.m_v0 );
                StripInfo strip02 = new StripInfo( new StripStartInfo( nextFace, edge02, false ), stripId++, experimentId++ );
                experiments.get( experimentIndex++ ).add( strip02 );
            }
            
            //
            // PHASE 2: Iterate through that we setup in the last phase
            // and really build each of the strips and strips that follow to
            // see how
            // far we get
            //
            int numExperiments = experimentIndex;
            for ( int i = 0; i < numExperiments; i++ )
            {
                
                // get the strip set
                
                // build the first strip of the list
                experiments.get( i ).get( 0 ).build( allEdgeInfos, allFaceInfos );
                int experimentId2 = experiments.get( i ).get( 0 ).m_experimentId;
                
                StripInfo stripIter = experiments.get( i ).get( 0 );
                StripStartInfo startInfo = new StripStartInfo( null, null, false );
                while ( findTraversal( allFaceInfos, allEdgeInfos, stripIter, startInfo ) )
                {
                    
                    // create the new strip info
                    //TODO startInfo clone ?
                    stripIter = new StripInfo( startInfo, stripId++, experimentId2 );
                    
                    // build the next strip
                    stripIter.build( allEdgeInfos, allFaceInfos );
                    
                    // add it to the list
                    experiments.get( i ).add( stripIter );
                }
            }
            
            //
            // Phase 3: Find the experiment that has the most promise
            //
            int bestIndex = 0;
            double bestValue = 0;
            for ( int i = 0; i < numExperiments; i++ )
            {
                float avgStripSizeWeight = 1.0f;
                float numStripsWeight = 0.0f;
                float avgStripSize = avgStripSize( experiments.get( i ) );
                float numStrips = (float)experiments.get( i ).size();
                float value = avgStripSize * avgStripSizeWeight + ( numStrips * numStripsWeight );
                //float value = 1.f / numStrips;
                //float value = numStrips * avgStripSize;
                
                if ( value > bestValue )
                {
                    bestValue = value;
                    bestIndex = i;
                }
            }
            
            //
            // Phase 4: commit the best experiment of the bunch
            //
            commitStrips( allStrips, experiments.get( bestIndex ) );
        }
    }
    
    /**
     * Splits the input vector of strips (allBigStrips) into smaller, cache
     * friendly pieces, then reorders these pieces to maximize cache hits
     * The final strips are output through outStrips.
     * 
     * @param allStrips
     * @param outStrips
     * @param edgeInfos
     * @param outFaceList
     */
    void splitUpStripsAndOptimize( ArrayList< StripInfo > allStrips, ArrayList< StripInfo > outStrips, ArrayList< EdgeInfo > edgeInfos, ArrayList< FaceInfo > outFaceList )
    {
        int threshold = cacheSize;
        ArrayList< StripInfo > tempStrips = new ArrayList< StripInfo >();
        int j;
        
        //split up strips into threshold-sized pieces
        for ( int i = 0; i < allStrips.size(); i++ )
        {
            StripInfo currentStrip;
            StripStartInfo startInfo = new StripStartInfo( null, null, false );
            
            int actualStripSize = 0;
            for ( j = 0; j < allStrips.get( i ).m_faces.size(); ++j )
            {
                if ( !isDegenerate( allStrips.get( i ).m_faces.get( j ) ) )
                    actualStripSize++;
            }
            
            if ( actualStripSize /* allStrips.at(i).m_faces.size() */> threshold )
            {
                
                int numTimes = actualStripSize /* allStrips.at(i).m_faces.size() *// threshold;
                int numLeftover = actualStripSize /* allStrips.at(i).m_faces.size() */% threshold;
                
                int degenerateCount = 0;
                for ( j = 0; j < numTimes; j++ )
                {
                    currentStrip = new StripInfo( startInfo, 0, -1 );
                    
                    int faceCtr = j * threshold + degenerateCount;
                    boolean bFirstTime = true;
                    while ( faceCtr < threshold + ( j * threshold ) + degenerateCount )
                    {
                        if ( isDegenerate( allStrips.get( i ).m_faces.get( faceCtr ) ) )
                        {
                            degenerateCount++;
                            
                            //last time or first time through, no need for a
                            // degenerate
                            if ( ( ( ( faceCtr + 1 ) != threshold + ( j * threshold ) + degenerateCount ) || ( ( j == numTimes - 1 ) && ( numLeftover < 4 ) && ( numLeftover > 0 ) ) ) && !bFirstTime )
                            {
                                currentStrip.m_faces.add( allStrips.get( i ).m_faces.get( faceCtr++ ) );
                            }
                            else
                                ++faceCtr;
                        }
                        else
                        {
                            currentStrip.m_faces.add( allStrips.get( i ).m_faces.get( faceCtr++ ) );
                            bFirstTime = false;
                        }
                    }
                    /*
                     * threshold; faceCtr
                     * < threshold+(j*threshold); faceCtr++) {
                     * currentStrip.m_faces.add(allStrips.at(i).m_faces.at(faceCtr]); }
                     */
                    ///*
                    if ( j == numTimes - 1 ) //last time through
                    {
                        if ( ( numLeftover < 4 ) && ( numLeftover > 0 ) ) //way too
                        // small
                        {
                            //just add to last strip
                            int ctr = 0;
                            while ( ctr < numLeftover )
                            {
                                if ( !isDegenerate( allStrips.get( i ).m_faces.get( faceCtr ) ) )
                                {
                                    currentStrip.m_faces.add( allStrips.get( i ).m_faces.get( faceCtr++ ) );
                                    ++ctr;
                                }
                                else
                                {
                                    currentStrip.m_faces.add( allStrips.get( i ).m_faces.get( faceCtr++ ) );
                                    ++degenerateCount;
                                }
                            }
                            numLeftover = 0;
                        }
                    }
                    //*/
                    tempStrips.add( currentStrip );
                }
                
                int leftOff = j * threshold + degenerateCount;
                
                if ( numLeftover != 0 )
                {
                    currentStrip = new StripInfo( startInfo, 0, -1 );
                    
                    int ctr = 0;
                    boolean bFirstTime = true;
                    while ( ctr < numLeftover )
                    {
                        if ( !isDegenerate( allStrips.get( i ).m_faces.get( leftOff ) ) )
                        {
                            ctr++;
                            bFirstTime = false;
                            currentStrip.m_faces.add( allStrips.get( i ).m_faces.get( leftOff++ ) );
                        }
                        else if ( !bFirstTime )
                            currentStrip.m_faces.add( allStrips.get( i ).m_faces.get( leftOff++ ) );
                        else
                            leftOff++;
                    }
                    /*
                     * for(int k = 0; k < numLeftover; k++) {
                     * currentStrip.m_faces.add(allStrips.at(i).m_faces[leftOff++]); }
                     */

                    tempStrips.add( currentStrip );
                }
            }
            else
            {
                //we're not just doing a tempStrips.add(allBigStrips[i])
                // because
                // this way we can delete allBigStrips later to free the memory
                currentStrip = new StripInfo( startInfo, 0, -1 );
                
                for ( j = 0; j < allStrips.get( i ).m_faces.size(); j++ )
                    currentStrip.m_faces.add( allStrips.get( i ).m_faces.get( j ) );
                
                tempStrips.add( currentStrip );
            }
        }
        
        //add small strips to face list
        ArrayList< StripInfo > tempStrips2 = new ArrayList< StripInfo >();
        removeSmallStrips( tempStrips, tempStrips2, outFaceList );
        
        outStrips.clear();
        //screw optimization for now
        //  for(i = 0; i < tempStrips.size(); ++i)
        //    outStrips.add(tempStrips[i]);
        
        if ( tempStrips2.size() != 0 )
        {
            //Optimize for the vertex cache
            VertexCache vcache = new VertexCache( cacheSize );
            
            float bestNumHits = -1.0f;
            float numHits;
            int bestIndex = -99999;
            
            int firstIndex = 0;
            float minCost = 10000.0f;
            
            for ( int i = 0; i < tempStrips2.size(); i++ )
            {
                int numNeighbors = 0;
                
                //find strip with least number of neighbors per face
                for ( j = 0; j < tempStrips2.get( i ).m_faces.size(); j++ )
                {
                    numNeighbors += numNeighbors( tempStrips2.get( i ).m_faces.get( j ), edgeInfos );
                }
                
                float currCost = (float)numNeighbors / (float)tempStrips2.get( i ).m_faces.size();
                if ( currCost < minCost )
                {
                    minCost = currCost;
                    firstIndex = i;
                }
            }
            
            updateCacheStrip( vcache, tempStrips2.get( firstIndex ) );
            outStrips.add( tempStrips2.get( firstIndex ) );
            
            tempStrips2.get( firstIndex ).visited = true;
            
            boolean bWantsCW = ( tempStrips2.get( firstIndex ).m_faces.size() % 2 ) == 0;
            
            //this n^2 algo is what slows down stripification so much....
            // needs to be improved
            while ( true )
            {
                bestNumHits = -1.0f;
                
                //find best strip to add next, given the current cache
                for ( int i = 0; i < tempStrips2.size(); i++ )
                {
                    if ( tempStrips2.get( i ).visited )
                        continue;
                    
                    numHits = calcNumHitsStrip( vcache, tempStrips2.get( i ) );
                    if ( numHits > bestNumHits )
                    {
                        bestNumHits = numHits;
                        bestIndex = i;
                    }
                    else if ( numHits >= bestNumHits )
                    {
                        //check previous strip to see if this one requires it
                        // to switch polarity
                        StripInfo strip = tempStrips2.get( i );
                        int nStripFaceCount = strip.m_faces.size();
                        
                        FaceInfo tFirstFace = new FaceInfo( strip.m_faces.get( 0 ).m_v0, strip.m_faces.get( 0 ).m_v1, strip.m_faces.get( 0 ).m_v2 );
                        
                        // If there is a second face, reorder vertices such
                        // that the
                        // unique vertex is first
                        if ( nStripFaceCount > 1 )
                        {
                            int nUnique = getUniqueVertexInB( strip.m_faces.get( 1 ), tFirstFace );
                            if ( nUnique == tFirstFace.m_v1 )
                            {
                                int tmp = tFirstFace.m_v0;
                                tFirstFace.m_v0 = tFirstFace.m_v1;
                                tFirstFace.m_v1 = tmp;
                            }
                            else if ( nUnique == tFirstFace.m_v2 )
                            {
                                int tmp = tFirstFace.m_v0;
                                tFirstFace.m_v0 = tFirstFace.m_v2;
                                tFirstFace.m_v2 = tmp;
                            }
                            
                            // If there is a third face, reorder vertices such
                            // that the
                            // shared vertex is last
                            if ( nStripFaceCount > 2 )
                            {
                                int[] nShared = new int[ 2 ];
                                getSharedVertices( strip.m_faces.get( 2 ), tFirstFace, nShared );
                                if ( ( nShared[ 0 ] == tFirstFace.m_v1 ) && ( nShared[ 1 ] == -1 ) )
                                {
                                    int tmp = tFirstFace.m_v2;
                                    tFirstFace.m_v2 = tFirstFace.m_v1;
                                    tFirstFace.m_v1 = tmp;
                                }
                            }
                        }
                        
                        // Check CW/CCW ordering
                        if ( bWantsCW == isCW( strip.m_faces.get( 0 ), tFirstFace.m_v0, tFirstFace.m_v1 ) )
                        {
                            //I like this one!
                            bestIndex = i;
                        }
                    }
                }
                
                if ( bestNumHits == -1.0f )
                    break;
                tempStrips2.get( bestIndex ).visited = true;
                updateCacheStrip( vcache, tempStrips2.get( bestIndex ) );
                outStrips.add( tempStrips2.get( bestIndex ) );
                bWantsCW = ( tempStrips2.get( bestIndex ).m_faces.size() % 2 == 0 ) ? bWantsCW : !bWantsCW;
            }
        }
    }
    
    /**
     * @param in_indices the input indices of the mesh to stripify
     * @param in_cacheSize the target cache size.
     * @param in_minStripLength
     * @param maxIndex
     * @param outStrips
     * @param outFaceList
     */
    void stripify( Vector< Integer > in_indices, int in_cacheSize, int in_minStripLength, int maxIndex, ArrayList< StripInfo > outStrips, ArrayList< FaceInfo > outFaceList )
    {
        meshJump = 0.0f;
        bFirstTimeResetPoint = true; //used in FindGoodResetPoint()
        
        //the number of times to run the experiments
        int numSamples = 10;
        
        //the cache size, clamped to one
        cacheSize = Math.max( 1, in_cacheSize - CACHE_INEFFICIENCY );
        
        minStripLength = in_minStripLength;
        //this is the strip size threshold below which we dump the strip into
        // a list
        
        indices = in_indices;
        
        // build the stripification info
        ArrayList< FaceInfo > allFaceInfos = new ArrayList< FaceInfo >();
        ArrayList< EdgeInfo > allEdgeInfos = new ArrayList< EdgeInfo >();
        
        buildStripifyInfo( allFaceInfos, allEdgeInfos, maxIndex );
        
        ArrayList< StripInfo > allStrips = new ArrayList< StripInfo >();
        
        // stripify
        findAllStrips( allStrips, allFaceInfos, allEdgeInfos, numSamples );
        
        //split up the strips into cache friendly pieces, optimize them, then
        // dump these into outStrips
        splitUpStripsAndOptimize( allStrips, outStrips, allEdgeInfos, outFaceList );
    }
}
