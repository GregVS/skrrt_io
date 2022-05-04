package entities;

import buffers.EntityUnion;
import buffers.LaunchpadBuffer;
import buffers.PositionBuffer;
import com.google.flatbuffers.FlatBufferBuilder;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gregory on 6/23/17.
 */
public class Launchpad extends Entity {

    public Launchpad(Transform transform, Integer id) {
        super(transform, new Dimension(32, 48), id);
    }

    public void launchPlayer(Player player) {
        if(player.getStats().isBoostProtection()) return;
        player.getStats().setSpeed(player.getStats().getSpeed() * 3);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() { player.getStats().resetToNormalSpeed(); }
        }, 400); //reset speed after x millis
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void update() {
    }

    @Override
    public String getType() {
        return EntityType.LAUNCHPAD;
    }

    @Override
    public byte serializedType() {
        return EntityUnion.LaunchpadBuffer;
    }

    @Override
    public int serialize(FlatBufferBuilder builder) {
        LaunchpadBuffer.startLaunchpadBuffer(builder);
        LaunchpadBuffer.addId(builder, getId());
        LaunchpadBuffer.addPosition(builder, PositionBuffer.createPositionBuffer(builder, getX(), getY(), getRotation()));
        return LaunchpadBuffer.endLaunchpadBuffer(builder);
    }
}
