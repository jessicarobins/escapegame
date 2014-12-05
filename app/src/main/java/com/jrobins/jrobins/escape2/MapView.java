package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class MapView extends View {

    private final int MAX_MOVES = 4;

    private Paint wallPaint = new Paint();
    private Paint fillPaint = new Paint();
    private Paint cachePaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint labelPaint = new Paint();

    private Path combPath;
    private Bitmap cacheBmp;
    private Canvas cacheCan;

    private int cellWidth;
    private int moveWidth;

    private int columns;
    private int rows;
    private boolean[][] cellSet;

    private OnCellClickListener listener;

    private int cellColor;
    private Sector[][] sectors;
    RectF rectf = new RectF();
    RectF moveSquare = new RectF();
    Rect rect = new Rect();


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
        cachePaint.setStyle(Paint.Style.FILL);

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

        cacheBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        cacheCan = new Canvas(cacheBmp);

        //cellWidth = 2 * w / (2 * columns + columns - 1);
        //cellWidth = (int)( 2*w / (1.75 * columns));
        //# cols - .5*(#cols-1)/2
        //cellWidth = Math.min(sqrt(3)/2 * cellWidth)
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

        canvas.drawColor(getResources().getColor(R.color.map_background));
        drawGridWithZigZagRows(canvas);

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

                    cachePaint.setColor(Color.argb(255, 1, c, r));
                    drawSector(canvas, sectors[c][r]);


                    combPath.offset(0,(int)  (cellWidth * Math.sqrt(3) / 2));
                    yOff += (cellWidth * Math.sqrt(3) / 2);


                }
            }

            //combPath.offset(cellWidth * .75f, (float)(-yOff));
            combPath.offset(cellWidth * .75f, (float)(-yOff + (oddCol ? -1 : 1) * (cellWidth * Math.sqrt(3) / 4)));
        }

    }

    private void drawGridWithZigZagCols(Canvas canvas){
        boolean oddRow;
        int xOff;



        combPath = getHexPath(cellWidth / 2f, cellWidth / 2f, (float) (cellWidth * Math.sqrt(3) / 4));

        for (int r = 0; r < rows; r++)
        {
            oddRow = (r & 1) == 1;
            xOff = 0;


            for (int c = 0; c < columns; c++)
            {
                if (!(oddRow && c == columns - 1))
                {
                    if(sectors !=null)
                        cellColor = getResources().getColor(sectors[c][r].color());
                    else
                        cellColor = Color.MAGENTA;
                    fillPaint.setColor(cellSet[c][r] ? Color.RED : cellColor);




                    cachePaint.setColor(Color.argb(255, 1, c, r));
                    drawSector(canvas, sectors[c][r]);
                    //cacheCan.drawPath(combPath, cachePaint);

                    combPath.offset((int) (1.5f * cellWidth), 0);
                    xOff += 1.5f * cellWidth;


                }
            }


            combPath.offset(-xOff + (oddRow ? -1 : 1) * 3 * cellWidth / 4, (float) (cellWidth * Math.sqrt(3) / 4));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return true;

        int pixel = cacheBmp.getPixel((int) event.getX(), (int) event.getY());

        int r = Color.red(pixel);
        if (r == 1)
        {
            int g = Color.green(pixel);
            int b = Color.blue(pixel);

            if (listener != null)
            {
                listener.onCellClick(g, b);
            }
        }
        return true;
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

        labelPaint.getTextBounds(label, 0, label.length(), rect);
        x = centerX - rect.exactCenterX();
        y = centerY - rect.exactCenterY();
        canvas.drawText(label, x, y, labelPaint);
    }


    private void drawHexagon(Canvas canvas){
        canvas.drawPath(combPath, fillPaint);
        wallPaint.setStrokeWidth(5f);
        canvas.drawPath(combPath, wallPaint);

        cacheCan.drawPath(combPath, cachePaint);
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
        int certainty;
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
}
