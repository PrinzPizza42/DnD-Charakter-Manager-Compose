package Main.ItemClasses.Weapons;

public class LongRangeWeapon extends Weapon{
    public LongRangeWeapon(String name, String description, int weight, int valueInGold, int amount, String damage) {
        super(name, description, weight, valueInGold, amount, damage);
        this.iconName = "standardClassIcons/Longbow.png";
    }

    public LongRangeWeapon() {
        super();
        this.iconName = "standardClassIcons/Longbow.png";
    }
}
