package com.jrobins.jrobins.escape2;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

public class HexagonView extends View {

    //hexagon stuff
    private Path hexagonPath;
    private Path hexagonBorderPath;
    private float radius;
    private float width, height;


    //gameplay stuff
    private int color;
    char x;
    int y;


    public HexagonView(Context context) {
        super(context);
        init();
    }

    public HexagonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        hexagonPath = new Path();
        hexagonBorderPath = new Path();
        color = Color.WHITE;
    }

    public void setRadius(float r) {
        this.radius = r;
        calculatePath();
    }

    public void setColor(int color) {
        this.color = color;
    }

    private void calculatePath() {

        float r = radius;
        float w = radius*2;
        float h = (float)(radius * Math.sqrt(3));
        float side = radius * 3 / 2;

        hexagonPath.moveTo(r/2, 0);
        hexagonPath.lineTo(side, 0);
        hexagonPath.lineTo(w, h/2);
        hexagonPath.lineTo(side, h);
        hexagonPath.lineTo(r/2, h);
        hexagonPath.lineTo(0, h/2);
        hexagonPath.moveTo(r/2, 0);

        hexagonBorderPath.moveTo(r/2, 0);
        hexagonBorderPath.lineTo(side, 0);
        hexagonBorderPath.lineTo(w, h/2);
        hexagonBorderPath.lineTo(side, h);
        hexagonBorderPath.lineTo(r/2, h);
        hexagonBorderPath.lineTo(0, h/2);
        hexagonBorderPath.moveTo(r/2, 0);

        invalidate();
    }

    @Override
    public void onDraw(Canvas c){
        super.onDraw(c);
        c.drawColor(color);
        c.clipPath(hexagonBorderPath, Region.Op.DIFFERENCE);
        c.drawColor(Color.BLACK);
        c.save();
        c.clipPath(hexagonPath, Region.Op.DIFFERENCE);
        c.drawColor(getResources().getColor(android.R.color.transparent));
        c.save();
    }

    // getting the view size and default radius
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height =  MeasureSpec.getSize(heightMeasureSpec);
        radius = height / 2 - 10;
        calculatePath();
    }
}