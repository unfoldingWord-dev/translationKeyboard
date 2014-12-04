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

import java.beans.BeanInfo;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import freemarker.ext.util.ModelCache;
import freemarker.ext.util.ModelFactory;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.log.Logger;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.Collections12;
import freemarker.template.utility.SecurityUtilities;

/**
 * Utility class that provides generic services to reflection classes.
 * It handles all polymorphism issues in the {@link #wrap(Object)} and {@link #unwrap(TemplateModel)} methods.
 * @author Attila Szegedi
 * @version $Id: BeansWrapper.java,v 1.75.2.3 2004/04/23 11:07:56 szegedia Exp $
 */
public class BeansWrapper implements ObjectWrapper
{
    // When this property is true, some things are stricter. This is mostly to
    // catch anomalous things in development that can otherwise be valid situations
    // for our users.
    private static final boolean DEVELOPMENT = "true".equals(SecurityUtilities.getSystemProperty("freemarker.development"));
    
    private static final Class BIGINTEGER_CLASS = java.math.BigInteger.class;
    
    private static final Logger logger = Logger.getLogger("freemarker.beans");
    
    private static final Set UNSAFE_METHODS = createUnsafeMethodsSet();
    
    static final Object GENERIC_GET_KEY = new Object();
    private static final Object CONSTRUCTORS = new Object();
    
    /**
     * The default instance of BeansWrapper
     */
    private static final BeansWrapper INSTANCE = new BeansWrapper();

    // Cache of hash maps that contain already discovered properties and methods
    // for a specified class. Each key is a Class, each value is a hash map. In
    // that hash map, each key is a property/method name, each value is a
    // MethodDescriptor or a PropertyDescriptor assigned to that property/method.
    private final Map classCache = new HashMap();
    private Set cachedClassNames = new HashSet();

    private final StaticModels staticModels = new StaticModels(this);

    private final ModelCache modelCache = new ModelCache(this);
    
    private final BooleanModel FALSE = new BooleanModel(Boolean.FALSE, this);
    private final BooleanModel TRUE = new BooleanModel(Boolean.TRUE, this);

    /**
     * At this level of exposure, all methods and properties of the
     * wrapped objects are exposed to the template.
     */
    public static final int EXPOSE_ALL = 0;
    
    /**
     * At this level of exposure, all methods and properties of the wrapped
     * objects are exposed to the template except methods that are deemed
     * not safe. The not safe methods are java.lang.Object methods wait() and
     * notify(), java.lang.Class methods getClassLoader() and newInstance(),
     * java.lang.reflect.Method and java.lang.reflect.Constructor invoke() and
     * newInstance() methods, all java.lang.reflect.Field set methods, all 
     * java.lang.Thread and java.lang.ThreadGroup methods that can change its 
     * state, as well as the usual suspects in java.lang.System and
     * java.lang.Runtime.
     */
    public static final int EXPOSE_SAFE = 1;
    
    /**
     * At this level of exposure, only property getters are exposed.
     * Additionally, property getters that map to unsafe methods are not
     * exposed (i.e. Class.classLoader and Thread.contextClassLoader).
     */
    public static final int EXPOSE_PROPERTIES_ONLY = 2;

    /**
     * At this level of exposure, no bean properties and methods are exposed.
     * Only map items, resource bundle items, and objects retrieved through
     * the generic get method (on objects of classes that have a generic get
     * method) can be retrieved through the hash interface. You might want to 
     * call {@link #setMethodsShadowItems(boolean)} with <tt>false</tt> value to
     * speed up map item retrieval.
     */
    public static final int EXPOSE_NOTHING = 3;

    private int exposureLevel = EXPOSE_SAFE;
    private TemplateModel nullModel = null;
    private boolean methodsShadowItems = true;
    private int defaultDateType = TemplateDateModel.UNKNOWN;

    private ObjectWrapper outerIdentity = this;
    private boolean simpleMapWrapper;
    
    /**
     * Creates a new instance of BeansWrapper. The newly created instance
     * will use the null reference as its null object, it will use
     * {@link #EXPOSE_SAFE} method exposure level, and will not cache
     * model instances.
     */
    public BeansWrapper()
    {
    }

    /**
     * When wrapping an object, the BeansWrapper commonly needs to wrap
     * "sub-objects", for example each element in a wrapped collection.
     * Normally it wraps these objects using itself. However, this makes
     * it difficult to delegate to a BeansWrapper as part of a custom
     * aggregate ObjectWrapper. This method lets you set the ObjectWrapper
     * which will be used to wrap the sub-objects.
     * @param outerIdentity the aggregate ObjectWrapper
     */
    public void setOuterIdentity(ObjectWrapper outerIdentity)
    {
        this.outerIdentity = outerIdentity;
    }

    /**
     * By default returns <tt>this</tt>.
     * @see #setOuterIdentity(ObjectWrapper)
     */
    public ObjectWrapper getOuterIdentity()
    {
        return outerIdentity;
    }

    /**
     * By default the BeansWrapper wraps classes implementing
     * java.util.Map using {@link MapModel}. Setting this flag will
     * cause it to use a {@link SimpleMapModel} instead. The biggest
     * difference is that when using a {@link SimpleMapModel}, the
     * map will be visible as <code>TemplateHashModelEx</code>,
     * and the subvariables will be the content of the map,
     * without the other methods and properties of the map object.
     * @param simpleMapWrapper enable simple map wrapping
     */
    public void setSimpleMapWrapper(boolean simpleMapWrapper)
    {
        this.simpleMapWrapper = simpleMapWrapper;
    }

    public boolean isSimpleMapWrapper()
    {
        return simpleMapWrapper;
    }

    /**
     * Sets the method exposure level. By default, set to <code>EXPOSE_SAFE</code>.
     * @param exposureLevel can be any of the <code>EXPOSE_xxx</code>
     * constants.
     */
    public void setExposureLevel(int exposureLevel)
    {
        if(exposureLevel < EXPOSE_ALL || exposureLevel > EXPOSE_NOTHING)
        {
            throw new IllegalArgumentException("Illegal exposure level " + exposureLevel);
        }
        this.exposureLevel = exposureLevel;
    }
    
    int getExposureLevel()
    {
        return exposureLevel;
    }
    
    /**
     * Sets whether methods shadow items in beans. When true (this is the
     * default value), <code>${object.name}</code> will first try to locate
     * a bean method or property with the specified name on the object, and
     * only if it doesn't find it will it try to call
     * <code>object.get(name)</code>, the so-called "generic get method" that
     * is usually used to access items of a container (i.e. elements of a map).
     * When set to false, the lookup order is reversed and generic get method
     * is called first, and only if it returns null is method lookup attempted.
     */
    public synchronized void setMethodsShadowItems(boolean methodsShadowItems)
    {
        this.methodsShadowItems = methodsShadowItems;
    }
    
    boolean isMethodsShadowItems()
    {
        return methodsShadowItems;
    }
    
    /**
     * Sets the default date type to use for date models that result from
     * a plain <tt>java.util.Date</tt> instead of <tt>java.sql.Date</tt> or
     * <tt>java.sql.Time</tt> or <tt>java.sql.Timestamp</tt>. Default value is 
     * {@link TemplateDateModel#UNKNOWN}.
     * @param defaultDateType the new default date type.
     */
    public synchronized void setDefaultDateType(int defaultDateType) {
        this.defaultDateType = defaultDateType;
    }
    
    int getDefaultDateType() {
        return defaultDateType;
    }
    
    /**
     * Sets whether this wrapper caches model instances. Default is false.
     * When set to true, calling {@link #wrap(Object)} multiple times for
     * the same object will likely return the same model (although there is
     * no guarantee as the cache items can be cleared anytime).
     */
    public void setUseCache(boolean useCache)
    {
        modelCache.setUseCache(useCache);
    }
    
    /**
     * Sets the null model. This model is returned from the
     * {@link #wrap(Object)} method whenever the underlying object 
     * reference is null. It defaults to null reference, which is dealt 
     * with quite strictly on engine level, however you can substitute an 
     * arbitrary (perhaps more lenient) model, such as 
     * {@link freemarker.template.TemplateScalarModel#EMPTY_STRING}.
     */
    public void setNullModel(TemplateModel nullModel)
    {
        this.nullModel = nullModel;
    }
    
    /**
     * Returns the default instance of the wrapper. This instance is used
     * when you construct various bean models without explicitly specifying
     * a wrapper. It is also returned by 
     * {@link freemarker.template.ObjectWrapper#BEANS_WRAPPER}
     * and this is the sole instance that is used by the JSP adapter.
     * You can modify the properties of the default instance (caching,
     * exposure level, null model) to affect its operation. By default, the
     * default instance is not caching, uses the <code>EXPOSE_SAFE</code>
     * exposure level, and uses null reference as the null model.
     */
    public static final BeansWrapper getDefaultInstance()
    {
        return INSTANCE;
    }

    /**
     * Wraps the object with a template model that is most specific for the object's
     * class. Specifically:
     * <ul>
     * <li>if the object is null, returns the {@link #setNullModel(TemplateModel) null model},</li>
     * <li>if the object is a Number returns a {@link NumberModel} for it,</li>
     * <li>if the object is a Date returns a {@link DateModel} for it,</li>
     * <li>if the object is a Boolean returns 
     * {@link freemarker.template.TemplateBooleanModel#TRUE} or 
     * {@link freemarker.template.TemplateBooleanModel#FALSE}</li>
     * <li>if the object is already a TemplateModel, returns it unchanged,</li>
     * <li>if the object is an array, returns a {@link ArrayModel} for it
     * <li>if the object is a Map, returns a {@link MapModel} for it
     * <li>if the object is a Collection, returns a {@link CollectionModel} for it
     * <li>if the object is an Iterator, returns a {@link IteratorModel} for it
     * <li>if the object is an Enumeration, returns a {@link EnumerationModel} for it
     * <li>if the object is a String, returns a {@link StringModel} for it
     * <li>otherwise, returns a generic {@link BeanModel} for it.
     * </ul>
     */
    public TemplateModel wrap(Object object) throws TemplateModelException
    {
        if(object == null)
            return nullModel;
        if(object instanceof TemplateModel)
            return (TemplateModel)object;
        if(object instanceof Map)
            return modelCache.getInstance(object, simpleMapWrapper ? SimpleMapModel.FACTORY : MapModel.FACTORY);
        if(object instanceof Collection)
            return modelCache.getInstance(object, CollectionModel.FACTORY);
        if(object.getClass().isArray())
            return modelCache.getInstance(object, ArrayModel.FACTORY);
        if(object instanceof Number)
            return modelCache.getInstance(object, NumberModel.FACTORY);
        if(object instanceof Date)
            return modelCache.getInstance(object, DateModel.FACTORY);
        if(object instanceof Boolean)
        {
            return ((Boolean)object).booleanValue() ? TRUE : FALSE;
        }
        if(object instanceof ResourceBundle)
            return modelCache.getInstance(object, ResourceBundleModel.FACTORY);
        if(object instanceof Iterator)
            return new IteratorModel((Iterator)object, this);
        if(object instanceof Enumeration)
            return new EnumerationModel((Enumeration)object, this);
        return modelCache.getInstance(object, StringModel.FACTORY);
    }

    protected TemplateModel create(Object object, Object factory)
    {
        return ((ModelFactory)factory).create(object, this);
    }

    /**
     * Attempts to unwrap a model into underlying object. Generally, this
     * method is the inverse of the {@link #wrap(Object)} method. In addition
     * it will unwrap arbitrary {@link TemplateNumberModel} instances into
     * a number, arbitrary {@link TemplateDateModel} instances into a date,
     * {@link TemplateScalarModel} instances into a String, and
     * {@link TemplateBooleanModel} instances into a Boolean.
     * All other objects are returned unchanged.
     */
    public Object unwrap(TemplateModel model)
        throws
        TemplateModelException
    {
        if(model == nullModel)
            return null;
        if(model instanceof WrapperTemplateModel)
            return ((WrapperTemplateModel)model).getWrappedObject();
        if(model instanceof TemplateNumberModel)
            return ((TemplateNumberModel)model).getAsNumber();
        if(model instanceof TemplateDateModel)
            return ((TemplateDateModel)model).getAsDate();
        if(model instanceof TemplateScalarModel)
            return ((TemplateScalarModel)model).getAsString();
        if(model instanceof TemplateBooleanModel)
            return ((TemplateBooleanModel)model).getAsBoolean() ? Boolean.TRUE : Boolean.FALSE;
        return model;
    }

    /**
     * Auxiliary method that unwraps arguments for a method or constructor call.
     * @param arguments the argument list of template models
     * @return Object[] the unwrapped arguments. null if the passed list was
     * null.
     * @throws TemplateModelException if unwrapping any argument throws one
     */
    Object[] unwrapArguments(List arguments) throws TemplateModelException
    {
        Object[] args = null;
        if(arguments != null)
        {
            int size = arguments.size();
            args = new Object[size];
            Iterator it = arguments.iterator();
            int i = 0;
            while(it.hasNext())
                args[i++] = unwrap((TemplateModel)it.next());
        }
        return args;
    }
    
   /**
     * Returns a hash model that represents the so-called class static models.
     * Every class static model is itself a hash through which you can call
     * static methods on the specified class. To obtain a static model for a
     * class, get the element of this hash with the fully qualified class name.
     * For example, if you place this hash model inside the root data model
     * under name "statics", you can use i.e. <code>statics["java.lang.
     * System"]. currentTimeMillis()</code> to call the {@link 
     * java.lang.System#currentTimeMillis()} method.
     * @return a hash model whose keys are fully qualified class names, and
     * that returns hash models whose elements are the static models of the
     * classes.
     */
    public TemplateHashModel getStaticModels()
    {
        return staticModels;
    }
    
    public Object newInstance(Class clazz, List arguments)
    throws
        TemplateModelException
    {
        try
        {
            introspectClass(clazz);
            Object ctors = ((Map)classCache.get(clazz)).get(CONSTRUCTORS);
            if(ctors == null)
            {
                throw new TemplateModelException("Class " + clazz.getName() + 
                        " has no public constructors.");
            }
            Constructor ctor = null;
            Object[] objargs = unwrapArguments(arguments);
            if(ctors instanceof Constructor)
            {
                ctor = (Constructor)ctors;
            }
            else if(ctors instanceof MethodMap)
            {
                ctor = (Constructor)((MethodMap)ctors).getMostSpecific(objargs);
            }
            else
            {
                // Cannot happen
                throw new Error();
            }
            if(objargs != null) {
                coerceBigDecimals(ctor, objargs);
            }
            return ctor.newInstance(objargs);
        }
        catch (TemplateModelException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new TemplateModelException(
                    "Could not create instance of class " + clazz.getName(), e);
        }
    }
    
    void introspectClass(Class clazz)
    {
        synchronized(classCache)
        {
            if(!classCache.containsKey(clazz))
            {
                String className = clazz.getName();
                if(cachedClassNames.contains(className))
                {
                    if(logger.isInfoEnabled())
                    {
                        logger.info("Detected a reloaded class [" + className + 
                                "]. Clearing BeansWrapper caches.");
                    }
                    // Class reload detected, throw away caches
                    classCache.clear();
                    cachedClassNames = new HashSet();
                    synchronized(this)
                    {
                        modelCache.clearCache();
                    }
                    staticModels.clearCache();
                }
                classCache.put(clazz, populateClassMap(clazz));
                cachedClassNames.add(className);
            }
        }
    }

    Map getClassKeyMap(Class clazz)
    {
        synchronized(classCache)
        {
            return (Map)classCache.get(clazz);
        }
    }

    /**
     * Returns the number of introspected methods/properties that should
     * be available via the TemplateHashModel interface. Affected by the
     * {@link #setMethodsShadowItems(boolean)} and {@link
     * #setExposureLevel(int)} settings.
     */
    int keyCount(Class clazz)
    {
        Map map = getClassKeyMap(clazz);
        int count = map.size();
        if (map.containsKey(CONSTRUCTORS))
            count--;
        if (map.containsKey(GENERIC_GET_KEY))
            count--;
        return count;
    }

    /**
     * Returns the Set of names of introspected methods/properties that
     * should be available via the TemplateHashModel interface. Affected
     * by the {@link #setMethodsShadowItems(boolean)} and {@link
     * #setExposureLevel(int)} settings.
     */
    Set keySet(Class clazz)
    {
        Set set = new HashSet(getClassKeyMap(clazz).keySet());
        set.remove(CONSTRUCTORS);
        set.remove(GENERIC_GET_KEY);
        return set;
    }
    
    /**
     * Populates a map with property and method descriptors for a specified
     * class. If any property or method descriptors specifies a read method
     * that is not accessible, replaces it with appropriate accessible method
     * from a superclass or interface.
     */
    private Map populateClassMap(Class clazz)
    {
        // Populate first from bean info
        Map map = populateClassMapWithBeanInfo(clazz);
        // Next add constructors
        try
        {
            Constructor[] ctors = clazz.getConstructors();
            if(ctors.length == 1)
            {
                map.put(CONSTRUCTORS, ctors[0]);
            }
            else if(ctors.length > 1)
            {
                MethodMap ctorMap = new MethodMap("<init>");
                for (int i = 0; i < ctors.length; i++)
                {
                    ctorMap.addConstructor(ctors[i]);
                }
                map.put(CONSTRUCTORS, ctorMap);
            }
        }
        catch(SecurityException e)
        {
            logger.warn("Canont discover constructors for class " + 
                    clazz.getName(), e);
        }
        switch(map.size())
        {
            case 0:
            {
                map = Collections12.EMPTY_MAP;
                break; 
            }
            case 1:
            {
                Map.Entry e = (Map.Entry)map.entrySet().iterator().next();
                map = Collections12.singletonMap(e.getKey(), e.getValue());
                break;
            }
        }
        return map;
    }

    private Map populateClassMapWithBeanInfo(Class clazz)
    {
        Map classMap = new HashMap();
        Map accessibleMethods = discoverAccessibleMethods(clazz);
        Method genericGet = (Method)accessibleMethods.get(MethodSignature.GET_STRING_SIGNATURE);
        if(genericGet == null)
        {
            genericGet = (Method)accessibleMethods.get(MethodSignature.GET_OBJECT_SIGNATURE);
        }
        if(genericGet != null)
        {
            classMap.put(GENERIC_GET_KEY, genericGet);
        }
        if(exposureLevel == EXPOSE_NOTHING)
        {
            return classMap;
        }
        
        try
        {
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
            PropertyDescriptor[] pda = beanInfo.getPropertyDescriptors();
            MethodDescriptor[] mda = beanInfo.getMethodDescriptors();

            for(int i = pda.length - 1; i >= 0; --i)
            {
                PropertyDescriptor pd = pda[i];
                if(pd instanceof IndexedPropertyDescriptor)
                {
                    IndexedPropertyDescriptor ipd = (IndexedPropertyDescriptor)pd;
                    Method readMethod = ipd.getIndexedReadMethod();
                    Method publicReadMethod = getAccessibleMethod(readMethod, accessibleMethods);
                    if(publicReadMethod != null && isSafeMethod(publicReadMethod))
                    {
                        try
                        {
                            if(readMethod != publicReadMethod)
                                ipd.setIndexedReadMethod(publicReadMethod);
                            classMap.put(ipd.getName(), ipd);
                        }
                        catch(IntrospectionException e)
                        {
                            logger.warn("Couldn't properly perform introspection", e);
                        }
                    }
                }
                else
                {
                    Method readMethod = pd.getReadMethod();
                    Method publicReadMethod = getAccessibleMethod(readMethod, accessibleMethods);
                    if(publicReadMethod != null && isSafeMethod(publicReadMethod))
                    {
                        try
                        {
                            if(readMethod != publicReadMethod)
                                pd.setReadMethod(publicReadMethod);
                            classMap.put(pd.getName(), pd);
                        }
                        catch(IntrospectionException e)
                        {
                            logger.warn("Couldn't properly perform introspection", e);
                        }
                    }
                }
            }
            if(exposureLevel < EXPOSE_PROPERTIES_ONLY)
            {
                for(int i = mda.length - 1; i >= 0; --i)
                {
                    MethodDescriptor md = mda[i];
                    Method method = md.getMethod();
                    Method publicMethod = getAccessibleMethod(method, accessibleMethods);
                    if(publicMethod != null && isSafeMethod(publicMethod))
                    {
                        String name = md.getName();
                        Object previous = classMap.get(name);
                        if(previous instanceof Method)
                        {
                            // Overloaded method - replace method with a method map
                            MethodMap methodMap = new MethodMap(name);
                            methodMap.addMethod((Method)previous);
                            methodMap.addMethod(publicMethod);
                            classMap.put(name, methodMap);
                        }
                        else if(previous instanceof MethodMap)
                        {
                            // Already overloaded method - add new overload
                            ((MethodMap)previous).addMethod(publicMethod);
                        }
                        else
                        {
                            // Simple method (this far)
                            classMap.put(name, publicMethod);
                        }
                    }
                }
            }
            return classMap;
        }
        catch(IntrospectionException e)
        {
            logger.warn("Couldn't properly perform introspection", e);
            return new HashMap();
        }
    }

    private static Method getAccessibleMethod(Method m, Map accessibles)
    {
        return m == null ? null : (Method)accessibles.get(new MethodSignature(m));
    }
    
    boolean isSafeMethod(Method method)
    {
        return exposureLevel < EXPOSE_SAFE || !UNSAFE_METHODS.contains(method);
    }
    
    /**
     * Retrieves mapping of methods to accessible methods for a class.
     * In case the class is not public, retrieves methods with same 
     * signature as its public methods from public superclasses and 
     * interfaces (if they exist). Basically upcasts every method to the 
     * nearest accessible method.
     */
    private static Map discoverAccessibleMethods(Class clazz)
    {
        Map map = new HashMap();
        discoverAccessibleMethods(clazz, map);
        return map;
    }
    
    private static void discoverAccessibleMethods(Class clazz, Map map)
    {
        if(Modifier.isPublic(clazz.getModifiers()))
        {
            try
            {
                Method[] methods = clazz.getMethods();
                for(int i = 0; i < methods.length; i++)
                {
                    Method method = methods[i];
                    MethodSignature sig = new MethodSignature(method);
                    map.put(sig, method);
                }
                return;
            }
            catch(SecurityException e)
            {
                logger.warn("Could not discover accessible methods of class " + 
                        clazz.getName() + 
                        ", attemping superclasses/interfaces.", e);
                // Fall through and attempt to discover superclass/interface 
                // methods
            }
        }

        Class[] interfaces = clazz.getInterfaces();
        for(int i = 0; i < interfaces.length; i++)
        {
            discoverAccessibleMethods(interfaces[i], map);
        }
        Class superclass = clazz.getSuperclass();
        if(superclass != null)
        {
            discoverAccessibleMethods(superclass, map);
        }
    }

    private static final class MethodSignature
    {
        private static final MethodSignature GET_STRING_SIGNATURE = 
            new MethodSignature("get", new Class[] { String.class });
        private static final MethodSignature GET_OBJECT_SIGNATURE = 
            new MethodSignature("get", new Class[] { Object.class });

        private final String name;
        private final Class[] args;
        
        private MethodSignature(String name, Class[] args)
        {
            this.name = name;
            this.args = args;
        }
        
        MethodSignature(Method method)
        {
            this(method.getName(), method.getParameterTypes());
        }
        
        public boolean equals(Object o)
        {
            if(o instanceof MethodSignature)
            {
                MethodSignature ms = (MethodSignature)o;
                return ms.name.equals(name) && Arrays.equals(args, ms.args);
            }
            return false;
        }
        
        public int hashCode()
        {
            return name.hashCode() ^ args.length;
        }
    }
    
    private static final Set createUnsafeMethodsSet()
    {
        Properties props = new Properties();
        InputStream in = BeansWrapper.class.getResourceAsStream("unsafeMethods.txt");
        if(in != null)
        {
            String methodSpec = null;
            try
            {
                try
                {
                    props.load(in);
                }
                finally
                {
                    in.close();
                }
                Set set = new HashSet(props.size() * 4/3, .75f);
                Map primClasses = createPrimitiveClassesMap();
                for (Iterator iterator = props.keySet().iterator(); iterator.hasNext();)
                {
                    methodSpec = (String) iterator.next();
                    try {
                        set.add(parseMethodSpec(methodSpec, primClasses));
                    }
                    catch(ClassNotFoundException e) {
                        if(DEVELOPMENT) {
                            throw e;
                        }
                    }
                    catch(NoSuchMethodException e) {
                        if(DEVELOPMENT) {
                            throw e;
                        }
                    }
                }
            }
            catch(Exception e)
            {
                throw new RuntimeException("Could not load unsafe method " + methodSpec + " " + e.getClass().getName() + " " + e.getMessage());
            }
        }
        return Collections.EMPTY_SET;
    }
                                                                           
    private static Method parseMethodSpec(String methodSpec, Map primClasses)
    throws
        ClassNotFoundException,
        NoSuchMethodException
    {
        int brace = methodSpec.indexOf('(');
        int dot = methodSpec.lastIndexOf('.', brace);
        Class clazz = ClassUtil.forName(methodSpec.substring(0, dot));
        String methodName = methodSpec.substring(dot + 1, brace);
        String argSpec = methodSpec.substring(brace + 1, methodSpec.length() - 1);
        StringTokenizer tok = new StringTokenizer(argSpec, ",");
        int argcount = tok.countTokens();
        Class[] argTypes = new Class[argcount];
        for (int i = 0; i < argcount; i++)
        {
            String argClassName = tok.nextToken();
            argTypes[i] = (Class)primClasses.get(argClassName);
            if(argTypes[i] == null)
            {
                argTypes[i] = ClassUtil.forName(argClassName);
            }
        }
        return clazz.getMethod(methodName, argTypes);
    }

    private static Map createPrimitiveClassesMap()
    {
        Map map = new HashMap();
        map.put("boolean", Boolean.TYPE);
        map.put("byte", Byte.TYPE);
        map.put("char", Character.TYPE);
        map.put("short", Short.TYPE);
        map.put("int", Integer.TYPE);
        map.put("long", Long.TYPE);
        map.put("float", Float.TYPE);
        map.put("double", Double.TYPE);
        return map;
    }


    /**
     * Converts any {@link BigDecimal}s in the passed array to the type of
     * the corresponding formal argument of the method.
     */
    public static void coerceBigDecimals(AccessibleObject callable, Object[] args)
    {
        Class[] formalTypes = null;
        for(int i = 0, l = args.length; i < l; ++i)
        {
            Object arg = args[i];
            if(arg instanceof BigDecimal)
            {
                BigDecimal bd = (BigDecimal)arg;
                if(formalTypes == null)
                {
                    if(callable instanceof Method) {
                        formalTypes = ((Method)callable).getParameterTypes();
                    }
                    else if(callable instanceof Constructor) {
                        formalTypes = ((Constructor)callable).getParameterTypes();
                    }
                    else {
                        // Cannot happen
                        throw new Error();
                    }
                    if(formalTypes.length != l)
                    {
                        // This will die anyway due to incorrect number of
                        // arguments, so there's no point in checking
                        return;
                    }
                }
                Class formalType = formalTypes[i];
                // int is expected in most situations, so we check it first
                if(formalType == Integer.TYPE || formalType == Integer.class)
                {
                    args[i] = new Integer(bd.intValue());
                }
                else if(formalType == Double.TYPE || formalType == Double.class)
                {
                    args[i] = new Double(bd.doubleValue());
                }
                else if(formalType == Long.TYPE || formalType == Long.class)
                {
                    args[i] = new Long(bd.longValue());
                }
                else if(formalType == Float.TYPE || formalType == Float.class)
                {
                    args[i] = new Float(bd.floatValue());
                }
                else if(formalType == Short.TYPE || formalType == Short.class)
                {
                    args[i] = new Short(bd.shortValue());
                }
                else if(formalType == Byte.TYPE || formalType == Byte.class)
                {
                    args[i] = new Byte(bd.byteValue());
                }
                else if(BIGINTEGER_CLASS.isAssignableFrom(formalType))
                {
                    args[i] = bd.toBigInteger();
                }
            }
        }
    }
}
