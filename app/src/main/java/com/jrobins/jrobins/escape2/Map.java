package com.jrobins.jrobins.escape2;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Map implements Parcelable {
    private Sector [][] sectors;
    private String name;

    public Map(){
        this.name = "My Super Awesome Map";
        this.sectors = initializeDefaultSectors();
    }

    public Map (Map map){
        this.name = map.name;
        this.sectors = map.sectors;
    }


    public Map(String name, Sector[][] sectors){
        this.name = name;
        this.sectors = sectors;
    }

    public Map(Parcel source) {
        name = source.readString();
        int cols = source.readInt();
        int rows = source.readInt();
        sectors = new Sector[cols][rows];
        for (int col = 0; col < cols; col++){
            for(int row = 0; row < rows; row++){
                sectors[col][row] = source.readParcelable(Sector.class.getClassLoader());
            }
        }

    }

    public Sector[][] sectors(){
        return sectors;
    }

    public String name(){
        return name;
    }

    public String toString() {
        StringBuilder mapString = new StringBuilder();

        mapString.append(name+"\n");
        for(int col = 0; col < sectors.length; col++){
            for(int row = 0; row < sectors[0].length;row++){
                mapString.append(sectors[col][row].sectorType());
            }
        }
        return mapString.toString();
    }

    public void setName(String name){
        this.name = name;
    }

    public void setSectors( Sector[][]sectors){
        this.sectors = sectors;
    }

    public Sector[][] initializeDefaultSectors(){
        Sector[][] sectors = new Sector[23][14];
        //set alien start
        sectors[11][5] = new Sector(11, 5, Sector.ALIEN_START);

        //set human start
        sectors[11][7] = new Sector(11, 7, Sector.HUMAN_START);

        //set 4 escape hatches
        sectors[2][2] = new Sector(2, 2, Sector.ESCAPE_HATCH);
        sectors[2][11] = new Sector(2, 11, Sector.ESCAPE_HATCH);
        sectors[20][2] = new Sector(20, 2, Sector.ESCAPE_HATCH);
        sectors[20][11] = new Sector(20, 11, Sector.ESCAPE_HATCH);

        //initialize the rest as invalid

        for(int col = 0; col < sectors.length; col++){
            for(int row = 0; row < sectors[0].length; row++){
                if(sectors[col][row] == null){
                    sectors[col][row] = new Sector(col, row, Sector.INVALID);
                }
            }
        }

        return sectors;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        //just write in the number of rows and columns. is this cheating? who knows
        dest.writeInt(sectors.length);
        dest.writeInt(sectors[0].length);
        for (int col = 0; col < sectors.length; col++){

            //dest.writeTypedList(new ArrayList(Arrays.asList(sectors[col])));
            for(int row = 0; row < sectors[0].length; row++){
                dest.writeParcelable(sectors[col][row],0);
            }

        }

        /*
        for(int col = 0; col<sectors.length; col++){
            for(int row = 0; row<sectors[0].length; row++){
                s = sectors[col][row];
                dest.writeInt(s.xCoordinate());
                dest.writeInt(s.yCoordinate());
                dest.writeInt(s.sectorType());

            }
        }*/
    }

    public static final Parcelable.Creator<Map> CREATOR
            = new Parcelable.Creator() {
        public Map createFromParcel(Parcel in) {
            return new Map(in);
        }

        public Map[] newArray(int size) {
            return new Map[size];
        }
    };
}
