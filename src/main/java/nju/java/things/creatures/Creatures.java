package nju.java.things.creatures;

import nju.java.Ground;
import nju.java.things.Things;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cbcwestwolf on 2017/12/26.
 */
public abstract class Creatures extends Things implements Runnable{
    protected Ground ground; // // Creatures的创建需要Ground是因为能调用Ground的repaint函数

    protected int blood = 100; // 血条为 0 ~ 100
    protected int power ;      // 战斗力，0 ~ 100 ， 每次可打击对方的力量

    protected Image image;

    public Creatures(){
        super(0,0);
        this.ground = null;
    }

    public Creatures(int x, int y, Ground ground) {
        super(x,y);
        this.ground = ground;
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

    public boolean move(int x_off, int y_off){ // 成功返回true

        if(Ground.isStop() || Ground.getStatus() != Ground.Status.FIGHTING )
            return false;

        int nx = this.getX() + x_off;
        int ny = this.getY() + y_off;

        // TODO: 判断是否成功并返回 true or false
        this.setX(nx);
        this.setY(ny);
        return true;
    }

    public abstract void run();


}
