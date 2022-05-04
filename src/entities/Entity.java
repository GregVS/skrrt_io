package entities;

import com.google.flatbuffers.FlatBufferBuilder;

import java.awt.*;

/**
 * Created by Gregory on 6/9/17.
 */
public abstract class Entity {

    protected Transform transform;
    private Dimension size;
    private Integer id;

    public Entity(Transform transform, Dimension size, Integer id) {
        this.transform = transform;
        this.size = size;
        this.id = id;
    }

    public Rectangle createBoundingRectangle() {
        if (transform.getRotation() == Rotation.UP || transform.getRotation() == Rotation.DOWN) {
            return new Rectangle((int) (transform.getX() - size.getWidth() / 2), (int) (transform.getY() - size.getHeight() / 2), size.width, size.height);
        }
        //entity is sideways, return sideways rectangle
        return new Rectangle((int) (transform.getX() - size.getHeight() / 2), (int) (transform.getY() - size.getWidth() / 2), size.height, size.width);
    }

    public abstract boolean isAlive();

    public abstract void update();

    public abstract String getType();

    public Transform getTransform() {
        return transform;
    }

    public float getX() {
        return transform.getX();
    }

    public float getY() {
        return transform.getY();
    }

    public float getRotation() {
        return transform.getRotation();
    }

    public Integer getId() {
        return id;
    }

    public void setTransform(Transform t) {
        this.transform = t;
    }

    public abstract byte serializedType();

    public abstract int serialize(FlatBufferBuilder builder);

    public int distanceTo(Entity entity) {
        return (int) Math.hypot(entity.getX() - this.getX(), entity.getY() - this.getY());
    }

}
