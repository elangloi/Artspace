package com.example.elizabethlanglois.artspace;
import android.content.SharedPreferences;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.text.Spanned;
import android.text.InputFilter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends AppCompatActivity{

    private DatabaseReference db;
    private Button b1, b2;
    private EditText mUsername, mPassword;


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
        mPassword = (EditText)findViewById(R.id.editText2);

        mUsername.setFilters(new InputFilter[] { usernameFilter });
        mPassword.setFilters(new InputFilter[] { passwordFilter });

        db = FirebaseDatabase.getInstance().getReference("Users");
        user = new UserItem();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mUsername.length() > 0 && mPassword.length() > 0) {


                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot i : dataSnapshot.getChildren()) {
                                UserItem userfound = i.getValue(UserItem.class);
                                try {
                                    if (userfound.username.equals(mUsername.getText().toString()) && userfound.password != null && userfound.password.equals(SHA1(mPassword.getText().toString()))) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "You're logged in!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        Intent k = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(k);
                                        return;
                                    }
                                } catch (Exception e){
                                    Log.i("DebugLog","Error hashing!");
                                    return;
                                }
                            }
                            Toast toast = Toast.makeText(getApplicationContext(), "not registered or wrong password!", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("DebugLog", "firebase failed to get data");
                        }
                    });

                    //placeholder check for now
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();

                    editor.putString(MY_USERNAME, mUsername.getText().toString());
                    editor.apply();
                }

            }
        });

        b2 = (Button) findViewById(R.id.btnRegister);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUsername.length() > 0 && mPassword.length() > 0){

                    db.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot i : dataSnapshot.getChildren()) {
                                UserItem userfound = i.getValue(UserItem.class);
                                if (userfound.username.equals(mUsername.getText().toString())) {
                                        Toast toast = Toast.makeText(getApplicationContext(), "username already exists!", Toast.LENGTH_SHORT);
                                        toast.show();
                                        return;
                                }
                            }
                            try {
                                user.username = mUsername.getText().toString();
                                user.password = SHA1(mPassword.getText().toString());
                                db.child(user.username).setValue(user);
                                //create firebase and then sharedPreference

                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString(MY_USERNAME, mUsername.getText().toString());
                                editor.apply();

                                Toast toast = Toast.makeText(getApplicationContext(), "Registered as " + user.username, Toast.LENGTH_LONG);
                                toast.show();

                            } catch (Exception e) {
                                Log.i("DebugLog", "Error hashing!");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.i("DebugLog", "firebase failed to get data");
                        }

                    });
                }
            }
        });
    }



    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    InputFilter usernameFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {

            for (int i = start;i < end;i++) {
                if (!Character.isLetterOrDigit(source.charAt(i)) &&
                        !Character.toString(source.charAt(i)).equals("-"))
                {
                    return "";
                }
            }
            return null;
        }
    };

    InputFilter passwordFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start,
                                   int end, Spanned dest, int dstart, int dend) {

            for (int i = start;i < end;i++) {
                if (Character.toString(source.charAt(i)).equals(" "))
                {
                    return "";
                }
            }
            return null;
        }
    };

}