package entities;

import game.RoadMap;

/**
 * Created by Gregory on 6/9/17.
 */
public class Transform {

    private float x, y;
    private int rotation;

    public Transform(float x, float y, int rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getRotation() {
        return rotation;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation % 360;
    }

    @Override
    public String toString() {
        return x / (RoadMap.SECTION_SIZE * 3) + ", " + y  / (RoadMap.SECTION_SIZE * 3)+ ", " + rotation;
    }
}
