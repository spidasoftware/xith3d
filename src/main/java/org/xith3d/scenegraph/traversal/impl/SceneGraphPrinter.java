/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xith3d.scenegraph.traversal.impl;

import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Node;

/**
 *
 * @author mford
 */
public class SceneGraphPrinter extends DefaultDetailedTraversal {

    private StringBuilder sceneGraphString = new StringBuilder();
    int depth = 0;

    @Override
    public boolean traversalOperationCommon(Node node) {

        for (int i=0; i<depth; i++)
        {
            sceneGraphString.append('\t');
        }
        sceneGraphString.append(node.getClass() + ": ");
        sceneGraphString.append(node.getName() + "\n");
        return true;
    }

    @Override
    public boolean traversalOperationCommon(GroupNode group)
    {
        traversalOperationCommon((Node)group);
        depth++;
        return true;
    }

    @Override
    public boolean traversalOperationCommonAfter(GroupNode group)
    {
        depth--;
        return true;
    }

    public String getSceneGraphString()
    {
        return sceneGraphString.toString();
    }

}
