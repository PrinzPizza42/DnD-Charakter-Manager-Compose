package Main.ItemClasses;

public class Consumable extends Item {
    public Consumable(String name, String description, int weight, int valueInGold, int amount) {
        super(name, description, weight, valueInGold, amount);
        this.iconName = "standardClassIcons/Loaf of Bread.png";
    }

    public Consumable() {
        super();
        this.iconName = "standardClassIcons/Loaf of Bread.png";
    }
}
