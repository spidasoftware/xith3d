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
package org.xith3d.render;

import java.util.HashMap;
import java.util.Map;

/**
 * Storage class for configuring renderer options.
 * 
 * Efficiency of this class is important to overall rendering time. All set and
 * get opertaions are simple O(1) complexity operations which should not
 * increase rendering time by a tracable margin. Constructors (and loading of
 * default options or cloning RenderOptions using the loadOptions method) are
 * O(n) operations but should only rarely be used. Import/Export methods are
 * added for convenience and have no time constraints.
 * 
 * @author William Denniss
 * @author Marvin Froehlich (aka Qudus)
 */
public class RenderOptions
{
    /**
     * The GL-states-cache on|off flag.
     */
    private static final boolean DEFAULT_VALUE_GL_STATES_CACHE_ENABLED = true;
    
    /**
     * The use lights on|off flag.
     */
    public static final boolean DEFAULT_VALUE_LIGHTING_ENABLED = true;
    
    /**
     * Whether or not to use vertex buffer objects to store vertex data on the video card.
     */
    public static final boolean DEFAULT_VALUE_VBOS_ENABLED = true;
    
    /**
     * Whether or not to use display lists for vertex data drawing.
     */
    public static final boolean DEFAULT_VALUE_DISPLAY_LISTS_ENABLED = true;
    
    /**
     * Whether or not to render textures.
     */
    public static final boolean DEFAULT_VALUE_TEXTURE_MAPPING_ENABLED = true;
    
    /**
     * Whether or not to render in only wireframe (GL_LINE) mode.
     */
    public static final boolean DEFAULT_VALUE_WIREFRAME_MODE_ENABLED = false;
    
    /**
     * Whether the graphic card should be checked for errors on every call. if
     * there is an error then an exception will be thrown.
     */
    public static final boolean DEFAULT_VALUE_GL_ERROR_CHECKS_ENABLED = false;
    
    /**
     * Whether or not we should turn on the lowest level of graphics card
     * tracing. Very expensive.
     */
    public static final boolean DEFAULT_VALUE_GL_TRACING_ENABLED = false;
    
    
    /**
     * The GL-states-cache on|off flag.
     */
    private boolean glStatesCacheEnabled = DEFAULT_VALUE_GL_STATES_CACHE_ENABLED;
    
    /**
     * The use lights on|off flag.
     */
    private boolean lightingEnabled = DEFAULT_VALUE_LIGHTING_ENABLED;
    
    /**
     * Whether or not to use vertex buffer objects to store vertex data on the video card.
     */
    private boolean vbosEnabled = DEFAULT_VALUE_VBOS_ENABLED;
    
    /**
     * Whether or not to use display lists for vertex data drawing.
     */
    private boolean displayListsEnabled = DEFAULT_VALUE_DISPLAY_LISTS_ENABLED;
    
    /**
     * Whether or not to render textures.
     */
    private boolean textureMappingEnabled = DEFAULT_VALUE_TEXTURE_MAPPING_ENABLED;
    
    /**
     * Whether or not to render in only wireframe (GL_LINE) mode.
     */
    private boolean wireframeModeEnabled = DEFAULT_VALUE_WIREFRAME_MODE_ENABLED;
    
    /**
     * Whether the graphic card should be checked for errors on every call. if
     * there is an error then an exception will be thrown.
     */
    private boolean errorChecksEnabled = DEFAULT_VALUE_GL_ERROR_CHECKS_ENABLED;
    
    /**
     * Whether or not we should turn on the lowest level of graphics card
     * tracing. Very expensive.
     */
    private boolean tracingEnabled = DEFAULT_VALUE_GL_TRACING_ENABLED;
    
    
    /**
     * Sets the use GL-states-cache on|off flag.
     * 
     * @param enabled
     */
    public void setGLStatesCacheEnabled( boolean enabled )
    {
        this.glStatesCacheEnabled = enabled;
    }
    
    /**
     * @return the use GL-states-cache on|off flag.
     */
    public final boolean isGLStatesCacheEnabled()
    {
        return ( glStatesCacheEnabled );
    }
    
    
    /**
     * Sets the use lights on|off flag.
     * 
     * @param enabled
     */
    public void setLightingEnabled( boolean enabled )
    {
        this.lightingEnabled = enabled;
    }
    
    /**
     * @return the use lights on|off flag.
     */
    public final boolean isLightingEnabled()
    {
        return ( lightingEnabled );
    }
    
    /**
     * Sets whether or not to use vertex buffer objects to store vertex data on the video card.
     * 
     * @param enabled
     */
    public void setVBOsEnabled( boolean enabled )
    {
        this.vbosEnabled = enabled;
    }
    
    /**
     * @return whether or not to use vertex buffer objects to store vertex data on the video card.
     */
    public final boolean areVBOsEnabled()
    {
        return ( vbosEnabled );
    }
    
    /**
     * Sets whether or not to use display lists for vertex data drawing.
     * 
     * @param enabled
     */
    public void setDisplayListsEnabled( boolean enabled )
    {
        this.displayListsEnabled = enabled;
    }
    
    /**
     * @return whether or not to use display lists for vertex data drawing.
     */
    public final boolean areDisplayListsEnabled()
    {
        return ( displayListsEnabled );
    }
    
    /**
     * Sets whether or not to render textures.
     * 
     * @param enabled
     */
    public void setTextureMappingEnabled( boolean enabled )
    {
        this.textureMappingEnabled = enabled;
    }
    
    /**
     * @return whether or not to render textures.
     */
    public final boolean isTextureMappingEnabled()
    {
        return ( textureMappingEnabled && !wireframeModeEnabled );
    }
    
    /**
     * Sets whether or not to render in only wireframe (GL_LINE) mode.
     * 
     * @param enabled
     */
    public void setWireframeModeEnabled( boolean enabled )
    {
        this.wireframeModeEnabled = enabled;
    }
    
    /**
     * @return whether or not to render in only wireframe (GL_LINE) mode.
     */
    public final boolean isWireframeModeEnabled()
    {
        return ( wireframeModeEnabled );
    }
    
    /**
     * Switches wireframe mode.
     * 
     * @return the new state.
     */
    public boolean switchWireframeMode()
    {
        setWireframeModeEnabled( !isWireframeModeEnabled() );
        
        return ( isWireframeModeEnabled() );
    }
    
    /**
     * Sets whether the graphic card should be checked for errors on every call. if
     * there is an error then an exception will be thrown.
     * 
     * @param enabled
     */
    public void setGLErrorChecksEnabled( boolean enabled )
    {
        this.errorChecksEnabled = enabled;
    }
    
    /**
     * @return whether the graphic card should be checked for errors on every call. if
     * there is an error then an exception will be thrown.
     */
    public final boolean areGLErrorChecksEnabled()
    {
        return ( errorChecksEnabled );
    }
    
    /**
     * Sets whether or not we should turn on the lowest level of graphics card
     * tracing. Very expensive.
     * 
     * @param enabled
     */
    public void setGLTracingEnabled( boolean enabled )
    {
        this.tracingEnabled = enabled;
    }
    
    /**
     * @return whether or not we should turn on the lowest level of graphics card
     * tracing. Very expensive.
     */
    public final boolean isGLTracingEnabled()
    {
        return ( tracingEnabled );
    }
    
    
    /**
     * Loads the Xith3D default options.
     * 
     */
    public void loadDefaultOptions()
    {
        this.glStatesCacheEnabled = DEFAULT_VALUE_GL_STATES_CACHE_ENABLED;
        this.lightingEnabled = DEFAULT_VALUE_LIGHTING_ENABLED;
        this.vbosEnabled = DEFAULT_VALUE_VBOS_ENABLED;
        this.displayListsEnabled = DEFAULT_VALUE_DISPLAY_LISTS_ENABLED;
        this.textureMappingEnabled = DEFAULT_VALUE_TEXTURE_MAPPING_ENABLED;
        this.wireframeModeEnabled = DEFAULT_VALUE_WIREFRAME_MODE_ENABLED;
        this.errorChecksEnabled = DEFAULT_VALUE_GL_ERROR_CHECKS_ENABLED;
        this.tracingEnabled = DEFAULT_VALUE_GL_TRACING_ENABLED;
    }
    
    /**
     * Reads the options from the passed RenderOptions and sets them in this
     * RenderOptions.
     * 
     * @param renderOptions The render options that will be cloned by this
     *            RenderOptions
     */
    public void loadOptions( RenderOptions renderOptions )
    {
        this.glStatesCacheEnabled = renderOptions.glStatesCacheEnabled;
        this.lightingEnabled = renderOptions.lightingEnabled;
        this.vbosEnabled = renderOptions.vbosEnabled;
        this.displayListsEnabled = renderOptions.displayListsEnabled;
        this.textureMappingEnabled = renderOptions.textureMappingEnabled;
        this.wireframeModeEnabled = renderOptions.wireframeModeEnabled;
        this.errorChecksEnabled = renderOptions.errorChecksEnabled;
        this.tracingEnabled = renderOptions.tracingEnabled;
    }
    
    /**
     * Populates a HashMap with the options and their values. The values are
     * mapped using their Option enum's name.
     * 
     * @return populated HashMap with Option names mapped to their values
     */
    public HashMap< String, Object > exportOptions()
    {
        HashMap< String, Object > exported = new HashMap< String, Object >();
        
        exported.put( "GL_STATES_CACHE_ENABLED", this.glStatesCacheEnabled );
        exported.put( "LIGHTING_ENABLED", this.lightingEnabled );
        exported.put( "VBOS_ENABLED", this.vbosEnabled );
        exported.put( "DISPLAY_LISTS_ENABLED", this.displayListsEnabled );
        exported.put( "TEXTURE_MAPPING_ENABLED", this.textureMappingEnabled );
        exported.put( "WIREFRAME_MODE_ENABLED", this.wireframeModeEnabled );
        exported.put( "GL_ERROR_CHECKS_ENABLED", this.errorChecksEnabled );
        exported.put( "GL_TRACING_ENABLED", this.tracingEnabled );
        
        return ( exported );
    }
    
    private static final boolean importBooleanOption( Map<String, Object> map, String key, boolean defaultValue )
    {
        Object value = map.get( key );
        
        if ( ( value == null ) || ( !( value instanceof Boolean ) ) )
            return ( defaultValue );
        
        return ( ( (Boolean)value ).booleanValue() );
    }
    
    /**
     * Sets all options from the given HashMap into the current RenderOptions
     * object. The HashMap is interperated and the values are fed into the
     * setValue methods. Hence, if a non-existant Option is used, a
     * RuntimeException may be thrown.
     * 
     * @param map The options to import.
     */
    public void importOptions( Map< String, Object > map )
    {
        this.glStatesCacheEnabled = importBooleanOption( map, "GL_STATES_CACHE_ENABLED", DEFAULT_VALUE_GL_STATES_CACHE_ENABLED );
        this.lightingEnabled = importBooleanOption( map, "LIGHTING_ENABLED", DEFAULT_VALUE_LIGHTING_ENABLED );
        this.vbosEnabled = importBooleanOption( map, "VBOS_ENABLED", DEFAULT_VALUE_VBOS_ENABLED );
        this.displayListsEnabled = importBooleanOption( map, "DISPLAY_LISTS_ENABLED", DEFAULT_VALUE_DISPLAY_LISTS_ENABLED );
        this.textureMappingEnabled = importBooleanOption( map, "TEXTURE_MAPPING_ENABLED", DEFAULT_VALUE_TEXTURE_MAPPING_ENABLED );
        this.wireframeModeEnabled = importBooleanOption( map, "WIREFRAME_MODE_ENABLED", DEFAULT_VALUE_WIREFRAME_MODE_ENABLED );
        this.errorChecksEnabled = importBooleanOption( map, "GL_ERROR_CHECKS_ENABLED", DEFAULT_VALUE_GL_ERROR_CHECKS_ENABLED );
        this.tracingEnabled = importBooleanOption( map, "GL_TRACING_ENABLED", DEFAULT_VALUE_GL_TRACING_ENABLED );
    }
    
    /**
     * Default constructor uses the default settings for all of the render
     * options.
     */
    public RenderOptions()
    {
        // Loads the default options
        loadDefaultOptions();
    }
    
    /**
     * Creates a new RenderOptions class which mimmics the given RenderOptions.
     * 
     * @param base The RenderOptions whose options will be cloned. If it is null
     *            then the default options are used.
     */
    public RenderOptions( RenderOptions base )
    {
        if ( base == null )
        {
            loadDefaultOptions();
        }
        else
        {
            loadOptions( base );
        }
    }
}
