package Main.ItemClasses;

public class Miscellaneous extends Item{
    public Miscellaneous(String name, String description, int weight, int valueInGold, int amount) {
        super(name, description, weight, valueInGold, amount);
        this.iconName = "Log of Wood.png";
    }

    public Miscellaneous() {
        super();
        this.iconName = "Loaf of Wood.png";
    }
}
