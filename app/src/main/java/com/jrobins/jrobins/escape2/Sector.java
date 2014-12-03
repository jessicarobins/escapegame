package com.jrobins.jrobins.escape2;

import android.graphics.Color;

import java.util.ArrayList;

/**
 * Created by jrobins on 12/1/2014.
 */
public class Sector {
    private int x; //this represents A-Z, the x coordinate
    private int y;
    private int sectorType; //0 = invalid, 1 = safe, 2 = unsafe, 3 = alien start, 4 = human start
                            //5 = escape hatch
    private static final String[] sectorTypeMap = {"invalid",
                                                    "safe",
                                                    "unsafe",
                                                    "alien start",
                                                    "human start",
                                                    "escape hatch"};

    //need some datastructure to represent the moves - this should be dependent on what is easiest
    //  for gridview
    //lets do an arraylist of moves for now
    ArrayList<Move> moves;


    public Sector(){
        moves = new ArrayList<Move>();
    }

    public Sector(int x, int y, int sectorType){
        this.x = x;
        this.y = y;
        this.sectorType = sectorType;
        moves = new ArrayList<Move>();
    }

    public Sector(String x, int y, int sectorType){
        x = x.toUpperCase();
        char c = x.charAt(0);
        this.x = ((int)c)-65;
        this.y = y;
        this.sectorType = sectorType;
        moves = new ArrayList<Move>();
    }

    public Sector(char x, int y, int sectorType){
        String newX = x+"";
        newX = newX.toUpperCase();
        char c = newX.charAt(0);
        this.x = ((int)c)-65;
        this.y = y;
        this.sectorType = sectorType;
        moves = new ArrayList<Move>();
    }

    public int xCoordinate(){
        return x;
    }

    public int yCoordinate(){
        return y;
    }

    public String xCoordinateToString(){
        //0 = A, 1 = B, 2 = C etc...
        char c = (char)(x+65);
        return c+"";
    }

    public String yCoordinateToString(){
        //we want to add 1 to y because the grid starts at 1 not 0
        int yy = y+1;
        if(yy>=10)
            return yy+"";
        //else we prepend a 0
        else
            return "0"+yy;
    }

    public int color(){
        switch(sectorType){
            case 0:
                return R.color.invalid;

            case 1:
                return R.color.safe;

            case 2:
                return R.color.unsafe;

            case 3:
                return R.color.alien_start;

            case 4:
                return R.color.human_start;

            case 5:
                return R.color.escape_hatch;
        }
        return Color.WHITE;
    }

    public String getId(){
        //returns an ID of the form A10, B03 etc...
        return xCoordinateToString()+yCoordinateToString();
    }

    public ArrayList<Move> moves(){
        return moves;
    }

    //this function is almost certainly useless
    public static String sectorTypeToString(int sector){
        return sectorTypeMap[sector];
    }
}
