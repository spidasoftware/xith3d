package org.xith3d.loaders.models.impl.dae;

import java.awt.Font;
import java.util.HashMap;

import org.jagatoo.loaders.models.collada.datastructs.animation.Bone;
import org.jagatoo.loaders.models.collada.datastructs.animation.Skeleton;
import org.openmali.vecmath2.Colorf;
import org.openmali.vecmath2.Point3f;
import org.openmali.vecmath2.Vector3f;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.Transform;
import org.xith3d.scenegraph.Geometry.Optimization;
import org.xith3d.scenegraph.primitives.Line;
import org.xith3d.scenegraph.primitives.Sphere;
import org.xith3d.scenegraph.primitives.TextBillboard;

/**
 * A Skeleton visualizer in x-ray style, e.g. it
 * displays lines for each joint.
 *
 * @author Amos Wenger (aka BlueSky)
 */
public class SkeletonXRayVisualizer extends Group
{
    /** The skeleton we're displaying */
    private final Skeleton skeleton;
    
    /** A Bone->Line map to update them accordingly */
    private final HashMap<Bone, Line> mapLines = new HashMap<Bone, Line>();
    private final HashMap<Bone, Transform> mapSpheres = new HashMap<Bone, Transform>();
    
    /**
     * Updates a specific bone/line.
     * 
     * @param parentTransform
     * @param bone
     */
    private void update( Bone parentBone, Bone bone )
    {
        if ( !mapLines.containsKey( bone ) )
        {
            Line line = new Line( Point3f.ZERO, ( ( bone.numChildren() > 0 ) ? Colorf.GREEN : Colorf.WHITE ) );
            line.setAntialiasingEnabled( true );
            line.setWidth( 4 );
            line.getGeometry().setOptimization( Optimization.NONE );
            
            mapLines.put( bone, line );
            this.addChild( line );
        }
        
        if ( !mapSpheres.containsKey( bone ) )
        {
            Transform transform = new Transform();
            transform.add( new Sphere( 0.05f, 10, 10, Colorf.RED ) );
            transform.add(
                    TextBillboard.createFixedHeight(
                            0.2f,
                            bone.getName(),
                            Colorf.WHITE,
                            Font.decode( "Arial-plain-20" )
                    )
            );
            
            mapSpheres.put( bone, transform );
            this.addChild( transform );
        }
        
        Vector3f b = new Vector3f();
        b.set( ( parentBone == null ) ? bone.getAbsoluteTranslation() : parentBone.getAbsoluteTranslation() );
        Vector3f e = new Vector3f();
        e.set( bone.getAbsoluteTranslation() );
        
        mapLines.get( bone ).setCoordinates( b, e );
        mapSpheres.get( bone ).setTranslation( b.getX(), b.getY(), b.getZ() );
        
        for ( int i = 0; i < bone.numChildren(); i++ )
        {
            update( bone, bone.getChild( i ) );
        }
    }
    
    /**
     * Updates the visualizer.
     */
    public final void update()
    {
        update( null, skeleton.getRootBone() );
    }
    
    /**
     * Create a new {@link SkeletonXRayVisualizer}
     *
     * @param skeleton
     */
    public SkeletonXRayVisualizer( Skeleton skeleton )
    {
        this.skeleton = skeleton;
    }
}
