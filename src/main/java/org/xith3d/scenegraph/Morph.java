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

/**
 * The Morph leaf node permits an application to morph between multiple GeometryArrays. 
 * The Morph node contains a single Appearance node, an array of GeometryArray objects, 
 * and an array of corresponding weights. The Morph node combines these GeometryArrays 
 * into an aggregate shape based on each GeometryArray's corresponding weight. 
 * Typically, Behavior nodes will modify the weights to achieve various morphing effects. 
 * <p>
 * The following restrictions apply to each GeometryArray object in the specified array of GeometryArray objects: 
 * <p>
 * - All N geometry arrays must be of the same type (that is, the same subclass of GeometryArray). 
 * <p>
 * - The vertexFormat, texCoordSetCount, and validVertexCount must be the same for all N geometry arrays. 
 * <p>
 * - The texCoordSetMap array must be identical (element-by-element) for all N geometry arrays. 
 * <p>
 * - For IndexedGeometryArray objects, the validIndexCount must be the same for all N geometry arrays. 
 * <p>
 * - For GeometryStripArray objects, the stripVertexCounts array must be identical (element-by-element) for all N geometry arrays. 
 * <p>
 * - For IndexedGeometryStripArray objects, the stripIndexCounts array must be identical (element-by-element) for all N geometry arrays. 
 * <p>
 * For indexed geometry, the array lengths of each enabled vertex component (coord, color, normal, texcoord) 
 * must be the same for all N geometry arrays. 
 * For IndexedGeometryArray objects, the vertex arrays are morphed before the indexes are applied. 
 * Only the indexes in the first geometry array (geometry[0]) are used when rendering the geometry. 
 * 
 * @author David Yazel
 */
public class Morph extends Shape3D
{
    // only here for compatibility
    public static final int ALLOW_GEOMETRY_ARRAY_READ = 20;
    public static final int ALLOW_GEOMETRY_ARRAY_WRITE = 21;
    public static final int ALLOW_WEIGHTS_READ = 22;
    public static final int ALLOW_WEIGHTS_WRITE = 23;
    
    private Geometry[] geometryArrays = null;
    private double[] weights = null;
    
    private float[][] coordinateData = null;
    private float[][] normalData = null;
    private float[][] colorData = null;
    private float[][][] texCoordData = null;
    
    private float[][] refData;
    private float[] refWeights;
    private int[] refGeoms;
    
    public void setGeometryArrays( Geometry[] geometryArrays )
    {
        if ( ( geometryArrays == null ) || ( geometryArrays.length == 0 ) )
            throw new IllegalArgumentException( "Morph: Number of GeometryArrays should be non-zero" );
        for ( int i = 1; i < geometryArrays.length; i++ )
        {
            doErrorCheck( geometryArrays[ i ], geometryArrays[ i - 1 ] );
        }
        
        this.geometryArrays = geometryArrays;
        buildCompatibleGeometry( geometryArrays[ 0 ] );
        
        double[] weights = new double[ geometryArrays.length ];
        weights[ 0 ] = 1.0;
        setWeights( weights );
    }
    
    private void doErrorCheck( Geometry a1, Geometry a2 )
    {
        if ( ( a1 == null ) || ( a2 == null ) )
            throw new IllegalArgumentException( "Morph: All GeometryArrays must be non-null" );
        if ( ( a1.getVertexFormat() != a2.getVertexFormat() ) || ( a1.getValidVertexCount() != a2.getValidVertexCount() ) )
            throw new IllegalArgumentException( "Morph: All GeometryArrays must have same vertexFormat, same validVertexCount" );
        
        int[] texMap1 = a1.getTexCoordSetMap();
        int[] texMap2 = a2.getTexCoordSetMap();
        if ( ( texMap1 != null ) && ( texMap2 != null ) )
        {
            if ( texMap1.length != texMap2.length )
                throw new IllegalArgumentException( "Morph: All GeometryArrays must have same texCoordSetMap length" );
            for ( int i = 0; i < texMap1.length; i++ )
            {
                if ( texMap1[ i ] != texMap2[ i ] )
                    throw new IllegalArgumentException( "Morph: All GeometryArrays must have same texCoordSetMap" );
            }
        }
        else if ( ( texMap1 != null ) || ( texMap2 != null ) )
            throw new IllegalArgumentException( "Morph: All GeometryArrays must have same non-null texCoordSetMap" );
    }
    
    private void buildCompatibleGeometry( Geometry a )
    {
        setGeometry( a.cloneNodeComponent( true ) );
        coordinateData = new float[ geometryArrays.length ][];
        for ( int j = 0; j < geometryArrays.length; j++ )
        {
            coordinateData[ j ] = geometryArrays[ j ].getCoordRefFloat();
        }
        
        if ( ( a.getVertexFormat() & Geometry.NORMALS ) != 0 )
        {
            normalData = new float[ geometryArrays.length ][];
            for ( int j = 0; j < geometryArrays.length; j++ )
            {
                normalData[ j ] = geometryArrays[ j ].getNormalRefFloat();
            }
        }
        else
        {
            normalData = null;
        }
        
        if ( a.hasColorAlpha() )
        {
            colorData = new float[ geometryArrays.length ][];
            for ( int j = 0; j < geometryArrays.length; j++ )
            {
                colorData[ j ] = geometryArrays[ j ].getColorRefFloat();
            }
        }
        else
        {
            colorData = null;
        }
        
        final int[] tuSetMap = a.getTexCoordSetMap();
        if ( tuSetMap.length > 0 )
        {
            texCoordData = new float[ tuSetMap.length ][][];
            for ( int i = 0; i < tuSetMap.length; i++ )
            {
                texCoordData[ i ] = new float[ geometryArrays.length ][];
                for ( int j = 0; j < geometryArrays.length; j++ )
                {
                    texCoordData[ i ][ j ] = geometryArrays[ j ].getTexCoordRefFloat( tuSetMap[ i ] );
                }
            }
        }
        else
        {
            texCoordData = null;
        }
    }
    
    public final Geometry getGeometryArray( int i )
    {
        return ( geometryArrays[ i ] );
    }
    
    /**
     * Sets this Morph node's morph weight vector. 
     * The Morph node "weights" the corresponding GeometryArray by the amount specified. 
     * The weights apply a morph weight vector component that creates the desired morphing effect. 
     * The length of the weights parameter must be equal to the length of the array with which this 
     * Morph node was created, otherwise an IllegalArgumentException is thrown. 
     * 
     * There is no requirement that sum of all weights should be equal to 1.0.
     */
    public void setWeights( double[] weights )
    {
        if ( weights.length != geometryArrays.length )
            throw new IllegalArgumentException( "Morph: number of weights not same as number of GeometryArrays" );
        if ( ( this.weights == null ) || ( this.weights.length != weights.length ) )
            this.weights = new double[ weights.length ];
        for ( int i = weights.length - 1; i >= 0; i-- )
            this.weights[ i ] = weights[ i ];
        updateGeometryData();
    }
    
    /**
     * Note that we assume we are using only NIO buffers, i.e. GeomNioFloatData.
     */
    private void doWeight( float[][] src, float[] weights, int n, GeomNioFloatData dstData )
    {
        final FloatBuffer dst = dstData.getBuffer();
        dst.rewind();
        int dstCount = src[ 0 ].length;
        switch ( n )
        {
            case 1:
            {
                float[] s0 = src[ 0 ];
                float w0 = weights[ 0 ];
                for ( int i = 0; i < dstCount; i++ )
                    dst.put( s0[ i ] * w0 );
            }
                return;
                
            case 2:
            {
                float[] s0 = src[ 0 ];
                float[] s1 = src[ 1 ];
                float w0 = weights[ 0 ];
                float w1 = weights[ 1 ];
                
                for ( int i = 0; i < dstCount; i++ )
                    dst.put( s0[ i ] * w0 + s1[ i ] * w1 );
            }
                return;
                
            case 3:
            {
                float[] s0 = src[ 0 ];
                float[] s1 = src[ 1 ];
                float[] s2 = src[ 2 ];
                float w0 = weights[ 0 ];
                float w1 = weights[ 1 ];
                float w2 = weights[ 2 ];
                for ( int i = 0; i < dstCount; i++ )
                    dst.put( s0[ i ] * w0 + s1[ i ] * w1 + s2[ i ] * w2 );
            }
                return;
                
            default:
            {
                for ( int i = 0; i < dstCount; i++ )
                {
                    float v = src[ 0 ][ i ] * weights[ 0 ];
                    for ( int j = 1; j < n; j++ )
                    {
                        v += src[ j ][ i ] * weights[ j ];
                    }
                    dst.put( v );
                }
            }
        }
    }
    
    private void updateGeometryData()
    {
        Geometry ga = getGeometry();
        if ( ( refData == null ) || ( refData.length != geometryArrays.length ) )
        {
            refData = new float[ geometryArrays.length ][];
            refWeights = new float[ geometryArrays.length ];
            refGeoms = new int[ geometryArrays.length ];
        }
        int n = 0;
        for ( int i = 0; i < weights.length; i++ )
        {
            float w = (float)weights[ i ];
            if ( w != 0.0f )
            {
                refWeights[ n ] = w;
                refGeoms[ n++ ] = i;
            }
        }
        if ( n == 0 )
            return;
        
        // TODO Reimplement array changes via direct access to NIO buffer data
        //float[] dst;
        
        for ( int i = 0; i < n; i++ )
        {
            refData[ i ] = coordinateData[ refGeoms[ i ] ];
        }
        doWeight( refData, refWeights, n, ga.getCoordinatesData() );
        ga.getCoordinatesData().setDirty( true );
        
        if ( ( ga.getVertexFormat() & Geometry.NORMALS ) != 0 )
        {
            for ( int i = 0; i < n; i++ )
            {
                refData[ i ] = normalData[ refGeoms[ i ] ];
            }
            doWeight( refData, refWeights, n, ga.getNormalsData() );
            ga.getNormalsData().setDirty( true );
        }
        
        if ( ga.hasColorAlpha() )
        {
            for ( int i = 0; i < n; i++ )
            {
                refData[ i ] = colorData[ refGeoms[ i ] ];
            }
            doWeight( refData, refWeights, n, ga.getColorData() );
            ga.getColorData().setDirty( true );
        }
        
        int[] texCoordSetMap = ga.getTexCoordSetMap();
        int texCoordSetCount = ( texCoordSetMap == null ) ? 0 : texCoordSetMap.length;
        if ( texCoordSetCount > 0 )
        {
            for ( int j = 0; j < texCoordSetCount; j++ )
            {
                for ( int i = 0; i < n; i++ )
                {
                    refData[ i ] = texCoordData[ j ][ refGeoms[ i ] ];
                }
                doWeight( refData, refWeights, n, ga.getTexCoordsData( j ) );
                ga.getTexCoordsData( j ).setDirty( true );
            }
        }
    }
    
    public final int getGeometryArrayCount()
    {
        return ( geometryArrays.length  );
    }
    
    /**
     * Constructs a new Morph object with a null geometry array components and
     * a null appearance component.
     */
    public Morph()
    {
        super();
    }
    
    public Morph( Geometry[] geometryArrays )
    {
        setGeometryArrays( geometryArrays );
    }
    
    public Morph( Geometry[] geometryArrays, Appearance appearance )
    {
        setGeometryArrays( geometryArrays );
        setAppearance( appearance );
    }
}
