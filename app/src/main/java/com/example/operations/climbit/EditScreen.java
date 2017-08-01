package com.example.operations.climbit;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_screen);

        mDbHelper = new FeedReaderContract.FeedReaderDbHelper(getApplicationContext());

        // grab the route being passed
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainScreen.EXTRA_MESSAGE);
        route_vals = message.split(";", 9);

        // listeners to update the EditTexts whenever a date is chosen from the DatePicker
        final DatePickerDialog.OnDateSetListener start_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartLabel();
            }
        };

        final DatePickerDialog.OnDateSetListener finish_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFinishLabel();
            }
        };

        // click listener on the start_date_editText to open a DatePicker
        View.OnClickListener start_onClickListener = getOnClickListener(start_date);

        // click listener on the finish_date_editText to open a DatePicker
        View.OnClickListener finish_onClickListener = getOnClickListener(finish_date);

        // attach the listeners to the date_editTexts
        EditText start_date_editText = (EditText) findViewById(R.id.start_date_editText);
        EditText finish_date_editText = (EditText) findViewById(R.id.finish_date_editText);

        start_date_editText.setOnClickListener(start_onClickListener);
        finish_date_editText.setOnClickListener(finish_onClickListener);

        // update the fields of the views
        updateViewsFields();

        // update the ImageView AFTER the layout has been set
        final ImageView imageView = (ImageView) findViewById(R.id.route_imageView);
        final ViewTreeObserver viewTreeObserver = imageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                String image_path = route_vals[8];
                if (!image_path.isEmpty()) {
                    updateViewWithImage(image_path);
                }
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        if (!mCurrentPhotoPath.isEmpty()) {
            File old_file = new File(mCurrentPhotoPath);
            if (old_file.exists()) {
                old_file.delete();
            }
        }
        super.onDestroy();
    }

    /**
     * This helper method takes an OnDateSetListener to create a new
     * OnClickListener, and returns it.
     *
     * @param dateSetListener OnDateSetListener to create the OnClickListener.
     * @return An OnClickListener with the defined
     * OnDateSetListener.
     */
    private View.OnClickListener getOnClickListener(final DatePickerDialog.OnDateSetListener dateSetListener) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(EditScreen.this, dateSetListener, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };
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

        // image is set after the window has populated the views
    }

    /**
     * This method takes the image path of the current Route,
     * and sets its bitmap to the ImageView.
     *
     * @param image_path Path to the image of the current Route.
     */
    private void updateViewWithImage(String image_path) {
        final ImageView imageView = (ImageView) findViewById(R.id.route_imageView);

        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image_path, options);

        int photoW = options.outWidth;
        int photoH = options.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(image_path, options);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        imageView.setImageBitmap(rotatedBitmap);
    }

    /**
     * This method is called when the update button is clicked on the EditScreen. It creates a
     * new intent, populates the list of routes, updates the route in question's fields, and saves
     * the new list, all through the use of helper methods.
     *
     * @param view Button view calling this method.
     */
    public void updateRouteInformation(View view) {
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

        if (!route_vals[8].isEmpty()) {
            File old_file = new File(route_vals[8]);
            if (old_file.exists()) {
                old_file.delete();
            }
        }

        startActivity(intent);
    }

    /**
     * This method puts all the new* Route information into
     * ContentValues to update the database later on.
     *
     * @return ContentValues containing Route information.
     */
    private ContentValues putUpdatedValues() {
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
        temp.put(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE, mCurrentPhotoPath);

        return temp;
    }

    /**
     * This makes sure that the user wants to delete the
     * current Route, and if so, calls a helper method to
     * delete it.
     *
     * @param view Button pressed to delete the current Route.
     */
    public void deleteRoute(View view) {
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

    /**
     * This method queries the database for the current Route,
     * and makes a deletion call. It also deletes the image in
     * the internal directory tied to the Route, if it exists.
     */
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

    /**
     * This method starts the process of taking/saving a picture. This does so by creating an
     * ACTION_IMAGE_CAPTURE intent.
     *
     * @param view Take picture button pressed to call this method.
     */
    public void dispatchTakePictureEvent(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * This method creates a unique file to store the image taken by the user. This does so
     * by setting its name to a timestamp of when the picture was taken, and putting it into
     * the pictures directory of the filesystem.
     *
     * @return Newly created file to hold the image.
     * @throws IOException Error occurred when trying to create file.
     */
    private File createImageFile() throws IOException {
        if (!mCurrentPhotoPath.isEmpty()) {
            File old_file = new File(mCurrentPhotoPath);
            if (old_file.exists()) {
                old_file.delete();
            }
        }
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * This method, based on the success of the picture taking activity, grabs the image and
     * places it into the ImageView on the screen.
     *
     * @param requestCode Code returned from the activity request.
     * @param resultCode  Code of the result of the activity.
     * @param data        The intent that initiated the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            final ImageView mImageView = (ImageView) findViewById(R.id.route_imageView);

            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            // flip the picture from landscape to portrait
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

            mImageView.setImageBitmap(rotatedBitmap);
        }
    }

}