package com.jrobins.jrobins.escape2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jessica.robins on 3/25/2015.
 */
public class MapPack {
    List<Map> maps;
    String name;

    public MapPack(){
        //this.name = new String();
        //this.maps = new ArrayList<Map>();
    }

    public MapPack (MapPack mp){
        this.name = mp.name();
        this.maps = mp.maps();
    }

    public MapPack(String name, List <Map> maps){
        this.name = name;
        this.maps = maps;
    }

    public String name(){
        return name;
    }

    public List<Map> maps(){
        return maps;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setMaps(List<Map> maps){
        this.maps = maps;
    }
}
