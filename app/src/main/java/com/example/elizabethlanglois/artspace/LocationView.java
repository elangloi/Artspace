package com.example.elizabethlanglois.artspace;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationView extends AppCompatActivity {

    public static final String ART_ITEM_TAG = "ART_ITEM_ID";

    private String itemID;

    DatabaseReference db;

    ArtItem artItem;

    TextView vTitle;
    TextView vDescription;
    TextView vLocation;
    LinearLayout vCollab;
    LinearLayout vInAppCollab;
    TextView vContact;
    TextView vDate;
    TextView vTime;
    ImageView vCanvas;
    Button vFavorite;

    SharedPreferences sp;
    String username;
    DatabaseReference userDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        setTitle("View Art");

        sp = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);

        ScrollView vScoll = findViewById(R.id.scrollview);
        RelativeLayout vErr = findViewById(R.id.layoutErr);

        // Retrieve art item ID from calling activity bundle
        Bundle b = getIntent().getExtras();
        if(b != null) {
            itemID = b.getString(ART_ITEM_TAG);
            vScoll.setVisibility(ScrollView.VISIBLE);
            vErr.setVisibility(RelativeLayout.GONE);

            //Find views
            vTitle = findViewById(R.id.title);
            vDescription = findViewById(R.id.description);
            vLocation = findViewById(R.id.location);
            vCollab = findViewById(R.id.collaboration);
            vInAppCollab = findViewById(R.id.inAppCollaboration);
            vContact = findViewById(R.id.contactPhone);
            vDate = findViewById(R.id.date);
            vTime = findViewById(R.id.time);
            vCanvas = findViewById(R.id.canvas);
            vFavorite = findViewById(R.id.btnFavorite);

            vFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Retrieve firebase favorite status of current user
                    username = sp.getString(LoginActivity.MY_USERNAME, null);
                    if(username != null) {
                        userDB = FirebaseDatabase.getInstance().getReference("Users");
                        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // If it is a favorite, remove it
                                // If it isn't a favorite, add it
                                UserItem currUser = dataSnapshot.child(username).getValue(UserItem.class);

                                if(currUser.favorites == null) {
                                    currUser.favorites = new ArrayList<String>();
                                }

                                if(!currUser.favorites.contains(itemID)) {
                                    // Add the favorite
                                    currUser.favorites.add(itemID);

                                    // Adjust the button text
                                    vFavorite.setText("Remove Favorite");

                                    // Show confirmatory toast
                                    Toast.makeText(getApplicationContext(), "Added to favorites!", Toast.LENGTH_SHORT);
                                } else {
                                    // Remove the favorite
                                    currUser.favorites.remove(itemID);

                                    // Adjust the button text
                                    vFavorite.setText("Add to Favorites");

                                    // Show confirmatory toast
                                    Toast.makeText(getApplicationContext(), "Removed from favorites!", Toast.LENGTH_SHORT);
                                }

                                // Update the new value in firebase
                                userDB.child(username).setValue(currUser);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Do nothing
                            }
                        });
                    }

                }
            });

            populateLayout();
        } else {
            vScoll.setVisibility(ScrollView.GONE);
            vErr.setVisibility(RelativeLayout.VISIBLE);
        }
    }

    protected void populateLayout() {

        // Connect to firebase to retrieve values associated with artitem
        db = FirebaseDatabase.getInstance().getReference("Art_items").child(itemID);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                artItem = dataSnapshot.getValue(ArtItem.class);

                vTitle.setText(artItem.title);
                vDescription.setText(artItem.description);
                vLocation.setText(artItem.location);

                // Show fields for a collaboration
                if(artItem.type.equals("collaboration")) {
                    vCollab.setVisibility(LinearLayout.VISIBLE);
                    // Show contact, date, time
                    vContact.setText(artItem.contact);
                    vDate.setText(artItem.date);
                    vTime.setText(artItem.time);
                } else {
                    vCollab.setVisibility(LinearLayout.GONE);
                }

                // Show fields for an in-app collaboration
                if(artItem.type.equals("drawing")) {
                    vInAppCollab.setVisibility(LinearLayout.VISIBLE);

                    // Get image encoding from firebase and format as drawing
                    Bitmap drawing = getImageFromData(artItem.drawing);
                    vCanvas.setImageBitmap(drawing);

                    // On image click, allow image to be edited
                    // Start canvasdrawing with the art item id
                    vCanvas.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(LocationView.this,
                                    CanvasDrawing.class).putExtra(LocationView.ART_ITEM_TAG, itemID));
                        }
                    });

                } else {
                    vInAppCollab.setVisibility(LinearLayout.GONE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("firebase err", "loadPost:onCancelled", databaseError.toException());
            }
        };
        db.addValueEventListener(postListener);

    }

    // Rebuild bitmap from firebase image string
    public static Bitmap getImageFromData(String bytes) {
        byte[] decodedString = Base64.decode(bytes.getBytes(), Base64.URL_SAFE);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
}
