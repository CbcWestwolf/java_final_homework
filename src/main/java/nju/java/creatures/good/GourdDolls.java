package nju.java.creatures.good;

import nju.java.Ground;
import nju.java.creatures.Creatures;

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

    public void run(){
        while (!Thread.interrupted()) {
            int newX = this.getX() + 1 ;

            if( newX < this.ground.MAX_X && newX >= 0)
                this.move(1,0);
            try {

                Thread.sleep(this.ground.TIME_CLOCK);
                this.ground.repaint();

            } catch (Exception e) {

            }
        }
    }
}
