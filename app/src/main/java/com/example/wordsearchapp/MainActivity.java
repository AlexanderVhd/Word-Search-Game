package com.example.wordsearchapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.wordsearchapp.Models.GameWord;
import com.example.wordsearchapp.Models.Grid;
import com.example.wordsearchapp.Models.Word;

import org.w3c.dom.Text;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView word_found;
    TextView numWords;
    GridView gridView;
    GridView wordsListView;

    int numCol;
    int wordsFound = 0;

    //declare used words, directions, and random object
    final String [] usedWords = {"APOLLO", "ZEUS", "HERA", "HADES", "CRONOS", "GAIA", "ARES"};
    Direction[] directions = Direction.values();
    Random random = new Random();

    //declare variables for grid cell selection
    int firstSelection = -1, secondSelection = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup UI widgets and UI toolbar
        setupWidgets();

        //get gridsize from menu activity
        numCol = getIntent().getIntExtra("gridSize", 10);

        //setup gamewords and grid objects with user input
        final GameWord gameWords[] = new GameWord[usedWords.length];
        Grid currGrid = new Grid(numCol);

        //initialize gameword objects with the used words
        for(int i = 0; i< usedWords.length; i++){
            gameWords[i] = new GameWord(usedWords[i].length(), usedWords[i]);
        }

        //setup data and layout of the word search grid
        setupGrid(currGrid, gameWords);

        //set grid adapter to display the grid
        final GridAdapter gridAdapter = new GridAdapter(this, currGrid);
        gridView.setAdapter(gridAdapter);

        //set words list grid adapter to display the used words
        WordAdapter wordAdapter = new WordAdapter(this, gameWords);
        wordsListView.setAdapter(wordAdapter);

        numWords.setText(wordsFound + "/" + usedWords.length);

        for(GameWord word : gameWords){
            Log.d("Words", word.toString() + " " + Integer.toString(word.getStartCell()) + Integer.toString(word.getEndCell()));
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(view.getTag().equals(true)){
                    /*if(secondSelection == position){
                        secondSelection = -1;
                        highlightCell(view, false);
                    }
                    else if(firstSelection == position){
                        firstSelection = -1;
                        highlightCell(view, false);
                    }*/
                    if(firstSelection == position){

                        //remove highlight on gridcell to indicate it is no longer selected
                        firstSelection = -1;
                        highlightCell(view, false);
                    }
                }
                else{
                    /*if(firstSelection == -1){
                        firstSelection = position;
                        highlightCell(view, true);
                    }
                    else if(secondSelection == -1){
                        secondSelection = position;
                        highlightCell(view, true);

                        for(GameWord word : gameWords){
                            if((firstSelection == word.getStartCell() && secondSelection == word.getEndCell()) || (firstSelection == word.getEndCell() && secondSelection == word.getStartCell())){
                                Log.d("Test", "Word Found: " + word.toString());
                                break;
                            }
                        }
                    }
                    else{
                        ViewGroup prevSelection;

                        if(secondSelection == position){
                            secondSelection = -1;
                            prevSelection = (ViewGroup) gridView.getChildAt(secondSelection);
                            highlightCell(prevSelection, false);

                        }
                        else if(firstSelection == position){
                            firstSelection = -1;
                            highlightCell(view, false);
                        }

                        secondSelection = position;
                        highlightCell(view, true);
                    }*/
                    if(firstSelection == -1){
                        firstSelection = position;
                        highlightCell(view, true);
                    }
                    else if(secondSelection == -1){

                        //highlight gridcell to indicate it is selected
                        highlightCell(view, true);

                        //get letter of current selection as well as the view and letter of the previous selection
                        TextView currTextView = view.findViewById(R.id.text_letter);
                        ViewGroup prevSelection = (ViewGroup) gridView.getChildAt(firstSelection);
                        TextView prevTextView = prevSelection.findViewById(R.id.text_letter);

                        animate(view, "backgroundColor", Color.parseColor("#445478"), Color.TRANSPARENT);
                        animate(currTextView, "textColor", Color.parseColor("#FFFFFF"), Color.parseColor("#757575"));

                        animate(prevSelection, "backgroundColor", Color.parseColor("#445478"), Color.TRANSPARENT);
                        animate(prevTextView, "textColor", Color.parseColor("#FFFFFF"), Color.parseColor("#757575"));

                        for(GameWord word : gameWords){
                            if((firstSelection == word.getStartCell() && secondSelection == word.getEndCell()) || (firstSelection == word.getEndCell() && secondSelection == word.getStartCell())){
                                Log.d("Test", "Word Found: " + word.toString());
                                break;
                            }
                        }

                        view.setTag(false);
                        prevSelection.setTag(false);
                        secondSelection = -1;
                        firstSelection = -1;
                    }
                }
            }
        });
    }

    public void setupWidgets(){
        gridView = (GridView) findViewById(R.id.grid);
        word_found = findViewById(R.id.word_found_txt);
        numWords = findViewById(R.id.num_words_found);
        wordsListView = findViewById(R.id.words_list);
    }

    /*
    * Grid layout and data setup methods
     */
    public void setupGrid(Grid currGrid, GameWord gameWords[]){

        //get density of screen for conversion of dp to px
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;

        //iterate through every word in the used words list and use them to populate the grid
        for(int i = 0; i < gameWords.length; i++){

            Direction wordDirection = chooseDirection(directions);

            switch (wordDirection){
                case Top:
                    populateWord(Direction.Top, currGrid, gameWords, i);
                    break;

                case Left:
                    populateWord(Direction.Left, currGrid, gameWords, i);
                    break;

                case Right:
                    populateWord(Direction.Right, currGrid, gameWords, i);
                    break;

                case Down:
                    populateWord(Direction.Down, currGrid, gameWords, i);
                    break;

                case TopLeft:
                    populateWord(Direction.TopLeft, currGrid, gameWords, i);
                    break;

                case TopRight:
                    populateWord(Direction.TopRight, currGrid, gameWords, i);
                    break;

                case DownLeft:
                    populateWord(Direction.DownLeft, currGrid, gameWords, i);
                    break;

                case DownRight:
                    populateWord(Direction.DownRight, currGrid, gameWords, i);
                    break;

            }
        }

        currGrid.fillGrid();

        //setup layout of grid depending on the grid size selected
        if(numCol == 8){
            gridView.setNumColumns(numCol);
            gridView.setColumnWidth((int) (35 * scale + 0.5f));
        }
        else if(numCol == 12){
            gridView.setNumColumns(numCol);
            gridView.setColumnWidth((int) (23 * scale + 0.5f));
        }
        else{
            gridView.setNumColumns(numCol);
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

        //determine if there are available cell positions in given direction
        int dirOrdinal = directionsList.get(dir.ordinal());
        int index = dirOrdinal;
        cellArray = findCellPositions(directions[dirOrdinal], grid, cellArray, word);

        //keep searching for available cell positions in a certain direction until at least one position is found
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

    /*
     * Grid selection functionality methods
     */
    public void highlightCell(View view, boolean active){

        if(active){
            view.setTag(active);
            view.setBackgroundColor(Color.parseColor("#445478"));

            TextView textView = view.findViewById(R.id.text_letter);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        else{
            view.setTag(active);
            view.setBackgroundColor(Color.TRANSPARENT);

            TextView textView = view.findViewById(R.id.text_letter);
            textView.setTextColor(Color.parseColor("#757575"));
        }

    }

    public void animate(View view, String property, int startColor, int endColor){

        ValueAnimator animator = ObjectAnimator.ofInt(view, property, startColor, endColor);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(400);
        animator.start();

    }

}