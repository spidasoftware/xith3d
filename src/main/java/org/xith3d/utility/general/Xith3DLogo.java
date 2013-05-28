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
package org.xith3d.utility.general;

import java.io.IOException;

import org.jagatoo.input.InputSystem;
import org.jagatoo.input.devices.components.AnalogDeviceComponent;
import org.jagatoo.input.devices.components.DeviceComponent;
import org.jagatoo.input.events.InputEvent;
import org.jagatoo.input.listeners.InputStateListener;
import org.jagatoo.util.arrays.ArrayUtils;
import org.openmali.FastMath;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple3f;
import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.ModelLoader;
import org.xith3d.loaders.sound.ExtensionSoundLoader;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.loop.opscheduler.OperationScheduler;
import org.xith3d.loop.opscheduler.impl.ScheduledOperationImpl;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.RenderOptions;
import org.xith3d.render.RenderPass;
import org.xith3d.scenegraph.BackgroundSound;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.PointLight;
import org.xith3d.scenegraph.SceneGraph;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.primitives.RectBillboard;
import org.xith3d.scenegraph.utils.ShapeUtils;
import org.xith3d.ui.hud.HUD;
import org.xith3d.ui.hud.utils.HUDFont;
import org.xith3d.ui.hud.widgets.Label;
import org.xith3d.ui.text2d.TextAlignment;

/**
 * Displays a small video with the Xith3D logo rotating.
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Xith3DLogo
{
    public static interface Xith3DLogoFinishListener
    {
        public void onLogoFinished();
    }
    
    private static final HUDFont font;
    static
    {
        HUDFont f = null;
        
        try
        {
            //Font.TRUETYPE_FONT
            f = HUDFont.getFont( Xith3DLogo.class.getClassLoader().getResource( "resources/org/xith3d/fonts/DirtyEgo.ttf" ), HUDFont.PLAIN, 75 );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
        
        font = f;
    }
    
    private Colorf oldCanvasBackgroundColor;
    private SceneGraph sceneGraph;
    private RenderPass renderPass;
    private PointLight light;
    private TransformGroup modelTrans;
    private BackgroundSound sound;
    private HUD hud;
    private Label label;
    private boolean finished;
    
    private static final float interpolate( float time, float startTime, float endTime, float startValue, float endValue, float preValue, float postValue )
    {
        if ( time < startTime )
        {
            return ( preValue );
        }
        else if ( time <= endTime )
        {
            return ( startValue + ( endValue - startValue ) * ( time - startTime ) / ( endTime - startTime ) );
        }
        else
        {
            return ( postValue );
        }
    }
    
    private void setupInterpolators( final Canvas3D canvas, final OperationScheduler opScheder )
    {
        opScheder.scheduleOperation( new ScheduledOperationImpl( true )
        {
            private long startTime = -1L;
            
            public void update( long gameTime, long frameTime, TimingMode timingMode )
            {
                if ( finished )
                {
                    this.setAlive( false );
                    return;
                }
                
                if ( startTime == -1L )
                {
                    startTime = gameTime;
                }
                
                float time = timingMode.getSecondsAsFloat( gameTime - startTime );
                
                if ( time <= 2.1f )
                {
                    Tuple3f tmp = Tuple3f.fromPool();
                    
                    modelTrans.getTransform().getRotation( tmp );
                    tmp.setY( interpolate( time, 0f, 2.1f, FastMath.TWO_PI, 0f, 0f, 0f ) );
                    modelTrans.getTransform().setRotation( tmp );
                    
                    modelTrans.getTransform().getTranslation( tmp );
                    tmp.setZ( interpolate( time, 0f, 2.1f, -50f, -0.5f, -50f, -0.5f ) );
                    modelTrans.getTransform().setTranslation( tmp );
                    
                    Tuple3f.toPool( tmp );
                }
                
                float alpha = interpolate( time, 2.0f, 3.0f, 1f, 0f, 1f, 0f );
                
                if ( ( time >= 2.0f ) && ( time <= 3.0f ) )
                {
                    label.setTransparency( alpha );
                }
                
                canvas.setBackgroundColor( alpha, alpha, alpha );
                
                if ( time <= 4.0f )
                {
                    float lightXPos = interpolate( time, 2.0f, 4.0f, -20f, 50f, -20f, 50f );
                    light.setLocation( lightXPos, 10f, 0f );
                }
                else
                {
                    float lightXPos = interpolate( time, 4.0f, 5.0f, 50f, 0f, 50f, 0f );
                    light.setLocation( lightXPos, 10f, 0f );
                }
            }
        } );
    }
    
    private void loadLogoModel( GroupNode parentGroup ) throws IOException
    {
        light = new PointLight( 1f, 1f, 1f, 0f, 10f, 0f, 0.0005f );
        parentGroup.addChild( light );
        
        modelTrans = new TransformGroup();
        Model model = ModelLoader.getInstance().loadModel( this.getClass().getClassLoader().getResource( "resources/org/xith3d/models/Xith3DLogo.obj" ) );
        modelTrans.addChild( model );
        parentGroup.addChild( modelTrans );
    }
    
    private void setupSound( GroupNode parentGroup )
    {
        final String soundFile = "resources/org/xith3d/sounds/Du Rififi a Noubaka - Extrait.ogg";
        
        try
        {
            this.sound = new BackgroundSound( new ExtensionSoundLoader().loadSound( this.getClass().getClassLoader().getResource( soundFile ) ), 1f );
            sound.setEnabled( false );
            sound.setLoopType( BackgroundSound.INFINITE_LOOPS );
            
            parentGroup.addChild( sound );
            
        }
        catch ( IOException e )
        {
            System.err.println( "Could not find sound file " + soundFile );
        }
    }
    
    private HUD createHUD()
    {
        HUD hud = new HUD( 800, 600, 800f );
        
        label = new Label( 800f, 175f, "Xith3D\nEnjoyable creativity", font, new Colorf( 0.8f, 0.0f, 0.0f ), TextAlignment.CENTER_CENTER );
        label.setTransparency( 1.0f );
        
        hud.getContentPane().addWidget( label, ( hud.getResX() - label.getWidth() ) / 2f, hud.getResY() - label.getHeight() - 10f );
        
        return ( hud );
    }
    
    protected void end( final Canvas3D canvas, final Xith3DLogoFinishListener finishListener )
    {
        sound.setEnabled( false );
        
        canvas.setBackgroundColor( oldCanvasBackgroundColor );
        
        sceneGraph.removeHUD( hud );
        sceneGraph.removeRenderPass( renderPass );
        
        if ( finishListener != null )
        {
            finishListener.onLogoFinished();
        }
    }
    
    protected void end( final OperationScheduler opScheder, final Canvas3D canvas, final Xith3DLogoFinishListener finishListener )
    {
        finished = true;
        
        final RectBillboard blackFader = new RectBillboard( 100f, 100f, new Colorf( 0f, 0f, 0f, 0f ) );
        TransformGroup tg = new TransformGroup( 0f, 0f, 8.9f );
        tg.addChild( blackFader );
        renderPass.getBranchGroup().addChild( tg );
        
        opScheder.scheduleOperation( new ScheduledOperationImpl( true )
        {
            private long startTime = -1L;
            
            public void update( long gameTime, long frameTime, TimingMode timingMode )
            {
                if ( startTime == -1L )
                {
                    startTime = gameTime;
                }
                
                float time = timingMode.getSecondsAsFloat( gameTime - startTime );
                
                if ( time <= 0.5f )
                {
                    float alpha = interpolate( time, 0f, 0.5f, 0f, 1f, 0f, 1f );
                    
                    ShapeUtils.setTransparency( blackFader, 1f - alpha );
                    label.setTransparency( alpha );
                    //sound.setVolume( 1f - alpha ); // TODO: Check, why this doesn't work!
                }
                else
                {
                    this.setAlive( false );
                    
                    end( canvas, finishListener );
                }
            }
        } );
    }
    
    /**
     * Plays the Xith3DLogo video on the specified canvas.
     * This video has a short music, it lasts ~10 seconds
     * and can be cut by the user if he presses any key.
     * 
     * @param opScheder
     * @param canvas
     * @param stopComponents an array of KeyCodes, that will cause the logo to stop playing
     * @param inputSystem
     * @param finishListener
     */
    public void play( final OperationScheduler opScheder, final Canvas3D canvas, final DeviceComponent[] stopComponents, final InputSystem inputSystem, final Xith3DLogoFinishListener finishListener )
    {
        if ( finished )
        {
            throw new IllegalStateException( "This logo has already been finished!" );
        }
        
        this.oldCanvasBackgroundColor = new Colorf( canvas.getBackgroundColor() );
        
        sceneGraph.addRenderPass( renderPass );
        sceneGraph.addHUD( hud );
        sound.setEnabled( true );
        
        // Set up interpolators
        setupInterpolators( canvas, opScheder );
        
        inputSystem.addInputStateListener( new InputStateListener()
        {
            public void onInputStateChanged( InputEvent e, DeviceComponent comp, int delta, int state )
            {
                if ( delta <= 0 )
                    return;
                
                boolean isStopComp = false;
                
                if ( stopComponents == null )
                {
                    /*
                     * If no stop-component has explicitly been specified,
                     * all components (keys, mouse-buttons, controller-buttons)
                     * are used as stop-component. But we exclude analog devices
                     * like mouse axes or joystick-axis, etc.
                     */
                    
                    if ( ( comp != null ) && !( comp instanceof AnalogDeviceComponent ) )
                    {
                        isStopComp = true;
                    }
                }
                else if ( ArrayUtils.contains( stopComponents, comp, true ) )
                {
                    /*
                     * If stop-components have explicitly been specified,
                     * we simply check, if the component of the changed state
                     * is on of them.
                     */
                    
                    isStopComp = true;
                }
                
                if ( isStopComp )
                {
                    inputSystem.removeInputStateListener( this );
                    
                    Xith3DLogo.this.end( opScheder, canvas, finishListener );
                }
            }
        } );
    }
    
    /**
     * Plays the Xith3DLogo video on the specified canvas.
     * This video has a short music, it lasts ~10 seconds
     * and can be cut by the user if he presses any key.
     * 
     * @param opScheder
     * @param canvas
     * @param stopComponents an array of KeyCodes, that will cause the logo to stop playing
     * @param finishListener
     */
    public void play( final OperationScheduler opScheder, final Canvas3D canvas, final DeviceComponent[] stopComponents, final Xith3DLogoFinishListener finishListener )
    {
        play( opScheder, canvas, stopComponents, InputSystem.getInstance(), finishListener );
    }
    
    /**
     * Plays the Xith3DLogo video on the specified canvas.
     * This video has a short music, it lasts ~10 seconds
     * and can be cut by the user if he presses any key.
     * 
     * @param opScheder
     * @param canvas
     * @param finishListener
     */
    public void play( final OperationScheduler opScheder, final Canvas3D canvas, final Xith3DLogoFinishListener finishListener )
    {
        play( opScheder, canvas, null, finishListener );
    }
    
    public Xith3DLogo( SceneGraph sceneGraph ) throws IOException
    {
        this.sceneGraph = sceneGraph;
        
        this.renderPass = RenderPass.createPerspective();
        Transform3D viewTransform = new Transform3D();
        viewTransform.lookAt( 0f, 0f, 10f,
                              0f, 0f, 0f,
                              0f, 1f, 0f
                            );
        renderPass.getConfig().setBackClipDistance( 60f );
        renderPass.getConfig().setFrontClipDistance( 1f );
        renderPass.getConfig().setViewTransform( viewTransform );
        renderPass.getConfig().setRenderOptions( new RenderOptions() );
        renderPass.getConfig().getRenderOptions().setLightingEnabled( true );
        
        loadLogoModel( renderPass.getBranchGroup() );
        
        setupSound( renderPass.getBranchGroup() );
        
        //sceneGraph.addRenderPass( renderPass );
        
        this.hud = createHUD();
        
        //sceneGraph.addHUD( hud );
        
        finished = false;
    }
}
