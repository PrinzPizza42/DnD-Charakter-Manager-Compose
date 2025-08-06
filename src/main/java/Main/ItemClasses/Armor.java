package Main.ItemClasses;

public class Armor extends Item{
    private Integer armorValue;
    private ArmorClasses armorClass;

    public Armor() {
        super();
        this.standardIconName = "armorIcon.png";
    }

    public Armor(
            String name,
            String description,
            int weight,
            int valueInGold,
            int amount,
            int armorValue,
            ArmorClasses armorClass
    ) {
        super(name, description, weight, valueInGold, amount);
        this.armorValue = armorValue;
        this.armorClass = armorClass;
        this.standardIconName = "armorIcon.png";
    }

    public Integer getArmorValue() {
        return armorValue;
    }

    public void setArmorValue(Integer armorValue) {
        this.armorValue = armorValue;
    }

    public ArmorClasses getArmorClass() {
        return this.armorClass;
    }

    public void setArmorClass(ArmorClasses armorClass) {
        this.armorClass = armorClass;
    }
}
