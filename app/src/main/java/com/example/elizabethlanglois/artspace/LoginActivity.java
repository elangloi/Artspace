package com.example.elizabethlanglois.artspace;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity{

    Button b1;
    EditText username;
    EditText password;

    //placeholder database for now
    private String [] usernames = {"User1"};
    private String [] passwords = {"Pass1"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_login);

        b1 = (Button) findViewById(R.id.button1);
        username = (EditText)findViewById(R.id.editText1);
        password = (EditText)findViewById(R.id.editText2);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //placeholder check for now
                if(username.getText().toString().equals(usernames[0]) && password.getText().toString().equals(passwords[0])){
                    Log.d("Success", "Got through fine");
                    //correct password
                }else{
                    Log.d("Failure", "Failed");
                    //wrong password
                }
            }

        });
    }



}
