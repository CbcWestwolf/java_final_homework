package nju.java; /**
 * Created by cbcwestwolf on 2017/12/26.
 */

import javax.swing.*;

public class GameBoard extends JFrame{

    public GameBoard(){
        initUI();
    }

    private void initUI(){

        Ground ground = new Ground();
        add(ground);  // 在GameBourd

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // TODO: 调整背景大小
        setSize(ground.getPixelWidth()+20, ground.getPixelHeight()+50);
        setLocationRelativeTo(null);
        setTitle("Ground");
    }

    public static void main(String[] arg){
        GameBoard gameBoard = new GameBoard();
        gameBoard.setVisible(true);
    }
}
