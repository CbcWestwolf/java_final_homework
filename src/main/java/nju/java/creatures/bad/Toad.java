package nju.java.creatures.bad;

import nju.java.BackEnd;
import nju.java.Ground;
import nju.java.creatures.Creatures;

/**
 * Created by cbcwestwolf on 2017/12/28.
 */
public class Toad extends Bad{

    private int id ;
    public Toad(int x, int y, BackEnd backEnd, int id) {
        super(x, y, backEnd);
        power = 50;
        this.id = id+1;

    }

    @Override
    public String toString(){
        return "马仔"+id+"号";
    }

}
