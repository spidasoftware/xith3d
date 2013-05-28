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
package org.xith3d.render.config;

/**
 * The OpenGLLayer is an abstract selection mechanism for the underlying
 * rendering engine.<br>
 * It is not designed as an enum to be extensible for other engines
 * not directly integrated into Xith3D's core.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OpenGLLayer implements Comparable< OpenGLLayer >
{
    protected static int next_ordinal = 0;
    
    private static OpenGLLayer[] array = new OpenGLLayer[ 5 ];
    private static OpenGLLayer[] array2 = new OpenGLLayer[ 5 ];
    
    public static final OpenGLLayer JOGL_AWT = new OpenGLLayer( "JOGL_AWT", true, "org.xith3d.render.jsr231.DisplayModeSelectorAWTImpl", "org.xith3d.render.jsr231.CanvasPeerImplAWT" );
    public static final OpenGLLayer JOGL_SWING = new OpenGLLayer( "JOGL_SWING", false, "org.xith3d.render.jsr231.DisplayModeSelectorAWTImpl", "org.xith3d.render.jsr231.CanvasPeerImplSwing" );
    public static final OpenGLLayer JOGL_SWT = new OpenGLLayer( "JOGL_SWT", false, "org.xith3d.render.jsr231.DisplayModeSelectorAWTImpl", "org.xith3d.render.jsr231.CanvasPeerImplSWT" );
    public static final OpenGLLayer LWJGL = new OpenGLLayer( "LWJGL", true, "org.xith3d.render.lwjgl.DisplayModeSelectorNativeImpl", "org.xith3d.render.lwjgl.CanvasPeerImplNative" );
    public static final OpenGLLayer LWJGL_AWT = new OpenGLLayer( "LWJGL_AWT", false, "org.xith3d.render.lwjgl.DisplayModeSelectorAWTImpl", "org.xith3d.render.lwjgl.CanvasPeerImplAWT" );
    
    private final String name;
    private final int ordinal;
    private final boolean isStandaloneCapable;
    private final String displayModeSelectorClassName;
    private final String canvasPeerImplClassName;
    
    /**
     * @return the OpenGLLayer's name.
     */
    public final String name()
    {
        return ( name );
    }
    
    /**
     * @return an ordinal index unique in the list of OpenGLLayers.
     */
    public final int ordinal()
    {
        return ( ordinal );
    }
    
    /**
     * @return whether this {@link OpenGLLayer} is capable of creating a standalone window (no owner).
     */
    public final boolean isStandaloneCapable()
    {
        return ( isStandaloneCapable );
    }
    
    /**
     * @return the name of the Class-object for the corresponding DisplayModeSelector.
     */
    public final String getDisplayModeSelectorClassName()
    {
        return ( displayModeSelectorClassName );
    }
    
    /**
     * @return the name of the Class-object for the corresponding CanvasPeerImpl.
     */
    public final String getCanvasPeerImplClassName()
    {
        return ( canvasPeerImplClassName );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( name );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        return ( ordinal() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        return ( ( o instanceof OpenGLLayer ) && ( ( (OpenGLLayer)o ).ordinal() == this.ordinal() ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( OpenGLLayer o )
    {
        // TODO: Check, if a String-comapre of the names is expected for an enum!
        
        if ( this.ordinal() > o.ordinal() )
            return ( 1 );
        
        if ( this.ordinal() < o.ordinal() )
            return ( -1 );
        
        return ( 0 );
    }
    
    /**
     * @return an array of all fields of this syntetic enum.
     */
    public static OpenGLLayer[] values()
    {
        if ( array2.length != array.length )
            array2 = new OpenGLLayer[ array.length ];
        
        System.arraycopy( array, 0, array2, 0, array.length );
        
        return ( array2 );
    }
    
    /**
     * @param name
     * 
     * @return the OpenGLLayer corresponding to the given name.
     */
    public static OpenGLLayer valueOf( String name )
    {
        for ( int i = 0; i < array.length; i++ )
        {
            if ( array[ i ].name.equals( name ) )
                return ( array[ i ] );
        }
        
        throw new IllegalArgumentException( "Unknown OpenGLLayer" );
    }
    
    /**
     * @return the default render engine to use
     * 
     * This can be specified via the system property "org.xith3d.defaultRenderer".
     * If not specified this is JOGL_AWT.
     */
    public static OpenGLLayer getDefault()
    {
        String defaultRenderer = System.getProperty( "org.xith3d.defaultRenderer", "jogl" );
        defaultRenderer = defaultRenderer.trim().toLowerCase().replaceAll( "[-_]", "" );
        if ( defaultRenderer.equals( "joglswing" ) )
            return ( JOGL_SWING );
        else if ( defaultRenderer.equals( "joglswt" ) )
            return ( JOGL_SWING );
        else if ( defaultRenderer.equals( "lwjgl" ) )
            return ( LWJGL );
        else if ( defaultRenderer.equals( "lwjglawt" ) )
            return ( LWJGL_AWT );
        return ( JOGL_AWT );
    }
    
    /**
     * @return true, if this OpenGLLayer is one of the JOGL ones
     */
    public boolean isJOGL()
    {
        return ( ( this == JOGL_AWT ) || ( this == JOGL_SWING ) || ( this == JOGL_SWT ) );
    }
    
    /**
     * @return true, if this OpenGLLayer is the LWJGL one
     */
    public boolean isLWJGL()
    {
        return ( ( this == LWJGL ) || ( this == LWJGL_AWT ) );
    }
    
    /**
     * @return true, if this OpenGLLayer AWT based
     */
    public boolean isAWT()
    {
        return ( ( this == JOGL_AWT ) || ( this == LWJGL_AWT ) );
    }
    
    /**
     * @return true, if this OpenGLLayer AWT or Swing based
     */
    public boolean isAWTorSwing()
    {
        return ( ( this == JOGL_AWT ) || ( this == JOGL_SWING ) || ( this == LWJGL_AWT ) );
    }
    
    public OpenGLLayer( String name, boolean isStandaloneCapable, String displayModeSelectorClassName, String canvasPeerImplClassName )
    {
        this.name = name;
        this.isStandaloneCapable = isStandaloneCapable;
        this.ordinal = next_ordinal++;
        this.displayModeSelectorClassName = displayModeSelectorClassName;
        this.canvasPeerImplClassName = canvasPeerImplClassName;
        
        if ( array.length <= this.ordinal )
        {
            OpenGLLayer[] newArray = new OpenGLLayer[ this.ordinal + 1 ];
            System.arraycopy( array, 0, newArray, 0, array.length );
            array = newArray;
        }
        
        array[ this.ordinal ] = this;
    }
}
