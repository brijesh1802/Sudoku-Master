package com.example.sudokumaster;

import static com.example.sudokumaster.SudokuGenerator.generateSudoku;
import static com.example.sudokumaster.SudokuGenerator.setupNumberButtons;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private TextView selectedCell; // This will hold the currently selected cell
    private TextView mistakeCounterTextView; // TextView for displaying mistakes
    private int mistakes = 0; // Count of mistakes made
    private static final int MAX_MISTAKES = 3; // Maximum allowed mistakes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout sudokuBoard = findViewById(R.id.sudokuBoard);
        mistakeCounterTextView = findViewById(R.id.mistakecounter); // Initialize mistake counter TextView

        // Generate the Sudoku puzzle
        generateSudoku(sudokuBoard, this);

        // Number buttons 1-9
        MaterialButton[] numberButtons = new MaterialButton[9];
        for (int i = 0; i < numberButtons.length; i++) {
            int resId = getResources().getIdentifier("num" + (i + 1), "id", getPackageName());
            numberButtons[i] = findViewById(resId);
        }

        // Set up number button click logic
        for (int i = 0; i < numberButtons.length; i++) {
            final int number = i + 1;
            numberButtons[i].setOnClickListener(v -> {
                Log.d("Sudoku", "Number button clicked: " + number); // Log button clicks
                setNumberInSelectedCell(number);
            });
        }


        setupNumberButtons(sudokuBoard, numberButtons);
    }

    // Method to set the number in the selected cell
    private void setNumberInSelectedCell(int number) {
        if (selectedCell != null) {
            int correctValue = (int) selectedCell.getTag(); // Get the correct value from the tag
            Log.d("Sudoku", "Entered number: " + number + ", Correct value: " + correctValue); // Log values

            if (number != correctValue) {
                mistakes++; // Increment mistakes if the number is incorrect
                updateMistakeCounter(); // Update the displayed mistake counter
                Log.d("Sudoku", "Mistakes incremented: " + mistakes); // Log the incremented mistakes
            }

            selectedCell.setText(String.valueOf(number)); // Set the number in the selected cell
            selectedCell.setBackgroundColor(Color.TRANSPARENT); // Remove highlight after setting the number
            selectedCell = null; // Deselect the cell
        }
    }


    // Method to update the mistake counter display
    private void updateMistakeCounter() {
        mistakeCounterTextView.setText(mistakes + "/" + MAX_MISTAKES); // Update the display
        if (mistakes >= MAX_MISTAKES) {
            Toast.makeText(this, "Game Over! You've made too many mistakes.", Toast.LENGTH_SHORT).show();
            disableNumberButtons();
        }
    }

    // Method to disable number buttons
    private void disableNumberButtons() {
        GridLayout sudokuBoard = findViewById(R.id.sudokuBoard);
        for (int i = 0; i < sudokuBoard.getChildCount(); i++) {
            View child = sudokuBoard.getChildAt(i);
            if (child instanceof MaterialButton) {
                child.setEnabled(false); // Disable the number buttons
            }
        }
    }
}
