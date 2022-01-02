package fact.it.edge_microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import fact.it.edge_microservice.model.PlayerData;
import fact.it.edge_microservice.model.TypeTamagotchi;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerUnitTests {

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
    private final ObjectMapper mapper = new ObjectMapper();

    // --- PlayerData mock data ---
    private final PlayerData playerData1 = new PlayerData("12345abcde","Slijmie","Rimu Tempest",80,50, LocalDateTime.of(2017, 2, 13, 15, 56, 42),LocalDateTime.of(2017, 2, 13, 15, 56, 5),30);
    private final PlayerData playerData2 = new PlayerData("abcde12345","Slakkie","Slakkie Slak",70,60,LocalDateTime.of(2019, 3, 4, 15, 56, 12),LocalDateTime.of(2019, 3, 5, 15, 56, 35),40);

    List<PlayerData> playerDataList = Arrays.asList(playerData1, playerData2);

    // --- TypeTamagotchi mock data ---
    private final TypeTamagotchi type1 = new TypeTamagotchi("Slijmie","Een slijmerig maar schattig dier",160,80,50,32,80,30);
    private final TypeTamagotchi type2 = new TypeTamagotchi("Slakkie","Een slak",120,70,60,98,120,40);

    List<TypeTamagotchi> typeList = Arrays.asList(type1, type2);

    @BeforeEach
    public void initializeMockserver() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
}
