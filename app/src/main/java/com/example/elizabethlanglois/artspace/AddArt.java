package com.example.elizabethlanglois.artspace;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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
import android.widget.ImageView;
import android.location.Geocoder;
import android.location.Address;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class AddArt extends AppCompatActivity {

    DatabaseReference db;
    private static final int CAMERA_REQUEST = 1888;
    ArtItem artItem;

    ImageView photoThumb;
    Button btnPhoto;

    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_art);

        setTitle("Add Art");

        photoThumb = ((ImageView)findViewById(R.id.imgLocation));
        photoThumb.setVisibility(ImageView.GONE);
        btnPhoto = ((Button)findViewById(R.id.btnGetLocation));
        btnPhoto.setVisibility(Button.VISIBLE);

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

        // Submit button should notify new art created and upload to firebase
        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(createArtItem()) {
                    //TODO Connect created art item to the user who is logged in

                    setResult(AddArt.RESULT_OK);
                    finish();
                }

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

        // Hide collab details, show drawing when the user wants to create a drawing
        // Show collab details, hide drawing when the user wants an in person collaboration
        final Switch btnDrawing = (Switch) findViewById(R.id.swInAppCollab);
        final LinearLayout layoutDraw = (LinearLayout) findViewById(R.id.layoutDraw);
        final LinearLayout layoutNonDraw = (LinearLayout) findViewById(R.id.layoutNonDraw);
        btnDrawing.setChecked(false);
        layoutDraw.setVisibility(LinearLayout.GONE);
        layoutNonDraw.setVisibility(LinearLayout.VISIBLE);
        btnDrawing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                layoutDraw.setVisibility(!btnDrawing.isChecked() ?
                    LinearLayout.GONE: LinearLayout.VISIBLE);
                layoutNonDraw.setVisibility(!btnDrawing.isChecked() ?
                    LinearLayout.VISIBLE : LinearLayout.GONE);
            }
        });

        // Allow users to take photos of a location
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkSelfPermission(android.Manifest.permission.CAMERA)
                        == getPackageManager().PERMISSION_DENIED) {
                    requestPermissions(new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST);
                } else {
                    // App has permissions
                    takePicture();
                }

            }
        });

        artItem = new ArtItem();

        geocoder = new Geocoder(getApplicationContext());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_REQUEST) {
            if (grantResults[0] == getPackageManager().PERMISSION_GRANTED) {
                // Permission is granted
                takePicture();
            } else {
                Toast.makeText(this, "This feature requires access to your camera!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean createArtItem() {
        // Retrieve basic information
        String title = ((TextView)findViewById(R.id.txtTitle)).getText().toString();
        String location = ((TextView)findViewById(R.id.txtLocation)).getText().toString();
        String description = ((TextView)findViewById(R.id.txtDescription)).getText().toString();

        // If any of the required fields are null or empty, show err
        if(title == null || title.isEmpty() || location == null || location.isEmpty()
                || description == null || description.isEmpty()) {
            Toast.makeText(this, "Please provide title, location, and description",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        artItem.title = title;
        artItem.location = location;
        artItem.description = description;

        try {
            // Try to pin location entered by user
            List<Address> locations = geocoder.getFromLocationName(location, 1);
            if(locations != null && !locations.isEmpty()) {
                Address addr = locations.get(0);
                artItem.latitude = addr.getLatitude();
                artItem.longitude = addr.getLongitude();
            }

        } catch (Exception ex) {
            // No location provided
            artItem.latitude = 0;
            artItem.longitude = 0;
        }

        if(((Switch) findViewById(R.id.swCollab)).isChecked()) {

            if(((Switch) findViewById(R.id.swInAppCollab)).isChecked()) {

                // Set drawing details
                artItem.setType("drawing");

                // Upload a blank image to firebase if user did not take a picture
                if(artItem.drawing == null) {
                    Bitmap newDrawing = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                    artItem.drawing = getImageData(newDrawing);
                }

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
        return true;

    }

    // Prep image encoding to be stored in firebase
    public String getImageData(Bitmap bmp) {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao);
        //bmp.recycle();

        // Background test with hard coded image
        // Bitmap testBackground = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.addicon);
        // testBackground.compress(Bitmap.CompressFormat.PNG, 100, bao);

        byte[] byteArray = bao.toByteArray();
        String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);

        return imageB64;
    }

    // Camera management
    private void takePicture() { //you can call this every 5 seconds using a timer or whenever you want
        Intent cameraIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == android.app.Activity.RESULT_OK) {

            Bitmap picture = (Bitmap) data.getExtras().get("data");

            // Show image and hide picture taking button
            photoThumb.setVisibility(ImageView.VISIBLE);
            btnPhoto.setVisibility(Button.GONE);

            photoThumb.setImageBitmap(picture);
            artItem.drawing = getImageData(picture);
        }
    }
}
