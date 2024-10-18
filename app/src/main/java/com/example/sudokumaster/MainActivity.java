package com.example.sudokumaster;

import static com.example.sudokumaster.SudokuGenerator.generateSudoku;
import static com.example.sudokumaster.SudokuGenerator.setupNumberButtons;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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

import java.util.Arrays;
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

        ImageView undoImg = findViewById(R.id.undoimg);


        timeSelectImg.setOnClickListener(v -> showTimeSelectionDialog());

        // Generate the Sudoku puzzle
        generateSudoku(sudokuBoard, this);

        numberButtons = new MaterialButton[9];

        numberButtons[0] = findViewById(R.id.num1);
        numberButtons[1] = findViewById(R.id.num2);
        numberButtons[2] = findViewById(R.id.num3);
        numberButtons[3] = findViewById(R.id.num4);
        numberButtons[4] = findViewById(R.id.num5);
        numberButtons[5] = findViewById(R.id.num6);
        numberButtons[6] = findViewById(R.id.num7);
        numberButtons[7] = findViewById(R.id.num8);
        numberButtons[8] = findViewById(R.id.num9);

        for (MaterialButton numberButton : numberButtons) {
            numberButton.setOnClickListener(this);
        }


        // Setup number buttons in Sudoku
        setupNumberButtons(numberButtons,this);

        // Start the timer when activity is opened
        //startTimer();

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
            //updateTimer();
        });

    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < 9; i++) {
            if (v.getId() == numberButtons[i].getId()) {
                int number = i + 1;
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
            // Get the position of the selected cell (assuming the cell stores row and column in its tag)
            Cell cellTag = (Cell) selectedCell.getTag();
            int row = cellTag.row;
            int col = cellTag.col;


            // Get the correct value from the original grid (defined in GameData)
            int correctValue = GameData.originalGrid[row][col];

            // Check if the entered number is correct
            if (number != correctValue) {
                mistakes++; // Increment mistakes if the number is incorrect
                updateMistakeCounter();
            }

            // Set the number in the selected cell
            selectedCell.setText(String.valueOf(number));

            // Optionally, update cell appearance to indicate it was filled
            selectedCell.setBackgroundColor(Color.TRANSPARENT); // Remove highlight after setting number
            selectedCell = null; // Deselect the cell after setting
        } else {
            Log.d("SudokuGame", "No cell is selected.");
        }
    }

    public void updateMistakeCounter() {
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

}
