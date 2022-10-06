package com.example.puzzle15squares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *  SurfaceView: 15 squares game. Draws a grid, populates grid squares with randomized numbers, and controls game logic.
 *
 * @author Ronnie Delos Santos
 */
public class PuzzleSurface extends SurfaceView {

    /**
     *  currentSize determines the side length of the grid.
     *  _pixelsPerSquare is a placeholder for the standard size of each square.
     */
    public static int currentSize = 4;
    public static float _pixelsPerSquare;

    /**
     *  gridHeight determines the height of the grid.
     *  gridWidth determines the width of the grid.
     *  gridCanvas is declared so that other methods can access the canvas.
     */
    public static float gridHeight;
    public static float gridWidth;
    public static Canvas gridCanvas;

    /**
     *  puzzleGrid is a 2-dimensional array holding all the GridSquare objects.
     *  initialGridSquare is the original position of the selected GridSquare.
     *  selectedGridSquare is the GridSquare that is being dragged around.
     */
    public static GridSquare[][] puzzleGrid;
    public static GridSquare initialGridSquare;
    public static GridSquare selectedGridSquare;

    /**
     *  gameStarted is needed to draw on the canvas properly. Without it, puzzleGrid is not appropriately initialized.
     */
    public static boolean gameStarted = false;

    /**
     * Generic constructor.
     */
    public PuzzleSurface(Context context) {
        super(context);
        init();
    }

    /**
     *  Generic constructor.
     */
    public PuzzleSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     *  Invokes initializeGridArrays, shuffleArray, fixPuzzleGridData, invalidate.
     *  Resets initialGridSquare to null so that the next selected GridSquare does not conflict.
     */
    public void init() {
        initializeGridArrays();
        shuffleArray();
        fixPuzzleGridData();
        invalidate();
        initialGridSquare = null;
    }

    /**
     *  Assigns GridSquare objects to each index in the puzzleGrid arrays.
     *  Assigns a number to GridSquare object.
     */
    public void initializeGridArrays() {
        int gridSquareNumber = 0;
        puzzleGrid = new GridSquare[currentSize][currentSize];
        for (int i = 0; i < currentSize; i++) {
            for (int j = 0; j < currentSize; j++) {
                GridSquare gridSquare = new GridSquare();
                gridSquare.number = ++gridSquareNumber;
                if (gridSquare.number == currentSize * currentSize) {
                    gridSquare.number = 0;
                }
                puzzleGrid[i][j] = gridSquare;
            }
        }
    }

    /**
     *  Iterates through puzzleGrid and determines the bounds and column and row of each GridSquare.
     *  Resets initialGridSquare and selectedGridSquare to prepare for next GridSquare drags.
     */
    public void fixPuzzleGridData() {
        float min = Math.min(gridHeight, gridWidth);
        float pixelsPerSquare = min / (currentSize + 2);

        for (int col = 0; col < currentSize; col++) {
            for (int row = 0; row < currentSize; row++) {
                GridSquare gridSquare = puzzleGrid[col][row];
                float left = (row + 1) * pixelsPerSquare + 5 - currentSize;
                float top = (col + 1) * pixelsPerSquare + 5 - currentSize;
                float right = (row + 2) * pixelsPerSquare - 5 - currentSize;
                float bottom = (col + 2) * pixelsPerSquare - 5 - currentSize;

                RectF rectF = new RectF(left, top, right, bottom);
                gridSquare.rectF = rectF;
                gridSquare.col = col;
                gridSquare.row = row;
            }
        }

        initialGridSquare = null;
        selectedGridSquare = null;
        printGrid();
        Log.e("check", Boolean.toString(checkPuzzleGrid()));
    }

    /**
     *  Checks if puzzleGrid has the correct solution and then returns true. Returns false otherwise.
     */
    public boolean checkPuzzleGrid() {
        int currentNumber = 1;
        for (int i = 0; i < currentSize; i++) {
            for (int j = 0; j < currentSize; j++) {
                if (currentNumber == currentSize * currentSize) {
                    currentNumber = 0;
                }
                Log.e("", currentNumber + "");
                if (puzzleGrid[i][j].number != currentNumber++) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *  Randomizes puzzleGrid.
     */
    public void shuffleArray() {
        Random rnd = ThreadLocalRandom.current();

        for (int i = 0; i < currentSize; i++) {
            int index = rnd.nextInt(currentSize);
            GridSquare[] gridSquareArray = puzzleGrid[index];
            puzzleGrid[index] = puzzleGrid[i];
            puzzleGrid[i] = gridSquareArray;
        }

        for (int i = 0; i < currentSize; i++) {
            for (int j = 0; j < currentSize; j++) {
                int index = rnd.nextInt(currentSize);
                int gridSquare = puzzleGrid[i][index].number;
                puzzleGrid[i][index].number = puzzleGrid[i][j].number;
                puzzleGrid[i][j].number = gridSquare;
                puzzleGrid[i][j].col = i;
                puzzleGrid[i][j].row = j;
            }
        }
    }

    /**
     *  Prints puzzleGrid to the console.
     */
    public void printGrid() {
        Integer[][] newArray = new Integer[currentSize][currentSize];
        for (int i = 0; i < currentSize; i++) {
            for (int j = 0; j < currentSize; j++) {
                newArray[i][j] = puzzleGrid[i][j].number;
            }
        }

        for (int i = 0; i < currentSize; i++) {
            Log.e("puzzleGrid", Arrays.deepToString(newArray[i]));
        }
        Log.e("", "=======================");
    }

    /**
     *  Draws each GridSquare in puzzleGrid and their number.
     *  Draws a green border around the screen if the grid has the correct solution.
     */
    public void drawGridSquares(Canvas c) {
        float min = Math.min(gridHeight, gridWidth);
        float pixelsPerSquare = min / (currentSize + 2);

        _pixelsPerSquare = pixelsPerSquare;

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(Color.WHITE);

        Paint strokePaint = new Paint();
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(Color.BLACK);
        strokePaint.setStrokeWidth(5);

        Paint textStrokePaint = new Paint();
        textStrokePaint.setColor(Color.BLACK);
        textStrokePaint.setTextAlign(Paint.Align.CENTER);

        for (int col = 0; col < currentSize; col++) {
            for (int row = 0; row < currentSize; row++) {
                int num = puzzleGrid[col][row].number;
                RectF rectF = puzzleGrid[col][row].rectF;
                textStrokePaint.setTextSize(rectF.width() / 2);

                if (num == 0) continue;

                c.drawRoundRect(rectF, 10, 10, fillPaint);
                c.drawRoundRect(rectF, 10, 10, strokePaint);
                if (num < 10) {
                    c.drawText(String.valueOf(num),
                            rectF.centerX(),
                            rectF.centerY() + rectF.height() / 5,
                            textStrokePaint);
                } else {
                    c.drawText(String.valueOf(num),
                            rectF.centerX(),
                            rectF.centerY() + rectF.height() / 5,
                            textStrokePaint);
                }
            }
        }

        if (checkPuzzleGrid()) {
            strokePaint.setColor(Color.GREEN);
            strokePaint.setStrokeWidth(50);
            RectF greenAccent = new RectF(0, 0, c.getWidth(), c.getHeight());
            c.drawRect(greenAccent, strokePaint);
        }
    }

    /**
     *  Assigns the height and width of the canvas to gridHeight and gridWidth respectively.
     *  Assigns canvas object to gridCanvas.
     *  Controls startup of the game by invoking init().
     *  Invokes drawGridSquares to draw the puzzle.
     */
    public final void onDraw(Canvas canvas) {
        gridHeight = canvas.getHeight();
        gridWidth = canvas.getWidth();
        gridCanvas = canvas;

        if (gameStarted == false) {
            init();
            gameStarted = true;
        }

        drawGridSquares(canvas);
    }

    /**
     *  Listens for mouse input. Controls dragging logic of the selected square.
     */
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (selectedGridSquare == null) {
            selectedGridSquare = getGridSquareFromCoordinates(x, y);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (selectedGridSquare == null) return true;
                if (initialGridSquare != null) break;
                initialGridSquare = new GridSquare();
                initialGridSquare.number = selectedGridSquare.number;
                initialGridSquare.rectF = new RectF(selectedGridSquare.rectF);
                break;
            case MotionEvent.ACTION_MOVE:
                if (selectedGridSquare == null) return true;
                dragGridSquare(selectedGridSquare, x, y);
                break;
            case MotionEvent.ACTION_UP:
                if (selectedGridSquare == null || initialGridSquare == null) return true;
                float squareSizeThreshold = selectedGridSquare.rectF.width() / 2;
                float dy = Math.abs(selectedGridSquare.rectF.centerY() - initialGridSquare.rectF.centerY());
                float dx = Math.abs(selectedGridSquare.rectF.centerX() - initialGridSquare.rectF.centerX());
                if (dy >= squareSizeThreshold || dx >= squareSizeThreshold) {
                    GridSquare emptyGridSquare = getEmptyGridSquare();
                    GridSquare temp = puzzleGrid[selectedGridSquare.col][selectedGridSquare.row];
                    puzzleGrid[selectedGridSquare.col][selectedGridSquare.row] = emptyGridSquare;
                    puzzleGrid[emptyGridSquare.col][emptyGridSquare.row] = temp;
                    fixPuzzleGridData();
                    invalidate();
                    break;
                }
                fixPuzzleGridData();
                invalidate();
                break;
        }
        return true;
    }

    /**
     *  Gets GridSquare from cursor coordinates.
     * @param x x position of cursor
     * @param y y position of cursor
     */
    public GridSquare getGridSquareFromCoordinates(float x, float y) {
        for (int col = 0; col < currentSize; col++) {
            for (int row = 0; row < currentSize; row++) {
                GridSquare gridSquare = puzzleGrid[col][row];
                RectF rectF = gridSquare.rectF;
                float left = rectF.left;
                float right = rectF.right;
                float top = rectF.top;
                float bottom = rectF.bottom;

                if (left < x && x < right && top < y && y < bottom && checkGridSquareAdjacentEmpty(col, row) != null) {
                    gridSquare.dragDirection = checkGridSquareAdjacentEmpty(col, row);
                    return gridSquare;
                }
            }
        }

        return null;
    }

    /**
     *  Looks through puzzleGrid and returns the empty grid square.
     */
    public GridSquare getEmptyGridSquare() {
        for (int col = 0; col < currentSize; col++) {
            for (int row = 0; row < currentSize; row++) {
                if (puzzleGrid[col][row].number == 0) {
                    return puzzleGrid[col][row];
                }
            }
        }

        return null;
    }

    /**
     *  Checks if the selectedGridSquare has an adjacent empty square.
     *  Returns the direction of the empty grid square relative to the selected
     *  grid square.
     *
     * @param col the column of the GridSquare
     * @param row the row of the GridSquare
     */
    public DragDirection checkGridSquareAdjacentEmpty(int col, int row) {
        boolean up = col - 1 >= 0 && puzzleGrid[col - 1][row].number == 0;
        boolean right = row + 1 < currentSize && puzzleGrid[col][row + 1].number == 0;
        boolean down = col + 1 < currentSize && puzzleGrid[col + 1][row].number == 0;
        boolean left = row - 1 >= 0 && puzzleGrid[col][row - 1].number == 0;

        if (up) {
            return DragDirection.UP;
        } else if (down) {
            return DragDirection.DOWN;
        } else if (left) {
            return DragDirection.LEFT;
        } else if (right) {
            return DragDirection.RIGHT;
        }

        return null;
    }

    /**
     *  Draws the real time position of the selected square that is being dragged
     *  in relation to the cursor.
     *
     * @param selectedGridSquare the selected grid square adjacent to the empty grid square
     * @param x x position of cursor
     * @param y y position of cursor
     */
    public void dragGridSquare(GridSquare selectedGridSquare, float x, float y) {
        GridSquare emptyGridSquare = getEmptyGridSquare();
        float gridSquareSize = emptyGridSquare.rectF.right - emptyGridSquare.rectF.left;
        float newY = y - gridSquareSize / 2;
        float newX = x - gridSquareSize / 2;
        if (selectedGridSquare == null || initialGridSquare == null) return;
        switch (selectedGridSquare.dragDirection) {
            case UP:
                RectF newRectF = new RectF(selectedGridSquare.rectF);
                newRectF.offsetTo(selectedGridSquare.rectF.left, newY);
                if (initialGridSquare.rectF.bottom >= newRectF.bottom
                        && emptyGridSquare.rectF.top <= newRectF.top) {
                    selectedGridSquare.rectF.offsetTo(selectedGridSquare.rectF.left, newY);
                }
                break;
            case DOWN:
                newRectF = new RectF(selectedGridSquare.rectF);
                newRectF.offsetTo(selectedGridSquare.rectF.left, newY);
                if (initialGridSquare.rectF.top <= newRectF.top
                        && emptyGridSquare.rectF.bottom >= newRectF.bottom) {
                    selectedGridSquare.rectF.offsetTo(selectedGridSquare.rectF.left, newY);
                }
                break;
            case LEFT:
                newRectF = new RectF(selectedGridSquare.rectF);
                newRectF.offsetTo(newX, selectedGridSquare.rectF.top);
                if (initialGridSquare.rectF.right >= newRectF.right
                        && emptyGridSquare.rectF.left <= newRectF.left) {
                    selectedGridSquare.rectF.offsetTo(newX, selectedGridSquare.rectF.top);
                }
                break;
            case RIGHT:
                newRectF = new RectF(selectedGridSquare.rectF);
                newRectF.offsetTo(newX, selectedGridSquare.rectF.top);
                if (initialGridSquare.rectF.left <= newRectF.left
                        && emptyGridSquare.rectF.right >= newRectF.right) {
                    selectedGridSquare.rectF.offsetTo(newX, selectedGridSquare.rectF.top);
                }
                break;
        }
        invalidate();
    }

    /**
     *  Sets the side length of the grid.
     *
     * @param size the side length of the grid
     */
    public void setCurrentSize(int size) {
        currentSize = size + 4;
        init();
    }
}
