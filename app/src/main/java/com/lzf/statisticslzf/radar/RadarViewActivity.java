package com.lzf.statisticslzf.radar;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import com.lzf.statisticslzf.R;

import java.util.ArrayList;

/**
 * Created by MJCoder on 2017-10-18.
 */

public class RadarViewActivity extends Activity {
    RadarView mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radarview);

        mChart = (RadarView) findViewById(R.id.radarView);

        initRadar();
    }

    private void initRadar() {
        String[] mActivities = new String[]{"商业模式", "核心壁垒", "团队结构", "运营管理", "盈利能力"};
        mChart.setTitles(mActivities);
        ArrayList<Float> data = new ArrayList<>();
        ArrayList<Float> data2 = new ArrayList<>();
        data.add(3.2f);
        data.add(4f);
        data.add(3.8f);
        data.add(2.2f);
        //        data.add(3.0f);   //, "成长能力"
        data.add(4.5f);
        data2.add(1.5f);
        data2.add(4.6f);
        data2.add(4.2f);
        data2.add(2.3f);
        //        data.add(3.0f);   //, "成长能力"
        data2.add(1.5f);
        mChart.setData(data);
        mChart.setData2(data2);
        mChart.setMaxValue(5);
        mChart.setValuePaintColor2(Color.BLUE);
        mChart.setValuePaintColor(Color.RED);
        mChart.setStrokeWidth(5f);
        mChart.setMainPaintColor(Color.RED);
        mChart.setCircleRadius(5f);
        mChart.setTextPaintTextSize(40);
        mChart.setInnerAlpha(100);
        mChart.setLableCount(6);
        mChart.setDrawLabels(false);
        mChart.setShowValueText(false);
        mChart.invalidate();
    }
}
