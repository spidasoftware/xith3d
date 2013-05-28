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
package org.xith3d.ui.hud.widgets.assemblies;

import org.openmali.types.twodee.Dim2f;
import org.openmali.types.twodee.Sized2fRO;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;

import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.base.WidgetContainer;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.utils.HUDTextureUtils;
import org.xith3d.ui.hud.widgets.Image;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.hud.widgets.ProgressBar;

import org.xith3d.loop.RenderLoopController;
import org.xith3d.render.RenderPass;
import org.xith3d.resources.LoadingScreenUpdater;
import org.xith3d.resources.ProgressValueSource;
import org.xith3d.resources.ResourceLoader;
import org.xith3d.resources.ResourceLoaderListener;
import org.xith3d.resources.ResourceRequest;
import org.xith3d.scenegraph.Texture2D;

/**
 * Many games need a loading screen. This clas will help you with it.
 * 
 * @see LoadingScreenUpdater
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class LoadingScreen implements LoadingScreenUpdater, ProgressValueSource, ResourceLoaderListener
{
    private Image backgroundImage = null;
    private Label captionLabel = null;
    private ProgressBar progressBar;
    
    private RenderLoopController renderLoopController = null;
    
    /**
     * Sets the RenderLoopController, that is invoked each update.
     */
    public void setRenderLoopController( RenderLoopController rlc )
    {
        this.renderLoopController = rlc;
    }
    
    /**
     * @return the RenderLoopController, that is invoked each update
     */
    public RenderLoopController getRenderLoopController()
    {
        return ( renderLoopController );
    }
    
    /**
     * @return the Image Widget holding the background image.
     */
    public Image getBackgroundImage()
    {
        return ( backgroundImage );
    }
    
    /**
     * @return the Label holding the caption for the progress.
     */
    public Label getCaptionLabel()
    {
        return ( captionLabel );
    }
    
    /**
     * @return the ProgressBar Widget.
     */
    public ProgressBar getProgressBar()
    {
        return ( progressBar );
    }
    
    /**
     * @return the value of getProgressBar().getHUD().
     */
    public HUD getHUD()
    {
        return ( progressBar.getHUD() );
    }
    
    /**
     * @see org.xith3d.ui.hud.HUD#getRenderPass()
     * 
     * @return the RenderPass assotiated to the HUD
     */
    public RenderPass getRenderPass()
    {
        return ( getHUD().getRenderPass() );
    }
    
    /**
     * @return the current progress value.
     */
    public int getProgressValue()
    {
        if ( progressBar == null )
            throw new NullPointerException( "The ProgressBar Widget is invalid" );
        
        return ( progressBar.getValue() );
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public int getMaxProgressValue()
    {
        if ( progressBar == null )
            throw new NullPointerException( "The ProgressBar Widget is invalid" );
        
        return ( progressBar.getMaxValue() );
    }
    
    /**
     * @return the current progress caption.
     */
    public String getProgressCaption()
    {
        if ( captionLabel == null )
            throw new NullPointerException( "The caption label Widget is invalid" );
        
        return ( captionLabel.getText() );
    }
    
    /**
     * Adds all LoadingScreen Widgets to the given WidgetContainer.
     * 
     * @param container the WidgetContainer to place them on
     */
    public void attach( WidgetContainer container )
    {
        if ( backgroundImage != null )
        {
            container.addWidget( backgroundImage );
        }
        
        container.addWidget( progressBar, ( container.getResX() - progressBar.getWidth() ) / 2f, ( container.getResY() - progressBar.getHeight() ) / 2f );
        
        if ( captionLabel != null )
        {
            final float labelLocX = progressBar.getLeft();
            final float labelLocY = progressBar.getTop() - ( progressBar.getHeight() * 0.1f ) - captionLabel.getHeight();
            
            container.addWidget( captionLabel, labelLocX, labelLocY );
        }
    }
    
    /**
     * Removes all LoadingScreen Widgets from their WidgetContainer.
     */
    public void detach()
    {
        HUD hud = progressBar.getHUD();
        
        if ( ( backgroundImage != null ) && ( backgroundImage.getContainer() != null ) )
        {
            backgroundImage.detach();
        }
        
        if ( ( progressBar != null ) && ( progressBar.getContainer() != null ) )
        {
            progressBar.detach();
        }
        
        if ( ( captionLabel != null ) && ( captionLabel.getContainer() != null ) )
        {
            captionLabel.detach();
        }
        
        if ( hud != null )
        {
            hud.detach();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void init( int maxValue, String caption, Texture2D backgroundTexture )
    {
        if ( maxValue >= 0 )
            getProgressBar().setMaxValue( maxValue );
        else if ( ( maxValue < 0 ) && ( getProgressBar().getMaxValue() < -maxValue ) )
            getProgressBar().setMaxValue( -maxValue );
        
        //getProgressBar().setValue( 0 );
        
        if ( ( caption != null ) && ( captionLabel != null ) )
            captionLabel.setText( caption );
        
        if ( ( backgroundTexture != null ) && ( backgroundImage != null ) )
            backgroundImage.setTexture( backgroundTexture );
        
        if ( renderLoopController != null )
            renderLoopController.nextFrame();
    }
    
    /**
     * {@inheritDoc}
     */
    public final void init( int maxValue, String caption, String backgroundTexture )
    {
        init( maxValue, caption, HUDTextureUtils.getTextureOrNull( backgroundTexture, true ) );
    }
    
    /**
     * Updates all the contents of the {@link LoadingScreen}.
     * 
     * @param value
     * @param maxValue
     * @param caption
     * @param backgroundTexture
     */
    protected void updateContent( int value, int maxValue, String caption, Texture2D backgroundTexture )
    {
        if ( progressBar != null )
            progressBar.setValue( value );
        
        if ( ( caption != null ) && ( captionLabel != null ) )
            captionLabel.setText( caption );
        
        if ( ( backgroundTexture != null ) && ( backgroundImage != null ) )
            backgroundImage.setTexture( backgroundTexture );
    }
    
    /**
     * {@inheritDoc}
     */
    public void update( int incValue, String caption, Texture2D backgroundTexture )
    {
        updateContent( getProgressValue() + incValue, progressBar.getMaxValue(), caption, backgroundTexture );
        
        if ( renderLoopController != null )
            renderLoopController.nextFrame();
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue, Texture2D backgroundTexture )
    {
        update( incValue, (String)null, backgroundTexture );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( Texture2D backgroundTexture )
    {
        update( +1, (String)null, backgroundTexture );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void updateOnly( Texture2D backgroundTexture )
    {
        update( 0, (String)null, backgroundTexture );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue, String caption, String backgroundTexture )
    {
        update( incValue, caption, HUDTextureUtils.getTexture( backgroundTexture, true ) );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue, String caption )
    {
        update( incValue, caption, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( int incValue )
    {
        update( incValue, (String)null, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void updateOnly( String caption )
    {
        update( 0, caption, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update( String caption )
    {
        update( +1, caption, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void update()
    {
        update( +1, (String)null, (Texture2D)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public void beforeAnyResourceLoaded( ResourceLoader resLoader ) {}
    
    /**
     * {@inheritDoc}
     */
    public void beforeResourceBundleLoaded( ResourceLoader resLoader, Class<? extends ResourceRequest> bundleType ) {}
    
    /**
     * {@inheritDoc}
     */
    public void afterResourceBundleLoaded( ResourceLoader resLoader, Class<? extends ResourceRequest> bundleType ) {}
    
    /**
     * {@inheritDoc}
     */
    public void beforeResourceLoaded( ResourceLoader resLoader, ResourceRequest request ) {}
    
    /**
     * {@inheritDoc}
     */
    public void afterResourceLoaded( ResourceLoader resLoader, ResourceRequest request, Object resource ) {}
    
    /**
     * {@inheritDoc}
     */
    public void afterAllResourceLoaded( ResourceLoader resLoader ) {}
    
    /**
     * Creates a new LoadingScreen with the given parameters.
     * 
     * @param width the width of the backgroundImage
     * @param height the height of the backgroundImage
     * @param backgroundTexture the Texture for the backgroundImage
     * @param pbDesc description of the ProgressBar
     * @param maxValue maximum value for the ProgressBar
     * @param labelDesc description of the caption label Widget
     * @param initialCaption initial caption
     */
    public LoadingScreen( float width, float height, Texture2D backgroundTexture, ProgressBar.Description pbDesc, int maxValue, Label.Description labelDesc, String initialCaption )
    {
        if ( backgroundTexture != null )
        {
            this.backgroundImage = new Image( width, height, backgroundTexture );
        }
        
        float pbWidth = width * 0.8f;
        float pbHeight = height * 0.075f;
        if ( pbDesc == null )
            pbDesc = HUD.getTheme().getProgressBarDescription();
        this.progressBar = new ProgressBar( pbWidth, pbHeight, 0, maxValue, pbDesc );
        
        if ( labelDesc == null )
        {
            labelDesc = HUD.getTheme().getLabelDescription();
            labelDesc.setFont( labelDesc.getFont( false ).derive( HUDFont.FontStyle.BOLD, 24 ), false );
            labelDesc.setFontColor( Colorf.WHITE, false );
        }
        this.captionLabel = new Label( pbWidth, pbHeight, initialCaption, labelDesc );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.
     * 
     * @param width the width of the backgroundImage
     * @param height the height of the backgroundImage
     * @param backgroundTexture the Texture for the backgroundImage
     * @param pbDesc description of the ProgressBar
     * @param maxValue maximum value for the ProgressBar
     * @param labelDesc description of the caption label Widget
     * @param initialCaption initial caption
     */
    public LoadingScreen( float width, float height, String backgroundTexture, ProgressBar.Description pbDesc, int maxValue, Label.Description labelDesc, String initialCaption )
    {
        this( width, height, ( backgroundTexture != null ? HUDTextureUtils.getTexture( backgroundTexture, true ) : null ), pbDesc, maxValue, labelDesc, initialCaption );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.
     * 
     * @param size the size of the backgroundImage
     * @param backgroundTexture the Texture for the backgroundImage
     * @param pbDesc description of the ProgressBar
     * @param maxValue maximum value for the ProgressBar
     * @param labelDesc description of the caption label Widget
     * @param initialCaption initial caption
     */
    public LoadingScreen( Sized2fRO size, Texture2D backgroundTexture, ProgressBar.Description pbDesc, int maxValue, Label.Description labelDesc, String initialCaption )
    {
        this( size.getWidth(), size.getHeight(), backgroundTexture, pbDesc, maxValue, labelDesc, initialCaption );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.
     * 
     * @param size the size of the backgroundImage
     * @param backgroundTexture the Texture for the backgroundImage
     * @param pbDesc description of the ProgressBar
     * @param maxValue maximum value for the ProgressBar
     * @param labelDesc description of the caption label Widget
     * @param initialCaption initial caption
     */
    public LoadingScreen( Sized2fRO size, String backgroundTexture, ProgressBar.Description pbDesc, int maxValue, Label.Description labelDesc, String initialCaption )
    {
        this( size, ( backgroundTexture != null ? HUDTextureUtils.getTexture( backgroundTexture, true ) : null ), pbDesc, maxValue, labelDesc, initialCaption );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.
     * 
     * @param width the width of the backgroundImage
     * @param height the height of the backgroundImage
     * @param backgroundTexture the Texture for the backgroundImage
     * @param maxValue maximum value for the ProgressBar
     * @param initialCaption initial caption
     */
    public LoadingScreen( float width, float height, Texture2D backgroundTexture, int maxValue, String initialCaption )
    {
        this( new Dim2f( width, height ), backgroundTexture, (ProgressBar.Description)null, maxValue, (Label.Description)null, initialCaption );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.
     * 
     * @param width the width of the backgroundImage
     * @param height the height of the backgroundImage
     * @param backgroundTexture the Texture for the backgroundImage
     * @param maxValue maximum value for the ProgressBar
     * @param initialCaption initial caption
     */
    public LoadingScreen( float width, float height, String backgroundTexture, int maxValue, String initialCaption )
    {
        this( new Dim2f( width, height ), ( backgroundTexture != null ? HUDTextureUtils.getTexture( backgroundTexture, true ) : null ), (ProgressBar.Description)null, maxValue, (Label.Description)null, initialCaption );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.<br>
     * Implicitly creates a new HUD and places the LoadingScreen on it.
     * 
     * @param canvas the canvas to create the HUD on
     * @param backgroundTexture the Texture for the backgroundImage
     * @param pbDesc description of the ProgressBar
     * @param maxValue maximum value for the ProgressBar
     * @param labelDesc description of the caption label Widget
     * @param initialCaption initial caption
     */
    public static LoadingScreen createWithHUD( Sized2iRO canvas, Texture2D backgroundTexture, ProgressBar.Description pbDesc, int maxValue, Label.Description labelDesc, String initialCaption )
    {
        HUD hud = new HUD( canvas );
        
        LoadingScreen ls = new LoadingScreen( hud.getResX(), hud.getResY(), backgroundTexture, pbDesc, maxValue, labelDesc, initialCaption );
        
        ls.attach( hud.getContentPane() );
        
        return ( ls );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.<br>
     * Implicitly creates a new HUD and places the LoadingScreen on it.
     * 
     * @param canvas the canvas to create the HUD on
     * @param backgroundTexture the Texture for the backgroundImage
     * @param pbDesc description of the ProgressBar
     * @param maxValue maximum value for the ProgressBar
     * @param labelDesc description of the caption label Widget
     * @param initialCaption initial caption
     */
    public static LoadingScreen createWithHUD( Sized2iRO canvas, String backgroundTexture, ProgressBar.Description pbDesc, int maxValue, Label.Description labelDesc, String initialCaption )
    {
        HUD hud = new HUD( canvas );
        
        LoadingScreen ls = new LoadingScreen( hud.getResX(), hud.getResY(), backgroundTexture, pbDesc, maxValue, labelDesc, initialCaption );
        
        ls.attach( hud.getContentPane() );
        
        return ( ls );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.<br>
     * Implicitly creates a new HUD and places the LoadingScreen on it.
     * 
     * @param canvas the canvas to create the HUD on
     * @param backgroundTexture the Texture for the backgroundImage
     * @param maxValue maximum value for the ProgressBar
     * @param initialCaption initial caption
     */
    public static LoadingScreen createWithHUD( Sized2iRO canvas, Texture2D backgroundTexture, int maxValue, String initialCaption )
    {
        HUD hud = new HUD( canvas );
        
        LoadingScreen ls = new LoadingScreen( hud.getResX(), hud.getResY(), backgroundTexture, (ProgressBar.Description)null, maxValue, (Label.Description)null, initialCaption );
        
        ls.attach( hud.getContentPane() );
        
        return ( ls );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.<br>
     * Implicitly creates a new HUD and places the LoadingScreen on it.
     * 
     * @param canvas the canvas to create the HUD on
     * @param backgroundTexture the Texture for the backgroundImage
     * @param maxValue maximum value for the ProgressBar
     * @param initialCaption initial caption
     */
    public static LoadingScreen createWithHUD( Sized2iRO canvas, String backgroundTexture, int maxValue, String initialCaption )
    {
        HUD hud = new HUD( canvas );
        
        LoadingScreen ls = new LoadingScreen( hud.getResX(), hud.getResY(), backgroundTexture, (ProgressBar.Description)null, maxValue, (Label.Description)null, initialCaption );
        
        ls.attach( hud.getContentPane() );
        
        return ( ls );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.<br>
     * Implicitly creates a new HUD and places the LoadingScreen on it.
     * 
     * @param canvas the canvas to create the HUD on
     * @param backgroundTexture the Texture for the backgroundImage
     * @param maxValue maximum value for the ProgressBar
     */
    public static LoadingScreen createWithHUD( Sized2iRO canvas, Texture2D backgroundTexture, int maxValue )
    {
        return ( createWithHUD( canvas, backgroundTexture, maxValue, "" ) );
    }
    
    /**
     * Creates a new LoadingScreen with the given parameters.<br>
     * Implicitly creates a new HUD and places the LoadingScreen on it.
     * 
     * @param canvas the canvas to create the HUD on
     * @param backgroundTexture the Texture for the backgroundImage
     * @param maxValue maximum value for the ProgressBar
     */
    public static LoadingScreen createWithHUD( Sized2iRO canvas, String backgroundTexture, int maxValue )
    {
        return ( createWithHUD( canvas, backgroundTexture, maxValue, "" ) );
    }
}
