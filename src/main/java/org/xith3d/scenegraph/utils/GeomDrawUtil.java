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
package org.xith3d.scenegraph.utils;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.TexCoord1f;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.TexCoord3f;
import org.openmali.vecmath2.TexCoord4f;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Geometry;

/**
 * The {@link GeomDrawUtil} is a utility class, that allows for
 * drawing a geometry, i.e. creating a geometry through step-by-step
 * drawig instructions.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GeomDrawUtil
{
    private Geometry geom;
    
    /**
     * The following are used for building a mask of what you are planning on
     * changing during the drawing cycle.
     */
    public static final int CHANGE_COORDINATES = 1;
    public static final int CHANGE_NORMALS = 2;
    public static final int CHANGE_COLORS = 4;
    public static final int CHANGE_TEXCOORDS = 8;
    
    //private int mask;
    
    private int currIndex = -1;
    
    public void setGeom( Geometry geom )
    {
        if ( geom == null )
            throw new IllegalArgumentException( "geom must not be null." );
        
        this.geom = geom;
    }
    
    public GeomDrawUtil( Geometry geom )
    {
        this.setGeom( geom );
    }
    
    /**
     * Resets the vertices back to zero and sets the types of changes that will
     * be made during the drawing cycle. In some implementations the data will
     * be destroyed in any buffer referenced in the mask.
     * 
     * @param mask The bitmask of things which can change.
     */
    public void drawStart( int mask )
    {
        geom.setBoundsDirty();
        //this.mask = mask;
        geom.setValidVertexCount( 0 );
        currIndex = -1;
        
        if ( ( mask & CHANGE_COORDINATES ) != 0 )
        {
            if ( geom.getCoordinatesData() != null )
            {
                geom.getCoordinatesData().start();
            }
        }
        
        if ( ( mask & CHANGE_NORMALS ) != 0 )
        {
            if ( geom.getNormalsData() != null )
            {
                geom.getNormalsData().start();
            }
        }
        
        if ( ( mask & CHANGE_COLORS ) != 0 )
        {
            if ( geom.getColorData() != null )
            {
                geom.getColorData().start();
            }
        }
        
        if ( ( mask & CHANGE_TEXCOORDS ) != 0 )
        {
            final int[] tuSetMap = geom.getTexCoordSetMap();
            for ( int i = 0; i < tuSetMap.length; i++ )
            {
                if ( geom.getTexCoordsData( tuSetMap[ i ] ) != null )
                {
                    geom.getTexCoordsData( tuSetMap[ i ] ).start();
                }
            }
        }
    }
    
    /**
     * Resets the vertices back to zero and sets the types of changes that will
     * be made during the drawing cycle. This assumes all the data is going to
     * change. Use the drawStart(mask) version if you will only be changing some
     * of the data.
     */
    public void drawStart()
    {
        drawStart( CHANGE_COORDINATES | CHANGE_NORMALS | CHANGE_COLORS | CHANGE_TEXCOORDS );
    }
    
    /**
     * Starts a new vertex to be updated. This must be called before setting the
     * vertex information.
     * 
     * @return The index of the new vertex.
     */
    public int newVertex()
    {
        final int v = geom.getValidVertexCount();
        geom.setValidVertexCount( v + 1 );
        
        currIndex = v;
        
        return ( v );
    }
    
    /**
     * Sets the current coordinate's value. Only call this once per vertex since
     * some implementations will auto increment the pointers.
     * 
     * @param p
     */
    public void setCoordinate( Tuple3f p )
    {
        geom.setCoordinate( currIndex, p );
        geom.setBoundsDirty();
    }
    
    public void setCoordinate( float x, float y, float z )
    {
        geom.setCoordinate( currIndex, x, y, z );
        geom.setBoundsDirty();
    }
    
    public void setNormal( float x, float y, float z )
    {
        geom.setNormal( currIndex, x, y, z );
    }
    
    public void setNormal( Vector3f normal )
    {
        geom.setNormal( currIndex, normal );
    }
    
    public void setColor( Colorf color )
    {
        if ( color.hasAlpha() )
            geom.setColor( currIndex, color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() );
        else
            geom.setColor( currIndex, color.getRed(), color.getGreen(), color.getBlue() );
    }
    
    public void setColor( float r, float g, float b )
    {
        geom.setColor( currIndex, r, g, b );
    }
    
    public void setColor( float r, float g, float b, float a )
    {
        geom.setColor( currIndex, r, g, b, a );
    }
    
    public void setColor( byte r, byte g, byte b )
    {
        geom.setColor( currIndex, r, g, b );
    }
    
    public void setColor( byte r, byte g, byte b, byte a )
    {
        geom.setColor( currIndex, r, g, b, a );
    }
    
    public void setTexCoord( int unit, TexCoord1f t )
    {
        geom.setTextureCoordinate( unit, currIndex, t.getS() );
    }
    
    public void setTexCoord( int unit, TexCoord2f t )
    {
        geom.setTextureCoordinate( unit, currIndex, t.getS(), t.getT() );
    }
    
    public void setTexCoord( int unit, TexCoord3f t )
    {
        geom.setTextureCoordinate( unit, currIndex, t.getS(), t.getT(), t.getP() );
    }
    
    public void setTexCoord( int unit, TexCoord4f t )
    {
        geom.setTextureCoordinate( unit, currIndex, t.getS(), t.getT(), t.getP(), t.getQ() );
    }
    
    public void setTexCoord( int unit, float s )
    {
        geom.setTextureCoordinate( unit, currIndex, s );
    }
    
    public void setTexCoord( int unit, float s, float t )
    {
        geom.setTextureCoordinate( unit, currIndex, s, t );
    }
    
    public void setTexCoord( int unit, float s, float t, float p )
    {
        geom.setTextureCoordinate( unit, currIndex, s, t, p );
    }
    
    public void setTexCoord( int unit, float s, float t, float p, float q )
    {
        geom.setTextureCoordinate( unit, currIndex, s, t, p, q );
    }
    
    /**
     * Ends the drawing cycle and commits the geometry changes to the 3d card.
     * The number of vertices will be set.
     */
    public void drawEnd()
    {
        /*
        if (!(geom instanceof GeometryStripArray)) {
        
        if (reversed) { if ((mask & CHANGE_COLORS) == CHANGE_COLORS) { if
        (colors != null) { geom.setInitialColorIndex(0); } }
        
        if ((mask & CHANGE_NORMALS) == CHANGE_NORMALS) { if (normals != null) {
        geom.setInitialNormalIndex(0); } }
        
        if ((mask & CHANGE_COORDS) == CHANGE_COORDS) { if (coords != null) {
        geom.setInitialCoordIndex(0); } }
        
        if ((mask & CHANGE_TEXCOORDS) == CHANGE_TEXCOORDS) { if (texcoords_1 !=
        null) { geom.setInitialTexCoordIndex(0, 0); }
        
        if (texcoords_2 != null) { geom.setInitialTexCoordIndex(1, 0); } }
        
        geom.setValidVertexCount(numVertices);
        
        if ((mask & CHANGE_COLORS) == CHANGE_COLORS) { if (colors != null) {
        geom.setInitialColorIndex(colors.getInitialIndex() / colorSize); } }
        
        if ((mask & CHANGE_NORMALS) == CHANGE_NORMALS) { if (normals != null) {
        geom.setInitialNormalIndex(normals.getInitialIndex() / 3); } }
        
        if ((mask & CHANGE_COORDS) == CHANGE_COORDS) { if (coords != null) { //
        Log.print("Num vertices are "+numVertices+" and initial index is
        "+coords.getInitialIndex());
        geom.setInitialCoordIndex(coords.getInitialIndex() / 3); } }
        
        if ((mask & CHANGE_TEXCOORDS) == CHANGE_TEXCOORDS) { if (texcoords_1 !=
        null) { geom.setInitialTexCoordIndex(0, texcoords_1.getInitialIndex() /
        2); }
        
        if (texcoords_2 != null) { geom.setInitialTexCoordIndex(1,
        texcoords_2.getInitialIndex() / 2); } } } else {
        geom.setValidVertexCount(numVertices); } }
        */
    }
}
