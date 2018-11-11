package com.example.elizabethlanglois.artspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class LocationView extends AppCompatActivity {

    public static final String ART_ITEM_TAG = "ART_ITEM_ID";

    private String itemID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_view);

        ScrollView vScoll = findViewById(R.id.scrollview);
        RelativeLayout vErr = findViewById(R.id.layoutErr);

        // Retrieve art item ID from calling activity bundle
        Bundle b = getIntent().getExtras();
        if(b != null) {
            itemID = b.getString(ART_ITEM_TAG);
            vScoll.setVisibility(ScrollView.VISIBLE);
            vErr.setVisibility(RelativeLayout.GONE);
            populateLayout();
        } else {
            vScoll.setVisibility(ScrollView.GONE);
            vErr.setVisibility(RelativeLayout.VISIBLE);
        }
    }

    protected void populateLayout() {

        // TODO Connect to firebase to retrieve values associated with artitem
        String title = "Fish Mural";
        String location = "Ocean";
        String description = "Wow fish paintings!";
        boolean collab = true;
        String contactNumber = "410-123-4321";
        String date = "12/25/2018";
        String time = "11:00:AM";
        boolean inApp = true;
        // TODO get image encoding and format as drawing?

        // Retrieve views and populate
        TextView vTitle = findViewById(R.id.title);
        TextView vDescription = findViewById(R.id.description);
        TextView vLocation = findViewById(R.id.location);
        LinearLayout vCollab = findViewById(R.id.collaboration);
        LinearLayout vInAppCollab = findViewById(R.id.inAppCollaboration);

        vTitle.setText(title);
        vDescription.setText(description);
        vLocation.setText(location);

        if(collab) {
            vCollab.setVisibility(LinearLayout.VISIBLE);

            // Show contact, date, time
            TextView vContact = findViewById(R.id.contactPhone);
            TextView vDate = findViewById(R.id.date);
            TextView vTime = findViewById(R.id.time);

            vContact.setText(contactNumber);
            vDate.setText(date);
            vTime.setText(time);
        } else {
            vCollab.setVisibility(LinearLayout.GONE);
        }


        if(inApp) {
            vInAppCollab.setVisibility(LinearLayout.VISIBLE);

            // TODO Show image with sticker addition options
            ImageView vCanvas = findViewById(R.id.canvas);
        } else {
            vInAppCollab.setVisibility(LinearLayout.GONE);
        }


    }
}
