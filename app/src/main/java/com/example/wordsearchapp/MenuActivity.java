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
import android.widget.Spinner;
import android.widget.Toast;

import com.jgabrielfreitas.core.BlurImageView;

public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button startBtn;
    Spinner spinner;
    BlurImageView blurImageView;

    int gridSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //setup UI elements
        setupWidgets();

        //set blur effect on background
        blurImageView.setBlur(2);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                intent.putExtra("gridSize", gridSize);
                startActivity(intent);
            }
        });

        //setup data and theme of adapter for the dropdown menu that will display the grid options
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MenuActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.grid_options));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

    }

    public void setupWidgets(){
        startBtn = findViewById(R.id.start_btn);
        spinner =  findViewById(R.id.grid_size_option);
        blurImageView = findViewById(R.id.menu_background);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        //get array of dropdown menu options and the selected dropdown menu item
        String [] optionsList = getResources().getStringArray(R.array.grid_options);
        String option = adapterView.getItemAtPosition(i).toString();

        //set grid size based on dropdown menu item selected
        if(option.equals(optionsList[0])){
            gridSize = 10;
        }
        else if(option.equals(optionsList[1])){
            gridSize = 12;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}