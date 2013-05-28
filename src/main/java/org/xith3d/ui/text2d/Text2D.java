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
package org.xith3d.ui.text2d;

import java.awt.Font;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openmali.spatial.bodies.Frustum;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.scenegraph.UpdatableNode;
import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.View;

/**
 * As there is no ( or no dynamic ) text node for Xith3D here is mine.<br />
 * 
 * While the Text2D version org.xith3d.text.Text2D by Terje Wiesener uses a single face with a static texture
 * this class uses a seperate Shape3d for every char it uses. Those shapes are stored in a static HashMap to prevent
 * some load when using many text objects. </br>
 * 
 * <ul>features:
 * <li>- fast text and color changes</li>
 * <li>- multiline support</li>
 * <li>- horizontal and vertical text aligning</li>
 * <li>- Java2D font support</li>
 * </ul>
 * 
 * Thanks to Terje Wiesener ( who wrote the other Text2D implementation ), i borrowed the render stuff from his work :)
 * 
 * @see <a href="http://192.18.37.44/forums/index.php?topic=10106.0">First Announcement (old version)</a>
 * @see <a href="http://192.18.37.44/forums/index.php?topic=10638.0">Secondary Announcement (new version)</a>
 * 
 * @author Florian Hofmann (aka Goliat)
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 * 
 * @version 2.0
 */
public class Text2D extends TransformGroup implements UpdatableNode
{
    private String text;
    private Font font;
    private Colorf color = new Colorf();
    private TextAlignment align;
    private Tuple2f size;
    
    private Vector3f tmpVec = new Vector3f();
    
    private boolean dirty = false;
    
    private List< Line2D > lines;
    private List< Line2D > unmodLines;
    
    private TransparencyAttributes cachedTA = null;
    
    /**
     * @return an unmodifiable List of all contained Lines
     */
    public List< Line2D > getLines()
    {
        return ( unmodLines );
    }
    
    /**
     * Sets the color of current text </br>
     * has a small bug: while changing text rapidly the color doesn't change correctly when setColor is called before setText
     * 
     * @param r red value
     * @param g green value
     * @param b blue value
     */
    public void setColor( float r, float g, float b )
    {
        for ( int i = 0; i < lines.size(); i++ )
        {
            lines.get( i ).setColor( r, g, b );
        }
        
        this.color.set( r, g, b );
    }
    
    /**
     * Sets the color of current text </br>
     * (this should be called after setText if both are called once per frame)
     * 
     * @see #setColor( float, float, float )
     * 
     * @param color new Color to set
     */
    public void setColor( Colorf color )
    {
        setColor( color.getRed(), color.getGreen(), color.getBlue() );
    }
    
    public Colorf getColor()
    {
        return ( color );
    }
    
    public float getRedValue()
    {
        return ( color.getRed() );
    }
    
    public float getBlueValue()
    {
        return ( color.getGreen() );
    }
    
    public float getGreenValue()
    {
        return ( color.getBlue() );
    }
    
    public void setTransparency( float transparency )
    {
        if ( cachedTA != null )
        {
            cachedTA.setTransparency( transparency );
            
            return;
        }
        
        cachedTA = new TransparencyAttributes( TransparencyAttributes.BLENDED, transparency );
        
        for ( int i = 0; i < lines.size(); i++ )
        {
            lines.get( i ).setTransparency( cachedTA );
        }
    }
    
    public float getTransparency()
    {
        if ( cachedTA == null )
            return ( 0.0f );
        
        return ( cachedTA.getTransparency() );
    }
    
    public Tuple2f getSize()
    {
        return ( size );
    }
    
    /**
     * Changes/sets the text's font
     * 
     * @param font new font to use
     */
    public void setFont( Font font )
    {
        this.font = font;
        
        dirty = true;
    }
    
    /**
     * Changes/sets the text's font name
     * 
     * @param fontname new font to use
     */
    public void setFont( String fontname )
    {
        if ( this.font != null )
        {
            this.font = new Font( fontname, this.font.getStyle(), this.font.getSize() );
        }
        else
        {
            this.font = new Font( fontname, Font.PLAIN, 12 );
        }
        
        dirty = true;
    }
    
    public Font getFont()
    {
        return ( font );
    }
    
    /**
     * Sets new fontflags @see java.awt.Font.
     * 
     * @param fontFlags new flags
     */
    public void setFontFlags( int fontFlags )
    {
        if ( this.font != null )
        {
            this.font = new Font( this.font.getName(), fontFlags, this.font.getSize() );
        }
        else
        {
            this.font = new Font( "Monospace", fontFlags, 12 );
        }
        
        dirty = true;
    }
    
    /**
     * Sets new resolution.
     * 
     * @param resolution new resolution
     */
    public void setResolution( int resolution )
    {
        if ( this.font != null )
        {
            this.font = new Font( this.font.getName(), this.font.getStyle(), resolution );
        }
        else
        {
            this.font = new Font( "Monospace", Font.PLAIN, resolution );
        }
        
        dirty = true;
    }
    
    /**
     * Sets new text alignment.
     * 
     * @param alignment new text alignment
     */
    public void setAlignment( TextAlignment alignment )
    {
        this.align = alignment;
        
        dirty = true;
    }
    
    /**
     * @return the current text alignment
     */
    public TextAlignment getAlignment()
    {
        return ( this.align );
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean update( View view, Frustum frustum, long nanoTime, long nanoStep )
    {
        if ( dirty )
        {
            this.removeAllChildren();
            lines.clear();
            
            setText( this.text );
            
            dirty = false;
        }
        
        return ( true );
    }
    
    /**
     * Set new text (this is finally done in an efficient way).
     * 
     * @param newText new text
     */
    public void setText( String newText )
    {
        TextAlignment lineAlign = null;
        if ( align.isLeftAligned() )
            lineAlign = TextAlignment.TOP_LEFT;
        else if ( align.isHCenterAligned() )
            lineAlign = TextAlignment.TOP_CENTER;
        else if ( align.isRightAligned() )
            lineAlign = TextAlignment.TOP_RIGHT;
        
        // create a StringTokenizer to seperate the lines
        StringTokenizer lineTokens = new StringTokenizer( newText, "\n\f" );
        
        Transform3D t3d;
        Line2D currLine;
        float offsetY = 0.0f;
        int index = 0;
        while ( lineTokens.hasMoreTokens() )
        {
            if ( lines.size() <= index )
            {
                currLine = new Line2D( lineTokens.nextToken(), color, font, lineAlign );
                lines.add( currLine );
                
                this.addChild( currLine );
                
                t3d = currLine.getTransform();
                t3d.getTranslation( tmpVec );
                tmpVec.setY( offsetY );
                t3d.setTranslation( tmpVec );
                currLine.setTransform( t3d );
                
                offsetY -= currLine.getSize().getY();
            }
            else
            {
                currLine = lines.get( index );
                
                t3d = currLine.getTransform();
                t3d.getTranslation( tmpVec );
                tmpVec.setY( offsetY );
                t3d.setTranslation( tmpVec );
                currLine.setTransform( t3d );
                
                currLine.setText( lineTokens.nextToken() );
                
                offsetY -= currLine.getSize().getY();
            }
            
            index++;
        }
        
        // remove the unnecessary lines
        for ( int i = lines.size() - 1; i >= index; i-- )
        {
            this.removeChild( lines.get( i ) );
            lines.remove( i );
        }
        
        // calculate size
        if ( newText.length() == 0 )
        {
            size.set( 0f, 0f );
        }
        else
        {
            float width = 0f;
            float height = 0f;
            
            for ( int i = 0; i < lines.size(); i++ )
            {
                final Line2D line = lines.get( i );
                if ( line.getSize().getX() > width )
                {
                    width = line.getSize().getX();
                }
                height += line.getSize().getY();
            }
            
            size.set( width, height );
        }
        
        // adjust translation by (vertical) alignment
        t3d = this.getTransform();
        if ( align.isVCenterAligned() )
        {
            t3d.setTranslation( 0f, ( size.getY() / 2.0f ), 0f );
        }
        else if ( align.isBottomAligned() )
        {
            t3d.setTranslation( 0f, size.getY(), 0f );
        }
        this.setTransform( t3d );
        
        this.text = newText;
    }
    
    /**
     * Create a new Text Object with all options
     * 
     * @param text the initial text to set
     * @param r the foreground color to use
     * @param g the foreground color to use
     * @param b the foreground color to use
     * @param font the font to use </br> as the font size is fixed on to the texture it's just necessary to give the fontname which can be any font that Font can handle
     * @param alignFlags align flags to use
     */
    public Text2D( String text, float r, float g, float b, Font font, TextAlignment alignFlags )
    {
        super();
        
        this.color.set( r, g, b );
        this.font = font;
        this.align = alignFlags;
        
        this.size = new Point2f();
        this.lines = new Vector< Line2D >( 1 );
        this.unmodLines = Collections.unmodifiableList( lines );
        
        setText( text );
    }
    
    /**
     * Create a new Text Object with all options
     * 
     * @param text the initial text to set
     * @param font the font to use </br> as the font size is fixed on to the texture it's just necessary to give the fontname which can be any font that Font can handle
     * @param color the foreground color to use
     * @param alignFlags align flags to use
     */
    public Text2D( String text, Colorf color, Font font, TextAlignment alignFlags )
    {
        this( text, color.getRed(), color.getGreen(), color.getBlue(), font, alignFlags );
    }
    
    /**
     * Create a new Text Object with all options
     * 
     * @param text the initial text to set
     * @param r the foreground color to use
     * @param g the foreground color to use
     * @param b the foreground color to use
     * @param fontname the font to use </br> as the font size is fixed on to the texture it's just necessary to give the fontname which can be any font that Font can handle
     * @param fontFlags font flags to use @see java.awt.Font 
     * @param resolution font resolution </br> every letter has a fixed geometry size of 1x1.5. The resolution is the width of the texture
     * @param alignFlags align flags to use
     */
    public Text2D( String text, float r, float g, float b, String fontname, int fontFlags, int resolution, TextAlignment alignFlags )
    {
        this( text, r, g, b, new Font( fontname, fontFlags, resolution ), alignFlags );
    }
    
    /**
     * Create a new Text Object with all options
     * 
     * @param text the initial text to set
     * @param fontname the font to use </br> as the font size is fixed on to the texture it's just necessary to give the fontname which can be any font that Font can handle
     * @param fontFlags font flags to use @see java.awt.Font 
     * @param resolution font resolution </br> every letter has a fixed geometry size of 1x1.5. The resolution is the width of the texture
     * @param color the foreground color to use
     * @param alignFlags align flags to use
     */
    public Text2D( String text, Colorf color, String fontname, int fontFlags, int resolution, TextAlignment alignFlags )
    {
        this( text, color.getRed(), color.getGreen(), color.getBlue(), fontname, fontFlags, resolution, alignFlags );
    }
    
    /**
     * Creates a new Text2D object
     * 
     * @param text new text 
     * @param color color
     */
    public Text2D( String text, Colorf color )
    {
        this( text, color, "Monospace", Font.BOLD, 12, TextAlignment.CENTER_CENTER );
    }
    
    /**
     * Create a text object with alignflags
     * 
     * @param text new text
     * @param alignFlags new alignFlags
     */
    public Text2D( String text, TextAlignment alignFlags )
    {
        this( text, 1.0f, 1.0f, 1.0f, "Monospace", Font.BOLD, 12, alignFlags );
    }
    
    /**
     * Create a new Text Object with standard Attributes
     * 
     * @param text first Text
     */
    public Text2D( String text )
    {
        this( text, 1.0f, 1.0f, 1.0f, "Monospace", Font.BOLD, 12, TextAlignment.CENTER_CENTER );
    }
}
