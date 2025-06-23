package Main;

public class Spell {
    private String name;
    private String description;
    private int cost;

    public Spell() {}

    public Spell(String name, String description, int cost) {
        this.name = name;
        this.description = description;
        this.cost = cost;
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
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
