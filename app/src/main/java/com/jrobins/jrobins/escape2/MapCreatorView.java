package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;


public class MapCreatorView extends MapView {
    //clicking
    //private OnCellClickListener listener;
    //private GestureDetector mGestureDetector;

    public MapCreatorView(Context context) {
        super(context);
    }

    public MapCreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //set the fillpaint to be black
        setFillPaint(Color.BLACK);
        //set wallpaint to be white
        setWallPaint(Color.GRAY);

        //set this to not be a static view
        //setMapThread(new MapDrawingThread(getHolder(),context, this, false));

        //initialize the pan thing
        //mGestureDetector = new GestureDetector(context, new GestureListener());

        //initialize with a blank default map
        initialize(new Map());
    }

/*
    //clicking
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mGestureDetector.onTouchEvent(event);
        return true;

    }*/



    public void changeSectorType(int column, int row)
    {
        sectors()[column][row].setSectorType(
                (sectors()[column][row].sectorType()+1)%3);
    }

    /*
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {


            if (listener != null) {
                float x = event.getX();
                float y = event.getY();

                int radius = cellWidth() / 2;
                int cellHeight = (int) (((float) radius) * Math.sqrt(3));
                int side = radius * 3 / 2;


                int ci = (int) Math.floor(x / (float) side);
                int cx = (int) (x - side * ci);

                int ty = (int) (y - (ci % 2) * cellHeight / 2);
                int cj = (int) Math.floor((float) ty / (float) cellHeight);
                int cy = ty - cellHeight * cj;

                if (cx > Math.abs(radius / 2 - radius * cy / cellHeight)) {
                    listener.onCellClick(ci, cj);
                } else {
                    listener.onCellClick(ci - 1, cj + (ci % 2) - ((cy < cellHeight / 2) ? 1 : 0));
                }

            }
            return true;
        }


    }*/
}
