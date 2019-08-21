package com.lzf.statisticslzf.line;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 条形统计图+折线图的组合图表
 */
public class LineChart extends View {
    /* 用户点击到了无效位置 */
    public static final int INVALID_POSITION = -1;
    private int screenWidth, screenHeight;
    private List<LineChartBean> mBarData;
    //    private List<Float> winds;//风力的集合
    //    private List<Float> humidity;//湿度的集合
    //    private List<Float> temperature;//温度的集合
    //柱形图的颜色集合
    private int colors[] = new int[]{Color.parseColor("#00000000"), Color.parseColor("#78DA9F"), Color.parseColor("#FCAE84")}; //red代替了parseColor("#6FC5F4")
    //    private String[] rightYLabels; //右边的Y轴
    /**
     * item中的最大值
     */
    private float maxValueInItems;
    /**
     * bar的最高值
     */
    private float maxHeight;
    /**
     * 各种画笔 柱形图的 轴的 文本的 线形图的 画点的
     */
    private Paint barPaint, axisPaint, textPaint, linePaint, pointPaint;
    /**
     * 原点的半径
     */
    private static final float RADIUS = 10;
    /**
     * 各种巨型 柱形图的 左边白色部分 右边白色部分
     */
    private Rect barRect, leftWhiteRect, rightWhiteRect, topWhiteRect, bottomWhiteRect;
    //    private Rect barRect1, barRect2;
    /**
     * 左边和上边的边距
     */
    private int leftMargin, topMargin;
    /**
     * 每一个bar的宽度
     */
    private int barWidth;
    /**
     * 每个bar之间的距离
     */
    private int barSpace;
    /**
     * x轴 y轴 起始坐标
     */
    private float xStartIndex, yStartIndex;
    /**
     * 背景的颜色
     */
    private static final int BG_COLOR = Color.parseColor("#313744"); //WHITE代替了parseColor("#EEEEEE")
    /**
     * 向右边滑动的距离
     */
    private float leftMoving;
    /**
     * 左后一次的x坐标
     */
    private float lastPointX;
    private float lastPointY;
    /**
     * 当前移动的距离
     */
    private float movingThisTime = 0.0f;
    /**
     * 最大和最小分度值
     */
    private float maxDivisionValue, minDivisionValue;
    private int maxRight, minRight;
    /**
     * 线的路径
     */
    Path linePathW;//风
    //    Path linePathH;//湿度
    //    Path linePathT;//温度

    //风点的颜色
    private static final int WIND_COLOR = Color.parseColor("#EF6868");
    //湿度线点的颜色
    //    private static final int HUM_COLOR = Color.parseColor("#549FF4");
    //温度点的颜色
    //    private static final int TEM_COLOR = Color.parseColor("#FFD400");
    /**
     * 右边的Y轴分成3份  每一分的高度
     */
    //    private float lineEachHeight;
    /**
     * 右边的Y轴分成2份  每一分的高度
     */
    private float lineEachHeightT;
    /**
     * 温度的最大值减最小值
     */
    //    private float eachTotalValueT;
    /**
     * 温度的最小刻度值
     */
    //    private float mTMinValue;
    /**
     * 湿度的最小刻度值
     */
    //    private float mHMinValue;
    /**
     * 湿度的最大值减最小值
     */
    //    private float eachTotalValueH;
    //左边Y轴的单位
    private String leftAxisUnit = "";
    private OnItemBarClickListener mOnItemBarClickListener;
    private OnSlideLeftListener onSlideLeftListener; //监听滑动到最左边的事件
    private GestureDetector mGestureListener;

    public interface OnItemBarClickListener {
        void onClick(int position);
    }

    public interface OnSlideLeftListener { //监听滑动到最左边的事件
        void load(); //分页加载
    }

    /**
     * 保存bar的左边和右边的x轴坐标点
     */
    private List<Integer> leftPoints = new ArrayList<>();
    private List<Integer> rightPoints = new ArrayList<>();

    public void setOnItemBarClickListener(OnItemBarClickListener onRangeBarClickListener) {
        this.mOnItemBarClickListener = onRangeBarClickListener;
    }

    public void setOnSlideLeftListener(OnSlideLeftListener onSlideLeftListener) { //滑动到最左边的监听器
        this.onSlideLeftListener = onSlideLeftListener;
    }

    public LineChart(Context context) {
        super(context);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        mGestureListener = new GestureDetector(context, new RangeBarOnGestureListener());

        leftMargin = ScreenUtils.dp2px(context, 50); //手机右边的空白（统计图自己的左边）
        topMargin = ScreenUtils.dp2px(context, 23); //

        barPaint = new Paint();
        barPaint.setColor(colors[0]);

        axisPaint = new Paint();
        axisPaint.setStrokeWidth(2); //轴线的宽度（X轴、Y轴）
        axisPaint.setColor(Color.GRAY);//轴线的颜色（X轴、Y轴）

        textPaint = new Paint();
        textPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(2);
        linePaint.setColor(Color.WHITE); //Color.WHITE
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint = new Paint();
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);

        barRect = new Rect(0, 0, 0, 0);
        //        barRect1 = new Rect(0, 0, 0, 0);
        //        barRect2 = new Rect(0, 0, 0, 0);

        linePathW = new Path();
        //        linePathH = new Path();
        //        linePathT = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenWidth = getMeasuredWidth();
        screenHeight = getMeasuredHeight();

        //设置矩形的顶部 底部 右边Y轴的3部分每部分的高度
        getStatusHeight();
        leftWhiteRect = new Rect(0, 0, 0, screenHeight);
        rightWhiteRect = new Rect(screenWidth - leftMargin, 0, screenWidth, screenHeight); //screenWidth - leftMargin * 2 - 10该值控制右边的遮罩层宽度
        topWhiteRect = new Rect(0, 0, screenWidth, topMargin / 2);
        bottomWhiteRect = new Rect(0, (int) yStartIndex, screenWidth, screenHeight);
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = (int) ScreenUtils.dp2px(getContext(), 50f);
        setMeasuredDimension(
                Math.max(getSuggestedMinimumWidth(),
                        resolveSize(size,
                                widthMeasureSpec)),
                Math.max(getSuggestedMinimumHeight(),
                        resolveSize(size,
                                heightMeasureSpec)));
        //得到每个bar的宽度
        if (mBarData != null) {
            getItemsWidth(screenWidth, mBarData.size());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        leftPoints.clear();
        rightPoints.clear();
        canvas.drawColor(BG_COLOR);
        if (mBarData == null) //winds == null ||  || humidity == null || temperature == null
            return;
        //重置3条线
        linePathW.reset();
        linePathW.incReserve(mBarData.size());
        //        linePathH.reset();
        //        linePathH.incReserve(winds.size());
        //        linePathT.reset();
        //        linePathT.incReserve(winds.size());
        checkTheLeftMoving();
        barPaint.setColor(BG_COLOR); //Color.WHITE(设置顶部和底部的背景颜色)
        canvas.drawRect(bottomWhiteRect, barPaint);
        canvas.drawRect(topWhiteRect, barPaint);
        //画X轴 下面的和上面的
        canvas.drawLine(xStartIndex, yStartIndex, screenWidth - leftMargin, yStartIndex, axisPaint);
        //        canvas.drawLine(xStartIndex, topMargin / 2, screenWidth - leftMargin, topMargin / 2, axisPaint); //上面的X轴

        //画右边的Y轴
        //        canvas.drawLine(screenWidth - leftMargin * 2 - 10, yStartIndex, screenWidth - leftMargin * 2 - 10, topMargin / 2, axisPaint);
        //画右边Y轴text
        //        drawRightYText(canvas);
        //画矩形
        drawBars(canvas);
        canvas.save();
        //画线型图
        canvas.drawPath(linePathW, linePaint);
        //        canvas.drawPath(linePathH, linePaint);
        //        canvas.drawPath(linePathT, linePaint);
        //画线上的点
        drawCircles(canvas);
        //        linePath.rewind();
        //画左边和右边的遮罩层
        int c = barPaint.getColor();
        leftWhiteRect.right = leftMargin; //xStartIndex左边的遮罩层宽度
        barPaint.setColor(BG_COLOR);
        canvas.drawRect(leftWhiteRect, barPaint);
        canvas.drawRect(rightWhiteRect, barPaint);
        barPaint.setColor(c);

        //画左边的Y轴
        canvas.drawLine(xStartIndex, yStartIndex, xStartIndex, topMargin / 2, axisPaint);
        //画左边的Y轴text
        drawLeftYAxis(canvas);
        //左边Y轴的单位
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 20));
        canvas.drawText(leftAxisUnit, xStartIndex - textPaint.measureText(leftAxisUnit) - 5, topMargin / 2, textPaint);
    }

    private void drawLeftYAxis(Canvas canvas) {
        int maxYHeight = (int) (maxHeight / maxValueInItems * maxDivisionValue);
        for (int i = 1; i <= 10; i++) {
            float startY = barRect.bottom - maxYHeight * 0.1f * i;
            if (startY < topMargin / 2) {
                break;
            }
            //            canvas.drawLine(xStartIndex, startY, screenWidth - leftMargin, startY, axisPaint); //画从Y轴延伸出的与X轴平行的线
            String text = String.valueOf(maxDivisionValue * 0.1f * i);
            textPaint.setColor(Color.WHITE); //Y轴颜色
            textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 10));
            canvas.drawText(text, xStartIndex - textPaint.measureText(text) - 5, startY + textPaint.measureText("0"), textPaint); //
        }
    }

    private void drawBars(Canvas canvas) {
        for (int i = 0; i < mBarData.size(); i++) {
            barRect.left = (int) (xStartIndex + barWidth * i + barSpace * (i + 1) - leftMoving);
            barRect.top = (int) (maxHeight + topMargin * 2 - (maxHeight * (mBarData.get(i).getyNum() / maxValueInItems)));
            //            Log.v("maxHeight + topMargin * 2", (maxHeight + topMargin * 2) + "");
            barRect.right = barRect.left + barWidth;
            int temp = barRect.bottom;
            barRect.bottom = (int) yStartIndex;
            if (barRect.top > barRect.bottom || barRect.top == 0) {  //barRect.top > barRect.bottom：为了防止向下（及X轴下面）画条形统计图;
                barRect.top = (int) yStartIndex;
                if (barRect.top == 0) { //barRect.top == 0：为了防止没有数据或是数据全为0（及maxValueInItems=0）
                    //在此做些处理；比如说：提示没有数据、添加图等等
                }
            }
            //            Log.v("barRect.top", mBarData.get(i).getyNum() + ">>>" + barRect.top + "");
            //            Log.v("barRect.bottom ", mBarData.get(i).getyNum() + ">>>" + barRect.bottom + "");
            leftPoints.add(barRect.left);
            rightPoints.add(barRect.right);
            barPaint.setColor(colors[0]);
            canvas.drawRect(barRect, barPaint);

            //            barRect1.top = (int) maxHeight + topMargin * 2 - (int) (maxHeight * (mBarData.get(i).getyNum() / maxValueInItems))
            //                    - (int) (maxHeight * (mBarData.get(i).getyNum1() / maxValueInItems));
            //            barRect1.left = (int) (xStartIndex + barWidth * i + barSpace * (i + 1) - leftMoving);
            //            barRect1.right = barRect.left + barWidth;
            //            barRect1.bottom = barRect.top;
            //            barPaint.setColor(colors[1]);
            //            canvas.drawRect(barRect1, barPaint);

            //            barRect2.top = (int) maxHeight + topMargin * 2 - (int) (maxHeight * (mBarData.get(i).getyNum() / maxValueInItems))
            //                    - (int) (maxHeight * (mBarData.get(i).getyNum1() / maxValueInItems)) - (int) (maxHeight * (mBarData.get(i).getyNum2() / maxValueInItems));
            //            barRect2.left = (int) (xStartIndex + barWidth * i + barSpace * (i + 1) - leftMoving);
            //            barRect2.right = barRect.left + barWidth;
            //            barRect2.bottom = barRect1.top;
            //            barPaint.setColor(colors[2]);
            //            canvas.drawRect(barRect2, barPaint);
            //画x轴的text
            barRect.bottom = temp;  //控制文本和底部X轴的距离
            String text = mBarData.get(i).getxLabel();
            textPaint.setColor(Color.WHITE); //x轴的文本颜色
            //            textPaint.setTextSize(50f); //x轴的文本大小
            textPaint.setTextSize(ScreenUtils.sp2px(getContext(), 13)); //x轴的文本大小
            //            canvas.drawText(text, barRect.left - (textPaint.measureText(text) - barWidth) / 2, barRect.bottom + ScreenUtils.dp2px(getContext(), 10), textPaint);
            canvas.drawText(text, barRect.left - (textPaint.measureText(text) - barWidth) / 2, barRect.bottom + ScreenUtils.dp2px(getContext(), 12), textPaint);

            //确定线形图的路径 和 画圆点
            drawLines(canvas, i);
        }
    }

    /**
     * 画右边的Y轴的text
     *
     * @param canvas
     */
    /*private void drawRightYText(Canvas canvas) {
        if (rightYLabels.length == 9) {
            float eachHeight = ((barRect.bottom - topMargin / 2) / 6f);
            for (int j = 0; j < 7; j++) {
                float startY = barRect.bottom - eachHeight * j;
                //            if (startY < topMargin / 2) {
                //                break;
                //            }
                canvas.drawLine(screenWidth - leftMargin * 2 - 10, startY, screenWidth - leftMargin * 2 - 20, startY, axisPaint);
                String text = rightYLabels[j];
                if (j < 2) {
                    textPaint.setColor(WIND_COLOR);
                    canvas.drawText(text, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                } else {
                    switch (j) {
                        case 2:
                            canvas.drawText(text, screenWidth - leftMargin * 2 - 5, startY + textPaint.measureText("级"), textPaint);
                            String text2 = rightYLabels[j + 1];
                            //                            textPaint.setColor(HUM_COLOR);
                            canvas.drawText(text2, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                            break;
                        case 3:
                            String text3 = rightYLabels[j + 1];
                            canvas.drawText(text3, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                            break;
                        case 4:
                            String text4 = rightYLabels[j + 1];
                            canvas.drawText(text4, screenWidth - leftMargin * 2 - 5, startY + textPaint.measureText("级"), textPaint);
                            String text41 = rightYLabels[j + 2];
                            //                            textPaint.setColor(TEM_COLOR);
                            canvas.drawText(text41, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                            break;
                        case 5:
                            String text5 = rightYLabels[j + 2];
                            canvas.drawText(text5, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                            break;
                        case 6:
                            String text6 = rightYLabels[j + 2];
                            canvas.drawText(text6, screenWidth - leftMargin * 2 - 5, startY + textPaint.measureText("级"), textPaint);
                            textPaint.setColor(Color.BLACK);
                            break;
                    }
                }
            }
        } else {
            float eachHeight = ((barRect.bottom - topMargin / 2) / 4f);
            for (int k = 0; k < 5; k++) {
                float startY = barRect.bottom - eachHeight * k;
                canvas.drawLine(screenWidth - leftMargin * 2 - 10, startY, screenWidth - leftMargin * 2 - 20, startY, axisPaint);
                String text = rightYLabels[k];
                if (k < 2) {
                    //                    textPaint.setColor(HUM_COLOR);
                    canvas.drawText(text, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                } else {
                    switch (k) {
                        case 2:
                            canvas.drawText(text, screenWidth - leftMargin * 2 - 5, startY + textPaint.measureText("级"), textPaint);
                            String text2 = rightYLabels[k + 1];
                            //                            textPaint.setColor(TEM_COLOR);
                            canvas.drawText(text2, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                            break;
                        case 3:
                            String text3 = rightYLabels[k + 1];
                            canvas.drawText(text3, screenWidth - leftMargin * 2 - 5, startY, textPaint);
                            break;
                        case 4:
                            String text4 = rightYLabels[k + 1];
                            canvas.drawText(text4, screenWidth - leftMargin * 2 - 5, startY + textPaint.measureText("级"), textPaint);
                            textPaint.setColor(Color.BLACK);
                            break;
                    }
                }
            }
        }
    }*/

    /**
     * 画线上的点
     */
    private void drawCircles(Canvas canvas) {
        for (int i = 0; i < mBarData.size(); i++) {
            //            if (rightYLabels.length == 9) {
            //            float lineHeight = winds.get(i) * lineEachHeight / 10f;
            pointPaint.setColor(WIND_COLOR);

            barRect.left = (int) (xStartIndex + barWidth * i + barSpace * (i + 1) - leftMoving);
            barRect.top = (int) (maxHeight + topMargin * 2 - (maxHeight * (mBarData.get(i).getyNum() / maxValueInItems)));
            //            Log.v("maxHeight + topMargin * 2", (maxHeight + topMargin * 2) + "");
            barRect.right = barRect.left + barWidth;
            barRect.bottom = (int) yStartIndex;
            if (barRect.top > barRect.bottom || barRect.top == 0) {  //barRect.top > barRect.bottom：为了防止向下（及X轴下面）画条形统计图;
                barRect.top = (int) yStartIndex;
                if (barRect.top == 0) { //barRect.top == 0：为了防止没有数据或是数据全为0（及maxValueInItems=0）
                    //在此做些处理；比如说：提示没有数据、添加图等等
                }
            }

            canvas.drawCircle(leftPoints.get(i) + barWidth / 2, barRect.top, RADIUS, pointPaint);
            //            canvas.drawCircle(leftPoints.get(i) + barWidth / 2, barRect.bottom - lineHeight, RADIUS, pointPaint);
            //                float lineHeight2 = (humidity.get(i) - mHMinValue) * lineEachHeight / eachTotalValueH;
            //                pointPaint.setColor(HUM_COLOR);
            //                canvas.drawCircle(leftPoints.get(i) + barWidth / 2, barRect.bottom - lineHeight2 - lineEachHeight, RADIUS, pointPaint);
            //                float lineHeight3 = Math.abs(temperature.get(i) - mTMinValue) * lineEachHeight / eachTotalValueT;
            //                pointPaint.setColor(TEM_COLOR);
            //                canvas.drawCircle(leftPoints.get(i) + barWidth / 2, barRect.bottom - lineHeight3 - lineEachHeight * 2, RADIUS, pointPaint);
            //            } else {
            //                float lineHeight = (humidity.get(i) - mHMinValue) * lineEachHeightT / eachTotalValueH;
            //                pointPaint.setColor(HUM_COLOR);
            //                canvas.drawCircle(leftPoints.get(i) + barWidth / 2, barRect.bottom - lineHeight, RADIUS, pointPaint);
            //                float lineHeight1 = Math.abs(temperature.get(i) - mTMinValue) * lineEachHeightT / eachTotalValueT;
            //                pointPaint.setColor(TEM_COLOR);
            //                canvas.drawCircle(leftPoints.get(i) + barWidth / 2, barRect.bottom - lineHeight1 - lineEachHeightT, RADIUS, pointPaint);
            //            }
        }
    }

    /**
     * 画线形图
     */
    private void drawLines(Canvas canvas, int i) {
        //        if (rightYLabels.length == 9) {
        //        float lineHeight = winds.get(i) * lineEachHeight / 10f;
        barRect.left = (int) (xStartIndex + barWidth * i + barSpace * (i + 1) - leftMoving);
        barRect.top = (int) (maxHeight + topMargin * 2 - (maxHeight * (mBarData.get(i).getyNum() / maxValueInItems)));
        //            Log.v("maxHeight + topMargin * 2", (maxHeight + topMargin * 2) + "");
        barRect.right = barRect.left + barWidth;
        barRect.bottom = (int) yStartIndex;
        if (barRect.top > barRect.bottom || barRect.top == 0) {  //barRect.top > barRect.bottom：为了防止向下（及X轴下面）画条形统计图;
            barRect.top = (int) yStartIndex;
            if (barRect.top == 0) { //barRect.top == 0：为了防止没有数据或是数据全为0（及maxValueInItems=0）
                //在此做些处理；比如说：提示没有数据、添加图等等
            }
        }
        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.RED);
        shadowPaint.setStrokeWidth(10);// 默认线宽为3
        shadowPaint.setAlpha(50);
        Path shadowPath = new Path();
        if (i == 0) {
            linePathW.moveTo(barRect.left + barWidth / 2, barRect.top);
            //            linePathW.moveTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight);
        } else {
            linePathW.lineTo(barRect.left + barWidth / 2, barRect.top);
            //            linePathW.lineTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight);
            shadowPath.moveTo((int) (xStartIndex + barWidth * (i - 1) + barSpace * i - leftMoving) + barWidth / 2, yStartIndex); //左底
            shadowPath.lineTo((int) (xStartIndex + barWidth * (i - 1) + barSpace * i - leftMoving) + barWidth / 2, (int) (maxHeight + topMargin * 2 - (maxHeight * (mBarData.get(i - 1).getyNum() / maxValueInItems)))); //左顶
            shadowPath.lineTo(barRect.left + barWidth / 2, barRect.top); //右顶
            shadowPath.lineTo(barRect.left + barWidth / 2, yStartIndex); //右底
            shadowPath.close();
            canvas.drawPath(shadowPath, shadowPaint);
        }
        //            float lineHeight2 = (humidity.get(i) - mHMinValue) * lineEachHeight / eachTotalValueH;
        //            if (i == 0) {
        //                linePathH.moveTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight2 - lineEachHeight);
        //            } else {
        //                linePathH.lineTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight2 - lineEachHeight);
        //            }

        //            float lineHeight3 = Math.abs(temperature.get(i) - mTMinValue) * lineEachHeight / eachTotalValueT;
        //            if (i == 0) {
        //                linePathT.moveTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight3 - lineEachHeight * 2);
        //            } else {
        //                linePathT.lineTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight3 - lineEachHeight * 2);
        //            }
        //        } else {
        //            float lineHeight2 = (humidity.get(i) - mHMinValue) * lineEachHeightT / eachTotalValueH;
        //            if (i == 0) {
        //                linePathH.moveTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight2);
        //            } else {
        //                linePathH.lineTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight2);
        //            }
        //            float lineHeight3 = Math.abs(temperature.get(i) - mTMinValue) * lineEachHeightT / eachTotalValueT;
        //            if (i == 0) {
        //                linePathT.moveTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight3 - lineEachHeightT);
        //            } else {
        //                linePathT.lineTo(barRect.left + barWidth / 2, barRect.bottom - lineHeight3 - lineEachHeightT);
        //            }
        //        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);// 请求SlidingMenu不要拦截这个事件(防止滑动冲突)
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);// 请求SlidingMenu不要拦截这个事件(防止滑动冲突)

                // 请求父类不要拦截这个事件(防止滑动冲突)
                this.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                lastPointX = event.getRawX();
                lastPointY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                this.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);// 请求SlidingMenu不要拦截这个事件(防止滑动冲突)

                // 请求父类不要拦截这个事件(防止滑动冲突)
                this.getParent().getParent().requestDisallowInterceptTouchEvent(true);
                float movex = event.getRawX();
                float movey = event.getRawY();
                movingThisTime = lastPointX - movex; //该值很重要(小于0是从左向右滑；大于0是从右向左滑)
                //                Log.v("lastPointX", lastPointX + "");
                //                Log.v("movex", movex + "");
                //                Log.v("lastPointY", lastPointY + "");
                //                Log.v("movey", movey + "");
                if (Math.abs(lastPointY - movey) > 150) {
                    this.getParent().getParent().requestDisallowInterceptTouchEvent(false);
                    this.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);// 请求SlidingMenu不要拦截这个事件(防止滑动冲突)
                }
                leftMoving = leftMoving + movingThisTime;
                lastPointX = movex;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                this.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);// 请求SlidingMenu不要拦截这个事件(防止滑动冲突)
                if (movingThisTime < 0 && leftMoving <= 0) { //分页加载
                    if (onSlideLeftListener != null) {
                        onSlideLeftListener.load();
                    }
                }
                new Thread(new SmoothScrollThread(movingThisTime)).start();
                break;
            default:
                this.getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);// 请求SlidingMenu不要拦截这个事件(防止滑动冲突)

                return super.onTouchEvent(event);
        }
        if (mGestureListener != null) {
            mGestureListener.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 检查向左滑动的距离 确保没有画出屏幕
     */
    private void checkTheLeftMoving() {
        //        Log.v("leftMoving", leftMoving + "");
        if (leftMoving < 0) {
            leftMoving = 0;
        }
        //        Log.v("maxRight", maxRight + "");
        //        Log.v("minRight", minRight + "");
        if (leftMoving > (maxRight - minRight)) {
            leftMoving = maxRight - minRight;
        }
    }

    /**
     * 设置矩形的顶部 底部 右边Y轴的3部分每部分的高度
     */
    private void getStatusHeight() {
        barRect.top = topMargin * 2;
        barRect.bottom = screenHeight - topMargin / 2;
        maxHeight = barRect.bottom - barRect.top;
        //        lineEachHeight = (barRect.bottom - topMargin / 2) / 3;
        lineEachHeightT = (barRect.bottom - topMargin / 2) / 2;


        yStartIndex = barRect.bottom - 15; //之前只是barRect.bottom
    }

    public void setRightYLabels(String[] rightYLabels) {
        //                this.rightYLabels = rightYLabels;
        changeRightYLabels();
        invalidate();
    }

    /**
     * 赋值
     *
     * @param items       柱形图的值
     * @param winds       风力线形图的值
     * @param humidity    湿度线形图的值
     * @param temperature 温度线形图的值
     */
    public void setItems(List<LineChartBean> items, List<Float> winds, List<Float> humidity, List<Float> temperature, String[] rightYLabels) {
        if (items == null) { //|| winds == null
            throw new RuntimeException("BarChartView.setItems(): the param items cannot be null.");
        }
        if (items.size() == 0) {
            return;
        }
        this.mBarData = items;
        //        this.rightYLabels = rightYLabels;
        //        this.winds = winds;
        //        this.humidity = humidity;
        //        this.temperature = temperature;
        //计算最大值
        maxValueInItems = items.get(0).getyNum() + items.get(0).getyNum1() + items.get(0).getyNum2();
        for (LineChartBean lineChartBean : items) {
            float totalNum = lineChartBean.getyNum() + lineChartBean.getyNum1() + lineChartBean.getyNum2();
            if (totalNum > maxValueInItems) {
                maxValueInItems = totalNum;
            }
        }
        changeRightYLabels();
        //获取分度值
        getRange(maxValueInItems, 0);

        requestLayout(); //执行onMeasure方法和onLayout方法
        invalidate(); //执行onDraw方法
    }

    /**
     * 计算右边Y轴的刻度标签的值
     */
    private void changeRightYLabels() {
        //        float HMaxValue = humidity.get(0);
        //        float HMinValue = humidity.get(0);
        //        for (Float hum : humidity) {
        //            if (hum > HMaxValue) {
        //                HMaxValue = hum;
        //            }
        //            if (hum < HMinValue) {
        //                HMinValue = hum;
        //            }
        //        }
        //        float TMaxValue = temperature.get(0);
        //        float TMinValue = temperature.get(0);
        //        for (Float tem : temperature) {
        //            if (tem > TMaxValue) {
        //                TMaxValue = tem;
        //            }
        //            if (tem < TMinValue) {
        //                TMinValue = tem;
        //            }
        //        }
        //        int hMaxScale = getScale(HMaxValue);
        //        float unHMaxScaleValue = (float) (HMaxValue / Math.pow(10, hMaxScale));
        //        int hMinScale = getScale(HMinValue);
        //        float unHMinScaleValue = (float) (HMinValue / Math.pow(10, hMinScale));
        //        int hMax = (int) (getRangeTop(unHMaxScaleValue) * Math.pow(10, hMaxScale));
        //        int hMin = (int) (getRangeMin(unHMinScaleValue) * Math.pow(10, hMinScale));

        //        int tMaxScale = getScale(Math.abs(TMaxValue));
        //        float unTMaxScaleValue = (float) (TMaxValue / Math.pow(10, tMaxScale));
        //        int tMinScale = getScale(Math.abs(TMinValue));
        //        float unTMinScaleValue = (float) (TMinValue / Math.pow(10, tMinScale));
        //        int tMax = (int) (getRangeTop(Math.abs(unTMaxScaleValue)) * Math.pow(10, tMaxScale));
        //        int tMin = (int) (getRangeMin(Math.abs(unTMinScaleValue)) * Math.pow(10, tMinScale));
        //        tMax = TMaxValue < 0 ? -tMax : tMax;
        //        tMin = TMinValue < 0 ? -tMin : tMin;
        //        if (rightYLabels.length == 9) {
        //            rightYLabels[3] = hMin + "%rh";
        //            rightYLabels[4] = hMin + (hMax - hMin) / 2 + "%rh";
        //            rightYLabels[5] = hMax + "%rh";
        //            rightYLabels[6] = tMin + getResources().getString(R.string.degree_centigrade);
        //            rightYLabels[7] = tMin + (tMax - tMin) / 2 + getResources().getString(R.string.degree_centigrade);
        //            rightYLabels[8] = tMax + getResources().getString(R.string.degree_centigrade);
        //        } else {
        //            rightYLabels[0] = hMin + "%rh";
        //            rightYLabels[1] = hMin + (hMax - hMin) / 2 + "%rh";
        //            rightYLabels[2] = hMax + "%rh";
        //            rightYLabels[3] = tMin + getResources().getString(R.string.degree_centigrade);
        //            rightYLabels[4] = tMin + (tMax - tMin) / 2 + getResources().getString(R.string.degree_centigrade);
        //            rightYLabels[5] = tMax + getResources().getString(R.string.degree_centigrade);
        //        }
        //        eachTotalValueH = hMax - hMin;
        //        eachTotalValueT = tMax - tMin;
        //        mHMinValue = hMin;
        //        mTMinValue = tMin;
    }

    /**
     * 设置左边的Y轴的单位
     *
     * @param labels
     */
    public void setLeftYAxisLabels(String labels) {
        this.leftAxisUnit = labels;
    }

    /**
     * 设定每个bar的宽度 和向右边滑动的时候右边的最大距离
     *
     * @param screenWidth
     * @param size
     */
    private void getItemsWidth(int screenWidth, int size) {
        int barMinWidth = 0;
        int barMinSpace = 0;
        Log.v("lzf-mBarData", mBarData.toString());
        //        if (mBarData.get(0).getTime() == 0) { //周统计的条形统计图
        //            barMinWidth = ScreenUtils.dp2px(getContext(), 70); //ScreenUtils.dp2px(getContext(), 40);
        //            barMinSpace = ScreenUtils.dp2px(getContext(), 30); // ScreenUtils.dp2px(getContext(), 10)
        //        } else { //日统计和月统计的条形统计图
        barMinWidth = ScreenUtils.dp2px(getContext(), 30); //ScreenUtils.dp2px(getContext(), 40);
        barMinSpace = ScreenUtils.dp2px(getContext(), 25); // ScreenUtils.dp2px(getContext(), 10)
        //            if (mBarData.get(0).getxLabel().equals("") || mBarData.get(0).getxLabel() == null) { //默认
        //                barMinWidth = ScreenUtils.dp2px(getContext(), 50); //ScreenUtils.dp2px(getContext(), 40);
        //                barMinSpace = ScreenUtils.dp2px(getContext(), 20); // ScreenUtils.dp2px(getContext(), 10)
        //            } else {
        //                barMinWidth = ScreenUtils.dp2px(getContext(), 40); //ScreenUtils.dp2px(getContext(), 40);
        //                barMinSpace = ScreenUtils.dp2px(getContext(), 20); // ScreenUtils.dp2px(getContext(), 10)
        //            }
        //        }

        //        barWidth = (screenWidth - leftMargin * 2) / (size + 3); ///(size + 3);
        //        barSpace = (screenWidth - leftMargin * 2 - barWidth * size) / (size + 1);
        //        if (barWidth < barMinWidth || barSpace < barMinSpace) {
        barWidth = barMinWidth;
        barSpace = barMinSpace;
        //        }
        //        maxRight = (int) (xStartIndex + (barSpace + barWidth) * mBarData.size()) + barSpace * 2;
        //                minRight = screenWidth - barSpace - leftMargin;
        maxRight = (int) (xStartIndex + (barSpace + barWidth) * mBarData.size()) + barSpace;
        minRight = getMeasuredWidth() - (int) (barSpace * 1.1);
    }

    /**
     * 得到柱状图的最大和最小的分度值
     *
     * @param maxValueInItems
     * @param min
     */

    private void getRange(float maxValueInItems, float min) {
        int scale = getScale(maxValueInItems);
        float unScaleValue = (float) (maxValueInItems / Math.pow(10, scale));

        maxDivisionValue = (float) (getRangeTop(unScaleValue) * Math.pow(10, scale));

        xStartIndex = leftMargin; //getDivisionTextMaxWidth(maxDivisionValue) + 10
    }

    /**
     * 得到最大宽度值得文本
     *
     * @param maxDivisionValue
     * @return
     */
    private float getDivisionTextMaxWidth(float maxDivisionValue) {
        Paint textPaint = new Paint();
        textPaint.setTextSize(ScreenUtils.dp2px(getContext(), 10));
        float max = textPaint.measureText(String.valueOf(maxDivisionValue * 1.0f));
        for (int i = 2; i <= 10; i++) {
            float w = textPaint.measureText(String.valueOf(maxDivisionValue * 0.1f * i));
            if (w > max) {
                max = w;
            }
        }
        return max;
    }

    private float getRangeTop(float value) {
        //value: [1,10)
        if (value < 1.2) {
            return 1.2f;
        }

        if (value < 1.5) {
            return 1.5f;
        }

        if (value < 2.0) {
            return 2.0f;
        }

        if (value < 3.0) {
            return 3.0f;
        }

        if (value < 4.0) {
            return 4.0f;
        }

        if (value < 5.0) {
            return 5.0f;
        }

        if (value < 6.0) {
            return 6.0f;
        }
        if (value < 7.0) {
            return 7.0f;
        }

        if (value < 8.0) {
            return 8.0f;
        }

        return 10.0f;
    }

    private float getRangeMin(float value) {
        //value: [1,10)
        if (value < 1.0) {
            return 0f;
        }

        if (value < 1.5) {
            return 1.0f;
        }

        if (value < 2.0) {
            return 1.0f;
        }

        if (value < 3.0) {
            return 2.0f;
        }

        if (value < 4.0) {
            return 3.0f;
        }

        if (value < 5.0) {
            return 4.0f;
        }

        if (value < 6.0) {
            return 5.0f;
        }
        if (value < 7.0) {
            return 6.0f;
        }

        if (value < 8.0) {
            return 7.0f;
        }
        if (value < 9.0) {
            return 8.0f;
        }

        return 9.0f;
    }

    /**
     * 获取这个最大数 数总共有几位
     *
     * @param value
     * @return
     */
    public static int getScale(float value) {
        if (value >= 1 && value < 10) {
            return 0;
        } else if (value >= 10) {
            return 1 + getScale(value / 10);
        } else if (value < 1 && value > 0) {
            return getScale(value * 10) - 1;
        } else { // if (value == 0)
            return 0;  //不懂。
        } /*else{
            return getScale(value * 10) - 1;
        }*/
    }

    /**
     * 根据点击的手势位置识别是第几个柱图被点击
     *
     * @param x
     * @param y
     * @return -1时表示点击的是无效位置
     */
    private int identifyWhichItemClick(float x, float y) {
        float leftx = 0;
        float rightx = 0;
        for (int i = 0; i < mBarData.size(); i++) {
            leftx = leftPoints.get(i);
            rightx = rightPoints.get(i);
            if (x < leftx) {
                break;
            }
            if (leftx <= x && x <= rightx) {
                return i;
            }
        }
        return INVALID_POSITION;
    }

    /**
     * 手势监听器
     *
     * @author A Shuai
     */
    private class RangeBarOnGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = identifyWhichItemClick(e.getX(), e.getY());
            if (position != INVALID_POSITION && mOnItemBarClickListener != null) {
                mOnItemBarClickListener.onClick(position);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }

    }

    /**
     * 左右滑动的时候 当手指抬起的时候  使滑动慢慢停止 不会立刻停止
     */
    private class SmoothScrollThread implements Runnable {
        float lastMoving;
        boolean scrolling = true;

        private SmoothScrollThread(float lastMoving) {
            this.lastMoving = lastMoving;
            scrolling = true;
            //            Log.v("lastMoving", lastMoving + "");
            //            Log.v("scrolling", scrolling + "");
        }

        @Override
        public void run() {
            while (scrolling) {
                long start = System.currentTimeMillis();
                //                Log.v("start", start + "");
                lastMoving = (int) (0.9f * lastMoving);
                //                Log.v(" lastMoving = (int) (0.9f * lastMoving)", lastMoving + "");
                leftMoving += lastMoving;
                //                Log.v(" leftMoving += lastMoving", lastMoving + "");
                checkTheLeftMoving();
                postInvalidate();
                if (Math.abs(lastMoving) < 5) {
                    scrolling = false;
                }
                //                Log.v("scrolling", scrolling + "");
                long end = System.currentTimeMillis();
                //                Log.v("end", end + "");
                if (end - start < 20) {
                    try {
                        Thread.sleep(20 - (end - start));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
