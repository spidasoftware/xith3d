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
package org.xith3d.scenegraph;

import org.jagatoo.datatypes.Enableable;
import org.jagatoo.opengl.enums.TexCoordGenMode;
import org.openmali.vecmath2.Vector4f;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.states.StateNode;
import org.xith3d.render.states.StateTrackable;
import org.xith3d.utility.comparator.ComparatorHelper;

/**
 * TexCoordGeneration defines attributes that apply to texture coordinates generation.
 * 
 * @author David Yazel
 * @author YVG
 * @author Artur Biesiadowski
 * @author William Denniss
 * @author unascribed
 * @author Marvin Froehlich (aka Qudus)
 */
public class TexCoordGeneration extends NodeComponent implements StateTrackable< TexCoordGeneration >, Enableable
{
    public enum CoordMode
    {
        /**
         * Generates 1D texture coordinates (S).
         */
        TEXTURE_COORDINATES_1( 1, 1 ),
        
        /**
         * Generates 2D texture coordinates (S and T).
         */
        TEXTURE_COORDINATES_2( 2, 3 ),
        
        /**
         * Generates 3D texture coordinates (S, T and R).
         */
        TEXTURE_COORDINATES_3( 3, 7 ),
        
        /**
         * Generates 4D texture coordinates (S, T, R and Q).
         */
        TEXTURE_COORDINATES_4( 4, 15 );
        
        private final int numTUs;
        private final int bitmaks;
        
        /**
         * @return the number of generated TUs.
         */
        public final int getNumTUs()
        {
            return ( numTUs );
        }
        
        /**
         * @return a bitmask with all bits set, that are lower or equal to this format's index.
         */
        public final int getBitMask()
        {
            return ( bitmaks );
        }
        
        public static CoordMode getFromNumber( int number )
        {
            switch ( number )
            {
                case 1:
                    return ( TEXTURE_COORDINATE_1 );
                case 2:
                    return ( TEXTURE_COORDINATE_2 );
                case 3:
                    return ( TEXTURE_COORDINATE_3 );
                case 4:
                    return ( TEXTURE_COORDINATE_4 );
            }
            
            throw new IllegalArgumentException( number + " is not a valid TextureCoordGeneration mode.");
        }
        
        private CoordMode( int numTUs, int bitmaks )
        {
            this.numTUs = numTUs;
            this.bitmaks = bitmaks;
        }
    }
    
    /**
     * Generates texture coordinates as a linear function in object coordinates.
     * 
     * @see TexCoordGenMode#OBJECT_LINEAR
     */
    public static final TexCoordGenMode OBJECT_LINEAR = TexCoordGenMode.OBJECT_LINEAR;
    
    /**
     * Generates texture coordinates as a linear function in eye coordinates.
     * 
     * @see TexCoordGenMode#OBJECT_LINEAR
     */
    public static final TexCoordGenMode EYE_LINEAR = TexCoordGenMode.EYE_LINEAR;
    
    /**
     * Generates texture coordinates using a sphereical
     * reflection mapping in eye coordinates.
     * 
     * @see TexCoordGenMode#OBJECT_LINEAR
     */
    public static final TexCoordGenMode SPHERE_MAP = TexCoordGenMode.SPHERE_MAP;
    
    /**
     * @see TexCoordGenMode#OBJECT_LINEAR
     */
    public static final TexCoordGenMode NORMAL_MAP = TexCoordGenMode.NORMAL_MAP;
    
    /**
     * @see TexCoordGenMode#OBJECT_LINEAR
     */
    public static final TexCoordGenMode REFLECTION_MAP = TexCoordGenMode.REFLECTION_MAP;
    
    /**
     * @see CoordMode#TEXTURE_COORDINATES_1
     */
    public static final CoordMode TEXTURE_COORDINATE_1 = CoordMode.TEXTURE_COORDINATES_1;
    
    /**
     * @see CoordMode#TEXTURE_COORDINATES_2
     */
    public static final CoordMode TEXTURE_COORDINATE_2 = CoordMode.TEXTURE_COORDINATES_2;
    
    /**
     * @see CoordMode#TEXTURE_COORDINATES_3
     */
    public static final CoordMode TEXTURE_COORDINATE_3 = CoordMode.TEXTURE_COORDINATES_3;
    
    /**
     * @see CoordMode#TEXTURE_COORDINATES_4
     */
    public static final CoordMode TEXTURE_COORDINATE_4 = CoordMode.TEXTURE_COORDINATES_4;
    
    /**
     * The desired texture generation mode. OBJECT_LINEAR, EYE_LINEAR or SHPERE_MAP
     */
    private TexCoordGenMode genMode = OBJECT_LINEAR;
    
    /**
     * The desired texture format (2D or 3D). TEXTURE_COORDINATE_2 or TEXTURE_COORDINATE_3
     */
    private CoordMode format = CoordMode.TEXTURE_COORDINATES_2;
    
    /**
     * Plane equation for the S coordinate.
     */
    private final Vector4f planeS = new Vector4f( 1f, 0f, 0f, 0f );
    
    /**
     * Plane equation for the T coordinate.
     */
    private final Vector4f planeT = new Vector4f( 0f, 1f, 0f, 0f );
    
    /**
     * Plane equation for the R coordinate.
     */
    private final Vector4f planeR = new Vector4f( 0f, 0f, 0f, 0f );
    
    /**
     * Plane equation for the Q coordinate.
     */
    private final Vector4f planeQ = new Vector4f( 0f, 0f, 0f, 0f );
    
    /**
     * Enable or disable texture coordinate generation
     */
    private boolean enabled = true;
    
    /**
     * Enables or disables texture coordinate generation
     */
    public void setEnabled( boolean state )
    {
        this.enabled = state;
        
        setChanged( true );
    }
    
    /**
     * Is texture coordinate generation enabled or disabled
     */
    public final boolean isEnabled()
    {
        return ( enabled );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setChanged( boolean changed )
    {
        super.setChanged( changed );
    }
    
    /**
     * Sets the desired texture format (1D, 2D, 3D or 4D).
     * TEXTURE_COORDINATE_1, TEXTURE_COORDINATE_2, TEXTURE_COORDINATE_3 or TEXTURE_COORDINATE_4
     */
    public final void setFormat( CoordMode format )
    {
        this.format = format;
        
        setChanged( true );
    }
    
    /**
     * Gets the texture format
     */
    public final CoordMode getFormat()
    {
        return ( format );
    }
    
    /**
     * Sets the desired texture generation mode.
     */
    public final void setGenMode( TexCoordGenMode genMode )
    {
        this.genMode = genMode;
        
        setChanged( true );
    }
    
    /**
     * Gets the texture generation mode.
     */
    public final TexCoordGenMode getGenMode()
    {
        return ( genMode );
    }
    
    /**
     * Sets the S coordinate plane equation.
     * 
     * @param planeSx
     * @param planeSy
     * @param planeSz
     * @param planeSw
     */
    public final void setPlaneS( float planeSx, float planeSy, float planeSz, float planeSw )
    {
        this.planeS.set( planeSx, planeSy, planeSz, planeSw );
        
        setChanged( true );
    }
    
    /**
     * Sets the S coordinate plane equation.
     * 
     * @param planeS
     */
    public final void setPlaneS( Vector4f planeS )
    {
        this.planeS.set( planeS );
        
        setChanged( true );
    }
    
    /**
     * Gets the S coordinate plane equation.
     */
    public final Vector4f getPlaneS()
    {
        return ( planeS );
    }
    
    /**
     * Sets the T coordinate plane equation.
     * 
     * @param planeTx
     * @param planeTy
     * @param planeTz
     * @param planeTw
     */
    public final void setPlaneT( float planeTx, float planeTy, float planeTz, float planeTw )
    {
        this.planeT.set( planeTx, planeTy, planeTz, planeTw );
        
        setChanged( true );
    }
    
    /**
     * Sets the T coordinate plane equation.
     * 
     * @param planeT
     */
    public final void setPlaneT( Vector4f planeT )
    {
        this.planeT.set( planeT );
        
        setChanged( true );
    }
    
    /**
     * Gets the T coordinate plane equation.
     */
    public final Vector4f getPlaneT()
    {
        return ( planeT );
    }
    
    /**
     * Sets the R coordinate plane equation.
     * 
     * @param planeRx
     * @param planeRy
     * @param planeRz
     * @param planeRw
     */
    public final void setPlaneR( float planeRx, float planeRy, float planeRz, float planeRw )
    {
        this.planeR.set( planeRx, planeRy, planeRz, planeRw );
        
        setChanged( true );
    }
    
    /**
     * Sets the R coordinate plane equation.
     * 
     * @param planeR
     */
    public final void setPlaneR( Vector4f planeR )
    {
        this.planeR.set( planeR );
        
        setChanged( true );
    }
    
    /**
     * Gets the R coordinate plane equation.
     */
    public final Vector4f getPlaneR()
    {
        return ( planeR );
    }
    
    /**
     * Sets the Q coordinate plane equation.
     * 
     * @param planeQx
     * @param planeQy
     * @param planeQz
     * @param planeQw
     */
    public final void setPlaneQ( float planeQx, float planeQy, float planeQz, float planeQw )
    {
        this.planeQ.set( planeQx, planeQy, planeQz, planeQw );
        
        setChanged( true );
    }
    
    /**
     * Sets the Q coordinate plane equation.
     * 
     * @param planeQ
     */
    public final void setPlaneQ( Vector4f planeQ )
    {
        this.planeQ.set( planeQ );
        
        setChanged( true );
    }
    
    /**
     * Gets the Q coordinate plane equation.
     */
    public final Vector4f getPlaneQ()
    {
        return ( planeQ );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return ( TexCoordGeneration.class.getSimpleName() + " {" + this.getGenMode() + ", " + this.getFormat() + ", " + ( this.isEnabled() ? "enabled" : "disabled" ) + "}" );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void duplicateNodeComponent( NodeComponent original, boolean forceDuplicate )
    {
        super.duplicateNodeComponent( original, forceDuplicate );
        
        final TexCoordGeneration origTG = (TexCoordGeneration)original;
        
        this.genMode = origTG.genMode;
        this.format = TEXTURE_COORDINATE_2;
        this.planeS.set( origTG.planeS );
        this.planeT.set( origTG.planeT );
        this.planeR.set( origTG.planeR );
        this.planeQ.set( origTG.planeQ );
        this.enabled = origTG.enabled;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TexCoordGeneration cloneNodeComponent( boolean forceDuplicate )
    {
        TexCoordGeneration tcg = new TexCoordGeneration();
        
        tcg.duplicateNodeComponent( this, forceDuplicate );
        
        return ( tcg );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
    }
    
    // ////////////////////////////////////////////////////////////////
    // ///////////// SUPPORT FOR STATE TRACKABLE INTERFACE ////////////
    // ////////////////////////////////////////////////////////////////
    
    private StateNode stateNode = null;
    private long stateId = -1L;
    
    /**
     * {@inheritDoc}
     */
    public final void setStateNode( StateNode node )
    {
        this.stateNode = node;
        this.stateId = node.getId();
    }
    
    /**
     * {@inheritDoc}
     */
    public final StateNode getStateNode()
    {
        return ( stateNode );
    }
    
    public final long getStateId()
    {
        return ( stateId );
    }
    
    /**
     * {@inheritDoc}
     */
    public TexCoordGeneration getCopy()
    {
        return ( cloneNodeComponent( true ) );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
            return ( true );
        if ( !( o instanceof TexCoordGeneration ) )
            return ( false );
        
        TexCoordGeneration tcgo = (TexCoordGeneration)o;
        
        if ( this.genMode != tcgo.genMode )
            return ( false );
        
        if ( this.format != tcgo.format )
            return ( false );
        
        if ( !this.planeS.equals( tcgo.planeS ) )
            return ( false );
        if ( !this.planeT.equals( tcgo.planeT ) )
            return ( false );
        if ( !this.planeR.equals( tcgo.planeR ) )
            return ( false );
        if ( !this.planeQ.equals( tcgo.planeQ ) )
            return ( false );
        
        return ( true );
    }
    
    /**
     * {@inheritDoc}
     */
    public int compareTo( TexCoordGeneration o )
    {
        if ( this == o )
            return ( 0 );
        
        if ( this.genMode.ordinal() < o.genMode.ordinal() )
            return ( -1 );
        else if ( this.genMode.ordinal() > o.genMode.ordinal() )
            return ( 1 );
        
        if ( this.format.ordinal() < o.format.ordinal() )
            return ( -1 );
        else if ( this.format.ordinal() > o.format.ordinal() )
            return ( 1 );
        
        int val = ComparatorHelper.compareTuple( this.planeS, o.planeS );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareTuple( this.planeT, o.planeT );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareTuple( this.planeR, o.planeR );
        if ( val != 0 )
            return ( val );
        
        val = ComparatorHelper.compareTuple( this.planeQ, o.planeQ );
        if ( val != 0 )
            return ( val );
        
        return ( 0 );
    }
    
    /**
     * Constructs a new TexCoordGeneration object.
     */
    public TexCoordGeneration()
    {
        super( false );
    }
    
    /**
     * Constructs a new TexCoordGeneration object.
     * 
     * @param enabled
     */
    public TexCoordGeneration( boolean enabled )
    {
        this();
        
        this.enabled = enabled;
    }
    
    /**
     * Constructs a new TexCoordGeneration object.
     * 
     * @param genMode
     * @param format
     */
    public TexCoordGeneration( TexCoordGenMode genMode, CoordMode format )
    {
        this();
        
        this.genMode = genMode;
        this.format = format;
    }
    
    /**
     * Constructs a new TexCoordGeneration object.
     * 
     * @param genMode
     * @param format
     * @param planeS
     */
    public TexCoordGeneration( TexCoordGenMode genMode, CoordMode format, Vector4f planeS )
    {
        this( genMode, format );
        
        this.planeS.set( planeS );
    }
    
    /**
     * Constructs a new TexCoordGeneration object.
     * 
     * @param genMode
     * @param format
     * @param planeS
     * @param planeT
     */
    public TexCoordGeneration( TexCoordGenMode genMode, CoordMode format, Vector4f planeS, Vector4f planeT )
    {
        this( genMode, format, planeS );
        
        this.planeT.set( planeT );
    }
    
    /**
     * Constructs a new TexCoordGeneration object.
     * 
     * @param genMode
     * @param format
     * @param planeS
     * @param planeT
     * @param planeR
     */
    public TexCoordGeneration( TexCoordGenMode genMode, CoordMode format, Vector4f planeS, Vector4f planeT, Vector4f planeR )
    {
        this( genMode, format, planeS, planeT );
        
        this.planeR.set( planeR );
    }
    
    /**
     * Constructs a new TexCoordGeneration object.
     * 
     * @param genMode
     * @param format
     * @param planeS
     * @param planeT
     * @param planeR
     * @param planeQ
     */
    public TexCoordGeneration( TexCoordGenMode genMode, CoordMode format, Vector4f planeS, Vector4f planeT, Vector4f planeR, Vector4f planeQ )
    {
        this( genMode, format, planeS, planeT, planeR );
        
        this.planeQ.set( planeQ );
    }
}
