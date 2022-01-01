package fact.it.edge_microservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerTamagotchi {

    private String playerDataId;
    private int typeTamagotchiId;

    public PlayerTamagotchi(String playerDataId, int typeTamagotchiId) {
        this.playerDataId = playerDataId;
        this.typeTamagotchiId = typeTamagotchiId;
    }
}
