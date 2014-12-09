package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;


public class ChooseMapActivity extends Activity {
    List<Map> maps;
    Map selectedMap;
    BasicHexagonGridView map;
    ListView mapListView;
    MapChoiceAdapter mapChoiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_map);

        loadMaps();
        initializeMapList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_map, menu);
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

    private void loadMaps(){
        MapParser parser = new MapParser(this);

        try {
            maps = parser.getMaps();
            Log.d("maps", "created maps?");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initializeMapList(){
        mapChoiceAdapter = new MapChoiceAdapter(this, maps);
        mapListView = (ListView) findViewById(R.id.mapList);
        mapListView.setAdapter(mapChoiceAdapter);
        mapListView.setClickable(true);
        mapListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                selectedMap = mapChoiceAdapter.getItem(position);
                System.out.println(map);

            }
        });
    }


}
