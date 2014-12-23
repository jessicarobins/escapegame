package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ChooseMapActivity extends Activity {
    //map lists
    List<Map> defaultMaps;
    List<Map> myMaps;

    //adapters
    MapChoiceAdapter mapChoiceAdapter;

    ArrayList<Player> players;
    Map selectedMap;

    //views
    BasicHexagonGridView map;
    ListView mapListView;

    Menu optionsMenu;
    boolean viewingDefaultMaps = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_map);

        getPlayers();
        loadAllMaps();
        initializeMapList();
        //testMap2();

    }

    @Override
    protected void onPause(){
        super.onPause();
        if(map != null && map.threadIsRunning())
            map.stopThread();

    }

    @Override
    protected void onResume(){
        super.onResume();

        initializeMapList();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_map, menu);
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_default_maps) {
            if(!viewingDefaultMaps) {
                changeMapList(defaultMaps);
                viewingDefaultMaps = true;
            }
            return true;
        }

        if (id == R.id.action_my_maps) {
            if(viewingDefaultMaps) {
                if(myMaps != null) {
                    changeMapList(myMaps);
                    viewingDefaultMaps = false;
                }
                else
                    Toast.makeText(this, "You have no maps", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadAllMaps(){
        loadDefaultMaps();
        loadMyMaps();
    }

    private void loadDefaultMaps(){
        MapParser parser = new MapParser(this);

        try {
            defaultMaps = parser.getMaps();
            Log.d("maps", "created maps?");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void loadMyMaps(){
        myMaps = MapTxtParser.readMapsFromExternalStorage(this);

    }



    private void initializeMapList(){
        mapChoiceAdapter = new MapChoiceAdapter(this, defaultMaps);
        mapListView = (ListView) findViewById(R.id.mapList);
        mapListView.setAdapter(mapChoiceAdapter);

        mapListView.setClickable(true);
        mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectedMap = mapChoiceAdapter.getItem(position);
                Intent intent = new Intent(ChooseMapActivity.this, GamePlay.class);
                intent.putParcelableArrayListExtra("players", players);
                intent.putExtra("map", selectedMap);
                startActivity(intent);


            }
        });
    }

    private void changeMapList(List<Map> maps){
        mapChoiceAdapter = new MapChoiceAdapter(this, maps);
        mapListView.setAdapter(mapChoiceAdapter);
        /*
        mapChoiceAdapter.clear();
        mapChoiceAdapter.addAll(maps);
        mapChoiceAdapter.notifyDataSetChanged();*/
    }

    private void getPlayers(){
        players = getIntent().getParcelableArrayListExtra("players");
    }

    private void testMap(){
        BasicHexagonGridView view = new BasicHexagonGridView(this);

        view.initialize(defaultMaps.get(0));
        setContentView(view);
    }


}
