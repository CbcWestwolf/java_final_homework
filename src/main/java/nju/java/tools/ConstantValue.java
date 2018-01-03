package nju.java.tools;

/**
 * Created by cbcwestwolf on 2018/1/3.
 */
public interface ConstantValue {
    // 常量定义
    public static final int STEP = 5; // 每次移动的距离
    public static final int SPACE = 16 * STEP; // 图片的边长   (必须是STEP的整数倍）
    public static final int DISTANCE = 12 * STEP; // 攻击范围
    public static final int TIME_CLOCK = 100; // 线程休眠时间 （毫秒）
    public static final int REPLAY_CLOCK = 1;
    public static final int PIXEL_HEIGHT = 720; // 上下的高度（像素点）
    public static final int PIXEL_WIDTH = 1280; // 左右的长度
    public static final int MAX_X = (PIXEL_WIDTH - SPACE) / STEP;
    public static final int MAX_Y = (PIXEL_HEIGHT - SPACE) / STEP;
    public static final String SUFFIX = ".fight";

}