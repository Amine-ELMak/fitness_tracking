package com.fitness_tracking.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fitness_tracking.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;


public class CategoryActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercice_category);

        // Assuming these are your CardViews
        CardView chestCard = findViewById(R.id.chestcard);
        CardView armCard = findViewById(R.id.armcard);
        CardView abCard = findViewById(R.id.abcard);
        CardView legCard = findViewById(R.id.legcard);
        CardView cardioCard = findViewById(R.id.cardiocard);

        // Set tags for each CardView representing the category
        chestCard.setTag("Chest");
        armCard.setTag("Arms");
        legCard.setTag("Legs");
        abCard.setTag("Abbs");
        cardioCard.setTag("Cardio");

        // Set click listeners to each CardView
        chestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCardClick(v);
            }
        });

        armCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCardClick(v);
            }
        });

        abCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCardClick(v);
            }
        });

        legCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCardClick(v);
            }
        });

        cardioCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCardClick(v);
            }
        });

        bottomNavigationView= findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.workout);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id==R.id.person) {
                    Intent intent4 = new Intent(CategoryActivity.this, ProfileActivity.class);
                    startActivity(intent4);
                    return true;

                }else
                if(id==R.id.home) {
                    Intent intent4 = new Intent(CategoryActivity.this, WorkoutActivity.class);
                    startActivity(intent4);
                    return true;

                }else if(id==R.id.fitness) {
                    Intent intent4 = new Intent(CategoryActivity.this, ProductActivity.class);
                    startActivity(intent4);
                    return true;

                }else if(id== R.id.workout){
                    Toast.makeText(CategoryActivity.this, "workout.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });
    }

    private void handleCardClick(View v) {
        // Retrieve the category tag
        String category = (String) v.getTag();
        // Print the category
        System.out.println("Clicked category: " + category);
        // Navigate to ExerciceActivity
        Intent intent = new Intent(CategoryActivity.this, ExerciceActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}