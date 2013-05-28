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

import org.jagatoo.opengl.enums.ColorTarget;
import org.openmali.vecmath2.Colorf;

import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * The Material object defines the appearance of an object under illumination.
 * If the Material object in an Appearance object is null, lighting is disabled
 * for all nodes that use that Appearance object.
 * 
 * The properties that can be set for a Material object are:
 * <ul><li>Ambient color - the ambient RGB color reflected off the surface
 * of the material.
 * The range of values is 0f to 1f. The default ambient color is (.2f, .2f, .2f).</li>
 * <li>Diffuse color - the RGB color of the material when illuminated.
 * The range of values is 0f to 1f. The default diffuse color is (1f, 1f, 1f).</li>
 * <li>Specular color - the RGB specular color of the material (highlights).
 * The range of values is 0f to 1f. The default specular color is (1f, 1f, 1f).</li>
 * <li>Emissive color - the RGB color of the light the material emits, if any.
 * The range of values is 0f to 1f. The default emissive color is (0f, 0f, 0f).</li>
 * <li>Shininess - the material's shininess, in the range [1f, 128f]
 * with 1f being not shiny and 128f being very shiny. Values outside this range are
 * clamped. The default value for the material's shininess is 64f.</li></ul>
 *
 * The Material object also enables or disables lighting.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class Material extends NodeComponent implements StateTrackable< Material >
{
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * The desired ambient color.
     */
    private final Colorf ambient = new Colorf( 0.2f, 0.2f, 0.2f );
    
    /**
     * The desired emissive color.
     */
    private final Colorf emissive = new Colorf( 0f, 0f, 0f );
    
    /**
     * The desired diffuse color.
     */
    private final Colorf diffuse = new Colorf( 1f, 1f, 1f );
    
    /**
     * The desired specular color.
     */
    private final Colorf specular = new Colorf( 1f, 1f, 1f );
    
    /**
     * The desired shininess.
     */
    private float shininess = 64f;
    
    /**
     * Is lighting enabled.
     */
    private boolean lightingEnabled = true;
    
    /**
     * Is normalizing normals enabled.
     */
    private boolean normalizeNormals = true;
    
    /**
     * @see ColorTarget#NONE
     */
    public static final ColorTarget NONE = ColorTarget.NONE;
    
    /**
     * @see ColorTarget#AMBIENT
     */
    public static final ColorTarget AMBIENT = ColorTarget.AMBIENT;
    
    /**
     * @see ColorTarget#EMISSIVE
     */
    public static final ColorTarget EMISSIVE = ColorTarget.EMISSIVE;
    
    /**
     * @see ColorTarget#DIFFUSE
     */
    public static final ColorTarget DIFFUSE = ColorTarget.DIFFUSE;
    
    /**
     * @see ColorTarget#SPECULAR
     */
    public static final ColorTarget SPECULAR = ColorTarget.SPECULAR;
    
    /**
     * @see ColorTarget#AMBIENT_AND_DIFFUSE
     */
    public static final ColorTarget AMBIENT_AND_DIFFUSE = ColorTarget.AMBIENT_AND_DIFFUSE;
    
    private ColorTarget colorTarget = ColorTarget.NONE;
    
    /**
     * Sets the ambient color value.
     */
    public final void setAmbientColor( Colorf color )
    {
        this.ambient.set( color );
        
        setChanged( true );
    }
    
    /**
     * Sets the ambient color value.
     * 
     * @param r
     * @param g
     * @param b
     */
    public final void setAmbientColor( float r, float g, float b )
    {
        this.ambient.set( r, g, b );
        
        setChanged( true );
    }
    
    public final void getAmbientColor( Colorf c )
    {
        c.set( ambient );
    }
    
    /**
     * @return the ambient color value.
     */
    public final Colorf getAmbientColor()
    {
        return ( ambient.getReadOnly() );
    }
    
    /**
     * Sets the emissive color value.
     * 
     * @param r
     * @param g
     * @param b
     */
    public final void setEmissiveColor( Colorf color )
    {
        this.emissive.set( color );
        
        setChanged( true );
    }
    
    public final void setEmissiveColor( float r, float g, float b )
    {
        this.emissive.set( r, g, b );
        setChanged( true );
    }
    
    public final void getEmissiveColor( Colorf c )
    {
        c.set( this.emissive );
    }
    
    /**
     * @return the emissive color value.
     */
    public final Colorf getEmissiveColor()
    {
        return ( emissive.getReadOnly() );
    }
    
    /**
     * Sets the diffuse color value.
     */
    public final void setDiffuseColor( Colorf color )
    {
        this.diffuse.set( color );
        
        setChanged( true );
    }
    
    /**
     * Sets the diffuse color value.
     * 
     * @param r
     * @param g
     * @param b
     */
    public final void setDiffuseColor( float r, float g, float b )
    {
        this.diffuse.set( r, g, b );
        
        setChanged( true );
    }
    
    public final void getDiffuseColor( Colorf c )
    {
        c.set( diffuse );
    }
    
    /**
     * @return the diffuse color value.
     */
    public final Colorf getDiffuseColor()
    {
        return ( diffuse.getReadOnly() );
    }
    
    /**
     * Sets the specular color value.
     */
    public final void setSpecularColor( Colorf color )
    {
        this.specular.set( color );
        
        setChanged( true );
    }
    
    /**
     * Sets the specular color value.
     * 
     * @param r
     * @param g
     * @param b
     */
    public final void setSpecularColor( float r, float g, float b )
    {
        this.specular.set( r, g, b );
        
        setChanged( true );
    }
    
    public final void getSpecularColor( Colorf c )
    {
        c.set( this.specular );
    }
    
    /**
     * @return the specular color value.
     */
    public final Colorf getSpecularColor()
    {
        return ( specular.getReadOnly() );
    }
    
    /**
     * Sets the shininess value.<br />
     * This value must be in the interval [0, 128].
     * 
     * @param shininess
     */
    public final void setShininess( float shininess )
    {
        shininess = Math.max( 0f, Math.min( shininess, 128f ) );
        
        this.shininess = shininess;
        
        setChanged( true );
    }
    
    /**
     * @return the shininess value.
     */
    public final float getShininess()
    {
        return ( shininess );
    }
    
    /**
     * Sets the color target for per-vertex colors. When lighting is enabled and
     * per-vertex colors are present (and not ignored) in the geometry for a
     * given Shape3D node, those per-vertex colors are used in place of the
     * specified material color(s) for this Material object.
     * 
     * If no per-vertex colors are present in geometry, color from
     * ColoringAttributes used in place of per-vertex colors and will substitute
     * the material color specified by color target.
     * 
     * The color target is ignored when lighting is disabled.
     * 
     * The default target is NONE, which causes only colors from this Material
     * to be used for lighting calculations.
     * 
     * @param colorTarget
     */
    public final void setColorTarget( ColorTarget colorTarget )
    {
        this.colorTarget = colorTarget;
        setChanged( true );
    }
    
    /**
     * @return the current ColorTarget for this material.
     */
    public final ColorTarget getColorTarget()
    {
        return ( colorTarget );
    }
    
    /**
     * Sets flag indicating the normal vecrots should be normalized after
     * transformations applied.
     * 
     * Normals specified in the shape geomerty need not have unit length. If
     * normalization is enabled, then normals are normalized after
     * transformation. By default, normalization is enabled.
     * 
     * @param normalizeNormals New value for normal normalization flag
     */
    public final void setNormalizeNormals( boolean normalizeNormals )
    {
        this.normalizeNormals = normalizeNormals;
        setChanged( true );
    }
    
    /**
     * Retrieves the current value of normal normalization flag
     * 
     * @return Normal normalization flag currently set for this material
     */
    public final boolean getNormalizeNormals()
    {
        return ( normalizeNormals );
    }
    
    /**
     * Turns lighting on or off.
     */
    public final void setLightingEnabled( boolean enabled )
    {
        this.lightingEnabled = enabled;
        
        setChanged( true );
    }
    
    /**
     * @return Is lighting enabled or not.
     */
    public final boolean isLightingEnabled()
    {
        return ( lightingEnabled );
    }
    
    /**
     * Used by the render engine to set the state id for the node.
     * 
     * @param stateNode
     */
    public final void setStateNode( StateNode stateNode )
    {
        this.stateNode = stateNode;
        this.stateId = stateNode.getId();
    }
    
    /**
     * @return the id of the object. This should return -1 if there is no
     *         assigned id.
     */
    public final StateNode getStateNode()
    {
        return ( stateNode );
    }
    
    public final long getStateId()
    {
        return ( stateId );
    }
    
    /**
     * {@inheritDoc}
     */
    public Material getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        
        if ( !( o instanceof Material ) )
            return ( false );
        
        final Material om = (Material)o;
        
        if ( !ambient.equals( om.getAmbientColor() ) )
            return ( false );
        if ( !diffuse.equals( om.getDiffuseColor() ) )
            return ( false );
        if ( !emissive.equals( om.getEmissiveColor() ) )
            return ( false );
        if ( !specular.equals( om.getSpecularColor() ) )
            return ( false );
        if ( shininess != om.shininess )
            return ( false );
        if ( lightingEnabled != om.lightingEnabled )
            return ( false );
        if ( colorTarget != om.colorTarget )
            return ( false );
        if ( normalizeNormals != om.normalizeNormals )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( Material o )
    {
        if ( this == o )
            return ( 0 );
        
        int val;
        val = ComparatorHelper.compare( ambient, o.ambient );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( diffuse, o.diffuse );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( emissive, o.emissive );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compare( specular, o.specular );
        if ( val != 0 )
            return ( val );
        
        if ( shininess < o.shininess )
            return ( -1 );
        else if ( shininess > o.shininess )
            return ( 1 );
        
        int al = ( lightingEnabled ) ? 1 : 0;
        int bl = ( o.lightingEnabled ) ? 1 : 0;
        if ( al < bl )
            return ( -1 );
        else if ( al > bl )
            return ( 1 );
        
        if ( colorTarget.ordinal() < o.colorTarget.ordinal() )
            return ( -1 );
        else if ( colorTarget.ordinal() > o.colorTarget.ordinal() )
            return ( 1 );
        
        al = ( normalizeNormals ) ? 1 : 0;
        bl = ( o.normalizeNormals ) ? 1 : 0;
        if ( al < bl )
            return ( -1 );
        else if ( al > bl )
            return ( 1 );
        
        return ( 0 );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        Material o = (Material)original;
        if ( forceDuplicate )
        {
            o.getAmbientColor( ambient );
            o.getEmissiveColor( emissive );
            o.getDiffuseColor( diffuse );
            o.getSpecularColor( specular );
        }
        else
        {
            setAmbientColor( o.getAmbientColor() );
            setEmissiveColor( o.getEmissiveColor() );
            setDiffuseColor( o.getDiffuseColor() );
            setSpecularColor( o.getSpecularColor() );
        }
        
        setShininess( o.getShininess() );
        setLightingEnabled( o.isLightingEnabled() );
        setNormalizeNormals( o.getNormalizeNormals() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Material cloneNodeComponent( boolean forceDuplicate )
    {
        Material m = new Material();
        
        m.duplicateNodeComponent( this, forceDuplicate );
        
        return m;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( this.getClass().getName() + " \"" + this.getName() + "\"\n" +
        		"    ambient: " + this.ambient + "\n" +
                "    diffuse: " + this.diffuse + "\n" +
                "    emissive: " + this.emissive + "\n" +
                "    specular: " + this.specular + "\n" +
                "    shininess: " + this.shininess + "\n" +
                "    ColorTarget: " + this.colorTarget + "\n" +
                "    norm-normals: " + this.normalizeNormals + "\n" +
                "    lighting-enabled: " + this.lightingEnabled
              );
    }
    
    /**
     * Constructs a new Material object.
     */
    public Material()
    {
        super( false );
    }
    
    public Material( boolean lightEnabled )
    {
        this();
        
        this.lightingEnabled = lightEnabled;
    }
    
    public Material( boolean lightEnabled, float shininess )
    {
        this( lightEnabled );
        
        this.shininess = shininess;
    }
    
    public Material( float shininess )
    {
        this( true, shininess );
    }
    
    /**
     * Constructs a new Material object.
     */
    public Material( Colorf ambientColor, Colorf emissiveColor, Colorf diffuseColor, Colorf specularColor, float shininess )
    {
        this();
        
        if ( ambientColor != null )
            this.ambient.set( ambientColor );
        if ( emissiveColor != null )
            this.emissive.set( emissiveColor );
        if ( diffuseColor != null )
            this.diffuse.set( diffuseColor );
        if ( specularColor != null )
            this.specular.set( specularColor );
        
        this.shininess = shininess;
    }
    
    /**
     * Constructs a new Material object.
     */
    public Material( Colorf ambientColor, Colorf emissiveColor, Colorf diffuseColor, Colorf specularColor, float shininess, boolean lightingEnabled )
    {
        this( ambientColor, emissiveColor, diffuseColor, specularColor, shininess );
        
        this.lightingEnabled = lightingEnabled;
    }
    
    /**
     * Constructs a new Material object.
     */
    public Material( Colorf ambientColor, Colorf emissiveColor, Colorf diffuseColor, Colorf specularColor, float shininess, ColorTarget colorTarget, boolean lightingEnabled )
    {
        this( ambientColor, emissiveColor, diffuseColor, specularColor, shininess );
        
        this.colorTarget = colorTarget;
        this.lightingEnabled = lightingEnabled;
    }
    
    /**
     * Constructs a new Material object.
     */
    public Material( Colorf ambientColor, Colorf emissiveColor, Colorf diffuseColor, Colorf specularColor, float shininess, ColorTarget colorTarget, boolean normalizeNormals, boolean lightingEnabled )
    {
        this( ambientColor, emissiveColor, diffuseColor, specularColor, shininess );
        
        this.colorTarget = colorTarget;
        this.normalizeNormals = normalizeNormals;
        this.lightingEnabled = lightingEnabled;
    }
}
