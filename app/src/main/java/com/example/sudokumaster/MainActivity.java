package com.example.sudokumaster;

import static com.example.sudokumaster.SudokuGenerator.generateSudoku;
import static com.example.sudokumaster.SudokuGenerator.setupNumberButtons;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.Stack;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView selectedCell; // This will hold the currently selected cell
    private TextView mistakeCounterTextView; // TextView for displaying mistakes
    private int mistakes = 0; // Count of mistakes made
    private static final int MAX_MISTAKES = 3; // Maximum allowed mistakes
    MaterialButton[] numberButtons;
    private ImageView timerimg, timeSelectImg,restartimg;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 600000; // Default: 10 minutes
    GridLayout sudokuBoard;
    private Stack<Integer> previousMoves; // Stack to keep track of previous moves
    private Stack<TextView> previousCells; // Stack to keep track of the cells modified

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sudokuBoard = findViewById(R.id.sudokuBoard);
        mistakeCounterTextView = findViewById(R.id.mistakecounter);
        timerimg = findViewById(R.id.timerimg);
        timerTextView = findViewById(R.id.timer);
        timeSelectImg = findViewById(R.id.timeselect);
        restartimg = findViewById(R.id.restartimg);
        previousMoves = new Stack<>(); // Initialize the stack for moves
        previousCells = new Stack<>(); // Initialize the stack for cells

        ImageView undoImg = findViewById(R.id.undoimg);
        undoImg.setOnClickListener(v -> undoLastMove());

        timeSelectImg.setOnClickListener(v -> showTimeSelectionDialog());

        // Generate the Sudoku puzzle
        generateSudoku(sudokuBoard, this);

        // Initialize number buttons
        numberButtons = new MaterialButton[9];
        for (int i = 0; i < numberButtons.length; i++) {
            int resId = getResources().getIdentifier("num" + (i + 1), "id", getPackageName());
            numberButtons[i] = findViewById(resId);
            numberButtons[i].setOnClickListener(this);
        }

        // Setup number buttons in Sudoku
        setupNumberButtons(sudokuBoard, numberButtons);

        // Start the timer when activity is opened
        startTimer();

        // Set onClickListener to pause/resume the timer
        timerimg.setOnClickListener(v -> {
            if (isTimerRunning) {
                pauseTimer();
            } else {
                resumeTimer();
            }
        });
        restartimg.setOnClickListener(v -> {
            generateSudoku(sudokuBoard, getApplicationContext());
            updateTimer();
        });

    }

    @Override
    public void onClick(View v) {
        // Check if the clicked view is a number button
        for (int i = 0; i < 9; i++) {
            if (v.getId() == numberButtons[i].getId()) {
                int number = i + 1; // Get the number corresponding to the button
                setNumberInSelectedCell(number);
                break;
            }
        }
    }

    private void showTimeSelectionDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_timer_selection);

        RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup);
        Button setTimeBtn = dialog.findViewById(R.id.btn_set_time);

        setTimeBtn.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();

            if (selectedId == R.id.radio_5min) {
                timeLeftInMillis = 300000; // 5 minutes
                timerTextView.setText("05:00");
                timerimg.setEnabled(true);
                generateSudoku(sudokuBoard, getApplicationContext());
                startTimer();
            } else if (selectedId == R.id.radio_10min) {
                timeLeftInMillis = 600000; // 10 minutes
                timerTextView.setText("10:00");
                generateSudoku(sudokuBoard, getApplicationContext());
                timerimg.setEnabled(true);
                startTimer();
            } else if (selectedId == R.id.radio_15min) {
                timeLeftInMillis = 900000; // 15 minutes
                timerTextView.setText("15:00");
                generateSudoku(sudokuBoard, getApplicationContext());
                timerimg.setEnabled(true);
                startTimer();
            } else if (selectedId == R.id.radio_no_time) {
                pauseTimer();
                timerTextView.setText("--");
                generateSudoku(sudokuBoard, getApplicationContext());
                timerimg.setEnabled(false);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                // Timer finished, handle game over
            }
        }.start();

        isTimerRunning = true;
        timerimg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause)); // Change to pause icon
    }

    private void pauseTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        isTimerRunning = false;
        timerimg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play)); // Change to play icon
    }

    private void resumeTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                // Timer finished, handle game over
            }
        }.start();

        isTimerRunning = true;
        timerimg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause)); // Change to pause icon
    }

    private void updateTimer() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void setNumberInSelectedCell(int number) {
        if (selectedCell != null) {
            int correctValue = (int) selectedCell.getTag(); // Get the correct value from the tag

            if (number != correctValue) {
                mistakes++; // Increment mistakes if the number is incorrect
                updateMistakeCounter();
            }

            previousMoves.push(Integer.parseInt(selectedCell.getText().toString()));
            previousCells.push(selectedCell);

            selectedCell.setText(String.valueOf(number)); // Set the number in the selected cell
            selectedCell.setBackgroundColor(Color.TRANSPARENT); // Remove highlight
            selectedCell = null; // Deselect the cell
        }
    }

    private void updateMistakeCounter() {
        mistakeCounterTextView.setText(mistakes + "/" + MAX_MISTAKES); // Update the display
        if (mistakes >= MAX_MISTAKES) {
            Toast.makeText(this, "Game Over! You've made too many mistakes.", Toast.LENGTH_SHORT).show();
            disableNumberButtons();
        }
    }

    private void disableNumberButtons() {
        for (MaterialButton button : numberButtons) {
            button.setEnabled(false); // Disable the number buttons
        }
    }

    // Method to undo the last move
    private void undoLastMove() {
        if (!previousMoves.isEmpty() && !previousCells.isEmpty()) {
            // Get the last number and cell
            int lastNumber = previousMoves.pop();
            TextView lastCell = previousCells.pop();

            // Restore the last number in the cell
            lastCell.setText(String.valueOf(lastNumber));
            lastCell.setBackgroundColor(Color.TRANSPARENT); // Optionally reset the background color
        } else {
            Toast.makeText(this, "No more moves to undo!", Toast.LENGTH_SHORT).show();
        }
    }

}
