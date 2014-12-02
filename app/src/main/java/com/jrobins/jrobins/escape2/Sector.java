package com.jrobins.jrobins.escape2;

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
        if(y>=10)
            return y+"";
        //else we prepend a 0
        else
            return "0"+y;
    }

    public String getId(){
        //returns an ID of the form A10, B03 etc...
        return xCoordinateToString()+yCoordinateToString();
    }

    //this function is almost certainly useless
    public static String sectorTypeToString(int sector){
        return sectorTypeMap[sector];
    }
}
