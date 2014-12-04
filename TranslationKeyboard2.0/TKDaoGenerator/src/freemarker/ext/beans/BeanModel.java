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

import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleCollection;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateScalarModel;

/**
 * A class that will wrap an arbitrary object into
 * {@link freemarker.template.TemplateHashModel} interface allowing calls to
 * arbitrary property getters and invocation of
 * accessible methods on the object from a template using the
 * <tt>object.foo</tt> to access properties and <tt>object.bar(arg1, arg2)</tt> to
 * invoke methods on it. You can also use the <tt>object.foo[index]</tt> syntax to
 * access indexed properties. It uses Beans {@link java.beans.Introspector}
 * to dynamically discover the properties and methods. 
 * @author Attila Szegedi
 * @version $Id: BeanModel.java,v 1.39.2.2 2004/03/11 12:52:40 ddekany Exp $
 */

public class BeanModel
    implements
    TemplateHashModelEx, WrapperTemplateModel
{
    private static final Logger logger = Logger.getLogger("freemarker.beans");
    protected final Object object;
    protected final BeansWrapper wrapper;
    
    static final ModelFactory FACTORY =
        new ModelFactory()
        {
            public TemplateModel create(Object object, ObjectWrapper wrapper)
            {
                return new BeanModel(object, (BeansWrapper)wrapper);
            }
        };

    // Cached template models that implement member properties and methods for this
    // instance. Keys are FeatureDescriptor instances (from classCache values),
    // values are either ReflectionMethodModels/ReflectionScalarModels
    private final HashMap memberMap = new HashMap();

    /**
     * Creates a new model that wraps the specified object. Note that there are
     * specialized subclasses of this class for wrapping arrays, collections,
     * enumeration, iterators, and maps. Note also that the superclass can be
     * used to wrap String objects if only scalar functionality is needed. You
     * can also choose to delegate the choice over which model class is used for
     * wrapping to {@link BeansWrapper#wrap(Object)}.
     * @param object the object to wrap into a model.
     */
    public BeanModel(Object object, BeansWrapper wrapper)
    {
        this.object = object;
        this.wrapper = wrapper;
        if (object == null) {
            return;
        }
        wrapper.introspectClass(object.getClass());
    }

    /**
     * Uses Beans introspection to locate a property or method with name
     * matching the key name. If a method or property is found, it is wrapped
     * into {@link freemarker.template.TemplateMethodModelEx} (for a method or
     * indexed property), or evaluated on-the-fly and the return value wrapped
     * into appropriate model (for a simple property) Models for various
     * properties and methods are cached on a per-class basis, so the costly
     * introspection is performed only once per property or method of a class.
     * (Side-note: this also implies that any class whose method has been called
     * will be strongly referred to by the framework and will not become
     * unloadable until this class has been unloaded first. Normally this is not
     * an issue, but can be in a rare scenario where you create many classes on-
     * the-fly. Also, as the cache grows with new classes and methods introduced
     * to the framework, it may appear as if it were leaking memory. The
     * framework does, however detect class reloads (if you happen to be in an
     * environment that does this kind of things--servlet containers do it when
     * they reload a web application) and flushes the cache. If no method or
     * property matching the key is found, the framework will try to invoke
     * methods with signature
     * <tt>non-void-return-type get(java.lang.String)</tt>,
     * then <tt>non-void-return-type get(java.lang.Object)</tt>, or 
     * alternatively (if the wrapped object is a resource bundle) 
     * <tt>Object getObject(java.lang.String)</tt>.
     * @throws TemplateModelException if there was no property nor method nor
     * a generic <tt>get</tt> method to invoke.
     */
    public TemplateModel get(String key)
        throws
        TemplateModelException
    {
        Class clazz = object.getClass();
        Map keyMap = wrapper.getClassKeyMap(clazz);
        
        try
        {
            if(wrapper.isMethodsShadowItems())
            {
                Object fd = keyMap.get(key);
                if(fd != null)
                {
                    return invokeThroughDescriptor(fd); 
                }
                TemplateModel retval = invokeGenericGet(keyMap, clazz, key);
                if(retval == null && logger.isDebugEnabled())
                {
                    logNoSuchKey(key, keyMap);
                }
                return retval;
            }
            else
            {
                TemplateModel model = invokeGenericGet(keyMap, clazz, key);
                if(model != null)
                {
                    return model;
                }
                Object fd = keyMap.get(key);
                if(fd == null)
                {
                    if(logger.isDebugEnabled())
                    {
                        logNoSuchKey(key, keyMap);
                    }
                    return null;
                }
                return invokeThroughDescriptor(fd);
            }
        }
        catch(TemplateModelException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new TemplateModelException("get(" + key + ") failed on " +
                "instance of " + object.getClass().getName(), e);
        }
    }

    private void logNoSuchKey(String key, Map keyMap)
    {
        logger.debug("Key '" + key + "' was not found on instance of " + 
            object.getClass().getName() + ". Introspection information for " +
            "the class is: " + keyMap);
    }
    
    private TemplateModel invokeThroughDescriptor(Object desc)
        throws
        IllegalAccessException,
        InvocationTargetException,
        TemplateModelException
    {
        // See if this particular instance has a cached implementation
        // for the requested feature descriptor
        TemplateModel member = null;
        synchronized(memberMap)
        {
            member = (TemplateModel)memberMap.get(desc);
        }

        if(member != null)
            return member;

        TemplateModel retval = null;
        if(desc instanceof IndexedPropertyDescriptor)
        {
            retval = member = 
                new SimpleMethodModel(
                    object, 
                    ((IndexedPropertyDescriptor)desc).getIndexedReadMethod(), 
                    wrapper);
        }
        else if(desc instanceof PropertyDescriptor)
        {
            PropertyDescriptor pd = (PropertyDescriptor)desc;
            retval = wrap(pd.getReadMethod().invoke(object, null));
            // (member == null) condition remains, as we don't cache these
        }
        else if(desc instanceof Method)
        {
            retval = member = 
                new SimpleMethodModel(object, (Method)desc, wrapper);
        }
        else if(desc instanceof MethodMap)
        {
            retval = member = 
                new OverloadedMethodModel(object, (MethodMap)desc, wrapper);
        }
        
        // If new cacheable member was created, cache it
        if(member != null)
        {
            synchronized(memberMap)
            {
                memberMap.put(desc, member);
            }
        }
        return retval;
    }

    protected TemplateModel invokeGenericGet(Map keyMap, Class clazz, String key)
    throws
        IllegalAccessException,
        InvocationTargetException,
        TemplateModelException
    {
        Method genericGet = (Method)keyMap.get(BeansWrapper.GENERIC_GET_KEY);
        if(genericGet == null)
            return null;

        return wrap(genericGet.invoke(object, new Object[] { key }));
    }

    protected TemplateModel wrap(Object obj)
    throws TemplateModelException
    {
        return wrapper.getOuterIdentity().wrap(obj);
    }
    
    protected Object unwrap(TemplateModel model)
    throws
        TemplateModelException
    {
        return wrapper.unwrap(model);
    }

    /**
     * Tells whether the model is empty. It is empty if either the wrapped 
     * object is null, or it is a Boolean with false value.
     */
    public boolean isEmpty()
    {
        if (object instanceof String) {
            return ((String) object).length() == 0;
        }
        if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        }
	if (object instanceof Map) {
	    return ((Map) object).isEmpty();
	}
        return object == null || Boolean.FALSE.equals(object);
    }
    
    /**
     * Returns the underlying object.
     */
    public Object getWrappedObject()
    {
        return object;
    }

    public int size()
    {
        return wrapper.keyCount(object.getClass());
    }

    public TemplateCollectionModel keys()
    {
        return new SimpleCollection(keySet(), wrapper);
    }

    public TemplateCollectionModel values() throws TemplateModelException
    {
        List values = new ArrayList(size());
        TemplateModelIterator it = keys().iterator();
        while (it.hasNext()) {
            String key = ((TemplateScalarModel)it.next()).getAsString();
            values.add(get(key));
        }
        return new SimpleCollection(values, wrapper);
    }

    /**
     * Helper method to support TemplateHashModelEx. Returns the Set of
     * Strings which are available via the TemplateHashModel
     * interface. Subclasses that override <tt>invokeGenericGet</tt> to
     * provide additional hash keys should also override this method.
     */
    protected Set keySet()
    {
        return wrapper.keySet(object.getClass());
    }    
}
