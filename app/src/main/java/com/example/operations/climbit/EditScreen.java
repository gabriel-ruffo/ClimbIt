package com.example.operations.climbit;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * EditScreen.java
 * Purpose: This class takes care of the functionality of editing an existing Route. Reachable
 * only through an <i>existing</i> Route, EditScreen loads in all of the information stored in
 * a Route, and allows the user to make changes/delete fields.
 *
 * @author Gabriel Ruffo
 */
public class EditScreen extends AppCompatActivity {
    private String[] route_vals;

    private final Calendar calendar = Calendar.getInstance();
    private FeedReaderContract.FeedReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_screen);

        mDbHelper = new FeedReaderContract.FeedReaderDbHelper(getApplicationContext());

        // grab the route being passed
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainScreen.EXTRA_MESSAGE);
        route_vals = message.split(";", 9);

        // listener to update the start_date_editText whenever a date is chosen from the DatePicker
        final DatePickerDialog.OnDateSetListener start_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartLabel();
            }
        };

        // click listener on the start_date_editText to open a DatePicker
        View.OnClickListener start_onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditScreen.this, start_date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };

        // listener to update the finish_date_editText whenever a date is chosen from the DatePicker
        final DatePickerDialog.OnDateSetListener finish_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFinishLabel();
            }
        };

        // click listener on the finish_date_editText to open a DatePicker
        View.OnClickListener finish_onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditScreen.this, finish_date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };

        // attach the listeners to the date_editTexts
        EditText start_date_editText = (EditText) findViewById(R.id.start_date_editText);
        EditText finish_date_editText = (EditText) findViewById(R.id.finish_date_editText);

        start_date_editText.setOnClickListener(start_onClickListener);
        finish_date_editText.setOnClickListener(finish_onClickListener);

        // update the fields of the views
        updateViewsFields();
    }

    /**
     * This simply sets the text of the start_date_editText to a readable date format.
     */
    private void updateStartLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        EditText start = (EditText) findViewById(R.id.start_date_editText);
        start.setText(sdf.format(calendar.getTime()));
    }

    /**
     * This simply sets the text of the start_date_editText to a readable date format.
     */
    private void updateFinishLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        EditText finish = (EditText) findViewById(R.id.finish_date_editText);
        finish.setText(sdf.format(calendar.getTime()));
    }

    /**
     * This method updates the empty views with the route in question wanting to be edited.
     */
    private void updateViewsFields() {
        // TODO: Update an ImageView with Route's image
        EditText name = (EditText) findViewById(R.id.name_editText);
        EditText grade = (EditText) findViewById(R.id.grade_editText);
        EditText setter = (EditText) findViewById(R.id.setter_editText);
        EditText start = (EditText) findViewById(R.id.start_date_editText);
        EditText finish = (EditText) findViewById(R.id.finish_date_editText);
        RatingBar rating = (RatingBar) findViewById(R.id.rating_bar);
        EditText feltLike = (EditText) findViewById(R.id.felt_like_editText);
        EditText location = (EditText) findViewById(R.id.location_editText);

        name.setText(route_vals[0]);
        grade.setText(route_vals[1]);
        setter.setText(route_vals[2]);
        start.setText(route_vals[3]);
        finish.setText(route_vals[4]);
        rating.setRating(Float.parseFloat(route_vals[5]));
        feltLike.setText(route_vals[6]);
        location.setText(route_vals[7]);
        String image = route_vals[8];
    }

    /**
     * This method is called when the update button is clicked on the EditScreen. It creates a
     * new intent, populates the list of routes, updates the route in question's fields, and saves
     * the new list, all through the use of helper methods.
     *
     * @param view Button view calling this method.
     */
    public void updateRouteInformation(View view) {
        // TODO: implement image taking/saving functionality from AddNewRouteActivity
        // TODO: it will be very similar...
        Intent intent = new Intent(this, MainScreen.class);
        SQLiteDatabase db_writer = mDbHelper.getWritableDatabase();

        ContentValues values = putUpdatedValues();
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_GRADE + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_SETTER + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_START + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_FINISH + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_RATING + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_FELT_LIKE + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION + " = ?";

        String[] selectionArgs = {route_vals[0], route_vals[1], route_vals[2],
                route_vals[3], route_vals[4], route_vals[5],
                route_vals[6], route_vals[7]};

        int updatedRows = db_writer.update(
                FeedReaderContract.FeedEntry.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        if (updatedRows < 0) {
            Log.d("E", "ERROR Updating rows.");
        }

        startActivity(intent);
    }

    private ContentValues putUpdatedValues() {
        // TODO: implement image... same as mCurrentImagePath
        ContentValues temp = new ContentValues();

        EditText name = (EditText) findViewById(R.id.name_editText);
        EditText grade = (EditText) findViewById(R.id.grade_editText);
        EditText setter = (EditText) findViewById(R.id.setter_editText);
        EditText start = (EditText) findViewById(R.id.start_date_editText);
        EditText finish = (EditText) findViewById(R.id.finish_date_editText);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        EditText feltLike = (EditText) findViewById(R.id.felt_like_editText);
        EditText location = (EditText) findViewById(R.id.location_editText);

        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME, name.getText().toString());
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_GRADE, grade.getText().toString());
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SETTER, setter.getText().toString());
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_START, start.getText().toString());
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_FINISH, finish.getText().toString());
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_RATING, ratingBar.getRating());
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_FELT_LIKE, feltLike.getText().toString());
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION, location.getText().toString());

        return temp;
    }

    public void deleteRoute(View view) {
        // TODO: remember to delete the image through its filepath
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        Intent intent = new Intent(EditScreen.this, MainScreen.class);
                        deleteRouteFromDB();
                        startActivity(intent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setMessage("Are you sure you want to delete this Route?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void deleteRouteFromDB() {
        SQLiteDatabase db_writer = mDbHelper.getWritableDatabase();
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_NAME + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_GRADE + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_SETTER + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_START + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_FINISH + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_RATING + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_FELT_LIKE + " = ? AND " +
                FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION + " = ?";

        String[] selectionArgs = {route_vals[0], route_vals[1], route_vals[2],
                route_vals[3], route_vals[4], route_vals[5],
                route_vals[6], route_vals[7]};

        db_writer.delete(FeedReaderContract.FeedEntry.TABLE_NAME, selection, selectionArgs);

        // delete associated picture from storage
        if (!route_vals[8].isEmpty()) {
            File old_file = new File(route_vals[8]);
            if (old_file.exists()) {
                old_file.delete();
            }
        }
    }
}