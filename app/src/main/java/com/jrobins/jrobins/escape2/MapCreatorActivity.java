package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;


public class MapCreatorActivity extends Activity implements MapCreatorView.OnCellClickListener{

    private MapCreatorView hexagonMap;
    Map map;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpWindow();
        initializeHexagonMap();
        setUpSaveButton();
    }

    @Override
    protected void onPause(){
        super.onPause();
        if(hexagonMap.threadIsRunning())
            hexagonMap.stopThread();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //this is where we'd restore from cache i think
        if (hexagonMap != null)
            hexagonMap.newThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpWindow(){

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_map_creator);


    }

    private void setUpSaveButton(){
        saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMap();
            }

        });
    }

    private void saveMap(){
        //pop up to enter map name
        getMapName();

    }

    private void getMapName(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Map name");

        // Set up the input
        final EditText input = new EditText(this);
        input.setText(map.name());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set the name
                //need to check at some point if this is null or blank
                map.setName(input.getText().toString());

                //write the map to the file
                writeMapToFile();

                //close the editor
                closeMapEditor();

                //should display some sort of toast or something
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void writeMapToFile(){
        MapTxtParser.writeMapToExternalStorage(this, map);
    }

    private void closeMapEditor(){
        Intent intent = new Intent(MapCreatorActivity.this, HomeScreenActivity.class);
        startActivity(intent);
    }

    private void initializeHexagonMap(){
        hexagonMap = (MapCreatorView) findViewById(R.id.map);
        //don't need to initialize
        this.map = hexagonMap.map();
        hexagonMap.setOnCellClickListener(this);
    }

    //this is what happens when we click hexagons
    @Override
    public void onCellClick(int column, int row)
    {
        //need to check that we are even in the array
        if(column < map.sectors().length && row < map.sectors()[0].length && column>=0 && row >=0) {
            //if the cell is not special
            if (!map.sectors()[column][row].isSpecial()) {
                //hexagonMap.setCell(column, row, !hexagonMap.isCellSet(column, row));
                hexagonMap.changeSectorType(column, row);
            }
        }
    }
}
