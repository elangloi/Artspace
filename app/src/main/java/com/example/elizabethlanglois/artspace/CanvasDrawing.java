package com.example.elizabethlanglois.artspace;

import java.util.Random;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.Button;

public class CanvasDrawing extends AppCompatActivity {

    DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        setTitle("Collaboration Canvas");

        drawingView = new DrawingView(this, null);

        // TODO Pull image from firebase and draw into current view rather than hardcoded image
        drawingView.setBackground(getResources().getDrawable(R.drawable.addicon));

        LayoutInflater inflater = (LayoutInflater)getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout drawingLayout = (LinearLayout) inflater.inflate(R.layout.drawing_space, null);
        drawingLayout.addView(drawingView);

        LinearLayout drawingContainer = (LinearLayout) findViewById(R.id.canvasLayout);
        drawingContainer.addView(drawingLayout);

        ((Button)findViewById(R.id.btnCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(AddArt.RESULT_CANCELED);
                finish();
            }
        });

        ((Button)findViewById(R.id.btnSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap drawing = drawingView.saveView();

                //TODO Upload new drawing to firebase

                setResult(AddArt.RESULT_OK);
                finish();
            }
        });
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
}
