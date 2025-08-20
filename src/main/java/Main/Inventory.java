package Main;

import com.fasterxml.jackson.annotation.JsonIgnore;
import Main.ItemClasses.Consumable;
import Main.ItemClasses.Item;
import Main.ItemClasses.Miscellaneous;
import Main.ItemClasses.Weapons.Weapon;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.UUID;

public class Inventory {
    private ArrayList<Item> items = new ArrayList<>();
    private final ArrayList<Spell> spells = new ArrayList<>();
    private String name = "Inventory";
    private volatile ArrayList<Integer> spellSlotsUsed = new ArrayList<>();
    private volatile ArrayList<Integer> spellSlotsMax = new ArrayList<>();
    private boolean loadedLevels = false;
    private float maxCarryingCapacity = 100f;
    @JsonIgnore
    public UUID uuid = UUID.randomUUID();
    @JsonIgnore
    private final ArrayList<Pair<Integer, Integer>> spellLevels = new ArrayList<>();


    public Inventory(String name) {
        this.name = name;
    }

    public Inventory(String name, UUID uuid, ArrayList<Item> items) {
        this.name = name;
        this.uuid = uuid;
        this.items = items;
    }

    public float getMaxCarryingCapacity() {
        return maxCarryingCapacity;
    }

    public void setMaxCarryingCapacity(float maxCarryingCapacity) {
        this.maxCarryingCapacity = maxCarryingCapacity;
    }

    public void addLastSpellSlot(Pair<Integer, Integer> slot) {
        spellLevels.addLast(slot);
    }

    public void removeSpellSlot(int index) {
        spellLevels.remove(index);
    }

    public ArrayList<Pair<Integer, Integer>> getSpellLevels() {
        if(!loadedLevels) {
            loadedLevels = true;
            spellSlotsUsed.forEach(used ->
                    spellLevels.add(new Pair<>(used, spellSlotsMax.get(spellSlotsUsed.indexOf(used))))
            );
        }

        System.out.println("spellSlots: " + spellLevels);
        System.out.println("spellSlotsUsed: " + spellSlotsUsed);
        System.out.println("spellSlotsMax: " + spellSlotsMax);
        return spellLevels;
    }

    public void resetUsedSpellSlots() {
        spellSlotsUsed.forEach(used -> spellSlotsUsed.set(spellSlotsUsed.indexOf(used), spellSlotsMax.get(spellSlotsUsed.indexOf(used))));
        for(Pair<Integer, Integer> level : spellLevels) {
            int index = spellLevels.indexOf(level);
            int a = level.component1();
            int b = level.component2();
            Pair<Integer, Integer> replacement = new Pair<>(b, b);
            spellLevels.set(index, replacement);
        }
        System.out.println("reset spellslotsUsed " + spellSlotsUsed);
    }

    public void setSpellSlotsUsed(ArrayList<Integer> spellSlotsUsed) {
        System.out.println("setting spellSlotsUsed to " + spellSlotsUsed);
        this.spellSlotsUsed = spellSlotsUsed;
    }

    public ArrayList<Integer> getSpellSlotsUsed() {
        System.out.println("Before clear: " + spellSlotsUsed);
        spellSlotsUsed.clear();
        System.out.println("After clear: " + spellSlotsUsed);
        System.out.println("getting spellSlotsUsed:");
        System.out.println("Spelllevels: " + spellLevels);
        for (Pair<Integer, Integer> spellLevel : spellLevels) {
            spellSlotsUsed.addLast(spellLevel.getFirst());
        }
        System.out.println(spellSlotsUsed);
        return this.spellSlotsUsed;
    }

    public void setSpellSlotsMax(ArrayList<Integer> spellSlotsMax) {
        this.spellSlotsMax = spellSlotsMax;
    }

    public ArrayList<Integer> getSpellSlotsMax() {
        spellSlotsMax.clear();
        System.out.println("getting spellSlotsMax:");
        for (Pair<Integer, Integer> spellLevel : spellLevels) {
            spellSlotsMax.addLast(spellLevel.getSecond());
        }
        System.out.println(spellSlotsMax);
        return this.spellSlotsMax;
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
    public Inventory(Inventory other) {
        this.name = other.name;
        this.uuid = UUID.randomUUID();
        this.items = new ArrayList<>(other.items);
        this.spells.addAll(other.spells);
        this.spellSlotsUsed.addAll(other.spellSlotsUsed);
        this.spellSlotsMax.addAll(other.spellSlotsMax);
        this.loadedLevels = other.loadedLevels;
        this.spellLevels.addAll(other.spellLevels);
    }
}