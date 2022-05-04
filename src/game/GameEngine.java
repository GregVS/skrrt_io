package game;

import buffers.InputPacketBuffer;
import com.dictiography.collections.IndexedTreeSet;
import entities.*;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by Gregory on 6/9/17.
 */
public class GameEngine {

    private HashMap<Integer, Entity> entityMap = new HashMap<>();
    private Queue<Entity> pendingEntityQueue = new LinkedList<>();
    private IndexedTreeSet<Integer> leaderboard = new IndexedTreeSet<>(new LeaderboardComparator(entityMap));

    private SpatialHash spatialHash = new SpatialHash();
    private HashSet<Integer> deadClients = new HashSet<>();
    private EntitySpawner entitySpawner = new EntitySpawner(spatialHash);

    //bots
    private ArrayList<Bot> bots = new ArrayList<>();

    private int maxBots = 15;
    private int botCount = maxBots;

    public void initBots(int n) {
        for(int i = 0; i < n; i++) {
            Player p = addPlayerEntity(StaticGlobals.IDCOUNTER.getAndIncrement(), Bot.names[(int) (Math.random() * Bot.names.length)]);
            bots.add(new Bot(p, spatialHash));
        }
    }

    public Player addPlayerEntity(int id, String playerName) {
        Player player = new Player(playerName, entitySpawner.getPlayerSpawnLocation(), id);
        pendingEntityQueue.add(player);
        return player;
    }

    public void spawnStaticEntities() {
        entitySpawner.spawnStaticEntities(pendingEntityQueue);
    }

    public void update() {
        resetForUpdate();

        Iterator<Map.Entry<Integer, Entity>> iterator = entityMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Entity entity = iterator.next().getValue();
            entity.update();
            if (!entity.isAlive()) {
                handleEntityDeath(entity);
                iterator.remove();
                continue;
            }
            if(entity.getType().equals(EntityType.PLAYER)) leaderboard.add(entity.getId());
            spatialHash.insertEntity(entity);
        }
        updateBots();
        Collision.handleAllCollisions(entityMap, spatialHash);
    }

    private void updateBots() {
        Iterator<Bot> i = bots.iterator();
        while(i.hasNext()) {
            Bot b = i.next();
            if(!b.getPlayer().isAlive()) {
                i.remove();
                continue;
            }
            b.update();
        }
        if(bots.size() < botCount) initBots(botCount - bots.size());
    }

    private void resetForUpdate() {
        addPendingEntities();
        spatialHash.reset();
        leaderboard.clear();
    }

    private void handleEntityDeath(Entity entity) {
        if(entity.getType().equals(EntityType.PLAYER)) {
            spawnWreckageForPlayer((Player) entity);
            deadClients.add(entity.getId());
        } else if (entity.getType().equals(EntityType.GASCAN)) {
            pendingEntityQueue.add(entitySpawner.createGasCan(false));
        } else if(entity.getType().equals(EntityType.REPAIRKIT)) {
            pendingEntityQueue.add(entitySpawner.createRepairKit(false));
        }
    }

    private void spawnWreckageForPlayer(Player player) {
        pendingEntityQueue.add(entitySpawner.createWreckageForPlayer(player));
    }

    private void addPendingEntities() {
        for(int i = 0; i < pendingEntityQueue.size(); i++) {
            Entity e = pendingEntityQueue.remove();
            if(e.getType().equals(EntityType.GASCAN) || e.getType().equals(EntityType.REPAIRKIT)) {
                Transform t = entitySpawner.getSafeSpawnLocation();
                t.setRotation(0);
                e.setTransform(t);
            }
            entityMap.put(e.getId(), e);
            spatialHash.insertEntity(e);
        }
    }

    public void removePlayerEntity(Integer id) {
        Player p = (Player) entityMap.remove(id);
        if(p != null) {
            p.getStats().die();
        }
    }

    public void attachToInputPacket(Integer id, InputPacketBuffer buf) {
        Player player = (Player) entityMap.get(id);
        if(player == null) return;
        player.getInputPacket().load(buf);
    }

    public ByteBuffer buildSnapshotForPlayer(Integer id) {
        return BufferBuilder.buildSnapshotForPlayer(entityMap, leaderboard, spatialHash, id);
    }

    public HashSet<Integer> getDeadClients() {
        return deadClients;
    }

    public Entity getEntityById(int id) {
        return entityMap.get(id);
    }

    public void updateBotCount(int playerCount) {
        botCount = maxBots - playerCount;
        if(botCount < 0) botCount = 0;
    }

    public void logBotCount() {
        System.out.println(bots.size());
    }

    public void logBots() {
        for(int i = 0; i < bots.size(); i++) {
            System.out.println((i + 1) + ". " + bots.get(i).getPlayer().getName());
        }
    }

    public void clearBots() {
        for(Bot b : bots) {
            removePlayerEntity(b.getPlayer().getId());
        }
        bots.clear();
    }


    public void setMaxBots(int maxBots, int playerCount) {
        this.maxBots = maxBots;
        updateBotCount(playerCount);
    }

    public int getBotCount() {
        return botCount;
    }

    public int getMaxBots() {
        return maxBots;
    }
}
