package fact.it.edge_microservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerTamagotchi {

    private PlayerData playerData;
    private TypeTamagotchi typeTamagotchi;

    public PlayerTamagotchi(PlayerData playerData, TypeTamagotchi typeTamagotchi) {
        this.playerData = playerData;
        this.typeTamagotchi = typeTamagotchi;
    }
}
