package com.example.repairservicesapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.repairservicesapp.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String
            LOG = "DatabaseHelper",
            DATABASE_NAME = "RepairServicesApp.db",
            TABLE_USERS = "USERS",
            U_COLUMN_ID = "user_id",
            U_COLUMN_FNAME = "first_name",
            U_COLUMN_LNAME = "last_name",
            U_COLUMN_ADDRESS = "address",
            U_COLUMN_PHONE = "phone_number",
            U_COLUMN_EMAIL = "email",
            U_COLUMN_PASSWORD = "u_password",
            U_COLUMN_TYPE = "user_type";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS +
                        " (" + U_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        U_COLUMN_FNAME + " TEXT, " +
                        U_COLUMN_LNAME + " TEXT, " +
                        U_COLUMN_ADDRESS + " TEXT, " +
                        U_COLUMN_PHONE + " TEXT, " +
                        U_COLUMN_EMAIL + " TEXT, " +
                        U_COLUMN_PASSWORD + " TEXT, " +
                        U_COLUMN_TYPE + " TEXT);";
        db.execSQL(CREATE_USER_TABLE);
        initData(db);
        Log.d(LOG, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void initData(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        addTestUsers(db, cv);
        cv.clear();
    }

    private void addTestUsers(SQLiteDatabase db, ContentValues cv) {
        cv.put(U_COLUMN_FNAME, "Ciclo Coppi");
        cv.put(U_COLUMN_LNAME, "Bike Shop");
        cv.put(U_COLUMN_ADDRESS, "Carrera 29 # 28 19");
        cv.put(U_COLUMN_PHONE, "+573156438709");
        cv.put(U_COLUMN_EMAIL, "contacto@ciclocoppi.com");
        cv.put(U_COLUMN_PASSWORD, "123");
        cv.put(U_COLUMN_TYPE, "ADMIN");
        db.insert(TABLE_USERS, null, cv);
        cv.clear();
        Log.d(LOG, "Test user 1 added.");
    }

    /** USER DB METHODS **/

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { U_COLUMN_ID, U_COLUMN_FNAME, U_COLUMN_LNAME, U_COLUMN_ADDRESS, U_COLUMN_PHONE, U_COLUMN_PASSWORD, U_COLUMN_TYPE };
        String selection = U_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            String firstName = cursor.getString(1);
            String lastName = cursor.getString(2);
            String address = cursor.getString(3);
            String phoneNumber = cursor.getString(4);
            String password = cursor.getString(5);
            String userType = cursor.getString(6);
            user = new User(userId, firstName, lastName, address, phoneNumber, email, password, userType);
        }
        cursor.close();
        //db.close();
        return user;
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_FNAME, user.getFirstName());
        cv.put(U_COLUMN_LNAME, user.getLastName());
        cv.put(U_COLUMN_ADDRESS, user.getAddress());
        cv.put(U_COLUMN_PHONE, user.getPhoneNumber());
        cv.put(U_COLUMN_EMAIL, user.getEmail());
        cv.put(U_COLUMN_PASSWORD, user.getPassword());
        cv.put(U_COLUMN_TYPE, user.getUserType().toString());
        long result = db.insert(TABLE_USERS, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Unexpected error in adding user.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "User has been added successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public long updateUserData(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_FNAME, user.getFirstName());
        cv.put(U_COLUMN_LNAME, user.getLastName());
        cv.put(U_COLUMN_ADDRESS, user.getAddress());
        cv.put(U_COLUMN_PHONE, user.getPhoneNumber());
        cv.put(U_COLUMN_EMAIL, user.getEmail());
        String selection = U_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { String.valueOf(user.getEmail()) };
        long result = db.update(TABLE_USERS, cv, selection, selectionArgs);
        return result;
    }

    public long updateUserPassword(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_PASSWORD, user.getPassword());
        String selection = U_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { String.valueOf(user.getEmail()) };
        long result = db.update(TABLE_USERS, cv, selection, selectionArgs);
        return result;
    }

    public boolean checkUserCredentials (String email, String password) {
        String userPassword = "";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String[] column = {U_COLUMN_PASSWORD};
        String selection = "email = ? ";
        String[] selectionArgs = { email };
        Cursor cursor = sqLiteDatabase.query(TABLE_USERS, column, selection, selectionArgs, null,
                null, null);
        while (cursor.moveToNext()) {
            userPassword = cursor.getString(0);
        }
        cursor.close();
        return userPassword.equals(password);
    }
}