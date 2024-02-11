package com.fitness_tracking.Dao;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fitness_tracking.R;
import com.fitness_tracking.auth.SessionManager;
import com.fitness_tracking.entities.Exercice;
import com.fitness_tracking.entities.Repat;
import com.fitness_tracking.entities.Workout;
import com.fitness_tracking.pages.WorkoutActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutAdapter extends ArrayAdapter<Workout> {

    private final Context activityContext;

    private final WorkoutActivity workoutActivity = new WorkoutActivity();

    public WorkoutAdapter(@NonNull Context context, Context activityContext, List<Workout> dataArrayList) {
        super(context, R.layout.card_workout_item, dataArrayList);
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Workout workoutData = getItem(position);
        DatabaseHandler db=new DatabaseHandler(activityContext);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.card_workout_item, parent, false);
        }

        TextView workoutName = view.findViewById(R.id.workoutName);
        TextView workoutWeight = view.findViewById(R.id.workoutWeight);
        TextView workoutRepetition = view.findViewById(R.id.workoutRepetition);
        TextView workoutDate = view.findViewById(R.id.workoutDate);

        if (workoutData != null && workoutData.getIdExercice()!=0) {
            Exercice ex=db.getExerciseById(workoutData.getIdExercice());

            workoutName.setText(ex.getName());
            workoutWeight.setText("Weight: " + String.valueOf(workoutData.getWeight()));
            workoutRepetition.setText("Repetition: " + String.valueOf(workoutData.getRepetition()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(workoutData.getDate());
            workoutDate.setText("Date: " + formattedDate);
        }

        ImageButton btnDeleteWorkout = view.findViewById(R.id.btnDeleteWorkout);
        btnDeleteWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Workout workoutToDelete = getItem(position);

                if (workoutToDelete != null) {
                    deleteWorkout(workoutToDelete.getId());
                }
            }
        });

        ImageButton btnEditWorkout = view.findViewById(R.id.btnEditWorkout);
        btnEditWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Workout workoutToEdit = getItem(position);

                if (workoutToEdit != null) {
                    workoutActivity.showAddWorkoutDialog("Edit Product",activityContext,workoutToEdit);
                }
            }
        });

        return view;
    }

    private void deleteWorkout(long workoutId) {
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        databaseHandler.deleteWorkout(workoutId);

        Long id = SessionManager.getInstance().getCurrentUser().getId();

        this.clear();
        this.addAll(databaseHandler.getAllWorkoutsForUser(id));
        this.notifyDataSetChanged();

    }
}
