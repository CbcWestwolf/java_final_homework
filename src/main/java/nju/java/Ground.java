package nju.java;

import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;
import nju.java.creatures.good.Good;
import nju.java.tools.*;

import static nju.java.tools.ConstantValue.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.URL;


/***
 * @author cbcwestwolf
 * <br>
 * This class is used to paint the gmae for players.
 * The inner logic of the game is defined in BackEnd class.
 * It is used by Main class.
 *
 * @see nju.java.BackEnd
 * @see nju.java.Main
 */
public class Ground extends JPanel {

    private BackEnd backEnd = null;


    // 背景图片
    private Image backgroundImage = null; // 背景图片
    private Image grandpaImage = null;
    private Image[] gourddollsImage = null;
    private Image snakeImage = null;
    private Image scorpionImage = null;
    private Image toadImage = null;
    private Image goodTombstoneImage = null;
    private Image badTombstoneImage = null;

    public Ground( BackEnd backEnd ){
        this.backEnd = backEnd;
        setFocusable(true); // 设置可见
        addKeyListener(new TAdapter());// 添加键盘监视器
        loadPictures(); // 加载图像
    }


    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public Image getGrandpaImage() {
        return grandpaImage;
    }

    public Image[] getGourddollsImage() {
        return gourddollsImage;
    }

    public Image getSnakeImage() {
        return snakeImage;
    }

    public Image getScorpionImage() {
        return scorpionImage;
    }

    public Image getToadImage() {
        return toadImage;
    }

    public Image getGoodTombstoneImage() {
        return goodTombstoneImage;
    }

    public Image getBadTombstoneImage() {
        return badTombstoneImage;
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
                if( BackEnd.status == Status.WELCOME){

                    /*初始化生物、计时器和线程*/
                    BackEnd.status = Status.FIGHTING;
                    System.out.println("状态从WELCOME转为FIGHTING");
                    backEnd.initThread();
                    backEnd.initTimer(TIME_CLOCK);
                }
            }
            else if(key == KeyEvent.VK_L){ // 回放

                /*
                 1.添加读入记录
                 2.根据记录更新某个生物体的位置、状态
                 3.repaint();
                 3.休眠一段时间
                 4.继续读入记录
                 */

                if( BackEnd.status == Status.WELCOME || BackEnd.status == Status.FINISHED ) {
                    BackEnd.status = Status.REPLAYING;
                    backEnd.initThread();
                    backEnd.initTimer(REPLAY_CLOCK);
                    System.out.println("REPLAYING");

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
                    backEnd.setReadFile(jFileChooser.getSelectedFile());
                }

            }
            else if(key == KeyEvent.VK_P){ // 暂停
                BackEnd.stop = !BackEnd.stop;
                if(BackEnd.stop)
                    System.out.println("暂停！");
                else
                    System.out.println("解除暂停！");
            }
            else if( key == KeyEvent.VK_ESCAPE )
                System.exit(0);
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

        if( BackEnd.status == Status.WELCOME )
            g.drawString("按下空格开始游戏",SPACE,PIXEL_HEIGHT/2);
        if( BackEnd.status == Status.WELCOME || BackEnd.status == Status.FINISHED ) {
            g.drawString("按下L开始回放", SPACE, PIXEL_HEIGHT / 2 + 20);
        }
        if( BackEnd.status == Status.FIGHTING || BackEnd.status == Status.REPLAYING) {
            if (BackEnd.stop)
                g.drawString("按下P恢复", SPACE, PIXEL_HEIGHT / 2 + 40);
            else
                g.drawString("按下P暂停", SPACE, PIXEL_HEIGHT / 2 + 40);
        }
        g.drawString("按下Esc退出",SPACE,PIXEL_HEIGHT / 2 + 60);

        if( BackEnd.status != Status.WELCOME ) {
            if (backEnd.getGoodCreatures() != null)
                for (Good c : backEnd.getGoodCreatures()) {
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
                }
            if (backEnd.getBadCreatures() != null)
                for (Bad c : backEnd.getBadCreatures()) {
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
                }
            if (backEnd.getDeadCreatures() != null)
                for (Creatures c : backEnd.getDeadCreatures()) {
                    g.drawImage(c.getImage(), c.getX() * STEP, c.getY() * STEP, SPACE, SPACE, this);
                }
        }
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintImage(g);

    }
}
