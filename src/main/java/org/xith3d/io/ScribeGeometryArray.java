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
package org.xith3d.io;

import java.io.IOException;

import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.TriangleArray;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.utility.logging.X3DLog;

/**
 * Writes a GeometryArray out to a stream.
 * 
 * @author David Yazel
 */
public class ScribeGeometryArray extends Scribe
{
    private static final int VERSION = 1;
    private static final int TYPE_TRIANGLE_ARRAY = 0;
    private static final int TYPE_TRIANGLE_STRIP_ARRAY = 1;
    
    public ScribeGeometryArray()
    {
    }
    
    /**
     * Writes out the geometry array out to the stream
     */
    public static void writeGeometryArray( ScribeOutputStream out, Geometry geom ) throws IOException
    {
        out.writeByte( Scribe.SCRIBE_GEOMETRY_ARRAY ); // for compatibility
        
        int geomType;
        
        if ( geom instanceof TriangleArray )
        {
            geomType = TYPE_TRIANGLE_ARRAY;
        }
        else if ( geom instanceof TriangleStripArray )
        {
            geomType = TYPE_TRIANGLE_STRIP_ARRAY;
        }
        else
        {
            throw new Error( "Cannot scribe geometry array type " + geom.getClass().getName() );
        }
        
        // get the format and texture unit information
        final int format = geom.getVertexFormat();
        final int numUnits = geom.getNumTextureUnits();
        final int[] unitMap = new int[ numUnits ];
        final int vertexCount = geom.getVertexCount();
        geom.getTexCoordSetMap( unitMap );
        
        // write out the header information
        out.writeInt( VERSION );
        out.writeInt( geomType );
        out.writeInt( format );
        out.writeInt( vertexCount );
        out.writeInt( numUnits );
        out.writeIntArray( unitMap );
        
        //if ( geom.hasCoordinates() )
        {
            X3DLog.debug( "  Writing out coordinates" );
            
            float[] data = geom.getCoordinatesData().getData();
            out.writeFloatArray( data );
        }
        
        if ( geom.hasNormals() )
        {
            X3DLog.debug( "  Writing out normals" );
            
            float[] data = geom.getNormalsData().getData();
            out.writeFloatArray( data );
        }
        
        if ( geom.hasColors() )
        {
            X3DLog.debug( "  Writing out color3" );
            
            float[] data = geom.getColorData().getData();
            out.writeFloatArray( data );
        }
        
        if ( geom.hasTextureCoordinates() ) 
        {
            for ( int j = 0; j < numUnits; j++ )
            {
                float[] data = geom.getTexCoordsData( unitMap[ j ] ).getData();
                out.writeFloatArray( data );
                
                X3DLog.debug( "  Writing data of length ", data.length );
                
                X3DLog.debug( "  Writing out tex coords ", j );
                
                if ( unitMap[ j ] == 1 )
                {
                    float maxu = 0;
                    
                    for ( int k = 0; k < data.length; k++ )
                    {
                        if ( data[ k ] > maxu )
                        {
                            maxu = data[k];
                        }
                    }
                    
                    X3DLog.debug( "  Max U on tex 2 is ", maxu );
                }
            }
        }
    }
    
    /**
     * read the geometry array from the stream
     */
    public static Geometry readGeometryArray( ScribeInputStream in ) throws IOException
    {
        in.readByte(); // for compatibility;
        
        int geomType = in.readInt();
        int format = in.readInt();
        int vertexCount = in.readInt();
        int numUnits = in.readInt();
        int[] unitMap = new int[ numUnits ];
        in.readIntArray( unitMap );
        Geometry geom = null;
        
        switch ( geomType )
        {
            case TYPE_TRIANGLE_ARRAY:
                geom = new TriangleArray( vertexCount/*, format*/ );
                
                break;
        }
        
        if ( (Geometry.COORDINATES & format) != 0 )
        {
            float[] data = in.readFloatArray();
            geom.setCoordinates( 0, data );
        }
        
        if ( (Geometry.NORMALS & format) != 0 )
        {
            float[] data = in.readFloatArray();
            geom.setNormals( 0, data );
        }
        
        // TODO: handle alpha information!
        
        if ( (Geometry.COLORS & format) != 0 )
        {
            float[] data = in.readFloatArray();
            geom.setColors( 0, 3, data );
        }
        
        if ( (Geometry.COLORS & format) != 0 )
        {
            float[] data = in.readFloatArray();
            geom.setColors( 0, 4, data );
        }
        
        if ( (Geometry.TEXTURE_COORDINATES & format) != 0 ) // assume tex-coords-2
        {
            X3DLog.debug( "  Reading in ", numUnits, " sets of tex coords" );
            
            for ( int j = 0; j < numUnits; j++ )
            {
                float[] data = in.readFloatArray();
                
                X3DLog.debug( "  Read data of length ", data.length );
                
                geom.setTextureCoordinates( j, 0, 2, data );
                
                float maxu = 0;
                
                for ( int k = 0; k < data.length; k++ )
                {
                    if ( data[ k ] > maxu )
                    {
                        maxu = data[ k ];
                    }
                }
                
                X3DLog.debug( "  Max U on tex ", j, " is ", maxu );
            }
        }
        
        return ( geom );
    }
}
