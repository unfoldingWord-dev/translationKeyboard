/*
 * Copyright (c) 2003 The Visigoth Software Society. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Visigoth Software Society (http://www.visigoths.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. Neither the name "FreeMarker", "Visigoth", nor any of the names of the 
 *    project contributors may be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact visigoths@visigoths.org.
 *
 * 5. Products derived from this software may not be called "FreeMarker" or "Visigoth"
 *    nor may "FreeMarker" or "Visigoth" appear in their names
 *    without prior written permission of the Visigoth Software Society.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE VISIGOTH SOFTWARE SOCIETY OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Visigoth Software Society. For more
 * information on the Visigoth Software Society, please see
 * http://www.visigoths.org/
 */

package freemarker.ext.beans;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import freemarker.template.TemplateModelException;

class MethodMap
{
    private static final Class BIGDECIMAL_CLASS = java.math.BigDecimal.class;
    private static final Class NUMBER_CLASS = java.lang.Number.class;
    
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final Class NULL_CLASS = java.lang.Object.class;
    private static final ClassString EMPTY_STRING = new ClassString(EMPTY_ARGS);    
    
    private static final Object NO_SUCH_METHOD = new Object();
    private static final Object AMBIGUOUS_METHOD = new Object();
    
    private final String name;
    private final Map cache = new HashMap();
    private final List methods = new LinkedList();
    
    MethodMap(String name)
    {
        this.name = name;
    }
    
    void addMethod(Method method)
    {
        methods.add(method);
    }
    
    void addConstructor(Constructor constructor)
    {
        methods.add(constructor);
    }
    String getName()
    {
        return name;
    }
    
    AccessibleObject getMostSpecific(Object[] args)
        throws TemplateModelException
    {
        ClassString cs = null;
        if(args == null)
        {
            args = EMPTY_ARGS;
            cs = EMPTY_STRING;
        }
        else
        {
            cs = new ClassString(args);
        }
        synchronized(cache)
        {
            Object obj = cache.get(cs);
            if(obj == null)
            {
                cache.put(cs, obj = cs.getMostSpecific(methods));
            }
            if(obj instanceof AccessibleObject)
            {
                return (AccessibleObject)obj;
            }
            if(obj == NO_SUCH_METHOD)
            {
                throw new TemplateModelException("No signature of method " + name + " matches " + cs.listArgumentTypes());
            }
            else
            {
                // Can be only AMBIGUOUS_METHOD
                throw new TemplateModelException("Multiple signatures of method " + name + " match " + cs.listArgumentTypes());
            }
        }
    }
    
    private static final class ClassString
    {
        private final Class[] classes;
        
        ClassString(Object[] objects)
        {
            int l = objects.length;
            classes = new Class[l];
            for(int i = 0; i < l; ++i)
            {
                Object obj = objects[i];
                classes[i] = obj == null ? NULL_CLASS : obj.getClass();
            }
        }
        
        public int hashCode()
        {
            int hash = 0;
            for(int i = 0; i < classes.length; ++i)
            {
                hash ^= classes[i].hashCode();
            }
            return hash;
        }
        
        public boolean equals(Object o)
        {
            if(o instanceof ClassString)
            {
                ClassString cs = (ClassString)o;
                if(cs.classes.length != classes.length)
                {
                    return false;
                }
                for(int i = 0; i < classes.length; ++i)
                {
                    if(cs.classes[i] != classes[i])
                    {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        
        private static final int MORE_SPECIFIC = 0;
        private static final int LESS_SPECIFIC = 1;
        private static final int INDETERMINATE = 2;
        
        Object getMostSpecific(List methods)
        {
            LinkedList applicables = getApplicables(methods);
            if(applicables.isEmpty())
            {
                return NO_SUCH_METHOD;
            }
            if(applicables.size() == 1)
            {
                return applicables.getFirst();
            }
            LinkedList maximals = new LinkedList();
            for (Iterator applicable = applicables.iterator(); 
                 applicable.hasNext();)
            {
                Object objapp = applicable.next();
                Class[] appArgs = getParameterTypes(objapp);
                boolean lessSpecific = false;
                for (Iterator maximal = maximals.iterator(); 
                     !lessSpecific && maximal.hasNext();)
                {
                    Object max = maximal.next();
                    switch(moreSpecific(appArgs, getParameterTypes(max)))
                    {
                        case MORE_SPECIFIC:
                        {
                            maximal.remove();
                            break;
                        }
                        case LESS_SPECIFIC:
                        {
                            lessSpecific = true;
                            break;
                        }
                    }
                }
                if(!lessSpecific)
                {
                    maximals.addLast(objapp);
                }
            }
            if(maximals.size() > 1)
            {
                return AMBIGUOUS_METHOD;
            }
            return maximals.getFirst();
        }
        
        private static Class[] getParameterTypes(Object obj)
        {
            if(obj instanceof Method)
            {
                return ((Method)obj).getParameterTypes();
            }
            if(obj instanceof Constructor)
            {
                return ((Constructor)obj).getParameterTypes();
            }
            // Cannot happen
            throw new Error();
        }
        
        private static int moreSpecific(Class[] c1, Class[] c2)
        {
            boolean c1MoreSpecific = false;
            boolean c2MoreSpecific = false;
            for(int i = 0; i < c1.length; ++i)
            {
                if(c1[i] != c2[i])
                {
                    c1MoreSpecific = 
                        c1MoreSpecific ||
                        isMoreSpecific(c1[i], c2[i]);
                    c2MoreSpecific = 
                        c2MoreSpecific ||
                        isMoreSpecific(c2[i], c1[i]);
                }
            }
            if(c1MoreSpecific)
            {
                if(c2MoreSpecific)
                {
                    return INDETERMINATE;
                }
                return MORE_SPECIFIC;
            }
            if(c2MoreSpecific)
            {
                return LESS_SPECIFIC;
            }
            return INDETERMINATE;
        }
        
        
        /**
         * Returns all methods that are applicable to actual
         * parameter classes represented by this ClassString object.
         */
        LinkedList getApplicables(List methods)
        {
            LinkedList list = new LinkedList();
            for (Iterator imethod = methods.iterator(); imethod.hasNext();)
            {
                Object method = imethod.next();
                if(isApplicable(method))
                {
                    list.add(method);
                }
                
            }
            return list;
        }
        
        /**
         * Returns true if the supplied method is applicable to actual
         * parameter classes represented by this ClassString object.
         * 
         */
        private boolean isApplicable(Object method)
        {
            Class[] methodArgs = getParameterTypes(method);
            if(methodArgs.length != classes.length)
            {
                return false;
            }
            for(int i = 0; i < classes.length; ++i)
            {
                if(!isMethodInvocationConvertible(methodArgs[i], classes[i]))
                {
                    return false;
                }
            }
            return true;
        }
        
        /**
         * Determines whether a type represented by a class object is
         * convertible to another type represented by a class object using a 
         * method invocation conversion, treating object types of primitive 
         * types as if they were primitive types (that is, a Boolean actual 
         * parameter type matches boolean primitive formal type). This behavior
         * is because this method is used to determine applicable methods for 
         * an actual parameter list, and primitive types are represented by 
         * their object duals in reflective method calls.
         * @param formal the formal parameter type to which the actual 
         * parameter type should be convertible
         * @param actual the actual parameter type.
         * @return true if either formal type is assignable from actual type, 
         * or formal is a primitive type and actual is its corresponding object
         * type or an object type of a primitive type that can be converted to
         * the formal type.
         */
        private static boolean isMethodInvocationConvertible(Class formal, Class actual)
        {
            // Check for identity or widening reference conversion
            if(formal.isAssignableFrom(actual))
            {
                return true;
            }
            // Check for boxing with widening primitive conversion. Note that 
            // actual parameters are never primitives.
            if(formal.isPrimitive())
            {
                if(formal == Boolean.TYPE && actual == Boolean.class)
                    return true;
                if(formal == Character.TYPE && actual == Character.class)
                    return true;
                if(formal == Byte.TYPE && actual == Byte.class)
                    return true;
                if(formal == Short.TYPE &&
                   (actual == Short.class || actual == Byte.class))
                    return true;
                if(formal == Integer.TYPE && 
                   (actual == Integer.class || actual == Short.class || 
                    actual == Byte.class))
                    return true;
                if(formal == Long.TYPE && 
                   (actual == Long.class || actual == Integer.class || 
                    actual == Short.class || actual == Byte.class))
                    return true;
                if(formal == Float.TYPE && 
                   (actual == Float.class || actual == Long.class || 
                    actual == Integer.class || actual == Short.class || 
                    actual == Byte.class))
                    return true;
                if(formal == Double.TYPE && 
                   (actual == Double.class || actual == Float.class || 
                    actual == Long.class || actual == Integer.class || 
                    actual == Short.class || actual == Byte.class))
                    return true; 
            }
            // Special case for BigDecimals as we deem BigDecimal to be
            // convertible to any numeric type - either object or primitive.
            // This can actually cause us trouble as this is a narrowing 
            // conversion, not widening. 
            return isBigDecimalConvertible(formal, actual);
        }
        
        /**
         * Determines whether a type represented by a class object is 
         * convertible to another type represented by a class object using a 
         * method invocation conversion, without matching object and primitive
         * types. This method is used to determine the more specific type when
         * comparing signatures of methods.
         * @param formal the formal parameter type to which the actual 
         * parameter type should be convertible
         * @param actual the actual parameter type.
         * @return true if either formal type is assignable from actual type, 
         * or formal and actual are both primitive types and actual can be
         * subject to widening conversion to formal.
         */
        private static boolean isMoreSpecific(Class specific, Class generic)
        {
            // Check for identity or widening reference conversion
            if(generic.isAssignableFrom(specific))
            {
                return true;
            }
            // Check for widening primitive conversion.
            if(generic.isPrimitive())
            {
                if(generic == Short.TYPE && (specific == Byte.TYPE))
                    return true;
                if(generic == Integer.TYPE && 
                   (specific == Short.TYPE || specific == Byte.TYPE))
                    return true;
                if(generic == Long.TYPE && 
                   (specific == Integer.TYPE || specific == Short.TYPE || 
                    specific == Byte.TYPE))
                    return true;
                if(generic == Float.TYPE && 
                   (specific == Long.TYPE || specific == Integer.TYPE || 
                    specific == Short.TYPE || specific == Byte.TYPE))
                    return true;
                if(generic == Double.TYPE && 
                   (specific == Float.TYPE || specific == Long.TYPE || 
                    specific == Integer.TYPE || specific == Short.TYPE || 
                    specific == Byte.TYPE))
                    return true; 
            }
            return isBigDecimalConvertible(generic, specific);
        }
        
        private static boolean isBigDecimalConvertible(Class formal, Class actual)
        {
            // BigDecimal 
            if(BIGDECIMAL_CLASS.isAssignableFrom(actual))
            {
                if(NUMBER_CLASS.isAssignableFrom(formal))
                {
                    return true;
                }
                if(formal.isPrimitive() && 
                   formal != Boolean.TYPE && formal != Character.TYPE)
                {
                   return true;
                }
            }
            return false;
        }
        
        private String listArgumentTypes()
        {
            StringBuffer buf = 
                new StringBuffer(classes.length * 32).append('(');
            for(int i = 0; i < classes.length; ++i)
            {
                buf.append(classes[i].getName()).append(',');
            }
            buf.setLength(buf.length() - 1);
            return buf.append(')').toString();
        }
    }
}
