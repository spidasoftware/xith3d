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
package org.xith3d.utility.hud.editor.info;

import java.lang.reflect.Constructor;

import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Sized2fRO;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;

import org.xith3d.ui.hud.base.Widget;
import org.xith3d.ui.hud.listeners.WidgetEventsReceiverAdapter;

/**
 * Widget info class used in the HUD editor
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class HEInfo_Widget
{
    private Widget widget;
    
    private final Point2f location;
    private final Dim2f size;
    
    public HEInfo_Widget( Tuple2f location, Sized2fRO size )
    {
        this.location = new Point2f( location );
        this.size = new Dim2f( size );
    }
    
    protected void setWidget( Widget widget )
    {
        this.widget = widget;
        
        WidgetEventsReceiverAdapter wera = new WidgetEventsReceiverAdapter()
        {
            @Override
            public void onWidgetLocationChanged( Widget widget, float oldLeft, float oldTop, float newLeft, float newTop )
            {
                HEInfo_Widget.this.location.setX( newLeft );
                HEInfo_Widget.this.location.setY( newTop );
            }
            
            @Override
            public void onWidgetSizeChanged( Widget widget, float oldWidth, float oldHeight, float newWidth, float newHeight )
            {
                HEInfo_Widget.this.size.setWidth( newWidth );
                HEInfo_Widget.this.size.setHeight( newHeight );
            }
        };
        
        widget.addLocationListener( wera );
        widget.addSizeListener( wera );
    }
    
    public boolean setLocation( float x, float y )
    {
        final boolean result = ( ( x != widget.getLeft() ) || ( y != widget.getTop() ) );
        
        this.location.set( x, y );
        widget.setLocation( location );
        
        return ( result );
    }
    
    public final boolean setLocation( Tuple2f location )
    {
        return ( setLocation( location.getX(), location.getY() ) );
    }
    
    public final Point2f getLocation()
    {
        return ( location );
    }
    
    public boolean setSize( float width, float height )
    {
        final boolean result = ( ( width != widget.getWidth() ) || ( height != widget.getHeight() ) );
        
        this.size.set( width, height );
        widget.setSize( size );
        
        return ( result );
    }
    
    public final boolean setSize( Sized2fRO size )
    {
        return ( setSize( size.getWidth(), size.getHeight() ) );
    }
    
    public final Dim2f getSize()
    {
        return ( this.size );
    }
    
    public final Widget getWidget()
    {
        return ( widget );
    }
    
    /**
     * Create a new InfoWidget
     * 
     * @param clazzName
     *                Name of the class
     * @param location
     *                Location of the widget
     * @param size
     *                Size of the widget
     */
    @SuppressWarnings( "unchecked" )
    public static HEInfo_Widget newWidget( String clazzName, Tuple2f location, Sized2fRO size )
    {
        HEInfo_Widget widgetInfo = null;
        
        try
        {
            Class< HEInfo_Widget > infoClazz = (Class< HEInfo_Widget >)Thread.currentThread().getContextClassLoader().loadClass( HEInfo_Widget.class.getPackage().getName() + ".HEInfo_" + clazzName );
            Constructor< HEInfo_Widget > ic = infoClazz.getConstructor( Tuple2f.class, Tuple2f.class );
            widgetInfo = ic.newInstance( location, size );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return ( widgetInfo );
    }
}
