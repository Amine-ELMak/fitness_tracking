package com.fitness_tracking.Dao;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fitness_tracking.R;
import com.fitness_tracking.entities.Workout;
import com.fitness_tracking.pages.ExerciceActivity;
import com.fitness_tracking.auth.SessionManager;
import com.fitness_tracking.entities.Exercice;

import java.util.List;
import com.bumptech.glide.Glide;

public class ExerciceAdapter extends ArrayAdapter<Exercice> {

    private final Context activityContext;
    private final ExerciceActivity exerciceActivity = new ExerciceActivity();

    public ExerciceAdapter(@NonNull Context context, Context activityContext, List<Exercice> dataArrayList) {
        super(context, R.layout.card_item_exercice, dataArrayList);
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Exercice listData = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.card_item_exercice, parent, false);
        }

        TextView exerciceName = view.findViewById(R.id.exerciceName);
        TextView exerciceCategory = view.findViewById(R.id.exerciceCategory);
        TextView exerciceDescription = view.findViewById(R.id.exerciceDescription);
        ImageView image=view.findViewById(R.id.exerciceImage);

        if (listData != null) {
            exerciceName.setText(listData.getName());
            exerciceDescription.setText(listData.getDescription());
            exerciceCategory.setText(listData.getCategory());
            Bitmap ee= BitmapFactory.decodeFile(listData.getPath());
            image.setImageBitmap(ee);
        }

        ImageButton btnDeleteExercice = view.findViewById(R.id.btnDeleteExercice);
        btnDeleteExercice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exercice exerciceToDelete = getItem(position);

                if (exerciceToDelete != null) {
                    deleteExercice(exerciceToDelete.getId());
                }
            }
        });

        ImageButton btnEditExercice = view.findViewById(R.id.btnEditExercice);
        btnEditExercice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Exercice exerciceToEdit = getItem(position);

                if (exerciceToEdit != null) {
                    exerciceActivity.showAddExerciceDialog("Edit Exercise", activityContext, exerciceToEdit);
                }
            }
        });

        return view;
    }

    private void deleteExercice(long exerciceId) {
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        Long id = SessionManager.getInstance().getCurrentUser().getId();
        String cate=databaseHandler.getExerciseById(exerciceId).getCategory();

        List<Workout> worByIdExe=databaseHandler.getWorkoutByIdExercice(exerciceId,id);

        for(Workout w:worByIdExe){
            databaseHandler.deleteWorkout(w.getId());
        }

        databaseHandler.deleteExercice(exerciceId);

        this.clear();
        this.addAll(databaseHandler.getExerciceByUserAndCategory(id,cate));
        this.notifyDataSetChanged();
    }

}
