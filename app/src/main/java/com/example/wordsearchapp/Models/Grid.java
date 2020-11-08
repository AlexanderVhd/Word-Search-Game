package com.example.wordsearchapp.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Grid {

    private int gridSize;
    private int numCol;
    private char[] gridData;

    Random random = new Random();

    public Grid(int numCol){
        this.numCol = numCol;
        this.gridSize = numCol * numCol;
        this.gridData = new char[numCol * numCol];
    }

    /*
    * getter and setter methods
    */
    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public int getNumCol() {
        return numCol;
    }

    public void setNumCol(int numCol) {
        this.numCol = numCol;
    }

    public void setGridData(char[] gridData) {
        this.gridData = gridData;
    }

    public char getGridData(int pos){
        return gridData[pos];
    }

    public void addGridData(int pos, char letter){
        this.gridData[pos] = letter;
    }

    //Fills in empty cells on grid with random letters
    public void fillGrid(){
        for (int i = 0; i < this.gridSize; i++){
            if(this.gridData[i] == '\u0000'){
                char letter = (char)('A' + random.nextInt(26));
                this.addGridData(i, letter);
            }
        }
    }

}
