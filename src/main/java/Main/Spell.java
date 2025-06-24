package Main;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

public class Spell {
    private String name;
    private String description;
    private int cost;
    @JsonIgnore
    private final UUID uuid = UUID.randomUUID();
    private Boolean isTemplate = false;

    @JsonIgnore
    public UUID getUuid() {
        return uuid;
    }

    public Spell() {}

    public Spell(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;

        isTemplate = name.equalsIgnoreCase("Neuer Zauber (Vorlage)");
    }

    public Boolean isTemplate() {
        return isTemplate;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCost() {
        return cost;
    }

    public void setName(String name) {
        this.name = name;
        isTemplate = name.equalsIgnoreCase("Neuer Zauber (Vorlage)");
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
