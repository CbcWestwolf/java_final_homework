/**
 * Created by cbcwestwolf on 2017/12/26.
 */

import javax.swing.*;

public class GameBourd extends JFrame{

    public GameBourd(){
        System.out.println("GameBourd constructor");
        initUI();
    }

    private void initUI(){
        // TODO
        System.out.println("GameBourd.initUI()");
    }

    public static void main(String[] arg){
        System.out.println("GameBourd.main()");
        GameBourd gameBourd = new GameBourd();
    }
}
