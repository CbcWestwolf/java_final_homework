package nju.java;

import nju.java.creatures.Creatures;
import nju.java.creatures.Good.Good;
import nju.java.creatures.Good.GourdDolls;
import nju.java.creatures.Good.Grandpa;
import nju.java.creatures.bad.Bad;
import nju.java.creatures.bad.ScorpionKing;
import nju.java.creatures.bad.SnakeQueen;
import nju.java.creatures.bad.Toad;
import nju.java.tools.FileOperation;
import nju.java.tools.GourddollsName;
import nju.java.tools.Status;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import static nju.java.tools.ConstantValue.*;
import static nju.java.tools.ConstantValue.SPACE;
import static nju.java.tools.ConstantValue.STEP;

/***
 * @author cbcwestwolf
 * <br>
 * BackEnd类用于实现游戏的内部逻辑 <br>
 * 游戏的绘图逻辑由Ground类实现<br>
 * BackEnd类被Main调用<br>
 *
 * @see nju.java.Ground
 * @see nju.java.Main
 */
public class BackEnd extends JFrame {

    Ground ground = null;

    // 状态定义
    public static boolean stop; // 玩家是否按下暂停键
    public static Status status = Status.WELCOME; // 4种状态：未开始，打斗中，回放中

    // 正方
    private Grandpa grandpa = null;// 爷爷是唯一的
    private GourdDolls[] gourdDolls = null;

    // 反方
    // 蝎子精和蛇精是唯一的
    private SnakeQueen snake = null;
    private ScorpionKing scorpion = null;
    private Toad[] toads = null; // 小马仔们

    private ArrayList<Good> goodCreatures = null;
    private ArrayList<Bad> badCreatures = null;

    // 死者
    private ArrayList<Creatures> deadCreatures = null; // 记录死亡的生物

    // 线程，每个生物对应一个线程
    private ArrayList<Thread> creaturesThreads = null;;

    // 计时器和事件监听器
    private Timer timer ;
    private ActionListener timerTask ;

    public void setGround(Ground ground) {
        this.ground = ground;
    }


    public static boolean isStop() {
        return stop;
    }

    public static Status getStatus() {
        return status;
    }

    public Ground getGround() {
        return ground;
    }

    public ArrayList<Good> getGoodCreatures(){
        return goodCreatures;
    }

    public ArrayList<Bad> getBadCreatures(){
        return badCreatures;
    }

    public ArrayList<Creatures> getDeadCreatures() {
        return deadCreatures;
    }

    /**
     * 初始化所有角色，包括位置和图片
     */
    private void initCreature(){

        // 初始化爷爷
        grandpa = new Grandpa(4*SPACE/STEP,MAX_Y/2,this);
        grandpa = new Grandpa(4*SPACE/STEP,MAX_Y/2,this);
        grandpa.setImage(ground.getGrandpaImage());

        // 初始化葫芦娃
        gourdDolls = new GourdDolls[7]; // 默认为鹤翼阵型for
        for(int i = 0 ; i < 7 ; ++ i) {
            gourdDolls[i] = new GourdDolls( (7 - Math.abs(i-3))*SPACE/STEP, (i+1)*SPACE/STEP,this,i);
            gourdDolls[i].setImage(ground.getGourddollsImage()[i]);
        }

        // 把爷爷和葫芦娃添加到队列中
        goodCreatures = new ArrayList<Good>();
        goodCreatures.add(grandpa);
        for( GourdDolls g : gourdDolls ) {
            goodCreatures.add(g);

        }

        // 初始化蛇精
        snake = new SnakeQueen(MAX_X-3*SPACE/STEP,MAX_Y/2-SPACE/STEP,this);
        snake.setImage(ground.getSnakeImage());

        // 初始化蝎子精
        scorpion =  new ScorpionKing(MAX_X-3*SPACE/STEP,MAX_Y/2+SPACE/STEP,this);
        scorpion.setImage(ground.getScorpionImage());

        toads = new Toad[7];
        for(int i = 0 ; i < 7 ; ++ i){
            if( i != 3 && i != 5)
                toads[i] = new Toad(MAX_X-3*SPACE/STEP,i*SPACE/STEP,this,i);
            else if (i == 3 )
                toads[i] = new Toad(MAX_X-3*SPACE/STEP,7*SPACE/STEP,this,i);
            else
                toads[i] = new Toad(MAX_X-3*SPACE/STEP,8*SPACE/STEP,this,i);
            toads[i].setImage(ground.getToadImage());
        }

        badCreatures = new ArrayList<Bad>();
        badCreatures.add(snake);
        badCreatures.add(scorpion);
        for(Bad c : toads) {
            badCreatures.add(c);

        }

        deadCreatures = new ArrayList<Creatures>();
    }

    /**
     * 初始化一个Timer对象
     * @param time 检查的时间周期
     */
    public void initTimer(int time){
        timerTask = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                check();
                ground.repaint();
            }
        };
        timer = new Timer(time,timerTask);
        timer.start();
    }

    /**
     * 初始化角色，并为每个角色对应一个线程
     */
    public void initThread() {

        initCreature();

        creaturesThreads = new ArrayList<Thread>();
        if( ! goodCreatures.isEmpty() )
            for (Creatures c : goodCreatures) {
                creaturesThreads.add(new Thread(c));
                c.setThread(creaturesThreads.get(creaturesThreads.size() - 1));
            }
        if( ! badCreatures.isEmpty() )
            for (Creatures c : badCreatures) {
                creaturesThreads.add(new Thread(c));
                c.setThread(creaturesThreads.get(creaturesThreads.size()-1));
            }
        if( ! deadCreatures.isEmpty() )
            for (Creatures c : deadCreatures) {
                creaturesThreads.add(new Thread(c));
                c.setThread(creaturesThreads.get(creaturesThreads.size()-1));
            }
        for (Thread t : creaturesThreads)
            t.start();
        //System.out.println("初始化所有线程");
    }

    /**
     * 检查两个Creatures列表,将死了的生物拖到deadCreatures中。如果出现一方已经死亡，暂停游戏
     */
    @SuppressWarnings("deprecation")
    public synchronized void check(){

        if( status == Status.FIGHTING ) {
            Iterator<Good> g = goodCreatures.iterator();
            while (g.hasNext()) {
                Good temp = g.next();
                if (temp.isDead()) {
                    temp.setImage(ground.getGoodTombstoneImage());
                    deadCreatures.add(temp);
                    g.remove();
                }
            }

            Iterator<Bad> b = badCreatures.iterator();
            while (b.hasNext()) {
                Bad temp = b.next();
                if (temp.isDead()) {
                    temp.setImage(ground.getBadTombstoneImage());
                    deadCreatures.add(temp);
                    b.remove();
                }
            }

            try {
                FileOperation.writeFile(goodCreatures, badCreatures, deadCreatures);
            }
            catch (IOException e){
//                e.printStackTrace();
            }

            if( goodCreatures.isEmpty() || badCreatures.isEmpty() ) {
                status = Status.FINISHED;


                for (Thread t : creaturesThreads)
                    t.suspend();

                return;
            }
        }

        if( status == Status.REPLAYING && (! stop) ) {

            String str = null;
            if (FileOperation.getReadFile() == null) {
                return;
            }

            str = FileOperation.getNextString();
            if (str != null) {
                replaying(str);
            } else {
                for (Thread t : creaturesThreads)
                    t.suspend();

                status = Status.CLOSE;
            }


        }
    }

    /**
     * 该方法接受一个来自角色的攻击请求，判断该攻击是否合法，如果合法就进行攻击。<br>
     * 返回攻击是否成功 <br>
     * 检查的内容是距离
     * @param attacker 攻击者
     * @param attacked 被攻击者
     * @return 攻击是否成功
     */
    public boolean requireAttack(Creatures attacker, Creatures attacked){
        int distance = Creatures.distance(attacker,attacked);
        if( distance > 0 && distance <= DISTANCE/STEP ){ // 可以攻击
            // 对双方的血量进行减少
            int attackerBlood = attacker.getBlood()-attacked.getPower()/2;
            int attackedBlood = attacked.getBlood()-attacker.getPower();
            // System.out.println(attacker.toString()+"血量降为"+attackerBlood+" "+attacked.toString()+" 血量降为"+attackedBlood);
            attacker.setBlood(attackerBlood);
            attacked.setBlood(attackedBlood);
            return true;
        }
        else
            return false;
    }

    /**
     * 该方法接受一个来自角色的移动请求，判断该移动是否合法，如果合法就进行移动。<br>
     * 返回移动是否成功
     * @param c 发出移动请求的角色
     * @param x_off x方向移动的距离
     * @param y_off y方向移动的距离
     * @return 移动是否成功
     */
    public boolean requireWalk(Creatures c, int x_off, int y_off){
        if( x_off * y_off != 0 || Math.abs(x_off+y_off) != 1 ) // 是否只有一个值为1
            return false;
        int newX = c.getX()+x_off, newY = c.getY() + y_off;
        if(newX < 0 || newX > MAX_X || newY<0 || newY > MAX_Y ) // 是否越界
            return false;

        // 是否重合
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


    /**
     * 用于将文件中读出的信息写入各个角色列表中
     * @param str str为从文件中读取的信息
     */
    private synchronized void replaying(String str) {

        String name = null;
        int x = -1, y = -1;
        boolean isAlive = false;

        String[] temp = str.split(" ");
        if( temp.length != 4 )
            return;
        name = temp[0];
        x = Integer.parseInt(temp[1]);
        y = Integer.parseInt(temp[2]);
        isAlive = (temp[3].equals("0")) ? false : true;

        if (name.equals("爷爷") ) {
            grandpa.setX(x);
            grandpa.setY(y);
            if (isAlive)
                grandpa.setImage(ground.getGrandpaImage());
            else
                grandpa.setImage(ground.getGoodTombstoneImage());
        } else if (name.equals( "蝎子大王")) {
            scorpion.setX(x);
            scorpion.setY(y);
            if (isAlive)
                scorpion.setImage(ground.getScorpionImage());
            else
                scorpion.setImage(ground.getBadTombstoneImage());
        } else if (name.equals("蛇精")) {
            snake.setX(x);
            snake.setY(y);
            if (isAlive)
                snake.setImage(ground.getSnakeImage());
            else
                snake.setImage(ground.getBadTombstoneImage());
        } else if(name.substring(1).equals("娃")) {

            GourddollsName g = GourddollsName.valueOf(name);
            int num = g.ordinal();

            gourdDolls[num].setX(x);
            gourdDolls[num].setY(y);
            if(isAlive)
                gourdDolls[num].setImage(name+".png");
            else
                gourdDolls[num].setImage(ground.getGoodTombstoneImage());

        }
        else if(name.substring(0,2).equals("马仔")){
            ArrayList<String> ss = new ArrayList<String>();
            int num = 0;
            for(String sss:name.replaceAll("[^0-9]", ",").split(",")){
                if (sss.length()>0)
                    num = Integer.parseInt(sss);
            }
            num--;
            if( num >= 7 || num < 0){
                System.out.println(name);
            }
            toads[num].setX(x);
            toads[num].setY(y);
            if (isAlive)
                toads[num].setImage(ground.getToadImage());
            else
                toads[num].setImage(ground.getBadTombstoneImage());
        }
        ground.repaint();
    }

}
