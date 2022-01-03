package fact.it.edge_microservice;


import fact.it.edge_microservice.model.TypeTamagotchi;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class PlayerTamagotchiModelTests {
    // --- TypeTamagotchi mock data ---
    private final TypeTamagotchi type1 = new TypeTamagotchi("Slijmie","Een slijmerig maar schattig dier",160,80,50,32,80,30);

    @Test
    void whenSetTypeTamagotchiTypeName_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setTypeName("SlijmieSet");
        assertThat(typeToSetTypeName.getTypeName()).isEqualTo("SlijmieSet");
    }

    @Test
    void whenSetTypeTamagotchiDescription_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setDescription("A new description for slime");
        assertThat(typeToSetTypeName.getDescription()).isEqualTo("A new description for slime");
    }

    @Test
    void whenSetTypeTamagotchiMaxWeight_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setMaxWeight(250);
        assertThat(typeToSetTypeName.getMaxWeight()).isEqualTo(250);
    }

    @Test
    void whenSetTypeTamagotchiMinWeight_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setMinWeight(10);
        assertThat(typeToSetTypeName.getMinWeight()).isEqualTo(10);
    }

    @Test
    void whenSetTypeTamagotchiMinHealth_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setMinHealth(25);
        assertThat(typeToSetTypeName.getMinHealth()).isEqualTo(25);
    }

    @Test
    void whenSetTypeTamagotchiNeuroticism_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setNeuroticism(50);
        assertThat(typeToSetTypeName.getNeuroticism()).isEqualTo(50);
    }

    @Test
    void whenSetTypeTamagotchiMetabolism_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setMetabolism(45);
        assertThat(typeToSetTypeName.getMetabolism()).isEqualTo(45);
    }

    @Test
    void whenSetTypeTamagotchiMinHappiness_thenReturnTypeTamagotchi() throws Exception {
        TypeTamagotchi typeToSetTypeName = type1;
        typeToSetTypeName.setMinHappiness(50);
        assertThat(typeToSetTypeName.getMinHappiness()).isEqualTo(50);
    }
}
