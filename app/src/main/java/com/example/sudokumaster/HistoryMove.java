package com.example.sudokumaster;

import java.util.Stack;

// Create a class to represent a move
import java.util.Stack;

// Create a class to represent a move
class HistoryMove {
    int row;
    int col;
    int value;

    Stack<HistoryMove> moveStack = new Stack<>();

    HistoryMove(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}
