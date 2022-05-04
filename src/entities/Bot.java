package entities;

import game.InputPacket;
import game.RoadMap;
import game.SectionType;
import game.SpatialHash;

/**
 * Created by Gregory on 8/3/17.
 */
public class Bot {

    private Player player;
    private Entity currTarget;

    public static final String[] names = {
            "Geoff",
            "Santa's Elf",
            "VroomVroom",
            "DontKillMe",
            "chiken",
            "usa",
            "thechosenone",
            "John_doe",
            "beep beep",
            "asdfkjl",
            "johnnydepp",
            "fried chicken",
            "team?",
            "not a noob",
            "gottagofast",
            "Chuck Norris",
            "that one guy",
            "not a bot",
            "4 wheel drive",
            "sonic",
            "Marcos",
            "spel wurds rite"
    };

    private SpatialHash spatialHash;

    public Bot(Player player, SpatialHash spatialHash) {
        this.player = player;
        this.spatialHash = spatialHash;
    }

    public void update() {
        findGasTarget();
        findTarget();

        SectionType type = RoadMap.getSectionTypeAt(player.getX(), player.getY());
        if(type == SectionType.INTERSECTION) {
            if (currTarget == null) moveInIntersection();
            else {
                if(currTarget.getType().equals(EntityType.GASCAN)) chooseTurnForGasCan();
                else chooseTurn();
            }
        } else if (type == SectionType.ROAD) {
            if(currTarget == null || currTarget.distanceTo(player) > RoadMap.SECTION_SIZE / 2) moveInRoad();
            else chooseTurn();
        }
    }

    private void moveInRoad() {
        if(Math.random() < 0.002) player.getInputPacket().setLaneChange(-1);
        else if(Math.random() < 0.002) player.getInputPacket().setLaneChange(1);
        else player.getInputPacket().setLaneChange(0);
    }

    private void findGasTarget() {
        if(currTarget != null && !currTarget.isAlive()) currTarget = null;
        if(player.getStats().getGasLevel() > 30 || (currTarget != null && currTarget.getType().equals(EntityType.GASCAN))) return;
        Entity target = spatialHash.getNearestGasCan(player);
        if(target != null) currTarget = target;
    }

    private void findTarget() {
        if(currTarget != null) {
            if(currTarget.getType().equals(EntityType.GASCAN)) return;
            if (!currTarget.isAlive() || Math.random() < 0.00003 || ((Player) currTarget).getStats().getLevel() >= player.getStats().getLevel()) {
                currTarget = null;
            } else {
                return;
            }
        }
        Entity target = spatialHash.getClosestBotTarget(player);
        if(target != null) currTarget = target;
    }

    private int distanceAngle(int alpha, int beta) {
        int phi = Math.abs(beta - alpha) % 360;       // This is either the distance or 360 - distance
        return phi > 180 ? 360 - phi : phi;
    }

    private void chooseTurnForGasCan() { //this uses the center of the section the gas can is in to prevent a wierd bug
        int angleToTarget = (270 - (int) Math.toDegrees(Math.atan2(
                currTarget.getY() - Math.floorMod((int) currTarget.getY(), RoadMap.SECTION_SIZE) - player.getY(),
                currTarget.getX() - Math.floorMod((int) currTarget.getX(), RoadMap.SECTION_SIZE) - player.getX())) + 360) % 360;
        turnToMinimizeAngle(angleToTarget);
    }

    private void chooseTurn() {
        int angleToTarget = (270 - (int) Math.toDegrees(Math.atan2(currTarget.getY() - player.getY(), currTarget.getX() - player.getX())) + 360) % 360;
        turnToMinimizeAngle(angleToTarget);
    }

    private void turnToMinimizeAngle(int angle) {
        int leftAngleDiff = distanceAngle((int) (player.getRotation() + Rotation.LEFT), angle);
        int rightAngleDiff = distanceAngle((int) (player.getRotation() + Rotation.RIGHT), angle);
        int forwardAngleDiff = distanceAngle((int) player.getRotation(), angle);
        if(leftAngleDiff <= rightAngleDiff && leftAngleDiff <= forwardAngleDiff) {
            player.getInputPacket().setLaneChange(InputPacket.LEFT_SHIFT);
        } else if(rightAngleDiff <= leftAngleDiff && rightAngleDiff <= forwardAngleDiff) {
            player.getInputPacket().setLaneChange(InputPacket.RIGHT_SHIFT);
        } else {
            player.getInputPacket().setLaneChange(InputPacket.NO_SHIFT);
        }
    }

    private void moveInIntersection() {
        if(preventOOB()) return;
        float playerDrivingAxis = (player.transform.getRotation() == Rotation.UP || player.transform.getRotation() == Rotation.DOWN) ? player.transform.getY() : player.transform.getX();
        float halfwayLine = playerDrivingAxis - Math.floorMod((int) playerDrivingAxis, RoadMap.SECTION_SIZE) + RoadMap.SECTION_SIZE / 2;
        if (playerDrivingAxis <= halfwayLine) {
            if(Math.random() <= 0.002) player.getInputPacket().setLaneChange(Player.bottomHalfTurnMap.get(player.transform.getRotation()));
        } else if (playerDrivingAxis > halfwayLine) {
            if(Math.random() <= 0.002) player.getInputPacket().setLaneChange(Player.bottomHalfTurnMap.get((player.getTransform().getRotation() + 180) % 360));
        }
    }

    //prevent from going out of bounds
    private boolean preventOOB() {
        int tx = (int) player.getX(), ty = (int) player.getY();
        if(player.getX() < 0) tx = 0;
        else if(player.getY() < 0) ty = 0;
        else if(player.getX() > RoadMap.MAP_UNITS) tx = RoadMap.MAP_UNITS;
        else if(player.getY() > RoadMap.MAP_UNITS) ty = RoadMap.MAP_UNITS;
        else return false;

        int angleToMapCenter = 270 - (int) Math.toDegrees(Math.atan2(ty - player.getY(), tx -player.getX()));
        turnToMinimizeAngle(angleToMapCenter);
        return true;
    }


    public Player getPlayer() {
        return player;
    }
}
