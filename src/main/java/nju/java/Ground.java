package nju.java;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Created by cbcwestwolf on 2017/12/26.
 */
public class Ground extends JPanel {

    private int myHeight = 100; // 上下的高度

    private int myWidth = 100; // 左右的长度

    private Image backgroundImage = null; // 背景图片

    public Ground(){
        // TODO:添加键盘监听器
        setFocusable(true);
        initGround();
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

    private void paintGround(Graphics g){

        // TODO: add all drawImage() here

        g.drawImage(backgroundImage,0,0,myWidth,myHeight,this);
    }

    @Override
    public void paint(Graphics g){
        super.paint(g);
        paintGround(g);
    }


}
