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

import org.openmali.spatial.bodies.Classifier;
import org.openmali.spatial.bodies.Frustum;
import org.openmali.spatial.bodies.Classifier.Classification;
import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.octree.OcCell;
import org.openmali.spatial.octree.OcTree;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.picking.PickRay;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.preprocessing.FrustumCuller;
import org.xith3d.render.preprocessing.RenderBinProvider;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OcTreeGroup extends Group implements SpecialCullingNode< OcTreeGroup >
{
    private final OcTree<Node> ocTree;
    
    final OcTree<Node> getOcTree()
    {
        return ( ocTree );
    }
    
    public void setMinNodesBeforeSplit( int minNodesBeforeSplit )
    {
        ocTree.setMinNodesBeforeSplit( minNodesBeforeSplit );
    }
    
    public final int getMinNodesBeforeSplit()
    {
        return ( ocTree.getMinNodesBeforeSplit() );
    }
    
    public void setMaxLevelForExtendedCells( int maxLevelForExtendedCells )
    {
        ocTree.setMaxLevelForExtendedCells( maxLevelForExtendedCells );
    }
    
    public final int getMaxLevelForExtendedCells()
    {
        return ( ocTree.getMaxLevelForExtendedCells() );
    }
    
    public final int getMaxOcTreeLevel()
    {
        return ( ocTree.getMaxLevel() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild( Node child, int index )
    {
        super.addChild( child, index );
        
        ocTree.insertNode( child );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node removeChild( int index )
    {
        Node node = super.removeChild( index );
        
        ocTree.removeNode( node );
        
        return ( node );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllChildren()
    {
        super.removeAllChildren();
        
        ocTree.clear();
    }
    
    /**
     * This method must be called after a node's position or size has changed.
     * You don't need to call this method, if you exactly know, that the
     * modification won't affect the node's placement in the tree.
     * 
     * @param node
     */
    public final void updateNodePosition( Node node )
    {
        if ( node.getParent() != this )
            throw new Error( "The given Node is not in this group." );
        
        ocTree.updateNodePosition( node );
    }
    
    @Override
    public final void dump()
    {
        ocTree.dump();
    }
    
    /**
     * This is a very obvious standard OcTree-culling code.
     */
    private final void cullOcTreeAtoms( Classification parentClassify, OcCell<Node> cell, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller )
    {
        //if ( ( cell.getNumNodes() == 0 ) && !cell.hasChildCells() )
        //    return ( 0 );
        
        Classification classify = parentClassify;
        
        if ( !cullingSuppressed && ( classify != Classification.INSIDE ) )
        {
            classify = Classifier.classifyFrustumBox( frustum, cell );
            
            if ( classify == Classification.OUTSIDE )
                return;
        }
        else
        {
            //classify = Classification.INSIDE;
            classify = parentClassify;
            cullingSuppressed = true;
        }
        
        for ( int i = 0; i < cell.getNumNodes(); i++ )
        {
            frustumCuller.cullNodeAtoms( cell.getNode( i ), classify, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
            //frustumCuller.cullNodeAtoms( cell.getNode( i ), classify, true, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
        }
        
        if ( cell.hasChildCells() )
        {
            if ( cell.getCellOcLowerBackLeft() != null )
                cullOcTreeAtoms( classify, cell.getCellOcLowerBackLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( cell.getCellOcLowerBackRight() != null )
                cullOcTreeAtoms( classify, cell.getCellOcLowerBackRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( cell.getCellOcLowerFrontLeft() != null )
                cullOcTreeAtoms( classify, cell.getCellOcLowerFrontLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( cell.getCellOcLowerFrontRight() != null )
                cullOcTreeAtoms( classify, cell.getCellOcLowerFrontRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( cell.getCellOcUpperBackLeft() != null )
                cullOcTreeAtoms( classify, cell.getCellOcUpperBackLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( cell.getCellOcUpperBackRight() != null )
                cullOcTreeAtoms( classify, cell.getCellOcUpperBackRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( cell.getCellOcUpperFrontLeft() != null )
                cullOcTreeAtoms( classify, cell.getCellOcUpperFrontLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( cell.getCellOcUpperFrontRight() != null )
                cullOcTreeAtoms( classify, cell.getCellOcUpperFrontRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
        }
    }
    
    private static final byte B0 = (byte)0x00;
    private static final byte B1 = (byte)( 1 << 0 );
    private static final byte B2 = (byte)( 1 << 1 );
    private static final byte B3 = (byte)( 1 << 2 );
    private static final byte B4 = (byte)( 1 << 3 );
    private static final byte B5 = (byte)( 1 << 4 );
    private static final byte B6 = (byte)( 1 << 5 );
    private static final byte B7 = (byte)( 1 << 6 );
    private static final byte B8 = (byte)( 1 << 7 );
    
    /**
     * This methods traverses the an OcTree cell by cell.
     * This Ext-version also checks the extended cells.
     * A bitmask is modified, if a "dominating" extended cell was culled
     * to avoid unnecessary checks of standard-OcCells.
     */
    private final byte cullOcTreeAtomsExt( byte result, Classification parentClassify, OcCell<Node> cell, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller )
    {
        //if ( ( cell.getNumNodes() == 0 ) && !cell.hasChildCells() )
        //    return;
        
        Classification classify;
        
        if ( !cullingSuppressed && ( parentClassify != Classification.INSIDE ) )
        {
            classify = Classifier.classifyFrustumBox( frustum, cell );
            
            if ( classify == Classification.OUTSIDE )
                return ( result );
        }
        else
        {
            //classify = Classification.INSIDE;
            classify = parentClassify;
            cullingSuppressed = true;
        }
        
        byte culled = B0;
        
        //final boolean cs = cullingSuppressed || ( cell.getNumNodes() <= 4 );
        final boolean cs = cullingSuppressed;
        
        for ( int i = 0; i < cell.getNumNodes(); i++ )
        {
            frustumCuller.cullNodeAtoms( cell.getNode( i ), classify, cs, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
            //frustumCuller.cullNodeAtoms( cell.getNode( i ), classify, true, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass );
        }
        
        if ( cell.hasChildCells() )
        {
            if ( cell.usesExtendedCells() )
            {
                // B1 : OcLowerBackLeft
                // B2 : OcLowerBackRight
                // B3 : OcLowerFrontLeft
                // B4 : OcLowerFrontRight
                // B5 : OcUpperBackLeft
                // B6 : OcUpperBackRight
                // B7 : OcUpperFrontLeft
                // B8 : OcUpperFrontRight
                
                if ( cell.getCellQuUpperBack() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B5 | B6), classify, cell.getCellQuUpperBack(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuUpperFront() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B7 | B8), classify, cell.getCellQuUpperFront(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuUpperLeft() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B5 | B7), classify, cell.getCellQuUpperLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuUpperRight() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B6 | B8), classify, cell.getCellQuUpperRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuLowerBack() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B1 | B2), classify, cell.getCellQuLowerBack(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuLowerFront() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B3 | B4), classify, cell.getCellQuLowerFront(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuLowerLeft() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B1 | B3), classify, cell.getCellQuLowerLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuLowerRight() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B2 | B4), classify, cell.getCellQuLowerRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuBackLeft() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B1 | B5), classify, cell.getCellQuBackLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuBackRight() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B2 | B6), classify, cell.getCellQuBackRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuFrontLeft() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B3 | B7), classify, cell.getCellQuFrontLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellQuFrontRight() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B4 | B8), classify, cell.getCellQuFrontRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                
                // B1 : OcLowerBackLeft
                // B2 : OcLowerBackRight
                // B3 : OcLowerFrontLeft
                // B4 : OcLowerFrontRight
                // B5 : OcUpperBackLeft
                // B6 : OcUpperBackRight
                // B7 : OcUpperFrontLeft
                // B8 : OcUpperFrontRight
                
                if ( cell.getCellHUpper() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B5 | B6 | B7 | B8), classify, cell.getCellHUpper(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHLower() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B1 | B2 | B3 | B4), classify, cell.getCellHLower(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHLeft() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B1 | B3 | B5 | B7), classify, cell.getCellHLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHRight() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B2 | B4 | B6 | B8), classify, cell.getCellHRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHBack() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B1 | B2 | B5 | B6), classify, cell.getCellHBack(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHFront() != null )
                    culled |= cullOcTreeAtomsExt( (byte)(B3 | B4 | B7 | B8), classify, cell.getCellHFront(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            }
            
            // B1 : OcLowerBackLeft
            // B2 : OcLowerBackRight
            // B3 : OcLowerFrontLeft
            // B4 : OcLowerFrontRight
            // B5 : OcUpperBackLeft
            // B6 : OcUpperBackRight
            // B7 : OcUpperFrontLeft
            // B8 : OcUpperFrontRight
            
            if ( ( (byte)( culled & B1 ) == B0 ) && ( cell.getCellOcLowerBackLeft() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcLowerBackLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B2 ) == B0 ) && ( cell.getCellOcLowerBackRight() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcLowerBackRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B3 ) == B0 ) && ( cell.getCellOcLowerFrontLeft() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcLowerFrontLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B4 ) == B0 ) && ( cell.getCellOcLowerFrontRight() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcLowerFrontRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B5 ) == B0 ) && ( cell.getCellOcUpperBackLeft() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcUpperBackLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B6 ) == B0 ) && ( cell.getCellOcUpperBackRight() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcUpperBackRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B7 ) == B0 ) && ( cell.getCellOcUpperFrontLeft() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcUpperFrontLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B8 ) == B0 ) && ( cell.getCellOcUpperFrontRight() != null ) )
                cullOcTreeAtomsExt( B0, classify, cell.getCellOcUpperFrontRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
        }
        
        return ( B0 );
    }
    
    /**
     * Further traverses this group to find Shape3Ds.
     * Checks for state changes and cares for the state-stack.
     */
    private final void cullOcTreeAtoms( OcTreeGroup ocTreeGroup, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller )
    {
        if ( ocTreeGroup.getTransformGroup() != null )
        {
            throw new Error( "An OcTreeGroup must not be nested into a parent TransformGroup!" );
        }
        
        OcCell<Node> rootCell = _SG_PrivilegedAccess.getOcTree( ocTreeGroup ).getRootCell();
        
        if ( rootCell.usesExtendedCells() )
            cullOcTreeAtomsExt( B0, null, rootCell, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
        else
            cullOcTreeAtoms( null, rootCell, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
    }
    
    /**
     * {@inheritDoc}
     */
    public void cullSpecialNode( OcTreeGroup node, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller )
    {
        cullOcTreeAtoms( node, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
    }
    
    public OcTreeGroup( float centerX, float centerY, float centerZ, float sizeX, float sizeY, float sizeZ, boolean useExtendedCells )
    {
        this.ocTree = new OcTree<Node>( centerX, centerY, centerZ, sizeX, sizeY, sizeZ, useExtendedCells );
        
        //setBoundsAutoCompute( false );
        
        //this.getBounds().set( ocTree.getRootCell() );
        //this.setBounds( this.getBounds() );
        //this.setBounds( new BoundingSphere( 0f, 0f, 0f, 1000f ) );
        
        BoundingBox bb = new BoundingBox();
        //bb.set( ocTree.getRootCell() );
        this.setBounds( bb );
    }
    
    public OcTreeGroup( Tuple3f center, float sizeX, float sizeY, float sizeZ, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), sizeX, sizeY, sizeZ, useExtendedCells );
    }
    
    public OcTreeGroup( float centerX, float centerY, float centerZ, float size, boolean useExtendedCells )
    {
        this( centerX, centerY, centerZ, size, size, size, useExtendedCells );
    }
    
    public OcTreeGroup( Tuple3f center, float size, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), size, useExtendedCells );
    }
}
