package entities;

import buffers.EntityUnion;
import buffers.PositionBuffer;
import buffers.WreckageBuffer;
import com.google.flatbuffers.FlatBufferBuilder;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gregory on 6/21/17.
 */
public class Wreckage extends Entity {

    private boolean alive = true;
    private Player attachedPlayer;
    private static Timer timer = new Timer();

    public Wreckage(Player attachedPlayer, Integer id) {
        super(attachedPlayer.getTransform(), new Dimension(32, 48), id);
        this.attachedPlayer = attachedPlayer;
    }

    public void startDecaying() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() { alive = false; }
        }, 5000 * attachedPlayer.getStats().getLevel()); //kill after x millis
    }

    public void hitPlayer(Player player) {
        if(!alive) return;
        player.getStats().takeDamage(attachedPlayer.getStats().getDamage());
        player.getStats().setSpeed((int) (player.getStats().getSpeed() * 0.4));
        timer.schedule(new TimerTask() {
            @Override
            public void run() { player.getStats().resetToNormalSpeed(); }
        }, 2000); //reset speed after x millis
        alive = false;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    @Override
    public void update() {
    }

    @Override
    public String getType() {
        return EntityType.WRECKAGE;
    }

    @Override
    public byte serializedType() {
        return EntityUnion.WreckageBuffer;
    }

    @Override
    public int serialize(FlatBufferBuilder builder) {
        WreckageBuffer.startWreckageBuffer(builder);
        WreckageBuffer.addId(builder, getId());
        WreckageBuffer.addPosition(builder, PositionBuffer.createPositionBuffer(builder, getX(), getY(), getRotation()));
        return WreckageBuffer.endWreckageBuffer(builder);
    }
}
