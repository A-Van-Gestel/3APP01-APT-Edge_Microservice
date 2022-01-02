package fact.it.edge_microservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypeTamagotchi {
    private int id;
    private String typeName;
    private String description;
    private int maxWeight;
    private int minWeight;
    private int minHealth;
    private int neuroticism;
    private int metabolism;
    private int minHappiness;

    public TypeTamagotchi() {
    }

    public TypeTamagotchi(String typeName, String description, int maxWeight, int minWeight, int minHealth, int neuroticism, int metabolism, int minHappiness) {
        this.typeName = typeName;
        this.description = description;
        this.maxWeight = maxWeight;
        this.minWeight = minWeight;
        this.minHealth = minHealth;
        this.neuroticism = neuroticism;
        this.metabolism = metabolism;
        this.minHappiness = minHappiness;
    }
}
