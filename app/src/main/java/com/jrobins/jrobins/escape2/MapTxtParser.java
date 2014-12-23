package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

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

    public static void writeMapToExternalStorage(Activity activity, Map map){

        createExternalStorageFile(activity, map);
    }

    public static void createExternalStorageFile(Activity activity, Map map){

        File maps = new File (activity.getExternalFilesDir(null), "myMaps.txt");

        try {
            FileOutputStream f = new FileOutputStream(maps);
            PrintWriter pw = new PrintWriter(f);
            pw.println(map.toString());
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
