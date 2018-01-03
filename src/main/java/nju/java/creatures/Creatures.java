package nju.java.creatures;

import nju.java.BackEnd;
import nju.java.creatures.Good.Good;


import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.net.URL;

/***
 * @author cbcwestwolf
 * <br>
 * Creatures类定义了角色的基本属性 <br>
 * 角色的更多属性在Good类和Bad类中定义
 *
 * @see Good
 * @see nju.java.creatures.bad.Bad
 */

public abstract class Creatures implements Runnable ,Serializable {

    private int x ; // [ 0 , MAX_X ]
    private int y ; // [ 0 , MAX_Y ]
    public Thread thread = null;

    public void setThread(Thread thread) {
        this.thread = thread;
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


    protected BackEnd backEnd; // // Creatures的创建需要Ground是因为能调用Ground的repaint函数

    protected int blood = 100; // 血条为 0 ~ 100

    protected int power = 0;      // 战斗力，0 ~ 100 ， 每次可打击对方的力量

    protected Image image;

    public Creatures(int x, int y, BackEnd backEnd) {
        this.x = x ;
        this.y = y;
        this.backEnd = backEnd;
    }

    public int getPower() {
        return power;
    }

    public int getBlood() {
        return blood;
    }

    public synchronized void setBlood(int blood) {
        this.blood = blood;
    }

    public boolean isDead(){
        return blood <= 0;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImage(String filename){
        URL url = this.getClass().getClassLoader().getResource(filename);
        ImageIcon imageIcon = new ImageIcon(url);
        Image image = imageIcon.getImage();
        this.image = image;
    }

    public abstract void run(); // 行动有两种：一种是攻击(Attack)，一种是移动(Walk)

    /**
     * 计算攻击的得分 <br>
     * @param c 攻击的对象
     * @return 返回得分
     */
    protected int attackValue(Creatures c){ // 计算攻击得分
        int get = c.getBlood() < this.getPower() ? c.getBlood() : this.getPower();
        int loss = this.blood < (c.getPower()/2) ? this.blood : (c.getPower()/2);
        return get - loss;
    }

    /**
     * 计算两个生物体之间的曼哈顿距离
     * @param a
     * @param b
     * @return a和b的曼哈顿距离
     */
    public static final int distance(Creatures a, Creatures b){
        return Math.abs(a.getX()-b.getX()) + Math.abs(a.getY()-b.getY());
    }

}
