package com.example.elizabethlanglois.artspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseDatabase.getInstance().getReference("Users");
        Log.i("TEST", db.toString());

    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.btnaddart) {
            // Move to art art activity
            startActivity(new Intent(MainActivity.this, AddArt.class));
        } else if (id == R.id.btnviewart) {
            startActivity(new Intent(MainActivity.this,
                    LocationView.class).putExtra(LocationView.ART_ITEM_TAG, "ID"));
        } else if(id == R.id.btnmyart){
            startActivity(new Intent(MainActivity.this, MyArt.class));
        } else if(id == R.id.btndraw) {
            startActivity(new Intent(MainActivity.this, CanvasDrawing.class));
        } else if(id == R.id.btnexplore) {
            startActivity(new Intent(MainActivity.this, ExploreActivity.class));
        } else if(id == R.id.btnlogin){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
