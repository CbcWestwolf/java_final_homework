package nju.java.creatures.bad;

import nju.java.Ground;
import nju.java.creatures.Creatures;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class ScorpionKing extends Bad {


    public ScorpionKing(int x, int y , Ground ground){
        super(x,y,ground);
        power = 80;
    }

    @Override
    public String toString(){
        return "蝎子大王";
    }
}
