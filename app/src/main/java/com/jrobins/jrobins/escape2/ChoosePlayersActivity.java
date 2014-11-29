package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;


public class ChoosePlayersActivity extends Activity implements OnItemSelectedListener {
    private Spinner spinner;
    private TextView box;
    private ListView playerListView;

    private ArrayAdapter playerArrayAdapter;
    private int numberOfPlayers = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_players);

        box = (TextView) findViewById(R.id.numberOfPlayers);
        spinner = (Spinner) findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        setUpPlayerList();
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
        setUpPlayerList(Integer.parseInt(selState));
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    private void setUpPlayerList() {
        setUpPlayerList(4);
    }

    private void setUpPlayerList(int numberOfPlayers) {

        playerArrayAdapter = new PlayerChoiceAdapter(this, new String[numberOfPlayers]);
        playerListView = (ListView) findViewById(R.id.playerList);
        playerListView.setAdapter(playerArrayAdapter);
    }
}
