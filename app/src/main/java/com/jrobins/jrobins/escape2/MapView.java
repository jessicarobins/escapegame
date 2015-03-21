package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ScrollView;

import java.util.ArrayList;

public class MapView extends BasicHexagonGridView {

    //some helpful constants
    final public int UNSET = 0;
    final public int CERTAIN = 1;
    final public int GUESS = 2;

    //the max number of moves in a row per hexagon
    private final int MAX_MOVES = 3;

    //min size a move square can be in order to put the text in
    private final int MIN_MOVE_TEXT_SIZE = 18;

    //paints!
    private Paint textPaint = new Paint();
    private Paint labelPaint = new Paint();


    Context context;
    private float moveWidth;


    //private Sector[][] sectors;

    //rectangles! for drawing things the right size
    RectF rectf = new RectF();
    RectF moveSquare = new RectF();
    Rect rect = new Rect();

    //clicking
    private OnCellClickListener listener;


    //zooming
    private ScaleGestureDetector mScaleDetector;


    private Matrix drawMatrix;
    private Matrix tempMatrix;
    private float saveScale = 1.f;
    final private float MIN_SCALE = .75f;
    final private float MAX_SCALE = 8.f;
    float lastFocusX;
    float lastFocusY;
    //float origWidth;
    //float origHeight;
    float canvasWidth;
    float canvasHeight;

    private float originX = 0f; // current position of viewport
    private float originY = 0f;


    //panning
    private GestureDetector mGestureDetector;
    private float offsetX = 0f;
    private float offsetY = 0f;




    public MapView(Context context)
    {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        //wallpaint, fillpaint, cellcolor taken care of in basichexagongridview



        //for sector names that aren't special sector types
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        // text shadow
        //textPaint.setShadowLayer(1f, 0f, 1f, Color.LTGRAY);

        textPaint.setTextSize(20);

        //for special sector types (e.g., H, E, A)
        labelPaint.setStyle(Paint.Style.FILL);
        labelPaint.setColor(Color.BLACK);
        labelPaint.setAntiAlias(true);
        labelPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        // text shadow
        //textPaint.setShadowLayer(1f, 0f, 1f, Color.LTGRAY);
        labelPaint.setTextSize(20);


        drawMatrix = new Matrix();
        tempMatrix = new Matrix();
        //initialize zoom thing
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        //initialize the pan thing
        mGestureDetector = new GestureDetector(context, new GestureListener());

        //threads
        setMapThread(new MapDrawingThread(getHolder(),context, this, false));

        //origHeight = getHeight();
        //origWidth = getWidth();
    }

    public void newThread(){
        if (!mapThread.isAlive()) {
            mapThread = new MapDrawingThread(getHolder(), context, this, false);
        }
    }

    public GestureDetector gestureDetector(){
        return mGestureDetector;
    }

    public ScaleGestureDetector scaleGestureDetector(){
        return mScaleDetector;
    }



    public int scaledCellRadius(){
        return (int)(super.cellRadius()*saveScale);
    }

    public OnCellClickListener listener(){
        return listener;
    }

    /*****surfaceview thing******/
    @Override
    public void doDraw(Canvas canvas){
        super.doDraw(canvas);
        canvas.save();

        //canvas.translate(originX, originY);
        //canvas.scale(mScaleFactor, mScaleFactor, originX, originY);
        //canvas.scale(mScaleFactor, mScaleFactor);
        //canvas.translate(offsetX, offsetY);
        canvas.setMatrix(drawMatrix);
        setCellWidth(cellWidth());
        moveWidth = cellWidth()/7;

        //not sure if we need to do this again because we are doing it in the superclass
        //i think we do though
        canvas.drawColor(getResources().getColor(R.color.map_background));
        drawGridWithZigZagRows(canvas);


        canvas.restore();

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();

    }


    //only clickable for actual maps so this stays

    public interface OnCellClickListener
    {
        public void onCellClick(int column, int row);
    }

    public void setOnCellClickListener(OnCellClickListener listener)
    {
        this.listener = listener;
    }


    public void setCell(int column, int row, Move m)
    {
        Move existingMove;
        int i = sectors()[column][row].moves().indexOf(m);
        //if this move already exists
        if (i>=0){
            existingMove = sectors()[column][row].moves().get(i);

            //get the certainty
            switch(existingMove.certainty()) {
                case Move.UNCERTAIN: {//if it's uncertain, make it certain
                    existingMove.setCertainty(Move.CERTAIN);
                    break;
                }
                case Move.CERTAIN: {
                    //if it's uncertain, remove it
                    removeMove(column, row, existingMove);
                    break;
                    //map.sectors()[column][row].moves().get(i).incrementCertainty();
                }
            }
        }
        else {
            //add a new move
            //sectors()[column][row].moves().add(m);
            super.addMove(column, row, m);
        }

    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        labelPaint.setTextSize(cellWidth()/2);
        moveWidth = cellWidth()/7;
    }



    /****** touch events stay *************************/
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        mGestureDetector.onTouchEvent(event);
        if (event.getPointerCount() > 1){
            mScaleDetector.onTouchEvent(event);
            return true;
        }
        return true;

    }


    /****** are we drawing the sector names here or in basichexagongridview?*******/
    private void drawSectorName(Canvas canvas, String sectorName, float centerX, float centerY){
        //Rect textBounds;

        //float x,y;
        //textBounds = new Rect();
        textPaint.setTextSize(cellWidth()/5);
        textPaint.setColor(Color.BLACK);
        textPaint.clearShadowLayer();
        textPaint.getTextBounds(sectorName, 0, sectorName.length(), rect);
        //x = centerX - rect.exactCenterX();
        //y = centerY - rect.exactCenterY();
        canvas.drawText(sectorName, centerX - rect.exactCenterX(), centerY - rect.exactCenterY(), textPaint);
    }

    private void drawSectorLabel(Canvas canvas, String label, float centerX, float centerY){
        //Rect textBounds;

        //float x,y;
        //textBounds = new Rect();
        labelPaint.setTextSize(cellWidth()/2);
        labelPaint.getTextBounds(label, 0, label.length(), rect);
        //x = centerX - rect.exactCenterX();
        //y = centerY - rect.exactCenterY();
        canvas.drawText(label, centerX - rect.exactCenterX(), centerY - rect.exactCenterY(), labelPaint);
    }

    public void drawSectorLabel(Canvas canvas, String label, float cellWidth, float centerX, float centerY){
        //Rect textBounds;

        //float x,y;
        //textBounds = new Rect();
        labelPaint.setTextSize(cellWidth/2);
        labelPaint.getTextBounds(label, 0, label.length(), rect);
        //x = centerX - rect.exactCenterX();
        //y = centerY - rect.exactCenterY();
        canvas.drawText(label, centerX - rect.exactCenterX(), centerY - rect.exactCenterY(), labelPaint);
    }


    private void drawMoves(Canvas canvas, ArrayList<Move> moves){
        //hexagon is combPath
        //probably need to compute the bounds...
        combPath().computeBounds(rectf, true);

        //these are the coordinates where the grid should start
        //  so x is (MAX_MOVES/2) moves left of center and y is right on center
        float gridStartX;
        int movesInRow;// = Math.min(moves.size(), MAX_MOVES);
        float localMoveWidth = 0;
        float localMoveYOffset = 0;
        switch(moves.size()){
            case 1: {
                localMoveWidth = (int)(cellWidth()/2.5);
                localMoveYOffset = localMoveWidth/4;
                movesInRow = 1;
                break;
            }
            case 2:  {
                localMoveWidth = (int)(cellWidth() / 3.5);
                localMoveYOffset = localMoveWidth/4;
                movesInRow = 2;
                break;
            }
            case 3: {
                localMoveWidth = cellWidth() / 4;
                movesInRow = 3;
                break;
            }
            case 4:{
                localMoveWidth = cellWidth() / 4;
                movesInRow = 2;
                break;
            }
            case 5:case 6: {
                localMoveWidth = cellWidth() / 5;
                movesInRow = 3;
                break;
            }
            default: {
                localMoveWidth = moveWidth;
                movesInRow = MAX_MOVES;
                break;
            }
        }

        if(movesInRow % 2 == 0)
            gridStartX = rectf.centerX()-(movesInRow/2)*localMoveWidth + .5f*localMoveWidth;
        else
            gridStartX = rectf.centerX()-(movesInRow/2)*localMoveWidth;

        float gridStartY = rectf.centerY() + localMoveYOffset;

        //the max # of moves in a row is now a class-level
        //  constant MAX_MOVES and equal to 4



        //need a for loop with a yoffset for creating rows
        int yOffset = 0;

        for(int i = 0; i<moves.size(); i++){
            //check that we haven't reached the max number of moves in
            //  a row
            if(i > 0 && i%movesInRow==0)
                yOffset+=localMoveWidth;



            //draw the shape in the right place
            //that'll be (i%MAX_MOVES)*moveWidth + gridStartX and then gridStartY+yOffset
            drawMove(canvas, moves.get(i), (i % movesInRow) * localMoveWidth + gridStartX, gridStartY + yOffset, localMoveWidth);


        }
        rectf.round(rect);
        //invalidate(rect);
    }

    private void drawMove(Canvas canvas, Move move, float centerX, float centerY){

        //get rectangle
        setMoveSquare(centerX, centerY);

        //set the fillpaint to be the player color
        fillPaint().setColor(move.color());
        wallPaint().setStrokeWidth(1f);
        //draw rectangle
        canvas.drawRect(moveSquare, fillPaint());
        canvas.drawRect(moveSquare, wallPaint());
        if(moveWidth>MIN_MOVE_TEXT_SIZE)
            drawTextInMoveSquare(canvas, move);


    }



    private void drawMove(Canvas canvas, Move move, float centerX, float centerY, float size){

        //get rectangle
        setMoveSquare(centerX, centerY, size);

        //set the fillpaint to be the player color
        fillPaint().setColor(move.color());
        wallPaint().setStrokeWidth(1f);

        //if the move is not a guess, draw a square
        if(move.certainty() == Move.CERTAIN) {
            //draw rectangle
            canvas.drawRect(moveSquare, fillPaint());
            canvas.drawRect(moveSquare, wallPaint());
        }
        else {
            fillPaint().setAlpha(100);
            canvas.drawCircle(centerX, centerY, size / 2, fillPaint());
            canvas.drawCircle(centerX, centerY, size / 2, wallPaint());
            fillPaint().setAlpha(255);
        }

        if(size>10)
            drawTextInMoveSquare(canvas, move, size);
    }

    private void setMoveSquare(float centerX, float centerY, float size){
        moveSquare.set(centerX - size / 2, centerY - size / 2, centerX + size / 2, centerY + size / 2);
    }

    private void drawTextInMoveSquare(Canvas canvas, Move move, float sizeOfMove){
        //find the right text color - this is certainty color, should be text

        /* doing certainty a different way
        int certainty = move.certainty();
        switch (certainty) {
            case 0: textPaint.setColor(getResources().getColor(R.color.bluff));
                break;
            case 1: textPaint.setColor(getResources().getColor(R.color.certain));
                break;
            case 2: textPaint.setColor(getResources().getColor(R.color.uncertain));
                break;
        }*/
        //draw number inside rectangle
        textPaint.setTextSize((int) (sizeOfMove / 1.5));

        textPaint.getTextBounds(move.turnNumberToString(), 0, move.turnNumberToString().length(), rect);
        float x = moveSquare.centerX() - rect.exactCenterX();
        float y = moveSquare.centerY() - rect.exactCenterY();
        canvas.drawText(move.turnNumberToString(), x,y, textPaint);
    }

    private void drawTextInMoveSquare(Canvas canvas, Move move){
        //find the right text color - this is certainty color, should be text

        /* doing certainty a different way
        int certainty = move.certainty();
        switch (certainty) {
            case 0: textPaint.setColor(getResources().getColor(R.color.bluff));
                break;
            case 1: textPaint.setColor(getResources().getColor(R.color.certain));
                break;
            case 2: textPaint.setColor(getResources().getColor(R.color.uncertain));
                break;
        }*/
        //draw number inside rectangle
        textPaint.setTextSize(cellWidth()/10);

        textPaint.getTextBounds(move.turnNumberToString(), 0, move.turnNumberToString().length(), rect);
        //float x = moveSquare.centerX() - rect.exactCenterX();
        //float y = moveSquare.centerY() - rect.exactCenterY();
        canvas.drawText(move.turnNumberToString(), moveSquare.centerX() - rect.exactCenterX(),
                moveSquare.centerY() - rect.exactCenterY(), textPaint);
    }

    private void setMoveSquare(float centerX, float centerY){
        moveSquare.set(centerX - moveWidth / 2, centerY - moveWidth / 2, centerX + moveWidth / 2, centerY + moveWidth / 2);
    }


    @Override
    public void drawSector(Canvas canvas, Sector sector){
        super.drawSector(canvas, sector);
        //RectF hexBounds = new RectF();
        combPath().computeBounds(rectf, true);


        //we want text about 1/4 of the way from the top
        //float y = rectf.top + rectf.height()/4;

        //sector is special like H, E, A
        if(sector.isSpecial())
            drawSectorLabel(canvas, sector.label(), rectf.centerX(), rectf.centerY());
        //sector is not special but still valid
        else if(sector.isValid()) {
            drawSectorName(canvas, sector.label(), rectf.centerX(), rectf.top + rectf.height()/4);
            if(sector.moves() != null && sector.moves().size()>0)
                drawMoves(canvas, sector.moves());
        }
    }



    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            //getParent().requestDisallowInterceptTouchEvent(true);
            //super.onScaleBegin(detector);
            lastFocusX = detector.getFocusX();
            lastFocusY = detector.getFocusY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            // get scale
            float factor = detector.getScaleFactor();

            // Don't let the object get too small or too large.
            if (factor * saveScale > MAX_SCALE) {
                factor = MAX_SCALE / saveScale;
            } else if (factor * saveScale < MIN_SCALE) {
                factor = MIN_SCALE / saveScale;
            }

            // store local scale
            saveScale *= factor;

            Matrix transformationMatrix = new Matrix();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            //Zoom focus is where the fingers are centered,
            transformationMatrix.postTranslate(-focusX, -focusY);

            //mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            //mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            transformationMatrix.postScale(factor, factor);

            /* Adding focus shift to allow for scrolling with two pointers down. Remove it to
            skip this functionality. This could be done in fewer lines, but for clarity I do it this way here */
            //Edited after comment by chochim
            float focusShiftX = focusX - lastFocusX;
            float focusShiftY = focusY - lastFocusY;
            offsetX = focusX + focusShiftX;
            offsetY = focusY + focusShiftY;
            transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);
            drawMatrix.postConcat(transformationMatrix);
            lastFocusX = focusX;
            lastFocusY = focusY;
            return true;
        }


    }

    @Override
    public Point pixelToHex(int px, int py){
        tempMatrix.reset();
        float[] transformedPoint = new float[]{px, py};

        drawMatrix.invert(tempMatrix);

        tempMatrix.mapPoints(transformedPoint);

        return super.pixelToHex((int)transformedPoint[0], (int)transformedPoint[1]);
    }

    protected class GestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

            /*
            // Scrolling uses math based on the viewport (as opposed to math using pixels).

            // Pixel offset is the offset in screen pixels, while viewport offset is the
            // offset within the current viewport.
            float viewportOffsetX = distanceX * mCurrentViewport.width()
                    / mContentRect.width();
            float viewportOffsetY = -distanceY * mCurrentViewport.height()
                    / mContentRect.height();

            // Updates the viewport, refreshes the display.
            setViewportBottomLeft(
                    mCurrentViewport.left + viewportOffsetX,
                    mCurrentViewport.bottom + viewportOffsetY);
            */

            //offsetX -=distanceX;
            //offsetY -=distanceY;
            float[] values = new float[9];
            drawMatrix.getValues(values);
            float matrixX = values[Matrix.MTRANS_X];
            float matrixY = values[Matrix.MTRANS_Y];
            float width = values[Matrix.MSCALE_X]*canvasWidth;
            float height = values[Matrix.MSCALE_Y]*canvasHeight;

            float buffer = scaledCellRadius()*6;

            //if image goes outside left
            //if the right of the matrix is less than the buffer that means we are out of bounds
            //  on the left
            if ( (matrixX + width) + distanceX <= buffer){
                //System.out.println("image went outside left bound");
                //return true;
                //distanceX = -matrixX;
                distanceX = -(Math.abs(matrixX + width +distanceX-buffer));
            }
            //if image will go outside right bound
            //  that means if the left of the matrix goes less than the buffer away from getwidth
            else if(matrixX - distanceX > (getWidth() - buffer)){
                //distanceX = getWidth() - matrixX - width;
                distanceX = Math.abs(-matrixX + getWidth()- buffer );
                //System.out.println("image went outside right bound");
            }
            //if image will go oustside top bound
            //that means if the bottom edge is less than the buffer
            else if (matrixY + height + distanceY <= buffer){
                distanceY = -(Math.abs(matrixY + height +distanceY - buffer));
                //System.out.println("image went outside top bound");
                //return true;
            }
            //if image will go outside bottom bound
            else if( (matrixY - distanceY) > (getHeight() - buffer)){
                //System.out.println("image went outside bottom bound");
                distanceY = Math.abs(getHeight() - buffer - matrixY);
            }

            drawMatrix.postTranslate(-distanceX, -distanceY);

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent event) {

            tempMatrix.reset();
            float[] transformedPoint = new float[]{event.getX(), event.getY()};

            drawMatrix.invert(tempMatrix);

            tempMatrix.mapPoints(transformedPoint);

            Point p = pixelToHex((int)event.getX(), (int)event.getY());
            listener.onCellClick(p.x, p.y);
            return true;
        }



        /*
        @Override
        public boolean onDoubleTapEvent(MotionEvent event) {
            Log.d("gestures", "onDoubleTapEvent: " + event.toString());




            float fx = event.getX();
            float fy = event.getY();
            //originX = event.getX();
            //originY = event.getY();

            originX += fx/mScaleFactor; // move origin to focus
            originY += fy/mScaleFactor;


            mScaleFactor *= 1.25;

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            //originX -= fx/mScaleFactor; // move back, allow us to zoom with (fx,fy) as center
            //originY -= fy/mScaleFactor;


            invalidate();
            return true;

        }
        */
    }

}

