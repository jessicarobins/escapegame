package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by jrobins on 12/8/2014.
 */
public class BasicHexagonGridView extends View {

    //paints - just wall and fill, no text
    private Paint wallPaint = new Paint();
    private Paint fillPaint = new Paint();

    //cell info
    private int columns;
    private int rows;

    //path of a hexagon
    private Path combPath;

    //widths of things
    private int cellWidth;
    private int cellColor;

    private Sector[][] sectors;

    public BasicHexagonGridView(Context context)
    {
        this(context, null);
    }

    public BasicHexagonGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        wallPaint.setColor(getResources().getColor(R.color.map_outline));
        wallPaint.setStyle(Paint.Style.STROKE);
        //wallPaint.setStrokeWidth(5f);

        fillPaint.setStyle(Paint.Style.FILL);


        cellColor = Color.MAGENTA;
    }
    /***************** setters & getters ********/

    public Sector[][] sectors(){
        return sectors;
    }

    public int columns(){
        return columns;
    }

    public int rows(){
        return rows;
    }


    public Paint fillPaint(){
        return fillPaint;
    }

    public Paint wallPaint(){
        return wallPaint;
    }

    public Path combPath(){
        return combPath;
    }

    public int cellWidth(){
        return cellWidth;
    }

    public void setCellWidth(float cellWidth){
        this.cellWidth = (int) cellWidth;
    }


    /**************** moves *********************/
    public void addMove(int col, int row, Move move){
        sectors[col][row].addMove(move);
    }

    public void removeLastMove(int col, int row){
        sectors[col][row].removeLastMove();

    }

    /*****************initializing stuff*********/

    public void initialize(Map map){
        this.sectors = map.sectors();
        initialize(sectors.length, sectors[0].length);
    }

    public void initialize(Sector[][] sectors){

        this.sectors = sectors;
        initialize(sectors.length, sectors[0].length);
    }

    public void initialize(int columns, int rows)
    {
        this.columns = columns;
        this.rows = rows;
    }

    /************* drawing the map****************/
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        float cellHeight = h/rows;
        int cellWidth1 = (int)( cellHeight*2 / Math.sqrt(3)); //if height is limiting
        int cellWidth2 = (int)(w/ (columns - (.25*(columns-1)))); //if width is limiting
        cellWidth = Math.min(cellWidth1, cellWidth2);
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.map_background));
        drawGridWithZigZagRows(canvas);

    }

    public void drawGridWithZigZagRows(Canvas canvas){
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

    private void drawHexagon(Canvas canvas){
        canvas.drawPath(combPath, fillPaint);
        wallPaint.setStrokeWidth(5f);
        canvas.drawPath(combPath, wallPaint);

    }


    public void drawSector(Canvas canvas, Sector sector){

        drawHexagon(canvas);


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
