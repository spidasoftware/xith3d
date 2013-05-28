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
package org.xith3d.utility.platform;

/**
 * Insert type comment here.
 * 
 * @author Kevin Kevin (aka Horati)
 */
public class OSInfo
{
    public enum Category
    {
        WINDOWS,
        MAC_OSX,
        LINUX,
        UNKNOWN;
    }
    
    public enum CategoryDetail
    {
        WINDOWS_98,
        WINDOWS_ME,
        WINDOWS_2K,
        WINDOWS_NT4,
        WINDOWS_XP1,
        WINDOWS_XP2,
        WINDOWS_VISTA,
        LINUX_24_KERNEL,
        LINUX_26_KERNEL,
        MAC_OSX_100,
        MAC_OSX_101,
        MAC_OSX_102,
        MAC_OSX_103,
        MAC_OSX_104,
        MAC_OSX_105,
        UNKNOWN;
    }
    
    private static OSInfo instance = null;
    private final Category category;
    private final CategoryDetail categoryDetail;
    
    public static OSInfo getInstance()
    {
        if ( OSInfo.instance == null )
        {
            OSInfo.instance = new OSInfo();
        }
        
        return ( OSInfo.instance  );
    }
    
    public Category getCategory()
    {
        return ( this.category );
    }
    
    public CategoryDetail getCategoryDetail()
    {
        return ( this.categoryDetail );
    }
    
    @Override
    public String toString()
    {
        return ( "O/S Category: " + getCategory() + " (" + getCategoryDetail() + ")" );
    }
    
    private OSInfo()
    {
        String osName = System.getProperty( "os.name" );
        String osVersion = System.getProperty( "os.version" );
        
        if ( osName.startsWith( "Mac OS X" ) )
        {
            this.category = OSInfo.Category.MAC_OSX;
            if ( osVersion.startsWith( "10.5" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.MAC_OSX_105;
            }
            else if ( osVersion.startsWith( "10.4" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.MAC_OSX_104;
            }
            else if ( osVersion.startsWith( "10.3" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.MAC_OSX_103;
            }
            else if ( osVersion.startsWith( "10.2" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.MAC_OSX_102;
            }
            else if ( osVersion.startsWith( "10.1" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.MAC_OSX_101;
            }
            else if ( osVersion.startsWith( "10.0" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.MAC_OSX_100;
            }
            else
            {
                this.categoryDetail = OSInfo.CategoryDetail.UNKNOWN;
            }
        }
        else if ( osName.startsWith( "Win" ) )
        {
            this.category = OSInfo.Category.WINDOWS;
            this.categoryDetail = OSInfo.CategoryDetail.UNKNOWN;
        }
        else if ( osName.startsWith( "Linux" ) )
        {
            this.category = OSInfo.Category.LINUX;
            
            if ( osVersion.startsWith( "2.6" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.LINUX_26_KERNEL;
            }
            else if ( osVersion.startsWith( "2.4" ) )
            {
                this.categoryDetail = OSInfo.CategoryDetail.LINUX_24_KERNEL;
            }
            else
            {
                this.categoryDetail = OSInfo.CategoryDetail.UNKNOWN;
            }
        }
        else
        {
            this.category = OSInfo.Category.UNKNOWN;
            this.categoryDetail = OSInfo.CategoryDetail.UNKNOWN;
        }
    }
}
