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
package org.xith3d.utility.hud;

import org.openmali.vecmath2.Colorf;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.schedops.properties.ValueInterpolator;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.widgets.Image;

/**
 * The HUDFadeOutHandler covers the HUD with a black Image and fades to black.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class HUDFadeOutHandler extends ValueInterpolator
{
    private final Image image;
    private Colorf color;
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void applyValue( float value )
    {
        color.setAlpha( value );
        image.setColor( color );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onInterpolationFinished()
    {
        super.onInterpolationFinished();
        
        image.detach();
    }
    
    public HUDFadeOutHandler( HUD hud, OperationScheduler opScheder, long fadeTime, Colorf color )
    {
        super( fadeTime, 20L, 1.0f, 0.0f );
        
        this.color = new Colorf( color.getRed(), color.getGreen(), color.getBlue(), 1.0f );
        this.image = new Image( hud.getResX(), hud.getResY(), color );
        hud.getContentPane().addWidget( image, 0.0f, 0.0f, 1000 );
        
        opScheder.addInterval( this );
    }
    
    public HUDFadeOutHandler( HUD hud, OperationScheduler opScheder, long fadeTime )
    {
        this( hud, opScheder, fadeTime, Colorf.BLACK );
    }
    
    public static void fade( HUD hud, OperationScheduler opScheder, long fadeTime, Colorf color )
    {
        new HUDFadeOutHandler( hud, opScheder, fadeTime, color );
    }
    
    public static void fade( HUD hud, OperationScheduler opScheder, long fadeTime )
    {
        fade( hud, opScheder, fadeTime, Colorf.BLACK );
    }
}
