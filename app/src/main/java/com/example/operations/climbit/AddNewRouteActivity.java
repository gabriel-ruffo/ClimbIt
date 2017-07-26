package com.example.operations.climbit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * AddNewRouteActivity.java
 * Purpose: This class adds a new route to the SQLite database. It presents the user with a form
 * format view, allowing the user to enter all information pertaining to a route.
 *
 * @author Gabriel Ruffo
 */
public class AddNewRouteActivity extends AppCompatActivity {
    private final Calendar calendar = Calendar.getInstance();
    private FeedReaderContract.FeedReaderDbHelper mDbHelper;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);

        mDbHelper = new FeedReaderContract.FeedReaderDbHelper(getApplicationContext());
        // create an OnDateSetListener for the start date
        final DatePickerDialog.OnDateSetListener start_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartLabel();
            }
        };

        // create an OnClickListener to the start date DatePicker
        View.OnClickListener start_onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddNewRouteActivity.this, start_date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };

        // create an OnDateSetListener for the finish date
        final DatePickerDialog.OnDateSetListener finish_date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateFinishLabel();
            }
        };

        // create an OnClickListener to the finish date DatePicker
        View.OnClickListener finish_onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddNewRouteActivity.this, finish_date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        };

        // find the start and finish date EditTexts
        EditText start_date_editText = (EditText) findViewById(R.id.start_date_editText);
        EditText finish_date_editText = (EditText) findViewById(R.id.finish_date_editText);

        // attach their listeners
        start_date_editText.setOnClickListener(start_onClickListener);
        finish_date_editText.setOnClickListener(finish_onClickListener);
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    /**
     * This method takes the start date and sets it with the correct format to its EditText view.
     */
    private void updateStartLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        EditText start = (EditText) findViewById(R.id.start_date_editText);
        start.setText(sdf.format(calendar.getTime()));
    }

    /**
     * This method takes the finish date and sets it with the correct format to its EditText view.
     */
    private void updateFinishLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        EditText finish = (EditText) findViewById(R.id.finish_date_editText);
        finish.setText(sdf.format(calendar.getTime()));
    }

    /**
     * This function starts the process of retrieving the information from the form on-screen
     * and saving it to the list of routes in-file. This does so by finding all of the views,
     * grabbing their values, inserting them into a Route {@link Route} object, opening the file
     * of existing routes, and saving it to that file.
     *
     * @param view The submit button calling this function.
     * @throws IOException The file was not found.
     */
    public void getRouteInformation(View view) throws IOException {
        Intent intent = new Intent(this, MainScreen.class);

        // Get all of the information from adding a new route
        EditText name = (EditText) findViewById(R.id.name_editText);
        EditText grade = (EditText) findViewById(R.id.grade_editText);
        EditText setter = (EditText) findViewById(R.id.setter_editText);
        EditText start = (EditText) findViewById(R.id.start_date_editText);
        EditText finish = (EditText) findViewById(R.id.finish_date_editText);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        EditText feltLike = (EditText) findViewById(R.id.felt_like_editText);
        EditText location = (EditText) findViewById(R.id.location_editText);

        float rating = ratingBar.getRating();

        Route route = new Route(name.getText().toString(), grade.getText().toString(),
                setter.getText().toString(), start.getText().toString(),
                finish.getText().toString(), rating, feltLike.getText().toString(),
                location.getText().toString());


        // new way of doing things. get outta here old man, the future is now
        insertNewRoute(mDbHelper, route);

        startActivity(intent);
    }

    private void insertNewRoute(FeedReaderContract.FeedReaderDbHelper mDbHelper, Route route) {
        mDbHelper.insertNewRoute(route.getName(), route.getGrade(), route.getSetter(), route.getStart(), route.getFinish(),
                route.getRating(), route.getFeltLike(), route.getLocation());
    }

    /**
     * This method starts the process of taking/saving a picture. This does so by creating an
     * ACTION_IMAGE_CAPTURE intent.
     *
     * @param view Take picture button pressed to call this method.
     */
    public void dispatchTakePictureIntent(View view) {
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


    private boolean isImageFitToScreen = false;

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
            final ImageView mImageView = (ImageView) findViewById(R.id.imageView);

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
