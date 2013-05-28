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
package org.xith3d.selection;

import java.util.List;
import org.openmali.vecmath2.Tuple2i;
import org.openmali.vecmath2.Tuple3f;

/**
 *
 * @author cylab
 */
public abstract class AbstractAction implements Action
{
    private String id;
    private String displayName;

    public String getID()
    {
        return id;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName( String displayName )
    {
        this.displayName = displayName;
    }

    public boolean testEnabledState( Tuple2i mouseCoords, Tuple3f worldCoords, List<Selectable> selection, List<Selectable> selectedContext )
    {
        return true;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( obj == null )
            return false;
        if ( !Action.class.isAssignableFrom( obj.getClass() ) )
            return false;
        final Action other = (Action) obj;
        if ( this.getID() != other.getID() && ( this.getID() == null || !this.getID().equals( other.getID() ) ) )
            return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        return ( this.id != null ? this.id.hashCode() : 0 );
    }

    public AbstractAction( String id, String displayName )
    {
        this.id = id;
        this.displayName = displayName;
    }

    public AbstractAction( String id )
    {
        this( id, id );
    }
}
