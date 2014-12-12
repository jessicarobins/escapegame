package com.jrobins.jrobins.escape2;

/**
 * Created by jrobins on 12/1/2014.
 */
public class Move {
    private Player player; //can use player.color() for color - might need other stuff eventually?
    private int turnNumber;
    private int certainty; //0 = bluff, 1 = certain, 2 = uncertain
    final public static int BLUFF = 0;
    final public static int CERTAIN = 1;
    final public static int UNCERTAIN = 2;

    public Move(Player p, int turnNumber, int certainty){
        this.player = p;
        this.turnNumber = turnNumber;
        this.certainty = certainty;
    }

    public int color(){
        return player.color();
    }

    public int turnNumber(){
        return turnNumber;
    }

    public String turnNumberToString(){
        if(turnNumber<10)
            return "0"+turnNumber;
        else
            return ""+turnNumber;
    }

    public int certainty(){
        return certainty;
    }

    public void setPlayer(Player p){
        this.player = p;
    }

    public void setTurnNumber(int turnNumber){
        this.turnNumber = turnNumber;
    }

    public void setCertainty(int certainty){
        this.certainty = certainty;
    }

}
