package com.movielix.util;

public class Tuple<X, Y> {

    private final X mX;
    private final Y mY;

    public Tuple(X x, Y y) {
        this.mX = x;
        this.mY = y;
    }

    public X x() { return mX; }
    public Y y() { return mY; }
}
