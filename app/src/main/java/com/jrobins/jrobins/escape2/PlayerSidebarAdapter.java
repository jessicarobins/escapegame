package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jrobins on 11/30/2014.
 */
public class PlayerSidebarAdapter extends ArrayAdapter<Player>{
    private Activity context;
    private LayoutInflater inflater;
    public ArrayList<Player> players;
    HashMap<String, Integer> haloColors = new HashMap<String, Integer>();
    int[] colors;

    public PlayerSidebarAdapter(Activity activity, List<Player> players){
        super(activity, R.layout.player_sidebar, players);
        this.context = activity;
        this.players = (ArrayList<Player>) players;
        colors = getContext().getResources().getIntArray(R.array.halo_colors);
        inflater = activity.getWindow().getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){


        if(convertView == null)
        {
            convertView  = (LinearLayout)inflater.inflate(R.layout.player_sidebar, parent, false);
        }

        final TextView playerID = (TextView) convertView.findViewById(R.id.player_id);
        final LinearLayout halo = (LinearLayout) convertView.findViewById(R.id.halo);
        final LinearLayout currentTurn = (LinearLayout) convertView.findViewById(R.id.currentTurn);

        currentTurn.setLayoutParams(new AbsListView.LayoutParams(parent.getWidth(),parent.getWidth()));
        //size the buttons so they all fit on the screen


        int w = parent.getWidth();
        playerID.setTextSize( w/5);

        //playerID.setTextSize( parent.getWidth()/2 / players.size());


        playerID.setBackgroundColor(players.get(position).color());
        String playerName = players.get(position).name();

        Rect bounds = new Rect();
        Paint textPaint = playerID.getPaint();

        textPaint.getTextBounds("MM",0,2,bounds);
        int h = bounds.width();

        //playerID.setWidth((int)(h*1.25));
        //playerID.setHeight((int)(h*1.25));
        //playerID.setWidth(w);
        //playerID.setHeight(w);


        if (playerName.length() == 1)
            playerID.setText(playerName.substring(0, 1).toUpperCase());
        else {
            String btnText = playerName.substring(0, 1).toUpperCase() +
                    playerName.substring(1,2).toLowerCase();
            playerID.setText(btnText);
        }




        //make the halo for aliens/humans


        halo.setTag("halo"+position);
        if(!haloColors.containsKey(halo.getTag()))
            haloColors.put(halo.getTag().toString(), new Integer(colors[2]));

        halo.setBackgroundColor(haloColors.get(halo.getTag()));



        final int p = position;
        playerID.setClickable(true);
        playerID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove everybody else's turn
                for(int i = 0; i < players.size(); i++){
                    if (i!=p) {
                        players.get(i).setTurn(false);
                        //currentTurn.setBackgroundColor(v.getResources().getColor(android.R.color.black));
                    }
                }

                //set it to be this player's turn
                players.get(p).setTurn(true);
                notifyDataSetChanged();
                //currentTurn.setBackgroundColor(v.getResources().getColor(R.color.current_turn));
            }
        });

        playerID.setLongClickable(true);


        playerID.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View arg0) {
                showAlertDialog(halo);

                return true;
            }
        });


        /*
        if (players.get(position).isAlien())
            halo.setBackgroundColor(convertView.getResources().getColor(R.color.alien));
        else if (players.get(position).isHuman())
            halo.setBackgroundColor(convertView.getResources().getColor(R.color.human));
        else
            halo.setBackgroundColor(convertView.getResources().getColor(R.color.neutral));
        */

        if(players.get(position).turn())
            currentTurn.setBackgroundColor(convertView.getResources().getColor(R.color.current_turn));
        else
            currentTurn.setBackgroundColor(convertView.getResources().getColor(android.R.color.black));

        return convertView ;
    }


    private void showAlertDialog(final LinearLayout halo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //final Button button = (Button) findViewById(R.id.button);
        GridView gridView = new GridView(getContext());
        //int w = getContext().getResources().getDisplayMetrics().widthPixels;
        //gridView.setLayoutParams(new AbsListView.LayoutParams(w/2, AbsListView.LayoutParams.WRAP_CONTENT));

        final int[] colors = getContext().getResources().getIntArray(R.array.halo_colors);
        final String[] labels = getContext().getResources().getStringArray(R.array.halo_color_labels);

        gridView.setAdapter(new ColorChoiceGridViewAdapter(getContext(), colors, labels));
        gridView.setNumColumns(3);
        builder.setView(gridView);
        final AlertDialog alertDialog = builder.create();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // do something here
                halo.setBackgroundColor(colors[position]);
                haloColors.put(halo.getTag().toString(), new Integer(colors[position]));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        //alertDialog.getWindow().setLayout(400, 800);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 300;
        //lp.height = 800;
        //lp.x=-170;
        //lp.y=100;
        alertDialog.getWindow().setAttributes(lp);
    }

    @Override
    public Player getItem(int position){
        return players.get(position);
    }

    public int getCount(){
        return players.size();
    }


}
