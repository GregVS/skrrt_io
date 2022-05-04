package game;

import entities.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gregory on 6/13/17.
 */
public class Collision {

    public static void handleAllCollisions(HashMap<Integer, Entity> entityMap, SpatialHash spatialHash) {
        for(Map.Entry<Integer, Entity> entry : entityMap.entrySet()) {
            if( ! entry.getValue().getType().equals(EntityType.PLAYER)) continue;
            handleAllCollisionsForPlayer((Player) entry.getValue(), spatialHash);
        }
    }

    private static void handleAllCollisionsForPlayer(Player player, SpatialHash spatialHash) {
        Rectangle boundingBox = player.createBoundingRectangle();
        for(Entity testEntity : spatialHash.getEntitiesInSpatialRadius((int) player.getX(), (int) player.getY(), RoadMap.SECTION_SIZE)) {
            if(testEntity.getId().equals(player.getId())) continue;
            if(boundingBox.intersects(testEntity.createBoundingRectangle())) handleEntityCollisionForPlayer(player, testEntity);
        }
    }

    private static void handleEntityCollisionForPlayer(Player player, Entity entity) {
        if(entity.getType().equals(EntityType.PLAYER)) playersAttack(player, (Player) entity);
        else if(entity.getType().equals(EntityType.GASCAN)) handleGasCollision(player, (GasCan) entity);
        else if(entity.getType().equals(EntityType.WRECKAGE)) ((Wreckage) entity).hitPlayer(player);
        else if(entity.getType().equals(EntityType.LAUNCHPAD)) ((Launchpad) entity).launchPlayer(player);
        else if(entity.getType().equals(EntityType.REPAIRKIT)) {
            RepairKit rk = (RepairKit) entity;
            player.getStats().gainHealth(rk.getRepairAmount());
            rk.setUsed(true);
        }
    }

    private static void playersAttack(Player player1, Player player2) {
        if(player1.getStats().getLevel() == player2.getStats().getLevel()) {
            return;
        }

        Player attackingPlayer = player1.getStats().getLevel() > player2.getStats().getLevel() ? player1 : player2;
        Player attackedPlayer = player1.getStats().getLevel() < player2.getStats().getLevel() ? player1 : player2;

        if(attackedPlayer.getStats().getHealth() <= 0) return;
        attackedPlayer.getStats().takeDamage(attackingPlayer.getStats().getDamage());
        if(attackedPlayer.getStats().getHealth() <= 0) {
            attackingPlayer.getStats().earnXP(PlayerStats.xpForKill(attackedPlayer.getStats().getLevel()));
            pushPlayer(attackedPlayer, 250);
        } else {
            pushPlayer(attackedPlayer, 150); //push attacked player forwards
        }
    }

    private static void pushPlayer(Player player, int amount) {
        player.getTransform().setX(player.getTransform().getX() + amount * Maths.dcos(270 - player.getTransform().getRotation()));
        player.getTransform().setY(player.getTransform().getY() + amount * Maths.dsin(270 - player.getTransform().getRotation()));
    }

    private static void handleGasCollision(Player player, GasCan gas) {
        player.getStats().earnXP(gas.getXp());
        player.getStats().refuel(25);
        gas.setUsed(true);
    }

}
