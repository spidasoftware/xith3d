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

import java.nio.FloatBuffer;

import org.jagatoo.geometry.GeomNioData;
import org.openmali.spatial.bodies.Frustum;
import org.openmali.spatial.bounds.Bounds;
import org.openmali.spatial.octree.OcTree;
import org.openmali.spatial.quadtree.QuadTree;
import org.xith3d.render.RenderPassConfig;
import org.xith3d.render.preprocessing.ShadowAtom;
import org.xith3d.render.preprocessing.ShapeAtom;

/**
 * This class provides pass-through methods for methods, that
 * are only for internal use and therefore should not be public,
 * but need to be publicly accessible in some way to access them
 * from different packages in xith. 
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class _SG_PrivilegedAccess
{
    public static final void setChanged( NodeComponent nc, boolean changed )
    {
        nc.setChanged( changed );
    }
    
    public static void markStaticClean( Appearance app )
    {
        app.markStaticClean();
    }
    
    /*
    public static final boolean isStaticDirty( Appearance app )
    {
        return ( app.isStaticDirty() );
    }
    */
    
    public static final void setChanged( Transform3D t3d, boolean changed )
    {
        t3d.setChanged( changed );
    }
    
    public static final void incGlobalOptionsChangeId()
    {
        GlobalOptions.getInstance().incChangeID();
    }
    
    public static final FloatBuffer getFloatBuffer( Transform3D t3d, boolean refillOnDemand )
    {
        return ( t3d.getFloatBuffer( refillOnDemand ) );
    }
    
    /**
     * Returns the {@link OcTree} from the given {@link OcTreeGroup}
     * by calling the {@link OcTreeGroup#getOcTree()} method, which has
     * package-access.
     * 
     * @param ocTreeGroup
     * 
     * @return the given {@link OcTreeGroup}'s {@link OcTree}.
     */
    public static final OcTree< Node > getOcTree( OcTreeGroup ocTreeGroup )
    {
        return ( ocTreeGroup.getOcTree() );
    }
    
    public static final QuadTree< Node > getQuadTree( QuadTreeGroup quadTreeGroup )
    {
        return ( quadTreeGroup.getQuadTree() );
    }
    
    public static final boolean isDirty( ShaderProgram<?> shaderProgram )
    {
        return ( shaderProgram.isDirty() );
    }
    
    public static final void setDirty( Shader shader, boolean dirty )
    {
        shader.setDirty( dirty );
    }
    
    public static final boolean isDirty( Shader shader )
    {
        return ( shader.isDirty() );
    }
    
    public static final void setDirty( GeomNioData geomData, boolean dirty )
    {
        if ( geomData instanceof GeomNioFloatData )
            ( (GeomNioFloatData)geomData ).setDirty( dirty );
        else
            ( (GeomNioIntData)geomData ).setDirty( dirty );
    }
    
    public static final boolean isDirty( GeomNioData geomData )
    {
        if ( geomData instanceof GeomNioFloatData )
            return ( ( (GeomNioFloatData)geomData ).isDirty() );
        
        return ( ( (GeomNioIntData)geomData ).isDirty() );
    }
    
    public static final void incrementFramesSinceDirty( GeomNioData geomData )
    {
        if ( geomData instanceof GeomNioFloatData )
            ( (GeomNioFloatData)geomData ).incrementFramesSinceDirty();
        else
            ( (GeomNioIntData)geomData ).incrementFramesSinceDirty();
    }
    
    public static final int getFramesSinceDirty( GeomNioData geomData )
    {
        if ( geomData instanceof GeomNioFloatData )
            return ( ( (GeomNioFloatData)geomData ).getFramesSinceDirty() );
        
        return ( ( (GeomNioIntData)geomData ).getFramesSinceDirty() );
    }
    
    public static final void setDirty( GeomNioFloatData geomData, boolean dirty )
    {
        geomData.setDirty( dirty );
    }
    
    public static final boolean isDirty( GeomNioFloatData geomData )
    {
        return ( geomData.isDirty() );
    }
    
    public static final void incrementFramesSinceDirty( GeomNioFloatData geomData )
    {
        geomData.incrementFramesSinceDirty();
    }
    
    public static final int getFramesSinceDirty( GeomNioFloatData geomData )
    {
        return ( geomData.getFramesSinceDirty() );
    }
    
    public static final void setDirty( GeomNioIntData geomData, boolean dirty )
    {
        geomData.setDirty( dirty );
    }
    
    public static final boolean isDirty( GeomNioIntData geomData )
    {
        return ( geomData.isDirty() );
    }
    
    public static final void incrementFramesSinceDirty( GeomNioIntData geomData )
    {
        geomData.incrementFramesSinceDirty();
    }
    
    public static final int getFramesSinceDirty( GeomNioIntData geomData )
    {
        return ( geomData.getFramesSinceDirty() );
    }
    
    public static final void setAtom( GroupNode group, ShadowAtom shadowAtom )
    {
        group.setAtom( shadowAtom );
    }
    
    public static final ShadowAtom getAtom( GroupNode group )
    {
        return ( group.getAtom() );
    }
    
    public static final void setAtom( Shape3D shape, ShapeAtom atom )
    {
        shape.setAtom( atom );
    }
    
    public static final ShapeAtom getAtom( Shape3D shape )
    {
        return ( shape.getAtom() );
    }
    
    public static final Transform3D getLeafWorldTransform( Leaf leaf )
    {
        return ( leaf.getLeafWorldTransform() );
    }
    
    public static final void forceRefill( BranchGroup bg, boolean force )
    {
        bg.forceRefill( force );
    }
    
    public static final boolean isRefillForeced( BranchGroup bg )
    {
        return ( bg.isRefillForeced() );
    }
    
    public static final void forceRecull( BranchGroup bg, boolean force )
    {
        bg.forceRecull( force );
    }
    
    public static final boolean isRecullForeced( BranchGroup bg )
    {
        return ( bg.isRecullForeced() );
    }
    
    public static final void setGLSLShaderProgramLinked( GLSLShaderProgram shaderProg, boolean linked )
    {
        shaderProg.setLinked( linked );
    }
    
    public static final void setGLSLShaderProgramLinkError( GLSLShaderProgram shaderProg, boolean linkError )
    {
        shaderProg.setLinkingError( linkError );
    }
    
    public static final void setPassId( Shape3D shape, int passId )
    {
        shape.setPassId( passId );
    }
    
    public static final int getPassId( Shape3D shape )
    {
        return ( shape.getPassId() );
    }
    
    public static final boolean notifyDrawCallbacks( Texture2DCanvas tc, long nanoTime )
    {
        return ( tc.notifyDrawCallbacks( nanoTime ) );
    }
    
    /*
    public static final void setResourceName( Texture texture, String resName )
    {
        texture.setResourceName( resName );
    }
    */
    
    public static final void resetSizeChanged( Texture texture )
    {
        texture.resetSizeChanged();
    }
    
    public static final void setDirty( Texture texture, boolean dirty )
    {
        texture.setDirty( dirty );
    }
    
    public static final void setCachedBounds( Bounds bounds, Geometry geom )
    {
        geom.setCachedBounds( bounds );
    }
    
    public static final Bounds getCachedBounds( Geometry geom )
    {
        return ( geom.getCachedBounds() );
    }
    
    public static final void set( View view, boolean b, RenderPassConfig rpc )
    {
        view.set( b, rpc );
    }
    
    public static final void update( UpdatableNode updatableNode, View view, Frustum frustum, long nanoTime, long nanoStep )
    {
        updatableNode.update( view, frustum, nanoTime, nanoStep );
    }
}
