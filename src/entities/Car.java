package entities;

import game.InputPacket;

/**
 * Created by Gregory on 8/19/17.
 */
public class Car {

    private Vector2 forwardVector, rightVector;
    private Transform transform;

    public Car(Transform transform) {
        this.transform = transform;
        this.forwardVector = new Vector2().normalizeFromAngle(transform.getRotation());
    }

    public void move(InputPacket inputPacket) {

    }
}
