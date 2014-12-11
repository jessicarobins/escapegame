package com.jrobins.jrobins.escape2;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by jrobins on 12/1/2014.
 */
public class Sector implements Parcelable{
    private int x; //this represents A-Z, the x coordinate
    private int y;
    final public static int INVALID = 0;
    final public static int SAFE = 1;
    final public static int UNSAFE = 2;
    final public static int ALIEN_START = 3;
    final public static int HUMAN_START = 4;
    final public static int ESCAPE_HATCH = 5;

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

    public Sector(Parcel in) {
        moves = new ArrayList<Move>();
        this.x = in.readInt();
        this.y = in.readInt();
        this.sectorType = in.readInt();
    }

    public Sector(int x, int y, int sectorType){
        this.x = x;
        this.y = y;
        this.sectorType = sectorType;
        moves = new ArrayList<Move>();
    }

    public Sector(int x, int y, String sectorType){
        this.x = x;
        this.y = y;
        this.sectorType = sectorTypeToInt(sectorType);
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

    public String label(){
        switch(sectorType()){
            case 0: return null;
            case 1: case 2: return  getId();
            case 3: return "A";
            case 4: return "H";
            case 5: return "E";
        }
        return null;
    }

    public ArrayList<Move> moves(){
        return moves;
    }

    //this function is almost certainly useless
    public static String sectorTypeToString(int sector){
        return sectorTypeMap[sector];
    }

    public static int sectorTypeToInt(String sector){
        if(sector.equals("invalid"))
            return 0;
        else if (sector.equals("safe"))
            return 1;
        else if (sector.equals("unsafe"))
            return 2;
        else if (sector.contains("alien"))
            return 3;
        else if (sector.contains("human"))
            return 4;
        else if (sector.contains("escape"))
            return 5;
        else {
            Log.d("sector", "invalid sector type " + sector);
            return -1;
        }
    }

    public void addMoves(ArrayList<Move> moves){
        this.moves.addAll(moves);
    }

    public void addMove(Move move){
        this.moves.add(move);
    }

    public void removeLastMove(){
        moves.remove(moves.size()-1);
    }

    public boolean isValid(){
        //returns true if this is a sector that's not invalid (i.e., sectorType!=0)
        return (sectorType !=0);
    }

    public boolean isAlienStart(){
        return (sectorType == 3);
    }

    public boolean isHumanStart(){
        return (sectorType == 4);
    }

    public boolean isEscapeHatch(){
        return (sectorType == 5);
    }

    public boolean isSpecial(){
        return (sectorType > 2 );
    }

    public boolean isNormal() {
        return (sectorType == 1 || sectorType == 2);
    }

    public int sectorType(){
        return sectorType;
    }

    public void setSectorType (String sectorType){
        this.sectorType = sectorTypeToInt(sectorType);
    }

    public void setSectorType (int sectorType){
        this.sectorType = sectorType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.x);
        dest.writeInt(this.y);
        dest.writeInt(this.sectorType);
    }

    public static final Parcelable.Creator<Sector> CREATOR
            = new Parcelable.Creator() {
        public Sector createFromParcel(Parcel in) {
            return new Sector(in);
        }

        public Sector[] newArray(int size) {
            return new Sector[size];
        }
    };

}
