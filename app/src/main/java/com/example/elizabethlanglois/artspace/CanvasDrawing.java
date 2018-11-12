package com.example.elizabethlanglois.artspace;

import java.util.Random;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.*;
import android.util.AttributeSet;
import android.view.*;
import android.content.Context;
import android.widget.LinearLayout;

public class CanvasDrawing extends AppCompatActivity {

    DrawingView drawingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        setTitle("Collaboration Canvas");

        drawingView = new DrawingView(this, null);

        LayoutInflater inflater = (LayoutInflater)getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout drawingLayout = (LinearLayout) inflater.inflate(R.layout.drawing_space, null);
        drawingLayout.addView(drawingView);

        LinearLayout drawingContainer = (LinearLayout) findViewById(R.id.canvasLayout);
        drawingContainer.addView(drawingLayout);


        //setContentView(drawingView);
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
    }
}
