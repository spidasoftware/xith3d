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
package org.xith3d.utility.classes.beans;

import java.lang.reflect.Method;

/**
 * Bean util class for runtime use of get/set methods based on member name
 * via Reflection, or field setting if available
 * 
 * @author Amos Wenger (aka BlueSky)
 */
public class BeanUtil {
    
    /**
     * This Exception is thrown when no getter has been found
     * @author Amos Wenger (aka BlueSky)
     */
    public static class NoGetterException extends Exception {
        
        private static final long serialVersionUID = -8473000634200869273L;
        
        /**
         * Create a new {@link NoGetterException}
         * @param msg
         */
        public NoGetterException(String msg) {
            super(msg);
        }
    }
    
    /**
     * This Exception is thrown when no setter has been found
     * @author Amos Wenger (aka BlueSky)
     */
    public static class NoSetterException extends Exception {
        
        private static final long serialVersionUID = -2022823537582982371L;
        
        /**
         * Create a new {@link NoSetterException}
         * @param msg
         */
        public NoSetterException(String msg) {
            super(msg);
        }
    }
    
    /**
     * Get a member using a getter method, or if there isn't any,
     * try to retry it via member access if it's public.
     * 
     * @param object
     *                The object from which to get the member
     * @param member
     *                The member to get
     * @return The value of the member asked for, or null if there's no getter
     */
    public static Object get(Object object, String member) {
        
        Object value = null;
        
        try {
            // Try to find a getter
            Method met = getGetter(object, member);
            value = met.invoke(object, (Object[]) null);
        } catch (NoGetterException e) {
            try {
                // If impossible, try to return it via member access
                value = object.getClass().getField(member).get(object);
            } catch (NoSuchFieldException ex) {
                throw new Error("Could not find either a getter or a field named "+member+" in object of type "+object.getClass().getSimpleName());
            } catch (Exception ex) {
                throw new Error(e);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
        
        return value;
        
    }
    
    /**
     * Finds the appropriate the getter for an Object.
     * 
     * @param object The object
     * @param member The member
     * @return The getter for the specified member of this Object
     * @throws NoGetterException if no getter can be found
     */
    public static Method getGetter(Object object, String member) throws NoGetterException {
        
        String name = member.substring(0, 1).toUpperCase()
        + member.substring(1);
        
        Method met = null;
        
        try {
            met = object.getClass().getMethod("get" + name, (Class[]) null);
        } catch (Exception e) {
            try {
                met = object.getClass().getMethod("is" + name, (Class[]) null);
            } catch (Exception ex) {
                throw new NoGetterException("Could not find a getter for member: "+member+" of object of type "+object.getClass().getSimpleName());
            }
        }
        
        return met;
        
    }
    
    /**
     * Sets a member to a value using a setter method, or if none found,
     * using member access if the member is public
     * 
     * @param object
     *                The object of which to set the member
     * @param member
     *                The member to change
     * @param value
     *                The value to set the member to
     */
    public static void set(Object object, String member, Object value) {
        
        try {
            
            // try to find a setter
            Method met = getSetter(object, member);
            try {
                met.invoke(object, new Object[] { value });
            } catch (Exception e) {
                throw new Error(e);
            }
            
        } catch (NoSetterException e) {
            
            try {
                // try to set it via member access
                object.getClass().getField(member).set(object, value);
            } catch (NoSuchFieldException ex) {
                throw new Error("Couldn't find setter nor member of name "+member+" in object of type : "+object.getClass().getSimpleName());
            } catch (Exception ex) {
                throw new Error(ex);
            }
            
        }
        
    }
    
    /**
     * Finds the appropriate setter for the member of an object.
     * 
     * @param object The object
     * @param member Its member
     * @return The setter
     * @throws NoSetterException if no setter can be found
     */
    public static Method getSetter(Object object, String member) throws NoSetterException {
        
        StringBuffer buffer = new StringBuffer(member.length() + 4);
        buffer.append("set");
        buffer.append(member.substring(0, 1).toUpperCase());
        buffer.append(member.substring(1));
        String name = buffer.toString();
        
        Method met = null;
        
        try {
            // First try to get the most evident method, getMethod(memberType);
            Class<?> clazz = object.getClass().getDeclaredField(member).getType();
            // Try to find the method
            met = object.getClass().getMethod(name, new Class[] { clazz });
        } catch (Exception e) {
            // Didn't work ? Well, let's try another way..
            Method[] methods = object.getClass().getMethods();
            search : for (Method potentialMethod : methods) {
                // If right method and only one parameter, we found it !!
                if(potentialMethod.getName().equals(name)
                        && potentialMethod.getParameterTypes().length == 1) {
                    met = potentialMethod;
                    break search;
                }
            }
        }
        
        if(met == null) {
            throw new NoSetterException("Could not find a setter for member "+member+" of object of type "+object.getClass().getSimpleName());
        }
        
        return met;
        
    }
    
}
