package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.GridLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


public class GamePlay extends Activity implements MapView.OnCellClickListener {

    //data structures
    private ArrayList<Player> players;
    private Sector[][] sectors;

    //adapters
    private PlayerSidebarAdapter playerSidebarAdapter;

    //views
    private ListView playerListView;
    private MapView hexagonMap;
    private TableLayout mapGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_play);


        initializePlayers();
        createTestMap(5, 3);
        initializeHexagonMap();
        initializeMapGrid();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_play, menu);
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

    //this is what happens when we click hexagons
    @Override
    public void onCellClick(int column, int row)
    {
        hexagonMap.setCell(column, row, !hexagonMap.isCellSet(column, row));
    }

    private void initializeHexagonMap(){
        hexagonMap = (MapView) findViewById(R.id.hexagonMap);
        //hexagonMap.initialize(3, 5);
        hexagonMap.initialize(sectors);
        hexagonMap.setOnCellClickListener(this);
    }

    private void initializePlayers(){
        //gets the player data from the previous screen
        players = getIntent().getParcelableArrayListExtra("players");
        for(int i = 0; i< players.size(); i++){
            System.out.println("player " + i + " = " + players.get(i).name());
        }

        //sets up the sidebar with player info
        playerSidebarAdapter = new PlayerSidebarAdapter(this, players);
        playerListView = (ListView) findViewById(R.id.playerList);
        playerListView.setAdapter(playerSidebarAdapter);
    }

    /*
    private void initializeMapGrid(){

        mapGrid = (GridLayout) findViewById(R.id.map_grid);
        int rows = sectors.length;
        int cols = sectors[0].length;
        mapGrid.setColumnCount(cols);
        mapGrid.setRowCount(rows);

        GridLayout.Spec row;
        GridLayout.Spec col;
        GridLayout.LayoutParams gridLayoutParam;




        for(int i = 0; i<rows; i++){
            row = GridLayout.spec(i);
            for (int j = 0; j<cols; j++){
                col = GridLayout.spec(j);
                gridLayoutParam = new GridLayout.LayoutParams(row, col);
                TextView text = new TextView(this);
                text.setText(sectors[i][j].getId());
                text.setBackgroundColor(Color.YELLOW);
                text.setGravity(Gravity.CENTER);

                mapGrid.addView(text,gridLayoutParam);
            }
        }
    }
    */

    private void initializeMapGrid(){
        mapGrid = (TableLayout) findViewById(R.id.map_grid);
        int rows = sectors.length;
        int cols = sectors[0].length;

        TextView text;
        TableRow row;
        TableLayout.LayoutParams params;
        for(int i = 0; i<rows; i++){
            row = new TableRow(this);
            params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
            //params.weight = 1;

            row.setLayoutParams(params);
            row.setGravity(Gravity.CENTER_VERTICAL);
            for (int j = 0; j<cols; j++){
                text = new TextView(this);
                text.setText(sectors[i][j].getId());
                text.setGravity(Gravity.CENTER);

                row.addView(text);
            }
            mapGrid.addView(row);
        }
    }

    private void createTestMap(int x, int y){
        //create an x by y test map of sectors
        System.out.println("creating a new grid with x = " + x + " & y = " + y);
        sectors = new Sector[x][y];
        for(int i = 0; i<x; i++){
            for (int j = 0; j<y; j++){
                sectors[i][j] = new Sector(i,j,j%5);
            }
        }
    }
}
