package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


public class BasicHexagonGridView   extends SurfaceView
                                    implements SurfaceHolder.Callback{

    //paints - just wall and fill, no text
    private Paint wallPaint = new Paint();
    private Paint fillPaint = new Paint();

    //cell info
    private int columns;
    private int rows;

    //path of a hexagon
    private Path combPath;

    //widths of things
    private float cellWidth;


    private int cellColor;

    private Sector[][] sectors;
    Map map;



    private Context context;

    //multithreading
    MapDrawingThread mapThread;

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

        this.context = context;

        getHolder().addCallback(this);
        mapThread = new MapDrawingThread(getHolder(),context, this, true);
        //setFocusable(true);
    }


    /**************** surfaceholder implementation ******/
    @Override
    public void surfaceCreated(SurfaceHolder holder){

        if (mapThread.getState() == Thread.State.NEW) {

            mapThread.setRunning(true);

            mapThread.start();
        }
    }

    public void setMapThread(MapDrawingThread mapThread){
        this.mapThread = mapThread;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        /*float cellHeight = h/rows;
        int cellWidth1 = (int)( cellHeight*2 / Math.sqrt(3)); //if height is limiting
        int cellWidth2 = (int)(w/ (columns - (.25*(columns-1)))); //if width is limiting
        cellWidth = Math.min(cellWidth1, cellWidth2);*/

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {



        boolean retry = true;

        while(retry){
            try{
                mapThread.join();
                retry = false;
                mapThread.setRunning(false);
                Log.v("map thread", "surface destroyed");
            }

            catch(Exception e) {
                Log.v("Exception Occurred", e.getMessage());
            }
        }
    }

    public void doDraw(Canvas canvas){
        /*float cellHeight = getHeight()/rows;
        int cellWidth1 = (int)( cellHeight*2 / Math.sqrt(3)); //if height is limiting
        int cellWidth2 = (int)(getWidth()/ (columns - (.25*(columns-1)))); //if width is limiting
        cellWidth = Math.min(cellWidth1, cellWidth2);*/


        drawGridWithZigZagRows(canvas);

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

    public MapDrawingThread thread(){
        return mapThread;
    }

    public Paint fillPaint(){
        return fillPaint;
    }

    public void setFillPaint(int color) {
        fillPaint.setColor(color);
    }

    public Paint wallPaint(){
        return wallPaint;
    }

    public void setWallPaint(int color) {
        wallPaint.setColor(color);
    }

    public Path combPath(){
        return combPath;
    }

    public float cellWidth(){
        return cellWidth;
    }

    public float cellRadius(){
        return cellWidth/2;
    }

    public float cellSide() {
        return cellRadius();
    }

    public int cellHeight(){
        return (int)(Math.sqrt(3)*cellWidth/2);
    }

    public Sector sectorAt(Point p){
        return sectors[p.x][p.y];
    }

    public Point getSectorFromTouchPoint(int x, int y){
        //get the location in the sectors matrix of the sector, given the place the user touched

        int ci = (int) Math.floor(x / (float) cellSide());
        int cx = (int) (x - cellSide() * ci);

        int ty = (int) (y - (ci % 2) * cellHeight() / 2);
        int cj = (int) Math.floor((float) ty / (float) cellHeight());
        int cy = ty - cellHeight() * cj;

        if (cx > Math.abs(cellRadius() / 2 - cellRadius() * cy / cellHeight())) {
            return new Point(ci, cj);
        } else {
            return new Point(ci - 1, cj + (ci % 2) - ((cy < cellHeight() / 2) ? 1 : 0));
        }
    }

    public Point pixelToHex(int px, int py){
        //q = col, r = row
        //get an approx axial coord
        px -= cellRadius();
        py -= cellHeight()*3/2;
        double q = (2./3 * px / cellRadius());
        double r = (-1./3 * px + 1./3*Math.sqrt(3) * py) / cellRadius();

        //use hex rounding to convert fractional coord into integer cube coord

        //convert axial to cube so we can use hexrounding function
        /*# convert axial to cube
            x = q
            z = r
            y = -x-z
        */
        double x = q;
        double z = r;
        double y = -x-z;

        //hexrounding function:

        long rx = Math.round(x);
        long ry = Math.round(y);
        long rz = Math.round(z);

        double x_diff = Math.abs(rx - x);
        double y_diff = Math.abs(ry - y);
        double z_diff = Math.abs(rz - z);

        if ( (x_diff > y_diff) && (x_diff > z_diff) )
            rx = -ry-rz;
        else if (y_diff > z_diff)
            ry = -rx-rz;
        else
            rz = -rx-ry;

        //rx, ry, rz is the cube coords. convert to odd-q vertical layout
        /*# convert cube to odd-q offset
            q = x
            r = z + (x - (x&1)) / 2
        */
        //rx-=1;
        //rz-=1;
        //ry-=1;
        int fq = (int)(rx);
        int fr = (int) (rz + (rx - (rx&1)) / 2);
        return new Point(fq, fr+1);
    }

    //to use with pixeltohex to make sure the point is in the array
    public boolean isValidPoint(Point p){
        return (p.x<sectors.length &&
                p.y<sectors[0].length &&
                p.x>=0 &&
                p.y>=0);
    }


    public void setCellWidth(float cellWidth){
        this.cellWidth = (int) cellWidth;
    }

    public void stopThread(){
        mapThread.setRunning(false);
    }

    public void startThread(){
        mapThread.setRunning(true);
    }

    public boolean threadIsRunning(){
        return mapThread.isRunning();
    }

    public Map map(){
        return map;
    }


    /**************** moves *********************/
    public void addMove(int col, int row, Move move){
        sectors[col][row].addMove(move);
    }

    public void removeLastMove(int col, int row){
        sectors[col][row].removeLastMove();

    }

    public void removeMove(int col, int row, Move move){
        sectors[col][row].removeMove(move);
    }

    /*****************initializing stuff*********/

    public void initialize(Map map){
        this.map = map;
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

/*
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.map_background));
        drawGridWithZigZagRows(canvas);


    }*/

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
                //if (!(oddCol && r == rows - 1))
                //{
                    if(sectors !=null)
                        cellColor = getResources().getColor(sectors[c][r].color());
                    else
                        cellColor = Color.MAGENTA;
                    //fillPaint.setColor(cellSet[c][r] ? Color.RED : cellColor);
                    fillPaint.setColor(cellColor);


                    drawSector(canvas, sectors[c][r]);


                    combPath.offset(0,(int)  (cellWidth * Math.sqrt(3) / 2));
                    yOff += (cellWidth * Math.sqrt(3) / 2);


                //}
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

    //find the centerpoint of the hexagon based on its position in the array
    /*
    public Point findHexagon(int col, int row){
        //x = size * 3/2 * q
        //y = size * sqrt(3) * (r + 0.5 * (q&1))
        //where 'size' = 1/2 width
        return new Point((cellWidth/2)*(3/2)*col, (int)((cellWidth/2)*Math.sqrt(3)*(row+.5*(col&1))));
    }*/

    public Point findHexagonTopLeft(int col, int row){

        return new Point((int)(col*cellWidth*(3/4)), (int) (row*cellHeight()+(col%2)*(cellHeight()/2)));
    }

    public Path getHexPath(float size, float centerX, float centerY)
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
