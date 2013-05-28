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

import java.util.ArrayList;
import java.util.HashMap;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix3f;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.TexCoordf;
import org.openmali.vecmath2.TupleNf;
import org.openmali.vecmath2.TupleNi;
import org.xith3d.render.CanvasPeer;

/**
 * Created on Jul 7, 2006 by florian for project 'xith3d_glsl_shader_support'
 * 
 * @author Florian Hofmann (aka Goliat)
 * @author Marvin Froehlich (aka Qudus)
 */
public class GLSLParameters extends NodeComponent// implements StateTrackable< GLSLShaderProgramParameters >
{
    private final HashMap< String, Object > uniformVars = new HashMap< String, Object >();
    private final ArrayList< String > floatUniformVarNames = new ArrayList< String >();
    private final ArrayList< String > intUniformVarNames = new ArrayList< String >();
    private final ArrayList< Integer > floatUniformVarBaseSizes = new ArrayList< Integer >();
    private final ArrayList< Integer > intUniformVarBaseSizes = new ArrayList< Integer >();
    
    public final int getNumUniformVarsFloat()
    {
        return ( floatUniformVarNames.size() );
    }
    
    public final int getNumUniformVarsInt()
    {
        return ( intUniformVarNames.size() );
    }
    
    public final boolean hasUniformVars()
    {
        return ( ( floatUniformVarNames.size() > 0 ) || ( intUniformVarNames.size() > 0 ) );
    }
    
    public final int getUniformVarBaseSizeFloat( int i )
    {
        return ( floatUniformVarBaseSizes.get( i ).intValue() );
    }
    
    public final int getUniformVarBaseSizeInt( int i )
    {
        return ( intUniformVarBaseSizes.get( i ).intValue() );
    }
    
    public final String getFloatUniformVarName( int i )
    {
        return ( floatUniformVarNames.get( i ) );
    }
    
    public final String getIntUniformVarName( int i )
    {
        return ( intUniformVarNames.get( i ) );
    }
    
    public final float[] getUniformVarValueFloat( String varName )
    {
        return ( (float[])uniformVars.get( varName ) );
    }
    
    public final int[] getUniformVarValueInt( String varName )
    {
        return ( (int[])uniformVars.get( varName ) );
    }
    
    public final float[] getUniformVarValueFloat( int i )
    {
        return ( (float[])uniformVars.get( floatUniformVarNames.get( i ) ) );
    }
    
    public final int[] getUniformVarValueInt( int i )
    {
        return ( (int[])uniformVars.get( intUniformVarNames.get( i ) ) );
    }
    
    // floats
    public final void setUniformVar( String name, float[] values )
    {
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[ values.length ];
            System.arraycopy( values, 0, newValue, 0, values.length );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[ values.length ];
            System.arraycopy( values, 0, newValue, 0, values.length );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != values.length )
        {
            newValue = new float[ values.length ];
            System.arraycopy( values, 0, newValue, 0, values.length );
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, 1 );
            
        }
        else
        {
            newValue = (float[])oldValueObj;
            System.arraycopy( values, 0, newValue, 0, values.length );
        }
    }
    
    // floats
    public final void setUniformVar( String name, float value )
    {
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[] { value };
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[] { value };
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != 1 )
        {
            newValue = new float[] { value };
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, 1 );
        }
        else
        {
            newValue = (float[])oldValueObj;
            newValue[ 0 ] = value;
        }
    }
    
    // integers
    public final void setUniformVar( String name, int[] values )
    {
        Object oldValueObj = uniformVars.get( name );
        
        int[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new int[ values.length ];
            System.arraycopy( values, 0, newValue, 0, values.length );
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof int[] ) )
        {
            int floatIndex = floatUniformVarNames.indexOf( name );
            if ( floatIndex >= 0 )
            {
                floatUniformVarNames.remove( floatIndex );
                floatUniformVarBaseSizes.remove( floatIndex );
            }
            
            newValue = new int[ values.length ];
            System.arraycopy( values, 0, newValue, 0, values.length );
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( ( (int[])oldValueObj ).length != values.length )
        {
            newValue = new int[ values.length ];
            System.arraycopy( values, 0, newValue, 0, values.length );
            
            uniformVars.put( name, newValue );
            int intIndex = intUniformVarNames.indexOf( name );
            intUniformVarBaseSizes.set( intIndex, 1 );
        }
        else
        {
            newValue = (int[])oldValueObj;
            System.arraycopy( values, 0, newValue, 0, values.length );
        }
    }
    
    // integers
    public final void setUniformVar( String name, int value )
    {
        Object oldValueObj = uniformVars.get( name );
        
        int[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new int[] { value };
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof int[] ) )
        {
            int floatIndex = floatUniformVarNames.indexOf( name );
            if ( floatIndex >= 0 )
            {
                floatUniformVarNames.remove( floatIndex );
                floatUniformVarBaseSizes.remove( floatIndex );
            }
            
            newValue = new int[] { value };
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( 1 );
            uniformVars.put( name, newValue );
        }
        else if ( ( (int[])oldValueObj ).length != 1 )
        {
            newValue = new int[] { value };
            
            uniformVars.put( name, newValue );
            int intIndex = intUniformVarNames.indexOf( name );
            intUniformVarBaseSizes.set( intIndex, 1 );
        }
        else
        {
            newValue = (int[])oldValueObj;
            newValue[ 0 ] = value;
        }
    }
    
    private static final void copyMatricesToArray( Matrix3f[] mats, float[] target )
    {
        int offset = 0;
        
        for ( int i = 0; i < mats.length; i++ )
        {
            /*
            target[ offset + 0 ] = mats[ i ].m00();
            target[ offset + 1 ] = mats[ i ].m10();
            target[ offset + 2 ] = mats[ i ].m20();
            target[ offset + 3 ] = mats[ i ].m01();
            target[ offset + 4 ] = mats[ i ].m11();
            target[ offset + 5 ] = mats[ i ].m21();
            target[ offset + 6 ] = mats[ i ].m02();
            target[ offset + 7 ] = mats[ i ].m12();
            target[ offset + 8 ] = mats[ i ].m22();
            */
            
            mats[ i ].getColumnMajor( target, offset );
            
            offset += 9;
        }
    }
    
    // matrices
    // altough 2x2 matrices are avaible in glsl there is no such type in vecmath ...
    // i'll just leave it out ... if anyone needs it i can implement it later on
    public final void setUniformVar( String name, Matrix3f[] values )
    {
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[ values.length * 9 ];
            copyMatricesToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 9 );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[ values.length * 9 ];
            copyMatricesToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 9 );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != values.length * 9 )
        {
            newValue = new float[ values.length * 9 ];
            copyMatricesToArray( values, newValue );
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, 9 );
        }
        else
        {
            newValue = (float[])oldValueObj;
            copyMatricesToArray( values, newValue );
        }
    }
    
    // matrices
    // altough 2x2 matrices are avaible in glsl there is no such type in vecmath ...
    // i'll just leave it out ... if anyone needs it i can implement it later on
    public final void setUniformVar( String name, Matrix3f value )
    {
        setUniformVar( name, new Matrix3f[] { value } );
    }
    
    private static final void copyMatricesToArray( Matrix4f[] mats, float[] target )
    {
        int offset = 0;
        
        for ( int i = 0; i < mats.length; i++ )
        {
            /*
            target[ offset + 0 ] = mats[ i ].m00();
            target[ offset + 1 ] = mats[ i ].m10();
            target[ offset + 2 ] = mats[ i ].m20();
            target[ offset + 3 ] = mats[ i ].m30();
            target[ offset + 4 ] = mats[ i ].m01();
            target[ offset + 5 ] = mats[ i ].m11();
            target[ offset + 6 ] = mats[ i ].m21();
            target[ offset + 7 ] = mats[ i ].m31();
            target[ offset + 8 ] = mats[ i ].m02();
            target[ offset + 9 ] = mats[ i ].m12();
            target[ offset + 10 ] = mats[ i ].m22();
            target[ offset + 11 ] = mats[ i ].m32();
            target[ offset + 12 ] = mats[ i ].m03();
            target[ offset + 13 ] = mats[ i ].m13();
            target[ offset + 14 ] = mats[ i ].m23();
            target[ offset + 15 ] = mats[ i ].m33();
            */
            
            mats[ i ].getColumnMajor( target, offset );
            
            offset += 16;
        }
    }
    
    public final void setUniformVar( String name, Matrix4f[] values )
    {
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[ values.length * 16 ];
            copyMatricesToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 16 );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[ values.length * 16 ];
            copyMatricesToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( 16 );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != values.length * 16 )
        {
            newValue = new float[ values.length * 16 ];
            copyMatricesToArray( values, newValue );
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, 16 );
        }
        else
        {
            newValue = (float[])oldValueObj;
            copyMatricesToArray( values, newValue );
        }
    }
    
    public final void setUniformVar( String name, Matrix4f value )
    {
        setUniformVar( name, new Matrix4f[] { value } );
    }
    
    private static final void copyTuplesToArray( TupleNf<?>[] tuples, float[] target )
    {
        int offset = 0;
        
        for ( int i = 0; i < tuples.length; i++ )
        {
            tuples[ i ].get( target, offset );
            
            offset += tuples[ i ].getSize();
        }
    }
    
    // arrays ==================================================
    // float arrays --------------------------------------------
    public final void setUniformVar( String name, TupleNf<?>[] values )
    {
        int size = values[0].getSize();
        
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[ values.length * size ];
            copyTuplesToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[ values.length * size ];
            copyTuplesToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != values.length * size )
        {
            newValue = new float[ values.length * size ];
            copyTuplesToArray( values, newValue );
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, size );
        }
        else
        {
            newValue = (float[])oldValueObj;
            copyTuplesToArray( values, newValue );
        }
    }
    
    // arrays ==================================================
    // float arrays --------------------------------------------
    public final void setUniformVar( String name, TupleNf<?> value )
    {
        setUniformVar( name, new TupleNf<?>[] { value } );
    }
    
    // arrays ==================================================
    // float arrays --------------------------------------------
    public final void setUniformVar( String name, int baseSize, float... values )
    {
        int size = values.length - ( values.length % baseSize );
        
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[ size ];
            System.arraycopy( values, 0, newValue, 0, size );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( baseSize );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[ size ];
            System.arraycopy( values, 0, newValue, 0, size );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( baseSize );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != size )
        {
            newValue = new float[ size ];
            System.arraycopy( values, 0, newValue, 0, size );
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, baseSize );
        }
        else
        {
            newValue = (float[])oldValueObj;
            System.arraycopy( values, 0, newValue, 0, size );
        }
    }
    
    // arrays ==================================================
    // int arrays --------------------------------------------
    public final void setUniformVar( String name, int baseSize, int... values )
    {
        int size = values.length - ( values.length % baseSize );
        
        Object oldValueObj = uniformVars.get( name );
        
        int[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new int[ size ];
            System.arraycopy( values, 0, newValue, 0, size );
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( baseSize );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof int[] ) )
        {
            int floatIndex = floatUniformVarNames.indexOf( name );
            if ( floatIndex >= 0 )
            {
                floatUniformVarNames.remove( floatIndex );
                floatUniformVarBaseSizes.remove( floatIndex );
            }
            
            newValue = new int[ size ];
            System.arraycopy( values, 0, newValue, 0, size );
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( baseSize );
            uniformVars.put( name, newValue );
        }
        else if ( ( (int[])oldValueObj ).length != size )
        {
            newValue = new int[ size ];
            System.arraycopy( values, 0, newValue, 0, size );
            
            uniformVars.put( name, newValue );
            int intIndex = intUniformVarNames.indexOf( name );
            intUniformVarBaseSizes.set( intIndex, baseSize );
        }
        else
        {
            newValue = (int[])oldValueObj;
            System.arraycopy( values, 0, newValue, 0, size );
        }
    }
    
    private static final void copyColorsToArray( Colorf[] colors, float[] target )
    {
        int offset = 0;
        
        for ( int i = 0; i < colors.length; i++ )
        {
            colors[ i ].get( target, offset );
            
            offset += colors[ i ].getSize();
        }
    }
    
    // arrays ==================================================
    // float arrays --------------------------------------------
    public final void setUniformVar( String name, Colorf[] values )
    {
        int size = values[0].hasAlpha() ? 4 : 3;
        
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[ values.length * size ];
            copyColorsToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[ values.length * size ];
            copyColorsToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != values.length * size )
        {
            newValue = new float[ values.length * size ];
            copyColorsToArray( values, newValue );
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, size );
        }
        else
        {
            newValue = (float[])oldValueObj;
            copyColorsToArray( values, newValue );
        }
    }
    
    // arrays ==================================================
    // float arrays --------------------------------------------
    public final void setUniformVar( String name, Colorf value )
    {
        setUniformVar( name, new Colorf[] { value } );
    }
    
    private static final void copyTexCoordsToArray( TexCoordf<?>[] texCoords, float[] target )
    {
        int offset = 0;
        
        for ( int i = 0; i < texCoords.length; i++ )
        {
            texCoords[ i ].get( target, offset );
            
            offset += texCoords[ i ].getSize();
        }
    }
    
    // arrays ==================================================
    // float arrays --------------------------------------------
    public final void setUniformVar( String name, TexCoordf<?>[] values )
    {
        int size = values[0].getSize();
        
        Object oldValueObj = uniformVars.get( name );
        
        float[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new float[ values.length * size ];
            copyTexCoordsToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof float[] ) )
        {
            int intIndex = intUniformVarNames.indexOf( name );
            if ( intIndex >= 0 )
            {
                intUniformVarNames.remove( intIndex );
                intUniformVarBaseSizes.remove( intIndex );
            }
            
            newValue = new float[ values.length * size ];
            copyTexCoordsToArray( values, newValue );
            
            floatUniformVarNames.add( name );
            floatUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( ( (float[])oldValueObj ).length != values.length * size )
        {
            newValue = new float[ values.length * size ];
            copyTexCoordsToArray( values, newValue );
            
            uniformVars.put( name, newValue );
            int floatIndex = floatUniformVarNames.indexOf( name );
            floatUniformVarBaseSizes.set( floatIndex, size );
        }
        else
        {
            newValue = (float[])oldValueObj;
            copyTexCoordsToArray( values, newValue );
        }
    }
    
    // arrays ==================================================
    // float arrays --------------------------------------------
    public final void setUniformVar( String name, TexCoordf<?> value )
    {
        setUniformVar( name, new TexCoordf<?>[] { value } );
    }
    
    
    private static final void copyTuplesToArray( TupleNi<?>[] tuples, int[] target )
    {
        int offset = 0;
        
        for ( int i = 0; i < tuples.length; i++ )
        {
            tuples[ i ].get( target, offset );
            
            offset += tuples[ i ].getSize();
        }
    }
    
    // arrays ==================================================
    // int arrays --------------------------------------------
    public final void setUniformVar( String name, TupleNi<?>[] values )
    {
        int size = values[0].getSize();
        
        Object oldValueObj = uniformVars.get( name );
        
        int[] newValue;
        
        if ( oldValueObj == null )
        {
            newValue = new int[ values.length * size ];
            copyTuplesToArray( values, newValue );
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( !( oldValueObj instanceof int[] ) )
        {
            int floatIndex = floatUniformVarNames.indexOf( name );
            if ( floatIndex >= 0 )
            {
                floatUniformVarNames.remove( floatIndex );
                floatUniformVarBaseSizes.remove( floatIndex );
            }
            
            newValue = new int[ values.length * size ];
            copyTuplesToArray( values, newValue );
            
            intUniformVarNames.add( name );
            intUniformVarBaseSizes.add( size );
            uniformVars.put( name, newValue );
        }
        else if ( ( (int[])oldValueObj ).length != values.length * size )
        {
            newValue = new int[ values.length * size ];
            copyTuplesToArray( values, newValue );
            
            uniformVars.put( name, newValue );
            int intIndex = intUniformVarNames.indexOf( name );
            intUniformVarBaseSizes.set( intIndex, size );
        }
        else
        {
            newValue = (int[])oldValueObj;
            copyTuplesToArray( values, newValue );
        }
    }
    
    // arrays ==================================================
    // int arrays --------------------------------------------
    public final void setUniformVar( String name, TupleNi<?> value )
    {
        setUniformVar( name, new TupleNi<?>[] { value } );
    }
    
    
    public final void removeUniformVar( String name )
    {
        int index;
        
        index = floatUniformVarNames.indexOf( name );
        if ( index >= 0 )
        {
            floatUniformVarNames.remove( index );
            floatUniformVarBaseSizes.remove( index );
            
            uniformVars.remove( name );
            
            return;
        }
        
        index = intUniformVarNames.indexOf( name );
        if ( index >= 0 )
        {
            intUniformVarNames.remove( index );
            intUniformVarBaseSizes.remove( index );
            
            uniformVars.remove( name );
            
            return;
        }
    }
    
    
    //////////////////////////////////////////////////////////////////
    /////////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    //////////////////////////////////////////////////////////////////
    
    public GLSLParameters getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        GLSLParameters orgParams = (GLSLParameters)original;
        
        uniformVars.clear();
        floatUniformVarNames.clear();
        intUniformVarNames.clear();
        floatUniformVarBaseSizes.clear();
        intUniformVarBaseSizes.clear();
        
        uniformVars.putAll( orgParams.uniformVars );
        floatUniformVarNames.addAll( orgParams.floatUniformVarNames );
        intUniformVarNames.addAll( orgParams.intUniformVarNames );
        floatUniformVarBaseSizes.addAll( orgParams.floatUniformVarBaseSizes );
        intUniformVarBaseSizes.addAll( orgParams.intUniformVarBaseSizes );
    }
    
    @Override
    public GLSLParameters cloneNodeComponent( boolean forceDuplicate )
    {
        GLSLParameters clone = new GLSLParameters();
        
        clone.duplicateNodeComponent( this, forceDuplicate );
        
        return ( clone );
    }
    
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof GLSLParameters ) )
            return ( false );
        
        return ( false );
    }
    
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    /*
    @Override
    public int compareTo( GLSLShaderProgramParameters o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.getNumVertexShaders() > o.getNumVertexShaders() )
            return ( 1 );
        else if ( this.getNumVertexShaders() < o.getNumVertexShaders() )
            return ( -1 );
        else if ( this.getNumFragmentShaders() > o.getNumFragmentShaders() )
            return ( 1 );
        else if ( this.getNumFragmentShaders() < o.getNumFragmentShaders() )
            return ( -1 );
        
        int result = 0;
        for ( int i = 0; i < getNumVertexShaders(); i++ )
        {
            result += this.getVertexShader( i ).compareTo( o.getVertexShader( i ) );
        }
        for ( int i = 0; i < getNumFragmentShaders(); i++ )
        {
            result += this.getFragmentShader( i ).compareTo( o.getFragmentShader( i ) );
        }
        
        if ( result > 0 )
            return ( +1 );
        else if ( result < 0 )
            return ( -1 );
        else
            return ( 0 );
    }
    */
    
    public GLSLParameters()
    {
        super( false );
    }
}
