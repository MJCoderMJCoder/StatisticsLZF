package com.lzf.statisticslzf.line;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.lzf.statisticslzf.R;

import java.util.ArrayList;
import java.util.List;

public class LineChartActivity extends AppCompatActivity {
    private LineData lineData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_chart);

        testPhilJayMPAndroidChart();
        testMJCoderLineChart();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        findViewById(R.id.barChart).setSelected(true);
    }

    /**
     * MPAndroidChart测试
     */
    private void testPhilJayMPAndroidChart() {

        final LineChart chart = (LineChart) findViewById(R.id.chart);
        chart.setNoDataText("没有数据");//设置图表为空时应显示的文本。
        chart.setDrawGridBackground(false); //如果启用，则绘制图表绘制区域后面的背景矩形将被绘制。
        chart.setTouchEnabled(true); //允许启用/禁用与图表的所有可能的触摸交互。
        chart.setDragEnabled(true); //启用/禁用拖动（平移）图表。
        chart.setScaleEnabled(false); //启用/禁用两个轴上图表的缩放比例。
        chart.setDoubleTapToZoomEnabled(false); //将其设置为false以禁止通过双击缩放图表来缩放图表。
        chart.setDragDecelerationEnabled(true); //如果设置为true，图表会在修改后继续滚动。默认值：true。
        chart.setDragDecelerationFrictionCoef(0.5f); //减速摩擦系数在[0; 1]间隔时，较高的值表示速度将缓慢下降，例如，如果设置为0，则会立即停止。1是无效值，将自动转换为0.9999。
        chart.getAxisRight().setEnabled(false); //设置轴启用或禁用。如果禁用，则不管任何其他设置，都不会绘制轴的任何部分。
        chart.getAxisLeft().setDrawGridLines(false); //将其设置为true以启用绘制轴的网格线
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM); //设置XAxis应该出现的位置。在TOP，BOTTOM，BOTH_SIDED，TOP_INSIDE或BOTTOM_INSIDE之间选择。
        chart.getXAxis().setDrawGridLines(false); //将其设置为true以启用绘制轴的网格线
        chart.getXAxis().setGranularity(1f);//限制间隔为1（最小）
        chart.getXAxis().setAvoidFirstLastClipping(true);
        chart.getAxisRight().setDrawAxisLine(false); //如果应绘制轴（轴线）旁边的线条，则将其设置为true。
        chart.setAutoScaleMinMaxEnabled(true); //表示是否启用y轴上的自动缩放的标志。 如果启用，则每当视口更改时，y轴会自动调整到当前x轴范围的最小值和最大值y值。 这对于显示财务数据的图表特别有用。 默认值：false
        chart.setVisibleXRangeMaximum(5); //设置应该一次最大可见的区域的大小（在x轴上的范围）。如果这是例如设置为10，则在不滚动的情况下可以一次查看不超过10个x轴上的值。
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) { //在图表中选择了一个值时调用
                Log.v("Entry", e.toString());
                Log.v("Highlight", h.toString());
            }

            @Override
            public void onNothingSelected() { //当没有选择任何东西或已经做出“取消选择”时调用。
                Log.v("NothingSelected", "NothingSelected");
            }
        });

        final List<Entry> entries = new ArrayList<Entry>();
        // turn your data into Entry objects
        entries.add(new Entry(0, 6.3f));
        entries.add(new Entry(1, 8.9f));
        entries.add(new Entry(2, 20.9f));
        entries.add(new Entry(3, 5.3f));
        entries.add(new Entry(4, 11.1f));
        entries.add(new Entry(5, 20.1f));
        entries.add(new Entry(6, 1));
        entries.add(new Entry(7, 5));

        final LineDataSet dataSet = new LineDataSet(entries, "标签"); // add entries to dataset
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.GREEN); // styling, ...

        lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.invalidate(); // refresh

        findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lineData.addEntry(new Entry(8, 22.9f), 0);
                lineData.addEntry(new Entry(9, 0.1f), 0);
                lineData.addEntry(new Entry(10, 23.1f), 0);
                lineData.addEntry(new Entry(11, 2.3f), 0);
                lineData.addEntry(new Entry(12, 0.23f), 0);
                chart.notifyDataSetChanged();
                chart.invalidate(); // refresh
            }
        });

        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {

            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) { //监听左右、上下滑动的手势。
                Log.v("me1", me1.toString());
                Log.v("me2", me2.toString());
                Log.v("velocityX", velocityX + "");
                Log.v("velocityY", velocityY + "");
                chart.moveViewToX(lineData.getXMin() - 3);
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
            }
        });
    }

    /**
     * 自己手绘的折线统计图
     */
    private void testMJCoderLineChart() {
        //初始化条形统计图
        com.lzf.statisticslzf.line.LineChart lineChart = (com.lzf.statisticslzf.line.LineChart) findViewById(R.id.barChart);
        final List<LineChartBean> data = new ArrayList<>();
        data.add(new LineChartBean("左", 21)); //, 500, 600
        data.add(new LineChartBean("11/18", 0)); //, 555, 666
        data.add(new LineChartBean("11月", 67)); //, 333, 222
        data.add(new LineChartBean("本月", 43)); //, 111, 444
        data.add(new LineChartBean("Dec 5", 6)); //, 111, 444
        data.add(new LineChartBean("11/21", 1)); //, 500, 600
        data.add(new LineChartBean("11/22", 8)); //, 456, 123
        data.add(new LineChartBean("11/24", 65)); //, 951, 12
        data.add(new LineChartBean("今天", 29)); //, 111, 444
        data.add(new LineChartBean("右", 85)); //, 111, 444
        lineChart.setLeftYAxisLabels("");
        lineChart.setItems(data, null, null, null, null);
        lineChart.setOnItemBarClickListener(new com.lzf.statisticslzf.line.LineChart.OnItemBarClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(LineChartActivity.this, "单击了第" + position + "项", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
