package com.example.wordsearchapp;

import com.example.wordsearchapp.Models.GameWord;
import com.example.wordsearchapp.Models.Grid;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

enum Direction{
    Top,
    Left,
    Right,
    Down,
    TopLeft,
    TopRight,
    DownLeft,
    DownRight
}

public class GameSetup {

    private int id;
    private Grid gameGrid;
    private GameWord[] words;

    public GameSetup(int id, Grid gameGrid, GameWord[] words){
        this.id = id;
        this.gameGrid = gameGrid;
        this.words = words;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Grid getGameGrid() {
        return gameGrid;
    }

    public void setGameGrid(Grid gameGrid) {
        this.gameGrid = gameGrid;
    }

    public GameWord[] getWords() {
        return words;
    }

    public void setWords(GameWord[] words) {
        this.words = words;
    }

}
