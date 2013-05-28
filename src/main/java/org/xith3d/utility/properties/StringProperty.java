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
package org.xith3d.utility.properties;

/**
 * :Id: StringProperty.java,v 1.5 2003/02/24 00:13:53 wurp Exp $
 * 
 * :Log: StringProperty.java,v $
 * Revision 1.5  2003/02/24 00:13:53  wurp
 * Formatted all java code for cvs (strictSunConvention.xml)
 * 
 * Revision 1.4  2001/06/20 04:05:42  wurp
 * added log4j.
 * 
 * Revision 1.3  2001/01/02 11:12:26  wurp
 * Added getProperty to PropertyManager; made convertToString
 * slightly more efficient in StringProperty.
 * 
 * Revision 1.2  2000/10/21 18:23:21  wizofid
 * Added setter method
 * 
 * Revision 1.2  2000/09/23 05:19:42  dyazel
 * Continuing the integration with client/server code
 * 
 * @author David Yazel
 */
public class StringProperty extends Property implements PropertyInterface
{
    protected String value;
    
    public void setString( String v )
    {
        this.value = v;
    }
    
    public String getString()
    {
        return ( value );
    }
    
    /**
     * {@inheritDoc}
     */
    public String convertToString()
    {
        return ( value );
    }
    
    /**
     * {@inheritDoc}
     */
    public void convertFromString( String text )
    {
        value = text;
    }
    
    public StringProperty( String value, String name, String comment )
    {
        super( name, comment );
        
        this.value = value;
    }
}
