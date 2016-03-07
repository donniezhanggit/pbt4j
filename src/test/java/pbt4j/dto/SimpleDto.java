package pbt4j.dto;

import java.util.List;

/**
 * @author OZY on 2016.03.07.
 */
public class SimpleDto {

    public final String name;
    public final int total;
    public final List<String> neighbours;

    public SimpleDto(String name, int total, List<String> neighbours) {
        this.name = name;
        this.total = total;
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        return "SimpleDto{" +
                "name='" + name + '\'' +
                ", total=" + total +
                ", neighbours=" + neighbours +
                '}';
    }
}
