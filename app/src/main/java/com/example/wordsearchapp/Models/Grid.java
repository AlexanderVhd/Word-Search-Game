package com.example.wordsearchapp.Models;

import com.example.wordsearchapp.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {

    private int gridSize;
    private int numCol;
    private int numRow;
    private char[] gridData;

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

    Direction[] directions = Direction.values();
    Random random = new Random();

    public Grid(int numCol, int numRow){
        this.numCol = numCol;
        this.numRow = numRow;
        this.gridSize = numCol * numRow;
        this.gridData = new char[numCol * numRow];
    }

    /*
    * getter and setter methods
    */
    public int getGridSize() {
        return gridSize;
    }

    public int getNumCol() {
        return numCol;
    }

    public char getGridData(int pos){
        return gridData[pos];
    }

    public void addGridData(int pos, char letter){
        this.gridData[pos] = letter;
    }


    /*
     * Grid layout and grid data setup methods
     */
    public boolean populateGrid(GameWord [] gameWords){

        Direction wordDirection;

        //iterate through every word in the used words list and use them to populate the grid
        for(GameWord gameWord : gameWords){

            wordDirection = chooseDirection(directions);

            switch (wordDirection){
                case Top:
                    populateWord(Direction.Top, gameWord);
                    break;

                case Left:
                    populateWord(Direction.Left, gameWord);
                    break;

                case Right:
                    populateWord(Direction.Right, gameWord);
                    break;

                case Down:
                    populateWord(Direction.Down, gameWord);
                    break;

                case TopLeft:
                    populateWord(Direction.TopLeft, gameWord);
                    break;

                case TopRight:
                    populateWord(Direction.TopRight, gameWord);
                    break;

                case DownLeft:
                    populateWord(Direction.DownLeft, gameWord);
                    break;

                case DownRight:
                    populateWord(Direction.DownRight, gameWord);
                    break;

            }

            //check if word was successfully positioned on grid
            if(!gameWord.isPositioned()){
                return false;
            }
        }

        return true;
    }

    public void populateWord(Direction dir, GameWord gameWord){

        ArrayList<Integer> cellArray = new ArrayList<Integer>();
        ArrayList<Integer> directionsList = new ArrayList<Integer>();
        int wordLength = gameWord.getGameWord().length();
        char[] word = gameWord.getGameWord().toCharArray();
        int[] positions = new int[wordLength];

        //place oridinals of all directions into array in order
        for(int d = 0; d < directions.length; d++){
            directionsList.add(d);
        }

        //determine if there are available cell positions in given direction
        int dirOrdinal = directionsList.get(dir.ordinal());
        int index = dirOrdinal;
        cellArray = findFreePositions(directions[dirOrdinal], cellArray, word);

        //keep searching for available cell positions in a certain direction until at least one position is found
        while(cellArray.size() <= 0){
            directionsList.remove(index);

            if(directionsList.size() <= 0){
                return;
            }
            else{
                index = random.nextInt(directionsList.size());
                dirOrdinal = directionsList.get(index);
                cellArray = findFreePositions(directions[dirOrdinal], cellArray, word);
            }
        }

        //choose random cell position from cellArray
        int pos = cellArray.get(random.nextInt(cellArray.size()));

        //iterate through letters in the current word
        for(int l = 0; l < wordLength; l++){

            //input letter in the grid and save the current grid cell position of the letter for the gameWord data
            addGridData(pos, word[l]);
            positions[l] = pos;

            pos = findNextCell(directions[dirOrdinal], pos);
        }

        //save the grid cell positions of the word and mark word as positioned
        gameWord.setPositionList(positions);
        gameWord.setPositioned(true);
    }

    public ArrayList<Integer> findFreePositions(Direction dir, ArrayList<Integer> cellArray, char [] word){

        int wordLength = word.length;

        //iterate through grid cells
        for(int c = 0; c < gridSize; c++){
            if(checkCell(dir, c, wordLength)){
                int nextCell = c;
                boolean letterPos = true;

                //iterate through letters in the current word
                for(int l = 0; l < wordLength; l++){

                    //check letter positions on grid to see if they are empty or have a matching letter
                    if(sameLetter(word[l], getGridData(nextCell)) || getGridData(nextCell) == '\u0000'){
                        nextCell = findNextCell(dir, nextCell);
                    }
                    else{
                        letterPos = false;
                        break;
                    }

                }

                if(letterPos){
                    cellArray.add(c);
                }

            }
        }
        return cellArray;
    }

    public boolean checkCell(Direction dir, int currentCell, int wordLength){
        switch (dir){
            case Top:
                return currentCell+1 > numCol * (wordLength-1);

            case Left:
                return (currentCell+1) % numCol >= wordLength || ((currentCell+1) % numCol == 0 && wordLength <= numCol);

            case Right:
                return (currentCell+1) % numCol <= numCol - (wordLength-1) && (currentCell+1) % numCol != 0;

            case Down:
                return currentCell+1 <= gridSize - (numCol * (wordLength - 1));

            case TopLeft:
                return (currentCell+1 > numCol * (wordLength-1)) && ((currentCell+1) % numCol >= wordLength || ((currentCell+1) % numCol == 0 && wordLength <= numCol));

            case TopRight:
                return (currentCell+1 > numCol * (wordLength-1)) && ((currentCell+1) % numCol <= numCol - (wordLength-1) && (currentCell+1) % numCol != 0);

            case DownLeft:
                return (currentCell+1 <= gridSize - (numCol * (wordLength - 1))) && ((currentCell+1) % numCol >= wordLength || ((currentCell+1) % numCol == 0 && wordLength <= numCol));

            case DownRight:
                return (currentCell+1 <= gridSize - (numCol * (wordLength - 1))) && ((currentCell+1) % numCol <= numCol - (wordLength-1) && (currentCell+1) % numCol != 0);

            default:
                return false;
        }
    }

    public int findNextCell(Direction dir, int currentCell){
        switch (dir){
            case Top:
                return currentCell - numCol;

            case Left:
                return currentCell - 1;

            case Right:
                return currentCell + 1;

            case Down:
                return currentCell + numCol;

            case TopLeft:
                return currentCell - (numCol + 1);

            case TopRight:
                return currentCell - (numCol - 1);

            case DownLeft:
                return currentCell + (numCol - 1);

            case DownRight:
                return currentCell + (numCol + 1);

            default:
                return currentCell;
        }
    }

    public Direction chooseDirection(Direction [] directions){
        return directions[random.nextInt(directions.length)];
    }

    public boolean sameLetter(char letter1, char letter2){
        return letter1 == letter2;
    }

    public void fillGrid(){
        for (int i = 0; i < this.gridSize; i++){
            if(this.gridData[i] == '\u0000'){
                char letter = (char)('A' + random.nextInt(26));
                this.addGridData(i, letter);
            }
        }
    }

}
