package Main;

import androidx.compose.runtime.MutableState;
import com.fasterxml.jackson.annotation.JsonIgnore;
import Main.ItemClasses.Consumable;
import Main.ItemClasses.Item;
import Main.ItemClasses.Miscellaneous;
import Main.ItemClasses.Weapons.Weapon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Inventory {
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Spell> spells = new ArrayList<>();
    private String name = "Inventory";
    @JsonIgnore
    public UUID uuid = UUID.randomUUID();

    public Inventory(String name) {
        this.name = name;
    }

    public Inventory(String name, UUID uuid, ArrayList<Item> items) {
        this.name = name;
        this.uuid = uuid;
        this.items = items;
    }

    public Inventory() {}

    public String getName() {
        return this.name;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    @JsonIgnore
    public void removeItem(Item item) {
        items.remove(item);
    }

    @JsonIgnore
    public void print() {
        for (Item item : items) {
            System.out.println((items.indexOf(item) + 1) + ". " + item.getAmount() + "x - " + item.getValueInGold() + "G - " + item.getName() + ": " + item.getDescription() + " - " + item.getWeight() + "lb");
        }
    }

    @JsonIgnore
    public void printSorted() {
        System.out.println("Waffen:");
        boolean foundWeapons = false;
        for (Item item : items) {
            if(item instanceof Weapon) {
                foundWeapons = true;
                System.out.println((items.indexOf(item) + 1) + ". " + item.getAmount() + "x - " + ((Weapon) item).getDamage() + "DMG - " + item.getName() + ": " + item.getDescription());
            }
        }
        if(!foundWeapons) System.out.println("Keine Waffen gefunden");

        System.out.println("\nVerbrauchbares:");
        boolean foundConsumables = false;
        for (Item item : items) {
            if(item instanceof Consumable) {
                foundConsumables = true;
                System.out.println((items.indexOf(item) + 1) + ". " + item.getAmount() + "x - " + item.getName() + ": " + item.getDescription());
            }
        }
        if(!foundConsumables) System.out.println("Keine verbrauchbaren Gegenstände gefunden");

        System.out.println("\nSonstiges:");
        boolean foundMisc = false;
        for (Item item : items) {
            if(item instanceof Miscellaneous) {
                foundMisc = true;
                System.out.println((items.indexOf(item) + 1) + ". " + item.getAmount() + "x - " + item.getName() + ": " + item.getDescription());
            }
        }
        if(!foundMisc) System.out.println("Keine sonstigen Gegenstände gefunden");
    }

    @JsonIgnore
    public Item getAtIndex(int index) {
        return items.get(index);
    }

    @JsonIgnore
    public boolean contains(Item item) {
        return items.contains(item);
    }

    @JsonIgnore
    public void moveItemToPlace(Item item, int place) {
        if(!items.contains(item)) return;
        items.remove(item);
        items.add(place, item);
    }

    @JsonIgnore
    public Inventory copy() {
        return new Inventory(
                this.name,
                this.uuid,
                this.items);
    }
}