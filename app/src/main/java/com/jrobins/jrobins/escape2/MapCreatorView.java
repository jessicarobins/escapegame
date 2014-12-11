package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;


public class MapCreatorView extends BasicHexagonGridView {

    public MapCreatorView(Context context) {
        super(context);
    }

    public MapCreatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //set the fillpaint to be black
        setFillPaint(Color.BLACK);
        //set wallpaint to be white
        setWallPaint(Color.WHITE);

        //set this to not be a static view
        setMapThread(new MapDrawingThread(getHolder(),context, this, false));

        //initialize with a blank default map
        initialize(new Map());
    }
}
