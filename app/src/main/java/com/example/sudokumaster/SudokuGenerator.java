package com.example.sudokumaster;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuGenerator {

    private static final int GRID_SIZE = 9;


    private static TextView selectedCell = null;


    public static void generateSudoku(GridLayout sudokuBoard, Context context) {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        fillValues(grid);

        int[][] originalGrid = new int[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < GRID_SIZE; i++) {
            System.arraycopy(grid[i], 0, originalGrid[i], 0, GRID_SIZE);
        }


        removeNumbers(grid);

        sudokuBoard.removeAllViews();

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                TextView textView = new TextView(context);
                textView.setText(grid[i][j] == 0 ? "" : String.valueOf(grid[i][j]));

                textView.setTextColor(Color.BLACK);
                textView.setTextSize(24);
                textView.setGravity(Gravity.CENTER);

                // Set background for 3x3 matrix alternating color
                int gridRow = i / 3;
                int gridCol = j / 3;
                if ((gridRow + gridCol) % 2 == 0) {
                    textView.setBackgroundColor(Color.parseColor("#EEEEEE"));
                } else {
                    textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                }

                // Set LayoutParams to position the TextView in the grid
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1f);
                params.columnSpec = GridLayout.spec(j, 1f);
                params.width = 0;
                params.height = 0;
                params.setMargins(2, 2, 2, 2);
                textView.setLayoutParams(params);

                textView.setTag(originalGrid[i][j]);


                if (grid[i][j] == 0) {
                    textView.setOnClickListener(v -> {
                        if (selectedCell != null) {
                            selectedCell.setBackgroundColor(Color.TRANSPARENT); // Reset the background of the previously selected cell
                        }
                        selectedCell = textView;
                        textView.setBackgroundColor(Color.YELLOW); // Highlight selected cell
                    });
                } else {
                    textView.setClickable(false);
                    textView.setFocusable(false);
                    //textView.setBackgroundColor(Color.LTGRAY); // Change background for fixed cells
                }

                sudokuBoard.addView(textView, params);
            }
        }
    }


    // New method to remove some numbers from the filled grid
    private static void removeNumbers(int[][] grid) {
        int count = 30; // Adjust this to set how many numbers you want to remove
        while (count > 0) {
            int row = (int) (Math.random() * GRID_SIZE);
            int col = (int) (Math.random() * GRID_SIZE);
            if (grid[row][col] != 0) {
                grid[row][col] = 0; // Set the value to 0 to make it empty
                count--;
            }
        }
    }

    public static void setupNumberButtons(GridLayout sudokuBoard, Button[] numberButtons) {
        for (int i = 0; i < numberButtons.length; i++) {
            int number = i + 1; // Buttons are 1 to 9

            numberButtons[i].setOnClickListener(v -> {
                if (selectedCell != null) {
                    selectedCell.setText(String.valueOf(number));
                    selectedCell.setBackgroundColor(Color.TRANSPARENT); // Remove highlight after number is set
                    selectedCell = null; // Clear the selected cell
                }
            });
        }
    }



    private static boolean fillValues(int[][] grid) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (grid[row][col] == 0) {
                    List<Integer> numbers = new ArrayList<>();
                    for (int i = 1; i <= GRID_SIZE; i++) {
                        numbers.add(i);
                    }
                    Collections.shuffle(numbers);

                    for (int num : numbers) {
                        if (isSafe(grid, row, col, num)) {
                            grid[row][col] = num;

                            if (fillValues(grid)) {
                                return true;
                            } else {
                                grid[row][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isSafe(int[][] grid, int row, int col, int num) {
        // Check if 'num' is not present in the current row, current column, and current 3x3 subgrid
        for (int x = 0; x < 9; x++) {
            if (grid[row][x] == num || grid[x][col] == num) {
                return false;
            }
        }
        int startRow = row - row % 3;
        int startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
    }
}
