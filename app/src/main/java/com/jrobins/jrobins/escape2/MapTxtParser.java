package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jessica.robins on 12/23/2014.
 */
public class MapTxtParser {


    /*
    public MapTxtParser(Activity activity, int id){

    }

    public MapTxtParser(Activity activity){
        //this(activity, R.raw.my_maps);
    }
    */
    public static ArrayList<Map> readMaps(Activity activity, int id){
        InputStream inputStream = activity.getResources().openRawResource(id);
        ArrayList<Map> maps = new ArrayList<Map>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int lineNumber = 0;
        int mapNumber = 0;
        try {
            while (( line = reader.readLine()) != null) {
                //if line number is even, it's a name
                if(lineNumber%2 == 0){
                    maps.get(mapNumber).setName(line);
                }
                //otherwise it's the map data
                else {
                    maps.get(mapNumber).setSectors(readSectors(line));
                    mapNumber++;
                }
                lineNumber++;
            }
        } catch (IOException e) {
            return null;
        }

        return maps;
    }


    private static Sector[][] readSectors(String line){
        Sector[][]sectors = new Sector[23][14];
        int sectorType = 0;
        int index = 0; //index in the string
        for(int col = 0; col < 23; col++){
            for(int row = 0; row < 14; row++){
                sectorType = Character.getNumericValue(line.charAt(index));
                sectors[col][row] = new Sector(col, row, sectorType);
                index++;
            }
        }
        return sectors;
    }

    public static List<MapPack> readMapPacksFromInternalStorage(Activity activity){

        List<MapPack> mapPacks = new ArrayList<MapPack>();
        //MapPack mapPack;
        List<Map> maps;
        Map map;
        String mapPackName = new String();
        int mapPackNumber = 1;

        //this returns 0 when id is not found. use that.
        int resId = activity.getResources().getIdentifier("raw/maps_pack_"+mapPackNumber, null, activity.getPackageName());

        while (resId != 0){
            //mapPack = new MapPack();
            maps = new ArrayList<Map>();
            InputStream is = activity.getResources().openRawResource(resId);


            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(is));
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            String line;
            int lineNumber = 0;
            map = new Map();
            try {
                while ((line = reader.readLine()) != null) {
                    Log.d("line", line);
                    //if it's the very first line, it's the name of the maps pack
                    if (lineNumber == 0) {
                        mapPackName = line;
                    }
                    //if line number is odd, it's a name
                    else if (lineNumber % 2 != 0) {
                        map.setName(line);
                    }
                    //otherwise it's the map data
                    else {
                        map.setSectors(readSectors(line));
                        maps.add(new Map(map));
                    }
                    lineNumber++;
                }
            } catch (IOException e) {
                return null;
            }
            //mapPack.setMaps(maps);
            mapPacks.add(new MapPack(mapPackName, maps));
            mapPackNumber++;
            resId = activity.getResources().getIdentifier("raw/maps_pack_"+mapPackNumber, null, activity.getPackageName());
        }
        return mapPacks;
    }

    public static List<Map> readMapsFromInternalStorage(Activity activity){


        List<Map> maps = new ArrayList<Map>();
        InputStream is = activity.getResources().openRawResource(R.raw.maps_pack_1);


        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        String line;
        int lineNumber = 0;
        Map map = new Map();
        try {
            while (( line = reader.readLine()) != null) {
                Log.d("line", line);

                //if line number is even, it's a name
                if(lineNumber%2 == 0){
                    map.setName(line);
                }
                //otherwise it's the map data
                else {
                    map.setSectors(readSectors(line));
                    maps.add(new Map(map));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            return null;
        }

        return maps;
    }

    public static List<Map> readMapsFromExternalStorage(Activity activity){
        if(!hasExternalStoragePrivateFile(activity))
            return null;

        List<Map> maps = new ArrayList<Map>();
        File mapFile = new File (activity.getExternalFilesDir(null), "myMaps.txt");

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(mapFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        String line;
        int lineNumber = 0;
        Map map = new Map();
        try {
            while (( line = reader.readLine()) != null) {
                Log.d("line", line);
                //if line number is even, it's a name
                if(lineNumber%2 == 0){
                    map.setName(line);
                }
                //otherwise it's the map data
                else {
                    map.setSectors(readSectors(line));
                    maps.add(new Map(map));
                }
                lineNumber++;
            }
        } catch (IOException e) {
            return null;
        }

        return maps;
    }

    public static void writeMapToExternalStorage(Activity activity, Map map){

        createExternalStorageFile(activity, map);
    }

    public static void createExternalStorageFile(Activity activity, Map map){

        File maps = new File (activity.getExternalFilesDir(null), "myMaps.txt");

        try {
            FileOutputStream f = new FileOutputStream(maps,true);
            PrintWriter pw = new PrintWriter(f);

            pw.append(map.toString()+"\n");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i("files", "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("files", "File written");
    }

    private static boolean hasExternalStoragePrivateFile(Activity activity) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(activity.getExternalFilesDir(null), "myMaps.txt");
        if (file != null) {
            return file.exists();
        }
        return false;
    }
}
