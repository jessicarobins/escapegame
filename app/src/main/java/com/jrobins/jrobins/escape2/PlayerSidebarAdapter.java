package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jrobins on 11/30/2014.
 */
public class PlayerSidebarAdapter extends ArrayAdapter<Player>{

    private LayoutInflater inflater;
    public ArrayList<Player> players;

    public PlayerSidebarAdapter(Activity activity, List<Player> players){
        super(activity, R.layout.player_sidebar, players);
        this.players = (ArrayList<Player>) players;
        inflater = activity.getWindow().getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){


        if(convertView == null)
        {
            convertView  = (LinearLayout)inflater.inflate(R.layout.player_sidebar, parent, false);
        }

        Button button = (Button) convertView.findViewById(R.id.button);
        button.setBackgroundColor(players.get(position).color());
        String playerName = players.get(position).name();
        if (playerName.length() == 1)
            button.setText(playerName.substring(0, 1).toUpperCase());
        else {
            String btnText = playerName.substring(0, 1).toUpperCase() +
                    playerName.substring(1,2).toLowerCase();
            button.setText(btnText);
        }

        //make the halo for aliens/humans

        LinearLayout halo = (LinearLayout) convertView.findViewById(R.id.halo);
        if (players.get(position).isAlien())
            halo.setBackgroundColor(convertView.getResources().getColor(R.color.alien));
        else if (players.get(position).isHuman())
            halo.setBackgroundColor(convertView.getResources().getColor(R.color.human));
        else
            halo.setBackgroundColor(convertView.getResources().getColor(R.color.neutral));

        LinearLayout currentTurn = (LinearLayout) convertView.findViewById(R.id.currentTurn);
        if(players.get(position).turn())
            currentTurn.setBackgroundColor(convertView.getResources().getColor(R.color.current_turn));
        else
            currentTurn.setBackgroundColor(convertView.getResources().getColor(android.R.color.transparent));

        return convertView ;
    }
}
