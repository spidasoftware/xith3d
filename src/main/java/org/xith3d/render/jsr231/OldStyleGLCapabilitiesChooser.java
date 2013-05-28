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
package org.xith3d.render.jsr231;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLCapabilitiesChooser;
import javax.media.opengl.GLException;

import org.xith3d.utility.logging.X3DLog;

/**
 * <P>
 * The default implementation of the {@link GLCapabilitiesChooser} interface,
 * which provides consistent visual selection behavior across platforms. The
 * precise algorithm is deliberately left loosely specified. Some properties
 * are:
 * </P>
 * 
 * <UL>
 * 
 * <LI> As long as there is at least one available non-null GLCapabilities which
 * matches the "stereo" option, will return a valid index.
 * 
 * <LI> Attempts to match as closely as possible the given GLCapabilities, but
 * will select one with fewer capabilities (i.e., lower color depth) if
 * necessary.
 * 
 * <LI> Prefers hardware-accelerated visuals to non-hardware-accelerated.
 * 
 * <LI> If there is no exact match, prefers a more-capable visual to a
 * less-capable one.
 * 
 * <LI> If there is more than one exact match, chooses an arbitrary one.
 * 
 * <LI> May select the opposite of a double- or single-buffered visual (based on
 * the user's request) in dire situations.
 * 
 * <LI> Color depth (including alpha) mismatches are weighted higher than depth
 * buffer mismatches, which are in turn weighted higher than accumulation buffer
 * (including alpha) and stencil buffer depth mismatches.
 * 
 * </UL>
 * 
 * @author Yuri Vl. Guschchin
 */
public class OldStyleGLCapabilitiesChooser implements GLCapabilitiesChooser
{
    public int chooseCapabilities( GLCapabilities desired, GLCapabilities[] available, int windowSystemRecommendedChoice )
    {
        // Create score array
        int[] scores = new int[ available.length ];
        int NO_SCORE = -9999999;
        int DOUBLE_BUFFER_MISMATCH_PENALTY = 1000;
        int STENCIL_MISMATCH_PENALTY = 500;
        int SAMPLE_BUFFERS_MISMATCH_PENALTY = 100;
        int NUM_SAMPLES_MISMATCH_PENALTY = 10;
        // Pseudo attempt to keep equal rank penalties scale-equivalent
        // (e.g., stencil mismatch is 3 * accum because there are 3 accum
        // components)
        int COLOR_MISMATCH_PENALTY_SCALE = 36;
        int DEPTH_MISMATCH_PENALTY_SCALE = 6;
        int ACCUM_MISMATCH_PENALTY_SCALE = 1;
        int STENCIL_MISMATCH_PENALTY_SCALE = 3;
        for ( int i = 0; i < scores.length; i++ )
        {
            scores[ i ] = NO_SCORE;
        }
        // Compute score for each
        for ( int i = 0; i < scores.length; i++ )
        {
            GLCapabilities cur = available[ i ];
            if ( cur == null )
            {
                continue;
            }
            if ( desired.getStereo() != cur.getStereo() )
            {
                continue;
            }
            int score = 0;
            // Compute difference in color depth
            // (Note that this decides the direction of all other penalties)
            score += ( COLOR_MISMATCH_PENALTY_SCALE * ( ( cur.getRedBits() + cur.getGreenBits() + cur.getBlueBits() + cur.getAlphaBits() ) - ( desired.getRedBits() + desired.getGreenBits() + desired.getBlueBits() + desired.getAlphaBits() ) ) );
            // Compute difference in depth buffer depth
            score += ( DEPTH_MISMATCH_PENALTY_SCALE * sign( score ) * Math.abs( cur.getDepthBits() - desired.getDepthBits() ) );
            // Compute difference in accumulation buffer depth
            score += ( ACCUM_MISMATCH_PENALTY_SCALE * sign( score ) * Math.abs( ( cur.getAccumRedBits() + cur.getAccumGreenBits() + cur.getAccumBlueBits() + cur.getAccumAlphaBits() ) - ( desired.getAccumRedBits() + desired.getAccumGreenBits() + desired.getAccumBlueBits() + desired.getAccumAlphaBits() ) ) );
            // Compute difference in stencil bits
            score += STENCIL_MISMATCH_PENALTY_SCALE * sign( score ) * ( cur.getStencilBits() - desired.getStencilBits() );
            if ( cur.getDoubleBuffered() != desired.getDoubleBuffered() )
            {
                score += sign( score ) * DOUBLE_BUFFER_MISMATCH_PENALTY;
            }
            if ( ( desired.getStencilBits() > 0 ) && ( cur.getStencilBits() == 0 ) )
            {
                score += sign( score ) * STENCIL_MISMATCH_PENALTY;
            }
            if ( desired.getSampleBuffers() != cur.getSampleBuffers() )
                score += SAMPLE_BUFFERS_MISMATCH_PENALTY;
            score += NUM_SAMPLES_MISMATCH_PENALTY * sign( score ) * Math.abs( desired.getNumSamples() - cur.getNumSamples() );
            
            scores[ i ] = score;
            
            X3DLog.debug( "Available " + i + ": " + available[ i ] + " MS: " + available[ i ].getSampleBuffers() + " " + available[ i ].getNumSamples() + " Score: " + scores[ i ] );
        }
        
        // Now prefer hardware-accelerated visuals by pushing scores of
        // non-hardware-accelerated visuals out
        boolean gotHW = false;
        int maxAbsoluteHWScore = 0;
        for ( int i = 0; i < scores.length; i++ )
        {
            int score = scores[ i ];
            if ( score == NO_SCORE )
            {
                continue;
            }
            
            GLCapabilities cur = available[ i ];
            if ( cur.getHardwareAccelerated() )
            {
                int absScore = Math.abs( score );
                if ( !gotHW || ( absScore > maxAbsoluteHWScore ) )
                {
                    gotHW = true;
                    maxAbsoluteHWScore = absScore;
                }
            }
        }
        
        if ( gotHW )
        {
            for ( int i = 0; i < scores.length; i++ )
            {
                int score = scores[ i ];
                if ( score == NO_SCORE )
                {
                    continue;
                }
                GLCapabilities cur = available[ i ];
                if ( !cur.getHardwareAccelerated() )
                {
                    if ( score <= 0 )
                    {
                        score -= maxAbsoluteHWScore;
                    }
                    else if ( score > 0 )
                    {
                        score += maxAbsoluteHWScore;
                    }
                    scores[ i ] = score;
                }
            }
        }
        
        // Ready to select. Choose score closest to 0.
        int scoreClosestToZero = NO_SCORE;
        int chosenIndex = -1;
        for ( int i = 0; i < scores.length; i++ )
        {
            final int score = scores[ i ];
            if ( score == NO_SCORE )
            {
                continue;
            }
            
            // Don't substitute a positive score for a smaller negative score
            if ( ( scoreClosestToZero == NO_SCORE ) || ( Math.abs( score ) < Math.abs( scoreClosestToZero ) && ( ( sign( scoreClosestToZero ) < 0 ) || ( sign( score ) > 0 ) ) ) )
            {
                
                scoreClosestToZero = score;
                chosenIndex = i;
            }
        }
        
        if ( chosenIndex < 0 )
        {
            throw new GLException( "Unable to select one of the provided GLCapabilities" );
        }
        
        X3DLog.debug( "Chosen index: ", chosenIndex );
        X3DLog.debug( "Chosen capabilities:" );
        if ( available[ chosenIndex ] != null )
            X3DLog.debug( available[ chosenIndex ] + " MS: " + available[ chosenIndex ].getSampleBuffers() + " " + available[ chosenIndex ].getNumSamples() );
        
        return ( chosenIndex );
    }
    
    private static int sign( int score )
    {
        if ( score < 0 )
        {
            return ( -1 );
        }
        
        return ( 1 );
    }
}
