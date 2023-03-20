package jerem.coopcycle.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jerem.coopcycle.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RestaurateurDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurateurDTO.class);
        RestaurateurDTO restaurateurDTO1 = new RestaurateurDTO();
        restaurateurDTO1.setId(1L);
        RestaurateurDTO restaurateurDTO2 = new RestaurateurDTO();
        assertThat(restaurateurDTO1).isNotEqualTo(restaurateurDTO2);
        restaurateurDTO2.setId(restaurateurDTO1.getId());
        assertThat(restaurateurDTO1).isEqualTo(restaurateurDTO2);
        restaurateurDTO2.setId(2L);
        assertThat(restaurateurDTO1).isNotEqualTo(restaurateurDTO2);
        restaurateurDTO1.setId(null);
        assertThat(restaurateurDTO1).isNotEqualTo(restaurateurDTO2);
    }
}
