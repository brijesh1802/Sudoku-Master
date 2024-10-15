package com.example.sudokumaster;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GridLayout sudokuBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sudokuBoard = findViewById(R.id.sudokuBoard);
        SudokuGenerator.generateSudoku(sudokuBoard);
    }
}