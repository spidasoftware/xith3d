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
package org.xith3d.terrain;

import javax.imageio.ImageIO;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import org.openmali.vecmath2.Vector3f;

/**
 * @author Mathias 'cylab' Henze
 */
public class HeightMapSampler implements GridSampler
{
    public enum Type
    {
        RAW_8, RAW_16
    }
    private float[][] map;
    private int sDim;
    private int tDim;
    private float offset = 0;
    private float scale = 1;
    private float s1 = 0.0f;
    private float t1 = 0.0f;
    private float s2 = 1.0f;
    private float t2 = 1.0f;

    public HeightMapSampler( URL resource ) throws IOException
    {
        String loc = resource.toString();
        if( loc.endsWith( ".hfz" ) || loc.endsWith( ".gz" ) )
        {
            HF2Map hf = new HF2Map( resource );
            sDim = (int) hf.getWidth();
            tDim = (int) hf.getHeight();
            map = hf.getData();
            offset = hf.getMin();
            scale = 1f / (hf.getMax() - hf.getMin());
        }
        else
        {
            BufferedImage image = ImageIO.read( resource );
            sDim = image.getWidth( null );
            tDim = image.getHeight( null );
            PixelGrabber grabber = new PixelGrabber( image, 0, 0, sDim, tDim, true );
            try
            {
                grabber.grabPixels( 0 );
            }
            catch( InterruptedException e )
            {
                e.printStackTrace();
            }
            int[] source = (int[]) grabber.getPixels();
            float[][] result = new float[sDim][tDim];
            int i = 0;
            for( int t = tDim - 1; t >= 0; t-- )
            {
                for( int s = 0; s < sDim; s++ )
                {
                    int data = source[i++];
                    result[s][t] = (float) (((data & 0x00ff0000) >> 16) + ((data & 0xff00) >> 8) + (data & 0xff)) / 3 / 255f;
                }
            }
            this.map = result;
        }
    }

    public HeightMapSampler( URL resource, int sDim, int tDim ) throws IOException
    {
        this( resource, sDim, tDim, Type.RAW_8 );
    }

    public HeightMapSampler( URL resource, int sDim, int tDim, Type type ) throws IOException
    {
        this.sDim = sDim;
        this.tDim = tDim;
        InputStream in = null;
        float[][] result = new float[sDim][tDim];
        try
        {
            in = resource.openStream();
            for( int t = tDim - 1; t >= 0; t-- )
            {
                for( int s = 0; s < sDim; s++ )
                {
                    int highbyte = 0;
                    int lowbyte = 0;
                    if( type == Type.RAW_16 )
                    {
                        lowbyte = in.read();
                        if( lowbyte == -1 )
                        {
                            throw (new IOException( "EOF" ));
                        }
                    }
                    highbyte = in.read();
                    if( highbyte == -1 )
                    {
                        throw (new IOException( "EOF" ));
                    }
                    result[s][t] = ((float) (((highbyte & 0xff) << 8) + (lowbyte & 0xff))) / 65535f;
                }
            }
        }
        finally
        {
            try
            {
                if( in != null )
                {
                    in.close();
                }
            }
            catch( Exception ignore )
            {
            }
        }
        this.map = result;
    }

    public HeightMapSampler( float[][] map)
    {
        this.sDim = map.length;
        this.tDim = map[0].length;
        this.map = map;
    }

    public HeightMapSampler(int sDim, int tDim)
    {
        this.sDim = sDim;
        this.tDim = tDim;
        this.map = new float[sDim][tDim];
    }


    public HeightMapSampler( GridResourceSpec<GridSampler> spec ) throws IOException
    {
        this( spec.getLocations()[0] );
        s1 = spec.getS1();
        s2 = spec.getS2();
        t1 = spec.getT1();
        t2 = spec.getT2();
        float min = spec.getMin();
        float max = spec.getMax();
        if( min < max )
        {
            offset = min;
            scale = 1f / (max - min);
        }
    }

    public void release()
    {

    }

    public float sampleHeight( float s, float t )
    {
        s = (1f / (s2 - s1)) * (s - s1);
        t = (1f / (t2 - t1)) * (t - t1);
        int sIndex = Math.max( 0, Math.min( (int) (s * sDim), sDim - 1 ) );
        int tIndex = Math.max( 0, Math.min( (int) (t * tDim), tDim - 1 ) );

        float sr = s * sDim - sIndex;
        float tr = t * tDim - tIndex;
        float y = 0;
        // if we are not at the edges of the map, interpolate the height values
        if( sIndex < sDim - 1 && tIndex < tDim - 1 )
        {
            y = map[sIndex][tIndex] * (1 - sr) * (1 - tr);
            y += map[sIndex + 1][tIndex] * sr * (1 - tr);
            y += map[sIndex][tIndex + 1] * (1 - sr) * tr;
            y += map[sIndex + 1][tIndex + 1] * sr * tr;
        }
        // at the edges of the map, just take the absolute value. 
        else
        {
            y = map[sIndex][tIndex];
        }

        return (Math.min( (y - offset) * scale, 1 ));
    }

    /**
     * Returns the backing two dimensional float array for direct manipulation.
     * @return The backing two dimensional float array 
     */
    public float[][] getMap()
    {
        return map;
    }

    public int getSDim()
    {
        return sDim;
    }

    public int getTDim()
    {
        return tDim;
    }

    public float getOffset()
    {
        return offset;
    }

    public void setOffset(float offset)
    {
        this.offset = offset;
    }

    public float getScale()
    {
        return scale;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public float getS1()
    {
        return s1;
    }

    public void setS1(float s1)
    {
        this.s1 = s1;
    }

    public float getS2()
    {
        return s2;
    }

    public void setS2(float s2)
    {
        this.s2 = s2;
    }

    public float getT1()
    {
        return t1;
    }

    public void setT1(float t1)
    {
        this.t1 = t1;
    }

    public float getT2()
    {
        return t2;
    }

    public void setT2(float t2)
    {
        this.t2 = t2;
    }

    public Vector3f sampleBinormal( float s, float t )
    {
        return new Vector3f(1f,0,0);
    }

    public Vector3f sampleNormal( float s, float t )
    {
        return new Vector3f(0,1f,0);
    }

    public Vector3f sampleTangent( float s, float t )
    {
        return new Vector3f(0,0,1f);
    }
    
}
