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
package org.xith3d.loaders.models.util.specific;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.ModelLoader;
import org.xith3d.loaders.models.animations.ModelAnimation;
import org.xith3d.loaders.models.animations.PrecomputedAnimationKeyFrame;
import org.xith3d.loaders.models.animations.PrecomputedAnimationKeyFrameController;
import org.xith3d.scenegraph.Geometry;
import org.xith3d.scenegraph.Shape3D;

/**
 * Utility methods for OBJ models.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OBJTools
{
    private static Model loadOBJFrames( ModelLoader loader, final String baseURL, ArrayList<Geometry[]> frames )
    {
        Model baseModel = null;
        
        int frameCount = -1;
        
        for ( int i = 1; i < Integer.MAX_VALUE; i++ )
        {
            // Find file name
            String num = Integer.toString( i );
            final float length = num.length();
            for ( int j = 0; j < 6 - length; j++ )
            {
                num = "0" + num;
            }
            
            final String frameURL = baseURL + "_" + num + ".obj";
            
            try
            {
                Model frameModel = loader.loadModel( new URL( frameURL ) );
                
                if ( i == 1 )
                {
                    baseModel = frameModel;
                    
                    if ( baseModel.getShapesCount() == 0 )
                    {
                        System.err.println( "Incorrectly loaded file : " + frameURL );
                    }
                }
                
                if ( frameModel.getShapesCount() != baseModel.getShapesCount() )
                {
                    throw new Error( "Incorrectly loaded file : " + frameURL );
                }
                
                Geometry[] geoms = new Geometry[ frameModel.getShapesCount() ];
                for ( int j = 0; j < frameModel.getShapesCount(); j++ )
                {
                    geoms[j] = frameModel.getShape( j ).getGeometry();
                }
                
                frames.add( geoms );
            }
            catch ( final FileNotFoundException e )
            {
                if ( frameCount == -1 )
                {
                    e.printStackTrace();
                }
                
                // If file not found it means we got to the end of the anim
                return ( baseModel );
            }
            catch ( final IOException e )
            {
                if ( frameCount == -1 )
                {
                    e.printStackTrace();
                }
                
                // If file not found it means we got to the end of the anim
                return ( baseModel );
            }
            
            // If loaded successfully, maybe one more ?
            frameCount++;
        }
        
        return ( baseModel );
    }
    
    public static Model loadPrecomputedModel( URL url )
    {
        ArrayList<Geometry[]> frames = new ArrayList<Geometry[]>();
        
        if ( url.toExternalForm().endsWith( ".amo" ) )
        {
            try
            {
                BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream() ) );
                
                String objFileName = reader.readLine();
                
                objFileName = url.toExternalForm().substring( 0, url.toExternalForm().lastIndexOf( "/" ) ) + "/" + objFileName;
                Model baseModel = loadOBJFrames( ModelLoader.getInstance(), objFileName, frames );
                
                ArrayList<ModelAnimation> anims = new ArrayList<ModelAnimation>();
                
                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    StringTokenizer tokenizer = new StringTokenizer( line );
                    String animName = tokenizer.nextToken();
                    int from = Integer.valueOf( tokenizer.nextToken() );
                    int to = Integer.valueOf( tokenizer.nextToken() );
                    /* boolean loop = (Integer.valueOf( */tokenizer.nextToken()/* * ) == 1)*/;
                    
                    int numFrames = to - from + 1;
                    
                    PrecomputedAnimationKeyFrameController[] controllers = new PrecomputedAnimationKeyFrameController[ baseModel.getShapesCount() ];
                    
                    for ( int i = 0; i < baseModel.getShapesCount(); i++ )
                    {
                        Shape3D shape = baseModel.getShape( i );
                        
                        PrecomputedAnimationKeyFrame[] keyFrames = new PrecomputedAnimationKeyFrame[ numFrames ];
                        
                        int k = 0;
                        for ( int j = from; j <= to; j++ )
                        {
                            keyFrames[k++] = new PrecomputedAnimationKeyFrame( frames.get( j )[i] );
                        }
                        
                        controllers[i] = new PrecomputedAnimationKeyFrameController( keyFrames, shape );
                    }
                    
                    anims.add( new ModelAnimation( animName, numFrames, 25f, controllers ) );
                }
                
                baseModel.setAnimations( anims.toArray( new ModelAnimation[ anims.size() ] ) );
                
                return ( baseModel );
            }
            catch ( FileNotFoundException e )
            {
                e.printStackTrace();
                
                return ( null );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                
                return ( null );
            }
        }
        
        // not ".amo"!
        {
            Model baseModel = loadOBJFrames( ModelLoader.getInstance(), url.toExternalForm(), frames );
            
            PrecomputedAnimationKeyFrameController[] controllers = new PrecomputedAnimationKeyFrameController[ baseModel.getShapesCount() ];
            
            for ( int i = 0; i < baseModel.getShapesCount(); i++ )
            {
                Shape3D shape = baseModel.getShape( i );
                
                PrecomputedAnimationKeyFrame[] keyFrames = new PrecomputedAnimationKeyFrame[ frames.size() ];
                
                for ( int j = 0 ; j < frames.size(); j++ )
                {
                    keyFrames[j] = new PrecomputedAnimationKeyFrame( frames.get( j )[i] );
                }
                
                controllers[i] = new PrecomputedAnimationKeyFrameController( keyFrames, shape );
            }
            
            ModelAnimation[] anims = new ModelAnimation[]
            {
                new ModelAnimation( "default", frames.size(), 25f, controllers )
            };
            
            baseModel.setAnimations( anims );
            
            return ( baseModel );
        }
    }
    
    public static Model loadPrecomputedModel( File file )
    {
        try
        {
            return ( loadPrecomputedModel( file.toURI().toURL() ) );
        }
        catch ( MalformedURLException e )
        {
            e.printStackTrace();
            
            return ( null );
        }
    }
    
    public static Model loadPrecomputedModel( String filename )
    {
        return ( loadPrecomputedModel( new File( filename ) ) );
    }
}
