package org.distantshoresmedia.utilities;

/**
 * Created by Fechner on 7/7/15.
 */
public class URLHelper {

    private static final String kBaseURL = "http://remote.actsmedia.com/api/";
    private static final String kVersionUrlTag = "v2/";
    private static final String kKeyboardUrlTag = "keyboard/";

    static public String getKeyboardUrl() {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag;
    }

    static public String getKeyboardUrl(String keyboardKey) {
        return kBaseURL + kVersionUrlTag + kKeyboardUrlTag + keyboardKey;
    }
}
