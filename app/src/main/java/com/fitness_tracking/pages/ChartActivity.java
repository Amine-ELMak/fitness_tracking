package com.fitness_tracking.pages;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fitness_tracking.Dao.DatabaseHandler;
import com.fitness_tracking.R;
import com.fitness_tracking.auth.LoginActivity;
import com.fitness_tracking.auth.SessionManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ChartActivity extends AppCompatActivity {

    DatabaseHandler DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);
        DB = new DatabaseHandler(this);

        BarChart barChart = findViewById(R.id.barChart);

        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Set<String> dateList=DB.getDistinctFormattedDatesForUser(id);

        List<Float> consumptionValues = new ArrayList<>();
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        int i=0;
        for(String dddd: dateList){
            consumptionValues.add((float) DB.getTotalWeightForDateAndUser(dddd,id));
            entries.add(new BarEntry(i, consumptionValues.get(i)));
            labels.add(dddd);
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Total Calories(daily)");
        barDataSet.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        barChart.getDescription().setEnabled(false);
        BarData barData = new BarData(barDataSet);

        barChart.setDrawGridBackground(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getAxisLeft().setDrawGridLines(false);

        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1);


        barChart.invalidate();


    }
}
