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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ClassUtil;

/**
 * <p>Abstract class used to implement test cases for FreeMarker.  This
 * class is an abstract subclass of the <code>TestCase</code> class
 * provided by <a href="http://junit.sourceforge.net/"
 * target="_top">JUnit</a>.  All FreeMarker testcases are subclassed off
 * this class.
 *
 * <p>This class offers functionality to its subclasses to ease testing where
 * a template is read in, and then written out with a specific data model.
 * Passing the test means that the resulting page matches a given reference
 * text.  This class also supports the creation of those reference texts.<P>
 *
 * <p>To write a new test by subclassing this class, you'll want to do the
 * following:
 *
 * <ul>
 *
 *  <li> Create a template which your test case will use as input, and store
 *       it in the <tt>templates</tt> directory located immediately below
 *       this directory.  Say it is called <tt>test-foo.html</tt>.</li>
 *
 * <li> Create a subclass of <tt>AbstractTestCase</tt>, and in the
 *       <tt>setUp</tt> method, call <tt>setUpFile("test-foo.html")</tt>.
 *       This will initialize a couple of protected instance variables: 
 *      <tt>referenceText</tt> will contain the contents of
 *       file "reference/test-foo.html"  and
 *       <tt>filename</tt> will contain the string "test-foo.html".
 *
 *       <p>You can also set up a data model, as per usual JUnit practice.</li>
 *
 *  <li> You may optionally override the runTest method in your subclass.</li>
 *
 *  <li> In addition, if there is a difference, the actual output produced
 *       by the test will be written out in the current directory (wherever
 *       the test was called from), in this case, as "test-foo.html".
 *       This makes it possible to examine the test result and see what
 *       was actually output.  It also makes it possible to copy that
 *       into the reference/ directory.</li>
 *
 * </ul>
 *
 * @version $Id: AbstractTestCase.java,v 1.31 2003/04/02 11:25:53 szegedia Exp $
 */
public abstract class AbstractTestCase extends junit.framework.TestCase {

    static Configuration config;    
    private static final int TEST_BUFFER_SIZE = 1024;
    
    static
    {
        try
        {
            config = Configuration.getDefaultConfiguration();
            config.setClassForTemplateLoading(AbstractTestCase.class, "/freemarker/testcase/template");
            config.setDefaultEncoding("UTF-8");
            Class log4jbc = ClassUtil.forName("org.apache.log4j.BasicConfigurator");
            log4jbc.getMethod("configure", null).invoke(null, null);
        }
        catch(Exception e)
        {
        }
    }
    protected String referenceText;
    protected String filename;
    protected SimpleHash root = new SimpleHash();

    /**
     * Creates new AbstractTestCase, with a filename from which to populate
     * reference text and template text.
     */
    public AbstractTestCase( String aTestName ) {
        super( aTestName );
    }
    
    public void runTest() throws TemplateException, IOException {
        StringWriter sw = new StringWriter();
        Template template = config.getTemplate(filename);
        template.process(root, sw);
        showTestResults( referenceText, sw.toString() );
    }

    /**
     * Sets up the reference and template files to be used for the test.
     * This would normally be called from the setUp() method in the JUnit
     * framework.
     *
     * @param aFilename the filename to be used for retrieving the reference
     * text and the template
     */
    protected void setUpFiles( String aFilename ) {
        try {
            referenceText = getReferenceText( aFilename );
        } catch( IOException e ) {
            referenceText = "";
        }
        filename = aFilename;
    }

    /**
     * Reads text from a file. The file is obtained relative to the
     * implementing class.
     *
     * @param aFilename the filename to read from
     */
    protected String getTextFromFile( String aFilename ) throws IOException {
        Class thisClass = this.getClass();
        InputStream cStream;
        Reader cReader;
        StringBuffer cBuffer = new StringBuffer();
        char[] aBuffer = new char[ TEST_BUFFER_SIZE ];
        int nLength;

        cStream = thisClass.getResourceAsStream( aFilename );

        if( cStream == null ) {
            throw new IOException( "Could not find stream " + aFilename );
        }
        cReader = new InputStreamReader( cStream, "UTF-8" );
//        cReader = new InputStreamReader( cStream, "ISO-8859-1" );

        nLength = cReader.read( aBuffer );

        while( nLength > 0 ) {
            cBuffer.append( aBuffer, 0, nLength );
            nLength = cReader.read( aBuffer );
        }
        return cBuffer.toString();
    }

    /**
     * Gets the reference text for the implementing test class.
     */
    protected String getReferenceText( String aFilename ) throws IOException {

        return getTextFromFile( "reference/" + aFilename );
    }

    /**
     * Gets the template text for the implementing test class.
     */
    protected String getTemplateText( String aFilename ) throws IOException {

        return getTextFromFile( "template/" + aFilename );
    }

    /**
     * Performs the test on the output text to indicate whether the text is
     * identical to the reference text.
     */
    protected void isTextIdentical( String aReference, String aOutput ) throws TestCaseException {
        BufferedReader r1 = new BufferedReader(new StringReader(aReference));
        BufferedReader r2 = new BufferedReader(new StringReader(aOutput));
        int line = 0;
        try
        {
            for(;;)
            {
                String l1 = r1.readLine();
                String l2 = r2.readLine();
                if(l1 == null)
                {
                    if(l2 == null)
                    {
                        return;
                    }
                    throw new TestCaseException("Output text is " + (aOutput.length() - aReference.length())  + " characters longer than reference text.");
                }
                if(l2 == null)
                {
                    throw new TestCaseException("Output text is " + (aReference.length() - aOutput.length())  + " characters shorter than reference text.");
                }
                ++line; 
                if(!l1.equals(l2)) {
                    int l = Math.min(l1.length(), l2.length());
                    for(int i = 0; i < l; ++i) {
                        if(l1.charAt(i) != l2.charAt(i)) {
                            throw new TestCaseException( "Difference encountered at line " +
                                line + ", character " + (i + 1) + "." );
                        }
                    }
                    throw new TestCaseException( "Difference encountered at line " +
                        line + ", character " + l + "." );
                }
            }
        }
        catch(IOException e)
        {
            // Cannot happen
            throw new Error(); 
        }
    }

    /**
     * Verify that the output of a test is identical to the reference text.
     * If they are identical, say nothing (in the JUnit tradition).  If
     * they differ, output the first line number and character where they
     * differ.
     *
     * @param aReference the reference text
     * @param aOutput the output from the test
     */
    protected void showTestResults( String aReference, String aOutput ) {

        try {
            isTextIdentical( referenceText, aOutput );
        } catch( TestCaseException error ) {
            try {
                writeText( filename, aOutput );
            } catch( IOException writeException ) {
                fail( writeException.getMessage() );
            }
            assertTrue( error.getMessage(), false );
        }
    }

    /**
     * Writes text to a given filename. Useful when we want to set up the
     * reference file for future tests to compare against.
     */
    protected void writeText( String aFilename, String aText ) throws IOException {
        FileOutputStream cStream = new FileOutputStream( aFilename );
        OutputStreamWriter cWriter = new OutputStreamWriter( cStream, "UTF-8" );

        cWriter.write( aText );
        cWriter.flush();
        cWriter.close();
        cStream.close();
    }

    /**
     * Retrieve the root path of the FreeMarker distribution from a
     * properties file. This is used to set the root of the FreeMarker
     * cache, which in turn is used to find the example source code.
     */
    protected String getTestcasePath() {
        java.net.URL url = getClass().getResource("AbstractTestCase.class");
        return new File(url.getFile()).getParent();
    }

}
