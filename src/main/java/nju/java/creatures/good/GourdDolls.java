package nju.java.creatures.good;

import nju.java.Ground;
import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class GourdDolls extends Good {

    public GourdDolls(int x, int y, Ground ground) {
        super(x, y, ground);
        power = 50 + new Random().nextInt(50);
        //System.out.println(blood);
    }

}
