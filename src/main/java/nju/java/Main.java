package nju.java; /**
 * Created by cbcwestwolf on 2017/12/26.
 */

import javax.swing.*;
import static nju.java.ConstantValue.*;

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
