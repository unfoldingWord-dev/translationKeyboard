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

import junit.framework.Test;
import junit.framework.TestSuite;
import freemarker.testcase.servlets.TestJspTaglibs;

/**
 * Test suite for FreeMarker. The suite conforms to interface expected by
 * <a href="http://junit.sourceforge.net/" target="_top">JUnit</a>.
 *
 * @version $Id: FreeMarkerTestSuite.java,v 1.71 2003/04/02 11:35:43 szegedia Exp $
 */
public class FreeMarkerTestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();

        suite.addTest( new TestComment( "commentTest" ));
        suite.addTest( new TestVariables( "variableTest" ));
        suite.addTest( new TestBoolean( "booleanTest" ));
        suite.addTest( new TestMultiModels( "multiModelTest" ));
        suite.addTest( new TestNoParse( "noParseTest" ));
        suite.addTest( new TestTransform( "transformTest" ));
        suite.addTest( new TestCompress( "compressionTest" ));
        suite.addTest( new TestLastCharacter( "lastCharacterTest" ));
        suite.addTest( new TestListIterators( "listIteratorTest" ));
        suite.addTest( new TestListLiteral( "listLiteralTest" ));
        suite.addTest( new TestHashLiteral( "hashLiteralTest" ));
        suite.addTest( new TestNumberLiteral( "numberLiteralTest" ));
        suite.addTest( new TestNewlines1( "newlineTest1" ));
        suite.addTest( new TestNewlines2( "newlineTest2" ));
        suite.addTest( new TestComparisons( "comparisonTest" ));
        suite.addTest( new TestJavaTransform( "javaToHtmlTest" ));
        suite.addTest( new TestFreeMarkerTransform( "freeMarkerToHtmlTest" ));
        suite.addTest( new TestIdentifier( "testIdentifier" ));
        suite.addTest( new TestPrecedence( "testPrecedence" ));
        suite.addTest( new TestFunction( "testFunction" ));
        suite.addTest( new TestSwitchCase( "testSwitchCase" ));
        suite.addTest( new TestExtendedList( "testExtendedList" ));
        suite.addTest( new TestBeans( "testBeans" ));
        suite.addTest( new TestBeanMaps( "testBeanMaps" ));
        suite.addTest( new TestArithmetic("testArithmetic"));
        suite.addTest( new TestEncodingBuiltins("encodingBuiltins"));
        suite.addTest( new TestStringLiteral("stringLiteral"));
        suite.addTest( new TestXmlModel("xmlModel"));
        suite.addTest( new TestInterpret("interpret"));
        suite.addTest( new TestStringBuiltins( "stringBuiltins" ));
        suite.addTest( new TestStringBuiltins2( "stringBuiltins2" ));
        suite.addTest( new TestTypeBuiltins( "typeBuiltins" ));
        suite.addTest( new TestNested1( "nested1" ));
        suite.addTest( new TestCharsetInHeader( "charsetInHeader" ));
        suite.addTest( new TestStrictInHeader( "strictInHeader" ));
        suite.addTest( new TestWsStripInHeader( "wsstripInHeader" ));
        suite.addTest( new TestImport( "import" ));
        suite.addTest( new TestRoot( "testRoot" ));
        suite.addTest( new TestVarLayers("testVarLayers"));
        suite.addTest( new TestDateFormat( "testDateFormat" ));
        suite.addTest( new TestJspTaglibs( "testJspTaglibs" ));
        suite.addTest( new TestEscapes( "testEscapes" ));
        suite.addTest( new TestLoopvar( "testLoopvar" ));
        suite.addTest( new TestWsTrim( "testWsTrim" ));
        suite.addTest( new TestStringBiMethods( "testStringBiMethods" ));
        suite.addTest( new TestSequenceBuiltins( "sequenceBuiltins" ));

        // This could be problematic, since it assumes a particular ordering
        // for keysets and values from a hash, which are ultimately
        // dependent on the implementation of the underlying JVM class library.
        //suite.addTest( new TestExtendedHash( "testExtendedHash" ));

        // These tests depend on the properties file being set up correctly
        suite.addTest( new TestLocalization( "testLocale" ));
        suite.addTest( new TestInclude( "testInclude" ));

//        This test is screwy because the line numbers in the
//        stack trace are always changing.
//        suite.addTest( new TestInclude2( "testInclude2" ));

        // These tests are problematic in an automatic suite --
        // commenting out for now.
        //suite.addTest( new TestException( "exceptionTest" ));
        //suite.addTest( new TestExecModel( "execTest" ));

        return suite;
    }

    public static void main (String[] args) {
//        junit.textui.TestRunner.run(FreeMarkerTestSuite.class);
        junit.swingui.TestRunner.run (FreeMarkerTestSuite.class);
//        junit.awtui.TestRunner.run (FreeMarkerTestSuite.class);
    }
}
