package jerem.coopcycle.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SocietaireMapperTest {

    private SocietaireMapper societaireMapper;

    @BeforeEach
    public void setUp() {
        societaireMapper = new SocietaireMapperImpl();
    }
}
