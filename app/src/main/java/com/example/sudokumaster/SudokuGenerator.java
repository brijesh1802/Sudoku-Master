package com.example.sudokumaster;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.material.resources.CancelableFontCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SudokuGenerator {

    private static final int GRID_SIZE = 9;

    public static void generateSudoku(GridLayout sudokuBoard) {
        int[][] grid = new int[GRID_SIZE][GRID_SIZE];
        fillValues(grid);

        // Clear existing views in the GridLayout
        sudokuBoard.removeAllViews();

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                TextView textView = new TextView(sudokuBoard.getContext());
                textView.setText(grid[i][j] == 0 ? "" : String.valueOf(grid[i][j])); // Display 0 as empty


                // THEN, set the alternate background color for 3x3 matrices
                int gridRow = i / 3;
                int gridCol = j / 3;
                if ((gridRow + gridCol) % 2 == 0) {
                    textView.setBackgroundColor(Color.parseColor("#EEEEEE")); // Light gray
                } else {
                    textView.setBackgroundColor(Color.parseColor("#FFFFFF")); // White
                }

                textView.setTextColor(Color.BLACK);
                textView.setTextSize(24);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i, 1f); // Distribute rows evenly
                params.columnSpec = GridLayout.spec(j, 1f); // Distribute columns evenly
                sudokuBoard.addView(textView, params);
            }
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
                                grid[row][col] = 0; // Backtrack
                            }
                        }
                    }
                    return false; // Trigger backtracking
                }
            }
        }
        return true; // Puzzle solved
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
