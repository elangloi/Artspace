package com.example.elizabethlanglois.artspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.*;

import java.io.FileInputStream;

public class AddArt extends AppCompatActivity {

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_art);

        setTitle("Add Art");

        db = FirebaseDatabase.getInstance().getReference("Art_items");

        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object document = dataSnapshot.getValue();
                Log.i("db change", document.toString());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.i("db cancel", error.toString());
            }
        });

        // Cancel button return to main screen
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(AddArt.RESULT_CANCELED);
                finish();
            }
        });

        // Submit button should notify new art created and return to main screen
        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createArtItem();
                //TODO uncomment
                /*setResult(AddArt.RESULT_OK);
                finish();*/
            }
        });

        // Hide collab layout and uncheck by default
        // Show and hide the entry options for a collaboration
        final Switch btnCollab = (Switch) findViewById(R.id.swCollab);
        final LinearLayout layoutCollab = (LinearLayout) findViewById(R.id.layoutCollab);
        btnCollab.setChecked(false);
        layoutCollab.setVisibility(LinearLayout.GONE);
        btnCollab.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                layoutCollab.setVisibility(btnCollab.isChecked() ?
                        LinearLayout.VISIBLE : LinearLayout.GONE);
            }
        });

    }

    public void createArtItem() {
        // Retrieve basic information
        String title = ((TextView)findViewById(R.id.txtTitle)).getText().toString();
        String location = ((TextView)findViewById(R.id.txtLocation)).getText().toString();
        String description = ((TextView)findViewById(R.id.txtDescription)).getText().toString();

        ArtItem artItem = new ArtItem(title, location, description);

        if(((Switch) findViewById(R.id.swCollab)).isChecked()) {
            // Get collaboration details

            if(((Switch) findViewById(R.id.swInAppCollab)).isChecked()) {
                artItem.setType("drawing");
                // TODO Upload a blank image to firebase?
            } else {
                artItem.setType("collaboration");
            }

            String date = ((TextView)findViewById(R.id.txtDate)).getText().toString();
            String time = ((TextView)findViewById(R.id.txtTime)).getText().toString();
            String contact = ((TextView)findViewById(R.id.txtPhone)).getText().toString();

            artItem.setDate(date);
            artItem.setTime(time);
            artItem.setContact(contact);

        } else {
            artItem.setType("viewable");
        }

        // TODO fix firebase?? following code will not actually upload
        String id = db.push().getKey();
        db.child(id).setValue(artItem);
        Toast.makeText(this, "Art added!", Toast.LENGTH_SHORT).show();

    }
}
