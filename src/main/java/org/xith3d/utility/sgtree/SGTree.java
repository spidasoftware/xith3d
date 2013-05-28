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
package org.xith3d.utility.sgtree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.xith3d.scenegraph.SceneGraph;
import org.xith3d.utility.sgtree.infoitems.SGTInfo_Object;

/**
 * Xith3DTree
 * 
 * Displays a Xith3D scenegraph in a Swing Tree control
 * 
 * @author Daniel Selman (Java3D version)
 * @author Hawkwind
 * @author Amos Wenger (aka BlueSky)
 * @author Marvin Froehlich (aka Qudus)
 */
public class SGTree extends JFrame implements TreeSelectionListener
{
    private static final long serialVersionUID = -3374869095150421975L;
    
    private JEditorPane m_TextPane = null;
    
    private JSplitPane m_SplitPane = null;
    
    private JTree m_Tree = null;
    
    private static final String INFO_PACKAGE_NAME = org.xith3d.utility.sgtree.infoitems.SGTInfo_Object.class.getPackage().getName() + ".";
    
    private Map< String, Object > m_ObjectInfoTable;
    
    public SGTree()
    {
        super( "Xith3D Scenegraph Tree" );
        
        m_ObjectInfoTable = new Hashtable< String, Object >( 64 );
        
        buildObjectInfoTable();
    }
    
    private void buildObjectInfoTable()
    {
        m_ObjectInfoTable = new Hashtable< String, Object >( 64 );
        
        addObjectInfoTableItem( org.xith3d.scenegraph.AmbientLight.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Appearance.class );
        //addObjectInfoTableItem(org.xith3d.scenegraph.Bounds.class);
        addObjectInfoTableItem( org.xith3d.scenegraph.BoundingLeaf.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.BranchGroup.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.ColoringAttributes.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Clip.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.DirectionalLight.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Fog.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Group.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Leaf.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Light.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.LineAttributes.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Material.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Morph.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.GroupNode.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.OrderedGroup.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.PointAttributes.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.PointLight.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.PolygonAttributes.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.RenderingAttributes.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.SceneGraphObject.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Shape3D.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Sound.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.SpotLight.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Switch.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.TexCoordGeneration.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Texture.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.TextureAttributes.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.Transform3D.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.TransformGroup.class );
        addObjectInfoTableItem( org.xith3d.scenegraph.TransparencyAttributes.class );
    }
    
    private void addObjectInfoTableItem( Class< ? > sgClass )
    {
        final String className = sgClass.getName();
        final String infoClassName = INFO_PACKAGE_NAME + "SGTInfo_" + sgClass.getSimpleName();
        
        // System.out.println(className + "-->" + infoClassName);
        
        Class< ? > classObject = getClass( infoClassName );
        
        try
        {
            if ( classObject != null )
            {
                m_ObjectInfoTable.put( className, classObject.newInstance() );
                return;
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        System.err.println( "Unable to create a new instance of info class for: " + className );
    }
    
    SGTInfo_Object getObjectInfo( String szClass )
    {
        // look it up, maybe we have a direct match...
        // System.out.println( "Looking up: " + szClass );
        
        SGTInfo_Object objReturn = (SGTInfo_Object)m_ObjectInfoTable.get( szClass );
        // System.out.println( "Direct Match: " + szClass );
        
        Class< ? > classObject = null;
        
        try
        {
            classObject = Class.forName( szClass );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        Class< ? > parentClass = null;
        
        if ( classObject != null )
            parentClass = classObject.getSuperclass();
        
        while ( objReturn == null && parentClass != null )
        {
            // if we did not get a match we should walk
            // up the inheritance tree and look for a match on a base classe
            objReturn = (SGTInfo_Object)m_ObjectInfoTable.get( parentClass.getName() );
            parentClass = parentClass.getSuperclass();
            
            // System.out.println( "Parent Class: " + parentClass );
        }
        
        // System.out.println( "Returning: " + objReturn );
        
        return objReturn;
    }
    
    private Class< ? > getClass( String infoClass )
    {
        Class< ? > classObject = null;
        
        try
        {
            classObject = Class.forName( infoClass );
        }
        catch ( Exception e )
        {
            try
            {
                classObject = Class.forName( INFO_PACKAGE_NAME + "SGTInfo_Object" );
            }
            catch ( Exception e2 )
            {
                classObject = null;
            }
        }
        
        return classObject;
    }
    
    public void recurseObject( Object obj, DefaultMutableTreeNode parent )
    {
        if ( obj != null )
        {
            SGTInfo_Object objInfo = getObjectInfo( obj.getClass().getName() );
            
            if ( objInfo != null )
            {
                objInfo.addToTree( this, parent, obj );
            }
        }
        else
        {
            System.err.println( "Warning: ignored null object in recurseObject." );
        }
    }
    
    public void valueChanged( TreeSelectionEvent e )
    {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)m_Tree.getLastSelectedPathComponent();
        
        if ( node == null )
            return;
        
        Object sceneGraphObject = node.getUserObject();
        
        // get the _Info object for the node type
        SGTInfo_Object objInfo = getObjectInfo( sceneGraphObject.getClass().getName() );
        
        try
        {
            if ( objInfo != null )
            {
                String szText = objInfo.getInfo( sceneGraphObject );
                // System.out.println( szText );
                displayText( szText );
            }
            else
                displayText( "No Info Handler for:" + sceneGraphObject );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
            displayText( ex.toString() );
        }
    }
    
    private void displayText( String szText )
    {
        m_TextPane.setText( szText );
    }
    
    public void updateNodes( SceneGraph sceneGraph )
    {
        // Create the top level parent node.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode( "Scenegraph" );
        
        final int n = sceneGraph.getNumberOfBranchGroups();
        for ( int i = 0; i < n; i++ )
            recurseObject( sceneGraph.getBranchGroup( i ), top );
        
        // Create a tree that allows one selection at a time.
        m_Tree = new JTree( top );
        m_Tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        
        // Listen for when the selection changes.
        m_Tree.addTreeSelectionListener( this );
        
        // Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane( m_Tree );
        Dimension minimumSize = new Dimension( 100, 50 );
        treeView.setMinimumSize( minimumSize );
        
        // Add the scroll panes to a split pane.
        if ( m_SplitPane == null )
        {
            // Create the viewing pane.
            m_TextPane = new JEditorPane();
            m_TextPane.setEditable( false );
            JScrollPane htmlView = new JScrollPane( m_TextPane );
            
            m_SplitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
            
            m_SplitPane.setTopComponent( treeView );
            m_SplitPane.setBottomComponent( htmlView );
            
            htmlView.setMinimumSize( minimumSize );
            m_SplitPane.setDividerLocation( 100 );
            
            m_SplitPane.setPreferredSize( new Dimension( 500, 300 ) );
            
            // Add the split pane to this frame.
            getContentPane().add( m_SplitPane, BorderLayout.CENTER );
            
            pack();
            setVisible( true );
        }
        else
        {
            m_SplitPane.setTopComponent( treeView );
        }
    }
}
