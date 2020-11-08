package com.example.wordsearchapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.wordsearchapp.Models.GameWord;
import com.example.wordsearchapp.Models.Grid;
import com.example.wordsearchapp.Models.Word;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView word_found;
    TextView numWords;
    GridView gridView;
    GridView wordsListView;

    int gridSize;
    int wordsFound = 0;

    //declare used words, directions, and random object
    final String [] usedWords = {"JAVA", "SWIFT", "KOTLIN", "OBJECTIVEC", "VARIABLE", "MOBILE"};
    Direction[] directions = Direction.values();
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup UI widgets
        setupWidgets();

        //get gridsize from menu activity
        gridSize = getIntent().getIntExtra("gridSize", 10);

        //setup UI toolbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        //setup gamewords and grid objects with user input
        GameWord gameWords[] = new GameWord[usedWords.length];
        Grid currGrid = new Grid(gridSize);

        //initialize gameword objects with the used words
        for(int i = 0; i< usedWords.length; i++){
            gameWords[i] = new GameWord(usedWords[i].length(), usedWords[i]);
        }

        //populate the grid
        populateGrid(currGrid, gameWords);
        currGrid.fillGrid();

        //set the number of columns for the grid
        gridView.setNumColumns(gridSize);

        //set grid adapter to display the grid
        GridAdapter gridAdapter = new GridAdapter(this, currGrid);
        gridView.setAdapter(gridAdapter);

        //set words list grid adapter to display the used words
        WordAdapter wordAdapter = new WordAdapter(this, gameWords);
        wordsListView.setAdapter(wordAdapter);

        numWords.setText(wordsFound + "/" + usedWords.length);
    }

    public void setupWidgets(){
        gridView = (GridView) findViewById(R.id.grid);
        word_found = findViewById(R.id.word_found_txt);
        numWords = findViewById(R.id.num_words_found);
        wordsListView = findViewById(R.id.words_list);
    }

    public void populateGrid(Grid grid, GameWord gameWords[]){

        //iterate through every word in the used words list
        for(int i = 0; i < gameWords.length; i++){

            Direction wordDirection = chooseDirection(directions);

            switch (wordDirection){
                case Top:
                    populateWord(Direction.Top, grid, gameWords, i);
                    break;

                case Left:
                    populateWord(Direction.Left, grid, gameWords, i);
                    break;

                case Right:
                    populateWord(Direction.Right, grid, gameWords, i);
                    break;

                case Down:
                    populateWord(Direction.Down, grid, gameWords, i);
                    break;

                case TopLeft:
                    populateWord(Direction.TopLeft, grid, gameWords, i);
                    break;

                case TopRight:
                    populateWord(Direction.TopRight, grid, gameWords, i);
                    break;

                case DownLeft:
                    populateWord(Direction.DownLeft, grid, gameWords, i);
                    break;

                case DownRight:
                    populateWord(Direction.DownRight, grid, gameWords, i);
                    break;

            }
        }
    }

    public void populateWord(Direction dir, Grid grid, GameWord gameWords[], int gameWordIndex){

        ArrayList<Integer> cellArray = new ArrayList<Integer>();
        ArrayList<Integer> directionsList = new ArrayList<Integer>();
        int wordLength = gameWords[gameWordIndex].getGameWord().length();
        char[] word = gameWords[gameWordIndex].getGameWord().toCharArray();

        //place oridinals of all directions into array in order
        for(int d = 0; d < directions.length; d++){
            directionsList.add(d);
        }

        int dirOrdinal = directionsList.get(dir.ordinal());
        int index = dirOrdinal;
        cellArray = findCellPositions(directions[dirOrdinal], grid, cellArray, word);

        //keep searching for available cell positions in a certain direction until positions are found
        while(cellArray.size() <= 0){
            directionsList.remove(index);
            index = random.nextInt(directionsList.size());
            dirOrdinal = directionsList.get(index);
            cellArray = findCellPositions(directions[dirOrdinal], grid, cellArray, word);
        }

        //choose random cell position from cellArray
        int pos = cellArray.get(random.nextInt(cellArray.size()));

        //iterate through letters in the current word
        for(int l = 0; l < wordLength; l++){

            //input letter in the grid
            grid.addGridData(pos, word[l]);

            if(l == 0){
                gameWords[gameWordIndex].setStartCell(pos);
            }
            else if(l == wordLength -1){
                gameWords[gameWordIndex].setEndCell(pos);
            }
            pos = findNextCell(directions[dirOrdinal], pos, grid.getNumCol());
        }
    }

    public ArrayList<Integer> findCellPositions(Direction dir, Grid grid, ArrayList<Integer> cellArray, char [] word){

        int gridSize = grid.getGridSize();
        int numCol = grid.getNumCol();
        int wordLength = word.length;

        //iterate through grid cells
        for(int c = 0; c < gridSize; c++){
            if(checkCell(dir, c, numCol, wordLength)){
                int nextCell = c;
                boolean letterPos = true;

                //iterate through letters in the current word
                for(int l = 0; l < wordLength; l++){

                    //check letter positions on grid to see if they are empty or have a matching letter
                    if(sameLetter(word[l], grid.getGridData(nextCell)) || grid.getGridData(nextCell) == '\u0000'){
                        nextCell = findNextCell(dir, nextCell, numCol);
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

    public boolean checkCell(Direction dir, int currentCell, int numCol, int wordLength){
        switch (dir){
            case Top:
                return currentCell+1 > numCol * (wordLength-1);

            case Left:
                return (currentCell+1) % numCol >= wordLength || (currentCell+1) % numCol == 0;

            case Right:
                return (currentCell+1) % numCol <= numCol - (wordLength-1) && (currentCell+1) % numCol != 0;

            case Down:
                return currentCell+1 <= (numCol * numCol) - (numCol * (wordLength - 1));

            case TopLeft:
                return (currentCell+1 > numCol * (wordLength-1)) && ((currentCell+1) % numCol >= wordLength || (currentCell+1) % numCol == 0);

            case TopRight:
                return (currentCell+1 > numCol * (wordLength-1)) && ((currentCell+1) % numCol <= numCol - (wordLength-1) && (currentCell+1) % numCol != 0);

            case DownLeft:
                return (currentCell+1 <= (numCol * numCol) - (numCol * (wordLength - 1))) && ((currentCell+1) % numCol >= wordLength || (currentCell+1) % numCol == 0);

            case DownRight:
                return (currentCell+1 <= (numCol * numCol) - (numCol * (wordLength - 1))) && ((currentCell+1) % numCol <= numCol - (wordLength-1) && (currentCell+1) % numCol != 0);

            default:
                return false;
        }
    }

    public int findNextCell(Direction dir, int currentCell, int numCol){
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

}