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
package org.xith3d.render.preprocessing;

/**
 * The ordered state is used to denote that a particular atom must be sorted
 * according to an order state first, before material sorting. The ordered state
 * has the ordered group id, as well as the child index of the child of the
 * ordered group. These two numbers are used to sort inside the ordered group.
 * 
 * @author David Yazel
 */
public class OrderedState
{
    /**
     * Total number of allowable nested groups
     */
    public static final int MAX_NESTED_ORDER_GROUPS = 10;
    
    public long orderIds[] = new long[ MAX_NESTED_ORDER_GROUPS ];
    public int depth = 0;
    
    public OrderedState()
    {
    }
    
    /**
     * adds a new depth element to the array
     * 
     * @param number
     */
    public void addDepth( long number )
    {
        orderIds[ depth++ ] = number;
    }
    
    /**
     * Clones the ordered state. This is usually done when passing a new ordered
     * state down to a child of an ordered group.
     * 
     * @return the clone
     */
    @Override
    public Object clone()
    {
        OrderedState newState = new OrderedState();
        System.arraycopy( orderIds, 0, newState.orderIds, 0, orderIds.length );
        newState.depth = depth;
        
        return ( newState );
    }
    
    @Override
    public String toString()
    {
        StringBuffer s = new StringBuffer();
        s.append( "OrderedState[" );
        for ( int i = 0; i < depth; i++ )
        {
            if ( i > 0 )
                s.append( '-' );
            s.append( Long.toString( orderIds[ i ] ) );
        }
        s.append( "]" );
        
        return ( s.toString() );
    }
}
