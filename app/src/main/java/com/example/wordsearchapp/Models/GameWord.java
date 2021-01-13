package com.example.wordsearchapp.Models;

import android.graphics.Color;
import android.util.Log;

import com.example.wordsearchapp.R;

public class GameWord extends Word {

    private int[] positionList;

    public GameWord(int wordSize, String word){
        super(wordSize, word);
        this.positionList = new int[wordSize];
    }

    public void setPositionList(int[] positionList){
        this.positionList = positionList;
    }

    public int[] getPositionList(){
        return positionList;
    }

    public int getCellPosition(int index){
        return positionList[index];
    }

    public int getStartCell() {
        return positionList[0];
    }

    public int getEndCell() {
        return positionList[getWordSize()-1];
    }

}
