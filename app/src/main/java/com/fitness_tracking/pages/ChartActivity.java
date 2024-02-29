package com.fitness_tracking.pages;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fitness_tracking.Dao.DatabaseHandler;
import com.fitness_tracking.R;
import com.fitness_tracking.auth.LoginActivity;
import com.fitness_tracking.auth.SessionManager;
import com.fitness_tracking.entities.Steps;
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

        ImageView iconGoBack = findViewById(R.id.icon_go_back2);
        iconGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute method when the user clicks the "Go Back" icon
                // Example: onBackPressed();
                System.out.println("go back is pressed ******************");
                finish();
            }
        });


        BarChart barChart = findViewById(R.id.barChart);  // Corrected ID
        BarChart stepChart = findViewById(R.id.lineChart);  // Corrected ID

        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Set<String> dateList = DB.getDistinctFormattedDatesForUser(id);

        List<Float> consumptionValues = new ArrayList<>();
        List<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        List<Steps> steps = DB.getAllStepsForUser(id);
        List<Float> consumptionValuesstep = new ArrayList<>();
        List<BarEntry> entriesstep = new ArrayList<>();
        ArrayList<String> labelsstep = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        int j = 0;
        for (Steps dddd : steps) {
            consumptionValuesstep.add((float) dddd.getStep());
            entriesstep.add(new BarEntry(j, consumptionValuesstep.get(j))); // Corrected usage
            labelsstep.add(dateFormat.format(dddd.getDate()));
            j++;
        }

        BarDataSet barDataSetStep = new BarDataSet(entriesstep, "Total Steps(daily)");
        barDataSetStep.setColors(new int[]{getResources().getColor(R.color.colorPrimary)});
        stepChart.getDescription().setEnabled(false);
        BarData barDataStep = new BarData(barDataSetStep);

        stepChart.setDrawGridBackground(false);
        stepChart.getXAxis().setDrawGridLines(false);
        stepChart.getAxisLeft().setDrawGridLines(false);

        stepChart.setData(barDataStep);

        XAxis xAxisStep = stepChart.getXAxis();
        xAxisStep.setValueFormatter(new IndexAxisValueFormatter(labelsstep)); // Corrected usage
        xAxisStep.setGranularity(1);
        stepChart.invalidate();

        int i = 0;
        for (String dddd : dateList) {
            consumptionValues.add((float) DB.getTotalWeightForDateAndUser(dddd, id));
            entries.add(new BarEntry(i, consumptionValues.get(i)));
            labels.add(dddd);
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Total Calories(daily)");
        barDataSet.setColors(new int[]{getResources().getColor(R.color.colorGr)});
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
