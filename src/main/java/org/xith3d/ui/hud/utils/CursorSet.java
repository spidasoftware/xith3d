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
package org.xith3d.ui.hud.utils;


/**
 * A {@link CursorSet} holds references to {@link Cursor}s for all different
 * {@link Cursor.Type}s. They can be <code>null</code>, which means, that the
 * default system cursor is used.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class CursorSet
{
    private Cursor pointer1 = null;
    private Cursor pointer2 = null;
    private Cursor crosshair = null;
    private Cursor text = null;
    private Cursor wait = null;
    private Cursor help = null;
    
    /**
     * Sets the {@link Cursor} for the given cursor.
     * 
     * @param type
     * @param cursor
     */
    public final void set( Cursor.Type type, Cursor cursor )
    {
        switch ( type )
        {
            case POINTER1:
                this.pointer1 = cursor;
                break;
            
            case POINTER2:
                this.pointer2 = cursor;
                break;
            
            case CROSSHAIR:
                this.crosshair = cursor;
                break;
            
            case TEXT:
                this.text = cursor;
                break;
            
            case WAIT:
                this.wait = cursor;
                break;
            
            case HELP:
                this.help = cursor;
                break;
            
            default:
                throw new IllegalArgumentException( "This method is not applicable for the Cursor type " + type );
        }
    }
    
    /**
     * @param cursorType
     * 
     * @return the {@link Cursor} for the given cursor.
     */
    public final Cursor get( Cursor.Type cursorType )
    {
        if ( cursorType == null )
            return ( null );
        
        switch ( cursorType )
        {
            case POINTER1:
                return ( pointer1 );
                
            case POINTER2:
                return ( pointer2 );
                
            case CROSSHAIR:
                return ( crosshair );
                
            case TEXT:
                return ( text );
                
            case WAIT:
                return ( wait );
                
            case HELP:
                return ( help );
                
            default:
                throw new IllegalArgumentException( "This method is not applicable for the Cursor type " + cursorType );
        }
    }
    
    /**
     * Sets the {@link Cursor} for the (default) POINTER1 cursor.
     * 
     * @param cursor
     */
    public final void setPointer1( Cursor cursor )
    {
        set( Cursor.Type.POINTER1, cursor );
    }
    
    /**
     * @return the {@link Cursor} for the (default) POINTER1 cursor.
     */
    public final Cursor getPointer1()
    {
        return ( get( Cursor.Type.POINTER1 ) );
    }
    
    /**
     * Sets the {@link Cursor} for the POINTER2 cursor.
     * 
     * @param cursor
     */
    public final void setPointer2( Cursor cursor )
    {
        set( Cursor.Type.POINTER2, cursor );
    }
    
    /**
     * @return the {@link Cursor} for the POINTER2 cursor.
     */
    public final Cursor getPointer2()
    {
        return ( get( Cursor.Type.POINTER2 ) );
    }
    
    /**
     * Sets the {@link Cursor} for thTexturee CROSSHAIR cursor.
     * 
     * @param cursor
     */
    public final void setCrosshair( Cursor cursor )
    {
        set( Cursor.Type.CROSSHAIR, cursor );
    }
    
    /**
     * @return the {@link Cursor} for the CROSSHAIR cursor.
     */
    public final Cursor getCrosshair()
    {
        return ( get( Cursor.Type.CROSSHAIR ) );
    }
    
    /**
     * Sets the {@link Cursor} for the TEXT cursor.
     * 
     * @param cursor
     */
    public final void setTextCursor( Cursor cursor )
    {
        set( Cursor.Type.TEXT, cursor );
    }
    
    /**
     * @return the Cursor for the TEXT cursor.
     */
    public final Cursor getTextCursor()
    {
        return ( get( Cursor.Type.TEXT ) );
    }
    
    /**
     * Sets the {@link Cursor} for the WAIT cursor.
     * 
     * @param cursor
     */
    public final void setWaitCursor( Cursor cursor )
    {
        set( Cursor.Type.WAIT, cursor );
    }
    
    /**
     * @return the {@link Cursor} for the WAIT cursor.
     */
    public final Cursor getWaitCursor()
    {
        return ( get( Cursor.Type.WAIT ) );
    }
    
    /**
     * Sets the {@link Cursor} for the HELP cursor.
     * 
     * @param cursor
     */
    public final void setHelpCursor( Cursor cursor )
    {
        set( Cursor.Type.HELP, cursor );
    }
    
    /**
     * @return the {@link Cursor} for the HELP cursor.
     */
    public final Cursor getHelpCursor()
    {
        return ( get( Cursor.Type.HELP ) );
    }
    
    
    public void set( CursorSet template )
    {
        this.pointer1 = template.pointer1;
        this.pointer2 = template.pointer2;
        this.crosshair = template.crosshair;
        this.text = template.text;
        this.wait = template.wait;
        this.help = template.help;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final CursorSet clone()
    {
        return ( new CursorSet( this ) );
    }
    
    
    public CursorSet( CursorSet template )
    {
        this.set( template );
    }
    
    public CursorSet()
    {
    }
}
