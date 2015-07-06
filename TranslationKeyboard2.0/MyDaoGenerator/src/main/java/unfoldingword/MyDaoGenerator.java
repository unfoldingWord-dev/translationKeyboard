package unfoldingword;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

//    private static final String UW_DATABASE_MODEL_PROTOCOL = "model.UWDatabaseModel";

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(100, "org.distantshoresmedia.model.daoModels");

        schema.enableKeepSectionsByDefault();
        setupData(schema);

        new DaoGenerator().generateAll(schema, args[0]);
    }

    private static void setupData(Schema schema){

        Entity availableKeyboard = createAvailableKeyboard(schema);
        Entity baseKeyboard = createBaseKeyboard(schema, availableKeyboard);
        Entity keyboardVariant= createKeyboardVariant(schema, baseKeyboard);
        Entity keyPosition = createKeyPosition(schema, keyboardVariant);
        createKeyCharacter(schema, keyPosition);
    }

    private static Entity createAvailableKeyboard(Schema schema) {

        DaoHelperMethods.EntityInformation availableKeyboardInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.AVAILABLE_KEYBOARD, ModelNames.AVAILABLE_KEYBOARD_STRING_ATTRIBUTES,
                        ModelNames.AVAILABLE_KEYBOARD_DATE_ATTRIBUTES );
        Entity availableKeyboard = DaoHelperMethods.createEntity(schema, availableKeyboardInfo);
//        project.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        return availableKeyboard;
    }

    private static Entity createBaseKeyboard(Schema schema, Entity availableKeyboard) {

        DaoHelperMethods.EntityInformation baseKeyboardInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.BASE_KEYBOARD, ModelNames.BASE_KEYBOARD_STRING_ATTRIBUTES,
                        ModelNames.BASE_KEYBOARD_DATE_ATTRIBUTES);
        Entity baseKeyboard = DaoHelperMethods.createEntity(schema, baseKeyboardInfo);

//        language.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        DaoHelperMethods.createParentChildRelationship(
                availableKeyboard, ModelNames.AVAILABLE_KEYBOARD_BASE_KEYBOARDS_ATTRIBUTE,
                baseKeyboard, ModelNames.BASE_KEYBOARD_AVAILABLE_KEYBOARD_ATTRIBUTE);

        return baseKeyboard;
    }

    private static Entity createKeyboardVariant(Schema schema, Entity baseKeyboard) {

        DaoHelperMethods.EntityInformation keyboardVariantInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.KEYBOARD_VARIANT, ModelNames.KEYBOARD_VARIANT_STRING_ATTRIBUTES,
                        ModelNames.KEYBOARD_VARIANT_DATE_ATTRIBUTES);
        Entity keyboardVariant = DaoHelperMethods.createEntity(schema, keyboardVariantInfo);

//        version.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        DaoHelperMethods.createParentChildRelationship(
                baseKeyboard, ModelNames.BASE_KEYBOARD_KEYBOARD_VARIANT_ATTRIBUTE,
                keyboardVariant, ModelNames.KEYBOARD_VARIANT_BASE_KEYBOARD_ATTRIBUTE);

        return keyboardVariant;
    }

    private static Entity createKeyPosition(Schema schema, Entity keyboardVariant) {

        DaoHelperMethods.EntityInformation keyPositionInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.KEY_POSITION);
        keyPositionInfo.intAttributes = ModelNames.KEY_POSITION_INT_ATTRIBUTES;
        Entity keyPosition = DaoHelperMethods.createEntity(schema, keyPositionInfo);

//        book.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);
        DaoHelperMethods.createParentChildRelationship(
                keyboardVariant, ModelNames.KEYBOARD_VARIANT_KEY_POSITION_ATTRIBUTE,
                keyPosition, ModelNames.KEY_POSITION_KEYBOARD_VARIANT_ATTRIBUTE);

        return keyPosition;
    }

    private static void createKeyCharacter(Schema schema, Entity keyPosition) {

        DaoHelperMethods.EntityInformation keyCharacterInfo =
                new DaoHelperMethods.EntityInformation(ModelNames.KEY_CHARACTER);
        keyCharacterInfo.intAttributes = ModelNames.KEY_CHARACTER_INT_ATTRIBUTES;
        Entity keyCharacter = DaoHelperMethods.createEntity(schema, keyCharacterInfo);
//        verification.setSuperclass(UW_DATABASE_MODEL_PROTOCOL);

        DaoHelperMethods.createParentChildRelationship(
                keyPosition, ModelNames.KEY_POSITION_KEY_CHARACTER_ATTRIBUTE,
                keyCharacter, ModelNames.KEY_CHARACTER_KEY_POSITION_ATTRIBUTE);
    }

}
