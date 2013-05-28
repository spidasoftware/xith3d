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
package org.xith3d.ui.hud;

import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Dim2i;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Tuple2i;
import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.base.__HUD_base_PrivilegedAccess;

/**
 * The coordinates converter converts sizes and locations
 * from and to different coortinate spaces.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HUDCoordinatesConverter
{
    private final HUD hud;
    
    public final HUD getHUD()
    {
        return ( hud );
    }
    
    /**
     * @return a width that's visually equal to the given width
     * 
     * @param height the height to calculate a visually equal width
     */
    public final float getEqualWidth( float height )
    {
        //if ( !hud.hasCustomResolution() )
        //    return ( height );
        
        //if ( hud.getAspect() == 0f )
        //    return ( 0f );
        
        return ( height * hud.getResAspect() / hud.getAspect() );
    }
    
    /**
     * Calculates a height that's visually equal to the given width.
     * 
     * @param width the width to calculate a visually equal height
     * 
     * @return the buffer back again
     */
    public final float getEqualHeight( float width )
    {
        //if ( !hud.hasCustomResolution() )
        //    return ( width );
        
        //if ( hud.getResAspect() == 0f )
        //    return ( 0f );
        
        return ( width * hud.getAspect() / hud.getResAspect()  );
    }
    
    /**
     * Calculates HUD size from these pixel-values.
     * 
     * @param w the canvas-x-value to transform
     * @param h the canvas-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Dim2f_ extends Dim2f> Dim2f_ getSizePixels2HUD( int w, int h, Dim2f_ buffer )
    {
        return ( getSizeSG2HUD( w, h, buffer ) );
    }
    
    /**
     * Calculates HUD location from these pixel-values.
     * 
     * @param x the canvas-x-value to transform
     * @param y the canvas-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationPixels2HUD( int x, int y, Tuple2f_ buffer )
    {
        buffer.set( x * hud.getResX() / hud.getWidth(), y * hud.getResY() / hud.getHeight() );
        
        return ( buffer );
    }
    
    /**
     * Calculates pixel size from these HUD-values.
     * 
     * @param w the HUD-x-value to transform
     * @param h the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Dim2i_ extends Dim2i> Dim2i_ getSizeHUD2Pixels( float w, float h, Dim2i_ buffer )
    {
        if ( hud.hasCustomResolution() )
            buffer.set( Math.round( w * hud.getWidth() / hud.getResX() ), Math.round( h * hud.getHeight() / hud.getResY() ) );
        else
            buffer.set( Math.round( w ), Math.round( h ) );
        
        return ( buffer );
    }
    
    /**
     * Calculates pixel location from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Tuple2i_ extends Tuple2i> Tuple2i_ getLocationHUD2Pixels( float x, float y, Tuple2i_ buffer )
    {
        if ( hud.hasCustomResolution() )
            buffer.set( Math.round( x * hud.getWidth() / hud.getResX() ), Math.round( y * hud.getHeight() / hud.getResY() ) );
        else
            buffer.set( Math.round( x ), Math.round( y ) );
        
        return ( buffer );
    }
    
    /**
     * Calculates scenegraph width and height from these HUD-values.
     * 
     * @param w the HUD-x-value to transform
     * @param h the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Dim2f_ extends Dim2f> Dim2f_ getSizeHUD2SG( float w, float h, Dim2f_ buffer )
    {
        if ( hud.hasCustomResolution() )
            buffer.set( Math.round( w * hud.getWidth() / hud.getResX() ), Math.round( h * hud.getHeight() / hud.getResY() ) );
        else
            buffer.set( Math.round( w ), Math.round( h ) );
        
        return ( buffer );
    }
    
    /**
     * Calculates scenegraph location from these HUD-values.
     * 
     * @param x the HUD-x-value to transform
     * @param y the HUD-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationHUD2SG( float x, float y, Tuple2f_ buffer )
    {
        if ( hud.hasCustomResolution() )
            buffer.set( Math.round( x * hud.getWidth() / hud.getResX() ), Math.round( -y * hud.getHeight() / hud.getResY() ) );
        else
            buffer.set( Math.round( x ), Math.round( -y ) );
        
        return ( buffer );
    }
    
    /**
     * Calculates HUD size from these scenegraph-values.
     * 
     * @param w the scenegraph-x-value to transform
     * @param h the scenegraph-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Dim2f_ extends Dim2f> Dim2f_ getSizeSG2HUD( float w, float h, Dim2f_ buffer )
    {
        if ( hud.hasCustomResolution() )
            buffer.set( w * hud.getResX() / hud.getWidth(), h * hud.getResY() / hud.getHeight() );
        else
            buffer.set( Math.round( w ), Math.round( h ) );
        
        return ( buffer );
    }
    
    /**
     * Calculates HUD location from these scenegraph-values.
     * 
     * @param x the scenegraph-x-value to transform
     * @param y the scenegraph-y-value to transform
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Tuple2f_ extends Tuple2f> Tuple2f_ getLocationSG2HUD( float x, float y, Tuple2f_ buffer )
    {
        if ( hud.hasCustomResolution() )
            buffer.set( x * hud.getResX() / hud.getWidth(), -y * hud.getResY() / hud.getHeight() );
        else
            buffer.set( Math.round( x ), Math.round( -y ) );
        
        return ( buffer );
    }
    
    /**
     * Retrieves the size these pixels have on this WidgetContainer.
     * 
     * @param x the x-count of pixels 
     * @param y the y-count of pixels
     * @param buffer the buffer to write the values to
     * 
     * @return the buffer back again
     */
    public final <Dim2f_ extends Dim2f> Dim2f_ getSizeOfPixels( int x, int y, Dim2f_ buffer )
    {
        getSizePixels2HUD( x, y, buffer );
        
        return ( buffer );
    }
    
    /**
     * Computes the absolute position of the given Widget on the HUD.
     * 
     * @param widget
     * @param buffer
     */
    public final void getAbsoluteLocationOnHUD( Widget widget, Tuple2f buffer )
    {
        buffer.set( widget.getLeft(), widget.getTop() );
        
        WidgetContainer container = widget.getContainer();
        //Window window = null;
        
        Dim2f buffer2 = Dim2f.fromPool();
        
        while ( container != null )
        {
            __HUD_base_PrivilegedAccess.getContentOffset( container, buffer2 );
            buffer.add( buffer2.getWidth(), buffer2.getHeight() );
            
            buffer.add( container.getLeft(), container.getTop() );
            
            //window = container.isContentPane() ? container.getParentWindow() : null;
            container = container.getContainer();
        }
        
        /*
        if ( window != null )
        {
            System.out.print( buffer + ", " );
            
            buffer.add( window.getLeft(), window.getTop() );
            
            buffer.addY( window.getHeaderHeight() );
            
            if ( window.getBorder() != null )
            {
                buffer.add( window.getBorder().getLeftWidth(), window.getBorder().getTopHeight() );
            }
            
            System.out.println( buffer );
        }
        */
        
        if ( buffer2 != null )
            Dim2f.toPool( buffer2 );
    }
    
    public HUDCoordinatesConverter( HUD hud )
    {
        this.hud = hud;
    }
}
