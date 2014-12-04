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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

import freemarker.template.TemplateTransformModel;

/**
 * <p>A FreeMarker transformer that performs syntax coloring of FreeMarker
 * source files, and outputs the results in HTML. The HTML is HTML 3.2 compliant,
 * though later transitional DTDs should have no problems with this code.</p>
 *
 * <p>The transformation uses the Jakarta ORO regular expression library,
 * available from the <a href="http://jakarta.apache.org/oro/"
 * target="_top">Jakarta Apache</a> web site.</p>
 *
 * @author  Nicholas Cull
 * @version $Id: FreeMarkerToHtml.java,v 1.38 2003/02/25 00:28:16 revusky Exp $
 */
public class FreeMarkerToHtml implements TemplateTransformModel {

    private Perl5Util cMatcher = null;


    public Writer getWriter(final Writer out,
                            Map args)
    {
        // This isn't thread-safe, but it's no problem if multiple instances get
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
                StringWriter sw = new StringWriter();
                doFreeMarkerTransformation(buf.toString(), sw);
                out.write(sw.toString());
            }
        };
    }

    /**
     * Perform all FreeMarker template transformations.
     */
    private void doFreeMarkerTransformation(String textBuffer, Writer out)
        throws IOException
    {
        textBuffer = cMatcher.substitute( "s/</&lt;/g", textBuffer );
        textBuffer = cMatcher.substitute( "s/>/&gt;/g", textBuffer );
//        textBuffer = cMatcher.substitute( "s/&/&amp;/g", textBuffer );
        out.write( "<pre>" );
        doBlockComment( textBuffer, out);
        out.write( "</pre>" );
    }

    /**
     * Format FreeMarker block comments.
     */
    private void doBlockComment(String aInput, Writer out)
        throws IOException
    {
        while (cMatcher.match( "m/&lt;comment\\b(?:.*?)&lt;\\/comment\\s*&gt;/s",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doFreeMarkerTags( aInput.substring( 0, cResult.beginOffset(0) ), out );
            out.write( "<font color=\"#008000\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doFreeMarkerTags( aInput, out );
    }

    /**
     * Format FreeMarker start/standalone tags.
     */
    private void doFreeMarkerTags(String aInput, Writer out)
        throws IOException
    {
        while (cMatcher.match( "m/(&lt;(?:list|if|else|elseif|switch|case|break|default|" +
                               "assign|include|function|call|compress|comment|noparse|foreach|" +
                               "transform))\\b(.*?)(&gt;)/s",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doFreeMarkerEndTags( aInput.substring( 0, cResult.beginOffset(0) ), out );
            out.write( "<font color=\"#0000ff\">" );
            out.write( cResult.group( 1 ) );
            out.write( "</font>" );
            doDoubleQuotes( cResult.group( 2 ), out );
            out.write( "<font color=\"#0000ff\">" );
            out.write( cResult.group( 3 ) );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doFreeMarkerEndTags( aInput, out );
    }

    /**
     * Format FreeMarker end tags.
     */
    private void doFreeMarkerEndTags(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/(&lt;\\/(?:list|if|switch|" +
                               "function|compress|comment|noparse|foreach|" +
                               "transform))\\s*(&gt;)/s",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doFreeMarkerVariableInstruction( aInput.substring( 0, cResult.beginOffset(0) ), out );
            out.write( "<font color=\"#0000ff\">" );
            out.write( cResult.group( 0 ) );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doFreeMarkerVariableInstruction( aInput, out );
    }

    /**
     * Format FreeMarker variable instructions.
     */
    private void doFreeMarkerVariableInstruction(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/([\\$\\#]\\{)(.*?)(\\})/s",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            out.write( aInput.substring( 0, cResult.beginOffset(0) ));
            out.write( "<font color=\"#0000ff\">" );
            out.write( cResult.group( 1 ) );
            out.write( "</font>" );
            doDoubleQuotes( cResult.group( 2 ), out );
            out.write( "<font color=\"#0000ff\">" );
            out.write( cResult.group( 3 ) );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        out.write( aInput );
    }

    /**
     * Format FreeMarker double-quoted strings.
     */
    private void doDoubleQuotes(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\"(?:\\\\.|[^\"])*\"/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            String aMatch = cResult.toString();
            doDigits( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<font color=\"#808080\">" );
            out.write( aMatch );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doDigits( aInput, out );
    }

    /**
     * Format FreeMarker number literals.
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
     * Format FreeMarker operators.
     */
    private void doOperator(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/(?:[\\+\\[\\]\\{\\}\\=\\.\\(\\)\\;\\!]|&amp;)+/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doSpecialWords( aInput.substring( 0, cResult.beginOffset(0) ), out);
            out.write( "<b>" );
            out.write( cResult.toString() );
            out.write( "</b>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doSpecialWords( aInput, out );
    }

    /**
     * Format FreeMarker special words, used in "foreach" and "list"
     * instructions.
     */
    private void doSpecialWords(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\b(?:as|in|type|gt|lt|gte|lte)\\b/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            doOtherWords( aInput.substring( 0, cResult.beginOffset(0) ), out );
            out.write( "<font color=\"#ff8000\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        doOtherWords( aInput, out );
    }

    /**
     * Format FreeMarker variables.
     */
    private void doOtherWords(String aInput, Writer out)
    throws IOException
    {
        while (cMatcher.match( "m/\\w+/m",  aInput)) {
            MatchResult cResult = cMatcher.getMatch();
            out.write( aInput.substring( 0, cResult.beginOffset(0) ));
            out.write( "<font color=\"#804040\">" );
            out.write( cResult.toString() );
            out.write( "</font>" );
            aInput = aInput.substring( cResult.endOffset(0) );
        }
        out.write( aInput );
    }
}
