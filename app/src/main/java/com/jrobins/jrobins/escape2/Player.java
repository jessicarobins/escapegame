package com.jrobins.jrobins.escape2;

/**
 * Created by jrobins on 11/29/2014.
 */
public class Player {
    private String name;
    private int color;

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
}
