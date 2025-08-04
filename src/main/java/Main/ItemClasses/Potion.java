package Main.ItemClasses;

public class Potion extends Consumable {
    public Potion(String name, String description, int weight, int valueInGold, int amount) {
        super(name, description, weight, valueInGold, amount);
        this.iconName = "Potion - Rainbow.png";
    }

    public Potion() {
        super();
        this.iconName = "Potion - Rainbow.png";
    }
}
