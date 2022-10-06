package com.example.puzzle15squares;

import android.graphics.RectF;

public class GridSquare {
    public RectF rectF;
    public int number;
    public DragDirection dragDirection;
    public int col;
    public int row;

    public GridSquare() {}

    public GridSquare(RectF r, int n, DragDirection d) {
        rectF = r;
        number = n;
        dragDirection = d;
    }
}
