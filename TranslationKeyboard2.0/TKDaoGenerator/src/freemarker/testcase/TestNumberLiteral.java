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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import freemarker.template.SimpleHash;
import freemarker.template.SimpleSequence;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.testcase.models.SimpleTestMethod;

/** Test class for testing FreeMarker's number literal syntax.
 *
 * @version $Id: TestNumberLiteral.java,v 1.28 2003/04/02 11:25:54 szegedia Exp $
 */
public class TestNumberLiteral extends AbstractTestCase {

    /** Create a new TestNumberLiteral test case */
    public TestNumberLiteral( String aTestname ) {
        super( aTestname );
    }

    /**
     * Set up the test case prior to running.
     */
    public void setUp() {
        setUpFiles( "test-numberliteral.html" );

        // Make a template data model.
        SimpleHash cModel1 = new SimpleHash();

        cModel1.put("1", "one");
        cModel1.put("12", "twelve");
        cModel1.put( "2one", "two-one" );
        cModel1.put( "one2", "one-two" );

        SimpleSequence cModel2 = new SimpleSequence();

        cModel2.add( "zero" );
        cModel2.add( "one" );
        cModel2.add( "two" );
        cModel2.add( "three" );
        cModel2.add( "four" );
        cModel2.add( "five" );
        cModel2.add( "six" );
        cModel2.add( "seven" );
        cModel2.add( "eight" );
        cModel2.add( "nine" );
        cModel2.add( "tenth" );
        cModel2.add( "eleven" );
        cModel2.add( "twelve" );

        root.put("message", "Hello, world!");
        root.put("foo", "bar");
        root.put("one", "1");
        root.put("1", "one");
        root.put("12", "twelve");
        root.put("2one", "two-one");
        root.put("one2", "one-two");
        root.put("hash", cModel1);
        root.put("list", cModel2);
        root.put("call", new SimpleTestMethod() );
    }

    /**
     * Abstract method for performing the test. The implementing class should
     * indicate clearly whether the test has passed. A pass result should be
     * written to screen. A fail result should also generate information about
     * the failure.
     */
    public void runTest() throws TemplateException, IOException {
        StringWriter sw = new StringWriter();
        config.setStrictSyntaxMode(true);
        Locale prevLocale = config.getLocale();
        config.setLocale(Locale.FRANCE);
        try {
            Template template = config.getTemplate("test-numberliteral.html");
            template.process(root, sw);
        }
        finally {
            config.setStrictSyntaxMode(false);
            config.setLocale(prevLocale);
        }
        showTestResults( referenceText, sw.toString() );
    }

    /** Bootstrap for the self-test code.
     */
    public static void main( String[] argc ) throws Exception {
        AbstractTestCase cTest = new TestNumberLiteral( "test-numberliteral.html" );
        cTest.run();
    }
}
