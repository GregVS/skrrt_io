package game;

import buffers.*;
import com.dictiography.collections.IndexedTreeSet;
import com.google.flatbuffers.FlatBufferBuilder;
import entities.Entity;
import entities.Player;
import server.ClientProfile;

import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by Gregory on 6/20/17.
 */
public class BufferBuilder {

    public static ByteBuffer createDeathBuffer(ClientProfile profile) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        DeathBuffer.startDeathBuffer(builder);
        DeathBuffer.addScore(builder, profile.getPlayer().getStats().getXp());
        DeathBuffer.addLevel(builder, profile.getPlayer().getStats().getLevel());
        builder.finish(MessageBuffer.createMessageBuffer(builder, MessageUnion.DeathBuffer, DeathBuffer.endDeathBuffer(builder)));
        return builder.dataBuffer();
    }

    public static ByteBuffer buildSnapshotForPlayer(HashMap<Integer, Entity> entityMap, IndexedTreeSet<Integer> leaderboard, SpatialHash spatialHash, Integer id) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        int[] entityOffsets = new int[entityMap.size()];
        int counter = 0;

        Player playerEntity = (Player) entityMap.get(id);
        if (playerEntity == null) {
            return null;
        }
        if(!leaderboard.contains(id)) return null; //this tends to cause some problems

        for (Entity entity : spatialHash.getEntitiesInSpatialRadius((int) playerEntity.getX(), (int) playerEntity.getY(), 2000)) {
            entityOffsets[counter] = EntityBuffer.createEntityBuffer(builder, entity.serializedType(), entity.serialize(builder));
            counter++;
        }

        int info = PlayerInfo.createPlayerInfo(builder, builder.createString(playerEntity.getName()), (short) (1 + leaderboard.entryIndex(playerEntity.getId())), playerEntity.getStats().getXp());
        int entities = SnapshotBuffer.createEntitiesVector(builder, entityOffsets);
        int lbBuf = createLeaderboardBuffer(builder, entityMap, leaderboard);
        int myplayer = playerEntity.serialize(builder);

        SnapshotBuffer.startSnapshotBuffer(builder);
        SnapshotBuffer.addEntities(builder, entities);
        SnapshotBuffer.addPlayer(builder,myplayer);
        SnapshotBuffer.addLeaderboard(builder, lbBuf);
        SnapshotBuffer.addMyInfo(builder, info);
        SnapshotBuffer.addGasLevel(builder, playerEntity.getStats().getGasLevel());
        SnapshotBuffer.addServerTimeMs(builder, System.currentTimeMillis());
        int snapshotbuf = SnapshotBuffer.endSnapshotBuffer(builder);

        builder.finish(MessageBuffer.createMessageBuffer(builder, MessageUnion.SnapshotBuffer, snapshotbuf));
        return builder.dataBuffer();
    }

    private static int createLeaderboardBuffer(FlatBufferBuilder builder, HashMap<Integer, Entity> entityMap, IndexedTreeSet<Integer> leaderboard) {
        int[] lboard = new int[10];
        for (int i = 0; i < 10; i++) {
            String name = "";
            int score = 0;
            if (leaderboard.size() > i) {
                Player player = (Player) entityMap.get(leaderboard.exact(i));
                if(player != null) {
                    name = player.getName();
                    score = player.getStats().getXp();
                }
            }
            lboard[i] = PlayerInfo.createPlayerInfo(builder, builder.createString(name), (short) (i + 1), score);
        }
        return SnapshotBuffer.createLeaderboardVector(builder, lboard);
    }

    public static ByteBuffer createServerDataByteBuffer(int playerCount) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1024);
        int buf = MessageBuffer.createMessageBuffer(builder, MessageUnion.ServerDataBuffer, ServerDataBuffer.createServerDataBuffer(builder, playerCount));
        builder.finish(buf);
        return builder.dataBuffer();
    }

    public static ByteBuffer createInfoMessage(String msg) {
        FlatBufferBuilder builder = new FlatBufferBuilder(128);
        int msgBuf = builder.createString(msg);
        int buf = MessageBuffer.createMessageBuffer(builder, MessageUnion.InfoBuffer, InfoBuffer.createInfoBuffer(builder, msgBuf));
        builder.finish(buf);
        return builder.dataBuffer();
    }

}
