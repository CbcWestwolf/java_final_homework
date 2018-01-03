package nju.java;

import javax.swing.*;
import static nju.java.tools.ConstantValue.*;

/***
 * @author cbcwestwolf
 * <br>
 * 程序入口类<br>
 * 使用BackEnd类和Ground类完成游戏逻辑<br>
 *
 * @see nju.java.Ground
 * @see nju.java.BackEnd
 */

public class Main extends JFrame{

    public Main(){
        BackEnd backEnd = new BackEnd();
        Ground ground = new Ground(backEnd);
        backEnd.setGround(ground);
        add(ground);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(PIXEL_WIDTH+20, PIXEL_HEIGHT+50+125);
        setLocationRelativeTo(null);
        setTitle("葫芦娃");
    }

    public static void main(String[] arg){
        Main gameBoard = new Main();
        gameBoard.setVisible(true);
    }

}
