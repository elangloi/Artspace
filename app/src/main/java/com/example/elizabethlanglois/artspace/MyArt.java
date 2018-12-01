package com.example.elizabethlanglois.artspace;

import android.app.Activity;
import android.app.ListActivity;

import java.lang.reflect.Array;
import java.util.*;
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


public class MyArt extends AppCompatActivity {
    ListView listView ;
    boolean myFavoritesShown = false;
    boolean myFoundShown = false;
    boolean myCollabsShown = false;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    // key for above preference.
    public static final String MY_USERNAME = "Username";

    // Defined Array values to show in ListView
    String[] myFavoritesvalues = new String[] { "Tuttomondo - Keith Haring",
            "Nelson Mandela Mural - Shepard Fairey",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini",
            "Mural in Aspen - Christina Angelina",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini",
            "Mural in Aspen - Christina Angelina",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini",
            "Mural in Aspen - Christina Angelina"
    };
    String[] myFoundvalues = new String[] { "Fake - FakeData",
            "Nelson Mandela Mural - Shepard Fairey",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini"
    };
    String[] myCollabvalues = new String[] { "Keith Haring",
            "Shepard Fairey",
            "Eduardo Kobra",
            "Os Gemeos",
            "Koz Dos",
            "Dzia",
            "Alice Pasquini",
            "Christina Angelina",
            "Eduardo Kobra",
            "Jimmy",
            "Roberta",
            "Phillip",
            "Jorge"
    };

    DatabaseReference dbUsers;
    DatabaseReference dbItems;
    String username;
    String itemID;
    SharedPreferences sp;
    UserItem currUser;
    List<String> createdArt;
    ArtItem artItem;
    String currItem;

    ArrayList<String> foundTitles;
    ArrayList<String> foundDescriptions;
    ArrayList<String> collabTitles;
    ArrayList<String> collabDescriptions;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("My Art");
        sp = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);

        createdArt = new ArrayList<>();



        setContentView(R.layout.activity_my_art);
        // get access to buttons
        final Button btnFavorites = (Button) findViewById(R.id.btnFavorites);
        final Button btnFound = (Button) findViewById(R.id.btnFound);
        final Button btnCollabs = (Button) findViewById(R.id.btnCollabs);

        // set buttons original background color
        btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
        btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
        btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // MyFavorites fake data
        String[] myFavoritesMuralNames ={
                "Tuttomondo","Nelson Mandela Mural", "Etnias","The Giant of Boston",
                "Mural in Maracay", "Mural in Maracay","The Golden Elephant","Along The Way",
                "Mural in Aspen", "Tuttomondo","Nelson Mandela Mural", "Etnias",
                "The Giant of Boston", "Mural in Maracay", "Mural in Maracay","The Golden Elephant"
        };

        String[] myFavoritesArtistNames ={
                "Keith Haring","Eduardo Kobra", "Os Gemeos","Koz Dos", "Dzia", "Alice Pasquini",
                "Christina Angelina", "Eduardo Kobra", "Jimmy", "Roberta", "Phillip", "Jorge",
                "Keith Haring","Eduardo Kobra", "Os Gemeos","Koz Dos"
        };
        Integer[] myFavoritesimgid={
                R.drawable.monalisa,

        };
        // MyFound fake data
        String[] myFoundMuralNames ={
                "Tuttomondo","Nelson Mandela Mural", "Etnias","The Giant of Boston",
                "Mural in Maracay", "Mural in Maracay","The Golden Elephant","Along The Way",

        };

        String[] myFoundArtistNames ={
                "Keith Haring","Eduardo Kobra", "Os Gemeos","Koz Dos", "Dzia", "Alice Pasquini",
                "Christina Angelina", "Eduardo Kobra",
        };
        Integer[] myFoundimgid={
                R.drawable.monalisa,

        };
        // MyCollabs fake data
        String[] myCollabsMuralNames ={
                "Tuttomondo","Nelson Mandela Mural"
        };

        String[] myCollabsArtistNames ={
                "Keith Haring","Eduardo Kobra"
        };
        Integer[] myCollabsimgid={
                R.drawable.monalisa,

        };
       /* dbUsers = FirebaseDatabase.getInstance().getReference("Users");
        // get current user

        dbUsers.child(name);*/
       //get current user
        username = sp.getString(LoginActivity.MY_USERNAME, null);
        Log.i("username",username.toString());
        if(username != null) {
            // Get a pointer to the user's created items
            dbUsers = FirebaseDatabase.getInstance().getReference("Users");
            dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Retrieve the user data list
                    currUser = dataSnapshot.child(username).getValue(UserItem.class);
                    Log.i("CurrUser",currUser.toString());
                    if(currUser.created_art != null) { // if user has created some art
                        createdArt = currUser.created_art; //createdArt is a list of the users created art -> now look this up on their art_items databse
                        Log.i("CreatedArt",currUser.created_art.toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        if(createdArt != null) {  //if user has created Art
            // instantiate arraylists that hold each value
            foundTitles = new ArrayList<>();
            foundDescriptions = new ArrayList<>();
            collabTitles = new ArrayList<>();
            collabDescriptions = new ArrayList<>();
            // get a reference to Art_items
            dbItems = FirebaseDatabase.getInstance().getReference("Art_items");
            // iterate through all the art Items in the database
            dbItems.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //place each art item itnto it's corresponding array
                    for (int i = 0; i < createdArt.size(); i++) {
                        currItem = createdArt.get(i);
                        artItem = dataSnapshot.child(currItem).getValue(ArtItem.class);
                        if(artItem.type.equals("collaboration")) {
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
                        }
                    }

                    //    currItem = dataSnapshot.child(username).getValue(ArtItem.class);
                    //    createdArt = currUser.created_art; //createdArt is a list of the users created art -> now look this up on their art_items databse

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

//todo: add myFavorites, add drawings, add default value if no art yet added, clean up style


        /* Define an adapter for each listView*/
    //  final  MyArtListView myFavoritesAdapter=new MyArtListView(this, myFavoritesMuralNames, myFavoritesArtistNames,myFavoritesimgid);
      final  MyArtListView myFoundAdapter=new MyArtListView(this, foundTitles, foundDescriptions,myFoundimgid);
      final  MyArtListView myCollabsAdapter=new MyArtListView(this, collabTitles, collabDescriptions,myCollabsimgid);

        // setOnClickListener for myFavorites
        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                    /* show myFavorites Listview */
               //     listView.setAdapter(myFavoritesAdapter);
                    myFavoritesShown = true;
                    btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN);
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


            }
        });


    }



}
