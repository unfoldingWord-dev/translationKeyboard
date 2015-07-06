package unfoldingword;

/**
 * Created by Fechner on 5/9/15.
 */
public class ModelNames {

    static public final String AVAILABLE_KEYBOARD = "AvailableKeyboard";
    static public final String[] AVAILABLE_KEYBOARD_STRING_ATTRIBUTES = { "isoLanguage", "isoRegion", "languageName" };
    static public final String[] AVAILABLE_KEYBOARD_DATE_ATTRIBUTES = { "updatedAt" };
    static public final String AVAILABLE_KEYBOARD_BASE_KEYBOARDS_ATTRIBUTE = "baseKeyboards";


    static public final String BASE_KEYBOARD = "BaseKeyboard";
    static public final String[] BASE_KEYBOARD_STRING_ATTRIBUTES = { "keyboardName", "isoRegion", "isoLanguage" };
    static public final String[] BASE_KEYBOARD_DATE_ATTRIBUTES = { "createdAt", "updatedAt" };
    static public final String BASE_KEYBOARD_AVAILABLE_KEYBOARD_ATTRIBUTE = "availableKeyboardId";
    static public final String BASE_KEYBOARD_KEYBOARD_VARIANT_ATTRIBUTE = "keyboardVariants";


    static public final String KEYBOARD_VARIANT = "KeyboardVariant";
    static public final String[] KEYBOARD_VARIANT_STRING_ATTRIBUTES = { "name" };
    static public final String[] KEYBOARD_VARIANT_DATE_ATTRIBUTES = { "createdAt", "updatedAt" };
    static public final String KEYBOARD_VARIANT_BASE_KEYBOARD_ATTRIBUTE = "baseKeyboardId";
    static public final String KEYBOARD_VARIANT_KEY_POSITION_ATTRIBUTE = "keyPositions";


    static public final String KEY_POSITION = "KeyPosition";
    static public final String[] KEY_POSITION_INT_ATTRIBUTES = { "percentWidth", "row", "column"};
    static public final String KEY_POSITION_KEYBOARD_VARIANT_ATTRIBUTE = "keyboardVariantId";
    static public final String KEY_POSITION_KEY_CHARACTER_ATTRIBUTE = "keyPositions";


    static public final String KEY_CHARACTER = "KeyCharacter";
    static public final String[] KEY_CHARACTER_INT_ATTRIBUTES = { "modMask", "unicode"};
    static public final String KEY_CHARACTER_KEY_POSITION_ATTRIBUTE = "keyPositionId";
}
