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
package org.xith3d.utility.geometry;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.LineArray;
import org.xith3d.scenegraph.Shape3D;

/**
 * This class visualizes the normals of a Shape3D's Geometry.<br>
 * It must be placed into the same TransformGroup as the opbject Shape3D.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class NormalsVisualizer extends Shape3D
{
    public static final Colorf DEFAULT_NORMAL_COLOR = Colorf.GREEN;
    
    private Shape3D object;
    private Geometry objectGeom;
    private Point3f[] vertices = null;
    private Vector3f[] normals = null;
    private Point3f[] coords = null;
    private LineArray geometry = null;
    private float normalScale = 1.0f;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public LineArray getGeometry()
    {
        return ( (LineArray)super.getGeometry() );
    }
    
    /**
     * Sets the scale factor for the visualized normals.
     * 
     * @param scale
     */
    public void setNormalScale( float scale )
    {
        this.normalScale = scale;
        
        if ( coords != null )
            update();
    }
    
    /**
     * @return the scale factor for the visualized normals.
     */
    public float getNormalScale()
    {
        return ( normalScale );
    }
    
    /**
     * Updates the normals lines in this Shape's LineArray.
     * (synchronizes them with the object Shape3D)
     */
    public void update()
    {
        if ( ( geometry == null ) || ( object.getGeometry().getVertexCount() != vertices.length ) )
        {
            vertices = new Point3f[ object.getGeometry().getVertexCount() ];
            normals = new Vector3f[ object.getGeometry().getVertexCount() ];
            coords = new Point3f[ vertices.length * 2 ];
            
            for ( int i = 0; i < vertices.length; i++ )
            {
                vertices[ i ] = new Point3f();
                normals[ i ] = new Vector3f();
                coords[ i * 2 + 0 ] = new Point3f();
                coords[ i * 2 + 1 ] = new Point3f();
            }
            
            geometry = new LineArray( coords.length );
        }
        
        objectGeom.getCoordinates( 0, vertices );
        objectGeom.getNormals( 0, normals );
        
        for ( int i = 0; i < vertices.length; i++ )
        {
            coords[ i * 2 + 0 ].set( vertices[ i ] );
            
            coords[ i * 2 + 1 ].set( vertices[ i ] );
            //coords[ i * 2 + 1 ].add( normals[ i ] );
            coords[ i * 2 + 1 ].addX( normals[ i ].getX() * getNormalScale() );
            coords[ i * 2 + 1 ].addY( normals[ i ].getY() * getNormalScale() );
            coords[ i * 2 + 1 ].addZ( normals[ i ].getZ() * getNormalScale() );
        }
        
        geometry.setCoordinates( 0, coords );
        
        this.setGeometry( geometry );
    }
    
    /**
     * Sets the color for the normals lines.
     * 
     * @param color
     */
    public void setColor( Colorf color )
    {
        getAppearance( true ).getColoringAttributes( true ).setColor( color );
    }
    
    /**
     * Create a new NormalsVisualizer for the given object Shape3D.
     * Directly calls {@link #update()}.
     * 
     * @param object the object Shape3D
     * @param color
     * @param normalScale
     */
    public NormalsVisualizer( Shape3D object, Colorf color, float normalScale )
    {
        super();
        
        this.object = object;
        this.objectGeom = object.getGeometry();
        
        if ( ( objectGeom.getVertexFormat() & Geometry.NORMALS ) == 0 )
            throw new Error( "The given Shape3D's Geometry doesn't have normals." );
        
        update();
        
        getAppearance( true ).getLineAttributes( true ).setLineAntialiasingEnabled( false );
        getAppearance( true ).getLineAttributes( true ).setLineWidth( 1.0f );
        
        setColor( color );
        setNormalScale( normalScale );
    }
    
    /**
     * Create a new NormalsVisualizer for the given object Shape3D.
     * Directly calls {@link #update()}.
     * 
     * @param object the object Shape3D
     * @param color
     */
    public NormalsVisualizer( Shape3D object, Colorf color )
    {
        this( object, color, 1.0f );
    }
    
    /**
     * Create a new NormalsVisualizer for the given object Shape3D.
     * Directly calls {@link #update()}.
     * 
     * @param object the object Shape3D
     * @param normalScale
     */
    public NormalsVisualizer( Shape3D object, float normalScale )
    {
        this( object, DEFAULT_NORMAL_COLOR, normalScale );
    }
    
    /**
     * Create a new NormalsVisualizer for the given object Shape3D.
     * Directly calls {@link #update()}.
     * 
     * @param object the object Shape3D
     */
    public NormalsVisualizer( Shape3D object )
    {
        this( object, DEFAULT_NORMAL_COLOR );
    }
}
