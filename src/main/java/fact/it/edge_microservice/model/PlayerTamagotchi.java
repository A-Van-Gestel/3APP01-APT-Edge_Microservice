package fact.it.edge_microservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PlayerTamagotchi {

    private String playerDataId;
    private Integer typeTamagotchiId;

    public PlayerTamagotchi(String playerDataId, Integer typeTamagotchiId) {
        this.playerDataId = playerDataId;
        this.typeTamagotchiId = typeTamagotchiId;
    }
}
