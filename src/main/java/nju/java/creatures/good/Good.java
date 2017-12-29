package nju.java.creatures.good;

import nju.java.Ground;
import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;

import java.util.ArrayList;

/**
 * Created by cbcwestwolf on 2017/12/28.
 */
public class Good extends Creatures {

    public Good(int x, int y, Ground ground) {
        super(x, y, ground);
    }

    public void run() {

        while (!Thread.interrupted()) {

            try {
            if (isDead() || Ground.isStop() || Ground.getStatus() != Ground.Status.FIGHTING) {
                //System.out.println("没状态？");
                Thread.sleep(this.ground.TIME_CLOCK);
                this.ground.repaint();
                continue;
            }

            ArrayList<Bad> bads = getAttackable();
            int maxValue = 0;
            Bad goal = null;
            if( ! bads.isEmpty() ){
                // 找到得分高的
                for( Bad b : bads )
                    if( attackValue(b) > maxValue ){
                        maxValue = attackValue(b);
                        goal = b;
                    }
            }
            if( goal != null )
                this.ground.requireAttack(this,goal);
            else {
                // 找到距离近的
                Bad bad = getNearestBad();
                int x_off = bad.getX() - this.getX();
                int y_off = bad.getY() - this.getY();
                if (Math.abs(x_off) > Math.abs(y_off)) {
                    if (x_off > 0)
                        this.ground.requireWalk(this, 1, 0);
                    else
                        this.ground.requireWalk(this, -1, 0);
                } else {
                    if (y_off > 0)
                        this.ground.requireWalk(this, 0, 1);
                    else
                        this.ground.requireWalk(this, 0, -1);
                }
            }
                Thread.sleep(this.ground.TIME_CLOCK);
                this.ground.checkCreature();
                this.ground.repaint();


            }catch (Exception e) {

            }
        }
    }

    @Override
    public String toString(){
        return "正方方";
    }

    // 寻找可攻击的敌人
    public ArrayList<Bad> getAttackable(){
        ArrayList<Bad> all = this.ground.getBadCreatures();
        ArrayList<Bad> result = new ArrayList<Bad>();

        for( Bad b : all ){
            if( this.ground.distance(this,b) <= Ground.DISTANCE/Ground.STEP )
                result.add(b);
        }
        return result;
    }

    // 寻找距离最近的敌人
    public Bad getNearestBad(){
        ArrayList<Bad> all = this.ground.getBadCreatures();
        int minDistance = Integer.MAX_VALUE;
        Bad result = null;
        for( Bad b : all ) {
            if (this.ground.distance(this, b) < minDistance) {// TODO:判断
                minDistance = this.ground.distance(this, b);
                result = b;
            }
        }
        return result;
    }

}
