package com.jrobins.jrobins.escape2;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jrobins on 11/29/2014.
 */
public class Player implements Parcelable {
    private String name;
    private int color;
    private int human = 2; //0 = human, 1 = alien, 2 = dunno, 3 = dead

    public final static int UNCERTAIN = 2;
    public final static int HUMAN = 0;
    public final static int ALIEN = 1;
    public final static int DEAD = 3;

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

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Player))
            return false;
        Player p = (Player)o;
        if(!p.name().equals(name))
            return false;
        if(p.color() != color)
            return false;
        return true;
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



    public boolean isDead() {
        return (human == Player.DEAD);
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
