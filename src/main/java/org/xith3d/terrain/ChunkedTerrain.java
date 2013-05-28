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
package org.xith3d.terrain;

import java.util.ArrayList;

import org.openmali.FastMath;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.LODSwitch;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.LazyLoadable;

/**
 * @author Mathias 'cylab' Henze
 */
public class ChunkedTerrain extends Group implements LazyLoadable
{
    protected static class Spec
    {
        public GridResourceProvider resourceProvider;
        public float x;
        public float y;
        public float z;
        public float scale;
        public float height;

        public Spec()
        {
            
        }

        public Spec( GridResourceProvider resourceProvider, float x, float y, float z, float scale, float height )
        {
            this.resourceProvider = resourceProvider;
            this.x = x;
            this.y = y;
            this.z = z;
            this.scale = scale;
            this.height = height;
        }
    }

    private ArrayList<GridSampler> samplers;
    private ArrayList<GridSurface> surfaces;
    private GridResourceProvider resourceProvider;
    private Point3f location;
    private Tuple3f dimension;
    private float scale = 1.0f;
    private float height = 1.0f;
    private float x;
    private float y;
    private float z;
    private float parentYOffset;
    private int tilesPerSide;
    private float s1 = 0f;
    private float t1 = 0f;
    private float s2 = 1.0f;
    private float t2 = 1.0f;
    
    // ---
    private int spatialTreeDepth = 2;
    private int geomTreeDepth;
    private int maxGeomTreeDepth;
    private int complexity = 4;
    private float baseTolerance = 0.1f;
    private boolean isSetUp= false;
    private boolean isLazy= false;
    private Group content= null;
    
    public ChunkedTerrain( GridSampler gridSampler, GridSurface gridShader, float x,float y, float z, float scale, float height )
    {
        this( new SimpleGridResourceProvider(gridSampler, gridShader), x, y, z,scale,height );
    }

    public ChunkedTerrain( GridResourceProvider resourceProvider, float x, float y, float z, float scale, float height )
    {
        this( resourceProvider, x, y, z,scale,height, 0f, 0f, 1.0f, 1.0f, 0f, 0, 2, false  );
    }

    protected ChunkedTerrain( Spec spec)
    {
        this(spec.resourceProvider, spec.x, spec.y, spec.z, spec.scale, spec.height);
    }

    protected ChunkedTerrain( GridResourceProvider resourceProvider, float x,float y, float z, float scale, float height, float s1, float t1, float s2, float t2, float parentYOffset, int geomTreeDepth, int maxGeomTreeDepth, boolean lazy)
    {
        this.resourceProvider = resourceProvider;
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = scale;
        this.height = height;
        this.geomTreeDepth = geomTreeDepth;
        this.maxGeomTreeDepth = maxGeomTreeDepth;
        this.s1 = s1;
        this.t1 = t1;
        this.s2 = s2;
        this.t2 = t2;
        this.parentYOffset = parentYOffset;
        this.isLazy = lazy;
        this.dimension = new Tuple3f( scale, height, scale );
        this.location = new Point3f( x, y, z );
        this.tilesPerSide = FastMath.pow( 2, spatialTreeDepth  );
        this.samplers = new ArrayList<GridSampler>( tilesPerSide * tilesPerSide );
        this.surfaces = new ArrayList<GridSurface>( tilesPerSide * tilesPerSide );
        
        if ( !lazy )
        {
            prepare();
            setUp();
        }
        else
        {
             content = new Group();
        }
    }

    public float getHeight()
    {
        return height;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getZ()
    {
        return z;
    }

    public Point3f getLocation()
    {
        return location;
    }

    public Tuple3f getDimension()
    {
        return dimension;
    }

    public float pickY(float x, float z)
    {
        float s = Math.max( 0, Math.min( 1, ((x - this.x) / scale) - s1 ) );
        float t = Math.max( 0, Math.min( 1, ((z - this.z) / scale) - t1 ) );
        
        GridSampler gridSampler= resourceProvider.findSampler( s,t,s,t,-1 );

        return gridSampler.sampleHeight(s, t)*height+y;
    }
    
    private void addTreeLevel( Group parent, int x1, int z1, int x2, int z2, int l )
    {
        int xc = ( x1 + x2 ) / 2;
        int zc = ( z1 + z2 ) / 2;
        if ( l == spatialTreeDepth - 1 )
        {
            addTiles( parent, x1, z1, xc, zc );
            addTiles( parent, xc, z1, x2, zc );
            addTiles( parent, xc, zc, x2, z2 );
            addTiles( parent, x1, zc, xc, z2 );
            return;
        }
        l++;
        Group nwGroup = new Group();
        addTreeLevel( nwGroup, x1, z1, xc, zc, l );
        parent.addChild( nwGroup );
        Group neGroup = new Group();
        addTreeLevel( neGroup, xc, z1, x2, zc, l );
        parent.addChild( neGroup );
        Group seGroup = new Group();
        addTreeLevel( seGroup, xc, zc, x2, z2, l );
        parent.addChild( seGroup );
        Group swGroup = new Group();
        addTreeLevel( swGroup, x1, zc, xc, z2, l );
        parent.addChild( swGroup );
    }
    
    private void addTiles( Group group, int x1, int z1, int x2, int z2 )
    {
        float ds = s2 - s1;
        float dt = t2 - t1;
        for ( int ix = x1; ix < x2; ix++ )
        {
            for ( int iz = z1; iz < z2; iz++ )
            {
                GridSampler gridSampler= resourceProvider.findSampler( s1 + ( ds / tilesPerSide ) * ix, t1 + ( dt / tilesPerSide ) * iz, s1 + ( ds / tilesPerSide ) * ( ix + 1 ), t1 + ( dt / tilesPerSide ) * ( iz + 1 ), geomTreeDepth );
                GridSurface gridSurface= resourceProvider.findSurface( s1 + ( ds / tilesPerSide ) * ix, t1 + ( dt / tilesPerSide ) * iz, s1 + ( ds / tilesPerSide ) * ( ix + 1 ), t1 + ( dt / tilesPerSide ) * ( iz + 1 ), geomTreeDepth );
                samplers.add(gridSampler);
                surfaces.add(gridSurface);
                float xoffset = ( scale / tilesPerSide ) / 2;
                GridTriangulator triangulator = new GridTriangulator( gridSampler, complexity, height, 0 - xoffset, 0 - xoffset, ( scale / tilesPerSide ) - xoffset, ( scale / tilesPerSide ) - xoffset, s1 + ( ds / tilesPerSide ) * ix, t1 + ( dt / tilesPerSide ) * iz, s1 + ( ds / tilesPerSide ) * ( ix + 1 ), t1 + ( dt / tilesPerSide ) * ( iz + 1 ), baseTolerance );
                float yoffset = triangulator.getMinY()+(triangulator.getMaxY()-triangulator.getMinY())/2;
                TerrainTile terrainTile = new TerrainTile( gridSampler, gridSurface ,triangulator , geomTreeDepth  );
                StaticTransform.translate (terrainTile,0,-yoffset,0);
                TransformGroup transform = new TransformGroup( new Transform3D( this.x + ( scale / tilesPerSide ) * ix + xoffset, this.y+yoffset-parentYOffset, this.z + ( scale / tilesPerSide ) * iz + xoffset ) );
                if ( geomTreeDepth < maxGeomTreeDepth )
                {
                    final float threshold = scale / tilesPerSide * 1.5f;
                    LODSwitch lodSwitch = new LODSwitch();
                    ChunkedTerrain subTerrain = new ChunkedTerrain( resourceProvider, 0 - xoffset, 0, 0 - xoffset, scale / tilesPerSide, height, s1 + ( ds / tilesPerSide ) * ix, t1 + ( dt / tilesPerSide ) * iz, s1 + ( ds / tilesPerSide ) * ( ix + 1 ), t1 + ( dt / tilesPerSide ) * ( iz + 1 ), yoffset, geomTreeDepth+1,maxGeomTreeDepth, true );
                    lodSwitch.addLODItem( terrainTile, threshold, Float.MAX_VALUE );
                    lodSwitch.addLODItem( subTerrain, 0, threshold );
                    transform.addChild( lodSwitch );
                }
                else
                {
                    transform.addChild( terrainTile );
                }
                group.addChild( transform );
            }
        }
    }

    public void prepare()
    {
//        System.out.println("prepare called for "+getName()+"!");
        if(!isSetUp)
        {
            addTreeLevel( isLazy?content:this, 0, 0, tilesPerSide, tilesPerSide, 0 );
        }
//        System.out.println("prepare of "+name+" finished!");
    }

    
    public void setUp()
    {
//        System.out.println("setUp called for "+name+"!");
        if(!isSetUp)
        {
            if(isLazy)this.addChild(content);
            isSetUp=true;
        }
//        System.out.println("setUp of "+name+" finished!");
    }

    public void tearDown()
    {
        if(isLazy && isSetUp)
        {
            this.removeChild( content );
            content = new Group();
            isSetUp=false;
        }
    }

    public boolean isSetUp()
    {
        return isSetUp;
    }

    public void cleanUp()
    {
        if(isLazy && isSetUp)
        {
            int size = samplers.size();
            for( int i = 0; i < size; i++ )
            {
               resourceProvider.releaseSampler(samplers.remove(0));
            }
            size = surfaces.size();
            for( int i = 0; i < size; i++ )
            {
               resourceProvider.releaseSurface(surfaces.remove(0));
            }
        }
    }
}
