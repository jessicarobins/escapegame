package com.jrobins.jrobins.escape2;

/**
 * Created by jrobins on 12/1/2014.
 */
public class Move {
    private Player player; //can use player.color() for color - might need other stuff eventually?
    private int turnNumber;
    private int certainty; //0 = bluff, 1 = certain, 2 = uncertain

    public int color(){
        return player.color();
    }

    public int turnNumber(){
        return turnNumber;
    }
}
