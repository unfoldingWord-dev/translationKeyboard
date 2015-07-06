package org.distantshoresmedia.sideloading;

/**
 * Created by Fechner on 7/6/15.
 */
public class SideLoader {

    public interface SideLoaderListener{
        void sideLoadingSucceeded(String response);
        void sideLoadingFailed(String errorMessage);
    }


    public static void startSideLoading(SideLoadType type, SideLoaderListener listener){

        switch (type){
            case SIDE_LOAD_TYPE_BLUETOOTH:{
                startSideLoadBluetooth(listener);
                break;
            }
            case SIDE_LOAD_TYPE_NFC:{
                startSideLoadNFC(listener);
                break;
            }
            case SIDE_LOAD_TYPE_WIFI:{
                startSideLoadWIFI(listener);
                break;
            }
            case SIDE_LOAD_TYPE_STORAGE:{
                startSideLoadStorage(listener);
                break;
            }
            default:{

            }
        }
    }

    private static void startSideLoadBluetooth(SideLoaderListener listener){

    }

    private static void startSideLoadNFC(SideLoaderListener listener){

    }

    private static void startSideLoadWIFI(SideLoaderListener listener){

    }

    private static void startSideLoadStorage(SideLoaderListener listener){

    }

    public static void startDataSharing(SideLoadType type, String text, SideLoaderListener listener){

        switch (type){
            case SIDE_LOAD_TYPE_BLUETOOTH:{
                startShareBluetooth(text, listener);
                break;
            }
            case SIDE_LOAD_TYPE_NFC:{
                startShareNFC(text, listener);
                break;
            }
            case SIDE_LOAD_TYPE_WIFI:{
                startShareWIFI(text, listener);
                break;
            }
            case SIDE_LOAD_TYPE_STORAGE:{
                startShareStorage(text, listener);
                break;
            }
            default:{

            }
        }
    }

    private static void startShareBluetooth(String shareText, SideLoaderListener listener){

    }

    private static void startShareNFC(String shareText, SideLoaderListener listener){

    }

    private static void startShareWIFI(String shareText, SideLoaderListener listener){

    }

    private static void startShareStorage(String shareText, SideLoaderListener listener){

    }

}
