package pbt4j.dto;

/**
 * @author Linas on 2016.03.06.
 */
public class Bean {

    public static final String STATIC_FIELD = "some data";

    private String name;
    private int age;
    public final String finalValue = "test";

    public Bean() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
