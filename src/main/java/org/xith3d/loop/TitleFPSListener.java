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
package org.xith3d.loop;

/**
 * This FPSListener prints the cought FPS to the title of some titled object.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public abstract class TitleFPSListener implements ConsciousFPSListener
{
    private String baseTitle;
    private RenderLoop renderLoop;
    
    /**
     * Sets the base-title to prefix the Canvas3D's title with.
     * 
     * @param baseTitle
     */
    public final void setBaseTitle( String baseTitle )
    {
        this.baseTitle = baseTitle;
    }
    
    /**
     * @return the base-title to prefix the Canvas3D's title with.
     */
    public final String getBaseTitle()
    {
        return ( baseTitle );
    }
    
    /**
     * This is called when the listener has been detaced from teh RenderLoop.
     */
    protected abstract void onDetachedFromRenderLoop();
    
    /**
     * {@inheritDoc}
     */
    public final void setRenderLoop( RenderLoop renderLoop )
    {
        final boolean detached = ( this.renderLoop != null ) && ( renderLoop == null );
        
        this.renderLoop = renderLoop;
        
        if ( detached )
            onDetachedFromRenderLoop();
    }
    
    /**
     * @return the RenderLoop, this ConsciousFPSListener is linked with.
     */
    public final RenderLoop getRenderLoop()
    {
        return ( renderLoop );
    }
    
    protected boolean isLimited()
    {
        return ( ( renderLoop != null ) && ( renderLoop.getMinIterationTime() != 0L ) );
    }
    
    protected String getLimitedString()
    {
        return ( " (limited)" );
    }
    
    protected String getDynamicTitlePart( float fps, boolean limited )
    {
        if ( limited )
            return ( ", FPS: " + (int)fps + getLimitedString() );
        
        return ( ", FPS: " + (int)fps );
    }
    
    protected abstract void setTitle( String title );
    
    /**
     * {@inheritDoc}
     */
    public void onFPSCountIntervalHit( float fps )
    {
        setTitle( getBaseTitle() + getDynamicTitlePart( fps, isLimited() ) );
    }
    
    public TitleFPSListener( String baseTitle )
    {
        this.baseTitle = baseTitle;
    }
}
