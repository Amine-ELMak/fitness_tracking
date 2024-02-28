package com.fitness_tracking.Dao;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.fitness_tracking.R;
import com.fitness_tracking.auth.SessionManager;
import com.fitness_tracking.entities.Steps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SteepsCounter extends AppCompatActivity implements SensorEventListener {
    DatabaseHandler db = new DatabaseHandler(this);
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean runing = false;
    private int countSteeps;
    TextView count;

    private static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.steeps_counter);
        count = (TextView) findViewById(R.id.steepsConter);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
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

            count.setText(String.valueOf(event.values[0]));
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

            Steps existingSteps = db.getStepsByDate(date);
            int a =db.getCountStepByDate(date,idUser);
            int b=intValue-a;
            System.out.println(b);
            if(b >50 && b<70){
            showNotification(this.getApplicationContext(), "Goal reached", "Bravo vous etes attiendre lobjectif ");

}
            if (existingSteps == null) {
                long i = db.addSteps(new Steps(null, date, b, idUser));
                System.out.println(date);
            } else {
                existingSteps.setStep(b);
                db.updateSteps(existingSteps);
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
                    .setSmallIcon(android.R.drawable.ic_dialog_info);

            Notification notification = builder.build();
            notificationManager.notify(1, notification);
        }
    }
}
