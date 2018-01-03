package nju.java.creatures.good;

import nju.java.Ground;
import java.util.Random;
import nju.java.tools.GourddollsName;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class GourdDolls extends Good {

    private int id;
    public GourdDolls(int x, int y, Ground ground,int id) {
        super(x, y, ground);
        power = 50 + new Random().nextInt(50);
        this.id = id;

    }

    @Override
    public String toString(){
        return GourddollsName.values()[id].toString();
    }

}
