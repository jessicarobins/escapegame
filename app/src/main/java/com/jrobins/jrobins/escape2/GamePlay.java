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
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
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
        createTestMap(3,5);
        initializeHexagonMap();
        //initializeMapGrid();

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
        //hexagonMap.initialize(5,10);
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



    private void initializeMapGrid(){
        mapGrid = (TableLayout) findViewById(R.id.map_grid);
        int cols = sectors.length;
        int sectorRows = sectors[0].length;
        int rows = sectorRows*2;

        TextView text;
        GridView moveGrid;
        TableRow row;
        TableLayout.LayoutParams tableParams;

        int yText; //the sector value for x, based on i, j


        //j is the number of ROWS which means it is the Y VALUE OMG wtf WHY
        for(int j = 0; j<rows; j++){
            row = new TableRow(this);
            tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT, 1.0f);
            //params.weight = 1;

            row.setLayoutParams(tableParams);
            row.setGravity(Gravity.CENTER);


            //i is the number of COLUMNS which means it is the X VALUE HOLY SHIT
            for (int i = 0; i<cols; i++){
                //if THE X VALUE IS EVEN (so this is A, C, E etc)
                //  then the new Y value, will just be the Y-value of the table divided
                //  by two
                if((i%2)==0) {
                    yText = j/2;
                }
                //if THE X VALUE IS ODD (that's B, D, F etc)
                //  the y-value in the table we are displaying is HIGHER than the y-value in
                //  the sector array. so we have to make sure we aren't getting something
                //  less than zero from the sector array
                else {
                    yText = (j -1) / 2;
                }
                //if i+j is even, we want to put the sector name
                //we need to make sure it's not the very last row because that will
                //  arrayindexoutofbounds our asses
                if( (yText>=0) && (yText<sectorRows) && ((i+j)%2 == 0) && (j<(rows-1))) {

                    text = new TextView(this);
                    text.setText(sectors[i][yText].getId());
                    text.setGravity(Gravity.CENTER);
                    text.setBackgroundColor(Color.YELLOW);
                    row.addView(text);
                }


                //i think ygrid is never going to be less than 0 for odd rows but
                //  we don't want to print the first row either

                else if ( (j>0) && (yText>=0) && ((i+j)%2==1) ) {
                    //check to make sure there are any moves before we do anything
                    if (!sectors[i][yText].moves().isEmpty()) {
                        //get the moves
                        moveGrid = new GridView(this);
                        moveGrid.setNumColumns(5);

                        moveGrid.setAdapter(new MoveGridAdapter(this, sectors[i][yText].moves()));
                        //add the view to the row
                        row.addView(moveGrid);
                    }
                    //if the moves array is empty
                    else {
                        text = new TextView(this);
                        text.setText("blank");
                        text.setGravity(Gravity.CENTER);
                        text.setBackgroundColor(Color.MAGENTA);
                        row.addView(text);
                    }
                }
                else {
                        text = new TextView(this);
                        text.setText("blank");
                        text.setGravity(Gravity.CENTER);
                        text.setBackgroundColor(Color.GREEN);
                        row.addView(text);
                }
            }
            mapGrid.addView(row);
        }
    }

    private void createTestMap(int cols, int rows){
        //create an x by y test map of sectors

        sectors = new Sector[cols][rows];
        for(int i = 0; i<cols; i++){
            for(int j = 0; j<rows; j++){
                sectors[i][j] = new Sector(i, j, j%5);
                sectors[i][j].addMoves(createRandomArrayOfMoves());
            }
        }
    }

    private Move createTestMove(Player p){
        //create a random move for a given player
        int certainty =  (int)(Math.random()*2);
        int turnNumber = 1 + (int)(Math.random()*10);
        return new Move(p, turnNumber, certainty);
    }

    private ArrayList<Move> createRandomArrayOfMoves(){
        //create a random array of moves (for testing purposes)
        ArrayList<Move> moves = new ArrayList<Move>();
        int playerIndex = 0;
        int numberOfMoves = (int)(Math.random()*5);
        for(int i = 0; i < numberOfMoves; i++) {
            moves.add(createTestMove(players.get(playerIndex)));
            if(playerIndex == players.size())
                playerIndex = 0;
            else
                playerIndex++;
        }
        return moves;
    }
}
