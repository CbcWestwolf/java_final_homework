package nju.java;

import nju.java.things.creatures.Creatures;
import nju.java.things.creatures.GourdDolls;
import nju.java.things.creatures.Grandpa;
import nju.java.things.creatures.enemies.ScorpionKing;
import nju.java.things.creatures.enemies.SnakeQueen;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

import static nju.java.Ground.Status.*;

/**
 * Created by cbcwestwolf on 2017/12/26.
 */
public class Ground extends JPanel {


    public static final int STEP = 20; // 每次移动的距离
    public static final int SPACE = 4*STEP ; // 图片的边长   (必须是STEP的整数倍）
    public static final int DISTANCE = 2*STEP; // 攻击范围
    public static final int TIME_CLOCK = 200; // 线程休眠时间 （毫秒）
    public static final int PIXEL_HEIGHT = 720; // 上下的高度（像素点）
    public static final int PIXEL_WIDTH = 1280; // 左右的长度
    public static final int MAX_X = (PIXEL_WIDTH-SPACE) / STEP ;
    public static final int MAX_Y = (PIXEL_HEIGHT-SPACE) / STEP ;


    private static boolean stop; // 玩家是否按下暂停键
    private static Status status = FIGHTING; // 3种状态：未开始，打斗中，回放中


    private Image backgroundImage = null; // 背景图片

    private Grandpa grandpa = null;
    private GourdDolls [] gourdDolls = null;
    private Creatures [] goodCreatures = null;

    // 蝎子精和蛇精是唯一的
    private SnakeQueen snake = null;
    private ScorpionKing scorpion = null;
    private ArrayList<Creatures> enemy = new ArrayList<Creatures>(); // 小马仔们
    private Creatures [] badCreatures = null;


    public Ground(){
        addKeyListener(new TAdapter());
        setFocusable(true);

        //initFormation();
        initGround();
        initCreature();
        //readFormation();

        actionCreature();
    }



    public static boolean isStop() {
        return stop;
    }

    public static void setStop(boolean stop) {
        Ground.stop = stop;
    }

    public static Status getStatus() {
        return status;
    }

    public static void setStatus(Status status) {
        Ground.status = status;
    }


    // Creatures API: get grandpa's location for enemy
    public int[] getGrandpaLocation(){
        int[] result = new int[2];
        if(grandpa == null){
            result[0] = result[1] = -1;
        }
        else {
            result[0] = grandpa.getX();
            result[1] = grandpa.getY();
        }
        return result;
    }

    // Creatures API: get nearest enemy's location for gourdDolls
    private int[] getNearestEnemy(int x, int y){

        int[] result = new int[0]; // 目标坐标
        int minDistance = -1;

        if(scorpion != null ) {
            minDistance = (scorpion.getX() - x) * (scorpion.getX() - x)
                    + (scorpion.getY() - y) * (scorpion.getY() - y);
            result[0] = scorpion.getX();
            result[1] = scorpion.getY();
        }
        if(snake != null ){
            int tempDistance = (snake.getX() - x) * (snake.getX() - x)
                    + (snake.getY() - y) * (snake.getY() - y);
            if( tempDistance < minDistance ){
                minDistance = tempDistance;
                result[0] = snake.getX();
                result[1] = snake.getY();
            }
            else if( tempDistance == minDistance ){ // compare the distance to the grandpa
                if( (grandpa.getX()-snake.getX())*(grandpa.getX()-snake.getX()) +
                        (grandpa.getY()-snake.getY())*(grandpa.getY()-snake.getY())
                        < (grandpa.getX()-result[0])*(grandpa.getX()-result[0]) +
                        (grandpa.getY()-result[1])*(grandpa.getY()-result[1])) {
                    result[0] = snake.getX();
                    result[1] = snake.getY();
                }
            }
        }

        for( Creatures c : enemy){
            int tempDistance = (c.getX() - x)*(c.getX() - x)
                    + (c.getY() - y)*(c.getY() - y);
            if( tempDistance < minDistance ){
                minDistance = tempDistance;
                result[0] = c.getX();
                result[1] = c.getY();
            }
            else if( tempDistance == minDistance ){
                if( (grandpa.getX()-c.getX())*(grandpa.getX()-c.getX()) +
                        (grandpa.getY()-c.getY())*(grandpa.getY()-c.getY())
                        < (grandpa.getX()-result[0])*(grandpa.getX()-result[0]) +
                        (grandpa.getY()-result[1])*(grandpa.getY()-result[1])) {
                    result[0] = c.getX();
                    result[1] = c.getY();
                }
            }

        }
        return result;
    }

    private void initFormation(){

    }

    private void initGround(){
        // 背景分辨率为 1280*720 , 即16:9 。 每个格子的边长为80分辨率
        URL loc = this.getClass().getClassLoader().getResource("背景2.png");
        ImageIcon iia = new ImageIcon(loc); // Image是抽象类，所以只能通过ImageIcon来创建
        backgroundImage = iia.getImage();

    }

    private void initCreature(){

        grandpa = new Grandpa(0,MAX_Y/2,this);
        grandpa.setImage("爷爷.png");
        gourdDolls = new GourdDolls[7]; // 默认为鹤翼阵型
        gourdDolls[0] = new GourdDolls(0,1*4,this);
        gourdDolls[1] = new GourdDolls(1*SPACE/STEP,2*SPACE/STEP,this);
        gourdDolls[2] = new GourdDolls(2*SPACE/STEP,3*SPACE/STEP,this);
        gourdDolls[3] = new GourdDolls(3*SPACE/STEP,4*SPACE/STEP,this);
        gourdDolls[4] = new GourdDolls(2*SPACE/STEP,5*SPACE/STEP,this);
        gourdDolls[5] = new GourdDolls(1*SPACE/STEP,6*SPACE/STEP,this);
        gourdDolls[6] = new GourdDolls(0,7*SPACE/STEP,this);
        gourdDolls[0].setImage("大娃.png");
        gourdDolls[1].setImage("二娃.png");
        gourdDolls[2].setImage("三娃.png");
        gourdDolls[3].setImage("四娃.png");
        gourdDolls[4].setImage("五娃.png");
        gourdDolls[5].setImage("六娃.png");
        gourdDolls[6].setImage("七娃.png");

        snake = new SnakeQueen(MAX_X,MAX_Y/2,this);
        snake.setImage("蛇精.png");

    }

    private void readFormation(){


    }

    private void actionCreature(){
        Thread[] gourddollsThreads = new Thread[7];
        Thread grandpaThread = new Thread(grandpa);
        Thread snakeThread = new Thread(snake);

        for(int i = 0 ; i < 7  ; ++ i) {
            gourddollsThreads[i] = new Thread(gourdDolls[i]);
            gourddollsThreads[i].start();
        }

        grandpaThread.start();
        snakeThread.start();
    }

    private void paintGround(Graphics g){

        // TODO: add all drawImage() here

        g.drawImage(backgroundImage,0,0, PIXEL_WIDTH, PIXEL_HEIGHT,this);

        g.drawImage(grandpa.getImage(),grandpa.getX()* STEP, grandpa.getY()* STEP, SPACE, SPACE,this);

        for(int i = 0 ; i<7 ; ++ i)
            g.drawImage(gourdDolls[i].getImage(),
                    gourdDolls[i].getX()* STEP,gourdDolls[i].getY()* STEP,
                    SPACE, SPACE,this);

        g.drawImage(snake.getImage(),snake.getX()* STEP,snake.getY()* STEP,
                SPACE, SPACE,this);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintGround(g);


    }

    @Override
    public void repaint(){

        super.repaint();
    }

    public enum Status { BEGIN, FIGHTING, REPLAYING };

    class TAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e){
            int key = e.getKeyCode();

            if(key == KeyEvent.VK_SPACE){
                if( status == BEGIN ){
                    status = FIGHTING;
                }
            }
            else if(key == KeyEvent.VK_R){
                if( status == BEGIN ){
                    status = REPLAYING;
                }
            }
            else if(key == KeyEvent.VK_P){
                stop = !stop;
            }

            System.out.println("Status="+status.toString()+" isStop="+stop);

            repaint();
        }
    }

}
