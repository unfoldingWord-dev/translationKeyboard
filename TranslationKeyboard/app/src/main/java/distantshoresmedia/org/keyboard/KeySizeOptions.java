package distantshoresmedia.org.keyboard;

/**
 * Created by Fechner on 11/16/14.
 */
public class KeySizeOptions {
    /** Height of the key, not including the gap */
    public int height;
    /** Width of the key, not including the gap */
    public float width;

    public int verticalGap;
    /** The horizontal gap before this key */
    public float horizontalGap;

    /** X coordinate of the key in the keyboardConfig layout */
    public int x;
    /** Y coordinate of the key in the keyboardConfig layout */
    public int y;

   public KeySizeOptions(int height, float widht, int verticalGap, float horizontalGap, int x, int y){
       this.height = height;
       this.width = width;
       this.verticalGap = verticalGap;
       this.horizontalGap = horizontalGap;
       this.x = x;
       this.y = y;
   }
}
