package server;

/**
 * Created by Gregory on 6/9/17.
 */
public class GameTimeManager {

    private static float deltaSeconds = 0;
    private static long lastTime = System.currentTimeMillis();

    public static void update() {
        long currTime = System.currentTimeMillis();
        deltaSeconds = (currTime - lastTime) / 1000f;
        lastTime = currTime;
    }

    public static float getDeltaSeconds() {
        return deltaSeconds;
    }
}
