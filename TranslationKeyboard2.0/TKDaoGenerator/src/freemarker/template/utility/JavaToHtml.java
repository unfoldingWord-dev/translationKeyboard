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

package freemarker.template.utility;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import java.io.*;
import java.util.Map;
import freemarker.template.*;

/**
 * <p>A FreeMarker transformer that performs syntax coloring of Java source
 * files, and outputs the results in HTML. The HTML is HTML 3.2 compliant,
 * though later transitional DTDs should have no problems with this code.</p>
 *
 * <p>The transformation uses the Jakarta ORO regular expression library,
 * available from the <a href="http://jakarta.apache.org/oro/"
 * target="_top">Jakarta Apache</a> web site.</p>
 *
 * @author  Nicholas Cull
 * @version $Id: JavaToHtml.java,v 1.34 2003/02/25 00:28:17 revusky Exp $
 */
public class JavaToHtml implements TemplateTransformModel {
    private Perl5Util cMatcher = null;

    public Writer getWriter(final Writer out,
                            Map args)
    {
        // This isn't thread safe, but it's no problem if multiple instances get
        // created, so we don't want to pay the synchronization price
        if(cMatcher == null)
        {
            cMatcher = new Perl5Util();
        }

        final StringBuffer buf = new StringBuffer();
        return new Writer() {
            public void write(char cbuf[], int off, int len) {
                buf.append(cbuf, off, len);
            }

            public void flush() throws IOException {
                out.flush();
            }

            public void close() throws IOException {
                StringReader sr = new StringReader(buf.toString());
                StringWriter sw = new StringWriter();
                transform(sr, sw);
                out.write(sw.toString());
            }
        };
    }


    /**
     * Perform all Java language transformations.
     */
    private void doJavaTransformation( String textBuffer, Writer out ) throws IOException {
        textBuffer = cMatcher.substitute( "s/&/&amp;/g", textBuffer );
        textBuffer = cMatcher.substitute( "s/</&lt;/g", textBuffer );
        textBuffer = cMatcher.substitute( "s/>/&gt;/g", textBuffer );
        out.write("<pre>");
        doBlockComment( textBuffer, out );
        out.write( "</pre>" );
    }

    /**
     * Format Java block comments.
     */
    private void doBlockComment(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\/\\*(?:.*?)\\*\\//s",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doDoubleQuotes( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#008000\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doDoubleQuotes( aInput, out );
    }

    /**
     * Format double-quoted strings.
     */
    private void doDoubleQuotes(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\"(?:\\\\.|[^\"])*\"/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            String aMatch = cResult.toString();
            doSingleQuotes( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#808080\">" );
            out.write( aMatch );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doSingleQuotes( aInput, out );
    }

    /**
     * Format Java single quotes (ie. character data).
     */
    private void doSingleQuotes(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/'(?:\\\\.|[^\'])*'/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doLineComment( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#808080\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doLineComment( aInput, out );
    }

    /**
     * Format Java line comments (// syntax).
     */
    private void doLineComment(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\/\\/.*?$/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doPrimitiveTypes( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#008000\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doPrimitiveTypes( aInput, out );
    }

    /**
     * Format Java primitive types.
     */
    private void doPrimitiveTypes(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\b(?:boolean|byte|char|double|float|int|" +
                               "long|short|void)\\b/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doKeywords( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#ff8000\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doKeywords( aInput, out );
    }

    /**
     * Format Java keywords.
     */
    private void doKeywords(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\b(?:abstract|break|case|catch|" +
                               "class|continue|default|do|else|extends|" +
                               "false|final|finally|for|goto|" +
                               "if|implements|import|instanceof|interface|" +
                               "native|new|null|package|private|protected|public|" +
                               "return|static|strictfp|super|switch|synchronized|" +
                               "this|throw|throws|transient|true|try|" +
                               "volatile|while" +
                               ")\\b/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doJavaPackages( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#0000ff\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doJavaPackages( aInput, out );
    }

    /**
     * Format three of the main Java classes: Java.lang, Java.io, and Java.util.
     */
    private void doJavaPackages(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\b(?:AbstractCollection|AbstractList|AbstractMap|" +
                               "AbstractSequentialList|AbstractSet|ArrayList|Arrays|" +
                               "BitSet|Boolean|BufferedInputStream|" +
                               "BufferedOutputStream|BufferedReader|BufferedWriter|Byte|" +
                               "ByteArrayInputStream|ByteArrayOutputStream|Calendar|Character|" +
                               "Class|CharArrayReader|CharArrayWriter|ClassLoader|Cloneable|" +
                               "Collection|Collections|Comparable|Comparator|" +
                               "Compiler|DataInput|DataInputStream|DataOutput|DataOutputStream|" +
                               "Date|Dictionary|Double|Enumeration|EventListener|EventObject|" +
                               "Externalizable|File|FileDescriptor|FileFilter|FilenameFilter|" +
                               "FileInputStream|FileOutputStream|FilePermission|FileReader|" +
                               "FileWriter|FilterInputStream|FilterOutputStream|FilterReader|" +
                               "FilterWriter|Float|GregorianCalendar|HashMap|HashSet|Hashtable|" +
                               "InheritableThreadLocal|InputStream|InputStreamReader|Integer|" +
                               "Iterator|LineNumberInputStream|LineNumberReader|LinkedList|" +
                               "List|ListIterator|ListResourceBundle|Locale|Long|Map|Math|" +
                               "Number|Object|ObjectInput|ObjectInputStream|ObjectInputValidation|" +
                               "ObjectOutput|ObjectOutputStream|ObjectStreamClass|" +
                               "ObjectStreamConstants|ObjectStreamField|Observable|Observer|" +
                               "OutputStream|OutputStreamWriter|Package|PipedInputStream|" +
                               "PipedOutputStream|PipedReader|PipedWriter|PrintStream|" +
                               "PrintWriter|Process|Properties|PropertyPermission|" +
                               "PropertyResourceBundle|PushbackInputStream|PushbackReader|" +
                               "Random|RandomAccessFile|Reader|ResourceBundle|Runnable|" +
                               "Runtime|RuntimePermission|SecurityManager|SequenceInputStream|" +
                               "Serializable|SerializablePermission|Set|Short|SimpleTimeZone|" +
                               "SortedMap|SortedSet|Stack|StreamTokenizer|StrictMath|String|" +
                               "StringBuffer|StringBufferInputStream|StringReader|" +
                               "StringTokenizer|StringWriter|System|Thread|ThreadGroup|" +
                               "ThreadLocal|Timer|TimerTask|TimeZone|TreeMap|TreeSet|" +
                               "Vector|Void|WeakHashMap|Writer" +
                               ")\\b/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doDigits( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#804040\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doDigits( aInput, out );
    }

    /**
     * Format digits in Java source code.
     */
    private void doDigits(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\b\\d+\\b/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doOperator( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#ff0000\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doOperator( aInput, out );
    }

    /**
     * Format Java operators.
     */
    private void doOperator(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/(?:\\w+\\s*\\(|[\\+\\*\\^\\$\\-\\{\\}\\[\\]" +
                               "\\=\\.\\(\\)\\,\\:\\/\\;]|&(lt|gt|amp);" +
                               ")+/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doExceptions( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<b>" );
            out.write( cResult.toString() );
            out.write( "</b>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doExceptions( aInput, out );
    }

    /**
     * Format Java exception classes.
     */
    private void doExceptions(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\w*(Error|Exception|Throwable)\\b/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            out.write( aInput.substring( 0, cResult.beginOffset(0) ));
            out.write( "<font color=\"#ff0000\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        out.write( aInput );
    }

    /**
     * Performs a transformation/filter on FreeMarker output.
     *
     * @param source the input to be transformed
     * @param output the destination of the transformation
     */
    public void transform(Reader source, Writer output)
    throws IOException
    {
        StringBuffer cText = new StringBuffer();
        char cBuffer[] = new char[ 1024 ];
        int nSize = source.read( cBuffer );
        while ( nSize >= 0 ) {
            cText.append( cBuffer, 0, nSize );
            nSize = source.read( cBuffer );
        }
        doJavaTransformation( cText.toString(), output );
    }
}
