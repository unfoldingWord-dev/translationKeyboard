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

package freemarker.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;

/**
 * This is a common superclass of Configuration, Template, and Environment
 * classes. It provides settings that are common to each of them. FreeMarker
 * uses a three-level setting hierarchy - the return value of every property
 * getter method on Configurable objects inherits its value from its parent 
 * Configurable object, unless explicitly overridden by a call to a 
 * corresponding setter method on the object itself. The parent of an 
 * Environment object is a Template object, the parent of a Template object is a
 * Configuration object.
 * @version $Id: Configurable.java,v 1.26.2.2 2004/03/11 23:15:16 ddekany Exp $
 * @author Attila Szegedi
 */
public class Configurable
{
    public static final String LOCALE_KEY = "locale";
    public static final String NUMBER_FORMAT_KEY = "number_format";
    public static final String TIME_FORMAT_KEY = "time_format";
    public static final String DATE_FORMAT_KEY = "date_format";
    public static final String DATETIME_FORMAT_KEY = "datetime_format";
    public static final String TIME_ZONE_KEY = "time_zone";
    public static final String CLASSIC_COMPATIBLE_KEY = "classic_compatible";
    public static final String TEMPLATE_EXCEPTION_HANDLER_KEY = "template_exception_handler";
    public static final String ARITHMETIC_ENGINE_KEY = "arithmetic_engine";
    public static final String OBJECT_WRAPPER_KEY = "object_wrapper";
    public static final String BOOLEAN_FORMAT_KEY = "boolean_format";

    private static final char COMMA = ',';
    
    private Configurable parent;
    private Properties properties;

    private Locale locale;
    private String numberFormat;
    private String timeFormat;
    private String dateFormat;
    private String dateTimeFormat;
    private TimeZone timeZone;
    private String trueFormat;
    private String falseFormat;
    private Boolean classicCompatible;
    private TemplateExceptionHandler templateExceptionHandler;
    private ArithmeticEngine arithmeticEngine;
    private ObjectWrapper objectWrapper;
    
    Configurable() {
        parent = null;
        locale = Locale.getDefault();
        timeZone = TimeZone.getDefault();
        numberFormat = "number";
        timeFormat = "";
        dateFormat = "";
        dateTimeFormat = "";
        trueFormat = "true";
        falseFormat = "false";
        classicCompatible = Boolean.FALSE;
        templateExceptionHandler = TemplateExceptionHandler.DEBUG_HANDLER;
        arithmeticEngine = ArithmeticEngine.BIGDECIMAL_ENGINE;
        objectWrapper = ObjectWrapper.SIMPLE_WRAPPER;
        
        properties = new Properties();
        properties.setProperty(LOCALE_KEY, locale.toString());
        properties.setProperty(TIME_FORMAT_KEY, timeFormat);
        properties.setProperty(DATE_FORMAT_KEY, dateFormat);
        properties.setProperty(DATETIME_FORMAT_KEY, dateTimeFormat);
        properties.setProperty(TIME_ZONE_KEY, timeZone.getID());
        properties.setProperty(NUMBER_FORMAT_KEY, numberFormat);
        properties.setProperty(CLASSIC_COMPATIBLE_KEY, classicCompatible.toString());
        properties.setProperty(TEMPLATE_EXCEPTION_HANDLER_KEY, templateExceptionHandler.getClass().getName());
        properties.setProperty(ARITHMETIC_ENGINE_KEY, arithmeticEngine.getClass().getName());
        properties.setProperty(BOOLEAN_FORMAT_KEY, "true,false");
    }
    
    Configurable(Configurable parent) {
        this.parent = parent;
        locale = null;
        numberFormat = null;
        trueFormat = null;
        falseFormat = null;
        classicCompatible = null;
        templateExceptionHandler = null;
        properties = new Properties(parent.properties);
    }

    protected Object clone() throws CloneNotSupportedException {
        Configurable copy = (Configurable)super.clone();
        copy.properties = new Properties(properties);
        return copy;
    }
    
    Configurable getParent() {
        return parent;
    }
    
    /**
     * Reparenting support. This is used by Environment when it includes a
     * template - the included template becomes the parent configurable during
     * its evaluation.
     */
    void setParent(Configurable parent) {
        this.parent = parent;
    }
    
    /**
     * Toggles the "Classic Compatibile" mode. For a comprehensive description
     * of this mode, see {@link #isClassicCompatible()}.
     */
    public void setClassicCompatible(boolean classicCompatibility) {
        this.classicCompatible = classicCompatibility ? Boolean.TRUE : Boolean.FALSE;
        properties.setProperty(CLASSIC_COMPATIBLE_KEY, classicCompatible.toString());
    }

    /**
     * Returns whether the engine runs in the "Classic Compatibile" mode.
     * When this mode is active, the engine behavior is altered in following
     * way: (these resemble the behavior of the 1.7.x line of FreeMarker engine,
     * now named "FreeMarker Classic", hence the name).
     * <ul>
     * <li>handle undefined expressions gracefully. Namely when an expression
     *   "expr" evaluates to null:
     *   <ul>
     *     <li>as argument of the <tt>&lt;assign varname=expr></tt> directive, 
     *       <tt>${expr}</tt> directive, <tt>otherexpr == expr</tt> or 
     *       <tt>otherexpr != expr</tt> conditional expressions, or 
     *       <tt>hash[expr]</tt> expression, then it is treated as empty string.
     *     </li>
     *     <li>as argument of <tt>&lt;list expr as item></tt> or 
     *       <tt>&lt;foreach item in expr></tt>, the loop body is not executed
     *       (as if it were a 0-length list)
     *     </li>
     *     <li>as argument of <tt>&lt;if></tt> directive, or otherwise where a
     *       boolean expression is expected, it is treated as false
     *     </li>
     *   </ul>
     * </li>
     * <li>Non-boolean models are accepted in <tt>&lt;if></tt> directive,
     *   or as operands of logical operators. "Empty" models (zero-length string,
     * empty sequence or hash) are evaluated as false, all others are evaluated as
     * true.</li>
     * <li>When boolean value is treated as a string (i.e. output in 
     *   <tt>${...}</tt> directive, or concatenated with other string), true 
     * values are converted to string "true", false values are converted to 
     * empty string.
     * </li>
     * <li>Scalar models supplied to <tt>&lt;list></tt> and 
     *   <tt>&lt;foreach></tt> are treated as a one-element list consisting
     *   of the passed model.
     * </li>
     * <li>Paths parameter of <tt>&lt;include></tt> will be interpreted as
     * absolute path.
     * </li>
     * </ul>
     * In all other aspects, the engine is a 2.1 engine even in compatibility
     * mode - you don't lose any of the new functionality by enabling it.
     */
    public boolean isClassicCompatible() {
        return classicCompatible != null ? classicCompatible.booleanValue() : parent.isClassicCompatible();
    }

    /**
     * Sets the locale to assume when searching for template files with no 
     * explicit requested locale.
     */
    public void setLocale(Locale locale) {
        if (locale == null) throw new IllegalArgumentException("Setting \"locale\" can't be null");
        this.locale = locale;
        properties.setProperty(LOCALE_KEY, locale.toString());
    }

    /**
     * Returns the time zone to use when formatting time values. Defaults to 
     * system time zone.
     */
    public TimeZone getTimeZone() {
        return timeZone != null ? timeZone : parent.getTimeZone();
    }

    /**
     * Sets the time zone to use when formatting time values. 
     */
    public void setTimeZone(TimeZone timeZone) {
        if (timeZone == null) throw new IllegalArgumentException("Setting \"time_zone\" can't be null");
        this.timeZone = timeZone;
        properties.setProperty(TIME_ZONE_KEY, timeZone.getID());
    }

    /**
     * Returns the assumed locale when searching for template files with no
     * explicit requested locale. Defaults to system locale.
     */
    public Locale getLocale() {
        return locale != null ? locale : parent.getLocale();
    }

    /**
     * Sets the number format used to convert numbers to strings.
     */
    public void setNumberFormat(String numberFormat) {
        if (numberFormat == null) throw new IllegalArgumentException("Setting \"number_format\" can't be null");
        this.numberFormat = numberFormat;
        properties.setProperty(NUMBER_FORMAT_KEY, numberFormat);
    }

    /**
     * Returns the default number format used to convert numbers to strings.
     * Defaults to <tt>"number"</tt>
     */
    public String getNumberFormat() {
        return numberFormat != null ? numberFormat : parent.getNumberFormat();
    }

    public void setBooleanFormat(String booleanFormat) {
        if (booleanFormat == null) {
            throw new IllegalArgumentException("Setting \"boolean_format\" can't be null");
        } 
        int comma = booleanFormat.indexOf(COMMA);
        if(comma == -1) {
            throw new IllegalArgumentException("Setting \"boolean_format\" must consist of two comma-separated values for true and false respectively");
        }
        trueFormat = booleanFormat.substring(0, comma);
        falseFormat = booleanFormat.substring(comma + 1);
        properties.setProperty(BOOLEAN_FORMAT_KEY, booleanFormat);
    }
    
    public String getBooleanFormat() {
        if(trueFormat == null) {
            return parent.getBooleanFormat(); 
        }
        return trueFormat + COMMA + falseFormat;
    }
    
    String getBooleanFormat(boolean value) {
        return value ? getTrueFormat() : getFalseFormat(); 
    }
    
    private String getTrueFormat() {
        return trueFormat != null ? trueFormat : parent.getTrueFormat(); 
    }
    
    private String getFalseFormat() {
        return falseFormat != null ? falseFormat : parent.getFalseFormat(); 
    }

    /**
     * Sets the date format used to convert date models representing time-only
     * values to strings.
     */
    public void setTimeFormat(String timeFormat) {
        if (timeFormat == null) throw new IllegalArgumentException("Setting \"time_format\" can't be null");
        this.timeFormat = timeFormat;
        properties.setProperty(TIME_FORMAT_KEY, timeFormat);
    }

    /**
     * Returns the date format used to convert date models representing
     * time-only dates to strings.
     * Defaults to <tt>"time"</tt>
     */
    public String getTimeFormat() {
        return timeFormat != null ? timeFormat : parent.getTimeFormat();
    }

    /**
     * Sets the date format used to convert date models representing date-only
     * dates to strings.
     */
    public void setDateFormat(String dateFormat) {
        if (dateFormat == null) throw new IllegalArgumentException("Setting \"date_format\" can't be null");
        this.dateFormat = dateFormat;
        properties.setProperty(DATE_FORMAT_KEY, dateFormat);
    }

    /**
     * Returns the date format used to convert date models representing 
     * date-only dates to strings.
     * Defaults to <tt>"date"</tt>
     */
    public String getDateFormat() {
        return dateFormat != null ? dateFormat : parent.getDateFormat();
    }

    /**
     * Sets the date format used to convert date models representing datetime
     * dates to strings.
     */
    public void setDateTimeFormat(String dateTimeFormat) {
        if (dateTimeFormat == null) throw new IllegalArgumentException("Setting \"datetime_format\" can't be null");
        this.dateTimeFormat = dateTimeFormat;
        properties.setProperty(DATETIME_FORMAT_KEY, dateTimeFormat);
    }

    /**
     * Returns the date format used to convert date models representing datetime
     * dates to strings.
     * Defaults to <tt>"datetime"</tt>
     */
    public String getDateTimeFormat() {
        return dateTimeFormat != null ? dateTimeFormat : parent.getDateTimeFormat();
    }

    /**
     * Sets the exception handler used to handle template exceptions. 
     * @param templateExceptionHandler the template exception handler to use for 
     * handling {@link TemplateException}s. By default, 
     * {@link TemplateExceptionHandler#HTML_DEBUG_HANDLER} is used.
     */
    public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
        if (templateExceptionHandler == null) throw new IllegalArgumentException("Setting \"template_exception_handler\" can't be null");
        this.templateExceptionHandler = templateExceptionHandler;
        properties.setProperty(TEMPLATE_EXCEPTION_HANDLER_KEY, templateExceptionHandler.getClass().getName());
    }

    /**
     * Retrieves the exception handler used to handle template exceptions. 
     */
    public TemplateExceptionHandler getTemplateExceptionHandler() {
        return templateExceptionHandler != null
                ? templateExceptionHandler : parent.getTemplateExceptionHandler();
    }

    /**
     * Sets the arithmetic engine used to perform arithmetic operations.
     * @param arithmeticEngine the arithmetic engine used to perform arithmetic
     * operations.By default, {@link ArithmeticEngine#BIGDECIMAL_ENGINE} is 
     * used.
     */
    public void setArithmeticEngine(ArithmeticEngine arithmeticEngine) {
        if (arithmeticEngine == null) throw new IllegalArgumentException("Setting \"arithmetic_engine\" can't be null");
        this.arithmeticEngine = arithmeticEngine;
        properties.setProperty(ARITHMETIC_ENGINE_KEY, arithmeticEngine.getClass().getName());
    }

    /**
     * Retrieves the arithmetic engine used to perform arithmetic operations.
     */
    public ArithmeticEngine getArithmeticEngine() {
        return arithmeticEngine != null
                ? arithmeticEngine : parent.getArithmeticEngine();
    }

    /**
     * Sets the object wrapper used to wrap objects to template models.
     * @param objectWrapper the object wrapper used to wrap objects to template
     * models.By default, {@link ObjectWrapper#SIMPLE_WRAPPER} is used.
     */
    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        if (objectWrapper == null) throw new IllegalArgumentException("Setting \"object_wrapper\" can't be null");
        this.objectWrapper = objectWrapper;
        properties.setProperty(OBJECT_WRAPPER_KEY, objectWrapper.getClass().getName());
    }

    /**
     * Retrieves the object wrapper used to wrap objects to template models.
     */
    public ObjectWrapper getObjectWrapper() {
        return objectWrapper != null
                ? objectWrapper : parent.getObjectWrapper();
    }

    /**
     * Sets a setting by a name and string value.
     * 
     * <p>List of supported names and their valid values:
     * <ul>
     *   <li><code>"locale"</code>: local codes with the usual format, such as <code>"en_US"</code>.
     *   <li><code>"classic_compatible"</code>:
     *       <code>"true"</code>, <code>"false"</code>, <code>"yes"</code>, <code>"no"</code>,
     *       <code>"t"</code>, <code>"f"</code>, <code>"y"</code>, <code>"n"</code>.
     *       Case insensitive.
     *   <li><code>"template_exception_handler"</code>:  If the value contains dot, then it is
     *       interpreted as class name, and the object will be created with
     *       its parameterles constructor. If the value does not contain dot,
     *       then it must be one of these special values:
     *       <code>"rethrow"</code>, <code>"debug"</code>,
     *       <code>"html_debug"</code>, <code>"ignore"</code> (case insensitive).
     *   <li><code>"arithmetic_engine"</code>: If the value contains dot, then it is
     *       interpreted as class name, and the object will be created with
     *       its parameterles constructor. If the value does not contain dot,
     *       then it must be one of these special values:
     *       <code>"bigdecimal"</code>, <code>"conservative"</code> (case insensitive).  
     *   <li><code>"object_wrapper"</code>: If the value contains dot, then it is
     *       interpreted as class name, and the object will be created with
     *       its parameterles constructor. If the value does not contain dot,
     *       then it must be one of these special values:
     *       <code>"simple"</code>, <code>"beans"</code>, <code>"jython"</code> (case insensitive).
     *   <li><code>"number_format"</code>: pattern as <code>java.text.DecimalFormat</code> defines.
     *   <li><code>"boolean_format"</code>: the textual value for boolean true and false,
     *       separated with comma. For example <code>"yes,no"</code>.
     *   <li><code>"date_format", "time_format", "datetime_format"</code>: patterns as
     *       <code>java.text.SimpleDateFormat</code> defines.
     *   <li><code>"time_zone"</code>: time zone, with the format as
     *       <code>java.util.TimeZone.getTimeZone</code> defines. For example <code>"GMT-8:00"</code> or
     *       <code>"America/Los_Angeles"</code>
     * </ul>
     * 
     * @param key the name of the setting.
     * @param value the string that describes the new value of the setting.
     * 
     * @throws UnknownSettingException if the key is wrong.
     * @throws TemplateException if the new value of the setting can't be set
     *     for any other reasons.
     */
    public void setSetting(String key, String value) throws TemplateException {
        try {
            if (LOCALE_KEY.equals(key)) {
                setLocale(StringUtil.deduceLocale(value));
            } else if (NUMBER_FORMAT_KEY.equals(key)) {
                setNumberFormat(value);
            } else if (TIME_FORMAT_KEY.equals(key)) {
                setTimeFormat(value);
            } else if (DATE_FORMAT_KEY.equals(key)) {
                setDateFormat(value);
            } else if (DATETIME_FORMAT_KEY.equals(key)) {
                setDateTimeFormat(value);
            } else if (TIME_ZONE_KEY.equals(key)) {
                setTimeZone(TimeZone.getTimeZone(value));
            } else if (CLASSIC_COMPATIBLE_KEY.equals(key)) {
                setClassicCompatible(StringUtil.getYesNo(value));
            } else if (TEMPLATE_EXCEPTION_HANDLER_KEY.equals(key)) {
                if (value.indexOf('.') == -1) {
                    if ("debug".equalsIgnoreCase(value)) {
                        setTemplateExceptionHandler(
                                TemplateExceptionHandler.DEBUG_HANDLER);
                    } else if ("html_debug".equalsIgnoreCase(value)) {
                        setTemplateExceptionHandler(
                                TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                    } else if ("ignore".equalsIgnoreCase(value)) {
                        setTemplateExceptionHandler(
                                TemplateExceptionHandler.IGNORE_HANDLER);
                    } else if ("rethrow".equalsIgnoreCase(value)) {
                        setTemplateExceptionHandler(
                                TemplateExceptionHandler.RETHROW_HANDLER);
                    } else {
                        throw invalidSettingValueException(key, value);
                    }
                } else {
                    setTemplateExceptionHandler(
                            (TemplateExceptionHandler) ClassUtil.forName(value)
                            .newInstance());
                }
            } else if (ARITHMETIC_ENGINE_KEY.equals(key)) {
                if (value.indexOf('.') == -1) { 
                    if ("bigdecimal".equalsIgnoreCase(value)) {
                        setArithmeticEngine(ArithmeticEngine.BIGDECIMAL_ENGINE);
                    } else if ("conservative".equalsIgnoreCase(value)) {
                        setArithmeticEngine(ArithmeticEngine.CONSERVATIVE_ENGINE);
                    } else {
                        throw invalidSettingValueException(key, value);
                    }
                } else {
                    setArithmeticEngine(
                            (ArithmeticEngine) ClassUtil.forName(value)
                            .newInstance());
                }
            } else if (OBJECT_WRAPPER_KEY.equals(key)) {
                if (value.indexOf('.') == -1) {
                    if ("simple".equalsIgnoreCase(value)) {
                        setObjectWrapper(ObjectWrapper.SIMPLE_WRAPPER);
                    } else if ("beans".equalsIgnoreCase(value)) {
                        setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
                    } else if ("jython".equalsIgnoreCase(value)) {
                        Class clazz = Class.forName(
                                "freemarker.ext.jython.JythonWrapper");
                        setObjectWrapper(
                                (ObjectWrapper) clazz.getField("INSTANCE").get(null));        
                    } else {
                        throw invalidSettingValueException(key, value);
                    }
                    
                } else {
                    setObjectWrapper((ObjectWrapper) ClassUtil.forName(value)
                            .newInstance());
                }
            } else if (BOOLEAN_FORMAT_KEY.equals(key)) {
                setBooleanFormat(value);
            } else {
                throw unknownSettingException(key);
            }
        } catch(TemplateException e) {
            throw e;
        } catch(Exception e) {
            throw new TemplateException(
                    "Failed to set setting " + key + " to value " + value,
                    e, getEnvironment());
        }
    }
    
    /**
     * Returns the textual representation of a setting.
     * @param key the setting key. Can be any of standard <tt>XXX_KEY</tt>
     * constants, or a custom key.
     */
    public String getSetting(String key) {
        return properties.getProperty(key);
    }
    
    public Map getSettings() {
        return Collections.unmodifiableMap(properties);
    }
    
    protected Environment getEnvironment() {
        return 
            this instanceof Environment 
            ? (Environment)this
            : Environment.getCurrentEnvironment();
    }
    
    protected TemplateException unknownSettingException(String name) {
        return new UnknownSettingException(name, getEnvironment());
    }

    protected TemplateException invalidSettingValueException(String name, String value) {
        return new TemplateException("Invalid value for setting " + name + ": " + value, getEnvironment());
    }
    
    public class UnknownSettingException extends TemplateException {
        private UnknownSettingException(String name, Environment env) {
            super("Unknown setting: " + name, env);
        }
    }

    /**
     * Set the settings stored in a <code>Properties</code> object.
     * 
     * @throws TemplateException if the <code>Properties</code> object contains
     *     invalid keys, or invalid setting values, or any other error occurs
     *     while changing the settings.
     */    
    public void setSettings(Properties props) throws TemplateException {
        Iterator it = props.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            setSetting(key, props.getProperty(key).trim()); 
        }
    }
    
    /**
     * Reads a setting list (key and element pairs) from the input stream.
     * The stream has to follow the usual <code>.properties</code> format.
     *
     * @throws TemplateException if the stream contains
     *     invalid keys, or invalid setting values, or any other error occurs
     *     while changing the settings.
     * @throws IOException if an error occurred when reading from the input stream.
     */
    public void setSettings(InputStream propsIn) throws TemplateException, IOException {
        Properties p = new Properties();
        p.load(propsIn);
        setSettings(p);
    }

}