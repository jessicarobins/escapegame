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
    //Paint dragOutlinePaint = new Paint();


    //for dragging
    Point sectorLocation; // the sector being moved
    Point newSectorLocation; //the location of touch up, where the sector will be moved to if possible
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
        //dragOutlinePaint.setStyle(Paint.Style.STROKE);
        //dragOutlinePaint.setColor(Color.GRAY);
        //dragOutlinePaint.setStrokeWidth(5f);
        longPressListener = new GestureDetector(context, new LongPressListener());
        initialize(new Map());
    }

    @Override
    public void doDraw(Canvas canvas){
        super.doDraw(canvas);
        if(dragPath != null){
            //draw dragpath
            drawDrag(canvas);
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

        //we want no long presses by default so that we can scroll if a sector is not special
        //  or if it's not even in a sector
        longPressListener.setIsLongpressEnabled(false);

        //scaling
        if (event.getPointerCount() > 1){
            scaleGestureDetector().onTouchEvent(event);
            return true;
        }

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                //Log.d("mapcreation", "action down");
                //dragged=false;
                //get the point we touched
                touchDown = new PointF(event.getX(), event.getY());

                sectorLocation = pixelToHex((int)touchDown.x,(int)touchDown.y);

                //if it's a valid point, we find the sector and check if it's special
                //  if it's special, we set it to be longpressable
                if(isValidPoint(sectorLocation)){
                    //find the sector we are dragging
                    draggedSector = sectors()[sectorLocation.x][sectorLocation.y];
                    if (draggedSector.isSpecial()){
                        longPressListener.setIsLongpressEnabled(true);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE:
            {
                //Log.d("mapcreation", "action move");
                dragged = true;
                /*if(editing) {
                    //draw a hexagon? sector? around the person's finger
                    dragPath = getHexPath(scaledCellRadius(), event.getX(), event.getY());
                    draggedSector = sectors()[sectorLocation.x][sectorLocation.y];
                    setPaint(draggedSector.color());
                }*/
                if(editing) {
                    setDragPath(event.getX(), event.getY());
                    touchDown.set(event.getX(), event.getY());
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                //Log.d("mapcreation", "action up. editing: " + editing + " dragged: " + dragged);
                if(editing && dragged){


                    //find the new sector
                    //need to check if it's valid otherwise we'll just reset the original sector
                    touchDown.set(event.getX(), event.getY());
                    newSectorLocation = pixelToHex((int)touchDown.x,(int)touchDown.y);

                    //if we didn't drag the sector off the screen. we also shouldn't be able to drag
                    //  the sector onto another special sector
                    if(isValidPoint(newSectorLocation) && !sectorAt(newSectorLocation).isSpecial()) {
                        //set the sector where the finger is to be the sector type of the old sector
                        sectors()[newSectorLocation.x][newSectorLocation.y].setSectorType(sectorType);

                    }
                    else {
                        sectors()[sectorLocation.x][sectorLocation.y].setSectorType(sectorType);
                    }
                    resetDrag();
                }

                break;
            }

            default :
            {

                break;
            }
        }
        longPressListener.onTouchEvent(event);
        return true;


    }

    protected class LongPressListener extends GestureListener {
        @Override
        public void onLongPress(MotionEvent event){
            //we only want to be able to longpress/drag special sectors

            //if it's special, we do drag stuff. otherwise not.

            //we are editing
            editing = true;
            //set everything up

            //set the color
            setPaint(draggedSector.color());
            //get the sector type of the old sector
            sectorType = sectors()[sectorLocation.x][sectorLocation.y].sectorType();
            //set the old sector to be invalid
            sectors()[sectorLocation.x][sectorLocation.y].setSectorType(Sector.INVALID);
            //start drawing the drag
            setDragPath(event.getX(), event.getY());
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

    private void setDragPath(float centerX, float centerY){
        //set the path
        dragPath = getHexPath(scaledCellRadius()*1.25f, centerX, centerY);

    }

    private void drawDrag(Canvas canvas){
        canvas.drawPath(dragPath, dragPaint);
        canvas.drawPath(dragPath, wallPaint());
        drawSectorLabel(canvas, Sector.label(sectorType), scaledCellRadius()*2, touchDown.x, touchDown.y);

    }

    private void resetDrag(){
        dragPath = null;
        editing = false;
        dragged = false;
    }
}
