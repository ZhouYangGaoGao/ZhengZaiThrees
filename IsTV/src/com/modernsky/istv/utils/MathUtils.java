package com.modernsky.istv.utils;

import java.math.BigDecimal;

/**
 * 数值运算工具类
 *
 * @author mufaith
 * @time 16/3/19 下午4:49
 */
public class MathUtils {

    /**
     *  获取保留几位小数的数值
     * @param f:初始值
     * @param scale:保留几位小数
     * @param isUp   是否四舍五入
     * @return
     */
    public static double getWantedDecimal(double f, int scale, boolean isUp) {
        double result;
        BigDecimal b = new BigDecimal(f);
        if (isUp) {

            result = b.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } else {
            result = b.setScale(scale, BigDecimal.ROUND_FLOOR).doubleValue();
        }
        return result;
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale 参数指 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}
