package com.example.tripplaner.service.Callbacks.DistanceMatrix;

import java.util.Objects;

public class IndexesPair {
    private final int x;
    private final int y;
    private int hashCode;

    public IndexesPair(int x, int y) {
        this.x = x;
        this.y = y;
        this.hashCode = Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        IndexesPair that = (IndexesPair) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }
}
