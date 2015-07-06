package org.distantshoresmedia.sideloading;

/**
 * Created by Fechner on 7/6/15.
 */
public enum SideLoadType {

    SIDE_LOAD_TYPE_NONE(2), SIDE_LOAD_TYPE_BLUETOOTH(1), SIDE_LOAD_TYPE_NFC(2), SIDE_LOAD_TYPE_WIFI(3),
    SIDE_LOAD_TYPE_STORAGE(4);


    private int num;


    SideLoadType(int num) {
        this.num = num;
    }
}
