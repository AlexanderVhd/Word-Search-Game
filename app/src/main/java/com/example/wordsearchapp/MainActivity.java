package com.example.wordsearchapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.wordsearchapp.Models.GameWord;
import com.example.wordsearchapp.Models.Grid;
import com.example.wordsearchapp.Models.Word;

import org.w3c.dom.Text;

import java.io.Console;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {

    TextView word_found;
    TextView numWords;
    GridView gridView;
    GridView wordsListView;
    Chronometer timer;
    ImageView homeIcon;

    //declare constants
    final int UNSELECTED = -1;

    //declare required global variables for game (word selection colors, grid cell selection, and timer)
    String currentColor = null, currentTextColor = null;
    int firstSelection = UNSELECTED, secondSelection = UNSELECTED;
    int wordsFound = 0;
    boolean timerRunning = false;
    long timerOffset;

    //declare used words, directions, and random object
    final String [] usedWords = {"APOLLO", "ZEUS", "HERA", "POSEIDEN", "HADES", "CRONOS", "GAIA", "ARES"};
    Direction[] directions = Direction.values();
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup UI widgets and get gridsize from menu activity
        setupWidgets();
        int numCol = getIntent().getIntExtra("gridSize", 10);

        //setup gamewords, grid objects, and color array variables
        final GameWord gameWords[] = new GameWord[usedWords.length];
        Grid currGrid = new Grid(numCol);
        final String[] colors = this.getResources().getStringArray(R.array.word_colors);

        //rearrange color array for game words, and set the first color for cell selection
        shuffleColors(colors);
        updateSelectionColor(colors);

        //initialize gameword objects with the used words
        for(int i = 0; i < usedWords.length; i++){
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

        //set UI elements
        numWords.setText("0/" + usedWords.length);
        startTimer();

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(view.getTag(R.string.active).equals(true)){
                    if(firstSelection == position){

                        //remove highlight on gridcell to indicate it is no longer selected
                        firstSelection = UNSELECTED;
                        selectCell(view, false);
                    }
                }
                else{
                    if(firstSelection == UNSELECTED){

                        //highlight gridcell to indicate it is selected
                        firstSelection = position;
                        selectCell(view, true);
                    }
                    else{

                        GameWord foundWord = null;

                        //highlight gridcell to indicate it is selected
                        selectCell(view, true);

                        //get view of current selection as well as view and textview of previous selection
                        TextView currTextView = view.findViewById(R.id.text_letter);
                        ViewGroup prevSelection = (ViewGroup) gridView.getChildAt(firstSelection);
                        TextView prevTextView = prevSelection.findViewById(R.id.text_letter);

                        //check if user has found any of the words with their selection
                        for(GameWord word : gameWords){
                            if((firstSelection == word.getStartCell() && position == word.getEndCell()) || (firstSelection == word.getEndCell() && position == word.getStartCell())){
                                foundWord = word;
                                break;
                            }
                        }

                        if(foundWord != null){

                            //animate all grid cell positions that are in the word
                            for(int i = 0; i < foundWord.getWordSize(); i++){
                                ViewGroup gridCell = (ViewGroup) gridView.getChildAt(foundWord.getCellPosition(i));
                                TextView cellLetter = gridCell.findViewById(R.id.text_letter);

                                animateCell(gridCell, "backgroundColor", Color.parseColor((String) gridCell.getTag(R.string.background_color)), Color.parseColor(currentColor));
                                animateCell(cellLetter, "textColor", Color.parseColor((String) gridCell.getTag(R.string.text_color)), Color.parseColor(currentTextColor));

                                gridCell.setTag(R.string.background_color, currentColor);
                                gridCell.setTag(R.string.text_color, currentTextColor);
                            }

                            //identify the found word and animate it on the words list to indicate that it has been found
                            for(int i = 0; i < wordsListView.getCount(); i++){
                                ViewGroup wordView = (ViewGroup) wordsListView.getChildAt(i);
                                CardView wordContainer = wordView.findViewById(R.id.word_item_container);
                                CardView wordBackground = wordView.findViewById(R.id.word_item_background);
                                TextView wordText = wordView.findViewById(R.id.text_word);

                                if(foundWord.getGameWord().equals(wordView.getTag())){
                                    animateCell(wordContainer, "cardBackgroundColor", Color.TRANSPARENT, Color.parseColor(currentColor));
                                    animateCell(wordBackground, "cardBackgroundColor", R.color.word_background, Color.parseColor(currentColor));
                                    animateCell(wordText, "textColor", Color.parseColor("#757575"), Color.parseColor(currentTextColor));
                                }
                            }

                            //update the words found
                            wordsFound++;
                            numWords.setText(wordsFound + "/" + usedWords.length);

                            //stop timer and end game if user has found all the words
                            if(wordsFound == usedWords.length){
                                stopTimer();

                                float wholeTimeValue = (float) timerOffset/60000;
                                int minutes = (int) ((float) timerOffset/60000);
                                double decimalTime = (wholeTimeValue - minutes) * 60;

                                String stringTime = decimalTime < 10 ? "0" + String.valueOf((int) decimalTime) : String.valueOf((int) decimalTime);

                                Log.d("Time", minutes + ":" + stringTime);
                            }

                            //update the current word color
                            updateSelectionColor(colors);
                        }
                        else{
                            animateCell(view, "backgroundColor", Color.parseColor(currentColor), Color.parseColor((String) view.getTag(R.string.background_color)));
                            animateCell(currTextView, "textColor", Color.parseColor(currentTextColor), Color.parseColor((String) view.getTag(R.string.text_color)));

                            animateCell(prevSelection, "backgroundColor", Color.parseColor(currentColor), Color.parseColor((String) prevSelection.getTag(R.string.background_color)));
                            animateCell(prevTextView, "textColor", Color.parseColor(currentTextColor), Color.parseColor((String) prevSelection.getTag(R.string.text_color)));
                        }

                        //update current view and previous view after selection
                        view.setTag(R.string.active, false);
                        prevSelection.setTag(R.string.active, false);
                        firstSelection = UNSELECTED;
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
        timer = findViewById(R.id.timer);
        homeIcon = findViewById(R.id.home_icon);
    }

    public void shuffleColors(String[] colors) {
        Random rnd = new Random();

        for (int i = colors.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);

            //swap random index with current index
            String a = colors[index];
            colors[index] = colors[i];
            colors[i] = a;
        }
    }

    public void startTimer(){
        if(!timerRunning){

            //set base for timer from the offset, then start timer
            timer.setBase(SystemClock.elapsedRealtime() - timerOffset);
            timer.start();
            timerRunning = true;
        }
    }

    public void stopTimer(){
        if(timerRunning){

            //stop timer and save the time elapsed from the point at which the timer started (used when continuing timer again)
            timer.stop();
            timerOffset = SystemClock.elapsedRealtime() - timer.getBase();
            timerRunning = false;
        }
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
        if(currGrid.getNumCol() == 8){
            gridView.setNumColumns(currGrid.getNumCol());
            gridView.setColumnWidth((int) (35 * scale + 0.5f));
        }
        else if(currGrid.getNumCol() == 12){
            gridView.setNumColumns(currGrid.getNumCol());
            gridView.setColumnWidth((int) (23 * scale + 0.5f));
        }
        else{
            gridView.setNumColumns(currGrid.getNumCol());
        }
    }

    public void populateWord(Direction dir, Grid grid, GameWord gameWords[], int gameWordIndex){

        ArrayList<Integer> cellArray = new ArrayList<Integer>();
        ArrayList<Integer> directionsList = new ArrayList<Integer>();
        int wordLength = gameWords[gameWordIndex].getGameWord().length();
        char[] word = gameWords[gameWordIndex].getGameWord().toCharArray();
        int[] positions = new int[wordLength];

        //place oridinals of all directions into array in order
        for(int d = 0; d < directions.length; d++){
            directionsList.add(d);
        }

        //determine if there are available cell positions in given direction
        int dirOrdinal = directionsList.get(dir.ordinal());
        int index = dirOrdinal;
        cellArray = findFreePositions(directions[dirOrdinal], grid, cellArray, word);

        //keep searching for available cell positions in a certain direction until at least one position is found
        while(cellArray.size() <= 0){
            directionsList.remove(index);
            index = random.nextInt(directionsList.size());
            dirOrdinal = directionsList.get(index);
            cellArray = findFreePositions(directions[dirOrdinal], grid, cellArray, word);
        }

        //choose random cell position from cellArray
        int pos = cellArray.get(random.nextInt(cellArray.size()));

        //iterate through letters in the current word
        for(int l = 0; l < wordLength; l++){

            //input letter in the grid and save the current grid cell position of the letter
            grid.addGridData(pos, word[l]);
            positions[l] = pos;

            pos = findNextCell(directions[dirOrdinal], pos, grid.getNumCol());
        }

        //save the grid cell positions of the word
        gameWords[gameWordIndex].setPositionList(positions);
    }

    public ArrayList<Integer> findFreePositions(Direction dir, Grid grid, ArrayList<Integer> cellArray, char [] word){

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
    public void selectCell(View view, boolean active){

        if(active){
            view.setTag(R.string.active, active);
            view.setBackgroundColor(Color.parseColor(currentColor));

            TextView textView = view.findViewById(R.id.text_letter);
            textView.setTextColor(Color.parseColor(currentTextColor));
        }
        else{
            view.setTag(R.string.active, active);
            view.setBackgroundColor(Color.parseColor((String) view.getTag(R.string.background_color)));

            TextView textView = view.findViewById(R.id.text_letter);
            textView.setTextColor(Color.parseColor((String) view.getTag(R.string.text_color)));
        }
    }

    public void animateCell(View view, String property, int startColor, int endColor){

        ValueAnimator animator = ObjectAnimator.ofInt(view, property, startColor, endColor);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(350);
        animator.start();

    }

    public void updateSelectionColor(String[] colors){

        //update current color and text color for grid cell selection
        currentColor = colors[wordsFound];
        currentTextColor = (currentColor.equals(getString(R.string.jonquil)) || currentColor.equals(getString(R.string.lawnGreen)) || currentColor.equals(getString(R.string.voilet))) ? "#757575" : "#FFFFFF";

    }



}