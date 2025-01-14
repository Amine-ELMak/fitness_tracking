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
import com.fitness_tracking.entities.Produit;
import com.fitness_tracking.entities.Repat;
import com.fitness_tracking.pages.WorkoutActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RepatAdapter extends ArrayAdapter<Repat> {

    private final Context activityContext;

    private final WorkoutActivity workoutActivity = new WorkoutActivity();

    public RepatAdapter(@NonNull Context context, Context activityContext, List<Repat> dataArrayList) {
        super(context, R.layout.card_repat_item, dataArrayList);
        this.activityContext = activityContext;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Repat repatData = getItem(position);
        DatabaseHandler db=new DatabaseHandler(activityContext);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.card_repat_item, parent, false);
        }

        // declare vars for card
        TextView repatName = view.findViewById(R.id.repName);
        TextView repatWeight = view.findViewById(R.id.repWeight);
        TextView repatCalorie = view.findViewById(R.id.repCal);

        if (repatData != null && repatData.getIdProduit()!=0) {
            // our product
            Produit p=db.getProduitById(repatData.getIdProduit());

            repatName.setText(p.getName());
            repatWeight.setText("Weight: " + repatData.getWeight() + " g");

            // calculate calories
            double calories = p.getCalorie() * ( repatData.getWeight() / 100);
            repatCalorie.setText("Calories: " +calories + " cal");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(repatData.getDate());

        }

        ImageButton btnDeleteRepat = view.findViewById(R.id.btnDeleteRep);
        btnDeleteRepat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Repat repatToDelete = getItem(position);

                if (repatToDelete != null) {
                    deleteRepat(repatToDelete.getId());
                }
            }
        });

        ImageButton btnEditRepat = view.findViewById(R.id.btnEditRep);
        btnEditRepat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Repat repatToEdit = getItem(position);

                if (repatToEdit != null) {
                    workoutActivity.showAddRepatDialog("Edit Product",activityContext,repatToEdit);
                }
            }
        });

        return view;
    }



    private void deleteRepat(long repatId) {
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        databaseHandler.deleteRepat(repatId);

        Long id = SessionManager.getInstance().getCurrentUser().getId();

        this.clear();
        this.addAll(databaseHandler.getAllRepatsForUser(id));
        this.notifyDataSetChanged();
    }
}
