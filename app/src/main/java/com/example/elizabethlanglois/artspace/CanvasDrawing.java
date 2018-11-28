package com.example.elizabethlanglois.artspace;

import java.io.ByteArrayOutputStream;
import java.nio.Buffer;
import java.util.Random;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.*;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CanvasDrawing extends AppCompatActivity {

    DrawingView drawingView;
    private String itemID;
    DatabaseReference db;
    String imageEncoding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        setTitle("Collaboration Canvas");

        // Note that this activity requires an art item id and an existing drawing
        Bundle b = getIntent().getExtras();
        if(b == null) {
            // Uncomment to work with test ID
            // itemID = "-LSQgGgZYCmrn1bDZl6C";

            Toast.makeText(getApplicationContext(), "Sorry, we couldn't find that collaboration :(", Toast.LENGTH_LONG).show();
            setResult(AddArt.RESULT_CANCELED);
            finish();
        } else {
            itemID = b.getString(LocationView.ART_ITEM_TAG);

            drawingView = new DrawingView(this, null);

            // Get encoded image that users have collaborated on
            db = FirebaseDatabase.getInstance().getReference("Art_items");
            db.child(itemID).child("drawing").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String drawingEncoding = dataSnapshot.getValue(String.class);
                    imageEncoding = drawingEncoding;

                    // Pull image from firebase and draw into current view
                    Bitmap oldImage = getImageFromData(imageEncoding);
                    drawingView.setBackground(new BitmapDrawable(getResources(), oldImage));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("Firebase err", databaseError.toString());
                }
            });

            // Prepare for drawing
            LayoutInflater inflater = (LayoutInflater)getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout drawingLayout = (LinearLayout) inflater.inflate(R.layout.drawing_space, null);
            drawingLayout.addView(drawingView);

            LinearLayout drawingContainer = (LinearLayout) findViewById(R.id.canvasLayout);
            drawingContainer.addView(drawingLayout);

            // Handle cancel button
            ((Button)findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(AddArt.RESULT_CANCELED);
                    finish();
                }
            });

            // Handle submit button
            ((Button)findViewById(R.id.btnSubmit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Upload new drawing to firebase under existing art item
                    Bitmap drawing = drawingView.saveView();
                    String encodedDrawing = getImageData(drawing);

                    db.child(itemID).child("drawing").setValue(encodedDrawing);
                    Toast.makeText(CanvasDrawing.this, "Collaboration uploaded!", Toast.LENGTH_SHORT).show();

                    setResult(AddArt.RESULT_OK);
                    finish();
                }
            });
        }

    }

    public class DrawingView extends View {

        Paint paint;
        Path path;
        Random rand;

        public DrawingView(Context c, AttributeSet a) {

            super(c, a);

            paint = new Paint();
            path = new Path();
            rand = new Random();

            paint.setAntiAlias(true);
            changeColor();
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10f);

        }

        private void changeColor() {
            switch(rand.nextInt(4)) {
                case 1:
                    paint.setColor(Color.BLUE);
                    break;
                case 2:
                    paint.setColor(Color.RED);
                    break;
                case 3:
                    paint.setColor(Color.GREEN);
                    break;
                case 4:
                    paint.setColor(Color.YELLOW);
                    break;
                case 5:
                    paint.setColor(Color.BLACK);
                    break;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float xPos = event.getX();
            float yPos = event.getY();

            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(xPos,yPos);
                    return true;

                case MotionEvent.ACTION_MOVE:
                    path.lineTo(xPos,yPos);
                    break;

                case MotionEvent.ACTION_UP:
                    break;

                default:
                    return false;
            }

            invalidate();
            return true;
        }

        // Bulid new bitmap using existing image and current changes
        public Bitmap saveView() {
            Bitmap drawing = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas bindingCanvas = new Canvas(drawing);
            Drawable bgDrawable = getBackground();
            if(bgDrawable != null) {
                bgDrawable.draw(bindingCanvas);
            } else {
                bindingCanvas.drawColor(Color.WHITE);
            }
            draw(bindingCanvas);
            return drawing;
        }
    }

    // Rebuild bitmap from firebase image
    public static Bitmap getImageFromData(String bytes) {

        Log.i("from firebase", bytes);

        byte[] decodedString = Base64.decode(bytes.getBytes(), Base64.URL_SAFE);

        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        //Log.i("test", decodedString.toString());

        return decodedByte;

    }

    // Build encoded string from bitmap for firebase
    public String getImageData(Bitmap bmp) {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        bmp.compress(Bitmap.CompressFormat.PNG, 100, bao); // bmp is bitmap from user image file
        bmp.recycle();

        // Background test with hard coded image
        // Bitmap testBackground = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.addicon);
        // testBackground.compress(Bitmap.CompressFormat.PNG, 100, bao);


        byte[] byteArray = bao.toByteArray();
        String imageB64 = Base64.encodeToString(byteArray, Base64.URL_SAFE);
        return imageB64;
    }
}
