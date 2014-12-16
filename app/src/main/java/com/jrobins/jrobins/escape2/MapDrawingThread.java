package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * Created by jrobins on 12/9/2014.
 */
public class MapDrawingThread extends Thread {


    //whether the thread is running
    boolean running;
    boolean staticImage = false;
    Canvas canvas;
    SurfaceHolder surfaceHolder;
    Context context;
    BasicHexagonGridView hexagonGrid;



    public MapDrawingThread(){

    }

    public MapDrawingThread(SurfaceHolder surfaceHolder, BasicHexagonGridView hexagonGrid) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.hexagonGrid = hexagonGrid;
    }

    public MapDrawingThread(SurfaceHolder sholder, Context ctx, BasicHexagonGridView hexagonGrid){

        this.surfaceHolder = sholder;

        this.context = ctx;

        this.running = false;

        this.hexagonGrid = hexagonGrid;

    }

    public MapDrawingThread(SurfaceHolder sholder, Context ctx, BasicHexagonGridView hexagonGrid, boolean staticImage){

        this.surfaceHolder = sholder;

        this.context = ctx;

        this.running = false;

        this.hexagonGrid = hexagonGrid;

        this.staticImage = staticImage;

    }


    public void setRunning(boolean running){

        this.running = running;

    }

    public boolean isRunning(){
        return running;
    }


    @Override

    public void run(){


        super.run();

        while(running){

            canvas = surfaceHolder.lockCanvas();

            if(canvas != null){

                hexagonGrid.doDraw(canvas);

                surfaceHolder.unlockCanvasAndPost(canvas);
                if(staticImage)
                    running = false;
            }

        }

    }
}


