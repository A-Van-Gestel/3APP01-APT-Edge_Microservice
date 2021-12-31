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


//    @PostMapping("/player")
//    public Player addPlayer(@RequestParam Integer userId, @RequestParam String ISBN, @RequestParam Integer score){
//
//        PlayerData playerData =
//                restTemplate.postForObject("http://" + playerDataServiceBaseUrl + "/playerDatas",
//                        new PlayerData(userId,ISBN,score),PlayerData.class);
//
//        TypeTamagotchi typeTamagotchi =
//                restTemplate.getForObject("http://" + typeTamagotchiServiceBaseUrl + "/typeTamagotchis/{ISBN}",
//                        TypeTamagotchi.class,ISBN);
//
//        return new Player(typeTamagotchi, playerData);
//    }
//
//    @PutMapping("/rankings")
//    public Player updateRanking(@RequestParam Integer userId, @RequestParam String ISBN, @RequestParam Integer score){
//
//        PlayerData playerData =
//                restTemplate.getForObject("http://" + playerDataServiceBaseUrl + "/playerDatas/user/" + userId + "/typeTamagotchi/" + ISBN,
//                        PlayerData.class);
//        playerData.setScoreNumber(score);
//
//        ResponseEntity<PlayerData> responseEntityPlayerData =
//                restTemplate.exchange("http://" + playerDataServiceBaseUrl + "/playerDatas",
//                        HttpMethod.PUT, new HttpEntity<>(playerData), PlayerData.class);
//
//        PlayerData retrievedPlayerData = responseEntityPlayerData.getBody();
//
//        TypeTamagotchi typeTamagotchi =
//                restTemplate.getForObject("http://" + typeTamagotchiServiceBaseUrl + "/typeTamagotchis/{ISBN}",
//                        TypeTamagotchi.class,ISBN);
//
//        return new Player(typeTamagotchi, retrievedPlayerData);
//    }
//
//    @DeleteMapping("/rankings/{userId}/typeTamagotchi/{ISBN}")
//    public ResponseEntity deleteRanking(@PathVariable Integer userId, @PathVariable String ISBN){
//
//        restTemplate.delete("http://" + playerDataServiceBaseUrl + "/playerDatas/user/" + userId + "/typeTamagotchi/" + ISBN);
//
//        return ResponseEntity.ok().build();
//    }
}
