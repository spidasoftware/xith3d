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

import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import static java.lang.Math.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.openmali.spatial.polygons.Triangle;

/**
 * A GridTriangulator is used to create a set of triangles from a regular Grid.
 *
 * @author Mathias 'cylab' Henze (cylab _at_ highteq _dot_ net)
 * @author Shamus Young (http://www.shamusyoung.com/twentysidedtale/?cat=10)
 * @see GridSampler
 */
@SuppressWarnings( "unused" )
public class GridTriangulator
{
    private GridSampler grid;
    private int complexity;
    private float yScale;
    private float x1;
    private float z1;
    private float x2;
    private float z2;
    private float s1;
    private float s2;
    private float t1;
    private float t2;
    private float minY;
    private float maxY;
    private float baseTolerance;
    private boolean changed;
    private Tuple3f[] coordinateCache;
    private Vector3f[] normals;
    private int[] index;
    private boolean backgroundOptimized=false;
    
    private int size;
    // TODO: capsule this in a data structure and pass it as arguments.
    private int indexCount;
    private boolean yield;
    private int yieldCount;
    private int baseVertexCount;
    private int edgeVertexCount;
    private int[] boundry;
    private int[] indexmapping;
    private int[] skirtindexmapping;
    private boolean[] enabledpoints;
    private int[] tmpindex;
    private boolean expandEdges = true;
    private float maxTolerance;
    private Triangle tmpTri = new Triangle();
    private Vector3f tmpVec = new Vector3f();
    private Tuple3f tmpP0 = new Tuple3f();
    private Tuple3f tmpP1 = new Tuple3f();
    private Tuple3f tmpP2 = new Tuple3f();
    private Tuple3f tmpP3 = new Tuple3f();
    private Tuple3f tmpP4 = new Tuple3f();
    
    public GridTriangulator( GridSampler grid, int complexity )
    {
        this( grid, complexity, (float)pow( 2, complexity ) + 1, (float)-pow( 2, complexity ) / 2, (float)-pow( 2, complexity ) / 2, (float)pow( 2, complexity ) / 2, (float)pow( 2, complexity ) / 2, 0, 0, 1, 1, 0.07f );
    }
    
    public GridTriangulator( GridSampler grid, int complexity, float yScale )
    {
        this( grid, complexity, yScale, (float)-pow( 2, complexity ) / 2, (float)-pow( 2, complexity ) / 2, (float)pow( 2, complexity ) / 2, (float)pow( 2, complexity ) / 2, 0, 0, 1, 1, 0.07f );
    }
    
    public GridTriangulator( GridSampler grid, int complexity, float yScale, float x1, float z1, float x2, float z2 )
    {
        this( grid, complexity, yScale, x1, z1, x2, z2, 0, 0, 1, 1, 0.07f );
    }
    
    public GridTriangulator( GridSampler grid, int complexity, float yScale, float x1, float z1, float x2, float z2, float s1, float t1, float s2, float t2 )
    {
        this( grid, complexity, yScale, x1, z1, x2, z2, s1, t1, s2, t2, 0.07f );
    }
    
    /**
     *
     * @param grid the gridsampler to read the values to triangulate from.
     * @param complexity defines the amount of samples to be pow(2,complexity)+1 * pow(2,complexity)+1. A complexity of e.g. 3 results in a 9x9 triangle mesh.
     * @param yScale the scale factor to scale the read grid values with.
     * @param x1 the x position of the left furthermost corner of the resulting triangle mesh
     * @param z1 the z position of the left furthermost corner of the resulting triangle mesh
     * @param x2 the x position of the nearest right corner of the resulting triangle mesh
     * @param z2 the z position of the nearest right corner of the resulting triangle mesh
     * @param s1 the s coordinate of the top left corner of the grid to be sampled
     * @param t1 the t coordinate of the top left corner of the grid to be sampled
     * @param s2 the s coordinate of the lower right corner of the grid to be sampled
     * @param t2 the t coordinate of the lower right corner of the grid to be sampled
     * @param baseTolerance the tolerance used to reduce the triangle indexCount of the resulting mesh
     */
    public GridTriangulator( GridSampler grid, int complexity, float yScale, float x1, float z1, float x2, float z2, float s1, float t1, float s2, float t2, float baseTolerance )
    {
        if ( complexity < 1 )
        {
            throw new IllegalArgumentException( "Construction argument 'complexity' must be a positive number!" );
        }
        this.grid = grid;
        this.complexity = complexity;
        this.yScale = yScale;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.s1 = s1;
        this.t1 = t1;
        this.s2 = s2;
        this.t2 = t2;
        this.baseTolerance = baseTolerance;
        setChanged( true );
        // Fill the cache
        getCoordinates();
    }
    
    public boolean isChanged()
    {
        return changed;
    }
    
    public void setChanged( boolean changed )
    {
        this.changed = changed;
    }

    public boolean isBackgroundOptimized()
    {
        return backgroundOptimized;
    }

    public void setBackgroundOptimized(boolean backgroundOptimized)
    {
        this.backgroundOptimized = backgroundOptimized;
    }
    
    public GridSampler getGrid()
    {
        return grid;
    }
    
    public void setGrid( GridSampler grid )
    {
        if ( this.grid != grid )
            setChanged( true );
        this.grid = grid;
    }
    
    public int getComplexity()
    {
        return complexity;
    }
    
    public void setComplexity( int complexity )
    {
        if ( this.complexity != complexity )
            setChanged( true );
        this.complexity = complexity;
    }
    
    public int getSize()
    {
        return size;
    }
    
    public float getYScale()
    {
        return yScale;
    }
    
    public void setYScale( float yScale )
    {
        if ( this.yScale != yScale )
            setChanged( true );
        this.yScale = yScale;
    }
    
    public float getX1()
    {
        return x1;
    }
    
    public void setX1( float x1 )
    {
        if ( this.x1 != x1 )
            setChanged( true );
        this.x1 = x1;
    }
    
    public float getZ1()
    {
        return z1;
    }
    
    public void setZ1( float z1 )
    {
        if ( this.z1 != z1 )
            setChanged( true );
        this.z1 = z1;
    }
    
    public float getX2()
    {
        return x2;
    }
    
    public void setX2( float x2 )
    {
        if ( this.x2 != x2 )
            setChanged( true );
        this.x2 = x2;
    }
    
    public float getZ2()
    {
        return z2;
    }
    
    public void setZ2( float z2 )
    {
        if ( this.z2 != z2 )
            setChanged( true );
        this.z2 = z2;
    }
    
    public float getS1()
    {
        return s1;
    }
    
    public void setS1( float s1 )
    {
        if ( this.s1 != s1 )
            setChanged( true );
        this.s1 = s1;
    }
    
    public float getS2()
    {
        return s2;
    }
    
    public void setS2( float s2 )
    {
        if ( this.s2 != s2 )
            setChanged( true );
        this.s2 = s2;
    }
    
    public float getT1()
    {
        return t1;
    }
    
    public void setT1( float t1 )
    {
        if ( this.t1 != t1 )
            setChanged( true );
        this.t1 = t1;
    }
    
    public float getT2()
    {
        return t2;
    }
    
    public void setT2( float t2 )
    {
        if ( this.t2 != t2 )
            setChanged( true );
        this.t2 = t2;
    }

    public float getMaxY()
    {
        return maxY;
    }

    public float getMinY()
    {
        return minY;
    }
    
    public float getBaseTolerance()
    {
        return baseTolerance;
    }
    
    public void setBaseTolerance( float baseTolerance )
    {
        if ( this.baseTolerance != baseTolerance )
            setChanged( true );
        this.baseTolerance = baseTolerance;
    }
    
    public Vector3f[] getNormals()
    {
        // update caches
        if ( isChanged() )
            getCoordinates();
        return normals;
    }
    
    public Tuple3f[] getCoordinates()
    {
        return getCoordinates( true );
    }
    
    /**
     * TODO (cylab 2007-MAR-06) implement the optimized flag
     * @param optimized
     * @return (something)
     */
    public Tuple3f[] getCoordinates( boolean optimized )
    {
        //        System.out.println("Calculating coordinates: optimized="+optimized+" tolerance="+baseTolerance);
        Tuple3f[] coords = calculateCoordinates( optimized, backgroundOptimized );
        //        printEnabledIndices();
        return coords;
    }
    
    private Tuple3f[] calculateCoordinates( boolean optimized, boolean background )
    {
        
        if ( !isChanged() )
            return coordinateCache;
        if ( background )
            startCalculation();
        size = (int)( pow( 2, complexity ) + 1 );
        boundry = new int[ size ];
        enabledpoints = new boolean[ size * size ];
        tmpindex = new int[ size * size * 6 ];
        indexmapping = null;
        minY=Float.MAX_VALUE;
        maxY=0f;

        //This finds the largest power-of-two denominator for the given number.  This
        //is used to determine what level of the quadtree a grid position occupies.
        for ( int n = 0; n < size; n++ )
        {
            boundry[ n ] = -1;
            if ( n == 0 )
                boundry[ n ] = (short)( size - 1 );
            else
            {
                for ( int level = size; level > 1; level /= 2 )
                {
                    if ( ( n % level ) == 0 )
                    {
                        boundry[ n ] = level;
                        break;
                    }
                }
                if ( boundry[ n ] == -1 )
                    boundry[ n ] = 1;
            }
            if ( background )
                pauseCalculation();
        }
        
        index = getIndex( baseTolerance );
        
        coordinateCache = new Tuple3f[ baseVertexCount + edgeVertexCount ];
        normals= new Vector3f[ baseVertexCount + edgeVertexCount ];
        int i = 0;
        for ( int x = 0; x < size; x++ )
        {
            for ( int z = 0; z < size; z++ )
            {
                if ( isEnabled( x, z ) )
                {
                    coordinateCache[ i ] = samplePoint( x, z );
                    normals[ i++ ] = sampleNormal( x, z );
                }
            }
            if ( background )
                pauseCalculation();
        }
        
        if ( expandEdges )
        {
            for ( int x = 0; x < size; x++ )
            {
                if ( isEnabled( x, size - 1 ) )
                {
                    coordinateCache[ i ] = samplePoint( x, size - 1, -yScale / 32 );
                    normals[ i++ ] = sampleNormal( x, size - 1 );
                }
            }
            for ( int z = size - 1; z >= 0; z-- )
            {
                if ( isEnabled( size - 1, z ) )
                {
                    coordinateCache[ i ] = samplePoint( size - 1, z, -yScale / 32 );
                    normals[ i++ ] = sampleNormal( size - 1, z );
                }
            }
            for ( int x = size - 1; x >= 0; x-- )
            {
                if ( isEnabled( x, 0 ) )
                {
                    coordinateCache[ i ] = samplePoint( x, 0, -yScale / 32 );
                    normals[ i++ ] = sampleNormal( x, 0 );
                }
            }
            for ( int z = 0; z < size; z++ )
            {
                if ( isEnabled( 0, z ) )
                {
                    coordinateCache[ i ] = samplePoint( 0, z, -yScale / 32 );
                    normals[ i++ ] = sampleNormal( 0, z );
                }
            }
            if ( background )
                stopCalculation();
        }
        
        // TODO: correct this the right way
        int last= i-1;
        int len= coordinateCache.length;
        for(int j= i; j< len; j++)
        {
            coordinateCache[ j ]= coordinateCache[ last ];
        }
        setChanged( false );
        return coordinateCache;
    }
    
    public int[] getIndex()
    {
        // update caches
        if ( isChanged() )
            getCoordinates();
        return index;
    }
    
    public int[] getIndex( float tolerance )
    {
        if ( tolerance <= this.baseTolerance && !isChanged() )
            return getIndex();
        //        System.out.println("Calculating index: tolerance="+tolerance);
        int[] index = calculateIndex( tolerance, null, 0, backgroundOptimized);
        //        if(tolerance>this.baseTolerance) printEnabledIndices();
        //        if(tolerance>this.baseTolerance) printReturnedIndices(index);
        return index;
    }
    
    private void printEnabledIndices()
    {
        System.out.print( "\n\n" );
        for ( int x = 0; x < size * 3 + 3 + ( expandEdges ? 6 : 0 ); x++ )
        {
            System.out.print( "#" );
        }
        System.out.print( "\n" );
        if ( expandEdges )
        {
            System.out.print( "#    " );
            for ( int x = 0; x < size; x++ )
            {
                System.out.print( isEnabled( x, 0 ) ? f( findSkirtIndex( x, 0 ) ) : " - " );
            }
            System.out.println( "   #" );
        }
        for ( int z = 0; z < size; z++ )
        {
            System.out.print( "# " );
            if ( expandEdges )
            {
                System.out.print( isEnabled( 0, z ) ? f( findSkirtIndex( 0, z ) ) : " - " );
            }
            for ( int x = 0; x < size; x++ )
            {
                System.out.print( isEnabled( x, z ) ? f( findIndex( x, z ) ) : " - " );
            }
            if ( expandEdges )
            {
                System.out.print( isEnabled( size - 1, z ) ? f( findSkirtIndex( size - 1, z ) ) : " - " );
            }
            System.out.println( "#" );
        }
        if ( expandEdges )
        {
            System.out.print( "#    " );
            for ( int x = 0; x < size; x++ )
            {
                System.out.print( isEnabled( x, size - 1 ) ? f( findSkirtIndex( x, size - 1 ) ) : " - " );
            }
            System.out.println( "   #" );
        }
        for ( int x = 0; x < size * 3 + 3 + ( expandEdges ? 6 : 0 ); x++ )
        {
            System.out.print( "#" );
        }
        System.out.print( "\n\n" );
    }
    
    private void printReturnedIndices( int[] index )
    {
        System.out.print( "\n\n" );
        int i = 0;
        while ( true )
        {
            for ( int j = 0; j < 5; j++ )
            {
                System.out.print( ( j > 0 ? ", " : "" ) + index[ i++ ] );
                if ( i == index.length )
                    break;
            }
            System.out.print( "\n" );
            if ( i == index.length )
                break;
        }
        System.out.print( "\n\n" );
    }
    
    private String f( int index )
    {
        String result = Integer.toString( index );
        if ( result.length() < 2 )
            result = " " + result;
        return result + " ";
    }
    
    public int[] getIndex( float tolerance, Tuple3f eyePosition, float threshold )
    {
        return calculateIndex( tolerance, eyePosition, threshold, false );
    }
    
    public void getIndex( final float tolerance, final Tuple3f eyePosition, final float threshold, final IndexCallback callback )
    {
        Thread worker = new Thread("xith3d grid triangulator")
        {
            @Override
            public void run()
            {
                callback.indexFinished( calculateIndex( tolerance, eyePosition, threshold, true ) );
            }
        };
        worker.start();
    }
    
    private int[] calculateIndex( float tolerance, Tuple3f eyePosition, float threshold, boolean background )
    {
        if ( tolerance <= this.baseTolerance )
            tolerance = this.baseTolerance;
        
        if ( background )
            startCalculation();
        indexCount = 0;
        Arrays.fill( enabledpoints, false );
        enablePoint( 0, 0 );
        enablePoint( ( size - 1 ), 0 );
        enablePoint( ( size - 1 ), ( size - 1 ) );
        enablePoint( 0, ( size - 1 ) );
        enablePoint( ( size - 1 ) / 2, ( size - 1 ) / 2 );
        for ( int y = 2; y < size; y++ )
        {
            for ( int x = 0; x < size; x++ )
            {
                if ( isEnabled( x, y ) )
                    continue;
                int xx = boundry[ x ];
                int yy = boundry[ y ];
                int level = min( xx, yy );
                splitQuad( x - level, y - level, level * 2 + 1, tolerance, eyePosition, threshold );
                if ( background )
                    pauseCalculation();
            }
        }
        // Create the index only the first time
        if ( indexmapping == null )
        {
            indexmapping = new int[ size * size ];
            baseVertexCount = 0;
            for ( int x = 0; x < size; x++ )
            {
                for ( int z = 0; z < size; z++ )
                {
                    if ( isEnabled( x, z ) )
                    {
                        mapIndex( x, z, baseVertexCount++ );
                    }
                    else
                        mapIndex( x, z, -1 );
                    if ( background )
                        pauseCalculation();
                }
            }
        }
        
        if ( expandEdges )
        {
            // Create the skirtindex only the first time
            if ( skirtindexmapping == null )
            {
                skirtindexmapping = new int[ size * 4 ];
                edgeVertexCount = 0;
                for ( int x = 0; x < size; x++ )
                {
                    if ( isEnabled( x, size - 1 ) )
                    {
                        mapSkirtIndex( x, size - 1, baseVertexCount + edgeVertexCount++ );
                    }
                    else
                        mapSkirtIndex( x, size - 1, -1 );
                }
                for ( int z = size - 1; z >= 0; z-- )
                {
                    if ( isEnabled( size - 1, z ) )
                    {
                        mapSkirtIndex( size - 1, z, baseVertexCount + edgeVertexCount++ );
                    }
                    else
                        mapSkirtIndex( size - 1, z, -1 );
                }
                for ( int x = size - 1; x >= 0; x-- )
                {
                    if ( isEnabled( x, 0 ) )
                    {
                        mapSkirtIndex( x, 0, baseVertexCount + edgeVertexCount++ );
                    }
                    else
                        mapSkirtIndex( x, 0, -1 );
                }
                for ( int z = 0; z < size; z++ )
                {
                    if ( isEnabled( 0, z ) )
                    {
                        mapSkirtIndex( 0, z, baseVertexCount + edgeVertexCount++ );
                    }
                    else
                        mapSkirtIndex( 0, z, -1 );
                }
                if ( background )
                    pauseCalculation();
            }
        }
        
        //        triangulateBlock(0, 0, size);
        //        int[] result= new int[indexCount];
        //        System.arraycopy(tmpindex,0,result,0, indexCount);
        
        ArrayList< Integer > triangleStrip = new ArrayList< Integer >( ( size * size + 4 * size ) * 2 );
        refineMesh( triangleStrip, complexity );
        
        if ( expandEdges )
        {
            for ( int x = 0; x < size; x++ )
            {
                if ( isEnabled( x, size - 1 ) )
                {
                    appendIndex( triangleStrip, x, size - 1 );
                    appendSkirtIndex( triangleStrip, x, size - 1 );
                }
            }
            for ( int z = size - 1; z >= 0; z-- )
            {
                if ( isEnabled( size - 1, z ) )
                {
                    appendIndex( triangleStrip, size - 1, z );
                    appendSkirtIndex( triangleStrip, size - 1, z );
                }
            }
            for ( int x = size - 1; x >= 0; x-- )
            {
                if ( isEnabled( x, 0 ) )
                {
                    appendIndex( triangleStrip, x, 0 );
                    appendSkirtIndex( triangleStrip, x, 0 );
                }
            }
            for ( int z = 0; z < size; z++ )
            {
                if ( isEnabled( 0, z ) )
                {
                    appendIndex( triangleStrip, 0, z );
                    appendSkirtIndex( triangleStrip, 0, z );
                }
            }
            if ( background )
                pauseCalculation();
        }
        
        int[] result = new int[ triangleStrip.size() ];
        for ( int i = 0; i < result.length; i++ )
        {
            result[ i ] = triangleStrip.get( i );
        }
        if ( background )
            stopCalculation();
        return result;
    }
    
    private void startCalculation()
    {
        yield = true;
        yieldCount = 0;
    }
    
    private void pauseCalculation()
    {
        if ( yield )
        {
            yieldCount++;
            if ( yieldCount % 5000 == 0 )
                Thread.yield();
        }
    }
    
    private void stopCalculation()
    {
        yield = false;
        yieldCount = 0;
    }
    
    private Vector3f sampleNormal( int x, int y )
    {
        return (Vector3f) sampleNormal( x, y, new Vector3f() );
    }
    
    private Tuple3f sampleNormal( int x, int y, Tuple3f result )
    {
        samplePoint( x, y, 0, tmpP0 );
        samplePoint( x - 1, y - 1 , 0, tmpP1 );
        samplePoint( x + 1, y - 1 , 0, tmpP2 );
        samplePoint( x + 1, y + 1 , 0, tmpP3 );
        samplePoint( x - 1, y + 1 , 0, tmpP4 );
        tmpTri.setVertexCoordA( tmpP0 );
        tmpTri.setVertexCoordB( tmpP1 );
        tmpTri.setVertexCoordC( tmpP2 );
        tmpTri.getFaceNormalACAB( tmpVec );
        result.set( tmpVec );
        tmpTri.setVertexCoordA( tmpP0 );
        tmpTri.setVertexCoordB( tmpP2 );
        tmpTri.setVertexCoordC( tmpP3 );
        result.add( tmpVec );
        tmpTri.setVertexCoordA( tmpP0 );
        tmpTri.setVertexCoordB( tmpP3 );
        tmpTri.setVertexCoordC( tmpP4);
        result.add( tmpVec );
        tmpTri.setVertexCoordA( tmpP0 );
        tmpTri.setVertexCoordB( tmpP4 );
        tmpTri.setVertexCoordC( tmpP1);
        result.add( tmpVec );
        result.div( 4 );
        return result;
    }

    private Point3f samplePoint( int x, int y )
    {
        return samplePoint( x, y, 0 );
    }
    
    private Point3f samplePoint( int x, int y, float offset )
    {
        return (Point3f) samplePoint( x, y, offset, new Point3f() );
    }
    
    private Tuple3f samplePoint( int x, int y, float offset, Tuple3f result )
    {
        float realX = x1 + ( ( (float)x ) / ( size - 1 ) ) * ( x2 - x1 );
        float realZ = z1 + ( ( (float)y ) / ( size - 1 ) ) * ( z2 - z1 );
        final float realY = sampleHeight(x, y, offset);
        if(realY>maxY) maxY=realY;
        if(realY<minY) minY=realY;
        result.set( realX, realY ,realZ );
        return result;
    }

    private float sampleHeight( int x, int y, float offset )
    {
        float s = s1 + ( ( (float)x ) / ( size - 1 ) ) * ( s2 - s1 );
        float t = t1 + ( ( (float)y ) / ( size - 1 ) ) * ( t2 - t1 );
        return grid.sampleHeight(s, t) * yScale + offset;
    }
    
    private boolean isMasked( int x, int y )
    {
        x = max( min( x, size - 1 ), 0 );
        y = max( min( y, size - 1 ), 0 );
        return indexmapping != null && indexmapping[ x + y * size ] == -1;
    }
    
    private boolean isEnabled( int x, int y )
    {
        x = max( min( x, size - 1 ), 0 );
        y = max( min( y, size - 1 ), 0 );
        return enabledpoints[ x + y * size ];
    }
    
    private void enable( int x, int y )
    {
        x = max( min( x, size - 1 ), 0 );
        y = max( min( y, size - 1 ), 0 );
        enabledpoints[ x + y * size ] = true;
    }
    
    private void mapIndex( int x, int y, int i )
    {
        x = max( min( x, size - 1 ), 0 );
        y = max( min( y, size - 1 ), 0 );
        indexmapping[ x + y * size ] = i;
    }
    
    private int findIndex( int x, int y )
    {
        x = max( min( x, size - 1 ), 0 );
        y = max( min( y, size - 1 ), 0 );
        return indexmapping[ x + y * size ];
    }
    
    private void mapSkirtIndex( int x, int y, int i )
    {
        if ( y == 0 )
        {
            skirtindexmapping[ x ] = i;
        }
        else if ( x == 0 )
        {
            skirtindexmapping[ size + y ] = i;
        }
        else if ( y == size - 1 )
        {
            skirtindexmapping[ size * 2 + x ] = i;
        }
        else if ( x == size - 1 )
        {
            skirtindexmapping[ size * 3 + y ] = i;
        }
    }
    
    private int findSkirtIndex( int x, int y )
    {
        if ( y == 0 )
        {
            return skirtindexmapping[ x ];
        }
        else if ( x == 0 )
        {
            return skirtindexmapping[ size + y ];
        }
        else if ( y == size - 1 )
        {
            return skirtindexmapping[ size * 2 + x ];
        }
        else if ( x == size - 1 )
        {
            return skirtindexmapping[ size * 3 + y ];
        }
        return -1;
    }
    
    private void appendSkirtIndex( List< Integer > triangleStrip, int x, int y )
    {
        x = max( min( x, size - 1 ), 0 );
        y = max( min( y, size - 1 ), 0 );
        int i = findSkirtIndex( x, y );
        if ( i != -1 )
        {
            triangleStrip.add( i );
        }
    }
    
    /*-----------------------------------------------------------------------------
    This is tricky stuff.  When this is called, it means the given point is needed
    for the terrain we are working on.  Each point, when activated, will recusivly
    require two other points at the next lowest level of detail.  This is what
    causes the "shattering" effect that breaks the terrain into triangles.
    If you want to know more, Google for Peter Lindstrom, the inventor of this
    very clever system.
    -----------------------------------------------------------------------------*/
    private void enablePoint( int x, int y )
    {
        
        int xl;
        int yl;
        int level;
        
        if ( x < 0 || x >= size || y < 0 || y >= size )
            return;
        if ( isEnabled( x, y ) )
            return;
        if ( isMasked( x, y ) )
            return;
        enable( x, y );
        xl = boundry[ x ];
        yl = boundry[ y ];
        level = min( xl, yl );
        if ( xl > yl )
        {
            enablePoint( x - level, y );
            enablePoint( x + level, y );
        }
        else if ( xl < yl )
        {
            enablePoint( x, y + level );
            enablePoint( x, y - level );
        }
        else
        {
            int x2;
            int y2;
            
            x2 = x & ( level * 2 );
            y2 = y & ( level * 2 );
            if ( x2 == y2 )
            {
                enablePoint( x - level, y + level );
                enablePoint( x + level, y - level );
            }
            else
            {
                enablePoint( x + level, y + level );
                enablePoint( x - level, y - level );
            }
        }
        
    }
    
    /*-----------------------------------------------------------------------------

                upper
             ul-------ur
              |\      |
             l| \     |r
             e|  \    |i
             f|   c   |g
             t|    \  |h
              |     \ |t
              |      \|
             ll-------lr
                lower

    This considers a quad for splitting. This is done by looking to see how
    coplanar the quad is.  The elevation of the corners are averaged, and compared
    to the elevation of the center.  The geater the difference between these two
    values, the more non-coplanar this quad is.
    -----------------------------------------------------------------------------*/
    
    private void splitQuad( int x1, int y1, int size, float tolerance, Tuple3f eyePos, float threshold )
    {
        
        int xc, yc, x2, y2;
        int half;
        float ul, ur, ll, lr, center;
        float average;
        float delta, dist = 0f;
        float sizeBias;
        Point3f pos= new Point3f();
        
        half = ( size - 1 ) / 2;
        xc = x1 + half;
        x2 = x1 + size - 1;
        yc = y1 + half;
        y2 = y1 + size - 1;
        if ( x2 >= this.size || y2 >= this.size || x1 < 0 || y1 < 0 )
            return;
        if ( eyePos != null )
        {
            Vector3f vec = new Vector3f();
            samplePoint( xc, yc, 0, vec );
            vec.sub( eyePos );
            dist = (float)max( min( vec.length() / threshold, 1.0 ), 0 );
        }
        
        ul = sampleHeight( x1, y1, 0 );
        ur = sampleHeight( x2, y1, 0 );
        ll = sampleHeight( x1, y2, 0 );
        lr = sampleHeight( x2, y2, 0 );
        center = sampleHeight( xc, yc, 0 );
        average = ( ul + lr + ll + ur ) / 4.0f;
        //look for a delta between the center point and the average elevation
        delta = abs( ( average - center ) ) * 5.0f;
        //scale the delta based on the size of the quad we are dealing with
        delta /= (float)size;
        if ( eyePos != null )
        {
            //scale based on distance
            delta *= ( 1.0f - ( dist * 0.85f ) );
            //if the distance is very close, then we want a lot more detail
            if ( dist < 0.15f )
                delta *= 10.0f;
        }
        //smaller quads are much less imporant
        sizeBias = (float)( this.size + size ) / (float)( this.size * 2 );
        delta *= sizeBias;
        if ( delta > tolerance )
            enablePoint( xc, yc );
        
    }
    
    public static interface CoordinateCallback
    {
        void coordinatesFinished( Tuple3f[] coordinates );
    }
    
    public static interface IndexCallback
    {
        void indexFinished( int[] index );
    }
    
    int lastParity;
    
    public void refineMesh( List< Integer > triangleStrip, int complexity )
    {
        int x1 = 0;
        int y1 = 0;
        int x2;
        int y2;
        int xc;
        int yc;
        int nextSize;
        int size = (int)( Math.pow( 2, complexity ) + 1 );
        
        int n = complexity * 2; // the triangle complexity is double the quad complexity
        pauseCalculation(); // give another Thread some time
        
        //Define the shape of this block.  x1 and y1 are the upper-left (Northwest)
        //origin, xc and yc define the center, and x2, y2 mark the lower-right
        //(Southeast) corner, and nextSize is half the size of this block.
        nextSize = ( size - 1 ) / 2 + 1;
        x2 = x1 + ( size - 1 );
        y2 = y1 + ( size - 1 );
        xc = x1 + nextSize - 1;
        yc = y1 + nextSize - 1;
        
        /*
        ** Initialize triangle strip with two copies of the first vertex.
        ** The first copy should be skipped over during rendering.
        */

        triangleStrip.add( findIndex( x1, y2 ) );
        triangleStrip.add( findIndex( x1, y2 ) );
        lastParity = 0;
        
        // South
        refineSubMesh( triangleStrip, xc, yc, x1, y2, x2, y2, n );
        appendIndex( triangleStrip, x2, y2, 1 );
        
        // East
        refineSubMesh( triangleStrip, xc, yc, x2, y2, x2, y1, n );
        appendIndex( triangleStrip, x2, y1, 1 );
        
        // North
        refineSubMesh( triangleStrip, xc, yc, x2, y1, x1, y1, n );
        appendIndex( triangleStrip, x1, y1, 1 );
        
        // West
        refineSubMesh( triangleStrip, xc, yc, x1, y1, x1, y2, n );
        appendIndex( triangleStrip, x1, y2, 1 );
    }
    
    public void refineSubMesh( List< Integer > triangleStrip, int xi, int yi, int xj, int yj, int xk, int yk, int level )
    {
        // calculate the center vertex, that splits the given triangle into two halfves
        // depending on the orientation of the triangle, either xj and xk or yj anc yk are the same so the
        // coordinate (xc,yc) always specifies a point, that is centered on one of the triangles edges
        int xc = ( xj + xk ) / 2;
        int yc = ( yj + yk ) / 2;
        // if we have some refinement levels to do and the vertex we found by splitting one edge is enabled, then we refine further 
        boolean refine = level > 1 && isEnabled( xc, yc );
        //        if(refine) System.out.println("refine: level="+level+" Vi=["+xi+","+yi+"] Vj=["+xj+","+yj+"] Vk=["+xk+","+yk+"] Vc=["+xc+","+yc+"] enabled="+isEnabled(xc,yc));
        // "left" child
        if ( refine )
            refineSubMesh( triangleStrip, xc, yc, xj, yj, xi, yi, level - 1 );
        appendIndex( triangleStrip, xi, yi, level & 1 );
        // "right" child
        if ( refine )
            refineSubMesh( triangleStrip, xc, yc, xi, yi, xk, yk, level - 1 );
    }
    
    private void appendIndex( List< Integer > triangleStrip, int x, int y )
    {
        int i = findIndex( x, y );
        if ( i != -1 )
        {
            triangleStrip.add( i );
        }
    }
    
    private void appendIndex( List< Integer > triangleStrip, int x, int y, int parity )
    {
        int i = findIndex( x, y );
        int n = triangleStrip.size() - 1;
        if ( i != triangleStrip.get( n - 1 ) && i != triangleStrip.get( n ) )
        {
            if ( parity != lastParity )
                lastParity = parity;
            else
                triangleStrip.add( triangleStrip.get( n - 1 ) );
            triangleStrip.add( i );
        }
    }
}
