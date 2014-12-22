package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class MapCreatorView extends MapView {
    //clicking
    //private OnCellClickListener listener;
    //private GestureDetector mGestureDetector;

    //dragging & dropping

    PointF touchDown; //to begin the dragging & dropping
    boolean dragged = false; //this is if we are drag and dropping. otherwise we are clicking.
    boolean editing = false;
    GestureDetector longPressListener;
    Path dragPath;
    Paint dragPaint = new Paint();

    public MapCreatorView(Context context) {
        super(context);
    }

    public MapCreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //set the fillpaint to be black
        setFillPaint(Color.BLACK);
        //set wallpaint to be white
        setWallPaint(Color.GRAY);
        dragPaint.setStyle(Paint.Style.FILL);
        longPressListener = new GestureDetector(context, new LongPressListener());
        initialize(new Map());
    }

    @Override
    public void doDraw(Canvas canvas){
        super.doDraw(canvas);
        if(dragPath != null){
            //draw dragpath
            canvas.drawPath(dragPath, dragPaint);
        }
    }



    public void changeSectorType(int column, int row)
    {
        sectors()[column][row].setSectorType(
                (sectors()[column][row].sectorType()+1)%3);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //super.onTouchEvent(event);
        longPressListener.onTouchEvent(event);
        Point sectorLocation;

        int sectorType = 0; //the type of the sector being moved


        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                Log.d("mapcreation", "action down");
                dragged=false;
                //get the point we touched
                touchDown = new PointF(event.getX(), event.getY());

                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                Log.d("mapcreation", "action move");
                dragged = true;



                //draw a hexagon? sector? around the person's finger
                dragPath = getHexPath(cellWidth(), event.getX(), event.getY());
                return true;
            }
            case MotionEvent.ACTION_UP: {
                Log.d("mapcreation", "action up. editing: " + editing + " dragged: " + dragged);
                if(editing && dragged){
                    //find location of old sector
                    sectorLocation = pixelToHex((int)touchDown.x,(int)touchDown.y);
                    //make that sector invalid - gotta do this here not in action_down
                    //  because clicking is a thing - actually maybe we can even do it in action_up?
                    //  no that would still be clicking.
                    sectorType = sectors()[sectorLocation.x][sectorLocation.y].sectorType();
                    sectors()[sectorLocation.x][sectorLocation.y].setSectorType(Sector.INVALID);

                    //find the new sector

                    touchDown = new PointF(event.getX(), event.getY());
                    sectorLocation = pixelToHex((int)touchDown.x,(int)touchDown.y);

                    //set the sector where the finger is to be the sector type of the old sector
                    sectors()[sectorLocation.x][sectorLocation.y].setSectorType(sectorType);
                    editing = false;
                    dragged = false;
                }

                return true;
            }

            default :
            {

                return true;
            }
        }

    }

    protected class LongPressListener extends GestureListener {
        @Override
        public void onLongPress(MotionEvent event){
            editing = true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            editing = false;
            return super.onSingleTapConfirmed(event);
        }

    }

    private void setPaint(int color){

        dragPaint.setColor(color);
    }

}
