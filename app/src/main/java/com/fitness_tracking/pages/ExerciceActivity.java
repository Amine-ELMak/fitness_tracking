package com.fitness_tracking.pages;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fitness_tracking.Dao.DatabaseHandler;
import com.fitness_tracking.Dao.ExerciceAdapter;
import com.fitness_tracking.R;
import com.fitness_tracking.auth.LoginActivity;
import com.fitness_tracking.auth.Register;
import com.fitness_tracking.auth.SessionManager;
import com.fitness_tracking.entities.Exercice;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExerciceActivity extends AppCompatActivity {
    ExerciceAdapter listAdapter;
    List<Exercice> dataArrayList = new ArrayList<>();
    DatabaseHandler databaseHandler = new DatabaseHandler(this);


    Button btnAddImage;
    ImageView imagePreview;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercice);

        ImageView iconGoBack = findViewById(R.id.icon_go_back);
        ImageView iconAdd = findViewById(R.id.icon_add);

        ListView listView = findViewById(R.id.listviewExercice);

        Long id = SessionManager.getInstance().getCurrentUser().getId();

        // Retrieve category information from intent
        String category = getIntent().getStringExtra("category");

        // Use the category information to query the database and fetch exercises
        dataArrayList = databaseHandler.getExerciceByUserAndCategory(id, category);

        //dataArrayList = databaseHandler.getAllExercicesForUser(id);

        listAdapter = new ExerciceAdapter(getApplicationContext(), this, dataArrayList);

        listView.setAdapter(listAdapter);

        iconGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute method when the user clicks the "Go Back" icon
                // Example: onBackPressed();
                System.out.println("go back is pressed ******************");
                finish();
            }
        });

        iconAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Execute method when the user clicks the "Add" icon
                // Example: showAddDialog();
                System.out.println("add is pressed ******************");
                showAddExerciceDialog("Add Exercise", ExerciceActivity.this, null);

            }
        });

    }

    public void showAddExerciceDialog(String title, Context context, Exercice exerciceToEdit) {
        listAdapter = new ExerciceAdapter(context, context, dataArrayList);
        databaseHandler = new DatabaseHandler(context);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_add_exercice, null);
        dialogBuilder.setView(dialogView);

        final Spinner spinner = dialogView.findViewById(R.id.spinnerCatgoryExercice);
        List<String> categorys = new ArrayList<>();
        categorys.add("Chest");
        categorys.add("Abbs");
        categorys.add("Arms");
        categorys.add("Legs");
        categorys.add("Cardio");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, categorys);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        final EditText editTextExerciceName = dialogView.findViewById(R.id.editTextExerciceName);
        final EditText editTextExerciceDescription = dialogView.findViewById(R.id.editTextExerciceDescription);
        TextView titlePage = dialogView.findViewById(R.id.titleAddExercice);
        titlePage.setText(title);

        btnAddImage = dialogView.findViewById(R.id.btnAddImage);
        imagePreview = dialogView.findViewById(R.id.imagePreview);

        if (exerciceToEdit != null) {
            editTextExerciceName.setText(exerciceToEdit.getName());
            editTextExerciceDescription.setText(exerciceToEdit.getDescription());
            btnAddImage.setVisibility(View.GONE);
            imagePreview.setVisibility(View.GONE);
        }

        Button btnCancel = dialogView.findViewById(R.id.btnCancelExercice);
        Button btnOK = dialogView.findViewById(R.id.btnOKExercice);

        final AlertDialog alertDialog = dialogBuilder.create();

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciceName = editTextExerciceName.getText().toString();
                String category = (String)spinner.getSelectedItem();
                System.out.println(category);
                String exerciceDescription = editTextExerciceDescription.getText().toString();
                if (exerciceToEdit == null && selectedImageUri!=null) {
                    Long idddd=saveExerciceToDatabase(exerciceName, copyImageToInternalStorage(selectedImageUri), exerciceDescription, category);
                    Toast.makeText(ExerciceActivity.this,""+idddd,Toast.LENGTH_SHORT);
                } else{

                    updateExerciceInDatabase(exerciceToEdit.getId(), exerciceName, exerciceToEdit.getPath(), exerciceDescription, category);
                }

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private Long saveExerciceToDatabase(String exerciceName, String exercicePath, String exerciceDescription, String category) {
        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Exercice exercice = new Exercice(null, exerciceName, exercicePath, exerciceDescription, id, category);
        System.out.println(exercice.getCategory());
        Long saved = databaseHandler.addExercice(exercice);
        if (saved != -1) {
            finish();
            startActivity(getIntent());
        }
        return saved;
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {

                Uri selectedImageUri = data.getData();
                this.selectedImageUri = selectedImageUri;
                imagePreview.setImageURI(selectedImageUri);

            }
        }
    }


    private String copyImageToInternalStorage(Uri uri) {
        String destinationPath = null;
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";
            File directory = getApplicationContext().getDir("images", Context.MODE_PRIVATE);
            File file = new File(directory, fileName);
            destinationPath = file.getAbsolutePath();
            OutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destinationPath;
    }

    private void updateExerciceInDatabase(Long exerciceId, String exerciceName, String exercicePath, String exerciceDescription, String category) {
        Long id = SessionManager.getInstance().getCurrentUser().getId();
        Exercice exercice = new Exercice(exerciceId, exerciceName, exercicePath, exerciceDescription, id, category);
        databaseHandler.updateExercice(exercice);
        startActivity(getIntent());
    }

}
