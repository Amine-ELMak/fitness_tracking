package com.fitness_tracking.Dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.fitness_tracking.auth.Register;
import com.fitness_tracking.entities.Exercice;
import com.fitness_tracking.entities.Produit;
import com.fitness_tracking.entities.Repat;
import com.fitness_tracking.entities.User;
import com.fitness_tracking.entities.Workout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String database="fitness";

    private static final String CREATE_TABLE_USER =
            "CREATE TABLE IF NOT EXISTS USER (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "password TEXT, " +
                    "weight DOUBLE, " +
                    "height DOUBLE, " +
                    "sex TEXT" +
                    ");";

    private static final String CREATE_TABLE_EXERCISE =
            "CREATE TABLE IF NOT EXISTS EXERCICE (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "path TEXT, " +
                    "description TEXT, " +
                    "id_user INTEGER, " +
                    "FOREIGN KEY (id_user) REFERENCES USER(id)" +
                    ");";

    private static final String CREATE_TABLE_PRODUIT =
            "CREATE TABLE IF NOT EXISTS PRODUIT (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "calorie DOUBLE, " +
                    "proteine DOUBLE, " +
                    "carbe DOUBLE, " +
                    "fate DOUBLE, " +
                    "id_user INTEGER, " +
                    "FOREIGN KEY (id_user) REFERENCES USER(id)" +
                    ");";

    private static final String CREATE_TABLE_REPAT =
            "CREATE TABLE IF NOT EXISTS REPAT (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_produit INTEGER, " +
                    "weight DOUBLE,"+
                    "date Date, " +
                    "id_user INTEGER, " +
                    "FOREIGN KEY (id_produit) REFERENCES PRODUIT(id), " +
                    "FOREIGN KEY (id_user) REFERENCES USER(id)" +
                    ");";

    private static final String CREATE_TABLE_WORKOUT =
            "CREATE TABLE IF NOT EXISTS WORKOUT (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "id_exercice INTEGER, " +
                    "weight DOUBLE, " +
                    "serie INTEGER, " +
                    "repetition INTEGER, " +
                    "date TEXT, " +
                    "id_user INTEGER, " +
                    "FOREIGN KEY (id_exercice) REFERENCES EXERCICE(id), " +
                    "FOREIGN KEY (id_user) REFERENCES USER(id)" +
                    ");";


    public DatabaseHandler(@Nullable Context context) {
        super(context, database, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_EXERCISE);
        db.execSQL(CREATE_TABLE_PRODUIT);
        db.execSQL(CREATE_TABLE_WORKOUT);
        db.execSQL(CREATE_TABLE_REPAT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS User");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Exercice");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Workout");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Produit");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Repat");
        onCreate(sqLiteDatabase);
    }

    public Boolean checkEmail(String email) {
        SQLiteDatabase MyDatabase = this.getReadableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM user WHERE email = ?", new String[]{email});
        return cursor.getCount() == 0;
    }

    public Boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        return cursor.getCount() > 0;
    }

    private ContentValues getUserContentValues(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", user.getEmail());
        contentValues.put("name", user.getName());
        contentValues.put("password", user.getPassword());
        contentValues.put("weight", user.getWeight());
        contentValues.put("height", user.getHeight());
        contentValues.put("sex", user.getSex());
        return contentValues;
    }

    public Optional<User> getUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = null;

        try {
            cursor = sqLiteDatabase.rawQuery("select * from user where email = ? and password = ?", new String[]{email, password});

            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int emailIndex = cursor.getColumnIndex("email");
                int passwordIndex = cursor.getColumnIndex("password");
                int weightIndex = cursor.getColumnIndex("weight");
                int heightIndex = cursor.getColumnIndex("height");
                int sexIndex = cursor.getColumnIndex("sex");

                if (nameIndex != -1 && weightIndex != -1 && passwordIndex != -1 && emailIndex != -1 && heightIndex != -1 && sexIndex!= -1) {
                    Long userId = cursor.getLong(idIndex);
                    User user = new User(
                            userId,
                            cursor.getString(emailIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(passwordIndex),
                            cursor.getDouble(weightIndex),
                            cursor.getDouble(heightIndex),
                            cursor.getString(sexIndex)
                    );
                    return Optional.of(user);
                } else {
                    Log.e("DatabaseHandler", "One or more columns not found in the result set");
                    return Optional.empty();
                }
            } else {
                Log.d("DatabaseHandler", "No user found with the given email and password");
                return Optional.empty();
            }
        } catch (Exception e) {
            Log.e("DatabaseHandler", "Error while retrieving user", e);
            return Optional.empty();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            sqLiteDatabase.close();
        }
    }

    public boolean checkUserCredentials(String email, String password) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM user WHERE email = ? AND password = ?", new String[]{email, password});

        try {
            if (cursor.getCount()>0) {
                return true;
            } else {
                return false;
            }
        } finally {
            cursor.close();
        }
    }

    public boolean saveUser(User user) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = getUserContentValues(user);

        try {
            long result = sqLiteDatabase.insert("user", null, contentValues);

            if (result != -1) {
                Log.d("DatabaseHandler", "User saved successfully");
                return true;
            } else {
                Log.e("DatabaseHandler", "Error while saving user: " + result);
                return false;
            }
        } catch (Exception e) {
            Log.e("DatabaseHandler", "Error while saving user", e);
            return false;
        } finally {
            sqLiteDatabase.close();
        }
    }

    public void updateUser(User user) {
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = this.getWritableDatabase();
            ContentValues contentValues = getUserContentValues(user);
            int rowsAffected = sqLiteDatabase.update("user", contentValues, "id = ?", new String[]{String.valueOf(user.getId())});
            if (rowsAffected > 0) {
                Log.d("DatabaseHandler", "User updated successfully");
            } else {
                Log.d("DatabaseHandler", "No rows affected, user might not exist");
            }
        } catch (SQLiteException e) {
            Log.e("DatabaseHandler", "SQLiteException while updating user", e);
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
    }


    public void updateExercice(Exercice exercice) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", exercice.getName());
        contentValues.put("path", exercice.getPath());
        contentValues.put("description", exercice.getDescription());
        contentValues.put("id_user", exercice.getIdUser());

        sqLiteDatabase.update("EXERCICE", contentValues, "id = ?", new String[]{String.valueOf(exercice.getId())});

        sqLiteDatabase.close();
    }

    public long addExercice(Exercice exercice) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", exercice.getName());

        contentValues.put("path", exercice.getPath());
        contentValues.put("description", exercice.getDescription());
        contentValues.put("id_user", exercice.getIdUser());

        long id = sqLiteDatabase.insert("EXERCICE", null, contentValues);

        sqLiteDatabase.close();
        return id;
    }

    public void deleteExercice(long id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete("EXERCICE", "id = ?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }
    @SuppressLint("Range")
    public Exercice getExerciseById(long id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Exercice exercise = null;

        Cursor cursor = sqLiteDatabase.query(
                "EXERCICE", // Table name
                new String[] {"id", "name", "path", "description", "id_user"}, // Columns to retrieve
                "id=?", // Selection criteria (WHERE clause)
                new String[] {String.valueOf(id)}, // Selection arguments
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Construct the Exercise object from the cursor data
            exercise = new Exercice(
                    cursor.getLong(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getLong(cursor.getColumnIndex("id_user"))
            );
            cursor.close();
        }

        return exercise;
    }


    @SuppressLint("Range")
    public List<Exercice> getAllExercicesForUser(long userId) {
        List<Exercice> exercicesList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;
        try{
            sqLiteDatabase = this.getReadableDatabase();

            cursor = sqLiteDatabase.query("EXERCICE", null, "id_user = ?", new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Exercice exercice = new Exercice(
                            cursor.getLong(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getString(cursor.getColumnIndex("path")),
                            cursor.getString(cursor.getColumnIndex("description")),
                            cursor.getLong(cursor.getColumnIndex("id_user"))
                    );
                    exercicesList.add(exercice);
                } while (cursor.moveToNext());

                cursor.close();

            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }

        return exercicesList;
    }

    @SuppressLint("Range")
    public List<Exercice> getAllExercices() {
        List<Exercice> exercicesList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("EXERCICE", null, null, null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Exercice exercice = new Exercice(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("path")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getLong(cursor.getColumnIndex("id_user"))
                );
                exercicesList.add(exercice);
            } while (cursor.moveToNext());

            cursor.close();
        }

        sqLiteDatabase.close();
        return exercicesList;
    }

    public long addProduit(Produit produit) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", produit.getName());
        contentValues.put("calorie", produit.getCalorie());
        contentValues.put("proteine", produit.getProteine());
        contentValues.put("carbe", produit.getCarbe());
        contentValues.put("fate", produit.getFate());
        contentValues.put("id_user", produit.getIdUser());

        long id = sqLiteDatabase.insert("PRODUIT", null, contentValues);

        sqLiteDatabase.close();
        return id;
    }

    public void updateProduit(Produit produit) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("name", produit.getName());
        contentValues.put("calorie", produit.getCalorie());
        contentValues.put("proteine", produit.getProteine());
        contentValues.put("carbe", produit.getCarbe());
        contentValues.put("fate", produit.getFate());
        contentValues.put("id_user", produit.getIdUser());

        sqLiteDatabase.update("PRODUIT", contentValues, "id = ?", new String[]{String.valueOf(produit.getId())});

        sqLiteDatabase.close();
    }

    public void deleteProduit(long id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete("PRODUIT", "id = ?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }

    @SuppressLint("Range")
    public Produit getProduitById(long id) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Produit produit = null;

        Cursor cursor = sqLiteDatabase.query(
                "PRODUIT",
                new String[] {"id", "name", "calorie", "proteine", "carbe", "fate", "id_user"},
                "id=?", // Selection criteria (WHERE clause)
                new String[] {String.valueOf(id)},
                null, // GROUP BY
                null, // HAVING
                null // ORDER BY
        );

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                // Ensure that the cursor has at least one row
                // Construct the Produit object from the cursor data
                produit = new Produit(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getDouble(cursor.getColumnIndex("calorie")),
                        cursor.getDouble(cursor.getColumnIndex("proteine")),
                        cursor.getDouble(cursor.getColumnIndex("carbe")),
                        cursor.getDouble(cursor.getColumnIndex("fate")),
                        cursor.getLong(cursor.getColumnIndex("id_user"))
                );
            }
            cursor.close();
        }

        return produit;
    }


    @SuppressLint("Range")
    public List<Produit> getAllProduitsForUser2(long id) {
        List<Produit> dataArrayList=new ArrayList<>();
        dataArrayList.add(new Produit(1L," String name",350, 5, 10, 3,id));
        dataArrayList.add(new Produit(2L," String name",350, 5, 10, 3,id));
        dataArrayList.add(new Produit(3L," String name",350, 5, 10, 3,id));
        return dataArrayList;
    }

    @SuppressLint("Range")
    public List<Produit> getAllProduitsForUser(long userId) {
        ArrayList<Produit> produitsList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = null;
        Cursor cursor = null;

        try {
            sqLiteDatabase = this.getReadableDatabase();

            cursor = sqLiteDatabase.query("PRODUIT", null, "id_user = ?", new String[]{String.valueOf(userId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Produit produit = new Produit(
                            cursor.getLong(cursor.getColumnIndex("id")),
                            cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getDouble(cursor.getColumnIndex("calorie")),
                            cursor.getDouble(cursor.getColumnIndex("proteine")),
                            cursor.getDouble(cursor.getColumnIndex("carbe")),
                            cursor.getDouble(cursor.getColumnIndex("fate")),
                            cursor.getLong(cursor.getColumnIndex("id_user"))
                    );
                    produitsList.add(produit);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close cursor and database connection
            if (cursor != null) {
                cursor.close();
            }
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }

        return produitsList;
    }


    public long addRepat(Repat repat) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id_produit", repat.getIdProduit());
        contentValues.put("date", repat.getDate().getTime());
        contentValues.put("weight",repat.getWeight());
        contentValues.put("id_user", repat.getIdUser());

        long id = sqLiteDatabase.insert("REPAT", null, contentValues);

        sqLiteDatabase.close();
        return id;
    }

    public void updateRepat(Repat repat) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_produit", repat.getIdProduit());
        contentValues.put("date", repat.getDate().getTime());
        contentValues.put("weight",repat.getWeight());
        contentValues.put("id_user", repat.getIdUser());

        sqLiteDatabase.update("REPAT", contentValues, "id = ?", new String[]{String.valueOf(repat.getId())});

        sqLiteDatabase.close();
    }

    public void deleteRepat(long id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete("REPAT", "id = ?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }

    @SuppressLint("Range")
    public List<Repat> getAllRepatsForUser(long userId) {
        List<Repat> repatsList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();


        Cursor cursor = sqLiteDatabase.query("REPAT", null, "id_user = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Repat repat = new Repat(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getLong(cursor.getColumnIndex("id_produit")),
                        cursor.getDouble(cursor.getColumnIndex("weight")),
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getLong(cursor.getColumnIndex("id_user"))
                );
                repatsList.add(repat);
            } while (cursor.moveToNext());

            cursor.close();
        }

        sqLiteDatabase.close();
        return repatsList;
    }

    public long addWorkout(Workout workout) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_exercice", workout.getIdExercice());
        contentValues.put("weight", workout.getWeight());
        contentValues.put("serie", workout.getSerie());
        contentValues.put("repetition", workout.getRepetition());
        contentValues.put("date", workout.getDate().getTime());
        contentValues.put("id_user", workout.getIdUser());

        long id = sqLiteDatabase.insert("WORKOUT", null, contentValues);

        sqLiteDatabase.close();
        return id;
    }

    public void updateWorkout(Workout workout) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_exercice", workout.getIdExercice());
        contentValues.put("weight", workout.getWeight());
        contentValues.put("serie", workout.getSerie());
        contentValues.put("repetition", workout.getRepetition());
        contentValues.put("date", workout.getDate().getTime()); // Stocker la date sous forme de timestamp
        contentValues.put("id_user", workout.getIdUser());

        sqLiteDatabase.update("WORKOUT", contentValues, "id = ?", new String[]{String.valueOf(workout.getId())});

        sqLiteDatabase.close();
    }

    public void deleteWorkout(long id) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        sqLiteDatabase.delete("WORKOUT", "id = ?", new String[]{String.valueOf(id)});

        sqLiteDatabase.close();
    }

    @SuppressLint("Range")
    public List<Workout> getAllWorkoutsForUser(long userId) {
        List<Workout> workoutsList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query("WORKOUT", null, "id_user = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Workout workout = new Workout(
                        cursor.getLong(cursor.getColumnIndex("id")),
                        cursor.getLong(cursor.getColumnIndex("id_exercice")),
                        cursor.getDouble(cursor.getColumnIndex("weight")),
                        cursor.getInt(cursor.getColumnIndex("serie")),
                        cursor.getInt(cursor.getColumnIndex("repetition")),
                        new Date(cursor.getLong(cursor.getColumnIndex("date"))),
                        cursor.getLong(cursor.getColumnIndex("id_user"))
                );
                workoutsList.add(workout);
            } while (cursor.moveToNext());

            cursor.close();
        }

        sqLiteDatabase.close();
        return workoutsList;
    }

}
