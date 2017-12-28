package nju.java;

import nju.java.things.creatures.Creatures;
import nju.java.things.creatures.GourdDolls;
import nju.java.things.creatures.Grandpa;
import nju.java.things.creatures.enemies.ScorpionKing;
import nju.java.things.creatures.enemies.SnakeQueen;
import nju.java.things.creatures.enemies.Toad;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.Array;
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
    private ArrayList< Creatures>  goodCreatures = null;

    // 蝎子精和蛇精是唯一的
    private SnakeQueen snake = null;
    private ScorpionKing scorpion = null;
    private Toad[] toads = null; // 小马仔们
    private ArrayList< Creatures> badCreatures = null;

    private ArrayList<Creatures> deadCreatures = null; // 记录死亡的生物
    private ArrayList<Thread> creaturesThreads = null;

    private Timer timer ;
    private ActionListener timerTask ;

    public Ground(){
        addKeyListener(new TAdapter());
        setFocusable(true);


        initGround();
        initCreature();

        initTimer();
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

    // Creatures API
    public ArrayList<Creatures> getGoodCreatures(){
        return goodCreatures;
    }

    public ArrayList< Creatures> getBadCreatures(){
        return badCreatures;
    }


    // Creatures API: get grandpa's location for toads
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

    // Creatures API: get nearest toads's location for gourdDolls
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

        for( Creatures c : toads){
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


    private void initGround(){
        // 背景分辨率为 1280*720 , 即16:9 。 每个格子的边长为80分辨率
        URL loc = this.getClass().getClassLoader().getResource("背景2.png");
        ImageIcon iia = new ImageIcon(loc); // Image是抽象类，所以只能通过ImageIcon来创建
        backgroundImage = iia.getImage();

    }

    private void initCreature(){

        // 初始化爷爷
        grandpa = new Grandpa(0,MAX_Y/2,this);
        grandpa.setImage("爷爷.png");

        // 初始化葫芦娃
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

        // 把爷爷和葫芦娃添加到队列中
        goodCreatures = new ArrayList<Creatures>();
        goodCreatures.add(grandpa);
        for( GourdDolls g : gourdDolls )
            goodCreatures.add(g);

        // 初始化蛇精
        snake = new SnakeQueen(MAX_X,MAX_Y/2-SPACE/STEP,this);
        snake.setImage("蛇精.png");

        // 初始化蝎子精
        scorpion =  new ScorpionKing(MAX_X,MAX_Y/2+SPACE/STEP,this);
        scorpion.setImage("蝎子精.png");

        toads = new Toad[7];
        for(int i = 0 ; i < 7 ; ++ i){

            if( i != 3 && i != 5)
                toads[i] = new Toad(MAX_X,i*SPACE/STEP,this);
            else if (i == 3 )
                toads[i] = new Toad(MAX_X,7*SPACE/STEP,this);
            else
                toads[i] = new Toad(MAX_X,8*SPACE/STEP,this);

            toads[i].setImage("蛤蟆精.png");
        }

        badCreatures = new ArrayList<Creatures>();
        badCreatures.add(snake);
        badCreatures.add(scorpion);
        for(Creatures c : toads)
            badCreatures.add(c);

        deadCreatures = new ArrayList<Creatures>();
    }

    private void initTimer(){
        timerTask = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO:添加更多定时检查
                checkCreature();
            }
        };
        timer = new Timer(TIME_CLOCK,timerTask);
        timer.start();
    }

    private void actionCreature(){

        creaturesThreads = new ArrayList<Thread>();
        for(Creatures c : goodCreatures)
            creaturesThreads.add(new Thread(c));
        for(Creatures c : badCreatures)
            creaturesThreads.add(new Thread(c));
        for(Thread t : creaturesThreads)
            t.start();
    }

    // 检查两个Creatures列表,将死了的生物拖到deadCreatures中。如果出现一方已经死亡，暂停游戏
    private void checkCreature(){

        /*System.out.println("检查生物:3个列表中的生物个数为:"
                +goodCreatures.size()+" "
                +badCreatures.size()+" "
                +deadCreatures.size());*/
        if( goodCreatures.isEmpty() || badCreatures.isEmpty() ){
            status = BEGIN;
            System.out.println("状态转为BEGIN");
            stop = true;
            // TODO:弹出游戏信息提示
        }
        for( Creatures c : goodCreatures ){
            if( c.isDead() ){
                c.setImage("葫芦娃墓碑.png");
                deadCreatures.add(c);
                goodCreatures.remove(c);
            }
        }
        for(Creatures c:badCreatures){
            if( c.isDead() ){
                c.setImage("妖怪墓碑.png");
                deadCreatures.add(c);
                badCreatures.remove(c);
            }
        }

    }

    private void paintGround(Graphics g){

        // TODO: add all drawImage() here

        g.drawImage(backgroundImage,0,0, PIXEL_WIDTH, PIXEL_HEIGHT,this);

        for( Creatures c : goodCreatures )
            g.drawImage(c.getImage(),c.getX()*STEP,c.getY()*STEP,SPACE,SPACE,this);

        for(Creatures c : badCreatures)
            g.drawImage(c.getImage(),c.getX()*STEP,c.getY()*STEP,SPACE,SPACE,this);

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

            if(key == KeyEvent.VK_SPACE){ // 开始
                if( status == BEGIN ){
                    status = FIGHTING;
                    System.out.println("状态从BEGIN转为FIGHTING");
                }
            }
            else if(key == KeyEvent.VK_S){ // 回放
                if( status == BEGIN ){
                    status = REPLAYING;
                    System.out.println("状态从BEGIN转为REPLAYING");
                }
            }
            else if(key == KeyEvent.VK_P){ // 暂停
                stop = !stop;
            }
            else if(key == KeyEvent.VK_R){ // 回到主页面
                status = BEGIN;
                System.out.println("状态转为BEGIN");
            }

            //System.out.println("Status="+status.toString()+" isStop="+stop);

            repaint();
        }
    }

    // Creature API : 攻击成功返回boolean
    // 检查的重点：距离
    public boolean requireAttack(Creatures attacker, Creatures attacked){
        int temp = DISTANCE/STEP;
        int x1 = attacked.getX() , x2 = attacker.getX();
        int y1 = attacked.getY() , y2 = attacker.getY();
        int distance = (x1>x2?x1-x2:x2-x1) + (y1>y2?y1-y2:y2-y1);
        if( distance > 0 && distance <= DISTANCE/STEP ){ // 可以位移
            // 对双方的血量进行减少
            attacker.setBlood(attacker.getBlood()-attacked.getPower()/2);
            attacked.setBlood(attacked.getBlood()-attacker.getPower());
            return true;
        }
        else
            return false;
    }

    // Creature API : 攻击成功返回boolean
    // 检查的重点：是否重合，是否越界
    public boolean requireWalk(Creatures c, int x_off, int y_off){
        int newX = c.getX()+x_off, newY = c.getY() + y_off;
        if(newX < 0 || newX > MAX_X || newY<0 || newY > MAX_Y )
            return false;
        for(Creatures i : goodCreatures ){
            if(i == c)
                continue;
            if( i.getX() == newX && i.getY() == newY ){
                return false;
            }
        }
        for( Creatures i : badCreatures){
            if(i == c)
                continue;
            if( i.getX() == newX && i.getY() == newY ){
                return false;
            }
        }
        c.setX(newX);
        c.setY(newY);
        return true;
    }

}
