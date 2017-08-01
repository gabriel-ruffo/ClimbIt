package com.example.operations.climbit;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * SettingsScreen.java
 * Purpose: This class contains the settings options for the user.
 *
 * @author Gabriel Ruffo
 */
public class SettingsScreen extends AppCompatActivity {
    private FeedReaderContract.FeedReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);

        mDbHelper = new FeedReaderContract.FeedReaderDbHelper(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    /**
     * This method checks to make sure if a user wants to delete all Routes,
     * and if so, does so, and returns to the MainScreen.
     *
     * @param view Button pressed to delete all Routes.
     */
    public void deleteAllRoutes(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        Intent intent = new Intent(SettingsScreen.this, MainScreen.class);
                        SQLiteDatabase db_writer = mDbHelper.getWritableDatabase();
                        mDbHelper.remakeDB(db_writer);
                        startActivity(intent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure you want to delete all Routes? This can't be undone.").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
