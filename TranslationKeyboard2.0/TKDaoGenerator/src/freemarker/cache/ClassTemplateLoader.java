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

import java.net.URL;

/**
 * A {@link TemplateLoader} that uses streams reachable through 
 * {@link Class#getResourceAsStream(String)} as its source of templates.
 * @author Attila Szegedi, szegedia at freemail dot hu
 * @version $Id: ClassTemplateLoader.java,v 1.8 2003/01/12 23:40:11 revusky Exp $
 */
public class ClassTemplateLoader extends URLTemplateLoader
{
    private final Class loaderClass;
    private final String path;
    
    /**
     * Creates a resource template cache that will use its own class to
     * load the resources. It will use the base path of <code>"/"</code>.
     */
    public ClassTemplateLoader()
    {
        this(ClassTemplateLoader.class);
    }

    /**
     * Creates a resource template cache that will use the specified class
     * to load the resources. It will use the base path of <code>""</code>
     * meaning templates will be resolved relative to the class location.
     * @param loaderClass the class whose
     * {@link Class#getResource(String)} will be used to load the templates.
     */
    public ClassTemplateLoader(Class loaderClass)
    {
        this(loaderClass, "");
    }

    /**
     * Creates a resource template cache that will use the specified class
     * to load the resources. It will use the specified base path. A path
     * that doesn't start with a slash (/) is a path relative to the path
     * of the current class. A path that starts with a slash is an absolute
     * path starting from the classpath root. Path components should be
     * separated by forward slashes independently of the separator character
     * used by the underlying operating system.
     * @param loaderClass the class whose
     * {@link Class#getResource(String)} will be used to load the templates.
     * @param path the base path to template resources.
     */
    public ClassTemplateLoader(Class loaderClass, String path)
    {
        if(loaderClass == null)
        {
            throw new IllegalArgumentException("loaderClass == null");
        }
        if(path == null)
        {
            throw new IllegalArgumentException("path == null");
        }
        
        this.loaderClass = loaderClass;
        this.path = canonicalizePrefix(path);
    }

    protected URL getURL(String name)
    {
        return loaderClass.getResource(path + name);
    }
}
