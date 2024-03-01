package com.fitness_tracking.pages;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fitness_tracking.Dao.DatabaseHandler;
import com.fitness_tracking.Dao.SteepsCounter;
import com.fitness_tracking.R;
import com.fitness_tracking.auth.SessionManager;
import com.fitness_tracking.entities.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ProfileActivity extends AppCompatActivity {
    Long id = -1L;
    String email = "";
    String name = "";
    String password = "";
    double weight = -1;
    double height = -1;
    String sex = "";
    DatabaseHandler databaseHandler;
    TextView textViewName ;
    TextView textViewName2 ;
    TextView textViewEmail ;
    TextView textViewWeight ;
    TextView textViewHeight ;
    TextView textViewSex ;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // Initializing database handler
        databaseHandler = new DatabaseHandler(this);

        // Retrieving user information from SessionManager
        User currentUser = SessionManager.getInstance().getCurrentUser();
        id = currentUser.getId();
        email = currentUser.getEmail();
        name = currentUser.getName();
        password = currentUser.getPassword();
        weight = currentUser.getWeight();
        height = currentUser.getHeight();
        sex = currentUser.getSex();

        // Initializing TextViews
        textViewName = findViewById(R.id.fullname_field);
        textViewName2 = findViewById(R.id.full_name_profile2);
        textViewEmail = findViewById(R.id.Email_profile);
        textViewWeight = findViewById(R.id.weight_label);
        textViewHeight = findViewById(R.id.height_label);
        textViewSex = findViewById(R.id.sexText);
        //RadioGroup radioGroup = findViewById(R.id.radioGroupSex);

        // Setting user information to TextViews
        textViewName.setText(name);
        textViewName2.setText(name);
        textViewEmail.setText(email);
        textViewWeight.setText(String.valueOf(weight));
        textViewHeight.setText(String.valueOf(height));
        textViewSex.setText(sex);

        Button btnStep = findViewById(R.id.stepChart);
        btnStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(ProfileActivity.this, ChartActivity.class);
                startActivity(intent4);
            }
        });
        Button teest = findViewById(R.id.stttt);
        teest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(ProfileActivity.this, SteepsCounter.class);
                startActivity(intent4);
            }
        });
        // Initializing Edit Profile Button and setting click listener
        Button btnEditProfile = findViewById(R.id.updatebutton);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.person);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.person) {
                    Toast.makeText(ProfileActivity.this, "person", Toast.LENGTH_SHORT).show();
                    return true;

                } else if (id == R.id.home) {
                    Intent intent4 = new Intent(ProfileActivity.this, WorkoutActivity.class);
                    startActivity(intent4);
                    return true;

                } else if (id == R.id.fitness) {
                    Intent intent4 = new Intent(ProfileActivity.this, ProductActivity.class);
                    startActivity(intent4);
                    return true;

                } else if (id == R.id.workout) {
                    Intent intent4 = new Intent(ProfileActivity.this, CategoryActivity.class);
                    startActivity(intent4);
                    return true;
                }
                return false;
            }
        });
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null);
        builder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.editTextName);
        EditText editTextEmail = dialogView.findViewById(R.id.editTextEmail);
        EditText editTextWeight = dialogView.findViewById(R.id.editTextWeight);
        EditText editTextHeight = dialogView.findViewById(R.id.editTextHeight);
        EditText oldPasswordEditText = dialogView.findViewById(R.id.editTextOldPassword);
        EditText newPasswordEditText = dialogView.findViewById(R.id.editTextNewPassword);

        editTextName.setText(name);
        editTextEmail.setText(email);
        editTextWeight.setText(String.valueOf(weight));
        editTextHeight.setText(String.valueOf(height));

        editTextEmail.setEnabled(false);
        editTextEmail.setFocusable(false);
        editTextEmail.setClickable(false);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retrieve updated profile information from EditText fields
                String newName = editTextName.getText().toString();
                String newEmail = editTextEmail.getText().toString();
                String newWeight = editTextWeight.getText().toString();
                String newHeight = editTextHeight.getText().toString();
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();

                if (newName.isEmpty() || newEmail.isEmpty() || newWeight.isEmpty() || newHeight.isEmpty() || oldPassword.isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (newPassword.isEmpty()){
                    newPassword=oldPassword;
                }
                try {
                    Double.parseDouble(newWeight);
                    Double.parseDouble(newHeight);
                } catch (NumberFormatException e) {
                    Toast.makeText(ProfileActivity.this, "Please enter valid values for weight and height.", Toast.LENGTH_SHORT).show();
                    return;
                }

               if (!newEmail.equals(email) && databaseHandler.checkEmail(newEmail)) {
                    Toast.makeText(ProfileActivity.this, "Email already exists.", Toast.LENGTH_SHORT).show();
                    return;
                }

                User updatedUser = new User(id, newEmail, newName, newPassword, Double.parseDouble(newWeight), Double.parseDouble(newHeight), sex);

                databaseHandler.updateUser(updatedUser);

                SessionManager.getInstance().loginUser(updatedUser);

                textViewName.setText(newName);
                textViewEmail.setText(newEmail);
                textViewWeight.setText(newWeight);
                textViewHeight.setText(newHeight);

                Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
