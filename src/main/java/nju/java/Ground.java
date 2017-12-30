package nju.java;

import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;
import nju.java.creatures.good.Good;
import nju.java.creatures.good.GourdDolls;
import nju.java.creatures.good.Grandpa;
import nju.java.creatures.bad.ScorpionKing;
import nju.java.creatures.bad.SnakeQueen;
import nju.java.creatures.bad.Toad;

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

import static nju.java.Ground.Status.*;

/**
 * Created by cbcwestwolf on 2017/12/26.
 */
public class Ground extends JPanel {

    // 常量定义
    public static final int STEP = 10; // 每次移动的距离
    public static final int SPACE = 8*STEP ; // 图片的边长   (必须是STEP的整数倍）
    public static final int DISTANCE = 6*STEP; // 攻击范围
    public static final int TIME_CLOCK = 100; // 线程休眠时间 （毫秒）
    public static final int PIXEL_HEIGHT = 720; // 上下的高度（像素点）
    public static final int PIXEL_WIDTH = 1280; // 左右的长度
    public static final int MAX_X = (PIXEL_WIDTH-SPACE) / STEP ;
    public static final int MAX_Y = (PIXEL_HEIGHT-SPACE) / STEP ;
    public static final int CREATURE_NUM = 17; // 所有生物的数量
    public static final String SUFFIX = ".fight";

    // 读写的文件
    private File writeFile = null;
    private File readFile = null;

    // 状态定义
    private static boolean stop; // 玩家是否按下暂停键
    private static Status status = WELCOME; // 4种状态：未开始，打斗中，回放中

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
        loadBackground(); // 装载场景
        initPictures(); // 为各种生物加载图像
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
    public ArrayList<Good> getGoodCreatures(){
        return goodCreatures;
    }

    public ArrayList<Bad> getBadCreatures(){
        return badCreatures;
    }

    private void loadBackground(){
        // 背景分辨率为 1280*720 , 即16:9 。 每个格子的边长为80分辨率
        URL loc = this.getClass().getClassLoader().getResource("背景2.png");
        ImageIcon iia = new ImageIcon(loc); // Image是抽象类，所以只能通过ImageIcon来创建
        backgroundImage = iia.getImage();

    }

    private void initPictures(){
        URL url = this.getClass().getClassLoader().getResource("爷爷.png");
        ImageIcon imageIcon = new ImageIcon(url);
        grandpaImage = imageIcon.getImage();

        gourddollsImage = new Image[7];

        url = this.getClass().getClassLoader().getResource("大娃.png");
        imageIcon = new ImageIcon(url);
        gourddollsImage[0] = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("二娃.png");
        imageIcon = new ImageIcon(url);
        gourddollsImage[1] = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("三娃.png");
        imageIcon = new ImageIcon(url);
        gourddollsImage[2] = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("四娃.png");
        imageIcon = new ImageIcon(url);
        gourddollsImage[3] = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("五娃.png");
        imageIcon = new ImageIcon(url);
        gourddollsImage[4] = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("六娃.png");
        imageIcon = new ImageIcon(url);
        gourddollsImage[5] = imageIcon.getImage();

        url = this.getClass().getClassLoader().getResource("七娃.png");
        imageIcon = new ImageIcon(url);
        gourddollsImage[6] = imageIcon.getImage();

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
        public synchronized void keyPressed(KeyEvent e){
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_SPACE){ // 开始
                if( status == WELCOME){

                    /*初始化生物、计时器和线程*/
                    status = FIGHTING;
                    System.out.println("状态从WELCOME转为FIGHTING");
                    initCreature(); // 初始化生物
                    initThread();
                }
            }
            else if(key == KeyEvent.VK_L){ // 回放
                if( status == FINISHED ){

                    status = REPLAYING;
                    System.out.println("状态从FINISHED转为REPLAYING");
                    //replaying();
                }
                else if( status == WELCOME ){
                    status = REPLAYING;
                    System.out.println("状态从WELCOME转为REPLAYING");
                    initCreature(); // 初始化生物
                    initThread();
                    //replaying();
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
            repaint();
        }
    }





    private void initCreature(){

        // 初始化爷爷
        grandpa = new Grandpa(0,MAX_Y/2,this);
        grandpa.setImage(grandpaImage);

        // 初始化葫芦娃
        gourdDolls = new GourdDolls[7]; // 默认为鹤翼阵型
        gourdDolls[0] = new GourdDolls(0,1*4,this,0);
        gourdDolls[1] = new GourdDolls(1*SPACE/STEP,2*SPACE/STEP,this,1);
        gourdDolls[2] = new GourdDolls(2*SPACE/STEP,3*SPACE/STEP,this,2);
        gourdDolls[3] = new GourdDolls(3*SPACE/STEP,4*SPACE/STEP,this,3);
        gourdDolls[4] = new GourdDolls(2*SPACE/STEP,5*SPACE/STEP,this,4);
        gourdDolls[5] = new GourdDolls(1*SPACE/STEP,6*SPACE/STEP,this,5);
        gourdDolls[6] = new GourdDolls(0,7*SPACE/STEP,this,6);
        for(int i = 0 ; i < 7 ; ++ i)
            gourdDolls[i].setImage(gourddollsImage[i]);

        // 把爷爷和葫芦娃添加到队列中
        goodCreatures = new ArrayList<Good>();
        goodCreatures.add(grandpa);
        for( GourdDolls g : gourdDolls ) {
            goodCreatures.add(g);
            //System.out.println(g);
        }

        // 初始化蛇精
        snake = new SnakeQueen(MAX_X,MAX_Y/2-SPACE/STEP,this);
        snake.setImage(snakeImage);

        // 初始化蝎子精
        scorpion =  new ScorpionKing(MAX_X,MAX_Y/2+SPACE/STEP,this);
        scorpion.setImage(scorpionImage);

        toads = new Toad[7];
        for(int i = 0 ; i < 7 ; ++ i){
            if( i != 3 && i != 5)
                toads[i] = new Toad(MAX_X,i*SPACE/STEP,this,i);
            else if (i == 3 )
                toads[i] = new Toad(MAX_X,7*SPACE/STEP,this,i);
            else
                toads[i] = new Toad(MAX_X,8*SPACE/STEP,this,i);
            toads[i].setImage(toadImage);
        }

        badCreatures = new ArrayList<Bad>();
        badCreatures.add(snake);
        badCreatures.add(scorpion);
        for(Bad c : toads) {
            badCreatures.add(c);
            //System.out.println(c);
        }

        deadCreatures = new ArrayList<Creatures>();
    }

    private void initTimer(){
        timerTask = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO:添加更多定时检查

            }
        };
        timer = new Timer(TIME_CLOCK,timerTask);
        timer.start();
    }

    private void initThread() {

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
    public synchronized void checkCreature(){

        /*System.out.println("检查生物:3个列表中的生物个数为:"
                +goodCreatures.size()+" "
                +badCreatures.size()+" "
                +deadCreatures.size());*/
        //System.out.println("检查：状态为"+status.toString());

        if( status == FIGHTING &&
                ( goodCreatures.isEmpty() || badCreatures.isEmpty() ) ) {
            status = FINISHED;
            System.out.println("FINISHED");
            // TODO:弹出游戏信息提示
            return;
        }

        Iterator<Good> g = goodCreatures.iterator();
        while(g.hasNext()){
            Good temp = g.next();
            if(temp.isDead()){
                temp.setImage("葫芦娃墓碑.png");
                deadCreatures.add(temp);
                g.remove();
            }
        }

        Iterator<Bad> b = badCreatures.iterator();
        while(b.hasNext()){
            Bad temp = b.next();
            if(temp.isDead()){
                temp.setImage("妖怪墓碑.png");
                deadCreatures.add(temp);
                b.remove();
            }
        }

        if( status == FIGHTING &&
                ( goodCreatures.isEmpty() || badCreatures.isEmpty() ) ) {
            status = FINISHED;
            System.out.println("FINISHED");
            // TODO:弹出游戏信息提示
            return;
        }

        if( status == FIGHTING ) {
            //writeFile();
            // TODO:写入文件
            //System.out.println("Good:"+goodCreatures.size()+                " Bad:"+badCreatures.size()+                " Dead:"+deadCreatures.size());
        }

    }

    private synchronized void paintImage(Graphics g) {

        g.drawImage(backgroundImage, 0, 0, PIXEL_WIDTH, PIXEL_HEIGHT, this);

        if( status != WELCOME ) {
            if (goodCreatures != null)
                for (Good c : goodCreatures)
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
            if (badCreatures != null)
                for (Bad c : badCreatures)
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
            if (deadCreatures != null)
                for (Creatures c : deadCreatures)
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
        }


    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintImage(g);
        g.drawString("按下空格开始游戏，按下L开始回放",0,PIXEL_HEIGHT);
    }

    public enum Status {WELCOME, FIGHTING, REPLAYING , FIGHTINGG, FINISHED};

    // Creature API : 攻击成功返回boolean
    // 检查的重点：距离
    public boolean requireAttack(Creatures attacker, Creatures attacked){
        int distance = distance(attacker,attacked);
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

    // Creatures API:
    // 返回两个生物体在坐标轴上的距离(x距离+y距离
    public final int distance(Creatures a, Creatures b){
        return Math.abs(a.getX()-b.getX()) + Math.abs(a.getY()-b.getY());
    }

    // TODO
    private synchronized void writeFile(){
        // 寻找一个可用的文件
        if (writeFile == null){
            Date now = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            String str = "save"+File.separator+simpleDateFormat.format(now)+SUFFIX;
            // System.out.println(str);
            writeFile = new File(str);
        }

        // 把三个ArrayList中的对象都写进文件
        try {
            FileWriter fileWriter = new FileWriter(writeFile,true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            ArrayList<Creatures> temp = new ArrayList<Creatures>();
            if( ! goodCreatures.isEmpty() )
                for( Creatures g : goodCreatures )
                    temp.add(g);
            if( ! badCreatures.isEmpty() )
                for( Creatures b : badCreatures )
                    temp.add(b);

            for( Creatures c : temp){
                bufferedWriter.write(c.toString()+" "+c.getX()+" "+c.getY()+" "+1 );
                bufferedWriter.newLine();
                System.out.println(c.toString()+" "+c.getX()+" "+c.getY()+" "+true);
            }

            temp.clear();

            if( ! deadCreatures.isEmpty() )
                for( Creatures d : deadCreatures )
                    temp.add(d);
            for( Creatures c : temp){
                bufferedWriter.write(c.toString()+" "+c.getX()+" "+c.getY()+" "+ 0 );
                bufferedWriter.newLine();
                System.out.println(c.toString()+" "+c.getX()+" "+c.getY()+" "+false);
            }
            bufferedWriter.close();
            fileWriter.close();
        }
        catch (FileNotFoundException e){

        }
        catch (IOException e){

        }

    }

    // TODO
    private synchronized void replaying(){

        JFileChooser jFileChooser = new JFileChooser(new File("save"));
        jFileChooser.setDialogTitle("选择作战记录（文件名即为作战时间）");
        jFileChooser.showDialog(null,null);
        readFile = jFileChooser.getSelectedFile();

        if( readFile != null ) {
            // System.out.println(readFile.toString());
            InputStream inputStream = null;
            DataInputStream dataInputStream = null;
            try {

                inputStream = new FileInputStream(readFile);
                dataInputStream = new DataInputStream(inputStream);
                FileReader fileReader = new FileReader(readFile);
                String name = null;
                int tempX = -1, tempY = -1;
                boolean l = false;
                int i = 0;

                while (dataInputStream.available() != 0) {
                    name = dataInputStream.readUTF();
                    tempX = dataInputStream.readInt();
                    tempY = dataInputStream.readInt();
                    l = dataInputStream.readBoolean();

                    System.out.println(name + " " + tempX + " " + tempY + " " + l);


                    if (name == "爷爷") {
                        grandpa.setX(tempX);
                        grandpa.setY(tempY);
                        if (l)
                            grandpa.setImage(grandpaImage);
                        else
                            grandpa.setImage("葫芦娃墓碑.png");
                    } else if (name == "蝎子精") {
                        scorpion.setX(tempX);
                        scorpion.setY(tempY);
                        if (l)
                            scorpion.setImage(scorpionImage);
                        else
                            scorpion.setImage("妖怪墓碑");
                    } else if (name == "蛇精") {
                        snake.setX(tempX);
                        snake.setY(tempY);
                        if (l)
                            snake.setImage(snakeImage);
                        else
                            snake.setImage("妖怪墓碑");
                    } else if (name == "大娃") {
                        gourdDolls[0].setX(tempX);
                        gourdDolls[0].setY(tempY);
                        if (l)
                            gourdDolls[0].setImage(gourddollsImage[0]);
                        else
                            gourdDolls[0].setImage("葫芦娃墓碑.png");
                    } else if (name == "二娃") {
                        gourdDolls[1].setX(tempX);
                        gourdDolls[1].setY(tempY);
                        if (l)
                            gourdDolls[1].setImage(gourddollsImage[1]);
                        else
                            gourdDolls[1].setImage("葫芦娃墓碑.png");
                    } else if (name == "三娃") {
                        gourdDolls[2].setX(tempX);
                        gourdDolls[2].setY(tempY);
                        if (l)
                            gourdDolls[2].setImage(gourddollsImage[2]);
                        else
                            gourdDolls[2].setImage("葫芦娃墓碑.png");
                    } else if (name == "四娃") {
                        gourdDolls[3].setX(tempX);
                        gourdDolls[3].setY(tempY);
                        if (l)
                            gourdDolls[3].setImage(gourddollsImage[3]);
                        else
                            gourdDolls[3].setImage("葫芦娃墓碑.png");
                    } else if (name == "五娃") {
                        gourdDolls[4].setX(tempX);
                        gourdDolls[4].setY(tempY);
                        if (l)
                            gourdDolls[4].setImage(gourddollsImage[4]);
                        else
                            gourdDolls[4].setImage("葫芦娃墓碑.png");
                    } else if (name == "六娃") {
                        gourdDolls[5].setX(tempX);
                        gourdDolls[5].setY(tempY);
                        if (l)
                            gourdDolls[5].setImage(gourddollsImage[5]);
                        else
                            gourdDolls[5].setImage("葫芦娃墓碑.png");
                    } else if (name == "七娃") {
                        gourdDolls[6].setX(tempX);
                        gourdDolls[6].setY(tempY);
                        if (l)
                            gourdDolls[6].setImage(gourddollsImage[6]);
                        else
                            gourdDolls[6].setImage("葫芦娃墓碑.png");
                    } else if(name == "马仔1号"){ // 蛤蟆精
                        toads[0].setX(tempX);
                        toads[0].setY(tempY);
                        if (l)
                            toads[0].setImage(toadImage);
                        else
                            toads[0].setImage("妖怪墓碑.png");
                    }else if(name == "马仔2号"){ // 蛤蟆精
                        toads[1].setX(tempX);
                        toads[1].setY(tempY);
                        if (l)
                            toads[1].setImage(toadImage);
                        else
                            toads[1].setImage("妖怪墓碑.png");
                    }
                    else if(name == "马仔3号"){ // 蛤蟆精
                        toads[2].setX(tempX);
                        toads[2].setY(tempY);
                        if (l)
                            toads[2].setImage(toadImage);
                        else
                            toads[2].setImage("妖怪墓碑.png");
                    }
                    else if(name == "马仔4号"){ // 蛤蟆精
                        toads[3].setX(tempX);
                        toads[3].setY(tempY);
                        if (l)
                            toads[3].setImage(toadImage);
                        else
                            toads[3].setImage("妖怪墓碑.png");
                    }
                    else if(name == "马仔5号"){ // 蛤蟆精
                        toads[4].setX(tempX);
                        toads[4].setY(tempY);
                        if (l)
                            toads[4].setImage(toadImage);
                        else
                            toads[4].setImage("妖怪墓碑.png");
                    }
                    else if(name == "马仔6号"){ // 蛤蟆精
                        toads[5].setX(tempX);
                        toads[5].setY(tempY);
                        if (l)
                            toads[5].setImage(toadImage);
                        else
                            toads[5].setImage("妖怪墓碑.png");
                    }
                    else if(name == "马仔7号"){ // 蛤蟆精
                        toads[6].setX(tempX);
                        toads[6].setY(tempY);
                        if (l)
                            toads[6].setImage(toadImage);
                        else
                            toads[6].setImage("妖怪墓碑.png");
                    }
                    repaint();
                }
                dataInputStream.close();
                inputStream.close();
            }
            catch (FileNotFoundException e){
                System.out.println("Here 1");
            }
            catch(IOException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
//            catch(ClassNotFoundException e){
//                System.out.println("Here 3");
//            }

        }
    }

}
