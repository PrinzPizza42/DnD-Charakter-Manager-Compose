package Main.ItemClasses;

import Data.ImageLoader;
import Data.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import Main.ItemClasses.Weapons.LongRangeWeapon;
import Main.ItemClasses.Weapons.ShortRangeWeapon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.NoSuchElementException;
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
        @JsonSubTypes.Type(value = Miscellaneous.class, name = "misc"),
        @JsonSubTypes.Type(value = Armor.class, name = "Armor")
})
public class Item {
    private String name;
    private String description;
    private int weight;
    private int valueInGold;
    private int amount;
    @JsonIgnore
    protected String standardIconName = "Log of Wood.png";
    protected String userIconName;
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

    @JsonIgnore
    public String getIconName() {
        return standardIconName;
    }

    public void setUserIconName(String userIconName) {
        this.userIconName = userIconName;
    }

    public String getUserIconName() {
        return this.userIconName;
    }

    @JsonIgnore
    public BufferedImage getIcon() {
        BufferedImage icon;
        if(this.userIconName != null) {
            try {
               icon = ImageLoader.loadImageFromFile(JsonUtil.getUserImagesPathPath().resolve(userIconName).toAbsolutePath().toString()).get();
               return icon;
            } catch (NoSuchElementException e) {
                System.out.println("Could not find user icon " + this.userIconName + " for " + this.name);
                this.userIconName = null;
                System.out.println("Reset user icon name for " + this.name);
            }
        }
        icon = ImageLoader.loadImageFromResources(this.standardIconName).get();
        return icon;
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
}
