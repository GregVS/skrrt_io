package entities;

import game.RoadMap;
import game.SpatialHash;
import game.StaticGlobals;

import java.util.Queue;

/**
 * Created by Gregory on 6/20/17.
 */
public class EntitySpawner {

    private static final int[][] ROAD_SECTIONS = {{0, 1}, {1,0}, {1,2}, {2, 1}};
    private SpatialHash spatialHash;

    public EntitySpawner(SpatialHash spatialHash) {
        this.spatialHash = spatialHash;
    }

    public void spawnStaticEntities(Queue<Entity> pendingEntityQueue) {
        long lt = System.currentTimeMillis();
        for(int i = 0; i < RoadMap.NUM_CHUNKS * 3; i++) {
            Launchpad pad = new Launchpad(getSafeSpawnLocation(), StaticGlobals.IDCOUNTER.getAndIncrement());
            pendingEntityQueue.add(pad);
            spatialHash.insertEntity(pad);
        }
        for(int i = 0; i < RoadMap.NUM_CHUNKS * RoadMap.NUM_CHUNKS * 15; i++) {
            GasCan gasCan = createGasCan(true);
            pendingEntityQueue.add(gasCan);
            spatialHash.insertEntity(gasCan);
        }
        for(int i = 0; i < 10; i++) {
            RepairKit repairKit = createRepairKit(true);
            pendingEntityQueue.add(repairKit);
            spatialHash.insertEntity(repairKit);
        }
        System.out.println("Time taken to spawn: " + (System.currentTimeMillis() - lt));
    }

    public Transform getPlayerSpawnLocation() {
        int chunkRow = (int) (Math.random() * RoadMap.NUM_CHUNKS);
        int chunkCol = (chunkRow == 0 || chunkRow == RoadMap.NUM_CHUNKS - 1) ? (int) (Math.random() * RoadMap.NUM_CHUNKS) : (Math.random() > 0.5 ? 0 : RoadMap.NUM_CHUNKS - 1);
        if(chunkCol == 0) return calculatePosition(chunkRow, chunkCol, new int[] {1, 0}, Maths.randomBetween(RoadMap.NUM_ONEWAY_LANES, RoadMap.NUM_ONEWAY_LANES * 2 - 1));
        else if(chunkCol == RoadMap.NUM_CHUNKS - 1) return calculatePosition(chunkRow, chunkCol, new int[] {1, 2}, Maths.randomBetween(0, RoadMap.NUM_ONEWAY_LANES - 1));
        if(chunkRow == 0) return calculatePosition(chunkRow, chunkCol, new int[] {0, 1}, Maths.randomBetween(0, RoadMap.NUM_ONEWAY_LANES - 1));
        else return calculatePosition(chunkRow, chunkCol, new int[] {2, 1}, Maths.randomBetween(RoadMap.NUM_ONEWAY_LANES, RoadMap.NUM_ONEWAY_LANES * 2 - 1));
    }

    public RepairKit createRepairKit(boolean includeTransform) {
        Transform pos = includeTransform ? getSafeSpawnLocation() : new Transform(0,0, 0);
        pos.setRotation(0);
        return new RepairKit(pos, StaticGlobals.IDCOUNTER.getAndIncrement());
    }

    public GasCan createGasCan(boolean includeTransform) {
        Transform pos = includeTransform ? getSafeSpawnLocation() : new Transform(0,0, 0);
        pos.setRotation(0);
        return new GasCan(pos, StaticGlobals.IDCOUNTER.getAndIncrement());
    }

    public Wreckage createWreckageForPlayer(Player player) {
        int id = StaticGlobals.IDCOUNTER.getAndIncrement();
        Wreckage wreckage = new Wreckage(player, id);
        wreckage.startDecaying();
        return wreckage;
    }

    public Transform getSafeSpawnLocation() {
        Transform transform;
        byte counter = 0;
        do {
            transform = getRandomSpawnLocation();
            if(++counter > 10) {
//                System.err.println("Couldn't find location within 10 tries");
                break;
            }
        } while(!isSafeSpawn(transform));
        return transform;
    }

    private boolean isSafeSpawn(Transform transform) {
        for (Entity e : spatialHash.getEntitiesInSpatialRadius((int) transform.getX(), (int) transform.getY(), 30)) {
            if(Math.hypot(e.getX() - transform.getX(), e.getY() - transform.getY()) < 30) return false;
        }
        return true;
    }

    private Transform getRandomSpawnLocation() {
        int chunkRow = (int) (Math.random() * RoadMap.NUM_CHUNKS), chunkCol = (int) (Math.random() * RoadMap.NUM_CHUNKS);
        int[] section = findNonBorderSection(chunkRow, chunkCol);
        return calculatePosition(chunkRow, chunkCol, section, (int) (RoadMap.NUM_ONEWAY_LANES * 2 * Math.random()));
    }

    private int[] findNonBorderSection(int chunkRow, int chunkCol) {
        int[] pos;
        do {
            pos = ROAD_SECTIONS[(int) (Math.random() * ROAD_SECTIONS.length)];
        } while((chunkRow == 0 && pos[0] == 0) || (chunkRow == RoadMap.NUM_CHUNKS - 1 && pos[0] == 2) || (chunkCol == 0 && pos[1] == 0) || (chunkCol == RoadMap.NUM_CHUNKS - 1 && pos[1] == 2));
        return pos;
    }

    private Transform calculatePosition(int chunkRow, int chunkCol, int[] section, int lane) {
        float x = chunkCol * RoadMap.SECTION_SIZE * 3 + section[1] * RoadMap.SECTION_SIZE;
        float y = chunkRow * RoadMap.SECTION_SIZE * 3 + section[0] * RoadMap.SECTION_SIZE;

        int rotation = 0;
        if (section[1] == 0 || section[1] == 2) {
            y = RoadMap.lockToLane(lane, y);
            x = x + Maths.randomBetween(10, RoadMap.SECTION_SIZE - 10);
            if (lane >= RoadMap.NUM_ONEWAY_LANES) {
                rotation = 270;
            } else {
                rotation = 90;
            }
        } else {
            x = RoadMap.lockToLane(lane, x);
            y = y + Maths.randomBetween(10, RoadMap.SECTION_SIZE - 10);
            if (lane >= RoadMap.NUM_ONEWAY_LANES) {
                rotation = 0;
            } else {
                rotation = 180;
            }
        }
        return new Transform(x, y, rotation);
    }
}
