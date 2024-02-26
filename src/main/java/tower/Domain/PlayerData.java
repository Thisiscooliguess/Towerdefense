package tower.Domain;



import mindustry.gen.Player;
import mindustry.gen.Unit;



/**
 * Represents the data associated with a player in the tower defense game.
 * This class stores information such as the player's name, UUID, IP address, health points,
 * the unit they are controlling, points accumulated, and other game-related data.
 */
public class PlayerData {

    private Unit unit;
    private float hp;
    private String uuid;
    private float lastUpdatedPoints;
    private String name;
    private boolean showStats;
    private float points;

    public void addPointsWithReduction(float reductionPercentage) {
        float basePoints =  1f; // Base points to be added
        float reduction = this.points * reductionPercentage /  100; // Calculate reduction
        float pointsToAdd = basePoints - reduction; // Calculate points to add after reduction
        this.points += pointsToAdd;
    }
    public void addKills (int amount) {
    }

    public void setKills (int amount) {
    }

    public void addPoints(float amount, Player player) {
        this.points += amount;
   
    }
    public float calculateReductionPercentage(float points) {

        return points *   0.1f;
    }
    public void subtractPoints(float amount, Player player) {
        this.points -= amount;
    }
    public float getLastUpdatedPoints() {
        return lastUpdatedPoints;
    }
    
    public void setLastUpdatedPoints(float lastUpdatedPoints) {
        this.lastUpdatedPoints = lastUpdatedPoints;
    }
    public void setPoints(float points, Player player) {
        this.points = points;

    }


    public float getPoints() {
        return points;
    }
    public Unit getUnit() {
        return unit;
    }
    
    public void setStats(boolean value) {
        this.showStats = value;
    }
    
    public boolean stats() {
        return showStats;
    }
    public void setHp(float hp) {
        this.hp = hp;
    }
    public String getName() {
        return name;
    }

    public String getUuid() {
        return this.uuid;
    }

    public float getHp() {
        return hp;
    }
    
    
    public void setName(String name) {
        this.name = name;
    }

    public PlayerData(Player player) {
        this.uuid = player.uuid();
        player.ip();
        this.hp = player.unit().health();
        this.name = player.name();
        this.points= 0;
    }
}