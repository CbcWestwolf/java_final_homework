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

    private int id;
    public GourdDolls(int x, int y, Ground ground,int id) {
        super(x, y, ground);
        power = 50 + new Random().nextInt(50);
        this.id = id;

    }

    @Override
    public String toString(){
        String result = "没人认领？";
        switch (id){
            case 0 : result = "大娃";
                break;
            case 1 : result = "二娃";
                break;
            case 2 : result = "三娃";
                break;
            case 3 : result = "四娃";
                break;
            case 4 : result = "五娃";
                break;
            case 5 : result = "六娃";
                break;
            case 6 : result = "七娃";
                break;
        }
        return result;
    }

}
