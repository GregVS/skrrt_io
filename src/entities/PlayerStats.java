package entities;

import server.GameTimeManager;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Gregory on 6/15/17.
 */
public class PlayerStats {

    private static final int MAX_LEVEL = 8;
    public static final int MAXHEALTH = 30;
    private static Timer timer = new Timer();

    private int xp = 10;
    private int level = 1;
    private float health = maxHPForLevel(level);
    private int damage = 0;
    private int speed = 0;
    private boolean hurtFlag = false;
    private boolean boostProtection = false;

    private boolean spawnProtected = false;
    private static final int PROTECTION_DUR_MS = 2200;
    private static final Timer protectionTimer = new Timer(); //handles spawn protection

    private float gasLevel = 100;
    private Player player;

    public PlayerStats(Player player) {
        resetToNormalSpeed();
        this.player = player;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
        boostProtection = this.speed != speedForLevel(this.level);
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHealth() {
        return (int) health;
    }

    public int getDamage() {
        return damage;
    }

    public void gainHealth(int hp) {
        this.health += hp;
        if(this.health > MAXHEALTH) this.health = MAXHEALTH;
    }

    public void die() {
        this.health = 0;
    }

    public void resetToNormalSpeed() {
        boostProtection = false;
        speed = speedForLevel(level);
    }

    private float gasPerSec(int level) {
        return player.getInputPacket().isSlow() ? (0.7f * level + 1f) * 0.2f : 0.7f * level + 1f;
    }

    void updateGas() {
        gasLevel -= GameTimeManager.getDeltaSeconds() * gasPerSec(level);
        if(gasLevel <= 0) {
            takeDamage((float) (maxHPForLevel(level) * 0.3 * GameTimeManager.getDeltaSeconds()));
        }
    }

    public void earnXP(int amount) {
        this.xp += amount;
        if(this.xp >= maxXpForLevel(this.level) && this.level < MAX_LEVEL) { //level up
            this.level++;
            this.damage = 8;
            this.speed = speedForLevel(this.level);
            this.health = maxHPForLevel(this.level);
            gasLevel = 100;
        }
    }

    public void takeDamage(float amount) {
        if(spawnProtected) return;
        this.health -= amount;
        if(!hurtFlag) {
            hurtFlag = true;
            timer.schedule(new TimerTask() {
                @Override
                public void run() { hurtFlag = false; }
            }, 1500); //reset speed after x millis
        }
    }

    private int speedForLevel(int lvl) {
        return 30 * lvl + 200;
    }

    public int maxHPForLevel(int lvl) {
        return MAXHEALTH;
    }

    public int maxXpForLevel(int lvl) {
        return (int) (Math.pow(2, lvl) - Math.pow(2, lvl - 1)) * 200;
    }

    public boolean hasHurtFlag() {
        return hurtFlag;
    }

    public boolean isBoostProtection() {
        return boostProtection;
    }

    public static int xpForKill(int level) {
        return (int) ((Math.pow(2, level) - Math.pow(2, level - 1)) * 150f);
    }

    public float getGasLevel() {
        return gasLevel;
    }

    public void refuel(float i) {
        gasLevel += i;
        if(gasLevel > 100) gasLevel = 100;
    }

    public void activeSpawnProtection() {
        spawnProtected = true;
        protectionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                spawnProtected = false;
            }
        }, PROTECTION_DUR_MS);
    }

    public boolean isSpawnProtected() {
        return spawnProtected;
    }
}
