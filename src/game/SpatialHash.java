package game;

import entities.Entity;
import entities.EntityType;
import entities.GasCan;
import entities.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gregory on 6/14/17.
 */
public class SpatialHash {

    private static final int CELLSIZE = RoadMap.SECTION_SIZE;
    private HashMap<GridHash, ArrayList<Entity>> spatialMap = new HashMap<>();

    public void insertEntity(Entity entity) {
        ArrayList<Entity> arr = spatialMap.get(hashFunction(entity));
        if (arr == null) {
            arr = new ArrayList<>();
            arr.add(entity);
            spatialMap.put(hashFunction(entity), arr);
        } else {
            arr.add(entity);
        }
    }

    public void reset() {
        spatialMap.clear();
    }

    public ArrayList<Entity> getEntitiesInSpatialRadius(int x, int y, int radius) {
        ArrayList<Entity> nearbyEntities = new ArrayList<>();
        for (java.util.Map.Entry<GridHash, ArrayList<Entity>> entry : spatialMap.entrySet()) {
            GridHash h = entry.getKey();
            if (circleTouchesRect(x, y, radius, h.x * CELLSIZE, h.y * CELLSIZE, (h.x + 1) * CELLSIZE, (h.y + 1) * CELLSIZE)) {
                nearbyEntities.addAll(entry.getValue());
            }
        }
        return nearbyEntities;
    }

    private boolean circleTouchesRect(int cx, int cy, int radius, int left, int top, int right, int bottom) {
        int closestX = (cx < left ? left : (cx > right ? right : cx));
        int closestY = (cy < top ? top : (cy > bottom ? bottom : cy));
        int dx = closestX - cx;
        int dy = closestY - cy;

        return (dx * dx + dy * dy) <= radius * radius;
    }

    private GridHash hashFunction(Entity entity) {
        return new GridHash((int) (entity.getX() / CELLSIZE), (int) (entity.getY() / CELLSIZE));
    }

    public Player getClosestBotTarget(Player player) {
        Player nearestPlayer = null;
        int nearestDist = Integer.MAX_VALUE;
        for (java.util.Map.Entry<GridHash, ArrayList<Entity>> entry : spatialMap.entrySet()) {
            for (Entity e : entry.getValue()) {
                if (e.getType().equals(EntityType.PLAYER)) {
                    Player p = (Player) e;
                    int dist = p.distanceTo(player);
                    if (!p.getId().equals(player.getId()) && p.getStats().getLevel() < player.getStats().getLevel() && dist < nearestDist) {
                        nearestPlayer = p;
                        nearestDist = dist;
                    }
                }
            }
        }
        return nearestPlayer;
    }

    public GasCan getNearestGasCan(Player player) {
        GasCan nearestCan = null;
        int nearestDist = Integer.MAX_VALUE;
        for (java.util.Map.Entry<GridHash, ArrayList<Entity>> entry : spatialMap.entrySet()) {
            GridHash h = entry.getKey();
            if (circleTouchesRect((int) player.getX(), (int) player.getY(), RoadMap.SECTION_SIZE * 3 * 2, h.x * CELLSIZE, h.y * CELLSIZE, (h.x + 1) * CELLSIZE, (h.y + 1) * CELLSIZE)) {
                for(Entity e : entry.getValue()) {
                    int dist = player.distanceTo(e);
                    if(e.getType().equals(EntityType.GASCAN) && dist < nearestDist) {
                        nearestCan = (GasCan) e;
                        nearestDist = dist;
                    }
                }
            }
        }
        return nearestCan;
    }

    private class GridHash {
        int x, y;

        GridHash(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + x;
            result = 31 * result + y;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof GridHash)) {
                return false;
            }
            GridHash gh = (GridHash) obj;
            return gh.x == this.x && gh.y == this.y;
        }
    }

}
