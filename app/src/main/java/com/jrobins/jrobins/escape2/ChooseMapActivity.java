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
    List<MapPack> mapPacks;

    //adapters
    MapChoiceAdapter mapChoiceAdapter;

    ArrayList<Player> players;
    Map selectedMap;

    //views
    BasicHexagonGridView map;
    ListView mapListView;


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
        viewingDefaultMaps = true;

    }


    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_map, menu);

        return true;
    }
    */

    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        for(int i = 0; i <mapPacks.size(); i++) {
            menu.add(0, i, Menu.NONE, mapPacks.get(i).name());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id <mapPacks.size()) {

            changeMapList(mapPacks.get(id).maps());


            return true;
        }

        else {
            if(viewingDefaultMaps) {
                if(myMaps != null) {
                    changeMapList(myMaps);
                    viewingDefaultMaps = false;
                }
                else
                    Toast.makeText(this, "You have no maps. Go make some!", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        //return super.onOptionsItemSelected(item);
    }

    private void loadAllMaps(){
        loadDefaultMaps();
        //loadMyMaps();
    }

    private void loadDefaultMaps(){
        /*
        MapParser parser = new MapParser(this);

        try {
            defaultMaps = parser.getMaps();
            Log.d("maps", "created maps?");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //defaultMaps = MapTxtParser.readMapsFromInternalStorage(this);
        mapPacks = MapTxtParser.readMapPacksFromInternalStorage(this);
    }



    private void loadMyMaps(){
        myMaps = MapTxtParser.readMapsFromExternalStorage(this);

    }



    private void initializeMapList(){
        mapChoiceAdapter = new MapChoiceAdapter(this, mapPacks.get(0).maps());
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
