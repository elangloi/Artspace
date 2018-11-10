package com.example.elizabethlanglois.artspace;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.graphics.PorterDuff;
import android.widget.Toast;




import java.util.ArrayList;


public class MyArt extends Activity {
    ListView listView ;
    boolean myFavoritesShown = false;
    boolean myFoundShown = false;
    boolean myCollabsShown = false;

    // Defined Array values to show in ListView
    String[] myFavoritesvalues = new String[] { "Tuttomondo - Keith Haring",
            "Nelson Mandela Mural - Shepard Fairey",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini",
            "Mural in Aspen - Christina Angelina",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini",
            "Mural in Aspen - Christina Angelina",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini",
            "Mural in Aspen - Christina Angelina"
    };
    String[] myFoundvalues = new String[] { "Fake - FakeData",
            "Nelson Mandela Mural - Shepard Fairey",
            "Etnias - Eduardo Kobra",
            "The Giant of Boston - Os Gemeos",
            "Mural in Maracay - Koz Dos",
            "The Golden Elephant - Dzia",
            "Along The Way - Alice Pasquini"
    };
    String[] myCollabvalues = new String[] { "Keith Haring",
            "Shepard Fairey",
            "Eduardo Kobra",
            "Os Gemeos",
            "Koz Dos",
            "Dzia",
            "Alice Pasquini",
            "Christina Angelina",
            "Eduardo Kobra",
            "Jimmy",
            "Roberta",
            "Phillip",
            "Jorge"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_art);
// get access to buttons
        final Button btnFavorites = (Button) findViewById(R.id.btnFavorites);
        final Button btnFound = (Button) findViewById(R.id.btnFound);
        final Button btnCollabs = (Button) findViewById(R.id.btnCollabs);

        // set buttons original background color
        btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
        btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
        btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        // MyFavorites fake data
        String[] myFavoritesMuralNames ={
                "Tuttomondo","Nelson Mandela Mural", "Etnias","The Giant of Boston",
                "Mural in Maracay", "Mural in Maracay","The Golden Elephant","Along The Way",
                "Mural in Aspen", "Tuttomondo","Nelson Mandela Mural", "Etnias",
                "The Giant of Boston", "Mural in Maracay", "Mural in Maracay","The Golden Elephant"
        };

        String[] myFavoritesArtistNames ={
                "Keith Haring","Eduardo Kobra", "Os Gemeos","Koz Dos", "Dzia", "Alice Pasquini",
                "Christina Angelina", "Eduardo Kobra", "Jimmy", "Roberta", "Phillip", "Jorge",
                "Keith Haring","Eduardo Kobra", "Os Gemeos","Koz Dos"
        };
        Integer[] myFavoritesimgid={
                R.drawable.monalisa,

        };
        // MyFound fake data
        String[] myFoundMuralNames ={
                "Tuttomondo","Nelson Mandela Mural", "Etnias","The Giant of Boston",
                "Mural in Maracay", "Mural in Maracay","The Golden Elephant","Along The Way",

        };

        String[] myFoundArtistNames ={
                "Keith Haring","Eduardo Kobra", "Os Gemeos","Koz Dos", "Dzia", "Alice Pasquini",
                "Christina Angelina", "Eduardo Kobra",
        };
        Integer[] myFoundimgid={
                R.drawable.monalisa,

        };
        // MyCollabs fake data
        String[] myCollabsMuralNames ={
                "Tuttomondo","Nelson Mandela Mural"
        };

        String[] myCollabsArtistNames ={
                "Keith Haring","Eduardo Kobra"
        };
        Integer[] myCollabsimgid={
                R.drawable.monalisa,

        };
        /* Define an adapter for each listView*/
      final  MyArtListView myFavoritesAdapter=new MyArtListView(this, myFavoritesMuralNames, myFavoritesArtistNames,myFavoritesimgid);
      final  MyArtListView myFoundAdapter=new MyArtListView(this, myFoundMuralNames, myFoundArtistNames,myFoundimgid);
      final  MyArtListView myCollabsAdapter=new MyArtListView(this, myCollabsMuralNames, myCollabsArtistNames,myCollabsimgid);

        // setOnClickListener for myFavorites
        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                    /* show myFavorites Listview */
                    listView.setAdapter(myFavoritesAdapter);
                    myFavoritesShown = true;
                    btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN);
            }
        });
        // setOnClickListener for myFound
        btnFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                listView.setAdapter(myFoundAdapter);
                btnFound.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN);


            }
        });
        // setOnClickListener for myCollabs
        btnCollabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnFavorites.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                btnFound.getBackground().setColorFilter(getResources().getColor(R.color.holo_blue_light),PorterDuff.Mode.SRC_IN);
                listView.setAdapter(myCollabsAdapter);
                btnCollabs.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent),PorterDuff.Mode.SRC_IN);


            }
        });


    }


}
