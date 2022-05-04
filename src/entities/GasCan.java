package entities;

import buffers.EntityUnion;
import buffers.GasCanBuffer;
import buffers.PositionBuffer;
import com.google.flatbuffers.FlatBufferBuilder;

import java.awt.*;

/**
 * Created by Gregory on 6/20/17.
 */
public class GasCan extends Entity {

    private int xp = (int) (10 + (Math.random() * 5));
    private boolean used = false;

    public GasCan(Transform transform, Integer id) {
        super(transform, new Dimension(28, 28), id);
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public boolean isAlive() {
        return !used;
    }

    @Override
    public void update() {

    }

    public boolean isUsed() {
        return used;
    }

    public int getXp() {
        return xp;
    }

    @Override
    public String getType() {
        return EntityType.GASCAN;
    }

    @Override
    public byte serializedType() {
        return EntityUnion.GasCanBuffer;
    }

    @Override
    public int serialize(FlatBufferBuilder builder) {
        GasCanBuffer.startGasCanBuffer(builder);
        GasCanBuffer.addId(builder, getId());
        GasCanBuffer.addPosition(builder, PositionBuffer.createPositionBuffer(builder, getX(), getY(), getRotation()));
        return GasCanBuffer.endGasCanBuffer(builder);
    }
}
