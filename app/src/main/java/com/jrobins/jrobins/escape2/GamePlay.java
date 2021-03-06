package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class GamePlay extends Activity implements MapView.OnCellClickListener {

    //max round number constant
    private final int MAX_ROUNDS = 39;

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
    private Button advanceAbsoluteTurnButton;
    private LinearLayout advanceAbsoluteTurnLayout;
    private Button editButton;
    private LinearLayout editPanel;

    //are we editing?
    private boolean editing = false;

    //preferences
    private boolean showDeadPlayerMoves = true;
    private boolean dimOldMoves = true;
    private boolean dimOtherPlayerMoves = true;
    SharedPreferences.OnSharedPreferenceChangeListener listener;

    //private LinearLayout sidebar;

    //to deal with screen being locked
    private BroadcastReceiver mReceiver;
    private boolean locked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {

                // fix our issues for sharedpreferences
                SharedPreferences sp = getSharedPreferences("current_game", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("activeGame", false);
                ed.commit();

                // Handle everthing else
                defaultHandler.uncaughtException(thread, throwable);
            }
        });


        setUpReceiver();
        getPreferences();
        getPlayerList();

        setUpWindow();
        setUpSidebar();
        initializePlayers();
        initializeMap();
        //createTestMap(4,5);
        initializeHexagonMap();
        setUpTurnLogic();


    }


    @Override
    protected void onStart() {
        super.onStart();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("current_game", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("activeGame", true);
        ed.apply();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Store our shared preference
        SharedPreferences sp = getSharedPreferences("current_game", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putBoolean("activeGame", false);
        ed.apply();
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

        if(locked) {
            System.out.println("screen was previously locked");
            locked = false;
            startActivity(new Intent(GamePlay.this, HomeScreenActivity.class));
        }
        else {

            //this is where we'd restore from cache i think
            if (hexagonMap != null)
                hexagonMap.newThread();

            else {
                //initializeMap();
                initializeHexagonMap();
            }
        }

    }

    //when pressing the back button, we want to go all the way back to the home screen
    //  (as opposed to back to the map creator screen)
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(GamePlay.this, HomeScreenActivity.class);

        startActivity(intent);
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
                m = new Move(playerSidebarAdapter.getItem(currentPlayer), prevTurnNumber, Move.UNCERTAIN);
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


    }

    private void getPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        showDeadPlayerMoves = sharedPref.getBoolean(SettingsActivity.SHOW_DEAD_PLAYER_MOVES,true);
        dimOldMoves = sharedPref.getBoolean(SettingsActivity.DIM_OLD_MOVES, true);
        dimOtherPlayerMoves = sharedPref.getBoolean(SettingsActivity.DIM_OTHER_PLAYER_MOVES, true);
        listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        // listener implementation
                        if(key.equals(SettingsActivity.SHOW_DEAD_PLAYER_MOVES)) {
                            showDeadPlayerMoves = prefs.getBoolean(key, true);
                            hexagonMap.setPrefShowDeadPlayerMoves(showDeadPlayerMoves);
                        }
                        else if(key.equals(SettingsActivity.DIM_OLD_MOVES)) {
                            dimOldMoves = prefs.getBoolean(key, true);
                            hexagonMap.setPrefDimOldMoves(dimOldMoves);
                        }
                        else if(key.equals(SettingsActivity.DIM_OTHER_PLAYER_MOVES)) {
                            dimOtherPlayerMoves = prefs.getBoolean(key, true);
                            hexagonMap.setPrefDimOtherPlayerMoves(dimOtherPlayerMoves);
                        }
                    }
                };
        sharedPref.registerOnSharedPreferenceChangeListener(listener);


    }

    private void setUpSidebar(){
        playerListView = (ListView) findViewById(R.id.playerList);
        Point size = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(size);

        //int w = size.x/(25 - (players.size()-4));
        int n = Math.max(players.size(),4);
        int w = size.y/(n+1);
        //int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, w, getResources().getDisplayMetrics());
        playerListView.getLayoutParams().width = w;
    }

    private void initializeHexagonMap(){
        hexagonMap = (MapView) findViewById(R.id.hexagonMap);
        //hexagonMap.initialize(5,10);
        hexagonMap.initialize(map.sectors());
        hexagonMap.setOnCellClickListener(this);

        //get the preferences
        hexagonMap.setPrefShowDeadPlayerMoves(showDeadPlayerMoves);
        hexagonMap.setPrefDimOldMoves(dimOldMoves);
        hexagonMap.setPrefShowDeadPlayerMoves(showDeadPlayerMoves);
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




        setUpAbsTurnBox();
        setUpAdvanceAbsTurnButton();
        setUpAdvanceTurnButton();
        setUpPrevTurnButton();
        setUpEditButton();
    }

    private void setUpAbsTurnBox(){
        LinearLayout absTurnBox = (LinearLayout) findViewById(R.id.absolute_turn_box);
        absTurnBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                resetToCurrentTurn();
            }
        });
    }

    private void setUpAdvanceAbsTurnButton(){
        advanceAbsoluteTurnButton = (Button) findViewById(R.id.advance_absolute_turn);

        advanceAbsoluteTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advanceTurn();
            }

        });
    }

    private void setUpPrevTurnButton(){
        prevTurnButton = (Button) findViewById(R.id.previous_turn);
        prevTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prevTurnNumber>1)
                    prevTurn();
            }

        });
    }

    private void setUpAdvanceTurnButton(){
        advanceTurnButton = (Button) findViewById(R.id.advance_turn);
        advanceTurnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advanceEditTurn();
            }

        });
    }

    private void setUpEditButton(){
        editButton = (Button) findViewById(R.id.edit);
        editPanel = (LinearLayout) findViewById(R.id.editPanel);
        advanceAbsoluteTurnLayout = (LinearLayout) findViewById(R.id.advance_absolute_turn_layout);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editing){
                    //hide the panel
                    editPanel.setVisibility(View.GONE);
                    advanceAbsoluteTurnLayout.setVisibility(View.VISIBLE);
                    editButton.setBackgroundResource(R.drawable.ic_action_edit);
                    editing = false;
                    //set the turn currently being edited to the absolute turn number
                    prevTurnNumber = turnNumber;
                }
                else{
                    //show the panel
                    editPanel.setVisibility(View.VISIBLE);
                    advanceAbsoluteTurnLayout.setVisibility(View.GONE);
                    editButton.setBackgroundResource(R.drawable.ic_action_edit_selected);
                    editing = true;
                    //put the abs turn number in the text box representing the turn
                    //  currently being edited
                    prevTurnNumberTextBox.setText(turnNumber+"");
                }

            }

        });
    }

    private void resetToCurrentTurn(){
        resetAllPlayerTurns();

        prevTurnNumber = turnNumber;

        prevTurnNumberTextBox.setText(prevTurnNumber+"");

    }

    private void prevTurn() {
        //resetAllPlayerTurns();

        //hexagonMap.loadPreviousCellSet(prevTurnNumber-1, (prevTurnNumber != turnNumber));

        prevTurnNumber--;

        prevTurnNumberTextBox.setText(prevTurnNumber+"");

    }

    private void advanceEditTurn(){

        //if we are at the very end, the right arrow advances the round
        if(prevTurnNumber == turnNumber) {
            if(endOfGame())
                return;
            advanceRound();
        }
        else{
            prevTurnNumber++;
            prevTurnNumberTextBox.setText(prevTurnNumber+"");
        }
    }

    private boolean endOfGame(){
        int i = indexOfNextValidPlayer();
        if (i == currentPlayer) {
            Toast.makeText(this, "Game over! " + players.get(currentPlayer).name() + " won!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    private void advanceTurn(){
        //we need to make sure the next player isn't dead. if they are, move on to the player after
        //  or the next round. if all but one of the players are dead, show some sort of pop up i guess.
        if(endOfGame())
            return;
        
        //remove current player's turn
        for (int i = 0; i < players.size(); i++) {
            if(players.get(i).turn())
                currentPlayer = i;
            players.get(i).setTurn(false);
        }



        currentPlayer = indexOfNextValidPlayer();

        //if the player is the last one in the array, increment the turn count
        if (currentPlayer == 0) {
            /*
            //increment turn number
            turnNumber++;
            prevTurnNumber = turnNumber;

            //sent the currentturn array index back to 0
            //currentPlayer = 0;

            //increment the number that's at the top
            turnNumberTextBox.setText(turnNumber + "");
            */
            advanceRound();
            
        }

        //set next player's turn
        players.get(currentPlayer).setTurn(true);

        playerSidebarAdapter.notifyDataSetChanged();
    }

    private int indexOfNextValidPlayer(){
        int i = 1;
        while(players.get( (currentPlayer+i)%players.size() ).isDead()){
            i++;
        }

        return (currentPlayer+i)%players.size();
    }

    private int indexOfFirstValidPlayer(){
        int i = 0;
        while(players.get( i%players.size() ).isDead()){
            i++;
        }

        return i%players.size();
    }

    //if we are at the very end, the right arrow advances the round
    private void advanceRound() {

        //check to see if we reached the max number of rounds
        if(turnNumber == MAX_ROUNDS){
            Toast.makeText(this, "Game over! Aliens win!", Toast.LENGTH_SHORT).show();
            return;
        }

        resetAllPlayerTurns();

        //reset the boolean values in the map
        //hexagonMap.resetAllCells( (prevTurnNumber != turnNumber) );

        //increment turn number
        turnNumber++;
        prevTurnNumber = turnNumber;

        //increment the number that's at the top
        turnNumberTextBox.setText(turnNumber+"");
        prevTurnNumberTextBox.setText(prevTurnNumber+"");
        hexagonMap.nextRound();
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
            //playerSidebarAdapter.getItem(i).setTurn(false);
            players.get(i).setTurn(false);
        }

        currentPlayer = indexOfFirstValidPlayer()%players.size();
        //playerSidebarAdapter.getItem(currentPlayer).setTurn(true);
        players.get(currentPlayer).setTurn(true);

        playerSidebarAdapter.notifyDataSetChanged();
        playerListView.setSelectionAfterHeaderView();
    }

    private void setUpReceiver() {
        try {
            IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);

            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);

            mReceiver = new ReceiverScreen();

            registerReceiver(mReceiver, filter);
        } catch (Exception e) {

        }
    }

    protected class ReceiverScreen extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                /*if(hexagonMap != null && !hexagonMap.threadIsRunning())
                    hexagonMap.newThread();

                else {
                    //initializeMap();
                    initializeHexagonMap();
                }*/

            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                System.out.println("screen off!");
                /*
                if(hexagonMap.threadIsRunning())
                    hexagonMap.stopThread();
                */
                locked = true;

            }
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
                System.out.println("action user present");
                /*System.out.println("is the thread running? " + hexagonMap.threadIsRunning());
                if(hexagonMap != null && !hexagonMap.threadIsRunning())
                    hexagonMap.newThread();
                System.out.println("is the thread running NOW? " + hexagonMap.threadIsRunning());*/
                /*if(hexagonMap.threadIsRunning())
                    hexagonMap.stopThread();
                startActivity(new Intent(GamePlay.this, HomeScreenActivity.class));*/
            }
        }

    }
}
