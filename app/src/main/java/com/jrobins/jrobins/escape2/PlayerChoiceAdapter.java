package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
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

import com.jrobins.jrobins.escape2.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerChoiceAdapter extends ArrayAdapter<Player> {
    private LayoutInflater inflater;


    public ArrayList<Player> players;
    private HashMap<String, String> playerNameValues = new HashMap<String, String>();


    public PlayerChoiceAdapter(Activity activity, List<Player> players){
        super(activity, R.layout.player_choice, players);
        this.players = (ArrayList<Player>) players;
        inflater = activity.getWindow().getLayoutInflater();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        boolean convertViewWasNull = false;
        if(convertView == null)
        {
            convertView  = (LinearLayout)inflater.inflate(R.layout.player_choice, parent, false);
            convertViewWasNull = true;
        }

        final int p = position;
        final EditText playerName = (EditText) convertView.findViewById(R.id.playerName);
        //playerName.setText("Player " + position);
        playerName.setText(players.get(position).name());
        final Button button = (Button) convertView.findViewById(R.id.button);
        button.setBackgroundColor(players.get(position).color());

        playerName.setTag("theFirstEditTextAtPos:"+position);
        //playerNameValues.put(playerName.getTag().toString(), playerName.getText().toString());

        playerName.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    button.setText("X");
                    players.get(p).setName(null);
                }
                else {
                    if (s.length() == 1)
                        button.setText(playerName.getText().toString().substring(0, 1).toUpperCase());
                    else {
                        String btnText = playerName.getText().toString().substring(0, 1).toUpperCase() +
                                playerName.getText().toString().substring(1,2).toLowerCase();
                        button.setText(btnText);
                    }
                    PlayerChoiceAdapter.this.playerNameValues.put(playerName.getTag().toString(), s.toString());
                    //players.get(p).setName(playerName.getText().toString());
                    //System.out.println("setting player name = " +playerName.getText().toString());
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });


        playerName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    players.get(p).setName(playerName.getText().toString());
                    System.out.println("setting name to " + playerName.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return convertView ;
    }

    public String getPlayerName(int position){
        //here you need to recreate the id for the first editText
        String result = playerNameValues.get("theFirstEditTextAtPos:"+position);
        if(result ==null)
            result = "default value";

        return result;
    }

    @Override
    public Player getItem(int position){
        String result = playerNameValues.get("theFirstEditTextAtPos:"+position);
        if(result ==null)
            result = "default value";

        return new Player(result);
    }
}