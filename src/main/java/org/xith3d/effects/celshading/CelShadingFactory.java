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
package org.xith3d.effects.celshading;

import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.openmali.vecmath2.Colorf;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.LineAttributes;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Texture2D;

/**
 * Insert type comment here.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class CelShadingFactory
{
    private static Texture2D texture = null;
    
    private static final PolygonAttributes outlinePolyAttribs;
    private static final RenderingAttributes outlineRenderingAttribs;
    private static final ColoringAttributes outlineColoringAttribs;
    private static final LineAttributes outlineLineAttribs;
    static
    {
        outlinePolyAttribs = new PolygonAttributes( DrawMode.LINE, FaceCullMode.FRONT );
        outlineRenderingAttribs = new RenderingAttributes();
        outlineRenderingAttribs.setDepthTestFunction( RenderingAttributes.LESS_OR_EQUAL );
        outlineColoringAttribs = new ColoringAttributes( Colorf.BLACK, ColoringAttributes.NICEST );
        outlineLineAttribs = new LineAttributes( 3, LineAttributes.PATTERN_SOLID, false );
    }
    
    public static final Texture2D getTexture()
    {
        if ( texture == null )
            texture = TextureLoader.getInstance().loadTexture( CelShadingFactory.class.getClassLoader().getResource( "resources/org/xith3d/shaders/celshading/celshading.png" ) );
        
        return ( texture );
    }
    
    public static final void setOutlineLineWidth( float width )
    {
        outlineLineAttribs.setLineWidth( Math.max( 1f, width ) );
    }
    
    public static final float getOutlineLineWidth()
    {
        return ( outlineLineAttribs.getLineWidth() );
    }
    
    public static final Shape3D createOutlineShape( Geometry geometry )
    {
        Appearance appearance = new Appearance();
        appearance.setPolygonAttributes( outlinePolyAttribs );
        appearance.setRenderingAttributes( outlineRenderingAttribs );
        appearance.setColoringAttributes( outlineColoringAttribs );
        appearance.setLineAttributes( outlineLineAttribs );
        
        return ( new Shape3D( geometry, appearance ) );
    }
    
    protected Appearance getBaseAppearance()
    {
        Appearance app = new Appearance();
        
        app.setTexture( getTexture() );
        
        return ( app );
    }
    
    protected abstract Shape3D createMainShape( Geometry geometry );
    
    public final void prepareForCelShading( Geometry geometry, GroupNode group )
    {
        group.addChild( createMainShape( geometry ) );
        group.addChild( createOutlineShape( geometry ) );
    }
}
