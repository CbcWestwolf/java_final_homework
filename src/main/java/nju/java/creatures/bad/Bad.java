package nju.java.creatures.bad;

import nju.java.BackEnd;
import nju.java.creatures.Creatures;
import nju.java.creatures.Good.Good;
import nju.java.tools.Status;

import java.util.ArrayList;

import static nju.java.tools.ConstantValue.*;

/***
 * @author cbcwestwolf
 * <br>
 * This class define the specific properties of Bad creatuures
 * The basic properties is defined in Creatures class.
 *
 * @see nju.java.creatures.Creatures
 */
public abstract class Bad extends Creatures {
    public Bad(int x, int y, BackEnd backEnd) {
        super(x, y, backEnd);
    }

    public ArrayList<Good> getAttackable() {
        ArrayList<Good> all = this.backEnd.getGoodCreatures();
        ArrayList<Good> result = new ArrayList<Good>();

        for (Good g : all) {
            if (distance(this, g) <=  DISTANCE / STEP )
                result.add(g);
        }
        return result;
    }

    // 寻找距离最近的敌人
    public Good getNearestGood() {
        ArrayList<Good> all = this.backEnd.getGoodCreatures();
        int minDistance = Integer.MAX_VALUE;
        Good result = null;
        for (Good a : all) {
            if (distance(this, a) < minDistance) {// TODO:判断
                minDistance = distance(this, a);
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
            if (BackEnd.getStatus() == Status.FIGHTING) {
                try {
                    if (isDead() || BackEnd.isStop() || BackEnd.getStatus() != Status.FIGHTING) {
                        //System.out.println("没状态？");
                        Thread.sleep(TIME_CLOCK);
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
                        this.backEnd.requireAttack(this, goal);
                    else {
                        // 找到距离近的
                        Good g = getNearestGood();
                        int x_off = g.getX() - this.getX();
                        int y_off = g.getY() - this.getY();
                        if (Math.abs(x_off) > Math.abs(y_off)) {
                            if (x_off > 0)
                                this.backEnd.requireWalk(this, 1, 0);
                            else
                                this.backEnd.requireWalk(this, -1, 0);
                        } else {
                            if (y_off > 0)
                                this.backEnd.requireWalk(this, 0, 1);
                            else
                                this.backEnd.requireWalk(this, 0, -1);
                        }
                    }

                    Thread.sleep(TIME_CLOCK);

                } catch (Exception e) {

                }
            } else if (BackEnd.getStatus() ==    Status.REPLAYING) {

                try {
                    Thread.sleep(0,TIME_CLOCK/REPLAY_CLOCK);

                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

    }
}
