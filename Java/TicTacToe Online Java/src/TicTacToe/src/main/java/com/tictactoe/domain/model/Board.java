package com.tictactoe.domain.model;

import java.util.Arrays;

public class Board {
    private static final int N = 3;
    private int board[][];

    public Board() {
        this.board = new int[N][N];
    }

    public Board(int[][] board) {
        this.board = new int[N][N];
        for (int i = 0; i < N; i++) {
            this.board[i] = Arrays.copyOf(board[i], N);
        }
    }

    public Board(Board other) {
        this.board = new int[N][N];
        for (int i = 0; i < N; i++) {
            this.board[i] = Arrays.copyOf(other.board[i], N);
        }
    }

    public int getCell(int row, int col) {
        return board[row][col];
    }

    public void setCell(int row, int col, int value) {
        if (row < 0 || row >= N || col < 0 || col >= N) {
            throw new IllegalArgumentException("Out of Board: (" + row + ", " + col + ")");
        }
        if (value != 0 && value != 1 && value != 2) {
            throw new IllegalArgumentException("Wrong value sign: " + value + ". Must be 0, 1, or 2");
        }
        board[row][col] = value;
    }

    public int[][] getBoard() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            copy[i] = Arrays.copyOf(board[i], N);
        }
        return copy;
    }

    public Board copy() {
        Board newBoard = new Board();
        for (int i = 0; i < N; i++) {
            System.arraycopy(this.board[i], 0, newBoard.board[i], 0, N);
        }
        return newBoard;
    }

}