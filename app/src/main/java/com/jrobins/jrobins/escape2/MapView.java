package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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

public class MapView extends View {

    //the max number of moves in a row per hexagon
    private final int MAX_MOVES = 4;

    //paints!
    private Paint wallPaint = new Paint();
    private Paint fillPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint labelPaint = new Paint();

    //path of a hexagon
    private Path combPath;


    //widths of things
    private int cellWidth;
    private int moveWidth;

    private int columns;
    private int rows;
    private boolean[][] cellSet;
    private int cellColor;

    private Sector[][] sectors;

    //rectangles! for drawing things the right size
    RectF rectf = new RectF();
    RectF moveSquare = new RectF();
    Rect rect = new Rect();

    //clicking
    private OnCellClickListener listener;

    //zooming
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    private float originX = 0f; // current position of viewport
    private float originY = 0f;


    //panning
    private GestureDetector mGestureDetector;
    private float offsetX = 0f;
    private float offsetY = 0f;


    private static final float AXIS_X_MIN = -1f;
    private static final float AXIS_X_MAX = 1f;
    private static final float AXIS_Y_MIN = -1f;
    private static final float AXIS_Y_MAX = 1f;
    private RectF mCurrentViewport = new RectF(AXIS_X_MIN, AXIS_Y_MIN, AXIS_X_MAX, AXIS_Y_MAX);
    private Rect mContentRect = new Rect();

    public MapView(Context context)
    {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        wallPaint.setColor(getResources().getColor(R.color.map_outline));
        wallPaint.setStyle(Paint.Style.STROKE);
        //wallPaint.setStrokeWidth(5f);

        fillPaint.setStyle(Paint.Style.FILL);


        cellColor = Color.MAGENTA;

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


        //initialize zoom thing
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        //initialize the pan thing
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void initialize(Sector[][] sectors){

        this.sectors = sectors;
        initialize(sectors.length, sectors[0].length);
    }

    public void initialize(int columns, int rows)
    {
        this.columns = columns;
        this.rows = rows;

        this.cellSet = new boolean[columns][rows];
    }

    public interface OnCellClickListener
    {
        public void onCellClick(int column, int row);
    }

    public void setOnCellClickListener(OnCellClickListener listener)
    {
        this.listener = listener;
    }

    public void setCell(int column, int row, boolean isSet)
    {
        //this is the old 'setcell' user
        cellSet[column][row] = isSet;
        invalidate();
    }

    public boolean isCellSet(int column, int row)
    {
        return cellSet[column][row];
    }

    public void setCell(int column, int row, boolean isSet, Move move)
    {
        if (isSet){ //cell is now set
            //add move to array
            sectors[column][row].addMove(move);
        }
        else { //isSet == false, cell is now not set
            //remove move from array
            sectors[column][row].removeLastMove();
        }

        cellSet[column][row] = isSet;
        invalidate();
    }

    public void resetAllCells(){
        cellSet = new boolean[columns][rows];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        float cellHeight = h/rows;
        int cellWidth1 = (int)( cellHeight*2 / Math.sqrt(3)); //if height is limiting
        int cellWidth2 = (int)(w/ (columns - (.25*(columns-1)))); //if width is limiting
        cellWidth = Math.min(cellWidth1, cellWidth2);

        labelPaint.setTextSize(cellWidth/2);
        moveWidth = cellWidth/7;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.save();

        //canvas.translate(originX, originY);
        //canvas.scale(mScaleFactor, mScaleFactor, originX, originY);
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.translate(offsetX, offsetY);

        cellWidth *=mScaleFactor;
        moveWidth = cellWidth/7;
        canvas.drawColor(getResources().getColor(R.color.map_background));
        drawGridWithZigZagRows(canvas);


        canvas.restore();

        mScaleFactor = 1.f;
    }


    private void drawGridWithZigZagRows(Canvas canvas){
        boolean oddCol;
        int yOff;

        combPath = getHexPath(cellWidth / 2f, cellWidth / 2f, (float) (cellWidth * Math.sqrt(3) / 4));

        for (int c = 0; c < columns; c++)
        {
            oddCol = (c & 1) == 1;
            yOff = 0;


            for (int r = 0; r < rows; r++)
            {
                if (!(oddCol && r == rows - 1))
                {
                    if(sectors !=null)
                        cellColor = getResources().getColor(sectors[c][r].color());
                    else
                        cellColor = Color.MAGENTA;
                    //fillPaint.setColor(cellSet[c][r] ? Color.RED : cellColor);
                    fillPaint.setColor(cellColor);


                    drawSector(canvas, sectors[c][r]);


                    combPath.offset(0,(int)  (cellWidth * Math.sqrt(3) / 2));
                    yOff += (cellWidth * Math.sqrt(3) / 2);


                }
            }

            //combPath.offset(cellWidth * .75f, (float)(-yOff));
            combPath.offset(cellWidth * .75f, (float)(-yOff + (oddCol ? -1 : 1) * (cellWidth * Math.sqrt(3) / 4)));
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        mGestureDetector.onTouchEvent(event);
        if (event.getPointerCount() > 1){
            mScaleDetector.onTouchEvent(event);
            return true;
        }

        else {


            if (event.getAction() != MotionEvent.ACTION_DOWN)
                return true;


            if (listener != null) {
                float x = event.getX()-offsetX;
                float y = event.getY()-offsetY;

                int radius = cellWidth / 2;
                int cellHeight = (int) (((float) radius) * Math.sqrt(3));
                int side = radius * 3 / 2;


                int ci = (int) Math.floor((float) x / (float) side);
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

    }


    private void drawSectorName(Canvas canvas, String sectorName, float centerX, float centerY){
        //Rect textBounds;

        float x,y;
        //textBounds = new Rect();
        textPaint.setTextSize(cellWidth/5);
        textPaint.setColor(Color.BLACK);
        textPaint.clearShadowLayer();
        textPaint.getTextBounds(sectorName, 0, sectorName.length(), rect);
        x = centerX - rect.exactCenterX();
        y = centerY - rect.exactCenterY();
        canvas.drawText(sectorName, x, y, textPaint);
    }

    private void drawSectorLabel(Canvas canvas, String label, float centerX, float centerY){
        //Rect textBounds;

        float x,y;
        //textBounds = new Rect();
        labelPaint.setTextSize(cellWidth/2);
        labelPaint.getTextBounds(label, 0, label.length(), rect);
        x = centerX - rect.exactCenterX();
        y = centerY - rect.exactCenterY();
        canvas.drawText(label, x, y, labelPaint);
    }


    private void drawHexagon(Canvas canvas){
        canvas.drawPath(combPath, fillPaint);
        wallPaint.setStrokeWidth(5f);
        canvas.drawPath(combPath, wallPaint);

    }

    private void drawMoves(Canvas canvas, ArrayList<Move> moves){
        //hexagon is combPath
        //probably need to compute the bounds...
        combPath.computeBounds(rectf, true);

        //these are the coordinates where the grid should start
        //  so x is (MAX_MOVES/2) moves left of center and y is right on center
        float gridStartX = rectf.centerX()-(MAX_MOVES/2)*moveWidth + .5f*moveWidth;
        float gridStartY = rectf.centerY();

        //the max # of moves in a row is now a class-level
        //  constant MAX_MOVES and equal to 4

        //need a for loop with a yoffset for creating rows
        int yOffset = 0;

        for(int i = 0; i<moves.size(); i++){
            //check that we haven't reached the max number of moves in
            //  a row
            if(i > 0 && i%MAX_MOVES==0)
                yOffset+=moveWidth;



            //draw the shape in the right place
            //that'll be (i%MAX_MOVES)*moveWidth + gridStartX and then gridStartY+yOffset
            drawMove(canvas, moves.get(i), (i % MAX_MOVES) * moveWidth + gridStartX, gridStartY + yOffset);


        }

    }

    private void drawMove(Canvas canvas, Move move, float centerX, float centerY){

        //get rectangle
        setMoveSquare(centerX, centerY);

        //set the fillpaint to be the player color
        fillPaint.setColor(move.color());
        wallPaint.setStrokeWidth(1f);
        //draw rectangle
        canvas.drawRect(moveSquare, fillPaint);
        canvas.drawRect(moveSquare, wallPaint);
        if(moveWidth>22)
            drawTextInMoveSquare(canvas, move);
    }

    private void drawTextInMoveSquare(Canvas canvas, Move move){
        //find the right text color - this is certainty color, should be text

        int certainty = move.certainty();
        switch (certainty) {
            case 0: textPaint.setColor(getResources().getColor(R.color.bluff));
                break;
            case 1: textPaint.setColor(getResources().getColor(R.color.certain));
                break;
            case 2: textPaint.setColor(getResources().getColor(R.color.uncertain));
                break;
        }
        //draw number inside rectangle
        textPaint.setTextSize(cellWidth/10);

        textPaint.getTextBounds(move.turnNumberToString(), 0, move.turnNumberToString().length(), rect);
        float x = moveSquare.centerX() - rect.exactCenterX();
        float y = moveSquare.centerY() - rect.exactCenterY();
        canvas.drawText(move.turnNumberToString(), x,y, textPaint);
    }

    private void setMoveSquare(float centerX, float centerY){
        moveSquare.set(centerX - moveWidth/2, centerY-moveWidth/2, centerX+moveWidth/2, centerY + moveWidth/2);
    }



    private void drawSector(Canvas canvas, Sector sector){
        //RectF hexBounds = new RectF();
        combPath.computeBounds(rectf, true);
        drawHexagon(canvas);

        //we want text about 1/4 of the way from the top
        float y = rectf.top + rectf.height()/4;

        //sector is special like H, E, A
        if(sector.isSpecial())
            drawSectorLabel(canvas, sector.label(), rectf.centerX(), rectf.centerY());
        //sector is not special but still valid
        else if(sector.isValid()) {
            drawSectorName(canvas, sector.label(), rectf.centerX(), y);

            drawMoves(canvas, sector.moves());
        }
    }

    private Path getHexPath(float size, float centerX, float centerY)
    {
        Path path = new Path();

        for (int j = 0; j <= 6; j++)
        {
            double angle = j * Math.PI / 3;
            float x = (float) (centerX + size * Math.cos(angle));
            float y = (float) (centerY + size * Math.sin(angle));
            if (j == 0)
            {
                path.moveTo(x, y);
            }
            else
            {
                path.lineTo(x, y);
            }
        }

        return path;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {

            getParent().requestDisallowInterceptTouchEvent(true);
            super.onScaleBegin(detector);
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            if (detector.getScaleFactor() < 0.01)
                return false; // ignore small changes


            //float fx = detector.getFocusX();
            //float fy = detector.getFocusY();
            originX = detector.getFocusX();
            originY = detector.getFocusY();

            //originX += fx/mScaleFactor; // move origin to focus
            //originY += fy/mScaleFactor;


            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            //originX -= fx/mScaleFactor; // move back, allow us to zoom with (fx,fy) as center
            //originY -= fy/mScaleFactor;


            invalidate();
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector1) {
            getParent().requestDisallowInterceptTouchEvent(false);
            super.onScaleEnd(detector1);

        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {


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
            offsetX -=distanceX;
            offsetY -=distanceY;
            invalidate();
            return true;
        }

    }

    /**
     * Sets the current viewport (defined by mCurrentViewport) to the given
     * X and Y positions. Note that the Y value represents the topmost pixel position,
     * and thus the bottom of the mCurrentViewport rectangle.
     */
    private void setViewportBottomLeft(float x, float y) {
        /*
         * Constrains within the scroll range. The scroll range is simply the viewport
         * extremes (AXIS_X_MAX, etc.) minus the viewport size. For example, if the
         * extremes were 0 and 10, and the viewport size was 2, the scroll range would
         * be 0 to 8.
         */

        float curWidth = mCurrentViewport.width();
        float curHeight = mCurrentViewport.height();
        x = Math.max(AXIS_X_MIN, Math.min(x, AXIS_X_MAX - curWidth));
        y = Math.max(AXIS_Y_MIN + curHeight, Math.min(y, AXIS_Y_MAX));

        mCurrentViewport.set(x, y - curHeight, x + curWidth, y);

        // Invalidates the View to update the display.
        ViewCompat.postInvalidateOnAnimation(this);
    }

}

