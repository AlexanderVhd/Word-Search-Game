package com.example.wordsearchapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class GameSetup {

    private Level level;
    private GridSize gridSize;
    private String theme;
    private int minNumWords;  //minimum number of words to be searched in a game
    private int maxNumWords;  //maximum number of words to be searched in a game
    private int maxWordLength; //maximum length of a word to be allowed in game

    final private int numWords = 6; //initial minimum number of words for game (used for determining minNumWords and maxNumWords)

    public GameSetup(GridSize gridSize, Level level, String theme){
        this.level = level;
        this.gridSize = gridSize;
        this.theme = theme;
        this.maxWordLength = Level.Easy == level ? gridSize.getNumCol() - 1 : gridSize.getNumRow() - 1;

        //determine the range of words for the game based on the gridsize and level selected by user
        switch (gridSize){
            case Small:
                minNumWords = numWords + level.ordinal();
                maxNumWords = minNumWords + 1;
                break;

            case Medium:
                minNumWords = numWords + 1 + level.ordinal();
                maxNumWords = minNumWords + 1;
                break;

            case Large:
                minNumWords = numWords + 2 + level.ordinal();
                maxNumWords = minNumWords + 1;
                break;
        }

    }

    public int getMinNumWords() {
        return minNumWords;
    }

    public int getMaxNumWords() {
        return maxNumWords;
    }

    public int getMaxWordLength() {
        return maxWordLength;
    }

    public Level getLevel() {
        return level;
    }

    public GridSize getGridSize() {
        return gridSize;
    }

    public String getTheme() {
        return theme;
    }
}
