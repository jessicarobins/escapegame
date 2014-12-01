package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ListView;

import java.util.ArrayList;


public class GamePlay extends Activity implements MapView.OnCellClickListener {
    ArrayList<Player> players;
    PlayerSidebarAdapter playerSidebarAdapter;
    private ListView playerListView;
    MapView hexagonMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game_play);

        players = getIntent().getParcelableArrayListExtra("players");
        for(int i = 0; i< players.size(); i++){
            System.out.println("player " + i + " = " + players.get(i).name());
        }

        playerSidebarAdapter = new PlayerSidebarAdapter(this, players);
        playerListView = (ListView) findViewById(R.id.playerList);
        playerListView.setAdapter(playerSidebarAdapter);




        hexagonMap = new MapView(this);
        hexagonMap.initialize(4, 8);
        hexagonMap.setOnCellClickListener(this);
        setContentView(hexagonMap);
        /*
        AbsoluteLayout layout = (AbsoluteLayout) findViewById(R.id.sectorGrid);
        HexagonView sector1 = new HexagonView(this);
        layout.addView(sector1);
        HexagonView sector2 = new HexagonView(this);
        layout.addView(sector2);
        */

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

    @Override
    public void onCellClick(int column, int row)
    {
        hexagonMap.setCell(column, row, !hexagonMap.isCellSet(column, row));
    }
}
