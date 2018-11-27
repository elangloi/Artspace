package com.example.elizabethlanglois.artspace;

import android.graphics.Bitmap;
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
import android.util.Base64;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.*;

import java.io.ByteArrayOutputStream;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class AddArt extends AppCompatActivity {

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_art);

        setTitle("Add Art");

        db = FirebaseDatabase.getInstance().getReference("Art_items");

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

                //TODO Connect created art item to the user who is logged in

                setResult(AddArt.RESULT_OK);
                finish();
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

        // Hide collab details when the user wants to create a drawing
        final Switch btnDrawing = (Switch) findViewById(R.id.swInAppCollab);
        final LinearLayout layoutNonDraw = (LinearLayout) findViewById(R.id.layoutNonDraw);
        btnDrawing.setChecked(false);
        layoutNonDraw.setVisibility(LinearLayout.VISIBLE);
        btnDrawing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutNonDraw.setVisibility(btnDrawing.isChecked() ?
                    LinearLayout.GONE: LinearLayout.VISIBLE);
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

            if(((Switch) findViewById(R.id.swInAppCollab)).isChecked()) {

                // Set drawing details
                artItem.setType("drawing");

                // Upload a blank image to firebase
                Bitmap newDrawing = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                artItem.drawing = getImageData(newDrawing);
            } else {

                // Set collaboration details
                artItem.setType("collaboration");

                String date = ((TextView)findViewById(R.id.txtDate)).getText().toString();
                String time = ((TextView)findViewById(R.id.txtTime)).getText().toString();
                String contact = ((TextView)findViewById(R.id.txtPhone)).getText().toString();

                artItem.setDate(date);
                artItem.setTime(time);
                artItem.setContact(contact);
            }

        } else {
            artItem.setType("viewable");
        }

        // Upload details to firebase
        String id = db.push().getKey();
        db.child(id).setValue(artItem);
        Toast.makeText(this, "Art added!", Toast.LENGTH_SHORT).show();

    }

    public static String getImageData(Bitmap bmp) {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        bmp.recycle();
        byte[] byteArray = bao.toByteArray(); //bYtE.toByteArray();
        String imageB64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        //  store & retrieve this string to firebase
        return imageB64;
    }
}
