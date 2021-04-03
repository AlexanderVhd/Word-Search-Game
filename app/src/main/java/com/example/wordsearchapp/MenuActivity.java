package com.example.wordsearchapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wordsearchapp.Models.GameSetup;
import com.example.wordsearchapp.Models.GridSize;
import com.example.wordsearchapp.Models.Level;

import java.util.Random;


public class MenuActivity extends AppCompatActivity{

    Button startBtn;
    Spinner spinner;
    ImageButton leftArrowGridSizeBtn;
    ImageButton rightArrowGridSizeBtn;
    ImageButton leftArrowLevelBtn;
    ImageButton rightArrowLevelBtn;
    TextView gridSizeTxt;
    TextView levelTxt;

    int levelIndex = 0, gridSizeIndex = 0;
    Level level = Level.Easy;
    GridSize gridSize = GridSize.Small;
    String theme;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //setup UI elements
        setupWidgets();

        final Level [] levelList = Level.values();
        final GridSize [] gridSizeList = GridSize.values();
        final String [] themeList = getResources().getStringArray(R.array.theme_options);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("gridSize", gridSize);
                intent.putExtra("level", level);
                intent.putExtra("theme", theme);
                startActivity(intent);
            }
        });

        leftArrowGridSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //iterate through the string array for the grid size and set the proper size
                gridSizeIndex = gridSizeIndex == 0 ? gridSizeList.length-1 : gridSizeIndex - 1;
                gridSize = gridSizeList[gridSizeIndex];

                gridSizeTxt.setText(gridSize.getNumCol() + "x" + gridSize.getNumRow() + " Grid");
            }
        });

        rightArrowGridSizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //iterate through the string array for the grid size and set the proper size
                gridSizeIndex = gridSizeIndex == gridSizeList.length-1 ? 0 : gridSizeIndex + 1;
                gridSize = gridSizeList[gridSizeIndex];

                gridSizeTxt.setText(gridSize.getNumCol() + "x" + gridSize.getNumRow() + " Grid");
            }
        });

        leftArrowLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //iterate through the string array for the levels and set the proper level
                levelIndex = levelIndex == 0 ? levelList.length-1 : levelIndex - 1;
                level = levelList[levelIndex];

                levelTxt.setText(level.toString());
            }
        });

        rightArrowLevelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //iterate through the string array for the levels and set the proper level
                levelIndex = levelIndex == levelList.length-1 ? 0 : levelIndex + 1;
                level = levelList[levelIndex];

                levelTxt.setText(level.toString());
            }
        });

        //setup array adapter for the theme option selection with the layout resource files for the spinner and spinner list UI
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.theme_options, themeList);
        adapter.setDropDownViewResource(R.layout.theme_options_dropdown);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Random rand = new Random();

                //check if random theme is selected
                if(position == 0){
                    int randomIndex = rand.nextInt(themeList.length - 1 + 1) + 1;
                    theme = themeList[randomIndex];
                }
                else{
                    theme = themeList[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //set initial content on UI carousel menus
        levelTxt.setText(levelList[levelIndex].toString());
        gridSizeTxt.setText(gridSize.getNumCol() + "x" + gridSize.getNumRow() + " Grid");
    }

    public void setupWidgets(){
        startBtn = findViewById(R.id.start_btn);
        spinner =  findViewById(R.id.theme_option_selection);
        leftArrowGridSizeBtn = findViewById(R.id.left_arrow_grid_size);
        rightArrowGridSizeBtn = findViewById(R.id.right_arrow_grid_size);
        gridSizeTxt = findViewById(R.id.grid_size_txt);
        leftArrowLevelBtn = findViewById(R.id.left_arrow_level);
        rightArrowLevelBtn = findViewById(R.id.right_arrow_level);
        levelTxt = findViewById(R.id.level_txt);
    }


}