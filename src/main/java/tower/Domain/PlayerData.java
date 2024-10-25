package tower.Domain;

import mindustry.gen.Player;
import mindustry.gen.Unit;

public class PlayerData {
    private static int totalPlayers = 0;
    private Unit unit;
    private float hp;
    private String uuid;

    private String name;
    private float Cash;
   public static int getTotalPlayers() {
        return totalPlayers;
    }

    public void addCash(float amount, Player player) {
        this.Cash += amount;

    }

    public void subtractCash(float amount, Player player) {
        this.Cash -= amount;
    }

    

    public void setCash(float Cash, Player player) {
        this.Cash = Cash;

    }

    public float getCash() {
        return Cash;
    }

    public Unit getUnit() {
        return unit;
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
        this.Cash = 0;
    }

}