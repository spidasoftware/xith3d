/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xith3d.scenegraph.traversal.impl;

import org.xith3d.loaders.models.Model;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Fog;
import org.xith3d.scenegraph.Group;
import org.xith3d.scenegraph.GroupNode;
import org.xith3d.scenegraph.Light;
import org.xith3d.scenegraph.Node;
import org.xith3d.scenegraph.OrderedGroup;
import org.xith3d.scenegraph.Shape3D;
import org.xith3d.scenegraph.Sound;
import org.xith3d.scenegraph.Switch;
import org.xith3d.scenegraph.TransformGroup;
import org.xith3d.scenegraph.traversal.DetailedTraversalCallback;

/**
 * Implements all TraversalCallback interfaces
 * This is to make my life easier when I want to do a detailed callback
 * because 90% of the time I still don't care at all about what
 * is going on on most of these nodes, I just need the "after" callback
 * @author mford
 */
public class DefaultDetailedTraversal implements DetailedTraversalCallback
{

    @Override
    public boolean traversalOperationCommon(Node node)
    {
        return true;
    }

    @Override
    public boolean traversalOperationCommon(GroupNode node)
    {
        return true;
    }

    @Override
    public boolean traversalCheckGroupCommon(GroupNode group)
    {
        return true;
    }

    @Override
    public boolean traversalCheckGroup(BranchGroup bg)
    {
        return true;
    }

    @Override
    public boolean traversalCheckGroup(Group group)
    {
        if (group instanceof Model)
        {
            return traversalCheckGroup((Model)group);
        }
        return true;
    }

    public boolean traversalCheckGroup(Model model)
    {
        return true;
    }

    @Override
    public boolean traversalCheckGroup(Switch sw)
    {
        return true;
    }

    @Override
    public boolean traversalCheckGroup(TransformGroup tg)
    {
        return true;
    }

    @Override
    public boolean traversalCheckGroup(OrderedGroup og)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(Node node)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(BranchGroup bg)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(Group group)
    {
        if (group instanceof Model)
        {
            return traversalOperation((Model)group);
        }
        return true;
    }

    public boolean traversalOperation(Model model)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(TransformGroup tg)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(OrderedGroup og)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(Switch sw)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(Shape3D shape)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(Light light)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(Fog fog)
    {
        return true;
    }

    @Override
    public boolean traversalOperation(Sound sound)
    {
        return true;
    }

    @Override
    public boolean traversalOperationCommonAfter(Node node)
    {
        return true;
    }

    @Override
    public boolean traversalOperationCommonAfter(GroupNode group)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(BranchGroup bg)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(Group group)
    {
         if (group instanceof Model)
        {
            return traversalOperationAfter((Model)group);
        }
        return true;
    }

    public boolean traversalOperationAfter(Model model)
    {
        return true;
    }
    
    @Override
    public boolean traversalOperationAfter(TransformGroup tg)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(OrderedGroup og)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(Node node)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(Switch sw)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(Shape3D shape)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(Light light)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(Fog fog)
    {
        return true;
    }

    @Override
    public boolean traversalOperationAfter(Sound sound)
    {
        return true;
    }


}
