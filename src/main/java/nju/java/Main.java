package nju.java; /**
 * Created by cbcwestwolf on 2017/12/26.
 */

import javax.swing.*;

public class Main extends JFrame{

    public Main(){
        Ground ground = new Ground();
        add(ground);  // 在GameBourd

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Ground.PIXEL_WIDTH+20, Ground.PIXEL_HEIGHT+50+125);
        setLocationRelativeTo(null);
        setTitle("葫芦娃");
    }

    public static void main(String[] arg){
        Main gameBoard = new Main();
        gameBoard.setVisible(true);
    }

}
