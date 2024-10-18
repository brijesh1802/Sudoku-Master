package com.example.sudokumaster;

import static com.example.sudokumaster.SudokuGenerator.generateSudoku;
import static com.example.sudokumaster.SudokuGenerator.setupNumberButtons;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static TextView mistakeCounterTextView; // TextView for displaying mistakes
    public static int mistakes = 0; // Count of mistakes made
    private static final int MAX_MISTAKES = 3; // Maximum allowed mistakes
    static MaterialButton[]  numberButtons;
    private ImageView timerimg, timeSelectImg,restartimg, erasevalue;
    private TextView timerTextView;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 600000; // Default: 10 minutes
    public static GridLayout sudokuBoard;

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
        erasevalue = findViewById(R.id.erasevalue);

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
        });

        erasevalue.setOnClickListener(v->{
            SudokuGenerator.eraseSelectedCell();
        });

    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < 9; i++) {
            if (v.getId() == numberButtons[i].getId()) {
                int number = i + 1; // Get the number based on the button clicked

                // Call the method to set the number in the selected cell
                SudokuGenerator.setNumberInSelectedCell(SudokuGenerator.selectedCell, number);
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


    @SuppressLint("DefaultLocale")
    public static void updateMistakeCounter() {
        mistakeCounterTextView.setText(String.format("%d/%d", mistakes, MAX_MISTAKES)); // Update the display
        if (mistakes >= MAX_MISTAKES) {
            LayoutInflater inflater = LayoutInflater.from(mistakeCounterTextView.getContext());
            View dialogView = inflater.inflate(R.layout.custom_max_mistake_dialog, null);

            // Create and set up the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(mistakeCounterTextView.getContext());
            builder.setView(dialogView);

            // Create the AlertDialog instance
            AlertDialog dialog = builder.create();

            // Get references to the buttons in the custom layout
            Button btnContinue = dialogView.findViewById(R.id.btnContinue);
            Button btnExit = dialogView.findViewById(R.id.btnExit);

            // Handle "Continue" button click
            btnContinue.setOnClickListener(v -> {
                // Reset mistake counter and update the mistake counter TextView
                mistakes = 0;
                mistakeCounterTextView.setText("--");
                dialog.dismiss(); // Close the dialog
            });

            // Handle "Exit" button click
            btnExit.setOnClickListener(v -> {
                ((Activity) mistakeCounterTextView.getContext()).finish(); // Exit the game
            });

            // Show the dialog
            dialog.show();
        }
    }


    private static void disableNumberButtons() {
        for (MaterialButton button : numberButtons) {
            button.setEnabled(false); // Disable the number buttons
        }
    }

    public static boolean isSudokuSolved() {
        for (int i = 0; i < SudokuGenerator.GRID_SIZE; i++) {
            for (int j = 0; j < SudokuGenerator.GRID_SIZE; j++) {
                TextView textView = (TextView) sudokuBoard.getChildAt(i * SudokuGenerator.GRID_SIZE + j);
                int currentValue = textView.getText().toString().isEmpty() ? 0 : Integer.parseInt(textView.getText().toString());
                int correctValue = GameData.originalGrid[i][j];

                // Check if the current value matches the original grid
                if (currentValue != correctValue) {
                    return false;
                }
            }
        }
        return true; // If all cells match, the Sudoku is solved
    }

    public static void showCongratulationsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(sudokuBoard.getContext());
        builder.setTitle("Congratulations!");
        builder.setMessage("You've successfully solved the Sudoku puzzle!");
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
