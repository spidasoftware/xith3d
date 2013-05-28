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
package org.xith3d.utility.sgtree.infoitems;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import org.xith3d.utility.sgtree.SGTree;

/**
 * Xith3DTree
 * 
 * Display a Xith3D scenegraph in a Swing Tree control
 * 
 * @author Daniel Selman (Java3D version)
 * @author Hawkwind
 * @author Amos Wenger (aka BlueSky)
 */
public abstract class SGTInfo_Object
{
    public SGTInfo_Object()
    {
    }
    
    public String insertSectionBreak( String szText )
    {
        return ( szText + "\r\n==============================\r\n" );
    }
    
    public int[] getCapabilityBits()
    {
        return null;
    }
    
    private List< ? > getChildrenHelper( Object obj )
    {
        List< ? > list = null;
        
        try
        {
            list = getChildren( obj );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return list;
    }
    
    List< ? > getChildren( Object obj )
    {
        
        return null;
    }
    
    // Vector + Enum
    Enumeration< ? > createCompoundEnumeration( Vector< Object > v, Enumeration< ? > enumx )
    {
        if ( enumx != null )
        {
            while ( enumx.hasMoreElements() == true && v != null )
                v.addElement( enumx.nextElement() );
        }
        
        if ( v == null )
            return enumx;
        
        return v.elements();
    }
    
    // Vector + Vector
    Enumeration< ? > createCompoundEnumeration( Vector< Object > v1, Vector< Object > v2 )
    {
        return createCompoundEnumeration( v1, v2.elements() );
    }
    
    // Enum + Enum
    Enumeration< ? > createCompoundEnumeration( Enumeration< ? > enum1, Enumeration< ? > enum2 )
    {
        Vector< Object > v = new Vector< Object >();
        
        while ( enum1 != null && enum1.hasMoreElements() == true )
            v.addElement( enum1.nextElement() );
        
        return createCompoundEnumeration( v, enum2 );
    }
    
    // Enum + Object
    Enumeration< ? > createCompoundEnumeration( Enumeration< ? > enum1, Object obj )
    {
        Vector< Object > v = new Vector< Object >();
        v.addElement( obj );
        
        while ( enum1 != null && enum1.hasMoreElements() == true )
            v.addElement( enum1.nextElement() );
        
        return v.elements();
    }
    
    int[] createCompoundArray( int[] a1, int[] a2 )
    {
        int[] aRet = null;
        int nTotalLen = 0;
        int nLen1 = 0;
        int nLen2 = 0;
        
        if ( a1 != null )
        {
            nTotalLen += a1.length;
            nLen1 = a1.length;
        }
        
        if ( a2 != null )
        {
            nTotalLen += a2.length;
            nLen2 = a2.length;
        }
        
        aRet = new int[ nTotalLen ];
        
        if ( a1 != null )
            System.arraycopy( a1, 0, aRet, 0, nLen1 );
        
        if ( a2 != null )
            System.arraycopy( a2, 0, aRet, nLen1, nLen2 );
        
        return aRet;
    }
    
    public void addToTree( SGTree tree, DefaultMutableTreeNode parent, Object obj )
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode( obj );
        parent.add( node );
        
        List< ? > list = getChildrenHelper( obj );
        
        if ( list != null )
        {
            for ( Object o: list )
            {
                tree.recurseObject( o, node );
            }
        }
    }
    
    protected String addFields( Class< ? > classObj, boolean bPublic )
    {
        String szText = new String();
        Field[] fieldArray = classObj.getDeclaredFields();
        
        if ( fieldArray != null )
        {
            for ( int n = 0; n < fieldArray.length; n++ )
            {
                Field field = fieldArray[ n ];
                int nModifiers = field.getModifiers();
                
                if ( bPublic != false )
                {
                    if ( Modifier.isPublic( nModifiers ) != false )
                        szText += field.getName() + "\r\n";
                }
                else
                {
                    if ( Modifier.isProtected( nModifiers ) != false )
                        szText += field.getName() + "\r\n";
                }
            }
        }
        
        return szText;
    }
    
    protected String addMethods( Class< ? > classObj, boolean bPublic )
    {
        String szText = new String();
        Method[] methodArray = classObj.getDeclaredMethods();
        
        if ( methodArray != null )
        {
            for ( int n = 0; n < methodArray.length; n++ )
            {
                Method method = methodArray[ n ];
                int nModifiers = method.getModifiers();
                
                if ( bPublic != false )
                {
                    if ( Modifier.isPublic( nModifiers ) != false )
                        szText += method.getName() + "\r\n";
                }
                else
                {
                    if ( Modifier.isProtected( nModifiers ) != false )
                        szText += method.getName() + "\r\n";
                }
            }
        }
        
        return szText;
    }
    
    public String getInfo( Object obj )
    {
        String szText = "";
        szText = insertSectionBreak( szText );
        
        szText += "Object\r\n";
        szText += "Type: " + obj.toString() + "\r\n";
        
        Class< ? > classObj = obj.getClass();
        
        szText += "\r\n";
        szText += "Public Fields\r\n";
        szText += "=============\r\n";
        szText += addFields( classObj, true );
        
        szText += "\r\n";
        szText += "Protected Fields\r\n";
        szText += "================\r\n";
        szText += addFields( classObj, false );
        
        szText += "\r\n";
        szText += "Public Methods\r\n";
        szText += "==============\r\n";
        szText += addMethods( classObj, true );
        
        szText += "\r\n";
        szText += "Protected Methods\r\n";
        szText += "=================\r\n";
        szText += addMethods( classObj, false );
        
        return szText;
    }
}
