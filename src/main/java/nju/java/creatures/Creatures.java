package nju.java.creatures;

import nju.java.Ground;


import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cbcwestwolf on 2017/12/26.
 */
public abstract class Creatures implements Runnable ,Serializable {

    private int x ; // [ 0 , MAX_X ]
    private int y ; // [ 0 , MAX_Y ]
    public Thread thread = null;

    public Thread getThread() {
        return thread;
    }

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


    protected Ground ground; // // Creatures的创建需要Ground是因为能调用Ground的repaint函数

    protected int blood = 100; // 血条为 0 ~ 100

    protected int power = 0;      // 战斗力，0 ~ 100 ， 每次可打击对方的力量

    protected Image image;

    public Creatures(){
        this.x = this.y = 0;
        this.ground = null;
    }

    public Creatures(int x, int y, Ground ground) {
        this.x = x ;
        this.y = y;
        this.ground = ground;
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
        URL loc = this.getClass().getClassLoader().getResource(filename);
        ImageIcon iia = new ImageIcon(loc);
        Image image = iia.getImage();
        this.setImage(image);
    }

    // 可行性检查在Ground中进行，Creatures不需要进行检查
    public synchronized void move(int x_off, int y_off){

        if(Ground.isStop() || Ground.getStatus() != Ground.Status.FIGHTING )
            return;

        if( isDead() )
            return;

        int nx = this.getX() + x_off;
        int ny = this.getY() + y_off;

        this.setX(nx);
        this.setY(ny);

    }

    public abstract void run(); // 行动有两种：一种是攻击(Attack)，一种是移动(Walk)

    protected int attackValue(Creatures c){ // 计算攻击得分
        int get = c.getBlood() < this.getPower() ? c.getBlood() : this.getPower();
        int loss = this.blood < (c.getPower()/2) ? this.blood : (c.getPower()/2);
        return get - loss;
    }

    //public abstract void Attack(); // 攻击敌人

    //public abstract void Walk(); // 向Ground请求位移，Ground检查无误后调用Creatures的位移函数

}
