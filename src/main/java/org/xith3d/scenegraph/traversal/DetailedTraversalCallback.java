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
package org.xith3d.scenegraph.traversal;

import org.xith3d.scenegraph.*;

/**
 * An implementation of this interface is passed to the traverse method
 * of a Node to implement the traversal operation.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public interface DetailedTraversalCallback
{
    /**
     * This method is (certainly) called for each Node in the
     * traversed Group, which is not a group itself.
     * It implements the operation to be done for the Nodes.
     * 
     * @param node the current Node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationCommon( Node node );
    
    /**
     * This method is (certainly) called for each Node in the
     * traversed Group, which is a group itself.
     * It implements the operation to be done for the Nodes.
     * 
     * @param node the current Node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationCommon( GroupNode node );
    
    /**
     * This method is (certainly) called for each group in the traversal.
     * It must return true to be further traversed (and its children)
     * 
     * @param group the Group to be checked
     * @return if false the traversal is not stopped, but the children are not further traversed
     */
    public boolean traversalCheckGroupCommon( GroupNode group );
    
    /**
     * This method is called for each group in the traversal.
     * It must return true to be further traversed (and its children)
     * 
     * @param bg the BranchGroup to be checked
     * @return if false the traversal is not stopped, but the children are not further traversed
     */
    public boolean traversalCheckGroup( BranchGroup bg );
    
    /**
     * This method is called for each group in the traversal.
     * It must return true to be further traversed (and its children)
     * 
     * @param group the Group to be checked
     * @return if false the traversal is not stopped, but the children are not further traversed
     */
    public boolean traversalCheckGroup( Group group );
    
    /**
     * This method is called for each group in the traversal.
     * It must return true to be further traversed (and its children)
     * 
     * @param sw the Switch group to be checked
     * @return if false the traversal is not stopped, but the children are not further traversed
     */
    public boolean traversalCheckGroup( Switch sw );
    
    /**
     * This method is called for each group in the traversal.
     * It must return true to be further traversed (and its children)
     * 
     * @param tg the TransformGroup to be checked
     * @return if false the traversal is not stopped, but the children are not further traversed
     */
    public boolean traversalCheckGroup( TransformGroup tg );
    
    /**
     * This method is called for each group in the traversal.
     * It must return true to be further traversed (and its children)
     * 
     * @param og the OrderedGroup to be checked
     * @return if false the traversal is not stopped, but the children are not further traversed
     */
    public boolean traversalCheckGroup( OrderedGroup og );
    
    /**
     * This method is called for each Node in the traversed Group.
     * It implements the operation to be done for the Nodes.
     * 
     * @param node the current Node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( Node node );
    
    /**
     * This method is called for each BranchGroup in the traversed Group.
     * It implements the operation to be done for the BranchGroups.
     * 
     * @param bg the current BranchGroup in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( BranchGroup bg );
    
    /**
     * This method is called for each Group in the traversed Group.
     * It implements the operation to be done for the Groups.
     * 
     * @param group the current Group in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( Group group );
    
    /**
     * This method is called for each TransformGroup in the traversed Group.
     * It implements the operation to be done for the TransformGroups.
     * 
     * @param tg the current TransformGroup in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( TransformGroup tg );
    
    /**
     * This method is called for each OrderedGroup in the traversed Group.
     * It implements the operation to be done for the OrderedGroups.
     * 
     * @param og the current OrderedGroup in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( OrderedGroup og );
    
    /**
     * This method is called for each Node in the traversed Group.
     * It implements the operation to be done for the Nodes.
     * 
     * @param sw the current Switch node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( Switch sw );
    
    /**
     * This method is called for each Node in the traversed Group.
     * It implements the operation to be done for the Nodes.
     * 
     * @param shape the current Shape3D in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( Shape3D shape );
    
    /**
     * This method is called for each Light node in the traversed Group.
     * It implements the operation to be done for the Lights.
     * 
     * @param light the current Light in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( Light light );
    
    /**
     * This method is called for each Light node in the traversed Group.
     * It implements the operation to be done for the Lights.
     * 
     * @param fog the current Fog in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( Fog fog );
    
    /**
     * This method is called for each Sound node in the traversed Group.
     * It implements the operation to be done for the Sound node.
     * 
     * @param sound the current Sound node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperation( Sound sound );
    
    /**
     * This method is (certainly) called for each Node in the traversed
     * Node, which is not a Group itself after the Node has been worked on.
     * It implements the operation to be done for the Groups.
     * 
     * @param node the current node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationCommonAfter( Node node );
    
    /**
     * This method is (certainly) called for each Group in the traversed
     * Node, which is a Group itself after the Node has been worked on.
     * It implements the operation to be done for the Groups.
     * 
     * @param group the current GroupNode in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationCommonAfter( GroupNode group );
    
    /**
     * This method is called for each BranchGroup in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the BranchGroups.
     * 
     * @param bg the current BranchGroup in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( BranchGroup bg );
    
    /**
     * This method is called for each Group in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the Groups.
     * 
     * @param group the current Group in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( Group group );
    
    /**
     * This method is called for each TransformGroup in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the TransformGroups.
     * 
     * @param tg the current TransformGroup in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( TransformGroup tg );
    
    /**
     * This method is called for each OrderedGroup in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the OrderedGroups.
     * 
     * @param og the current OrderedGroup in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( OrderedGroup og );
    
    /**
     * This method is called for each Node in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the Nodes.
     * 
     * @param node the current Node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( Node node );
    
    /**
     * This method is called for each Node in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the Nodes.
     * 
     * @param sw the current Switch node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( Switch sw );
    
    /**
     * This method is called for each Node in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the Nodes.
     * 
     * @param shape the current Shape3D in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( Shape3D shape );
    
    /**
     * This method is called for each Light node in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the Lights.
     * 
     * @param light the current Light in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( Light light );
    
    /**
     * This method is called for each Light node in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the Lights.
     * 
     * @param fog the current Fog in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( Fog fog );
    
    /**
     * This method is called for each Sound node in the traversed Group
     * after the Node has been worked on.
     * It implements the operation to be done for the Sound node.
     * 
     * @param sound the current Sound node in the traversal
     * @return if false, the traversal is stopped after this node
     */
    public boolean traversalOperationAfter( Sound sound );
}
