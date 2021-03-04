package com.example.wordsearchapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.telecom.Call;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.wordsearchapp.Models.Level;

import org.w3c.dom.Text;

public class EndGameFragment extends DialogFragment {

    private TextView foundWordsTxt;
    private TextView levelScoreTxt;
    private TextView timeScoreTxt;
    private TextView totalScoreTxt;
    private Button homeBtn;
    private Button restartBtn;
    private CallBackListener callBackListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //set the callbacklistener as parent activity
        if(getActivity() instanceof CallBackListener){
            callBackListener = (CallBackListener) getActivity();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //setup view (inflate view and set layout of fragment window)
        View view = inflater.inflate(R.layout.fragment_end_game, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setupWidgets(view);

        //get score data from the arguments
        int timeValue = getArguments().getInt("timeValue");
        int gamePoints = getArguments().getInt("wordsFound");
        Level level = (Level) getArguments().getSerializable("level");

        //set variables used for the point calculation
        double levelPoints, totalPoints, timePoints;
        double easyMillisecond = 300000;  // 5 minute window to get points for easy mode
        double mediumMillisecond = 480000;  // 8 minute window to get points for medium mode
        double hardMillisecond = 660000;  // 11 minute window to get points for hard mode

        //calculate level points and time points
        if(level.equals(Level.EASY)){

            levelPoints = 40;  //calculate level points (40 points for easy mode)

            //calculate timer points based on inverse of time value where less time taken means more points with with a maximum of 100 points and minimum of 10 points
            //if time taken is more than 90% of 5 minutes then 10 points is earned (last 10% of 5 minutes goes under 10 points)
            timePoints = timeValue < (0.9 * easyMillisecond) ? ((easyMillisecond - timeValue) / easyMillisecond) * 100 : 10;
        }
        else if(level.equals(Level.MEDIUM)){

            levelPoints = 70;  //calculate level points (40 points for easy mode)

            //calculate timer points based on inverse of time value where less time taken means more points with with a maximum of 100 points and minimum of 10 points
            //if time taken is more than 90% of 8 minutes then 10 points is earned (last 10% of 8 minutes goes under 10 points)
            timePoints = timeValue < (0.9 * mediumMillisecond) ? ((mediumMillisecond - timeValue) / mediumMillisecond) * 100 : 10;
        }
        else{

            levelPoints = 100;  //calculate level points (40 points for easy mode)

            //calculate timer points based on inverse of time value where less time taken means more points with with a maximum of 100 points and minimum of 10 points
            //if time taken is more than 90% of 11 minutes then 10 points is earned (last 10% of 11 minutes goes under 10 points)
            timePoints = timeValue < (0.9 * hardMillisecond) ? ((hardMillisecond - timeValue) / hardMillisecond) * 100 : 10;
        }

        //calculate game points and total points
        gamePoints = gamePoints * 5;
        totalPoints = gamePoints + levelPoints + timePoints;

        //set UI for fragment
        foundWordsTxt.setText(Integer.toString(gamePoints));
        levelScoreTxt.setText(Integer.toString((int) levelPoints));
        timeScoreTxt.setText(Integer.toString((int) timePoints));
        totalScoreTxt.setText(Integer.toString((int) totalPoints));

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MenuActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //call method in main activity when dismissing dialog fragment
                if(callBackListener != null){
                    callBackListener.onDismiss();
                }
                dismiss();
            }
        });

        return view;

    }

    public void setupWidgets(View view){
        foundWordsTxt = view.findViewById(R.id.found_words);
        timeScoreTxt = view.findViewById(R.id.time_score);
        levelScoreTxt = view.findViewById(R.id.level_score);
        totalScoreTxt = view.findViewById(R.id.total_score);
        homeBtn = view.findViewById(R.id.home_btn);
        restartBtn = view.findViewById(R.id.restart_btn);
    }
}