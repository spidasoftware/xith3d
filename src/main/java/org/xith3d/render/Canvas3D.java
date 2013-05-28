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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jagatoo.input.devices.InputDeviceFactory;
import org.jagatoo.input.devices.components.MouseButton;
import org.jagatoo.input.render.Cursor;
import org.openmali.types.twodee.ExtPositioned2i;
import org.openmali.types.twodee.ExtSized2i;
import org.openmali.types.twodee.Rect2i;
import org.openmali.types.twodee.Sized2iRO;
import org.openmali.types.twodee.util.RepositionListener2i;
import org.openmali.types.twodee.util.ResizeListener2i;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Matrix4f;
import org.openmali.vecmath2.Point2i;
import org.openmali.vecmath2.Tuple2i;
import org.openmali.vecmath2.Tuple3f;
import org.openmali.vecmath2.Vector4f;
import org.xith3d.picking.AllPickListener;
import org.xith3d.picking.NearestPickListener;
import org.xith3d.picking.PickEngine;
import org.xith3d.picking.PickPool;
import org.xith3d.picking.PickRequest;
import org.xith3d.render.util.WindowClosingListener;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.screenshots.ScreenshotEngine;

/**
 * A Canvas3D offers a plane area where the scene is projectively rendered on. A
 * RenderPassConfigProvider is used to define some rendering parameters. This
 * can be the configuration of a RenderPass or a View.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Kevin Finley (aka Horati)
 */
public class Canvas3D implements ExtPositioned2i, ExtSized2i, ScreenshotEngine, PickEngine
{
    protected int oldWidth = -1;
    protected int oldHeight = -1;
    
    private CanvasPeer peer;
    private View view;
    private Colorf backgroundColor = new Colorf();
    private List< ResizeListener2i > resizeListeners = new ArrayList< ResizeListener2i >();
    
    private Renderer renderer = null;
    
    /**
     * Sets the Renderer, this Canvas3D is registered to.
     * 
     * @param renderer
     */
    protected final void setRenderer( Renderer renderer )
    {
        this.renderer = renderer;
    }
    
    /**
     * @return the Renderer, this Canvas3D is registered to.
     */
    public final Renderer getRenderer()
    {
        return ( renderer );
    }
    
    /**
     * Not used!
     */
    public void addRepositionListener( RepositionListener2i listener )
    {
    }
    
    /**
     * Not used!
     */
    public void removeRepositionListener( RepositionListener2i listener )
    {
    }
    
    /**
     * {@inheritDoc}
     * Notification occurs in the Xith3D rendering thread and complies with
     * its threading rules.
     */
    public void addResizeListener( ResizeListener2i listener )
    {
        this.resizeListeners.add( listener );
    }
    
    /**
     * {@inheritDoc}
     */
    public void removeResizeListener( ResizeListener2i listener )
    {
        this.resizeListeners.remove( listener );
    }
    
    /**
     * Notifies any interested parties that this Canvas3D has been resized.
     * This method should not be called from the Renderer.
     */
    protected void fireResizeEvent()
    {
        if ( this.resizeListeners.size() > 0 )
        {
            final int width = getWidth();
            final int height = getHeight();
            
            for ( int i = 0; i < resizeListeners.size(); i++ )
            {
                if ( ( oldWidth == -1 ) && ( oldHeight == -1 ) )
                    resizeListeners.get( i ).onObjectResized( this, width, height, width, height );
                else
                    resizeListeners.get( i ).onObjectResized( this, oldWidth, oldHeight, width, height );
            }
            
            oldWidth = width;
            oldHeight = height;
        }
    }
    
    /**
     * Adds a new WindowClosingListener to be notified, when a non-fullscreen
     * Canvas3D's Window is to be closed by clicking the X-button.
     * 
     * @param l
     */
    public void addWindowClosingListener( WindowClosingListener l )
    {
        getPeer().addWindowClosingListener( l );
    }
    
    /**
     * Removes a WindowClosingListener.
     * 
     * @param l
     */
    public void removeWindowClosingListener( WindowClosingListener l )
    {
        getPeer().removeWindowClosingListener( l );
    }
    
    /**
     * Checks the Canvas3D for resizing.
     */
    protected void checkForResized()
    {
        if ( ( getWidth() != oldWidth ) || ( getHeight() != oldHeight ) )
        {
            fireResizeEvent();
        }
    }
    
    /**
     * Assotiates a View with this Canvas3D.
     * 
     * @param view
     */
    public final void setView( View view )
    {
        this.view = view;
    }
    
    /**
     * @return the assotiated View
     */
    public final View getView()
    {
        return ( view );
    }
    
    protected final void setPeer( CanvasPeer peer )
    {
        this.peer = peer;
        
        if ( this.backgroundColor != null )
            setBackgroundColor( backgroundColor );
    }
    
    /**
     * @return the CanvasPeer, backing this Canvas3D. This is the connection
     * to the rendering system.
     */
    public final CanvasPeer getPeer()
    {
        return ( peer );
    }
    
    /**
     * @deprecated Use and see {@link #getPeer()}.
     */
    @Deprecated
    public final CanvasPeer getCanvasPeer()
    {
        return ( getPeer() );
    }
    
    /**
     * @return the containing window's title
     */
    public final String getTitle()
    {
        return ( peer.getTitle() );
    }
    
    /**
     * Sets the containing window's title
     * 
     * @param title the new title
     */
    public final void setTitle( String title )
    {
        peer.setTitle( title );
    }
    
    /**
     * Changes the window icon.
     * 
     * @param iconResource the resource of the image for the new icon
     */
    public final void setIcon( URL iconResource ) throws IOException
    {
        if ( peer == null )
            throw new NullPointerException( "CanvasPeer not set" );
        
        peer.setIcon( iconResource );
    }
    
    /**
     * Clears the screen to BLACK.
     */
    public final void clear()
    {
        if ( peer == null )
            throw new NullPointerException( "CanvasPeer not set" );
        
        peer.clear();
    }
    
    /**
     * Sets the new Cursor for this Mouse.
     * Use <code>null</code> for an invisible Cursor.
     * Use {@link Cursor#DEFAULT_CURSOR} for the system's default Cursor.
     * 
     * @param cursor
     */
    public final void setCursor( Cursor cursor )
    {
        getPeer().setCursor( cursor );
    }
    
    public final Cursor getCursor()
    {
        return ( getPeer().getCursor() );
    }
    
    /**
     * @return the InputDeviceFactory, that provides methods to retrieve
     * InputDevices for the specified implementation.<br>
     * 
     * @see CanvasPeer#getInputDeviceFactory()
     */
    public final InputDeviceFactory getInputDeviceFactory()
    {
        return ( getPeer().getInputDeviceFactory() );
    }
    
    /**
     * @return The location of the Canvas3D
     */
    public final Tuple2i getLocation()
    {
        return ( new Point2i( getLeft(), getTop() ) );
    }
    
    /**
     * Sets the location of the Canvas3D
     * 
     * @param left
     * @param top
     * 
     * @return true, if the location actually has changed
     */
    public final Canvas3D setLocation( int left, int top )
    {
        peer.setLocation( left, top );
        
        return ( this );
    }
    
    /**
     * Adjusts this standalone canvas's location so that it is centered on the screen
     */
    public void setCentered()
    {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = ( ( d.width - this.getWidth() ) / 2 );
        int y = ( ( d.height - this.getHeight() ) / 2 );
        
        setLocation( x, y );
    }
    
    /**
     * Sets the location of the Canvas3D
     */
    public final Canvas3D setLocation( Tuple2i loc )
    {
        return ( setLocation( loc.getX(), loc.getY() ) );
    }
    
    /**
     * @return The left position of the Canvas3D
     */
    public final int getLeft()
    {
        return ( peer.getLeft() );
    }
    
    /**
     * @return The top position of the Canvas3D
     */
    public final int getTop()
    {
        return ( peer.getTop() );
    }
    
    /**
     * @return The width of the Canvas3D
     */
    public final Tuple2i getSize()
    {
        return ( new Point2i( peer.getWidth(), peer.getHeight() ) );
    }
    
    /**
     * Sets the size of the Canvas3D
     * 
     * @param width
     * @param height
     * 
     * @return true, if the size actually has changed
     */
    public final Canvas3D setSize( int width, int height )
    {
        peer.setSize( width, height );
        
        return ( this );
    }
    
    /**
     * Sets the size of the Canvas3D
     * 
     * @param size
     * 
     * @return true, if the size actually has changed
     */
    public final Canvas3D setSize( Sized2iRO size )
    {
        return ( setSize( size.getWidth(), size.getHeight() ) );
    }
    
    /**
     * Sets the size of the Canvas3D
     * 
     * @param size
     * 
     * @return true, if the size actually has changed
     */
    public final Canvas3D setSize( Tuple2i size )
    {
        return ( setSize( size.getX(), size.getY() ) );
    }
    
    public final void setWidth( int width )
    {
        setSize( width, getHeight() );
    }
    
    public final void setHeight( int height )
    {
        setSize( getWidth(), height );
    }
    
    /**
     * @return The width of the Canvas3D
     */
    public final int getWidth()
    {
        return ( peer.getWidth() );
    }
    
    /**
     * @return The height of the Canvas3D
     */
    public final int getHeight()
    {
        return ( peer.getHeight() );
    }
    
    /**
     * @return The aspect ratio of the Canvas3D
     */
    public final float getAspect()
    {
        return ( (float)getWidth() / (float)getHeight() );
    }
    
    /**
     * Switches this Canvas3D's fullscreen flag.<br>
     * Depending on the implementation this flag may be applied
     * on the next render-frame, but not instantly.
     * 
     * @param fullscreen
     * 
     * @return the previous fullscreen flag.
     */
    public final boolean setFullscreen( boolean fullscreen )
    {
        final boolean oldState = isFullscreen();
        
        if ( oldState == fullscreen )
            return ( oldState );
        
        peer.setFullscreen( fullscreen );
        
        return ( oldState );
    }
    
    /**
     * @return true, if the rendering is done in fullscreen mode
     */
    public final boolean isFullscreen()
    {
        return ( peer.isFullscreen() );
    }
    
    /**
     * @return true, if this Canvas'es window is undecorated.
     */
    public final boolean isUndecorated()
    {
        return ( peer.isUndecorated() );
    }
    
    /**
     * Sets this CanvasPeer's Viewport.
     * 
     * @param viewport
     */
    public final void setViewport( Rect2i viewport )
    {
        peer.setViewport( viewport );
    }
    
    /**
     * @return this CanvasPeer's Viewport
     */
    public final Rect2i getViewport()
    {
        return ( peer.getViewport() );
    }
    
    /**
     * Sets the background color for this Canvas3D (in fact for the CanvasPeer).
     * 
     * @param red the red component of the new background color
     * @param green the green component of the new background color
     * @param blue the blue component of the new background color
     */
    public final void setBackgroundColor( float red, float green, float blue )
    {
        this.backgroundColor.set( red, green, blue );
        
        getPeer().getRenderPeer().setClearColor( backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha() );
    }
    
    /**
     * Sets the background color for this Canvas3D (in fact for the CanvasPeer).
     * 
     * @param color the new background color
     */
    public final void setBackgroundColor( Colorf color )
    {
        assert ( color != null );
        
        setBackgroundColor( color.getRed(), color.getGreen(), color.getBlue() );
    }
    
    /**
     * @return the background color for this Canvas3D (in fact for the
     *         CanvasPeer).
     */
    public final Colorf getBackgroundColor()
    {
        return ( backgroundColor );
    }
    
    /**
     * Sets this Canvas3D's RenderOptions
     */
    public void setRenderOptions( RenderOptions ro )
    {
        getPeer().getRenderPeer().setRenderOptions( ro );
    }
    
    /**
     * @return this Canvas3D's RenderOptions
     */
    public final RenderOptions getRenderOptions()
    {
        return ( getPeer().getRenderPeer().getRenderOptions() );
    }
    
    /**
     * Enables or disables wireframe mode.
     * 
     * @param enabled if true, wireframe mode will be enabled
     */
    public void setWireframeMode( boolean enabled )
    {
        getRenderOptions().setWireframeModeEnabled( enabled );
    }
    
    /**
     * @return if wireframe mode is enabled or disabled.
     */
    public boolean isWireframeMode()
    {
        return ( getRenderOptions().isWireframeModeEnabled() );
    }
    
    /**
     * Switches wireframe mode.
     * 
     * @return the new state.
     */
    public boolean switchWireframeMode()
    {
        return ( getRenderOptions().switchWireframeMode() );
    }
    
    /**
     * Enables lighting on this Canvas3D.
     */
    public void enableLighting()
    {
        getRenderOptions().setLightingEnabled( true );
    }
    
    /**
     * Disables lighting on this Canvas3D.
     */
    public void disableLighting()
    {
        getRenderOptions().setLightingEnabled( false );
    }
    
    /**
     * Changes the gamma-, brightness- and contrast values for this CanvasPeer.
     * 
     * @param gamma the gamma value
     * @param brightness the brightness value [-1.0, +1.0]
     * @param contrast the contrast value [0, +1.0]
     */
    public void setGamma( float gamma, float brightness, float contrast )
    {
        getPeer().setGamma( gamma, brightness, contrast );
    }
    
    /**
     * @return the current gamma value for this CanvasPeer.
     */
    public float getGamma()
    {
        return ( getPeer().getGamma() );
    }
    
    /**
     * @return the current brightness value for this CanvasPeer.
     */
    public float getBrightness()
    {
        return ( getPeer().getBrightness() );
    }
    
    /**
     * @return the current contrast value for this CanvasPeer.
     */
    public float getContrast()
    {
        return ( getPeer().getContrast() );
    }
    
    /**
     * Takes a screenshot of the current rendering
     * 
     * @param file the file to save the screenshot to
     * @param alpha with alpha channel?
     */
    public final void takeScreenshot( File file, boolean alpha )
    {
        getPeer().takeScreenshot( file, alpha );
    }
    
    /**
     * Takes a screenshot of the current rendering
     * 
     * @param filenameBase the filenameBase to save the screenshot to (e.g.
     *            "screens/shot") The current date and ".png" are appended.
     * @param alpha with alpha channel?
     * @return the file where the screenshot has been saved
     */
    public final File takeScreenshot( String filenameBase, boolean alpha )
    {
        return ( getPeer().takeScreenshot( filenameBase, alpha ) );
    }
    
    /**
     * Takes a screenshot of the current rendering of the first added Canvas3D.
     * 
     * @param alpha with alpha channel?
     * @return the file where the screenshot has been saved
     */
    public final File takeScreenshot( boolean alpha )
    {
        return ( getPeer().takeScreenshot( alpha ) );
    }
    
    /**
     * Renders the scene in GLSelect mode.
     * 
     * @param x the Canvas3D-relative x-component of the picking
     * @param y the Canvas3D-relative y-component of the picking
     * @param width with of the pick-ray
     * @param height height of the pick-ray
     * 
     * @return a List of the results of this picking of the nearest result
     */
    private final void pick( List< ? extends GroupNode > rootGroups, MouseButton button, int x, int y, int width, int height, Object pickListener, boolean pickAll, Object userObject )
    {
        final List< RenderPass > renderPasses = getRenderer().getRenderPasses( ( (GroupNode)rootGroups.get( 0 ) ).getRoot() );
        
        /*
        final PickRequest preq = PickHeap.allocatePickRequest( renderPasses.get( 0 ), rootGroups, this, button, x, y, pickListener, userObject, pickAll );
        
        getRenderer().addPickRequest( preq );
        */

        for ( int i = 0; i < rootGroups.size(); i++ )
        {
            final PickRequest preq = PickPool.allocatePickRequest( renderPasses.get( 0 ), (GroupNode)rootGroups.get( i ), this, button, x, y, pickListener, userObject, pickAll );
            
            getRenderer().addPickRequest( preq );
        }
    }
    
    /**
     * Renders the scene in GLSelect mode.
     * 
     * @param x the Canvas3D-relative x-component of the picking
     * @param y the Canvas3D-relative y-component of the picking
     * @param width with of the pick-ray
     * @param height height of the pick-ray
     * 
     * @return a List of the results of this picking of the nearest result
     */
    private final void pick( GroupNode rootGroup, MouseButton button, int x, int y, int width, int height, Object pickListener, boolean pickAll, Object userObject )
    {
        final List< RenderPass > renderPasses = getRenderer().getRenderPasses( rootGroup.getRoot() );
        
        final PickRequest preq = PickPool.allocatePickRequest( renderPasses.get( 0 ), rootGroup, this, button, x, y, pickListener, userObject, pickAll );
        
        getRenderer().addPickRequest( preq );
    }
    
    public final void pickAll( List< ? extends GroupNode > groups, MouseButton button, int x, int y, int width, int height, AllPickListener pl, Object userObject )
    {
        pick( groups, button, x, y, width, height, pl, true, userObject );
    }
    
    public final void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int x, int y, int width, int height, NearestPickListener pl, Object userObject )
    {
        pick( groups, button, x, y, width, height, pl, false, userObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickAll( List< ? extends GroupNode > groups, MouseButton button, int x, int y, AllPickListener pl, Object userObject )
    {
        pick( groups, button, x, y, 1, 1, pl, true, userObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickAll( List< ? extends GroupNode > groups, MouseButton button, int x, int y, AllPickListener pl )
    {
        pick( groups, button, x, y, 1, 1, pl, true, (Object)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int x, int y, NearestPickListener pl, Object userObject )
    {
        pick( groups, button, x, y, 1, 1, pl, false, userObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickNearest( List< ? extends GroupNode > groups, MouseButton button, int x, int y, NearestPickListener pl )
    {
        pick( groups, button, x, y, 1, 1, pl, false, (Object)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickAll( GroupNode group, MouseButton button, int x, int y, AllPickListener pl, Object userObject )
    {
        pick( group, button, x, y, 1, 1, pl, true, userObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickAll( GroupNode group, MouseButton button, int x, int y, AllPickListener pl )
    {
        pick( group, button, x, y, 1, 1, pl, true, (Object)null );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickNearest( GroupNode group, MouseButton button, int x, int y, NearestPickListener pl, Object userObject )
    {
        pick( group, button, x, y, 1, 1, pl, false, userObject );
    }
    
    /**
     * {@inheritDoc}
     */
    public final void pickNearest( GroupNode group, MouseButton button, int x, int y, NearestPickListener pl )
    {
        pick( group, button, x, y, 1, 1, pl, false, (Object)null );
    }
    
    /**
     * Calculates screen coordinates for the given world coordinates.
     * 
     * @param world
     * @param screen
     */
    public void worldToScreen( Tuple3f world, Tuple2i screen )
    {
        Sized2iRO viewport = getViewport();
        if ( viewport == null )
            viewport = this;
        
        // get the model-view and the projection matrix
        Matrix4f mP = getView().calculatePerspective( viewport ).getMatrix4f();
        Matrix4f mM = getView().getModelViewTransform( true ).getMatrix4f();
        
        // convert the point into a vector of length 4
        Vector4f v0 = Vector4f.fromPool();
        v0.set( world.getX(), world.getY(), world.getZ(), 1f );
        Vector4f v = Vector4f.fromPool();
        
        // v' = P x M x v
        v.mul( mM, v0 );
        v0.set( v );
        v.mul( mP, v0 );
        
        v.setW( ( 1f / v.getW() ) * 0.5f );
        
        v.setX( v.getX() * v.getW() + 0.5f );
        v.setY( v.getY() * v.getW() + 0.5f );
        v.setZ( v.getZ() * v.getW() + 0.5f );
        
        float xs = viewport.getWidth() * v.getX();
        float ys = viewport.getHeight() - viewport.getHeight() * v.getY();
        
        if ( getViewport() != null )
        {
            xs += ( (Rect2i)viewport ).getLeft();
            ys += ( (Rect2i)viewport ).getTop();
        }
        
        screen.set( (int)Math.round( xs ), (int)Math.round( ys ) );
        
        Vector4f.toPool( v );
        Vector4f.toPool( v0 );
    }
    
    protected Canvas3D( CanvasPeer peer, float backgroundColorR, float backgroundColorG, float backgroundColorB )
    {
        assert ( peer != null );
        
        this.peer = peer;
        peer.setCanvas3D( this );
        setBackgroundColor( backgroundColorR, backgroundColorG, backgroundColorB );
    }
    
    protected Canvas3D( CanvasPeer peer, Colorf backgroundColor )
    {
        this( peer, backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue() );
    }
    
    protected Canvas3D( CanvasPeer peer )
    {
        this( peer, 0.2f, 0.2f, 0.2f );
    }
}
