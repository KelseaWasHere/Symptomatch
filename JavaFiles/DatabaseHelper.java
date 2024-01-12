package com.example.symptomatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "SymptomatchDB";

    public static final String TABLE_NAME = "Conditions";

    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_URL = "URL";
    public static final String COLUMN_SYMPTOMS = "Symptoms";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_TREATMENTS = "Treatments";
    public static final String COLUMN_ACTIONS = "Actions";
    public static final String COLUMN_SAVED = "Saved";


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                    + COLUMN_NAME + " TEXT NOT NULL UNIQUE,"
                    + COLUMN_URL + " TEXT NOT NULL,"
                    + COLUMN_SYMPTOMS + " TEXT,"
                    + COLUMN_DESCRIPTION + " TEXT,"
                    + COLUMN_TREATMENTS + " TEXT,"
                    + COLUMN_ACTIONS + " TEXT,"
                    + COLUMN_SAVED + " INTEGER DEFAULT 0,"
                    + "PRIMARY KEY(" + COLUMN_NAME + ")"
                    + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create Conditions table
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create table again
        onCreate(db);
    }

    public String insertCondition(String name, String url, String symptoms, String description,
                                String treatments,String actions) {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COLUMN_NAME, name);
        values.put(COLUMN_URL, url);
        values.put(COLUMN_SYMPTOMS, symptoms);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_TREATMENTS, treatments);
        values.put(COLUMN_ACTIONS, actions);

        // insert row
        db.insert(TABLE_NAME, null, values);

        // close db connection
        db.close();

        // return newly inserted condition name
        return name;
    }

    public Condition getCondition(String name) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_NAME, COLUMN_URL, COLUMN_SYMPTOMS, COLUMN_DESCRIPTION,
                COLUMN_TREATMENTS, COLUMN_ACTIONS, COLUMN_SAVED}, COLUMN_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor.getCount() == 0)
            return null;

        if (cursor != null)
            cursor.moveToFirst();

        // prepare Condition object
        Condition condition = new Condition(
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMPTOMS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TREATMENTS)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIONS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SAVED)));

        // close the db connection
        cursor.close();

        return condition;
    }

    public List<Condition> getAllConditions() {
        List<Condition> conditions = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Condition condition = new Condition();
                condition.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                condition.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)));
                condition.setSymptoms(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMPTOMS)));
                condition.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                condition.setTreatments(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TREATMENTS)));
                condition.setActions(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIONS)));
                condition.setSaved(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SAVED)));
                conditions.add(condition);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return conditions list
        return conditions;
    }
    public List<String> getConditionNames(){
        List<String> conditionNames = new ArrayList();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_SAVED + " == 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                conditionNames.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return condition names list
        return conditionNames;
    }
    public List<Condition> getSavedConditions() {
        List<Condition> conditions = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + COLUMN_SAVED + " == 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Condition condition = new Condition();
                condition.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                condition.setUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL)));
                condition.setSymptoms(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMPTOMS)));
                condition.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                condition.setTreatments(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TREATMENTS)));
                condition.setActions(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACTIONS)));
                condition.setSaved(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SAVED)));
                conditions.add(condition);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return conditions list
        return conditions;
    }

    public int updateCondition(Condition condition) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, condition.getName());
        values.put(COLUMN_URL, condition.getUrl());
        values.put(COLUMN_SYMPTOMS, condition.getSymptoms());
        values.put(COLUMN_DESCRIPTION, condition.getDescription());
        values.put(COLUMN_TREATMENTS, condition.getTreatments());
        values.put(COLUMN_ACTIONS, condition.getActions());
        values.put(COLUMN_SAVED, condition.getSaved());

        // updating row
        return db.update(TABLE_NAME, values, COLUMN_NAME + " = ?",
                new String[]{condition.getName()});
    }
}

