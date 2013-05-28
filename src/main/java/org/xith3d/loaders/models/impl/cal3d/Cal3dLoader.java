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
package org.xith3d.loaders.models.impl.cal3d;

import java.io.IOException;
import java.net.URL;

import org.jagatoo.loaders.IncorrectFormatException;
import org.jagatoo.loaders.ParsingException;
import org.jagatoo.loaders.models._util.AnimationFactory;
import org.jagatoo.loaders.models._util.AppearanceFactory;
import org.jagatoo.loaders.models._util.GeometryFactory;
import org.jagatoo.loaders.models._util.NodeFactory;
import org.jagatoo.loaders.models._util.SpecialItemsHandler;
import org.jagatoo.loaders.models.cal3d.core.CalLoader;
import org.jagatoo.loaders.models.cal3d.core.CalModel;
import org.jagatoo.loaders.models.cal3d.loader.KCal3dLoader;
import org.jagatoo.loaders.models.cal3d.loader.KCal3dDefinition.Cal3dModelDef;
import org.xith3d.loaders.models.Model;
import org.xith3d.loaders.models.ModelLoader;

/**
 * @author Dave LLoyd
 * @author kman
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class Cal3dLoader extends ModelLoader
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected Model loadModel( URL url, String filenameBase, SourceModelType modelType, URL baseURL, String skin, float scale, int flags, AppearanceFactory appFactory, GeometryFactory geomFactory, NodeFactory nodeFactory, AnimationFactory animFactory, SpecialItemsHandler siHandler, Model model ) throws IOException, IncorrectFormatException, ParsingException
    {
        Cal3dModel cal3dModel = new Cal3dModel();
        
        boolean convertZup2Yup = modelType.getConvertFlag( flags );
        if ( convertZup2Yup )
            CalLoader.setLoadingMode( CalLoader.LOADER_ROTATE_X_AXIS );
        
        Cal3dModelDef modelDef = KCal3dLoader.loadCfg( url.openStream(), baseURL );
        CalModel calModel = KCal3dLoader.getCalModel( modelDef, filenameBase, baseURL );
        cal3dModel.setName( filenameBase );
        cal3dModel.init( calModel );
        
        return ( cal3dModel );
    }
    
    /**
     * Constructs a ModelLoader with the specified flags word.
     * 
     * @param flags
     */
    public Cal3dLoader( int flags )
    {
        super( flags );
    }
    
    /**
     * Constructs a ModelLoader with default flags.
     */
    public Cal3dLoader()
    {
        super();
    }
}
