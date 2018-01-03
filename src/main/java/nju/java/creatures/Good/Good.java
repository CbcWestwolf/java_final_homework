package nju.java.creatures.Good;

import nju.java.BackEnd;
import nju.java.creatures.Creatures;
import nju.java.creatures.bad.Bad;
import nju.java.tools.Status;
import static nju.java.tools.ConstantValue.*;

import java.util.ArrayList;

/***
 * @author cbcwestwolf
 * <br>
 * Good类定义了正方角色具体的属性 <br>
 * 角色的基本属性在Creatures类中定义
 *
 * @see nju.java.creatures.Creatures
 */

public abstract class Good extends Creatures {

    public Good(int x, int y, BackEnd backEnd) {
        super(x, y, backEnd);
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

                    ArrayList<Bad> bads = getAttackable();
                    int maxValue = 0;
                    Bad goal = null;
                    if (!bads.isEmpty()) {
                        // 找到得分高的
                        for (Bad b : bads)
                            if (attackValue(b) > maxValue) {
                                maxValue = attackValue(b);
                                goal = b;
                            }
                    }
                    if (goal != null)
                        this.backEnd.requireAttack(this, goal);
                    else {
                        // 找到距离近的
                        Bad b = getNearestBad();
                        int x_off = b.getX() - this.getX();
                        int y_off = b.getY() - this.getY();
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
            } else if (BackEnd.getStatus() == Status.REPLAYING) {

                try {
                    Thread.sleep(0,REPLAY_CLOCK);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public String toString(){
        return "正方";
    }

    /**
     * 寻找可攻击的敌人
     *
     * @return 可攻击的敌人列表
     */
    public ArrayList<Bad> getAttackable(){
        ArrayList<Bad> all = this.backEnd.getBadCreatures();
        ArrayList<Bad> result = new ArrayList<Bad>();

        for( Bad b : all ){
            if( distance(this,b) <= DISTANCE/STEP )
                result.add(b);
        }
        return result;
    }

    /**
     * 寻找距离最近的敌人
     *
     * @return 距离最近的敌人
     */
    public Bad getNearestBad(){
        ArrayList<Bad> all = this.backEnd.getBadCreatures();
        int minDistance = Integer.MAX_VALUE;
        Bad result = null;
        for( Bad b : all ) {
            if (distance(this, b) < minDistance) {
                minDistance = distance(this, b);
                result = b;
            }
        }
        return result;
    }

}
