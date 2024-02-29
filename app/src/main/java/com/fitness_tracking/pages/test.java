package com.fitness_tracking.pages;

import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fitness_tracking.Dao.DatabaseHandler;
import com.fitness_tracking.R;
import com.fitness_tracking.entities.Steps;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class test extends AppCompatActivity {
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        db = new DatabaseHandler(this);

        PieChart pieChart = findViewById(R.id.pieChart);

        // Example data (replace this with your actual data)
        int maxSteps = 1000;
        Date today = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = dateFormat.format(today);
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Steps s= db.getStepsByDate(date);
        int stepsTaken = s.getStep();

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(stepsTaken, "Steps Taken"));
        entries.add(new PieEntry(maxSteps - stepsTaken, "Remaining Steps"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorGr), Color.LTGRAY}); // Colors for each entry
       
        PieData data = new PieData(dataSet);
        data.setDrawValues(true); // Whether to draw values (number of steps) on the chart

        pieChart.setData(data);

        // Customize PieChart
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(50f); // Size of the hole in the center of the PieChart
        pieChart.setTransparentCircleRadius(55f); // Size of the transparent circle around the PieChart
        pieChart.setEntryLabelColor(Color.BLACK); // Color of labels (e.g., Steps Taken, Remaining Steps)
        pieChart.setEntryLabelTextSize(12f); // Size of labels

        pieChart.invalidate(); // Refresh the chart
    }
}