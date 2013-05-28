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
package org.xith3d.render;

import org.jagatoo.datatypes.Enableable;
import org.openmali.spatial.bodies.Plane;

/**
 * A clipper can take up to six arbitrary clipping planes,
 * which can individually be enabled/disabled.
 * All values are measured in Node-Local coordinates.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class Clipper implements Enableable
{
    private static int nextId = 1;
    private final int id;
    
    private final Plane[] planes = new Plane[ 6 ];
    
    private boolean enabled = true;
    private final boolean[] enables = new boolean[] { true, true, true, true, true, true };
    
    private boolean worldCSUsed = false;
    
    /**
     * Returns the unique id of this clipper instance.
     */
    public final int getId()
    {
        return ( id );
    }
    
    /**
     * If this is true, the clipper uses world coordinates.
     * 
     * @param ignored
     */
    public final void setUseWorldCoordinateSystem( boolean ignored )
    {
        this.worldCSUsed = ignored;
    }
    
    /**
     * If this is true, the clipper uses world coordinates.
     */
    public final boolean isWorldCoordinateSystemUsed()
    {
        return ( worldCSUsed );
    }
    
    /**
     * Sets the i-th Plane of this Clipper.
     * 
     * @param i
     * @param plane
     */
    public void setPlane( int i, Plane plane )
    {
        this.planes[ i ] = plane;
    }
    
    /**
     * @param i
     * 
     * @return the i-th Plane of this Clipper.
     */
    public Plane getPlane( int i )
    {
        return ( planes[ i ] );
    }
    
    /**
     * Gets this Clipper's i-th Plane.
     * 
     * @param i
     * @param plane
     * 
     * @return the i-th Plane
     */
    public Plane getPlane( int i, Plane plane )
    {
        plane.set( planes[ i ] );
        
        return ( planes[ i ] );
    }
    
    /**
     * Enables/Disables this Clipper.
     * 
     * @param enabled
     */
    public void setEnabled( boolean enabled )
    {
        this.enabled = enabled;
    }
    
    /**
     * @return Is this Clipper enabled?
     */
    public boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * Enables/Disables this Clipper's Planes.
     * 
     * @param enables
     */
    public void setPlaneEnables( boolean[] enables )
    {
        for ( int i = 0; i < planes.length; i++ )
            this.enables[ i ] = enables[ i ];
    }
    
    /**
     * Gets the enabled states of this Clipper's Planes.
     * 
     * @param enables
     */
    public void getPlaneEnables( boolean[] enables )
    {
        for ( int i = 0; i < planes.length; i++ )
            enables[ i ] = this.enables[ i ];
    }
    
    /**
     * Sets the enabled states of this Clipper's i-th Plane.
     * 
     * @param i
     * @param enabled
     */
    public void setPlaneEnabled( int i, boolean enabled )
    {
        this.enables[ i ] = enabled;
    }
    
    /**
     * @return Is the i-th Plane of this Clipper enabled?
     * 
     * @param i
     */
    public boolean isPlaneEnabled( int i )
    {
        return ( enables[ i ] );
    }
    
    /**
     * Creates a new Clipper.
     * 
     * @param planes
     */
    public Clipper( Plane[] planes )
    {
        synchronized ( ClipperInfo.class )
        {
            this.id = nextId++;
        }
        
        if ( planes == null )
            throw new NullPointerException( "planes must not be null" );
        
        for ( int i = 0; i < planes.length; i++ )
            this.planes[ i ] = planes[ i ];
        
        // Set defaults for the remaining Planes and disable them
        if ( planes.length < 6 )
        {
            for ( int i = planes.length; i < 6; i++ )
            {
                this.planes[ i ] = new Plane();
                this.enables[ i ] = false;
            }
        }
    }
    
    /**
     * Creates a new Clipper.
     * 
     * @param plane0
     * @param plane1
     * @param plane2
     * @param plane3
     * @param plane4
     * @param plane5
     */
    public Clipper( Plane plane0, Plane plane1, Plane plane2, Plane plane3, Plane plane4, Plane plane5 )
    {
        synchronized ( ClipperInfo.class )
        {
            this.id = nextId++;
        }
        
        this.planes[ 0 ] = ( plane0 != null ) ? plane0 : new Plane();
        this.planes[ 1 ] = ( plane1 != null ) ? plane1 : new Plane();
        this.planes[ 2 ] = ( plane2 != null ) ? plane2 : new Plane();
        this.planes[ 3 ] = ( plane3 != null ) ? plane3 : new Plane();
        this.planes[ 4 ] = ( plane4 != null ) ? plane4 : new Plane();
        this.planes[ 5 ] = ( plane5 != null ) ? plane5 : new Plane();

        this.enables[ 0 ] = ( plane0 != null );
        this.enables[ 1 ] = ( plane1 != null );
        this.enables[ 2 ] = ( plane2 != null );
        this.enables[ 3 ] = ( plane3 != null );
        this.enables[ 4 ] = ( plane4 != null );
        this.enables[ 5 ] = ( plane5 != null );
    }
    
    /**
     * Creates a new Clipper.
     * 
     * @param plane0
     */
    public Clipper( Plane plane0 )
    {
        this( plane0, null, null, null, null, null );
    }
    
    /**
     * Creates a new Clipper.
     * 
     * @param plane0
     * @param plane1
     */
    public Clipper( Plane plane0, Plane plane1 )
    {
        this( plane0, plane1, null, null, null, null );
    }
    
    /**
     * Creates a new Clipper.
     * 
     * @param plane0
     * @param plane1
     * @param plane2
     */
    public Clipper( Plane plane0, Plane plane1, Plane plane2 )
    {
        this( plane0, plane1, plane2, null, null, null );
    }
    
    /**
     * Creates a new Clipper.
     * 
     * @param plane0
     * @param plane1
     * @param plane2
     * @param plane3
     */
    public Clipper( Plane plane0, Plane plane1, Plane plane2, Plane plane3 )
    {
        this( plane0, plane1, plane2, plane3, null, null );
    }
    
    /**
     * Creates a new Clipper.
     * 
     * @param plane0
     * @param plane1
     * @param plane2
     * @param plane3
     * @param plane4
     */
    public Clipper( Plane plane0, Plane plane1, Plane plane2, Plane plane3, Plane plane4 )
    {
        this( plane0, plane1, plane2, plane3, plane4, null );
    }
    
    /**
     * Creates a new Clipper.
     */
    public Clipper()
    {
        synchronized ( ClipperInfo.class )
        {
            this.id = nextId++;
        }
        
        for ( int i = 0; i < planes.length; i++ )
            this.planes[ i ] = new Plane();
    }
}
