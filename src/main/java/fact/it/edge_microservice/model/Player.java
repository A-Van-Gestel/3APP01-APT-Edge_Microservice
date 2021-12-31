package fact.it.edge_microservice.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Player {

    private String name;
    private String playerDataCode;
    private List<PlayerTamagotchi> playerTamagotchis;

    public Player(PlayerData playerData, TypeTamagotchi typeTamagotchi) {
        setName(playerData.getName());
        setPlayerDataCode(playerData.getPlayerDataCode());
        playerTamagotchis = new ArrayList<>();
        playerTamagotchis.add(new PlayerTamagotchi(playerData.getId(), typeTamagotchi.getId()));
        setPlayerTamagotchis(playerTamagotchis);
    }

    public List<PlayerTamagotchi> addTamagotchi(String playerDataId, Integer typeTamagotchiId) {
        playerTamagotchis = getPlayerTamagotchis();
        playerTamagotchis.add(new PlayerTamagotchi(playerDataId, typeTamagotchiId));
        setPlayerTamagotchis(playerTamagotchis);

        return playerTamagotchis;
    }
}
