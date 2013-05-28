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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jagatoo.datatypes.NamedObject;
import org.jagatoo.loaders.IncorrectFormatException;
import org.jagatoo.loaders.ParsingException;
import org.jagatoo.loaders.models._util.AnimationFactory;
import org.jagatoo.loaders.models._util.AppearanceFactory;
import org.jagatoo.loaders.models._util.GeometryFactory;
import org.jagatoo.loaders.models._util.GroupType;
import org.jagatoo.loaders.models._util.LoaderUtils;
import org.jagatoo.loaders.models._util.NodeFactory;
import org.jagatoo.loaders.models._util.SpecialItemsHandler;
import org.jagatoo.loaders.models.ac3d.AC3DPrototypeLoader;
import org.jagatoo.loaders.models.ase.AseReader;
import org.jagatoo.loaders.models.bsp.BSPPrototypeLoader;
import org.jagatoo.loaders.models.bsp.BSPTextureAnimator;
import org.jagatoo.loaders.models.md2.MD2File;
import org.jagatoo.loaders.models.md3.MD3File;
import org.jagatoo.loaders.models.md5.MD5AnimationReader;
import org.jagatoo.loaders.models.md5.MD5MeshReader;
import org.jagatoo.loaders.models.obj.OBJPrototypeLoader;
import org.jagatoo.loaders.models.tds.TDSFile;

import org.openmali.vecmath2.Matrix4f;

import org.xith3d.loaders.models.animations.ModelAnimation;
import org.xith3d.loaders.models.conversion.XithAnimationFactory;
import org.xith3d.loaders.models.conversion.XithAppearanceFactory;
import org.xith3d.loaders.models.conversion.XithGeometryFactory;
import org.xith3d.loaders.models.conversion.XithNodeFactory;
import org.xith3d.loaders.models.util.specific.bsp.BSPTextureAnimatedShape;
import org.xith3d.resources.ResourceLocator;
import org.xith3d.scenegraph.Fog;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Sound;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.View;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.primitives.SkyBox;

/**
 * The abstract Loader class is used to specify the location
 * and elements of a file format to load.
 * The class is used to give loaders of various
 * file formats a common public interface.
 * 
 * Ideally the Scene and Model classes will be extended
 * to give the user a consistent interface to extract the
 * data.
 *
 * @see org.xith3d.loaders.models.base.LoadedGraph
 * @see org.xith3d.loaders.models.base.Scene
 * @see org.xith3d.loaders.models.Model
 * 
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 * @author Andrew Hanson (aka Patheros) added canLoad
 */
public class ModelLoader
{
    protected static final class SpecialItemsHandlerImpl implements SpecialItemsHandler
    {
        private final Model model;
        
        private ArrayList<TransformGroup> nestedTransforms = null;
        private HashSet<TransformGroup> nestedTransformsSet = null;
        private ArrayList<Shape3D> shapes = new ArrayList<Shape3D>();
        private ArrayList<Light> lights = null;
        private ArrayList<Fog> fogs = null;
        private ArrayList<Sound> sounds = null;
        private ArrayList<View> cameras = null;
        private ArrayList<Matrix4f> spawnTransforms = null;
        private ArrayList<TransformGroup> mountTransforms = null;
        private ArrayList<ModelAnimation> animations = null;
        
        public void addSpecialItem( SpecialItemType type, String name, Object item )
        {
            switch ( type )
            {
                case MAIN_GROUP:
                    model.setMainGroup( (GroupNode)item );
                    //( (GroupNode)item ).setRenderable( false );
                    if ( ( name != null ) && ( name.length() > 0 ) )
                        model.addNamedObject( name, (NamedObject)item );
                    break;
                case SHAPE:
                    shapes.add( (Shape3D)item );
                    if ( ( name != null ) && ( name.length() > 0 ) )
                        model.addNamedObject( name, (NamedObject)item );
                    break;
                case SPAWN_TRANSFORM:
                    if ( spawnTransforms == null )
                        spawnTransforms = new ArrayList<Matrix4f>();
                    spawnTransforms.add( (Matrix4f)item );
                    break;
                case MOUNT_TRANSFORM:
                    TransformGroup mt = new TransformGroup( (Matrix4f)item );
                    mt.setName( name );
                    if ( mountTransforms == null )
                        mountTransforms = new ArrayList<TransformGroup>();
                    mountTransforms.add( mt );
                    break;
                case NESTED_TRANSFORM:
                    if ( nestedTransformsSet == null )
                        nestedTransformsSet = new HashSet<TransformGroup>();
                    if ( nestedTransforms == null )
                        nestedTransforms = new ArrayList<TransformGroup>();
                    if ( !nestedTransformsSet.contains( item ) )
                    {
                        nestedTransformsSet.add( (TransformGroup)item );
                        nestedTransforms.add( (TransformGroup)item );
                        if ( ( name != null ) && ( name.length() > 0 ) )
                            model.addNamedObject( name, (NamedObject)item );
                    }
                    break;
                case ITEM:
                    break;
                case SUB_MODEL:
                    /*
                    try
                    {
                        Model model = modelsCache.get( name );
                        if ( model == null )
                        {
                            model = ModelLoader.getInstance().loadModel( new URL( name ), "", worldScale );
                            modelsCache.put( name, model );
                        }
                        TransformGroup tg = new TransformGroup();
                        tg.getTransform().set( (Matrix4f)item );
                        tg.updateTransform();
                        model = model.getSharedInstance();
                        //model.setShowBounds( true );
                        tg.addChild( model );
                        scene.addChild( tg );
                        //model.updateBounds( true );
                    }
                    catch ( Exception e )
                    {
                        e.printStackTrace();
                    }
                    */
                    break;
                case LIGHT:
                    if ( lights == null )
                        lights = new ArrayList<Light>();
                    lights.add( (Light)item );
                    if ( ( name != null ) && ( name.length() > 0 ) )
                        model.addNamedObject( name, (NamedObject)item );
                    break;
                case FOG:
                    if ( fogs == null )
                        fogs = new ArrayList<Fog>();
                    fogs.add( (Fog)item );
                    if ( ( name != null ) && ( name.length() > 0 ) )
                        model.addNamedObject( name, (NamedObject)item );
                    break;
                case SOUND:
                    if ( sounds == null )
                        sounds = new ArrayList<Sound>();
                    sounds.add( (Sound)item );
                    if ( ( name != null ) && ( name.length() > 0 ) )
                        model.addNamedObject( name, (NamedObject)item );
                    break;
                case SKYBOX:
                    model.setSkyBox( (SkyBox)item );
                    if ( ( name != null ) && ( name.length() > 0 ) )
                        model.addNamedObject( name, (NamedObject)item );
                    break;
                case NAMED_OBJECT:
                    model.addNamedObject( name, (NamedObject)item );
                    break;
            }
        }
        
        public NamedObject createTextureAnimator( BSPTextureAnimator animator, NamedObject shapeObj )
        {
            Shape3D shape = (Shape3D)shapeObj;
            
            BSPTextureAnimatedShape animatedShape = new BSPTextureAnimatedShape( shape.getGeometry(), shape.getAppearance(), animator );
            animatedShape.setName( shape.getName() );
            
            return ( animatedShape );
        }
        
        public void addAnimation( Object animation )
        {
            if ( animations == null )
                animations = new ArrayList<ModelAnimation>();
            
            animations.add( (ModelAnimation)animation );
        }
        
        public void flush()
        {
            if ( nestedTransforms != null )
            {
                model.setNestedTransforms( nestedTransforms.toArray( new TransformGroup[ nestedTransforms.size() ] ) );
                nestedTransforms = null;
                nestedTransformsSet = null;
            }
            
            model.setShapes( shapes.toArray( new Shape3D[ shapes.size() ] ) );
            shapes.clear();
            
            if ( lights != null )
            {
                model.setLights( lights.toArray( new Light[ lights.size() ] ) );
                lights = null;
            }
            
            if ( fogs != null )
            {
                model.setFogs( fogs.toArray( new Fog[ fogs.size() ] ) );
                fogs = null;
            }
            
            if ( sounds != null )
            {
                model.setSounds( sounds.toArray( new Sound[ sounds.size() ] ) );
                sounds = null;
            }
            
            if ( cameras != null )
            {
                model.setCameras( cameras.toArray( new View[ cameras.size() ] ) );
                cameras = null;
            }
            
            if ( animations != null )
            {
                model.setAnimations( animations.toArray( new ModelAnimation[ animations.size() ] ) );
                animations = null;
            }
            
            if ( mountTransforms != null )
            {
                model.setMountTransforms( mountTransforms.toArray( new TransformGroup[ mountTransforms.size() ] ) );
                mountTransforms = null;
            }
            
            if ( spawnTransforms != null )
            {
                model.setSpawnTransforms( spawnTransforms.toArray( new Matrix4f[ spawnTransforms.size() ] ) );
                spawnTransforms = null;
            }
        }
        
        public SpecialItemsHandlerImpl( Model model )
        {
            this.model = model;
        }
    }
    
    public static final float SCALE = 1.0f;
    
    public static final String STANDARD_MODEL_FILE_EXTENSION = "md3";
    
    private static final XithAppearanceFactory appFactory = new XithAppearanceFactory();
    private static final XithNodeFactory nodeFactory = new XithNodeFactory();
    private static final XithAnimationFactory animFactory = new XithAnimationFactory();
    
    private static ModelLoader singletonInstance = null;
    
    /**
     * This flag enables the loading of light objects into the scene.
     */
    public static final int LOAD_LIGHT_NODES = 1;
    
    /**
     * This flag enables the loading of fog objects into the scene.
     */
    public static final int LOAD_FOG_NODES = 2;
    
    /**
     * This flag enables the loading of sound objects into the scene.
     */
    public static final int LOAD_SOUND_NODES = 4;
    
    /**
     * This flag enables the loading of camera (view) objects into the scene.
     */
    public static final int LOAD_CAMERAS = 8;
    
    /**
     * This flag enables the loading of camera (view) objects into the scene.
     */
    public static final int LOAD_SUB_MODELS = 16;
    
    /**
     * This flag makes the loader to convert the model from z-up
     * to y-up if the source model format is known to use z-up
     * by default (like 3DS, MD2, MD3, MD5 and BSP).
     */
    public static final int CONVERT_Z_UP_TO_Y_UP_IF_EXPECTED = 32;
    
    /**
     * This flag forces the conversion from Z-up to Y-up for all model types.
     */
    public static final int ALWAYS_CONVERT_Z_UP_TO_Y_UP = 64;
    
    public static final int DEFAULT_FLAGS = ~0 & ~ALWAYS_CONVERT_Z_UP_TO_Y_UP;
    
    protected static enum SourceModelType
    {
        AC3D( false, Optimization.USE_DISPLAY_LISTS ),
        ASE( true, Optimization.USE_DISPLAY_LISTS ),
        BSP( true, Optimization.USE_DISPLAY_LISTS ),
        CAL3D( false, Optimization.NONE ),
        COLLADA( false, Optimization.NONE ),
        MD2( true, Optimization.NONE ),
        MD3( true, Optimization.NONE ),
        MD5( true, Optimization.NONE ),
        MS3D( false, Optimization.NONE ),
        OBJ( false, Optimization.USE_DISPLAY_LISTS ),
        TDS( true, Optimization.USE_DISPLAY_LISTS ),
        ;
        
        private final boolean hasDefaultZUp;
        private final Optimization defaultOptimization;
        
        public final boolean hasDefaultZUp()
        {
            return ( hasDefaultZUp );
        }
        
        public final boolean getConvertFlag( int flags )
        {
            if ( ( flags & ALWAYS_CONVERT_Z_UP_TO_Y_UP ) != 0 )
            {
                return ( true );
            }
            else if ( ( flags & CONVERT_Z_UP_TO_Y_UP_IF_EXPECTED ) != 0 )
            {
                return ( hasDefaultZUp );
            }
            else
            {
                return ( false );
            }
        }
        
        public final Optimization getDefaultOptimization()
        {
            return ( defaultOptimization );
        }
        
        private SourceModelType( boolean hasDefaultZUp, Optimization defaultOptimization )
        {
            this.hasDefaultZUp = hasDefaultZUp;
            this.defaultOptimization = defaultOptimization;
        }
    }
    
    /** Stores the types of objects that the user wishes to load.*/
    private int loadFlags;
    
    /**
     * This method sets the load flags for the file.  The flags should
     * equal 0 by default (which tells the loader to only load geometry).
     */
    public final void setFlags( int flags )
    {
        loadFlags = flags;
    }
    
    /**
     * @return the current loading flags setting.
     */
    public final int getFlags()
    {
        return ( loadFlags );
    }
    
    /**
     * This method sets the specified load flag for the file.
     * The flags should equal 0 by default (which tells the loader to only load geometry).
     * 
     * @param flag the flag to set/reset
     * @param enable true to enable the flag
     */
    public final void setFlag( int flag, boolean enable )
    {
        if ( enable )
            loadFlags |= flag;
        else
            loadFlags &= ~flag;
    }
    
    /**
     * @return the current loading flag setting.
     */
    public final boolean getFlag( int flag )
    {
        return ( ( loadFlags & flag ) > 0 );
    }
    
    
    protected SourceModelType extractModelType( URL url, String filename )
    {
        if ( filename.endsWith( ".ac" ) )
            return ( SourceModelType.AC3D );
        
        if ( filename.endsWith( ".ase" ) )
            return ( SourceModelType.ASE );
        
        if ( filename.endsWith( ".bsp" ) )
            return ( SourceModelType.BSP );
        
        if ( filename.endsWith( ".cfg" ) )
            return ( SourceModelType.CAL3D );
        
        if ( filename.endsWith( ".dae" ) )
            return ( SourceModelType.COLLADA );
        
        if ( filename.endsWith( ".md2" ) )
            return ( SourceModelType.MD2 );
        
        if ( filename.endsWith( ".md2" ) )
            return ( SourceModelType.MD2 );
        
        if ( filename.endsWith( ".md3" ) )
            return ( SourceModelType.MD3 );
        
        if ( filename.endsWith( ".md5mesh" ) )
            return ( SourceModelType.MD5 );
        
        if ( filename.endsWith( ".obj" ) )
            return ( SourceModelType.OBJ );
        
        if ( filename.endsWith( ".3ds" ) )
            return ( SourceModelType.TDS );
        
        if ( url == null )
            throw new Error( "Can't load the model file \"" + filename + "\"." );
        
        throw new Error( "Can't load the model file \"" + url.toString() + "\"." );
    }
    
    protected final SourceModelType extractModelType( URL url )
    {
        return ( extractModelType( url, url.getFile().toLowerCase() ) );
    }
    
    protected final SourceModelType extractModelType( String filename )
    {
        return ( extractModelType( null, filename ) );
    }
    
    protected Model loadModel( URL url, String filenameBase, SourceModelType modelType, URL baseURL, String skin, float scale, int flags, AppearanceFactory appFactory, GeometryFactory geomFactory, NodeFactory nodeFactory, AnimationFactory animFactory, SpecialItemsHandler siHandler, Model model ) throws IOException, IncorrectFormatException, ParsingException
    {
        boolean convertZup2Yup = modelType.getConvertFlag( flags );
        
        switch ( modelType )
        {
            case AC3D:
                AC3DPrototypeLoader.load( url.openStream(), baseURL, appFactory, geomFactory, nodeFactory, true, model, siHandler );
                break;
            case ASE:
                AseReader.load( url.openStream(), baseURL, appFactory, geomFactory, convertZup2Yup, scale, nodeFactory, animFactory, siHandler, model );
                break;
            case BSP:
                BSPPrototypeLoader.load( url.openStream(), filenameBase, baseURL, geomFactory, true, 0.03f, appFactory, nodeFactory, model, GroupType.BSP_TREE, siHandler );
                break;
            case CAL3D:
                break;
            case COLLADA:
                break;
            case MD2:
                MD2File.load( url.openStream(), baseURL, appFactory, skin, geomFactory, convertZup2Yup, scale, nodeFactory, animFactory, siHandler, model );
                break;
            case MD3:
                MD3File.load( url.openStream(), baseURL, appFactory, geomFactory, convertZup2Yup, scale, nodeFactory, animFactory, siHandler, model );
                break;
            case MD5:
            {
                Object[][][] boneWeights = MD5MeshReader.load( url.openStream(), baseURL, appFactory, skin, geomFactory, convertZup2Yup, scale, nodeFactory, animFactory, siHandler, model );
                
                ( (SpecialItemsHandlerImpl)siHandler ).flush();
                
                List< URL > animResources = new ResourceLocator( baseURL ).findAllResources( "md5anim", true, false );
                
                for ( URL animURL: animResources )
                {
                    String filename = LoaderUtils.extractFilenameWithoutExt( animURL );
                    MD5AnimationReader.load( animURL.openStream(), filename, baseURL, appFactory, geomFactory, convertZup2Yup, scale, nodeFactory, model.getShapes(), boneWeights, animFactory, siHandler, model );
                }
            }
                break;
            case MS3D:
                break;
            case OBJ:
                // TODO: Implement direct scaling!
                GroupNode rootGroup = model;
                if ( scale != 1.0f )
                {
                    TransformGroup scaleGroup = new TransformGroup();
                    scaleGroup.getTransform().setScale( scale );
                    model.addChild( scaleGroup );
                    model.setMainGroup( scaleGroup );
                    rootGroup = scaleGroup;
                }
                OBJPrototypeLoader.load( url.openStream(), baseURL, appFactory, skin, geomFactory, convertZup2Yup, scale, nodeFactory, siHandler, rootGroup );
                break;
            case TDS:
                TDSFile.load( url.openStream(), baseURL, appFactory, geomFactory, convertZup2Yup, nodeFactory, animFactory, siHandler, model );
        }
        
        return ( model );
    }
    
    public Model loadModel( URL url, String filenameBase, URL baseURL, String skin, float scale, int flags ) throws IOException, IncorrectFormatException, ParsingException
    {
        SourceModelType modelType = extractModelType( url );
        
        Model model = new Model();
        model.setName( filenameBase );
        
        XithGeometryFactory geomFactory = new XithGeometryFactory( modelType.getDefaultOptimization() );
        SpecialItemsHandlerImpl siHandler = new SpecialItemsHandlerImpl( model );
        
        model = loadModel( url, filenameBase, modelType, baseURL, skin, scale, flags, appFactory, geomFactory, nodeFactory, animFactory, siHandler, model );
        
        siHandler.flush();
        
        model.updateBounds( false );
        
        return ( model );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param url the URL to load the Model from.
     * @param skin the skin resource name
     * @param scale pre-scaling factor
     * @param flags loading flags
     */
    public final Model loadModel( URL url, String skin, float scale, int flags ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( url, LoaderUtils.extractFilenameWithoutExt( url ), LoaderUtils.extractBaseURL( url ), skin, scale, flags ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param url the URL to load the Model from.
     * @param scale pre-scaling factor
     * @param flags loading flags
     */
    public final Model loadModel( URL url, float scale, int flags ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( url, null, scale, flags ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param filename the filename to load the Model from
     * @param skin the skin resource name
     * @param scale pre-scaling factor
     * @param flags loading flags
     */
    public final Model loadModel( String filename, String skin, float scale, int flags ) throws IOException, IncorrectFormatException, ParsingException
    {
        File file = new File( filename );
        if ( !file.isAbsolute() )
            file = file.getAbsoluteFile();
        
        return ( loadModel( file.toURI().toURL(), skin, scale, flags ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param filename the filename to load the Model from
     * @param scale pre-scaling factor
     * @param flags loading flags
     */
    public final Model loadModel( String filename, float scale, int flags ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( filename, null, scale, flags ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param url the URL to load the Model from.
     * @param skin the skin resource name
     * @param scale pre-scaling factor
     */
    public final Model loadModel( URL url, String skin, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( url, skin, scale, getFlags() ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param url the URL to load the Model from.
     * @param scale pre-scaling factor
     * @param flags loading flags
     */
    public final Model loadModel( URL url, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( url, scale, getFlags() ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param filename the filename to load the Model from
     * @param skin the skin resource name
     * @param scale pre-scaling factor
     */
    public final Model loadModel( String filename, String skin, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( filename, skin, scale, getFlags() ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param filename the filename to load the Model from
     * @param scale pre-scaling factor
     */
    public final Model loadModel( String filename, float scale ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( filename, null, scale, getFlags() ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param url the URL to load the Model from.
     * @param skin the skin resource name
     */
    public final Model loadModel( URL url, String skin ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( url, skin, 1.0f, getFlags() ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param url the URL to load the Model from.
     */
    public final Model loadModel( URL url ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( url, null ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param filename the filename to load the Model from
     * @param skin the skin resource name
     */
    public final Model loadModel( String filename, String skin ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( filename, skin, 1.0f, getFlags() ) );
    }
    
    /**
     * This method loads the Model from a URL.
     * Any data files referenced by the model file are searched in the
     * following locations in the following order:
     * <ol>
     *     <li>By absolute path as referenced in the model file</li>
     *     <li>By relative filename (relative to the baseURL)</li>
     *     <li>In case of textures see TextureLoader's TextureStreamLocator architecture</li>
     * </ol>
     * 
     * @param filename the filename to load the Model from
     */
    public final Model loadModel( String filename ) throws IOException, IncorrectFormatException, ParsingException
    {
        return ( loadModel( filename, null ) );
    }
    
    
    /**
     * Constructs a ModelLoader with the specified flags word.
     * 
     * @param flags
     */
    protected ModelLoader( int flags )
    {
        this.loadFlags = flags;
    }
    
    /**
     * Constructs a ModelLoader with default flags.
     */
    protected ModelLoader()
    {
        this( DEFAULT_FLAGS );
    }
    
    /**
     * If you decide to use the Loader as a singleton, here is the method to
     * get the instance from.
     * 
     * @return a singleton instance of the Loader
     */
    public static ModelLoader getInstance()
    {
        if ( singletonInstance == null )
            singletonInstance = new ModelLoader();
        
        return ( singletonInstance );
    }
}
