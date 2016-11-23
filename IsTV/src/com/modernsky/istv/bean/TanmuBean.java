/**
 *
 */
package com.modernsky.istv.bean;

import android.graphics.Color;

/**
 * @author： fengqingyun2008
 * @Email： fengqingyun2008@gmail.com
 * @version：1.0
 * @创建时间：2015-8-17 下午12:11:10
 * @类说明：
 */
public class TanmuBean extends BaseBean {
    private String[] items;
    private int color;
    private int minTextSize;
    private float range;

    public TanmuBean() {
        // init default value
        color = Color.parseColor("#eeeeee");
        minTextSize = 16;
        range = 0.5f;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getMinTextSize() {
        return minTextSize;
    }

    public void setMinTextSize(int minTextSize) {
        this.minTextSize = minTextSize;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }
}