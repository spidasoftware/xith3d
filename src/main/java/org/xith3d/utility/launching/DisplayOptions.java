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
package org.xith3d.utility.launching;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import org.xith3d.render.config.DisplayMode;
import org.xith3d.render.config.DisplayModeSelector;
import org.xith3d.render.config.FSAA;
import org.xith3d.render.config.OpenGLLayer;
import org.xith3d.render.config.DisplayMode.FullscreenMode;
import org.xith3d.utility.awt.WindowHelper;

/**
 * This class can be used to select resolution, fullscreen and OpenGLLayer
 * and then run an application.
 * 
 * Extend this class and fill the EAST panel with additional information
 * to customize the dialog.
 * 
 * @author Marvin Froehlich (aka Qudus)
 * @author jeepndesert
 */
public abstract class DisplayOptions extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1493957380741174538L;
    
    protected OpenGLLayer currentOGLLayer;
    protected String currentDisplayMode;
    protected FullscreenMode currentFullscreenMode;
    protected boolean currentVSync;
    protected FSAA currentFSAA;
    
    private JComboBox oglSelector;
    private Map< OpenGLLayer, Integer > oglCBIndices;
    
    private JComboBox dspModeSelector;
    
    private JCheckBox fsCheck;
    private JCheckBox vsyncCheck;
    
    private JComboBox fsaaSelector;
    private Map< FSAA, Integer > fsaaCBIndices;
    
    private JButton startButton;
    
    private boolean isStartRequested = false;
    
    /**
     * @return a File reference to the folder, where the properties file is to
     *         be stored.
     */
    protected File getPropertiesFileFolder()
    {
        final File userHomeDir = new File( (String)System.getProperties().get( "user.home" ) );
        
        return ( new File( userHomeDir, ".xith3d" ) );
    }

    /**
     * @return the name of the file, where the properties are to be stored.
     */
    protected String getPropertiesFilename()
    {
        return ( "displayoptions_selection.properties" );
    }
    
    /**
     * @return the comment to write to the properties file
     */
    protected String getPropertiesComment()
    {
        return ( "Last selected items in the DisplayOptions" );
    }
    
    /**
     * @return the File reference to the properties file.
     * 
     * @param forcePathExistance if true, the path is created, if it doesn't exist
     */
    protected File findPropertiesFile( boolean forcePathExistance )
    {
        try
        {
            File xithTLPropsFile = null;
            
            File xithPropsDir = getPropertiesFileFolder();
            if ( !xithPropsDir.exists() )
            {
                if ( forcePathExistance )
                    xithPropsDir.mkdir();
            }
            
            if ( xithPropsDir.exists() )
            {
                xithTLPropsFile = new File( xithPropsDir, getPropertiesFilename() );
            }
            
            return ( xithTLPropsFile );
        }
        catch ( Throwable t )
        {
            //t.printStackTrace();
            return ( null );
        }
    }
    
    /**
     * Creates (and fills) the Properties object to save the current selections.
     * 
     * @return the created (and filled) Properties instance
     */
    protected Properties createProperties()
    {
        final Properties props = new Properties();
        
        props.setProperty( "displayoptions.openGLLayer", String.valueOf( getCurrentOGLLayer() ) );
        props.setProperty( "displayoptions.displayMode", getCurrentDisplayMode().toLightString() );
        props.setProperty( "displayoptions.fullscreen", String.valueOf( getCurrentFullscreenMode() ) );
        props.setProperty( "displayoptions.vsync", String.valueOf( getCurrentVSync() ) );
        props.setProperty( "displayoptions.fsaa", String.valueOf( getCurrentFSAA() ) );
        
        return ( props );
    }
    
    private void saveSelections()
    {
        try
        {
            final File propertiesFile = findPropertiesFile( true );
            
            if ( propertiesFile != null )
            {
                Properties props = createProperties();
                
                props.store( new BufferedOutputStream( new FileOutputStream( propertiesFile ) ), getPropertiesComment() );
            }
        }
        catch ( Throwable t )
        {
            //t.printStackTrace();
        }
    }
    
    /**
     * Loads the selection Properties from the given File.
     * 
     * @param propertiesFile the File to load the Properties from
     * 
     * @return the Properties instance with the last stored selections.
     * 
     * @throws IOException
     */
    private Properties loadProperties( File propertiesFile ) throws IOException
    {
        Properties props = new Properties();
        
        if ( ( propertiesFile != null ) && ( propertiesFile.exists() ) )
        {
            props.load( new BufferedInputStream( new FileInputStream( propertiesFile ) ) );
        }
        
        return ( props );
    }
    
    protected void restoreSelections( Properties props ) throws Throwable
    {
        setCurrentOGLLayer( OpenGLLayer.valueOf( props.getProperty( "displayoptions.openGLLayer", "LWJGL" ) ) );
        setCurrentDisplayMode( org.xith3d.render.config.DisplayMode.parseDisplayMode( getCurrentOGLLayer(), props.getProperty( "displayoptions.displayMode", "800X600x24x75" ) ) );
        setCurrentFullscreenMode( FullscreenMode.valueOf( props.getProperty( "displayoptions.fullscreen", "WINDOWED" ) ) );
        setCurrentVSync( Boolean.valueOf( props.getProperty( "displayoptions.vsync", "true" ) ) );
        setCurrentFSAA( FSAA.valueOf( props.getProperty( "displayoptions.fsaa", "OFF" ) ) );
    }
    
    private void restoreSelections()
    {
        try
        {
            final File propertiesFile = findPropertiesFile( false );
            
            if ( ( propertiesFile != null ) && ( propertiesFile.exists() ) )
            {
                final Properties props = loadProperties( propertiesFile );
                
                restoreSelections( props );
            }
        }
        catch ( Throwable t )
        {
            //t.printStackTrace();
        }
    }
    
    /**
     * This event method is executed, when the "Start"-Button is was clicked.
     * 
     * @return true, to indicate success.
     *         false to indicate an error (the DisplayOptions frame is re-shown)
     */
    protected abstract boolean onStartButtonClicked();
    
    /**
     * Defines, if the Start-Button is enabled or not.
     * 
     * @param enabled
     */
    protected void setStartButtonEnabled( boolean enabled )
    {
        startButton.setEnabled( enabled );
    }
    
    public void requestStart()
    {
        saveSelections();
        
        //isStartRequested = true;
        setVisible( false );
        onStartButtonClicked();
    }
    
    /**
     * aggretation of all resolution- and fullscreen- selections
     * and cancel- and start-button actions.
     */
    public void actionPerformed( ActionEvent e )
    {
        if ( e.getSource() instanceof JCheckBox )
        {
            if ( e.getActionCommand().startsWith( "FULLSCREEN::" ) )
            {
                // TODO: Add support for windowed mode!
                currentFullscreenMode = ( (JCheckBox)e.getSource() ).isSelected() ? FullscreenMode.FULLSCREEN : FullscreenMode.WINDOWED;
            }
            else if ( e.getActionCommand().startsWith( "VSYNC::" ) )
            {
                currentVSync = ( (JCheckBox)e.getSource() ).isSelected();
            }
        }
        else if ( e.getSource() instanceof JComboBox )
        {
            if ( e.getActionCommand().startsWith( "OPENGL_LAYER::" ) )
            {
                final JComboBox cb = (JComboBox)e.getSource();
                final Object selItem = cb.getSelectedItem();
                
                currentOGLLayer = OpenGLLayer.valueOf( (String)selItem );
                
                refillDisplayModeSelector();
            }
            if ( e.getActionCommand().startsWith( "DISPLAY_MODE::" ) )
            {
                final JComboBox cb = (JComboBox)e.getSource();
                final Object selItem = cb.getSelectedItem();
                
                currentDisplayMode = (String)selItem;
            }
            if ( e.getActionCommand().startsWith( "FSAA::" ) )
            {
                final JComboBox cb = (JComboBox)e.getSource();
                final Object selItem = cb.getSelectedItem();
                
                currentFSAA = FSAA.valueOf( ( (String)selItem ).substring( 6 ) );
            }
        }
        else if ( e.getSource() instanceof JButton )
        {
            if ( e.getActionCommand().equals( "CANCEL" ) )
            {
                this.setVisible( false );
                
                System.exit( 0 );
            }
            else if ( e.getActionCommand().equals( "START" ) )
            {
                requestStart();
            }
        }
    }
    
    private static final String[] getValidOpenGLLayersAsString()
    {
        int n = OpenGLLayer.values().length;
        String[] tmpItems = new String[ n ];
        int j = 0;
        for ( int i = 0; i < n; i++ )
        {
            OpenGLLayer ogl = OpenGLLayer.values()[i];
            //if ( ogl.isStandaloneCapable() )
            if ( ogl != OpenGLLayer.JOGL_SWT )
                tmpItems[j++] = ogl.name();
        }
        String[] items = new String[ j ];
        System.arraycopy( tmpItems, 0, items, 0, j );
        
        return ( items );
    }
    
    /**
     * Creates a new JComponent with two RadioButtons to select the OpenGLLayer.
     * 
     * @return the new JComponent
     */
    protected JComponent createOpenGLLayerSelector()
    {
        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        panel.setMinimumSize( new Dimension( 1, 20 ) );
        panel.setPreferredSize( new Dimension( 1, 20 ) );
        panel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 20 ) );
        
        String[] items = getValidOpenGLLayersAsString();
        
        this.oglSelector = new JComboBox( items );
        oglSelector.setSelectedIndex( 0 );
        oglSelector.setActionCommand( "OPENGL_LAYER::" );
        oglSelector.addActionListener( this );
        
        oglCBIndices = new HashMap< OpenGLLayer, Integer >();
        
        for ( int i = 0; i < items.length; i++ )
        {
            oglCBIndices.put( OpenGLLayer.valueOf( items[i] ), i );
        }
        
        panel.add( oglSelector );
        
        return ( panel );
    }
    
    /**
     * Sets the current selected OpenGLLayer.
     * 
     * @param oglLayer
     */
    public void setCurrentOGLLayer( OpenGLLayer oglLayer )
    {
        int index = oglCBIndices.get( oglLayer );
        oglSelector.setSelectedIndex( index );
        currentOGLLayer = oglLayer;
    }
    
    /**
     * @return the current selected OpenGLLayer.
     */
    public OpenGLLayer getCurrentOGLLayer()
    {
        return ( currentOGLLayer );
    }
    
    protected void refillDisplayModeSelector()
    {
        final DisplayMode currDspMode = getCurrentDisplayMode();
        
        org.xith3d.render.config.DisplayMode[] displayModes = DisplayModeSelector.getImplementation( getCurrentOGLLayer() ).getAvailableModes();
        Vector< String > modeStrings = new Vector< String >();
        
        for ( org.xith3d.render.config.DisplayMode displayMode: displayModes )
        {
            modeStrings.add( displayMode.toLightString() );
        }
        
        dspModeSelector.setModel( new DefaultComboBoxModel( modeStrings ) );
        
        setCurrentDisplayMode( currDspMode );
    }
    
    /**
     * Creates a new JComponent with the DisplayMode selection controls.
     * 
     * @return the created JPanel
     */
    protected final JComponent createDisplayModeSelector()
    {
        this.dspModeSelector = new JComboBox( new String[] {} );
        dspModeSelector.setMinimumSize( new Dimension( 1, 20 ) );
        dspModeSelector.setPreferredSize( new Dimension( 1, 20 ) );
        dspModeSelector.setMaximumSize( new Dimension( Integer.MAX_VALUE, 20 ) );
        dspModeSelector.setActionCommand( "DISPLAY_MODE::" );
        dspModeSelector.addActionListener( this );
        
        refillDisplayModeSelector();
        
        return ( dspModeSelector );
    }
    
    /**
     * Sets the currently selected DisplayMode.
     * 
     * @param displayMode
     */
    public void setCurrentDisplayMode( DisplayMode displayMode )
    {
        int found = 0;
        int selIndex = 0;
        for ( int i = 0; i < dspModeSelector.getItemCount(); i++ )
        {
            final String[] modeParts = ( (String)dspModeSelector.getItemAt( i ) ).split( "x" );
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
                    final DisplayMode dm0 = DisplayModeSelector.getImplementation( getCurrentOGLLayer() ).getBestMode( width, height );
                    if ( dm0.getWidth() == displayMode.getWidth() && dm0.getHeight() == displayMode.getHeight() )
                    {
                        selIndex = i;
                        found = 1;
                    }
                    
                    break;
                }
                
                case 1:
                {
                    final DisplayMode dm1 = DisplayModeSelector.getImplementation( getCurrentOGLLayer() ).getBestMode( width, height, bpp );
                    if ( dm1.getWidth() == displayMode.getWidth() && dm1.getHeight() == displayMode.getHeight() && dm1.getBPP() == displayMode.getBPP() )
                    {
                        selIndex = i;
                        found = 2;
                    }
                    
                    break;
                }
                
                case 2:
                {
                    final DisplayMode dm2 = DisplayModeSelector.getImplementation( getCurrentOGLLayer() ).getBestMode( width, height, bpp, freq );
                    if ( dm2.getWidth() == displayMode.getWidth() && dm2.getHeight() == displayMode.getHeight() && dm2.getBPP() == displayMode.getBPP() && dm2.getFrequency() == displayMode.getFrequency() )
                    {
                        selIndex = i;
                    }
                    
                    break;
                }
            }
        }
        
        if ( dspModeSelector.getItemCount() > 0 )
            dspModeSelector.setSelectedIndex( selIndex );
        currentDisplayMode = displayMode.toLightString();
    }
    
    /**
     * @return the currently selected resolution.
     */
    public DisplayMode getCurrentDisplayMode()
    {
        final String[] modeParts = currentDisplayMode.split( "x" );
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
        
        return ( DisplayModeSelector.getImplementation( getCurrentOGLLayer() ).getBestMode( width, height, bpp, freq ) );
    }
    
    /**
     * Creates a new JComponent with a JCheckBox to select fullscreen.
     * 
     * @return the new JComponent
     */
    protected JComponent createFullscreenSelector()
    {
        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        panel.setMinimumSize( new Dimension( 1, 20 ) );
        panel.setPreferredSize( new Dimension( 1, 20 ) );
        panel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 20 ) );
        
        // TODO: Add support for windowed mode!
        fsCheck = new JCheckBox( "Fullscreen", currentFullscreenMode.isFullscreen() );
        fsCheck.setActionCommand( "FULLSCREEN::" );
        fsCheck.addActionListener( this );
        panel.add( fsCheck );
        
        return ( panel );
    }
    
    /**
     * Creates a new JComponent with a JCheckBox to select vsync.
     * 
     * @return the new JComponent
     */
    protected JComponent createVSyncSelector()
    {
        JPanel panel = new JPanel( new GridLayout( 1, 1 ) );
        panel.setMinimumSize( new Dimension( 1, 20 ) );
        panel.setPreferredSize( new Dimension( 1, 20 ) );
        panel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 20 ) );
        
        vsyncCheck = new JCheckBox( "V-Sync", currentVSync );
        vsyncCheck.setActionCommand( "VSYNC::" );
        vsyncCheck.addActionListener( this );
        panel.add( vsyncCheck );
        
        return ( panel );
    }
    
    /**
     * Changes the current state of the fullscreen selector checkbox.
     */
    public void setCurrentFullscreenMode( FullscreenMode fs )
    {
        fsCheck.setSelected( fs.isFullscreen() );
        currentFullscreenMode = fs;
    }
    
    /**
     * @return the current state of the fullscreen selector checkbox.
     */
    public FullscreenMode getCurrentFullscreenMode()
    {
        return ( currentFullscreenMode );
    }
    
    /**
     * Changes the current state of the fullscreen selector checkbox.
     */
    public void setCurrentVSync( boolean vsync )
    {
        vsyncCheck.setSelected( vsync );
        currentVSync = vsync;
    }
    
    /**
     * @return the current state of the vsync selector checkbox.
     */
    public boolean getCurrentVSync()
    {
        return ( currentVSync );
    }
    
    /**
     * Creates a new JComponent with a JCheckBox to select FSAA.
     * 
     * @return the new JComponent
     */
    protected JComponent createFSAASelector()
    {
        String[] items = new String[ FSAA.values().length ];
        for ( int i = 0; i < FSAA.values().length; i++ )
        {
            items[ i ] = "FSAA: " + FSAA.values()[ i ];
        }
        
        this.fsaaSelector = new JComboBox( items );
        fsaaSelector.setMinimumSize( new Dimension( 1, 20 ) );
        fsaaSelector.setPreferredSize( new Dimension( 1, 20 ) );
        fsaaSelector.setMaximumSize( new Dimension( Integer.MAX_VALUE, 20 ) );
        fsaaSelector.setActionCommand( "FSAA::" );
        fsaaSelector.addActionListener( this );
        
        fsaaCBIndices = new HashMap< FSAA, Integer >();
        int i = 0;
        for ( FSAA fsaa: FSAA.values() )
        {
            fsaaCBIndices.put( fsaa, i++ );
        }
        
        return ( fsaaSelector );
    }
    
    /**
     * Changes the current state of the FSAA selector checkbox.
     */
    public void setCurrentFSAA( FSAA fsaa )
    {
        int index = fsaaCBIndices.get( fsaa );
        fsaaSelector.setSelectedIndex( index );
        currentFSAA = fsaa;
    }
    
    /**
     * @return the current state of the FSAA selector checkbox.
     */
    public FSAA getCurrentFSAA()
    {
        return ( currentFSAA );
    }
    
    /**
     * Creates a new JComponent with the content for the NORTH (header).
     * 
     * @return the created JComponent
     */
    protected JComponent createNorthComponent()
    {
        return ( null );
    }
    
    /**
     * Creates a new JComponent with the content for the WEST-Panel.
     * 
     * @return the created JComponent
     */
    protected JComponent createCenterComponent()
    {
        return ( null );
    }
    
    /**
     * Creates a JComponent, that will be displayed in the WEST-Panel of the
     * DisplayOptions Frame. Return <i>null</i> to not display a component at
     * this place.
     * 
     * @return the created WEST-Component (or null for no component at this place)
     */
    protected JComponent createWestComponent()
    {
        return ( null );
    }
    
    /**
     * Creates a JComponent, that will be displayed in the NORTH-WEST of the
     * DisplayOptions Frame.<br>
     * By default this returns <i>null</i>.
     * 
     * @return the created NORTH-WEST-Component (or <i>null</i> for no Component)
     */
    protected JComponent createNorthWestComponent()
    {
        JPanel p = new JPanel();
        p.setLayout( new BoxLayout( p, BoxLayout.Y_AXIS ) );
        p.setBorder( new TitledBorder( new LineBorder( Color.BLACK, 2 ), "Configuration" ) );
        
        p.add( createOpenGLLayerSelector() );
        p.add( Box.createVerticalStrut( 5 ) );
        p.add( createDisplayModeSelector() );
        p.add( Box.createVerticalStrut( 5 ) );
        p.add( createFullscreenSelector() );
        p.add( Box.createVerticalStrut( 5 ) );
        p.add( createVSyncSelector() );
        p.add( Box.createVerticalStrut( 5 ) );
        p.add( createFSAASelector() );
        
        /*
        int height = 50;
        for ( Component comp: p.getComponents() )
        {
            height += comp.getPreferredSize().getHeight();
        }
        
        p.setMinimumSize( new Dimension( 150, height ) );
        p.setPreferredSize( new Dimension( 150, height ) );
        p.setMaximumSize( new Dimension( 150, height ) );
        */
        
        return ( p );
    }
    
    /**
     * Creates a JComponent, that will be displayed in the SOUTH-WEST of the
     * DisplayOptions Frame. Normally it displays a logo. Return <i>null</i>
     * to not display a component at this place.
     * 
     * @return the created SOUTH-WEST-Component (or <i>null</i> for no Component)
     */
    @SuppressWarnings( "serial" )
    protected JComponent createSouthWestComponent()
    {
        final URL location = this.getClass().getClassLoader().getResource( "resources/org/xith3d/pictures/Xith3D-Logo.png" );
        if ( location == null )
            return ( null );
        
        final ImageIcon icon = new ImageIcon( location );
        
        final double q = ( ( (double)( icon.getIconWidth() ) ) / ( (double)( icon.getIconHeight() ) ) );
        
        JComponent logo = new JComponent()
        {
            @Override
            public void paintComponent( Graphics g )
            {
                super.paintComponent( g );
                
                final int w = getWidth() - 6;
                final int h = (int)( w / q );
                
                g.drawImage( icon.getImage(), 3, getHeight() - 3 - h, w, h, this );
            }
        };
        logo.setMinimumSize( new Dimension( 0, 0 ) );
        logo.setPreferredSize( new Dimension( 150, 150 / (int)q ) );
        logo.setMaximumSize( new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE ) );
        
        return ( logo );
    }
    
    /**
     * Creates a JComponent, that will be displayed in the EAST-Panel of the
     * DisplayOptions Frame. Return <i>null</i> to not display a component at
     * this place.
     * 
     * @return the created EAST-Component (or null for no component at this place)
     */
    protected JComponent createEastComponent()
    {
        return ( null );
    }
    
    /**
     * Creates a JComponent, that will be displayed in the EAST-Panel of the
     * DisplayOptions Frame. Return <i>null</i> to not display a component at
     * this place.
     * 
     * @return the created EAST-Component (or null for no component at this place)
     */
    protected JComponent createNorthEastComponent()
    {
        return ( null );
    }
    
    /**
     * Creates a JComponent, that will be displayed in the EAST-Panel of the
     * DisplayOptions Frame. Return <i>null</i> to not display a component at
     * this place.
     * 
     * @return the created EAST-Component (or null for no component at this place)
     */
    protected JComponent createSouthEastComponent()
    {
        return ( null );
    }
    
    /**
     * Creates a JComponent, that will be displayed in the SOUTH-Panel of the
     * DisplayOptions Frame. Return <i>null</i> to not display a component at
     * this place.
     * 
     * @return the created SOUTH-Component (or null for no component at this place)
     */
    protected JComponent createSouthComponent()
    {
        return ( null );
    }
    
    /**
     * Creates a JComponent containing all the bottom-Buttons
     * (like Start and Cancel).
     * 
     * @return the JComponent with the bottom-Buttons
     */
    protected JComponent createFooterComponent()
    {
        final int height = 41;
        
        FlowLayout fl = new FlowLayout();
        fl.setAlignment( FlowLayout.RIGHT );
        JPanel comp = new JPanel( fl );
        comp.setMinimumSize( new Dimension( 0, height ) );
        comp.setPreferredSize( new Dimension( Integer.MAX_VALUE, height ) );
        comp.setMaximumSize( new Dimension( Integer.MAX_VALUE, height ) );
        comp.setBorder( new EmptyBorder( 3, 3, 3, 3 ) );
        
        startButton = new JButton( "Start" );
        startButton.setActionCommand( "START" );
        startButton.addActionListener( this );
        setStartButtonEnabled( true );
        comp.add( startButton, null );
        
        JButton cancelButton = new JButton( "Close" );
        cancelButton.setActionCommand( "CANCEL" );
        cancelButton.addActionListener( this );
        comp.add( cancelButton, null );
        
        return ( comp );
    }
    
    /**
     * Sets the icon of this JFrame.
     */
    protected void setIcon()
    {
        try
        {
            BufferedImage icon = ImageIO.read( this.getClass().getClassLoader().getResource( "resources/org/xith3d/pictures/Xith3D-Icon.png" ) );
            
            this.setIconImage( icon );
        }
        catch ( Exception e )
        {
            //e.printStackTrace();
        }
    }
    
    /**
     * Builds the whole GUI of the DisplayOptions Frame.
     * 
     * @return the GUI's size
     */
    protected Dimension buildGUI()
    {
        Dimension minSize = new Dimension( 100, 100 );
        Dimension prefSize = new Dimension( 100, 200 );
        Dimension maxSize = new Dimension( Integer.MAX_VALUE, Integer.MAX_VALUE );
        
        this.setLayout( new BorderLayout() );
        
        GridBagLayout gbl = new GridBagLayout();
        GridBagLayout westGbl = new GridBagLayout();
        GridBagLayout eastGbl = new GridBagLayout();
        GridBagConstraints gbc;
        
        final JComponent north = createNorthComponent();
        final JComponent center = createCenterComponent();
        final JComponent west = createWestComponent();
        final JComponent east = createEastComponent();
        final JComponent south = createSouthComponent();
        final JComponent footer = createFooterComponent();
        
        final JPanel p0 = new JPanel( new BorderLayout() );
        final JPanel p1 = new JPanel( gbl );
        
        this.getContentPane().add( p0, BorderLayout.CENTER );
        p0.add( p1, BorderLayout.CENTER );
        
        if ( north != null )
        {
            p0.add( north, BorderLayout.NORTH );
            minSize.height += north.getMinimumSize().height;
            prefSize.height += north.getPreferredSize().height;
        }
        
        if ( center != null )
        {
            //p0.add( center, BorderLayout.CENTER );
            
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbl.setConstraints( center, gbc );
            p1.add( center );
            
            if ( center.isMinimumSizeSet() )
            {
                minSize.width += -100 + center.getMinimumSize().width;
                minSize.height += -100 + center.getMinimumSize().height;
            }
            if ( center.isPreferredSizeSet() )
            {
                prefSize.width += -100 + center.getPreferredSize().width;
                prefSize.height += -200 + center.getPreferredSize().height;
            }
        }
        
        if ( west != null )
        {
            //p0.add( west, BorderLayout.WEST );
            
            gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.WEST;
            gbl.setConstraints( west, gbc );
            p1.add( west );
            
            minSize.width += west.getMinimumSize().width;
            prefSize.width += west.getPreferredSize().width;
        }
        else
        {
            final JComponent northWest = createNorthWestComponent();
            final JComponent southWest = createSouthWestComponent();
            
            int mw1 = 0, mw2 = 0;
            int pw1 = 0, pw2 = 0;
            
            JPanel westPanel = null;
            if ( ( northWest != null ) || ( southWest != null ) )
            {
                westPanel = new JPanel();
                //westPanel.setLayout( new BoxLayout( westPanel, BoxLayout.Y_AXIS ) );
                //p1.add( westPanel, BorderLayout.WEST );
                
                westGbl = new GridBagLayout();
                westPanel.setLayout( westGbl );
                
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.anchor = GridBagConstraints.NORTHWEST;
                gbl.setConstraints( westPanel, gbc );
                p1.add( westPanel );
            }
            
            if ( northWest != null )
            {
                //westPanel.add( northWest );
                
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.NORTHWEST;
                westGbl.setConstraints( northWest, gbc );
                westPanel.add( northWest );
                
                mw1 = northWest.getMinimumSize().width;
                pw1 = northWest.getPreferredSize().width;
            }
            
            if ( westPanel != null )
            {
                //westPanel.add( Box.createVerticalGlue() );
            }
            
            if ( southWest != null )
            {
                //westPanel.add( southWest );
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.SOUTHWEST;
                westGbl.setConstraints( southWest, gbc );
                westPanel.add( southWest );
                
                mw2 = southWest.getMinimumSize().width;
                pw2 = southWest.getPreferredSize().width;
            }
            
            minSize.width += Math.max( mw1, mw2 );
            prefSize.width += Math.max( pw1, pw2 );
        }
        
        if ( east != null )
        {
            //p0.add( east, BorderLayout.EAST );
            
            gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.anchor = GridBagConstraints.EAST;
            gbl.setConstraints( east, gbc );
            p1.add( east );
            
            minSize.width += east.getMinimumSize().width;
            prefSize.width += east.getPreferredSize().width;
        }
        else
        {
            final JComponent northEast = createNorthEastComponent();
            final JComponent southEast = createSouthEastComponent();
            
            int mw1 = 0, mw2 = 0;
            int pw1 = 0, pw2 = 0;
            
            JPanel eastPanel = null;
            if ( ( northEast != null ) || ( southEast != null ) )
            {
                eastPanel = new JPanel();
                //eastPanel.setLayout( new BoxLayout( eastPanel, BoxLayout.Y_AXIS ) );
                //p0.add( eastPanel, BorderLayout.EAST );
                
                eastGbl = new GridBagLayout();
                eastPanel.setLayout( eastGbl );
                
                gbc = new GridBagConstraints();
                gbc.gridx = 2;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.anchor = GridBagConstraints.NORTHEAST;
                gbl.setConstraints( eastPanel, gbc );
                p1.add( eastPanel );
            }
            
            if ( northEast != null )
            {
                //eastPanel.add( northEast );
                
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.NORTHEAST;
                eastGbl.setConstraints( northEast, gbc );
                eastPanel.add( northEast );
                
                mw1 = northEast.getMinimumSize().width;
                pw1 = northEast.getPreferredSize().width;
            }
            
            if ( eastPanel != null )
            {
                //eastPanel.add( Box.createVerticalGlue() );
            }
            
            if ( southEast != null )
            {
                //eastPanel.add( southEast );
                
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.SOUTHEAST;
                eastGbl.setConstraints( southEast, gbc );
                eastPanel.add( southEast );
                
                mw2 = southEast.getMinimumSize().width;
                pw2 = southEast.getPreferredSize().width;
            }
            
            minSize.width += Math.max( mw1, mw2 );
            prefSize.width += Math.max( pw1, pw2 );
        }
        
        if ( south != null )
        {
            p0.add( south, BorderLayout.SOUTH );
            minSize.height += south.getMinimumSize().height;
            prefSize.height += south.getPreferredSize().height;
        }
        
        if ( footer != null )
        {
            this.getContentPane().add( footer, BorderLayout.SOUTH );
            minSize.height += footer.getMinimumSize().height;
            prefSize.height += footer.getPreferredSize().height;
        }
        
        this.setMinimumSize( minSize );
        this.setPreferredSize( prefSize );
        this.setMaximumSize( maxSize );
        
        return ( prefSize );
    }
    
    /**
     * Waits for the start Button to be clicked in this Thread.
     */
    protected void waitForStart()
    {
        isStartRequested = false;
        
        setVisible( true );
        this.setAlwaysOnTop( true );
        this.toFront();
        this.requestFocus();
        
        while ( !isStartRequested )
        {
            try
            {
                Thread.sleep( 50L );
            }
            catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
        
        this.setVisible( false );
        
        if ( !onStartButtonClicked() )
        {
            waitForStart();
        }
    }
    
    protected DisplayOptions( String title, OpenGLLayer oglLayer, DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa )
    {
        super( title );
        this.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        this.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing( WindowEvent e )
            {
                System.exit( 0 );
            }
        } );
        
        currentOGLLayer = oglLayer;
        currentDisplayMode = displayMode.toLightString();
        currentFullscreenMode = fullscreen;
        currentVSync = vsync;
        currentFSAA = fsaa;
        
        setIcon();
        this.setSize( buildGUI() );
        WindowHelper.center( this );
        
        restoreSelections();
        
        waitForStart();
    }
    
    protected DisplayOptions( String title, OpenGLLayer oglLayer, DisplayMode displayMode, FullscreenMode fullscreen, FSAA fsaa )
    {
        this( title, oglLayer, displayMode, fullscreen, DisplayMode.VSYNC_ENABLED, fsaa );
    }
    
    protected DisplayOptions( String title, DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync, FSAA fsaa )
    {
        this( title, OpenGLLayer.getDefault(), displayMode, fullscreen, vsync, fsaa );
    }
    
    protected DisplayOptions( String title, DisplayMode displayMode, FullscreenMode fullscreen, FSAA fsaa )
    {
        this( title, OpenGLLayer.getDefault(), displayMode, fullscreen, fsaa );
    }
    
    protected DisplayOptions( String title, DisplayMode displayMode, FullscreenMode fullscreen, boolean vsync )
    {
        this( title, displayMode, fullscreen, vsync, FSAA.OFF );
    }
    
    protected DisplayOptions( String title, DisplayMode displayMode, FullscreenMode fullscreen )
    {
        this( title, displayMode, fullscreen, FSAA.OFF );
    }
    
    protected DisplayOptions( String title, DisplayMode displayMode )
    {
        this( title, displayMode, FullscreenMode.WINDOWED );
    }
    
    protected DisplayOptions( String title, boolean vsync )
    {
        this( title, DisplayModeSelector.getImplementation( OpenGLLayer.JOGL_AWT ).getBestMode( 800, 600 ), FullscreenMode.WINDOWED, vsync );
    }
    
    protected DisplayOptions( String title )
    {
        this( title, DisplayModeSelector.getImplementation( OpenGLLayer.JOGL_AWT ).getBestMode( 800, 600 ) );
    }
    
    protected DisplayOptions( boolean vsync )
    {
        this( "Xith3DApplicationLauncher", vsync );
    }
    
    protected DisplayOptions()
    {
        this( "Xith3DApplicationLauncher" );
    }
}
