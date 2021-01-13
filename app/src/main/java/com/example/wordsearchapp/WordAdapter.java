package com.example.wordsearchapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wordsearchapp.Models.GameWord;
import com.example.wordsearchapp.Models.Grid;

class WordAdapter extends BaseAdapter {

    private Context context;
    private GameWord [] gameWords;

    public WordAdapter(Context context, GameWord [] gameWords){
        this.context = context;
        this.gameWords = gameWords;
    }

    @Override
    public int getCount() {
        return gameWords.length;
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

        final String word = gameWords[i].getGameWord();

        if(view == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.word_item, null);

            view.setTag(word);
        }

        TextView textView =  view.findViewById(R.id.text_word);
        textView.setText(word);

        return view;
    }
}
