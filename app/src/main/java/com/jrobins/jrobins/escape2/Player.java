package com.jrobins.jrobins.escape2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jrobins on 11/29/2014.
 */
public class Player implements Parcelable {
    private String name;
    private int color;
    private int human; //0 = dunno, 1 = human, 2 = alien
    boolean currentTurn;

    public Player(Parcel source) {
        name = source.readString();
        color = source.readInt();
    }

    public Player(){
    }

    public Player (String name){
        setName(name);
    }

    public Player (String name, int color){
        setName(name);
        setColor(color);
    }

    public String name(){
        return name;
    }

    public int color() {
        return color;
    }

    public int human () {
        return human;
    }

    public boolean isHuman() {
        return (human == 1);
    }

    public boolean isAlien() {
        return (human == 2);
    }

    public boolean turn() {
        return currentTurn;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setColor(int color){
        this.color = color;
    }

    public void setHumanity(int human){
        this.human = human;
    }

    public void setAlien(){
        this.human = 2;
    }

    public void setHuman(){
        this.human=1;
    }

    public void setHumanityUndecided(){
        this.human=0;
    }

    public void setTurn(boolean turn){
        this.currentTurn = turn;
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
