package com.example.operations.climbit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * MainScreen.java
 * Purpose: This class describes the main screen the user sees on start up of the app. It holds
 * the hub for the three main activities: search existing routes {@link #searchByField(View)},
 * add a new route {@link #addRoutesToLayout(List, LinearLayout)}, and browse existing
 * routes {@link #updateProjectsView()}.
 * All other activities are reachable from this screen.
 *
 * @author Gabriel Ruffo
 */
public class MainScreen extends AppCompatActivity {
    // string to store extra strings to pass on to other activities
    public static final String EXTRA_MESSAGE = "com.example.ClimbIt.MESSAGE";
    private FeedReaderContract.FeedReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // connect to the database
        mDbHelper = new FeedReaderContract.FeedReaderDbHelper(getApplicationContext());

        updateProjectsView();

        // Set up the search functionality
        final Button button = (Button) findViewById(R.id.search_button);
        EditText searchField = (EditText) findViewById(R.id.search_text_box);

        // set button disabled initially
        button.setEnabled(false);

        // add a listener to the EditText so that the search button is enabled when text is entered
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    /**
     * This updates the projects div whenever you come back to the main page.
     */
    @Override
    public void onResume() {
        super.onResume();
        updateProjectsView();
    }

    /**
     * This refreshes the ProjectsView on the MainScreen. This does so by opening the routes
     * storage file, grabbing all routes - if there are some - and repopulating the view with
     * the routes. If there are no routes, then it appends a TextView explaining so.
     */
    private void updateProjectsView() {
        SQLiteDatabase db_reader = mDbHelper.getReadableDatabase();

        Cursor cursor = db_reader.rawQuery("SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME, null);
        List<Route> routes_from_query = populateList(cursor);

        LinearLayout routes_layout = (LinearLayout) findViewById(R.id.routes_layout);
        TextView error;

        if (routes_from_query.isEmpty()) {
            // no projects were found
            error = new TextView(this);
            error.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            error.setText("No current projects.");

            if (routes_layout.getChildCount() == 0)
                routes_layout.addView(error);

        } else {
            // if the error TextView was there before, get rid of it
            // or if you need to remove the old routes and put in the new ones
            if (routes_layout.getChildCount() > 0) {
                routes_layout.removeAllViews();
            }
            addRoutesToLayout(routes_from_query, routes_layout);
        }
    }

    private List<Route> populateList(Cursor cursor) {
        List<Route> routes = new ArrayList<>();
        while (cursor.moveToNext()) {
            Route temp = new Route(
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_GRADE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SETTER)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_START)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_FINISH)),
                    cursor.getFloat(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_RATING)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_FELT_LIKE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_LOCATION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_IMAGE)));
            routes.add(temp);
        }

        return routes;
    }

    /**
     * This method takes the list of routes and the wrapper linear layout and adds them all
     * as children to the layout. This does so by creating horizontal layouts, adding text views,
     * and adding the layout view to the wrapper.
     *
     * @param routes_list    List of routes on file
     * @param layout_wrapper The wrapping linear layout into which to put children layouts
     */
    private void addRoutesToLayout(List<Route> routes_list, LinearLayout layout_wrapper) {
        for (int i = 0; i < routes_list.size(); i++) {
            // create a temporary linear layout
            LinearLayout temp_layout = new LinearLayout(this);
            temp_layout.setOrientation(LinearLayout.HORIZONTAL);
            // set view of linear layout to width: parent and height: wrap content
            temp_layout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            // create a temporary text view
            TextView route_textView = getNewTextView(routes_list.get(i), i);

            // set the text to the routes' names
            //TODO: Extract and set image from filepath to imageView
            route_textView.setText("Name: " + routes_list.get(i).getName() + "\r\n" +
                    "Grade: " + routes_list.get(i).getGrade() + "\r\n" +
                    "Setter: " + routes_list.get(i).getSetter() + "\r\n" +
                    "Start: " + routes_list.get(i).getStart() + "\r\n" +
                    "Finish: " + routes_list.get(i).getFinish() + "\r\n" +
                    "Rating: " + routes_list.get(i).getRating() + "\r\n" +
                    "Felt Like: " + routes_list.get(i).getFeltLike() + "\r\n" +
                    "Location: " + routes_list.get(i).getLocation() + "\r\n");
            route_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            // set up a new ImageView with the Route's image
            String image_path = routes_list.get(i).getImage();

            if (!image_path.isEmpty()) {
                ImageView route_image = new ImageView(this);
                route_image.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));

                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(image_path, options);
                bitmap = Bitmap.createScaledBitmap(bitmap, 420, 350, true);

                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                route_image.setImageBitmap(rotatedBitmap);
                temp_layout.addView(route_image);
            }

            // add the view to the temporary linear layout
            temp_layout.addView(route_textView);

            // and the temporary layout to the wrapper layout
            layout_wrapper.addView(temp_layout);
        }
    }

    /**
     * Creates a TextView with the right LayoutParams and allows it to be clickable. A click
     * listener is attached such that when it is clicked, the EditScreen is brought up containing
     * the information stored in that TextView as an extra message.
     *
     * @param current_route  The route currently being looked at
     * @param route_position The index of the route in the storage file
     * @return A new TextView with LayoutParams and OnClickListener
     */
    private TextView getNewTextView(final Route current_route, final int route_position) {
        // TODO: implement an ImageView to show Route's image
        // initialize a temporary TextView with LayoutParams
        TextView temp = new TextView(this);
        temp.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // set up the TextView to be clickable and have a click listener
        temp.setClickable(true);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send everything over to EditScreen
                Intent intent = new Intent(MainScreen.this, EditScreen.class);
                // grab the route's values and store them in an extra message
                String route_values = current_route.getName() + ";" + current_route.getGrade() + ";"
                        + current_route.getSetter() + ";" + current_route.getStart() + ";"
                        + current_route.getFinish() + ";" + current_route.getRating() + ";"
                        + current_route.getFeltLike() + ";" + current_route.getLocation() + ";"
                        + current_route.getImage();
                intent.putExtra(EXTRA_MESSAGE, route_values);
                startActivity(intent);
            }
        });

        // return new TextView
        return temp;
    }

    /**
     * addNewRoute is called when add_new_route_button is clicked and sends the user to the
     * AddNewRouteActivity screen.
     *
     * @param view View calling this function
     */
    public void addNewRoute(View view) {
        // Go to add route screen
        Intent intent = new Intent(this, AddNewRouteActivity.class);
        startActivity(intent);
    }

    /**
     * This function checks the radio buttons and the text box for the search parameters needed
     * to filter the routes on file. Sends the user to SearchScreen screen to display the results.
     *
     * @param view View calling this function
     */
    public void searchByField(View view) {
        Intent intent = new Intent(this, SearchScreen.class);

        // grab all the search information
        EditText searchValue = (EditText) findViewById(R.id.search_text_box);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.search_radio_group);
        RadioButton checked = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

        // set the string to whichever radio button was checked
        String radioStr = "";
        switch (checked.getId()) {
            case R.id.grade_radio_button:
                radioStr = "Grade";
                break;
            case R.id.name_radio_button:
                radioStr = "Name";
                break;
            case R.id.rating_radio_button:
                radioStr = "Rating";
                break;
            case R.id.setter_radio_button:
                radioStr = "Setter";
                break;
        }

        String searchStr = searchValue.getText().toString();

        // put all strings into a message and send with intent
        String message = radioStr + ";" + searchStr;
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}