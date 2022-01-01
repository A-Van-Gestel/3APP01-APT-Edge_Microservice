package fact.it.edge_microservice.controller;

import fact.it.edge_microservice.model.Player;
import fact.it.edge_microservice.model.PlayerData;
import fact.it.edge_microservice.model.TypeTamagotchi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PlayerController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${playerdataservice.baseurl}")
    private String playerDataServiceBaseUrl;

    @Value("${typetamagotchiservice.baseurl}")
    private String typeTamagotchiServiceBaseUrl;


    // Get all players
    @GetMapping("/players")
    public List<Player> getAllPlayers(){

        List<Player> returnList = new ArrayList<>();

        ResponseEntity<List<PlayerData>> responseEntityPlayerDatas =
                restTemplate.exchange("http://" + playerDataServiceBaseUrl + "/playerDatas",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<PlayerData>>() {
                        });

        List<PlayerData> playerDatas = responseEntityPlayerDatas.getBody();

        assert playerDatas != null;
        for (PlayerData playerData: playerDatas) {
            TypeTamagotchi typeTamagotchi =
                    restTemplate.getForObject("http://" + typeTamagotchiServiceBaseUrl + "/types/name/{typeName}",
                            TypeTamagotchi.class, playerData.getTypeName());

            assert typeTamagotchi != null;
            returnList.add(new Player(playerData, typeTamagotchi));
        }

        return returnList;
    }


    // Get player with specific playerDataCode
    @GetMapping("/player/{playerDataCode}")
    public Player getPlayerByPlayerDataCode(@PathVariable String playerDataCode){

        ResponseEntity<PlayerData> responseEntityPlayerDatas =
                restTemplate.exchange("http://" + playerDataServiceBaseUrl + "/playerData/{playerDataCode}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<PlayerData>() {
                        }, playerDataCode);

        PlayerData playerData = responseEntityPlayerDatas.getBody();

        assert playerData != null;
        TypeTamagotchi typeTamagotchi =
                restTemplate.getForObject("http://" + typeTamagotchiServiceBaseUrl + "/types/name/{typeName}",
                        TypeTamagotchi.class, playerData.getTypeName());

        assert typeTamagotchi != null;
        return new Player(playerData, typeTamagotchi);
    }


    // Get all players with a specific tamagotchi type
    @GetMapping("/players/type/{typeName}")
    public List<Player> getPlayersByTypeName(@PathVariable String typeName){

        List<Player> returnList = new ArrayList<>();

        ResponseEntity<List<PlayerData>> responseEntityPlayerDatas =
                restTemplate.exchange("http://" + playerDataServiceBaseUrl + "/playerDatas/type/{typeName}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<PlayerData>>() {
                        }, typeName);

        List<PlayerData> playerDatas = responseEntityPlayerDatas.getBody();

        assert playerDatas != null;
        for (PlayerData playerData: playerDatas) {
            TypeTamagotchi typeTamagotchi =
                    restTemplate.getForObject("http://" + typeTamagotchiServiceBaseUrl + "/types/name/{typeName}",
                            TypeTamagotchi.class, playerData.getTypeName());

            assert typeTamagotchi != null;
            returnList.add(new Player(playerData, typeTamagotchi));
        }

        return returnList;
    }


    // Create a new player with given playerDataCode, typeName & TamagotchiName, return the player
    @PostMapping("/player")
    public Player addPlayer(@RequestParam String playerDataCode, @RequestParam String typeName, @RequestParam String name){

        LocalDateTime dateTime = LocalDateTime.now();

        PlayerData playerData =
                restTemplate.postForObject("http://" + playerDataServiceBaseUrl + "/playerData",
                        new PlayerData(playerDataCode, typeName, name, 100, 100, dateTime, dateTime, 0), PlayerData.class);

        TypeTamagotchi typeTamagotchi =
                restTemplate.getForObject("http://" + typeTamagotchiServiceBaseUrl + "/types/{typeName}",
                        TypeTamagotchi.class, typeName);


        assert playerData != null;
        assert typeTamagotchi != null;
        return new Player(playerData, typeTamagotchi);
    }


    // Allow the player to update their tamagotchi's name, return the player object
    @PutMapping("/player")
    public Player updatePlayer(@RequestParam String playerDataCode, @RequestParam String typeName, @RequestParam String name){

        PlayerData playerData =
                restTemplate.getForObject("http://" + playerDataServiceBaseUrl + "/playerData/" + playerDataCode,
                        PlayerData.class);
        assert playerData != null;
        playerData.setName(name);

        ResponseEntity<PlayerData> responseEntityPlayerData =
                restTemplate.exchange("http://" + playerDataServiceBaseUrl + "/playerData",
                        HttpMethod.PUT, new HttpEntity<>(playerData), PlayerData.class);

        PlayerData retrievedPlayerData = responseEntityPlayerData.getBody();

        TypeTamagotchi typeTamagotchi =
                restTemplate.getForObject("http://" + typeTamagotchiServiceBaseUrl + "/types/{typeName}",
                        TypeTamagotchi.class, typeName);

        assert retrievedPlayerData != null;
        assert typeTamagotchi != null;
        return new Player(retrievedPlayerData, typeTamagotchi);
    }


    // Allow the player to delete their player and progress made
    @DeleteMapping("/player/{playerDataCode}")
    public ResponseEntity<Object> deleteRanking(@PathVariable Integer playerDataCode){

        restTemplate.delete("http://" + playerDataServiceBaseUrl + "/playerData/" + playerDataCode);

        return ResponseEntity.ok().build();
    }
}
