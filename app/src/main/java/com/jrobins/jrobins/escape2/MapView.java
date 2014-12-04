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

/**
 * Created by jrobins on 12/1/2014.
 */
public class MapView extends View {
    private Paint wallPaint = new Paint();
    private Paint fillPaint = new Paint();
    private Paint cachePaint = new Paint();
    private Paint textPaint = new Paint();

    private Path combPath;
    private Bitmap cacheBmp;
    private Canvas cacheCan;

    private int cellWidth;
    private int columns;
    private int rows;
    private boolean[][] cellSet;

    private OnCellClickListener listener;

    private int cellColor;
    private Sector[][] sectors;


    public MapView(Context context)
    {
        this(context, null);
    }

    public MapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        wallPaint.setColor(getResources().getColor(R.color.map_outline));
        wallPaint.setStyle(Paint.Style.STROKE);
        wallPaint.setStrokeWidth(5f);

        fillPaint.setStyle(Paint.Style.FILL);
        cachePaint.setStyle(Paint.Style.FILL);

        cellColor = Color.MAGENTA;

        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.MAGENTA);
        textPaint.setAntiAlias(true);

        textPaint.setTextSize(20);
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
        cellSet[column][row] = isSet;
        invalidate();
    }

    public boolean isCellSet(int column, int row)
    {
        return cellSet[column][row];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        cacheBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        cacheCan = new Canvas(cacheBmp);

        cellWidth = 2 * w / (2 * columns + columns - 1);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawColor(getResources().getColor(R.color.map_background));

        boolean oddRow;
        int xOff;

        //values for the center of the current hexagon
        float centerX;
        float centerY = (float) (cellWidth * Math.sqrt(3) / 4);



        // min. rect of text
        Rect textBounds;
        String text;
        float x,y;

        combPath = getHexPath(cellWidth / 2f, cellWidth / 2f, (float) (cellWidth * Math.sqrt(3) / 4));

        for (int r = 0; r < rows; r++)
        {
            oddRow = (r & 1) == 1;
            xOff = 0;
            centerX =  cellWidth/2f + (oddRow ? 1 : 0) * 3 * cellWidth / 4;

            for (int c = 0; c < columns; c++)
            {
                if (!(oddRow && c == columns - 1))
                {
                    if(sectors !=null)
                        cellColor = getResources().getColor(sectors[c][r].color());
                    else
                        cellColor = Color.MAGENTA;
                    fillPaint.setColor(cellSet[c][r] ? Color.RED : cellColor);

                    /*canvas.drawPath(combPath, fillPaint);

                    canvas.drawPath(combPath, wallPaint);*/
                    //drawHexagon(canvas, combPath);

                    /*
                    textBounds = new Rect();
                    text = sectors[c][r].getId();
                    textPaint.getTextBounds(text, 0, text.length(), textBounds);
                    x = centerX - textBounds.exactCenterX();
                    y = centerY - textBounds.exactCenterY();
                    canvas.drawText(text, x, y, textPaint);
                    */

                    //drawSectorName(canvas,sectors[c][r].getId(), centerX, centerY);
                    drawSector(canvas, combPath, sectors[c][r].getId());
                    
                    cachePaint.setColor(Color.argb(255, 1, c, r));
                    cacheCan.drawPath(combPath, cachePaint);

                    combPath.offset((int) (1.5f * cellWidth), 0);
                    xOff += 1.5f * cellWidth;
                    centerX+=xOff;

                }
            }

            centerY+=(float) (cellWidth * Math.sqrt(3) / 4);
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
        Rect textBounds;

        float x,y;
        textBounds = new Rect();

        textPaint.getTextBounds(sectorName, 0, sectorName.length(), textBounds);
        x = centerX - textBounds.exactCenterX();
        y = centerY - textBounds.exactCenterY();
        canvas.drawText(sectorName, x, y, textPaint);
    }

    private void drawHexagon(Canvas canvas, Path path){
        canvas.drawPath(path, fillPaint);

        canvas.drawPath(path, wallPaint);
    }


    private void drawSector(Canvas canvas, Path hexPath, String sectorName){
        RectF hexBounds = new RectF();
        hexPath.computeBounds(hexBounds, true);
        drawHexagon(canvas, hexPath);
        drawSectorName(canvas, sectorName, hexBounds.centerX(), hexBounds.centerY());

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
