package fact.it.edge_microservice.controller;

import fact.it.edge_microservice.exception.BadArgumentsException;
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

import java.io.IOException;
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

    private static final String URL_PROTOCOL = "http://";
    private static final String PATTERN_LETTERS_AND_DIGITS_AND_SPACES = "^[a-zA-Z0-9 ]+$";
    private static final String PATTERN_LETTERS_AND_DIGITS = "^[a-zA-Z0-9]+$";
    private static final String PATTERN_LETTERS = "^[a-zA-Z]+$";

    // Get all players
    @GetMapping("/players")
    public List<Player> getAllPlayers(){

        List<Player> returnList = new ArrayList<>();

        ResponseEntity<List<PlayerData>> responseEntityPlayerDatas =
                restTemplate.exchange(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerDatas",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<PlayerData>>() {
                        });

        List<PlayerData> playerDatas = responseEntityPlayerDatas.getBody();

        for (PlayerData playerData: playerDatas) {
            Player playerToAdd = getPlayerToAdd(playerData);

            returnList.add(playerToAdd);
        }

        return returnList;
    }


    // Get player with specific playerDataCode
    @GetMapping("/player/{playerDataCode}")
    public Player getPlayerByPlayerDataCode(@PathVariable String playerDataCode){

        ResponseEntity<PlayerData> responseEntityPlayerDatas =
                restTemplate.exchange(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/{playerDataCode}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<PlayerData>() {
                        }, playerDataCode);

        PlayerData playerData = responseEntityPlayerDatas.getBody();

        assert playerData != null;
        TypeTamagotchi typeTamagotchi =
                restTemplate.getForObject(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/{typeName}",
                        TypeTamagotchi.class, playerData.getTypeName());

        return new Player(playerData, typeTamagotchi);
    }


    // Get all players with a specific tamagotchi type
    @GetMapping("/players/type/{typeName}")
    public List<Player> getPlayersByTypeName(@PathVariable String typeName){

        List<Player> returnList = new ArrayList<>();

        ResponseEntity<List<PlayerData>> responseEntityPlayerDatas =
                restTemplate.exchange(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerDatas/type/{typeName}",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<PlayerData>>() {
                        }, typeName);

        List<PlayerData> playerDatas = responseEntityPlayerDatas.getBody();

        for (PlayerData playerData: playerDatas) {
            TypeTamagotchi typeTamagotchi =
                    restTemplate.getForObject(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/{typeName}",
                            TypeTamagotchi.class, playerData.getTypeName());

            returnList.add(new Player(playerData, typeTamagotchi));
        }

        return returnList;
    }


    // Get all players with a specific alive state (true | false)
    @GetMapping("/players/alive/{alive}")
    public List<Player> getPlayersByAliveState(@PathVariable Boolean alive){

        List<Player> returnList = new ArrayList<>();

        ResponseEntity<List<PlayerData>> responseEntityPlayerDatas =
                restTemplate.exchange(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerDatas",
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<PlayerData>>() {
                        });

        List<PlayerData> playerDatas = responseEntityPlayerDatas.getBody();

        for (PlayerData playerData: playerDatas) {
            if (
                    (Boolean.TRUE.equals(alive) && playerData.getHealth() > 0) ||
                    (Boolean.FALSE.equals(alive) && playerData.getHealth() <= 0)
            ) {
                Player playerToAdd = getPlayerToAdd(playerData);
                returnList.add(playerToAdd);
            }
        }

        return returnList;
    }


    // Create a new player with given playerDataCode, typeName & TamagotchiName, return the player
    @PostMapping("/player")
    public Player addPlayer(@RequestParam String playerDataCode, @RequestParam String typeName, @RequestParam String name){

        LocalDateTime dateTime = LocalDateTime.now();

        PlayerData playerData =
                restTemplate.postForObject(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData",
                        new PlayerData(playerDataCode, typeName, name, 100, 100, dateTime, dateTime, 0), PlayerData.class);

        TypeTamagotchi typeTamagotchi =
                restTemplate.getForObject(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/{typeName}",
                        TypeTamagotchi.class, typeName);


        return new Player(playerData, typeTamagotchi);
    }


    // Allow the player to update their tamagotchi's name, return the player object
    @PutMapping("/player")
    public Player updatePlayer(@RequestParam String playerDataCode, @RequestParam String typeName, @RequestParam String name) throws IOException {

        // Check to validate if the user input is valid
        if (
                !playerDataCode.matches(PATTERN_LETTERS_AND_DIGITS) // Restrict the playerDataCode to letters and digits only
        ) {
            throw new BadArgumentsException("playerDataCode parameter contains bad characters. Only letters and digits are allowed.");
        } else if (
                !typeName.matches(PATTERN_LETTERS) // Restrict the typeName to letters only
        ) {
            throw new BadArgumentsException("typeName parameter contains bad characters. Only letters are allowed.");
        } else if (
                !name.matches(PATTERN_LETTERS_AND_DIGITS_AND_SPACES) // Restrict the name to letters and digits and spaces only      // Restrict the typeName to letters only
        ) {
            throw new BadArgumentsException("name parameter contains bad characters. Only letters, digits and spaces are allowed.");
        }

        PlayerData playerData =
                restTemplate.getForObject(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/" + playerDataCode,
                        PlayerData.class);

        assert playerData != null;
        playerData.setName(name);

        ResponseEntity<PlayerData> responseEntityPlayerData =
                restTemplate.exchange(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData",
                        HttpMethod.PUT, new HttpEntity<>(playerData), PlayerData.class);

        PlayerData retrievedPlayerData = responseEntityPlayerData.getBody();

        TypeTamagotchi typeTamagotchi =
                restTemplate.getForObject(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/{typeName}",
                        TypeTamagotchi.class, typeName);

        return new Player(retrievedPlayerData, typeTamagotchi);
    }


    // Allow the player to delete their player and progress made
    @DeleteMapping("/player/{playerDataCode}")
    public ResponseEntity<Object> deletePlayer(@PathVariable String playerDataCode) {

        // Check to validate if the user input is valid
        if (
                !playerDataCode.matches(PATTERN_LETTERS_AND_DIGITS) // Restrict the playerDataCode to letters and digits only
        ) {
            throw new BadArgumentsException("playerDataCode parameter contains bad characters. Only letters and digits are allowed.");
        }

        restTemplate.delete(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/" + playerDataCode);

        return ResponseEntity.ok().build();
    }

    private Player getPlayerToAdd(PlayerData playerData) {
        TypeTamagotchi typeTamagotchi =
                restTemplate.getForObject(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/{typeName}",
                        TypeTamagotchi.class, playerData.getTypeName());

        return new Player(playerData, typeTamagotchi);
    }
}
