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

package freemarker.cache;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.utility.SecurityUtilities;

/**
 * A class that performs caching and on-demand loading of the templates.
 * The actual loading is delegated to a {@link TemplateLoader}. Also,
 * various constructors provide you with convenient caches with predefined
 * behavior. Typically you don't use this class directly - in normal
 * circumstances it is hidden behind a {@link Configuration}.
 * @author Attila Szegedi, szegedia at freemail dot hu
 * @version $Id: TemplateCache.java,v 1.46.2.7 2004/03/29 12:40:36 szegedia Exp $
 */
public class TemplateCache
{
    private static final String ASTERISKSTR = "*";
    private static final String LOCALE_SEPARATOR = "_";
    private static final char ASTERISK = '*';
    private static final String CURRENT_DIR_PATH_PREFIX = "./";
    private static final String CURRENT_DIR_PATH = "/./";
    private static final String PARENT_DIR_PATH_PREFIX = "../";
    private static final String PARENT_DIR_PATH = "/../";
    private static final char SLASH = '/';
    private static final Logger logger = Logger.getLogger("freemarker.cache");

    private final TemplateLoader loader;
    /** Here we keep our cached templates */
    private final CacheStorage storage;
    /** The default refresh delay in milliseconds. */
    private long delay = 5000;
    /** Specifies if localized template lookup is enabled or not */
    private boolean localizedLookup = true;

    private Configuration config;

    /**
     * Returns a template cache that will first try to load a template from
     * the file system relative to the current user directory (i.e. the value
     * of the system property <code>user.dir</code>), then from the classpath.
     * This default template cache suits many applications.
     */
    public TemplateCache()
    {
        this(createDefaultTemplateLoader());
    }
    
    private static TemplateLoader createDefaultTemplateLoader()
    {
        ClassTemplateLoader ctl = new ClassTemplateLoader(TemplateCache.class, "/"); 
        try
        {
            return new MultiTemplateLoader(
                new TemplateLoader[] { new FileTemplateLoader(), ctl });
        }
        catch(Exception e)
        {
            logger.info(
                "Could not create default file template loader for ${user.dir}=" 
                + SecurityUtilities.getSystemProperty("user.dir"));
            return ctl;
        }        
    }

    /**
     * Creates a new template cache with a custom template loader that is used
     * to load the templates.
     * @param loader the template loader to use.
     */
    public TemplateCache(TemplateLoader loader)
    {
        this(loader, new SoftCacheStorage());
    }

    /**
     * Creates a new template cache with a custom template loader that is used
     * to load the templates.
     * @param loader the template loader to use.
     */
    public TemplateCache(TemplateLoader loader, CacheStorage storage)
    {
        if (loader == null)
        {
            throw new IllegalArgumentException("loader == null");
        }
        this.loader = loader;
        if(storage == null)
        {
            throw new IllegalArgumentException("storage == null");
        }
        this.storage = storage;
    }

    /**
     * Sets the configuration object to which this cache belongs. This
     * method is called by the configuration itself to establish the
     * relation, and should not be called by users.
     */
    public void setConfiguration(Configuration config)
    {
        this.config = config;
        clear();
    }

    public TemplateLoader getTemplateLoader()
    {
        return loader;
    }
    
    public CacheStorage getCacheStorage()
    {
        return storage;
    }
    
    /**
     * Loads a template with the given name, in the specified locale and
     * using the specified character encoding.
     *
     * @param name the name of the template. Can't be null. The exact syntax of the name
     * is interpreted by the underlying {@link TemplateLoader}, but the
     * cache makes some assumptions. First, the name is expected to be
     * a hierarchical path, with path components separated by a slash
     * character (not with backslash!). The path (the name) must <em>not</em> begin with slash;
     * the path is always relative to the "template root directory".
     * Then, the <tt>..</tt> and <tt>.</tt> path metaelements will be resolved.
     * For example, if the name is <tt>a/../b/./c.ftl</tt>, then it will be
     * simplified to <tt>b/c.ftl</tt>. The rules regarding this are same as with conventional
     * UN*X paths. The path must not reach outside the template root directory, that is,
     * it can't be something like <tt>"../templates/my.ftl"</tt> (not even if the pervious path
     * happens to be equivalent with <tt>"/my.ftl"</tt>).
     * Further, the path is allowed to contain at most
     * one path element whose name is <tt>*</tt> (asterisk). This path metaelement triggers the
     * <i>acquisition mechanism</i>. If the template is not found in
     * the location described by the concatenation of the path left to the
     * asterisk (called base path) and the part to the right of the asterisk
     * (called resource path), the cache will attempt to remove the rightmost
     * path component from the base path ("go up one directory") and concatenate
     * that with the resource path. The process is repeated until either a
     * template is found, or the base path is completely exhausted.
     *
     * @param locale the requested locale of the template. Can't be null.
     * Assuming you have specified <code>en_US</code> as the locale and
     * <code>myTemplate.html</code> as the name of the template, the cache will
     * first try to retrieve <code>myTemplate_en_US.html</code>, then
     * <code>myTemplate.html_en.html</code>, and finally
     * <code>myTemplate.html</code>.
     *
     * @param encoding the character encoding used to interpret the template
     * source bytes. Can't be null.
     *
     * @param parse if true, the loaded template is parsed and interpreted
     * as a regular FreeMarker template. If false, the loaded template is
     * treated as an unparsed block of text.
     *
     * @return the loaded template, or null if the template is not found.
     */
    public Template getTemplate(String name, Locale locale, String encoding, boolean parse)
    throws IOException
    {
        boolean debug = logger.isDebugEnabled();
        String debugName = debug ? name + "[" + locale + "," + encoding + (parse ? ",parsed] " : ",unparsed] ") : null;

        if (name == null) {
            throw new IllegalArgumentException("Argument \"name\" can't be null");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Argument \"locale\" can't be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument \"encoding\" can't be null");
        }
        name = normalizeName(name);
        if(name == null) {
            return null;
        }
        TemplateKey tk = new TemplateKey(name, locale, encoding, parse);

        synchronized (storage)
        {
            CachedTemplate cachedTemplate = (CachedTemplate)storage.get(tk);
            long now = System.currentTimeMillis();
            long lastModified = -1L;
            Object newlyFoundSource = null;
            try
            {
                if (cachedTemplate != null)
                {
                    // If we're within the refresh delay, return the cached copy
                    if (now - cachedTemplate.lastChecked < delay)
                    {
                        if(debug)
                        {
                            logger.debug(debugName + "cached copy not yet stale; using cached.");
                        }
                        return cachedTemplate.template;
                    }
                    // Else, update the last-checked flag
                    cachedTemplate.lastChecked = now;

                    // Find the template source
                    newlyFoundSource = findTemplateSource(name, locale);

                    // Template source was removed
                    if (newlyFoundSource == null)
                    {
                        if(debug)
                        {
                            logger.debug(debugName + "no source found (removing from cache if it was cached).");
                        }
                        storage.remove(tk);
                        return null;
                    }

                    // If the source didn't change and its last modified date
                    // also didn't change, return the cached version.
                    lastModified = loader.getLastModified(newlyFoundSource);
                    boolean lastModifiedNotChanged = lastModified == cachedTemplate.lastModified;
                    boolean sourceEquals = newlyFoundSource.equals(cachedTemplate.source);
                    if(lastModifiedNotChanged && sourceEquals)
                    {
                        if(debug)
                        {
                            logger.debug(debugName + "using cached since " + 
                                    newlyFoundSource + " didn't change.");
                        }
                        cachedTemplate.lastChecked = now;
                        return cachedTemplate.template;
                    }
                    else
                    {
                        if(debug && !sourceEquals)
                        {
                            logger.debug("Updating source, info for cause: " + 
                                "sourceEquals=" + sourceEquals + 
                                ", newlyFoundSource=" + newlyFoundSource + 
                                ", cachedTemplate.source=" + cachedTemplate.source);
                        }
                        if(debug && !lastModifiedNotChanged)
                        {
                            logger.debug("Updating source, info for cause: " + 
                                "lastModifiedNotChanged=" + lastModifiedNotChanged + 
                                ", cache lastModified=" + cachedTemplate.lastModified + 
                                " != file lastModified=" + lastModified);
                        }
                        // Update the source
                        cachedTemplate.source = newlyFoundSource;
                    }
                }
                else
                {
                    if(debug) 
                    {
                        logger.debug("Could not find template in cache, "
                            + "creating new one; id=[" + 
                            tk.name + "[" + tk.locale + "," + tk.encoding + 
                            (tk.parse ? ",parsed] " : ",unparsed] ") + "]");
                    }
                    
                    // Construct a new CachedTemplate entry. Note we set the
                    // cachedTemplate.lastModified to Long.MIN_VALUE. This is
                    // a flag that signs it has to be explicitly queried later on.
                    newlyFoundSource = findTemplateSource(name, locale);
                    if (newlyFoundSource == null)
                    {
                        return null;
                    }
                    cachedTemplate = new CachedTemplate();
                    cachedTemplate.source = newlyFoundSource;
                    cachedTemplate.lastChecked = now;
                    cachedTemplate.lastModified = lastModified = Long.MIN_VALUE;
                    storage.put(tk, cachedTemplate);
                }

                if(debug)
                {
                    logger.debug("Compiling FreeMarker template " + 
                        debugName + " from " + newlyFoundSource);
                }
                // If we get here, then we need to (re)load the template
                Object source = cachedTemplate.source;
                cachedTemplate.template =
                    loadTemplate(name, locale, encoding, parse, source);
                cachedTemplate.lastModified =
                    lastModified == Long.MIN_VALUE
                        ? loader.getLastModified(source)
                        : lastModified;
                return cachedTemplate.template;
            }
            finally
            {
                if(newlyFoundSource != null)
                {
                    loader.closeTemplateSource(newlyFoundSource);
                }
            }
        }
    }

    private Template loadTemplate(String name, Locale locale, String encoding,
                                   boolean parse, Object source)
    throws IOException
    {
        Template template;
        Reader reader = loader.getReader(source, encoding);
        try
        {
            if(parse)
            {
                try {
                    template = new Template(name, reader, config, encoding);
                }
                catch (Template.WrongEncodingException wee) {
                    encoding = wee.specifiedEncoding;
                    reader = loader.getReader(source, encoding);
                    template = new Template(name, reader, config, encoding);
                }
                template.setLocale(locale);
            }
            else
            {
                // Read the contents into a StringWriter, then construct a single-textblock
                // template from it.
                StringWriter sw = new StringWriter();
                char[] buf = new char[4096];
                for(;;)
                {
                    int charsRead = reader.read(buf);
                    if (charsRead > 0)
                    {
                        sw.write(buf, 0, charsRead);
                    }
                    else if(charsRead == -1)
                    {
                        break;
                    }
                }
                template = Template.getPlainTextTemplate(name, sw.toString(), config);
                template.setLocale(locale);
            }
            template.setEncoding(encoding);
        }
        finally
        {
            reader.close();
        }
        return template;
    }

    /**
     * Gets the delay in milliseconds between checking for newer versions of a
     * template source.
     * @return the current value of the delay
     */
    public synchronized long getDelay()
    {
        return delay;
    }

    /**
     * Sets the delay in milliseconds between checking for newer versions of a
     * template sources.
     * @param delay the new value of the delay
     */
    public synchronized void setDelay(long delay)
    {
        this.delay = delay;
    }

    /**
     * Returns if localized template lookup is enabled or not.
     */
    public synchronized boolean getLocalizedLookup()
    {
        return localizedLookup;
    }

    /**
     * Setis if localized template lookup is enabled or not.
     */
    public synchronized void setLocalizedLookup(boolean localizedLookup)
    {
        this.localizedLookup = localizedLookup;
    }

    /**
     * Removes all entries from the cache, forcing reloading of templates
     * on subsequent {@link #getTemplate(String, Locale, String, boolean)}
     * calls.
     */
    public void clear()
    {
        synchronized (storage) {
            storage.clear();
        }
    }

    private Object findTemplateSource(String name, Locale locale)
    throws
    	IOException
    {
        if (localizedLookup) {
            int lastDot = name.lastIndexOf('.');
            String prefix = lastDot == -1 ? name : name.substring(0, lastDot);
            String suffix = lastDot == -1 ? "" : name.substring(lastDot);
            String localeName = LOCALE_SEPARATOR + locale.toString();
            StringBuffer buf = new StringBuffer(name.length() + localeName.length());
            buf.append(prefix);
            for (;;)
            {
                buf.setLength(prefix.length());
                String path = buf.append(localeName).append(suffix).toString();
                Object templateSource = acquireTemplateSource(path);
                if (templateSource != null)
                {
                    return templateSource;
                }
                int lastUnderscore = localeName.lastIndexOf('_');
                if (lastUnderscore == -1)
                    break;
                localeName = localeName.substring(0, lastUnderscore);
            }
            return null;
        }
        else
        {
            return acquireTemplateSource(name);
        }
    }

    private Object acquireTemplateSource(String path) throws IOException
    {
        int asterisk = path.indexOf(ASTERISK);
        // Shortcut in case there is no acquisition
        if(asterisk == -1)
        {
            return loader.findTemplateSource(path);
        }
        StringTokenizer tok = new StringTokenizer(path, "/");
        int lastAsterisk = -1;
        List tokpath = new ArrayList();
        while(tok.hasMoreTokens())
        {
            String pathToken = tok.nextToken();
            if(pathToken.equals(ASTERISKSTR))
            {
                if(lastAsterisk != -1)
                {
                    tokpath.remove(lastAsterisk);
                }
                lastAsterisk = tokpath.size();
            }
            tokpath.add(pathToken);
        }
        String basePath = concatPath(tokpath, 0, lastAsterisk);
        String resourcePath = concatPath(tokpath, lastAsterisk + 1, tokpath.size());

        StringBuffer buf = new StringBuffer(path.length()).append(basePath);
        int l = basePath.length();
        boolean debug = logger.isDebugEnabled();
        for(;;)
        {
            String fullPath = buf.append(resourcePath).toString();
            if(debug)
            {
                logger.debug("Trying to find template source " + fullPath);
            }
            Object templateSource = loader.findTemplateSource(fullPath);
            if(templateSource != null)
            {
                return templateSource;
            }
            if(l == 0)
            {
                return null;
            }
            l = basePath.lastIndexOf(SLASH, l - 2) + 1;
            buf.setLength(l);
        }
    }

    private String concatPath(List path, int from, int to)
    {
        StringBuffer buf = new StringBuffer((to - from) * 16);
        for(int i = from; i < to; ++i)
        {
            buf.append(path.get(i)).append('/');
        }
        return buf.toString();
    }
    
    private static String normalizeName(String name) {
        if (name.indexOf("://") >0) {
            return name;
        }
        for(;;) {
            int parentDirPathLoc = name.indexOf(PARENT_DIR_PATH);
            if(parentDirPathLoc == 0) {
                // If it starts with /../, then it reaches outside the template
                // root.
                return null;
            }
            if(parentDirPathLoc == -1) {
                if(name.startsWith(PARENT_DIR_PATH_PREFIX)) {
                    // Another attempt to reach out of template root.
                    return null;
                }
                break;
            }
            int previousSlashLoc = name.lastIndexOf(SLASH, parentDirPathLoc - 1);
            name = name.substring(0, previousSlashLoc + 1) +
                   name.substring(parentDirPathLoc + PARENT_DIR_PATH.length());
        }
        for(;;) {
            int currentDirPathLoc = name.indexOf(CURRENT_DIR_PATH);
            if(currentDirPathLoc == -1) {
                if(name.startsWith(CURRENT_DIR_PATH_PREFIX)) {
                    name = name.substring(CURRENT_DIR_PATH_PREFIX.length());
                }
                break;
            }
            name = name.substring(0, currentDirPathLoc) +
                   name.substring(currentDirPathLoc + CURRENT_DIR_PATH.length() - 1);
        }
        // Editing can leave us with a leading slash; strip it.
        if(name.length() > 1 && name.charAt(0) == SLASH) {
            name = name.substring(1);
        }
        return name;
    }

    /**
     * This class holds a (name, locale) pair and is used as the key in
     * the cached templates map.
     */
    private static final class TemplateKey
    {
        private final String name;
        private final Locale locale;
        private final String encoding;
        private final boolean parse;

        TemplateKey(String name, Locale locale, String encoding, boolean parse)
        {
            this.name = name;
            this.locale = locale;
            this.encoding = encoding;
            this.parse = parse;
        }

        public boolean equals(Object o)
        {
            if (o instanceof TemplateKey)
            {
                TemplateKey tk = (TemplateKey)o;
                return
                    parse == tk.parse &&
                    name.equals(tk.name) &&
                    locale.equals(tk.locale) &&
                    encoding.equals(tk.encoding);
            }
            return false;
        }

        public int hashCode()
        {
            return
                name.hashCode() ^
                locale.hashCode() ^
                encoding.hashCode() ^
                (parse ? Boolean.FALSE : Boolean.TRUE).hashCode();
        }
    }

    /**
     * This class holds the cached template and associated information
     * (the source object, and the last-checked and last-modified timestamps).
     * It is used as the value in the cached templates map.
     */
    private static final class CachedTemplate
    {
        Template template;
        Object source;
        long lastChecked;
        long lastModified;
    }
}
