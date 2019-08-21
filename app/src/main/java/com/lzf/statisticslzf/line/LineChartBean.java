package com.lzf.statisticslzf.line;

import java.util.Arrays;

/**
 * 折线统计图的JavaBean
 */
public class LineChartBean {
    private String xLabel; //x轴上的文字标签
    private float yNum; //第一个y轴
    private float yNum1; //第二个y轴
    private float yNum2; //第三个y轴
    private int barColor[];
    private long time;

    public LineChartBean() {
    }

    public LineChartBean(String xLabel, float yNum) {
        this.xLabel = xLabel;
        this.yNum = yNum;
    }

    public LineChartBean(String xLabel, float yNum, long time) {
        this.xLabel = xLabel;
        this.yNum = yNum;
        this.time = time;
    }

    public LineChartBean(String xLabel, float yNum, float yNum1, float yNum2) {
        this.xLabel = xLabel;
        this.yNum = yNum;
        this.yNum1 = yNum1;
        this.yNum2 = yNum2;
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public float getyNum() {
        return yNum;
    }

    public void setyNum(float yNum) {
        this.yNum = yNum;
    }

    public float getyNum1() {
        return yNum1;
    }

    public void setyNum1(float yNum1) {
        this.yNum1 = yNum1;
    }

    public float getyNum2() {
        return yNum2;
    }

    public void setyNum2(float yNum2) {
        this.yNum2 = yNum2;
    }

    public int[] getBarColor() {
        return barColor;
    }

    public void setBarColor(int[] barColor) {
        this.barColor = barColor;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "LineChartBean{" +
                "xLabel='" + xLabel + '\'' +
                ", yNum=" + yNum +
                ", yNum1=" + yNum1 +
                ", yNum2=" + yNum2 +
                ", barColor=" + Arrays.toString(barColor) +
                ", time=" + time +
                '}';
    }
}
