package com.jrobins.jrobins.escape2;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jrobins.jrobins.escape2.R;

public class PlayerChoiceAdapter extends ArrayAdapter {
    private LayoutInflater inflater;

    public PlayerChoiceAdapter(Activity activity, String[] items){
        super(activity, R.layout.player_choice, items);
        inflater = activity.getWindow().getLayoutInflater();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        return inflater.inflate(R.layout.player_choice, parent, false);
    }

}