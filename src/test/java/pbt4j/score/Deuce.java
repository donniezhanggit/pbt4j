package pbt4j.score;

/**
 * @author OZY on 2016.08.29.
 */
public final class Deuce implements Score {

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass()) && o.getClass().equals(getClass());
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "Deuce";
    }
}
