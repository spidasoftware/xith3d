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
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.openmali.types.twodee.Sized2iRO;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.TexCoord2f;
import org.openmali.vecmath2.Tuple2f;
import org.xith3d.scenegraph.ASCIITexture;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.ColoringAttributes;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.TransparencyAttributes;
import org.xith3d.scenegraph.TriangleStripArray;
import org.xith3d.scenegraph.Geometry.Optimization;

/**
 * Represents a character in 3D space.
 * This class is part of the DynamicText toolkit for Xith3d.
 * 
 * @see <a href="DText2D.html">org.xith3d.text.Text2D</a>
 * 
 * @author Florian Hofmann (aka Goliat)
 * @author Marvin Froehlich (aka Qudus)
 * 
 * @version 2.0
 */
public class Character2D extends Shape3D
{
    private static float fontStretchFactor = 1.0f;
    
    // this HashMap contains all preloaded Characters
    // the key contains all character attributes ( char, font, resolution, flags )
    // like this: "a-Arial-64-0"
    // ( char, fontname, resolution, fontflags )
    private static final Map< String, Character2D > characterCache = new HashMap< String, Character2D >( 10, 10 );
    
    // the charId of this Character
    //private String charId;
    
    private Tuple2f size;
    private char character;
    
    public static void setFontStretchFactor( float factor )
    {
        fontStretchFactor = factor;
    }
    
    public static float getFontStretchFactor()
    {
        return ( fontStretchFactor );
    }
    
    /**
     * @return the char this Character2D represents
     */
    public char getChar()
    {
        return ( character );
    }
    
    /**
     * For debuging purposes: </br>
     * Returns the number of currently loaded chars
     * 
     * @return number of chars
     */
    public static int numberOfLoadedChars()
    {
        return ( characterCache.size() );
    }
    
    /**
     * Sets the color of this char.
     * 
     * @param r new Color
     * @param g new Color
     * @param b new Color
     */
    public void setColor( float r, float g, float b )
    {
        this.getAppearance( true ).getColoringAttributes( true ).setColor( r, g, b );
    }
    
    /**
     * Sets the color of this char.
     * 
     * @param color new Color
     */
    public void setColor( Colorf color )
    {
        setColor( color.getRed(), color.getGreen(), color.getBlue() );
    }
    
    /**
     * @return the current color of this Character
     */
    public Colorf getColor()
    {
        return ( this.getAppearance( true ).getColoringAttributes( true ).getColor() );
    }
    
    public Tuple2f getSize()
    {
        return ( size );
    }
    
    public float getWidth()
    {
        return ( size.getX() );
    }
    
    public float getHeight()
    {
        return ( size.getY() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( super.toString() + " [" + this.getChar() + "]" );
    }
    
    private Character2D( char character, Tuple2f size )
    {
        super();
        
        this.character = character;
        this.size = size;
    }
    
    private static Colorf tmpColor = new Colorf();
    
    /**
     * Creates a new Character with all attributes.
     * 
     * @param character new character
     * @param r the red value of the color
     * @param g the green value of the color
     * @param b the blue value of the color
     * @param font the font to use
     * @return returns a Node (should be casted to Character)
     */
    public static Character2D loadCharacter( char character, float r, float g, float b, Font font )
    {
        tmpColor.set( r, g, b );
        
        // --- create the char id
        final String charId = String.valueOf( character ) + "--" + font.hashCode() + "--" + tmpColor.hashCode();
        
        // --- lookup the character by id string
        if ( characterCache.containsKey( charId ) )
        {
            
            // create a copy of this character using the cached content
            final Character2D original = characterCache.get( charId );
            final Character2D copy = new Character2D( character, original.getSize() );
            copy.setGeometry( original.getGeometry() );
            copy.setAppearance( original.getAppearance() );
            /*
            Appearance app = original.getAppearance().cloneNodeComponent( true );
            
            app.setColoringAttributes( new ColoringAttributes( color, ColoringAttributes.FASTEST ) );
            
            copy.setAppearance( app );
            */

            // return the copy
            return ( copy );
            
            // if we have found the character -> return it
            //return characterCache.get( charId ).sharedCopy();
        } // else: load the character
        
        //System.out.println( "Creating new Character2D " + character + "..." );
        
        final ASCIITexture texture = Font2D.getFontTexture( font );
        final Sized2iRO charSize = texture.getCharSize( character );
        
        if ( charSize == null )
            return ( null );
        
        // --- create the geometry
        TriangleStripArray geometry = new TriangleStripArray( 4, null );
        geometry.setOptimization( Optimization.USE_VBOS );
        
        // create vertices
        float[] vertices = new float[]
        {
            0.0f, -(float)charSize.getHeight() * getFontStretchFactor(), 0f, (float)charSize.getWidth() * getFontStretchFactor(), -(float)charSize.getHeight() * getFontStretchFactor(), 0f, 0.0f, 0.0f, 0.0f, (float)charSize.getWidth() * getFontStretchFactor(), 0.0f, 0.0f
        };
        // add vertices to geometry
        geometry.setCoordinates( 0, vertices );
        
        TexCoord2f[] tc = texture.getTextureCoordinates( character );
        float[] texCoords = new float[]
        {
            tc[ 0 ].getS(), tc[ 0 ].getT(), tc[ 1 ].getS(), tc[ 0 ].getT(), tc[ 0 ].getS(), tc[ 1 ].getT(), tc[ 1 ].getS(), tc[ 1 ].getT()
        };
        
        // add texture coords to geometry
        geometry.setTextureCoordinates( 0, 0, 2, texCoords );
        
        // --- create appearance
        Appearance app = new Appearance();
        // set transparency 
        app.setTransparencyAttributes( new TransparencyAttributes( TransparencyAttributes.BLENDED, 0.0f ) );
        // set color
        ColoringAttributes ca = new ColoringAttributes( r, g, b, ColoringAttributes.NICEST );
        app.setColoringAttributes( ca );
        // add texture
        app.setTexture( texture );
        
        // --- create Character
        Character2D newCharacter = new Character2D( character, new Tuple2f( charSize.getWidth() * getFontStretchFactor(), charSize.getHeight() * getFontStretchFactor() ) );
        
        // set geometry
        newCharacter.setGeometry( geometry );
        // set appearance
        newCharacter.setAppearance( app );
        // set charId
        //newCharacter.charId = charId;
        
        // --- add character to cache
        characterCache.put( charId, newCharacter );
        
        // --- return character
        return ( newCharacter );
    }
    
    /**
     * Creates a new Character with all attributes.
     * 
     * @param character new character
     * @param color new Color
     * @param font the font to use
     * @return returns a Node (should be casted to Character)
     */
    public static Character2D loadCharacter( char character, Colorf color, Font font )
    {
        return ( loadCharacter( character, color.getRed(), color.getGreen(), color.getBlue(), font ) );
    }
    
    /**
     * Creates a new Character with all attributes.
     * 
     * @param character new character
     * @param r new Color
     * @param g new Color
     * @param b new Color
     * @param fontName the font to use
     * @param resolution resolution to use
     * @param fontFlags fontflags to use
     * @return returns a Node (should be casted to Character)
     */
    public static Character2D loadCharacter( char character, float r, float g, float b, String fontName, int resolution, int fontFlags )
    {
        return ( loadCharacter( character, r, g, b, new Font( fontName, fontFlags, resolution ) ) );
    }
    
    public static Character2D loadCharacter( char character, Colorf color, String fontName, int resolution, int fontFlags )
    {
        return ( loadCharacter( character, color.getRed(), color.getGreen(), color.getBlue(), fontName, fontFlags, resolution ) );
    }
    
    /**
     * Creates a new Character by charId.
     * 
     * @param character new character
     * @param r new Color
     * @param g new Color
     * @param b new Color
     * @param charId new charId
     * @return returns a Node (should be casted to Character)
     */
    protected static Character2D loadCharacter( char character, float r, float g, float b, String charId )
    {
        StringTokenizer charIdAttributes = new StringTokenizer( charId, "-" );
        
        // as the charid contains the parameters we can use the following line to create the new character
        return ( loadCharacter( character, // char
                               r, g, b, // color
                               charIdAttributes.nextToken(), // font
                               Integer.parseInt( charIdAttributes.nextToken() ), // resolution
                               Integer.parseInt( charIdAttributes.nextToken() ) // fontflags
        ) );
    }
    
    /**
     * Creates a new Character by charId.
     * 
     * @param character new character
     * @param color new Color
     * @param charId new charId
     * @return returns a Node (should be casted to Character)
     */
    protected static Character2D loadCharacter( char character, Colorf color, String charId )
    {
        return ( loadCharacter( character, color.getRed(), color.getGreen(), color.getBlue(), charId ) );
    }
}
