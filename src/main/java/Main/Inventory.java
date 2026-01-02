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


    public Inventory() {}

    public Inventory(String name) {
        this.name = name;
        addLastSpellLevel(new Pair<>(3, 3));
    }

    public Inventory(String name, UUID uuid, ArrayList<Item> items) {
        this.name = name;
        this.uuid = uuid;
        this.items = items;
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
        this.maxCarryingCapacity = other.maxCarryingCapacity;
        this.spellLevels.addAll(other.spellLevels);
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public float getMaxCarryingCapacity() {
        return maxCarryingCapacity;
    }

    public void setMaxCarryingCapacity(float maxCarryingCapacity) {
        this.maxCarryingCapacity = maxCarryingCapacity;
    }

    public ArrayList<Spell> getSpells() {
        return spells;
    }

    public void addLastSpellLevel(Pair<Integer, Integer> level) {
        spellLevels.addLast(level);
    }

    public void removeSpellLevel(int index) {
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
    }
}