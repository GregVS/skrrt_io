package server;

/**
 * Created by Gregory on 8/10/17.
 */
public class TickTimer {

    private long lastTime = System.currentTimeMillis();
    private int millis;

    public TickTimer(int millis) {
        this.millis = millis;
    }

    public boolean shouldUpdate() {
        long currTime = System.currentTimeMillis();
        if(currTime - lastTime >= millis) {
            lastTime = currTime;
            return true;
        }
        return false;
    }

}
