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


    //for dragging
    Point sectorLocation; // the sector being moved
    int sectorType = 0; //the type of the sector being moved
    Sector draggedSector;

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
        if (event.getPointerCount() > 1){
            scaleGestureDetector().onTouchEvent(event);
            return true;
        }
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                Log.d("mapcreation", "action down");
                //dragged=false;
                //get the point we touched
                touchDown = new PointF(event.getX(), event.getY());
                sectorLocation = pixelToHex((int)touchDown.x,(int)touchDown.y);
                return true;
            }
            case MotionEvent.ACTION_MOVE:
            {
                Log.d("mapcreation", "action move");
                dragged = true;
                /*if(editing) {
                    //draw a hexagon? sector? around the person's finger
                    dragPath = getHexPath(scaledCellRadius(), event.getX(), event.getY());
                    draggedSector = sectors()[sectorLocation.x][sectorLocation.y];
                    setPaint(draggedSector.color());
                }*/
                if(editing)
                    drawDrag(event.getX(), event.getY());
                return true;
            }
            case MotionEvent.ACTION_UP: {
                Log.d("mapcreation", "action up. editing: " + editing + " dragged: " + dragged);
                if(editing && dragged){

                    //make the old sector invalid - gotta do this here not in action_down
                    //  because clicking is a thing - needs to be moved to action_move eventually
                    /*sectorType = sectors()[sectorLocation.x][sectorLocation.y].sectorType();
                    sectors()[sectorLocation.x][sectorLocation.y].setSectorType(Sector.INVALID);*/

                    //find the new sector
                    touchDown = new PointF(event.getX(), event.getY());
                    sectorLocation = pixelToHex((int)touchDown.x,(int)touchDown.y);

                    //set the sector where the finger is to be the sector type of the old sector
                    sectors()[sectorLocation.x][sectorLocation.y].setSectorType(sectorType);
                    resetDrag();
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
            //we are editing
            editing = true;
            //set everything up
            //find the sector we are dragging
            draggedSector = sectors()[sectorLocation.x][sectorLocation.y];
            //set the color
            setPaint(draggedSector.color());
            //get the sector type of the old sector
            sectorType = sectors()[sectorLocation.x][sectorLocation.y].sectorType();
            //set the old sector to be invalid
            sectors()[sectorLocation.x][sectorLocation.y].setSectorType(Sector.INVALID);
            //start drawing the drag
            drawDrag(event.getX(), event.getY());
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {
            editing = false;
            return super.onSingleTapConfirmed(event);
        }

    }

    private void setPaint(int color){

        dragPaint.setColor(getResources().getColor(color));
    }

    private void drawDrag(float centerX, float centerY){
        //get the path
        dragPath = getHexPath(scaledCellRadius()*1.25f, centerX, centerY);
        //draw the letter
    }

    private void resetDrag(){
        dragPath = null;
        editing = false;
        dragged = false;
    }
}
