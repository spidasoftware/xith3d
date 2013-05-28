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
package org.xith3d.utility.general;

import java.util.ArrayList;

/**
 * :Id: SparseVector.java,v 1.5 2003/02/24 00:13:51 wurp Exp $
 * 
 * :Log: SparseVector.java,v $
 * Revision 1.5  2003/02/24 00:13:51  wurp
 * Formatted all java code for cvs (strictSunConvention.xml)
 * 
 * Revision 1.4  2001/06/20 04:05:42  wurp
 * added log4j.
 * 
 * Revision 1.3  2001/01/28 07:52:20  wurp
 * Removed <dollar> from Id and Log in log comments.
 * Added several new commands to AdminApp
 * Unfortunately, several other changes that I have lost track of.  Try diffing this
 * version with the previous one.
 * 
 * Revision 1.2  2000/12/16 22:07:33  wurp
 * Added Id and Log to almost all of the files that didn't have it.  It's
 * possible that the script screwed something up.  I did a commit and an update
 * right before I ran the script, so if a file is screwed up you should be able
 * to fix it by just going to the version before this one.
 * 
 * @author David Yazel
 */
public class SparseVector< T >
{
    /**
     * Class for holding information on a single node, including links to the
     * next and prior node.
     */
    private class LinkedNode
    {
        public LinkedNode next;
        public LinkedNode prev;
        public T obj;
        public int i;
        
        public LinkedNode( int i, T obj )
        {
            this.i = i;
            this.obj = obj;
            next = null;
            prev = null;
        }
    }
    
    private LinkedNode first = null;
    private LinkedNode last = null;
    
    public SparseVector()
    {
    }
    
    /**
     * Used to find the predecessor of a particular index.
     * 
     * @return null if there is no predecessor, the predecessor otherwise
     */
    private LinkedNode findPred( int i )
    {
        LinkedNode p1;
        LinkedNode p2;
        p1 = null;
        p2 = first;
        
        while ( p2 != null )
        {
            if ( p2.i >= i )
            {
                return p1;
            }
            
            p1 = p2;
            p2 = p2.next;
        }
        
        return p1;
    }
    
    /**
     * Inserts the element at the location specified.  If the element already exists then
     * the object will be replaced
     */
    public void insertAt( int i, T obj )
    {
        LinkedNode node = new LinkedNode( i, obj );
        
        if ( first == null )
        {
            node.prev = null;
            node.next = null;
            node.obj = obj;
            
            first = node;
            last = node;
        }
        else
        {
            LinkedNode pred = findPred( i );
            
            // if there is no predecessor then link the node into front of
            // the list
            if ( pred == null )
            {
                node.next = first;
                node.prev = null;
                first.prev = node;
                first = node;
                
                /*
                   } else if (pred.next.i == i) {
                      pred.next.obj = obj;
                 */
            }
            else
            {
                node.prev = pred;
                node.next = pred.next;
                pred.next = node;
                
                if ( node.next != null )
                {
                    node.next.prev = node;
                }
            }
        }
    }
    
    /**
     * @return the linked node at the position specified or null if
     * the node is not there.
     */
    private LinkedNode nodeAt( int i )
    {
        if ( first == null )
        {
            return ( null );
        }
        
        LinkedNode pred = findPred( i );
        
        if ( pred == null )
        {
            if ( first.i == i )
            {
                return ( first );
            }
            
            return null;
        }
        
        if ( pred.next == null )
        {
            return ( null );
        }
        
        if ( pred.next.i == i )
        {
            return ( pred.next );
        }
        
        return ( null );
    }
    
    /**
     * @return the element at the location specified or null if the
     * the object does not exist
     */
    public T elementAt( int i )
    {
        LinkedNode node = nodeAt( i );
        
        if ( node == null )
            return ( null );
        
        return ( node.obj );
    }
    
    /**
     * Removes the specified element from the list.
     */
    public void removeAt( int i )
    {
        LinkedNode node = nodeAt( i );
        
        if ( node != null )
        {
            if ( last == node )
            {
                last = node.prev;
            }
            
            if ( first == node )
            {
                first = node.next;
            }
            
            if ( node.prev != null )
            {
                node.prev.next = node.next;
            }
            
            if ( node.next != null )
            {
                node.next.prev = node.prev;
            }
        }
    }
    
    /**
     * Sorts all the elements into two categories, those that fall within certain
     * certain bounds, and those that fall without those same bounds.
     */
    public void sortElements( ArrayList< T > within, ArrayList< T > without, int start, int stop )
    {
        LinkedNode p = first;
        
        while ( p != null )
        {
            if ( ( p.i < start ) || ( p.i > stop ) )
            {
                without.add( p.obj );
            }
            else
            {
                within.add( p.obj );
            }
            
            p = p.next;
        }
    }
    
    /*
    // test the code
    public static void main( String[] args )
    {
        final int NUM = 15000;
        Random r = new Random( 164372 );
        HashSet< Integer > map = new HashSet< Integer >( NUM );
        SparseVector< Integer > v;
        
        // build the test cases
        System.out.println( "Building test cases" );
        
        int n = 0;
        
        while ( n < NUM )
        {
            int i = r.nextInt( 8000000 );
            
            if ( !map.contains( i ) )
            {
                map.add( i );
                n++;
            }
        }
        
        System.out.println( "Inserting test cases into SparseVector" );
        
        v = new SparseVector< Integer >();
        
        for ( Integer i: map )
        {
            v.insertAt( i.intValue(), i );
        }
        
        System.out.println( "Getting all the test cases from SparseVector in order" );
        
        Object[] objs = map.toArray();
        for ( int i = 0; i < NUM; i++ )
        {
            Integer obj = (Integer)objs[ i ];
            Integer found = (Integer)v.elementAt( obj.intValue() );
            
            if ( found == null )
            {
                System.out.println( "Error finding test case in sparse vector" );
                System.exit( 0 );
            }
            
            if ( found.intValue() != obj.intValue() )
            {
                System.out.println( "Error matching retrieved value" );
                System.exit( 0 );
            }
        }
        
        System.out.println( "Removing 25 percent of items..." );
        
        for ( int i = (int)( NUM * 0.75f ); i < NUM; i++ )
        {
            Integer obj = (Integer)objs[ i ];
            v.removeAt( obj.intValue() );
            n--;
        }
        
        System.out.println( "Checking test cases again" );
        
        for ( int i = 0; i < n; i++ )
        {
            Integer obj = (Integer)objs[ i ];
            Integer found = (Integer)v.elementAt( obj.intValue() );
            
            if ( found == null )
            {
                System.out.println( "Error finding test case in sparse vector" );
                System.exit( 0 );
            }
            
            if ( found.intValue() != obj.intValue() )
            {
                System.out.println( "Error matching retrieved value" );
                System.exit( 0 );
            }
        }
        
        System.out.println( "Test complete" );
    }
    */
}
