package nju.java.creatures.good;

import nju.java.Ground;
import nju.java.creatures.Creatures;

/**
 * Created by cbcwestwolf on 2017/12/27.
 */
public class Grandpa extends Good {
    // TODO:使用单例模式实现

    public Grandpa(int x, int y, Ground ground) {
        super(x, y, ground);
        power = 10; // 爷爷羸弱无比
    }

    public void run(){
        while (!Thread.interrupted()) {

            int newX = this.getX() + 1 ;
            if( newX < this.ground.MAX_X -1 )
                this.move(1,0);
            try {

                Thread.sleep(this.ground.TIME_CLOCK);
                this.ground.repaint();

            } catch (Exception e) {

            }
        }
    }

}
