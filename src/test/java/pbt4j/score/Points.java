package pbt4j.score;

import pbt4j.Point;

import java.util.Objects;

/**
 * @author OZY on 2016.08.29.
 */
public final class Points implements Score {

    final Point playerOnePoint;
    final Point playerTwoPoint;

    Points(Point playerOnePoint, Point playerTwoPoint) {
        this.playerOnePoint = playerOnePoint;
        this.playerTwoPoint = playerTwoPoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Points points = (Points) o;
        return playerOnePoint == points.playerOnePoint &&
                playerTwoPoint == points.playerTwoPoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerOnePoint, playerTwoPoint);
    }

    @Override
    public String toString() {
        return "Points{" +
                "playerOnePoint=" + playerOnePoint +
                ", playerTwoPoint=" + playerTwoPoint +
                '}';
    }
}
