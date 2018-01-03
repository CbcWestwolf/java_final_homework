package nju.java.creatures.Good;

import nju.java.BackEnd;
import nju.java.creatures.Good.Good;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class Grandpa extends Good {

    public Grandpa(int x, int y, BackEnd backEnd) {
        super(x, y, backEnd);
        power = 10; // 爷爷羸弱无比

    }

    @Override
    public String toString(){
        return "爷爷";
    }

}
