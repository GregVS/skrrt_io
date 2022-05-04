package entities;

/**
 * Created by Gregory on 6/9/17.
 */
public class Vector2 {

    private float x = 0, y = 0;

    public Vector2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float length() {
        return (float) Math.sqrt((x * x) + (y * y));
    }

    public Vector2 normalizeFromAngle(float degrees) {
        return set(Maths.dcos(degrees), Maths.dsin(degrees)).normalize();
    }

    public Vector2 normalize() {
        float l = length();
        return set(x / l, y / l);
    }

    public Vector2 multiply(float a) {
        return set(x * a, y * a);
    }

    public void reset() {
        this.x = 0;
        this.y = 0;
    }
}
