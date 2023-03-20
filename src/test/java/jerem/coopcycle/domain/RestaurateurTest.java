package jerem.coopcycle.domain;

import static org.assertj.core.api.Assertions.assertThat;

import jerem.coopcycle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurateurTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Restaurateur.class);
        Restaurateur restaurateur1 = new Restaurateur();
        restaurateur1.setId(1L);
        Restaurateur restaurateur2 = new Restaurateur();
        restaurateur2.setId(restaurateur1.getId());
        assertThat(restaurateur1).isEqualTo(restaurateur2);
        restaurateur2.setId(2L);
        assertThat(restaurateur1).isNotEqualTo(restaurateur2);
        restaurateur1.setId(null);
        assertThat(restaurateur1).isNotEqualTo(restaurateur2);
    }
}
