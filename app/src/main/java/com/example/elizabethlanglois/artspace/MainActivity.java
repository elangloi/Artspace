package com.example.elizabethlanglois.artspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        }
        return super.onOptionsItemSelected(item);
    }
}
