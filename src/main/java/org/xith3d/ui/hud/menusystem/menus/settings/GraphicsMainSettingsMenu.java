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
package org.xith3d.ui.hud.menusystem.menus.settings;

import java.util.HashMap;

import org.xith3d.render.config.CanvasConstructionInfo;
import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.ui.hud.base.AbstractList;
import org.xith3d.ui.hud.base.StateButton;
import org.xith3d.ui.hud.layout.ListLayout;
import org.xith3d.ui.hud.listeners.WidgetEventsReceiverAdapter;
import org.xith3d.ui.hud.menusystem.menus.MenuBase;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Checkbox;
import org.xith3d.ui.hud.widgets.ComboBox;
import org.xith3d.ui.hud.widgets.EmptyWidget;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * This is a settings menu for main graphics settings.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class GraphicsMainSettingsMenu extends MenuBase
{
    public static final String NAME = GraphicsMainSettingsMenu.class.getSimpleName();
    
    private final Label openGLLayerCaption;
    private final ComboBox openGLLayer;
    private final Label displayModeCaption;
    private final ComboBox displayMode;
    private final Label fsaaCaption;
    private final ComboBox fsaa;
    private final Checkbox vsync;
    private final Checkbox fullscreen;
    
    private final HashMap<String, DisplayMode> displayModeMap = new HashMap<String, DisplayMode>();
    private final HashMap<OpenGLLayer, DisplayMode[]> openGLLayerDisplayModesMap = new HashMap<OpenGLLayer, DisplayMode[]>();
    
    private final void refillDisplayModes( DisplayMode oldDisplayMode, OpenGLLayer openGLLayer )
    {
        this.displayMode.removeSelectionListener( eventsHandler );
        
        displayMode.clear();
        displayMode.addItems( openGLLayerDisplayModesMap.get( openGLLayer ) );
        
        final DisplayModeSelector dspModeSelector = DisplayModeSelector.getImplementation( openGLLayer );
        
        int found = 0;
        int selIndex = 0;
        for ( int i = 0; i < this.displayMode.getItemsCount(); i++ )
        {
            final String[] modeParts = this.displayMode.getItem( i ).toString().split( "x" );
            final int width = Integer.parseInt( modeParts[ 0 ] );
            final int height = Integer.parseInt( modeParts[ 1 ] );
            final int bpp;
            if ( modeParts[ 2 ].equals( "?" ) )
                bpp = 24;
            else
                bpp = Integer.parseInt( modeParts[ 2 ] );
            final int freq;
            if ( modeParts[ 3 ].equals( "?" ) )
                freq = 75;
            else
                freq = Integer.parseInt( modeParts[ 3 ] );
            
            switch ( found )
            {
                case 0:
                {
                    final DisplayMode dm0 = dspModeSelector.getBestMode( width, height );
                    if ( dm0.getWidth() == oldDisplayMode.getWidth() && dm0.getHeight() == oldDisplayMode.getHeight() )
                    {
                        selIndex = i;
                        found = 1;
                    }
                    
                    break;
                }
                    
                case 1:
                {
                    final DisplayMode dm1 = dspModeSelector.getBestMode( width, height, bpp );
                    if ( dm1.getWidth() == oldDisplayMode.getWidth() && dm1.getHeight() == oldDisplayMode.getHeight() && dm1.getBPP() == oldDisplayMode.getBPP() )
                    {
                        selIndex = i;
                        found = 2;
                    }
                    
                    break;
                }
                    
                case 2:
                {
                    final DisplayMode dm2 = dspModeSelector.getBestMode( width, height, bpp, freq );
                    if ( dm2.getWidth() == oldDisplayMode.getWidth() && dm2.getHeight() == oldDisplayMode.getHeight() && dm2.getBPP() == oldDisplayMode.getBPP() && dm2.getFrequency() == oldDisplayMode.getFrequency() )
                    {
                        selIndex = i;
                    }
                    
                    break;
                }
            }
        }
        
        this.displayMode.addSelectionListener( eventsHandler );
        
        if ( this.displayMode.getItemsCount() > 0 )
            this.displayMode.setSelectedIndex( selIndex );
    }
    
    public void setOpenGLLayer( OpenGLLayer openGLLayer )
    {
        final OpenGLLayer oldOpenGLLayer = getOpenGLLayer();
        final DisplayMode oldDisplayMode = getDisplayMode();
        
        if ( openGLLayer != oldOpenGLLayer )
        {
            this.openGLLayer.setSelectedIndex( this.openGLLayer.findItem( openGLLayer ) );
            
            refillDisplayModes( oldDisplayMode, openGLLayer );
        }
    }
    
    public final OpenGLLayer getOpenGLLayer()
    {
        return ( (OpenGLLayer)this.openGLLayer.getSelectedItem() );
    }
    
    public void setDisplayMode( DisplayMode displayMode )
    {
        this.displayMode.setSelectedIndex( this.displayMode.findItem( displayMode.toLightString() ) );
    }
    
    public final DisplayMode getDisplayMode()
    {
        return ( displayModeMap.get( this.displayMode.getSelectedItem().toString() ) );
    }
    
    public void setFSAA( FSAA fsaa )
    {
        this.fsaa.setSelectedIndex( this.fsaa.findItem( fsaa ) );
    }
    
    public final FSAA getFSAA()
    {
        return ( (FSAA)this.fsaa.getSelectedItem() );
    }
    
    public void setVSync( boolean vsync )
    {
        this.vsync.setState( vsync );
    }
    
    public final boolean getVSync()
    {
        return ( this.vsync.getState() );
    }
    
    public void setFullscreenMode( FullscreenMode fullscreen )
    {
        this.fullscreen.setState( fullscreen.isFullscreen() );
    }
    
    public final FullscreenMode getFullscreenMode()
    {
        return ( this.fullscreen.getState() ? FullscreenMode.FULLSCREEN : FullscreenMode.WINDOWED );
    }
    
    /**
     * Applies the {@link CanvasConstructionInfo} to this settings menu.
     * 
     * @param canvasInfo
     */
    public void applyConfig( CanvasConstructionInfo canvasInfo )
    {
        setOpenGLLayer( canvasInfo.getOpenGLLayer() );
        setDisplayMode( canvasInfo.getDisplayMode() );
        setFSAA( canvasInfo.getFSAAMode() );
        setVSync( canvasInfo.isVSyncEnabled() );
        setFullscreenMode( canvasInfo.getFullscreenMode() );
    }
    
    /**
     * Reads the relevant settings from this settings menu
     * and applies it to the {@link CanvasConstructionInfo}.
     * 
     * @param canvasInfo
     */
    public void extractConfig( CanvasConstructionInfo canvasInfo )
    {
        canvasInfo.setOpenGLLayer( getOpenGLLayer() );
        canvasInfo.setDisplayMode( getDisplayMode() );
        canvasInfo.setFSAAMode( getFSAA() );
        canvasInfo.setVSyncEnabled( getVSync() );
        canvasInfo.setFullscreenMode( getFullscreenMode() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected float getPickHeight()
    {
        return ( getHeight() + 100f ); // add a little to the pick-height to make all the ComboBoxes reachable.
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initWidgets()
    {
        addWidget( openGLLayerCaption );
        addWidget( openGLLayer );
        
        addWidget( new EmptyWidget( 0f, 10f ) );
        
        addWidget( displayModeCaption );
        addWidget( displayMode );
        
        addWidget( new EmptyWidget( 0f, 10f ) );
        
        addWidget( fsaaCaption );
        addWidget( fsaa );
        
        addWidget( new EmptyWidget( 0f, 10f ) );
        
        addWidget( vsync );
        
        addWidget( new EmptyWidget( 0f, 10f ) );
        
        addWidget( fullscreen );
    }
    
    private final WidgetEventsReceiverAdapter eventsHandler = new WidgetEventsReceiverAdapter()
    {
        @Override
        public void onListSelectionChanged( AbstractList list, Object oldSelectedItem, Object newSelectedItem, int oldSelectedIndex, int newSelectedIndex )
        {
            if ( list == openGLLayer )
            {
                final DisplayMode oldDisplayMode = displayModeMap.get( String.valueOf( oldSelectedItem ) + "-" + displayMode.getSelectedItem() );
                final OpenGLLayer ogl = (OpenGLLayer)newSelectedItem;
                
                refillDisplayModes( oldDisplayMode, ogl );
                
                getMenuGroup().fireOnSettingChanged( GraphicsMainSettingsMenu.this, "OpenGLLayer", getOpenGLLayer() );
            }
            else if ( list == displayMode )
            {
                getMenuGroup().fireOnSettingChanged( GraphicsMainSettingsMenu.this, "DisplayMode", getDisplayMode() );
            }
            else if ( list == fsaa )
            {
                getMenuGroup().fireOnSettingChanged( GraphicsMainSettingsMenu.this, "FSAA", getFSAA() );
            }
        }
        
        @Override
        public void onButtonStateChanged( StateButton stateButton, boolean state, Object userObject )
        {
            if ( stateButton == vsync )
            {
                getMenuGroup().fireOnSettingChanged( GraphicsMainSettingsMenu.this, "vsync", getVSync() );
            }
            else if ( stateButton == fullscreen )
            {
                getMenuGroup().fireOnSettingChanged( GraphicsMainSettingsMenu.this, "fullscreenMode", getFullscreenMode() );
            }
        }
    };
    
    private final ComboBox createDisplayModeCombo( OpenGLLayer oglLayer )
    {
        ComboBox cb = ComboBox.newTextCombo( 0f, 20f );
        
        OpenGLLayer[] ogls = new OpenGLLayer[] { OpenGLLayer.JOGL_AWT, OpenGLLayer.LWJGL };
        for ( OpenGLLayer ogl: ogls )
        {
            DisplayMode[] displayModes = DisplayModeSelector.getImplementation( ogl ).getAvailableModes();
            
            for ( DisplayMode displayMode: displayModes )
            {
                this.displayModeMap.put( ogl.toString() + "-" + displayMode.toLightString(), displayMode );
                
                if ( ogl == oglLayer )
                {
                    cb.addItem( displayMode );
                }
            }
            
            openGLLayerDisplayModesMap.put( ogl, displayModes );
        }
        
        return ( cb );
    }
    
    public GraphicsMainSettingsMenu( float width, float height )
    {
        super( width, height, GraphicsMainSettingsMenu.NAME, new String[] { "save" } );
        
        ListLayout layout = new ListLayout( ListLayout.Orientation.VERTICAL, 2f, 10f, 10f, 10f, 10f );
        layout.setAlignment( ListLayout.Alignment.CENTER_TOP );
        layout.setOtherSpanCalculated( true );
        this.setLayout( layout );
        
        HUDFont font = HUDFont.getFont( "Verdana", HUDFont.PLAIN, 12 );
        final OpenGLLayer defaultOGL = OpenGLLayer.JOGL_AWT;
        
        this.openGLLayerCaption = new Label( 0f, 0f, "OpenGLLayer", font, TextAlignment.BOTTOM_LEFT );
        this.openGLLayer = ComboBox.newTextCombo( 0f, 20f );
        openGLLayer.addItem( OpenGLLayer.JOGL_AWT );
        openGLLayer.addItem( OpenGLLayer.LWJGL );
        this.displayModeCaption = new Label( 0f, 0f, "Display-Mode", font, TextAlignment.BOTTOM_LEFT );
        this.displayMode = createDisplayModeCombo( defaultOGL );
        this.fsaaCaption = new Label( 0f, 0f, "Full Scene Anti-Aliasing", font, TextAlignment.BOTTOM_LEFT );
        this.fsaa = ComboBox.newTextCombo( 0f, 20f );
        fsaa.addItem( FSAA.OFF );
        fsaa.addItem( FSAA.ON_2X );
        fsaa.addItem( FSAA.ON_4X );
        fsaa.addItem( FSAA.ON_8X );
        fsaa.addItem( FSAA.ON_16X );
        this.vsync = new Checkbox( 0f, 0f, "Vertical-Sync", font, null );
        this.fullscreen = new Checkbox( 0f, 0f, "Fullscreen", font, null );
        
        
        // Apply default settings...
        openGLLayer.setSelectedIndex( 0 );
        displayMode.setSelectedIndex( 0 );
        fsaa.setSelectedIndex( 0 );
        vsync.setState( true );
        fullscreen.setState( true );
        
        
        // add event handlers...
        openGLLayer.addSelectionListener( eventsHandler );
        displayMode.addSelectionListener( eventsHandler );
        fsaa.addSelectionListener( eventsHandler );
        vsync.addStateListener( eventsHandler );
        fullscreen.addStateListener( eventsHandler );
    }
    
    public GraphicsMainSettingsMenu( float width )
    {
        this( width, 0f );
        
        setMinimalHeight();
    }
}
