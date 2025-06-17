package Main.ItemClasses.Weapons;

import Main.ItemClasses.Item;

public class Weapon extends Item {
    private String damage;

    public Weapon() {
        super();
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public Weapon(String name, String description, int weight, int valueInGold, int amount, String damage) {
        super(name, description, weight, valueInGold, amount);
        this.damage = damage;
    }
}
