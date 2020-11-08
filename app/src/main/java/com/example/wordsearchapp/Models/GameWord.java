package com.example.wordsearchapp.Models;

import android.util.Log;

public class GameWord extends Word {

    private int startCell;
    private int endCell;

    public GameWord(int wordSize, String word){
        super(wordSize, word);
        this.startCell = 0;
        this.endCell = 0;
    }

    public int getStartCell() {
        return startCell;
    }

    public void setStartCell(int startCell) {
        this.startCell = startCell;
    }

    public int getEndCell() {
        return endCell;
    }

    public void setEndCell(int endCell) {
        this.endCell = endCell;
    }

}
