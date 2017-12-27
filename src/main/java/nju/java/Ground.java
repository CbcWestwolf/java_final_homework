package nju.java;

import nju.java.things.creatures.GourdDolls;
import nju.java.things.creatures.Grandpa;
import nju.java.things.creatures.enemies.Snake;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Created by cbcwestwolf on 2017/12/26.
 */
public class Ground extends JPanel {

    public static final int MAX_X = 15;
    public static final int MAX_Y = 8;
    public static final int SPACE = 80;

    private int myHeight = 100; // 上下的高度

    private int myWidth = 100; // 左右的长度

    private Image backgroundImage = null; // 背景图片

    private Grandpa grandpa = null;
    private GourdDolls [] gourdDolls = null;
    private Snake snake = null;

    public Ground(){
        // TODO:添加键盘监听器
        setFocusable(true);
        initGround();
        initCreature();
    }

    public int getMyHeight() {
        return myHeight;
    }

    public void setMyHeight(int myHeight) {
        this.myHeight = myHeight;
    }

    public int getMyWidth() {
        return myWidth;
    }

    public void setMyWidth(int myWidth) {
        this.myWidth = myWidth;
    }

    private void initGround(){

        // 背景分辨率为 1280*720 , 即16:9 。 每个格子的边长为80分辨率
        URL loc = this.getClass().getClassLoader().getResource("背景2.png");
        ImageIcon iia = new ImageIcon(loc); // Image是抽象类，所以只能通过ImageIcon来创建
        backgroundImage = iia.getImage();
        myWidth = iia.getIconWidth();
        myHeight = iia.getIconHeight();
        try {
            backgroundImage = ImageIO.read(new File("背景2.png"));
        }
        catch(Exception e){

        }
    }

    private void initCreature(){

        grandpa = new Grandpa(0,0,this);
        grandpa.setImage("爷爷.png");

        gourdDolls = new GourdDolls[7];
        for(int i = 0 ; i < 7 ; ++ i){
            gourdDolls[i] = new GourdDolls(0,i+1,this);
        }
        gourdDolls[0].setImage("大娃.png");
        gourdDolls[1].setImage("二娃.png");
        gourdDolls[2].setImage("三娃.png");
        gourdDolls[3].setImage("四娃.png");
        gourdDolls[4].setImage("五娃.png");
        gourdDolls[5].setImage("六娃.png");
        gourdDolls[6].setImage("七娃.png");

        snake = new Snake(MAX_X,MAX_Y/2,this);
        snake.setImage("蛇精.png");

    }

    private void paintGround(Graphics g){

        // TODO: add all drawImage() here

        g.drawImage(backgroundImage,0,0,myWidth,myHeight,this);
        g.drawImage(grandpa.getImage(),grandpa.getX()*SPACE , grandpa.getY()*SPACE,SPACE,SPACE,this);
        for(int i = 0 ; i<7 ; ++ i)
            g.drawImage(gourdDolls[i].getImage(),
                    gourdDolls[i].getX()*SPACE,gourdDolls[i].getY()*SPACE,
                    SPACE,SPACE,this);

        g.drawImage(snake.getImage(),snake.getX()*SPACE,snake.getY()*SPACE,
                SPACE,SPACE,this);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintGround(g);
    }


}
