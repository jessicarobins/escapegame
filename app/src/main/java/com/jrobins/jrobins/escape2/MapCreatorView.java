package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class MapCreatorView extends MapView {
    //clicking
    //private OnCellClickListener listener;
    //private GestureDetector mGestureDetector;

    PointF touchDown; //to begin the dragging & dropping

    public MapCreatorView(Context context) {
        super(context);
    }

    public MapCreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //set the fillpaint to be black
        setFillPaint(Color.BLACK);
        //set wallpaint to be white
        setWallPaint(Color.GRAY);


        initialize(new Map());
    }





    public void changeSectorType(int column, int row)
    {
        sectors()[column][row].setSectorType(
                (sectors()[column][row].sectorType()+1)%3);
    }

/*
    private class DragAndDropListener extends GestureListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub

            Point sectorLocation;
            Point newPosition;


            switch(event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    touchDown = new PointF(event.getRawX(), event.getRawY());
                    break;
                }
                case MotionEvent.ACTION_MOVE:
                {
                    //RelativeLayout.LayoutParams par = (RelativeLayout.LayoutParams) v.getLayoutParams();

                    //find the sector
                    sectorLocation = getSectorFromTouchPoint((int)touchDown.x,(int)touchDown.y);

                    float yDeff = ((event.getRawY() - touchDown.y)   / cellRadius() ) * cellRadius();
                    float xDeff = ((event.getRawX() - touchDown.x)  / cellRadius() ) * cellRadius();

                    if(Math.abs(xDeff) >= cellRadius())
                    {
                        par.leftMargin += (int)(xDeff / gridCellSize) * gridCellSize;
                        touchDown.x = event.getRawX() - (xDeff % gridCellSize);
                    }

                    if(Math.abs(yDeff) >= gridCellSize)
                    {
                        par.topMargin += (int)(yDeff / gridCellSize) * gridCellSize;
                        touchDown.y = event.getRawY() - (yDeff % gridCellSize);
                    }

                    v.setLayoutParams(par);
                    break;
                }
                default :
                {

                    break;
                }
            }


            return true;
        }




    }
*/
}
