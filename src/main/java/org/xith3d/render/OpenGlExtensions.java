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
package org.xith3d.render;

/**
 * Insert package comments here
 * 
 * Originally Coded by David Yazel on Sep 20, 2003 at 1:51:18 PM.
 * 
 * @author Marvin Froehlich (aka Qudus)
 */
public class OpenGlExtensions
{
    public static boolean ARB_vertex_buffer_object = false;
    public static boolean ARB_shadow = false;
    public static boolean GL_ARB_vertex_program = false;
    public static boolean GL_ARB_fragment_program = false;
    public static boolean GL_ARB_vertex_shader = false;
    public static boolean GL_ARB_fragment_shader = false;
    public static boolean GL_ARB_texture_cube_map = false;
    public static boolean GL_EXT_texture_filter_anisotropic = false;
    public static boolean GL_EXT_separate_specular_color = false;
    public static boolean GL_EXT_texture_cube_map = false;
    public static boolean GL_ARB_transpose_matrix = false;
    public static boolean GL_NV_texgen_reflection = false;
    public static boolean GL_KTX_buffer_region = false;
    public static boolean GL_CUSTOM_VERTEX_ATTRIBUTES = false;
    
    /**
     * Sets the static booleans for some known extensions used by the renderer.
     * 
     * @param oglInfo
     */
    public static void setExtensions( OpenGLInfo oglInfo )
    {
        ARB_vertex_buffer_object = oglInfo.hasExtension( "GL_ARB_vertex_buffer_object" ) && !oglInfo.getVendor().toLowerCase().contains( "intel" );
        ARB_shadow = oglInfo.hasExtension( "GL_ARB_shadow" );
        GL_ARB_vertex_program = oglInfo.hasExtension( "GL_ARB_vertex_program" );
        GL_ARB_fragment_program = oglInfo.hasExtension( "GL_ARB_fragment_program" );
        GL_ARB_vertex_shader = oglInfo.hasExtension( "GL_ARB_vertex_shader" );
        GL_ARB_fragment_shader = oglInfo.hasExtension( "GL_ARB_fragment_shader" );
        GL_ARB_texture_cube_map = oglInfo.hasExtension( "GL_ARB_texture_cube_map" );
        GL_EXT_texture_filter_anisotropic = oglInfo.hasExtension( "GL_EXT_texture_filter_anisotropic" );
        GL_EXT_separate_specular_color = oglInfo.hasExtension( "GL_EXT_separate_specular_color" );
        GL_EXT_texture_cube_map = oglInfo.hasExtension( "GL_EXT_texture_cube_map" );
        GL_NV_texgen_reflection = oglInfo.hasExtension( "GL_NV_texgen_reflection" );
        GL_ARB_texture_cube_map = GL_EXT_texture_cube_map = GL_NV_texgen_reflection = GL_EXT_texture_cube_map | GL_ARB_texture_cube_map | GL_NV_texgen_reflection;
        GL_ARB_transpose_matrix = oglInfo.hasExtension( "GL_ARB_transpose_matrix" );
        GL_KTX_buffer_region = oglInfo.hasExtension( "GL_KTX_buffer_region" );
        GL_CUSTOM_VERTEX_ATTRIBUTES = oglInfo.getVersionMajor() >= 2;
    }
}
