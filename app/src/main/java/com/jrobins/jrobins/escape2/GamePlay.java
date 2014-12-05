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
    private int turnNumber; //this is really the round but they call it turn in the game
    private int currentPlayer; //the index of the  player whose turn it is within the round (turn)...

    //adapters
    private PlayerSidebarAdapter playerSidebarAdapter;

    //views
    private ListView playerListView;
    private MapView hexagonMap;
    private TableLayout mapGrid;
    private TextView turnNumberTextBox;
    private Button advanceTurnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_play);


        initializePlayers();
        createTestMap(4,5);
        initializeHexagonMap();
        setUpTurnLogic();

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
        //if the cell is not invalid - we don't want invalid sectors to be clickable
        if (sectors[column][row].isValid())
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

    /******* turn logic**********/
    private void setUpTurnLogic(){
        turnNumber = 1;
        currentPlayer = 0;
        players.get(currentPlayer).setTurn(true);
        turnNumberTextBox = (TextView) findViewById(R.id.turnNumber);
        advanceTurnButton = (Button) findViewById(R.id.advance_turn);

        advanceTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advanceTurn();
            }

        });
    }

    private void advanceTurn() {

        //remove current player's turn
        players.get(currentPlayer).setTurn(false);

        //if the player is the last one in the array, increment the turn count
        if(currentPlayer == (players.size()-1)) {
            //increment turn number
            turnNumber++;

            //sent the currentturn array index back to 0
            currentPlayer = 0;

            //increment the number that's at the top

            turnNumberTextBox.setText(turnNumber+"");
            // do we need to redraw after this?
        }
        else
            currentPlayer++;

        //set next player's turn
        players.get(currentPlayer).setTurn(true);

    }

    /******* testing stuff*******/

    private void createTestMap(int cols, int rows){
        //create an x by y test map of sectors

        sectors = new Sector[cols][rows];
        for(int i = 0; i<cols; i++){
            for(int j = 0; j<rows; j++){
                sectors[i][j] = new Sector(i, j, j%6);
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
}
