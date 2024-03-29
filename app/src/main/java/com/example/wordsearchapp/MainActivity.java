package com.example.wordsearchapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.wordsearchapp.Models.GameSetup;
import com.example.wordsearchapp.Models.GameWord;
import com.example.wordsearchapp.Models.Grid;
import com.example.wordsearchapp.Models.GridSize;
import com.example.wordsearchapp.Models.Level;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements CallBackListener{

    TextView numWords;
    GridView gridView;
    GridView wordsListView;
    Chronometer timer;
    ImageView homeIcon;
    ImageView reloadIcon;
    ImageView pausePlayIcon;

    //declare constants
    final int UNSELECTED = -1;

    //declare required global variables for game (game grid, game words, word selection colors, grid cell selection, and timer)
    GameSetup gameSetup;
    Grid currGrid;
    GameWord gameWords[];
    GridAdapter gridAdapter;
    WordAdapter wordAdapter;
    String currentColor = null, currentTextColor = null;
    int firstSelection = UNSELECTED;
    int wordsFound = 0;
    boolean timerRunning = false;
    long timerOffset;
    boolean paused = false;

    //declare used words and colors
    String [] usedWords;
    String [] colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup UI widgets and get game data from menu activity
        setupWidgets();
        GridSize gridSize = (GridSize) getIntent().getSerializableExtra("gridSize");
        Level level = (Level) getIntent().getSerializableExtra("level");
        String theme = getIntent().getStringExtra("theme");

        //setup game settings and word data that will be used for the current game
        gameSetup = new GameSetup(gridSize, level, theme);
        colors = this.getResources().getStringArray(R.array.word_colors);
        usedWords = fetchWordSet();

        //initiate word search game
        setupGame();

        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

        reloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });

        pausePlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(paused){

                    //set UI elements for a on going game
                    pausePlayIcon.setImageResource(R.drawable.pause_icon);
                    animateGrid(gridView, View.ALPHA, 0f, 1f);

                    //resume the timer
                    paused = false;
                    startTimer();
                }
                else{

                    //set UI elements for a paused game
                    pausePlayIcon.setImageResource(R.drawable.play_icon);
                    animateGrid(gridView, View.ALPHA, 1f, 0f);

                    //pause the timer
                    paused = true;
                    stopTimer();

                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(view.getTag(R.string.active).equals(true)){
                    if(firstSelection == position){

                        //remove highlight on gridcell to indicate it is no longer selected
                        firstSelection = UNSELECTED;
                        selectCell((CardView)view, false);
                    }
                }
                else{
                    if(firstSelection == UNSELECTED){

                        //highlight gridcell to indicate it is selected
                        firstSelection = position;
                        selectCell( (CardView) view, true);
                    }
                    else{

                        GameWord foundWord = null;

                        //highlight gridcell to indicate it is selected
                        selectCell((CardView)view, true);

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

                            //animate word on grid and words list
                            animateWord(foundWord);

                            //update the words found
                            wordsFound++;
                            numWords.setText(wordsFound + "/" + usedWords.length);

                            //stop timer and end game if user has found all the words
                            if(wordsFound == usedWords.length){
                                stopTimer();

                                //set time and words found data for end game fragment
                                Bundle bundle = new Bundle();
                                bundle.putInt("timeValue", (int) timerOffset);
                                bundle.putInt("wordsFound", wordsFound);
                                bundle.putSerializable("level", gameSetup.getLevel());

                                //setup endgame fragment with data bundle
                                EndGameFragment endGameFragment = new EndGameFragment();
                                endGameFragment.setArguments(bundle);
                                endGameFragment.setCancelable(false);
                                endGameFragment.show(getSupportFragmentManager(), "EndGame");

                            }

                            //update the current word color
                            updateSelectionColor(colors);
                        }
                        else{
                            animateCell(view, "cardBackgroundColor", Color.parseColor(currentColor), Color.parseColor((String) view.getTag(R.string.background_color)));
                            animateCell(currTextView, "textColor", Color.parseColor(currentTextColor), Color.parseColor((String) view.getTag(R.string.text_color)));

                            animateCell(prevSelection, "cardBackgroundColor", Color.parseColor(currentColor), Color.parseColor((String) prevSelection.getTag(R.string.background_color)));
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
        reloadIcon = findViewById(R.id.reload_grid);
        pausePlayIcon = findViewById(R.id.pause_play_game);
        numWords = findViewById(R.id.num_words_found);
        wordsListView = findViewById(R.id.words_list);
        timer = findViewById(R.id.timer);
        homeIcon = findViewById(R.id.home_icon);
    }

    public void setupGame(){


        //get density of screen for conversion of dp to px
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;

        //set grid cell selection colors
        shuffleColors(colors);
        updateSelectionColor(colors);

        //set game variables (grid, game words)
        setGameVariables();

        //setup the grid data (check if word population of grid was successful, if not, then repeat)
        while(!currGrid.populateGrid(gameWords)){
            setGameVariables();
        }

        //fill rest of the grid
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

        //set grid adapter to display the grid
        gridAdapter = new GridAdapter(this, currGrid);
        gridView.setAdapter(gridAdapter);

        //set words list grid adapter to display the used words
        wordAdapter = new WordAdapter(this, gameWords);
        wordsListView.setAdapter(wordAdapter);

        //set UI elements
        numWords.setText("0/" + usedWords.length);
        startTimer();
    }

    public String[] fetchWordSet(){

        Random random = new Random();
        Set<String> wordSet = new HashSet<String>();

        try{
            //read entire json file containing the word sets into memory
            InputStream inputStream = this.getResources().openRawResource(R.raw.wordsetsjson);
            byte [] buffer = new byte[inputStream.available()];
            while (inputStream.read(buffer) != -1);

            //convert buffer content into string and parse string into json array
            String jsonText = new String(buffer);
            JSONObject entries = new JSONObject(jsonText);

            //filter entries based on theme and determine random amount of words for game
            JSONArray themeWords = entries.getJSONArray(gameSetup.getTheme());
            int numWords = random.nextInt(gameSetup.getMaxNumWords() - gameSetup.getMinNumWords() + 1) + gameSetup.getMinNumWords();

            //fill the set with random words from the theme words json array
            while(wordSet.size() <= numWords){
                int index = random.nextInt(themeWords.length());
                String word = themeWords.getString(index);

                if(!wordSet.contains(word) && word.length() <= gameSetup.getMaxWordLength()){
                    wordSet.add(word);
                }
            }
        }
        catch (Exception ex){
            Log.i("Error", ex.getMessage());
        }

        //return the string array copy of the word set
        return Arrays.copyOf(wordSet.toArray(), wordSet.size(), String[].class);
    }

    public void setGameVariables(){

        //setup grid object
        currGrid = new Grid(gameSetup.getGridSize().getNumCol(), gameSetup.getGridSize().getNumRow());

        //setup game words used for word search
        gameWords = new GameWord[usedWords.length];

        //initialize game words
        for(int i = 0; i < usedWords.length; i++){
            gameWords[i] = new GameWord(usedWords[i].length(), usedWords[i]);
        }
    }

    public void resetGame(){

        //reset all variables for game
        currentColor = null;
        currentTextColor = null;
        wordsFound = 0;
        firstSelection = UNSELECTED;

        //reset variables for timer
        timerRunning = false;
        timerOffset = 0;

        setupGame();
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
     * Grid selection functionality and gameplay methods
     */
    public void selectCell(CardView view, boolean active){

        if(active){
            view.setTag(R.string.active, true);
            view.setCardBackgroundColor(Color.parseColor(currentColor));

            TextView textView = view.findViewById(R.id.text_letter);
            textView.setTextColor(Color.parseColor(currentTextColor));
        }
        else{
            view.setTag(R.string.active, false);
            view.setCardBackgroundColor(Color.parseColor((String) view.getTag(R.string.background_color)));

            TextView textView = view.findViewById(R.id.text_letter);
            textView.setTextColor(Color.parseColor((String) view.getTag(R.string.text_color)));
        }
    }

    public void animateCell(View view, String property, int startValue, int endValue){

        ValueAnimator animator = ObjectAnimator.ofInt(view, property, startValue, endValue);
        animator.setEvaluator(new ArgbEvaluator());
        animator.setDuration(350);
        animator.start();
    }

    public void updateSelectionColor(String[] colors){

        //update current color and text color for grid cell selection
        currentColor = colors[wordsFound];
        currentTextColor = (currentColor.equals(getString(R.string.jonquil)) || currentColor.equals(getString(R.string.lawnGreen)) || currentColor.equals(getString(R.string.voilet))) ? "#0A0A0A" : "#FFFFFF";

    }

    public void animateGrid(View view, Property<View, Float> property, float startValue, float endValue){

        ValueAnimator animator = ObjectAnimator.ofFloat(view, property, startValue, endValue);
        animator.setDuration(250);
        animator.start();
    }

    public void animateWord(GameWord foundWord){

        //animate all grid cell positions that are in the word
        for(int i = 0; i < foundWord.getWordSize(); i++){
            ViewGroup gridCell = (ViewGroup) gridView.getChildAt(foundWord.getCellPosition(i));
            CardView cellContainer = gridCell.findViewById(R.id.grid_cell_container);
            TextView cellLetter = gridCell.findViewById(R.id.text_letter);

            animateCell(cellContainer, "cardBackgroundColor", Color.parseColor((String) gridCell.getTag(R.string.background_color)), Color.parseColor(currentColor));
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
                animateCell(wordBackground, "cardBackgroundColor", R.color.secondaryTheme, Color.parseColor(currentColor));
                animateCell(wordText, "textColor", Color.parseColor("#757575"), Color.parseColor(currentTextColor));
            }
        }
    }

    @Override
    public void onDismiss() {
        resetGame();
    }
}