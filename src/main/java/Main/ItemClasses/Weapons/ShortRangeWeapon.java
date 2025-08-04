package Main.ItemClasses.Weapons;

public class ShortRangeWeapon extends Weapon{
    public ShortRangeWeapon(String name, String description, int weight, int valueInGold, int amount, String damage) {
        super(name, description, weight, valueInGold, amount, damage);
        this.iconName = "standardClassIcons/Longsword.png";
    }

    public ShortRangeWeapon() {
        super();
        this.iconName = "standardClassIcons/Longsword.png";
    }
}
