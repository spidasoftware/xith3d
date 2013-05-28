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

import org.openmali.spatial.AxisIndicator;
import org.openmali.spatial.PlaneIndicator;
import org.openmali.spatial.bodies.Classifier;
import org.openmali.spatial.bodies.Frustum;
import org.openmali.spatial.bodies.Classifier.Classification;
import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.quadtree.QuadCell;
import org.openmali.spatial.quadtree.QuadTree;
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
public class QuadTreeGroup extends Group implements SpecialCullingNode< QuadTreeGroup >
{
    public static final PlaneIndicator DEFAULT_PLANE = QuadTree.DEFAULT_PLANE;
    
    private final QuadTree<Node> quadTree;
    
    final QuadTree<Node> getQuadTree()
    {
        return ( quadTree );
    }
    
    public void setMinNodesBeforeSplit( int minNodesBeforeSplit )
    {
        quadTree.setMinNodesBeforeSplit( minNodesBeforeSplit );
    }
    
    public final int getMinNodesBeforeSplit()
    {
        return ( quadTree.getMinNodesBeforeSplit() );
    }
    
    public void setMaxLevelForExtendedCells( int maxLevelForExtendedCells )
    {
        quadTree.setMaxLevelForExtendedCells( maxLevelForExtendedCells );
    }
    
    public final int getMaxLevelForExtendedCells()
    {
        return ( quadTree.getMaxLevelForExtendedCells() );
    }
    
    public final int getMaxOcTreeLevel()
    {
        return ( quadTree.getMaxLevel() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addChild( Node child, int index )
    {
        super.addChild( child, index );
        
        quadTree.insertNode( child );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Node removeChild( int index )
    {
        Node node = super.removeChild( index );
        
        quadTree.removeNode( node );
        
        return ( node );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAllChildren()
    {
        super.removeAllChildren();
        
        quadTree.clear();
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
        
        quadTree.updateNodePosition( node );
    }
    
    @Override
    public final void dump()
    {
        quadTree.dump();
    }
    
    private static final byte B0 = (byte)0x00;
    private static final byte B1 = (byte)( 1 << 0 );
    private static final byte B2 = (byte)( 1 << 1 );
    private static final byte B3 = (byte)( 1 << 2 );
    private static final byte B4 = (byte)( 1 << 3 );
    
    /**
     * This methods traverses the an OcTree cell by cell.
     * This Ext-version also checks the extended cells.
     * A bitmask is modified, if a "dominating" extended cell was culled
     * to avoid unnecessary checks of standard-OcCells.
     */
    private final byte cullQuadTreeAtomsExt( byte result, Classification parentClassify, QuadCell<Node> cell, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller )
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
        }
        
        if ( cell.hasChildCells() )
        {
            if ( cell.usesExtendedCells() )
            {
                // B1 : QuBackLeft
                // B2 : QuBackRight
                // B3 : QuFrontLeft
                // B4 : QuFrontRight
                
                if ( cell.getCellHLeft() != null )
                    culled |= cullQuadTreeAtomsExt( (byte)(B1 | B3), classify, cell.getCellHLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHRight() != null )
                    culled |= cullQuadTreeAtomsExt( (byte)(B2 | B4), classify, cell.getCellHRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHBack() != null )
                    culled |= cullQuadTreeAtomsExt( (byte)(B1 | B2), classify, cell.getCellHBack(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
                if ( cell.getCellHFront() != null )
                    culled |= cullQuadTreeAtomsExt( (byte)(B3 | B4), classify, cell.getCellHFront(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            }
            
            // B1 : QuBackLeft
            // B2 : QuBackRight
            // B3 : QuFrontLeft
            // B4 : QuFrontRight
            
            if ( ( (byte)( culled & B1 ) == B0 ) && ( cell.getCellQuBackLeft() != null ) )
                cullQuadTreeAtomsExt( B0, classify, cell.getCellQuBackLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B2 ) == B0 ) && ( cell.getCellQuBackRight() != null ) )
                cullQuadTreeAtomsExt( B0, classify, cell.getCellQuBackRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B3 ) == B0 ) && ( cell.getCellQuFrontLeft() != null ) )
                cullQuadTreeAtomsExt( B0, classify, cell.getCellQuFrontLeft(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
            if ( ( (byte)( culled & B4 ) == B0 ) && ( cell.getCellQuFrontRight() != null ) )
                cullQuadTreeAtomsExt( B0, classify, cell.getCellQuFrontRight(), cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
        }
        
        return ( B0 );
    }
    
    /**
     * Further traverses this group to find Shape3Ds.
     * Checks for state changes and cares for the state-stack.
     */
    private final void cullQuadTreeAtoms( QuadTreeGroup quadTreeGroup, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller )
    {
        if ( quadTreeGroup.getTransformGroup() != null )
        {
            throw new Error( "A QuadTreeGroup must not be nested into a parent TransformGroup!" );
        }
        
        QuadCell<Node> rootCell = _SG_PrivilegedAccess.getQuadTree( quadTreeGroup ).getRootCell();
        
        //if ( rootCell.usesExtendedCells() )
            cullQuadTreeAtomsExt( B0, null, rootCell, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
        //else
        //    cullQuadTreeAtoms( null, rootCell, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
    }
    
    /**
     * {@inheritDoc}
     */
    public void cullSpecialNode( QuadTreeGroup node, boolean cullingSuppressed, View view, Point3f viewPosition, Frustum frustum, RenderBinProvider binProvider, OpenGLCapabilities glCaps, long frameId, long nanoTime, long nanoStep, PickRay pickRay, boolean isShadowPass, FrustumCuller frustumCuller )
    {
        cullQuadTreeAtoms( node, cullingSuppressed, view, viewPosition, frustum, binProvider, glCaps, frameId, nanoTime, nanoStep, pickRay, isShadowPass, frustumCuller );
    }
    
    public QuadTreeGroup( float centerX, float centerY, float centerZ, PlaneIndicator plane, float width, float depth, float height, boolean useExtendedCells )
    {
        this.quadTree = new QuadTree<Node>( centerX, centerY, centerZ, plane, width, depth, height, useExtendedCells );
        
        //setBoundsAutoCompute( false );
        
        //this.getBounds().set( ocTree.getRootCell() );
        //this.setBounds( this.getBounds() );
        //this.setBounds( new BoundingSphere( 0f, 0f, 0f, 1000f ) );
        
        BoundingBox bb = new BoundingBox();
        //bb.set( ocTree.getRootCell() );
        this.setBounds( bb );
    }
    
    public QuadTreeGroup( Tuple3f center, PlaneIndicator plane, float width, float depth, float height, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), plane, width, depth, height, useExtendedCells );
    }
    
    public QuadTreeGroup( float centerX, float centerY, float centerZ, PlaneIndicator plane, float size, float height, boolean useExtendedCells )
    {
        this( centerX, centerY, centerZ, plane, size, size, height, useExtendedCells );
    }
    
    public QuadTreeGroup( Tuple3f center, PlaneIndicator plane, float size, float height, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), plane, size, height, useExtendedCells );
    }
    
    private static final PlaneIndicator getPlane( AxisIndicator upAxis )
    {
        switch ( upAxis )
        {
            case POSITIVE_Y_AXIS:
                return ( PlaneIndicator.X_Z_PLANE );
            case POSITIVE_Z_AXIS:
                return ( PlaneIndicator.X_Y_PLANE );
            case NEGATIVE_X_AXIS:
                return ( PlaneIndicator.Z_Y_PLANE );
            default:
                throw new IllegalArgumentException( "upAxis" );
        }
    }
    
    public QuadTreeGroup( Tuple3f center, AxisIndicator upAxis, float width, float depth, float height, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), getPlane( upAxis ), width, depth, height, useExtendedCells );
    }
    
    public QuadTreeGroup( float centerX, float centerY, float centerZ, AxisIndicator upAxis, float size, float height, boolean useExtendedCells )
    {
        this( centerX, centerY, centerZ, getPlane( upAxis ), size, size, height, useExtendedCells );
    }
    
    public QuadTreeGroup( Tuple3f center, AxisIndicator upAxis, float size, float height, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), getPlane( upAxis ), size, height, useExtendedCells );
    }
    
    public QuadTreeGroup( float centerX, float centerY, float centerZ, float width, float depth, float height, boolean useExtendedCells )
    {
        this( centerX, centerY, centerZ, DEFAULT_PLANE, width, depth, height, useExtendedCells );
    }
    
    public QuadTreeGroup( Tuple3f center, float width, float depth, float height, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), width, depth, height, useExtendedCells );
    }
    
    public QuadTreeGroup( float centerX, float centerY, float centerZ, float size, float height, boolean useExtendedCells )
    {
        this( centerX, centerY, centerZ, size, size, height, useExtendedCells );
    }
    
    public QuadTreeGroup( Tuple3f center, float size, float height, boolean useExtendedCells )
    {
        this( center.getX(), center.getY(), center.getZ(), size, height, useExtendedCells );
    }
}
