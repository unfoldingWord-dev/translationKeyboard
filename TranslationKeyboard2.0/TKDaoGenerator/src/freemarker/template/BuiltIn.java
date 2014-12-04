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

import java.text.Collator;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import freemarker.template.utility.StringUtil;


/**
 * The ? operator used to get the
 * functionality of built-in unary operators
 * @author <a href="mailto:jon@revusky.com">Jonathan Revusky</a>
 */
abstract class BuiltIn extends Expression implements Cloneable {
    Expression target;
    String key;

    static final HashMap builtins = new HashMap();

    static {
        // These are the only ones we have now.
        // We throw a parse exception if it's not one of these.
        builtins.put("byte", new byteBI());
        builtins.put("cap_first", new cap_firstBI());
        builtins.put("capitalize", new capitalizeBI());
        builtins.put("chop_linebreak", new chop_linebreakBI());
        builtins.put("date", new dateBI(TemplateDateModel.DATE));
        builtins.put("datetime", new dateBI(TemplateDateModel.DATETIME));
        builtins.put("default", new defaultBI());
        builtins.put("double", new doubleBI());
        builtins.put("ends_with", new ends_withBI());
        builtins.put("exists", new existsBI());
        builtins.put("first", new firstBI());
        builtins.put("float", new floatBI());
        builtins.put("has_content", new has_contentBI());
        builtins.put("html", new htmlBI());
        builtins.put("if_exists", new if_existsBI());
        builtins.put("index_of", new index_ofBI());
        builtins.put("int", new intBI());
        builtins.put("interpret", new Interpret());
        builtins.put("is_string", new is_stringBI());
        builtins.put("is_number", new is_numberBI());
        builtins.put("is_boolean", new is_booleanBI());
        builtins.put("is_date", new is_dateBI());
        builtins.put("is_method", new is_methodBI());
        builtins.put("is_macro", new is_macroBI());
        builtins.put("is_transform", new is_transformBI());
        builtins.put("is_hash", new is_hashBI());
        builtins.put("is_hash_ex", new is_hash_exBI());
        builtins.put("is_sequence", new is_sequenceBI());
        builtins.put("is_collection", new is_collectionBI());
        builtins.put("is_enumerable", new is_enumerableBI());
        builtins.put("is_indexable", new is_indexableBI());
        builtins.put("is_directive", new is_directiveBI());
        builtins.put("j_string", new j_stringBI());
        builtins.put("js_string", new js_stringBI());
        builtins.put("keys", new keysBI());
        builtins.put("last", new lastBI());
        builtins.put("last_index_of", new last_index_ofBI());
        builtins.put("length", new lengthBI());
        builtins.put("long", new longBI());
        builtins.put("lower_case", new lower_caseBI());
        builtins.put("namespace", new namespaceBI());
        builtins.put("new", new NewBI());
        builtins.put("number", new numberBI());
        builtins.put("reverse", new reverseBI());
        builtins.put("rtf", new rtfBI());
        builtins.put("replace", new replaceBI());
        builtins.put("short", new shortBI());
        builtins.put("size", new sizeBI());
        builtins.put("sort", new sortBI());
        builtins.put("sort_by", new sort_byBI());
        builtins.put("split", new splitBI());
        builtins.put("starts_with", new starts_withBI());
        builtins.put("string", new stringBI());
        builtins.put("time", new dateBI(TemplateDateModel.TIME));
        builtins.put("trim", new trimBI());
        builtins.put("uncap_first", new uncap_firstBI());
        builtins.put("upper_case", new upper_caseBI());
        builtins.put("values", new valuesBI());
        builtins.put("web_safe", builtins.get("html"));  // deprecated; use ?html instead
        builtins.put("word_list", new word_listBI());
        builtins.put("xml", new xmlBI());
    }

    static BuiltIn newBuiltIn(Expression target, String key) throws ParseException {
        BuiltIn bi = (BuiltIn) builtins.get(key);
        if (bi == null) {
            StringBuffer buf = new StringBuffer("Expecting one of: ");
            for (Iterator it= builtins.keySet().iterator(); it.hasNext();) {
                if (it.hasNext()) {
                    buf.append(" ");
                } else {
                    buf.append( " or ");
                }
                buf.append(it.next());
                if (it.hasNext()) {
                    buf.append(",\n");
                }
            }
            throw new ParseException(buf.toString(), target);
        }
        try {
            bi = (BuiltIn) bi.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
        bi.target = target;
        bi.key = key;
        return bi;
    }

    public String getCanonicalForm() {
        return target.getCanonicalForm() + "?" + key;
    }

    boolean isLiteral() {
        return false; // be on the safe side.
    }

    Expression _deepClone(String name, Expression subst) {
    	try {
	    	BuiltIn clone = (BuiltIn)clone();
	    	clone.target = target.deepClone(name, subst);
	    	return clone;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    abstract static class SequenceBuiltIn extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            if (!(model instanceof TemplateSequenceModel)) {
                throw invalidTypeException(model, target, env, "sequence");
            }
            return calculateResult((TemplateSequenceModel) model);
        }
        abstract TemplateModel calculateResult(TemplateSequenceModel tsm)
        throws
            TemplateModelException;
    }

    static class firstBI extends SequenceBuiltIn {
        TemplateModel calculateResult(TemplateSequenceModel tsm)
        throws
            TemplateModelException
        {
            if (tsm.size() == 0) {
                return null;
            }
            return tsm.get(0);
        }
    }

    static class lastBI extends SequenceBuiltIn {
        TemplateModel calculateResult(TemplateSequenceModel tsm)
        throws
            TemplateModelException
        {
            if (tsm.size() == 0) {
                return null;
            }
            return tsm.get(tsm.size() -1);
        }
    }

    static class reverseBI extends SequenceBuiltIn {
        TemplateModel calculateResult(TemplateSequenceModel tsm) {
            if (tsm instanceof ReverseSequence) {
                return ((ReverseSequence) tsm).seq;
            } else {
                return new ReverseSequence(tsm);
            }
        }

        private static class ReverseSequence implements TemplateSequenceModel
        {
            private final TemplateSequenceModel seq;

            ReverseSequence(TemplateSequenceModel seq)
            {
                this.seq = seq;
            }

            public int size() throws TemplateModelException
            {
                return seq.size();
            }

            public TemplateModel get(int index) throws TemplateModelException
            {
                return seq.get(seq.size() - 1 - index);
            }
        }
    }

    static class sortBI extends SequenceBuiltIn {
        static final int KEY_TYPE_STRING = 1;
        static final int KEY_TYPE_NUMBER = 2;
        
        TemplateModel calculateResult(TemplateSequenceModel seq)
                throws TemplateModelException {
            return sort(seq, null);
        }
        
        static String startErrorMessage(String key) {
            return (key == null ? "?sort" : "?sort_by(...)") + " failed: ";
        }
        
        static TemplateSequenceModel sort(TemplateSequenceModel seq, String key)
                throws TemplateModelException {
            Object obj;
            int i;

            int ln = seq.size();
            List res = new ArrayList(ln);
            if (ln != 0) {
                Object item;
                item = seq.get(0);
                if (key != null) {
                    if (!(item instanceof TemplateHashModel)) {
                        throw new TemplateModelException(
                                startErrorMessage(key)
                                + "You can't use ?sort_by when the "
                                + "sequence items are not hashes.");
                    }
                    item = ((TemplateHashModel) item).get(key);
                    if (item == null) {
                        throw new TemplateModelException(
                                startErrorMessage(key)
                                + "The key " + StringUtil.jQuote(key)
                                + " was not found in the sequence item.");
                    }
                }

                int keyType;
                if (item instanceof TemplateScalarModel) {
                    keyType = KEY_TYPE_STRING;
                } else if (item instanceof TemplateNumberModel) {
                    keyType = KEY_TYPE_NUMBER;
                } else {
                    throw new TemplateModelException(
                            startErrorMessage(key)
                            + "Values used for sorting must be numbers or strings.");
                }

                if (key == null) {
                    if (keyType == KEY_TYPE_STRING) {
                        for (i = 0; i < ln; i++) {
                            item = seq.get(i);
                            if (!(item instanceof TemplateScalarModel)) {
                                throw new TemplateModelException(
                                        startErrorMessage(key)
                                        + "All values in the sequence must be "
                                        + "strings, because the first value "
                                        + "was a string. "
                                        + "The value at index " + i
                                        + " is not string.");
                            }
                            res.add(new KVP(
                                    ((TemplateScalarModel) item).getAsString(),
                                    item));
                        }
                    } else if (keyType == KEY_TYPE_NUMBER) {
                        for (i = 0; i < ln; i++) {
                            item = seq.get(i);
                            if (!(item instanceof TemplateNumberModel)) {
                                throw new TemplateModelException(
                                        startErrorMessage(key)
                                        + "All values in the sequence must be "
                                        + "numbers, because the first value "
                                        + "was a number. "
                                        + "The value at index " + i
                                        + " is not number.");
                            }
                            res.add(new KVP(
                                    ((TemplateNumberModel) item).getAsNumber(),
                                    item));
                        }
                    } else {
                        throw new RuntimeException("Bug: Bad key type");
                    }
                } else {
                    for (i = 0; i < ln; i++) {
                        item = seq.get(i);
                        if (!(item instanceof TemplateHashModel)) {
                            throw new TemplateModelException(
                                    startErrorMessage(key)
                                    + "All values in the sequence must be "
                                    + "hashes, because you use a key to "
                                    + "access the value used for the sorting. "
                                    + "The value at index " + i
                                    + " is not a hash.");
                        }
                        obj = ((TemplateHashModel) item).get(key);
                        if (obj == null) {
                            throw new TemplateModelException(
                                    startErrorMessage(key)
                                    + "The key " + StringUtil.jQuote(key)
                                    + " was not found in the value at "
                                    + "index " + i);
                        }
                        if (keyType == KEY_TYPE_STRING) {
                            if (!(obj instanceof TemplateScalarModel)) {
                                throw new TemplateModelException(
                                        startErrorMessage(key)
                                        + "All key values in the sequence must be "
                                        + "strings, because the first key "
                                        + "value was a string. The key value at "
                                        + "index " + i + " is not a string.");
                            }
                            res.add(new KVP(
                                    ((TemplateScalarModel) obj).getAsString(),
                                    item));
                        } else if (keyType == KEY_TYPE_NUMBER) {
                            if (!(obj instanceof TemplateNumberModel)) {
                                throw new TemplateModelException(
                                        startErrorMessage(key)
                                        + "All key values in the sequence must be "
                                        + "numbers, because the first key "
                                        + "value was a number. The key value at "
                                        + "index " + i + " is not a number.");
                            }
                            res.add(new KVP(
                                    ((TemplateNumberModel) obj).getAsNumber(),
                                    item));
                        } else {
                            throw new RuntimeException("Bug: Bad key type");
                        }
                    }
                }

                Comparator cmprtr;
                if (keyType == KEY_TYPE_STRING) {
                    cmprtr = new LexicalKVPComparator(
                            Environment.getCurrentEnvironment().getCollator());
                } else if (keyType == KEY_TYPE_NUMBER) {
                    cmprtr = new NumericalKVPComparator(
                            Environment.getCurrentEnvironment()
                                    .getArithmeticEngine());
                } else {
                    throw new RuntimeException("Bug: Bad key type");
                }

                try {
                    Collections.sort(res, cmprtr);
                } catch (ClassCastException exc) {
                    throw new TemplateModelException(
                            startErrorMessage(key)
                            + "Unexpected error while sorting:" + exc, exc);
                }

                for (i = 0; i < ln; i++) {
                    res.set(i, ((KVP) res.get(i)).value);
                }
            } // if ln != 0

            return new TemplateModelListSequence(res);
        }

        private static class KVP {
            private KVP(Object key, Object value) {
                this.key = key;
                this.value = value;
            }

            private Object key;
            private Object value;
        }

        private static class NumericalKVPComparator implements Comparator {
            private ArithmeticEngine ae;

            private NumericalKVPComparator(ArithmeticEngine ae) {
                this.ae = ae;
            }

            public int compare(Object arg0, Object arg1) {
                try {
                    return ae.compareNumbers(
                            (Number) ((KVP) arg0).key,
                            (Number) ((KVP) arg1).key);
                } catch (TemplateException e) {
                    throw new ClassCastException(
                        "Failed to compare numbers: " + e);
                }
            }
        }

        private static class LexicalKVPComparator implements Comparator {
            private Collator collator;

            LexicalKVPComparator(Collator collator) {
                this.collator = collator;
            }

            public int compare(Object arg0, Object arg1) {
                return collator.compare(
                        ((KVP) arg0).key, ((KVP) arg1).key);
            }
        }
    }

    static class sort_byBI extends sortBI {
        TemplateModel calculateResult(TemplateSequenceModel seq)
        throws
            TemplateModelException
        {
            return new BIMethod(seq);
        }
        
        static class BIMethod implements TemplateMethodModelEx {
            TemplateSequenceModel seq;
            
            BIMethod(TemplateSequenceModel seq) {
                this.seq = seq;
            }
            
            public TemplateModel exec(List params)
                    throws TemplateModelException {
                if (params.size() != 1) {
                    throw new TemplateModelException(
                            "?sort_by(key) needs exactly 1 argument.");
                }
                Object obj = params.get(0);
                if (!(obj instanceof TemplateScalarModel)) {
                    throw new TemplateModelException(
                            "The argument to sort_by(key) must be a string");
                }
                String key = ((TemplateScalarModel) obj).getAsString();
                if (key == null) {
                    throw new TemplateModelException(
                            "The argument to sort_by(key) is null");
                }
                
                return sort(seq, key); 
            }
        }
    }

    abstract static class NumberBuiltIn extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            return calculateResult(EvaluationUtil.getNumber(model, target, env), model);
        }
        abstract TemplateModel calculateResult(Number num, TemplateModel model);
    }

    static class byteBI extends NumberBuiltIn {
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Byte) {
                return model;
            }
            return new SimpleNumber(new Byte(num.byteValue()));
        }
    }

    static class shortBI extends NumberBuiltIn {
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Short) {
                return model;
            }
            return new SimpleNumber(new Short(num.shortValue()));
        }
    }

    static class intBI extends NumberBuiltIn {
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Integer) {
                return model;
            }
            return new SimpleNumber(num.intValue());
        }
    }

    static class longBI extends NumberBuiltIn {
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Long) {
                return model;
            }
            return new SimpleNumber(num.longValue());
        }
    }

    static class floatBI extends NumberBuiltIn {
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Float) {
                return model;
            }
            return new SimpleNumber(num.floatValue());
        }
    }

    static class doubleBI extends NumberBuiltIn {
        TemplateModel calculateResult(Number num, TemplateModel model) {
            if (num instanceof Double) {
                return model;
            }
            return new SimpleNumber(num.doubleValue());
        }
    }

    abstract static class StringBuiltIn extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
        throws TemplateException
        {
            return calculateResult(target.getStringValue(env), env);
        }
        abstract TemplateModel calculateResult(String s, Environment env);
    }

    static class lengthBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
        throws TemplateException
        {
            return new SimpleNumber(target.getStringValue(env).length());
        }
    }

    static class capitalizeBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.capitalize(s));
        }
    }

    static class chop_linebreakBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.chomp(s));
        }
    }

    static class j_stringBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.javaStringEnc(s));
        }
    }

    static class js_stringBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.javaScriptStringEnc(s));
        }
    }
    
    static class cap_firstBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            int i = 0;
            int ln = s.length();
            while (i < ln  &&  Character.isWhitespace(s.charAt(i))) {
                i++;
            }
            if (i < ln) {
                StringBuffer b = new StringBuffer(s);
                b.setCharAt(i, Character.toUpperCase(s.charAt(i)));
                s = b.toString();
            }
            return new SimpleScalar(s);
        }
    }

    static class uncap_firstBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            int i = 0;
            int ln = s.length();
            while (i < ln  &&  Character.isWhitespace(s.charAt(i))) {
                i++;
            }
            if (i < ln) {
                StringBuffer b = new StringBuffer(s);
                b.setCharAt(i, Character.toLowerCase(s.charAt(i)));
                s = b.toString();
            }
            return new SimpleScalar(s);
        }
    }

    static class upper_caseBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env)
        {
            return new SimpleScalar(s.toUpperCase(env.getLocale()));
        }
    }

    static class lower_caseBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env)
        {
            return new SimpleScalar(s.toLowerCase(env.getLocale()));
        }
    }

    static class word_listBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            SimpleSequence result = new SimpleSequence();
            StringTokenizer st = new StringTokenizer(s);
            while (st.hasMoreTokens()) {
               result.add(st.nextToken());
            }
            return result;
        }
    }

    static class numberBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateNumberModel) {
                return model;
            }
            String s = target.getStringValue(env);
            try {
                return new SimpleNumber(env.getArithmeticEngine().toNumber(s));
            }
            catch(NumberFormatException nfe) {
                String mess = "Error: " + getStartLocation()
                             + "\nExpecting a number here, found: " + s;
                throw new NonNumericalException(mess, env);
            }
        }
    }

    static class dateBI extends BuiltIn {
        private final int dateType;
        
        dateBI(int dateType) {
            this.dateType = dateType;
        }
        
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateDateModel) {
                TemplateDateModel dmodel = (TemplateDateModel)model;
                int dtype = dmodel.getDateType();
                // Any date model can be coerced into its own type
                if(dateType == dtype) {
                    return model;
                }
                // unknown and datetime can be coerced into any date type
                if(dtype == TemplateDateModel.UNKNOWN || dtype == TemplateDateModel.DATETIME) {
                    return new SimpleDate(dmodel.getAsDate(), dateType);
                }
                throw new TemplateException(
                    "Cannot convert " + TemplateDateModel.TYPE_NAMES.get(dtype)
                    + " into " + TemplateDateModel.TYPE_NAMES.get(dateType), env);
            }
            // Otherwise, interpret as a string and attempt 
            // to parse it into a date.
            String s = target.getStringValue(env);
            return new DateParser(s, env);
        }
        
        private class DateParser
        implements
            TemplateDateModel,
            TemplateMethodModel,
            TemplateHashModel
        {
            private final String text;
            private final Environment env;
            private final DateFormat defaultFormat;
            private Date cachedValue;
            
            DateParser(String text, Environment env)
            throws
                TemplateModelException
            {
                this.text = text;
                this.env = env;
                this.defaultFormat = env.getDateFormatObject(dateType);
            }
            
            public Date getAsDate() throws TemplateModelException {
                if(cachedValue == null) {
                    cachedValue = parse(defaultFormat);
                }
                return cachedValue;
            }
            
            public int getDateType() {
                return dateType;
            }

            public TemplateModel get(String pattern) throws TemplateModelException {
                return new SimpleDate(
                    parse(env.getDateFormatObject(dateType, pattern)),
                    dateType);
            }

            public TemplateModel exec(List arguments)
                throws TemplateModelException {
                if (arguments.size() != 1) {
                    throw new TemplateModelException(
                            "string?" + key + "(...) requires exactly 1 argument.");
                }
                return get((String) arguments.get(0));
            }

            public boolean isEmpty()
            {
                return false;
            }

            private Date parse(DateFormat df)
            throws
                TemplateModelException
            {
                try {
                    return df.parse(text);
                }
                catch(java.text.ParseException e) {
                    String mess = "Error: " + getStartLocation()
                                 + "\nExpecting a date here, found: " + text;
                    throw new TemplateModelException(mess);
                }
            }
        }
    }

    static class stringBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateNumberModel) {
                return new NumberFormatter(EvaluationUtil.getNumber((TemplateNumberModel)model, target, env), env);
            }
            if (model instanceof TemplateDateModel) {
                TemplateDateModel dm = (TemplateDateModel)model;
                int dateType = dm.getDateType();
                return new DateFormatter(EvaluationUtil.getDate(dm, target, env), dateType, env);
            }
            if (model instanceof SimpleScalar) {
                return model;
            }
            if (model instanceof TemplateBooleanModel) {
                return new BooleanFormatter((TemplateBooleanModel) model, env);
            }
            if (model instanceof TemplateScalarModel) {
                return new SimpleScalar(((TemplateScalarModel) model).getAsString());
            }
            throw invalidTypeException(model, target, env, "number, date, or string");
        }

        private static class NumberFormatter
        implements
            TemplateScalarModel,
            TemplateHashModel,
            TemplateMethodModel
        {
            private final Number number;
            private final Environment env;
            private final NumberFormat defaultFormat;
            private String cachedValue;

            NumberFormatter(Number number, Environment env)
            {
                this.number = number;
                this.env = env;
                defaultFormat = env.getNumberFormatObject(env.getNumberFormat());
            }

            public String getAsString()
            {
                if(cachedValue == null) {
                    cachedValue = defaultFormat.format(number);
                }
                return cachedValue;
            }

            public TemplateModel get(String key)
            throws
                TemplateModelException
            {
                return new SimpleScalar(env.getNumberFormatObject(key).format(number));
            }
            
            public TemplateModel exec(List arguments)
                throws TemplateModelException {
                if (arguments.size() != 1) {
                    throw new TemplateModelException(
                            "number?string(...) requires exactly 1 argument.");
                }
                return get((String) arguments.get(0));
            }

            public boolean isEmpty()
            {
                return false;
            }
        }
        
        private static class DateFormatter
        implements
            TemplateScalarModel,
            TemplateHashModel,
            TemplateMethodModel
        {
            private final Date date;
            private final int dateType;
            private final Environment env;
            private final DateFormat defaultFormat;
            private String cachedValue;

            DateFormatter(Date date, int dateType, Environment env)
            throws
                TemplateModelException
            {
                this.date = date;
                this.dateType = dateType;
                this.env = env;
                defaultFormat = env.getDateFormatObject(dateType);
            }

            public String getAsString()
            throws
                TemplateModelException
            {
                if(dateType == TemplateDateModel.UNKNOWN) {
                    throw new TemplateModelException("Can't convert the date to string, because it is not known which parts of the date variable are in use. Use ?date, ?time or ?datetime built-in, or ?string.<format> or ?string(format) built-in with this date.");
                }
                if(cachedValue == null) {
                    cachedValue = defaultFormat.format(date);
                }
                return cachedValue;
            }

            public TemplateModel get(String key)
            throws
                TemplateModelException
            {
                return new SimpleScalar(env.getDateFormatObject(dateType, key).format(date));
            }
            
            public TemplateModel exec(List arguments)
                throws TemplateModelException {
                if (arguments.size() != 1) {
                    throw new TemplateModelException(
                            "date?string(...) requires exactly 1 argument.");
                }
                return get((String) arguments.get(0));
            }

            public boolean isEmpty()
            {
                return false;
            }
        }

        private static class BooleanFormatter
        implements 
            TemplateScalarModel, 
            TemplateMethodModel 
        {
            private final TemplateBooleanModel bool;
            private final Environment env;
            
            BooleanFormatter(TemplateBooleanModel bool, Environment env) {
                this.bool = bool;
                this.env = env;
            }

            public String getAsString() throws TemplateModelException {
                if (bool instanceof TemplateScalarModel) {
                    return ((TemplateScalarModel) bool).getAsString();
                } else {
                    return env.getBooleanFormat(bool.getAsBoolean());
                }
            }

            public TemplateModel exec(List arguments)
                    throws TemplateModelException {
                if (arguments.size() != 2) {
                    throw new TemplateModelException(
                            "boolean?string(...) requires exactly "
                            + "2 arguments.");
                }
                return new SimpleScalar(
                    (String) arguments.get(bool.getAsBoolean() ? 0 : 1));
            }
        }
    }

    static class trimBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(s.trim());
        }
    }

    static class htmlBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.HTMLEnc(s));
        }
    }

    static class xmlBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.XMLEnc(s));
        }
    }

    static class rtfBI extends StringBuiltIn {
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.RTFEnc(s));
        }
    }


    static class keysBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateHashModelEx) {
                TemplateCollectionModel keys = ((TemplateHashModelEx) model).keys();
                assertNonNull(keys, this, env);
                return keys;
            }
            throw invalidTypeException(model, target, env, "extended hash");
        }
    }

    static class valuesBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateHashModelEx) {
                TemplateCollectionModel values = ((TemplateHashModelEx) model).values();
                assertNonNull(values, this, env);
                return values;
            }
            throw invalidTypeException(model, target, env, "extended hash");
        }
    }

    static class sizeBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateSequenceModel) {
                int size = ((TemplateSequenceModel) model).size();
                return new SimpleNumber(size);
            }
            if (model instanceof TemplateHashModelEx) {
                int size = ((TemplateHashModelEx) model).size();
                return new SimpleNumber(size);
            }
            throw invalidTypeException(model, target, env, "extended hash or sequence");
        }
    }

    static class existsBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            try {
                TemplateModel model = target.getAsTemplateModel(env);
                return model==null ? TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
            } catch (InvalidReferenceException ire) {
                if (target instanceof ParentheticalExpression) {
                    return TemplateBooleanModel.FALSE;
                }
                throw ire;
            }
        }

        boolean isTrue(Environment env) throws TemplateException {
            return _getAsTemplateModel(env) == TemplateBooleanModel.TRUE;
        }
    }

    static class has_contentBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            try {
                TemplateModel model = target.getAsTemplateModel(env);
                return Expression.isEmpty(model) ?
                    TemplateBooleanModel.FALSE : TemplateBooleanModel.TRUE;
            } catch (InvalidReferenceException ire) {
                if (target instanceof ParentheticalExpression) {
                    return TemplateBooleanModel.FALSE;
                }
                throw ire;
            }
        }

        boolean isTrue(Environment env) throws TemplateException {
            return _getAsTemplateModel(env) == TemplateBooleanModel.TRUE;
        }
    }

    static class if_existsBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException
        {
            try {
                TemplateModel model = target.getAsTemplateModel(env);
                return model == null ? TemplateModel.NOTHING : model;
            } catch (InvalidReferenceException ire) {
                if (target instanceof ParentheticalExpression) {
                    return TemplateModel.NOTHING;
                }
                throw ire;
            }
        }
    }

    static class is_stringBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateScalarModel)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_numberBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateNumberModel)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_booleanBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateBooleanModel)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_dateBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateDateModel)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_methodBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateMethodModel)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_macroBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof Macro)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_transformBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateTransformModel)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_hashBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateHashModel) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_hash_exBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateHashModelEx) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_sequenceBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateSequenceModel) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_collectionBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateCollectionModel) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_indexableBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateSequenceModel) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_enumerableBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateSequenceModel || tm instanceof TemplateCollectionModel)  ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class is_directiveBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            assertNonNull(tm, target, env);
            return (tm instanceof TemplateTransformModel || tm instanceof Macro) ?
                TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
    }

    static class namespaceBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env) throws TemplateException {
            TemplateModel tm = target.getAsTemplateModel(env);
            if (!(tm instanceof Macro)) {
                invalidTypeException(tm, target, env, "macro");
            }
            return env.getMacroNamespace((Macro) tm);
        }
    }

    static class defaultBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(final Environment env)
                throws TemplateException
        {
            try {
                TemplateModel model = target.getAsTemplateModel(env);
                return
                    model == null
                    ? FIRST_NON_NULL_METHOD
                    : new ConstantMethod(model);
            } catch (InvalidReferenceException ire) {
                if (target instanceof ParentheticalExpression) {
                    return FIRST_NON_NULL_METHOD;
                }
                throw ire;
            }
        }

        private static class ConstantMethod implements TemplateMethodModelEx
        {
            private final TemplateModel constant;

            ConstantMethod(TemplateModel constant) {
                this.constant = constant;
            }

            public TemplateModel exec(List args) throws TemplateModelException {
                return constant;
            }
        }

        /**
         * A method that goes through the arguments one by one and returns
         * the first one that is non-null. If all args are null, returns null.
         */
        private static final TemplateMethodModelEx FIRST_NON_NULL_METHOD =
            new TemplateMethodModelEx() {
                public TemplateModel exec(List args) throws TemplateModelException {
                    if(args.isEmpty()) {
                        throw new TemplateModelException(
                            "?default(arg) expects at least one argument.");
                    }
                    TemplateModel result = null;
                    for (int i = 0; i< args.size(); i++ ) {
                        result = (TemplateModel) args.get(i);
                        if (result != null) {
                            break;
                        }
                    }
                    return result;
                }
            };
    }
    
    static class index_ofBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateScalarModel) {
                return new BIMethod(((TemplateScalarModel) model).getAsString());
            }
            throw invalidTypeException(model, target, env, "string");
        }
        
        private class BIMethod implements TemplateMethodModelEx {
            private String s;
            
            private BIMethod(String s) {
                this.s = s;
            }
            
            public TemplateModel exec(List args) throws TemplateModelException {
                Object obj;
                String sub;
                int fidx;

                int ln  = args.size();
                if (ln == 0) {
                    new TemplateModelException(
                            "?index_of(...) expects at least one argument.");
                }
                if (ln > 2) {
                    throw new TemplateModelException(
                            "?index_of(...) expects at most two arguments.");
                }

                obj = args.get(0);       
                if (!(obj instanceof TemplateScalarModel)) {
                    throw new TemplateModelException(
                            "?index_of(...) expects a string as "
                            + "its first argument.");
                }
                sub = ((TemplateScalarModel) obj).getAsString();
                
                if (ln > 1) {
                    obj = args.get(1);
                    if (!(obj instanceof TemplateNumberModel)) {
                        throw new TemplateModelException(
                                "?index_of(...) expects a number as "
                                + "its second argument.");
                    }
                    fidx = ((TemplateNumberModel) obj).getAsNumber().intValue();
                } else {
                    fidx = 0;
                }

                return new SimpleNumber(s.indexOf(sub, fidx));
            }
        } 
    }

    static class last_index_ofBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateScalarModel) {
                return new BIMethod(((TemplateScalarModel) model).getAsString());
            }
            throw invalidTypeException(model, target, env, "string");
        }

        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            public TemplateModel exec(List args) throws TemplateModelException {
                Object obj;
                String sub;

                int ln  = args.size();
                if (ln == 0) {
                    new TemplateModelException(
                            "?last_index_of(...) expects at least one argument.");
                }
                if (ln > 2) {
                    throw new TemplateModelException(
                            "?last_index_of(...) expects at most two arguments.");
                }

                obj = args.get(0);
                if (!(obj instanceof TemplateScalarModel)) {
                    throw new TemplateModelException(
                            "?last_index_of(...) expects a string as "
                            + "its first argument.");
                }
                sub = ((TemplateScalarModel) obj).getAsString();

                if (ln > 1) {
                    obj = args.get(1);
                    if (!(obj instanceof TemplateNumberModel)) {
                        throw new TemplateModelException(
                                "?last_index_of(...) expects a number as "
                                + "its second argument.");
                    }
                    int fidx = ((TemplateNumberModel) obj).getAsNumber().intValue();
                    return new SimpleNumber(s.lastIndexOf(sub, fidx));
                } else {
                    return new SimpleNumber(s.lastIndexOf(sub));
                }
            }
        }
    }
    
    static class starts_withBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateScalarModel) {
                return new BIMethod(((TemplateScalarModel) model).getAsString());
            }
            throw invalidTypeException(model, target, env, "string");
        }

        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            public TemplateModel exec(List args) throws TemplateModelException {
                String sub;

                if (args.size() != 1) {
                    new TemplateModelException(
                            "?starts_with(...) expects exactly 1 argument.");
                }

                Object obj = args.get(0);
                if (!(obj instanceof TemplateScalarModel)) {
                    throw new TemplateModelException(
                            "?starts_with(...) expects a string argument");
                }
                sub = ((TemplateScalarModel) obj).getAsString();

                return s.startsWith(sub) ?
                        TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            }
        }
    }

    static class ends_withBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateScalarModel) {
                return new BIMethod(((TemplateScalarModel) model).getAsString());
            }
            throw invalidTypeException(model, target, env, "string");
        }

        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            public TemplateModel exec(List args) throws TemplateModelException {
                String sub;

                if (args.size() != 1) {
                    new TemplateModelException(
                            "?ends_with(...) expects exactly 1 argument.");
                }

                Object obj = args.get(0);
                if (!(obj instanceof TemplateScalarModel)) {
                    throw new TemplateModelException(
                            "?ends_with(...) expects a string argument");
                }
                sub = ((TemplateScalarModel) obj).getAsString();

                return s.endsWith(sub) ?
                        TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            }
        }
    }
    
    static class replaceBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateScalarModel) {
                return new BIMethod(((TemplateScalarModel) model).getAsString());
            }
            throw invalidTypeException(model, target, env, "string");
        }

        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            public TemplateModel exec(List args) throws TemplateModelException {
                if (args.size() != 2) {
                    throw new TemplateModelException(
                            "?replace(...) needs exactly 2 arguments.");
                }
                Object obj1, obj2;
                obj1 = args.get(0);
                obj2 = args.get(1);
                if (!(obj1 instanceof TemplateScalarModel
                        && obj2 instanceof TemplateScalarModel)) {
                    throw new TemplateModelException(
                            "Parameters to ?replace(...) must be strings.");
                }

                return new SimpleScalar(StringUtil.replace(
                        s,
                        ((TemplateScalarModel) obj1).getAsString(),
                        ((TemplateScalarModel) obj2).getAsString() ));
            }
        }
    }

    static class splitBI extends BuiltIn {
        TemplateModel _getAsTemplateModel(Environment env)
                throws TemplateException {
            TemplateModel model = target.getAsTemplateModel(env);
            if (model instanceof TemplateScalarModel) {
                return new BIMethod(((TemplateScalarModel) model).getAsString());
            }
            throw invalidTypeException(model, target, env, "string");
        }

        private class BIMethod implements TemplateMethodModelEx {
            private String s;

            private BIMethod(String s) {
                this.s = s;
            }

            public TemplateModel exec(List args) throws TemplateModelException {
                if (args.size() != 1) {
                    new TemplateModelException(
                            "?split(...) expects exactly 1 argument.");
                }

                Object obj = args.get(0);
                if (!(obj instanceof TemplateScalarModel)) {
                    throw new TemplateModelException(
                            "?split(...) expects a string argument");
                }

                return new StringArraySequence(StringUtil.split(
                        s, ((TemplateScalarModel) obj).getAsString()));
            }
        }
    }

}
