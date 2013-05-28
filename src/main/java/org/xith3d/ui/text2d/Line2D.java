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
import java.util.Vector;

import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point2f;
import org.openmali.vecmath2.Tuple2f;
import org.openmali.vecmath2.Vector3f;

import org.xith3d.scenegraph.Transform3D;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.TransparencyAttributes;

/**
 * Represents a line of text in 3D space.
 * This class is part of the DynamicText toolkit for Xith3D.
 * 
 * @see <a href="DText2D.html">org.xith3d.text.DText2D</a>
 * 
 * @author Florian Hofmann (aka Goliat)
 * @author Marvin Froehlich (aka Qudus)
 * 
 * @version 2.0
 */
public class Line2D extends TransformGroup
{
    private Font font;
    private String charId;
    private TextAlignment align;
    private Colorf color = new Colorf();
    private String text = "";
    private Vector< Character2D > characters;
    private List< Character2D > unmodCharacters;
    private Tuple2f size;
    
    private Vector3f tmpVec = new Vector3f();
    
    private TransparencyAttributes cachedTA = null;
    
    /**
     * @return an unmodifiable List of all contained Characters
     */
    public List< Character2D > getCharacters()
    {
        return ( unmodCharacters );
    }
    
    /**
     * replaces the text of this line fast
     * 
     * @param newText new text
     */
    public void setText( String newText )
    {
        // remove chars if there are too much in the old string
        while ( characters.size() > newText.length() )
        {
            final Character2D c = characters.remove( characters.size() - 1 );
            this.removeChild( c.getParent() );
        }
        
        // create a new TransformGroup for every char and put a Character2D in it
        TransformGroup tg;
        Transform3D t3d;
        Character2D currChar;
        float offsetX = 0.0f;
        characters.ensureCapacity( newText.length() );
        for ( int i = 0; i < newText.length(); i++ )
        {
            // --- check if this char has to be ignored, replaced or created
            // if the old text of this line less then i children -> create and add character
            if ( characters.size() <= i )
            {
                // load character
                if ( font == null )
                    currChar = Character2D.loadCharacter( newText.charAt( i ), color, charId );
                else
                    currChar = Character2D.loadCharacter( newText.charAt( i ), color, font );
                
                /*
                if ( currChar == null )
                {
                    if ( font == null )
                        currChar = Character2D.loadCharacter( '?', color, charId );
                    else
                        currChar = Character2D.loadCharacter( '?', color, font );
                }
                */
                
                if ( currChar != null )
                {
                    // add this char to the list
                    characters.add( currChar );
                    
                    tg = new TransformGroup();
                    tg.addChild( currChar );
                    this.addChild( tg );
                }
            }
            // check if this character has to be replaced
            else if ( characters.get( i ).getChar() != newText.charAt( i ) )
            {
                // load new character
                currChar = Character2D.loadCharacter( newText.charAt( i ), color, charId );
                
                int oldShapeIndex = characters.get( i ).getParent().indexOf( characters.get( i ) );
                characters.get( i ).getParent().setChild( currChar, oldShapeIndex );
                
                characters.set( i, currChar );
            }
            else
            {
                currChar = characters.get( i );
            }
            
            if ( currChar != null )
            {
                // set translation depending on position in String
                tg = (TransformGroup)currChar.getParent();
                t3d = tg.getTransform();
                t3d.setTranslation( offsetX, 0, 0 );
                tg.setTransform( t3d );
                
                offsetX += currChar.getWidth();
            }
        }
        
        // calculate size
        if ( newText.length() == 0 )
        {
            size.set( 0f, 0f );
        }
        else
        {
            float width = 0f;
            
            for ( int i = 0; i < characters.size(); i++ )
            {
                width += characters.get( i ).getSize().getX();
            }
            
            size.set( width, characters.get( 0 ).getSize().getY() );
        }
        
        // adjust translation by (horizontal) alignment
        t3d = this.getTransform();
        t3d.getTranslation( tmpVec );
        
        if ( align.isHCenterAligned() )
        {
            tmpVec.setX( -( size.getX() / 2.0f ) );
        }
        else if ( align.isRightAligned() )
        {
            tmpVec.setX( -size.getX() );
        }
        
        t3d.setTranslation( tmpVec );
        this.setTransform( t3d );
        
        // all characters updated or created
        
        this.text = newText;
    }
    
    public String getText()
    {
        return ( text );
    }
    
    public Character2D char2DAt( int index )
    {
        return ( characters.get( index ) );
    }
    
    public Tuple2f getSize()
    {
        return ( size );
    }
    
    public Font getFont()
    {
        return ( font );
    }
    
    protected String getCharId()
    {
        return ( charId );
    }
    
    public TextAlignment getAlign()
    {
        return ( align );
    }
    
    /**
     * Sets the color of this text fast
     * 
     * @param r new Color
     * @param g new Color
     * @param b new Color
     */
    public void setColor( float r, float g, float b )
    {
        this.color.set( r, g, b );
        
        for ( int i = 0; i < characters.size(); i++ )
        {
            characters.get( i ).setColor( color );
        }
    }
    
    /**
     * changes the color of this text fast
     * @param color new Color
     */
    public void setColor( Colorf color )
    {
        setColor( color.getRed(), color.getGreen(), color.getBlue() );
    }
    
    /**
     * @return the Line's color (actually the color of the first character).
     */
    public Colorf getColor()
    {
        return ( color );
    }
    
    protected void setTransparency( TransparencyAttributes ta )
    {
        this.cachedTA = ta;
        
        for ( int i = 0; i < characters.size(); i++ )
        {
            characters.get( i ).getAppearance().setTransparencyAttributes( cachedTA );
        }
    }
    
    public void setTransparency( float transparency )
    {
        if ( cachedTA != null )
        {
            cachedTA.setTransparency( transparency );
            
            return;
        }
        
        cachedTA = new TransparencyAttributes( TransparencyAttributes.BLENDED, transparency );
        
        setTransparency( cachedTA );
    }
    
    public float getTransparency()
    {
        if ( cachedTA == null )
            return ( 0.0f );
        
        return ( cachedTA.getTransparency() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( super.toString() + " [" + this.getText() + "]" );
    }
    
    /**
     * Constructs a new Line2D with all parameters
     * 
     * @param string the string to set
     * @param r
     * @param g
     * @param b
     * @param charId
     * @param align
     */
    protected Line2D( String string, float r, float g, float b, String charId, TextAlignment align )
    {
        super();
        
        this.charId = charId;
        this.align = align;
        this.color.set( r, g, b );
        
        this.size = new Point2f();
        this.characters = new Vector< Character2D >( string.length() );
        this.unmodCharacters = Collections.unmodifiableList( characters );
        
        setText( string );
    }
    
    /**
     * Constructs a new Line2D with all parameters
     * 
     * @param string the string to set
     * @param color
     * @param charId
     * @param align
     */
    protected Line2D( String string, Colorf color, String charId, TextAlignment align )
    {
        this( string, color.getRed(), color.getGreen(), color.getBlue(), charId, align );
    }
    
    /**
     * Constructs a new Line2D with all parameters
     * 
     * @param string the string to set
     * @param r
     * @param g
     * @param b
     * @param font
     * @param align
     */
    public Line2D( String string, float r, float g, float b, Font font, TextAlignment align )
    {
        super();
        
        this.charId = font.getName() + "-" + font.getSize() + "-" + font.getStyle();
        this.font = font;
        this.align = align;
        this.color.set( r, g, b );
        
        this.size = new Point2f();
        this.characters = new Vector< Character2D >();
        this.unmodCharacters = Collections.unmodifiableList( characters );
        
        setText( string );
    }
    
    /**
     * Constructs a new Line2D with all parameters
     * 
     * @param string the string to set
     * @param font
     * @param color
     * @param align
     */
    public Line2D( String string, Colorf color, Font font, TextAlignment align )
    {
        this( string, color.getRed(), color.getGreen(), color.getBlue(), font, align );
    }
}
