package com.example.elizabethlanglois.artspace;

import android.app.Activity;
import android.app.ListActivity;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.*;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.graphics.PorterDuff;
import android.widget.Toast;
import android.content.SharedPreferences;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import javax.xml.transform.Result;


public class MyArt extends AppCompatActivity {
    ListView listView ;
    boolean myFavoritesShown = false;
    boolean myFoundShown = false;
    boolean myCollabsShown = false;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    // key for above preference.
    public static final String MY_USERNAME = "Username";

    DatabaseReference dbUsers;
    DatabaseReference dbItems;
    String username;
    String itemID;
    SharedPreferences sp;
    UserItem currUser;
    List<String> createdArt;
    List<String> favoriteArt;
    CanvasDrawing canvasDrawing;

    ArtItem artItem;
    String currItem;

    ArrayList<String> foundTitles;
    ArrayList<String> foundDescriptions;
    ArrayList<Bitmap> foundImages;
    ArrayList<String> collabTitles;
    ArrayList<String> collabDescriptions;
    ArrayList<Bitmap> collabImages;
    ArrayList<String> favoriteTitles;
    ArrayList<String> favoriteDescriptions;
    ArrayList<Bitmap> favoriteImages;

    ArrayList<String> favoriteIDS, collabIDs, foundIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Art");
        setContentView(R.layout.activity_my_art);

        favoriteIDS = new ArrayList<String>();
        collabIDs = new ArrayList<String>();
        foundIDs = new ArrayList<String>();

        sp = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);
        createdArt = new ArrayList<>();
        favoriteArt = new ArrayList<>();
        // get access to buttons
        final Button btnFavorites = (Button) findViewById(R.id.btnFavorites);
        final Button btnFound = (Button) findViewById(R.id.btnFound);
        final Button btnCollabs = (Button) findViewById(R.id.btnCollabs);

        // instantiate arraylists that hold each value
        foundTitles = new ArrayList<>();
        foundDescriptions = new ArrayList<>();
        foundImages = new ArrayList<>();
        collabTitles = new ArrayList<>();
        collabDescriptions = new ArrayList<>();
        collabImages = new ArrayList<>();
        favoriteTitles = new ArrayList<>();
        favoriteDescriptions = new ArrayList<>();
        favoriteImages = new ArrayList<>();



        // set buttons original background color
        btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
        btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
        btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);
       //get current user
        username = sp.getString(LoginActivity.MY_USERNAME, null);
        if(username != null) {
            // Get a pointer to the user's created items
            dbUsers = FirebaseDatabase.getInstance().getReference("Users");

            dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Retrieve the user data list
                    currUser = dataSnapshot.child(username).getValue(UserItem.class);
                    if(currUser.created_art != null) { // if user has created some art
                        createdArt = currUser.created_art; //createdArt is a list of the users created art -> now look this up on their art_items databse
                    }
                    if(currUser.favorites != null) { // if user has created some art
                        favoriteArt = currUser.favorites; //createdArt is a list of the users created art -> now look this up on their art_items databse
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        if(createdArt != null) {  //if user has created Art
            // get a reference to Art_items
            dbItems = FirebaseDatabase.getInstance().getReference("Art_items");
            // iterate through all the art Items in the database
            dbItems.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //place each art item itnto it's corresponding array

                    findViewById(R.id.LoadingText).setVisibility(View.VISIBLE);
                    for (int i = 0; i < createdArt.size(); i++) {
                        currItem = createdArt.get(i);
                        artItem = dataSnapshot.child(currItem).getValue(ArtItem.class);
                        if(artItem.type.equals("collaboration") || artItem.type.equals("drawing")) {
                            if (artItem.title != null) {
                                collabTitles.add(artItem.title);
                            } else {
                                collabTitles.add("No Title");
                            }
                            if (artItem.description != null) {
                                collabDescriptions.add(artItem.description);
                            } else {
                                collabDescriptions.add("No Description");
                            }
                            if (artItem.drawing != null) {
                                //insert correct drawing
                                collabImages.add(canvasDrawing.getImageFromData(artItem.drawing));
                            }else{
                                collabImages.add(null);
                            }

                            collabIDs.add(dataSnapshot.child(currItem).getKey());

                        }else{
                            if (artItem.title != null) {
                                foundTitles.add(artItem.title);
                            } else {
                                foundTitles.add("No Title");
                            }
                            if (artItem.description != null) {
                                foundDescriptions.add(artItem.description);
                            } else {
                                foundDescriptions.add("No Description");
                            }
                            if (artItem.drawing != null) {
                                foundImages.add(canvasDrawing.getImageFromData(artItem.drawing));
                            }else{
                                foundImages.add(null);
                            }

                            foundIDs.add(dataSnapshot.child(currItem).getKey());

                        }
                    }
                    //remove loadingText...
                    findViewById(R.id.LoadingText).setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        if(favoriteArt != null) {  //if user has favorite Art
            // get a reference to Art_items
            dbItems = FirebaseDatabase.getInstance().getReference("Art_items");
            // iterate through all the art Items in the database
            dbItems.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    findViewById(R.id.LoadingText).setVisibility(View.VISIBLE);
                    //place each art item itnto it's corresponding array
                    for (int i = 0; i < favoriteArt.size(); i++) {
                        currItem = favoriteArt.get(i);
                        artItem = dataSnapshot.child(currItem).getValue(ArtItem.class);
                        if (artItem.title != null) {
                            favoriteTitles.add(artItem.title);
                        } else {
                            favoriteTitles.add("No Title");
                        }
                        if (artItem.description != null) {
                            favoriteDescriptions.add(artItem.description);
                        } else {
                            favoriteDescriptions.add("No Description");
                        }
                        if (artItem.drawing != null) {
                            favoriteImages.add(canvasDrawing.getImageFromData(artItem.drawing));
                        }else{
                            favoriteImages.add(null);
                        }

                        favoriteIDS.add(dataSnapshot.child(currItem).getKey());
                    }
                    //remove loadingText...
                    findViewById(R.id.LoadingText).setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        // if no items indicate this to the user
     /*   if(favoriteTitles.size()==0){
            favoriteTitles.add("Oops");
            favoriteDescriptions.add("You have no favorite drawings!");
            favoriteImages.add(null);
        }
        if(foundTitles.size()==0){
            foundTitles.add("Oops");
            foundDescriptions.add("You have not found any drawings yet!");
            foundImages.add(null);

        }
        if(collabTitles.size()==0){
            collabTitles.add("Oops");
            collabDescriptions.add("You have not collaborated on any drawings yet!");
            collabImages.add(null);
        }*/
        /* Define an adapter for each listView*/
      final  MyArtListView myFavoritesAdapter=new MyArtListView(this, favoriteTitles, favoriteDescriptions,favoriteImages);
      final  MyArtListView myFoundAdapter=new MyArtListView(this, foundTitles, foundDescriptions,foundImages);
      final  MyArtListView myCollabsAdapter=new MyArtListView(this, collabTitles, collabDescriptions,collabImages);

        // setOnClickListener for myFavorites
        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                    /* show myFavorites Listview */
                    listView.setAdapter(myFavoritesAdapter);
                    myFavoritesShown = true;
                    btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent;

                        //  Get id for item
                        String itemId = favoriteIDS.get(position);

                        // Package id into intent for view item
                        // TODO Change to itemID
                        intent = new Intent(MyArt.this,LocationView.class);
                        intent.putExtra(LocationView.ART_ITEM_TAG, itemId);
                        //intent.putExtra(LocationView.ART_ITEM_TAG, "-LSRiUU075pbkD5TWO0s");


                        // Start activity
                        startActivity(intent);
                    }
                });
            }
        });
        // setOnClickListener for myFound
        btnFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                listView.setAdapter(myFoundAdapter);
                btnFound.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent;

                        //  Get id for item
                        String itemId = foundIDs.get(position);

                        // Package id into intent for view item
                        // TODO Change to itemID
                        intent = new Intent(MyArt.this,LocationView.class);
                        intent.putExtra(LocationView.ART_ITEM_TAG, itemId);
                        //intent.putExtra(LocationView.ART_ITEM_TAG, "-LSRiUU075pbkD5TWO0s");


                        // Start activity
                        startActivity(intent);
                    }
                });
            }
        });
        // setOnClickListener for myCollabs
        btnCollabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                listView.setAdapter(myCollabsAdapter);
                btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent;

                        //  Get id for item
                        String itemId = collabIDs.get(position);

                        // Package id into intent for view item
                        // TODO Change to itemID
                        intent = new Intent(MyArt.this,LocationView.class);
                        intent.putExtra(LocationView.ART_ITEM_TAG, itemId);
                        //intent.putExtra(LocationView.ART_ITEM_TAG, "-LSRiUU075pbkD5TWO0s");


                        // Start activity
                        startActivity(intent);
                    }
                });
            }
        });


    }



}
