package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.os.Bundle;
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

import java.util.ArrayList;


public class ChoosePlayersActivity extends Activity implements OnItemSelectedListener {
    private Spinner spinner;
    private Button playButton;
    private TextView box;
    private ListView playerListView;
    private ArrayList<Player> players;

    private ArrayAdapter playerArrayAdapter;
    private int numberOfPlayers = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);

        box = (TextView) findViewById(R.id.numberOfPlayers);
        spinner = (Spinner) findViewById(R.id.spinner);



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
        box.setText("# players: " + selState);
        setUpPlayerListView(Integer.parseInt(selState));
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
        playerArrayAdapter = new PlayerChoiceAdapter(this, players);
        playerListView = (ListView) findViewById(R.id.playerList);
        playerListView.setAdapter(playerArrayAdapter);
    }

    private void setUpPlayButton(){
        playButton = ( Button ) findViewById(R.id.play);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //This is a comment which does no good to your code. Feel free to remove it after you copy paste.
                //When the button is clicked, the control will come to this method.
                //To demonstrate this, let us try changing the label of the Button from 'Login' to 'I am clicked'

                playButton.setText("I am Clicked");}

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

    private void initializePlayerList(){
        players = new ArrayList<Player>();
        Player player;

        player = new Player();
        player.setName("Player 1");
        player.setColor(getResources().getColor(android.R.color.holo_blue_bright));
        players.add(player);

        player = new Player();
        player.setName("Player 2");
        player.setColor(getResources().getColor(android.R.color.holo_purple));
        players.add(player);

        player = new Player();
        player.setName("Player 3");
        player.setColor(getResources().getColor(android.R.color.holo_green_light));
        players.add(player);

        player = new Player();
        player.setName("Player 4");
        player.setColor(getResources().getColor(android.R.color.holo_red_light));
        players.add(player);

    }
}
