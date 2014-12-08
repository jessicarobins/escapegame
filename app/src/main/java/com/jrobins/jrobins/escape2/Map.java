package com.jrobins.jrobins.escape2;


import android.os.Parcel;
import android.os.Parcelable;

public class Map implements Parcelable {
    private Sector [][] sectors;
    private String name;

    public Map(String name, Sector[][] sectors){
        this.name = name;
        this.sectors = sectors;
    }

    public Map(Parcel source) {
        name = source.readString();
        for(int col = 0; col<sectors.length; col++){
            for(int row = 0; row<sectors[0].length; row++){
                sectors[col][row] = new Sector(source.readInt(), source.readInt(), source.readInt());

            }
        }

    }

    public Sector[][] sectors(){
        return sectors;
    }

    public String name(){
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        Sector s;
        for(int col = 0; col<sectors.length; col++){
            for(int row = 0; row<sectors[0].length; row++){
                s = sectors[col][row];
                dest.writeInt(s.xCoordinate());
                dest.writeInt(s.yCoordinate());
                dest.writeInt(s.sectorType());

            }
        }
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
