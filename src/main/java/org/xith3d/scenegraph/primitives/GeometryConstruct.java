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

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.TexCoord3f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;

/**
 * This is nothing more than a container for Geometry construction data.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GeometryConstruct
{
    private GeometryType  geometryTypeHint;
    private Tuple3f[]     coords;
    private Vector3f[]    normals;
    private TexCoord2f[]  texCoords2f;
    private TexCoord3f[]  texCoords3f;
    private Colorf[]      colors;
    private int[]         indices;
    private int[]         stripLengths;
    
    public GeometryType getGeometryTypeHint()
    {
        return ( geometryTypeHint );
    }
    
    public Tuple3f[] getCoordinates()
    {
        return ( coords );
    }
    
    public int numVertices()
    {
        if ( coords == null )
            return ( 0 );
        
        return ( coords.length  );
    }
    
    public Vector3f[] getNormals()
    {
        return ( normals );
    }
    
    public int numNormals()
    {
        if ( normals == null )
            return ( 0 );
        
        return ( normals.length  );
    }
    
    public TexCoord2f[] getTextureCoordinates2f()
    {
        return ( texCoords2f );
    }
    
    public int numTextureCoordinates2f()
    {
        if ( texCoords2f == null )
            return ( 0 );
        
        return ( texCoords2f.length  );
    }
    
    public TexCoord3f[] getTextureCoordinates3f()
    {
        return ( texCoords3f );
    }
    
    public int numTextureCoordinates3f()
    {
        if ( texCoords3f == null )
            return ( 0 );
        
        return ( texCoords3f.length  );
    }
    
    public Colorf[] getColors()
    {
        return ( colors );
    }
    
    public int numColors()
    {
        if ( colors == null )
            return ( 0 );
        
        return ( colors.length  );
    }
    
    public int[] getIndices()
    {
        return ( indices );
    }
    
    public int numIndices()
    {
        if ( indices == null )
            return ( 0 );
        
        return ( indices.length  );
    }
    
    public int[] getStripLengths()
    {
        return ( stripLengths );
    }
    
    /**
     * Calculates the GeometryArray features.
     * 
     * @return the GeometryArray features
     */
    public int calculateFeatures()
    {
        return ( GeomFactory.calculateFeatures( getCoordinates(), getNormals(), getColors(), getTextureCoordinates2f(), getTextureCoordinates3f() ) );
    }
    
    public void check()
    {
        if ( getGeometryTypeHint() == null )
            throw new Error( "GeometryTypeHint must not be null" );
        
        if ( numVertices() == 0 )
            throw new Error( "vertices must not be null nor 0 for " + getGeometryTypeHint() );
        
        switch ( getGeometryTypeHint() )
        {
            case INDEXED_TRIANGLE_STRIP_ARRAY:
                if ( numIndices() == 0 )
                    throw new Error( "vertices must not be null nor 0 for " + getGeometryTypeHint() );
                
                if ( ( getStripLengths() == null ) || ( getStripLengths().length == 0 ) )
                    throw new Error( "stripLength must not be null nor 0 for " + getGeometryTypeHint() );
                
                break;
            
            case INDEXED_TRIANGLE_ARRAY:
                if ( numIndices() == 0 )
                    throw new Error( "vertices must not be null nor 0 for " + getGeometryTypeHint() );
                
                break;
            
            case TRIANGLE_STRIP_ARRAY:
                if ( ( getStripLengths() == null ) || ( getStripLengths().length == 0 ) )
                    throw new Error( "stripLength must not be null nor 0 for " + getGeometryTypeHint() );
                
                break;
            
            case TRIANGLE_ARRAY:
                break;
        }
    }
    
    private GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord2f[] texCoords2f, TexCoord3f[] texCoords3f, Colorf[] colors, int[] indices, int[] stripLengths )
    {
        this.geometryTypeHint = geometryTypeHint;
        this.coords = coords;
        this.normals = normals;
        this.texCoords2f = texCoords2f;
        this.texCoords3f = texCoords3f;
        this.colors = colors;
        this.indices = indices;
        this.stripLengths = stripLengths;
        
        check();
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord2f[] texCoords2f, Colorf[] colors, int[] indices, int[] stripLengths )
    {
        this( geometryTypeHint, coords, normals, texCoords2f, null, colors, indices, stripLengths );
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord3f[] texCoords3f, Colorf[] colors, int[] indices, int[] stripLengths )
    {
        this( geometryTypeHint, coords, normals, null, texCoords3f, colors, indices, stripLengths );
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord2f[] texCoords2f, Colorf[] colors )
    {
        this( geometryTypeHint, coords, normals, texCoords2f, null, colors, (int[])null, (int[])null );
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord3f[] texCoords3f, Colorf[] colors )
    {
        this( geometryTypeHint, coords, normals, null, texCoords3f, colors, (int[])null, (int[])null );
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord2f[] texCoords2f, int[] indices, int[] stripLengths )
    {
        this( geometryTypeHint, coords, normals, texCoords2f, null, null, indices, stripLengths );
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord3f[] texCoords3f, int[] indices, int[] stripLengths )
    {
        this( geometryTypeHint, coords, normals, null, texCoords3f, null, indices, stripLengths );
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord2f[] texCoords2f )
    {
        this( geometryTypeHint, coords, normals, texCoords2f, null, null, (int[])null, (int[])null );
    }
    
    public GeometryConstruct( GeometryType geometryTypeHint, Tuple3f[] coords, Vector3f[] normals, TexCoord3f[] texCoords3f )
    {
        this( geometryTypeHint, coords, normals, null, texCoords3f, null, (int[])null, (int[])null );
    }
}
