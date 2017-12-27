package nju.java.things;


/**
 * Created by cbcwestwolf on 2017/12/26.
 */

// 表示空地或者生物体
public abstract class Things {

    private int x ; // [ 0 , 15 ]
    private int y ; // [ 0 , 8 ]

    public Things(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

}
