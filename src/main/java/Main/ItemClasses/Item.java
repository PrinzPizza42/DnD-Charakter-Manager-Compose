package Main.ItemClasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import Main.ItemClasses.Weapons.LongRangeWeapon;
import Main.ItemClasses.Weapons.ShortRangeWeapon;

import java.util.UUID;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ShortRangeWeapon.class, name = "shortRangeWeapon"),
        @JsonSubTypes.Type(value = LongRangeWeapon.class, name = "longRangeWeapon"),
        @JsonSubTypes.Type(value = Consumable.class, name = "consumable"),
        @JsonSubTypes.Type(value = Potion.class, name = "potion"),
        @JsonSubTypes.Type(value = Miscellaneous.class, name = "misc")
})
public class Item {
    private String name;
    private String description;
    private int weight;
    private int valueInGold;
    private int amount;
    protected String iconName = "standardClassIcons/Log of Wood.png";
    @JsonIgnore
    private UUID uuid;
    private Boolean isEquipped = false;

    public Boolean getEquipped() {
        return isEquipped;
    }

    public void setEquipped(Boolean equipped) {
        isEquipped = equipped;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getValueInGold() {
        return valueInGold;
    }

    public void setValueInGold(int valueInGold) {
        this.valueInGold = valueInGold;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @JsonIgnore
    public UUID getUuid() {
        return uuid;
    }

//    public void setIcon(ImageIcon icon) {
//        this.iconSrc = icon;
//    }

//    @JsonIgnore //TODO rewrite icon manager in kotlin
//    public ImageIcon getIcon() {
//        return IconManager.getIconNormalSize(iconName);
//    }

    public String getIconName() {
        return iconName;
    }

    public Item(String name, String description, int weight, int valueInGold, int amount) {
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.valueInGold = valueInGold;
        this.amount = amount;
        this.uuid = UUID.randomUUID();
    }

    public Item() {
        this.uuid = UUID.randomUUID();
    }

    public UUID newUUID() {
        this.uuid = UUID.randomUUID();
        return this.uuid;
    }
}
