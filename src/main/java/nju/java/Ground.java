package nju.java;

import nju.java.things.Things;
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

    public static final int MAX_X = 16;
    public static final int MAX_Y = 9;
    public static final int SPACE = 80;

    private int pixelHeight; // 上下的高度

    private int pixelWidth; // 左右的长度

    private Image backgroundImage = null; // 背景图片

    private Things things[][] = new Things[MAX_X][MAX_Y];

    private Grandpa grandpa = null;
    private GourdDolls [] gourdDolls = null;
    private Snake snake = null;

    public Ground(){
        // TODO:添加键盘监听器
        setFocusable(true);

        initFormation();
        initGround();
        initCreature();
        readFormation();

        actionCreature();
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    public void setPixelHeight(int pixelHeight) {
        this.pixelHeight = pixelHeight;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public void setPixelWidth(int pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    private void initFormation(){

    }

    private void initGround(){
        // 背景分辨率为 1280*720 , 即16:9 。 每个格子的边长为80分辨率
        URL loc = this.getClass().getClassLoader().getResource("背景2.png");
        ImageIcon iia = new ImageIcon(loc); // Image是抽象类，所以只能通过ImageIcon来创建
        backgroundImage = iia.getImage();
        pixelWidth = iia.getIconWidth();
        pixelHeight = iia.getIconHeight();
        try {
            backgroundImage = ImageIO.read(new File("背景2.png"));
        }
        catch(Exception e){

        }


    }

    private void initCreature(){


        grandpa = new Grandpa(0,4,this);
        grandpa.setImage("爷爷.png");
        things[0][4] = grandpa;

        snake = new Snake(MAX_X-1,MAX_Y/2,this);
        snake.setImage("蛇精.png");
        things[MAX_X-1][MAX_Y/2] = snake;

        gourdDolls = new GourdDolls[7]; // 默认为鹤翼阵型
        gourdDolls[0] = new GourdDolls(0,1,this);
        gourdDolls[1] = new GourdDolls(1,2,this);
        gourdDolls[2] = new GourdDolls(2,3,this);
        gourdDolls[3] = new GourdDolls(3,4,this);
        gourdDolls[4] = new GourdDolls(2,5,this);
        gourdDolls[5] = new GourdDolls(1,6,this);
        gourdDolls[6] = new GourdDolls(0,7,this);
        gourdDolls[0].setImage("大娃.png");
        gourdDolls[1].setImage("二娃.png");
        gourdDolls[2].setImage("三娃.png");
        gourdDolls[3].setImage("四娃.png");
        gourdDolls[4].setImage("五娃.png");
        gourdDolls[5].setImage("六娃.png");
        gourdDolls[6].setImage("七娃.png");
        for(int i = 0;  i < 7 ; ++ i)
            things[gourdDolls[i].getX()][gourdDolls[i].getY()] = gourdDolls[i];

    }

    private void readFormation(){


    }

    private void actionCreature(){
        Thread[] gourddollsThreads = new Thread[7];
        for(int i = 0 ; i < 7  ; ++ i) {
            gourddollsThreads[i] = new Thread(gourdDolls[i]);
            gourddollsThreads[i].start();
        }
    }

    private void paintGround(Graphics g){

        // TODO: add all drawImage() here

        g.drawImage(backgroundImage,0,0, pixelWidth, pixelHeight,this);
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

        // printFormation();
    }

    @Override
    public void repaint(){
        // TODO: 添加更新currentFormation

        super.repaint();
    }

}
