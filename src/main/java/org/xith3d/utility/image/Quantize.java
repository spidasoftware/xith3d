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
package org.xith3d.utility.image;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.IOException;

/**
 * @author David Yazel
 */
public class Quantize
{
    private final static boolean QUICK = true;
    private final static int MAX_RGB = 255;
    private final static int MAX_NODES = 266817;
    private final static int MAX_TREE_DEPTH = 8;
    
    // these are precomputed in advance
    private static int[] SQUARES;
    private static int[] SHIFT;
    
    static
    {
        SQUARES = new int[ MAX_RGB + MAX_RGB + 1 ];
        
        for ( int i = -MAX_RGB; i <= MAX_RGB; i++ )
        {
            SQUARES[ i + MAX_RGB ] = i * i;
        }
        
        SHIFT = new int[ MAX_TREE_DEPTH + 1 ];
        
        for ( int i = 0; i < ( MAX_TREE_DEPTH + 1 ); ++i )
        {
            SHIFT[ i ] = 1 << ( 15 - i );
        }
    }
    
    /**
     * Snag the pixels from an image.
     */
    static int[][] getPixels( Image image ) throws IOException
    {
        int w = image.getWidth( null );
        int h = image.getHeight( null );
        int[] pix = new int[ w * h ];
        PixelGrabber grabber = new PixelGrabber( image, 0, 0, w, h, pix, 0, w );
        
        try
        {
            if ( grabber.grabPixels() != true )
            {
                throw new IOException( "Grabber returned false: " + grabber.status() );
            }
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }
        
        int[][] pixels = new int[ w ][ h ];
        
        for ( int x = w; x-- > 0; )
        {
            for ( int y = h; y-- > 0; )
            {
                pixels[ x ][ y ] = pix[ ( y * w ) + x ];
            }
        }
        
        return pixels;
    }
    
    /**
     * Takes a buffered image and reduces the colors to the
     * specified number
     */
    public static BufferedImage quantize( BufferedImage source, int max_colors ) throws IOException
    {
        int[][] pixels = getPixels( source );
        int[] palette = quantizeImage( pixels, max_colors );
        
        int w = source.getWidth();
        int h = source.getHeight();
        BufferedImage dest = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
        
        int[] rgbArray = new int[ w * h ];
        
        for ( int y = h - 1; y >= 0; y-- )
            for ( int x = 0; x < w; x++ )
                rgbArray[ ( y * w ) + x ] = palette[ pixels[ x ][ y ] ];
        
        dest.setRGB( 0, 0, w, h, rgbArray, 0, w );
        
        return dest;
    }
    
    /**
     * Reduce the image to the given number of colors. The pixels are
     * reduced in place.
     * @return The new color palette.
     */
    public static int[] quantizeImage( int[][] pixels, int max_colors )
    {
        Cube cube = new Cube( pixels, max_colors );
        cube.classification();
        cube.reduction();
        cube.assignment();
        
        return cube.colormap;
    }
    
    static class Cube
    {
        int[][] pixels;
        int max_colors;
        int[] colormap;
        Node root;
        int depth;
        
        // counter for the number of colors in the cube. this gets
        // recalculated often.
        int colors;
        
        // counter for the number of nodes in the tree
        int nodes;
        
        Cube( int[][] pixels, int max_colors )
        {
            this.pixels = pixels;
            this.max_colors = max_colors;
            
            int i = max_colors;
            
            // tree_depth = log max_colors
            //                 4
            for ( depth = 1; i != 0; depth++ )
            {
                i /= 4;
            }
            
            if ( depth > 1 )
            {
                --depth;
            }
            
            if ( depth > MAX_TREE_DEPTH )
            {
                depth = MAX_TREE_DEPTH;
            }
            else if ( depth < 2 )
            {
                depth = 2;
            }
            
            root = new Node( this );
        }
        
        /**
         * Procedure Classification begins by initializing a color
         * description tree of sufficient depth to represent each
         * possible input color in a leaf. However, it is impractical
         * to generate a fully-formed color description tree in the
         * classification phase for realistic values of cmax. If
         * colors components in the input image are quantized to k-bit
         * precision, so that cmax= 2k-1, the tree would need k levels
         * below the root node to allow representing each possible
         * input color in a leaf. This becomes prohibitive because the
         * tree's total number of nodes is 1 + sum(i=1,k,8k).
         *
         * A complete tree would require 19,173,961 nodes for k = 8,
         * cmax = 255. Therefore, to avoid building a fully populated
         * tree, QUANTIZE: (1) Initializes data structures for nodes
         * only as they are needed; (2) Chooses a maximum depth for
         * the tree as a function of the desired number of colors in
         * the output image (currently log2(colormap size)).
         *
         * For each pixel in the input image, classification scans
         * downward from the root of the color description tree. At
         * each level of the tree it identifies the single node which
         * represents a cube in RGB space containing It updates the
         * following data for each such node:
         *
         *   number_pixels : Number of pixels whose color is contained
         *   in the RGB cube which this node represents;
         *
         *   unique : Number of pixels whose color is not represented
         *   in a node at lower depth in the tree; initially, n2 = 0
         *   for all nodes except leaves of the tree.
         *
         *   total_red/green/blue : Sums of the red, green, and blue
         *   component values for all pixels not classified at a lower
         *   depth. The combination of these sums and n2 will
         *   ultimately characterize the mean color of a set of pixels
         *   represented by this node.
         */
        void classification()
        {
            int[][] pixels = this.pixels;
            
            int width = pixels.length;
            int height = pixels[ 0 ].length;
            
            // convert to indexed color
            for ( int x = width; x-- > 0; )
            {
                for ( int y = height; y-- > 0; )
                {
                    int pixel = pixels[ x ][ y ];
                    int red = ( pixel >> 16 ) & 0xFF;
                    int green = ( pixel >> 8 ) & 0xFF;
                    int blue = ( pixel >> 0 ) & 0xFF;
                    
                    // a hard limit on the number of nodes in the tree
                    if ( nodes > MAX_NODES )
                    {
                        System.out.println( "pruning" );
                        root.pruneLevel();
                        --depth;
                    }
                    
                    // walk the tree to depth, increasing the
                    // number_pixels count for each node
                    Node node = root;
                    
                    for ( int level = 1; level <= depth; ++level )
                    {
                        int id = ( ( ( ( red > node.mid_red ) ? 1 : 0 ) << 0 ) | ( ( ( green > node.mid_green ) ? 1 : 0 ) << 1 ) | ( ( ( blue > node.mid_blue ) ? 1 : 0 ) << 2 ) );
                        
                        if ( node.child[ id ] == null )
                        {
                            new Node( node, id, level );
                        }
                        
                        node = node.child[ id ];
                        node.number_pixels += SHIFT[ level ];
                    }
                    
                    ++node.unique;
                    node.total_red += red;
                    node.total_green += green;
                    node.total_blue += blue;
                }
            }
        }
        
        /*
         * reduction repeatedly prunes the tree until the number of
         * nodes with unique > 0 is less than or equal to the maximum
         * number of colors allowed in the output image.
         *
         * When a node to be pruned has offspring, the pruning
         * procedure invokes itself recursively in order to prune the
         * tree from the leaves upward.  The statistics of the node
         * being pruned are always added to the corresponding data in
         * that node's parent.  This retains the pruned node's color
         * characteristics for later averaging.
         */
        void reduction()
        {
            int threshold = 1;
            
            while ( colors > max_colors )
            {
                colors = 0;
                threshold = root.reduce( threshold, Integer.MAX_VALUE );
            }
        }
        
        /**
         * Procedure assignment generates the output image from the
         * pruned tree. The output image consists of two parts: (1) A
         * color map, which is an array of color descriptions (RGB
         * triples) for each color present in the output image; (2) A
         * pixel array, which represents each pixel as an index into
         * the color map array.
         *
         * First, the assignment phase makes one pass over the pruned
         * color description tree to establish the image's color map.
         * For each node with n2 > 0, it divides Sr, Sg, and Sb by n2.
         * This produces the mean color of all pixels that classify no
         * lower than this node. Each of these colors becomes an entry
         * in the color map.
         *
         * Finally, the assignment phase reclassifies each pixel in
         * the pruned tree to identify the deepest node containing the
         * pixel's color. The pixel's value in the pixel array becomes
         * the index of this node's mean color in the color map.
         */
        void assignment()
        {
            colormap = new int[ colors ];
            
            colors = 0;
            root.colormap();
            
            int[][] pixels = this.pixels;
            
            int width = pixels.length;
            int height = pixels[ 0 ].length;
            
            Search search = new Search();
            
            // convert to indexed color
            for ( int x = width; x-- > 0; )
            {
                for ( int y = height; y-- > 0; )
                {
                    int pixel = pixels[ x ][ y ];
                    int red = ( pixel >> 16 ) & 0xFF;
                    int green = ( pixel >> 8 ) & 0xFF;
                    int blue = ( pixel >> 0 ) & 0xFF;
                    
                    // walk the tree to find the cube containing that color
                    Node node = root;
                    
                    for ( ;; )
                    {
                        int id = ( ( ( ( red > node.mid_red ) ? 1 : 0 ) << 0 ) | ( ( ( green > node.mid_green ) ? 1 : 0 ) << 1 ) | ( ( ( blue > node.mid_blue ) ? 1 : 0 ) << 2 ) );
                        
                        if ( node.child[ id ] == null )
                        {
                            break;
                        }
                        
                        node = node.child[ id ];
                    }
                    
                    if ( QUICK )
                    {
                        // if QUICK is set, just use that
                        // node. Strictly speaking, this isn't
                        // necessarily best match.
                        pixels[ x ][ y ] = node.color_number;
                    }
                    else
                    {
                        // Find the closest color.
                        search.distance = Integer.MAX_VALUE;
                        node.parent.closestColor( red, green, blue, search );
                        pixels[ x ][ y ] = search.color_number;
                    }
                }
            }
        }
        
        /**
         * The result of a closest color search.
         */
        static class Search
        {
            int distance;
            int color_number;
        }
        
        /**
         * A single Node in the tree.
         */
        static class Node
        {
            Cube cube;
            
            // parent node
            Node parent;
            
            // child nodes
            Node[] child;
            int nchild;
            
            // our index within our parent
            int id;
            
            // our level within the tree
            int level;
            
            // our color midpoint
            int mid_red;
            int mid_green;
            int mid_blue;
            
            // the pixel count for this node and all children
            int number_pixels;
            
            // the pixel count for this node
            int unique;
            
            // the sum of all pixels contained in this node
            int total_red;
            int total_green;
            int total_blue;
            
            // used to build the colormap
            int color_number;
            
            Node( Cube cube )
            {
                this.cube = cube;
                this.parent = this;
                this.child = new Node[ 8 ];
                this.id = 0;
                this.level = 0;
                
                this.number_pixels = Integer.MAX_VALUE;
                
                this.mid_red = ( MAX_RGB + 1 ) >> 1;
                this.mid_green = ( MAX_RGB + 1 ) >> 1;
                this.mid_blue = ( MAX_RGB + 1 ) >> 1;
            }
            
            Node( Node parent, int id, int level )
            {
                this.cube = parent.cube;
                this.parent = parent;
                this.child = new Node[ 8 ];
                this.id = id;
                this.level = level;
                
                ++cube.nodes;
                
                if ( level == cube.depth )
                {
                    ++cube.colors;
                }
                
                ++parent.nchild;
                parent.child[ id ] = this;
                
                // figure out our midpoint
                int bi = ( 1 << ( MAX_TREE_DEPTH - level ) ) >> 1;
                mid_red = parent.mid_red + ( ( ( id & 1 ) > 0 ) ? bi : ( -bi ) );
                mid_green = parent.mid_green + ( ( ( id & 2 ) > 0 ) ? bi : ( -bi ) );
                mid_blue = parent.mid_blue + ( ( ( id & 4 ) > 0 ) ? bi : ( -bi ) );
            }
            
            /**
             * Remove this child node, and make sure our parent
             * absorbs our pixel statistics.
             */
            void pruneChild()
            {
                --parent.nchild;
                parent.unique += unique;
                parent.total_red += total_red;
                parent.total_green += total_green;
                parent.total_blue += total_blue;
                parent.child[ id ] = null;
                --cube.nodes;
                cube = null;
                parent = null;
            }
            
            /**
             * Prune the lowest layer of the tree.
             */
            void pruneLevel()
            {
                if ( nchild != 0 )
                {
                    for ( int id = 0; id < 8; id++ )
                    {
                        if ( child[ id ] != null )
                        {
                            child[ id ].pruneLevel();
                        }
                    }
                }
                
                if ( level == cube.depth )
                {
                    pruneChild();
                }
            }
            
            /**
             * Remove any nodes that have fewer than threshold
             * pixels. Also, as long as we're walking the tree:
             *
             *  - figure out the color with the fewest pixels
             *  - recalculate the total number of colors in the tree
             */
            int reduce( int threshold, int next_threshold )
            {
                if ( nchild != 0 )
                {
                    for ( int id = 0; id < 8; id++ )
                    {
                        if ( child[ id ] != null )
                        {
                            next_threshold = child[ id ].reduce( threshold, next_threshold );
                        }
                    }
                }
                
                if ( number_pixels <= threshold )
                {
                    pruneChild();
                }
                else
                {
                    if ( unique != 0 )
                    {
                        cube.colors++;
                    }
                    
                    if ( number_pixels < next_threshold )
                    {
                        next_threshold = number_pixels;
                    }
                }
                
                return next_threshold;
            }
            
            /**
             * colormap traverses the color cube tree and notes each
             * colormap entry. A colormap entry is any node in the
             * color cube tree where the number of unique colors is
             * not zero.
             */
            void colormap()
            {
                if ( nchild != 0 )
                {
                    for ( int id = 0; id < 8; id++ )
                    {
                        if ( child[ id ] != null )
                        {
                            child[ id ].colormap();
                        }
                    }
                }
                
                if ( unique != 0 )
                {
                    int r = ( ( total_red + ( unique >> 1 ) ) / unique );
                    int g = ( ( total_green + ( unique >> 1 ) ) / unique );
                    int b = ( ( total_blue + ( unique >> 1 ) ) / unique );
                    cube.colormap[ cube.colors ] = ( ( ( 0xFF ) << 24 ) | ( ( r & 0xFF ) << 16 ) | ( ( g & 0xFF ) << 8 ) | ( ( b & 0xFF ) << 0 ) );
                    color_number = cube.colors++;
                }
            }
            
            /**
             * ClosestColor traverses the color cube tree at a
             * particular node and determines which colormap entry
             * best represents the input color.
             */
            void closestColor( int red, int green, int blue, Search search )
            {
                if ( nchild != 0 )
                {
                    for ( int id = 0; id < 8; id++ )
                    {
                        if ( child[ id ] != null )
                        {
                            child[ id ].closestColor( red, green, blue, search );
                        }
                    }
                }
                
                if ( unique != 0 )
                {
                    int color = cube.colormap[ color_number ];
                    int distance = distance( color, red, green, blue );
                    
                    if ( distance < search.distance )
                    {
                        search.distance = distance;
                        search.color_number = color_number;
                    }
                }
            }
            
            /**
             * Figure out the distance between this node and som color.
             */
            final static int distance( int color, int r, int g, int b )
            {
                return ( SQUARES[ ( ( color >> 16 ) & 0xFF ) - r + MAX_RGB ] + SQUARES[ ( ( color >> 8 ) & 0xFF ) - g + MAX_RGB ] + SQUARES[ ( ( color >> 0 ) & 0xFF ) - b + MAX_RGB ] );
            }
            
            @Override
            public String toString()
            {
                StringBuffer buf = new StringBuffer();
                
                if ( parent == this )
                {
                    buf.append( "root" );
                }
                else
                {
                    buf.append( "node" );
                }
                
                buf.append( ' ' );
                buf.append( level );
                buf.append( " [" );
                buf.append( mid_red );
                buf.append( ',' );
                buf.append( mid_green );
                buf.append( ',' );
                buf.append( mid_blue );
                buf.append( ']' );
                
                return new String( buf );
            }
        }
    }
}
