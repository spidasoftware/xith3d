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

import java.io.IOException;

import org.openmali.spatial.bounds.BoundingBox;
import org.openmali.spatial.bounds.BoundingSphere;
import org.openmali.spatial.bounds.Bounds;
import org.xith3d.effects.EffectFactory;
import org.xith3d.effects.bumpmapping.BumpMappingFactory;
import org.xith3d.effects.shadows.ShadowFactory;
import org.xith3d.render.CanvasPeer;
import org.xith3d.render.OpenGLCapabilities;
import org.xith3d.render.preprocessing.ShapeAtom;
import org.xith3d.scenegraph.modifications.ScenegraphModificationsListener;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;
import org.xith3d.scenegraph.utils.CopyListener;

/**
 * Shape3D is a class for all scene graph nodes that have no children. Leaf
 * nodes specify lights, geometry, sounds, etc.
 * 
 * @author David Yazel
 * @author Marvin Froehlich (aka Qudus)
 * @author Amos Wenger (aka BlueSky)
 */
public class Shape3D extends Leaf
{
    private ShapeAtom atom = null;
    
    /**
     * This is the geometry for the object.
     */
    private Geometry geometry = null;
    
    /**
     * This is the appearance for the object.
     */
    private Appearance appearance = null;
    
    @SuppressWarnings("unchecked")
    private Comparable customComparable = null;
    
    private boolean visible = true;
    
    /**
     * Appearance change flag
     */
    private long appChangeID = -1L;
    
    private boolean bumpMappingEnabled = false;
    private boolean isShadowReceiver = false;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setModListener( ScenegraphModificationsListener modListener )
    {
        super.setModListener( modListener );
        
        if ( appearance != null )
            appearance.setModListener( this.getModListener() );
        
        if ( geometry != null )
            geometry.setModListener( this.getModListener() );
    }
    
    /**
     * Sets this Shape3D is visible or invisible.<br />
     */
    public void setVisible( boolean visible )
    {
        this.visible = visible;
    }
    
    /**
     * Checks, whether this Shape3D is visible.<br />
     * <br />
     * If the Appearance or the Appearance's RenderingAttributes is null or the
     * RenderingAttributes are set to invisible false is returned.
     */
    public final boolean isVisible()
    {
        //return ( visible && !( ( appearance != null ) && ( appearance.getRenderingAttributes() != null ) && ( !appearance.getRenderingAttributes().getVisible() ) ) );
        return ( visible );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void setBounds( Bounds bounds )
    {
        if ( getGeometry() != null )
            getGeometry().setBoundsDirty();
        
        super.setBounds( bounds );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void updateBoundsCheap( boolean onlyDirty, boolean childrenToo, boolean parentToo, boolean onlyWorld )
    {
        // if we already have the bounds then return
        if ( ( isIgnoreBounds() ) || ( !boundsDirty && onlyDirty ) )
        {
            return;
        }
        
        if ( boundsAutoCompute && !onlyWorld )
        {
            final Geometry geom = this.getGeometry();
            if ( geom != null )
            {
                if ( ( geom.isBoundsDirty() ) || ( !onlyDirty ) )
                {
                    final Bounds b = geom.getCachedBounds();
                    final Bounds newBounds;
                    if ( ( b == null ) || ( b.getType() != untransformedBounds.getType() ) )
                    {
                        if ( bounds instanceof BoundingBox )
                            newBounds = new BoundingBox();
                        //else if (bounds instanceof BoundingPolytope)
                        //    newBounds = new BoundingPolytope();
                        else
                            //if (bounds instanceof BoundingSphere)
                            newBounds = new BoundingSphere();
                    }
                    else
                    {
                        newBounds = b;
                    }
                    
                    newBounds.compute( geom );
                    geom.setCachedBounds( newBounds );
                }
                
                untransformedBounds.set( geom.getCachedBounds() );
                bounds.set( geom.getCachedBounds() );
            }
        }
        
        super.updateBoundsCheap( onlyDirty, childrenToo, parentToo, onlyWorld );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void updateBounds( boolean onlyDirty )
    {
        updateBoundsCheap( onlyDirty, false, true, false );
    }
    
    /**
     * If this is a TransformGroupm then it will multiply the transform against
     * the parent transform and store it into the world-transform matrix.
     * 
     * <b>Never use this method on your own! It's just for internal use.</b>
     */
    @Override
    public final void updateWorldTransform()
    {
        super.updateWorldTransform();
    }
    
    private int passId = 0;
    
    final void setPassId( int passId )
    {
        this.passId = passId;
    }
    
    final int getPassId()
    {
        return ( passId );
    }
    
    /**
     * Sets the geometry for this object.
     * 
     * @param geometry The new geometry
     */
    public void setGeometry( Geometry geometry )
    {
        final boolean hasChanged = ( this.geometry != geometry );
        
        if ( ( geometry == null ) && ( this.geometry != null ) )
            this.geometry.setModListener( null );
        
        this.geometry = geometry;
        
        if ( geometry != null )
        {
            geometry.setModListener( this.getModListener() );
        }
        
        if ( ( hasChanged ) || ( ( geometry != null ) && ( geometry.isBoundsDirty() ) ) )
        {
            setBoundsDirty();
            updateBounds( true );
        }
    }
    
    /**
     * @return the Geometry for this object.
     */
    public Geometry getGeometry()
    {
        return ( geometry );
    }
    
    /**
     * Sets the appearance for this object.
     */
    public final void setAppearance( Appearance appearance )
    {
        if ( appearance == this.appearance )
            return;
        
        //this.appChangeID = -1L;
        
        if ( ( appearance == null ) && ( this.appearance != null ) )
            this.appearance.setModListener( null );
        
        this.appearance = appearance;
        if ( appearance != null )
            this.appearance.setChangedRecursive( true );
        
        if ( appearance != null )
            appearance.setModListener( this.getModListener() );
    }
    
    /**
     * @return the appearance for this object.
     */
    public final Appearance getAppearance()
    {
        return ( appearance );
    }
    
    /**
     * Returns this shape's Appearance, if it exists. If it doesn't exist, it
     * is created depending on the <b>forceExistance</b> parameter.
     * 
     * @param forceExistance if true, a new Appearance is created and attached,
     *                       if it doesn't already exist.
     * 
     * @return the appearance for this object
     */
    public Appearance getAppearance( boolean forceExistance )
    {
        if ( ( getAppearance() == null ) && ( forceExistance ) )
        {
            setAppearance( new Appearance() );
        }
        
        return ( getAppearance() );
    }
    
    /**
     * Creates a new appearance for this Shape3D and returns it.
     * 
     * @return A new appearance for this Shape3D
     */
    public Appearance newAppearance()
    {
        setAppearance( new Appearance() );
        
        return ( getAppearance() );
    }
    
    public final boolean verifyAppChange( OpenGLCapabilities glCaps )
    {
        if ( appearance != null )
        {
            final long change_id = appearance.verifyChange( this, glCaps );
            if ( change_id != appChangeID )
            {
                appChangeID = change_id;
                return ( true );
            }
        }
        else if ( appChangeID != -1L )
        {
            appChangeID = -1L;
            return ( true );
        }
        
        return ( false );
    }
    
    /**
     * Sets the custom comparable object.<br />
     * This object can be used for shape sorting, but isn't by default.
     * 
     * @param customComparable
     */
    @SuppressWarnings("unchecked")
    public void setCustomComparable( Comparable customComparable )
    {
        this.customComparable = customComparable;
    }
    
    /**
     * Returns the custom comparable object.<br />
     * This object can be used for shape sorting, but isn't by default.
     * 
     * @return the custom comparable object.
     */
    @SuppressWarnings("unchecked")
    public final Comparable getCustomComparable()
    {
        return ( customComparable );
    }
    
    public final void setBumpMappingEnabled( boolean enabled, Texture normalMapTex )
    {
        this.bumpMappingEnabled = enabled;
        
        if ( enabled && ( ( getGeometry() == null ) || ( getAppearance() == null ) ) )
        {
            System.err.println( "Warning: BumpMapping will fail, if this Shape doesn't have a Geometry AND an Appearance." );
        }
        else if ( enabled && ( getGeometry() != null ) && ( getAppearance() != null ) )
        {
            BumpMappingFactory bumpFactory = EffectFactory.getInstance().getBumpMappingFactory();
            
            if ( bumpFactory != null )
            {
                try
                {
                    bumpFactory.prepareForBumpMapping( this, normalMapTex );
                }
                catch ( IOException e )
                {
                    e.printStackTrace();
                }
            }
            else
            {
                System.err.println( "ERROR: Cannot apply BumpMapping, while no BumpMappingFactory is registered at the EffectFactory!" );
            }
        }
    }
    
    public final void setBumpMappingEnabled( boolean enabled, String normalMapTex )
    {
        BumpMappingFactory bumpFactory = EffectFactory.getInstance().getBumpMappingFactory();
        
        if ( bumpFactory != null )
        {
            setBumpMappingEnabled( enabled, BumpMappingFactory.loadNormalMap( normalMapTex ) );
        }
        else
        {
            System.err.println( "ERROR: Cannot apply BumpMapping, while no BumpMappingFactory is registered at the EffectFactory!" );
        }
    }
    
    public final boolean isBumpMappingEnabled()
    {
        return ( bumpMappingEnabled );
    }
    
    public final void setIsShadowReceiver( boolean isSR )
    {
        final boolean changed = this.isShadowReceiver != isSR;
        this.isShadowReceiver = isSR;
        
        if ( changed )
        {
            final EffectFactory effFact = EffectFactory.getInstance();
            if ( effFact != null )
            {
                final ShadowFactory shadowFact = effFact.getShadowFactory();
                if ( shadowFact != null )
                    shadowFact.onShadowReceiverStateChanged( this, isShadowReceiver );
            }
        }
    }
    
    public final boolean isShadowReceiver()
    {
        return ( isShadowReceiver );
    }
    
    /**
     * Sets this Shape3D's RenderAtom.
     * 
     * Do not use on your own ! For internal use only !
     * 
     * @param atom The new atom
     */
    final void setAtom( ShapeAtom atom )
    {
        this.atom = atom;
    }
    
    /**
     * Do not use on your own ! For internal use only !
     * 
     * @return this Shape3D's current RenderAtom
     */
    final ShapeAtom getAtom()
    {
        return ( atom );
    }
    
    protected void copy( Shape3D dest )
    {
        dest.setGeometry( this.getGeometry() );
        dest.setAppearance( this.getAppearance() );
        
        dest.setBoundsAutoCompute( false );
        dest.setBounds( this.getBounds() );
        dest.boundsDirty = true;
        dest.updateBounds( false );
        dest.setPickable( this.isPickable() );
        dest.setRenderable( this.isRenderable() );
        dest.setName( this.getName() );
    }
    
    /**
     * @return a new instance of this class. This is invoked by the sharedCopy() method.
     * 
     * @see #sharedCopy(CopyListener)
     */
    protected Shape3D newInstance()
    {
        boolean gib = Node.globalIgnoreBounds;
        Node.globalIgnoreBounds = this.isIgnoreBounds();
        Shape3D newShape = new Shape3D();
        Node.globalIgnoreBounds = gib;
        
        return ( newShape );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Shape3D sharedCopy( CopyListener listener )
    {
        Shape3D newShape = newInstance();
        
        copy( newShape );
        
        if ( listener != null )
        {
            listener.onNodeCopied( this, newShape, true );
        }
        
        return ( newShape );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Shape3D sharedCopy()
    {
        return ( (Shape3D)super.sharedCopy() );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void absorbDetails( Node node )
    {
        ( (Shape3D)node ).copy( this );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void freeOpenGLResources( CanvasPeer canvasPeer )
    {
        if ( getAppearance() != null )
            getAppearance().freeOpenGLResources( canvasPeer );
        
        if ( getGeometry() != null )
            getGeometry().freeOpenGLResources( canvasPeer );
    }
    
    /**
     * Traverses the scenegraph from this node on. If this Node is a Group it
     * will recusively run through each child.
     * 
     * @param callback the listener is notified of any traversed Node on the way
     * @return if false, the whole traversal will stop
     */
    @Override
    public boolean traverse( DetailedTraversalCallback callback )
    {
        return ( callback.traversalOperationCommon( this ) && callback.traversalOperation( this ) && callback.traversalOperationAfter( this ) && callback.traversalOperationCommonAfter( this ) );
    }
    
    /**
     * Constructs a new Shape3D object with a null geometry component
     * and a null appearance component.
     */
    public Shape3D()
    {
        this( (Geometry)null, (Appearance)null );
    }
    
    /**
     * Constructs a new Shape3D object with specified geometry component
     * and a null appearance component.
     */
    public Shape3D( Geometry geometry )
    {
        this( geometry, (Appearance)null );
    }
    
    /**
     * Constructs a new Shape3D object with specified geometry and
     * appearance components.
     */
    public Shape3D( Geometry geometry, Appearance appearance )
    {
        super();
        
        this.setGeometry( geometry );
        this.setAppearance( appearance );
    }
}
