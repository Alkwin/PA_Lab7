import java.util.ArrayList;

public class Player {
    public ArrayList<Integer> sequence;
    public String name;
    public Integer number;

    public Player(String name, ArrayList<Integer> sequence, Integer number) {
        this.sequence = sequence;
        this.name = name;
        this.number = number;
    }

    @Override
    public String toString() {
        return "Player{" +
                "sequence=" + sequence +
                ", name='" + name + '\'' +
                ", number=" + number +
                '}';
    }
}
