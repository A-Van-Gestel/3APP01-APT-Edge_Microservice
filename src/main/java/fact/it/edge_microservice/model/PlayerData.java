package fact.it.edge_microservice.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlayerData {
    private String id;
    private String playerDataCode;
    private String typeName;
    private String name;
    private int health;
    private int happiness;
    private LocalDateTime lastFed;
    private LocalDateTime lastPetted;
    private int age;

    public PlayerData(){
    }

    public PlayerData(String playerDataCode, String typeName, String name, int health, int happiness, LocalDateTime lastFed, LocalDateTime lastPetted, int age) {
        this.playerDataCode = playerDataCode;
        this.typeName = typeName;
        this.name = name;
        this.health = health;
        this.happiness = happiness;
        this.lastFed = lastFed;
        this.lastPetted = lastPetted;
        this.age = age;
    }
}