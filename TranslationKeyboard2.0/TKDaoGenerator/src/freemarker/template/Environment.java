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
import java.io.PrintWriter;
import java.io.Writer;
import java.text.Collator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;

import freemarker.ext.beans.BeansWrapper;
import freemarker.log.Logger;
import freemarker.template.utility.UndeclaredThrowableException;

/**
 * Object that represents the runtime environment during template processing.
 * For every invocation of a <tt>Template.process()</tt> method, a new instance
 * of this object is created, and then discarded when <tt>process()</tt> returns.
 * This object stores the set of temporary variables created by the template,
 * the value of settings set by the template, the reference to the data model root,
 * etc. Everything that is needed to fulfill the template processing job.
 *
 * <p>Data models that need to access the <tt>Environment</tt>
 * object that represents the template processing on the current thread can use
 * the {@link #getCurrentEnvironment()} method.
 *
 * <p>If you need to modify or read this object before or after the <tt>process</tt>
 * call, use {@link Template#createProcessingEnvironment(Object rootMap, Writer out, ObjectWrapper wrapper)}
 *
 * @author <a href="mailto:jon@revusky.com">Jonathan Revusky</a>
 * @author Attila Szegedi
 */
public final class Environment extends Configurable {

    private static final ThreadLocal threadEnv = new ThreadLocal();

    private static final Logger logger = Logger.getLogger("freemarker.runtime");

    private static final Map localizedNumberFormats = new HashMap();
    private static final Map localizedDateFormats = new HashMap();

    private final TemplateHashModel rootDataModel;
    private final ArrayList elementStack = new ArrayList();

    private NumberFormat numberFormat;
    private Map numberFormats;

    private DateFormat timeFormat;
    private DateFormat dateFormat;
    private DateFormat dateTimeFormat;
    private Map[] dateFormats;

    private Collator collator;

    private Writer out;
    private Macro.Context currentMacroContext;
    private ArrayList localContextStack, namespaceStack = new ArrayList();
    private Namespace mainNamespace, currentNamespace, globalNamespace;
    private HashMap loadedLibs;

    private Throwable lastThrowable = null;
    private HashMap macroToNamespaceLookup = new HashMap();

    /**
     * Retrieves the environment object associated with the current
     * thread. Data model implementations that need access to the
     * environment can call this method to obtain the environment object
     * that represents the template processing that is currently running
     * on the current thread.
     */
    public static Environment getCurrentEnvironment()
    {
        return (Environment)threadEnv.get();
    }

    Environment(Template template, final TemplateHashModel rootDataModel, Writer out)
    {
        super(template);
        this.globalNamespace = new Namespace();
        this.currentNamespace = mainNamespace = new Namespace();
        namespaceStack.add(currentNamespace);
        this.out = out;
        this.rootDataModel = rootDataModel;
        importMacros(template);
    }

    /**
     * Retrieves the currently processed template.
     */
    public Template getTemplate()
    {
        return (Template)getParent();
    }

    /**
     * Processes the template to which this environment belongs.
     */
    public void process() throws TemplateException, IOException {
        Object savedEnv = threadEnv.get();
        threadEnv.set(this);
        try {
            visit(getTemplate().rootElement);
        } finally {
            threadEnv.set(savedEnv);
        }
    }

    /**
     * "Visit" the template element.
     */
    void visit(TemplateElement element)
    throws TemplateException, IOException
    {
        pushElement(element);
        try {
            element.accept(this);
        }
        catch (TemplateException te) {
            handleTemplateException(te);
        }
        finally {
            popElement();
        }
    }

    /**
     * "Visit" the template element, passing the output
     * through a TemplateTransformModel
     * @param element the element to visit through a transform
     * @param transform the transform to pass the element output
     * through
     * @param args optional arguments fed to the transform
     */
    void visit(TemplateElement element,
               TemplateTransformModel transform,
               Map args)
    throws TemplateException, IOException
    {
        try {
            Writer tw = transform.getWriter(out, args);
            if (tw == null) tw = EMPTY_BODY_WRITER;
            TransformControl tc =
                tw instanceof TransformControl
                ? (TransformControl)tw
                : null;

            Writer prevOut = out;
            out = tw;
            try {
                if(tc == null || tc.onStart() != TransformControl.SKIP_BODY) {
                    do {
                        if(element != null) {
                            visit(element);
                        }
                    } while(tc != null && tc.afterBody() == TransformControl.REPEAT_EVALUATION);
                }
            }
            catch(Throwable t) {
                try {
                    if(tc != null) {
                        tc.onError(t);
                    }
                    else {
                        throw t;
                    }
                }
                catch(TemplateException e) {
                    throw e;
                }
                catch(IOException e) {
                    throw e;
                }
                catch(RuntimeException e) {
                    throw e;
                }
                catch(Error e) {
                    throw e;
                }
                catch(Throwable e) {
                    throw new UndeclaredThrowableException(e);
                }
            }
            finally {
                out = prevOut;
                tw.close();
            }
        }
        catch(TemplateException te) {
            handleTemplateException(te);
        }
    }

    /**
     * "Visit" a macro
     */
    void visit(Macro.Context mctxt)
    throws TemplateException, IOException
    {
        Macro.Context previousMacroContext = currentMacroContext;
        currentMacroContext = mctxt;
        ArrayList prevLocalContextStack = localContextStack;
        localContextStack = null;
        Namespace prevNamespace = currentNamespace;
        currentNamespace = getMacroNamespace(mctxt.getMacro());
        try {
            mctxt.runMacro(this);
        }
        catch (BreakInstruction.Return re) {
        }
        catch (TemplateException te) {
            handleTemplateException(te);
        } finally {
            currentMacroContext = previousMacroContext;
            localContextStack = prevLocalContextStack;
            currentNamespace = prevNamespace;
        }
    }

    void visit(BodyInstruction.Context bctxt) throws TemplateException, IOException {
        Macro.Context invokingMacroContext = getCurrentMacroContext();
        ArrayList prevLocalContextStack = getCurrentLocalContextStack();
        TemplateElement body = invokingMacroContext.body;
        if (body != null) {
            setCurrentMacroContext(invokingMacroContext.prevMacroContext);
            setCurrentNamespace(invokingMacroContext.bodyNamespace);
            setCurrentLocalContextStack(invokingMacroContext.prevLocalContextStack);
            if (invokingMacroContext.bodyParameterNames != null) {
                pushLocalContext(bctxt);
            }
            try {
                visit(body);
            }
            finally {
                if (invokingMacroContext.bodyParameterNames != null) {
                    popLocalContext();
                }
                setCurrentMacroContext(invokingMacroContext);
                setCurrentNamespace(getMacroNamespace(invokingMacroContext.getMacro()));
                setCurrentLocalContextStack(prevLocalContextStack);
            }
        }
    }

    /**
     * "visit" an IteratorBlock
     */
    void visit(IteratorBlock.Context ictxt)
    throws TemplateException, IOException
    {
        pushLocalContext(ictxt);
        try {
            ictxt.runLoop(this);
        }
        catch (BreakInstruction.Break br) {
        }
        catch (TemplateException te) {
            handleTemplateException(te);
        }
        finally {
            popLocalContext();
        }
    }
    
    void visitMacroDef(Macro macro) {
        macroToNamespaceLookup.put(macro, currentNamespace);
        currentNamespace.put(macro.getMacroName(), macro);
    }

    Namespace getMacroNamespace(Macro macro) {
        return (Namespace) macroToNamespaceLookup.get(macro);
    }
    
    Macro.Context getCurrentMacroContext() {
        return currentMacroContext;
    }

    void setCurrentMacroContext(Macro.Context macroContext) {
        this.currentMacroContext = macroContext;
    }

    ArrayList getCurrentLocalContextStack() {
        return localContextStack;
    }

    void setCurrentLocalContextStack(ArrayList localContextStack) {
        this.localContextStack = localContextStack;
    }

    private void handleTemplateException(TemplateException te)
        throws TemplateException
    {
        // Logic to prevent double-handling of the exception in
        // nested visit() calls.
        if(lastThrowable == te) {
            throw te;
        }
        lastThrowable = te;

        // Log the exception
        if(logger.isErrorEnabled()) {
            logger.error("", te);
        }

        // Stop exception is not passed to the handler, but
        // explicitly rethrown.
        if(te instanceof StopException) {
            throw te;
        }

        // Finally, pass the exception to the handler
        getTemplateExceptionHandler().handleTemplateException(te, this, out);
    }

    public void setLocale(Locale locale) {
        super.setLocale(locale);
        // Clear local format cache
        numberFormats = null;
        numberFormat = null;

        dateFormats = null;
        timeFormat = dateFormat = dateTimeFormat = null;

        collator = null;
    }


    public void setTimeZone(TimeZone timeZone) {
        super.setTimeZone(timeZone);
        // Clear local date format cache
        dateFormats = null;
        timeFormat = dateFormat = dateTimeFormat = null;
    }


    Collator getCollator() {
        if(collator == null) {
            collator = Collator.getInstance(getLocale());
        }
        return collator;
    }

    Writer getOut() {
        return out;
    }

    String formatNumber(Number number) {
        if(numberFormat == null) {
            numberFormat = getNumberFormatObject(getNumberFormat());
        }
        return numberFormat.format(number);
    }

    public void setNumberFormat(String formatName) {
        super.setNumberFormat(formatName);
        numberFormat = null;
    }

    String formatDate(Date date, int type)
    throws
        TemplateModelException
    {
        DateFormat df = getDateFormatObject(type);
        if(df == null) {
            throw new TemplateModelException("Can't convert the date to string, because it is not known which parts of the date variable are in use. Use ?date, ?time or ?datetime built-in, or ?string.<format> or ?string(format) built-in with this date.");
        }
        return df.format(date);
    }

    public void setTimeFormat(String formatName) {
        super.setTimeFormat(formatName);
        timeFormat = null;
    }

    public void setDateFormat(String formatName) {
        super.setDateFormat(formatName);
        dateFormat = null;
    }

    public void setDateTimeFormat(String formatName) {
        super.setDateTimeFormat(formatName);
        dateTimeFormat = null;
    }

    Configuration getConfiguration() {
        return getTemplate().getConfiguration();
    }

    NumberFormat getNumberFormatObject(String pattern)
    {
        if(numberFormats == null) {
            numberFormats = new HashMap();
        }

        NumberFormat format = (NumberFormat) numberFormats.get(pattern);
        if(format != null)
        {
            return format;
        }

        // Get format from global format cache
        synchronized(localizedNumberFormats)
        {
            Locale locale = getLocale();
            NumberFormatKey fk = new NumberFormatKey(pattern, locale);
            format = (NumberFormat)localizedNumberFormats.get(fk);
            if(format == null)
            {
                // Add format to global format cache. Note this is
                // globally done once per locale per pattern.
                if("number".equals(pattern))
                {
                    format = NumberFormat.getNumberInstance(locale);
                }
                else if("currency".equals(pattern))
                {
                    format = NumberFormat.getCurrencyInstance(locale);
                }
                else if("percent".equals(pattern))
                {
                    format = NumberFormat.getPercentInstance(locale);
                }
                else
                {
                    format = new DecimalFormat(pattern, new DecimalFormatSymbols(getLocale()));
                }
                localizedNumberFormats.put(fk, format);
            }
        }

        // Clone it and store the clone in the local cache
        format = (NumberFormat)format.clone();
        numberFormats.put(pattern, format);
        return format;
    }

    DateFormat getDateFormatObject(int dateType)
    throws
        TemplateModelException
    {
        switch(dateType) {
            case TemplateDateModel.UNKNOWN: {
                return null;
            }
            case TemplateDateModel.TIME: {
                if(timeFormat == null) {
                    timeFormat = getDateFormatObject(dateType, getTimeFormat());
                }
                return timeFormat;
            }
            case TemplateDateModel.DATE: {
                if(dateFormat == null) {
                    dateFormat = getDateFormatObject(dateType, getDateFormat());
                }
                return dateFormat;
            }
            case TemplateDateModel.DATETIME: {
                if(dateTimeFormat == null) {
                    dateTimeFormat = getDateFormatObject(dateType, getDateTimeFormat());
                }
                return dateTimeFormat;
            }
            default: {
                throw new TemplateModelException("Unrecognized date type " + dateType);
            }
        }
    }
    
    DateFormat getDateFormatObject(int dateType, String pattern)
    throws
        TemplateModelException
    {
        if(dateFormats == null) {
            dateFormats = new Map[4];
            dateFormats[TemplateDateModel.UNKNOWN] = new HashMap();
            dateFormats[TemplateDateModel.TIME] = new HashMap();
            dateFormats[TemplateDateModel.DATE] = new HashMap();
            dateFormats[TemplateDateModel.DATETIME] = new HashMap();
        }
        Map typedDateFormat = dateFormats[dateType];

        DateFormat format = (DateFormat) typedDateFormat.get(pattern);
        if(format != null) {
            return format;
        }

        // Get format from global format cache
        synchronized(localizedDateFormats) {
            Locale locale = getLocale();
            TimeZone timeZone = getTimeZone();
            DateFormatKey fk = new DateFormatKey(dateType, pattern, locale, timeZone);
            format = (DateFormat)localizedDateFormats.get(fk);
            if(format == null) {
                // Add format to global format cache. Note this is
                // globally done once per locale per pattern.
                StringTokenizer tok = new StringTokenizer(pattern, "_");
                int style = tok.hasMoreTokens() ? parseDateStyleToken(tok.nextToken()) : DateFormat.DEFAULT;
                if(style != -1) {
                    switch(dateType) {
                        case TemplateDateModel.UNKNOWN: {
                            throw new TemplateModelException(
                                "Can't convert the date to string using a " +
                                "built-in format, because it is not known which " +
                                "parts of the date variable are in use. Use " +
                                "?date, ?time or ?datetime built-in, or " +
                                "?string.<format> or ?string(<format>) built-in "+
                                "with explicit formatting pattern with this date.");
                        }
                        case TemplateDateModel.TIME: {
                            format = DateFormat.getTimeInstance(style, locale);
                            break;
                        }
                        case TemplateDateModel.DATE: {
                            format = DateFormat.getDateInstance(style, locale);
                            break;
                        }
                        case TemplateDateModel.DATETIME: {
                            int timestyle = tok.hasMoreTokens() ? parseDateStyleToken(tok.nextToken()) : style;
                            if(timestyle != -1) {
                                format = DateFormat.getDateTimeInstance(style, timestyle, locale);
                            }
                            break;
                        }
                    }
                }
                if(format == null) {
                    try {
                        format = new SimpleDateFormat(pattern, locale);
                    }
                    catch(IllegalArgumentException e) {
                        throw new TemplateModelException("Can't parse " + pattern + " to a date format.", e);
                    }
                }
                format.setTimeZone(timeZone);
                localizedDateFormats.put(fk, format);
            }
        }

        // Clone it and store the clone in the local cache
        format = (DateFormat)format.clone();
        typedDateFormat.put(pattern, format);
        return format;
    }

    int parseDateStyleToken(String token) {
        if("short".equals(token)) {
            return DateFormat.SHORT;
        }
        if("medium".equals(token)) {
            return DateFormat.MEDIUM;
        }
        if("long".equals(token)) {
            return DateFormat.LONG;
        }
        return -1;
    }

    TemplateTransformModel getTransform(Expression exp) throws TemplateException {
        TemplateTransformModel ttm = null;
        TemplateModel tm = exp.getAsTemplateModel(this);
        if (tm instanceof TemplateTransformModel) {
            ttm = (TemplateTransformModel) tm;
        }
        else if (exp instanceof Identifier) {
            tm = getConfiguration().getSharedVariable(exp.toString());
            if (tm instanceof TemplateTransformModel) {
                ttm = (TemplateTransformModel) tm;
            }
        }
        return ttm;
    }

    /**
     * Returns the loop or macro local variable corresponding to this
     * variable name. Possibly null.
     * (Note that the misnomer is kept for backward compatibility: loop variables
     * are not local variables according to our terminology.)
     */
    public TemplateModel getLocalVariable(String name) throws TemplateModelException {
        if (localContextStack != null) {
            for (int i = localContextStack.size()-1; i>=0; i--) {
                LocalContext lc = (LocalContext) localContextStack.get(i);
                TemplateModel tm = lc.getLocalVariable(name);
                if (tm != null) {
                    return tm;
                }
            }
        }
        return currentMacroContext == null ? null : currentMacroContext.getLocalVariable(name);
    }

    /**
     * Returns the variable that is visible in this context.
     * This is the correspondent to an FTL top-level variable reading expression.
     * That is, it tries to find the the variable in this order:
     * <ol>
     *   <li>An loop variable (if we're in a loop or user defined directive body) such as foo_has_next
     *   <li>A local variable (if we're in a macro)
     *   <li>A variable defined in the current namespace (say, via &lt;#assign ...&gt;)
     *   <li>A variable defined globally (say, via &lt;#global ....&gt;)
     *   <li>Variable in the data model:
     *     <ol>
     *       <li>A variable in the root hash that was exposed to this
                 rendering environment in the Template.process(...) call
     *       <li>A shared variable set in the configuration via a call to Configuration.setSharedVariable(...)
     *     </ol>
     *   </li>
     * </ol>
     */
    public TemplateModel getVariable(String name) throws TemplateModelException {
        TemplateModel result = getLocalVariable(name);
        if (result == null) {
            result = currentNamespace.get(name);
        }
        if (result == null) {
            result = getGlobalVariable(name);
        }
        return result;
    }

    /**
     * Returns the globally visible variable of the given name (or null).
     * This is correspondent to FTL <code>.globals.<i>name</i></code>.
     * This will first look at variables that were assigned globally via:
     * &lt;#global ...&gt; and then at the data model exposed to the template.
     */
    public TemplateModel getGlobalVariable(String name) throws TemplateModelException {
        TemplateModel result = globalNamespace.get(name);
        if (result == null) {
            result = rootDataModel.get(name);
        }
        if (result == null) {
            result = getConfiguration().getSharedVariable(name);
        }
        return result;
    }

    /**
     * Sets a variable that is visible globally.
     * This is correspondent to FTL <code><#global <i>name</i>=<i>model</i>></code>.
     * This can be considered a convenient shorthand for:
     * getGlobalNamespace().put(name, model)
     */
    public void setGlobalVariable(String name, TemplateModel model) {
        globalNamespace.put(name, model);
    }

    /**
     * Sets a variable in the current namespace.
     * This is correspondent to FTL <code><#assign <i>name</i>=<i>model</i>></code>.
     * This can be considered a convenient shorthand for:
     * getCurrentNamespace().put(name, model)
     */
    public void setVariable(String name, TemplateModel model) {
        currentNamespace.put(name, model);
    }

    /**
     * Sets a local variable (one effective only during a macro invocation).
     * This is correspondent to FTL <code><#local <i>name</i>=<i>model</i>></code>.
     * @param name the identifier of the variable
     * @param model the value of the variable.
     * @throws IllegalStateException if the environment is not executing a
     * macro body.
     */
    public void setLocalVariable(String name, TemplateModel model) {
        if(currentMacroContext == null) {
            throw new IllegalStateException("Not executing macro body");
        }
        currentMacroContext.setLocalVar(name, model);
    }

    /**
     * Returns a set of variable names that are known at the time of call. This
     * includes names of all shared variables in the {@link Configuration},
     * names of all global variables that were assigned during the template processing,
     * names of all variables in the current name-space, names of all local variables
     * and loop variables. If the passed root data model implements the
     * {@link TemplateHashModelEx} interface, then all names it retrieves through a call to
     * {@link TemplateHashModelEx#keys()} method are returned as well.
     * The method returns a new Set object on each call that is completely
     * disconnected from the Environment. That is, modifying the set will have
     * no effect on the Environment object.
     */
    public Set getKnownVariableNames() throws TemplateModelException {
        // shared vars.
        Set set = getConfiguration().getSharedVariableNames();
        
        // root hash
        if (rootDataModel instanceof TemplateHashModelEx) {
            TemplateModelIterator rootNames =
                ((TemplateHashModelEx) rootDataModel).keys().iterator();
            while(rootNames.hasNext()) {
                set.add(((TemplateScalarModel)rootNames.next()).getAsString());
            }
        }
        
        // globals
        for (TemplateModelIterator tmi = globalNamespace.keys().iterator(); tmi.hasNext();) {
            set.add(((TemplateScalarModel) tmi.next()).getAsString());
        }
        
        // current name-space
        for (TemplateModelIterator tmi = currentNamespace.keys().iterator(); tmi.hasNext();) {
            set.add(((TemplateScalarModel) tmi.next()).getAsString());
        }
        
        // locals and loop vars
        if(currentMacroContext != null) {
            set.addAll(currentMacroContext.getLocalVariableNames());
        }
        if (localContextStack != null) {
            for (int i = localContextStack.size()-1; i>=0; i--) {
                LocalContext lc = (LocalContext) localContextStack.get(i);
                set.addAll(lc.getLocalVariableNames());
            }
        }
        return set;
    }

    /**
     * Outputs the instruction stack. Useful for debugging.
     * {@link TemplateException}s incorporate this information in their stack
     * traces.
     */
    public void outputInstructionStack(PrintWriter pw) {
        pw.println("----------");
        ListIterator iter = elementStack.listIterator(elementStack.size());
        if(iter.hasPrevious()) {
            pw.print("==> ");
            TemplateElement prev = (TemplateElement) iter.previous();
            pw.print(prev.getDescription());
            pw.print(" [");
            pw.print(prev.getStartLocation());
            pw.println("]");
        }
        while(iter.hasPrevious()) {
            TemplateElement prev = (TemplateElement) iter.previous();
            if (prev instanceof UnifiedCall || prev instanceof Include) {
                String location = prev.getDescription() + " [" + prev.getStartLocation() + "]";
                if(location != null && location.length() > 0) {
                    pw.print(" in ");
                    pw.println(location);
                }
            }
        }
        pw.println("----------");
        pw.flush();
    }

    private void pushLocalContext(LocalContext localContext) {
        if (localContextStack == null) {
            localContextStack = new ArrayList();
        }
        localContextStack.add(localContext);
    }

    private void popLocalContext() {
        localContextStack.remove(localContextStack.size() - 1);
    }

    /**
     * Returns the name-space for the name if exists, or null.
     * @param name the template path that you have used with the <code>import</code> directive
     *     or {@link #importLib} call, in normalized form. That is, the path must be an absolute
     *     path, and it must not contain "/../" or "/./". The leading "/" is optional.
     */
    public Namespace getNamespace(String name) {
        if (name.startsWith("/")) name = name.substring(1);
        if (loadedLibs != null) {
            return (Namespace) loadedLibs.get(name);
        } else {
            return null;
        }
    }

    /**
     * Returns the main name-space.
     * This is correspondent of FTL <code>.main</code> hash.
     */
    public Namespace getMainNamespace() {
        return mainNamespace;
    }

    /**
     * Returns the main name-space.
     * This is correspondent of FTL <code>.namespace</code> hash.
     */
    public Namespace getCurrentNamespace() {
        return currentNamespace;
    }

    /**
     * Returns a fictitious name-space that contains the globally visible variables
     * that were created in the template, but not the variables of the data-model.
     * There is no such thing in FTL; this strange method was added because of the
     * JSP taglib support, since this imaginary name-space contains the page-scope
     * attributes.
     */
    public Namespace getGlobalNamespace() {
        return globalNamespace;
    }

    /**
     * Returns the data model hash.
     * This is correspondent of FTL <code>.datamodel</code> hash.
     * That is, it contains both the variables of the root hash passed to the
     * <code>Template.process(...)</code>, and the shared variables in the
     * <code>Configuration</code>.
     */
    public TemplateHashModel getDataModel() {
        return new TemplateHashModel() {
                public boolean isEmpty() {
                    return false;
                }
                public TemplateModel get(String key) throws TemplateModelException {
                    TemplateModel result = rootDataModel.get(key);
                    if (result == null) {
                        result = getConfiguration().getSharedVariable(key);
                    }
                    return result;
                }
            };
    }

    /**
     * Returns the read-only hash of globally visible variables.
     * This is the correspondent of FTL <code>.globals</code> hash.
     * That is, you see the variables created with
     * <code>&lt;#global ...></code>, and the variables of the data-model.
     * To create new global variables, use {@link #setGlobalVariable setGlobalVariable}.
     */
    public TemplateHashModel getGlobalVariables() {
        return new TemplateHashModel() {
            public boolean isEmpty() {
                return false;
            }
            public TemplateModel get(String key) throws TemplateModelException {
                TemplateModel result = globalNamespace.get(key);
                if (result == null) {
                    result = rootDataModel.get(key);
                }
                if (result == null) {
                    result = getConfiguration().getSharedVariable(key);
                }
                return result;
            }
        };
    }

    void setCurrentNamespace(Namespace namespace) {
        this.currentNamespace = namespace;
    }

    void pushNamespace(String name) {
        Namespace newNamespace = new Namespace();
        currentNamespace.put(name, newNamespace);
        if (currentNamespace == mainNamespace) {
            globalNamespace.put(name, newNamespace);
        }
        namespaceStack.add(newNamespace);
        this.currentNamespace = newNamespace;
    }

    void popNamespace() {
        int lastIndex = namespaceStack.size() -1;
        namespaceStack.remove(lastIndex);
        currentNamespace = (Namespace) namespaceStack.get(lastIndex -1);
    }

    private void pushElement(TemplateElement element) {
        elementStack.add(element);
    }

    private void popElement() {
        elementStack.remove(elementStack.size() - 1);
    }

    /**
     * Emulates <code>include</code> directive.
     * Obtains a Template through this environment's Configuration and processes
     * it in the context of this Environment, including its output in the
     * Environment's Writer.
     * @param name the name of the template
     * @param encoding the encoding of the obtained template. If null,
     * the encoding of the Template that is currently being processed in this
     * Environment is used.
     * @param parse whether to process a parsed template or just include the
     * unparsed template source.
     */
    public void include(String name, String encoding, boolean parse)
    throws IOException, TemplateException
    {
        if (encoding == null) {
            encoding = getTemplate().getEncoding();
        }
        if (encoding == null) {
            encoding = getConfiguration().getEncoding(this.getLocale());
        }
        Template includedTemplate = getConfiguration().getTemplate(name, getLocale(), encoding, parse);
        include(includedTemplate);
    }

    /**
     * Emulates <code>import</code> directive.
     */
    public void importLib(String name, String namespace)
    throws IOException, TemplateException
    {
        String encoding = getTemplate().getEncoding();
        if (encoding == null) {
            encoding = getConfiguration().getEncoding(this.getLocale());
        }
        Template loadedTemplate = getConfiguration().getTemplate(name, getLocale(), encoding, true);
        if (loadedLibs == null) {
            loadedLibs = new HashMap();
        }
        String templateName = loadedTemplate.getName();
        Namespace existingNamespace = (Namespace) loadedLibs.get(templateName);
        if (existingNamespace != null) {
            setVariable(namespace, existingNamespace);
        }
        else {
            pushNamespace(namespace);
            loadedLibs.put(templateName, currentNamespace);
            Writer prevOut = out;
            this.out = NULL_WRITER;
            try {
                include(loadedTemplate);
            } finally {
                this.out = prevOut;
                popNamespace();
            }
        }
    }

    void importMacros(Template template) {
        for (Iterator it = template.macros.values().iterator(); it.hasNext();) {
            visitMacroDef((Macro) it.next());
        }
    }

    /**
     * Processes a Template in the context of this Environment, including its
     * output in the Environment's Writer.
     * @param includedTemplate the template to process
     */
    public void include(Template includedTemplate)
    throws TemplateException, IOException
    {
        Template prevTemplate = getTemplate();
        setParent(includedTemplate);
        importMacros(includedTemplate);
        try {
            visit(includedTemplate.rootElement);
        }
        finally {
            setParent(prevTemplate);
        }
    }

    /**
     * A hook that Jython uses.
     */
    public Object __getitem__(String key) throws TemplateModelException {
        return BeansWrapper.getDefaultInstance().unwrap(getVariable(key));
    }

    /**
     * A hook that Jython uses.
     */
    public void __setitem__(String key, Object o) throws TemplateException {
        setGlobalVariable(key, BeansWrapper.getDefaultInstance().wrap(o));
    }

    private static final class NumberFormatKey
    {
        private final String pattern;
        private final Locale locale;

        NumberFormatKey(String pattern, Locale locale)
        {
            this.pattern = pattern;
            this.locale = locale;
        }

        public boolean equals(Object o)
        {
            if(o instanceof NumberFormatKey)
            {
                NumberFormatKey fk = (NumberFormatKey)o;
                return fk.pattern.equals(pattern) && fk.locale.equals(locale);
            }
            return false;
        }

        public int hashCode()
        {
            return pattern.hashCode() ^ locale.hashCode();
        }
    }

    private static final class DateFormatKey
    {
        private final int dateType;
        private final String pattern;
        private final Locale locale;
        private final TimeZone timeZone;

        DateFormatKey(int dateType, String pattern, Locale locale, TimeZone timeZone)
        {
            this.dateType = dateType;
            this.pattern = pattern;
            this.locale = locale;
            this.timeZone = timeZone;
        }

        public boolean equals(Object o)
        {
            if(o instanceof DateFormatKey)
            {
                DateFormatKey fk = (DateFormatKey)o;
                return dateType == fk.dateType && fk.pattern.equals(pattern) && fk.locale.equals(locale) && fk.timeZone.equals(timeZone);
            }
            return false;
        }

        public int hashCode()
        {
            return dateType ^ pattern.hashCode() ^ locale.hashCode() ^ timeZone.hashCode();
        }
    }

    static public class Namespace extends SimpleHash {}

    static private final Writer NULL_WRITER = new Writer() {
            public void write(char cbuf[], int off, int len) {}
            public void flush() {}
            public void close() {}
     };
     
     private static final Writer EMPTY_BODY_WRITER = new Writer() {
    
        public void write(char[] cbuf, int off, int len) throws IOException {
            if (len > 0) {
                throw new IOException(
                        "This transform does no allow nested content.");
            }
        }
    
        public void flush() throws IOException {
        }
    
        public void close() throws IOException {
        }
    };

}
