package com.example.elizabethlanglois.artspace;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.List;
import android.widget.TextView;
import android.widget.ListView;

import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;

public class ExploreActivity extends AppCompatActivity {

    private EditText LocationText,TitleText,DistanceInputText;
    private Button MinusButton,PlusButton,SearchButton;
    private CheckBox ViewArtBox,CollaborateArtBox;
    private ListView theList;

    private final List<Integer> ArtItems = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_art);

        LocationText = (EditText) findViewById(R.id.location);
        TitleText = (EditText) findViewById(R.id.title);

        MinusButton = (Button) findViewById(R.id.minus);
        DistanceInputText = (EditText) findViewById(R.id.distance);
        PlusButton = (Button) findViewById(R.id.plus);

        ViewArtBox = (CheckBox) findViewById(R.id.viewArtBox);
        CollaborateArtBox = (CheckBox) findViewById(R.id.collaborateArtBox);
        SearchButton = (Button) findViewById(R.id.search);

        theList = (ListView) findViewById(R.id.listView);

        setTitle("Explore Art");

        // On clicking the search button, search for results
        SearchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String location = LocationText.getText().toString();
                String title = TitleText.getText().toString();

                boolean viewArtChecked = ViewArtBox.isChecked();
                boolean collaborateArtChecked = CollaborateArtBox.isChecked();

                theList.setFooterDividersEnabled(true);

                // If view art is checked, add potential links to list
                if (viewArtChecked) {

                }

                // If collaborate on art is checked, add potential links to list
                if (collaborateArtChecked) {

                }

            }
        });

    }
}
