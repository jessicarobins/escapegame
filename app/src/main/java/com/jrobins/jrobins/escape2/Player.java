package com.jrobins.jrobins.escape2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jrobins on 11/29/2014.
 */
public class Player implements Parcelable {
    private String name;
    private int color;

    public Player(Parcel source) {
        name = source.readString();
        color = source.readInt();
    }

    Player(){

    }

    Player (String name){
        setName(name);
    }

    Player (String name, int color){
        setName(name);
        setColor(color);
    }

    public String name(){
        return name;
    }

    public int color() {
        return color;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setColor(int color){
        this.color = color;
    }

    public String toString(){
        return name;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(color);
    }

    public static final Parcelable.Creator<Player> CREATOR
            = new Parcelable.Creator() {
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}
