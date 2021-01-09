package com.example.wordsearchapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wordsearchapp.Models.Grid;

import java.util.List;

class GridAdapter extends BaseAdapter {

    private Context context;
    private Grid mGrid;

    public GridAdapter(Context context, Grid mGrid){
        this.context = context;
        this.mGrid = mGrid;
    }

    @Override
    public int getCount() {
        return mGrid.getGridSize();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final char letter = mGrid.getGridData(i);

        if(view == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.grid_item, null);

            view.setTag(false);
        }

        TextView textView =  view.findViewById(R.id.text_letter);
        textView.setText(String.valueOf(letter));

        if(mGrid.getNumCol() == 8){
            textView.setTextSize(21);
        }

        return view;
    }
}
