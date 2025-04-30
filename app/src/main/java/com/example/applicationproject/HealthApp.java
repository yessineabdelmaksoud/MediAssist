package com.example.applicationproject;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import database.DatabaseMigrationHelper;

public class HealthApp extends Application {

    private static final String PREFS_NAME = "AppPrefs";
    private static final String KEY_DB_MIGRATED = "database_migrated";

    @Override
    public void onCreate() {
        super.onCreate();

        // Vérifier si la migration a déjà été effectuée
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean dbMigrated = prefs.getBoolean(KEY_DB_MIGRATED, false);

        if (!dbMigrated) {
            // Effectuer la migration des données
            DatabaseMigrationHelper migrationHelper = new DatabaseMigrationHelper(this);
            migrationHelper.migrateData();

            // Marquer la migration comme terminée
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(KEY_DB_MIGRATED, true);
            editor.apply();
        }
    }
}