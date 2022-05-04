package game;

import entities.Entity;
import entities.Player;

import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Gregory on 7/4/17.
 */
public class LeaderboardComparator implements Comparator<Integer> {

    private HashMap<Integer, Entity> entityMap;

    public LeaderboardComparator(HashMap<Integer, Entity> entityMap) {
        this.entityMap = entityMap;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        Player e1, e2;
        try{
            e1 = (Player) entityMap.get(o1);
            e2 = (Player) entityMap.get(o2);
        } catch (ClassCastException e) {
            e.printStackTrace();
            return 1; //could not cast
        }
        if(e1 == null) return 1;
        else if(e2 == null) return -1;
        if(e1.getStats().getXp() > e2.getStats().getXp()) {
            return -1;
        } else if(e1.getStats().getXp() < e2.getStats().getXp()) {
            return 1;
        }
        return 0;
    }
}
