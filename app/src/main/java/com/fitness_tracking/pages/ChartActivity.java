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
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString=dateFormat.format(currentDate);
        Set<String> dateList=DB.getDistinctFormattedDatesForUser(id);

        Toast.makeText(ChartActivity.this, "the size list date is : "+dateList.size(), Toast.LENGTH_SHORT).show();

        List<Float> consumptionValues = new ArrayList<>();
        consumptionValues.add((float) DB.getTotalWeightForDateAndUser(currentDateString,id));
        consumptionValues.add(1.55f);
        consumptionValues.add(1.4f);
        consumptionValues.add(2.4f);

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Months are 0-indexed
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        int[] previousMonthYear = new int[]{1,1};
        int previousMonth = previousMonthYear[0];
        int previousYear = previousMonthYear[1];

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, consumptionValues.get(0))); // Actual month
        entries.add(new BarEntry(1, consumptionValues.get(1)));
        entries.add(new BarEntry(2, consumptionValues.get(2)));// Previous month
        entries.add(new BarEntry(3, consumptionValues.get(3)));

        BarDataSet barDataSet = new BarDataSet(entries, "Monthly Consumption");
        BarData barData = new BarData(barDataSet);

        // Customize the chart appearance if needed
        barChart.setData(barData);

        // Set labels for each bar
        ArrayList<String> labels = new ArrayList<>();
        labels.add("Year"); // Actual month
        labels.add("Total");
        labels.add("hhhh");
        labels.add("heelo"); // Previous month

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1);

        barChart.invalidate(); // Refresh the chart


    }
}
