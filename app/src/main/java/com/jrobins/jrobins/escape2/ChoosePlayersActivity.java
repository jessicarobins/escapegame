package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class ChoosePlayersActivity extends Activity implements OnItemSelectedListener {
    private Spinner spinner;
    private Button playButton;

    //players
    private ListView playerListView;
    private ArrayList<Player> players;
    private int [] colors;

    private ArrayAdapter playerArrayAdapter;
    private int numberOfPlayers = 4;

    //maps
    List <Map> maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);


        spinner = (Spinner) findViewById(R.id.spinner);


        initializeColorList();
        initializePlayerList();
        setUpPlayerListView();
        setUpSpinner();
        setUpPlayButton();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_players, menu);
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

    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        spinner.setSelection(position);
        String selState = (String) spinner.getSelectedItem();

        setUpPlayerListView(Integer.parseInt(selState));
        //changePlayerListView(Integer.parseInt(selState));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }






    private void setUpPlayerListView() {
        setUpPlayerListView(4);
    }

    private void setUpPlayerListView(int numberOfPlayers) {

        //playerArrayAdapter = new PlayerChoiceAdapter(this, new String[numberOfPlayers]);

        //You can also directly modify the underlying data structure and call the
        // notifyDataSetChanged() method on the adapter to notify it about the changes in data.

        setPlayerList(numberOfPlayers);
        playerArrayAdapter = new PlayerChoiceAdapter(this, players);
        playerListView = (ListView) findViewById(R.id.playerList);
        playerListView.setAdapter(playerArrayAdapter);
    }

    private void changePlayerListView(int numberOfPlayers) {

        Player p;
        players = new ArrayList<Player>();
        if (numberOfPlayers >= playerArrayAdapter.getCount()) {
            for (int i = 0; i < playerArrayAdapter.getCount(); i++) {
                p = (Player) playerArrayAdapter.getItem(i);
                p.setColor(colors[i]);

                //System.out.println("player name = " +p.name());
                players.add(p);
            }
            addPlayers(numberOfPlayers-players.size());
        }
        else {
            for (int i = 0; i < numberOfPlayers; i++) {
                p = (Player) playerArrayAdapter.getItem(i);
                players.add(p);
            }
        }
        //playerArrayAdapter.notifyDataSetChanged();
        playerArrayAdapter = new PlayerChoiceAdapter(this, players);
        playerListView = (ListView) findViewById(R.id.playerList);
        playerListView.setAdapter(playerArrayAdapter);
    }

    private void addPlayers(int numberOfPlayers){
        //number of players to add
        Player player;
        for (int i = 0; i < numberOfPlayers; i++){
            player = new Player();
            player.setName("Player " + (players.size()+i+1));
            player.setColor(colors[players.size()]);
            players.add(player);
        }
    }

    private void setUpPlayButton(){
        playButton = ( Button ) findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                players = new ArrayList<Player>();
                Player p;

                for(int i = 0; i<playerArrayAdapter.getCount();i++){
                    p = (Player)playerArrayAdapter.getItem(i);

                    //this is where we check to see if they have a name
                    if (p.name()==null || p.name().length() == 0){
                        //error message
                        new AlertDialog.Builder(ChoosePlayersActivity.this)
                                .setTitle("Enter player name")
                                .setMessage("Player names can't be blank yo")
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                        return;
                    }



                    //System.out.println("name = " + playerArrayAdapter.getItem(i).toString() + " i = " +i);
                    players.add(p);
                }


                Intent intent = new Intent(ChoosePlayersActivity.this, ChooseMapActivity.class);
                intent.putParcelableArrayListExtra("players", players);
                startActivity(intent);
            }

        });
    }

    private void setUpSpinner(){
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void setPlayerList(int numberOfPlayers){
        players = new ArrayList<Player>();
        Player player;
        for (int i = 0; i < numberOfPlayers; i++){
            player = new Player();
            player.setName("Player " + (i+1));
            player.setColor(colors[i]);
            players.add(player);
        }

    }

    private void initializeColorList(){
        /*
        colors = new int[8];
        colors[0] = getResources().getColor(android.R.color.holo_purple);
        colors[1] = getResources().getColor(android.R.color.holo_blue_bright);
        colors[2] = getResources().getColor(android.R.color.holo_green_light);
        colors[3] = getResources().getColor(android.R.color.holo_red_light);
        colors[4] = getResources().getColor(android.R.color.holo_orange_light);
        colors[5] = getResources().getColor(android.R.color.holo_blue_dark);
        colors[6] = getResources().getColor(android.R.color.darker_gray);
        colors[7] = getResources().getColor(android.R.color.holo_green_dark);
        */
        colors = getResources().getIntArray(R.array.player_color_choices);
    }

    private void initializePlayerList(){
        players = new ArrayList<Player>();
        Player player;
        for (int i = 0; i < 4; i++){
            player = new Player();
            player.setName("Player " + (i+1));
            player.setColor(colors[i]);
            players.add(player);
        }
    }


}
