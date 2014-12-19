package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GamePlay extends Activity implements MapView.OnCellClickListener {

    //data structures
    private ArrayList<Player> players;
    private Map map;
    //private Sector[][] sectors;
    private int turnNumber; //this is really the round but they call it turn in the game
    private int prevTurnNumber; //so we can edit previous turns
    private int currentPlayer; //the index of the  player whose turn it is within the round (turn)...

    //adapters
    private PlayerSidebarAdapter playerSidebarAdapter;

    //views
    private ListView playerListView;
    private MapView hexagonMap;
    private TextView turnNumberTextBox;
    private TextView prevTurnNumberTextBox;
    private Button advanceTurnButton;
    private Button prevTurnButton;
    //private LinearLayout sidebar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPlayerList();

        setUpWindow();
        initializePlayers();
        initializeMap();
        //createTestMap(4,5);
        initializeHexagonMap();
        setUpTurnLogic();

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(hexagonMap.threadIsRunning())
            hexagonMap.stopThread();

    }

    @Override
    protected void onResume(){
        super.onResume();
        //this is where we'd restore from cache i think
        if(hexagonMap != null)
            hexagonMap.newThread();

        else {
            //initializeMap();
            initializeHexagonMap();
        }


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
        Move m;

        //need to check that we are even in the array
        if(column < map.sectors().length && row < map.sectors()[0].length && column>=0 && row >=0) {
            //if the cell is not invalid - we don't want invalid sectors to be clickable
            if (map.sectors()[column][row].isNormal()) {
                //hexagonMap.setCell(column, row, !hexagonMap.isCellSet(column, row));

                //get the current player
                for(int i = 0; i < playerSidebarAdapter.getCount(); i++){
                    if (playerSidebarAdapter.getItem(i).turn()) {
                        currentPlayer = i;
                        break;
                    }
                }
                m = new Move(playerSidebarAdapter.getItem(currentPlayer), prevTurnNumber, Move.CERTAIN);
                /*
                int i = map.sectors()[column][row].moves().indexOf(m);
                if (i>=0){
                    map.sectors()[column][row].moves().get(i).incrementCertainty();
                }
                else {
                    map.sectors()[column][row].moves().add(m);
                }*/
                hexagonMap.setCell(column, row, m);
                //hexagonMap.setCell(column, row, (hexagonMap.isCellSet(column, row)+1)%3, new Move(playerSidebarAdapter.getItem(currentPlayer), prevTurnNumber, Move.CERTAIN));
            }
        }
    }


    private void setUpWindow(){

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_play);

        //set width of sidebar based on screen width

        //sidebar = (LinearLayout) findViewById(R.id.sidebar);
        playerListView = (ListView) findViewById(R.id.playerList);
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);

        int w = size.x/(25 - (players.size()-4));
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getResources().getDisplayMetrics());
        playerListView.getLayoutParams().width = width;
        
    }

    private void initializeHexagonMap(){
        hexagonMap = (MapView) findViewById(R.id.hexagonMap);
        //hexagonMap.initialize(5,10);
        hexagonMap.initialize(map.sectors());
        hexagonMap.setOnCellClickListener(this);
    }

    private void getPlayerList(){
        //gets the player data from the previous screen
        players = getIntent().getParcelableArrayListExtra("players");
    }

    private void initializePlayers(){

        //sets up the sidebar with player info
        playerSidebarAdapter = new PlayerSidebarAdapter(this, players);

        playerListView.setAdapter(playerSidebarAdapter);
    }

    private void initializeMap(){
        map = getIntent().getParcelableExtra("map");
        setTitle(map.name());
    }

    /******* turn logic**********/
    private void setUpTurnLogic(){
        turnNumber = 1;
        prevTurnNumber = 1;
        currentPlayer = 0;
        //players.get(currentPlayer).setTurn(true);
        playerSidebarAdapter.getItem(currentPlayer).setTurn(true);
        prevTurnNumberTextBox = (TextView) findViewById(R.id.prevTurnNumber);
        turnNumberTextBox = (TextView) findViewById(R.id.turnNumber);
        advanceTurnButton = (Button) findViewById(R.id.advance_turn);
        prevTurnButton = (Button) findViewById(R.id.previous_turn);
        turnNumberTextBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetToCurrentTurn();
            }
        });
        advanceTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advanceTurn();
            }

        });

        prevTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prevTurnNumber>1)
                    prevTurn();
            }

        });
    }

    private void resetToCurrentTurn(){
        resetAllPlayerTurns();

        prevTurnNumber = turnNumber;

        prevTurnNumberTextBox.setText(prevTurnNumber+"");

    }

    private void prevTurn() {
        resetAllPlayerTurns();

        //hexagonMap.loadPreviousCellSet(prevTurnNumber-1, (prevTurnNumber != turnNumber));

        prevTurnNumber--;

        prevTurnNumberTextBox.setText(prevTurnNumber+"");

    }

    private void advanceTurn(){
        //if we are at the very end, the right arrow advances the round
        if(prevTurnNumber == turnNumber)
            advanceRound();
        else{
            prevTurnNumber++;
            prevTurnNumberTextBox.setText(prevTurnNumber+"");
        }
    }

    //if we are at the very end, the right arrow advances the round
    private void advanceRound() {
        resetAllPlayerTurns();

        //reset the boolean values in the map
        //hexagonMap.resetAllCells( (prevTurnNumber != turnNumber) );

        //increment turn number
        turnNumber++;
        prevTurnNumber = turnNumber;

        //increment the number that's at the top
        turnNumberTextBox.setText(turnNumber+"");
        prevTurnNumberTextBox.setText(prevTurnNumber+"");

    }

    /******* testing stuff*******/

    private void createTestMap(int cols, int rows){
        //create an x by y test map of sectors

        map.setSectors(new Sector[cols][rows]);
        for(int i = 0; i<cols; i++){
            for(int j = 0; j<rows; j++){
                map.sectors()[i][j] = new Sector(i, j, j%6);
                //sectors[i][j].addMoves(createRandomArrayOfMoves());
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
        int numberOfMoves = (int)(Math.random()*12);
        for(int i = 0; i < numberOfMoves; i++) {
            moves.add(createTestMove(players.get(playerIndex)));
            if(playerIndex == (players.size()-1))
                playerIndex = 0;
            else
                playerIndex++;
        }
        return moves;
    }

    private void resetAllPlayerTurns(){
        for(int i = 0; i < playerSidebarAdapter.getCount(); i++) {
            playerSidebarAdapter.getItem(i).setTurn(false);
        }

        currentPlayer = 0;
        playerSidebarAdapter.getItem(currentPlayer).setTurn(true);

        playerSidebarAdapter.notifyDataSetChanged();
        playerListView.setSelectionAfterHeaderView();
    }
}
