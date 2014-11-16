package distantshoresmedia.org.keyboard;

/**
 * Created by Fechner on 11/15/14.
 */
public interface UIHandlerInterface {

    public void showKey(final int keyIndex, PointerTracker tracker);

    public void distmissPopupPreview();

    public boolean openPopupIfRequired(int keyIndex, PointerTracker tracker);

    public int getKeyRepeatInterval();

    public boolean popupIsShowing();

    public boolean previewTextIsVisible();
}
