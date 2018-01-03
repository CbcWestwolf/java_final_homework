package nju.java;

import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;
import nju.java.creatures.good.Good;
import nju.java.creatures.good.GourdDolls;
import nju.java.creatures.good.Grandpa;
import nju.java.creatures.bad.ScorpionKing;
import nju.java.creatures.bad.SnakeQueen;
import nju.java.creatures.bad.Toad;
import nju.java.tools.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by cbcwestwolf on 2017/12/26.
 */
public class Ground extends JPanel {

    // 常量定义
    public static final int STEP = 5; // 每次移动的距离
    public static final int SPACE = 16*STEP ; // 图片的边长   (必须是STEP的整数倍）
    public static final int DISTANCE = 12*STEP; // 攻击范围
    public static final int TIME_CLOCK = 100; // 线程休眠时间 （毫秒）
    public static final int REPLAY_CLOCK = 1;
    public static final int PIXEL_HEIGHT = 720; // 上下的高度（像素点）
    public static final int PIXEL_WIDTH = 1280; // 左右的长度
    public static final int MAX_X = (PIXEL_WIDTH-SPACE) / STEP ;
    public static final int MAX_Y = (PIXEL_HEIGHT-SPACE) / STEP ;

    // 读写的文件
    private File readFile = null;
    private FileReader fileReader;
    private BufferedReader bufferedReader ;

    // 状态定义
    private static boolean stop; // 玩家是否按下暂停键
    private static Status status = Status.WELCOME; // 4种状态：未开始，打斗中，回放中

    // 背景图片
    private Image backgroundImage = null; // 背景图片
    private Image grandpaImage = null;
    private Image[] gourddollsImage = null;
    private Image snakeImage = null;
    private Image scorpionImage = null;
    private Image toadImage = null;
    private Image goodTombstoneImage = null;
    private Image badTombstoneImage = null;

    // 正方
    private Grandpa grandpa = null;// 爷爷是唯一的
    private GourdDolls [] gourdDolls = null;

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

    public Ground(){
        setFocusable(true); // 设置可见
        addKeyListener(new TAdapter());// 添加键盘监视器
        loadPictures(); // 加载图像
    }

    public static boolean isStop() {
        return stop;
    }

    public static Status getStatus() {
        return status;
    }

    // Creatures API
    public ArrayList<Good> getGoodCreatures(){
        return goodCreatures;
    }

    public ArrayList<Bad> getBadCreatures(){
        return badCreatures;
    }

    private void loadPictures(){

        // 背景分辨率为 1280*720 , 即16:9 。 每个格子的边长为80分辨率
        URL url = this.getClass().getClassLoader().getResource("背景2.png");
        ImageIcon imageIcon = new ImageIcon(url); // Image是抽象类，所以只能通过ImageIcon来创建
        backgroundImage = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("爷爷.png");
        imageIcon = new ImageIcon(url);
        grandpaImage = imageIcon.getImage();

        gourddollsImage = new Image[7];

        for(int i = 0 ; i < 7 ; ++ i){
            url = this.getClass().getClassLoader().getResource(GourddollsName.values()[i].toString()+".png");
            imageIcon = new ImageIcon(url);
            gourddollsImage[i] = imageIcon.getImage();
        }

        url = this.getClass().getClassLoader().getResource("蝎子精.png");
        imageIcon = new ImageIcon(url);
        scorpionImage = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("蛇精.png");
        imageIcon = new ImageIcon(url);
        snakeImage = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("蛤蟆精.png");
        imageIcon = new ImageIcon(url);
        toadImage = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("葫芦娃墓碑.png");
        imageIcon = new ImageIcon(url);
        goodTombstoneImage = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("妖怪墓碑.png");
        imageIcon = new ImageIcon(url);
        badTombstoneImage = imageIcon.getImage();
    }

    class TAdapter extends KeyAdapter {
        @Override
        public synchronized void keyPressed(KeyEvent event){
            int key = event.getKeyCode();

            if (key == KeyEvent.VK_SPACE){ // 开始
                if( status == Status.WELCOME){

                    /*初始化生物、计时器和线程*/
                    status = Status.FIGHTING;
                    System.out.println("状态从WELCOME转为FIGHTING");
                    initThread();
                    initTimer(TIME_CLOCK);
                }
            }
            else if(key == KeyEvent.VK_L){ // 回放

                /**
                 1.添加读入记录
                 2.根据记录更新某个生物体的位置、状态
                 3.repaint();
                 3.休眠一段时间
                 4.继续读入记录
                 */

                if( status == Status.WELCOME || status == Status.FINISHED ) {
                    status = Status.REPLAYING;
                    initThread();
                    initTimer(REPLAY_CLOCK);
                    System.out.println("已经是REPLAYING");

                    int flag = 1;
                    JFileChooser jFileChooser = null;
                    FileFilterTest fileFilter = null ;
                    while(flag != JFileChooser.APPROVE_OPTION) {
                        fileFilter = new FileFilterTest();
                        jFileChooser = new JFileChooser(new File("save"));
                        jFileChooser.setFileFilter(fileFilter);
                        jFileChooser.setDialogTitle("选择作战记录（文件名即为作战时间）");
                        flag = jFileChooser.showDialog(null, null);
                    }
                    readFile = jFileChooser.getSelectedFile();

                }

            }
            else if(key == KeyEvent.VK_P){ // 暂停
                stop = !stop;
                if(stop)
                    System.out.println("暂停！");
                else
                    System.out.println("解除暂停！");
            }
            else if( key == KeyEvent.VK_ESCAPE )
                System.exit(0);

            //System.out.println("Status="+status.toString()+" isStop="+stop);

        }
    }

    private void initCreature(){

        // 初始化爷爷
        grandpa = new Grandpa(4*SPACE/STEP,MAX_Y/2,this);
        grandpa.setImage(grandpaImage);

        // 初始化葫芦娃
        gourdDolls = new GourdDolls[7]; // 默认为鹤翼阵型for
        for(int i = 0 ; i < 7 ; ++ i) {
            gourdDolls[i] = new GourdDolls( (7 - Math.abs(i-3))*SPACE/STEP, (i+1)*SPACE/STEP,this,i);
            gourdDolls[i].setImage(gourddollsImage[i]);
        }

        // 把爷爷和葫芦娃添加到队列中
        goodCreatures = new ArrayList<Good>();
        goodCreatures.clear();
        goodCreatures.add(grandpa);
        for( GourdDolls g : gourdDolls ) {
            goodCreatures.add(g);
            //System.out.println(g);
        }

        // 初始化蛇精
        snake = new SnakeQueen(MAX_X-3*SPACE/STEP,MAX_Y/2-SPACE/STEP,this);
        snake.setImage(snakeImage);

        // 初始化蝎子精
        scorpion =  new ScorpionKing(MAX_X-3*SPACE/STEP,MAX_Y/2+SPACE/STEP,this);
        scorpion.setImage(scorpionImage);

        toads = new Toad[7];
        for(int i = 0 ; i < 7 ; ++ i){
            if( i != 3 && i != 5)
                toads[i] = new Toad(MAX_X-3*SPACE/STEP,i*SPACE/STEP,this,i);
            else if (i == 3 )
                toads[i] = new Toad(MAX_X-3*SPACE/STEP,7*SPACE/STEP,this,i);
            else
                toads[i] = new Toad(MAX_X-3*SPACE/STEP,8*SPACE/STEP,this,i);
            toads[i].setImage(toadImage);
        }

        badCreatures = new ArrayList<Bad>();
        badCreatures.clear();
        badCreatures.add(snake);
        badCreatures.add(scorpion);
        for(Bad c : toads) {
            badCreatures.add(c);
            //System.out.println(c);
        }

        deadCreatures = new ArrayList<Creatures>();
        deadCreatures.clear();
    }

    private void initTimer(int time){
        timerTask = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                check();
                repaint();
            }
        };
        timer = new Timer(time,timerTask);
        timer.start();
    }

    private void initThread() {

        initCreature(); // 初始化生物

        creaturesThreads = new ArrayList<Thread>();
        if( ! goodCreatures.isEmpty() )
            for (Creatures c : goodCreatures) {
                creaturesThreads.add(new Thread(c));
                c.setThread(creaturesThreads.get(creaturesThreads.size() - 1));
                System.out.println(c.toString()+"线程初始化");
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
    }

    // 检查两个Creatures列表,将死了的生物拖到deadCreatures中。如果出现一方已经死亡，暂停游戏
    public synchronized void check(){

        if( status == Status.FIGHTING ) {
            Iterator<Good> g = goodCreatures.iterator();
            while (g.hasNext()) {
                Good temp = g.next();
                if (temp.isDead()) {
                    temp.setImage(goodTombstoneImage);
                    deadCreatures.add(temp);
                    g.remove();
                }
            }

            Iterator<Bad> b = badCreatures.iterator();
            while (b.hasNext()) {
                Bad temp = b.next();
                if (temp.isDead()) {
                    temp.setImage(badTombstoneImage);
                    deadCreatures.add(temp);
                    b.remove();
                }
            }

            FileOperation.writeFile(goodCreatures,badCreatures,deadCreatures);


            if( goodCreatures.isEmpty() || badCreatures.isEmpty() ) {
                status = Status.FINISHED;
                System.out.println("转为FINISHED");
                for(Thread t : creaturesThreads)
                    t.suspend();
                // TODO:弹出游戏信息提示
                return;
            }
        }

        if( status == Status.REPLAYING && (! stop) ){
            try {
                String str = null;
                if (readFile == null) {
                    //System.out.println("找不到文件");
                    return;
                }
                if (fileReader == null) {
                    synchronized (readFile) {
                        fileReader = new FileReader(readFile);
                        bufferedReader = new BufferedReader(fileReader);
                    }
                }

                if ((str = bufferedReader.readLine()) != null) {
                    replaying(str);
                } else {
                    bufferedReader.close();
                    fileReader.close();
                    for( Thread t : creaturesThreads )
                        t.suspend();
                    status = Status.CLOSE;
                    System.out.println("状态转为CLOSE");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void paintImage(Graphics g) {

        g.drawImage(backgroundImage, 0, 0, PIXEL_WIDTH, PIXEL_HEIGHT, this);

        GourddollsName name = GourddollsName.大娃;
        for(int i = 0 ; i < 7 ; ++ i) {
            g.drawImage(gourddollsImage[i], i * (20 + SPACE), PIXEL_HEIGHT, SPACE, SPACE, this);
            g.drawString(GourddollsName.values()[i].toString(),100*i+30,PIXEL_HEIGHT+SPACE+20);
        }
        g.drawImage(grandpaImage,7*(20+SPACE),PIXEL_HEIGHT,SPACE,SPACE,this);
        g.drawString("爷爷",100*7+30,PIXEL_HEIGHT+SPACE+20);
        g.drawImage(scorpionImage,8*(20+SPACE),PIXEL_HEIGHT,SPACE,SPACE,this);
        g.drawString("蝎子大王",100*8+20,PIXEL_HEIGHT+SPACE+20);
        g.drawImage(snakeImage,9*(20+SPACE),PIXEL_HEIGHT,SPACE,SPACE,this);
        g.drawString("蛇精",100*9+30,PIXEL_HEIGHT+SPACE+20);
        g.drawImage(toadImage,10*(20+SPACE),PIXEL_HEIGHT,SPACE,SPACE,this);
        g.drawString("小马仔",100*10+25,PIXEL_HEIGHT+SPACE+20);
        g.drawImage(goodTombstoneImage,11*(20+SPACE),PIXEL_HEIGHT,SPACE,SPACE,this);
        g.drawString("正方墓",100*11+25,PIXEL_HEIGHT+SPACE+20);
        g.drawImage(badTombstoneImage,12*(20+SPACE),PIXEL_HEIGHT,SPACE,SPACE,this);
        g.drawString("反方墓",100*12+25,PIXEL_HEIGHT+SPACE+20);

        if( status == Status.WELCOME )
            g.drawString("按下空格开始游戏",SPACE,PIXEL_HEIGHT/2);
        if( status == Status.WELCOME || status == Status.FINISHED ) {
            g.drawString("按下L开始回放", SPACE, PIXEL_HEIGHT / 2 + 20);
        }
        if( status == Status.FIGHTING || status == Status.REPLAYING) {
            if (stop)
                g.drawString("按下P恢复", SPACE, PIXEL_HEIGHT / 2 + 40);
            else
                g.drawString("按下P暂停", SPACE, PIXEL_HEIGHT / 2 + 40);
        }
        g.drawString("按下Esc退出",SPACE,PIXEL_HEIGHT / 2 + 60);

        if( status != Status.WELCOME ) {
            if (goodCreatures != null)
                for (Good c : goodCreatures) {
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
                }
            if (badCreatures != null)
                for (Bad c : badCreatures) {
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
                }
            if (deadCreatures != null)
                for (Creatures c : deadCreatures) {
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
                }
        }
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintImage(g);
        //System.out.println("调用paint()");
    }

    // Creature API : 攻击成功返回boolean
    // 检查的重点：距离
    public boolean requireAttack(Creatures attacker, Creatures attacked){
        int distance = Creatures.distance(attacker,attacked);
        if( distance > 0 && distance <= DISTANCE/STEP ){ // 可以攻击
            // 对双方的血量进行减少
            int attackerBlood = attacker.getBlood()-attacked.getPower()/2;
            int attackedBlood = attacked.getBlood()-attacker.getPower();
            System.out.println(attacker.toString()+"血量降为"+attackerBlood+" "
                    +attacked.toString()+" 血量降为"+attackedBlood);
            attacker.setBlood(attackerBlood);
            attacked.setBlood(attackedBlood);
            return true;
        }
        else
            return false;
    }

    // Creature API : 攻击成功返回boolean
    // 检查的重点：是否重合，是否越界，是否只有一个值为1
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

    private synchronized void replaying(String str) {

        String name = null;
        int x = -1, y = -1;
        boolean isAlive = false;

        // System.out.println(str);

        String[] temp = str.split(" ");
        name = temp[0];
        x = Integer.parseInt(temp[1]);
        y = Integer.parseInt(temp[2]);
        isAlive = (temp[3].equals("0")) ? false : true;

//        //System.out.println(name + " " + x + " " + y + " " + isAlive);
//        if( ! isAlive)
//            System.out.println(name + " " + x + " " + y + " " + isAlive);

        if (name.equals("爷爷") ) {
            grandpa.setX(x);
            grandpa.setY(y);
            if (isAlive)
                grandpa.setImage(grandpaImage);
            else
                grandpa.setImage(goodTombstoneImage);
        } else if (name.equals( "蝎子大王")) {
            scorpion.setX(x);
            scorpion.setY(y);
            if (isAlive)
                scorpion.setImage(scorpionImage);
            else
                scorpion.setImage(badTombstoneImage);
        } else if (name.equals("蛇精")) {
            snake.setX(x);
            snake.setY(y);
            if (isAlive)
                snake.setImage(snakeImage);
            else
                snake.setImage(badTombstoneImage);
        } else if(name.substring(1).equals("娃")) {

            GourddollsName g = GourddollsName.valueOf(name);
            int num = g.ordinal();

            gourdDolls[num].setX(x);
            gourdDolls[num].setY(y);
            if(isAlive)
                gourdDolls[num].setImage(name+".png");
            else
                gourdDolls[num].setImage(goodTombstoneImage);

        }
        else {
            ArrayList<String> ss = new ArrayList<String>();
            int num = 0;
            for(String sss:name.replaceAll("[^0-9]", ",").split(",")){
                if (sss.length()>0)
                    num = Integer.parseInt(sss);
            }
            // System.out.println(num);
            num -- ;
            toads[num].setX(x);
            toads[num].setY(y);
            if (isAlive)
                toads[num].setImage(toadImage);
            else
                toads[num].setImage(badTombstoneImage);
        }
        repaint();
    }

}
