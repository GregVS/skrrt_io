package entities;

import buffers.EntityUnion;
import buffers.PlayerBuffer;
import buffers.PositionBuffer;
import buffers.StatsBuffer;
import com.google.flatbuffers.FlatBufferBuilder;
import game.InputPacket;
import server.GameTimeManager;

import java.awt.*;
import java.util.HashMap;

/**
 * Created by Gregory on 6/9/17.
 */
public class Player extends Entity {

    private InputPacket inputPacket = new InputPacket(this);
    private Vector2 velocity = new Vector2();
    private PlayerStats stats = new PlayerStats(this);
    private String name;

    private Car car;

    public Player(String name, Transform transform, Integer id) {
        super(transform, new Dimension(32, 48), id);
        this.name = name;
        this.car = new Car(transform);
        stats.activeSpawnProtection();
    }

    @Override
    public boolean isAlive() {
        return stats.getHealth() > 0;
    }

    @Override
    public void update() {
        // update the players movement here
        car.move(inputPacket);
        updatePosition();
        inputPacket.reset();
        stats.updateGas();
    }

    private void updatePosition() {
        velocity.normalizeFromAngle(transform.getRotation()).multiply(stats.getSpeed());
        transform.setX(transform.getX() + velocity.getX() * GameTimeManager.getDeltaSeconds());
        transform.setY(transform.getY() + velocity.getY() * GameTimeManager.getDeltaSeconds());
    }

    public InputPacket getInputPacket() {
        return inputPacket;
    }

    @Override
    public String getType() {
        return EntityType.PLAYER;
    }

    @Override
    public byte serializedType() {
        return EntityUnion.PlayerBuffer;
    }

    @Override
    public int serialize(FlatBufferBuilder builder) {
        int nameOffset = builder.createString(name);
        PlayerBuffer.startPlayerBuffer(builder);
        PlayerBuffer.addPosition(builder, PositionBuffer.createPositionBuffer(builder, getX(), getY(), getRotation()));
        int statsBuf = StatsBuffer.createStatsBuffer(builder, stats.getXp(), stats.getLevel(), stats.getHealth(), stats.hasHurtFlag(), stats.isSpawnProtected());
        PlayerBuffer.addStats(builder, statsBuf);
        PlayerBuffer.addId(builder, getId());
        PlayerBuffer.addName(builder, nameOffset);
        return PlayerBuffer.endPlayerBuffer(builder);
    }

    public void kill() {
        stats.die();
    }

    public PlayerStats getStats() {
        return stats;
    }

    public String getName() {
        return name;
    }

}
