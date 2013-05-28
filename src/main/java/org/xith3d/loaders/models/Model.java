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
package org.xith3d.loaders.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.jagatoo.datatypes.NamedObject;
import org.openmali.spatial.bodies.Frustum;
import org.openmali.vecmath2.Matrix4f;
import org.xith3d.loaders.models.animations.AnimationListener;
import org.xith3d.loaders.models.animations.ModelAnimation;
import org.xith3d.loaders.models.animations._Anim_PrivilegedAccess;
import org.xith3d.loaders.models.util.meta.ModelMetaData;
import org.xith3d.loop.UpdatingThread.TimingMode;
import org.xith3d.scenegraph.Fog;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.UpdatableNode;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Sound;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.primitives.SkyBox;
import org.xith3d.scenegraph.utils.CopyListener;

/**
 * This class represents a Model loaded from some model file formats.
 * This class is responsible for both the storage and retrieval of data from
 * the Model. The storage methods (used only by Loader writers) are all
 * of the add*() routines. The retrieval methods (used primarily by Loader
 * users) are all of the get*() routines.
 * 
 * @author Marvin Freohlich (aka Qudus)
 * @author Andrew Hanson (aka Patheros) [added ModelMetaData]
 */
public class Model extends Group implements UpdatableNode
{
    private static final boolean debug = false;
    
    private static boolean defaultPickHostValue = true;
    
    private TransformGroup[] nestedTransforms = null;
    private Shape3D[] shapes = null;
    private org.xith3d.utility.geometry.NormalsVisualizer[] nvs;
    private Light[] lights = null;
    private Fog[] fogs = null;
    private Sound[] sounds = null;
    private View[] cameras = null;
    private Matrix4f[] spawnTransforms = null;
    private GroupNode mainSceneGroup = null;
    private SkyBox skyBox = null;
    private TreeMap<String, NamedObject> namedObjects = null;
    
    private final HashMap<String, ModelAnimation> animsMap = new HashMap<String, ModelAnimation>();
    private ModelAnimation[] anims = null;
    private TransformGroup[] mountTransforms = null;
    
    private ModelMetaData metaData = null;
    
    private float animationStartTime = -1f;
    private ModelAnimation currentAnimation = null;
    private final ArrayList<AnimationListener> animListeners = new ArrayList<AnimationListener>();
    private boolean finishedFired = false;
    
    /**
     * Sets the initial (default) value for a new Model's pick-host flag.
     * 
     * @param value
     */
    public static void setDefaultPickHost( boolean value )
    {
        Model.defaultPickHostValue = value;
    }
    
    /**
     * @return the initial (default) value for a new Model's pick-host flag.
     */
    public static boolean getDefaultPickHost()
    {
        return ( Model.defaultPickHostValue  );
    }
    
    protected void setNestedTransforms( TransformGroup[] nestedTransforms )
    {
        this.nestedTransforms = nestedTransforms;
    }
    
    /**
     * @param index
     * 
     * @return the nested TransformGroup by the given index.
     */
    public final TransformGroup getNestedTransform( int index )
    {
        return ( nestedTransforms[index] );
    }
    
    /**
     * @return the list of nested TransformGroups in this model.
     */
    public final TransformGroup[] getNestedTransforms()
    {
        return ( nestedTransforms );
    }
    
    protected void setShapes( Shape3D[] shapes )
    {
        this.shapes = shapes;
        
        if ( debug )
        {
            this.nvs = new org.xith3d.utility.geometry.NormalsVisualizer[ shapes.length ];
            
            for ( int i = 0; i < nvs.length; i++ )
            {
                nvs[i] = new org.xith3d.utility.geometry.NormalsVisualizer( shapes[i], 0.5f );
                this.addChild( nvs[i] );
            }
        }
    }
    
    /**
     * @return the number of Shape nodes defined in the file.
     */
    public final int getShapesCount()
    {
        if ( shapes == null )
            return ( 0 );
        
        return ( shapes.length );
    }
    
    /**
     * @param index
     * 
     * @return a Shape contained in the model.
     */
    public final Shape3D getShape( int index )
    {
        return ( shapes[index] );
    }
    
    /**
     * @return an array of all Shapes contained in the model.
     */
    public final Shape3D[] getShapes()
    {
        return ( shapes );
    }
    
    protected void setLights( Light[] lights )
    {
        this.lights = lights;
    }
    
    /**
     * @return the number of Light nodes defined in the file.
     */
    public final int getLightsCount()
    {
        if ( lights == null )
            return ( 0 );
        
        return ( lights.length );
    }
    
    /**
     * @return a Light defined in the file.
     */
    public final Light getLight( int index )
    {
        return ( lights[index] );
    }
    
    /**
     * @return an array of all Lights defined in the file (may be null).
     */
    public final Light[] getLights()
    {
        return ( lights );
    }
    
    protected void setFogs( Fog[] fogs )
    {
        this.fogs = fogs;
    }
    
    /**
     * @return the number of Fog nodes defined in the file.
     */
    public final int getFogssCount()
    {
        if ( fogs == null )
            return ( 0 );
        
        return ( fogs.length );
    }
    
    /**
     * @return a Fog defined in the file.
     */
    public final Fog getFog( int index )
    {
        return ( fogs[index] );
    }
    
    /**
     * @return an array of all Fogs defined in the file (may be null).
     */
    public final Fog[] getFogs()
    {
        return ( fogs );
    }
    
    protected void setSounds( Sound[] sounds )
    {
        this.sounds = sounds;
    }
    
    /**
     * @return the number of Sound nodes defined in the file.
     */
    public final int getSoundsCount()
    {
        if ( sounds == null )
            return ( 0 );
        
        return ( sounds.length );
    }
    
    /**
     * @return a Sound node defined in the file.
     */
    public final Sound getSound( int index )
    {
        return ( sounds[index] );
    }
    
    /**
     * @return an array of all the Sound nodes defined
     * in the file (may be null).
     */
    public final Sound[] getSounds()
    {
        return ( sounds );
    }
    
    protected void setCameras( View[] cameras )
    {
        this.cameras = cameras;
    }
    
    /**
     * @return the number of cameras defined in the file.
     */
    public final int getCamerasCount()
    {
        if ( cameras == null )
            return ( 0 );
        
        return ( cameras.length );
    }
    
    /**
     * @return a camera defined in the file.
     */
    public final View getCamera( int index )
    {
        return ( cameras[index] );
    }
    
    /**
     * @return an array of all the cameras defined
     * in the file (may be null).
     */
    public final View[] getCameras()
    {
        return ( cameras );
    }
    
    protected void setSpawnTransforms( Matrix4f[] spawnTransforms )
    {
        this.spawnTransforms = spawnTransforms;
    }
    
    /**
     * @return the number of spawn transforms.
     */
    public final int getSpawnTransformsCount()
    {
        if ( spawnTransforms == null )
            return ( 0 );
        
        return ( spawnTransforms.length );
    }
    
    /**
     * @return a spawn transform defined in  this scene.
     */
    public final Matrix4f getSpawnTransform( int index )
    {
        return ( spawnTransforms[index] );
    }
    
    /**
     * @return an array of all spawn transforms defined in  this scene.
     */
    public final Matrix4f[] getSpawnTransforms()
    {
        return ( spawnTransforms );
    }
    
    protected void setMainGroup( GroupNode mainGroup )
    {
        this.mainSceneGroup = mainGroup;
    }
    
    /**
     * @return the main group of this model.
     * This may be the model itself of a nested BSPTreeGroup, OcTreeGroup, etc.
     */
    public final GroupNode getMainGroup()
    {
        return ( mainSceneGroup );
    }
    
    protected void setSkyBox( SkyBox skyBox )
    {
        this.skyBox = skyBox;
    }
    
    /**
     * @return this scene's SkyBox (if any, null otherwise).
     */
    public final SkyBox getSkyBox()
    {
        return ( skyBox );
    }
    
    /**
     * Adds the given String/Object pair to the table of named objects.
     */
    protected void addNamedObject( String name, NamedObject object )
    {
        if ( namedObjects == null )
        {
            namedObjects = new TreeMap<String, NamedObject>();
        }
        
        NamedObject existingObject = namedObjects.get( name );
        
        if ( existingObject == null )
        {
            namedObjects.put( name, object );
        }
        else if ( existingObject != object )
        {
            // key already exists - append a unique integer to end of name
            int nameIndex = 1;
            boolean done = false;
            while ( !done )
            {
                // Iterate starting from "[1]" until we find a unique key
                String tempName = name + "[" + nameIndex + "]";
                if ( namedObjects.get( tempName ) == null )
                {
                    namedObjects.put( tempName, object );
                    done = true;
                }
                
                nameIndex++;
            }
        }
    }
    
    /**
     * @return the number of named objects in this model file.
     */
    public final int getNamedObjectsCount()
    {
        if ( namedObjects == null )
            return ( 0 );
        
        return ( namedObjects.size() );
    }
    
    /**
     * @return a Map, which contains a list of all named
     * objects in the file and their associated scene graph objects.  The
     * naming scheme for file objects is file-type dependent, but may include
     * such names as the DEF names of Vrml or filenames of objects (as
     * in Lightwave 3D).
     */
    public final Map<String, NamedObject > getNamedObjects()
    {
        return ( namedObjects );
    }
    
    /**
     * @return the named object with the given name.
     * The naming scheme for file objects is file-type dependent, but may
     * include such names as the DEF names of Vrml or filenames of subjects
     * (as in Lightwave 3D).
     * 
     * @param name the name of the named object to retrieve
     */
    public final NamedObject getNamedObject( String name )
    {
        if ( namedObjects == null )
            return ( null );
        
        return ( namedObjects.get( name ) );
    }
    
    public void dumpNamedObjects( boolean printValues )
    {
        if ( namedObjects == null )
        {
            System.out.println( "[No named objects]" );
        }
        else
        {
            for ( String name : namedObjects.keySet() )
            {
                if ( printValues )
                    System.out.println( "\"" + name + "\": " + namedObjects.get( name ) );
                else
                    System.out.println( "\"" + name + "\"" );
            }
        }
    }
    
    /**
     * Sets the meta data for this object.
     * Primalaly used by MetaLoader
     */
    public void setMetaData( ModelMetaData metaData )
    {
        this.metaData = metaData;
    }
    
    /**
     * @return the meta data associated with this object.
     * Usualy only objects loaded from the MetaLoader have meta data.
     * For objects without meta data this returns null.
     */
    public final ModelMetaData getMetaData()
    {
        return ( metaData );
    }
    
    public void setMountTransforms( TransformGroup[] mountTransforms )
    {
        if ( this.mountTransforms != null )
        {
            for ( int i = this.mountTransforms.length - 1; i >= 0; i-- )
            {
                this.removeChild( this.mountTransforms[i] );
            }
        }
        
        this.mountTransforms = mountTransforms;
        
        if ( this.mountTransforms != null )
        {
            for ( int i = 0; i < this.mountTransforms.length; i++ )
            {
                this.addChild( this.mountTransforms[i] );
            }
        }
    }
    
    public final int getMountTransformsCount()
    {
        if ( mountTransforms == null )
            return ( 0 );
        
        return ( mountTransforms.length );
    }
    
    public final TransformGroup[] getMountTransforms()
    {
        return ( mountTransforms );
    }
    
    public final TransformGroup getMountTransform( int index )
    {
        return ( mountTransforms[index] );
    }
    
    public final TransformGroup getMountTransform( String name )
    {
        // We don't need a Map here, since there will be very few MTs!
        
        for ( int i = 0; i < mountTransforms.length; i++ )
        {
            if ( mountTransforms[i].getName().equals( name ) )
                return ( mountTransforms[i] );
        }
        
        return ( null );
    }
    
    public void addAnimationListener( AnimationListener l )
    {
        this.animListeners.add( l );
    }
    
    public void removeAnimationListener( AnimationListener l )
    {
        this.animListeners.remove( l );
    }
    
    protected final void fireOnAnimationStarted( ModelAnimation anim )
    {
        if ( anim != null )
        {
            for ( int i = 0; i < animListeners.size(); i++ )
            {
                animListeners.get( i ).onAnimationStarted( (ModelAnimation)anim );
            }
            
            finishedFired = false;
        }
    }
    
    protected final void fireOnAnimationFinished( ModelAnimation anim )
    {
        if ( ( anim != null ) && !finishedFired )
        {
            finishedFired = true;
            
            for ( int i = 0; i < animListeners.size(); i++ )
            {
                animListeners.get( i ).onAnimationFinished( (ModelAnimation)anim );
            }
        }
    }
    
    private void addAnimation( ModelAnimation anim )
    {
        if ( animsMap.containsKey( anim.getName() ) )
            return;
        
        _Anim_PrivilegedAccess.setModel( this, anim );
        
        animsMap.put( anim.getName(), anim );
        
        if ( anims == null )
        {
            anims = new ModelAnimation[ 64 ];
        }
        else if ( anims.length < animsMap.size() )
        {
            ModelAnimation[] tmp = new ModelAnimation[ (int)( ( anims.length + 1 ) * 1.5 ) ];
            System.arraycopy( anims, 0, tmp, 0, anims.length );
            anims = tmp;
        }
        
        anims[ animsMap.size() - 1 ] = anim;
    }
    
    public void setAnimations( ModelAnimation[] anims )
    {
        animsMap.clear();
        this.anims = null;
        
        for ( int i = 0; i < anims.length; i++ )
        {
            addAnimation( anims[i] );
        }
    }
    
    /**
     * @return true, if the Model contains at least one animation.
     */
    public final boolean hasAnimations()
    {
        return ( !animsMap.isEmpty() );
    }
    
    /**
     * @return the number of animations for this model.
     */
    public final int getAnimationsCount()
    {
        return ( animsMap.size() );
    }
    
    /**
     * @return a List of all animations contained in the model
     */
    public ModelAnimation[] getAnimations()
    {
        if ( animsMap.size() == 0 )
            return ( null );
        
        if ( anims.length != animsMap.size() )
        {
            ModelAnimation[] tmp = new ModelAnimation[ animsMap.size() ];
            System.arraycopy( anims, 0, tmp, 0, animsMap.size() );
            anims = tmp;
        }
        
        return ( anims );
    }
    
    /**
     * @return an animation by index
     */
    public ModelAnimation getAnimation( int index )
    {
        if ( index >= animsMap.size() )
            throw new ArrayIndexOutOfBoundsException( index );
        
        return ( anims[index] );
    }
    
    /**
     * @return an animation by name
     */
    public ModelAnimation getAnimation( String name )
    {
        return ( animsMap.get( name ) );
    }
    
    /**
     * Dumps all animations contained in the model.
     */
    public final void dumpAnimations()
    {
        if ( animsMap.size() == 0 )
        {
            System.out.println( "This model doesn't have any animations." );
            return;
        }
        
        for ( int i = 0; i < anims.length; i++ )
        {
            System.out.println( anims[i] );
        }
    }
    
    /**
     * Sets the current animation being used.
     * 
     * @param anim the animation to use
     */
    public void setCurrentAnimation( ModelAnimation anim )
    {
        if ( ( getCurrentAnimation() != null ) && ( animationStartTime >= 0f ) )
        {
            fireOnAnimationFinished( getCurrentAnimation() );
        }
        
        currentAnimation = anim;
        animationStartTime = -1f;
        
        if ( anim != null )
        {
            anim.reset();
            
            fireOnAnimationStarted( anim );
        }
    }
    
    /**
     * Sets the current animation being used.
     * 
     * @param name the animation to use
     */
    public final void setCurrentAnimation( String name )
    {
        setCurrentAnimation( animsMap.get( name ) );
    }
    
    /**
     * Sets the current animation being used.
     * 
     * @param index the animation to use
     */
    public final void setCurrentAnimation( int index )
    {
        setCurrentAnimation( anims[index] );
    }
    
    /**
     * @return the current animation being used.
     */
    public final ModelAnimation getCurrentAnimation()
    {
        return ( currentAnimation );
    }
    
    /**
     * Interpolates the animations towards the next frame.
     * 
     * @param animStartTime the game-time, at which the current loop of the animation started
     * @param absAnimTime the amount of game-time, the current loop of the current animation runs
     */
    public void interpolateAnimation( float animStartTime, float absAnimTime )
    {
        if ( getCurrentAnimation() == null )
            return;
        
        ModelAnimation anim = getCurrentAnimation();
        
        if ( anim.update( false, absAnimTime, getMountTransforms() ) )
        {
            fireOnAnimationFinished( anim );
            
            fireOnAnimationStarted( anim );
        }
        
        if ( debug )
        {
            for ( int i = 0; i < nvs.length; i++ )
            {
                nvs[i].update();
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean update( View view, Frustum frustum, long nanoTime, long nanoStep )
    {
        if ( getCurrentAnimation() != null )
        {
            float gameSeconds = TimingMode.NANOSECONDS.getSecondsAsFloat( nanoTime );
            
            if ( animationStartTime < 0f )
            {
                animationStartTime = gameSeconds;
            }
            
            interpolateAnimation( animationStartTime, ( gameSeconds - animationStartTime ) );
        }
        
        return ( true );
    }
    
    /**
     * @return a new Instance of this Model. It will at least share it's
     * Geometry and animation data with this one.
     */
    public Model getSharedInstance()
    {
        final Model newModel = new Model();
        
        final ArrayList<TransformGroup> newNestedTransforms = new ArrayList<TransformGroup>();
        final ArrayList<Shape3D> newShapes = new ArrayList<Shape3D>();
        final ArrayList<Light> newLights = new ArrayList<Light>();
        final ArrayList<Fog> newFogs = new ArrayList<Fog>();
        final ArrayList<Sound> newSounds = new ArrayList<Sound>();
        
        CopyListener listener = new CopyListener()
        {
            public void onNodeCopied( Node original, Node newInstance, boolean shared )
            {
                if ( original == Model.this.mainSceneGroup )
                    newModel.mainSceneGroup = (GroupNode)newInstance;
                
                if ( newInstance instanceof TransformGroup )
                    newNestedTransforms.add( (TransformGroup)newInstance );
                else if ( newInstance instanceof Shape3D )
                    newShapes.add( (Shape3D)newInstance );
                else if ( newInstance instanceof Light )
                    newLights.add( (Light)newInstance );
                else if ( newInstance instanceof Fog )
                    newFogs.add( (Fog)newInstance );
                else if ( newInstance instanceof Sound )
                    newSounds.add( (Sound)newInstance );
                
                if ( ( newInstance.getName() != null ) && ( newInstance.getName().length() > 0 ) )
                    newModel.addNamedObject( newInstance.getName(), newInstance );
            }
        };
        
        for ( int i = 0; i < numChildren(); i++ )
        {
            newModel.addChild( this.getChild( i ).sharedCopy( listener ) );
        }
        
        if ( newNestedTransforms.size() > 0 )
        {
            newModel.setNestedTransforms( newNestedTransforms.toArray( new TransformGroup[ newNestedTransforms.size() ] ) );
        }
        
        newModel.setShapes( newShapes.toArray( new Shape3D[ newShapes.size() ] ) );
        
        if ( newLights.size() > 0 )
        {
            newModel.setLights( newLights.toArray( new Light[ newLights.size() ] ) );
        }
        
        if ( newFogs.size() > 0 )
        {
            newModel.setFogs( newFogs.toArray( new Fog[ newFogs.size() ] ) );
        }
        
        if ( newSounds.size() > 0 )
        {
            newModel.setSounds( newSounds.toArray( new Sound[ newSounds.size() ] ) );
        }
        
        newModel.setCameras( this.getCameras() );
        
        newModel.setSpawnTransforms( this.getSpawnTransforms() );
        
        newModel.setSkyBox( this.getSkyBox() );
        
        
        if ( hasAnimations() )
        {
            for ( ModelAnimation animation : getAnimations() )
            {
                newModel.addAnimation( animation.getSharedCopy( newModel.getNamedObjects() ) );
            }
        }
        
        if ( this.getMountTransforms() != null )
        {
            TransformGroup[] newMTs = new TransformGroup[ getMountTransformsCount() ];
            
            for ( int i = 0; i < getMountTransformsCount(); i++ )
            {
                newMTs[i] = new TransformGroup( this.getMountTransform( i ).getTransform() );
            }
        }
        
        return ( newModel );
    }
    
    public Model()
    {
        super();
        
        this.setPickHost( Model.defaultPickHostValue );
    }
}
