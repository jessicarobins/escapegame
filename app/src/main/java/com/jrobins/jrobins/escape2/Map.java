package com.jrobins.jrobins.escape2;




public class Map {
    private Sector [][] sectors;
    private String name;

    public Map(String name, Sector[][] sectors){
        this.name = name;
        this.sectors = sectors;
    }
}
