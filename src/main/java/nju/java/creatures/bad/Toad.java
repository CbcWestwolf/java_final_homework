package nju.java.creatures.bad;

import nju.java.Ground;
import nju.java.creatures.Creatures;

/**
 * Created by cbcwestwolf on 2017/12/28.
 */
public class Toad extends Bad{


    public Toad(int x, int y, Ground ground) {
        super(x, y, ground);
        power = 50;
    }

    public void run(){
        while (!Thread.interrupted()) {
            int newX = this.getX() - 1 ;

            if( newX < this.ground.MAX_X  && newX >= 0)
                this.move(-1,0);
            try {

                Thread.sleep(this.ground.TIME_CLOCK);
                this.ground.repaint();

            } catch (Exception e) {

            }
        }
    }
}
