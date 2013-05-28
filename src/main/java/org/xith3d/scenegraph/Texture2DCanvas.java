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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import org.jagatoo.util.arrays.ArrayUtils;
import org.openmali.types.twodee.Rect2i;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Tuple2i;

/**
 * This is an adapter for pixel-perfect drawing onto a Texture2D.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Texture2DCanvas extends Graphics2D
{
    public static abstract class DrawCallback2D
    {
        private boolean dirty = true;
        
        /**
         * Marks this draw callback dirty.
         * This tells the render system to call the {@link #drawTexture(Texture2DCanvas, int, int)} method.
         * 
         * @param dirty
         */
        public void setDirty( boolean dirty )
        {
            this.dirty = dirty;
        }
        
        /**
         * Checks, if this callback needs to be redrawn.
         * 
         * @param nanoTime
         * 
         * @return true, if the texture should be redrawn.
         */
        public boolean needsRedraw( long nanoTime )
        {
            boolean result = dirty;
            dirty = false;
            
            return ( result );
        }
        
        /**
         * This callback is invoked, when the Texture needs to be redrawn.
         * 
         * @param texCanvas the {@link Texture2DCanvas} to draw on.
         * @param texWidth the (original) width of the texture
         * @param texHeight the (original) height of the texture
         */
        public abstract void drawTexture( Texture2DCanvas texCanvas, int texWidth, int texHeight );
    }
    
    private final Texture2D texture;
    private final TextureImage2D texImg;
    
    private int imgWidth;
    private int imgHeight;
    private Graphics2D graphics;
    private final AffineTransform baseAffineTransform;
    
    private Rect2i currentUpdateRect = null;
    private int currentlyAppliedUpdateRects = 0;
    
    //private final ArrayList< DrawCallback2D > callbacks = new ArrayList< DrawCallback2D >( 1 );
    private DrawCallback2D[] callbacks = new DrawCallback2D[ 0 ];
    private int numCallbacks = 0;
    
    public final Texture2D getTexture()
    {
        return ( texture );
    }
    
    public final TextureImage2D getImage()
    {
        return ( texImg );
    }
    
    public void addDrawCallback( DrawCallback2D callback )
    {
        callbacks = ArrayUtils.ensureCapacity( callbacks, DrawCallback2D.class, callbacks.length + 1 );
        callbacks[numCallbacks++] = callback;
    }
    
    public void removeDrawCallback( DrawCallback2D callback )
    {
        int index = ArrayUtils.indexOf( callbacks, callback, false );
        
        callbacks[index] = null;
        System.arraycopy( callbacks, index + 1, callbacks, index, numCallbacks - index - 1 );
        numCallbacks--;
    }
    
    final boolean notifyDrawCallbacks( long nanoTime )
    {
        if ( numCallbacks == 0 )
            return ( false );
        
        for ( int i = 0; i < numCallbacks; i++ )
        {
            final DrawCallback2D callback = callbacks[i];
            
            if ( callback.needsRedraw( nanoTime ) )
                callback.drawTexture( this, imgWidth, imgHeight );
        }
        
        return ( texture.hasUpdateList() );
    }
    
    private final void markDirty( int x, int y, int width, int height )
    {
        if ( currentUpdateRect != null )
        {
            if ( ( x >= currentUpdateRect.getLeft() ) &&
                 ( y >= currentUpdateRect.getTop() ) &&
                 ( x + width <= currentUpdateRect.getLeft() + currentUpdateRect.getWidth() ) &&
                 ( y + height <= currentUpdateRect.getTop() + currentUpdateRect.getHeight() )
               )
            {
                return;
            }
        }
        
        Rect2i clip = getImage().getEffectiveClipRect();
        
        int clipX = clip.getLeft();
        int clipY = clip.getTop();
        int clipW = clip.getWidth();
        int clipH = clip.getHeight();
        
        x = Math.max( clipX, x );
        y = Math.max( clipY, y );
        x = Math.min( x, clipX + clipW - 1 );
        y = Math.min( y, clipY + clipH - 1 );
        width = Math.min( width, clipX + clipW - x );
        height = Math.min( height, clipY + clipH - y );
        
        if ( ( width > 0 ) && ( height > 0 ) )
        {
            if ( texImg != null )
                //texImg.update( x, y, width, height );
                texImg.update( x, imgHeight - height - y, width, height );
            
            if ( texture != null )
                texture.setHasUpdateList( true );
        }
    }
    
    private final void markDirty()
    {
        markDirty( 0, 0, imgWidth, imgHeight );
    }
    
    public final void beginUpdateRegion( int x, int y, int width, int height )
    {
        if ( currentUpdateRect != null )
        {
            currentUpdateRect.combine( x, y, width, height );
        }
        else
        {
            currentUpdateRect = Rect2i.fromPool( x, y, width, height );
        }
        
        currentlyAppliedUpdateRects++;
    }
    
    public final void beginUpdateRegionComplete()
    {
        beginUpdateRegion( 0, 0, imgWidth, imgHeight );
    }
    
    public final void finishUpdateRegion()
    {
        if ( currentUpdateRect == null )
            return;
        
        currentlyAppliedUpdateRects--;
        
        if ( currentlyAppliedUpdateRects == 0 )
        {
            int l = currentUpdateRect.getLeft();
            int t = currentUpdateRect.getTop();
            int w = currentUpdateRect.getWidth();
            int h = currentUpdateRect.getHeight();
            Rect2i.toPool( currentUpdateRect );
            currentUpdateRect = null;
            markDirty( l, t, w, h );
        }
    }
    
    
    public final java.awt.geom.AffineTransform getBaseAffineTransform()
    {
        return ( baseAffineTransform );
    }
    
    @Override
    public GraphicsConfiguration getDeviceConfiguration()
    {
        return ( graphics.getDeviceConfiguration() );
    }
    
    @Override
    public final void clearRect( int x, int y, int width, int height )
    {
        graphics.clearRect( x, y, width, height );
        
        markDirty( x, y, width, height );
    }
    
    public final void clearRect( Rect2i rect )
    {
        clearRect( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight() );
    }
    
    @Override
    public void clipRect( int x, int y, int width, int height )
    {
        graphics.clipRect( x, y, width, height );        
    }
    
    public final void clip( Rect2i rect )
    {
        clipRect( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight() );
    }
    
    @Override
    public final void clip( Shape shape )
    {
        graphics.clip( shape );
    }
    
    @Override
    public final void copyArea( int x, int y, int width, int height, int dx, int dy )
    {
        graphics.copyArea( x, y, width, height, dx, dy );
        
        markDirty( dx, dy, width, height );
    }
    
    public final void copyArea( Rect2i rect, int dx, int dy )
    {
        copyArea( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight(), dx, dy );
    }
    
    @Override
    public final void draw( Shape shape )
    {
        graphics.draw( shape );
        
        java.awt.Rectangle rect = shape.getBounds();
        
        markDirty( rect.x, rect.y, rect.width, rect.height );
    }
    
    @Override
    public final void drawArc( int x, int y, int width, int height, int startAngle, int arcAngle )
    {
        graphics.drawArc( x, y, width, height, startAngle, arcAngle );
        
        markDirty( x, y, width, height );
    }
    
    public final void drawCircle( int x, int y, int radius )
    {
        drawArc( x - radius, y - radius, radius + radius, radius + radius, 0, 360 );
    }
    
    @Override
    public final void drawBytes( byte[] data, int offset, int length, int x, int y )
    {
        graphics.drawBytes( data, offset, length, x, y );
        
        markDirty( x, y, imgWidth - x, imgHeight - y );
    }
    
    @Override
    public final void drawChars( char[] data, int offset, int length, int x, int y )
    {
        graphics.drawChars( data, offset, length, x, y );
        
        markDirty( x, y, imgWidth - x, imgHeight - y );
    }
    
    @Override
    public final void drawGlyphVector( GlyphVector g, float x, float y )
    {
        graphics.drawGlyphVector( g, x, y );
        
        markDirty( (int)x, (int)y, imgWidth - (int)x, imgHeight - (int)y );
    }
    
    @Override
    public final boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver imgOb )
    {
        if ( ( dx2 < 0 ) || ( dy2 < 0 ) || ( dx1 >= imgWidth ) || ( dy1 >= imgHeight ) )
            return ( true );
        
        final boolean result = graphics.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, imgOb );
        
        markDirty( dx1, dy1, dx2 - dx1 + 1, dy2 - dy1 + 1 );
        
        return ( result );
    }
    
    public final void drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2 )
    {
        drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, (ImageObserver)null );
    }
    
    @Override
    public final boolean drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgColor, ImageObserver imgOb )
    {
        final boolean result = graphics.drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgColor, imgOb );
        
        markDirty( dx1, dy1, dx2 - dx1 + 1, dy2 - dy1 + 1 );
        
        return ( result );
    }
    
    public final void drawImage( Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Colorf bgColor )
    {
        drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgColor.getAWTColor(), (ImageObserver)null );
    }
    
    @Override
    public final boolean drawImage( Image img, AffineTransform xform, ImageObserver imgOb )
    {
        final boolean result = graphics.drawImage( img, xform, imgOb );
        
        markDirty();
        
        return ( result );
    }
    
    @Override
    public final void drawImage( BufferedImage img, BufferedImageOp op, int x, int y )
    {
        graphics.drawImage( img, op, x, y );
        
        markDirty( x, y, img.getWidth() - x, img.getHeight() - y );
    }
    
    @Override
    public final boolean drawImage( Image img, int x, int y, ImageObserver imgOb )
    {
        final boolean result = graphics.drawImage( img, x, y, imgOb );
        
        markDirty( x, y, img.getWidth( null ) - x, img.getHeight( null ) - y );
        
        return ( result );
    }
    
    public final void drawImage( Image img, int x, int y )
    {
        drawImage( img, x, y, (ImageObserver)null );
    }
    
    @Override
    public final boolean drawImage( Image img, int x, int y, Color bgColor, ImageObserver imgOb )
    {
        final boolean result = graphics.drawImage( img, x, y, bgColor, imgOb );
        
        markDirty( x, y, img.getWidth( null ) - x, img.getHeight( null ) - y );
        
        return ( result );
    }
    
    public final void drawImage( Image img, int x, int y, Colorf bgColor )
    {
        drawImage( img, x, y, bgColor.getAWTColor(), (ImageObserver)null );
    }
    
    @Override
    public final boolean drawImage( Image img, int x, int y, int width, int height, ImageObserver imgOb )
    {
        final boolean result = graphics.drawImage( img, x, y, width, height, null );
        
        markDirty( x, y, width, height );
        
        return ( result );
    }
    
    public final void drawImage( Image img, int x, int y, int width, int height )
    {
        drawImage( img, x, y, width, height, (ImageObserver)null );
    }
    
    @Override
    public final boolean drawImage( Image img, int x, int y, int width, int height, Color bgColor, ImageObserver imgOb )
    {
        final boolean result = graphics.drawImage( img, x, y, width, height, bgColor, imgOb );
        
        markDirty( x, y, width, height );
        
        return ( result );
    }
    
    public final void drawImage( Image img, int x, int y, int width, int height, Colorf bgColor )
    {
        drawImage( img, x, y, width, height, bgColor.getAWTColor(), (ImageObserver)null );
    }
    
    @Override
    public void drawRenderableImage( RenderableImage img, AffineTransform xform )
    {
        graphics.drawRenderableImage( img, xform );
        
        markDirty();
    }
    
    @Override
    public void drawRenderedImage( RenderedImage img, AffineTransform xform )
    {
        graphics.drawRenderedImage( img, xform );
        
        markDirty();
    }
    
    @Override
    public final void drawLine( int x1, int y1, int x2, int y2 )
    {
        //graphics.drawLine( x1, yy( y1 ), x2, yy( y2 ) );
        graphics.drawLine( x1, y1, x2, y2 );
        
        markDirty( x1, y1, x2 - x1, y2 - y1 );
    }
    
    public final void drawLineOffset( int x, int y, int dx, int dy )
    {
        drawLine( x, y, x + dx, y + dy );
    }
    
    @Override
    public final void drawOval( int x, int y, int width, int height )
    {
        graphics.drawOval( x, y, width, height );
        
        markDirty( x, y, width, height );
    }
    
    @Override
    public final void drawPolygon( Polygon polygon )
    {
        graphics.drawPolygon( polygon );
        
        java.awt.Rectangle rect = polygon.getBounds();
        
        markDirty( rect.x, rect.y, rect.width, rect.height );
    }
    
    @Override
    public final void drawPolygon( int[] xPoints, int[] yPoints, int nPoints )
    {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for ( int i = 0; i < nPoints; i++ )
        {
            if ( xPoints[i] < minX )
                minX = xPoints[i];
            
            if ( xPoints[i] > maxX )
                maxX = xPoints[i];
        }
        
        for ( int i = 0; i < nPoints; i++ )
        {
            if ( yPoints[i] < minY )
                minY = yPoints[i];
            
            if ( yPoints[i] > maxY )
                maxY = yPoints[i];
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        
        if ( ( width > 0 ) && ( height > 0 ) )
        {
            graphics.drawPolygon( xPoints, yPoints, nPoints );
            
            markDirty( minX, minY, width, height );
        }
    }
    
    @Override
    public final void drawPolyline( int[] xPoints, int[] yPoints, int nPoints )
    {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for ( int i = 0; i < nPoints; i++ )
        {
            if ( xPoints[i] < minX )
                minX = xPoints[i];
            
            if ( xPoints[i] > maxX )
                maxX = xPoints[i];
        }
        
        for ( int i = 0; i < nPoints; i++ )
        {
            if ( yPoints[i] < minY )
                minY = yPoints[i];
            
            if ( yPoints[i] > maxY )
                maxY = yPoints[i];
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        
        if ( ( width > 0 ) && ( height > 0 ) )
        {
            graphics.drawPolyline( xPoints, yPoints, nPoints );
            
            markDirty( minX, minY, width, height );
        }
    }
    
    public final void drawPolygon( Tuple2i[] points )
    {
        final int nPoints = points.length;
        
        if ( nPoints == 0 )
            return;
        
        final int[] xPoints = new int[ nPoints ];
        final int[] yPoints = new int[ nPoints ];
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for ( int i = 0; i < nPoints; i++ )
        {
            xPoints[ i ] = points[ i ].getX();
            yPoints[ i ] = points[ i ].getY();
            
            if ( xPoints[ i ] < minX )
                minX = xPoints[ i ];
            if ( xPoints[ i ] > maxX )
                maxX = xPoints[ i ];
            if ( yPoints[ i ] < minY )
                minY = yPoints[ i ];
            if ( yPoints[ i ] > maxY )
                maxY = yPoints[ i ];
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        
        if ( ( width > 0 ) && ( height > 0 ) )
        {
            graphics.drawPolygon( xPoints, yPoints, nPoints );
            
            markDirty( minX, minY, width, height );
        }
    }
    
    public final void drawPolyline( Tuple2i[] points )
    {
        final int nPoints = points.length;
        
        if ( nPoints == 0 )
            return;
        
        final int[] xPoints = new int[ nPoints ];
        final int[] yPoints = new int[ nPoints ];
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for ( int i = 0; i < nPoints; i++ )
        {
            xPoints[ i ] = points[ i ].getX();
            yPoints[ i ] = points[ i ].getY();
            
            if ( xPoints[ i ] < minX )
                minX = xPoints[ i ];
            if ( xPoints[ i ] > maxX )
                maxX = xPoints[ i ];
            if ( yPoints[ i ] < minY )
                minY = yPoints[ i ];
            if ( yPoints[ i ] > maxY )
                maxY = yPoints[ i ];
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        
        if ( ( width > 0 ) && ( height > 0 ) )
        {
            graphics.drawPolyline( xPoints, yPoints, nPoints );
            
            markDirty( minX, minY, width, height );
        }
    }
    
    @Override
    public final void drawRect( int x, int y, int width, int height )
    {
        graphics.drawRect( x, y, width, height );
        
        markDirty( x, y, width, height );
    }
    
    public final void drawRect( Rect2i rect )
    {
        drawRect( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight() );
    }
    
    @Override
    public final void drawRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight )
    {
        graphics.drawRoundRect( x, y, width, height, arcWidth, arcHeight );
        
        markDirty( x, y, width, height );
    }
    
    public final void drawRoundRect( Rect2i rect, int arcWidth, int arcHeight )
    {
        drawRoundRect( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight(), arcWidth, arcHeight );
    }
    
    @Override
    public final void drawString( AttributedCharacterIterator iterator, float x, float y )
    {
        graphics.drawString( iterator, x, y );
        
        markDirty( (int)x, (int)y, imgWidth - (int)x, imgHeight - (int)y );
    }
    
    @Override
    public final void drawString( AttributedCharacterIterator iterator, int x, int y )
    {
        graphics.drawString( iterator, x, y );
        
        markDirty( x, y, imgWidth - x, imgHeight - y );
    }
    
    @Override
    public final void drawString( String s, float x, float y )
    {
        graphics.drawString( s, x, y );
        
        markDirty( (int)x, (int)y, imgWidth - (int)x, imgHeight - (int)y );
    }
    
    @Override
    public final void drawString( String s, int x, int y )
    {
        graphics.drawString( s, x, y );
        
        markDirty( x, y, imgWidth - x, imgHeight - y );
    }
    
    public final void drawString( String s, int x, int y, int boundsWidth, int boundsHeight )
    {
        graphics.drawString( s, x, y );
        
        markDirty( x, y - boundsHeight, boundsWidth, boundsHeight );
    }
    
    @Override
    public final void fill( Shape shape )
    {
        graphics.fill( shape );
        
        java.awt.Rectangle rect = shape.getBounds();
        
        markDirty( rect.x, rect.y, rect.width, rect.height );
    }
    
    @Override
    public final void fillArc( int x, int y, int width, int height, int startAngle, int arcAngle )
    {
        graphics.fillArc( x, y, width, height, startAngle, arcAngle );
        
        markDirty( x, y, width, height );
    }
    
    public final void fillCircle( int x, int y, int radius )
    {
        fillArc( x - radius, y - radius, radius + radius, radius + radius, 0, 360 );
    }
    
    @Override
    public final void fillOval( int x, int y, int width, int height )
    {
        graphics.fillOval( x, y, width, height );
        
        markDirty( x, y, width, height );
    }
    
    @Override
    public final void fillPolygon( int[] xPoints, int[] yPoints, int nPoints )
    {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for ( int i = 0; i < nPoints; i++ )
        {
            if ( xPoints[i] < minX )
                minX = xPoints[i];
            
            if ( xPoints[i] > maxX )
                maxX = xPoints[i];
        }
        
        for ( int i = 0; i < nPoints; i++ )
        {
            if ( yPoints[i] < minY )
                minY = yPoints[i];
            
            if ( yPoints[i] > maxY )
                maxY = yPoints[i];
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        
        if ( ( width > 0 ) && ( height > 0 ) )
        {
            graphics.fillPolygon( xPoints, yPoints, nPoints );
            
            markDirty( minX, minY, width, height );
        }
    }
    
    public final void fillPolygon( Tuple2i[] points )
    {
        final int nPoints = points.length;
        
        if ( nPoints == 0 )
            return;
        
        final int[] xPoints = new int[ nPoints ];
        final int[] yPoints = new int[ nPoints ];
        
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        
        for ( int i = 0; i < nPoints; i++ )
        {
            xPoints[ i ] = points[ i ].getX();
            yPoints[ i ] = points[ i ].getY();
            
            if ( xPoints[ i ] < minX )
                minX = xPoints[ i ];
            if ( xPoints[ i ] > maxX )
                maxX = xPoints[ i ];
            if ( yPoints[ i ] < minY )
                minY = yPoints[ i ];
            if ( yPoints[ i ] > maxY )
                maxY = yPoints[ i ];
        }
        
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        
        if ( ( width > 0 ) && ( height > 0 ) )
        {
            graphics.fillPolygon( xPoints, yPoints, nPoints );
            
            markDirty( minX, minY, width, height );
        }
    }
    
    @Override
    public final void fillRect( int x, int y, int width, int height )
    {
        graphics.fillRect( x, y, width, height );
        
        markDirty( x + 1, y + 1, width - 2, height - 2 );
    }
    
    public final void fillRect( Rect2i rect )
    {
        fillRect( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight() );
    }
    
    @Override
    public final void fillRoundRect( int x, int y, int width, int height, int arcWidth, int arcHeight )
    {
        graphics.fillRoundRect( x, y, width, height, arcWidth, arcHeight );
        
        markDirty( x, y, width, height );
    }
    
    public final void fillRoundRect( Rect2i rect, int arcWidth, int arcHeight )
    {
        fillRoundRect( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight(), arcWidth, arcHeight );
    }
    
    @Override
    public Color getBackground()
    {
        return ( graphics.getBackground() );
    }
    
    public final Colorf getBackgroundColor()
    {
        return ( new Colorf( graphics.getBackground() ) );
    }
    
    @Override
    public final java.awt.Shape getClip()
    {
        return ( graphics.getClip() );
    }
    
    public final Rect2i getClipRect2i()
    {
        java.awt.Rectangle awtRect = graphics.getClipBounds();
        
        return ( new Rect2i( awtRect.x, awtRect.y, awtRect.width, awtRect.height ) );
    }
    
    @Override
    public final Rectangle getClipBounds()
    {
        return ( graphics.getClipBounds() );
    }
    
    public final Rect2i getClipBounds( Rect2i rect )
    {
        java.awt.Rectangle awtRect = new java.awt.Rectangle( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight() );
        
        graphics.getClipBounds( awtRect );
        
        return ( rect );
    }
    
    @Override
    public final Color getColor()
    {
        return ( graphics.getColor() );
    }
    
    public final Colorf getColorf()
    {
        return ( new Colorf( graphics.getColor() ) );
    }
    
    @Override
    public final java.awt.Font getFont()
    {
        return ( graphics.getFont() );
    }
    
    @Override
    public final java.awt.FontMetrics getFontMetrics()
    {
        return ( graphics.getFontMetrics() );
    }
    
    @Override
    public FontMetrics getFontMetrics( Font font )
    {
        return ( graphics.getFontMetrics( font ) );
    }
    
    @Override
    public final java.awt.font.FontRenderContext getFontRenderContext()
    {
        return ( graphics.getFontRenderContext() );
    }
    
    @Override
    public final java.awt.Paint getPaint()
    {
        return ( graphics.getPaint() );
    }
    
    @Override
    public final Object getRenderingHint( java.awt.RenderingHints.Key hintKey )
    {
        return ( graphics.getRenderingHint( hintKey ) );
    }
    
    @Override
    public final java.awt.RenderingHints getRenderingHints()
    {
        return ( graphics.getRenderingHints() );
    }
    
    @Override
    public final java.awt.Stroke getStroke()
    {
        return ( graphics.getStroke() );
    }
    
    @Override
    public final java.awt.geom.AffineTransform getTransform()
    {
        return ( graphics.getTransform() );
    }
    
    @Override
    public final boolean hitClip( int x, int y, int width, int height )
    {
        return ( graphics.hitClip( x, y, width, height ) );
    }
    
    @Override
    public boolean hit( Rectangle rect, Shape s, boolean onStroke )
    {
        return ( graphics.hit( rect, s, onStroke ) );
    }
    
    @Override
    public final void rotate( double theta )
    {
        graphics.rotate( theta );
    }
    
    @Override
    public final void rotate( double theta, double x, double y )
    {
        graphics.rotate( theta, x, y );
    }
    
    @Override
    public final void scale( double sx, double sy )
    {
        graphics.scale( sx, sy );
    }
    
    public final void setBackgroundColor( Colorf color )
    {
        graphics.setBackground( color.getAWTColor() );
    }
    
    @Override
    public final void setBackground( Color color )
    {
        graphics.setBackground( color );
    }
    
    @Override
    public final void setClip( java.awt.Shape clip )
    {
        graphics.setClip( clip );
    }
    
    @Override
    public final void setClip( int x, int y, int width, int height )
    {
        graphics.setClip( x, y, width, height );
        
        getImage().setClipRect( x, y, width, height );
    }
    
    public final void setClip( Rect2i rect )
    {
        if ( rect == null )
            setClip( 0, 0, getImage().getWidth(), getImage().getHeight() );
        else
            setClip( rect.getLeft(), rect.getTop(), rect.getWidth(), rect.getHeight() );
    }
    
    public final <Rect2i_ extends Rect2i> Rect2i_ getClip( Rect2i_ rect )
    {
        return ( getImage().getClipRect( rect ) );
    }
    
    @Override
    public void setColor( Color color )
    {
        graphics.setColor( color );
    }
    
    public final void setColor( Colorf color )
    {
        graphics.setColor( color.getAWTColor() );
    }
    
    @Override
    public final void setFont( java.awt.Font font )
    {
        graphics.setFont( font );
    }
    
    @Override
    public final void setPaint( java.awt.Paint paint )
    {
        graphics.setPaint( paint );
    }
    
    @Override
    public final void setPaintMode()
    {
        graphics.setPaintMode();
    }
    
    @Override
    public final void setRenderingHint( java.awt.RenderingHints.Key hintKey, Object hintValue )
    {
        graphics.setRenderingHint( hintKey, hintValue );
    }
    
    public final void setRenderingHints( java.awt.RenderingHints hints )
    {
        graphics.setRenderingHints( hints );
    }
    
    @Override
    public void setRenderingHints( Map< ?, ? > hints )
    {
        graphics.setRenderingHints( hints );
    }
    
    @Override
    public void addRenderingHints( Map< ?, ? > hints )
    {
        graphics.addRenderingHints( hints );
    }
    
    public final void setAntialiazingEnabled( boolean enabled )
    {
        Object value = enabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF;
        
        graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, value );
    }
    
    public final boolean isAntialiazingEnabled()
    {
        Object value = graphics.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        
        return ( value == RenderingHints.VALUE_ANTIALIAS_ON );
    }
    
    @Override
    public final void setStroke( java.awt.Stroke stroke )
    {
        graphics.setStroke( stroke );
    }
    
    @Override
    public final void setTransform( java.awt.geom.AffineTransform Tx )
    {
        graphics.setTransform( Tx );
    }
    
    @Override
    public final void setXORMode( Color color )
    {
        graphics.setXORMode( color );
    }
    
    @Override
    public final void shear( double shx, double shy )
    {
        graphics.shear( shx, shy );
    }
    
    @Override
    public final void transform( java.awt.geom.AffineTransform Tx )
    {
        graphics.transform( Tx );
    }
    
    @Override
    public final void translate( double tx, double ty )
    {
        graphics.translate( tx, ty );
    }
    
    @Override
    public final void translate( int tx, int ty )
    {
        graphics.translate( tx, ty );
    }
    
    protected void updateAffineTransform()
    {
        baseAffineTransform.setToIdentity();
        
        if ( !texImg.getYUp() )
        {
            baseAffineTransform.concatenate( new AffineTransform( 1f, 0f, 0f, -1f, 0f, 0f ) );
            baseAffineTransform.concatenate( new AffineTransform( 1f, 0f, 0f, 1f, 0f, -imgHeight ) );
        }
        
        this.graphics.setTransform( baseAffineTransform );
    }
    
    public final void notifyImagesizeChanged( int imgWidth, int imgHeight, Graphics2D graphics )
    {
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        
        if ( ( graphics != null ) && ( graphics != this.graphics ) )
        {
            graphics.setBackground( this.graphics.getBackground() );
            graphics.setClip( this.graphics.getClip() );
            graphics.setColor( this.graphics.getColor() );
            graphics.setComposite( this.graphics.getComposite() );
            graphics.setFont( this.graphics.getFont() );
            graphics.setPaint( this.graphics.getPaint() );
            graphics.setRenderingHints( this.graphics.getRenderingHints() );
            graphics.setStroke( this.graphics.getStroke() );
            //graphics.setXORMode( this.graphics.getXORMode() );
            
            this.graphics = graphics;
        }
        
        updateAffineTransform();
    }
    
    @Override
    public void setComposite( Composite comp )
    {
        graphics.setComposite( comp );
    }
    
    @Override
    public Composite getComposite()
    {
        return ( graphics.getComposite() );
    }
    
    @Override
    public Graphics create()
    {
        throw new Error( "create() is not supported for this kind of Graphics2D." );
    }
    
    @Override
    public void dispose()
    {
        graphics.dispose();
    }
    
    public Texture2DCanvas( Texture2D texture, TextureImage2D ti, int imgWidth, int imgHeight, Graphics2D graphics )
    {
        if ( graphics == null )
            throw new IllegalArgumentException( "graphics must not be null." );
        
        this.texture = texture;
        this.texImg = ti;
        
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.graphics = graphics;
        
        this.baseAffineTransform = new AffineTransform( 1f, 0f, 0f, 1f, 0f, 0f );
        
        updateAffineTransform();
    }
    
    public Texture2DCanvas( Texture2D texture, TextureImage2D ti, int imgWidth, int imgHeight )
    {
        this( texture, ti, imgWidth, imgHeight, ti.createGraphics2D() );
    }
    
    public Texture2DCanvas( int imgWidth, int imgHeight, Graphics2D graphics )
    {
        this( null, null, imgWidth, imgHeight, graphics );
    }
}
