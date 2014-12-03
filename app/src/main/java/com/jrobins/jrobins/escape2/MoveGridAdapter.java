package com.jrobins.jrobins.escape2;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jrobins on 12/3/2014.
 */
public class MoveGridAdapter extends BaseAdapter {
    private Context context;
    private final ArrayList<Move> moves;

    public MoveGridAdapter(Context context, ArrayList<Move> moves) {
        this.context = context;
        this.moves= moves;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        TextView view;

        if (convertView == null) {
            view = new TextView(context);
        }
        else {
            view = (TextView) convertView;
        }

        view.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
        view.setText(" "+moves.get(position).turnNumberToString()+" ");
        int certainty = moves.get(position).certainty();
        switch (certainty) {
            case 0:
                view.setTextColor(view.getResources().getColor(R.color.bluff));
            case 1:
                view.setTextColor(view.getResources().getColor(R.color.certain));
            case 2:
                view.setTextColor(view.getResources().getColor(R.color.uncertain));
        }
        view.setGravity(Gravity.CENTER);
        view.setBackgroundColor(moves.get(position).color());
        return view;
    }


    @Override
    public int getCount() {
        return moves.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
