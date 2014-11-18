package distantshoresmedia.org.keyboard;

import android.graphics.Path;
import android.view.MotionEvent;

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

    /**
     * Flags that specify the anchoring to edges of the keyboardConfig for detecting touch events
     * that are just out of the boundary of the key.
     */
    private Edge[] edges;
    public Edge[] getEdges() {
        return edges;
    }
    public void addEdge(Edge edge){

        Edge[] newEdges = new Edge[this.edges.length + 1];

        for (int i = 0; i < this.edges.length; i++) {
            newEdges[i] = this.edges[i];
        }
        newEdges[this.edges.length + 1] = edge;

        this.edges = newEdges;
    }
    public void removeEdge(Edge edge){

        int index = 0;
        Edge newEdges[] = new Edge[this.edges.length - 1];

        for (int i = 0; i < this.edges.length; i++) {
            if(this.edges[i] != edge){
                newEdges[i] = this.edges[index];
                index++;
            }
        }

        this.edges = newEdges;
    }

   public KeySizeOptions(int height, float width, int verticalGap, float horizontalGap, int x, int y){
       this.height = height;
       this.width = width;
       this.verticalGap = verticalGap;
       this.horizontalGap = horizontalGap;
       this.x = x;
       this.y = y;
   }

    public boolean isUpAgainstEdge(Edge edge){

        for (Edge foo : this.edges) {
            if(edge == foo){
                return true;
            }
        }

        return false;
    }

    public int rightX(){
        return Math.round(this.x + this.width);
    }

    public int bottomY(){
        return Math.round(this.y + this.height);
    }

    public boolean isInside(int x, int y) {

        // is within the key
        if(x > this.x && x < this.rightX()){
            return true;
        }
        if(y > this.y && y < this.bottomY()){
            return true;
        }

        for(Edge edge : this.edges){
            switch (edge){
                case TOP:
                    if(y < this.y){
                        return true;
                    }
                    break;
                case LEFT:
                    if(x < this.x){
                        return true;
                    }
                    break;
                case RIGHT:
                    if(x > this.rightX()){
                        return true;
                    }
                    break;
                case BOTTOM:
                    if(y > this.bottomY()){
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public int centerX() {
        return Math.round(this.x + (this.width / 2));
    }
    public int centerY() {
        return Math.round(this.y + (this.height / 2 ));
    }

}
