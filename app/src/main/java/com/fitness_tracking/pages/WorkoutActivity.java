package com.fitness_tracking.pages;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fitness_tracking.Dao.DatabaseHandler;
import com.fitness_tracking.Dao.RepatAdapter;
import com.fitness_tracking.Dao.SteepsCounter;
import com.fitness_tracking.Dao.WorkoutAdapter;
import com.fitness_tracking.R;
import com.fitness_tracking.auth.LoginActivity;
import com.fitness_tracking.auth.Register;
import com.fitness_tracking.auth.SessionManager;
import com.fitness_tracking.entities.Exercice;
import com.fitness_tracking.entities.Produit;
import com.fitness_tracking.entities.Repat;
import com.fitness_tracking.entities.Steps;
import com.fitness_tracking.entities.Workout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WorkoutActivity extends AppCompatActivity   implements SensorEventListener {
    WorkoutAdapter listAdapter;
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean runing = true;
    private int countSteeps;
    RepatAdapter listRepatAdapter;
    List<Workout> dataArrayList = new ArrayList<>();

    List<Repat> dataRepatArrayList=new ArrayList<>();
    DatabaseHandler databaseHandler = new DatabaseHandler(this);
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        ListView listView = findViewById(R.id.workoutListView);

        ListView listView1=findViewById(R.id.repatListView);

        Long id = SessionManager.getInstance().getCurrentUser().getId();

        dataArrayList = databaseHandler.getAllWorkoutsForUser(id);

        Date today = new Date();
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // Format the date to string
        String dateString = dateFormat.format(today);
        dataRepatArrayList=databaseHandler.getRepatForDateAndUser(dateString,id);
        //dataRepatArrayList=databaseHandler.getAllRepatsForUser(id);
        System.out.println("------------- here -------------- "+dataRepatArrayList);

        listAdapter = new WorkoutAdapter(getApplicationContext(), this, dataArrayList);
        listRepatAdapter=new RepatAdapter(getApplicationContext(),this,dataRepatArrayList);
        int count=databaseHandler.getAllWorkoutsForUser(id).size();

        listView.setAdapter(listAdapter);
        listView1.setAdapter(listRepatAdapter);

        ImageButton btnAddWorkout = findViewById(R.id.btnAddWorkout);

        ImageButton btnAddRepat= findViewById(R.id.btnAddRepat);

        btnAddRepat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddRepatDialog("Add Repat", WorkoutActivity.this, null);
            }
        });

        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddWorkoutDialog("Add Workout", WorkoutActivity.this, null);
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.person) {
                    Intent intent4 = new Intent(WorkoutActivity.this, ProfileActivity.class);
                    startActivity(intent4);
                    return true;

                } else if (id == R.id.home) {
                    Toast.makeText(WorkoutActivity.this, "home", Toast.LENGTH_SHORT).show();
                    return true;

                } else if (id == R.id.fitness) {
                    Intent intent4 = new Intent(WorkoutActivity.this, ProductActivity.class);
                    startActivity(intent4);
                    return true;

                } else if (id == R.id.workout) {
                    Intent intent4 = new Intent(WorkoutActivity.this, CategoryActivity.class);
                    startActivity(intent4);
                    return true;
                }
                return false;
            }
        });

        PieChart pieChart = findViewById(R.id.pieChart);

        // Example data (replace this with your actual data)
        int maxSteps = 1000;
        Date todayy = new Date();
        SimpleDateFormat dateFormatt = new SimpleDateFormat("yyyy-MM-dd");
        String dateStringg = dateFormatt.format(todayy);
        Date date;
        try {
            date = dateFormat.parse(dateStringg);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Steps s= databaseHandler.getStepsByDate(date);
        int stepsTaken= 0;
        if(s!=null){
            stepsTaken= s.getStep();
        }else{
            databaseHandler.addSteps(new Steps(null,todayy,stepsTaken,id));
        }

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(stepsTaken));
        entries.add(new PieEntry(maxSteps - stepsTaken));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{getResources().getColor(R.color.colorGr), Color.LTGRAY}); // Colors for each entry

        PieData data = new PieData(dataSet);
        data.setDrawValues(false); // Whether to draw values (number of steps) on the chart

        pieChart.setData(data);

        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(70f); // Size of the hole in the center of the PieChart
        pieChart.setTransparentCircleRadius(55f); // Size of the transparent circle around the PieChart
        pieChart.setEntryLabelColor(Color.TRANSPARENT); // Set label color to transparent
        pieChart.setEntryLabelTextSize(12f); // Size of labels
        pieChart.getLegend().setEnabled(false);

        pieChart.setDrawEntryLabels(false);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(""+stepsTaken+"\n Steps");
        pieChart.setCenterTextSize(25f);
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.invalidate();
    }

    public void showAddWorkoutDialog(String title, Context context, Workout workoutToEdit) {
        listAdapter = new WorkoutAdapter(context, context, dataArrayList);
        databaseHandler = new DatabaseHandler(context);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_workout, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextWeight = dialogView.findViewById(R.id.editTextWeightWorkout);
        final EditText editTextRepetition = dialogView.findViewById(R.id.editTextRepetition);
        final EditText editTextSerie = dialogView.findViewById(R.id.editTextSerieWorkout);
        final Spinner spinner = dialogView.findViewById(R.id.spinnerWorkout);



        TextView titlePage = dialogView.findViewById(R.id.titleAddWorkout);
        titlePage.setText(title);
        long id=SessionManager.getInstance().getCurrentUser().getId();

        List<Exercice> workouts = databaseHandler.getAllExercicesForUser(id);
        ArrayAdapter<Exercice> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, workouts);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);



        if(workoutToEdit!=null){
            for(int i = 0; i < workouts.size(); i++) {
                if(workouts.get(i).getId() == workoutToEdit.getIdExercice()) {
                    spinner.setSelection(i);
                    break;
                }
            }
            editTextWeight.setText(String.valueOf(workoutToEdit.getWeight()));
            editTextRepetition.setText(String.valueOf(workoutToEdit.getRepetition()));
            editTextSerie.setText(String.valueOf(workoutToEdit.getSerie()));

        }

        Button btnCancel = dialogView.findViewById(R.id.btnCancelWorkout);
        Button btnOK = dialogView.findViewById(R.id.btnOKWorkout);

        final AlertDialog alertDialog = dialogBuilder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int workoutSerie =Integer.parseInt(editTextSerie.getText().toString());
                int workoutRepetition =Integer.parseInt(editTextRepetition.getText().toString());
                double workoutweight =Double.parseDouble(editTextWeight.getText().toString());
                Exercice ex = (Exercice) spinner.getSelectedItem();
                Long selectedExId = ex.getId();

                if(workoutToEdit==null){
                    saveWorkoutDatabase(workoutweight,workoutRepetition,workoutSerie,selectedExId);
                }else{

                    updateWorkoutInDatabase(workoutToEdit.getId(),workoutweight,workoutRepetition,workoutSerie,context,selectedExId);
                }

                alertDialog.dismiss();
            }
        });


        alertDialog.show();
    }

    public void showAddRepatDialog(String title, Context context, Repat repatToEdit) {
        listRepatAdapter = new RepatAdapter(context, context, dataRepatArrayList);
        databaseHandler = new DatabaseHandler(context);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_repat, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextWeight = dialogView.findViewById(R.id.editTextWeightRepat);
        final Spinner spinnerRepa = dialogView.findViewById(R.id.spinnerRepa);

        TextView titlePage = dialogView.findViewById(R.id.titleAddRepat);
        titlePage.setText(title);

        Long id=SessionManager.getInstance().getCurrentUser().getId();

        List<Produit> produits=databaseHandler.getAllProduitsForUser(id);
        ArrayAdapter<Produit> spinnerAdapterRepa=new ArrayAdapter<>(context, android.R.layout.simple_spinner_item,produits);
        spinnerAdapterRepa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepa.setAdapter(spinnerAdapterRepa);

        if (repatToEdit != null) {
            for(int i = 0; i < produits.size(); i++) {
                if (produits.get(i).getId() == repatToEdit.getIdProduit()) {
                    spinnerRepa.setSelection(i);
                    break;
                }
            }
            editTextWeight.setText(String.valueOf(repatToEdit.getWeight()));
        }

        Button btnCancel = dialogView.findViewById(R.id.btnCancelRepat);
        Button btnOK = dialogView.findViewById(R.id.btnOKRepat);

        final AlertDialog alertDialog = dialogBuilder.create();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double repatWeight = Double.parseDouble(editTextWeight.getText().toString());
                Produit selectedProduit = (Produit) spinnerRepa.getSelectedItem();
                Long selectedProduitId = selectedProduit.getId();

                if (repatToEdit == null) {
                    saveRepatDatabase(repatWeight,selectedProduitId);
                } else {
                    updateWorkoutInDatabase(repatToEdit.getId(), repatWeight, context,selectedProduitId);
                }

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private void saveWorkoutDatabase(double wei, int rep,int serie,Long idExercice) {
        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Workout workout=new Workout(null,idExercice,wei,serie,rep,new Date(),id);
        Long saved=databaseHandler.addWorkout(workout);
        if (saved != -1) {
            dataArrayList.clear();
            dataArrayList.addAll(databaseHandler.getAllWorkoutsForUser(id));
            listAdapter.notifyDataSetChanged();
        }
    }

    private void saveRepatDatabase(double wei,Long ProduitId) {
        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -0);
        Date yesterday = calendar.getTime();
        Repat repat=new Repat(null,ProduitId,wei,yesterday,id);
        Long saved=databaseHandler.addRepat(repat);

        if (saved != -1) {
            Toast.makeText(WorkoutActivity.this, "id saved :"+saved, Toast.LENGTH_SHORT).show();
            dataRepatArrayList.clear();
            dataRepatArrayList.addAll(databaseHandler.getAllRepatsForUser(id));
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateWorkoutInDatabase(Long RepatId, double weight,Context context,Long ProduitId) {
        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Repat repat=new Repat(RepatId,ProduitId,weight,new Date(),id);
        databaseHandler.updateRepat(repat);

    }

    private void updateWorkoutInDatabase(Long workoutId, double weight, int repetition, int serie,Context context,Long idExercice) {
        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Workout workout=new Workout(workoutId,idExercice,weight,serie,repetition,new Date(),id);
        databaseHandler.updateWorkout(workout);
    }



    @Override
    protected void onPause() {
        super.onPause();
        runing = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        runing = true;
        Sensor countSensor = sensorManager.getDefaultSensor(sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "SENSOR NOT FOUND", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (runing) {

            //count.setText(String.valueOf(event.values[0]));
            Long idUser = SessionManager.getInstance().getCurrentUser().getId();
            float value = event.values[0]; // Assuming event.values[0] contains the float value
            int intValue = (int) value;

            Date today = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = dateFormat.format(today); // Format today's date to yyyy-MM-dd
            Date date;
            try {
                date = dateFormat.parse(dateString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Steps existingSteps = databaseHandler.getStepsByDate(date);
            int a =databaseHandler.getCountStepByDate(date,idUser);
            int b=intValue-a;
            System.out.println(b);
            if(b >25 && b<70){
                showNotification(this.getApplicationContext(), "Goal reached", "Congratulations, you have reached the goal");

            }
            if (existingSteps == null) {
                long i = databaseHandler.addSteps(new Steps(null, date, b, idUser));
                System.out.println(date);
            } else {
                existingSteps.setStep(b);
                databaseHandler.updateSteps(existingSteps);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    public static void showNotification(Context context, String title, String message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "CHANNEL_NAME", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("CHANNEL_DESCRIPTION");
                notificationManager.createNotificationChannel(channel);
            }

            Intent intent = new Intent(context, SteepsCounter.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(context, "CHANNEL_ID");
            } else {
                builder = new Notification.Builder(context);
            }

            builder.setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.mipmap.ic_launcher);

            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }
    }
}
