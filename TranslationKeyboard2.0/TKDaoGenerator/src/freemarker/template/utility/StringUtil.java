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

import java.util.*;
import freemarker.template.ParseException;

/**
 *  Some text related utilities.
 *
 *  @version $Id: StringUtil.java,v 1.23.2.2 2004/03/11 23:15:16 ddekany Exp $
 */
public class StringUtil {
    private static final char[] ESCAPES = createEscapes();

    /*
     *  For better performance most methods are folded down. Don't you scream... :)
     */

    /**
     *  HTML encoding (does not convert line breaks).
     *  Replaces all '&gt;' '&lt;' '&amp;' and '"' with entity reference
     */
    public static String HTMLEnc(String s) {
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '<' || c == '>' || c == '&' || c == '"') {
                StringBuffer b =
                        new StringBuffer(s.substring(0, i));
                switch (c) {
                    case '<': b.append("&lt;"); break;
                    case '>': b.append("&gt;"); break;
                    case '&': b.append("&amp;"); break;
                    case '"': b.append("&quot;"); break;
                }
                i++;
                int next = i;
                while (i < ln) {
                    c = s.charAt(i);
                    if (c == '<' || c == '>' || c == '&' || c == '"') {
                        b.append(s.substring(next, i));
                        switch (c) {
                            case '<': b.append("&lt;"); break;
                            case '>': b.append("&gt;"); break;
                            case '&': b.append("&amp;"); break;
                            case '"': b.append("&quot;"); break;
                        }
                        next = i + 1;
                    }
                    i++;
                }
                if (next < ln) b.append(s.substring(next));
                s = b.toString();
                break;
            } // if c ==
        } // for
        return s;
    }

    /**
     *  XML Encoding.
     *  Replaces all '&gt;' '&lt;' '&amp;', "'" and '"' with entity reference
     */
    public static String XMLEnc(String s) {
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '<' || c == '>' || c == '&' || c == '"' || c == '\'') {
                StringBuffer b =
                        new StringBuffer(s.substring(0, i));
                switch (c) {
                    case '<': b.append("&lt;"); break;
                    case '>': b.append("&gt;"); break;
                    case '&': b.append("&amp;"); break;
                    case '"': b.append("&quot;"); break;
                    case '\'': b.append("&apos;"); break;
                }
                i++;
                int next = i;
                while (i < ln) {
                    c = s.charAt(i);
                    if (c == '<' || c == '>' || c == '&' || c == '"' || c == '\'') {
                        b.append(s.substring(next, i));
                        switch (c) {
                            case '<': b.append("&lt;"); break;
                            case '>': b.append("&gt;"); break;
                            case '&': b.append("&amp;"); break;
                            case '"': b.append("&quot;"); break;
                            case '\'': b.append("&apos;"); break;
                        }
                        next = i + 1;
                    }
                    i++;
                }
                if (next < ln) b.append(s.substring(next));
                s = b.toString();
                break;
            } // if c ==
        } // for
        return s;
    }

    /**
     *  XML encoding without replacing apostrophes.
     *  @see #XMLEnc(String)
     */
    public static String XMLEncNA(String s) {
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '<' || c == '>' || c == '&' || c == '"') {
                StringBuffer b =
                        new StringBuffer(s.substring(0, i));
                switch (c) {
                    case '<': b.append("&lt;"); break;
                    case '>': b.append("&gt;"); break;
                    case '&': b.append("&amp;"); break;
                    case '"': b.append("&quot;"); break;
                }
                i++;
                int next = i;
                while (i < ln) {
                    c = s.charAt(i);
                    if (c == '<' || c == '>' || c == '&' || c == '"') {
                        b.append(s.substring(next, i));
                        switch (c) {
                            case '<': b.append("&lt;"); break;
                            case '>': b.append("&gt;"); break;
                            case '&': b.append("&amp;"); break;
                            case '"': b.append("&quot;"); break;
                        }
                        next = i + 1;
                    }
                    i++;
                }
                if (next < ln) b.append(s.substring(next));
                s = b.toString();
                break;
            } // if c ==
        } // for
        return s;
    }
    
    /**
     *  XML encoding without replacing apostrophes and quotation marks and greater-than signs.
     *  @see #XMLEnc(String)
     */
    public static String XMLEncNQG(String s) {
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '<' || c == '&') {
                StringBuffer b =
                        new StringBuffer(s.substring(0, i));
                switch (c) {
                    case '<': b.append("&lt;"); break;
                    case '&': b.append("&amp;"); break;
                }
                i++;
                int next = i;
                while (i < ln) {
                    c = s.charAt(i);
                    if (c == '<' || c == '&') {
                        b.append(s.substring(next, i));
                        switch (c) {
                            case '<': b.append("&lt;"); break;
                            case '&': b.append("&amp;"); break;
                        }
                        next = i + 1;
                    }
                    i++;
                }
                if (next < ln) b.append(s.substring(next));
                s = b.toString();
                break;
            } // if c ==
        } // for
        return s;
    }
    
    /**
     *  Rich Text Format encoding (does not replace line breaks).
     *  Escapes all '\' '{' '}' and '"'
     */
    public static String RTFEnc(String s) {
        int ln = s.length();
        for (int i = 0; i < ln; i++) {
            char c = s.charAt(i);
            if (c == '\\' || c == '{' || c == '}') {
                StringBuffer b =
                        new StringBuffer(s.substring(0, i));
                switch (c) {
                    case '\\': b.append("\\\\"); break;
                    case '{': b.append("\\{"); break;
                    case '}': b.append("\\}"); break;
                }
                i++;
                int next = i;
                while (i < ln) {
                    c = s.charAt(i);
                    if (c == '\\' || c == '{' || c == '}') {
                        b.append(s.substring(next, i));
                        switch (c) {
                            case '\\': b.append("\\\\"); break;
                            case '{': b.append("\\{"); break;
                            case '}': b.append("\\}"); break;
                        }
                        next = i + 1;
                    }
                    i++;
                }
                if (next < ln) b.append(s.substring(next));
                s = b.toString();
                break;
            } // if c ==
        } // for
        return s;
    }

    private static char[] createEscapes()
    {
        char[] escapes = new char['\\' + 1];
        for(int i = 0; i < 32; ++i)
        {
            escapes[i] = (char) i;
        }
        escapes['\\'] = '\\';
        escapes['\''] = '\'';
        escapes['"'] = '"';
        escapes['<'] = 'l';
        escapes['>'] = 'g';
        escapes['&'] = 'a';
        escapes['\b'] = 'b';
        escapes['\t'] = 't';
        escapes['\n'] = 'n';
        escapes['\f'] = 'f';
        escapes['\r'] = 'r';
        return escapes;
    }

    public static String FTLStringLiteralEnc(String s)
    {
        StringBuffer buf = new StringBuffer(s);
        int l = s.length();
        int el = ESCAPES.length;
        for(int i = 0; i < l; i++)
        {
            char c = buf.charAt(i);
            if(c < el)
            {
                char escape = ESCAPES[c];
                switch(c)
                {
                    case 0:
                    {
                        // do nothing
                        break;
                    }
                    case 1:
                    {
                        // TODO: hex encoding for characters below 0x20
                        // that have no other escape representation
                        break;
                    }
                    default:
                    {
                        buf.setCharAt(i++, '\\');
                        buf.insert(i,  escape);
                        ++l;
                    }
                }
            }
        }
        return buf.length() == s.length() ? s : buf.toString();
    }

    /**
     * FTL string literal decoding.
     *
     * \\, \", \', \n, \t, \r, \b and \f will be replaced according to
     * Java rules. In additional, to help the previewing of SGML templates, it
     * knows \g, \l and \a which are replaced with &gt;, > and &amp; respectively.
     * \x works as hexadecimal character code escape. The character
     * codes are interpreted according to UCS basic plane (Unicode).
     * "f\x006Fo", "f\x06Fo" and "f\x6Fo" will be "foo".
     * "f\x006F123" will be "foo123" as the maximum number of digits is 4.
     *
     * All other \X (where X is any character not mentioned above or End-of-string)
     * will cause a ParseException.
     *
     * @param s String literal <em>without</em> the surrounding quotation marks
     * @return String with all escape sequences resolved
     * @throws ParseException if there string contains illegal escapes
     */
    public static String FTLStringLiteralDec(String s) throws ParseException {

        int idx = s.indexOf('\\');
        if (idx == -1)
            return s;

        int lidx = s.length() - 1;
        int bidx = 0;
        StringBuffer buf = new StringBuffer(lidx);
        do {
            buf.append(s.substring(bidx, idx));
            if (idx >= lidx) {
                throw new ParseException("The last character of string literal is backslash", 0,0);
            }
            char c = s.charAt(idx + 1);
            switch (c) {
                case '"':
                    buf.append('"');
                    bidx = idx + 2;
                    break;
                case '\'':
                    buf.append('\'');
                    bidx = idx + 2;
                    break;
                case '\\':
                    buf.append('\\');
                    bidx = idx + 2;
                    break;
                case 'n':
                    buf.append('\n');
                    bidx = idx + 2;
                    break;
                case 'r':
                    buf.append('\r');
                    bidx = idx + 2;
                    break;
                case 't':
                    buf.append('\t');
                    bidx = idx + 2;
                    break;
                case 'f':
                    buf.append('\f');
                    bidx = idx + 2;
                    break;
                case 'b':
                    buf.append('\b');
                    bidx = idx + 2;
                    break;
                case 'g':
                    buf.append('>');
                    bidx = idx + 2;
                    break;
                case 'l':
                    buf.append('<');
                    bidx = idx + 2;
                    break;
                case 'a':
                    buf.append('&');
                    bidx = idx + 2;
                    break;
                case 'x': {
                    idx += 2;
                    int x = idx;
                    int y = 0;
                    int z = lidx > idx + 3 ? idx + 3 : lidx;
                    while (idx <= z) {
                        char b = s.charAt(idx);
                        if (b >= '0' && b <= '9') {
                            y <<= 4;
                            y += b - '0';
                        } else if (b >= 'a' && b <= 'f') {
                            y <<= 4;
                            y += b - 'a' + 10;
                        } else if (b >= 'A' && b <= 'F') {
                            y <<= 4;
                            y += b - 'A' + 10;
                        } else {
                            break;
                        }
                        idx++;
                    }
                    if (x < idx) {
                        buf.append((char) y);
                    } else {
                        throw new ParseException("Invalid \\x escape in a string literal",0,0);
                    }
                    bidx = idx;
                    break;
                }
                default:
                    throw new ParseException("Invalid escape sequence (\\" + c + ") in a string literal",0,0);
            }
            idx = s.indexOf('\\', bidx);
        } while (idx != -1);
        buf.append(s.substring(bidx));

        return buf.toString();
    }

    public static Locale deduceLocale(String input) {
       Locale locale = Locale.getDefault();
       if (input.charAt(0) == '"') input = input.substring(1, input.length() -1);
       StringTokenizer st = new StringTokenizer(input, ",_ ");
       String lang = "", country = "";
       if (st.hasMoreTokens()) {
          lang = st.nextToken();
       }
       if (st.hasMoreTokens()) {
          country = st.nextToken();
       }
       if (!st.hasMoreTokens()) {
          locale = new Locale(lang, country);
       }
       else {
          locale = new Locale(lang, country, st.nextToken());
       }
       return locale;
    }

    public static String capitalize(String s) {
        StringTokenizer st = new StringTokenizer(s, " \t\r\n", true);
        StringBuffer buf = new StringBuffer(s.length());
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            buf.append(tok.substring(0, 1).toUpperCase());
            buf.append(tok.substring(1).toLowerCase());
        }
        return buf.toString();
    }

    public static boolean getYesNo(String s) {
        if (s.startsWith("\"")) {
            s = s.substring(1, s.length() -1);

        }
        if (s.equalsIgnoreCase("n")
                || s.equalsIgnoreCase("no")
                || s.equalsIgnoreCase("f")
                || s.equalsIgnoreCase("false")) {
            return false;
        }
        else if  (s.equalsIgnoreCase("y")
                || s.equalsIgnoreCase("yes")
                || s.equalsIgnoreCase("t")
                || s.equalsIgnoreCase("true")) {
            return true;
        }
        throw new IllegalArgumentException("Illegal boolean value: " + s);
    }

    /**
     * Splits a string at the specified character.
     */
    public static String[] split(String s, char c) {
        int i, b, e;
        int cnt;
        String res[];
        int ln = s.length();

        i = 0;
        cnt = 1;
        while ((i = s.indexOf(c, i)) != -1) {
            cnt++;
            i++;
        }
        res = new String[cnt];

        i = 0;
        b = 0;
        while (b <= ln) {
            e = s.indexOf(c, b);
            if (e == -1) e = ln;
            res[i++] = s.substring(b, e);
            b = e + 1;
        }
        return res;
    }

    /**
     * Splits a string at the specified string.
     */
    public static String[] split(String s, String sep) {
        int i, b, e;
        int cnt;
        String res[];
        int ln = s.length();
        int sln = sep.length();

        if (sln == 0) throw new IllegalArgumentException(
                "The separator string has 0 length");

        i = 0;
        cnt = 1;
        while ((i = s.indexOf(sep, i)) != -1) {
            cnt++;
            i += sln;
        }
        res = new String[cnt];

        i = 0;
        b = 0;
        while (b <= ln) {
            e = s.indexOf(sep, b);
            if (e == -1) e = ln;
            res[i++] = s.substring(b, e);
            b = e + sln;
        }
        return res;
    }

    /**
     * Replaces all occurrences of a sub-string in a string.
     * @param text The string where it will replace <code>oldsub</code> with
     *     <code>newsub</code>.
     * @return String The string after the replacements.
     */
    public static String replace(String text, String oldsub, String newsub) {
        int e = text.indexOf(oldsub);
        if (e == -1) return text;
        int b = 0;
        int tln = text.length();
        int oln = oldsub.length();
        StringBuffer buf = new StringBuffer(tln + 16);
        do {
            buf.append(text.substring(b, e));
            buf.append(newsub);
            b = e + oln;
            e = text.indexOf(oldsub, b);
        } while (e != -1);
        buf.append(text.substring(b));
        return buf.toString();
    }

    /**
     * Removes the line-break from the end of the string.
     */
    public static String chomp(String s) {
        if (s.endsWith("\r\n")) return s.substring(0, s.length() - 2);
        if (s.endsWith("\r") || s.endsWith("\n"))
                return s.substring(0, s.length() - 1);
        return s;
    }

    /**
     *  Quotes string as Java Language string literal.
     */
    public static String jQuote(String s) {
        String s2;
        int ln = s.length();
        int next = 0;
        int i = 0;
        StringBuffer b = new StringBuffer(ln + 3);
        b.append("\"");
        while (i < ln) {
            char c = s.charAt(i);
            if (c == '\\' || c == '"' || c < 0x20) {
                b.append(s.substring(next, i));
                switch (c) {
                case '\\':
                    b.append("\\\\"); break;
                case '"':
                    b.append("\\\""); break;
                case '\n':
                    b.append("\\n"); break;
                case '\r':
                    b.append("\\r"); break;
                case '\t':
                    b.append("\\t"); break;
                case '\b':
                    b.append("\\b"); break;
                case '\f':
                    b.append("\\f"); break;
                default:
                    b.append("\\u0000");
                    int x = b.length();
                    s2 = Integer.toHexString(c);
                    b.replace(x - s2.length(), x, s2);
                }
                next = i + 1;
            }
            i++;
        }
        if (next < ln) b.append(s.substring(next));
        b.append("\"");
        return b.toString();
    }

    /**
     * Escapes a string so it could be inserted into a Java Language string literal.
     */
    public static String javaStringEnc(String s) {
        String s2;
        int ln = s.length();
        int next = 0;
        int i = 0;
        StringBuffer b = new StringBuffer(ln + 3);
        while (i < ln) {
            char c = s.charAt(i);
            if (c == '\\' || c == '"' || c < 0x20) {
                b.append(s.substring(next, i));
                switch (c) {
                case '\\':
                    b.append("\\\\"); break;
                case '"':
                    b.append("\\\""); break;
                case '\n':
                    b.append("\\n"); break;
                case '\r':
                    b.append("\\r"); break;
                case '\t':
                    b.append("\\t"); break;
                case '\b':
                    b.append("\\b"); break;
                case '\f':
                    b.append("\\f"); break;
                default:
                    b.append("\\u0000");
                    int x = b.length();
                    s2 = Integer.toHexString(c);
                    b.replace(x - s2.length(), x, s2);
                }
                next = i + 1;
            }
            i++;
        }
        if (next < ln) {
            b.append(s.substring(next));
        }
        return b.toString();
    }

    /**
     * Escapes a string so it could be inserted into a JavaScript Language string literal.
     */
    public static String javaScriptStringEnc(String s) {
        int ln = s.length();
        int next = 0;
        int i = 0;
        StringBuffer b = new StringBuffer(ln + 3);
        while (i < ln) {
            char c = s.charAt(i);
            if (c == '\\' || c == '"'  || c == '\'' || c < 0x20) {
                b.append(s.substring(next, i));
                switch (c) {
                case '\\':
                    b.append("\\\\"); break;
                case '"':
                    b.append("\\\""); break;
                case '\'':
                    b.append("\\\'"); break;
                case '\n':
                    b.append("\\n"); break;
                case '\r':
                    b.append("\\r"); break;
                case '\t':
                    b.append("\\t"); break;
                case '\b':
                    b.append("\\b"); break;
                case '\f':
                    b.append("\\f"); break;
                default:
                    b.append("\\x");
                    int x = c / 0x10;
                    b.append((char) (x < 0xA ? x + '0' : x - 0xA + 'A'));
                    x = c & 0xF;
                    b.append((char) (x < 0xA ? x + '0' : x - 0xA + 'A'));
                }
                next = i + 1;
            }
            i++;
        }
        if (next < ln) {
            b.append(s.substring(next));
        }
        return b.toString();
    }
    
}
