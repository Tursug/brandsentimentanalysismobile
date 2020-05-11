package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        PieChart piechart = findViewById(R.id.pieChart);
        piechart.setUsePercentValues(true);

        Intent pos = getIntent();
        Intent neg = getIntent();
        Intent neu = getIntent();

        int positive = pos.getIntExtra("positive", 0);
        int negative = neg.getIntExtra("negative", 0);
        int neutral = neu.getIntExtra("neutral", 0);

        List<PieEntry> value = new ArrayList<>();

        value.add(new PieEntry(positive, "Positive"));
        value.add(new PieEntry(negative, "Negative"));
        value.add(new PieEntry(neutral, "Neutral"));


        PieDataSet pieDataSet = new PieDataSet(value, "Sentiment");
        pieDataSet.setColors(new int[] { Color.GREEN, Color.RED, Color.YELLOW});
        PieData pieData = new PieData(pieDataSet);
        piechart.setData(pieData);
        piechart.animateXY(1400,1400);
    }
}
