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
/**
 * :Id: GridIndexedFileHeap.java,v 1.5 2003/02/24 00:13:51 wurp Exp $
 *
 * :Log: GridIndexedFileHeap.java,v $
 * Revision 1.5  2003/02/24 00:13:51  wurp
 * Formatted all java code for cvs (strictSunConvention.xml)
 *
 * Revision 1.4  2001/06/20 04:05:42  wurp
 * added log4j.
 *
 * Revision 1.3  2001/01/28 07:52:19  wurp
 * Removed <dollar> from Id and Log in log comments.
 * Added several new commands to AdminApp
 * Unfortunately, several other changes that I have lost track of.  Try diffing this
 * version with the previous one.
 *
 * Revision 1.2  2000/12/16 22:07:33  wurp
 * Added Id and Log to almost all of the files that didn't have it.  It's
 * possible that the script screwed something up.  I did a commit and an update
 * right before I ran the script, so if a file is screwed up you should be able
 * to fix it by just going to the version before this one.
 */
package org.xith3d.utility.general;

/**
 * Generic utility class that indexes a random access file by a grid tree.  It is
 * extremely fast for grid style lookups and handles sparse data very well.  The leafs can
 * be an arbitrary sized data chunks.
 *
 * Manages an indexed file heap.
 * For example if you have a granularity of 10, depth of 4 and leafSize of 20 you can
 * index a grid of 10^4 * 20 by 10^4 * 20, or 200,000 x 200,2000  This means you can retrieve
 * a data handle for the data stored at x,z = (0..n,0..n) where N is 200k with 4 key node
 * retrievals.  If there is no data at that leaf then it could require less actual key
 * traversals since the key children might be null if that entire sub-tree is empty.
 *
 * In the above example each key node will be a 10x10 array of children pointers, where
 * any pointer can be null, indicating there is no data below that part of the tree.  The
 * storage is most efficient for sparse data.  In the pathalogical case of every possible
 * leaf being stored, the indexing overhead would probably surpass the stored data, and since
 * it could have been stored in a contiguous array with offsets being easily calculated, this
 * results in possibly an extremely inefficient usage. Note for these cases it is far better to
 * use a tree of depth one, so at least you can make use of the file heap and buffering, even
 * if the indexing no longer buys you anything.
 *
 * An example of this would be terrain heightmaps.  If some large percentage of the world
 * is unknown, or is water, you will realize some nice saves in storage, while retaining the
 * ability to load chunks of heightmaps quickly and with buffering.
 *
 * In the case of landscape details like trees and rocks, these can be fairly sparse and you can
 * easily have
 *
 * You can also request all non-null children for a range of (x1,z1) .. (x2,z2) and expect
 * extremely fast retrievals.
 * 
 * @author David Yazel
 */
public class GridIndexedFileHeap
{
    
    /**
     * Constructs the indexed file heap.
     *
     * @param filename The file to store the information
     * @param keyBufferSize  The amount of memory to use for buffering key nodes
     * @param leafBufferSize The amount of memory to use for buffering leaf nodes
     * @param granularity The dimension of each key grid N x N
     * @param depth The number of levels deep to build keys
     * @param leafSize
     */
    private GridIndexedFileHeap( String filename, int keyBufferSize, int leafBufferSize, int granularity, int depth, int leafSize )
    {
    }
}
