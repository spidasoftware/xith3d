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
package org.xith3d.selection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author cylab
 */
public class ActionList
{
    private static class ActionEntry implements Comparable<ActionEntry>
    {
        Action action;
        int preferredPosition;

        public ActionEntry( int preferredPosition, Action action )
        {
            this.action = action;
            this.preferredPosition = preferredPosition;
        }

        public int compareTo( ActionEntry o )
        {
            return ( preferredPosition - o.preferredPosition );
        }
    }
    private ArrayList<ActionEntry> actions = new ArrayList<ActionEntry>( 16 );

    public void addAll( ActionList source )
    {
        for ( int i = 0; i < source.actions.size(); i++ )
        {
            ActionEntry entry = source.actions.get( i );
            addAction( entry.preferredPosition, entry.action  );
        }
    }

    /**
     * Add an Action at the prefered position.
     * 
     * The position is not an index and adding two actions at the same position will
     * result in both action to be displayed. If the list is displayed as Context menu, this
     * means, that both Actions show below an Action with a lower preferedPosition
     * and above an Action with a higher preferredPosition value.
     * 
     * If an Action has the same ID property as an already contained action, this action is
     * overwritten. This is useful for merging ActionLists from multiple selected Nodes.
     * 
     * @param action the action to add
     * @param preferredPosition the position in the list where the action should be displayed
     */
    public void addAction( int preferredPosition, Action action )
    {
        removeAction( action );
        actions.add( new ActionEntry( preferredPosition, action  )  );
        Collections.sort( actions );
    }

    /**
     * Removes an Action from this list.
     * 
     * @param action the Action to be removed
     * @return true, if the Action was contained in this list.
     */
    public boolean removeAction( Action action )
    {
        boolean removed = false;
        for ( int i = actions.size() - 1; i >= 0; i-- )
        {
            ActionEntry actionEntry = actions.get( i );
            if ( actionEntry.action.equals( action ) )
            {
                actions.remove( i );
                removed = true;
            }
        }
        Collections.sort( actions );
        return removed;
    }

    /**
     * Returns the Actions contained in this list in the order resulting from the preferred positions.
     * 
     * @return A consolidated and sorted Action list
     */
    public List<Action> getActions()
    {
        ArrayList<Action> list = new ArrayList<Action>( actions.size() );
        for ( int i = 0; i < actions.size(); i++ )
        {
            list.add( actions.get( i ).action );
        }
        return list;
    }
}
