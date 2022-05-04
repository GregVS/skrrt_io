package game;

import buffers.InputPacketBuffer;
import entities.Player;

import java.util.HashMap;

/**
 * Created by Gregory on 6/9/17.
 */
public class InputPacket {

    public static final int LEFT_SHIFT = -1;
    public static final int RIGHT_SHIFT = 1;
    public static final int NO_SHIFT = 0;

    private int laneChange;
    private boolean slow;
    private Player player;

    private static HashMap<Integer, int[]> turnMap = new HashMap<>();

    public InputPacket(Player player) {
        this.player = player;
        this.laneChange = 0;
        this.slow = false;

        turnMap.put(0, new int[] {1, 3});
        turnMap.put(90, new int[] { 2, 0 });
        turnMap.put(180, new int[] { 3, 1 });
        turnMap.put(270, new int[] { 0, 2 });
    }

    public void setLaneChange(int laneChange) {
        this.laneChange = laneChange;
    }

    public void setSlow(boolean slow) {
        this.slow = slow;
    }

    public int getLaneChange() {
        return laneChange;
    }

    public boolean isSlow() {
        return this.slow;
    }

    public void reset() {
        laneChange = NO_SHIFT;
    }

    public void set(int laneChange, boolean slow) {
        this.laneChange = laneChange;
        this.slow = slow;
    }

    public void load(InputPacketBuffer buf) {
        int dir = buf.laneChange();
//        int[] relative = turnMap.get((int) player.getRotation());
//        if(relative[0] == dir) laneChange = LEFT_SHIFT;
//        else if(relative[1] == dir) laneChange = RIGHT_SHIFT;
//        else laneChange = NO_SHIFT;
        laneChange = buf.laneChange();
        slow = buf.slow();
    }
}
