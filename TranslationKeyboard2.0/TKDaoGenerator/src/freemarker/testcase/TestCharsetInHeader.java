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

package freemarker.testcase;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/** Test class for testing FreeMarker's &lt;include&gt; tag, and the two
 * AbstractTemplate implementations that can be included from it.
 *
 * @version $Id: TestCharsetInHeader.java,v 1.7 2003/04/02 11:25:53 szegedia Exp $
 */
public class TestCharsetInHeader extends AbstractTestCase {

    public TestCharsetInHeader(String aTestname) {
        super( aTestname );
    }

    /**
     * Set up the test case prior to running.
     */
    public void setUp() {
        setUpFiles( "test-charsetinheader.html" );
    }

    public void runTest() throws TemplateException, IOException {
        Template template;
        StringWriter sw = new StringWriter();
        String prevEncoding = config.getDefaultEncoding();
        try {
            File dir = new File(getTestcasePath(), "template");
            Configuration config = Configuration.getDefaultConfiguration();
            config.setDirectoryForTemplateLoading(dir);
            config.clearEncodingMap();
            config.setDefaultEncoding("ISO-8859-1");
            template = config.getTemplate("test-charsetinheader.html", "ISO-8859-5");
        } 
        catch( IOException e ) {
            fail( e.getMessage() );
            return;
        }
        finally {
            config.setDefaultEncoding(prevEncoding);
        }

        // Process the template.
        template.process(root, sw);
        showTestResults( referenceText, sw.toString() );
    }

    /** Bootstrap for the self-test code.
     */
    public static void main( String[] argc ) throws Exception {
        AbstractTestCase cTest = new TestCharsetInHeader( "test-charsetinheader.html" );
        cTest.run();
    }
}