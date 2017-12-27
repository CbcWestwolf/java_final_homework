package nju.java.things.creatures;

import nju.java.Ground;

import java.util.Random;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class GourdDolls extends Creatures{

    public GourdDolls(int x, int y, Ground ground) {
        super(x, y, ground);
    }

    public void run(){
        while (!Thread.interrupted()) {

            if( this.getX() < this.ground.MAX_X -1 )
                this.move(1,0);
            try {

                Thread.sleep(2000);
                this.ground.repaint();

            } catch (Exception e) {

            }
        }
    }
}
