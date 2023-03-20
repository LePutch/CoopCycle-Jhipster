package jerem.coopcycle.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RestaurateurMapperTest {

    private RestaurateurMapper restaurateurMapper;

    @BeforeEach
    public void setUp() {
        restaurateurMapper = new RestaurateurMapperImpl();
    }
}
