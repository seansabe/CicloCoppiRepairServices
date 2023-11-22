package com.example.repairservicesapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.repairservicesapp.model.Service;
import com.example.repairservicesapp.model.User;

import java.util.ArrayList;
import java.util.List;

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
            U_COLUMN_TYPE = "user_type",
            U_COLUMN_AVAILABILITY = "availability",
            U_COLUMN_TOKEN = "token",
            TABLE_SERVICES = "SERVICES",
            S_COLUMN_ID = "service_id",
            S_COLUMN_NAME = "service_name",
            S_COLUMN_DESCRIPTION = "service_description",
            S_COLUMN_PRICE = "service_price",
            S_COLUMN_DURATION = "service_duration";

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
                        U_COLUMN_TYPE + " TEXT, " +
                        U_COLUMN_AVAILABILITY + " INTEGER, " +
                        U_COLUMN_TOKEN + " TEXT);";
        db.execSQL(CREATE_USER_TABLE);
        String CREATE_SERVICE_TABLE = "CREATE TABLE " + TABLE_SERVICES +
                " (" + S_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                S_COLUMN_NAME + " TEXT, " +
                S_COLUMN_DESCRIPTION + " TEXT, " +
                S_COLUMN_PRICE + " INTEGER, " +
                S_COLUMN_DURATION + " INTEGER);";
        db.execSQL(CREATE_SERVICE_TABLE);
        initData(db);
        Log.d(LOG, "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVICES);
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
        cv.put(U_COLUMN_PHONE, "3156438709");
        cv.put(U_COLUMN_EMAIL, "admin@ciclocoppi.com");
        cv.put(U_COLUMN_PASSWORD, "123");
        cv.put(U_COLUMN_TYPE, "ADMIN");
        cv.put(U_COLUMN_AVAILABILITY, 777);
        cv.put(U_COLUMN_TOKEN, "");
        db.insert(TABLE_USERS, null, cv);
        cv.clear();
        Log.d(LOG, "Admin user added.");
    }

    /** USER DB METHODS **/

    // Get all technicians
    public List<User> getAllTechnicians() {
        List<User> users = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT u.* " +
                    "FROM USERS u " +
                    "WHERE u.user_type = 'TECHNICIAN'";
            String[] selectionArgs = {};
            Cursor cursor = db.rawQuery(query, selectionArgs);
            while (cursor.moveToNext()) {
                int userId = cursor.getInt(0);
                String firstName = cursor.getString(1);
                String lastName = cursor.getString(2);
                String phoneNumber = cursor.getString(3);
                String address = cursor.getString(4);
                String email = cursor.getString(5);
                String password = cursor.getString(6);
                String userType = cursor.getString(7);
                int availability = cursor.getInt(8);
                String token = cursor.getString(9);
                User user = new User(userId, firstName, lastName, address, phoneNumber, email, password, userType, token, availability);
                users.add(user);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Log.e("DatabaseError", "Error on database: " + e.getMessage());
        }
        return users;
    }

    // Get a user by name and last name of type Technician
    public User getTechnicianByName(String firstName, String lastName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { U_COLUMN_ID, U_COLUMN_ADDRESS, U_COLUMN_PHONE, U_COLUMN_EMAIL, U_COLUMN_PASSWORD, U_COLUMN_TYPE, U_COLUMN_AVAILABILITY, U_COLUMN_TOKEN };
        String selection = U_COLUMN_FNAME + " = ? AND " + U_COLUMN_LNAME + " = ? AND " + U_COLUMN_TYPE + " = ?";
        String[] selectionArgs = { firstName, lastName, "TECHNICIAN" };
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        User user = null;
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            String address = cursor.getString(1);
            String phoneNumber = cursor.getString(2);
            String email = cursor.getString(3);
            String password = cursor.getString(4);
            String userType = cursor.getString(5);
            int availability = cursor.getInt(6);
            String token = cursor.getString(7);
            user = new User(userId, firstName, lastName, address, phoneNumber, email, password, userType, token, availability);
        }
        cursor.close();
        //db.close();
        return user;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { U_COLUMN_ID, U_COLUMN_FNAME, U_COLUMN_LNAME, U_COLUMN_ADDRESS, U_COLUMN_PHONE, U_COLUMN_PASSWORD, U_COLUMN_TYPE, U_COLUMN_AVAILABILITY, U_COLUMN_TOKEN };
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
            int availability = cursor.getInt(7);
            String token = cursor.getString(8);
            user = new User(userId, firstName, lastName, address, phoneNumber, email, password, userType, token, availability);
        }
        cursor.close();
        return user;
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_FNAME, user.firstName);
        cv.put(U_COLUMN_LNAME, user.lastName);
        cv.put(U_COLUMN_ADDRESS, user.address);
        cv.put(U_COLUMN_PHONE, user.phoneNumber);
        cv.put(U_COLUMN_EMAIL, user.email);
        cv.put(U_COLUMN_PASSWORD, user.password);
        cv.put(U_COLUMN_TYPE, user.userType.toString());
        cv.put(U_COLUMN_TOKEN, "");
        cv.put(U_COLUMN_AVAILABILITY, user.userAvailability);
        long result = db.insert(TABLE_USERS, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Unexpected error in adding user.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "User has been added successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateUserData(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_FNAME, user.firstName);
        cv.put(U_COLUMN_LNAME, user.lastName);
        cv.put(U_COLUMN_ADDRESS, user.address);
        cv.put(U_COLUMN_PHONE, user.phoneNumber);
        cv.put(U_COLUMN_EMAIL, user.email);
        String selection = U_COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(user.getUserId()) };
        long result = db.update(TABLE_USERS, cv, selection, selectionArgs);
        if (result == -1) {
            Log.d("DATABASE", "Unexpected error in updating user data.");
        } else {
            Log.d("DATABASE", "User data has been updated successfully.");
        }
    }

    public void updateUserPassword(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_PASSWORD, user.password);
        String selection = U_COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { String.valueOf(user.email) };
        long result = db.update(TABLE_USERS, cv, selection, selectionArgs);
        if (result == -1) {
            Log.d("DATABASE", "Unexpected error in updating user password.");
        } else {
            Log.d("DATABASE", "User password has been updated successfully.");
        }
    }

    public void updateTechnicianAvailability(Integer userId, Integer availability) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_AVAILABILITY, availability);
        String selection = U_COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(userId) };
        long result = db.update(TABLE_USERS, cv, selection, selectionArgs);
        if (result == -1) {
            Log.d("DATABASE", "Unexpected error in updating technician availability.");
        } else {
            Log.d("DATABASE", "Technician availability has been updated successfully.");
        }
    }

    // Update user token
    public void updateUserToken(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(U_COLUMN_TOKEN, user.token);
        String selection = U_COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(user.getUserId()) };
        long result = db.update(TABLE_USERS, cv, selection, selectionArgs);
        if (result == -1) {
            Log.d("DATABASE", "Unexpected error in updating user token.");
        } else {
            Log.d("DATABASE", "User token has been updated successfully.");
        }
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

    /** SERVICE DB METHODS **/

    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT s.* " +
                    "FROM SERVICES s";
            String[] selectionArgs = {};
            Cursor cursor = db.rawQuery(query, selectionArgs);
            while (cursor.moveToNext()) {
                int serviceId = cursor.getInt(0);
                String serviceName = cursor.getString(1);
                String serviceDescription = cursor.getString(2);
                int servicePrice = cursor.getInt(3);
                int serviceDuration = cursor.getInt(4);
                Service service = new Service(serviceId, serviceName, serviceDescription, servicePrice, serviceDuration);
                services.add(service);
            }
            cursor.close();
            db.close();
        } catch (SQLiteException e) {
            Log.e("DatabaseError", "Error on database: " + e.getMessage());
        }
        return services;
    }

    public Service getServiceByName(String serviceName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { S_COLUMN_ID, S_COLUMN_NAME, S_COLUMN_DESCRIPTION, S_COLUMN_PRICE, S_COLUMN_DURATION };
        String selection = S_COLUMN_NAME + " = ?";
        String[] selectionArgs = { serviceName };
        Cursor cursor = db.query(TABLE_SERVICES, columns, selection, selectionArgs, null, null, null);
        Service service = null;
        if (cursor.moveToFirst()) {
            int serviceId = cursor.getInt(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            int price = cursor.getInt(3);
            int duration = cursor.getInt(4);
            service = new Service(serviceId, name, description, price, duration);
        }
        cursor.close();
        //db.close();
        return service;
    }

    public void addService(Service service) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(S_COLUMN_NAME, service.serviceName);
        cv.put(S_COLUMN_DESCRIPTION, service.serviceDescription);
        cv.put(S_COLUMN_PRICE, service.serviceCost);
        cv.put(S_COLUMN_DURATION, service.serviceDuration);
        long result = db.insert(TABLE_SERVICES, null, cv);
        if(result == -1) {
            Toast.makeText(context, "Unexpected error in adding service.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Service has been added successfully.", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateService(Service service) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(S_COLUMN_NAME, service.serviceName);
        cv.put(S_COLUMN_DESCRIPTION, service.serviceDescription);
        cv.put(S_COLUMN_PRICE, service.serviceCost);
        cv.put(S_COLUMN_DURATION, service.serviceDuration);
        String selection = S_COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(service.getServiceId()) };
        long result = db.update(TABLE_SERVICES, cv, selection, selectionArgs);
        if (result == -1) {
            Toast.makeText(context, "Unexpected error in updating service.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Service has been updated successfully.", Toast.LENGTH_SHORT).show();
        }
    }
}