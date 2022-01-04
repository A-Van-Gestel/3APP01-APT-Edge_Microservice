package fact.it.edge_microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import fact.it.edge_microservice.exception.BadArgumentsException;
import fact.it.edge_microservice.model.PlayerData;
import fact.it.edge_microservice.model.TypeTamagotchi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlayerControllerUnitTests {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${playerdataservice.baseurl}")
    private String playerDataServiceBaseUrl;

    @Value("${typetamagotchiservice.baseurl}")
    private String typeTamagotchiServiceBaseUrl;

    private static final String URL_PROTOCOL = "http://";

    @Autowired
    private MockMvc mockMvc;

    private MockRestServiceServer mockServer;

    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new ParameterNamesModule())
            .addModule(new Jdk8Module())
            .addModule(new JavaTimeModule())
            .build();

    // --- PlayerData mock data ---
    private final PlayerData playerData1 = new PlayerData("12345abcde","Slijmie","Rimu Tempest",80,50, LocalDateTime.of(2017, 2, 13, 15, 56, 42),LocalDateTime.of(2017, 2, 13, 15, 56, 5),30);
    private final PlayerData playerData2 = new PlayerData("abcde12345","Slakkie","Slakkie Slak",70,60,LocalDateTime.of(2019, 3, 4, 15, 56, 12),LocalDateTime.of(2019, 3, 5, 15, 56, 35),40);
    private final PlayerData playerData3 = new PlayerData("Fluffy12345","Fluffy","Fluffy Pluisbol",0,80, LocalDateTime.of(2021, 5, 12, 15, 35, 59),LocalDateTime.of(2021, 5, 12, 15, 35, 41),1);

    List<PlayerData> playerDataList = Arrays.asList(playerData1, playerData2, playerData3);
    List<PlayerData> playerDataTypeSlijmieList = List.of(playerData1);
    List<PlayerData> playerDataAliveTrueList = List.of(playerData1, playerData2);
    List<PlayerData> playerDataAliveFalseList = List.of(playerData3);

    // --- TypeTamagotchi mock data ---
    private final TypeTamagotchi type1 = new TypeTamagotchi("Slijmie","Een slijmerig maar schattig dier",160,80,50,32,80,30);
    private final TypeTamagotchi type2 = new TypeTamagotchi("Slakkie","Een slak",120,70,60,98,120,40);
    private final TypeTamagotchi type3 = new TypeTamagotchi("Fluffy","Een pluisbol",160,80,50,32,80,30);

    List<TypeTamagotchi> typeList = Arrays.asList(type1, type2, type3);

    @BeforeEach
    public void initializeMockserver() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void whenGetPlayers_thenReturnPlayersJson() throws Exception {

        // GET all playerData from playerDataCode '12345abcde'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerDatas")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerDataList))
                );

        // GET TypeTamagotchi with typeName 'Slijmie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slijmie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type1))
                );

        // GET TypeTamagotchi with typeName 'Slakkie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slakkie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type2))
                );

        // GET TypeTamagotchi with typeName 'Fluffy'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Fluffy")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type3))
                );

        mockMvc.perform(get("/players"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Array length is correct
                .andExpect(jsonPath("$", hasSize(3)))

                // player1 is correct
                .andExpect(jsonPath("$[0].name", is("Rimu Tempest")))
                .andExpect(jsonPath("$[0].playerDataCode", is("12345abcde")))
                // playerData1 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.playerDataCode",is("12345abcde")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.typeName",is("Slijmie")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.name",is("Rimu Tempest")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.health",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.happiness",is(50)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastFed",is("2017-02-13T15:56:42")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastPetted",is("2017-02-13T15:56:05")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.age",is(30)))
                // type1 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.typeName",is("Slijmie")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.description",is("Een slijmerig maar schattig dier")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)))

                // player2 is correct
                .andExpect(jsonPath("$[1].name", is("Slakkie Slak")))
                .andExpect(jsonPath("$[1].playerDataCode", is("abcde12345")))
                // playerData2 is correct
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.playerDataCode",is("abcde12345")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.typeName",is("Slakkie")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.name",is("Slakkie Slak")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.health",is(70)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.happiness",is(60)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.lastFed",is("2019-03-04T15:56:12")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.lastPetted",is("2019-03-05T15:56:35")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.age",is(40)))
                // type2 is correct
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.typeName",is("Slakkie")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.description",is("Een slak")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.maxWeight",is(120)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.minWeight",is(70)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.minHealth",is(60)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.neuroticism",is(98)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.metabolism",is(120)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.minHappiness",is(40)))

                // player3 is correct
                .andExpect(jsonPath("$[2].name", is("Fluffy Pluisbol")))
                .andExpect(jsonPath("$[2].playerDataCode", is("Fluffy12345")))
                // playerData3 is correct
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.playerDataCode",is("Fluffy12345")))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.typeName",is("Fluffy")))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.name",is("Fluffy Pluisbol")))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.health",is(0)))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.happiness",is(80)))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.lastFed",is("2021-05-12T15:35:59")))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.lastPetted",is("2021-05-12T15:35:41")))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].playerData.age",is(1)))
                // type3 is correct
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.typeName",is("Fluffy")))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.description",is("Een pluisbol")))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$[2].playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)));
    }


    @Test
    void whenGetPlayerByPlayerDataCode_thenReturnPlayerJson() throws Exception {

        // GET all playerData from playerDataCode '12345abcde'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/12345abcde")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerData1))
                );

        // GET TypeTamagotchi with typeName 'Slijmie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slijmie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type1))
                );

        mockMvc.perform(get("/player/{playerDataCode}", "12345abcde"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // player is correct
                .andExpect(jsonPath("$.name", is("Rimu Tempest")))
                .andExpect(jsonPath("$.playerDataCode", is("12345abcde")))

                // playerData1 is correct
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.playerDataCode",is("12345abcde")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.typeName",is("Slijmie")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.name",is("Rimu Tempest")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.health",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.happiness",is(50)))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.lastFed",is("2017-02-13T15:56:42")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.lastPetted",is("2017-02-13T15:56:05")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.age",is(30)))

                // type1 is correct
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.typeName",is("Slijmie")))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.description",is("Een slijmerig maar schattig dier")))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)));
    }


    @Test
    void whenGetPlayerByTypeTamagotchi_thenReturnPlayerJson() throws Exception {

        // GET all playerData from playerDataCode '12345abcde'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerDatas/type/Slijmie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerDataTypeSlijmieList))
                );

        // GET TypeTamagotchi with typeName 'Slijmie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slijmie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type1))
                );

        mockMvc.perform(get("/players/type/{typeName}", "Slijmie"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Array length is correct
                .andExpect(jsonPath("$", hasSize(1)))
                // player is correct
                .andExpect(jsonPath("$[0].name", is("Rimu Tempest")))
                .andExpect(jsonPath("$[0].playerDataCode", is("12345abcde")))

                // playerData1 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.playerDataCode",is("12345abcde")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.typeName",is("Slijmie")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.name",is("Rimu Tempest")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.health",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.happiness",is(50)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastFed",is("2017-02-13T15:56:42")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastPetted",is("2017-02-13T15:56:05")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.age",is(30)))

                // type1 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.typeName",is("Slijmie")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.description",is("Een slijmerig maar schattig dier")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)));
    }


    @Test
    void whenGetPlayersAliveTrue_thenReturnPlayersJson() throws Exception {

        // GET all playerData from playerDataCode '12345abcde'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerDatas")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerDataList))
                );

        // GET TypeTamagotchi with typeName 'Slijmie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slijmie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type1))
                );

        // GET TypeTamagotchi with typeName 'Slakkie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slakkie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type2))
                );

        mockMvc.perform(get("/players/alive/true"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Array length is correct
                .andExpect(jsonPath("$", hasSize(2)))

                // player1 is correct
                .andExpect(jsonPath("$[0].name", is("Rimu Tempest")))
                .andExpect(jsonPath("$[0].playerDataCode", is("12345abcde")))
                // playerData1 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.playerDataCode",is("12345abcde")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.typeName",is("Slijmie")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.name",is("Rimu Tempest")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.health",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.happiness",is(50)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastFed",is("2017-02-13T15:56:42")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastPetted",is("2017-02-13T15:56:05")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.age",is(30)))
                // type1 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.typeName",is("Slijmie")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.description",is("Een slijmerig maar schattig dier")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)))

                // player2 is correct
                .andExpect(jsonPath("$[1].name", is("Slakkie Slak")))
                .andExpect(jsonPath("$[1].playerDataCode", is("abcde12345")))
                // playerData2 is correct
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.playerDataCode",is("abcde12345")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.typeName",is("Slakkie")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.name",is("Slakkie Slak")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.health",is(70)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.happiness",is(60)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.lastFed",is("2019-03-04T15:56:12")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.lastPetted",is("2019-03-05T15:56:35")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].playerData.age",is(40)))
                // type2 is correct
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.typeName",is("Slakkie")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.description",is("Een slak")))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.maxWeight",is(120)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.minWeight",is(70)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.minHealth",is(60)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.neuroticism",is(98)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.metabolism",is(120)))
                .andExpect(jsonPath("$[1].playerTamagotchis[0].typeTamagotchi.minHappiness",is(40)));
    }


    @Test
    void whenGetPlayersAliveFalse_thenReturnPlayersJson() throws Exception {

        // GET all playerData from playerDataCode '12345abcde'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerDatas")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerDataList))
                );

        // GET TypeTamagotchi with typeName 'Fluffy'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Fluffy")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type3))
                );

        mockMvc.perform(get("/players/alive/false"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Array length is correct
                .andExpect(jsonPath("$", hasSize(1)))

                // player3 is correct
                .andExpect(jsonPath("$[0].name", is("Fluffy Pluisbol")))
                .andExpect(jsonPath("$[0].playerDataCode", is("Fluffy12345")))
                // playerData3 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.playerDataCode",is("Fluffy12345")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.typeName",is("Fluffy")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.name",is("Fluffy Pluisbol")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.health",is(0)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.happiness",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastFed",is("2021-05-12T15:35:59")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.lastPetted",is("2021-05-12T15:35:41")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].playerData.age",is(1)))
                // type3 is correct
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.typeName",is("Fluffy")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.description",is("Een pluisbol")))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$[0].playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)));
    }


    @Test
    void whenAddPlayer_thenReturnPlayerJson() throws Exception {

        // Would never be the same because of the LocalDateTime.now() on POST
        LocalDateTime dateTime = LocalDateTime.now();
        PlayerData playerDataToPost = new PlayerData("player123post", "Slijmie", "Posted Slijm", 100, 100, dateTime, dateTime, 0);

        // POST new user
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData")))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerDataToPost))
                );

        // GET TypeTamagotchi with typeName 'Slijmie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slijmie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type1))
                );

        mockMvc.perform(post("/player")
                        .param("playerDataCode", "player123post")
                        .param("typeName", "Slijmie")
                        .param("name", "Posted Slijm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // player is correct
                .andExpect(jsonPath("$.name", is("Posted Slijm")))
                .andExpect(jsonPath("$.playerDataCode", is("player123post")))

                // playerData1 is correct
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.playerDataCode",is("player123post")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.typeName",is("Slijmie")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.name",is("Posted Slijm")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.health",is(100)))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.happiness",is(100)))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.lastFed").exists())
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.lastPetted").exists())
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.age",is(0)))

                // type1 is correct
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.typeName",is("Slijmie")))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.description",is("Een slijmerig maar schattig dier")))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)));
    }

    @Test
    void whenUpdatePlayer_thenReturnPlayerJson() throws Exception {

        PlayerData playerDataToPost = new PlayerData("12345abcde","Slijmie","PUT Slijm", 80, 50, LocalDateTime.of(2017, 2, 13, 15, 56, 42),LocalDateTime.of(2017, 2, 13, 15, 56, 5),30);

        // GET playerData for playerDataCode '12345abcde'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/12345abcde")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerData1))
                );

        // PUT playerData with playerDataCode '12345abcde'; update the name to "PUT Slijm"
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData")))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playerDataToPost))
                );

        // GET TypeTamagotchi with typeName 'Slijmie'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + typeTamagotchiServiceBaseUrl + "/types/Slijmie")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(type1))
                );

        mockMvc.perform(put("/player")
                        .param("playerDataCode", playerDataToPost.getPlayerDataCode())
                        .param("typeName", playerDataToPost.getTypeName())
                        .param("name", playerDataToPost.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // player is correct
                .andExpect(jsonPath("$.name", is("PUT Slijm")))
                .andExpect(jsonPath("$.playerDataCode", is("12345abcde")))

                // playerData1 is correct
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.playerDataCode",is("12345abcde")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.typeName",is("Slijmie")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.name",is("PUT Slijm")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.health",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.happiness",is(50)))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.lastFed",is("2017-02-13T15:56:42")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.lastPetted",is("2017-02-13T15:56:05")))
                .andExpect(jsonPath("$.playerTamagotchis[0].playerData.age",is(30)))

                // type1 is correct
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.typeName",is("Slijmie")))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.description",is("Een slijmerig maar schattig dier")))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.maxWeight",is(160)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minWeight",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minHealth",is(50)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.neuroticism",is(32)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.metabolism",is(80)))
                .andExpect(jsonPath("$.playerTamagotchis[0].typeTamagotchi.minHappiness",is(30)));
    }


    @Test
    void whenUpdatePlayerBadPlayerDataCode_thenReturnFilledException() throws Exception {

        PlayerData playerDataToPost = new PlayerData("12345@abcde","Slijmie","PUT Slijm", 80, 50, LocalDateTime.of(2017, 2, 13, 15, 56, 42),LocalDateTime.of(2017, 2, 13, 15, 56, 5),30);

        mockMvc.perform(put("/player")
                        .param("playerDataCode", playerDataToPost.getPlayerDataCode())
                        .param("typeName", playerDataToPost.getTypeName())
                        .param("name", playerDataToPost.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentsException))
                .andExpect(result -> assertEquals("playerDataCode parameter contains bad characters. Only letters and digits are allowed.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void whenUpdatePlayerBadTypeName_thenReturnFilledException() throws Exception {

        PlayerData playerDataToPost = new PlayerData("12345abcde","Slijmie123","PUT Slijm", 80, 50, LocalDateTime.of(2017, 2, 13, 15, 56, 42),LocalDateTime.of(2017, 2, 13, 15, 56, 5),30);

        mockMvc.perform(put("/player")
                        .param("playerDataCode", playerDataToPost.getPlayerDataCode())
                        .param("typeName", playerDataToPost.getTypeName())
                        .param("name", playerDataToPost.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentsException))
                .andExpect(result -> assertEquals("typeName parameter contains bad characters. Only letters are allowed.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void whenUpdatePlayerBadName_thenReturnFilledException() throws Exception {

        PlayerData playerDataToPost = new PlayerData("12345abcde","Slijmie","PUT Slijm@123", 80, 50, LocalDateTime.of(2017, 2, 13, 15, 56, 42),LocalDateTime.of(2017, 2, 13, 15, 56, 5),30);

        mockMvc.perform(put("/player")
                        .param("playerDataCode", playerDataToPost.getPlayerDataCode())
                        .param("typeName", playerDataToPost.getTypeName())
                        .param("name", playerDataToPost.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentsException))
                .andExpect(result -> assertEquals("name parameter contains bad characters. Only letters, digits and spaces are allowed.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void whenUpdatePlayerPlayerDataNull_thenReturnFilledException() throws Exception {

        PlayerData playerDataToPost = new PlayerData("doesntExist","Slijmie","PUT Slijm", 80, 50, LocalDateTime.of(2017, 2, 13, 15, 56, 42),LocalDateTime.of(2017, 2, 13, 15, 56, 5),30);

        // GET playerData for playerDataCode 'doesntExist'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/" + playerDataToPost.getPlayerDataCode())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK));

        mockMvc.perform(put("/player")
                        .param("playerDataCode", playerDataToPost.getPlayerDataCode())
                        .param("typeName", playerDataToPost.getTypeName())
                        .param("name", playerDataToPost.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentsException))
                .andExpect(result -> assertEquals("PlayerData with this playerDataCode doesn't exist", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void whenDeleteRanking_thenReturnStatusOk() throws Exception {

        // DELETE playerData for player with playerDataCode 'theDeletedOne999'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/theDeletedOne999")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        mockMvc.perform(delete("/player/{playerDataCode}", "theDeletedOne999"))
                .andExpect(status().isOk());
    }


    @Test
    void whenDeleteRankingBadPlayerDataCode_thenReturnStatusOk() throws Exception {

        // DELETE playerData for player with playerDataCode 'theDeletedOne999'
        mockServer.expect(ExpectedCount.once(),
                        requestTo(new URI(URL_PROTOCOL + playerDataServiceBaseUrl + "/playerData/theDeletedOne999")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        mockMvc.perform(delete("/player/{playerDataCode}", "theDeletedOne999 @"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadArgumentsException))
                .andExpect(result -> assertEquals("playerDataCode parameter contains bad characters. Only letters and digits are allowed.", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
