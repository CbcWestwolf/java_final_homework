package nju.java.creatures.bad;

import nju.java.Ground;
import nju.java.creatures.Creatures;
import nju.java.creatures.good.Good;

import java.util.ArrayList;

/**
 * Created by cbcwestwolf on 2017/12/28.
 */
public class Bad extends Creatures {
    public Bad(int x, int y, Ground ground) {
        super(x, y, ground);
    }

    public ArrayList<Good> getAttackable() {
        ArrayList<Good> all = this.ground.getGoodCreatures();
        ArrayList<Good> result = new ArrayList<Good>();

        for (Good g : all) {
            if (this.ground.distance(this, g) <= Ground.DISTANCE / Ground.STEP)
                result.add(g);
        }
        return result;
    }

    // 寻找距离最近的敌人
    public Good getNearestGood() {
        ArrayList<Good> all = this.ground.getGoodCreatures();
        int minDistance = Integer.MAX_VALUE;
        Good result = null;
        for (Good a : all) {
            if (this.ground.distance(this, a) < minDistance) {// TODO:判断
                minDistance = this.ground.distance(this, a);
                result = a;
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "反方";
    }

    public void run() {

        while (!Thread.interrupted()) {
            if (Ground.getStatus() == Ground.Status.FIGHTING) {
                try {
                    if (isDead() || Ground.isStop() || Ground.getStatus() != Ground.Status.FIGHTING) {
                        //System.out.println("没状态？");
                        Thread.sleep(this.ground.TIME_CLOCK);
                        continue;
                    }

                    ArrayList<Good> goods = getAttackable();
                    int maxValue = 0;
                    Good goal = null;
                    if (!goods.isEmpty()) {
                        // 找到得分高的
                        for (Good g : goods)
                            if (attackValue(g) > maxValue) {
                                maxValue = attackValue(g);
                                goal = g;
                            }
                    }
                    if (goal != null)
                        this.ground.requireAttack(this, goal);
                    else {
                        // 找到距离近的
                        Good g = getNearestGood();
                        int x_off = g.getX() - this.getX();
                        int y_off = g.getY() - this.getY();
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

                } catch (Exception e) {

                }
            } else if (Ground.getStatus() == Ground.Status.REPLAYING) {

                try {
                    Thread.sleep(0,Ground.TIME_CLOCK/Ground.REPLAY_CLOCK);

                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

    }
}
