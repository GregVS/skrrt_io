package entities;

import buffers.EntityUnion;
import buffers.GasCanBuffer;
import buffers.PositionBuffer;
import buffers.RepairKitBuffer;
import com.google.flatbuffers.FlatBufferBuilder;

import java.awt.*;

/**
 * Created by Gregory on 8/15/17.
 */
public class RepairKit extends Entity {

    private boolean used = false;
    private int repairAmount = (int) (PlayerStats.MAXHEALTH * 0.33f + (Math.random() * 10));

    public RepairKit(Transform transform, Integer id) {
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
    public void update() {}

    @Override
    public String getType() {
        return EntityType.REPAIRKIT;
    }

    @Override
    public byte serializedType() {
        return EntityUnion.RepairKitBuffer;
    }

    @Override
    public int serialize(FlatBufferBuilder builder) {
        RepairKitBuffer.startRepairKitBuffer(builder);
        RepairKitBuffer.addId(builder, getId());
        RepairKitBuffer.addPosition(builder, PositionBuffer.createPositionBuffer(builder, getX(), getY(), getRotation()));
        return RepairKitBuffer.endRepairKitBuffer(builder);
    }

    public int getRepairAmount() {
        return repairAmount;
    }
}
