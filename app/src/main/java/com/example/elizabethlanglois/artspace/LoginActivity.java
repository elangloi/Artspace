package com.example.elizabethlanglois.artspace;
import android.content.SharedPreferences;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity{

    private DatabaseReference db;
    private Button b1, b2;
    private EditText mUsername;


    public static final String MY_PREFS_NAME = "MyPrefsFile";

    // key for above preference.
    public static final String MY_USERNAME = "Username";

    private SharedPreferences sp;

    private UserItem user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_login);

        b1 = (Button) findViewById(R.id.button1);
        mUsername = (EditText)findViewById(R.id.editText1);

        db = FirebaseDatabase.getInstance().getReference("Users");
        user = new UserItem();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //placeholder check for now
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                if(mUsername.length() > 0){
                    editor.putString(MY_USERNAME, mUsername.getText().toString());
                    editor.apply();
                }

            }
        });

        b2 = (Button) findViewById(R.id.btnRegister);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.username = mUsername.getText().toString();
                db.child(user.username).setValue(user);
                //create firebase and then sharedPreference

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                if(mUsername.length() > 0){
                    editor.putString(MY_USERNAME, mUsername.getText().toString());
                    editor.apply();
                }



            }
        });
    }



}
