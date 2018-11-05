package com.example.elizabethlanglois.artspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

public class AddArt extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_art);

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
                //Intent resultIntent = new Intent();
                //resultIntent.putExtra("NAME OF THE PARAMETER", valueOfParameter);
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



    }
}
