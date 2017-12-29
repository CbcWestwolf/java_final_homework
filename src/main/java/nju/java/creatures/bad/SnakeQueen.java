package nju.java.creatures.bad;

import nju.java.Ground;
import nju.java.creatures.Creatures;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class SnakeQueen extends Bad {


    public SnakeQueen(int x, int y, Ground ground) {
        super(x, y, ground);
        power = 20;

    }

    @Override
    public String toString(){
        return "蛇精";
    }

}
