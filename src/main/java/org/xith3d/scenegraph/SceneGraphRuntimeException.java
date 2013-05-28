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
package org.xith3d.scenegraph;

/**
 * The exception used by all of the scene graph objects and methods
 * that throw checked exceptions.
 * 
 * @author David Yazel
 */
public class SceneGraphRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -6675904874333678552L;
    
    /**
     * Constructs a new SceneGraphRuntimeException with null as its detail message.
     */
    public SceneGraphRuntimeException()
    {
        super();
    }
    
    /**
     * Constructs a new SceneGraphRuntimeException with the specified detail message.
     */
    public SceneGraphRuntimeException( String message )
    {
        super( message );
    }
    
    /**
     * Constructs a new SceneGraphRuntimeException with the specified detail message
     * and cause.
     */
    public SceneGraphRuntimeException( String message, Throwable cause )
    {
        super( message, cause );
    }
    
    /**
     * Constructs a new SceneGraphRuntimeException with the specified cause
     * and detail message of (cause==null ? null : cause.toString())
     * (which typically contains the class and detail message of cause).
     */
    public SceneGraphRuntimeException( Throwable cause )
    {
        super( cause );
    }
}
