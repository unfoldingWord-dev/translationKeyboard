package distantshoresmedia.org.keyboard;

import android.os.Handler;
import android.os.Message;

import distantshoresmedia.org.keyboard.UIHandlerInterface;
/**
 * Created by Fechner on 11/15/14.
 */
public class UIHandler extends Handler {
    private static final int MSG_POPUP_PREVIEW = 1;
    private static final int MSG_DISMISS_PREVIEW = 2;
    private static final int MSG_REPEAT_KEY = 3;
    private static final int MSG_LONGPRESS_KEY = 4;

    private boolean minKeyRepeat;

    private UIHandlerInterface handlerInterface;

    public UIHandler(UIHandlerInterface handlerInterface){

        super();
        this.handlerInterface = handlerInterface;
    }
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_POPUP_PREVIEW:
                handlerInterface.showKey(msg.arg1, (PointerTracker)msg.obj);
                break;
            case MSG_DISMISS_PREVIEW:
                this.handlerInterface.distmissPopupPreview();
                break;
            case MSG_REPEAT_KEY: {
                final PointerTracker tracker = (PointerTracker)msg.obj;
                tracker.repeatKey(msg.arg1);
                startKeyRepeatTimer(handlerInterface.getKeyRepeatInterval(), msg.arg1, tracker);
                break;
            }
            case MSG_LONGPRESS_KEY: {
                final PointerTracker tracker = (PointerTracker)msg.obj;
                this.handlerInterface.openPopupIfRequired(msg.arg1, tracker);
                break;
            }
        }
    }

    public void popupPreview(long delay, int keyIndex, PointerTracker tracker) {
        removeMessages(MSG_POPUP_PREVIEW);
        if (handlerInterface.popupIsShowing() && handlerInterface.previewTextIsVisible()) {
            // Show right away, if it's already visible and finger is moving around
            handlerInterface.showKey(keyIndex, tracker);
        } else {
            sendMessageDelayed(obtainMessage(MSG_POPUP_PREVIEW, keyIndex, 0, tracker),
                    delay);
        }
    }

    public void cancelPopupPreview() {
        removeMessages(MSG_POPUP_PREVIEW);
    }

    public void dismissPreview(long delay) {
        if (handlerInterface.popupIsShowing()) {
            sendMessageDelayed(obtainMessage(MSG_DISMISS_PREVIEW), delay);
        }
    }

    public void cancelDismissPreview() {
        removeMessages(MSG_DISMISS_PREVIEW);
    }

    public void startKeyRepeatTimer(long delay, int keyIndex, PointerTracker tracker) {
        minKeyRepeat = true;
        sendMessageDelayed(obtainMessage(MSG_REPEAT_KEY, keyIndex, 0, tracker), delay);
    }

    public void cancelKeyRepeatTimer() {
        minKeyRepeat = false;
        removeMessages(MSG_REPEAT_KEY);
    }

    public boolean isInKeyRepeat() {
        return minKeyRepeat;
    }

    public void startLongPressTimer(long delay, int keyIndex, PointerTracker tracker) {
        removeMessages(MSG_LONGPRESS_KEY);
        sendMessageDelayed(obtainMessage(MSG_LONGPRESS_KEY, keyIndex, 0, tracker), delay);
    }

    public void cancelLongPressTimer() {
        removeMessages(MSG_LONGPRESS_KEY);
    }

    public void cancelKeyTimers() {
        cancelKeyRepeatTimer();
        cancelLongPressTimer();
    }

    public void cancelAllMessages() {
        cancelKeyTimers();
        cancelPopupPreview();
        cancelDismissPreview();
    }
}