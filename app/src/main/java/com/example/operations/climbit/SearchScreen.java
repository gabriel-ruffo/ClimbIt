package com.example.operations.climbit;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchScreen.java
 * Purpose: This class shows the search results of the parameters given
 * it from the MainScreen.
 *
 * @author Gabriel Ruffo
 */
public class SearchScreen extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.ClimbIt.MESSAGE";
    private String[] search_values;
    private FeedReaderContract.FeedReaderDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        mDbHelper = new FeedReaderContract.FeedReaderDbHelper(getApplicationContext());

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainScreen.EXTRA_MESSAGE);
        search_values = message.split(";");

        ActionBar actionBar = getSupportActionBar();
        // set search parameter
        if (actionBar != null) {
            actionBar.setTitle("Searching by '" + search_values[0] + "'");
        }
        // set search value
        TextView search_field = (TextView) findViewById(R.id.search_field);
        search_field.setText("\"" + search_values[1] + "\":");

        updateSearchView();
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    /**
     * This method is called first in the process of populating the screen with the routes
     * that were found from the search. If no routes were found in the routes file, the default
     * error message is shown.
     */
    private void updateSearchView() {
        SQLiteDatabase db_reader = mDbHelper.getReadableDatabase();

        Cursor cursor = db_reader.rawQuery("SELECT * FROM " + FeedReaderContract.FeedEntry.TABLE_NAME, null);
        List<Route> routes_from_query = populateList(cursor);

        LinearLayout routes_from_search_layout = (LinearLayout) findViewById(R.id.search_layout);
        TextView error;

        // if there are no routes, alert the user
        if (routes_from_query.isEmpty()) {
            error = new TextView(this);
            error.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            error.setText("No routes matching that parameter!");

            if (routes_from_search_layout.getChildCount() == 0) {
                routes_from_search_layout.addView(error);
            }
        } else {
            // get rid of any children from before so that we can repopulate
            if (routes_from_search_layout.getChildCount() > 0) {
                routes_from_search_layout.removeAllViews();
            }
            // add the routes from the search to the screen
            addRoutesToLayout(routes_from_query, routes_from_search_layout);
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
     * This method finds the accepted routes from the base list of all routes against the
     * search criteria given by the user. If no results were given, an error message is shown on
     * the screen. Otherwise, for each route found, a small packet of information created by a
     * helper method is appended to the container layout.
     *
     * @param routeList      The list of resultant routes from the search.
     * @param layout_wrapper The container layout to house the sub layouts holding the routes'
     *                       information.
     */
    private void addRoutesToLayout(List<Route> routeList, LinearLayout layout_wrapper) {
        List<Route> searchList = getAcceptedRoutes(routeList);

        // check if the search brought up no results
        if (searchList.size() == 0) {
            setUpNoRoutesView(layout_wrapper);
        }

        // routes were found
        for (int i = 0; i < searchList.size(); i++) {
            setUpAndAddRouteToLayout(searchList, layout_wrapper, i);
        }
    }

    private void setUpNoRoutesView(LinearLayout layout_wrapper) {
        // initialize a temporary LinearLayout
        LinearLayout temp_layout = new LinearLayout(this);

        // set up the orientation and layout parameters
        temp_layout.setOrientation(LinearLayout.HORIZONTAL);
        temp_layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // create a TextView for the case where no routes were found
        TextView no_routes = new TextView(this);
        no_routes.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        no_routes.setText("No routes matching that parameter!");

        // add the TextView to the temporary layout
        temp_layout.addView(no_routes);
        // add the temporary layout to the wrapper layout
        layout_wrapper.addView(temp_layout);
    }

    private void setUpAndAddRouteToLayout(List<Route> searchList, LinearLayout layout_wrapper, int i) {
        // initialize a temporary LinearLayout to house the route information
        LinearLayout temp_layout = new LinearLayout(this);

        // set the orientation and layout parameters
        temp_layout.setOrientation(LinearLayout.HORIZONTAL);
        temp_layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // create a new TextView with basic information about the routes
        TextView route_textView = getNewTextView(searchList.get(i));
        route_textView.setText("Name: " + searchList.get(i).getName() + "\r\n" +
                "Grade: " + searchList.get(i).getGrade() + "\r\n" +
                "Rating: " + searchList.get(i).getRating() + "\r\n" +
                "Location: " + searchList.get(i).getLocation() + "\r\n");
        route_textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        // add the text view to the temporary layout
        temp_layout.addView(route_textView);
        // add the temporary layout to the wrapper layout
        layout_wrapper.addView(temp_layout);
    }
    /**
     * Creates a TextView with the right LayoutParams and allows it to be clickable. A click
     * listener is attached such that when it is clicked, the EditScreen is brought up containing
     * the information stored in that TextView as an extra message.
     *
     * @param current_route The route currently being looked at
     * @return A new TextView with LayoutParams and OnClickListener
     */
    private TextView getNewTextView(final Route current_route) {
        // initialize the temporary TextView and set its layout parameters
        TextView temp = new TextView(this);
        temp.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // attach an OnClickListener to the TextView that will send the route and its information
        // to the EditScreen
        temp.setClickable(true);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchScreen.this, EditScreen.class);
                String route_values = current_route.getName() + ";" + current_route.getGrade() + ";"
                        + current_route.getSetter() + ";" + current_route.getStart() + ";"
                        + current_route.getFinish() + ";" + current_route.getRating() + ";"
                        + current_route.getFeltLike() + ";" + current_route.getLocation() + ";"
                        + current_route.getImage();
                intent.putExtra(EXTRA_MESSAGE, route_values);
                startActivity(intent);
            }
        });
        return temp;
    }

    /**
     * This method takes the full list of routes in the main route file, and based on the search
     * parameter and search value, accepts only the routes that match those criteria. This does so
     * through the use of a switch statement and for loops, checking the respective fields for
     * the search value passed. The list is populated - or not if no matches are found - and
     * returned.
     *
     * @param original_list The list of routes from the main route file.
     * @return The doctored list of routes that match the criteria of the search.
     */
    private List<Route> getAcceptedRoutes(List<Route> original_list) {
        List<Route> accepted_routes = new ArrayList<>();
        String searchParam = search_values[0];
        String searchValue = search_values[1].toLowerCase().trim();

        // depending on search parameter, get specific values from routes on file
        switch (searchParam) {
            case "Grade":
                // searching by the grade of the route
                String original_value;
                for (int i = 0; i < original_list.size(); i++) {
                    original_value = original_list.get(i).getGrade().toLowerCase().trim();
                    if (original_value.contains(searchValue)) {
                        accepted_routes.add(original_list.get(i));
                    }
                }
                break;
            case "Name":
                // searching by the name of the route
                for (int i = 0; i < original_list.size(); i++) {
                    original_value = original_list.get(i).getName().toLowerCase().trim();
                    if (original_value.contains(searchValue)) {
                        accepted_routes.add(original_list.get(i));
                    }
                }
                break;
            case "Rating":
                // searching by the rating given to the route by the user
                float original_rating;
                for (int i = 0; i < original_list.size(); i++) {
                    original_rating = original_list.get(i).getRating();
                    if (original_rating == Float.valueOf(searchValue)) {
                        accepted_routes.add(original_list.get(i));
                    }
                }
                break;
            case "Setter":
                // searching by the setter of the route
                for (int i = 0; i < original_list.size(); i++) {
                    original_value = original_list.get(i).getSetter().toLowerCase().trim();
                    if (original_value.contains(searchValue)) {
                        accepted_routes.add(original_list.get(i));
                    }
                }
                break;
        }
        return accepted_routes;
    }
}
