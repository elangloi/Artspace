package com.example.elizabethlanglois.artspace;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;
import android.graphics.Bitmap;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ExploreActivityView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> maintitle;
    private final ArrayList<String> subtitle;
    private final ArrayList<String> type;
    private final ArrayList<Bitmap> img;
    private final ArrayList<String> date;
    private final ArrayList<String> time;
    private final ArrayList<String> location;
    private final ArrayList<String> item_id;
    private final Integer[] a;

    private SharedPreferences sp;
    private String username;
    private DatabaseReference userDB;
    private String itemID;
    private ImageButton vFavorite;

    private TextView ResultsText;

    public ExploreActivityView(Activity context, ArrayList<String> maintitle,ArrayList<String> subtitle, ArrayList<Bitmap> img,
                               ArrayList<String> type,ArrayList<String> date,ArrayList<String> time,ArrayList<String> location,ArrayList<String> item_id,
                               Integer[] a) {
        super(context, R.layout.explore_item, maintitle);

        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.img=img;
        this.type = type;
        this.date = date;
        this.time = time;
        this.location = location;
        this.item_id = item_id;
        this.a = a;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.explore_item, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.artTitle);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.subtitle);
        TextView typeText = (TextView) rowView.findViewById(R.id.type);
        TextView dateTimeText = (TextView) rowView.findViewById(R.id.datetime);
        TextView locationText = (TextView) rowView.findViewById(R.id.location);
        vFavorite = rowView.findViewById(R.id.btnFavorite);

        if(!maintitle.get(0).equals("No results found")) {
            titleText.setText(maintitle.get(position));
            if (img.get(position) != null) {
                //imageView.setImageResource(a[0]);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(img.get(position));
            }
            subtitleText.setVisibility(View.VISIBLE);
            subtitleText.setText(subtitle.get(position));
            typeText.setVisibility(View.VISIBLE);
            vFavorite.setVisibility(View.INVISIBLE);
            typeText.setText(type.get(position));

            // Change depending on if location exists
            if (!location.get(position).equals("")) {
                locationText.setVisibility(View.VISIBLE);
                String upper = location.get(position).substring(0, 1).toUpperCase() + location.get(position).substring(1);
                locationText.setText("Location: " + upper);
            } else {
                locationText.setVisibility(View.GONE);
                locationText.setText("");
            }

            // Change text depending on whether date and time is present
            if (type.get(position).equals("Collaboration")) {
                dateTimeText.setVisibility(View.VISIBLE);
                if (date.get(position) != null && !date.get(position).equals("")) {
                    if (time.get(position) != null && !time.get(position).equals("")) {
                        dateTimeText.setText(date.get(position) + " at " + time.get(position));
                    } else {
                        dateTimeText.setText("" + date.get(position));
                    }
                } else {
                    if (time.get(position) != null && !time.get(position).equals("")) {
                        dateTimeText.setText("" + time.get(position));
                    } else {
                        dateTimeText.setText("Time information unavailable");
                    }
                }
            } else {
                dateTimeText.setVisibility(View.GONE);
                dateTimeText.setText("");
            }
        }else{
            titleText.setText(maintitle.get(position));

            imageView.setVisibility(View.GONE);
            imageView.setImageBitmap(img.get(position));

            locationText.setVisibility(View.GONE);
            subtitleText.setText("");

            locationText.setVisibility(View.GONE);
            typeText.setText("");

            locationText.setVisibility(View.GONE);
            locationText.setText("");

            dateTimeText.setVisibility(View.GONE);
            dateTimeText.setText("");

            vFavorite.setVisibility(View.GONE);
        }

        if(position %2 == 1)
        {
            // Set a background color for ListView regular row/item
            rowView.setBackgroundColor(Color.parseColor("#DCDCDC"));
        }else{
            rowView.setBackgroundColor(Color.parseColor("#778899"));
            titleText.setTextColor(Color.parseColor("#FFFFFF"));
            subtitleText.setTextColor(Color.parseColor("#FFFFFF"));

        }

        // Retrieve firebase favorite status of current user

        sp = getContext().getSharedPreferences(LoginActivity.MY_PREFS_NAME, Context.MODE_PRIVATE);
        username = sp.getString(LoginActivity.MY_USERNAME, null);
        itemID = item_id.get(position);

        // TODO Button doesnt change on the spot
        vFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username != null) {
                    userDB = FirebaseDatabase.getInstance().getReference("Users");
                    userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // If it is a favorite, remove it
                            // If it isn't a favorite, add it
                            UserItem currUser = dataSnapshot.child(username).getValue(UserItem.class);

                            if (currUser.favorites == null) {
                                currUser.favorites = new ArrayList<String>();
                            }

                            if (!currUser.favorites.contains(itemID)) {
                                // Add the favorite
                                currUser.favorites.add(itemID);

                                // Adjust the button text
                                //vFavorite.setText("Remove Favorite");
                                vFavorite.setImageResource(R.drawable.heart_clear);

                                // Show confirmatory toast
                                // TODO Toast does nothing
                                Toast.makeText(context, "Added to favorites!", Toast.LENGTH_SHORT);
                            } else {
                                // Remove the favorite
                                currUser.favorites.remove(itemID);

                                // Adjust the button text
                                //vFavorite.setText("Add to Favorites");
                                vFavorite.setImageResource(R.drawable.heart_color);

                                // Show confirmatory toast
                                Toast.makeText(context, "Removed from favorites!", Toast.LENGTH_SHORT);
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

        return rowView;

    };
}
