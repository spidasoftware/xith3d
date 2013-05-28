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
package org.xith3d.loaders.sound.impl.ogg;

import java.io.BufferedInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import org.xith3d.sound.BufferFormat;
import org.xith3d.sound.SoundBuffer;
import org.xith3d.sound.SoundContainer;
import org.xith3d.sound.SoundDriver;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

/**
 * Insert type comment here.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 */
public class OggSoundContainer implements SoundContainer
{
    private ByteBuffer bbuffer;
    private SoundBuffer buffer = null;
    
    private static final boolean needs_byte_swap = ( ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN );
    
    // Ogg File Variables
    private byte[] buf = null;
    private int bytes = 0;
    private static int bufferMultiple_ = 4;
    /**
     * The File Load Play Buffer.
     */
    private static int bufferSize_ = bufferMultiple_ * 256 * 2 * 10;
    /**
     * Description of the Field
     */
    private static int convsize = bufferSize_ * 2;
    
    private byte[] convbuffer = new byte[ convsize ];
    private BufferedInputStream oggBitStream_;
    
    private SyncState oggSyncState_;
    private StreamState oggStreamState_;
    private Page oggPage_;
    private Packet oggPacket_;
    private Info vorbisInfo;
    private Comment vorbisComment;
    private DspState vorbisDspState;
    private Block vorbisBlock;
    private int index = 0;
    private long calcLength = 0;
    private float volumeMultiplier = 1;
    
    public int getNumChannels()
    {
        return ( vorbisInfo.channels  );
    }
    
    public void setVolumeMultiplier( float v )
    {
        volumeMultiplier = v;
    }
    
    public int getFreq()
    {
        return ( vorbisInfo.rate  );
    }
    
    /**
     * Can only be called after you get the data. Otherwise it will just return
     * zero.
     * 
     * @return The decoded size of the sound stream.
     */
    public long getDecodedSize()
    {
        return ( calcLength );
    }
    
    /**
     * Currently always returns false, because streaming is not supported.
     */
    public boolean isStreaming()
    {
        return ( false );
    }
    
    /**
     * Reads from the oggBitStream_ a specified number of Bytes(bufferSize_)
     * worth sarting at index and puts them in the specified buffer[].
     * 
     * @param buffer
     * @param index
     * @param bufferSize_
     * @return the number of bytes read or -1 if error.
     */
    private int readFromStream( byte[] buffer, int index, int bufferSize_ )
    {
        int bytes = 0;
        try
        {
            bytes = oggBitStream_.read( buffer, index, bufferSize_ );
        }
        catch ( Exception e )
        {
            System.out.println( "Cannot read from file" );
            bytes = -1;
        }
        
        return ( bytes );
    }
    
    /**
     * {@inheritDoc}
     */
    public SoundBuffer getData( SoundDriver driver )
    {
        if ( buffer != null )
            return ( buffer );
        
        // the buffer is not allocated in advance
        // int size = (int)getDecodedSize();
        // byte buf[] = new byte[size];
        // bbuffer = ByteBuffer.allocateDirect(size);
        // bbuffer.order(ByteOrder.nativeOrder());
        
        // contains the raw data
        LinkedList< byte[] > chunks = new LinkedList< byte[] >();
        
        int totalBytes = 0;
        int curBytes = 0;
        
        vorbisDspState.synthesis_init( vorbisInfo );
        vorbisBlock.init( vorbisDspState );
        int eos = 0;
        float[][][] _pcmf = new float[ 1 ][][];
        int[] _index = new int[ vorbisInfo.channels ];
        while ( eos == 0 )
        {
            while ( eos == 0 )
            {
                int result = oggSyncState_.pageout( oggPage_ );
                if ( result == 0 )
                {
                    break;
                } // need more data
                if ( result == -1 )
                { // missing or corrupt data at this page position
                    System.err.println( "Corrupt or missing data in bitstream; " + "continuing..." );
                }
                else
                {
                    oggStreamState_.pagein( oggPage_ );
                    while ( true )
                    {
                        result = oggStreamState_.packetout( oggPacket_ );
                        if ( result == 0 )
                        {
                            break;
                        } // need more data
                        if ( result == -1 )
                        { // missing or corrupt data at this page position
                            // no reason to complain; already complained above
                        }
                        else
                        {
                            // we have a packet. Decode it
                            int samples;
                            if ( vorbisBlock.synthesis( oggPacket_ ) == 0 )
                            { // test for success!
                                vorbisDspState.synthesis_blockin( vorbisBlock );
                            }
                            while ( ( samples = vorbisDspState.synthesis_pcmout( _pcmf, _index ) ) > 0 )
                            {
                                float[][] pcmf = _pcmf[ 0 ];
                                int bout = ( samples < convsize ? samples : convsize );
                                double fVal = 0.0;
                                // convert doubles to 16 bit signed ints (host
                                // order) and
                                // interleave
                                for ( int i = 0; i < vorbisInfo.channels; i++ )
                                {
                                    int pointer = i * 2;
                                    // int ptr=i;
                                    int mono = _index[ i ];
                                    for ( int j = 0; j < bout; j++ )
                                    {
                                        fVal = (float)pcmf[ i ][ mono + j ] * 32767.;
                                        /*
                                         * volume Adjust
                                         */
                                        fVal = fVal * volumeMultiplier;
                                        int val = (int)( fVal );
                                        if ( val > 32767 )
                                        {
                                            val = 32767;
                                        }
                                        if ( val < -32768 )
                                        {
                                            val = -32768;
                                        }
                                        if ( val < 0 )
                                        {
                                            val = val | 0x8000;
                                        }
                                        if ( needs_byte_swap )
                                        {
                                            convbuffer[ pointer + 1 ] = (byte)( val );
                                            convbuffer[ pointer + 0 ] = (byte)( val >>> 8 );
                                        }
                                        else
                                        {
                                            convbuffer[ pointer + 0 ] = (byte)( val );
                                            convbuffer[ pointer + 1 ] = (byte)( val >>> 8 );
                                        }
                                        pointer += 2 * ( vorbisInfo.channels );
                                    }
                                }
                                
                                curBytes = 2 * vorbisInfo.channels * bout;
                                totalBytes += curBytes;
                                // bbuffer.put(convbuffer,0,curBytes);
                                
                                // hold the chunks in a LinkedList
                                byte[] chunk = new byte[ curBytes ];
                                System.arraycopy( convbuffer, 0, chunk, 0, curBytes );
                                chunks.add( chunk );
                                
                                /*
                                 * outputLine.write(convbuffer, 0, 2 *
                                 * vorbisInfo.channels * bout);
                                 * System.out.println("bytes = "+curBytes+",
                                 * total = "+totalBytes);
                                 */
                                vorbisDspState.synthesis_read( bout );
                            }
                        }
                    }
                    
                    if ( oggPage_.eos() != 0 )
                    {
                        eos = 1;
                    }
                }
            }
            
            if ( eos == 0 )
            {
                index = oggSyncState_.buffer( bufferSize_ );
                buf = oggSyncState_.data;
                bytes = readFromStream( buf, index, bufferSize_ );
                if ( bytes == -1 )
                {
                    eos = 1;
                }
                else
                {
                    oggSyncState_.wrote( bytes );
                    if ( bytes == 0 )
                    {
                        eos = 1;
                    }
                }
            }
        }
        
        oggStreamState_.clear();
        vorbisBlock.clear();
        vorbisDspState.clear();
        vorbisInfo.clear();
        
        buffer = driver.allocateSoundBuffer();
        BufferFormat format = BufferFormat.getFromValues( 16, getNumChannels() );
        
        // calculate the length and fill the buffer
        calcLength = totalBytes;
        bbuffer = ByteBuffer.allocateDirect( totalBytes );
        bbuffer.order( ByteOrder.nativeOrder() );
        
        // loop through the chunks and put them in the buffer
        for ( byte[] chunk: chunks )
            bbuffer.put( chunk );
        
        buffer.setData( format, totalBytes, getFreq(), bbuffer );
        
        return ( buffer );
    }
    
    /**
     * {@inheritDoc}
     */
    public void returnData( SoundDriver driver, SoundBuffer buffer )
    {
    }
    
    /**
     * {@inheritDoc}
     */
    public void rewind( SoundDriver driver )
    {
    }
    
    private void load()
    {
        OggSoundContainer osc = this;
        
        osc.index = osc.oggSyncState_.buffer( OggSoundContainer.bufferSize_ );
        osc.buf = osc.oggSyncState_.data;
        osc.bytes = osc.readFromStream( osc.buf, osc.index, OggSoundContainer.bufferSize_ );
        
        if ( osc.bytes == -1 )
        {
            System.err.println( "Cannot get any data from selected Ogg bitstream." );
            return;
        }
        
        osc.oggSyncState_.wrote( osc.bytes );
        if ( osc.oggSyncState_.pageout( osc.oggPage_ ) != 1 )
        {
            if ( osc.bytes < OggSoundContainer.bufferSize_ )
            {
                return;
            }
            
            System.err.println( "Input does not appear to be an Ogg bitstream." );
            return;
        }
        
        osc.oggStreamState_.init( osc.oggPage_.serialno() );
        osc.vorbisInfo.init();
        osc.vorbisComment.init();
        if ( osc.oggStreamState_.pagein( osc.oggPage_ ) < 0 )
        {
            // error; stream version mismatch perhaps
            System.err.println( "Error reading first page of Ogg bitstream data." );
            return;
        }
        
        if ( osc.oggStreamState_.packetout( osc.oggPacket_ ) != 1 )
        {
            // no page? must not be vorbis
            System.err.println( "Error reading initial header packet." );
            return;
        }
        
        if ( osc.vorbisInfo.synthesis_headerin( osc.vorbisComment, osc.oggPacket_ ) < 0 )
        {
            // error case; not a vorbis header
            System.err.println( "This Ogg bitstream does not contain Vorbis audio data." );
            return;
        }
        
        int i = 0;
        while ( i < 2 )
        {
            while ( i < 2 )
            {
                int result = osc.oggSyncState_.pageout( osc.oggPage_ );
                if ( result == 0 )
                {
                    break;
                } // Need more data
                if ( result == 1 )
                {
                    osc.oggStreamState_.pagein( osc.oggPage_ );
                    while ( i < 2 )
                    {
                        result = osc.oggStreamState_.packetout( osc.oggPacket_ );
                        if ( result == 0 )
                        {
                            break;
                        }
                        if ( result == -1 )
                        {
                            System.err.println( "Corrupt secondary header.  Exiting." );
                            return;
                        }
                        osc.vorbisInfo.synthesis_headerin( osc.vorbisComment, osc.oggPacket_ );
                        i++;
                    }
                }
            }
            osc.index = osc.oggSyncState_.buffer( OggSoundContainer.bufferSize_ );
            osc.buf = osc.oggSyncState_.data;
            osc.bytes = osc.readFromStream( osc.buf, osc.index, OggSoundContainer.bufferSize_ );
            if ( osc.bytes == -1 )
            {
                break;
            }
            if ( osc.bytes == 0 && i < 2 )
            {
                System.err.println( "End of file before finding all Vorbis  headers!" );
                return;
            }
            osc.oggSyncState_.wrote( osc.bytes );
        }
        
        OggSoundContainer.convsize = OggSoundContainer.bufferSize_ / osc.vorbisInfo.channels;
        //X3DLog.debug( "convsize = " + osc.convsize );
        // the length is not calculated here anymore, we count the length
        // when we get the data
        // calcLength = file.pcm_total(-1) * getNumChannels() * 2;
    }
    
    /**
     * Initializes all the jOrbis and jOgg vars that are used for song playback.
     */
    OggSoundContainer( BufferedInputStream oggBitStream )
    {
        this.oggSyncState_ = new SyncState();
        this.oggStreamState_ = new StreamState();
        this.oggPage_ = new Page();
        this.oggPacket_ = new Packet();
        this.vorbisInfo = new Info();
        this.vorbisComment = new Comment();
        this.vorbisDspState = new DspState();
        this.vorbisBlock = new Block( vorbisDspState );
        this.buffer = null;
        this.bytes = 0;
        
        oggSyncState_.init();
        
        this.oggBitStream_ = oggBitStream;
        
        load();
    }
}
