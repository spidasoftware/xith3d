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
package org.xith3d.scenegraph.primitives;

import org.openmali.FastMath;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.IndexedTriangleArray;
import org.xith3d.scenegraph.IndexedTriangleStripArray;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.StaticTransform;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.TriangleArray;

/**
 * An open cylinder pointing in the +/-y direction.
 * Has unit height.
 * 
 * @author Daniel Herring
 * @author Marvin Froehlich (aka Qudus)
 */
public class Cylinder extends Shape3D
{
    private static GeometryType geomConstructTypeHint = GeometryType.TRIANGLE_ARRAY;
    
    /**
     * Sets the hint for this ShapeType's Geometry to be constructed of a certain type.
     * 
     * @param hint
     */
    public static void setGeometryConstructionTypeHint( GeometryType hint )
    {
        switch ( hint )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
            case INDEXED_TRIANGLE_ARRAY:
            case TRIANGLE_ARRAY:
                geomConstructTypeHint = hint;
                break;
            
            default:
                throw new UnsupportedOperationException( "Currently " + Cylinder.class.getSimpleName() + " does not support " + hint );
        }
    }
    
    /**
     * @return the hint for this ShapeType's Geometry to be constructed of a certain type.
     */
    public static GeometryType getGeometryConstructionTypeHint()
    {
        return ( geomConstructTypeHint );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param radius
     * @param height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITSA( float radius, float height, float taper, boolean closed, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( closed )
            System.err.println( "A closed Cylinder is currently not supported for non TriangleArray geometry" );
        
        closed = false;
        
        Point3f[] vertices = null;
        int[] indices = null;
        int v0 = 0;
        int i0 = 0;
        
        if ( closed )
        {
            v0 = 2;
            vertices = new Point3f[ slices * 2 + 2 + v0 ];
            vertices[ 0 ] = new Point3f( 0.0f, -0.5f, 0.0f );
            vertices[ 1 ] = new Point3f( 0.0f, +0.5f, 0.0f );
            
            i0 = (int)FastMath.floor( (float)slices / 3f ) * 5;
            if ( ( slices % 3 ) == 1 )
                i0 += 3;
            else if ( ( slices % 3 ) == 2 )
                i0 += 4;
            indices = new int[ slices * 2 + 2 + i0 ];
        }
        else
        {
            vertices = new Point3f[ slices * 2 + 2 ];
            indices = new int[ slices * 2 + 2 ];
        }
        
        Vector3f[] normals = null;
        TexCoord2f[] texCoords = null;
        
        if ( ( features & Geometry.NORMALS ) > 0 )
        {
            normals = new Vector3f[ vertices.length ];
        }
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texCoordsSize == 2 )
                texCoords = new TexCoord2f[ vertices.length ];
        }
        
        final float r0 = 1.0f * radius;
        final float r1 = taper * radius;
        final float halfHeight = height / 2f;
        
        for ( int i = 0; i < slices + 1; i++ )
        {
            float a = (float)i * FastMath.TWO_PI / (float)slices;
            
            vertices[ v0 + i * 2 + 0 ] = new Point3f( FastMath.cos( a ) * r0, -halfHeight, FastMath.sin( a ) * r0 );
            vertices[ v0 + i * 2 + 1 ] = new Point3f( FastMath.cos( a ) * r1, +halfHeight, FastMath.sin( a ) * r1 );
            
            if ( normals != null )
            {
                normals[ v0 + i * 2 + 0 ] = new Vector3f( FastMath.cos( a ), 0.0f, FastMath.sin( a ) );
                normals[ v0 + i * 2 + 1 ] = new Vector3f( FastMath.cos( a ), 0.0f, FastMath.sin( a ) );
            }
            
            if ( texCoords != null )
            {
                texCoords[ v0 + i * 2 + 0 ] = new TexCoord2f( (float)i / (float)slices, 0.0f );
                texCoords[ v0 + i * 2 + 1 ] = new TexCoord2f( (float)i / (float)slices, 1.0f );
            }
            
            indices[ i0 + i * 2 + 0 ] = ( i * 2 + 0 );
            indices[ i0 + i * 2 + 1 ] = ( i * 2 + 1 );
        }
        
        int[] stripLengths;
        
        if ( closed )
        {
            stripLengths = new int[ 2 ];
        }
        else
        {
            stripLengths = new int[ 2 ];
            for ( int i = 0; i < stripLengths.length; i++ )
            {
                stripLengths[ i ] = slices + 1;
            }
        }
        
        return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_STRIP_ARRAY, vertices, normals, texCoords, indices, stripLengths ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param radius
     * @param height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleStripArray createGeometryITSA( float radius, float height, float taper, boolean closed, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        GeometryConstruct gc = createGeometryConstructITSA( radius, height, taper, closed, slices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createIndexedTriangleStripArray( gc ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param radius
     * @param height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructITA( float radius, float height, float taper, boolean closed, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        Point3f[] vertices = new Point3f[ slices * 2 ];
        int[] indices = new int[ slices * 2 * 3 ];
        Vector3f[] normals = null;
        TexCoord2f[] texCoords = null;
        
        if ( ( features & Geometry.NORMALS ) > 0 )
        {
            normals = new Vector3f[ slices * 2 ];
        }
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) != 0 )
        {
            if ( texCoordsSize == 2 )
                texCoords = new TexCoord2f[ slices * 2 ];
        }
        
        final float r0 = 1.0f * radius;
        final float r1 = taper * radius;
        final float halfHeight = height / 2f;
        
        for ( int i = 0; i < slices; i++ )
        {
            float a = (float)i * FastMath.TWO_PI / (float)slices;
            
            vertices[ i * 2 + 0 ] = new Point3f( FastMath.cos( a ) * r0, -halfHeight, -FastMath.sin( a ) * r0 );
            vertices[ i * 2 + 1 ] = new Point3f( FastMath.cos( a ) * r1, +halfHeight, -FastMath.sin( a ) * r1 );
            
            if ( normals != null )
            {
                normals[ i * 2 + 0 ] = new Vector3f( FastMath.cos( a ), 0.0f, -FastMath.sin( a ) );
                normals[ i * 2 + 1 ] = new Vector3f( FastMath.cos( a ), 0.0f, -FastMath.sin( a ) );
            }
            
            if ( texCoords != null )
            {
                texCoords[ i * 2 + 0 ] = new TexCoord2f( (float)i / (float)slices, 0.0f );
                texCoords[ i * 2 + 1 ] = new TexCoord2f( (float)i / (float)slices, 1.0f );
            }
            
            indices[ i * 2 * 3 + 0 ] = ( i * 2 + 3 ) % ( slices * 2 );
            indices[ i * 2 * 3 + 1 ] = ( i * 2 + 1 ) % ( slices * 2 );
            indices[ i * 2 * 3 + 2 ] = ( i * 2 + 2 ) % ( slices * 2 );
            indices[ i * 2 * 3 + 3 ] = ( i * 2 + 1 ) % ( slices * 2 );
            indices[ i * 2 * 3 + 4 ] = ( i * 2 + 0 ) % ( slices * 2 );
            indices[ i * 2 * 3 + 5 ] = ( i * 2 + 2 ) % ( slices * 2 );
        }
        
        return ( new GeometryConstruct( GeometryType.INDEXED_TRIANGLE_ARRAY, vertices, normals, texCoords, indices, null ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param radius
     * @param height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static IndexedTriangleArray createGeometryITA( float radius, float height, float taper, boolean closed, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        GeometryConstruct gc = createGeometryConstructITA( radius, height, taper, closed, slices, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createIndexedTriangleArray( gc ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param radius
     * @param height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static GeometryConstruct createGeometryConstructTA( float radius, float height, float taper, boolean closed, int slices, int rows, int features, boolean colorAlpha, int texCoordsSize )
    {
        if ( slices < 3 )
        {
            throw new IllegalArgumentException( "slices < 3" );
        }
        
        float rTop, rBottom;
        taper = Math.abs( taper );
        if ( taper <= 1 )
        {
            rTop = taper * radius;
            rBottom = radius;
        }
        else
        {
            rTop = radius;
            rBottom = radius / taper;
        }
        
        Tuple3f[] vertices;
        Vector3f[] normals;
        TexCoord2f[] texture;
        int vtOff = 0;
        int noOff = 0;
        int txOff = 0;
        
        final int numVerticesBody = 3 * 2 * slices*rows;
        
        float texBase = height;
        
        if ( closed )
        {
            texBase = radius + height + radius;
            
            // Create caps...
            
            GeometryConstruct gcTop = Disk.createGeometryConstructTA( rTop, slices, features, colorAlpha, texCoordsSize );
            GeometryConstruct gcBottom = Disk.createGeometryConstructTA( rBottom, slices, features, colorAlpha, texCoordsSize );
            
            StaticTransform.rotateX( gcTop.getCoordinates(), FastMath.PI_HALF );
            StaticTransform.rotateX( gcBottom.getCoordinates(), -FastMath.PI_HALF );
            
            if ( gcTop.getNormals() != null )
            {
                StaticTransform.rotateX( gcTop.getNormals(), FastMath.PI_HALF );
            }
            
            if ( gcBottom.getNormals() != null )
            {
                StaticTransform.rotateX( gcBottom.getNormals(), -FastMath.PI_HALF );
            }
            
            StaticTransform.translate( gcTop.getCoordinates(), 0f, +( height / 2f ), 0f );
            StaticTransform.translate( gcBottom.getCoordinates(), 0f, -( height / 2f ), 0f );
            
            vtOff = gcTop.numVertices();
            noOff = gcTop.numNormals();
            txOff = gcTop.numTextureCoordinates2f();
            
            final int vtOff2 = vtOff + numVerticesBody;
            final int noOff2 = noOff + numVerticesBody;
            final int txOff2 = txOff + numVerticesBody;
            
            vertices = new Tuple3f[ vtOff + numVerticesBody + gcBottom.numVertices() ];
            normals = new Vector3f[ noOff + numVerticesBody + gcBottom.numNormals() ];
            texture = new TexCoord2f[ txOff + numVerticesBody + gcBottom.numTextureCoordinates2f() ];
            
            int j = 0;
            for ( int i = 0; i < gcTop.numVertices(); i++ )
            {
                vertices[ j++ ] = gcTop.getCoordinates()[ i ];
            }
            j = vtOff2;
            for ( int i = 0; i < gcBottom.numVertices(); i++ )
            {
                vertices[ j++ ] = gcBottom.getCoordinates()[ i ];
            }
            
            j = 0;
            for ( int i = 0; i < gcTop.numNormals(); i++ )
            {
                normals[ j++ ] = gcTop.getNormals()[ i ];
            }
            j = noOff2;
            for ( int i = 0; i < gcBottom.numNormals(); i++ )
            {
                normals[ j++ ] = gcBottom.getNormals()[ i ];
            }
            
            float texShift = ( radius + height ) / texBase;
            j = 0;
            int n = gcTop.numTextureCoordinates2f();
            for ( int i = 0; i < n; i++ )
            {
                texture[ j ] = gcTop.getTextureCoordinates2f()[ i ];
                final float i3 = i / 3;
                switch ( i % 3 )
                {
                    case 0:
                        texture[ j ].set( ( i3 + 0.0f ) / slices, texShift );
                        break;
                    case 1:
                        texture[ j ].set( ( i3 + 0.0f ) / slices, 1f );
                        break;
                    case 2:
                        texture[ j ].set( ( i3 + 1.0f ) / slices, texShift );
                        break;
                }
                
                j++;
            }
            texShift = radius / texBase;
            j = txOff2;
            n = gcBottom.numTextureCoordinates2f();
            for ( int i = 0; i < n; i++ )
            {
                texture[ j ] = gcBottom.getTextureCoordinates2f()[ i ];
                final float i3 = i / 3;
                switch ( i % 3 )
                {
                    case 0:
                        texture[ j ].set( ( i3 + 0.0f ) / slices, texShift );
                        break;
                    case 1:
                        texture[ j ].set( ( i3 + 0.0f ) / slices, 0f );
                        break;
                    case 2:
                        texture[ j ].set( ( i3 + 1.0f ) / slices, texShift );
                        break;
                }
                
                j++;
            }
        }
        else
        {
            vertices = new Tuple3f[ numVerticesBody ];
            normals = new Vector3f[ numVerticesBody ];
            texture = new TexCoord2f[ numVerticesBody ];
        }
        
        Vector3f normal0, normal1;
        Point3f top, bottom; // temporary variables
        TexCoord2f texTop, texBottom;
        top = new Point3f( rTop, +( height / 2f ), 0f );
        bottom = new Point3f( rBottom, -( height / 2f ), 0f );
        if ( closed )
        {
            texTop = new TexCoord2f( 0f, ( radius + height ) / texBase );
            texBottom = new TexCoord2f( 0f, radius / texBase );
        }
        else
        {
            texTop = new TexCoord2f( 0f, 1f );
            texBottom = new TexCoord2f( 0f, 0f );
        }
        normal0 = new Vector3f( 1.0f, 0f, 0f );
        normal1 = new Vector3f(1.0f, 0f, 0f);
        int index = 0;
        Point3f rowBottom = bottom.clone();
        float rowBottomRadius = rBottom;
        TexCoord2f rowTexBottom = texBottom.clone();
        float texIncrement = (texTop.getT()-texBottom.getT())/(float)rows;
        float rowIncrement = (top.getY() - bottom.getY())/(float)rows;

        float taperIncrement = (rTop-rBottom)/(float)rows;

        for (int row = 0; row < rows; row++)
        {
            float rowTopRadius = rowBottomRadius + taperIncrement ;
            Point3f rowTop = new Point3f(rowTopRadius, rowBottom.getY(), 0f);
            rowTop.addY(rowIncrement);
            TexCoord2f rowTexTop = rowTexBottom.clone();
            rowTexTop.addT(texIncrement);
            

            for (int i = 0; i < slices; i++)
            {
                float radPerSlice = FastMath.TWO_PI / slices;
                float xI = +FastMath.cos((i) * radPerSlice);
                float zI = -FastMath.sin((i) * radPerSlice);
                float xIPlus1 = +FastMath.cos((i + 1) * radPerSlice);
                float zIPlus1 = -FastMath.sin((i + 1) * radPerSlice);

                if (i+1 == slices) {
                    // cheat to make sure that we don't have a seam
                    xIPlus1 = 1f;
                    zIPlus1 = 0f;
                }

                normal0.setX(xI);
                normal0.setZ(zI);
                normal0.normalize();

                normal1.setX(xIPlus1);
                normal1.setZ(zIPlus1);
                normal1.normalize();

                // first triangle
                vertices[vtOff + index] = new Point3f(rowTop);
                normals[noOff + index] = new Vector3f(normal0);
                texture[txOff + index++] = new TexCoord2f(rowTexTop);

                vertices[vtOff + index] = new Point3f(rowBottom);
                normals[noOff + index] = new Vector3f(normal0);
                texture[txOff + index++] = new TexCoord2f(rowTexBottom);

                rowTop.setX(rowTopRadius * xIPlus1);
                rowTop.setZ(rowTopRadius * zIPlus1);
                rowTexTop.setS((i + 1f) / slices);

                vertices[vtOff + index] = new Point3f(rowTop);
                normals[noOff + index] = new Vector3f(normal1);
                texture[txOff + index++] = new TexCoord2f(rowTexTop);

                // second triangle
                vertices[vtOff + index] = new Point3f(rowTop);
                normals[noOff + index] = new Vector3f(normal1);
                texture[txOff + index++] = new TexCoord2f(rowTexTop);

                vertices[vtOff + index] = new Point3f(rowBottom);
                normals[noOff + index] = new Vector3f(normal0);
                texture[txOff + index++] = new TexCoord2f(rowTexBottom);

                rowBottom.setX(rowBottomRadius * xIPlus1);
                rowBottom.setZ(rowBottomRadius * zIPlus1);
                rowTexBottom.setS((i + 1f) / slices);

                vertices[vtOff + index] = new Point3f(rowBottom);
                normals[noOff + index] = new Vector3f(normal1);
                texture[txOff + index++] = new TexCoord2f(rowTexBottom);
            }

            rowBottom = rowTop;
            rowTexBottom = rowTexTop;
            rowBottomRadius = rowTopRadius;
        }
        if ( ( features & Geometry.NORMALS ) == 0 )
            normals = null;
        
        if ( ( features & Geometry.TEXTURE_COORDINATES ) == 0 )
            texture = null;
        
        return ( new GeometryConstruct( GeometryType.TRIANGLE_ARRAY, vertices, normals, texture ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param radius
     * @param height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public static TriangleArray createGeometryTA( float radius, float height, float taper, boolean closed, int slices, int rows, int features, boolean colorAlpha, int texCoordsSize )
    {
        GeometryConstruct gc = createGeometryConstructTA( radius, height, taper, closed, slices, rows, features, colorAlpha, texCoordsSize );
        
        return ( GeomFactory.createTriangleArray( gc ) );
    }
    
    public static Geometry createGeometry( float radius, float height, float taper, boolean closed, int slices, int rows, int features, boolean colorAlpha, int texCoordsSize )
    {
        switch ( getGeometryConstructionTypeHint() )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
                return ( createGeometryITSA( radius, height, taper, closed, slices, features, colorAlpha, texCoordsSize ) );
                
            case INDEXED_TRIANGLE_ARRAY:
                return ( createGeometryITA( radius, height, taper, closed, slices, features, colorAlpha, texCoordsSize ) );
                
            case TRIANGLE_ARRAY:
                return ( createGeometryTA( radius, height, taper, closed, slices, rows, features, colorAlpha, texCoordsSize ) );
                
            default:
                throw new Error( getGeometryConstructionTypeHint().getCorrespondingClass().getSimpleName() + " creation is not yet implemented." );
        }
    }
    
    /**
     * Generates n cylinder.
     */
    private Cylinder( Geometry geom )
    {
        super( geom );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit radius and height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Cylinder( float taper, boolean closed, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( createGeometry( 1f, 1f, taper, closed, slices, 1, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit radius and height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     */
    public Cylinder( float taper, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( taper, false, slices, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, boolean closed, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( createGeometry( size, size, taper, closed, slices, 1, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( size, taper, false, slices, features, colorAlpha, texCoordsSize );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, boolean closed, int slices, int rows, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( createGeometry( radius, height, taper, closed, slices, rows, features, colorAlpha, texCoordsSize ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param features Generate the data for GeometryArray.COLOR_3 | GeometryArray.NORMALS | ...
     * @param colorAlpha
     * @param texCoordsSize
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, int slices, int features, boolean colorAlpha, int texCoordsSize )
    {
        this( radius, height, taper, false, slices,1,  features, colorAlpha, texCoordsSize );
    }
    
    private static final int TEX_FEATURES = Geometry.COORDINATES | Geometry.NORMALS | Geometry.TEXTURE_COORDINATES;
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Cylinder( float taper, boolean closed, int slices, Texture texture )
    {
        this( taper, closed, slices, TEX_FEATURES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Cylinder( float taper, int slices, Texture texture )
    {
        this( taper, slices, TEX_FEATURES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, boolean closed, int slices, Texture texture )
    {
        this( size, taper, closed, slices, TEX_FEATURES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, int slices, Texture texture )
    {
        this( size, taper, slices, TEX_FEATURES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, boolean closed, int slices, int rows, Texture texture )
    {
        this( radius, height, taper, closed, slices, rows, TEX_FEATURES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, int slices, Texture texture )
    {
        this( radius, height, taper, slices, TEX_FEATURES, false, 2 );
        
        this.getAppearance( true ).setTexture( texture );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Cylinder( float taper, boolean closed, int slices, String texture )
    {
        this( taper, closed, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     */
    public Cylinder( float taper, int slices, String texture )
    {
        this( taper, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, boolean closed, int slices, String texture )
    {
        this( size, taper, closed, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, int slices, String texture )
    {
        this( size, taper, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param size amount to enlarge the cylinder by
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float size, float height, float taper, boolean closed, int slices, String texture )
    {
        this( size, height, taper, closed, slices,1, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param texture the Texture to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, int slices, String texture )
    {
        this( radius, height, taper, slices, TextureLoader.getInstance().getTexture( texture ) );
    }
    
    private static final int COLOR_FEATURES = Geometry.COORDINATES;
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param color the color to be applied to the Shape's Appearance
     */
    public Cylinder( float taper, boolean closed, int slices, Colorf color )
    {
        this( taper, closed, slices, COLOR_FEATURES, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param color the color to be applied to the Shape's Appearance
     */
    public Cylinder( float taper, int slices, Colorf color )
    {
        this( taper, slices, COLOR_FEATURES, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param color the color to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, boolean closed, int slices, Colorf color )
    {
        this( size, taper, closed, slices, COLOR_FEATURES, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param color the color to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, int slices, Colorf color )
    {
        this( size, taper, slices, COLOR_FEATURES, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param color the color to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, boolean closed, int slices, Colorf color )
    {
        this( radius, height, taper, closed, slices,1,  COLOR_FEATURES, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param color the color to be applied to the Shape's Appearance
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float taper, int slices, float radius, float height, Colorf color )
    {
        this( radius, height, taper, slices, COLOR_FEATURES, false, 2 );
        
        this.getAppearance( true ).getColoringAttributes( true ).setColor( color );
        if ( color.hasAlpha() )
            this.getAppearance( true ).getTransparencyAttributes( true ).setTransparency( color.getAlpha() );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     */
    public Cylinder( float taper, boolean closed, int slices, Appearance app )
    {
        this( taper, closed, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     */
    public Cylinder( float taper, int slices, Appearance app )
    {
        this( taper, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, boolean closed, int slices, Appearance app )
    {
        this( size, taper, closed, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * Has unit height.
     * 
     * @param size amount to enlarge the cylinder by
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     * 
     * @see StaticTransform#scale(Shape3D, float)
     */
    public Cylinder( float size, float taper, int slices, Appearance app )
    {
        this( size, taper, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param closed shall the cylinder have caps?
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, boolean closed, int slices, Appearance app )
    {
        this( radius, height, taper, closed, slices,1, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
    
    /**
     * Generate a cylinder pointing in the +/-y direction with unit height.
     * With specified height and radius
     * 
     * @param radius Cylinder radius
     * @param height Cylinder height
     * @param taper ratio of upper to lower radii (alpha=1 gives parallel walls)
     * @param slices Number of vertical stripes down the cone
     * @param app the Appearance to be applied to this Shape
     * 
     * @see StaticTransform#scale(Shape3D, Tuple3f)
     */
    public Cylinder( float radius, float height, float taper, int slices, Appearance app )
    {
        this( radius, height, taper, slices, Geometry.COORDINATES | Geometry.NORMALS | GeomFactory.getFeaturesFromAppearance( app ), false, GeomFactory.getTexCoordsSize( app ) );
        
        setAppearance( app );
    }
}
