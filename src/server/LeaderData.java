package server;

/**
 * Created by Gregory on 7/14/17.
 */
public class LeaderData {

    public String name;
    public int score;

    public LeaderData(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + " : " + score;
    }
}
