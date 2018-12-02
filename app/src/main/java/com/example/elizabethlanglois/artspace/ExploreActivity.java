package com.example.elizabethlanglois.artspace;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.widget.TextView;
import android.widget.ListView;
import android.graphics.Bitmap;

import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.widget.AdapterView;

public class ExploreActivity extends AppCompatActivity {

    private EditText SearchText;
    private Button SearchButton;
    private CheckBox ViewArtBox,CollaborateArtBox;
    private ListView theList;
    private TextView ResultsText;

    private String TAG = "ExploreActivity";



    private String username;
    private SharedPreferences sp;
    private DatabaseReference dbUsers;
    DatabaseReference db;
    private ArtItem currItem;

    ExploreActivity thisActivity = this;

    ArrayList<String> ArtItemIds = new ArrayList<String>();
    ArrayList<String> ArtNames = new ArrayList<String>();
    ArrayList<String> ArtDescriptions = new ArrayList<String>();
    ArrayList<String> ArtTypes = new ArrayList<String>();

    ArrayList<String> ArtDates = new ArrayList<String>();
    ArrayList<String> ArtTimes = new ArrayList<String>();

    ArrayList<String> ArtLocations = new ArrayList<String>();
    ArrayList<Bitmap> ArtBitmaps = new ArrayList<Bitmap>();

    boolean viewArtChecked;
    boolean collaborateArtChecked;

    ExploreActivityView allItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_art);

        SearchText = (EditText) findViewById(R.id.searchText);
        //LocationText = (EditText) findViewById(R.id.location);

        //MinusButton = (Button) findViewById(R.id.minus);
        //DistanceInputText = (EditText) findViewById(R.id.distance);
        //PlusButton = (Button) findViewById(R.id.plus);

        ViewArtBox = (CheckBox) findViewById(R.id.viewArtBox);
        CollaborateArtBox = (CheckBox) findViewById(R.id.collaborateArtBox);
        SearchButton = (Button) findViewById(R.id.search);
        ResultsText = (TextView) findViewById(R.id.resultsText);

        setTitle("Explore Art");



        // Get shared preferences
        sp = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);

        // Get current user
        username = sp.getString(LoginActivity.MY_USERNAME, null);

        Log.i(TAG,"User is " + username.toString());

        // Check if user exists
        if(username != null) {


        }

        // Get a pointer to all of the art items
        db = FirebaseDatabase.getInstance().getReference("Art_items");


        // On clicking the search button, search for results
        SearchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String search = SearchText.getText().toString();
                viewArtChecked = ViewArtBox.isChecked();
                collaborateArtChecked = CollaborateArtBox.isChecked();

                // Empty out the arrays
                ArtItemIds.clear();
                ArtNames.clear();
                ArtDescriptions.clear();
                ArtTypes.clear();
                ArtDates.clear();
                ArtTimes.clear();
                ArtLocations.clear();

                ResultsText.setText("Loading results");

                //theList.setFooterDividersEnabled(true);

                if (viewArtChecked || collaborateArtChecked) {
                    // Retrieve the user data list
                    db.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String title, location;
                            Boolean addThis;

                            Pattern p = Pattern.compile(SearchText.getText().toString().toLowerCase());


                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Matcher m;
                                    String description;

                                    // Reset back to false
                                    addThis = false;

                                    Log.i(TAG, "Is the id " + postSnapshot.getKey() + "?");

                                    // Get the ArtItem
                                    currItem = postSnapshot.getValue(ArtItem.class);
                                    Log.i(TAG, "currItem is " + currItem.toString());


                                    // TODO User input sanitization?
                                    // Check for title
                                    m = p.matcher(currItem.title.toLowerCase());
                                    if (currItem.title != null && m.find()) { // if user has created some art
                                        addThis = true;
                                        Log.i(TAG, "Title is " + currItem.title);
                                    }
                                    // Check for location
                                    m = p.matcher(currItem.location.toLowerCase());
                                    if (currItem.location != null && m.find()) { // if user has created some art
                                        addThis = true;
                                        Log.i(TAG, "Location is " + currItem.location);
                                    }
                                    // Check for description
                                    m = p.matcher(currItem.description.toLowerCase());
                                    if (currItem.description != null && m.find()) { // if user has created some art
                                        addThis = true;
                                        Log.i(TAG, "Description is " + currItem.description);
                                    }

                                    //Trim description if necessary
                                    description = currItem.description;
                                    if (description.length() > 30) {
                                        description = description.substring(0, 28);
                                        description += "...";
                                    }

                                    // If view art is checked, add potential links to list
                                    if (viewArtChecked
                                            && currItem.type.equals("viewable")
                                            && addThis) {
                                        ArtItemIds.add(postSnapshot.getKey());
                                        if(currItem.drawing != null) {
                                            ArtBitmaps.add(LocationView.getImageFromData(currItem.drawing));
                                        }else{
                                            ArtBitmaps.add(null);
                                        }
                                        ArtNames.add(currItem.title);
                                        ArtTypes.add(currItem.type.substring(0, 1).toUpperCase() + currItem.type.substring(1));
                                        ArtDates.add(currItem.date);
                                        ArtTimes.add(currItem.time);
                                        ArtLocations.add(currItem.location);
                                        ArtDescriptions.add(description);
                                    }

                                    // If collaborate on art is checked, add potential links to list
                                    if (collaborateArtChecked
                                            && (currItem.type.equals("collaboration") || currItem.type.equals("drawing"))
                                            && addThis) {
                                        ArtItemIds.add(postSnapshot.getKey());
                                        if(currItem.drawing != null) {
                                            ArtBitmaps.add(LocationView.getImageFromData(currItem.drawing));
                                        }else{
                                            ArtBitmaps.add(null);
                                        }
                                        ArtNames.add(currItem.title);
                                        ArtTypes.add(currItem.type.substring(0, 1).toUpperCase() + currItem.type.substring(1));
                                        ArtDates.add(currItem.date);
                                        ArtTimes.add(currItem.time);
                                        ArtLocations.add(currItem.location);
                                        ArtDescriptions.add(description);
                                    }

                                    ExploreActivityView allItemsAdapter;
                                    Log.i(TAG, "SIZE is " + ArtNames.size());
                                    if (ArtNames.size() != 0) {
                                        Integer[] a = {R.drawable.monalisa};
                                        allItemsAdapter = new ExploreActivityView(thisActivity, ArtNames, ArtDescriptions, ArtBitmaps, ArtTypes, ArtDates, ArtTimes, ArtLocations, ArtItemIds, a);

                                        // Get ListView object from xml
                                        ListView listView = (ListView) findViewById(R.id.list);

                                        Log.i(TAG, "Before click");

                                        // Remove Loading
                                        ResultsText.setText("");

                                        /* show myFavorites Listview */
                                        listView.setAdapter(allItemsAdapter);

                                        // TODO I want an onItemClickListener
                                        // Set onItemClickListener
                                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                Intent intent;

                                                Log.i(TAG, "Got click");

                                                //  Get id for item
                                                String itemId = ArtItemIds.get(position);

                                                // Package id into intent for view item
                                                // TODO Change to itemID
                                                intent = new Intent(ExploreActivity.this,LocationView.class);
                                                intent.putExtra(LocationView.ART_ITEM_TAG, "-LSRiUU075pbkD5TWO0s");

                                                // Start activity
                                                startActivity(intent);
                                            }
                                        });

                                    } else {
                                        ResultsText.setText("No results found");
                                    }
                                }
                        }

                        // Occurs if error on retrieving from Firebase
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    // Didn't search anything
                    ResultsText.setText("No results found");
                }
            }
        });

    }
}
