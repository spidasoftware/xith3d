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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

/**
 * @author Mathias 'cylab' Henze
 */
public class HF2Map
{
    public static enum Compression{ NONE, GZIP };

    private String fileId;
    private int version;
    private long width;
    private long height;
    private int tileSize;
    private float precision;
    private float horizontalScale;
    private float min;
    private float max;
    private float[][] data;

    public float[][] getData()
    {
        return data;
    }

    public String getFileId()
    {
        return fileId;
    }

    public long getHeight()
    {
        return height;
    }

    public float getHorizontalScale()
    {
        return horizontalScale;
    }

    public float getPrecision()
    {
        return precision;
    }

    public int getTileSize()
    {
        return tileSize;
    }

    public int getVersion()
    {
        return version;
    }

    public long getWidth()
    {
        return width;
    }

    public float getMax()
    {
        return max;
    }

    public float getMin()
    {
        return min;
    }


    private void load(URL location) throws IOException
    {
        InputStream in = null;
        try
        {
            in = location.openStream();
            final String loc = location.toString().toLowerCase();
            load(in,(loc.endsWith(".hfz")||loc.endsWith(".gz"))?Compression.GZIP:Compression.NONE);
        }
        finally
        {
            if( in != null ) try { in.close(); } catch( Exception ignore ){}
        }

    }

    private void load(InputStream in, Compression compression) throws IOException
    {
        MemoryCacheImageInputStream stream= null;
        try
        {
            stream = new MemoryCacheImageInputStream(compression==Compression.NONE?in:new GZIPInputStream(in));
            stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            // File ID  pos:0  size:4  type:string 
            // Should be �HF2� (null terminated)
            byte[] id= new byte[]
            {
                stream.readByte(),
                stream.readByte(),
                stream.readByte(),
            };
            if(stream.readByte()!=0 || !"HF2".equals(fileId= new String(id,"ISO-8859-15")))
            {
                throw new IOException("Unsupported format '"+fileId+"'!");
            }

            // Version no.  pos:4  size:2  type:unsigned short
            // Should be 0.
            version = stream.readUnsignedShort();
            if(version!=0)
            {
                throw new IOException("Unsupported version '"+version+"'!");
            }

            // Width  pos:6  size:4  type:unsigned int (32bit)
            // Width of map in pixels.
            width= stream.readUnsignedInt();

            // Height  pos:10  size:4  type:unsigned int (32bit)
            // Height of map in pixels.
            height= stream.readUnsignedInt();

            // Tile size  pos:14  size:2  type:unsigned short
            // Size of internal map tiles (8?65535). Default is 256.
            tileSize= stream.readUnsignedShort();

            // Vert. precis.  pos:16  size:4  type:float
            // Precision of vertical scale, in metres. Must be greater than zero. Default is 0.01.
            precision= stream.readFloat();

            // Horiz. scale  pos:20  size:4  type:float           
            // Horizontal pixel spacing, in metres. Must be greater than zero. Default is 1.
            horizontalScale= stream.readFloat();

            // Ext. header length  pos:24  size:4  type:unsigned int(32bit)
            // Length of extended header, in bytes. Zero is default.
            long extHeaderLength= stream.readUnsignedInt();

            if((width%tileSize)>0 || (height%tileSize)>0)
            {
                throw new IOException("Currently only files are supported, which tile size is a whole number divisor of the map size.");
            }

            stream.skipBytes(extHeaderLength);
            stream.flush();

            int htiles= (int)(width/tileSize)+((width%tileSize)>0?1:0);
            int vtiles= (int)(height/tileSize)+((height%tileSize)>0?1:0);
            data= new float[(int)width][(int)height];
            min= Float.MAX_VALUE;
            max= Float.MIN_VALUE;
            for( int v = 0; v < vtiles; v++ )
            {
                for( int h = 0; h < htiles; h++ )
                {
                    // Vert. scale  pos:0  size:4  type:float
                    // The vertical scaling of the data in the tile.
                    float vScale = stream.readFloat();
                    // Vert. offset  pos:4  size:4 	type:float
                    // The vertical offset of the data in the tile.
                    float vOffset = stream.readFloat();

                    for( int z = 0; z < tileSize; z++ )
                    {
                        // Byte depth  pos:0  size:1  type:byte
                        // The byte-depth used for difference encoding in the line. May be 1, 2 or 4.
                        byte bDepth= stream.readByte();
                        // Start value  pos:1  size:4  type:long
                        // The starting value of the line
                        long currentValue= stream.readUnsignedInt();
                        float value= (float)currentValue * vScale + vOffset;
                        if(value<min) min=value;
                        if(value>max) max=value;
                        data[h*tileSize][v*tileSize+z] = value;
                        for( int x = 1; x < tileSize; x++ )
                        {
                            currentValue+= bDepth==4?stream.readInt():(bDepth==2?stream.readShort():stream.readByte());
                            value= (float)currentValue * vScale + vOffset;
                            if(value<min) min=value;
                            if(value>max) max=value;
                            data[h*tileSize+x][v*tileSize+z] = value;
                        }
                        stream.flush();
                    }
                }
            }
        }
        finally
        {
            if( stream != null ) try { stream.close(); } catch( Exception ignore ){}
        }
    }

    public HF2Map( InputStream in, Compression compression ) throws IOException
    {
        load(in,compression);
    }

    public HF2Map( URL location ) throws IOException
    {
        load(location);
    }
}
