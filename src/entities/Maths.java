package entities;

/**
 * Created by Gregory on 6/28/17.
 */
public class Maths {

    public static int clamp(int c, int min, int max) {
        return Math.max(min, Math.min(c, max));
    }

    public static float clamp(float c, float min, float max) {
        return Math.max(min, Math.min(c, max));
    }

    public static float dcos(float degrees) {
        return (float) Math.cos(Math.toRadians(degrees));
    }

    public static float dsin(float degrees) {
        return (float) Math.sin(Math.toRadians(degrees));
    }

    public static int randomBetween(int min, int max) {
        return (int)((Math.random() * (max+1-min)) + min);
    }


}
