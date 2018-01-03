package nju.java.creatures.good;

import nju.java.Ground;
import nju.java.creatures.Creatures;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class Grandpa extends Good {

    public Grandpa(int x, int y, Ground ground) {
        super(x, y, ground);
        power = 10; // 爷爷羸弱无比

    }

    @Override
    public String toString(){
        return "爷爷";
    }

}
