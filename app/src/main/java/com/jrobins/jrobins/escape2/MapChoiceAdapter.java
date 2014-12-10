package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MapChoiceAdapter extends ArrayAdapter<Map> {
    private LayoutInflater inflater;
    BasicHexagonGridView map;
    TextView mapName;
    List <Map> maps;
    Activity activity;


    public MapChoiceAdapter(Activity activity, List<Map> maps){
        super(activity, R.layout.choose_map, maps);
        this.maps = (List<Map>) maps;
        this.activity = activity;
        inflater = activity.getWindow().getLayoutInflater();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        if(convertView == null)
        {
            convertView  = (LinearLayout)inflater.inflate(R.layout.choose_map, parent, false);

        }

        mapName = (TextView) convertView.findViewById(R.id.mapName);
        mapName.setText(maps.get(position).name());
        map = (BasicHexagonGridView) convertView.findViewById(R.id.map);
        int y = (int)(size.x*(.65));
        map.setMinimumHeight(y);
        map.setMinimumWidth(parent.getWidth());
        //map.setMinimumWidth(100);
        //map.setMinimumHeight(100);

        map.initialize(maps.get(position));



        return convertView ;
    }

    @Override
    public Map getItem(int position){

        return maps.get(position);
    }

    @Override
    public int getCount(){
        return maps.size();
    }

}
