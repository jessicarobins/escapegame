package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.jrobins.jrobins.escape2.R;

import java.util.ArrayList;
import java.util.List;

public class PlayerChoiceAdapter extends ArrayAdapter<Player> {
    private LayoutInflater inflater;

    Context context;
    ArrayList<Player> players;

    /*
    public PlayerChoiceAdapter(Activity activity, String[] items){
        super(activity, R.layout.player_choice, items);
        inflater = activity.getWindow().getLayoutInflater();
    }*/

    public PlayerChoiceAdapter(Activity activity, List<Player> players){
        super(activity, R.layout.player_choice, players);
        this.players = (ArrayList<Player>) players;
        inflater = activity.getWindow().getLayoutInflater();
    }

    public PlayerChoiceAdapter(Context context, int textViewResourceId,
                          List<Player> players) {
        super(context, textViewResourceId, players);
        this.players = (ArrayList<Player>) players;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null)
        {
            convertView  = (LinearLayout)inflater.inflate(R.layout.player_choice, parent, false);
        }

        //Button yourButton= (Button)  convertView  .findViewById(R.id.YOUR_BUTTON_ID);
        //yourButton.setOnClickListener(new OnClickListener()

        /*
        {
            @Override
            public void onClick(View v)
            {
                // Your code that you want to execute on this button click
                Intent myIntent = new Intent(CurrentActivity.this, NextActivity.class);
                CurrentActivity.this.startActivity(myIntent);

            }

        });*/

        final EditText playerName = (EditText) convertView.findViewById(R.id.playerName);
        //playerName.setText("Player " + position);
        playerName.setText(players.get(position).name());
        final Button button = (Button) convertView.findViewById(R.id.button);
        button.setBackgroundColor(players.get(position).color());

        playerName.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if(s.length() == 0)
                    button.setText("X");
                else
                    button.setText(playerName.getText().toString().substring(0,1).toUpperCase());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        return convertView ;
    }

}