package com.example.wordsearchapp.Models;

public enum GridSize {
    Small (8, 10),
    Medium (10, 12),
    Large (12, 14);

    private final int numCol;
    private final int numRow;

    GridSize(int numCol, int numRow){
        this.numCol = numCol;
        this.numRow = numRow;
    }

    public int getNumCol(){
        return numCol;
    }

    public int getNumRow() {
        return numRow;
    }
}
