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
package org.xith3d.ui.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.jagatoo.image.DirectBufferedImage;
import org.jagatoo.opengl.enums.DrawMode;
import org.jagatoo.opengl.enums.FaceCullMode;
import org.openmali.vecmath2.Colorf;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.PolygonAttributes;
import org.xith3d.scenegraph.RenderingAttributes;
import org.xith3d.scenegraph.TextureAttributes;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.View;
import org.xith3d.utility.logging.X3DLog;

/**
 * An overlay is 3d geometry which is aligned with the image plate.  This is
 * designed to be as simple as possible for the developer to use.  All that is
 * needed is to extend this class and override the paint function to update the
 * image which will be written displayed on the screen.
 *
 * As a note: Displaying large overlays can use a *huge* amount of texture memory.
 * If you created a pane which was 1024x768 you would consume over 3 mb of texture memory.
 *
 * Another thing to realize is that textures have to be a power of two.  Because of this
 * an overlay which was 513 x 257 would normally have to be generated using 1024 x 512.  Unlike
 * most textures, overlays cannot tolerate stretching and interpolation because of the fuzzyness
 * which would result.  So what the Xith overlay system attempts to do is break the overlay up
 * into small pieces so that extra texture memory is not wasted.  You are given a canvas which
 * represents the entire overlay.  After you finish updating it, the image is ripped apart and
 * written to the underlying textures.  Each of these underlying textures are double-buffered,
 * so that switching them to the Xith3d engine is quick and does not involve having to continuously
 * allocate and free large BufferedImages.  This means, of course, that for every overlay you
 * use 3 times (width * height * 4bytes) of system memory.  So a chatbox which was 512 x 200
 * would use 3 * ( 512 * 200 *4) bytes or 1.2 MB of system memory.
 *
 * Updates to the actual textures are done within a behavior within a frame.  This is very
 * fast because all that is happening is that the buffers are getting swapped.
 * We have to be certain that we are not in the process of copying the big buffer into the back
 * buffer at the same time the behavior attempts to do a buffer swap. This is handled by the
 * overlay by not updating texture if we are in the middle of drawing a new one.
 * The drawback to this is that numerous updates per second to the overlay could result in
 * several updates not get immediately reflected.  But since the area is always completely
 * redrawn this should not prove to be an issue.  Soon as we hit a frame where we are not updating
 * the buffer then it will be swapped.
 *
 * Remember, all you have to do to make translucent or non-square overlays is
 * to use the alpha channel.
 * <p>
 * Originally Coded by David Yazel on Oct 4, 2003 at 9:59:54 PM.
 *
 * @author David Yazel
 */
public class UIOverlay
{
    protected static final Boolean DEBUG = null;
    public static final int BACKGROUND_NONE = 0;
    public static final int BACKGROUND_COPY = 1;
    //private static double consoleZ = 5f;
    
    //   private static double consoleZ = 0.55f;
    private BufferedImage canvas; // the large drawing space
    private boolean clipAlpha; // are we clipping alpha == 0
    private boolean blendAlpha; // are we blending alpha < 1
    private int imageType; // image types for buffers
    private List< UISubOverlayBase > subOverlays; // list of SubOverlay nodes
    protected int width;
    protected int height;
    //private double consoleWidth; // width of console
    //private double consoleHeight; // height of console
    //private double scale; // scale into world coordinates
    //private Dimension canvasDim; // the dimensions of the Canvas3d
    //private Dimension checkDim; // used to check for dimension changes
    protected Group consoleBG; // branch group for overlay
    private RenderingAttributes ra = null;
    private PolygonAttributes pa = null;
    private TextureAttributes ta = null;
    private TransparencyAttributes tra = null;
    
    // the following are used to synchronize the updates to the backbuffer
    // The behavior will do a buffer swap if dirty and not painting.
    private boolean dirty = false;
    private boolean painting = false;
    private boolean owned = false;
    
    // used for supporting a pre-paint background draw
    int backgroundMode = BACKGROUND_NONE;
    private BufferedImage backgroundImage = null;
    
    // used for locking access to the painting.
    private Object mutex;
    
    // the location of the overlay in screen coordinates
    //private int posX;
    //private int posY;
    
    public boolean isOwned()
    {
        return ( owned );
    }
    
    public boolean isOpaque()
    {
        return ( !blendAlpha );
    }
    
    /**
     * This should be overwritten if you want to add any additional objects or
     * behaviors or initialization before the overlay goes live
     */
    protected void initialize()
    {
    }
    
    /**
     * This sets the position of the overlay in screen coordinates. 0,0 is the
     * upper left of the screen
     * 
     * @param x
     * @param y
     */
    public void setPosition( int x, int y )
    {
    }
    
    /**
     * Return the root of the overlay and its sub-overlays so it can be
     * added to the scenegraph
     */
    public Node getRoot()
    {
        return ( consoleBG );
    }
    
    /**
     * This internal method creates the sub-overlays for the overlay.  It will
     * attempt to create an optimal arrangement of textures.
     */
    private void createSubOverlays()
    {
        subOverlays = new ArrayList< UISubOverlayBase >();
        
        // calculate the number of columns and their sizes
        int numCols = 0;
        int[] cols = new int[ 20 ];
        int w = width;
        
        while ( w > 0 )
        {
            int p = optimalPower( w, 32 ) - 1;
            
            if ( p > w )
            {
                p = w;
            }
            
            cols[ numCols ] = p;
            w -= cols[ numCols++ ];
        }
        
        // now calculate the number of rows and their sizes
        int numRows = 0;
        int[] rows = new int[ 20 ];
        int h = height;
        
        while ( h > 0 )
        {
            int p = optimalPower( h, 32 );
            
            if ( p > h )
            {
                p = h;
            }
            
            rows[ numRows ] = p;
            h -= rows[ numRows++ ];
        }
        
        // now we have the optimal grid we need to create the sub-overlays.
        // The bounds passed to the sub-overlay are 0,0 based since they represent
        // pieces of the big canvas
        int yStart = 0;
        int yStop = rows[ 0 ] - 1;
        //int ly = 0;
        
        for ( int row = 0; row < numRows; row++ )
        {
            int xStart = 0;
            int xStop = cols[ 0 ] - 1;
            
            for ( int col = 0; col < numCols; col++ )
            {
                UISubOverlayBase s = new UISubOverlayOptimized( this, xStart, yStart, xStop, yStop );
                subOverlays.add( s );
                consoleBG.addChild( s.getShape() );
                
                if ( col < ( numCols - 1 ) )
                {
                    xStart = xStop + 1;
                    xStop = xStop + cols[ col + 1 ];
                }
            }
            
            if ( row < ( numRows - 1 ) )
            {
                yStart = yStop + 1;
                yStop = yStop + rows[ row + 1 ];
            }
        }
    }
    
    /**
     * @return an optimal power of two for the value given.  It will either
     * return the largest power of 2 which is less than or equal to the value, OR
     * it will return a larger power of two as long as the difference between
     * that and the value is not greater than the threshhold.
     */
    private int optimalPower( int value, int threshhold )
    {
        int n = 2;
        
        while ( n < value )
        {
            if ( ( n * 2 ) > 512 )
            {
                return ( 512 );
            }
            
            if ( ( n * 2 ) > value )
            {
                if ( ( ( n * 2 ) - value ) < threshhold )
                {
                    return ( n * 2 );
                }
                
                return ( n );
            }
            
            n *= 2;
        }
        
        return ( n );
    }
    
    /**
     * @return true if the overlay is clipping when alpha is zero
     */
    public boolean getClipAlpha()
    {
        return ( clipAlpha );
    }
    
    /**
     * @return true if the overlay is blending alpha.
     */
    public boolean getBlendAlpha()
    {
        return ( blendAlpha );
    }
    
    /**
     * @return the image type used for the buffered image
     */
    public int getImageType()
    {
        return ( imageType );
    }
    
    /**
     * Return the rendering attributes shared by all sub-overlays
     */
    public RenderingAttributes getRenderingAttributes()
    {
        return ( ra );
    }
    
    /**
     * Return the polygon attributes shared by all the sub-overlays
     */
    public PolygonAttributes getPolygonAttributes()
    {
        return ( pa );
    }
    
    /**
     * Return the texture attributes shared by all the sub-overlays
     */
    public TextureAttributes getTextureAttributes()
    {
        return ( ta );
    }
    
    /**
     * Return the transparency attributes
     */
    public TransparencyAttributes getTransparencyAttributes()
    {
        return ( tra );
    }
    
    /**
     * Move the contents of the drawing canvas into the various
     * backbuffers.
     */
    protected void moveToBackbuffer()
    {
        synchronized ( mutex )
        {
            // copy all the buffers to the back buffer
            painting = true;
            
            int n = subOverlays.size();
            
            for ( int i = 0; i < n; i++ )
            {
                UISubOverlayOptimized s = (UISubOverlayOptimized)subOverlays.get( i );
                
                //Rectangle r = new Rectangle( s.lx, s.ly, s.width, s.height );
                //if (r.intersects( 0, 0, 50, 50 ))
                s.update( canvas, s.getBounds() );
            }
            
            // flag the frame as dirty
            dirty = true;
            painting = false;
        }
    }
    
    /**
     * Prepares the canvas to be painted.  This should only be called internally
     * or from an owner like the ScrollingOverlay class
     */
    protected Graphics2D getPreppedCanvas()
    {
        if ( backgroundMode == BACKGROUND_COPY )
        {
            if ( backgroundImage != null )
            {
                canvas.setData( backgroundImage.getRaster() );
            }
        }
        
        Graphics2D g = (Graphics2D)canvas.getGraphics();
        
        return ( g );
    }
    
    static int testNum = 0;
    
    /**
     * Moves the contents of the buffered image to the canvas and then updates the
     * back buffers that have changed.
     *
     * @param image
     * @param dirtyAreas
     */
    public void repaintChanged( BufferedImage image, ArrayList< ? > dirtyAreas )
    {
        // move the image onto the canvas
        
        int n = subOverlays.size();
        
        // debugging step to highlight changed areas
        /*
        {
            int nn = dirtyAreas.size();
            for (int j = 0; j < nn; j++)
            {
                Rectangle r = ((UIDirtyRegion)dirtyAreas.get( j )).getAbsDirty();
                g.setColor( Color.yellow );
                g.drawRect( r.x, r.y, r.width - 1, r.height - 1 );
            }
        }
        */
        // update the changed sub overlays
        for ( int i = 0; i < n; i++ )
        {
            UISubOverlayOptimized s = (UISubOverlayOptimized)subOverlays.get( i );
            Rectangle sbounds = s.getBounds();
            
            int nn = dirtyAreas.size();
            //boolean dirtied = false;
            for ( int j = 0; j < nn; j++ )
            {
                Rectangle r = ( (UIDirtyRegion)dirtyAreas.get( j ) ).getAbsDirty();
                if ( r.intersects( sbounds ) )
                {
                    //g.setColor( Color.red );
                    //g.drawRect( sbounds.x, sbounds.y, sbounds.width - 1, sbounds.height - 1 );
                    s.update( image, r );
                    //dirtied = true;
                    //System.out.println( "updating sub overlay " + sbounds );
                }
            }
            
            //if (!dirtied)
            //    s.updateBackBufferByDraw( canvas );
        }
        //g.dispose();
        dirty = true;
    }
    
    public void repaint( BufferedImage image )
    {
        // move the image onto the canvas
        int n = subOverlays.size();
        
        // update the changed sub overlays
        for ( int i = 0; i < n; i++ )
        {
            UISubOverlayOptimized s = (UISubOverlayOptimized)subOverlays.get( i );
            Rectangle sbounds = s.getBounds();
            s.update( image, sbounds );
        }
        
        dirty = true;
    }
    
    /**
     * This is called to trigger a repaint of the overlay.  This will return once
     * the back buffer has been built, but before the swap.
     */
    public void repaint()
    {
        Graphics2D g = getPreppedCanvas();
        paint( g );
        g.dispose();
        
        moveToBackbuffer();
    }
    
    public void getSize( Dimension dim )
    {
        dim.width = width;
        dim.height = height;
    }
    
    /**
     * This method is ONLY called from the behavior.  This checks to see if it needs
     * to swap the buffers.  If so, it locks the mutex and swaps all the sub-overlay
     * buffers at once.  If this isn't called from a behavior then the sub-buffer swaps
     * might happen in seperate frames.
     */
    public void update()
    {
        if ( dirty && !painting )
        {
            synchronized ( mutex )
            {
                // swap all the sub-buffers
                int n = subOverlays.size();
                
                for ( int i = 0; i < n; i++ )
                {
                    UISubOverlayBase s = subOverlays.get( i );
                    
                    if ( s.dirty )
                    {
                        //System.out.println( "Swapping " + s.getBounds() );
                        s.swap();
                    }
                    /*
                    else
                        System.out.print( "+" );
                    */
                }
                
                dirty = false;
                
                //Log.log.println( LogType.EXHAUSTIVE, "Just swapped " + n + " suboverlays" );
            }
        }
    }
    
    /**
     * Changes the visibility of the overlay.
     * 
     * @param yes
     */
    public void setVisible( boolean yes )
    {
        //ra.setVisible( yes );
        throw new Error( "Implementation removed. Please talk to the author!" );
    }
    
    /**
     * This is where the actualy drawing of the window takes place.  Override
     * this to alter the contents of what is show in the window.
     */
    public void paint( Graphics2D g )
    {
        // set up a test pattern for checking for distortions
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
        
        /*
        g.setColor( Color.black );
        g.fillRect( 0, 0, width,height );
        */
        g.setColor( Color.red );
        g.fillOval( 1, 1, width - 2, height - 2 );
        
        g.setColor( Color.yellow );
        g.drawLine( 0, height - 1, width - 1, height - 1 );
        
        g.setColor( Color.cyan );
        g.drawLine( 0, 0, width - 1, 0 );
        
        g.setColor( Color.green );
        g.drawLine( 0, 0, 0, height - 1 );
        
        g.setColor( Color.magenta );
        g.drawLine( width - 1, 0, width - 1, height - 1 );
    }
    
    /**
     * Protected method to get the view which this overlay is matching
     */
    protected View getView()
    {
        return ( null );
        //return ( c3d.getView() );
    }
    
    /**
     * Allows an derived class to add a behavior or object to the same transform
     * group that the sub-overlays use
     */
    protected void attachNode( Node node )
    {
        consoleBG.addChild( node );
    }
    
    /**
     * Internal routine to return an appropriate buffered image for the overlay
     * size and format.
     */
    private DirectBufferedImage getAppropriateImage()
    {
        if ( clipAlpha || blendAlpha )
        {
            return ( DirectBufferedImage.makeDirectImageRGBA( width, height ) );
        }
        
        return ( DirectBufferedImage.makeDirectImageRGB( width, height ) );
    }
    
    /**
     * @return a BufferedImage appropriate for using as a background.  It will be the same
     * size as the overlay with an appropriate format.  If one has already been assigned to
     * this overlay then that is what will be returned, otherwise one is built and returned.
     * If a new one is built it is NOT assigned as a background image. To do that call
     * setBackgroundImage()
     */
    public BufferedImage getBackgroundImage()
    {
        if ( backgroundImage == null )
            return ( getAppropriateImage() );
        
        return ( backgroundImage );
    }
    
    /**
     * Sets the background to a solid color.  This will be set before every call to
     * paint() to set the drawing surface.  This actually builds a background image matching
     * the size and format of the overlay and then initializes it to the color.  If a background
     * image already exists then it will be initialized to to the color.  It is completely
     * appropriate to have an alpha component in the color if this is a alpha
     * capable overlay.  In general you should only use background images if this is an
     * overlay that is called frequently, since you could always paint it inside your
     * paint() method.  This is also designed to support the same background image
     * used for multiple overlays, like in a scrolling overlay.
     */
    public void setBackgroundColor( Color c )
    {
        if ( backgroundImage == null )
        {
            backgroundImage = getAppropriateImage();
        }
        
        int[] pixels = new int[ width * height ];
        int rgb = c.getRGB();
        
        for ( int i = 0; i < ( width * height ); i++ )
            pixels[ i ] = rgb;
        
        backgroundImage.setRGB( 0, 0, width, height, pixels, 0, width );
        backgroundMode = BACKGROUND_COPY;
    }
    
    /**
     * Sets the background image to the one specified.  It does not have to be the same size as the
     * overlay but the it should be at least big enough is the backgroundMode is BACKGROUND_COPY,
     * since that is a straight raster copy.  Setting this to null will remove the background
     * image.
     */
    public void setBackgroundImage( BufferedImage bi )
    {
        backgroundImage = bi;
        
        if ( bi == null )
        {
            backgroundMode = BACKGROUND_NONE;
        }
        else
        {
            backgroundMode = BACKGROUND_COPY;
        }
    }
    
    /**
     * Sets the background mode.  BACKGROUND_COPY will copy the raster data from the background
     * into the canvas before paint() is called.  BACKGROUND_NONE will cause the background
     * to be disabled and not used.
     */
    public void setBackgroundMode( int mode )
    {
        backgroundMode = mode;
    }
    
    /**
     * Constructs an overlay window.
     * @param width The width of the window in screen space
     * @param height The height of the window in screen space
     * @param clipAlpha Should we apply a polygon clip where alpha is zero
     * @param blendAlpha Should we blend to background if alpha is < 1
     * @param owned If this overlay is "owned" then no behavior will be built to update it since
     *    it is assumed that the owner will handle the repaints.  Also it will not be
     *    attached to the view transform since it is assumed the owner will do that.
     * @param offset
     * @param readDepthBuffer
     */
    public UIOverlay( int width, int height, boolean clipAlpha, boolean blendAlpha, boolean owned, float offset, boolean readDepthBuffer )
    {
        this.owned = owned;
        this.width = width;
        this.height = height;
        //this.blendAlpha = false;
        this.blendAlpha = blendAlpha;
        //this.clipAlpha = false;
        this.clipAlpha = clipAlpha;
        
        this.canvas = getAppropriateImage();
        
        // define the rendering attributes used by all sub-overlays
        this.ra = new RenderingAttributes();
        
        if ( clipAlpha )
        {
            ra.setAlphaTestFunction( RenderingAttributes.NOT_EQUAL );
            ra.setAlphaTestValue( 0 );
        }
        
        ra.setDepthBufferEnabled( false );
        ra.setDepthBufferWriteEnabled( false );
        
        /*
        ra.setDepthBufferEnable( readDepthBuffer );
        ra.setDepthBufferWriteEnable( true );
        */
        ra.setIgnoreVertexColors( true );
        //ra.setVisible( true );
        
        // define the polygon attributes for all the sub-overlays
        pa = new PolygonAttributes();
        pa.setBackFaceNormalFlip( false );
        pa.setFaceCullMode( FaceCullMode.NONE );
        pa.setDrawMode( DrawMode.FILL );
        pa.setPolygonOffsetFactor( offset );
        
        // define the texture attributes for all the sub-overlays
        ta = new TextureAttributes();
        ta.setTextureMode( TextureAttributes.REPLACE );
        ta.setPerspectiveCorrectionMode( TextureAttributes.NICEST );
        
        // if this needs to support transparancy set up the blend
        if ( clipAlpha || blendAlpha )
        {
            tra = new TransparencyAttributes( TransparencyAttributes.BLENDED, 1.0f );
            ta.setTextureBlendColor( new Colorf( 0f, 0f, 0f, 1f ) );
        }
        
        // define the branch group where we are putting all the sub-overlays
        consoleBG = new Group();
        
        // now we need to calculate the sub-overlays needed for the overlay
        X3DLog.debug( "Overlay : ", width, "x", height );
        
        createSubOverlays();
        
        // attach the behavior to the branchgroup
        initialize();
        mutex = new Object();
    }
    
    public UIOverlay( int width, int height, boolean clipAlpha, boolean blendAlpha, boolean owned )
    {
        this( width, height, clipAlpha, blendAlpha, owned, 0.0f, true );
    }
    
    public UIOverlay( int width, int height, boolean clipAlpha, boolean blendAlpha )
    {
        this( width, height, clipAlpha, blendAlpha, false );
    }
}
