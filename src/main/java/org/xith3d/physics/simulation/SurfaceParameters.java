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
package org.xith3d.physics.simulation;

import java.util.HashMap;

/**
 * This class contains parameters used when solving collision constraints.
 * 
 * It's implemented using a HashMap, because not all physics engines
 * use the same surface parameters :).
 * 
 * Physics engine developer, if you implement the XPAL interfaces, you
 * should tell in your documentation which surface parameters you accept,
 * which type they are and what do they correspond to.
 * PLEASE BE CLEAR ABOUT THAT. Think about your users.
 * 
 * The supported parameters can also pe programmatically read through the
 * {@link #getAvailableParameters()} method and additional info is returned
 * by the {@link #getParameterInfo(String)} method.
 * 
 * @see SimulationWorld#resolveCollision(org.xith3d.physics.collision.Collision, Body, Body, SurfaceParameters)
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class SurfaceParameters
{
    private final String[] availableParams;
    private final HashMap<String, Object> defaultParams;
    private final HashMap<String, Object> params;
    
    /**
     * @return an array filled with all the available parameter names for the
     *         current implementation.
     * 
     * @see #getParameterInfo(String)
     * @see #dumpAvailableParameters()
     */
    public final String[] getAvailableParameters()
    {
        String[] availableParams = new String[ this.availableParams.length ];
        System.arraycopy( this.availableParams, 0, availableParams, 0, this.availableParams.length );
        
        return ( availableParams );
    }
    
    /**
     * This method simply dumps all available parameter names.
     * 
     * @see #getAvailableParameters()
     * @see #getParameterInfo(String)
     * @see #dumpParameterInfo()
     */
    public final void dumpAvailableParameters()
    {
        System.out.println( "Available parameters:" );
        for ( String paramName: getAvailableParameters() )
        {
            System.out.println( "  " + paramName );
        }
    }
    
    /**
     * This method returns info about the given parameter's type
     * and supported values.
     * 
     * @see #getAvailableParameters()
     * @see #dumpParameterInfo()
     * 
     * @param paramName
     * 
     * @return parameter info
     */
    public abstract String getParameterInfo( String paramName );
    
    /**
     * This method dumps the result of {@link #getParameterInfo(String)}
     * for each available parameter.
     * 
     * @see #getAvailableParameters()
     */
    public final void dumpParameterInfo()
    {
        System.out.println( "Parameters info:" );
        for ( String paramName: getAvailableParameters() )
        {
            System.out.println( "  " + getParameterInfo( paramName ) );
        }
    }
    
    /**
     * @param paramName
     * 
     * @return <code>true</code>, if the queried parameter is available.
     */
    public final boolean isParamAvailable( String paramName )
    {
        return ( defaultParams.containsKey( paramName ) );
    }
    
    private final void checkAvailability( String paramName )
    {
        if ( !isParamAvailable( paramName ) )
            throw new IllegalArgumentException( "The parameter \"" + paramName + "\" is not available in the current implementation" );
    }
    
    /**
     * Sets a parameter's value.
     * 
     * @param paramName the parameter's name to be specified, e.g. "mu" or "bounce"
     * @param paramValue the values of the params to be specified, e.g. "0.1f" or "true"
     * 
     * @see #getAvailableParameters()
     */
    public void setParameter( String paramName, Object paramValue )
    {
        checkAvailability( paramName );
        
        if ( paramValue == null )
            resetParameter( paramName );
        else
            params.put( paramName, paramValue );
    }
    
    /**
     * Resets the given parameter to its default value.
     * 
     * @param paramName the parameter's name to be resetted
     * 
     * @see #getAvailableParameters()
     */
    public void resetParameter( String paramName )
    {
        checkAvailability( paramName );
        
        params.remove( paramName );
    }
    
    /**
     * Gets a parameter as an {@link Object}. (you should cast it to whatever it actually is.)
     * 
     * @param paramName the name of the requested parameter
     * 
     * @return The parameter value
     */
    public final Object getParameter( String paramName )
    {
        checkAvailability( paramName );
        
        final Object value = params.get( paramName );
        if ( value == null )
            return ( defaultParams.get( paramName ) );
        
        return ( value );
    }
    
    /**
     * @param paramName
     * 
     * @return true, if the erquested parameter has been set (and not been reset).
     */
    public final boolean isParameterSet( String paramName )
    {
        return ( params.containsKey( paramName ) );
    }
    
    /**
     * Gets a float parameter from the HashMap.
     * 
     * @param paramName the name of the requested parameter
     * 
     * @return The parameter value
     */
    public final float getFloatParameter( String paramName )
    {
        final Object value = params.get( paramName );
        if ( value == null )
            return ( ( (Float)defaultParams.get( paramName ) ).floatValue() );
        
        return ( ( (Float)value ).floatValue() );
    }
    
    /**
     * Gets a boolean parameter from the HashMap.
     * 
     * @param paramName the name of the requested parameter
     * 
     * @return The parameter value
     */
    public final boolean getBooleanParameter( String paramName )
    {
        final Object value = params.get( paramName );
        if ( value == null )
            return ( ( (Boolean)defaultParams.get( paramName ) ).booleanValue() );
        
        return ( ( (Boolean)value ).booleanValue() );
    }
    
    /**
     * Creates a new SurfaceParameters with specified params.
     * 
     * @param defaultParamers the default parameters set by teh implementation
     * @param paramStrings the param strings to be specified, e.g. "mu" or "bounce"
     * @param paramValues the values of the params to be specified, e.g. ".1f" or "true"
     * 
     * In order to know which params are valid, you should consult the documentation of
     * the XPAL implementation of your physic engine.
     * 
     * @see #setParameter(String, Object)
     */
    protected SurfaceParameters( HashMap<String, Object> defaultParamers, String[] paramStrings, Object[] paramValues )
    {
        if ( ( defaultParamers == null ) || ( defaultParamers.isEmpty() ) )
        {
            throw new IllegalArgumentException( "defaultParamers must not be null nor empty." );
        }
        
        this.params = new HashMap<String, Object>();
        this.availableParams = new String[ defaultParamers.size() ];
        this.defaultParams = defaultParamers;
        
        int i = 0;
        for ( String param: defaultParamers.keySet() )
        {
            this.availableParams[i++] = param;
        }
        
        if ( ( paramStrings != null ) && ( paramValues != null ) )
        {
            for ( i = 0; i < paramStrings.length; i++ )
            {
                params.put( paramStrings[i], paramValues[i] );
            }
        }
    }
}
